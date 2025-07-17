<template>
    <div class="flow-chart">
        <svg :width="width" :height="height" :id="id"></svg>
    </div>
</template>
<script>
import dagreD3 from 'dagre-d3'
import * as d3 from 'd3'
import globalStyle from '@/styles/theme.scss'
import { Hex_To_RGBA } from '@/utils/color'
export default {
    name: 'flow-chart',
    components: {},
    props: {
        // 流程图标识
        id: {
            type: String,
            default: 'default'
        },
        // svg宽度
        width: {
            type: [String, Number],
            default: '100%'
        },
        // svg高度
        height: {
            type: [String, Number],
            default: '100%'
        },
        // 节点数据
        nodes: {
            type: Array,
            default: () => []
        },
        // 连线数据
        edges: {
            type: Array,
            default: () => []
        },
        // 图配置信息
        graphConfig: {
            type: Object
        },
        // 节点配置信息
        nodeConfig: {
            type: Object
        },
        // 连线配置信息
        edgeConfig: {
            type: Object
        },
        // 最小缩放率
        minZoom: {
            type: Number,
            default: 0.5
        }
    },
    data () { // 定义页面变量
        return {
            // svg组对象
            svgGroup: null,
            // 缩放对象
            transform: null,
        }
    },
    computed: {
    },
    methods: { // 定义函数
        // 初始化加载页面
        init () {
            // 创建 graph 对象
            let g = new dagreD3.graphlib.Graph()
            // 获取图配置信息
            let _graphConfig = Object.assign({}, {
                rankdir:'LR', // 设置流程图的方向，LR表示从左到右
                align: 'DL',
                edgesep: 50
            }, this.graphConfig)
            // 设置图
            g.setGraph(_graphConfig)
            // 画节点
            this.nodes.forEach(item => {
                let fillStyle = Hex_To_RGBA(globalStyle.primary, 0.2)
                // 获取节点配置信息
                let _nodeConfig = Object.assign({}, {
                    // 节点标签
                    label: item.label,
                    // 节点形状
                    shape: item.shape,
                    // 节点样式
                    style: `fill:${fillStyle};stroke:rgb(5, 152, 244);`,
                    // 节点标签样式
                    labelStyle: `fill:${globalStyle.primary};font-weight:bold;`
                }, this.nodeConfig)
                // 绘制节点
                g.setNode(item.id, _nodeConfig)
            })
            // 画连线
            this.edges.forEach(item => {
                let lineColor = '#333'
                let labelColor = '#333'
                // 获取连线配置
                let _edgeConfig = Object.assign({}, {
                    // 边标签
                    label: item.label,
                    // 边样式
                    style: `fill:${lineColor};stroke:${lineColor};stroke-width:1.5px`,
                    labelStyle: `fill:${labelColor}`,
                    arrowhead: 'vee',
                    arrowheadStyle: `fill:${lineColor}`
                }, this.edgeConfig)
                // 绘制连线
                g.setEdge(item.source, item.target, _edgeConfig)
            })
            // 创建渲染器
            let render = new dagreD3.render()
            // 选择 svg 并添加一个g元素作为绘图容器
            let svg = d3.select(`#${this.id}`)
            this.svgGroup = svg.append('g')
            // 在绘图容器上运行渲染器生成流程图
            render(this.svgGroup, g)
            let that = this
            // 建立拖拽缩放
            let zoom = d3.zoom()
                .on('zoom', function () {
                    that.transform = d3.event.transform
                    // 获取缩放，设置最小缩放【注意作用域使用that】
                    that.transform.k = Math.max(that.transform.k, that.minZoom)
                    // 设置缩放【注意作用域使用that】
                    that.svgGroup.attr('transform', that.transform)
                    // 【后续添加的代码注意作用域使用that】
                })
            svg.call(zoom)
        },
        // 复位：位置与大小
        reset () {
            // 缩放控制为1
            this.transform.k = 1
            // 重置流程图-位置横坐标为0
            this.transform.x = 0
            // 重置流程图-位置纵坐标为0
            this.transform.y = 0
            // 更新缩放
            this.svgGroup.attr('transform', this.transform)
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        // 初始化加载
        this.init()
    },
    watch: {
        // 监听节点数据的变化
        nodes: {
            deep: true,
            handler: function (newVal, oldVal) {
                // 重新初始化加载
                this.init()
            }
        },
        // 监听连线数据的变化
        edges: {
            deep: true,
            handler: function (newVal, oldVal) {
                // 重新初始化加载
                this.init()
            }
        }
    }
}
</script>
<style lang='scss' scoped>
.flow-chart {
    padding: $padding;
    width: 100%;
    height: 100%;
}
</style>