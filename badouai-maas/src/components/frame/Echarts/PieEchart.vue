<!--
    环形图
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
    name: 'pie-echart',
    props: {
        seriesData: Array, // 需要显示的数据，下面有示例
        right: '', // 标签离右侧的距离 可以是百分比也可以是具体数值
        top: '', // 标签离上部的距离
        left: '',
        bottom: '',
        radius: { // 第一个是外部圆 第二个是内部圆
            type: Array,
            default: function () {
                return ['50%', '64%']
            }
        },
        center: { // 环形离面板的距离 第一个是离左侧距离 第二个是离顶部距离
            type: Array,
            default: function () {
                return ['27%', '50%']
            }
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
                seriesData: [],
                selected: {}
            },
            showLabel: true,
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
        // 示例 name和legend标签相关联，必传
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
        this.data.seriesData = this.seriesData
        this.data.legendData = this.data.seriesData.filter(item => item.value)
        this.getList(this.data.seriesData)
        // 设置默认选中的项
        let count = this.data.seriesData.length
        for (let i = 0; i < count; i++) {
            this.data.selected[name] = i < count
        }
        let myChart = echarts.init(this.$refs.chart)
        // myChart.on('legendselectchanged', function (obj) {
        //     let selected = obj.selected
        //     let selectedArray = []
        //     let name = obj.name
        //     // 把对象转换成数组
        //     for (name in selected) {
        //         selectedArray.push(selected[name])
        //     }
        //     console.log(selectedArray)
        //     let newArr = []
        //     newArr = selectedArray.filter(item => item !== false)
        //     if (newArr.length === 1) {
        //         this.showLabel = false
        //     }
        //     console.log(newArr)
        // })
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
                selected: this.data.selected,
                textStyle: {
                    fontSize: this.labelFontSize,
                    padding: 2
                },
                icon: 'circle', // icon 设置成圆 默认矩形
                itemWidth: this.labelFontSize, // icon的宽度 默认icon的宽度等于字体的大小
            },
            series: [{
                name: '类型',
                type: 'pie', // 饼状图
                center: this.center,
                radius: this.radius,
                data: this.data.seriesData,
                animation: true,
                left: 0,
                label: {
                    fontSize: 14,
                    normal: {
                        show: this.showLabel,
                        position: 'outside',
                        formatter: '{d}%', // 只显示对应的百分比
                    },
                },
                labelLine: { // 视觉引导线
                    normal: {
                        show: false,
                        length: -3
                    }
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