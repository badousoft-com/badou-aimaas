<!--
    折线图
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
    name: 'line-echart',
    props: {
        seriesData: { // 需要显示的数据
            type: Array,
            default: function () {
                return [820, 932, 901, 934, 1290]
            }
        },
        nameL: { // y轴左侧标题
            type: String,
            default: '单位：万元'
        },
        nameR: { // y轴右侧标题
            type: String,
            default: '近五年可调剂金额'
        },
        height: { // 高度
            type: String,
            default: '300px'
        },
        title: String, // 标题
    },
    data () {
        return {

        }
    },
    methods: {

    },
    created () {

    },
    mounted () {
        let myChart = echarts.init(this.$refs.chart)
        // 绘制图表
        myChart.setOption({
            title: {
                text: this.title
            },
            xAxis: {
                type: 'category',
                boundaryGap: false, // 是否断点连线
                data: ['2015', '2016', '2017', '2018', '2019'],
                axisLine: {
                    show: false,  // 这里的show用于设置是否显示y轴那一条线 默认为true
                },
                axisTick: {
                    show: false,  // 是否显示刻度线 默认为true
                    alignWithLabel: true
                },
            },
            yAxis: [
                {
                    type: 'value',
                    name: this.nameL,
                    nameTextStyle: {
                        fontSize: 14,
                        align: 'middle',
                    },
                    nameLocation: 'end', // 位置在顶部
                    nameGap: 30, // 与y轴距离
                    nameRotate: 0, // 角度
                    axisLabel: {
                        margin: 5,
                        // formatter: '{value}.00'
                    },
                    axisLine: {
                        show: false,  // 这里的show用于设置是否显示y轴那一条线 默认为true
                    },
                    axisTick: {
                        show: false,  // 是否显示刻度线 默认为true
                        alignWithLabel: true
                    },
                    splitLine: {    // 网格线
                        lineStyle: {
                            type: 'dashed'    // 设置网格线类型 dotted：虚线   solid:实线
                        },
                        show: true // 隐藏或显示
                    }
                },
                {
                    type: 'value',
                    name: this.nameR,
                    nameTextStyle: {
                        fontSize: 14,
                        align: 'right',
                    },
                    nameLocation: 'end', // 位置在顶部
                    nameGap: 30, // 与y轴距离
                    axisLine: {
                        show: false,  // 这里的show用于设置是否显示y轴那一条线 默认为true
                    },
                }
            ],
            tooltip: {
                trigger: 'item'
            },
            grid: { // 图表离面板的距离
                top: '22%',
                left: '10%',
                right: '10%',
                bottom: '12%',
                containLabel: true
            },
            series: [{
                type: 'line',
                symbolSize: 8, // 小圆点大小
                data: this.seriesData,
                lineStyle: {
                    color: '#8A7AFB'
                },
                areaStyle: {
                    color: 'rgba(138,122,251,0.2)'
                },
                itemStyle: {
                    color: '#8A7AFB'
                },
            }]
        })
        // 设置一个监听器 让图表可以自适应
        window.addEventListener('resize', function () {
            myChart.resize()
        })
    }
}
</script>