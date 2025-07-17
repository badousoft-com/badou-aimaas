// 新的时代已经来临
<template>
    <div class="bd-module-list defaultBg padding h-per-100">
        <m-title v-if="!noTitle && _title" class="bd-module-list__title b-b" :title="_title">
        </m-title>
        <search-filter v-if="!_hideSearchFilter" class="bd-module-list__filter" ref="searchFilter" :mdCode="mdCode"
            :searchParam="params.searchParam" @change="changeSearchFilter" @reset="resetSearchFilter">
        </search-filter>
        <m-search v-if="!_hideSearch" class="bd-module-list__search" :data="searchFieldList" :list-scope="scope"
            :btnList="diy_searchBtns" @change="searchConditionChange" @search="search()" @reset="searchReset">
            <!-- 右侧固定插槽 -->
            <template v-slot:fixedRight>
                <render-fun v-if="searchFixedRightSlot" :render="searchFixedRightSlot"></render-fun>
            </template>
        </m-search>
        <!-- 列表顶部插槽 -->
        <render-fun v-if="listTopSlot" :render="listTopSlot"></render-fun>
        <div class="bd-module-list__main">
            <div class="bd-module-list__btn" v-if="!hideBtn && btnList.length > 0">
                <template v-for="(i, index) in btnList">
                    <bd-button v-if="ShowStatus(i)" :key="index" :plain="true" :fillIcon="true" v-bind="i"
                        @click="handleClick(i)" :class="i.className" class="bd-module-list__btn__item">
                    </bd-button>
                </template>
                <!-- 按钮区最右侧固定插槽 -->
                <div class="btn-fixed-right-slot">
                    <render-fun v-if="btnFixedRightSlot" :render="btnFixedRightSlot"></render-fun>
                </div>
            </div>
            <!-- 添加按钮区下的插槽位 -->
            <render-fun v-if="btnNextLineSlot" :render="btnNextLineSlot"></render-fun>
            <bd-table-list v-if="tableParamsReady" class="bd-module-list__list" :data.sync="tableData"
                :sortObj.sync="sortObj" :btnList.sync="btnList" :finalRequestConfig.sync="finalRequestConfig"
                :btnScoped="scope" v-bind="tableAttrs" v-on="$listeners" @cellDblClick="dblClick">
            </bd-table-list>
        </div>
    </div>
</template>
<script>
import MSearch from '@/components/frame/Common/MSearch/index'
import SearchFilter from '@/components/frame/Module/SearchFilter'
import BdTableList from '@/components/frame/Common/MList/index.vue'
import RenderFun from '@/components/frame/RenderFun'
import { Has_Value } from '@/utils/index'
import { Get_Search_Type, Merge_Btn } from '@/service/module'
import { Module_List_URL } from '@/api/frame/common'
import GlobalConst from '@/service/global-const'
import { ShowStatus, HandleClick, GetSearchFieldList } from '@/service/module'
import { Jump_Drill_Url, Render_Field } from '@/service/base-service.js'
import MTitle from '@/components/frame/Common/MTitle'
import { Sort_List, Unique_Array } from '@/utils/list'
import { Deep_Clone } from '@/utils/clone'
import { Add, Edit, View, Import, Export, Delete } from '@/components/frame/Module/BtnBaseFun/list'
import { Get_Btn_ClickFun } from '@/service/event-expand'
import { FileSet } from '@/components/frame/Common/MForm/frameSet/index'
import Preview from '@/components/frame/Common/MForm/components/items/File/ImagePicker/Preview'
import { Allow_Preview, Show_Preview } from '../../Preview'
import Progress from '@/components/frame/Common/MForm/components/items/File/Attach/Progress.vue'
import { Download_Item_Animation } from '@/service/attach/index.js'

