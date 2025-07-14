<template>
    <div class="address-book-dialog">
        <el-dialog
            v-drag-dialog="{ hasDrag }"
            :id="id"
            custom-class="bd-dialog"
            v-setDialogSize = "{ 
                width: dialogWidth, 
                height: dialogHeight, 
                visibleStatus: dialogVisible,
                isAutoFix: isAutoFix,
                disabled: hasDrag }"
            :destroy-on-close = "true"
            :close-on-click-modal="false"
            :visible.sync="dialogVisible">
            <div
                v-if="config.title"
                slot="title"
                class="bd-dialog-title">
                <bd-icon name="pillar-fill" class="pillar fill"></bd-icon>
                <span>{{config.title}}</span>
            </div>
            <div class="bd-dialog__body">
                <div class="dialog-customize-content">
                    <div class="tree-block">
                        <el-collapse v-model="currentAddressType" accordion @change="handleCollapseChange">
                            <el-collapse-item
                                v-for="(item, index) in addressBookData" :key="index"
                                :title="item.name" :name="item.addressType">
                                <el-tree
                                    v-if="showTree[item.addressType]"
                                    lazy
                                    node-key="id"
                                    :props="treeProp"
                                    :default-expanded-keys="defaultExpandedKeys"
                                    :load="loadNode"
                                    @node-click="handleNodeClick">
                                </el-tree>
                            </el-collapse-item>
                        </el-collapse>
                    </div>
                    <div class="dialog-transfer">
                        <div class="marBottom">
                            <el-input
                                v-model="queryValue"
                                placeholder="请输入内容"
                                class="input-with-select"
                                @keyup.enter.native="searchOption">
                                <el-button slot="append" icon="el-icon-search" @click="searchOption"></el-button>
                            </el-input>
                        </div>
                        <el-transfer
                            @left-check-change="leftCheckChange"
                            @change="handleChange"
                            v-model="value"
                            :data="transdata"
                            :titles="['待选列表', '已选列表']"
                            :props="transferProp">
                            <div slot-scope="{ option }" @click="clickOption(option)" @dblclick="dbClickOption(option[transferProp.key])">
                                <span :title="option.desc">{{ option.name }}</span>
                            </div>
                        </el-transfer>
                        <div
                            :title="currentNodeDesc"
                            class="span-desc">
                            【描述】：{{currentNodeDesc || '暂无描述'}}
                        </div>
                    </div>
                </div>
            </div>
            <div
                slot="footer"
                v-if="btnList.length !== 0">
                <!-- isLoad：状态表示该按钮项存在loading状态，点击时会启用 -->
                <bd-button
                    v-for="(i, index) in btnList"
                    :key="index"
                    v-bind="i"
                    @click='exeMethod(i)'>
                </bd-button>
            </div>
        </el-dialog>
    </div>
