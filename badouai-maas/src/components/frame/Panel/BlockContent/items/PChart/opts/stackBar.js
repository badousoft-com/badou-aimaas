/*
 * @FilePath: @/components/frame/Panel/BlockContent/items/PChart/opts/stackBar.js
 * @Description: 堆叠柱状图
 */
import { Get_Gradient_Color_By_Index } from "@/components/frame/Panel/PanelCode/theme"
import echarts from "echarts"
export default {
    getOptions (data) {
        console.log(data)
        // 参数示例
        data = {
            cate: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
            series: [
                {
                    name: '电影',
                    data: [120, 132, 101, 134, 90, 230, 210]
                },
                {
                    name: '电视剧',
                    data: [220, 182, 191, 234, 290, 330, 310]
                },
                {
                    name: '田园生活',
                    stack: '综艺',
                    data: [150, 232, 201, 154, 190, 330, 410]
                },
                {
                    name: '王牌对王牌',
                    stack: '综艺',
                    data: [230, 210, 201, 154, 100, 330, 410]
                },
                {
                    name: '新闻联播',
                    data: [320, 332, 301, 334, 390, 330, 320]
                }
            ]
        }
        let { cate, series } = data
        if (!series || !series.length) {
            return null
        }
        let colorList = this.defaultChartAttrs?.color || []
        let getGradientColor = function (index) {
            let colors = Get_Gradient_Color_By_Index(index, colorList)
            return new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 1, color: colors[1] },
                { offset: 0.7, color: colors[0] },
                { offset: 0, color: colors[0] },
            ])
        }
        series = series.map((o, o_index) => ({
            type: 'bar',
            itemStyle: {
                color: getGradientColor(o_index)
            },
            emphasis: {
                itemStyle: {
                    color: Get_Gradient_Color_By_Index(o_index, colorList)[1]
                },
            },
            ...o,
        }))
        let chartOptions = {
            // barWidth: 18,
            tooltip: {
                trigger: 'axis',
                axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                    type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            legend: {
                textStyle: {
                    color: this.textColor,
                },
                data: series.map(o => o.name)
            },
            xAxis: [
                {
                    type: 'category',
                    data: cate,
                    axisTick: {
                        alignWithLabel: true
                    }
                }
            ],
            yAxis: [
                {
                    type: 'value'
                }
            ],
            series
        }
        return chartOptions
    }
}