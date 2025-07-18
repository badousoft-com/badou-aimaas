/*
 * @FilePath: @/components/frame/Panel/BlockContent/items/PChart/setUtils/setRing3D.js
 * @Description: 设置3D环形属性
 */
let getParametricEquation = function (startRatio, endRatio, isSelected, isHovered, k, h, config) {
    // 计算
    let midRatio = (startRatio + endRatio) / 2
    let startRadian = startRatio * Math.PI * 2
    let endRadian = endRatio * Math.PI * 2
    let midRadian = midRatio * Math.PI * 2
    // 如果只有一个扇形，则不实现选中效果。
    if (startRatio === 0 && endRatio === 1) {
        isSelected = false
    }
    // 通过扇形内径/外径的值，换算出辅助参数 k（默认值 1/3）
    k = typeof k !== 'undefined' ? k : 1 / 3
    // 计算选中效果分别在 x 轴、y 轴方向上的位移（未选中，则位移均为 0）
    let offsetX = (isSelected ? Math.cos(midRadian) * 0.1 : 0)
    let offsetY = (isSelected ? Math.sin(midRadian) * 0.1 : 0)
    // 计算高亮效果的放大比例（未高亮，则比例为 1）
    let hoverRate = isHovered ? 1.05 : 1
    // 返回曲面参数方程
    return {
        u: {
            min: -Math.PI,
            max: Math.PI * 3,
            step: Math.PI / 32
        },
        v: {
            min: 0,
            max: Math.PI * 2,
            step: Math.PI / 20
        },
        x: function (u, v) {
            if (u < startRadian) {
                return offsetX + Math.cos(startRadian) * (1 + Math.cos(v) * k) * hoverRate
            }
            if (u > endRadian) {
                return offsetX + Math.cos(endRadian) * (1 + Math.cos(v) * k) * hoverRate
            }
            return offsetX + Math.cos(u) * (1 + Math.cos(v) * k) * hoverRate
        },
        y: function (u, v) {
            if (u < startRadian) {
                return offsetY + Math.sin(startRadian) * (1 + Math.cos(v) * k) * hoverRate
            }
            if (u > endRadian) {
                return offsetY + Math.sin(endRadian) * (1 + Math.cos(v) * k) * hoverRate
            }
            return offsetY + Math.sin(u) * (1 + Math.cos(v) * k) * hoverRate
        },
        z: function (u, v) {
            if (u < -Math.PI * 0.5) {
                return Math.sin(u)
            }
            if (u > Math.PI * 2.5) {
                return Math.sin(u) * h * .1
            }
            return Math.sin(v) > 0 ? 1 * h * .1 : -1
        }
    }
}
let fomatFloat =  function (num, n) {
    let f = parseFloat(num)
    if (isNaN(f)) {
        return false
    }
    f = Math.round(num * Math.pow(10, n)) / Math.pow(10, n) // n 幂   
    let s = f.toString()
    let rs = s.indexOf('.')
    //判定如果是整数，增加小数点再补0
    if (rs < 0) {
        rs = s.length
        s += '.'
    }
    while (s.length <= rs + n) {
        s += '0'
    }
    return s
}
/**
 * @description: 设置3D环形属性
 * @param {Array} optionData：饼图的数据
 * @param {Object} config：图表的渲染属性数据
 *          ratio：透明的空心占比，默认：0.8；
 *          center：待确认，默认['10%', '50%']；
 *          alpha：倾斜角度，默认35；
 *          distance：调整视角到主体的距离，类似调整zoom，值越小，图越大，默认250，
 *          height：环形图高度，默认26
 *          startAngle: 起始角度，默认-20
 *          fontSize: 字体大小，默认14
 * @return {Object} 返回echart所需的option对象
 */
