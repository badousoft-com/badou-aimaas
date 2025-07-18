<!--
    雷达图
-->
<template>
    <div>
        <!--创建一个echarts的容器-->
        <div
            ref="chart"
            :style="{height: height}"
            class="w-per-100">
        </div>
    </div>
</template>

<script>
let echarts = require('echarts')
export default {
    name: 'radar-echart',
    props: {
        seriesData: { // 需要显示的数据，下面有示例
            type: Array,
            default: () => []
        },
        height: { // 装载echarts容器的高度, 柱状图的高度自适应容器的高度
            type: String,
            default: '400px'
        },
        max: { // 坐标轴的最大值
            type: Number,
            default: () => 50
        },
        indicator: {  // 雷达图的左边轴（逆时针）
            type: Array,
            default: () => ['支出', '异常', '人员数', '资产', '收入']
        },
        title: {
            type: String,
            default: '雷达图'
        }
    },
    data () {
        return {
            // 图例显示的标签数据
            legendData: [],
            exampleSeries: [
                {
                    value: [36, 46, 37, 29, 35],
                    // 设置区域边框和区域的颜色
                    itemStyle: {
                        normal: {
                            color: 'rgba(255,225,0,0.5)',
                            lineStyle: {
                                color: 'rgba(255,225,0,.5)',
                            },
                        },
                    },
                },
            ],
        }
    },
    methods: {

    },
    created () {

    },
    mounted () {
        let myChart = echarts.init(this.$refs.chart)
        // 默认显示的数据，可以看运行效果
        let seriesData = (this.seriesData !== undefined && this.seriesData.length > 0) ? this.seriesData : this.exampleSeries
        let indicator = []
        this.indicator.forEach((item, index, arr) => {
            indicator[index] = {}
            // 若将此属性放在radar下，则每条indicator都会显示圈上的数值，放在这儿，只在通信这条indicator上显示
            indicator[0].axisLabel = {
                show: true,
                fontSize: 12,
                color: '#838D9E',
                // 不显示最大值，即外圈不显示数字
                showMaxLabel: true,
                // 显示最小数字，即中心点显示0
                showMinLabel: true,
            }
            indicator[index].name = item
            indicator[index].max = this.max
        })
        // 绘制图表
        myChart.setOption({
            title: {
                text: this.title,
                textStyle: {
                    // 标题颜色
                    color: '#666',
                    fontSize: 14,
                    lineHeight: 20,
                },
                // 标题的位置，此时放在图的底边
                left: '10%',
                top: '10%',
            },
            // 图表的位置
            grid: {
                position: 'center',
            },
            tooltip: {
                // 雷达图的tooltip不会超出div，也可以设置position属性，position定位的tooltip 不会随着鼠标移动而位置变化，不友好
                confine: true,
                // 鼠标是否可以移动到tooltip区域内
                enterable: true,
            },
            radar: {
                shape: 'circle',
                splitNumber: 5, // 雷达图圈数设置
                name: {
                    textStyle: {
                        color: '#666',
                    },
                },
                // 设置雷达图中间射线的颜色
                axisLine: {
                    lineStyle: {
                        color: '#d8d8d8'
                    },
                },
                indicator: indicator,
                // 雷达图背景的颜色，在这儿随便设置了一个颜色，完全不透明度为0，就实现了透明背景
                splitArea: {
                    show: false,
                    areaStyle: {
                        // 图表背景的颜色
                        // color: 'rgba(255,0,0,0)',
                    },
                },
                splitLine: {
                    show: true,
                    lineStyle: {
                        width: 1,
                        // 设置网格的颜色
                        color: '#d8d8d8',
                    },
                },
            },
            series: [{
                // tooltip中的标题
                name: this.title,
                // 表示是雷达图
                type: 'radar',
                // 拐点的样式，还可以取值'rect','angle'等
                symbol: 'circle',
                // 拐点的大小
                symbolSize: 8,

                areaStyle: {
                    normal: {
                        width: 1,
                        opacity: 1,
                    },
                },
                data: seriesData,
            }],
        })
        // 设置一个监听器 实时自适应屏幕
        window.addEventListener('resize', function () {
            myChart.resize()
        })
    },
}
</script>