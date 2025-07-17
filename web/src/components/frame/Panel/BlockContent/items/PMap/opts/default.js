/*
 * @FilePath: @/components/frame/Panel/BlockContent/items/PMap/opts/default.js
 * @Description: 默认显示地图
 */
import { Get_Color_By_Index } from "@/components/frame/Panel/PanelCode/theme"
export default {
    getOptions (data) {
        let { normal, hover } = this.themeInfo
        let mapName = this.mapType || 'customMap'
        let mapSeries = {
            name: '',
            type: 'map',
            mapType: mapName,  // 自定义扩展图表类型
            roam: true,
            // 地图文本展示
            label: {
                normal: {
                    show: true,
                    textStyle: {
                        color: normal.textColor
                    }
                },
            },
            // 区域样式
            itemStyle: {
                normal: {
                    borderColor: normal.borderColor,
                    borderWidth: 1,
                    areaColor: normal.areaColor,
                },
                emphasis: {
                    areaColor: {
                        type: 'radial',
                        x: 0.5,
                        y: 0.5,
                        r: 1,
                        colorStops: [{
                            offset: 0,
                            color: normal.areaColor // 0% 处的颜色
                        }, {
                            offset: 1,
                            color: hover.areaColor // 100% 处的颜色
                        }],
                        globalCoord: false // 缺省为 false
                    },
                    borderWidth: 0,
                    color: hover.areaColor
                }
            },
        }
        let chartOptions = {
            tooltip: {
                formatter: function (e, t, n) {
                    return `${e.name}：${e.value || 0}`
                },
                textStyle: {
                    color: normal.tooltipTextColor
                }
            },
            legend: {
                show: false
            },
            series: [mapSeries]
        }
        let seriesData = data?.data?.series
        // 如果接口没有数据，直接返回，以便地图回显
        if (!seriesData?.length) return chartOptions
        // 计算出最小只与最大值
        let valueList = seriesData.map(o => o.value || 0)
        let min_data = Math.min.apply(Math, valueList)
        let max_data = Math.max.apply(Math, valueList)
        // 去除圆点
        mapSeries.showLegendSymbol = false
        // 给地图数据赋值
        mapSeries.data = seriesData
        // 当前地图色系
        let colorList = this.defaultChartAttrs?.color || []
        chartOptions.visualMap = {
            showLabel: true,
            calculable: true,
            textStyle: {
                color: this.textColor || '#333'
            }
        }
        // 获取地图数据数据段数
        let pieceNum = normal.pieceNum || colorList.length
        if (!pieceNum) {
            chartOptions.visualMap = Object.assign({
                min: min_data,
                max: max_data,
                inRange: {
                    color: [Get_Color_By_Index(1, colorList), Get_Color_By_Index(0, colorList)]
                },
                text: ['高', '低'], // 文本，默认为数值文本
            }, chartOptions.visualMap)
        } else {
            // 获取地图数据段
            let pieceColors = normal.pieceColor || colorList
            // 获取数据差值
            let diff = max_data - min_data
            // 每个数据段的大小
            let size = Math.floor(diff / pieceNum)
            let pieces = [], tempNum = min_data
            for (let i_index = 0; i_index < pieceNum; i_index++) {
                let color = Get_Color_By_Index(i_index, pieceColors)
                if (!i_index) pieces.push({ lte: tempNum, label: `小于或等于${tempNum}`, color })
                else if (i_index === pieceNum - 1) pieces.push({ gt: Math.floor(max_data - size), label: `${Math.floor(max_data - size)}或以上`, color })
                else {
                    pieces.push({
                        gt: tempNum,
                        lte: Math.floor(tempNum + size),
                        label: `大于${tempNum}，小于或等于${Math.floor(tempNum + size)}`,
                        color
                    })
                    tempNum = Math.floor(tempNum + size)
                }
            }
            chartOptions.visualMap = Object.assign({
                type: 'piecewise',
                pieces,
                splitNumber: pieceNum,
            }, chartOptions.visualMap)
        }
        return chartOptions
    }
}
