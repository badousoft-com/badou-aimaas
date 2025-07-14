/*
 * @FilePath: @/components/frame/Panel/BlockContent/items/PChart/opts/radar.js
 * @Description: 雷达图
 */
import { Get_Color_By_Index } from "@/components/frame/Panel/PanelCode/theme"
export default {
    getOptions (data) {
        // console.log(data)
        // 参数示例
        // data = {
        //     indicator: [
        //         { text: 'Indicator1' },
        //         { text: 'Indicator2' },
        //         { text: 'Indicator3' },
        //         { text: 'Indicator4' },
        //         { text: 'Indicator5' }
        //     ],
        //     series: [
        //         {
        //             value: [100, 8, 0.4, -80, 2000],
        //             name: 'Data A'
        //         },
        //         {
        //             value: [60, 5, 0.3, -100, 1500],
        //             name: 'Data B',
        //         }
        //     ]
        // }
        let { indicator, series } = data?.data
        if (!series || !series.length) {
            return null
        }
        let colorList = this.defaultChartAttrs?.color || []
        series.forEach((s, s_index) => {
            // 区域颜色
            let areaColor = Get_Color_By_Index(s_index, colorList)
            let areaStyle = {
                normal: { // 单项区域填充样式
                    color: {
                        type: 'linear',
                        x: 0, //右
                        y: 0, //下
                        x2: 1, //左
                        y2: 1, //上
                        colorStops: [{
                            offset: 0,
                            color: areaColor
                        }, {
                            offset: 0.5,
                            color: 'rgba(0, 0, 0, 0)'
                        }, {
                            offset: 1,
                            color: areaColor
                        }],
                        globalCoord: false
                    },
                    opacity: 1 // 区域透明度
                }
            }
            s.areaStyle = s.areaStyle || areaStyle
        })
        let chartOptions = {
            tooltip: {
                show: true,
                // 雷达图的tooltip不会超出div，也可以设置position属性，position定位的tooltip 不会随着鼠标移动而位置变化，不友好
                confine: true,
                // 鼠标是否可以移动到tooltip区域内
                enterable: true,

            },
            radar: [
                {
                    indicator,
                    // center: ['25%', '50%'],
                    // radius: 100,
                    // startAngle: 90,
                    // splitNumber: 4,
                    zlevel: 11,
                    shape: 'circle',
                    scale: true,
                    center: ['50%', '60%'],
                    radius: '70%',
                    startAngle: 30,
                    name: {
                        show: true,
                        // color: 'transparent'
                    },
                }
            ],
            polar: {
                radius: '40%',
            },
            angleAxis: {
                zlevel: 0,
                min: 0,
                max: 360,
                interval: 5,
                clockwise: false,
                axisTick: {
                    show: false
                },
                axisLabel: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                splitLine: {
                    show: false
                }
            },
            radiusAxis: {
                zlevel: 0,
                axisLabel: {
                    textStyle: {
                        color: this.textColor
                    }
                },
                axisLine: {
                    show: false
                },
                splitLine: {
                    show: false
                },
                splitArea: {
                    show: false
                },
            },

            series: [
                {
                    type: 'radar',
                    data: series
                },
            ]
        }
        return chartOptions
    }
}