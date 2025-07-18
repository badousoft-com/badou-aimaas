
// 环节选择：用于流程跳转使用
<template>
    <bd-table-list
        :ref="refName"
        :multiple="false"
        height="400px"
        :showPagination="false"
        :fieldList="fieldList"
        :isSelection="true"
        :data.sync="tableData">
    </bd-table-list>
</template>
<script>
import ScopeMixin from '@/components/frame/ScopeMixin'
import MList from '@/components/frame/Common/MList/index.vue'
import { Get_Node_Instance_List } from '@/api/frame/flow'
import { Has_Value } from '@/utils'
export default {
    name: 'node-select',
    mixins: [ScopeMixin],
    components: {
        [MList.name]: MList
    },
    props: {
        // 流程id
        fiId: {
            type: String
        },
        // 当前环节所在下角标，用于回显在列表上选中状态
        nodeIndex: {
            type: Number
        }
    },
    data () { // 定义页面变量
        return {
            // 定义组件ref名称
            refName: 'nodeList',
            // 定义表头数据
            fieldList: [
                { name: 'nodeName', label: '环节'},
            ],
            // 定义列表数据
            tableData: []
        }
    },
    computed: {
        tableRef () {
            return this.$refs[this.refName].listRef
        }
    },
    methods: { // 定义函数
        getData () {
            if (!this.fiId) return
            // 获取环节实例列表数据
            Get_Node_Instance_List({ fiId: this.fiId }).then(res => {
                this.tableData = res?.Rows || []
                this.$nextTick(() => {
                    // 判断是否传入当前所处的环节的下角标
                    if (Has_Value(this.nodeIndex) && ~this.nodeIndex) {
                        // 根据传入当前环节的下角标，渲染列表上该条数据选中状态
                        // 这里有个需要注意的点：这里的下角标是从完整环节传过来的（包含发起与归档环节），
                        // 但是这里的环节请求到的列表数据是去掉（发起与归档环节）的，所以传进来的下角标需要减掉1（this.nodeIndex必然＞1），顶掉发起环节的位置
                        this.tableRef.toggleRowSelection(this.tableData[this.nodeIndex - 1])
                    }
                })
            })
        },
        // 获取当前列表所选数据
        getSelection () {
            return this.tableRef.selection
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        // 获取列表数据
        this.getData()
    }
}
</script>
<style lang='scss' scoped>

</style>