/*
 * @FilePath: /public/panelJs/zhdp/rankingBar.js
 * @Description: 柱状图排行榜（当前20230420只支持到显示一个柱子）
 */
import { Get_Gradient_Color_By_Index } from "@/components/frame/Panel/PanelCode/theme"
import echarts from "echarts"
export default {
    getOptions (data) {
        // let cate = ['上海', '北京', '深圳', '天津', '河南', '新疆', '澳门']
        let { cate, series = [] } = data?.data || {}
        if (!series || !series.length) {
            return null
        }
        let fontSize = parseFloat(this.getRootFontSize())
        let barWidth = fontSize * 0.6
        let colorList = this.defaultChartAttrs?.color || []
        let getGradientColor = function (index) {
            let colors = Get_Gradient_Color_By_Index(index, colorList)
            return new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: colors[1] },
                { offset: 0.7, color: colors[1] },
                { offset: 1, color: colors[0] },
            ])
        }
        series = series.map((o, o_index) => ({
            type: 'bar',
            barWidth: `${barWidth}px`,
            zlevel: 3,
            itemStyle: {
                color: getGradientColor(o_index)
            },
            label: {
                normal: {
                    color: this.textColor,
                    show: true,
                    position: [0, `-${barWidth + fontSize / 2}px`],
                    formatter: function(a) {
                        return a.name
                    },
                }
            },
            emphasis: {
                itemStyle: {
                    color: Get_Gradient_Color_By_Index(o_index, colorList)[1]
                },
            },
            ...o,
        }))
        // 柱状图数据
        let dataList = series?.[0].data || []
        // 数据最大值向上取整
        let maxData = Math.ceil(Math.max(...dataList)) || 0
        // 柱状背景底数据
        let totalData = Array(dataList.length).fill(maxData)
        series.unshift({
            name: 'total',
            type: 'bar',
            zlevel: 1,
            barGap: '-100%',
            barWidth: `${barWidth}px`,
            data: totalData,
            legendHoverLink: false,
            itemStyle: {
                color: 'rgba(0, 0, 0, 0.45)'
            },
            label: {
                normal: {
                    color: this._attrs.importTextColor,
                    show: true,
                    position: ['80%', `-${barWidth + fontSize / 2}px`],
                    formatter: function(a) {
                        return dataList[a.dataIndex]
                    },
                }
            },
        })
        let option = {
            legend: {
                show: false
            },
            xAxis: {
                type: 'value',
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                splitLine: {
                    show: false
                },
                axisLabel: {
                    show: false
                }
            },
            yAxis: [
                {
                    type: 'category',
                    inverse: true,
                    axisTick: {
                        show: false
                    },
                    axisLine: {
                        show: false
                    },
                    axisLabel: {
                        show: false,
                        inside: false
                    },
                    data: cate
                }
            ],
            series,
        }
        return option
    }
}