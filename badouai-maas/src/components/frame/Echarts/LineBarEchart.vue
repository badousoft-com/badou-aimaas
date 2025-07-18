<!--
    折线图+柱状图混合现实
    也可单折线图显示
    或者单柱状图显示
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
    name: 'linebar-echart',
    props: {
        seriesData: { // 需要显示的数据，下面有示例
            type: Array,
            default: function () {
                return []
            }
        },
        height: { // 装载echarts容器的高度, 柱状图的高度自适应容器的高度
            type: String,
            default: '400px'
        },
        orient: { // 标签的排布方式
            type: String,
            default: 'horizontal'
        },
        top: { // 标签离面板顶部距离
            type: '',
            default: 20
        },
        ySuffixL: { // 左侧y轴标题的后缀 例如亿元或者%
            type: String,
            default: '亿元'
        },
        ySuffixR: { // 右侧y轴标题的后缀 例如亿元或者%
            type: String,
            default: '%'
        },
        nameR: { // 右侧y轴的标题
            type: String,
            default: '增减（%）'
        },
        nameL: { // 左侧y轴的标题
            type: String,
            default: '金额'
        },
        xData: { // x轴坐标标题数据
            type: Array,
            default: function () {
                return ['2015', '2016', '2017', '2018', '2019']
            }
        },
        minL: Number, // 左侧 y轴最小值
        maxL: Number, // 左侧 y轴最大值
        intervalL: Number,  // 左侧 y轴差值
        minR: Number, // 右侧 y轴最小值
        maxR: Number, // 右侧 y轴最大值
        intervalR: Number, // 右侧 y轴差值
        yShowR: { // 右侧y轴是否显示数据
            type: Boolean,
            default: true
        },
        icon: { // 图例图标 'circle', 'rect', 'roundRect', 'triangle', 'diamond', 'pin', 'arrow', 'none',可以通过 'image://url' 设置为图片，其中 URL 为图片的链接，或者 dataURI。
            type: String,
            default: ''
        },
        iconHeight: { // 图例图标高度
            type: Number,
            default: 14
        },
        iconWidth: { // 图例图标宽度
            type: Number,
            default: 20
        },
        colorTheme: { // 颜色主题
            type: String,
            default: 'default'
        },
    },
    data () {
        return {
            // 图例显示的标签数据
            legendData: [],
            series: { // 如若有3个柱状图 则传入3个bar类型
                data: [
                    {
                        name: '收入', // 与图例的标签相关联
                        type: 'bar',
                        data: [13, 13.5, 23.8, 34.5, 45]
                    },
                    {
                        name: '支出',
                        type: 'bar',
                        data: [11.3, 13.2, 21.5, 32.8, 37]
                    },
                    {
                        name: '收入增减',
                        type: 'line',
                        data: [3, 3.5, 6.8, 8.5, 10],
                        yAxisIndex: 1 // 设置哪个y轴 默认左侧的为0
                    },
                    {
                        name: '预测收入',
                        type: 'line',
                        data: [13, 13.5, 23.8, 34.5, 43],
                    },
                    {
                        name: '支出增减',
                        type: 'line',
                        data: [2.8, 3.1, 6.2, 8.3, 9.6],
                        yAxisIndex: 1
                    },
                    {
                        name: '预测支出',
                        type: 'line',
                        data: [11.3, 13.2, 21.5, 32.8, 36],
                    }
                ]
            }
        }
    },
    methods: {
        getList (list) {
            // 获取配色
            let colorList = this.colorScheme(this.colorTheme)
            for (let i = 0; i < list.length; i++) {
                list[i].itemStyle = {}
                list[i].itemStyle.color = colorList[i]
            }
        }
    },
    created () {

    },
    mounted () {
        let myChart = echarts.init(this.$refs.chart)
        // 默认显示的数据，可以看运行效果
        if (this.seriesData !== undefined && this.seriesData.length > 0) {
            this.series.data = this.seriesData
        }
        this.getList(this.series.data)
        this.legendData = this.series.data.filter(item => item.data)
        // 绘制图表
        myChart.setOption({
            legend: {
                data: this.legendData,
                orient: this.orient,
                left: 'center', // 标签居中显示
                top: this.top,
                textStyle: {
                    fontSize: 12
                },
                icon: this.icon,
                itemWidth: this.iconWidth,
                itemHeight: this.iconHeight
            },
            tooltip: {
                trigger: 'axis',
                // 添加水平辅助线
                axisPointer: {  // 坐标轴指示器，坐标轴触发有效，
                    type: 'cross', // 默认为line，line直线，cross十字准星，shadow阴影
                },
                formatter: (param) => {
                    // param传入的是整个series对象
                    let selectedYear = param[0].name
                    let text = ''
                    for (let i = 0; i < param.length; i++) {
                        let style = '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + param[i].color + '"></span>'
                        text = text + style + param[i].seriesName + ':' + param[i].data + '<br/>'
                    }
                    return selectedYear + '<br/>' + text
                }
            },
            // 横坐标
            xAxis: {
                type: 'category',
                data: this.xData,
                axisLine: {
                    show: false,  // 这里的show用于设置是否显示y轴那一条线 默认为true
                },
                axisTick: {
                    show: false,  // 是否显示刻度线 默认为true
                    alignWithLabel: true
                },
            },
            // 纵坐标
            yAxis: [
                {
                    // 左侧纵坐标
                    type: 'value',
                    name: this.nameL,
                    nameTextStyle: {
                        fontSize: 14,
                        align: 'middle',
                    },
                    nameLocation: 'end', // 位置在顶部
                    nameGap: 30, // 与y轴距离
                    nameRotate: 0, // 角度
                    min: this.minL, // 最小值
                    max: this.maxL, // 最大值
                    interval: this.intervalL, // 数值差
                    axisLabel: {
                        margin: 5,
                        formatter: '{value}' + this.ySuffixL,
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
                            type: 'dashed'    // 设置网格线类型 dotted:虚线   solid:实线
                        },
                        show: true // 隐藏或显示
                    },
                },
                // 右侧纵坐标
                {
                    type: 'value',
                    name: this.nameR,
                    nameTextStyle: {
                        fontSize: 14,
                        align: 'middle',
                    },
                    nameLocation: 'end', // 位置在顶部
                    nameGap: 30, // 与y轴距离
                    nameRotate: 0, // 角度
                    min: this.minR, // 最小值
                    max: this.maxR, // 最大值
                    interval: this.intervalR, // 数值差
                    axisLabel: {
                        margin: 5,
                        show: this.yShowR,
                        formatter: '{value}' + this.ySuffixR,
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
                    },
                },
            ],
            grid: {// 设置图标位置   上下左右距离也可以用   y y2 x x2 表示 可以用百分比表示也可以直接数字  例如 x:20
                left: '10%',
                right: '10%',
                top: '25%',
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