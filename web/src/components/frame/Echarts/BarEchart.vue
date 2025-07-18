<!--
    柱状图
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
    name: 'bar-echart',
    props: {
        dataSource: Array, // 定义图例标签的内容以及x轴的值和每个柱状图的数值，下面有示例
        seriesData: Array, // 定义图表类型，以及样式等
        height: { // 装载echarts容器的高度, 柱状图的高度自适应容器的高度
            type: String,
            default: '300px'
        },
        orient: { // 标签的排布方式
            type: String,
            default: 'vertical'
        },
        title: String, // 标题
        icon: {
            type: String,
            default: 'circle'
        },
        left: { // 标签离面板左侧距离
            type: '',
            default: 10
        },
        top: { // 标签离面板顶部距离
            type: '',
            default: 10
        },
        right: '',  // 标签离面板右侧距离
        bottom: '', // 标签离面板底部距离
        colorTheme: { // 颜色主题
            type: String,
            default: 'default'
        },
        xAxisType: { // 默认x轴为主轴 若x轴为次轴则改为 'value'
            type: String,
            default: 'category'
        },
        yAxisType: { // 默认y轴为次轴 若y轴为主轴则改为 'category'
            type: String,
            default: 'value'
        },
        xTitle: '', // x轴标题
        yTitle: '', // y轴标题
    },
    data () {
        return {
            // axisTickShow: 'false', //  y轴刻度线
            // 示例
            data: {
                source: [
                    ['product', '人员', '公用', '专项', '基建'], // 左侧显示的内容
                    ['2017', 131, 143, 158, 161], // 第一个是坐标轴的label,打后都是每个柱状图的数值
                    ['2018', 145, 159, 167, 120],
                    ['2019', 138, 162, 157, 175]
                ]
            },
            series: {
                data:
                    [
                        {
                            type: 'bar',
                        },
                        {
                            type: 'bar',
                        },
                        {
                            type: 'bar',
                        },
                        {
                            type: 'bar',
                        }
                    ]
            }
        }
    },
    methods: {
        getList (list) {
            // 获取全局配色
            let colorList = this.colorScheme(this.colorTheme)
            let length = list.length
            // 默认配柱子的宽度
            if (length === 1 && this.yAxisType === 'value') {
                list[0].barWidth = '25%'
            }
            for (let i = 0; i < length; i++) {
                // 为series.data添加颜色
                if (list[i].type && list[i].type !== '') {
                    list[i].itemStyle = {}
                    list[i].itemStyle.color = colorList[i]
                }
                if (this.yAxisType !== 'value') {
                    list[i].barWidth = '40%'
                }
            }
        }
    },
    created () {

    },
    computed: {
        axisTickShow () {
            let show = false
            if (this.yAxisType === 'category') {
                show = true
            }
            return show
        }
    },
    mounted () {
        if (this.dataSource !== undefined && this.dataSource.length > 0) {
            this.data.source = this.dataSource
        }
        if (this.seriesData !== undefined && this.seriesData.length > 0) {
            this.series.data = this.seriesData
        }
        this.getList(this.series.data)
        let myChart = echarts.init(this.$refs.chart)
        // 绘制图表
        myChart.setOption({
            title: {
                text: this.title
            },
            legend: {
                orient: this.orient,
                left: this.left,
                top: this.top,
                bottom: this.bottom,
                right: this.right,
                icon: this.icon, // 图标的形状
                itemWidth: 20,
            },
            tooltip: {
                trigger: 'item', // 触发悬浮效果
            },
            dataset: {
                source: this.data.source
            },
            // 横坐标
            xAxis: {
                type: this.xAxisType,
                name: this.xTitle,
                nameTextStyle: {
                    fontSize: 14,
                    align: 'middle',
                },
                nameLocation: 'end', // 位置在顶部
                nameGap: 30, // 与y轴距离
                axisLine: {
                    show: false,  // 这里的show用于设置是否显示y轴那一条线 默认为true
                },
                axisTick: {
                    show: false,  // 是否显示刻度线 默认为true
                    alignWithLabel: true
                },
            },
            // 纵坐标
            yAxis: {
                type: this.yAxisType,
                name: this.yTitle,
                nameTextStyle: {
                    fontSize: 14,
                    align: 'middle',
                },
                nameLocation: 'end', // 位置在顶部
                nameGap: 30, // 与y轴距离
                axisLine: {
                    show: false,  // 这里的show用于设置是否显示y轴那一条线 默认为true
                },
                axisTick: {
                    show: this.axisTickShow,  // 是否显示刻度线 默认为true
                    alignWithLabel: true
                },
                // min: 120,
                // max: 180,
                // splitNumber: 6, // 坐标轴分割的段数
                // interval: 12, // y轴数值之间的差值
                splitLine: {    // 网格线
                    lineStyle: {
                        type: 'dashed'    // 设置网格线类型 dotted：虚线   solid:实线
                    },
                    show: true // 隐藏或显示
                }
            },
            grid: {// 设置柱状图位置   上下左右距离也可以用   y y2 x x2 表示 可以用百分比表示也可以直接数字  例如 x:20
                left: 90,
                right: '10%',
                top: '10%',
                bottom: '5%',
                containLabel: true
            },
            series: this.series.data
        })
        // 设置一个监听器 实时自适应屏幕
        window.addEventListener('resize', function () {
            myChart.resize()
        })
    },
}
</script>
