<template>
    <div class="s-filter p-r font0" ref="sFilter">
        <resize-observer @notify="resizeMore" />
        <span class="s-filter-main d-ib font">
            <span class="s-filter-title d-ib" ref="sFilterTitle">
                我的过滤器:
            </span>
            <div class="s-filter-list d-ib" :style="{maxWidth: allowItemWidth}">
                <!-- 这里需要使用完整的list，不能使用过滤后展示的list，因为会动态计算所有item宽度
                如果使用了过滤后的list会导致动态计算错误，所以需要使用保持渲染item是使用list完整数据 -->
                <span
                    class="s-filter-item pointer d-ib"
                    v-for="(i, index) in list"
                    :key="index"
                    :class="{
                        'none': i.isMore
                    }"
                    ref="sFilterItem">
                    <span
                        class="s-filter-name"
                        :class="{'primaryC': currentFilterId === i.id}"
                        @click="setFilter(i)">
                        {{i.name}}
                    </span>
                    <bd-icon v-if="i.state.toString() === '1'" class="s-filter-share" name="allot" title="分享" @click="shareFilter(i)"></bd-icon>
                    <bd-icon v-if="i.state.toString() === '1'" class="s-filter-del" name="wrong" title="删除"  @click="deleteFilter(i, index)"></bd-icon>
                </span>
            </div>
        </span>
        <span v-if="showReset" class="warningC s-filter-reset pointer d-ib font" ref="sFilterReset" @click="reset()">
            <!-- TODO:图标待优化，修改为使用面性图标 -->
            <bd-icon name="logOut-fill"></bd-icon>
            重置
        </span>
        <span class="primaryC s-filter-save pointer d-ib font" ref="sFilterSave" @click="saveFilterOperate()">
            <bd-icon name="add-fill"></bd-icon>
            保存过滤器
        </span>
        <!-- 这个更多筛选条件应该始终保留，应该更多筛选条件下拉可以跳转至我的过滤器列表  -->
        <el-dropdown
            class="s-filter-more d-ib font pointer"
            ref="sFilterMore"
            trigger="click">
            <span class="el-dropdown-link">
                更多筛选条件<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
                <template v-if="list.filter(i => i.isMore).length > 0">
                    <el-dropdown-item
                        v-for="(i, index) in list"
                        :key="index"
                        :class="{
                            'none': !i.isMore
                        }">
                        <span @click.native="setFilter(i)">{{i.name}}</span>
                        <bd-icon v-if="i.state.toString() === '1'" class="s-filter-share" name="allot" title="分享" @click="shareFilter(i)"></bd-icon>
                        <bd-icon v-if="i.state.toString() === '1'" class="s-filter-del" name="wrong" title="删除"  @click="deleteFilter(i, index)"></bd-icon>
                    </el-dropdown-item>
                </template>
                <div v-else style="height: 100px">
                    <no-data style=" width: 140px;"></no-data>
                </div>
                <el-dropdown-item divided @click.native='jumpMyFilter'>
                    <bd-icon name="screen-fill"></bd-icon>
                    过滤器管理
                </el-dropdown-item>
            </el-dropdown-menu>
        </el-dropdown>
        <bd-dialog
            class="font"
            title="过滤器"
            :outScope="scope"
            :visible.sync="dialogVisible"
            :handlerList="handlerList"
            isAutoFix>
            <!-- 尽量不使用title的插槽 -->
            <div class="padding" v-if="list.length > 0">
                <p>当前已选中过滤器: <span class="primaryC">{{currentFilter.name}}</span></p>
                <p>是否【更新过滤器】或者另外【创建新的过滤器】？</p>
            </div>
        </bd-dialog>
    </div>
