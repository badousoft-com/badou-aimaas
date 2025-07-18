/*
 * @FilePath: @/components/frame/Panel/BlockContent/items/PChart/opts/customized.js
 * @Description: 玫瑰图（不规则饼图）
 */
import { Get_Gradient_Color_By_Index } from '@/components/frame/Panel/PanelCode/theme/index.js'
export default {
    getOptions (data) {
        // 参数示例
        // data = [
        //     {"name":"测试","value":0},
        //     {"name":"研发部","value":2},
        //     {"name":"11","value":1},
        //     {"name":"开发小组","value":2},
        //     {"name":"设计部","value":1},
        //     {"name":"测试部","value":6},
        //     {"name":"212","value":1},
        //     {"name":"11","value":1},
        //     {"name":"八斗开发","value":6}
        // ]
        let colorList = this.defaultChartAttrs?.color || []
        let series = data?.data || []
        if (!series.length) {
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
            },
            series: {
                type: 'pie',
                // radius: [20, 90],
                center: ['50%', '50%'],
                minAngle: 2,  // 最小的扇区角度（0~360），用于防止某个值过小导致扇区太小影响交互
                avoidLabelOverlap: true, // 是否启用防止标签重叠策略
                minAngle: 2,
                roseType: 'area',
                // label: {
                //     show: false
                // },
                roseType: 'radius',
                emphasis: {
                    label: {
                        show: true
                    }
                },
                itemStyle: {
                    shadowBlur: 10,
                    shadowColor: 'rgba(0, 103, 255, 0.2)',
                    shadowOffsetX: -5,
                    shadowOffsetY: 5,
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