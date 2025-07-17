/*
 * @FilePath: @/components/frame/Panel/BlockContent/items/PChart/opts/barMixLine.js
 * @Description: 柱线混合
 */
import { Get_Gradient_Color_By_Index } from "@/components/frame/Panel/PanelCode/theme"
import echarts from "echarts"
export default {
    getOptions (data) {
        // 参数示例
        // data = {
        //     cate: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
        //     series: [
        //         {
        //             name: '电影',
        //             data: [120, 132, 101, 134, 90, 230, 210]
        //         },
        //         {
        //             name: '电视剧',
        //             data: [220, 182, 191, 234, 290, 330, 310]
        //         },
        //         {
        //             name: '田园生活',
        //             data: [150, 232, 201, 154, 190, 330, 410]
        //         },
        //         {
        //             name: '王牌对王牌',
        //             data: [230, 210, 201, 154, 100, 330, 410]
        //         },
        //         {
        //             name: '新闻联播',
        //             type: 'line',
        //             data: [320, 332, 301, 334, 390, 330, 320]
        //         }
        //     ]
        // }
        let { cate, series, yUtilLeft, yUtilRight } = data?.data || {}
        if (!series || !series.length) {
            return null
        }
        let colorList = this.defaultChartAttrs?.color || []
        let getGradientColor = function (index) {
            let colors = Get_Gradient_Color_By_Index(index, colorList)
            return new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: colors[1] },
                { offset: 0.7, color: colors[1] },
                { offset: 1, color: colors[0] },
            ])
        }
        series = series.map((o, o_index) => {
            let item = { type: 'bar', ...o }
            // 柱状图时，需要设置柱子渐变色
            if (item.type === 'bar') {
                item.itemStyle = {
                    color: getGradientColor(o_index)
                }
                item.emphasis = {
                    itemStyle: {
                        color: Get_Gradient_Color_By_Index(o_index, colorList)[1]
                    },
                }
            }
            return item
        })
        let fontSize = parseFloat(this.getRootFontSize())
        let chartOptions = {
            // barWidth: 18,
            // grid: {
            //     right: '4%'
            // },
            tooltip: {
                trigger: 'axis',
                axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                    type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                },
            },
            legend: {
                data: series.map(o => o.name)
            },
            label: {            // 图图形上的文本标签
                normal: {
                    show: true,
                    position: 'top', // 标签的位置
                    distance: 18,
                    offset: [-8, 0],
                }
            },
            xAxis: [
                {
                    type: 'category',
                    data: cate,
                    axisTick: {
                        alignWithLabel: true
                    },
                    axisLine: {
                        lineStyle: {
                            type: 'dotted',
                        }
                    },
                    axisLabel: {
                        show: true,
                        textStyle: {
                            color: this.textColor,
                            fontSize,
                        }
                    },
                    splitLine: {
                        lineStyle: {
                            color: [this.textColor],
                            type: 'dotted',
                        }
                    },
                }
            ],
            yAxis: [],
            series
        }
        let yAxisItem = {
            type: 'value',
            axisLine: {
                lineStyle: {
                    type: 'dotted',
                }
            },
            axisLabel: {
                show: true,
                textStyle: {
                    color: this.textColor,
                    fontSize,
                }
            },
            splitLine: {
                lineStyle: {
                    color: [this.textColor],
                    type: 'dotted',
                }
            }
        }
        // 是否含有柱状图；是否含有折线图
        let barList = series.filter(o => !o.type || o.type === 'bar')
        let lineList = series.filter(o => o.type === 'line')
        chartOptions.yAxis.push({ ...yAxisItem, name: yUtilLeft || '', nameTextStyle: { align: 'left' } })
        // 两者都存在时，出现两纵轴
        if (barList.length && lineList.length) {
            // 添加第二个纵轴
            chartOptions.yAxis.push({ ...yAxisItem, name: yUtilRight || '', nameTextStyle: { align: 'right' }})
            // 将折线图的坐标轴设置为右侧
            lineList.forEach(l => {
                l.yAxisIndex = 1
            })
        }
        return chartOptions
    }
}