</template>
<script>
import { Get_Module_Filter_List, Save_Filter } from '@/api/frame/filter'
import { Is_Str_Empty } from '@/utils/index'
import Dialog from '@/components/frame/Dialog/index.vue'
import { S_Storage } from '@/utils/storage'
import NoData from '@/components/frame/NoData'
import { Share_Filter, Del_Filter } from '@/service/filter'
export default {
    name: 'search-filter',
    components: {
        NoData,
        [Dialog.name]: Dialog
    },
    props: {
        // 模型编码
        mdCode: {
            type: String,
            require: true
        },
        // 请求参数，字符串格式
        searchParam: {
            type: String
        },
        // 页面地址
        pageUri: {
            type: String
        },
        // 是否展示重置按钮
        showReset: {
            type: Boolean,
            default: false
        }
    },
    data () { // 定义页面变量
        return {
            // 过滤器项与项之间的间隔
            marDic: '17px',
            // 根据屏幕宽度允许放置过滤器项的最大宽度
            allowItemWidth: 0,
            // 过滤器数据
            list: [],
            // 当前选中的过滤器id值
            currentFilterId: null,
            // 过滤器提示保存框是否显示
            dialogVisible: false,
            // 过滤器提示保存框的操作按钮
            handlerList: [
                { id: 'update', name: '更新', type: 'success', icon: 'edit', click: function () {
                    // 关闭dialog
                    this.dialogVisible = false
                    // 更新过滤器
                    this.outScope.updateFilter()
                }},
                { id: 'save', name: '创建新的过滤器', type: 'primary', icon: 'save', click: function () {
                    // 关闭dialog
                    this.dialogVisible = false
                    // 添加过滤器
                    this.outScope.addFilter()
                }}
            ]
        }
    },
    computed: {
        // 当前组件作用域
        scope () {
            return this
        },
        // 获取当前页面地址，优先使用传入地址，若没有传入则使用组件所在的页面地址
        _pageUri () {
            return this.pageUri || this?.$route?.fullPath
        },
        // 当前选中的过滤器
        currentFilter () {
            if (this.currentFilterId === null) return {}
            return this.list.find(i => i.id === this.currentFilterId)
        },
        // 显示在更多过滤器中的列表数据
        _moreList () {
            return this.list.filter(i => i.isMore)
        }
    },
    methods: { // 定义函数
        // 重置过滤器的选中状态
        reset () {
            // hasChoosebeforeReset是告诉执行重置前是否有选中过滤器
            let hasChoosebeforeReset = !!this.currentFilterId
            // 重置过滤器选中样式
            this.currentFilterId = null
            // 抛出事件，告诉父组件重置
            this.$emit('reset', hasChoosebeforeReset)
        },
        // resize过滤器显示模块，数据变更时或者页面宽度发生变更时使用，重新计算可以展示的模块，其他过滤器添加到更多过滤器中进行展示
        resizeMore () {
            if (this.list.length > 0) {
                // 获取模块DOM对象
                let _panel = this.$refs.sFilter
                // 获取左侧标题区域DOM对象
                let _title = this.$refs.sFilterTitle
                // 获取过滤器子项DOM对象，因为子项是数组，所以这里获取到的也是子项数组DOM
                let _item = this.$refs.sFilterItem
                // 重置按钮DOM
                let _reset = this.$refs.sFilterReset
                // 获取保存过滤器按钮DOM对象
                let _save = this.$refs.sFilterSave
                // 获取更多过滤器按钮DOM对象
                let _more = this.$refs.sFilterMore?.$el
                // 获取每个过滤器项之间的间隔
                let itemSpace = parseInt(this.marDic)
                // 基于当前页面宽度，计算允许展示的过滤器子项的总和宽度
                let allowItemWidth = (_panel?.clientWidth || 0) -
                                   (_title.clientWidth  + 4) -
                                   (_reset ? (_reset.clientWidth + itemSpace) : 0) -
                                   _save.clientWidth -
                                   (_more ? (_more.clientWidth + itemSpace) : 0)
                // 更新给当前页面变量-允许展示的过滤器子项宽度的总和             
                this.allowItemWidth = `${allowItemWidth}px`
                // 接下来就是累加每一个过滤器子项的宽度，直到达到允许的过滤器子项宽度总和时，将符合条件的进行展示，超出部分存放进更多过滤器列表中
                
                // 定义符合展示在页面的过滤器子项下角标
                let sIndex = 0
                // 定义当前已有的过滤器子项宽度宽度
                let currentItemWidth = 0
                // 遍历所有的过滤器项
                for (let i = 0; i < _item.length; i++) {
                    // 因为过滤器项与项之间的间隔使用的margin，不是padding,所以在计算每块过滤器项展示宽度时需要加上margin值，也就是这里的itemSpace
                    currentItemWidth += _item[i].clientWidth + itemSpace
                    // 逐个累加，直到过滤器项总和超过允许展示的宽度时，获取此时的下角标
                    if (currentItemWidth > allowItemWidth) {
                        sIndex = i
                        break
                    }
                    // 如果过滤器数组所有都累加之后，还没有超出，则将长度+1作为下角标
                    //     +1是为什么，我暂时想不起来
                    if (i === _item.length - 1) {
                        sIndex = i + 1
                    }
                }
                // 根据以上获取的分割直接展示与在更多中展示的下角标，设置其状态
                this.list.forEach((i, index) => {
                    this.$set(i, 'isMore', index >= sIndex)
                })
            }
        },
        // 获取模型过滤器列表
        getModuleFilterList () {
            return new Promise((resolve, reject) => {
                Get_Module_Filter_List({
                    mdCode: this.mdCode
                }).then(res => {
                    // res = {
                    //     "Total":5,
                    //     "Rows":[
                    //         {"creator":"U00001","companyType":null,"flgDeleted":"0","pageUri":"/module/stander/list/wjx_test/placeholder","creatorName":"超级管理员","updateTime":"2021-07-22 07:23:21.0","sort":"100","userId":"U00001","content":"[{\"name\":\"sex\",\"value\":\"1065502662\",\"type\":\"text-query\",\"tagName\":\"\"},{\"name\":\"addressbookDesc\",\"value\":\"行政\",\"type\":\"text-query\",\"tagName\":\"\"}]","createTime":"2021-07-22 07:23:21","updatorName":"超级管理员","name":"2月过滤器","updator":"U00001","id":"402881687aca0164017acb61799a0004","state":"1","moduleId":null},
                    //         {"creator":"U00001","companyType":null,"flgDeleted":"0","pageUri":"/module/stander/list/wjx_test/placeholder","creatorName":"超级管理员","updateTime":"2021-07-22 07:23:01.0","sort":"100","userId":"U00001","content":"[{\"name\":\"sex\",\"value\":\"1065502662\",\"type\":\"text-query\",\"tagName\":\"\"}]","createTime":"2021-07-22 07:23:01","updatorName":"超级管理员","name":"1月过滤器","updator":"U00001","id":"402881687aca0164017acb612ce30003","state":"1","moduleId":null},
                    //         {"creator":"U00001","companyType":null,"flgDeleted":"0","pageUri":"/module/stander/list/wjx_test/placeholder","creatorName":"超级管理员","updateTime":"2021-07-22 07:22:38.0","sort":"100","userId":"U00001","content":"[{\"name\":\"sex\",\"value\":\"106550\",\"type\":\"text-query\",\"tagName\":\"\"},{\"name\":\"addressbookDesc\",\"value\":\"行政1\",\"type\":\"text-query\",\"tagName\":\"\"}]","createTime":"2021-07-22 07:22:38","updatorName":"超级管理员","name":"系统过滤器3","updator":"U00001","id":"402881687aca0164017acb60d31e0002","state":"0","moduleId":null},
                    //         {"creator":"U00001","companyType":null,"flgDeleted":"0","pageUri":"/module/stander/list/wjx_test/placeholder","creatorName":"超级管理员","updateTime":"2021-07-22 07:22:05.0","sort":"100","userId":"U00001","content":"[{\"name\":\"sex\",\"value\":\"106550\",\"type\":\"text-query\",\"tagName\":\"\"},{\"name\":\"addressbookDesc\",\"value\":\"行\",\"type\":\"text-query\",\"tagName\":\"\"}]","createTime":"2021-07-22 07:22:05","updatorName":"超级管理员","name":"系统过滤器2","updator":"U00001","id":"402881687aca0164017acb60512c0001","state":"0","moduleId":null},
                    //         {"creator":"U00001","companyType":null,"flgDeleted":"0","pageUri":"/module/stander/list/wjx_test/placeholder","creatorName":"超级管理员","updateTime":"2021-07-22 07:21:11.0","sort":"100","userId":"U00001","content":"[]","createTime":"2021-07-22 07:21:11","updatorName":"超级管理员","name":"系统过滤器1","updator":"U00001","id":"402881687aca0164017acb5f7dab0000","state":"0","moduleId":null}
                    //     ]
                    // }
                    let _list = (res?.Rows || []).map(i => ({
                        ...i,
                        isMore: false
                    }))
                    // state: 0-系统, 1-用户自定义
                    // 这里使用排序，将所有系统的排在前面，然后再排用户自定义内容
                    // 注意在添加过滤器的逻辑中：要将用户自定义添加的过滤器加在自定义过滤器的最前面，始终保持系统过滤器在最开始的位置
                    this.list = _list.sort((x, y) => {
                        return parseInt(x.state) - parseInt(y.state)
                    })
                    // 如果当前过滤器有默认选中值，则回显，这里一般是通过个人中心-我的过滤器跳转过来时，会使这里有值
                    if (this.currentFilterId) {
                        let _filter = this.list.find(i => i.id === this.currentFilterId)
                        // 设置过滤器选中
                        this.setFilter(_filter)
                    }
                    // 获取数据之后，动态计算可以展示的过滤器项，将展示不下的项放进更多过滤器中进行展示
                    this.$nextTick(() => {
                        this.resizeMore()
                    })
                    // 将结果列表的数据resolve回去，方便调用
                    resolve(this.list)
                })
            })
        },
        // 保存过滤器按钮的点击操作
        saveFilterOperate () {
            // 判断当前是否有选中过滤器
            if (this.currentFilterId !== null) {
                // 显示弹窗，询问是新增还是更新过滤器
                this.dialogVisible = true
            } else {
                // 添加过滤器
                this.addFilter()
            }  
        },
        // 添加过滤器
        addFilter () {
            this.$prompt('请输入过滤器名称', '保存过滤器', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                inputValidator: function (val) {
                    if (Is_Str_Empty(val)) {
                        return `请输入过滤器名称`
                    }
                    return true
                },
                inputErrorMessage: '请输入过滤器名称'
            }).then(({ value }) => {
                let params = {
                    // 过滤器名称
                    name: value,
                    // 搜索条件
                    content: this.searchParam,
                    // 页面地址
                    pageUri: this._pageUri,
                    // 模型编码
                    moduleCode: this.mdCode,
                    // 0-系统默认不可操作   1-用户自定义添加
                    state: 1
                }
                Save_Filter(params).then(res => {
                    if (res?.hasOk) {
                        let item = {
                            ...(res.bean || {}),
                            isMore: false
                        }
                        // 找到用户自定义过滤器最开始的下角标位置
                        //     如果存在下角标，则数据添加在下角标之前，确保可以展示在页面可视区域
                        //     如果下角标为-1，表示没有用户自定义过滤器，那么就直接添加在数据的最后面
                        let _userFirstIndex = this.list.findIndex(i => i.state.toString() === '1')
                        // 如果能找到用户自定义过滤器，那就在它之前添加该条数据，确保新增的能显示在页面，而不会被添加进更多过滤器中
                        if (~_userFirstIndex) {
                            this.list.splice(_userFirstIndex, 0, item)
                        } else {
                            // 如果没有找到，就直接添加，此时添加的用户自定义数据依旧满足最先
                            this.list.push(item)
                        }
                        // 将当前项的id更新给选中过滤器id，保存后能回显选中状态
                        this.currentFilterId = item.id
                        // 添加一条数据时，重新执行视图过滤器项的展示逻辑，不够放的存储进更多过滤器
                        this.$nextTick(() => {
                            this.resizeMore()
                        })
                        this.$message.success(this.getMessage(res?.message))
                    } else {
                        this.$message.error(this.getMessage(res?.message, false))
                    }
                })
            }).catch(() => {
                // 取消输入   
            })
        },
        // 更新已有过滤器数据
        updateFilter () {
            // 获取当前选中过滤器对象
            let currentFilter = this.list.find(i => i.id === this.currentFilterId)
            let params = {
                // 更新核心属性
                id: currentFilter.id,
                // 过滤器名称
                name: currentFilter.name,
                // 搜索条件-更新时重新获取
                content: this.searchParam,
                // 页面地址-更新时重新获取
                pageUri: this._pageUri,
                // 模型编码
                moduleCode: currentFilter.moduleCode,
                // 0-系统默认不可操作   1-用户自定义添加
                state: currentFilter.state
            }
            Save_Filter(params).then(res => {
                if (res?.hasOk) {
                    // 将当前接口返回的过滤器对象数据直接更新过滤器，省去重新过滤器列表数据的接口
                    currentFilter = res.bean
                    this.$message.success(this.getMessage(res?.message))
                } else {
                    this.$message.error(this.getMessage(res?.message, false))
                }
            })
        },
        /**
         * 分享过滤器
         * @params {Object} item 过滤器项对象
         */
        shareFilter (item) {
            // 执行分享事件
            Share_Filter.call(this, [item])
        },
        /**
         * 删除过滤器
         * @params {Object} item 过滤器项对象
         * @params {Number} index 过滤器项当前下角标
         */
        deleteFilter (item, index) {
            // 执行删除事件
            Del_Filter.call(this, [item]).then(res => {
                if (!res) return
                // let _index = this.list.findIndex(i => i.id === )
                this.list.splice(index, 1)
            })
        },
        /**
         * 设置过滤器选中
         * @params {Object} item 过滤器项对象
         */
        setFilter (item) {
            this.currentFilterId = item.id
            this.$emit('change', item)
        },
        /**
         * 跳转过滤器对应的页面
         * @params {Object} item 过滤器项对象
         */
        jumpMyFilter () {
            this.pushPage({
                path: '/module/stander/list/filter/placeholder',
                title: '过滤器管理'
            })
        },
        // 检查当前是否由【个人中心-我的过滤器】跳转过来的，跳转过来时会将点击的过滤器id存储session，
        //     在这里要判断下session中是否存在过滤器id，有则更新给页面变量值，确保回显能显示该过滤器被选中的状态
        watchSessionSelect () {
            // 获取存储在session中选中过滤器id
            let _currentFilterId = S_Storage.getItem('currentFilterId')
            // 若不存在则直接stop
            if (!_currentFilterId) return
            // 若存在，则将值更新给当前页面变量，用于回显选中与搜索逻辑处理
            this.currentFilterId = _currentFilterId
            // 使用后需要删除掉session中的信息，避免刷新后依旧是当前值以及影响了其他选中的逻辑，这里的session只为跳转逻辑使用
            S_Storage.removeItem('currentFilterId')
        },
        setItemDic () {
            document.body.style.setProperty('--mar-dic', this.marDic)
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        // 获取对应模型过滤器列表数据
        this.getModuleFilterList()
        // 检测是否从【个人中心-我的过滤器】选中过滤器跳转过来的，是则需要回显
        this.watchSessionSelect()
        // 设置项与项间隔
        this.setItemDic()
    }
}
</script>
<style lang='scss' scoped>
$height: 34px;
$marDic: var(--mar-dic);
.s-filter {
    width: 100%;
    height: $height;
    line-height: $height - 8px;
    // background: pink;
    .s-filter-main {
        .s-filter-title {
            padding-right: $padding;
        }
        .s-filter-list {
            overflow: hidden;
            white-space: nowrap;
            vertical-align: bottom;
            .s-filter-item {
                margin-right: $marDic;
                color: $fontC;
                .s-filter-name,
                .s-filter-share,
                .s-filter-del {
                    &:hover {
                        color: $primary;
                    }
                }
            }
        }
    }
    .s-filter-reset {
        margin-right: $marDic;
    }
    .s-filter-save {

    }
    .s-filter-more {
        margin-left: $marDic;
    }
}
</style>