<template>
    <div
        :ref="refName"
        :class="{
            'has-border': border,
            'full-height': fullHeight,
            'table-no-data': !temp_data.length && !parentHeight }"
        class="bd-table-list"
        :style="_rootStyle">
        <!-- 右键菜单组件 -->
        <right-menu
            v-if="showRightMenuPanel"
            ref="menu"
            :btnList="$attrs.btnList"
            :btnScoped="$attrs.btnScoped"
            @close="closeRightMenu">
        </right-menu>
        <el-table
            ref="list"
            v-loading="loading"
            :height="Is_Mobile()? mobileTableHeight : height"
            :max-height="maxHeight"
            :data="temp_data"
            row-key="treeUniqueId"
            :class="{
                'is-single': !multiple,
                'no-th-border': hasMuchLevel,
                'show-border': showBorder || spanMerge,
                'remove-tr-animation': spanMerge
            }"
            class="bd-table-list_box"
            border
            :show-summary="_summary.show"
            :summary-method="_summary.fun"
            :span-method="spanMethod"
            @select="select"
            @select-all="selectAll"
            @cell-click="cellClick"
            @cell-dblclick="cellDblClick"
            @sort-change="sortChange"
            @row-contextmenu="showRightMenu"
            @selection-change="selectionChangeFun">
            <!-- 是否带有选择框 -->
            <el-table-column
                v-if="isShowSelectionCol"
                type="selection"
                :prop="selectionProp"
                :fixed="true"
                :width="selectionWidth"
                :min-width="selectionWidth">
            </el-table-column>
            <!-- 是否带顺序标识 -->
            <!-- 默认配置顺序，当传入属性hasIndex为false时去除顺序 -->
            <!-- 当存在前置冻结列 freezeColumnIndex 时默认将序号列也冻结 -->
            <el-table-column
                v-if="isShowIndexCol"
                type="index"
                :prop="indexProp"
                :label="indexLabel"
                :fixed="_indexColumnFixed"
                :width="indexWidth"
                :min-width="indexWidth"
                :index="indexMethod">
            </el-table-column>
            <template 
                v-for="(i, index) in _fieldList">
                <slot :name="i.name">
                    <table-column-item
                        :column="i"
                        :hasData="_hasData"
                        :key="i.name || index"
                        :pageScope="pageScope"
                        :closeAllToolTip="closeAllToolTip"
                        @cellClick="cellClick">
                    </table-column-item>
                </slot>
            </template>
            <!-- 无数据展示状态,使用el-table的插槽empty -->
            <template v-slot:empty>
                <no-data
                    v-if="temp_data.length === 0 && !loading"
                    height>
                </no-data>
            </template>
        </el-table>
        <!-- 分页组件 -->
        <m-pagination
            v-if="isUsePagination && showPagination"
            class="bd-table-list__pagination"
            :pageNo="temp_params[paginationNameObj.no]"
            :pageSize="temp_params[paginationNameObj.size]"
            :pageSizes="paginationSizes"
            :total="total"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange">
            <!-- 只有带有复选框的列表才需要这个已选数据量的统计 -->
            <span
                v-if="isSelection"
                class="bd-table-list__selection">
                已选
                <span class="bd-table-list__num">{{_selection.length}}</span>
                条
            </span>
        </m-pagination>        
    </div>
