// 新的时代已经来临
<template>
    <div class="bd-module-list defaultBg padding h-per-100">
        <m-title
            v-if="!noTitle && title"
            class="bd-module-list__title"
            :title="title || name">
        </m-title>
        <search-filter
            v-if="!hideFilter && searchFieldList && searchFieldList.length && tableParamsReady"
            class="bd-module-list__filter"
            ref="searchFilter"
            :mdCode="mdCode"
            :searchParam="params.searchParam"
            @change="changeSearchFilter"
            @reset="resetSearchFilter">
        </search-filter>
        <m-search
            v-if="!hideSearch"
            class="bd-module-list__search"
            :data="searchFieldList"
            :list-scope="this"
            :btnList="diy_searchBtns"
            @change="searchConditionChange"
            @search="search"
            @reset="searchReset">
        </m-search>
        <div class="bd-module-list__main">
            <div class="bd-module-list__btn" v-if="!hideBtn && btnList.length > 0">
                <template v-for="(i, index) in btnList">
                    <bd-button
                        v-if="ShowStatus(i)"
                        class="bd-module-list__btn__item"
                        :key="index"
                        :plain="true"
                        :fillIcon="true"
                        v-bind="i"
                        @click="handleClick(i)">
                    </bd-button>
                </template>
            </div>
            <bd-table-list
                v-if="tableParamsReady"
                class="bd-module-list__list"
                :data.sync="tableData"
                :sortObj.sync="sortObj"
                v-bind="tableAttrs"
                v-on="$listeners"
                @cellDblClick="dblClick">
            </bd-table-list>
        </div>
    </div>