export function Set_Ring_3D (optionData, config = {}) {
    let {
        ratio = 0.8,
        center = ['10%', '50%'],
        alpha = 35,
        distance = 250,
        height = 26,
        startAngle = -20,
        fontSize = 14,
        grid3D = {},
    } = config
    let series = [], sumValue = 0, startValue = 0, endValue = 0,
        legendData = [], legendBfb = [], k = 1 - ratio
    optionData.sort((a, b) => {
        return (b.value - a.value)
    })
    // 获取3d丙图的最高扇区的高度
    let getHeight3D = function (series) {
        series.sort((a, b) => {
            return (b.pieData.value - a.pieData.value)
        })
        return height * 15 / series[0].pieData.value
    }
    // 为每一个饼图数据，生成一个 series-surface 配置
    for (let i = 0; i < optionData.length; i++) {
        sumValue += optionData[i].value
        let seriesItem = {
            name: typeof optionData[i].name === 'undefined' ? `series${i}` : optionData[i].name,
            type: 'surface',
            parametric: true,
            wireframe: {
                show: false
            },
            pieData: optionData[i],
            pieStatus: {
                selected: false,
                hovered: false,
                k: k
            },
            center
        }
        series.push(seriesItem)
    }

    // 使用上一次遍历时，计算出的数据和 sumValue，调用 getParametricEquation 函数，
    // 向每个 series-surface 传入不同的参数方程 series-surface.parametricEquation，也就是实现每一个扇形。
    legendData = [], legendBfb = []
    for (let i = 0; i < series.length; i++) {
        endValue = startValue + series[i].pieData.value
        series[i].pieData.startRatio = startValue / sumValue
        series[i].pieData.endRatio = endValue / sumValue
        series[i].parametricEquation = getParametricEquation(series[i].pieData.startRatio, series[i].pieData.endRatio,
            false, false, k, series[i].pieData.value, config)
        
        series[i].tooltip = {
            formatter (info) {
                return `${info.marker} ${info.seriesName}：${optionData?.[info.seriesIndex]?.value || 0}`
            }
        }
        startValue = endValue
        let bfb = fomatFloat(series[i].pieData.value / sumValue, 4)
        legendData.push({
            name: series[i].name,
            value: bfb
        })
        legendBfb.push({
            name: series[i].name,
            value: bfb
        })
    }
    let boxHeight = getHeight3D(series)//通过传参设定3d饼/环的高度，26代表26px
    // 准备待返回的配置项，把准备好的 legendData、series 传入。
    let option = {
        legend: {
            show: false,
            selectedMode: false,
        },
        labelLine: {
            show: true
        },
        textStyle: {
            color: null
        },
        label: {
            position: 'outside',
            rich: {
                text: {
                    // color: '#7BC0CB',
                    fontSize: fontSize,
                    lineHeight: fontSize * 1.4
                },
                percent: {
                    color: this.textColor,
                    fontSize: fontSize * 1.2,
                },
            },
            formatter: '{text|{b} \n}{percent|{d}}{text|  %}',
        },
        xAxis3D: {
            min: -1,
            max: 1
        },
        yAxis3D: {
            min: -1,
            max: 1
        },
        zAxis3D: {
            min: -1,
            max: 1
        },
        grid3D: {
            show: false,
            boxHeight: boxHeight, // 圆环的高度
            viewControl: { // 3d效果可以放大、旋转等，请自己去查看官方配置
                alpha, // 角度
                distance,// 调整视角到主体的距离，类似调整zoom
                rotateSensitivity: 0, // 设置为0无法旋转
                zoomSensitivity: 0, // 设置为0无法缩放
                panSensitivity: 0, // 设置为0无法平移
                autoRotate: false //自动旋转
            },
            ...(grid3D || {}),
        },
        series: series
    }
    option.series.push({
        name: 'pie2d',
        type: 'pie',
        tooltip: {
            show: false,
        },
        // labelLine: {
        //     length: 10,
        //     length2: 10
        // },
        startAngle, // 起始角度，支持范围[0, 360]。
        clockwise: false,// 饼图的扇区是否是顺时针排布。上述这两项配置主要是为了对齐3d的样式
        radius: ['10%', '50%'],
        center: ['50%', '50%'],
        data: optionData,
        itemStyle: {
            opacity: 0
        }
    })
    return option
}