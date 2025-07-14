// 地址本
<template>
    <div
        :class="selfClass"
        class="m-addressBook">
        <choose-tag
            v-if="!isDeny"
            :data="selection"
            :id-key="idKey"
            :text-key="textKey"
            :disabled="disabled"
            @showDialog="choose">
        </choose-tag>
        <!-- 兼容旧版本 -->
        <template v-else>
            <el-input
                class="s-input"
                type="text"
                :disabled="disabled"
                v-model="tempValue"
                :placeholder="placeholder || defaultPlaceholder" 
                :clearable="clearable">
            </el-input>
            <el-button
                class="s-btn"
                type="primary"
                :disabled="disabled"
                @click="choose">
                <bd-icon name="add"></bd-icon>
                选择
            </el-button>
        </template>
    </div>
</template>
<script>
import ChooseTag from '@/components/frame/Common/MForm/components/items/part/ChooseTagShow'
import GlobalConst from '@/service/global-const'
import { Get_Extra_Field } from '@/service/module'
export default {
    name: 'bd-address-book',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {
        [ChooseTag.name]: ChooseTag
    },
    props: {
        // 设置组件是否可编辑，默认可编辑Boolean(false)，非必须
        disabled: {
            type: Boolean,
            default: false
        },
        // 字段名
        name: {
            type: String
        },
        // 组件默认值传入
        value: {},
        // 标签名称
        label: {
            type: String,
            default: GlobalConst.form.label
        },
        // 预输入文本
        placeholder: {
            type: String,
            default: GlobalConst.form.placeholder.select
        },
        // 是否显示清除按钮
        clearable: {
            type: Boolean,
            default: true
        },
        // 地址本的类型 addressType值格式为 2-1-20-2 
        // 对应的配置项为type-singleChoose-selectType-indexType
        // 其中type: 地址本类型
            // 1：我的收藏夹           64：角色管理
            // 2：组织架构             128： 岗位选择
            // 4：我的公司             256： 群组选择
            // 8：我的部门             512：区域选择
            // 16：我的可管理部门       600：菜单选择
            // 32：指定组织选择
        // TODO:其他字段意义不明，待确认
        addressType: {
            type: String
        },
        // TODO：设计器表单配置字段，需要与后台进一步沟通确认字段含义
        valueFieldId: {
            type: String
        },
        // TODO：设计器表单配置字段，需要与后台进一步沟通确认字段含义
        valueFieldText: {
            type: String
        },
        // 地址本数据接口地址函数
        addressBookUrl: {
            type: [Function, String]
        },
        // 地址本节点数据地址函数
        nodeTreeUrl: {
            type: [Function, String]
        },
        // 选中节点下的列表数据
        dataUrl: {
            type: [Function, String]
        },
        // 搜索的接口地址函数
        searchUrl: {
            type: [Function, String]
        },
        // 地址本配置数据对象
        config: {
            type: Object
            // 可配置的配置属性如下参考
            // config: {
            //     chooseValue: '', // 地址本已选数据，一般传入格式为'20!@!超级管理员!@!U00001, 20!@!系统管理员!@!ff80808153c624e60153c62d40670002'
            //     addressTypes: '', // 地址本类型，优先级大于type，值为'x-x-x-x,y-y-y-y'
            //     title: '地址本选择', // 弹窗标题
            //     type: '', // 需要显示的地址本，不填的时候会加载全部，多个使用分号分割 如：2;600
            //     indexType: '', // 默认展开的地址本,根据type的值来填，不填默认展开第一个
            //     userPermission: false, // 权限校验，默认关闭
            //     selectType: null, // 确定数据范围，根据地址本的注册类有所区别，默认地址本注册类（BtAddressBookRegister）
            //     singleChoose: false, // 单选或多选 默认为多选
            //     includeParent: 1, // 点击节点时，是否包含父节点在待选框 1包含 0不包含 默认包含
            //     treeDefaultParams: null, // 树的默认查询参数，数据格式参考搜索组件的数据格式
            //     optionDefaultParams: null, // 选项的默认查询参数，数据格式参考搜索组件的数据格式
            //     registerDicCode: null, // 自定义注册bean的名称的数据字典值（如果需要自定义地址本注册类，模仿BtAddressBookRegister，并将改bean的名字保存在数据字典ADDRESS_BOOK_REGISTER中）
            //     treeLevels: null, // 待选框默认显示的层级
            // }
        },
        // 是否为查看状态
        isView: {
            type: Boolean,
            default: false
        },
        // 自定义class
        selfClass: {
            type: String,
            default: ''
        },
    },
    data () { // 定义页面变量
        return {
            // 选择的数据
            selection: [],
            // 选择地址本对象后对应取的id键
            idKey: 'value',
            // 选择地址本对象后用于展示取的text键
            textKey: 'name',
            // 当前的选中值，这里主要用于拦截value值的变更触发
            //     选择新的地址本的时候，selection变更，此时会触发change，合理触发
            //     value值会重新传入当前组件，若之前value无值，则此时会重新组装selection，所以
            //     为了避免此情况，添加该字段，用于判断
            currentChooseValue: null
        }
    },
    computed: {
        // 默认预输入文本
        defaultPlaceholder () {
            return GlobalConst.form.placeholder.select + this.label
        },
        // 目前tempValue的使用仅为旧版本：将冗余字段配置为当前字段的这种配法的逻辑
        tempValue: {
            get () {
                // 使用这种模式，因为主字段已经被赋值更新，所以文本必须要有冗余字段来存储，必须有值
                return this.valueFieldText && this.$attrs[this.valueFieldText] || this.$attrs?.extraValueObj?.[this.valueFieldText]
            },
            set (val) {
                this.$emit('input', val)
            }
        },
        // 是否禁止的模式：也就是将冗余字段id / text键配成当前字段
        //     考虑到对已有项目的兼容处理，这里做下处理，是这个模式的话就不做回显操作
        isDeny () {
            return this.valueFieldId === this.name || this.valueFieldText === this.name
        }
    },
    methods: { // 定义函数
        choose () {
            if (!this.addressType) {
                this.$message.warning(`请传入有效的地址本类型`)
                return false
            }
            // this.addressType有值时值格式为：2-1-20-2
            let [type, singleChoose, selectType, indexType] = this.addressType.split('-')
            // TODO:判断isSingle单地址本还是多地址本应该是传入数据，不是这里计算
            let isSingle = singleChoose === '1'
            // 定义地址本的配置信息
            let _config = Object.assign({}, {
                // 传入标题
                title: this.label ? `${this.label}选择` : `地址本选择`,
                // 传入默认值，打开地址本时回显
                chooseValue: this.value,
                addressTypes: this.addressType, // 新添加这个属性，从这个进行走通，尽量不影响原有属性
                type,
                singleChoose: isSingle,
                selectType,
                indexType: parseInt(indexType),

                addressBookUrl: this.addressBookUrl, // 获取地址本类型的接口地址函数
                nodeTreeUrl: this.nodeTreeUrl, // 获取地址本下节点接口地址函数
                dataUrl: this.dataUrl, // 选中节点下的列表接口地址函数
                searchUrl: this.searchUrl, // 搜索的接口地址函数
            }, this.config)
            // 调用地址本
            this.addressBook(_config).then(data => {
                // data数据格式:[{id:'222', name:'负责人', value: '20!@负责人!@222'}]
                this.selection = data
                // this.setAddressValue(data)
            })
        },
        /**
         * 根据选中的的地址本值更新对应表单值
         * @params {Array} data 选中的选中地址本数组值
         */
        setAddressValue (selection) {
            // 定义所选数组数据
            let _selection = selection || []
            // 定义地址本-类型集合
            let _types = []
            // 定义地址本-名称集合
            let _names = []
            // 定义地址本-id集合
            let _ids = []
            // 定义地址本-值集合
            let _values = []
            // 遍历完善：类型/名称/id/值集合
            _selection.forEach(i => {
                let { type, name, id, value } = i
                _types.push(type)
                _names.push(name)
                _ids.push(id)
                _values.push(value)
            })
            // 获取 类型/名称/id/值字符串
            _types = _types.join(GlobalConst.separator)
            _names = _names.join(GlobalConst.separator)
            _ids = _ids.join(GlobalConst.separator)
            _values = _values.join(GlobalConst.separator)
            // 更新临时值
            this.currentChooseValue = _values
            // 更新地址本的值
            this.$emit('input', _values)
            // 抛出change事件
            this.$emit('change', _values, _selection, {
                type: _types,
                name: _names,
                id: _ids,
                value: _values,
                selection: _selection,
                extraObj: this.$attrs,
            })
            let extraRelations = this.$attrs.extraRelations || {}
            extraRelations[this.name] = this.valueFieldId === this.name ? 'id' : this.idKey
            // 定义获取额外冗余字段
            let extraField = Get_Extra_Field({
                selection: _selection,
                idKey: 'id',
                textKey: this.textKey,
                extraObj: {
                    ...this.$attrs,
                    valueFieldId: this.valueFieldId,
                    valueFieldText: this.valueFieldText,
                    extraRelations
                },
            })
            // 选择数据变更时，通知父组件，更新冗余字段值
            this.$emit('updateExtraField', extraField, this.name)
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
    },
    watch: {
        value: {
            immediate: true,
            handler: function (newVal, oldVal) {
                // 初始进入组件的时候，都是通过value组装selection，但是如果是选择的过程中，selection变更时会触发change，会使新的value值
                //     重新传入当前组件，此时会进入此处逻辑，重新组装selection，实际是不使用的，所以添加个拦截规则
                if (this.currentChooseValue === newVal ||
                    // currentChooseValue若有值，格式为完整数据xx!@!xx!@!xx，但newVal可能是某一部分，所以这里用includes只判断包含即可
                   (this.currentChooseValue && newVal && this.currentChooseValue.includes(newVal))) {
                    return
                }
                // 【这里监听value的变化，核心是为了设置selection的值】
                // this.value数据格式可能存在两种：
                //     1. 值为【20!@!超级管理员!@!U00001】格式，推荐模式（此时冗余字段没有配置为当前字段）
                //     2. 值为上述值中的某一小块，可能为【超级管理员】，可能为【U00001】。（不推荐该配置方式，该方式为配置冗余字段为当前字段）
                // 基于以上两种值的可能，进行设置selection的操作，selection格式如下：
                //     [{ type: '20', name:'超级管理员', id:'U00001', value: '20!@!超级管理员!@!U00001'}]
                if (newVal) {
                    // 判断是将冗余字段配置成当前字段的模式时进入
                    if (this.isDeny && !newVal.includes(GlobalConst.addressbookValueSeparator)) {
                        // 根据键名获取字段值
                        function getValue (field, name) {
                            return name && field && field[name]
                        }
                        // 获取字段剩余属性，其中可能包含冗余字段的键值
                        let extraValueObj = this.$attrs.extraValueObj
                        // 获取名称集合
                        let _allName = this.name === this.valueFieldText ?
                                       newVal : (getValue(extraValueObj, this.valueFieldText) || '')
                        // 获取id集合
                        let _allId = this.name === this.valueFieldId ?
                                     newVal : (getValue(extraValueObj, this.valueFieldId) || '')
                        let _data = _allName || _allId
                        if (!_data) {
                            this.selection = []
                            return
                        }
                        // 根据分隔符拆解数据，处理单个/多个地址本的情况
                        this.selection = _data.split(GlobalConst.separator).map((i, index) => {
                            // 定义类型：冗余字段配制成当前字段的这种模型，一般获取不到type，直接空即可
                            let _type = ''
                            // 获取单个数据名称
                            let _name = _allName.split(GlobalConst.separator)[index] || ''
                            // 获取单个数据id
                            let _id = _allId.split(GlobalConst.separator)[index] || ''
                            // 组装单个地址本数据
                            let _value = [_type, _name, _id].join(GlobalConst.addressbookValueSeparator)
                            // 返回拼装selection
                            return {
                                type: _type,
                                name: _name,
                                id: _id,
                                value: _value
                            }
                        }) || []
                        // 配置冗余字段key为主字段的情况
                        let _message = `请联系管理员：不要将模型设计器【保存Key值给】配置为当前字段${this.name}`
                        console.warn(_message)
                    } else {
                        // 获取值数组
                        let valueList = newVal.split(GlobalConst.separator)
                        this.selection = valueList.map(i => {
                            let [type, name, id] = i.split(GlobalConst.addressbookValueSeparator)
                            return { type, name, id, value: i }
                        })
                    }
                } else {
                    this.selection = []
                }
            }
        },
        // 监听selection值
        selection: {
            handler: function (newVal, oldVal) {
                // 根据选中的的地址本值更新对应表单值
                this.setAddressValue(newVal)
            }
        },
    }
}
</script>
<style lang='scss' scoped>
$btnWidth: 70px;
$distance: 5px;
.m-addressBook {
    .s-btn {
        width: $btnWidth;
    }
    .s-input {
        width: calc(100% - #{$btnWidth} - #{$distance})
    }
    
}
</style>