</template>
<script>
    const SignName = 'sign'
    import setDialogSize from '@/directive/set-dialog-size'
    import DragDialogMixin from '@/components/frame/Dialog/DragMixin'
    import GlobalConst from '@/service/global-const'
    import { Merge_List } from '@/utils/list'
    import { Get_Full_Url } from '@/service/url'
    export default {
        components: {
        },
        directives: {
            setDialogSize
        },
        mixins: [DragDialogMixin],
        data () {
            return {
                // 弹窗是否自行拖动过
                hasDrag: false,
                // 用于区分两条地址本对象数据是否一致的对比属性名称
                signName: SignName,
                // 穿梭框属性
                transferProp: {
                    key: SignName,
                    label: 'name'
                },
                currentAddressType: '', // 当前的地址本类型编码
                addressTypeSeparator: '-', // 地址本类型分隔符 'x-x-x-x,y-y-y-y'
                typeSeparator: ';',  // 类型分隔符 'x;y'
                id: 'addressbook',
                dialogWidth: '890px', 
                dialogHeight: '500px', 
                isAutoFix: false,
                btnList: [
                    { id: 'cancel', name: '取消', icon: 'cancel', type: 'danger', click: () => { this.dialogVisible = false } },
                    { id: 'submit', name: '提交', icon: 'submit', type: 'primary', click: this.handleSubmitClick }
                ],
                // 地址本的所有配置
                config: {},
                addressBookData: [],
                // 加载树数据时用到的提交参数
                treeParams: {},
                // 加载选择数据时用到的提交参数
                dataParams: {},
                treeProp: {
                    label: 'name',
                    isLeaf(data) {
                        return !(data.isParent || data.hasChild)
                    }
                },
                defaultExpandedKeys: [],
                // input 框输入的搜索数据
                queryValue: '',
                transdata: [], //穿梭框数据渲染
                value: [], // 选中值
                listDataSet: {}, // 请求过的列表数据总和
                dialogVisible: true, //对话框显示控制,
                // 当前树节点的id
                currentTreeNodeId: '',
                // 控制是否渲染树
                showTree: [true],
                // 当前操作节点下的可选数据原数据，不拼接已选数据，主要用于双击取消时用该变量判断是否能将取消数据设置回左侧
                initialTransdata: [],
                currentNodeDesc: null, //当前选中节点描述
            }
        },
        computed: {
            // 获取地址本请求接口地址
            _addressBookUrl () {
                return this.getRealUrl('addressBookUrl', '/btaddressbook/btaddressbook/addressBookData')
            },
            // 获取地址本下节点数据的接口地址
            _nodeTreeUrl () {
                return this.getRealUrl('nodeTreeUrl', '/btaddressbook/btaddressbook/tree')
            },
            // 选中节点下列表数据的接口地址
            _dataUrl () {
                return this.getRealUrl('dataUrl', '/btaddressbook/btaddressbookquery/selectNode')
            },
            // 搜索数据的接口地址
            _searchUrl () {
                return this.getRealUrl('searchUrl', '/btaddressbook/btaddressbookquery/search')
            },
            // 根据传入数据this.config,组装回以下格式，获取地址本数据格式如下
            // [{   
            //     "value": "2-1-20-2",
            //     "type": "2",
            //     "singleChoose": "1",
            //     "selectType": "20",
            //     "indexType": "2"
            // }]
            addressTypeList () {
                let { addressTypes, type, singleChoose, selectType, indexType } = this.config
                // 如果直接传入地址本类型addressTypes:'x-x-x-x,y-y-y-y'，则优先级为最高不再处理其他type
                if (addressTypes) {
                    // 返回地址本类型列表['x-x-x-x', 'y-y-y-y']
                    let _addressTypeList = addressTypes.split(GlobalConst.separator)
                    return _addressTypeList.map(i => {
                        let [type, singleChoose, selectType, indexType] = i.split(this.addressTypeSeparator)
                        return {
                            value: i,
                            type,
                            singleChoose,
                            selectType,
                            indexType
                        }
                    })
                }
                // 如果是分散参数传入，则组装回['x-x-x-x']方式
                // type 与 selectType是必传字段，只需考虑singleChoose 与 indexType有无值
                let _singleChoose = singleChoose ? 1 : 0
                // indexType有传入则使用，没传入则默认使用type的值
                let _indexType = indexType.toString() || type
                let addressType = [type, _singleChoose, selectType, _indexType].join(this.addressTypeSeparator)
                return [{
                    value: addressType,
                    type,
                    singleChoose: _singleChoose,
                    selectType,
                    indexType: _indexType
                }]
            },
            // 获取type数组，即['type', 'singleChoos', 'selectType', 'indexType']
            typeList () {
                this.addressTypeList.forEach(i => {
                    i.split(this.addressTypeSeparator)
                })
            },
            // 获取当前选中地址本类型-type
            currentType () {
                if (!this.currentAddressType) return
                return this.addressTypeList.find(i => i.value === this.currentAddressType).type
            },
            // 获取当前选中地址本类型-indexType
            currentIndexType () {
                if (!this.currentAddressType) return
                return this.addressTypeList.find(i => i.value === this.currentAddressType).indexType
            },
            // 获取当前选中地址本类型-selectType
            currentSelectType () {
                if (!this.currentAddressType) return
                return this.addressTypeList.find(i => i.value === this.currentAddressType).selectType
            },
            // 获取当前选中地址本类型-singleChoose
            currentSingleChoose () {
                if (!this.currentAddressType) return
                let _addressbook = this.addressTypeList.find(i => i.value === this.currentAddressType)
                return _addressbook &&
                       _addressbook.singleChoose &&
                       !!parseInt(_addressbook.singleChoose)
            }
        },
        methods: {
            /**
             * 获取真正Url
             * @param {String} name 字段键名
             * @param {String} defaultUrl 默认接口地址
             */
            getRealUrl (name, defaultUrl) {
                // 获取config中的字段
                let _url = this.config?.[name]
                // 不存在，则使用默认接口地址
                if (!_url) return Get_Full_Url(defaultUrl)
                // 存在时，两种可能Function或String
                let _type = typeof _url
                if (_type === 'function') {
                    return _url.call(this)
                } else if (_type === 'string') {
                    return Get_Full_Url(_url)
                }
            },
            /**
             * 执行事件
             * @params {Object} itemObj 按钮对象
             */
            exeMethod (itemObj) {
                itemObj &&
                itemObj.constructor === Object &&
                typeof itemObj.click === 'function' &&
                (itemObj.click).call(this, itemObj)
            },
            loadAddress() {
                let _types = this.addressTypeList.map(i => i.type).join(this.typeSeparator)
                this.post(this._addressBookUrl, {type: _types}).then(res => {
                    if (res.hasOk) {
                        let data = res.bean
                        if (!(data && data.length > 0)) return
                        data = data.map((i, index) => ({
                            ...i,
                            // 追加每一块数据属性addressType: 'x-x-x-x'
                            addressType: this.addressTypeList[index].value
                        }))
                        // 检查当前是否有默认选中的地址本类型，没有的话默认选中第一个地址本类型的数据作为选中的地址本
                        if (!this.currentAddressType) {
                            this.currentAddressType = data[0].addressType
                        }
                        // 设置树的选中
                        this.showTree[this.currentAddressType] = true
                        this.addressBookData = data
                    } else {
                        this.alert(`获取地址本信息失败！${res.message}`)
                    }
                })
            },
            getTreeQeryParams() {
                return {
                    defaultParams: this.config.treeDefaultParams,
                    indexType: this.currentIndexType,
                    userPermission: this.config.userPermission,
                    registerDicCode: this.config.registerDicCode,
                    selectType: this.currentSelectType
                }
            },
            loadNode (node, resolve) {
                this.treeParams.indexType = this.currentIndexType
                let params = Object.assign({}, this.getTreeQeryParams())
                if (node.data) {
                    params.nodeId = node.data.id
                }
                this.post(this._nodeTreeUrl, params).then(res => {
                    resolve(res)
                    // 加载节点为根节点时，展开，并加载根节点的数据
                    if (node.level === 0) {
                        this.defaultExpandedKeys.push(res[0].id)
                        this.handleNodeClick(res[0])
                    }
                })
            },
            loadOptionData(url, params) {
                this.post(url, params).then(res => {
                    this.transdata = []
                    if (res &&
                        res.constructor === Array &&
                        res.length > 0) {
                        let _resList = res.map(i => ({
                            ...i,
                            // 构造用于对比两个地址本是否一致的字段
                            [this.signName]: this.getSign(i.type, i.id)
                        }))
                        // 临时存储数据
                        this.initialTransdata = _resList
                        // 将列表数据更新给数据总和对象listDataSet
                        this.updateDataObj(_resList)
                        this.transdata = this.setDefaultChoose(_resList)
                        // 抽象集合的处理 TODO待优化
                        if (this.currentType.toString() === '1') {
                            // 若当前为抽象处理人，则默认设置为已选列表（借助双击事件实现）
                            this.dbClickOption(this.transdata[0][this.transferProp.key])
                        }
                    } else {
                        this.transdata = this.setDefaultChoose([])
                    }
                })
            },
            /**
             * 将列表数据更新给数据总和对象listDataSet
             */
            updateDataObj (list) {
                if (!(list && list.length > 0)) return
                list.forEach(i => {
                    // 将列表数据存入对象listDataSet，以this.signName作为键
                    // 后续将使用this.value结合listDataSet获取最终的选中列表数据
                    this.listDataSet[i[this.signName]] = i
                })
            },
            // 获取已选数据
            getChooseList () {
                // 定义返回的结果集
                let result = []
                // 如果this.value没值，则直接返回[]
                if (!(this.value && Object.keys(this.listDataSet).length > 0)) return []
                // 根据value值匹配数据总和对象中的数据，组装结果集
                this.value.forEach(i => {
                    result.push(this.listDataSet[i])
                })
                return result
            },
            getOptionQueryParams() {
                return {
                    indexType: this.currentIndexType,
                    includeParent: this.config.includeParent,
                    defaultParams: this.config.treeDefaultParams,
                    treeLevels: this.config.treeLevels,
                    registerDicCode: this.config.registerDicCode,
                    selectType: this.currentSelectType
                }
            },
            searchOption() {
                let params = Object.assign({}, this.getOptionQueryParams(), {pid: this.currentTreeNodeId, value: this.queryValue.trim()})
                this.loadOptionData(this._searchUrl, params)
            },
            handleNodeClick(data) {
                // 抽象集合的处理 TODO待优化
                if (this.currentType.toString() === '1' && !data.pid) {
                    // 如果点击的是抽象集合ROOT节点，直接将数据清空，不需要请求数据
                    this.transdata = []
                    return
                }
                this.currentTreeNodeId = data.id
                let params = Object.assign({}, this.getOptionQueryParams(), {nodeId: data.id})
                this.loadOptionData(this._dataUrl, params)
            },
            handleSubmitClick() {
                // 获取当前选中的数据
                let data = this.getChooseList()
                if (data.length === 0) {
                    this.alert('请选择数据')
                    return
                }
                this.callback(data)
                this.dialogVisible = false
            },
            /**
             * @params {Array} currentValue 当前组件的选中值
             * @params {String} to 方向 ‘left/right’
             * @params {Array} changeValue 移动是数组数据
             */
            handleChange (currentValue, to, changeValue) {
                let add = to === 'right'
                if (add) {
                    // 如果是单选，先清除现有数据
                    if (this.currentSingleChoose) {
                        this.value = changeValue
                    }
                } else {
                    this.value = currentValue
                    // 删除的数据需要注意：如果不属于左侧待选数据，则直接清除掉，不加入左侧
                    changeValue.forEach(i => {
                        if (this.initialTransdata.every(j => j.sign !== i)) {
                            this.transdata.splice(this.transdata.findIndex(k => k.sign === i), 1)
                        }
                    })
                }
            },
            /**
             * 实现双击选择的功能
             * @param {string} dataId 双击项对应的id
             */
            dbClickOption (sign) {
                // 模拟-添加到左/右侧的情况 --start
                let addStatus = this.value.every(i => i !== sign)
                let way = addStatus ? 'right' : 'left'
                if (addStatus) {
                    this.value = [...this.value, sign]
                } else {
                    let index = this.value.findIndex(i => i === sign)
                    this.value.splice(index, 1)
                }
                // 模拟-添加到左/右侧的情况 --end
                // 然后执行变更事件
                this.handleChange(this.value, way, [sign])
            },
            // 展示选中数据的详细信息
            clickOption (option) {
                this.currentNodeDesc = option.desc
            },
            /**
             * 实现页面的单选效果
             * @param chooseArr 已勾选的数组，通过改变改数组的值，实现单选
             * @param current   当前选择的数组
             */
            leftCheckChange(chooseArr, current) {
                if (!this.currentSingleChoose) {
                    return
                }
                // 一个都没选或只选了一个，什么都不用做
                if (chooseArr.length === 0 || chooseArr.length === 1) {
                    return
                }

                /*
                 * current 在点击全选按钮的时候，很可能有多个，这种情况下，删除选中的，提示不能多选。
                 */
                if (current.length > 1) {
                    // 将数组转换为对象，方便后续查询
                    let obj = {}
                    chooseArr.forEach((e, i) => obj[e] = i)
                    // 获取 current 中的元素在 chooseArr 中的下标
                    let indexArr = current.map(e => obj[e])
                    /*
                     * 将下标从大到小排序，目的是为了可以从数组后面往前面删除，如果从前面删除的话，会导致
                     * 后面元素的下标发生改变
                     */
                    indexArr.sort((n1, n2) => n2 - n1)
                    indexArr.forEach(i => chooseArr.splice(i, 1))
                    this.alert('只能选择一个')
                    return
                }

                // current 只有1个，替换已有数据
                chooseArr.splice(0, chooseArr.length)
                chooseArr.push(...current)
            },
            handleCollapseChange(active) {
                this.showTree[active] = true
                this.currentAddressType = active
                // 切换地址本的时候，清除选中值
                this.value = []
                // 切换地址本的时候，清除数据总和对象
                this.listDataSet = {}
                
            },
            // 获取当前选中的地址本类型'x-x-x-x'
            getCurrentAddressType () {
                // 获取传入的指定地址本类型
                let _indexType = this.config.indexType.toString()
                if (_indexType) {
                    // 若存在传入的指定的地址本类型type，则返回对应的地址本addressType:'x-x-x-x'
                    let _currentList = this.addressTypeList.filter(i => i.indexType.toString() === _indexType.toString())
                    if (_currentList.length >= 1) {
                        return _currentList[0].addressType
                    }
                }
                // 如果没有传入，默认选择第一个
                return this.addressTypeList?.[0].addressType
            },
            // 对比两个地址本是否一致，需要通过type与id来判断
            getSign (type, id) {
                return `${type}-${id}`
            },
            // 用于回显选中值
            defaultChooseData () {
                // 获取传入的选中值数据
                let _chooseValue = this.config?.chooseValue
                if (!_chooseValue) return []
                let valueList = _chooseValue.split(GlobalConst.separator)
                // 处理将冗余字段配给当前字段的这种操作逻辑，这里拿到的值不是xx!@!xx!@!,不处理，不回显选中值
                // 不推荐将冗余字段配给当前字段
                if (!valueList.every(i => ~i.indexOf(GlobalConst.addressbookValueSeparator))) return
                let _currentList = valueList.map(i => {
                    let [type, name, id] = i.split(GlobalConst.addressbookValueSeparator)
                    return { 
                        type,
                        name,
                        id,
                        value: i,
                        // 构造用于对比两个地址本是否一致的对比字段
                        [this.signName]: this.getSign(type, id)
                    }
                })
                // 更新当前页面列表数据总和对象
                this.updateDataObj(_currentList)
                // 设置值选中
                this.value = _currentList.map(i => i[this.signName])
            },
            setDefaultChoose (data) {
                // 根据值获取已选数据列表
                let currentChooseList = this.value.map(i => this.listDataSet[i])
                // 将新请求的列表数据合并之前已选的数据，确保在同一地址本内进行切换时可以保留选中数据
                let result = Merge_List(data, currentChooseList, this.signName, {
                    isContentFirst: true, // 已最新请求到的列表数据内为主
                    isOrderFirst: false, // 以已选列表的数据顺序为主，避免已选数据顺序动来动去
                })
                return result
            }
        },
        created() {
            this.loadAddress()
            // 设置当前选中的地址本类型
            this.currentAddressType = this.getCurrentAddressType()
            this.defaultChooseData()
        },
        watch: {
            dialogVisible: function (newVal, oldVal) {
                if (!newVal) {
                    // 全局方法，从dom层级删除，每次弹出dialog都是通过新增元素，原有dialog的隐藏只是display，这样会导致内存溢出
                    this.closeAddressBook()
                }
            }
        }
    }
