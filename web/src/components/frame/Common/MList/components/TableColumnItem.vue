<template>
    <el-table-column
        :fixed="hasData && column.fixed"
        :prop="column.name"
        :label="column.label"
        :width="column.width"
        :sortable="hasData && (column.isSort?'custom':false)"
        :min-width="getMinWidth(column)">
        <!-- 表头自定义 -->
        <template slot="header" slot-scope="scope">
            <!-- 使用render -->
            <render-fun v-if="column.headerRender" :render="fieldHeaderRender(column, scope)"></render-fun>
            <!-- 使用formatter -->
            <span v-else-if="column.headerFormatter" v-html="fieldHeaderFormatter(column, scope)"></span>
            <!-- 默认 -->
            <span v-else>{{column.label}}</span>
        </template>
        <template slot-scope="scope" v-if="column.name">
            <!-- -
            1. 不使用模版
                1.1 有值
                1.2 无值
            2. 使用模版
            - -->
            <template v-if="column.render">
                <render-fun :render="fieldRender(column, scope)"></render-fun>
            </template>
            <template v-else>
                <!-- 有值的情况，使用el-popover -->
                <!-- 这里要使用hasValue判断下，避免0的情况，0为有值 -->
                <template v-if="hasValue(columnFormatter(column, scope))">
                    <!-- 使用悬浮提醒的形式 -->
                    <el-popover
                        v-if="!closeAllToolTip && !column.noTooltip"
                        popper-class="title-popover"
                        placement="bottom-start"
                        :offset="60"
                        :open-delay="popoverSetting.openDelay"
                        :close-delay="popoverSetting.closeDelay"
                        :visible-arrow="false"
                        trigger="hover">
                        <!-- 鼠标悬浮展示提醒 -->
                        <span v-html="columnFormatter(column, scope)"></span>
                        <!-- 页面默认展示 -->
                        <template slot="reference">
                            <cell-value
                                :data="column"
                                :scope="scope"
                                :value="columnFormatter(column, scope)"
                                @columnClick="columnClick">
                            </cell-value>
                        </template>
                    </el-popover>
                    <!-- 出于某些原因，不使用悬浮提醒的形式 -->
                    <span v-else>
                        <cell-value
                            :data="column"
                            :scope="scope"
                            :value="columnFormatter(column, scope)"
                            @columnClick="columnClick">
                        </cell-value>
                    </span>
                </template>
                <!-- 无值默认展示 -->
                <span v-else>{{defaultCellValue}}</span>
            </template>
        </template>
        <template v-if="column.children && column.children.length > 0">
            <template v-for="(columnChildItem, j_index) in column.children">
                <slot :name="columnChildItem.name">
                    <table-column-item
                        :column="columnChildItem"
                        :key="columnChildItem.name || j_index"
                        :pageScope="pageScope"
                        :closeAllToolTip="closeAllToolTip"
                        :hasData="hasData"
                        v-bind="$attrs"
                        v-on="$listeners">
                    </table-column-item>
                </slot>
            </template>
        </template>
    </el-table-column>
