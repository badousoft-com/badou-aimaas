<!--
 * @Author: your name
 * @Date: 2021-01-27 15:48:40
 * @LastEditTime: 2021-02-04 11:05:52
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: \badoufFrameWork-front-elementUI\src\components\frame\report\ReportList.vue
-->
<template>
    <div class="report-list">
        <!-- 普通列表 -->
        <el-table
            class="w-per-100"
            ref="tableRef"
            :data="data.tableData"
            :span-method="arraySpanMethod"
            :cell-class-name="setCellClassName"
            :cell-style="setCellStyle"
            :row-style="setRowStyle"
            :show-header="false"
            :height="fullTableHeight"
            border>
            <!-- 是否带有选择框 -->
            <el-table-column
                v-if="isSelection"
                type="selection"
                :fixed="fixedNum > 0"
                :width="selectionWidth">
            </el-table-column>
            <el-table-column
                v-for="(c, i) in data.tableColumn"
                :key="'col' + i"
                :width="c.width"
                :prop="c.field"
                :label="c.title"
                :fixed="i < fixedNum"
                show-overflow-tooltip>
                <template slot-scope="scope">
                    <!-- 序列行 -->
                    <template
                        v-if="data.indexColInfo &&
                        JSON.stringify(data.indexColInfo) !== '{}' &&
                        c.field === data.indexColInfo.field">
                        {{scope.$index+1 >= data.indexColInfo.start_row ? scope.$index-data.indexColInfo.start_row + 2 : scope.row[c.field]}}
                    </template>
                    <!-- 可点击单元格 -->
                    <span
                        v-else-if="data.elementInfoList &&
                        data.elementInfoList[scope.$index] &&
                        data.elementInfoList[scope.$index][c.field] &&
                        data.elementInfoList[scope.$index][c.field].clickFunctionName"
                        class="tap-style slot-cell"
                        @click="handColumnClick(scope.row, scope.$index, scope.column, i)">
                        {{scope.row[c.field]}}
                    </span>
                    <!-- 其他 -->
                    <span v-else class="slot-cell" v-html="formatterColumn(scope.row, scope.$index, scope.column, i)"></span>
                </template>
            </el-table-column>
        </el-table>

        <!-- 冻结行 -->
        <el-table
            ref="freezeTableRef"
            class="freezeInfo-table-row"
            v-if="data.freezeInfo && data.freezeInfo.freezeEndRowIndex"
            :data="data.tableData.slice(0, data.freezeInfo.freezeEndRowIndex)"
            :style="{width: `calc(100% - ${freezePosR}px)`, right: freezePosR + 'px'}"
            :span-method="arraySpanMethod"
            :cell-class-name="setCellClassName"
            :cell-style="setCellStyle"
            :row-style="setRowStyle"
            :show-header="false"
            border>
            <!-- 是否带有选择框 -->
            <el-table-column
                v-if="isSelection"
                type="selection"
                :fixed="fixedNum > 0"
                :width="selectionWidth">
            </el-table-column>
            <el-table-column
                v-for="(c, i) in data.tableColumn"
                :key="'col' + i"
                :width="c.width"
                :prop="c.field"
                :label="c.title"
                :fixed="i < fixedNum"
                show-overflow-tooltip>
                <template slot-scope="scope">
                    <!-- 序列行 -->
                    <template
                        v-if="data.indexColInfo &&
                        JSON.stringify(data.indexColInfo) !== '{}' &&
                        c.field === data.indexColInfo.field">
                        {{scope.$index+1 >= data.indexColInfo.start_row ? scope.$index-data.indexColInfo.start_row + 2 : scope.row[c.field]}}
                    </template>
                    <!-- 可点击单元格 -->
                    <span
                        v-else-if="data.elementInfoList &&
                        data.elementInfoList[scope.$index] &&
                        data.elementInfoList[scope.$index][c.field] &&
                        data.elementInfoList[scope.$index][c.field].clickFunctionName"
                        class="tap-style slot-cell"
                        @click="handColumnClick(scope.row, scope.$index, scope.column, i)">
                        {{scope.row[c.field]}}
                    </span>
                    <!-- 其他 -->
                    <span v-else class="slot-cell" v-html="formatterColumn(scope.row, scope.$index, scope.column, i)"></span>
                </template>
            </el-table-column>
        </el-table>
        <!-- 无数据展示状态 -->
        <template>
            <no-data
                class="no-data-box"
                v-if="data.pagination.total === 0"
                :height="(fullTableHeight) - 100">
            </no-data>
        </template>
        <!-- 分页组件 -->
        <m-pagination
            v-if="isShowPagination"
            class="report-pagination"
            :pageNo="data.pagination.pageNo"
            :pageSize="data.pagination.perPageSiz"
            :pageSizes="data.pagination.perPageOptions"
            :total="data.pagination.total"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange">
        </m-pagination>
    </div>
</template>

