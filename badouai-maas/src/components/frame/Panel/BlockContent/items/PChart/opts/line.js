/*
 * @FilePath: @/components/frame/Panel/BlockContent/items/PChart/opts/line.js
 * @Description: 折线图
 */
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
        //             name: '综艺',
        //             data: [150, 232, 201, 154, 190, 330, 410]
        //         },
        //         {
        //             name: '新闻联播',
        //             data: [320, 332, 301, 334, 390, 330, 320]
        //         }
        //     ]
        // }
        let { cate, series } = data?.data || {}
        if (!series || !series.length) {
            return null
        }
        let chartOptions = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                    type: 'line'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            legend: {
                textStyle: {
                    color: this.textColor,
                },
                data: series.map(o => o.name)
            },
            xAxis: {
                type: 'category',
                data: cate,
                splitLine: {
                    lineStyle: {
                        color: [this.textColor],
                    }
                }
            },
            yAxis: {
                type: 'value',
                splitLine: {
                    lineStyle: {
                        type: 'dotted',
                        color: [this.textColor],
                    }
                }
            },
            series: series.map(o => {
                return { name: o.name, type: 'line', data: o.data, smooth: true }
            })
        }
        return chartOptions
    }
}