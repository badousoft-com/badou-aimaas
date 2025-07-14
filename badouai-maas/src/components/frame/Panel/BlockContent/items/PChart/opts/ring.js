/*
 * @FilePath: @/components/frame/Panel/BlockContent/items/PChart/opts/ring.js
 * @Description: 环形图
 */
import { Get_Gradient_Color_By_Index } from '@/components/frame/Panel/PanelCode/theme/index.js'
export default {
    getOptions (data) {
        // 参数示例
        // data = {
        //     "legend": ["测试","研发部","11","开发小组","设计部","测试部","212","11","八斗开发"],
        //     "series": [
        //         {"name":"测试","value":0},
        //         {"name":"研发部","value":2},
        //         {"name":"11","value":1},
        //         {"name":"开发小组","value":2},
        //         {"name":"设计部","value":1},
        //         {"name":"测试部","value":6},
        //         {"name":"212","value":1},
        //         {"name":"11","value":1},
        //         {"name":"八斗开发","value":6}
        //     ]
        // }
        let colorList = this.defaultChartAttrs?.color || []
        let { series, legend } = data?.data || {}
        if (!series || !series.length) {
            return null
        }
        let chartOptions = {
            tooltip: {
                trigger: 'item',
                confine: true // 将此限制打开后tooltip将不再溢出
            },
            legend: {
                show: false,
                textStyle: {
                    color: this.textColor,
                },
                orient: 'vertical',
                left: 'left',
                data: legend
            },
            series: {
                type: 'pie',
                radius: ['30%', '50%'],
                avoidLabelOverlap: false,
                itemStyle: {
                    borderRadius: 10,
                    borderColor: this.defaultChartAttrs.backgroundColor,
                    borderWidth: 2,
                    color (params) { // 颜色定制显示（按顺序）
                        let resColor = Get_Gradient_Color_By_Index(params.dataIndex, colorList)
                        let obj = {
                            type: 'linear',
                            x: 0,
                            y: 0,
                            x2: 0,
                            y2: 1,
                            colorStops: [{
                                offset: 0, color: resColor[0] // 0% 处的颜色
                            }, {
                                offset: 1, color: resColor[1] // 100% 处的颜色
                            }],
                            global: false // 缺省为 false
                        }
                        return obj
                    }
                },
                data: series
            }
        }
        return chartOptions
    }
}