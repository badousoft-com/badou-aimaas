/*
 * @FilePath: @/components/frame/Panel/BlockContent/items/PChart/opts/stackArea.js
 * @Description: 堆叠区域图
 */
import { Get_Gradient_Color_By_Index } from "@/components/frame/Panel/PanelCode/theme"
import echarts from "echarts"
export default {
    getOptions (data) {
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
        let colorList = this.defaultChartAttrs?.color || []
        let getGradientColor = function (index) {
            let colors = Get_Gradient_Color_By_Index(index, colorList)
            return new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: colors[1] },
                { offset: 1, color: colors[0] },
            ])
        }
        series = series.map((o, o_index) => ({
            type: 'line',
            // 区域样式
            areaStyle: {
                color: getGradientColor(o_index)
            },
            ...o
        }))
        let chartOptions = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'line',
                    label: {
                        show: true
                    }
                }
            },
            legend: {
                textStyle: {
                    color: this.textColor,
                },
                data: series.map(o => o.name),
            },
            xAxis: [
                {
                    type: 'category',
                    boundaryGap: false,
                    data: cate
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