</template>
<script>
import MSearch from '@/components/frame/Common/MSearch/index'
import SearchFilter from '@/components/frame/Module/SearchFilter'
import BdTableList from '@/components/frame/Common/MList/index.vue'
import { Has_Value, Sort_By_Prop } from '@/utils/index'
import Default_Btn_List from './button'
import { Merge_Btn } from '@/service/module'
import { Module_List_URL } from '@/api/frame/common'
import GlobalConst from '@/service/global-const'
import globalStyle from '@/styles/theme.scss'
import { Find_Parent_By_ClassName, Find_By_ClassName } from '@/utils/find-dom'
import { ShowStatus, HandleClick, GetSearchFieldList } from '@/service/module'
import { Jump_Drill_Url, Render_Field } from '@/service/base-service.js'
import MTitle from '@/components/frame/Common/MTitle'
import { Unique_Array } from '@/utils/list'
export default {
    name: "module-list",
    // 禁止父级传入属性作为当前组件的根标签属性
    inheritAttrs: false,
    components: {
        MSearch,
        BdTableList,
        [MTitle.name]: MTitle,
        [SearchFilter.name]: SearchFilter
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
        // 隐藏过滤器
        hideFilter: {
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
        // 开启后，将会设置
        fullHeight: {
            type: Boolean,
            default: false
        },
        // 额外参数:存储不属于当前组件业务的参数；数据中转站
        elseAttrs: {
            type: Object
        }
    },
    data () { // 定义页面变量
        return {
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
            // 设置表格高度时间器
            setTableHeightTimer: null,
            // 列表铺满当前组件时高度值
            fullTableHeight: null,
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
            // 自定义列表请求数据url
            diy_dataUrl: null,
            // 请求前自定义更改参数
            diy_paramsBeforeRequest: null,
            // 搜索条件变化监听事件
            diy_searchConditionChange: null,
            // 自定义表格列字段编辑
            diy_renderField: null,
            // 请求列表数据之后需要执行的事件
            diy_afterListJSON: null,
            // 列表渲染后需要执行的事件
            diy_afterListRender: null,
            // 自定义创建的函数
            diy_createFun: null,
            // 自定义-导出事件接口地址
            diy_exportUrl: null,
            // 自定义-导出事件弹窗标题
            diy_exportDialogTitle: null,
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
            // 当选择项发生变化时会触发该事件
            diy_selectionChange: null,
            // 合并表头/修改表头
            diy_columnFormat: null
        }
    },
    computed: {
        // 获取页面地址
        pageUri () {
            // fullPath包含查询部分
            return this.$route.fullPath
        },
        // 获取列表组件Ref实例
        tableListRef () {
            return this.$refs?.[this.listRefName]
        },
        // 获取列表最终数据请求的参数
        finalListParams () {
            return this.tableListRef?.temp_params
        },
        // 获取列表对象属性
        tableAttrs () {
            return {
                ref: this.listRefName,
                url: this._url,
                height: this.tableHeight || this.fullTableHeight,
                isSelection: this.isSelection,
                fieldList: this._fieldList,
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
                closeAllToolTip: this.closeAllToolTip,
                selectionChange: this.diy_selectionChange,
                columnFormat: this.diy_columnFormat,
                elseAttrs: this.elseAttrs,
                ...this.elseTableAttrs
            }
        },
        // 用于处理搜索栏提供的搜索参数【转化参数数组数据】
        searchParams () {
            // 传入参数：搜索栏参数是否生效，作用于请求列表数据
            if (!this.searchAbled) return
            let params = this.searchFieldList.filter(i => Has_Value(i.value))
                .map(i => {
                    return {
                        name: i.id,
                        // 值为字符串时trim()去除前后空格
                        value: typeof i.type === 'string' ? i.value.trim() : i.value,
                        // 自定义传入的搜索类型queryType
                        type: i.queryType || this.searchType(i.type),
                        // 当前搜索字段同时支持的字段搜索，格式如'name,sex'，输入框输入值将会作为这些字段中（或的关系）一起去查询
                        tagName: i.tagName || ''
                    }
                })
            return params
        },
        // 当父组件传入表格高度tableHeight时，fullHeight属性将失效，因为fullHeight核心逻辑是在计算表格高度
        isfullHeight () {
            return this.fullHeight && !this.tableHeight
        },
        // 获取列表数据请求地址（优先级：自定义 > 父组件 > 默认）
        _url () {
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
            get () {
                return this.data
            },
            set (val) {
                this.$emit('update:data', val)
            }
        },
        // 获取表格表头数据
        _fieldList () {
            // 定义结果列表
            let resultList = []
            // 过滤隐藏字段
            let _list = this.fieldList.filter(i => ShowStatus(i))
            _list.forEach(i => {
                // 获取动态js中的自定义格式化字段对象
                let _fieldObj = this.getFormatterFieldObj(i) || {}
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
                    width: i.listWidth,
                    // 列表字段最小宽度
                    minWidth: i.minWidth,
                    // 标签自定义展示
                    formatter: _fieldObj?.formatter,
                    // 事件自定义
                    click: _fieldObj?.click,
                    // 自定义标签与事件
                    render: _fieldObj?.render,
                })
            })
            return resultList
        },
    },
    methods: { // 定义函数
        // 返回列表页面作用域
        listPageRef () {
            return this.$refs[this.listRefName]
        },
        // 获取列表页面-表格作用域
        // TODO:这里修改成使用组件里面的方法获取作用域，不直接指定list
        listRef () {
            return this.listPageRef()?.$refs?.list
        },
        // 返回表格列表选中项数组
        getSelection () {
            return this.listRef().selection
        },
        // 搜索条件change事件
        searchConditionChange (fieldName) {
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
        changeSearchFilter (item) {
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
        resetSearchFilter (hasChoosebeforeReset) {
            // 如果过滤器之前有选中数据，则过滤器的重置需要连带搜索这里的重置
            if (hasChoosebeforeReset) {
                this.searchReset()
            }
        },
        /**
         * 获取格式化后字段对象数据
         * @param {Object} fieldObj 字段对象
         */
        getFormatterFieldObj (fieldObj) {
            // 获取模型配置的字段钻取url：drillUrl
            // 获取模型配置的自定义字段属性：customOptions
            let { drillUrl, customOptions } = fieldObj
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
            return {}
        },
        /**
         * 获取渲染内容
         * @param {String} drillUrl 钻取地址
         * @param {Object} fieldSetting 字段配置数据
         */
        getRender (drillUrl, fieldSetting) {
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
         * @desciption: 根据搜索类型返回搜索关键词
         * @param {Number} type:搜索类型
         * @return: {String} 搜索类型下对应的搜索词
         */
        searchType (type) {
            let typeWord = ''
            switch (type) {
                // 下拉多选 || 其他搜索
                case 0:
                case 15:
                    typeWord = 'other-query'
                    break
                // 文本框 || 模糊搜索
                case 1:
                    typeWord = 'text-query'
                    break
                // 日期选择 || 日期搜索
                case 5:
                    typeWord = 'date-query'
                    break
                // 数字区间
                case 6:
                    typeWord = 'number-scope-query'
                    break
                // 单选框 / 精准搜索
                case 9:
                    typeWord = 'exact-match'
                    break
                // 通常没有type类型的表示是由其他页面传递过来的参数，其属性是不带type的
                // 一般为钻取事件携带，对数据可以使用精准搜索
                // 默认:精准搜索
                default:
                    typeWord = 'exact-match'
            }
            return typeWord
        },
        // 搜索函数
        search () {
            // 将搜索项参数更新给params：用于列表请求所需参数
            this.updateParams('searchParam', JSON.stringify(this.searchParams))
            // 需要使用nextTick等待列表页面params更新后才能请求列表
            this.$nextTick(() => {
                // 默认搜索方法
                this.$refs[this.listRefName].init()
            })
        },
        // 重置函数
        searchReset () {
            // 执行过滤器的搜索事件
            this.$refs.searchFilter.reset()
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
        updateParams (key, value) {
            // 不能使用this.params[key]=value,无法监听动态属性变化
            this.$set(this.params, key, value)
        },
        /**
         * 根据指定键-删除-列表数据请求参数
         * @param {String} key:键
         * @param {*} value：值
         */
        removeParam (key) {
            delete this.params[key]
        },
        /**
         * 使用自定义js配置项更新页面数据
         * @param {Object} customSetting 动态js配置对象
         */
        updateCustomSetting (customSetting) {
            // 获取动态自定义js文件customSetting
            let {
                buttons,  // 自定义功能按钮 {Array}
                diySearch,  // 自定义搜索事件
                hideSearch, // 隐藏搜索
                dataUrl,  // 自定义列表数据请求url
                paramsBeforeRequest, // 请求前自定义更改参数
                searchConditionChange,   // 搜索条件变化监听事件
                renderColumn,  // 【旧版本，不推荐使用】自定义表格列字段编辑
                renderField,  // 【推荐使用】自定义表格列字段编辑
                afterListJSON,  // 请求列表数据之后需要执行的事件
                afterListRender, // 列表渲染后需要执行的事件
                createFun, // 自定义创建的函数
                exportUrl, // 自定义导出事件接口地址
                exportDialogTitle, // 自定义导出事件弹窗标题
                multiple, // 自定义表格是否支持多选
                dblClick, // 自定义表格行双击事件
                isUsePagination, // 自定义是否启用分页功能
                isShowPagination,  // 自定义是否显示分页组件（不决定功能，只决策展示）
                searchBtns, // 自定义搜索栏按钮
                freezeColumnIndex, // 自定义前置冻结列
                freezeColumnLastIndex, // 自定义后置冻结列
                selectionChange, // 当选择项发生变化时会触发该事件
                columnFormat, // 合并表头/修改表头
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
            // 更新-自定义列表请求数据url
            this.diy_dataUrl = dataUrl
            // 更新-请求前自定义更改参数
            this.diy_paramsBeforeRequest = paramsBeforeRequest
            // 更新-搜索条件变化监听事件
            this.diy_searchConditionChange = searchConditionChange
            // 更新-自定义表格列字段编辑
            this.diy_renderField = renderField || renderColumn
            // 更新-请求列表数据之后需要执行的事件
            this.diy_afterListJSON = afterListJSON
            // 更新-列表渲染之后需要执行的事件
            this.diy_afterListRender = afterListRender
            // 更新-自定义创建的函数
            this.diy_createFun = createFun
            // 自定义-导出事件接口地址
            this.diy_exportUrl = exportUrl
            // 自定义-导出事件弹窗标题
            this.diy_exportDialogTitle = exportDialogTitle
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
            // 自定义-当选择项发生变化时会触发该事件
            this.diy_selectionChange = selectionChange
            // 自定义-合并表头/修改表头
            this.diy_columnFormat = columnFormat
        },
        // 表格项双击事件，默认为该行的编辑事件，标识符为edit
        dblClick (row, column, cell, event, scope) {
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
                // 双击默认走编辑事件
                // 获取编辑按钮对象
                let editBtn = this.btnList.find(i => i.id === 'edit' && ShowStatus(i))
                // 执行编辑edit事件
                editBtn && editBtn.click.call(this, editBtn)
            })
        },
        // 重置表格高度
        resetTableHeight () {
            // 按道理进入该函数时isfullHeight为true,中途异常变成false时，立即清除事件函数
            if (!this.isfullHeight) {
                this.setTableHeightTimer && clearInterval(this.setTableHeightTimer)
                return
            }
            try {
                // 定义表格所处在模块的展示高度(默认最小高度)
                let minHeight = parseInt(GlobalConst.table.height)
                // 获取列表模块ref
                let tableRef = this.$refs[this.listRefName]
                // ref尚未生成则直接return
                if (!tableRef) return false
                // 获取当前组件根标签DOM
                let ROOTEl = Find_Parent_By_ClassName(tableRef.$el, 'bd-module-list')
                // 标题
                let titleEl = Find_By_ClassName(ROOTEl, 'bd-module-list__title')
                // 搜索组件dom对象
                let searchbarEl = Find_By_ClassName(ROOTEl, 'bd-module-list__search')
                // 过滤器DOM对象
                let filterEl = Find_By_ClassName(ROOTEl, 'bd-module-list__filter')
                // 获取操作列表按钮DOM
                let btnEl = Find_By_ClassName(ROOTEl, 'bd-module-list__btn')
                // 获取分组组件DOM
                let paginationEl = Find_By_ClassName(ROOTEl, 'bd-pagination')
                // 边框高
                let borderHeight = 1
                // 获取铺满状态下table应该设置的高度
                // 指定父级高度 - 2倍间隔 - 搜索框高度 - 按钮区 - 底部分页
                let _tableHeight = ROOTEl?.clientHeight -
                    2 * parseInt(globalStyle.padding) -
                    (titleEl?.clientHeight - parseInt(globalStyle.padding) || 0) -
                    (filterEl?.clientHeight || 0) -
                    (searchbarEl?.clientHeight || 0) -
                    (btnEl?.clientHeight || 0) -
                    (paginationEl?.clientHeight || 0) -
                    borderHeight * 2
                // 更新页面表格铺满的高度值
                this.fullTableHeight = Math.max(_tableHeight, minHeight)
            } catch (e) {
                console.error(`计算表格自适应高度出现异常：${e}`)
                // 存在异常情况，直接清除时间函数
                clearInterval(this.setTableHeightTimer)
            }
        },
        // 获取按钮数组
        getBtnList () {
            // 定义初始默认按钮组
            let _defaultBtnList = Default_Btn_List
            // 判断是否传入初始默认按钮
            if (this.defaultBtnList &&
                this.defaultBtnList.constructor === Array) {
                _defaultBtnList = this.defaultBtnList
            }
            // 2021-07-16 之后使用以下：出现个场景，按钮的点击会影响其他按钮的显示隐藏，为了其他按钮可以操作到隐藏按钮，这里将不对按钮数据进行过滤，由html中去过滤隐藏项，确保按钮数据源拥有所有隐藏与显示的按钮组
            // 定义按钮结果集
            let result = _defaultBtnList
            // 判断是否存在自定义按钮
            if (this.diy_buttons) {
                // 1. 支持传入函数，函数需return回最终的按钮数据
                if (this.diy_buttons.constructor === Function) {
                    // 执行函数,获取函数返回的按钮组
                    let _allBtns = this.diy_buttons.call(this, _defaultBtnList)
                    // 去除重复项
                    result = Unique_Array(_allBtns, 'id')
                }
                // 2. 支持直接传入按钮组，会自行将默认与自定义按钮进行合并
                if (this.diy_buttons.constructor === Array) {
                    // 合并按钮
                    result = Merge_Btn(_defaultBtnList, this.diy_buttons)
                }
            }
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
            result = Sort_By_Prop(result, sortKey, defaultIndex)
            return result || []
            // 2021-07-16 之前使用
            // return Merge_Btn(_defaultBtnList, this.diy_buttons).filter(i => ShowStatus(i))

        }
    },
    // 可访问当前this实例
    created () { },
    // 挂载完成，可访问DOM元素
    async mounted () {
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
        this.btnList = this.getBtnList()
        // 是否使用铺满页面
        // 使用铺满，将使用时间函数不断进行计算，确保页面的铺满规则，使用时间函数的考虑
        //     1.搜索栏在操作过程中，例如【更多】项目会放出搜索项，高度会变化
        //     2.搜索过程若数据长度为0，分页组件会隐藏
        //     3.相关dom生成的时间不确定性，使用自定义指令较纠结
        if (this.isfullHeight) {
            this.setTableHeightTimer = setInterval(this.resetTableHeight, 400)
            this.$once('hook:beforeDestroy', () => clearInterval(this.setTableHeightTimer))
        }
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
    }
}
</script>
<style lang='scss' scoped>
.bd-module-list::v-deep {
    .bd-module-list__title {
        margin: -10px 0 0 -10px;
    }
    .bd-module-list__main {
        border: 1px solid $tableLineColor;
        border-radius: $borderRadius;
        .bd-module-list__btn {
            padding: 0 $padding;
            padding-bottom: $space;
            border-bottom: 1px solid $tableLineColor;
            .bd-module-list__btn__item {
                margin-top: $space;
                margin-right: $space;
                margin-left: 0 !important;
            }
        }
        .bd-module-list__list {
        }
    }
}
</style>