</template>
<script>
import MPagination from './components/Pagination'
import NoData from '@/components/frame/NoData'
import TableColumnItem from './components/TableColumnItem'
import RightMenu from './components/RightMenu.vue'
import GlobalConst from '@/service/global-const'
import { finalRequest } from '@/service/request'
import { Scroll_Top } from '@/utils/animate.js'
import { Is_Mobile } from '@/utils/browser.js'
import { Get_Full_Url } from '@/service/url'
import { Get_Data_By_Path, Remove_Space } from '@/utils'
import { Get_Table_Span } from '@/service/table'
export default {
    name: "bd-table-list",
    // 父组件传入的属性不作为当前页面根标签的属性
    inheritAttrs: false, 
    components: {
        MPagination,
        NoData,
        [RightMenu.name]: RightMenu,
        [TableColumnItem.name]: TableColumnItem
    },
    props: {
        // hasIndex、indexLabel、indexWidth使用关系
        // 1. 只传hasIndex，就会默认有顺序列
        // 2. 当传递indexLabel或者indexWidth时，可以不用传hasIndex进来

        // 是否带有顺序列，默认有
        hasIndex: {
            type: Boolean,
            default: true
        },
        // 顺序列标签名称
        indexLabel: {
            type: String,
            default: GlobalConst.table.indexLabel
        },
        // 顺序列标签宽度
        indexWidth: {
            type: String,
            default: GlobalConst.table.indexWidth
        },
        // 是否为可选表格
        isSelection: {
            type: Boolean,
            default: false
        },
        // 表格外部的边框
        border: {
            type: Boolean,
            default: false
        },
        // 表格内的边框线是否展示(td与td之间)
        showBorder: {
            type: Boolean,
            default: false
        },
        // 表格高度
        height: {
            type: [String, Number]
        },
        // 表格最大高度
        maxHeight: {
            type: [String, Number],
        },
        // 2021-07-17添加，当组件传入该属性true时，则会自动根据高度自适应，列表内数据可滚动
        fullHeight: {
            type: Boolean,
            default: false
        },
        // 表头数据源：[{ name:'字段键', label:'展示文本', width:120 }]
        fieldList: {
            type: Array,
            default: () => []
        },
        // 表头数据源-通过ajax请求获取
        fieldRequest: {
            type: Object,
            default: () => {
                return {
                    // 请求地址
                    url: '',
                    // 请求参数
                    params: {},
                    // 请求方式: get/post
                    method: 'post',
                    // 响应数据路径path
                    //     当响应数据为res,path为''时，则res将作为返回值
                    //     当path为'A,B',则res[A][B]将作为返回值
                    path: '',
                    // 数据对象更新
                    //     {
                    //         'name': '', 删除对象下的name属性
                    //         'name': 'uName', 将对象下name值更新给uName，若uName属性不存在则新建再赋值
                    //         '': 'newName' 增加对象下的newName属性
                    //     }
                    // update: []
                }
            }
        },
        // table列表数据
        data: {
            type: Array,
            default: () => []
        },
        // 列表数据请求地址
        url: {
            type: String,
            default: ''
        },
        // 列表数据请求参数对象
        params: {
            type: Object,
            default: () => {}
        },
        // 请求前的参数处理函数
        paramsBeforeRequest: {
            type: Function
        },
        // 是否一次性请求所有数据
        isLoadAll: {
            type: Boolean,
            default: false
        },
        // 响应数据中长度数据所在的路径
        totalPath: {
            type: String,
            default: 'Total'
        },
        // 期望数据在响应数据中的位置
        //     例如列表接口返回数据为res，optionResPath为'A, B'
        //         则表示页面渲染所需列表数据为res[A][B],返回此
        optionResPath: {
            type: String,
            default: ''
        },
        // 请求列表数据方式： get/post,默认为post
        requestMethod: {
            type: String,
            default: 'post'
        },
        // 请求的其他配置信息
        requestConfig: {
            type: Object,
            default: () => {}
        },
        // 排序对象数据
        sortObj: {
            type: Object,
            default: () => {}
        },
        // 分页参数对应名称
        paginationNameObj: {
            type: Object,
            default: () => {
                return {
                    no: GlobalConst.pagination.noName,
                    size: GlobalConst.pagination.sizeName
                }
            }
        },
        // 分页相关信息，配置初始进入页面默认分页参数
        // 参数示例 { no: 1, size: 20, sizes: [10, 20, 30] }
        // 即初始页码、初始每页大小、每页大小可选择范围
        paginationInfo: {
            type: Object,
            default: () => {}
        },
        // 是否使用分页功能
        usePagination: {
            type: Boolean,
            default: true
        },
        // 是否显示分页组件（决策显示，无关功能）
        showPagination: {
            type: Boolean,
            default: true
        },
        // 请求列表数据之后需要执行的事件（动态js中指定）
        afterListJSON: {
            type: Function
        },
        // 列表渲染后（DOM已存在）需要执行的事件函数
        afterListRender: {
            type: Function
        },
        // 创建新的函数（动态js中指定）
        createFun: {
            type: Object
        },
        // 是否关闭所有的悬浮提示：目前使用场景为
        //     当某个可点击跳转的字段会点击时，此时同时触发悬浮与跳转，会导致跳转到的页面依然残留有悬浮
        //     所以希望在页面跳转时关闭全部提示
        closeAllToolTip: {
            type: Boolean,
            default: false
        },
        // 自定义搜索事件
        diySearch: {
            type: Function
        },
        // 表格是否支持多选
        multiple: {
            type: Boolean,
            default: true
        },
        // 前置冻结列
        freezeColumnIndex: {
            type: Number
        },
        // 后置冻结列
        freezeColumnLastIndex: {
            type: Number
        },
        // 选择项变更事件
        selectionChange: {
            type: Function
        },
        // 列表表头字段格式化函数
        columnFormat: {
            type: Function
        },
        // 展示合并行，以下为3种支持类型
        //     1. {Boolean} 是否展示合并行 true时则默认计算所有列总计
        //     2. {String} 传入需要统计的字段名字符串，如'sex,name'则只统计这两列
        //     3. {Function} 自定义组装合计的函数,需返回数组 ({columns, data}) => [10,20]
        showSummary: {
            type: [Boolean, String, Function],
            default: false
        },
        // 表格行列合计
        spanMerge: {
            type: [Function, Object]
        },
        // 字段值的更新处理方式，格式如 { aa: (value) => value + 1  }表示处理字段aa的值+1,只关注值，不关注展示形式
        fieldValueSetting: {
            type: Object,
        },
        // 额外参数，业务级别好帮手
        elseAttrs: {
            type: Object
        },
        // 父组件容器的高度，用于设置表格无数据时的最小高度
        parentHeight: {
            type: Number,
            default: 0
        }
    },
    data () { // 定义页面变量
        return {
            // 是否展示右键按钮菜单
            showRightMenuPanel: false,
            // 右键菜单操作的临时选项数据
            tempSelection: null,
            indexProp: '__index',
            selectionProp: '__selection',
            refName: 'tableListRef',
            // 分页参数对象
            paginationObj: {
                no: GlobalConst.pagination.no,
                size: GlobalConst.pagination.size
            },
            temp_params: {},
            temp_data: [], // 页面列表数据
            total: 0, // 请求数据总条数
            loading: false, // 加载状态
            // 判断是否为移动端
            Is_Mobile: Is_Mobile,
            // 移动端列表高度
            mobileTableHeight: GlobalConst.mobile.tableHeight,
            // 页面作用域
            pageScope: this,
            newBtnList: [],
        }
    },
    computed: {
        // 获取当前已选数据
        _selection () {
            return this.isSelection && this.$refs?.list?.selection || []
        },
        // 获取根元素样式
        _rootStyle () {
            // 判断是否移动端
            if (Is_Mobile()) return {
                height: `${GlobalConst.mobile.tableHeight}px`
            }
            if (this.height) return { 
                height: `${parseInt(this.height)}px`
            }
            return {}
        },
        // 表格域
        listRef () {
            return this.$refs.list
        },
        // 判断是否有多维数据，用于样式处理。一维与多维不同
        hasMuchLevel () {
            return this._fieldList.some(i => i.children && i.children.length > 0)
        },
        /**
         * 获取列固定属性
         */
        _hasData () {
            // 通过判断列表数据长度
            return !!(this.temp_data && this.temp_data.length > 0)
        },
        // 表头字段信息
        _fieldList: {
            get () {
                // 判断是否存在前置冻结列
                if (this.freezeColumnIndex) {
                    let i = 0
                    while (i < this.freezeColumnIndex) {
                        // 设置列固定
                        this.fieldList[i].fixed = 'left'
                        i++
                    }
                }
                // 判断是否存在后置冻结列
                if (this.freezeColumnLastIndex) {
                    let i = 0
                    let len = this.fieldList.length
                    while (i < this.freezeColumnLastIndex) {
                        // 设置列固定
                        this.fieldList[len - i - 1].fixed = 'right'
                        i++
                    }
                }
                // 判断是否存在自定义表头字段格式化函数
                if (this.columnFormat && typeof this.columnFormat === 'function') {
                    return this.columnFormat.call(this, this.fieldList)
                }
                // 返回表头字段数组
                return this.fieldList
            },
            set (val) {
                this.$emit('update:fieldList', val)
            }
        },
        // 是否展示选择框
        isShowSelectionCol () {
            return this.isSelection && this.fieldList && this.fieldList.length
        },
        // 是否展示序号列
        isShowIndexCol () {
            return this.hasIndex && Boolean(this.indexWidth) && this.fieldList && this.fieldList.length
        },
        // 选择列的列宽
        selectionWidth () {
            return GlobalConst.table.selectionWidth
        },
        // 分页组件单页可请求数据条数数组
        paginationSizes () {
            // 获取自定义页码范围信息
            let diy_sizes = this.paginationInfo?.sizes
            // 如果存在自定义页码返回，适应自定义的页码
            if (diy_sizes && diy_sizes.length) return diy_sizes
            // 定义并获取下拉选择单页请求数的类型总数，[10,20,30,40]这为4种
            let sizeSelectNum = GlobalConst.pagination.sizeSelectNum
            // 定义并获取可选单页请求数的增幅，[5,15,25,35]增幅为10
            let sizeAddNum = GlobalConst.pagination.sizeAddNum
            // 定义分页单页请求数据条数数组
            let sizesArray = []
            // 获取默认单页请求数
            let startSize = parseInt(this.paginationObj.size)
            // 默认单页请求数据条数作为数组第一项，默认选中
            sizesArray.push(startSize)
            for (let i = 0; i < sizeSelectNum - 1; i++) {
                startSize += sizeAddNum
                sizesArray.push(startSize)
            }
            return sizesArray
        },
        // 是否使用分页组件
        isUsePagination () {
            return this.url &&  // 只有url动态请求才有分页
                    this.total > 0 &&  // 至少要一条数据，空白则不展示
                    this.usePagination  // 父组件传入，直接控制是否使用分页功能
        },
        // 获取表格合计的相关信息
        _summary () {
            // 定义合计的相关信息
            let _res = {
                show: false, // 是否使用合计
                fun: null, // 自定义合计计算函数
            }
            // 不使用合计，则直接返回
            if (!this.showSummary) return _res
            // 使用合计，更新状态
            _res.show = true
            // 定义基础列字段键
            let _baseProp = {
                index: this.indexProp,
                selection: this.selectionProp,
            }
            // 传入的showSummary可能有三种数据类型
            switch (this.showSummary.constructor) {
                case Boolean:
                    // 在前面已处理，这里就不需要做什么了
                    break
                case Function:
                    // 自定义合计的函数
                    _res.fun = ({columns, data}) => this.showSummary.call(this, columns, data, _baseProp)
                    break
                case String:
                    // 若传入字符串，则表示指定哪些列进行合计
                    let _columns = Remove_Space(this.showSummary).split(GlobalConst.separator)
                    // 更新合计函数
                    _res.fun = ({columns, data}) => {
                        // 定义返回的结果
                        const _sums = []
                        // 遍历列
                        columns.forEach((column, index) => {
                            // 判断若为复选框-选择列，则设置合计行 展示内容为空
                            if (column.property === _baseProp.selection) {
                                _sums[index] = ''
                                return
                            }
                            // 若为顺序列，则设置合计行 展示内容为【合计】
                            if (column.property === _baseProp.index) {
                                _sums[index] = '合计'
                                return
                            }
                            // 判断是否为指定的列，是则计算指定列的数据总和
                            if (column.property && _columns.includes(column.property)) {
                                const values = data.map(item => Number(item[column.property]))
                                if (!values.every(value => isNaN(value))) {
                                    // 统计，计算一列数字的累加
                                    _sums[index] = values.reduce((prev, curr) => {
                                        return !isNaN(Number(curr)) ? prev + curr : prev
                                    }, 0)
                                    return
                                }
                            }
                            // 不符合条件的合计一行该列默认设置为空
                            _sums[index] = ''
                        })
                        return _sums
                    }
                    break
                default:
                    // do something you like
            }
            return _res
        },
        // 判断选择框列是否固定左侧
        _indexColumnFixed () {
            // 当存在字段配置为固定并且值不为右侧固定时，此时同时设置序号列固定
            return !!this._fieldList.find(i => i.fixed && i.fixed !== 'right')
        }
    },
    methods: { // 定义函数     
        /**
         * 右键开启列表菜单
         */
        showRightMenu (row, column, event) {
            // 设置右键菜单显示
            this.showRightMenuPanel = true
            // 等待DOM生成
            this.$nextTick(() => {
                if (this.$refs.menu._isShowMenu) {
                    // 使列表选中右键的数据
                    this.tempSelection = [row]
                    // 获取菜单元素对象
                    let menu = this.$refs.menu.$el
                    //阻止元素发生默认的行为
                    event.preventDefault()
                    // 根据事件对象中鼠标点击的位置，进行定位
                    let documentWidth = document.documentElement.clientWidth
                    let documentHeight = document.documentElement.clientHeight
                    // 菜单left、top值
                    let menuLeft = 0
                    let menuTop = 0
                    // 处理菜单超出屏幕显示
                    menuLeft = documentWidth - event.pageX < menu.clientWidth ? event.pageX - menu.clientWidth : event.pageX 
                    menuTop = documentHeight - event.pageY < menu.clientHeight ? event.pageY - menu.clientHeight : event.pageY 
                    // 设置菜单位置
                    menu.style.left = `${menuLeft}px`
                    menu.style.top = `${menuTop}px`
                }
            })
        },
        // 关闭列表右键的事件
        closeRightMenu () {
            // 清空临时数据
            this.tempSelection = null
            // 关闭右键菜单组件
            this.showRightMenuPanel = false
        },
        /**
         * 合并行列的方法
         * @param {Object} spanItem
         *      @param {Object} row 当前行
         *      @param {Object} column 当前列
         *      @param {Number} rowIndex 当前行号
         *      @param {Number} columnIndex 当前列号
         */
        spanMethod (spanItem) {
            if (!this.spanMerge) return
            let _res = null
            // 判断传入的行列单元格合并参数
            switch (this.spanMerge.constructor) {
                case Function:
                    // 若为函数
                    _res = this.spanMerge.call(this, spanItem)
                    break
                case Object:
                    // 若传入为配置，格式如{column: 'textf'}，则使用获取合并的函数
                    _res = Get_Table_Span(spanItem, this.temp_data, this.spanMerge)
                    break
                default:
                    // do something
            }
            // 返回结果
            return _res
        },
        /**
         * 当表格的排序条件发生变化的时候会触发该事件
         * @param {Object} column 列字段对象
         * @param {String} prop 字段键名
         * @param {String} order 排序关键词 ascending / descending / null
         */
        sortChange ({ column, prop, order }) {
            // 定义排序对象
            let sortObj = {}
            // 判断排序方式
            switch (order) {
                case 'ascending': // 升序
                    sortObj = {
                        sortorder: 'asc',
                        sortname: prop
                    }
                    break
                case 'descending': // 倒序
                    sortObj = {
                        sortorder: 'desc',
                        sortname: prop
                    }
                    break
                case null: // 默认
                default:
                    sortObj = {}
            }
            // 更新父组件
            this.$emit('update:sortObj', sortObj)
            this.$nextTick(() => {
                // 触发重新请求列表数据
                this.init()
            })
        },
        // 自定义列序号
        indexMethod (index) {
            // 传入数据，则默认返回index
            if (!this.url) {
                return index + 1
            }
            // 传入动态数据，则根据页码计算排序的序号
            let _no = this.temp_params[this.paginationNameObj.no] || 1  // 1:表示默认第一页
            let _size = this.temp_params[this.paginationNameObj.size] || 0  // 0：将0代入下面公式可知，下角标顺序有数据量决定
            return (_no - 1 ) * _size + index + 1
        },
        // 重置+请求数据
        reset () {
            // 页面列表数据
            this.temp_data = []
            // 请求数据总条数
            this.total = 0
            // 重置请求状态
            this.loading = false
            // 重置页数
            this.paginationObj.no = 1
            // 通知父组件刷新数据，重新传入，实现重置
            this.$emit('update:sortObj', {})
            // 初始化请求数据
            this.init()
        },
        // 初始化请求数据
        async init () {
            let { showLoading = true } = arguments[0] || {}
            // 优化为promise函数，以确保调用init时可以再重新获取数据后执行其他操作
            return new Promise(async (resolve, reject) => {
                // 隐藏右键菜单
                this.showRightMenuPanel = false
                // 请求状态设置为true，启动loading
                this.loading = showLoading
                if (!this.usePagination || this.isLoadAll) {
                    // 如果设置为一次请求完，则修改单页请求数为设定峰值，一次请求完
                    this.temp_params[this.paginationNameObj.size] = GlobalConst.pagination.maxSizeNum
                }
                // 获取请求前最终的完整参数
                this.temp_params = {...(this.temp_params || {}), ...(this.sortObj || {})}
                // 判断是否含有自定义修改参数方法paramsBeforeRequest
                if (typeof this.paramsBeforeRequest === 'function') {
                    // 执行自定义更改参数的方法
                    this.temp_params = this.paramsBeforeRequest.call(this, this.temp_params)
                }
                // 将请求参数抛出，父级组件有需要就用
                this.$emit('update:finalListParams', this.temp_params)
                // 判断地址是否含有域名，没有则补齐
                let url = Get_Full_Url(this.url)
                let requestConfig = {
                    url,
                    method: this.requestMethod,
                    params: this.temp_params,
                    // 其他配置
                    ...this.requestConfig
                }
                // 将最终的请求完整信息抛出给父级
                this.$emit('update:finalRequestConfig', requestConfig)
                let _request = finalRequest(requestConfig)
                if (this.diySearch && typeof this.diySearch === 'function') {
                    _request = this.diySearch.call(this, requestConfig)
                }
                _request.then(async res => {
                    this.$emit('afterRequest', res)
                    // 更新数据-总条数
                    this.total = Get_Data_By_Path(res, this.totalPath) || 0
                    // 更新数据
                    let _data = Get_Data_By_Path(res, this.optionResPath) || []
                    // 更新文件类(图片/附件)字段值数据, 模型配置了列表展示类型，这里核心处理配置了但后端没做处理便返回的值
                    this.updateFieldValueBySetting(_data)
                    if (this.afterListJSON) {
                        // 检查模型动态js中是否有afterListJSON属性，执行自定义事件
                        // afterListJSON：表示请求完列表数据之后进行额外操作的事件
                        _data = this.afterListJSON.call(this, _data)
                    }
                    // 更新页面-列表数据
                    this.temp_data = _data
                    try {
                        // 更新数据时重置滚动状态回到顶部
                        let tableBodyEl = this.$refs.list.$refs['bodyWrapper']
                        // 是否需要滚动到初始状态（当列表展示loading时，否则时在用户无感知时刷新，此时不需要重置滚动条）
                        showLoading && Scroll_Top(tableBodyEl, 100)
                    } catch (e) {
                        console.log(`设置列表滚动高度重置失败：${JSON.stringify(e)}`)
                    }
                }).catch(err => {
                    console.log(err)
                }).finally(() => {
                    // 设置数据请求状态--请求结束
                    this.loading = false
                    // 列表渲染完之后（DOM生成）执行自定义事件
                    this.$nextTick(() => {
                        this.afterListRender &&
                        typeof this.afterListRender === 'function' &&
                        this.afterListRender.call(this)
                        resolve()
                    })
                })
            })
        },
        /**
         * 按照传入的字段值更新方式，进行更新字段数据更新
         * @param {Array} data 字段值数组
         */
        updateFieldValueBySetting (data) {
            if (!this.fieldValueSetting) return
            Object.keys(this.fieldValueSetting).forEach(i => {
                data.forEach(j => {
                    j[i] = this.fieldValueSetting[i].call(null, { name: i, value: j[i] }, j.id)
                })
            })
        },
        /**
         * 当选择项发生变化时会触发该事件
         * @param {Array} selection 列表选中数据
         */
        selectionChangeFun (selection) {
            if (this.selectionChange && typeof this.afterListRender === 'function') {
                this.selectionChange.call(this, selection)
            }
            this.$emit('select-change', selection)
        },
        /**
         * 当用户手动勾选数据行的 Checkbox选择框 时触发的事件
         * @param selection 已勾选的数组数据
         * @param row 当前操作行对象
         */
        select (selection, row) {
            // 若设置表格数据只能单选，则除开当前操作的该行数据，清除其他选中
            if (!this.multiple) {
                this.clearOtherSelect(selection, row)
            }
            this.$emit('select', selection, row)
        },
        /**
         * 当用户手动勾选 全选Checkbox选择框 时触发的事件
         * @param selection 已勾选的数组数据
         */
        selectAll (selection) {
            this.$emit('select-all', selection)
        },
        // 表格td点击事件
        cellClick (row, column, cell, event) {
            // 若设置表格数据只能单选，则除开当前操作的该行数据，清除其他选中
            if (!this.multiple) {
                this.clearOtherSelect(this.$refs.list.selection, row)
            }
            // 自动切换当前选中数据
            this.$refs.list.toggleRowSelection(row)
            // 抛给父级组件，我给出去，用不用随他
            this.$emit('cellClick', row, column, cell, event, this)
        },
        // 表格td双击事件
        cellDblClick (row, column, cell, event) {
            // 抛给父级组件，我给出去，用不用随他
            this.$emit('cellDblClick', row, column, cell, event, this)
        },
        /**
         * 清除selection下除开row其他数据的选中状态
         * @param {Array} selection 当前已选数据
         * @param {Object} row 当前操作该行的数据
         */
        clearOtherSelect (selection, row) {
            selection.forEach(i => {
                if (i !== row) {
                    console.log('clear', i, row)
                    this.$refs.list.toggleRowSelection(i, false)
                }
            })
        },
        /**
         * 单页数切换事件
         * @param {Number} val 单页数
         */
        handleSizeChange (val) {
            // 更新请求参数中【单页数据量】
            // tip： 更改单页数据量时，需要重置掉页数，
            let pageNo = 1
            this.$set(this.temp_params, this.paginationNameObj.no, pageNo)
            this.$set(this.temp_params, this.paginationNameObj.size, val)
            // 请求列表数据
            this.init()
        },
        /**
         * 页数切换事件
         * @param {Number} val 页数
         */
        handleCurrentChange (val) {
            // 更新请求参数中【页数】
            this.$set(this.temp_params, this.paginationNameObj.no, val)
            // 请求列表数据
            this.init()
        },
        // 设置固定列的高度
        setFixedHight (scrollEventRes) {
            let fixedbodyWrapper = this.$refs.list.$el.getElementsByClassName('el-table__fixed-body-wrapper') || []
            let footEl = this.$refs.list.$el.getElementsByClassName('el-table__footer')?.[0] || {}
            let footHeight = footEl.clientHeight || 0
            let scorllTop = scrollEventRes?.target?.scrollTop || 0
            let { clientHeight = 0, offsetHeight = 0 } = scrollEventRes?.target || {}
            //滚动条高度
            let scrollBarHight = offsetHeight - clientHeight
            for (let d_index = 0; d_index < fixedbodyWrapper.length; d_index++) {
                fixedbodyWrapper[d_index].scorllTop = scorllTop
                let t = fixedbodyWrapper[d_index]?.style?.top || '0px'
                fixedbodyWrapper[d_index].style.height = `calc(100% - ${t} - ${scrollBarHight}px - ${footHeight}px)`
            }
        },
    },
    // 可访问当前this实例
    created () {

    },
    // 挂载完成，可访问DOM元素
    mounted () {
        this.$refs.list.bodyWrapper.addEventListener('scroll', this.setFixedHight)
        typeof this.mounted === 'function' && this.mounted()
    },
    beforeDestroy () {
        this.$refs.list.bodyWrapper.removeEventListener('scroll', this.setFixedHight)
        typeof this.beforeDestroy === 'function' && this.beforeDestroy()
    },
    watch: {
        // 监听是否存在动态获取表头数组,有则请求
        fieldRequest: {
            immediate: true,
            handler: function (newVal, oldVal) {
                // 请求地址：url
                // 参数：params
                // 请求方式: method
                // 相应数据路径: path
                let { url, params, method, path } = newVal
                if (!url) {
                    return false
                }
                finalRequest({
                    url: url,
                    method: method,
                    params: params
                }).then(res => {
                    // 更新页面表头数据源
                    this._fieldList = Get_Data_By_Path(res, path)
                }).catch(err => {
                    this._fieldList = []
                })
            }
        },
        paginationInfo: {
            immediate: true,
            handler: function (val) {
                if (!val) return
                let diy_sizes = val.sizes
                // 如果存在自定义页码范围，将初始每页大小更改为自定义页码范围的第一项
                if (diy_sizes && diy_sizes.length) this.paginationObj.size = diy_sizes[0]
                Object.keys(this.paginationObj).forEach(key => {
                    // 如果存在自定义页面参数，优先使用自定义页码数据
                    if (val[key]) this.paginationObj[key] = val[key]
                })
            }
        },
        params: {
            immediate: true,
            deep: true,
            handler: function (newVal, oldVal) {
                let _params = {}
                if (newVal && newVal.constructor === Object) {
                    _params = JSON.parse(JSON.stringify(newVal))
                }
                // 更新页面全局分页参数，用于分页逻辑使用
                this.paginationObj.no = _params[this.paginationNameObj.no] || this.paginationObj.no
                this.paginationObj.size = _params[this.paginationNameObj.size] || this.paginationObj.size
                // 将分页参数赋给请求参数，用于请求
                _params[this.paginationNameObj.no] = this.paginationObj.no
                _params[this.paginationNameObj.size] = this.paginationObj.size
                this.temp_params = _params
            }
        },
        // 监听传入url是否有值，有值则表示动态请求列表数据
        url: {
            immediate: true,
            handler: function (newVal, oldVal) {
                if (newVal) {
                    // 初始化请求数据
                    this.init()
                }
            }
        },
        // 监听是否传入列表数据,是则更新【页面列表展示数据】
        data: {
            immediate: true,
            handler: function (newVal, oldVal) {
                this.temp_data = newVal
            }
        },
        // 【监听页面列表展示数据】
        // 这里主要确保父组件中可以通过:data.sync同步获得子组件中同步变更的数据
        temp_data: {
            immediate: true,
            handler: function (newVal, oldVal) {
                this.$emit('update:data', newVal)
            }
        },
        // 将模型配置的js的自定义方法注册到当前页面（createFun: 来源模型配置js文件中）
        createFun: {
            immediate: true,
            handler: function (newVal, oldVal) {
                // 若存在则注册
                if (newVal) {
                    Object.keys(newVal).forEach(i => {
                        this[i] = newVal[i]
                    })
                }
            }
        },
    }
}
</script>
<style lang='scss' scoped>
.f-v {
    display: flex;
    flex-direction: column;
}
.bd-table-list::v-deep {
    border-radius: $borderRadius;
    overflow: hidden; // 消除子元素中的边框角落显示的问题
    // .el-table__empty-text为el-table样式，这里进行更改
    @extend .f-v;
    .bd-table-list_box {
        @extend .f-v;
        overflow: hidden;
        height: 100%;
        .el-table__header-wrapper {
            flex: none;
        }
        .el-table__body-wrapper {
            flex: 1;
            overflow: auto;
        }
        .el-table__fixed-footer-wrapper,
        .el-table__footer-wrapper {
            tbody {
                tr {
                    td {
                        background-color: $formDisabledBg !important;
                        border-top: 1px solid #EBEEF5;
                    }
                    &:hover {
                        td {
                            box-shadow: unset !important;
                        }
                    }
                }
            }
        }
        .el-table__fixed-right, .el-table__fixed-left {
            @extend .f-v;
            overflow: hidden;
            .el-table__fixed-header-wrapper {
                flex: none;
            }
            .el-table__fixed-body-wrapper {
                flex: 1;
                overflow: hidden;
            }
        }
        // 展示表格内td之间的边框线
        &.show-border {
            thead {
                tr {
                    th {
                        border-right: 1px solid $tdLineColor;
                        &:last-of-type {
                            border-right: none
                        }
                    }
                }
            }
            tbody {
                tr {
                    td {
                        border-right: 1px solid $tdLineColor;
                        border-bottom: 1px solid $tdLineColor;
                        border-top: 0;
                        &:last-of-type {
                            border-right: none
                        }
                    }
                }
            }
        }
        // 是否删除行的变化样式相关（如合并行列存在时会拥有此类）
        &.remove-tr-animation {
            tbody {
                tr {
                    &:hover,
                    &.hover-row {
                        &:nth-child(2n + 1) {
                            background: $white;
                            td {
                                background: $white;
                            }
                        }
                        &:nth-child(2n) {
                            background: $tableStripeBg;
                            td {
                                background: $tableStripeBg;
                            }
                        }
                    }
                    
                }
            }
        }
    }
    .bd-table-list__pagination {
        flex: none;
        .bd-table-list__selection {
            display: inline-block;
            padding: 0 $padding;
            .bd-table-list__num {
                color: $primary;
                min-width: unset;
            }
        }
    }
    .el-table__empty-text {
        line-height: normal;
        min-height: 50px;
        width: 100%;
        height: 100%;
    }
    &.has-border {
        border: 1px solid $tableLineColor;
    }
    .bd-table-list__pagination {
        border-top: 1px solid $lineColor;
    }
    &.full-height {
        height: 100%;
    }
    &.table-no-data {
        .el-table__empty-text {
            height: 150px;
        }
    }
    // 单选状态时样式
    .is-single {
        .el-table__header-wrapper {
            .el-table__header {
                .el-table-column--selection {
                    .el-checkbox {
                        .el-checkbox__input {
                            display:none;
                        }
                    }
                }
            }
        }
    }
}
</style>
<style lang="scss">
.el-popover.title-popover {
    border: 1px solid $fontCT;
    border-radius: 0px;
    background: #fff;
    padding: 4px 2px;
    margin-left: $space;
    &, & * {
        color: $fontCT !important;
        font-size: $fontS;
    }
}
</style>