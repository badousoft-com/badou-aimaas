/*
 * @FilePath: @/components/frame/Panel/BlockContent/items/PChart/opts/wordCloud.js
 * @Description: 字符云
 */
import { Get_Color_By_Index } from '@/components/frame/Panel/PanelCode/theme/index.js'
export default {
    getOptions (data) {
        // 参数示例
        // series = [
        //     { name: '雨伞', value: 30 },
        //     { name: '晴天', value: 28 },
        //     { name: '电话', value: 24 },
        //     { name: '手机', value: 23 },
        // ]
        let series = data?.data || []
        if (!series.length) {
            return null
        }
        let colorList = this.defaultChartAttrs?.color || []
        let chartOptions = {
            tooltip: {
                show: true,
                position: 'top',
                textStyle: {
                    fontSize: 30
                }
            },
            series: [{
                type: 'wordCloud',
                // 网格大小，各项之间间距
                gridSize: 30,
                // 形状 circle 圆，cardioid  心， diamond 菱形，
                // triangle-forward 、triangle 三角，star五角星
                shape: 'circle',
                // 字体大小范围
                sizeRange: [20, 50],
                // 文字旋转角度范围
                rotationRange: [0, 0],
                // 旋转步值
                rotationStep: 90,
                // 自定义图形
                // maskImage: maskImage,
                left: 'center',
                top: 'center',
                right: null,
                bottom: null,
                // 画布宽
                width: '90%',
                // 画布高
                height: '80%',
                // 是否渲染超出画布的文字
                drawOutOfBound: false,
                textStyle: {
                    normal: {
                        color: function (e) {
                            return Get_Color_By_Index(e.dataIndex, colorList)
                        }
                    },
                    emphasis: {
                        shadowBlur: 10,
                        shadowColor: '#2ac'
                    }
                },
                data: series
            }]
        }
        return chartOptions
    }
}
