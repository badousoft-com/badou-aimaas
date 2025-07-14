<!--
    大小不一的饼图
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
    name: 'cus-pie',
    props: {
        seriesData: Array, // 需要显示的数据，下面有示例
        right: '', // 标签离右侧的距离 可以是百分比也可以是具体数值
        top: '', // 标签离上部的距离
        left: '',
        bottom: '',
        radius: { // 饼图的大小
            type: String,
            default: '70%'
        },
        center: {
            type: Array,
            default: function () {
                return ['27%', '50%']
            } // 环形离面板的距离 第一个是离左侧距离 第二个是离顶部距离
        },
        orient: { // 标签的排布方式
            type: String,
            default: 'vertical'
        },
        height: { // 容器的高度
            type: String,
            default: '300px'
        },
        labelFontSize: { // 标签字体大小
            type: Number,
            default: 14
        },
        colorTheme: { // 颜色主题
            type: String,
            default: 'default'
        }
    },
    data () {
        return {
            data: {
                legendData: [],
                seriesData: []
            },
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
        // 示例
        // this.data.seriesData = [
        //     {
        //         name: '农业产业发展类资金',
        //         value: 120,
        //         itemStyle: {
        //             color: '#F4B51F'
        //         }
        //     },
        //     {
        //         name: '专项经费',
        //         value: 140,
        //         itemStyle: {
        //             color: '#0482ea'
        //         }
        //     },
        //     {
        //         name: '农村人居环境整治类资金',
        //         value: 100,
        //         itemStyle: {
        //             color: '#8371F9'
        //         }
        //     }
        // ]
        // this.data.legendData = this.data.seriesData.filter(item => item.value)
        // for (let i = 0; i < this.data.seriesData.length; i++) {
        //     this.data.selected[name] = i < this.data.seriesData.length
        // }
        this.data.seriesData = this.seriesData
        this.getList(this.data.seriesData)
        this.data.legendData = this.data.seriesData.filter(item => item.value)
        let myChart = echarts.init(this.$refs.chart)
        // 绘制图表
        myChart.setOption({
            tooltip: {
                trigger: 'item', // 触发悬浮效果
                formatter: '{a} <br/>{b} : {c} ({d}%)' // 调整悬浮效果的格式
            },
            legend: {
                type: 'scroll', // 标签太多的话 会分页显示
                orient: this.orient,
                right: this.right,
                top: this.top,
                left: this.left,
                bottom: this.bottom,
                data: this.data.legendData,
                textStyle: {
                    fontSize: this.labelFontSize,
                    padding: 2
                },
                icon: 'circle', // icon 设置成圆 默认矩形
                itemWidth: this.labelFontSize, // icon的宽度 默认icon的宽度等于字体的大小
            },
            series: [{
                name: '类型',
                type: 'pie',
                radius: this.radius, // 饼图大小
                center: this.center,
                roseType: 'radius', // 设置成大小不一的饼图
                data: this.data.seriesData,
                animation: true,
                left: 0,
                label: {
                    fontSize: 14,
                    normal: {
                        show: true,
                        color: '#FFFFFF', // 字体颜色
                        position: 'inner', // 在图表内显示
                        formatter: '{d}%', // 只显示对应的百分比
                    },
                },
                // 动画效果
                animationType: 'scale',
                animationEasing: 'elasticOut',
                animationDelay: function (idx) {
                    return Math.random() * 200
                }
            }]
        })
        // 设置一个监听器 让图表可以自适应
        window.addEventListener('resize', function () {
            myChart.resize()
        })
    }
}
</script>