</template>
<script>
import GlobalConst from '@/service/global-const'
import RenderFun from '@/components/frame/RenderFun'
import CellValue from './CellValue'
import { Get_Word_Width, Has_Value } from '@/utils'
export default {
    name: 'table-column-item',
    components: {
        CellValue,
        [RenderFun.name]: RenderFun,
    },
    props: {
        // 列对象数据
        column: {
            type: Object
        },
        // 父级页面作用域
        pageScope: {
            type: Object
        },
        // 列表数据是否有值
        hasData: {
            type: Boolean
        },
        // 是否关闭所有的悬浮提示：目前使用场景为
        //     当某个可点击跳转的字段会点击时，此时同时触发悬浮与跳转，会导致跳转到的页面依然残留有悬浮
        //     所以希望在页面跳转时关闭全部提示
        closeAllToolTip: {
            type: Boolean,
            default: false
        },
    },
    data () { // 定义页面变量
        return {
            hasValue: Has_Value,
            // 默认cell值
            defaultCellValue: GlobalConst.table.value,
        }
    },
    computed: {
        // 获取表格列最小宽度
        minWidth () {
            return GlobalConst.table.minWidth
        },
        // 获取popover弹出层配置
        popoverSetting () {
            return GlobalConst.popover
        },
        /** 
         * 字段值处理函数
         * @params item [Object]: 表头列字段对象
         * @params scope [Object]: 作用域
         * @return [Function]
        */
        columnFormatter (item, scope) {
            return (item, scope) => {
                let { row, column, cellValue, index } = this.getCellScope(scope)
                // 判断是否存在值自定义操作
                if (!item.formatter) {
                    return cellValue
                }
                return item.formatter.call(this.pageScope, row, column, cellValue, index, item)
            }
        },
    },
    methods: { // 定义函数
        /**
         * 获取列的最小宽度值
         * @param {Object} 列属性对象 
         *      {
         *          @param {String} label 列的标签名称
         *          @param {String} minWidth 列的最小宽度
         *          @param {Boolean} isSort 是否排序
         *      }
         */
        getMinWidth ({label, minWidth, isSort}) {
            // 有最小宽度，则直接返回
            if (minWidth) return minWidth
            // 定义排序的图标宽度
            let sortWidth = 24
            // 计算容纳列名标签不换行所需的宽度值，包活根据是否排序计算排序图标宽度值；20为2倍padding值
            let labelWidth = Get_Word_Width(label, {}, 20 + (isSort ? sortWidth : 0))
            // 不换行标签值与默认标签宽度值，取最大的进行展示
            return Math.max(parseInt(labelWidth), GlobalConst.table.minWidth)
        },
        /**
         * 格式化表头(render模式)
         * @param {Object} item 列数据对象
         * @param {Object} scope 基于el-table的列数据对象
         */
        fieldHeaderRender (item, scope) {
            // 获取名称label，渲染函数headerRender
            let { label, headerRender } = item
            if (!(headerRender && typeof headerRender === 'function')) return label
            return (h, context) => {
                return headerRender.call(this.pageScope, h, context, label, item, scope)
            }
        },
        /** 
         * 字段值处理函数
         * @params item [Object]: 表头列字段对象
         * @params scope [Object]: 作用域
         * @return [Function]
        */
        fieldHeaderFormatter (item, scope) {
            let { label, headerFormatter } = item
            if (!(headerFormatter && typeof headerFormatter === 'function')) return label
            return headerFormatter.call(this.pageScope, label, item, scope)
        },
        /**
         * 字段自定义标签与事件（render + JSX），同时处理标签与事件
         * fieldFormatter+fieldClick可处理简单场景自定义，使用fieldRender可处理更复杂的场景
         * @param {Object} item 字段对象
         */
        fieldRender (item, scope) {
            let { row, column, cellValue, index } = this.getCellScope(scope)
            // 定义render自定义格式化标签展示与事件
            let { render } = item
            if (!(render && typeof render === 'function')) return
            return (h, context) => {
                return render.call(this.pageScope, h, context, row, column, cellValue, index, item, scope)
            }
        },
        /**
         * @param {Object} scope 表格cell所在作用域
         * @return {Object} {
         *     @params row [Object]: 所在行对象
         *     @params column [String]: 所在列对象
         *     @params cellValue [String/NUmber]: 传入值/当前值
         *     @params index [Number]: 该行下角标
         * }
         */
        getCellScope (scope) {
            let { row, column, $index: index } = scope
            let cellValue = row[column.property]
            return {
                row,
                column,
                cellValue,
                index
            }
        },
        /** 
         * 字段操作函数
         * @params item [Object]: 表头列字段对象
         * @params scope [Object]: 作用域
        */
        columnClick (item, scope) {
            // 执行数据点击事件
            let { row, column, cellValue, index } = this.getCellScope(scope)
            // 建议使用click，handle即将废弃
            let handleClick = item.click
            // 判断是否存在操作事件
            if (!(handleClick && typeof handleClick === 'function')) {
                // 当前cell无事件，则设置点击后该行数据被选中；有事件则使用stop冒泡阻止（已在html中添加）
                this.$emit('cellClick', row)
                return
            }
            return handleClick.call(this.pageScope, row, column, cellValue, index, item, scope)
        },
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>

</style>