const Default_Btn_List = [
    { id: 'add', click: Add, loading: false },
    { id: 'edit', click: Edit, loading: false },
    { id: 'view', click: View, loading: false },
    { id: 'import', click: Import, loading: false },
    { id: 'export', click: Export, loading: false },
    { id: 'delete', click: Delete, loading: false },
]
export default {
    name: "module-list",
    // 禁止父级传入属性作为当前组件的根标签属性
    inheritAttrs: false,
    components: {
        MSearch,
        BdTableList,
        [MTitle.name]: MTitle,
        [SearchFilter.name]: SearchFilter,
        [RenderFun.name]: RenderFun,
    },
    props: {
        // 是否启动刷新列表[keep-alive缓存组件时]
        //     为避免从列表跳转编辑页面，编辑修改数据时列表能实时响应，刷新缓存数据
        //     添加该状态执行刷新列表数据
        isStartReList: {
            type: Boolean,
            default: false
        },
        // ref属性名称
        listRefName: {
            type: String,
            default: 'list'
        },
        noTitle: {
            type: Boolean,
            default: true
        },
        // 标题
        title: {
            type: String
        },
        // 模型名称
        name: {
            type: String
        },
        // 模型编码
        mdCode: {
            type: String
        },
        // 表格数据
        data: {
            type: Array,
            default: () => []
        },
        // 列表数据请求地址
        url: {
            type: String
        },
        // 表头字段数组
        fieldList: {
            type: Array,
            default: () => []
        },
        // 自定义配置【来源于自定义js】
        customSetting: {
            type: Object,
            default: () => { }
        },
        // 搜索项字段列表（只含简单字段信息）
        searchCondition: {
            type: Array,
            default: () => []
        },
        // 搜索项字段列表（含详细字段信息）
        selectorData: {
            type: Array,
            default: () => []
        },
        // 表格高度：包含表头与tbody，不包含分页模块
        tableHeight: {
            type: [String, Number]
        },
        // 是否显示表格数据的选择框
        isSelection: {
            type: Boolean,
            default: true
        },
        // 接口请求获取列表数据后的获取路径: res[optionResPath]结果将用于列表数据展示
        optionResPath: {
            type: String,
            default: 'Rows'
        },
        // 获取数据总数total的获取路径：res[totalPath]结果将作为获取到的列表数据总数
        totalPath: {
            type: String,
            default: 'Total'
        },
        // 默认按钮列表
        defaultBtnList: {
            type: Array
        },
        // 隐藏按钮
        hideBtn: {
            type: Boolean,
            default: false
        },
        // 隐藏搜索栏
        hideSearch: {
            type: Boolean,
            default: false
        },
        // 隐藏搜索过滤器
        hideSearchFilter: {
            type: Boolean,
            default: false
        },
        // 搜索栏参数是否参与列表请求【注意隐藏只决定展不展示，默认地这里依旧是参与请求】
        searchAbled: {
            type: Boolean,
            default: true
        },
        // 是否显示分页工具栏
        showPagination: {
            type: Boolean,
            default: true
        },
        // 组件传入默认请求参数
        defaultParamsObj: {
            type: Object,
            default: () => { }
        },
        // 默认搜索栏参数
        searchParam: {
            type: Object,
            default: () => { }
        },
        // 开启后，将会设置高度100%
        fullHeight: {
            type: Boolean,
            default: false
        },
        // 权限按钮
        permissionBtnList: {
            type: Array,
        },
        // 额外参数:存储不属于当前组件业务的参数；数据中转站
        elseAttrs: {
            type: Object
        }
    },
    data() { // 定义页面变量
        return {
            finalRequestConfig: {},
            // 按钮区下面的插槽位
            btnNextLineSlot: null,
            // 搜索区右侧固定插槽
            searchFixedRightSlot: null,
            // 按钮区右侧固定插槽
            btnFixedRightSlot: null,
            // 列表顶部/搜索框下部插槽
            listTopSlot: null,
            ShowStatus: ShowStatus,
            // 未显示出现的用于列表的属性对象
            elseTableAttrs: null,
            // 排序对象数据
            sortObj: {
                // sortname: 排序字段键
                // sortorder：排序方式（顺/倒/无）
            },
            // 搜索配置数据
            searchFieldList: [],
            // 请求参数
            params: {},
            // 操作按钮数组
            btnList: [],
            // 按钮事件
            handleClick: HandleClick,
            // 列表参数是否准备齐全,需要准备齐全避免频繁无效请求
            tableParamsReady: false,
            // 是否关闭所有字段悬浮提示
            closeAllToolTip: false,

            // 自定义功能按钮
            diy_buttons: null,
            // 自定义是否启用分页功能
            diy_isUsePagination: true,
            // 自定义是否显示分页组件（不决定功能，只决策展示）
            diy_isShowPagination: true,
            // 自定义搜索事件
            diy_search: null,
            // 自定义隐藏搜索框
            diy_hideSearch: false,
            // 自定义隐藏搜索过滤器
            diy_hideSearchFilter: false,
            // 自定义列表请求数据url
            diy_dataUrl: null,
            // 请求前自定义更改参数
            diy_paramsBeforeRequest: null,
            // 搜索条件变化监听事件
            diy_searchConditionChange: null,
            // 自定义表格列字段编辑
            diy_renderField: null,
            // 自定义表格-字段表头
            diy_renderFieldHeader: null,
            // 请求列表数据之后需要执行的事件
            diy_afterListJSON: null,
            // 列表渲染后需要执行的事件
            diy_afterListRender: null,
            // 自定义创建的函数(作用域在mList/index.vue)
            diy_createFun: null,
            // 自定义创建的函数(作用域在moduleList/index.vue)
            diy_createListFun: null,

            // 自定义-导出事件接口地址
            diy_exportUrl: null,
            // 自定义-导出事件标题
            diy_exportTitle: null,
            // 自定义-导出事件弹窗标题(2022-10-22：TODO 即将废弃)
            diy_exportDialogTitle: null,
            // 自定义-导出的相关配置
            diy_exportProps: null,
            // 导出禁用字段
            diy_exportDefined: null,
            // 自定义-导入的接口地址
            diy_importUrl: null,
            // 自定义-导入的模版地址
            diy_importTemplateUrl: null,
            // 自定义-导入事件标题
            diy_importTitle: null,
            // 自定义-导入事件的参数
            diy_importKey: null,
            // 自定义-导入的相关配置
            diy_importProps: null,
            // 自定义-表格是否支持多选,默认为多选
            diy_multiple: true,
            // 自定义-表格行双击事件
            diy_dblClick: null,
            // 自定义-搜索栏按钮
            diy_searchBtns: null,
            // 前置冻结列
            diy_freezeColumnIndex: null,
            // 后置冻结列
            diy_freezeColumnLastIndex: null,
            // 展示合并行
            diy_showSummary: null,
            // 当选择项发生变化时会触发该事件
            diy_selectionChange: null,
            // 合并表头/修改表头
            diy_columnFormat: null,
            // 按钮区下面的插槽位
            diy_btnNextLineSlot: null,
            // 搜索区右侧固定插槽
            diy_searchFixedRightSlot: null,
            // 按钮区右侧固定插槽
            diy_btnFixedRightSlot: null,
            // 列表顶部/搜索框下部插槽
            diy_listTopSlot: null,
            // 表格行列合并
            diy_spanMerge: null,
            timer: null
        }
    },
    computed: {
        // 当前组件作用域
        scope() {
            return this
        },
        // 获取标题
        _title() {
            return this.title || this.name
        },
        // 是否隐藏搜索框
        _hideSearch() {
            return this.hideSearch || this.diy_hideSearch
        },
        // 隐藏搜索过滤器
        _hideSearchFilter() {
            // 如果搜索框隐藏了，那搜索过滤器也设置隐藏
            if (this.hideSearchFilter ||
                this.diy_hideSearchFilter ||
                this._hideSearch) return true
            return !(this.searchFieldList &&
                this.searchFieldList.length &&
                this.tableParamsReady)
        },
        // 获取页面地址
        pageUri() {
            // fullPath包含查询部分
            return this.$route.fullPath
        },
        // 获取列表组件Ref实例
        tableListRef() {
            return this.$refs?.[this.listRefName]
        },
        // 获取列表最终数据请求的参数
        finalListParams() {
            return this.tableListRef?.temp_params
        },
        // 获取列表对象属性
        tableAttrs() {
            return {
                ref: this.listRefName,
                url: this._url,
                height: this.tableHeight,
                isSelection: this.isSelection,
                fieldList: this._fieldList,
                fieldValueSetting: this._fieldValueSetting,
                params: this.params,
                paramsBeforeRequest: this.diy_paramsBeforeRequest,
                optionResPath: this.optionResPath,
                totalPath: this.totalPath,
                usePagination: this.diy_isUsePagination && this.showPagination, // 决策使用，showPagination这里后续将不推荐使用
                showPagination: this.diy_isShowPagination, // 决策展示
                multiple: this.diy_multiple,
                afterListJSON: this.diy_afterListJSON,
                afterListRender: this.diy_afterListRender,
                createFun: this.diy_createFun,
                diySearch: this.diy_search,
                freezeColumnIndex: this.diy_freezeColumnIndex,
                freezeColumnLastIndex: this.diy_freezeColumnLastIndex,
                showSummary: this.diy_showSummary,
                closeAllToolTip: this.closeAllToolTip,
                selectionChange: this.diy_selectionChange,
                columnFormat: this.diy_columnFormat,
                btnNextLineSlot: this.diy_btnNextLineSlot,
                spanMerge: this.diy_spanMerge,
                elseAttrs: this.elseAttrs,
                parentHeight: this.$attrs.parentHeight,
                ...this.elseTableAttrs
            }
        },
        // 用于处理搜索栏提供的搜索参数【转化参数数组数据】
        searchParams() {
            // 传入参数：搜索栏参数是否生效，作用于请求列表数据
            if (!this.searchAbled) return
            let params = this.searchFieldList.filter(i => Has_Value(i.value))
                .map(i => {
                    return {
                        name: i.id,
                        // 值为字符串时trim()去除前后空格
                        value: typeof i.type === 'string' ? i.value.trim() : i.value,
                        // 自定义传入的搜索类型queryType
                        type: i.queryType || Get_Search_Type(i.type),
                        // 当前搜索字段同时支持的字段搜索，格式如'name,sex'，输入框输入值将会作为这些字段中（或的关系）一起去查询
                        tagName: i.tagName || ''
                    }
                })
            return params
        },
        // 获取列表数据请求地址（优先级：自定义 > 父组件 > 默认）
        _url() {
            let _url = null
            if (this.diy_dataUrl && typeof this.diy_dataUrl === 'function') {
                _url = this.diy_dataUrl.call(this)
            }
            // 以下按照优先级：先后
            //     _url: 动态js传入自定义地址
            //     this.url: 父组件传入地址
            //     Module_List_URL: 当前组件默认请求地址
            return _url || this.url || Module_List_URL(this.mdCode)
        },
        // 表格列表数据
        tableData: {
            get() {
                return this.data
            },
            set(val) {
                this.$emit('update:data', val)
            }
        },
        // 获取字段值的更新方式
        //     一般来说，在模型-列表配置中配置的展示类型，会决策listJSON.do返回的数据
        //     但是部分展示类型，后端是不做处理的，所以需要在前端做处理
        //     这里就是根据特殊的展示类型，设置对应的值处理方案进而传递到mList/index中进行值更新
        _fieldValueSetting() {
            // 过滤隐藏字段
            let _fieldList = this.fieldList.filter(i => ShowStatus(i))
            let _res = null
            _fieldList.forEach(i => {
                if (!i.type) return
                let _itemValue = null
                switch (i.type) {
                    // 单图片
                    case 'imgSingle':
                        /**
                         * 获取单图对象
                         * @param {Object} item { name:'键名',value: 'attachId' }
                         * @returns {Array} 图片数组,项拥有完整属性的文件对象（只有一项）
                         */
                        _itemValue = (item) => {
                            if (!item?.value) return
                            return FileSet.editForm.getSingleImg(item)
                        }
                        break
                    // 多图片
                    case 'img':
                        /**
                         * 获取多图数组
                         * @param {Object} item 对象{ name:'键名',value:'attachId1,attachId2,attachId3,attachId4'}
                         * @returns {Array} 图片数组，项为拥有完整属性的文件对象
                         */
                        _itemValue = (item) => {
                            if (!item?.value) return
                            return item.value
                                .split(GlobalConst.separator)
                                .reduce((res, j) => {
                                    res = res.concat(FileSet.editForm.getSingleImg({ value: j }))
                                    return res
                                }, [])
                        }
                        break
                    // 单附件
                    case 'attach':
                    // 多附件
                    case 'attachMulti':
                        /**
                         * 获取附件对象
                         * @param {Object} item { name:'键名',value:{ ...第一个附件对象, otherIds: '多附件时会返回该属性，值为attachId1,attachId2,attachId3' }}
                         * @returns {Object} 拥有完整属性的文件对象
                         */
                        _itemValue = (item) => {
                            if (!item?.value) return
                            return FileSet.editForm.transferData(item.value)
                        }
                        break
                    // 默认
                    default:
                    // do something
                }
                if (!_itemValue) return
                // 更新结果集
                _res = Object.assign({}, _res, { [i.name]: _itemValue })
            })
            return _res
        },
        // 获取表格表头数据
        _fieldList() {
            // 定义结果列表
            let resultList = []
            // 过滤隐藏字段
            let _list = this.fieldList.filter(i => ShowStatus(i))
            _list.forEach(i => {
                // 获取动态js中的自定义格式化字段对象
                let _fieldObj = this.getFormatterFieldObj(i) || {}
                // 获取动态js中的自定义格式化列表表头字段
                let _fieldHeaderObj = this.getFieldHeader(i) || {}
                // 若存在有值，则格式为：
                // {
                //     formatter: function (row, column, cellValue, index){},
                //     click: function (row, column, cellValue, index) {},
                //     template: []
                // }
                resultList.push({
                    ...i,
                    // 字段键
                    name: i.name,
                    // 字段标签名
                    label: i.display,
                    // 列表字段宽度
                    width: i.listWidth || _fieldObj.listWidth,
                    // 列表字段最小宽度
                    minWidth: i.minWidth,
                    // 标签自定义展示
                    formatter: _fieldObj?.formatter,
                    // 事件自定义
                    click: _fieldObj?.click,
                    // 自定义标签与事件
                    render: _fieldObj?.render,
                    // 表头格式化-formatter模式
                    headerFormatter: _fieldHeaderObj?.formatter,
                    // 表头格式化-render模式
                    headerRender: _fieldHeaderObj?.render,
                })
            })
            return resultList
        },
        /**
         * 存储初始按钮，不包含自定义传入的按钮，方便在自定义按钮事件中去获取默认按钮中的事件进行再改造
         */
        _defaultBtnList() {
            // 判断是否传入初始默认按钮
            if (this.defaultBtnList &&
                this.defaultBtnList.constructor === Array) {
                return this.defaultBtnList
            }
            // 默认返回基础模型按钮
            return Default_Btn_List
        }
    },
    methods: { // 定义函数
        /**
         * 这里核心的面向模型js中调用的，获取基础事件，所以只在最初始默认的按钮事件中匹配
         * 场景:需要重写按钮事件，但核心的逻辑又是一致的只是修改少量代码时就可以通过当前函数快速获取默认事件，执行后再添加自己的少量业务代码
         * @param {String} id 函数id
         */
        getBaseBtnFun(id) {
            return Get_Btn_ClickFun(id, this._defaultBtnList)
        },
        // 获取按钮区右侧固定插槽
        async getBtnFixedRightSlot() {
            if (!(this.diy_btnFixedRightSlot && typeof this.diy_btnFixedRightSlot === 'function')) return
            this.btnFixedRightSlot = await this.diy_btnFixedRightSlot.call(this)
        },
        // 获取按钮区下一行的插槽位
        async getBtnNextLineSlot() {
            if (!(this.diy_btnNextLineSlot && typeof this.diy_btnNextLineSlot === 'function')) return
            this.btnNextLineSlot = await this.diy_btnNextLineSlot.call(this)
        },
        // 获取搜索区右侧固定插槽
        async getSearchFixedRightSlot() {
            if (!(this.diy_searchFixedRightSlot && typeof this.diy_searchFixedRightSlot === 'function')) return
            this.searchFixedRightSlot = await this.diy_searchFixedRightSlot.call(this)
        },
        // 获取列表顶部/搜索框下部插槽
        async getListTopSlot() {
            if (!(this.diy_listTopSlot && typeof this.diy_listTopSlot === 'function')) return
            this.listTopSlot = await this.diy_listTopSlot.call(this)
        },
        // 返回列表页面作用域
        listPageRef() {
            return this.$refs[this.listRefName]
        },
        // 获取列表页面-表格作用域
        // TODO:这里修改成使用组件里面的方法获取作用域，不直接指定list
        listRef() {
            return this.listPageRef()?.$refs?.list
        },
        // 返回表格列表选中项数组
        getSelection() {
            return this.listPageRef().tempSelection || this.listRef().selection
        },
        // 搜索条件change事件
        searchConditionChange(fieldName) {
            if (this.diy_searchConditionChange && typeof this.diy_searchConditionChange === 'function') {
                /**
                 * 判断是否存在自定义-搜索条件变化事件，存在则执行函数
                 * @param {String} fieldName 字段键名
                 * @param {Array} searchFieldList 搜索条件数组
                 */
                this.diy_searchConditionChange.call(this, fieldName, this.searchFieldList)
                return
            }
        },
        /**
         * 过滤器选中事件
         * @params {Object} item 过滤器项
         */
        changeSearchFilter(item) {
            let {
                content, // 获取搜索参数数组字符串
            } = item || {}
            if (!content) return
            let searchParams = []
            try {
                searchParams = JSON.parse(content) || []
            } catch (e) {
                console.error(`转化搜索参数数组字符串出现错误:${e}`)
            }
            // 搜索参数无值，则清空值
            if (searchParams.length === 0) {
                this.searchFieldList.forEach(i => i.value = null)
            } else {
                // 若搜索参数有，则找到匹配项，逐一赋值
                this.searchFieldList.forEach(i => {
                    let currentItem = searchParams.find(j => j.name === i.id)
                    if (currentItem) {
                        i.value = currentItem.value
                    } else {
                        i.value = null
                    }
                })
            }
            // 执行搜索事件
            this.search()
        },
        resetSearchFilter(hasChoosebeforeReset) {
            // 如果过滤器之前有选中数据，则过滤器的重置需要连带搜索这里的重置
            if (hasChoosebeforeReset) {
                this.searchReset()
            }
        },
        /**
         * 获取格式化后字段对象数据
         * @param {Object} fieldObj 字段对象
         */
        getFormatterFieldObj(fieldObj) {
            // 获取模型配置的字段钻取url：drillUrl
            // 获取模型配置的自定义字段属性：customOptions
            // 获取模型配置的字段列表展示类型: type
            let { drillUrl, customOptions, type } = fieldObj
            // 判断是否存在自定义js设置字段格式化
            // 优先级最高
            if (this.diy_renderField && this.diy_renderField[fieldObj.name]) {
                // 获取formatter,click,render
                let { formatter, click, render } = this.diy_renderField?.[fieldObj.name]
                // render模块优先级最高
                if (render) return { render }
                // 根据钻取地址定义钻取事件
                let drillClick = drillUrl ? (row, column, cellValue, index, fieldObj) => {
                    Jump_Drill_Url.call(this, { drillUrl }, row)
                } : null
                // 定义获取当前事件： 自定义click优先，其次才是钻取
                let _click = click || drillClick
                // 定义返回结果集
                let result = {}
                // 存在formatter，则更新结果集-标签格式展示
                if (formatter) result.formatter = formatter
                // 存在事件，则更新结果集-事件
                if (_click) result.click = _click
                return result
            }
            // 排除上面的情况，则这里根据钻取地址或自定义配置项模拟出渲染模块
            // 获取customOption自定义配置项中对字段的配置
            let _type = 'list'
            let _fieldSetting = null
            try {
                _fieldSetting = customOptions && JSON.parse(customOptions)?.[_type]
            } catch (e) {
                console.error(`处理customOptions数据转化发生异常`)
            }
            if (drillUrl || _fieldSetting) {
                // 获取渲染内容
                return this.getRender(drillUrl, _fieldSetting)
            }
            if (!type) return {}
            // 存在type，定义结果值_res
            let _res = {}
            // 存储作用域
            let that = this
            switch (type) {
                // 单图
                case 'imgSingle':
                // 多图
                case 'img':
                    /**
                     * 字段的render渲染
                     * @param {Array} cellValue 图片数组
                     */
                    _res.render = function (h, context, row, column, cellValue, index, fieldObj, scope) {
                        // 值无意义，返回默认展示
                        if (!(cellValue && cellValue.constructor === Array && cellValue.length > 0)) {
                            return (<span>{GlobalConst.view.value}</span>)
                        }
                        // 图片数大于1则需要显示【更多】的字样,点击后再出现完整图片列表。列表上默认只展示一张
                        let _showMore = cellValue.length > 1
                        let _data = [cellValue[0]]
                        return (
                            <div class="img-show">
                                <Preview v-model={_data} size="40"></Preview>
                                <span class="s-more" vOn:click_stop_prevent={() => {
                                    // that: 指向moduleList/index.vue页面作用域
                                    // this：指向mList/index.vue页面作用域
                                    that.showMore.call(that, cellValue, 'img')
                                }}>{_showMore ? '更多' : ''}</span>
                            </div>
                        )
                    }
                    break
                // 单附件:返回的数据格式为{ ...附件信息 }
                case 'attach':
                // 多附件:返回的数据格式为{ ...第一个附件信息, otherIds }
                case 'attachMulti':
                    /**
                     * 字段的render渲染
                     * @param {Array} cellValue {Object} 附件对象(单附件/多附件都是返回附件对象，多附件会多一个otherIds属性，表示其他附件id值)
                     */
                    _res.render = function (h, context, row, column, cellValue, index, fieldObj, scope) {
                        // 值无意义，返回默认展示
                        if (!(cellValue && cellValue.constructor === Object)) {
                            return (<div class="textL">{GlobalConst.view.value}</div>)
                        }
                        // 含有属性otherIds且有值，表示为多附件且有更多值
                        let _showMore = !!cellValue?.otherIds
                        return (
                            <div class="attach-show">
                                <div class="attach-show__main">
                                    <bd-icon
                                        name="import-fill"
                                        class="s-download h-c-primary"
                                        vOn:click_stop_prevent={() => {
                                            that.downloadAttach.call(that, cellValue)
                                        }}>
                                    </bd-icon>
                                    <div
                                        class="s-text text-o-1 h-c-primary"
                                        title={cellValue?.attachName}
                                        vOn:click_stop_prevent={() => {
                                            that.previewAttach(cellValue)
                                        }}>
                                        {cellValue.attachName}
                                    </div>
                                    <span
                                        class={'s-more ' + (!_showMore ? 'none' : '')}
                                        vOn:click_stop_prevent={() => {
                                            that.showMore(cellValue, 'attach', { formId: row.id, fieldName: column.property })
                                        }}>{_showMore ? '更多' : ''}</span>
                                </div>
                                <Progress
                                    value={cellValue.downloadPercent}
                                    seen={cellValue.downloading}>
                                </Progress>
                            </div>
                        )
                    }
                    // 设置默认的附件列表宽度
                    _res.listWidth = 300
                    break
                // 默认
                default:
                // do something
            }
            return _res
        },
        /**
         * 查看更多文件
         * @param {Array} data 展示数据
         * @param {String} type 文件类型 img / attach
         * @param {Object} setting 更多配置项
         */
        showMore(data, type, setting) {
            let _data = null
            switch (type) {
                case 'img':
                    _data = data
                    break
                case 'attach':
                    // 获取字段键名 fieldName
                    // 获取该条数据id formId
                    let { fieldName, formId } = setting || {}
                    // 更新值为Promise格式
                    _data = FileSet.editForm.getList({ name: fieldName }, formId)
                    break
                default:
                // do something
            }
            // 启用弹窗展示完整数据
            this.$pageDialog.init({
                title: '查看更多', // 弹窗标题
                pageUrl: '/common/file/FileView', // 弹窗显示的页面路径，会拼接src/views
                outScope: this, // 默认这样写即可，不要问
                isAutoFix: true, // 弹窗宽度根据内容自适应
                data: _data, // 展示数据
                type, // 类型
            })
        },
        /**
         * 下载附件
         * @param {Object} item 附件对象
         */
        downloadAttach(item) {
            Download_Item_Animation(item)
        },
        /**
         * 预览附件
         * @param {Object} item 附件对象
         */
        previewAttach(item) {
            let { name, url } = item
            // 判断是否允许预览
            if (Allow_Preview(item)) {
                Show_Preview(url, name)
                return
            }
            // 不支持预览的则使用下载的逻辑进行
            this.downloadAttach(item)
        },
        /**
         * 获取渲染内容
         * @param {String} drillUrl 钻取地址
         * @param {Object} fieldSetting 字段配置数据
         */
        getRender(drillUrl, fieldSetting) {
            let render = (h, context, row, column, cellValue, index, fieldObj) => {
                // 字段没有值的话，返回默认展示文本
                if (!Has_Value(cellValue)) return <span>{GlobalConst.table.value}</span>
                return Render_Field.call(this, h, fieldSetting, drillUrl, cellValue, ({ event }) => {
                    // 阻止事件冒泡，避免表格行被选中
                    event.stopPropagation()
                    // 钻取跳转事件
                    Jump_Drill_Url.call(this, fieldObj, row)
                }, fieldObj, row)
            }
            // 返回对象数据
            return {
                render
            }
        },
        /**
         * 获取格式化后字段对象数据
         * @param {Object} fieldObj 字段对象
         */
        getFieldHeader(fieldObj) {
            // 获取当前字段对象下对应的动态格式化表头数据
            let _renderFieldHeader = this.diy_renderFieldHeader && this.diy_renderFieldHeader[fieldObj.name]
            if (!_renderFieldHeader) return
            // 获取两种模式
            let { render, formatter } = _renderFieldHeader
            // 优先使用render
            if (render) return { render }
            if (formatter) return { formatter }
        },
        // 搜索函数
        search() {
            // 将搜索项参数更新给params：用于列表请求所需参数
            this.updateParams('searchParam', JSON.stringify(this.searchParams))
            // 需要使用nextTick等待列表页面params更新后才能请求列表
            this.$nextTick(() => {
                // 默认搜索方法
                this.$refs[this.listRefName].init(...arguments)
            })
        },
        // 重置函数
        searchReset() {
            // 执行过滤器的搜索事件
            this.$refs.searchFilter?.reset()
            // 初始将搜索项的值value已经处理存储与对象的属性defaultValue
            // 此时使用重置将重新将defaultValue赋值给value
            this.searchFieldList.forEach((i, index) => {
                this.$set(i, 'value', i.defaultValue)
            })
            // 将搜索项参数更新给params：用于列表请求所需参数
            this.updateParams('searchParam', JSON.stringify(this.searchParams))
            // 需要使用nextTick等待列表页面params更新后才能请求列表
            this.$nextTick(() => {
                this.$refs[this.listRefName].reset()
            })
        },
        /**
         * 更新列表数据请求参数
         * @param {String} key:键
         * @param {*} value：值
         */
        updateParams(key, value) {
            // 不能使用this.params[key]=value,无法监听动态属性变化
            this.$set(this.params, key, value)
        },
        /**
         * 根据指定键-删除-列表数据请求参数
         * @param {String} key:键
         * @param {*} value：值
         */
        removeParam(key) {
            delete this.params[key]
        },
        /**
         * 使用自定义js配置项更新页面数据
         * @param {Object} customSetting 动态js配置对象
         */
        updateCustomSetting(customSetting) {
            // 获取动态自定义js文件customSetting
            let {
                buttons,  // 自定义功能按钮 {Array}
                diySearch,  // 自定义搜索事件
                hideSearch, // 隐藏搜索
                hideSearchFilter, // 隐藏搜索过滤器
                dataUrl,  // 自定义列表数据请求url
                paramsBeforeRequest, // 请求前自定义更改参数
                searchConditionChange,   // 搜索条件变化监听事件
                renderColumn,  // 【旧版本，不推荐使用】自定义表格列字段编辑
                renderField,  // 【推荐使用】自定义表格列字段编辑
                renderFieldHeader,  // 自定义表格-字段表头
                afterListJSON,  // 请求列表数据之后需要执行的事件
                afterListRender, // 列表渲染后需要执行的事件
                createFun, // 自定义创建的函数(作用域在mList/index.vue)
                createListFun, // 自定义创建的函数(作用域在moduleList/index.vue)
                exportUrl, // 自定义导出事件接口地址
                exportTitle, // 自定义导出事件标题
                exportDialogTitle, // 自定义导出事件弹窗标题(2022-10-22：TODO 即将废弃)
                exportProps, // 自定义导出事件配置信息
                exportDefined, // 导出的禁用字段
                importUrl, // 自定义导入事件接口地址
                importTemplateUrl, // 自定义导入的模版地址
                importTitle, // 自定义导入事件标题
                importKey, // 自定义导入事件的参数
                importProps, // 自定义导入事件配置信息
                multiple, // 自定义表格是否支持多选
                dblClick, // 自定义表格行双击事件
                isUsePagination, // 自定义是否启用分页功能
                isShowPagination,  // 自定义是否显示分页组件（不决定功能，只决策展示）
                searchBtns, // 自定义搜索栏按钮
                freezeColumnIndex, // 自定义前置冻结列
                freezeColumnLastIndex, // 自定义后置冻结列
                showSummary, // 自定义展示合并行
                selectionChange, // 当选择项发生变化时会触发该事件
                columnFormat, // 合并表头/修改表头
                btnNextLineSlot, // 按钮区下面的插槽位
                searchFixedRightSlot, // 搜索区右侧固定插槽
                btnFixedRightSlot, // 按钮区右侧固定插槽
                listTopSlot, // 列表顶部/搜索框下部插槽
                spanMerge, // 表格行列合并
                ...elseTableAttrs // 其余参数
            } = customSetting || {}
            this.elseTableAttrs = elseTableAttrs
            // 自定义是否启用分页功能
            this.diy_isUsePagination = isUsePagination,
                // 自定义是否显示分页组件（不决定功能，只决策展示）
                this.diy_isShowPagination = isShowPagination,
                // 更新-自定义功能按钮
                this.diy_buttons = buttons
            // 更新-自定义搜索事件
            this.diy_search = diySearch
            // 隐藏搜索区
            this.diy_hideSearch = hideSearch,
                // 隐藏搜索过滤器
                this.diy_hideSearchFilter = hideSearchFilter,
                // 更新-自定义列表请求数据url
                this.diy_dataUrl = dataUrl
            // 更新-请求前自定义更改参数
            this.diy_paramsBeforeRequest = paramsBeforeRequest
            // 更新-搜索条件变化监听事件
            this.diy_searchConditionChange = searchConditionChange
            // 更新-自定义表格列字段编辑
            this.diy_renderField = renderField || renderColumn
            // 更新-自定义表格-字段表头
            this.diy_renderFieldHeader = renderFieldHeader
            // 更新-请求列表数据之后需要执行的事件
            this.diy_afterListJSON = afterListJSON
            // 更新-列表渲染之后需要执行的事件
            this.diy_afterListRender = afterListRender
            // 更新-自定义创建的函数(作用域在mList/index.vue)
            this.diy_createFun = createFun
            // 更新-自定义创建的函数(作用域在moduleList/index.vue)
            this.diy_createListFun = createListFun

            // 自定义-导出事件接口地址
            this.diy_exportUrl = exportUrl
            // 自定义-导出事件标题
            this.diy_exportTitle = exportTitle
            // 自定义-导出事件弹窗标题(2022-10-22：TODO 即将废弃)
            this.diy_exportDialogTitle = exportDialogTitle
            // 自定义-导出事件配置
            this.diy_exportProps = exportProps
            // 自定义-导出的禁用字段
            this.diy_exportDefined = exportDefined
            // 自定义-导入事件接口地址
            this.diy_importUrl = importUrl
            // 自定义-导入的模版地址
            this.diy_importTemplateUrl = importTemplateUrl,
                // 自定义-导入事件标题
                this.diy_importTitle = importTitle
            // 自定义-导入事件的参数
            this.diy_importKey = importKey
            // 自定义-导入事件配置
            this.diy_importProps = importProps
            // 自定义-表格是否支持多选
            this.diy_multiple = multiple
            // 自定义-表格行双击事件
            this.diy_dblClick = dblClick
            // 自定义-搜索栏按钮
            this.diy_searchBtns = searchBtns
            // 自定义-前置冻结列
            this.diy_freezeColumnIndex = freezeColumnIndex
            // 自定义-后置冻结列
            this.diy_freezeColumnLastIndex = freezeColumnLastIndex
            // 自定义-合并行
            this.diy_showSummary = showSummary
            // 自定义-当选择项发生变化时会触发该事件
            this.diy_selectionChange = selectionChange
            // 自定义-合并表头/修改表头
            this.diy_columnFormat = columnFormat
            // 自定义-按钮区下一行的插槽位
            this.diy_btnNextLineSlot = btnNextLineSlot
            // 自定义-搜索区右侧固定插槽
            this.diy_searchFixedRightSlot = searchFixedRightSlot
            // 自定义-按钮区右侧固定插槽
            this.diy_btnFixedRightSlot = btnFixedRightSlot
            // 自定义列表顶部/搜索框下部插槽
            this.diy_listTopSlot = listTopSlot
            // 自定义-表格行列合并
            this.diy_spanMerge = spanMerge
        },
        // 表格项双击事件，默认为该行的编辑事件，标识符为edit
        dblClick(row, column, cell, event, scope) {
            // 清空列表中所有行的选中状态
            this.listRef().clearSelection()
            // 设置当前行为选中
            this.listRef().toggleRowSelection(row)
            // 如果隐藏按钮，则双击事件将不生效，双击的规则是简便使用某一个按钮的事件，按钮隐藏则双击不再作用
            // 2022-08-29需求变更：存在场景，因为只有一个查看按钮太占空间，所以就隐藏整个按钮栏，但希望表格字段上面的点击事件依旧可以
            //     所以按钮隐藏的状态不再作为局限，按钮事件的是否可执行决策权交给按钮数组(非隐藏状态的按钮项)
            // 使用nextTick等待选中状态渲染后再进行逻辑，避免获取不到选中数据
            this.$nextTick(() => {
                // 判断是否有自定义双击事件
                if (this.diy_dblClick && typeof this.diy_dblClick === 'function') {
                    /**
                     * 执行自定义表格行的双击事件
                     * @param {Array} 按钮数组
                     */
                    this.diy_dblClick.call(this, this.btnList)
                    return
                }
                // 双击默认走【编辑(优先)/查看】事件, 获取按钮对象
                let editBtn = this.btnList.find(i =>
                    ShowStatus(i) &&
                    i.id === 'edit' ||
                    i.id === 'view'
                )
                // 执行编辑edit事件
                editBtn && editBtn.click.call(this, editBtn)
            })
        },
        // 获取按钮数组
        async getBtnList() {
            // 2021-07-16 之后使用以下：出现个场景，按钮的点击会影响其他按钮的显示隐藏，为了其他按钮可以操作到隐藏按钮，这里将不对按钮数据进行过滤，由html中去过滤隐藏项，确保按钮数据源拥有所有隐藏与显示的按钮组
            // 定义按钮结果集
            let result = Deep_Clone(this._defaultBtnList)
            // 判断是否存在自定义按钮
            if (this.diy_buttons) {
                // 1. 支持传入函数，函数需return回最终的按钮数据
                if (this.diy_buttons.constructor === Function) {
                    // 执行函数,获取函数返回的按钮组
                    // 执行深拷贝，避免地址引用导致异常
                    let _allBtns = await this.diy_buttons.call(this, result)
                    // 去除重复项
                    result = Unique_Array(_allBtns, 'id')
                }
                // 2. 支持直接传入按钮组，会自行将默认与自定义按钮进行合并
                if (this.diy_buttons.constructor === Array) {
                    // 合并按钮
                    result = Merge_Btn(result, this.diy_buttons)
                }
            }
            // 如果权限按钮存在，只显示权限按钮
            if (this.permissionBtnList) {
                result = this.permissionBtnList.map(o => {
                    let defaultItem = result.find(r => o.code === r.id) || {}
                    let resItem = {
                        ...defaultItem,
                        ...o,
                    }
                    return resItem
                })
            }
            // 按钮若含有特定的标识，并且没有配置右键菜单属性，则默认配置含有右键列表显示该按钮功能
            result.forEach(i => {
                if (['edit', 'view', 'delete'].includes(i.id) &&
                    !('rightMenu' in i)) {
                    i.rightMenu = true
                }
            })
            // 按钮排序key
            let sortKey = GlobalConst.button.sortKey
            // 获取指定按钮默认属性（根据id）
            let btnAttrs = GlobalConst.button.listAttrs
            // 非指定按钮默认排序
            let defaultIndex = GlobalConst.button.sortIndex
            result = result.map(b => {
                if (btnAttrs[b.id]) {
                    b = {
                        ...btnAttrs[b.id],
                        ...b
                    }
                }
                b[sortKey] = Has_Value(b[sortKey]) ? b[sortKey] : defaultIndex
                return b
            })
            result = Sort_List(result, sortKey)
            return result || []
            // 2021-07-16 之前使用
            // return Merge_Btn(_defaultBtnList, this.diy_buttons).filter(i => ShowStatus(i))

        },
        /**
         * 渲染页面的插槽区
         */
        renderSlot() {
            // 渲染按钮区下面的插槽位渲染
            this.getBtnNextLineSlot()
            // 渲染搜索区右侧固定插槽
            this.getSearchFixedRightSlot()
            // 渲染获取按钮区右侧固定插槽
            this.getBtnFixedRightSlot()
            // 渲染获取列表顶部/搜索框下部插槽
            this.getListTopSlot()
        }
    },
    // 可访问当前this实例
    created() { },
    // 挂载完成，可访问DOM元素
    async mounted() {
        if (this.mdCode == 'maas_fine_tuning_modeln') {
            this.timer = setInterval(() => {
                if (this.tableData.length > 0) {
                    //实时刷新字段
                    let idsArr = []
                    this.tableData.forEach((item, index) => {
                        if (item.doStatus == 1 || item.doStatus == 5 || item.doStatus == 6) {
                            for (const key in item) {
                                if ("id" == key) {
                                    idsArr.push(item[key])
                                }
                            }
                        }

                    })
                    this.post('/project/maas/tuningmodeln/tuningmodelnlist/getModelNewestStatus', idsArr,
                        {
                            headers: {
                                'Content-Type': 'application/json'
                            }
                        }
                    ).then((res) => {
                        if (res?.hasOk) {
                            res.bean.forEach((item, index) => {
                                this.tableData.forEach((row, index) => {
                                    if(row.id == item.id){
                                        row.costTime = item.costTime
                                        row.evaTotalCount = item.evaTotalCount
                                        row.evaTotalScore = item.evaTotalScore
                                        row.doStatus = item.doStatus
                                        row.doStatusDesc = item.doStatusDesc
                                        row.shelvesStatus = item.shelvesStatus
                                        row.shelvesStatusDesc = item.shelvesStatusDesc
                                    }
                                })
                            })

                        } else {
                            this.$message.error("无法计算字段!请联系管理员!")
                        }
                    }).finally(() => {
                    })
                }

            }, 2000)
        }
        if (this.mdCode == 'maas_model_app1') {
            this.timer = setInterval(() => {
                if (this.tableData.length > 0) {
                    //实时刷新字段
                    let itemArr = []
                    this.tableData.forEach((item, index) => {
                        if (item.status < 3) {
                            delete item.createTime
                            delete item.updateTime
                            delete item.startTime
                            itemArr.push(item)
                        }

                    })
                    this.post('/project/maas/modelapp/modelapplist/getModelNewestStatus', itemArr,
                        {
                            headers: {
                                'Content-Type': 'application/json'
                            }
                        }
                    ).then((res) => {
                        if (res?.hasOk) {
                            res.bean.forEach((item, index) => {
                                this.tableData.forEach((row, index) => {
                                    if(row.id == item.id){
                                        row.allRunTime = item.allRunTime
                                        // row.status = item.status
                                        // row.statusDesc = item.statusDesc
                                    }
                                })
                            })

                        } else {
                            this.$message.error("无法计算字段!请联系管理员!")
                        }
                    }).finally(() => {
                    })
                }

            }, 10000)
        }
        if (this.mdCode == 'maas_server_gpu') {
            if(!this.$route.params.serverId){
                this.timer = setInterval(() => {
                if (this.tableData.length > 0) {
                    this.post('/project/server/servergpulist/getServerGpu',{
                        serverId: this.$route.params.id
                    }).then((res) => {
                        if (res.Rows) {
                            this.tableData = res.Rows
                        } else {
                            this.$message.error("无法计算字段!请联系管理员!")
                        }
                    }).finally(() => {
                    })
                }

            }, 2000)
            }
        }
        // 获取完整搜索配置数据
        // 如果配置是hideSearch为true，意味着不需要搜索栏数据，不能单独只设置搜索框隐藏，还需要设置数据的清空，不然请求时依旧会携带含有默认值的搜索框参数进行请求
        let _searchFieldList = (!this.hideSearch && await GetSearchFieldList(this.searchCondition, this.selectorData)) || []
        // 判断父组件是否传入搜索栏参数
        let searchParamList = this.searchParam && Object.keys(this.searchParam) || []
        if (searchParamList && searchParamList.length > 0) {
            searchParamList.forEach(i => {
                // 在已有的搜索栏数据中查找匹配项
                let item = _searchFieldList.find(j => j.id === i)
                if (item) {
                    // 若匹配项存在，则更新其值
                    item.value = this.searchParam[i]
                } else {
                    // 若匹配项不存在，则添加一个隐藏项，设置其值
                    _searchFieldList.push({
                        // 键名
                        id: i,
                        // 值
                        value: this.searchParam[i],
                        // 设置为隐藏状态
                        isHide: true
                    })
                }
                // 请求列表时会从搜索栏数据中获取所有有值的参数作为参数进行请求
            })
        } else {
            // 若当前搜索栏无值，无法匹配修改值，则设置所有传入值为隐藏状态，并且添加到搜索栏中
            searchParamList.forEach(i => {
                _searchFieldList.push({
                    // 键名
                    id: i,
                    // 值
                    value: this.searchParam[i],
                    // 设置为隐藏状态
                    isHide: true
                })
            })
        }
        // 更新搜索栏数据
        this.searchFieldList = _searchFieldList
        // // 将搜索项参数更新给params：用于列表请求所需参数
        this.updateParams('searchParam', JSON.stringify(this.searchParams))
        // 使用自定义js配置项更新页面数据
        this.updateCustomSetting(this.customSetting)
        // 可以准备请求数据加载列表
        this.tableParamsReady = true
        // 更新页面按钮数组
        this.btnList = await this.getBtnList()
        // 开始渲染页面的插槽区
        this.renderSlot()
        typeof this.mounted === 'function' && this.mounted()
    },
    beforeDestroy() {
        if (this.timer != null) {
            clearInterval(this.timer)
        }
        typeof this.beforeDestroy === 'function' && this.beforeDestroy()
    },
    watch: {
        // 更新默认列表数据请求参数
        defaultParamsObj: {
            immediate: true, // 立即触发监听
            // 树型模型中使用:点击节点时变更参数，这里需要深度才能监听并且更新参数
            // 但：我觉得有点异常 TODO，逻辑不优雅
            deep: true,
            handler: function (newVal, oldVal) {
                if (!(newVal && newVal.constructor === Object)) return
                Object.keys(newVal).forEach(i => {
                    this.updateParams(i, newVal[i])
                })
            }
        },
        // 是否启动刷新列表[keep-alive缓存组件时]
        //     为避免从列表跳转编辑页面，编辑修改数据时列表能实时响应，刷新缓存数据
        //     添加该状态执行刷新列表数据
        isStartReList: {
            immediate: true,
            handler: function (newVal, oldVal) {
                // newVal:确保状态为true,表示当前需要刷新数据
                // this.tableParamsReady：确保当前模式是keep-alive
                if (newVal && this.tableParamsReady) {
                    // 触发列表请求，刷新页面数据
                    this.$refs[this.listRefName].init()
                }
            }
        },
        // 监听路由配置信息meta中是否含有hasList属性
        //     进入当前页面时回复悬浮提示tooltip
        //     离开当前页面时关闭悬浮提示tooltip
        '$route.meta': function (val) {
            this.closeAllToolTip = !(val && val.hasList)
        },
        // 将模型配置的js的自定义方法注册到当前页面（createListFun: 来源模型配置js文件中）
        diy_createListFun: {
            deep: true,
            handler: function (newVal, oldVal) {
                // 若存在则注册
                if (newVal) {
                    Object.keys(newVal).forEach(i => {
                        this[i] = newVal[i]
                    })
                }
            }
        }
    }
}
</script>
<style lang='scss' scoped>
.f-v {
    display: flex;
    flex-direction: column;
}

