<template>
    <div>
        <!-- 判断是否只展示流程环节 -->
        <template v-if="!onlyNode">
            <!-- 存在头部时使用 -->
            <m-card :title="traceTitle" v-if="!noHeader">
                <template v-slot:right>
                    <el-button type="text" class="cardBtn" @click="flowNodeChart.dialogVisible=true">流程环节图</el-button>
                    <el-button type="text" class="cardBtn" @click="flowTraceChart.dialogVisible=true">流程跟踪路线图</el-button>
                </template>
                <bd-table-list
                    v-if="!onlyNode"
                    fullHeight
                    :showPagination="false"
                    :fieldList="fieldList"
                    :data.sync="flowTraceData">
                </bd-table-list>
            </m-card>
            <!-- 不存在则使用以下 -->
            <bd-table-list
                v-else
                fullHeight
                :showPagination="false"
                :fieldList="fieldList"
                :data.sync="flowTraceData">
            </bd-table-list>
        </template>
        <template v-else>
            <el-button type="text" class="cardBtn" @click="flowNodeChart.dialogVisible=true">流程环节图</el-button>
        </template>
        <bd-dialog
            v-if="currentChart.dialogVisible"
            :title="currentChart.title"
            width="96%"
            :height="currentChart.height"
            :scope="scope"
            :visible.sync="currentChart.dialogVisible">
            <!-- 尽量不使用title的插槽 -->
            <flow-chart
                v-if="currentChart.isShow"
                :ref="currentChart.refName"
                :nodes="currentChart.nodes"
                :edges="currentChart.edges">
            </flow-chart>
            <template v-slot:footer>
                <bd-button icon="cancel" type="danger" @click="cancel">取消</bd-button>
                <bd-button icon="reset" type="primary" @click="reset">复位</bd-button>
            </template>
        </bd-dialog>
    </div>
