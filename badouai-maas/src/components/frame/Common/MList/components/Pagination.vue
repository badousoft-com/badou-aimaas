<template>
    <div class="padding bd-pagination">
        <el-pagination
            background
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pageNo"
            :page-sizes="pageSizes"
            :pager-count="pageCount"
            :page-size="pageSize"
            layout="total, sizes, jumper, slot, next, pager, prev"
            :total="total">
            <slot></slot>
        </el-pagination>
    </div>
</template>
<script>
import GlobalConst from '@/service/global-const'
export default {
    components: {},
    data () { // 定义页面变量
        return {
            num: 1,
        }
    },
    props: {
        // 当前页码
        pageNo: {
            type: Number,
            default: 1
        },
        // pageSize与pageSizes的使用规则注意
        //     原则上pageSize应该与pageSizes中的某一项相等，当同时出现pageSize与pageSizes,
        //     且pageSize与pageSizes中任意项不相等时，此时使用pageSizes数组中的第一项
        //     作为pageSize的值

        // 单页数据条数
        pageSize: {
            type: Number,
            default: GlobalConst.pagination.size
        },
        // 下拉可选单页数据条数数组 例如：[10, 20, 30, 40],非必须
        pageSizes: {
            type: Array,
            default: GlobalConst.pagination.sizes
        },
        // 页码按钮数目峰值
        pageCount: {
            type: Number,
            default: GlobalConst.pagination.maxBtnCount
        },
        // 总数
        total: {
            type: Number,
            default: 0
        }
    },
    computed: {},
    methods: { // 定义函数
        // 单页数据条数切换函数
        handleSizeChange (val) {
            this.$emit('size-change', val)
        },
        // 页码数切换函数
        handleCurrentChange (val) {
            this.$emit('current-change', val)
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>

</style>