.bd-module-list::v-deep {
    @extend .f-v;
    overflow: hidden;

    .bd-module-list__title,
    .s-filter,
    .m-search {
        flex: none;
    }

    .bd-module-list__title {
        margin: -10px -10px 5px -10px;
    }

    .bd-module-list__main {
        @extend .f-v;
        flex: 1;
        overflow: hidden;
        border: 1px solid $tableLineColor;
        border-radius: $borderRadius;

        .bd-module-list__btn {
            // start-为了处理按钮右侧的插槽
            display: flex;
            flex-wrap: wrap; // 按钮不够放时换行展示
            // end-为了处理按钮右侧的插槽
            flex: none;
            padding: 0 $padding;
            padding-bottom: $space;
            border-bottom: 1px solid $tableLineColor;

            .bd-module-list__btn__item {
                margin-top: $space;
                margin-right: $space;
                margin-left: 0 !important;
            }

            .btn-fixed-right-slot {
                text-align: right;
                flex: 1;
                align-items: center;
                margin-bottom: -$space;
                display: flex;
                flex-direction: row-reverse;
            }
        }

        .bd-module-list__list {
            flex: 1;

            .el-table {
                td {
                    .cell {
                        .img-show {
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            min-width: 100px;
                            position: relative;

                            .s-more {
                                display: inline-block;
                                font-size: $fontS;
                                color: $primary;
                                cursor: pointer;
                                width: 34px;
                                position: absolute;
                                left: 50%;
                                transform: translateX(60%);
                            }
                        }

                        .attach-show {
                            .attach-show__main {
                                display: flex;
                                align-items: center;

                                .s-download {
                                    font-size: $fontS;
                                    flex: 0 0 16px;
                                    margin-right: 4px;
                                }

                                .s-text {
                                    text-align: left;
                                    flex: 0 1 auto;
                                }

                                .s-more {
                                    flex: 0 0 34px;
                                    display: inline-block;
                                    font-size: $fontS;
                                    color: $primary;
                                    cursor: pointer;
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}
</style>