</script>
<style scoped lang="scss">
$current_node_desc_height: 36px;
.address-book-dialog::v-deep {
    .bd-dialog__body {
        padding: 0;
        .dialog-customize-content {
            padding: $padding $padding 0;
            display: flex;
            width: 100%;
            height: 100%;
            .tree-block {
                width: 260px;
                display: inline-block;
                border: 1px solid #efecec;
                height: calc(100% - #{$current_node_desc_height});
                border-radius: 4px;
                overflow: auto;
                border-top: none;
                p {
                    width: 100%;
                    height: 35px;
                    font-size: 16px;
                    text-align: center;
                    background-color: #409EFF;
                    color: white;
                    border: 1px solid #efecec;
                    margin-bottom: 5px;
                    padding-top: 4px;
                }
                .el-collapse {
                    .el-collapse-item {
                        .el-collapse-item__header {
                            height: 30px;
                            line-height: 30px;
                            background: $primary;
                            color: $white;
                            padding-left: 8px;
                            font-size: 15px;
                        }
                        .el-collapse-item__content {
                            padding-bottom: 0;
                        }
                    }
                }
            }
            .dialog-transfer {
                width: calc(100% - 260px);
                display: inline-block;
                margin-left: 10px;
                position: relative;
                .el-transfer {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    height: calc(100% - #{$inputHeight} - #{$padding} - #{$current_node_desc_height});
                    .el-transfer-panel {
                        height: 100%;
                        width: 240px;
                    }
                    .el-transfer__buttons {
                        padding: 0 10px;
                    }
                }
                .span-desc{
                    line-height: 18px;
                    width: 100%;
                    height: #{$current_node_desc_height};
                    overflow: auto;
                    font-size: 12px;
                }
            }
        }
    }
    .el-dialog__footer {
        text-align: center;
    }
}
</style>