</template>
<script>
import { Get_Trace_List } from '@/api/frame/flow'
import { Get_Common_List } from '@/api/frame/common'
import MCard from '@/components/frame/Common/MCard'
import ModuleUtils from '@/js/ModuleUtils'
import MList from '@/components/frame/Common/MList/index.vue'
import Dialog from '@/components/frame/Dialog/index.vue'
import FlowChart from '@/views/default/FlowChart'
import { Show_Status } from '@/service/module'
export default {
    name: 'flow-trace',
    components: {
        [MList.name]: MList,
        [MCard.name]: MCard,
        [Dialog.name]: Dialog,
        [FlowChart.name]: FlowChart
    },
    props: {
        // 业务实例id
        boId: {
            type: String,
        },
        // 流程id:用于显示流程环节图
        flowId: {
            type: String
        },
        // 是否要卡片头
        noHeader: {
            type: Boolean,
            default: false
        },
        // 是否铺满列表
        fullHeight: {
            type: Boolean,
            default: false
        },
        // 是否只展示流程环节块
        onlyNode: {
            type: Boolean,
            default: false
        }
    },
    data () {
        return {
            // 标题
            traceTitle: '流程跟踪',
            // 表头字段数组
            fieldList: [],
            // 表格数据
            flowTraceData: [],
            // 流程跟踪模型
            flowTraceMdCode: 'proccessTrace',
            // 流程图数据
            flowTraceChart: {
                title: '流程跟踪图',
                refName: 'flowTrace',
                height: '70%',
                nodes: [], // 节点数据
                edges: [], // 连线数据
                dialogVisible: false, // 弹窗状态
                isShow: false, // 展示状态
            },
            // 流程环节图数据
            flowNodeChart: {
                title: '流程环节图',
                refName: 'flowNode',
                height: '40%',
                nodes: [], // 节点数据
                edges: [], // 连线数据
                dialogVisible: false, // 弹窗状态
                isShow: false, // 展示状态
            },
            flowNodes: [], // 流程环节
        }
    },
    computed: {
        // 当前组件作用域
        scope () {
            return this
        },
        // 获取当前的流程图数据
        currentChart () {
            // 根据展示状态返回选中的流程对象数据，页面跟分别获取属性进行展示
            if (this.flowTraceChart.dialogVisible) return this.flowTraceChart
            if (this.flowNodeChart.dialogVisible) return this.flowNodeChart
            // 默认返回状态false，不显示流程图，避免异常处理
            return {
                dialogVisible: false
            }
        }
    },
    methods: {
        // 获取流程跟踪的表头数据
        getTraceModule () {
            ModuleUtils.listModuleCfg(this.flowTraceMdCode).then(res => {
                let _res = res.fieldList
                    .filter(i => Show_Status(i))
                    .map(i => {
                        let item = {
                            label: i.display,
                            // 列表字段宽度
                            width: i.listWidth,
                            // 列表字段最小宽度
                            minWidth: i.minWidth,
                            ...i
                        }
                        if (i.name === 'creatorName') {
                            // ---------- 阅件处理人左侧添加 已阅、未阅 标识
                            item.formatter = function (row, column, cellValue, index, fieldObj) {
                                if (String(row.type) === '1') {  // 办件
                                    let tag = `<span class="dangerTag marH-2" style="margin-right: 4px">待</span>`
                                    if (isNaN(row.fmtFinishTime) && !isNaN(Date.parse(row.fmtFinishTime))) {
                                        tag = `<span class="successTag marH-2" style="margin-right: 4px">已</span>`
                                    }
                                    return tag + cellValue
                                } else {  // 阅件
                                    let tag = `<span class="successTag marH-2" style="margin-right: 4px">已</span>`
                                    if (String(row.status) === '16') {
                                        tag = `<span class="dangerTag marH-2" style="margin-right: 4px">未</span>`
                                    }
                                    return tag + cellValue
                                }
                            }
                        } else if (i.name === 'type') {
                            // ---------- 2023/05/10 类型：1-办件、2-阅件
                            item.formatter = function (row, column, cellValue, index, fieldObj) {
                                switch (String(cellValue)) {
                                    case '1': // 办件
                                        return `<span class="primaryTag">办件</span>`
                                    case '2': // 阅件
                                        return `<span class="dangerTag">阅件</span>`
                                    default:
                                        return '--'
                                }
                            }
                        }
                        return item
                    })
                // 获取审核意见所在下角标
                let _optionsIndex = _res.findIndex(i => i.name === 'opinion')
                if (~_optionsIndex) {
                    // 设置审核意见显示全部文本，并且支持换行符
                    _res[_optionsIndex].render = function (h, context, row, column, cellValue, index, fieldObj, scope) {
                        return (
                            <div class="text-show-all w-space-pre">{cellValue}</div>
                        )
                    }
                }
                // 字段格式转化，更新this.fieldList
                this.fieldList = _res
            })
        },
        // 获取流程跟踪数据
        getTraceList () {
            if (!this.boId) return
            Get_Trace_List(this.boId).then(res => {
                this.flowTraceData = res?.Rows || []
                // 设置流程跟踪图数据
                this.setChart(this.flowTraceChart, this.flowTraceData, {
                    nodeName: 'creatorName',
                    edgeName: 'nodeName'
                })
            })
        },
        /**
         * 设置流程跟踪图数据
         * @param {Object} chartObj 流程图对象
         * @param {Array} data 列表数据
         * @param {Object} {nodeName, edgeName} 对应从列表数据中取出的数据对象键名
         */
        setChart (chartObj, data, {nodeName, edgeName}) {
            // 更新节点数据
            chartObj.nodes = data.map((i, index) => ({
                id: index,
                shape: 'rect',
                label: nodeName && i[nodeName],
            }))
            // 获取数据长度
            let nodeLen = data && data.length
            // 至少要有两个节点才能连线
            if (nodeLen >= 2) {
                // 获取连线数据源
                let edgesData = data.slice(0, nodeLen - 1)
                // 更新连线数据
                chartObj.edges = edgesData.map((i, index) => ({
                    source: index,
                    target: index + 1,
                    label: edgeName && i[edgeName]
                }))
            }
            // 允许展示
            chartObj.isShow = true
        },
        /**
         * 获取流程环节-树节点
         * @param {String} flowId 流程id
         */
        getFlowNode () {
            let defaultSearchParam = [{name: 'flowId', value: this.flowId, type: 'exact-match'}]
            let params = {
                mdCode: 'FlwNode',
                defaultSearchParam: JSON.stringify(defaultSearchParam)
            }
            // 获取流程节点数据
            Get_Common_List(params, 'post').then(res => {
                // 获取接口的数据字典项列表数据
                this.flowNodes = res?.Rows || []
                // 设置流程跟踪图数据
                this.setChart(this.flowNodeChart, this.flowNodes, {
                    nodeName: 'name',
                    edgeName: ''
                })
            })
        },
        // 重置流程图排版
        reset () {
            this.$refs[this.currentChart.refName].reset()
        },
        // 取消流程图弹窗
        cancel () {
            this.currentChart.dialogVisible = false
        },
    },
    created () {
    },
    mounted () {
        // 若当前只需要流程环节数据，则不操作流程跟踪相关
        if (!this.onlyNode) {
            // 获取流程跟踪的表头数据-测试
            this.getTraceModule()
            // 获取流程跟踪数据
            this.getTraceList()
        }
        // 获取流程环节数据
        this.getFlowNode()
    }
}
</script>
<style lang="scss" scoped>
</style>