<script>
import GlobalConst from '@/service/global-const'
import MPagination from '@/components/frame/Common/MList/components/Pagination'
import NoData from '@/components/frame/NoData'
import { Find_Parent_By_ClassName, Find_By_ClassName } from '@/utils/find-dom'
import globalStyle from '@/styles/theme.scss'
export default {
    components: {
        MPagination,
        NoData
    },
    props: {
        isSelection: false,
        data: {
            type: Object,
            default: {
                tableData: [],
                tableColumn: [],
                tableData: [],
                // 序列信息{field: '', start_row: 0}
                indexColInfo: {},
                // 表格样式信息（每行为一个对象，对象中的key对应单元格，其中value为对象）
                classInfoList: [],
                // 表格样式信息（每行为一个对象，对象中的key对应单元格，其中value为对象）
                stylesList: [],
                // 单元格所包含的信息（每行为一个对象，对象中的key对应单元格，其中value为对象）
                elementInfoList: [],
                // 单元格所合并信息（每行为一个对象，对象中的key对应单元格，其中value为对象）
                spanInfoList: [],
                // 行属性列表（主要是高度）
                rowInfoList: [],
                // 是否为多选表格，Boolean类型，默认false
                showSelection: false,
                pagination: {
                    perPageSize: GlobalConst.pagination.size,
                    pageNo: 1,
                    perPageOptions: GlobalConst.pagination.sizes,
                    total: 0
                }
            }
        },
        listJsObj: {
            type: Object,
            default: () => {}
        },
        // 开启后，将会设置自动计算高度
        fullHeight: {
            type: Boolean,
            default: true
        },
        // 表格高度：包含表头与tbody，不包含分页模块
        tableHeight: {
            type: [String, Number]
        },
    },
    computed: {
        // 选择列的列宽
        selectionWidth () {
            return GlobalConst.table.selectionWidth
        },
        // 冻结列数（数量）
        fixedNum () {
            let result = 0
            if(this.data.freezeInfo && this.data.freezeInfo.freezeEndColumnIndex) {
                result = this.data.freezeInfo.freezeEndColumnIndex
            }
            return result
        },
        // 当父组件传入表格高度tableHeight时，fullHeight属性将失效，因为fullHeight核心逻辑是在计算表格高度
        isfullHeight () {
            return this.fullHeight && !this.tableHeight
        },
        // 是否展示分页
        isShowPagination () {
            if (!this.data.pagination) {
                return false
            } else {
                return this.data.pagination.total > this.data.pagination.perPageOptions[0]
            }
        },
    },
    data () {
        return {
            // 自动计算表格高度时间函数
            setTableHeightTimer: null,
            // 自动计算表格高度
            fullTableHeight: null,
            // 冻结行距离右侧位置，主要用于出现纵向滚动条时，栅格线错位的情况
            freezePosR: 0,
            allSelectionTimer: null,
        }
    },
    methods: {
        // 根据返回数据设置样式（由于部分样式设置className无法满足需要）
        setCellStyle (e) {
            let styles = {}
            // 循环数据
            if (this.data.tableColumn[e.columnIndex] &&
                this.data.tableData.length > e.rowIndex) {
                let colTilte = this.data.tableColumn[e.columnIndex].field
                let styleObj = {}
                let stylesInfo = this.data.stylesList
                if (stylesInfo && stylesInfo[e.rowIndex]) {
                    styleObj = stylesInfo[e.rowIndex][colTilte] || {}
                }
                styles = styleObj
            }
            return styles
        },
        // 设置行样式（行高）
        setRowStyle (e) {
            let styles = ''
            let rowIndex = e.rowIndex
            let rowInfoList = this.data.rowInfoList
            if (rowInfoList && rowInfoList[rowIndex]) {
                for (let key in rowInfoList[rowIndex]) {
                    let s = `${key}:${rowInfoList[rowIndex][key]};`
                    styles = styles + s
                }
            }
            return styles
        },
        // 根据返回数据设置className
        setCellClassName (e) {
            let classNames = ''
            if (this.data.tableColumn[e.columnIndex] && this.data.tableData.length > e.rowIndex) {
                let colTilte = this.data.tableColumn[e.columnIndex].field
                let classNameArr = []
                let classInfo = this.data.classInfoList
                if (classInfo && classInfo[e.rowIndex]) {
                    classNameArr = classInfo[e.rowIndex][colTilte] || []
                }
                if (!!classNameArr) {
                    classNames = classNameArr.join(' ')
                }
            }
            return classNames
        },
        // 合并行列
        arraySpanMethod ({ row, column, rowIndex, columnIndex }) {
            let span = { rowspan: 1, colspan: 1 }
            if (!!this.data.tableColumn[columnIndex] && this.data.tableData.length > rowIndex) {
                let col_obj = this.data.tableColumn[columnIndex]
                let spanInfo = this.data.spanInfoList
                if (spanInfo && spanInfo[rowIndex]) {
                    span = spanInfo[rowIndex][col_obj.field] || spanInfo[rowIndex][col_obj.title]
                }
            }
            return span || { rowspan: 1, colspan: 1 }
        },
        // 返回数组里面的值
        formatterColumn (row, rowIndex, column, columnIndex) {
            let value = row[column.property]
            if (this.listJsObj.renderField) {
                return this.listJsObj.renderField.call(this, { row, rowIndex, column, columnIndex, value: value })
            }
            return value
        },
        // 自定义表格点击事件
        handColumnClick (row, rowIndex, column, columnIndex) {
            if (this.data.elementInfoList[rowIndex]) {
                let fnName = this.data.elementInfoList[rowIndex][column.property].clickFunctionName
                let rowData = this.dataSource.sheetArr[this.sheetIndex].sheetInfo.rowData[rowIndex + 1]
                this.listJsObj[fnName].call(this, { row, rowIndex, column, columnIndex, value: row[column.property], rowData })
            }
        },
        // 设置表格高度
        setTableHeight () {
            // 定义表格所处在模块的展示高度(默认最小高度)
            let minHeight = parseInt(GlobalConst.table.height)
            let result = minHeight
            if (!this.isfullHeight) {
                this.setTableHeightTimer && clearInterval(this.setTableHeightTimer)
                return result
            }
            try {
                // 获取报表模块ref
                let tableRef = this.$refs.tableRef
                // ref尚未生成则直接return
                if (!tableRef) return result
                // 获取当前组件根标签DOM
                let ROOTEl = Find_Parent_By_ClassName(tableRef.$el, 'stander-report')
                // 搜索组件dom对象
                let searchbarEl = Find_By_ClassName(ROOTEl, 'bd-report__search')
                // tabs 的高度
                let tabsTopEl = Find_By_ClassName(ROOTEl, 'el-tabs__header')
                // 获取分组组件DOM
                let paginationEl = Find_By_ClassName(ROOTEl, 'bd-pagination')
                // 获取单位dom
                let unitEl = Find_By_ClassName(ROOTEl, 'unit')
                // 边框高
                let borderHeight = 1
                // 获取铺满状态下table应该设置的高度
                // 指定父级高度 - 2倍间隔 - 搜索框高度 - 按钮区 - 底部分页
                result = ROOTEl?.clientHeight -
                        2 * parseInt(globalStyle.padding) -
                        (searchbarEl?.clientHeight || 0) -
                        (tabsTopEl ? (tabsTopEl.clientHeight + 3 * parseInt(globalStyle.padding)) : 0) -
                        (paginationEl?.clientHeight || 0) -
                        (unitEl?.clientHeight || 0) -
                        borderHeight * 2
                // 判断表格是否有横向滚动条，如果有，冻结行需要设置padding，否则栅格线将会出现错位
                let tableWrapperEl = Find_By_ClassName(tableRef.$el, 'el-table__body-wrapper')
                // 获取滚动条宽度
                this.freezePosR = tableWrapperEl.offsetWidth - tableWrapperEl.clientWidth
            } catch (error) {
                console.error(`计算表格自适应高度出现异常：${error}`)
                // 存在异常情况，直接清除时间函数
                clearInterval(this.setTableHeightTimer)
            }
            this.fullTableHeight = result
        },
        // 监听报表滚动
        tableScoll () {
            // 获取需要绑定的table
            this.dom = this.$refs.tableRef.bodyWrapper
            // 监听表格滚动
            this.dom.addEventListener('scroll', () => {
                // 横向滚动距离
                let scrollLeft = this.dom.scrollLeft
                // 冻结表格行滚动
                let freezeTableRef = this.$refs.freezeTableRef?.bodyWrapper
                if (freezeTableRef) {
                    freezeTableRef.scrollLeft = scrollLeft
                }
            })
        },
        // ------------------------------------------------- 以下是分页 -----------------------------------------------------
        // 每页显示数更改事件
        handleSizeChange (val) {
            this.$emit('size-change', val)
        },
        // 当前页码更改事件
        handleCurrentChange (val) {
            this.$emit('current-change', val)
        },
    },
    mounted () {
        // 监听报表滚动
        this.tableScoll()
        // 是否使用铺满页面
        // 使用铺满，将使用时间函数不断进行计算，确保页面的铺满规则，使用时间函数的考虑
        //     1.搜索栏在操作过程中，例如【更多】项目会放出搜索项，高度会变化
        //     2.搜索过程若数据长度为0，分页组件会隐藏
        //     3.相关dom生成的时间不确定性，使用自定义指令较纠结
        if (this.isfullHeight) {
            this.setTableHeightTimer = setInterval(this.setTableHeight, 400)
            this.$once('hook:beforeDestroy', () => clearInterval(this.setTableHeightTimer))
        }
    }
}
</script>

<style lang="scss" scoped>
.report-list {
    position: relative;
    .freezeInfo-table-row {
        width: 100%;
        overflow: hidden;
        position: absolute;
        top: -1px;
        right: 0;
        /deep/ .el-table__body-wrapper {
            overflow: hidden;
            border-bottom: 1px solid #f1f1f1;
            .el-table__body {
                border-bottom: 1px solid #f1f1f1;
            }
        }
        /deep/ .el-table__fixed {
            &::before {
                display: none;
            }
        }
    }
    .no-data-box {
        position: absolute;
        top: 75%;
    }
    .report-pagination {
        padding-bottom: 0;
    }
}
.tap-style {
    cursor: pointer;
    color: $primary;
}
</style>