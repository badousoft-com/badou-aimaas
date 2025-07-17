<!--
 * @FilePath: @/components/frame/Panel/BlockContent/items/PChart/index.vue
 * @Description: 面板图表
-->
<template>
    <view-box :isError="isError" v-bind="failAttrs">
        <div v-loading="loading" v-bind="loadingAttrs" class="p-echart" ref="chart"></div>
    </view-box>
</template>

<script>
let echarts = require('echarts')
import 'echarts-wordcloud/dist/echarts-wordcloud.min'
import 'echarts-gl'
import ReportCommonAttrs from '../mixins/ReportCommonAttrs.vue'
import { Merge_Obj } from '@/utils/object'
import { Is_Object } from '@/utils/data-type'
export default {
    inheritAttrs: false,
    mixins: [ReportCommonAttrs],
    data: () => ({
        myChart: null,
        // 默认配置
        defaultSetting: {},
        // 实际使用到的图表配置
        chartOptions: {},
        // 当前自适应的时间戳（菜单更改时，图表需要resize，此参数用于防抖）
        resizeTime: 0,
    }),
    computed: {
        // 所有的配置
        allSetting () {
            return Object.assign({}, this.defaultSetting, this.customSetting)
        },
        textColor () {
            return this?.themeInfo?.echart?.textColor || this._attrs.textColor
        },
        // 默认图表属性
        defaultChartAttrs () {
            let fontSize = parseFloat(this.getRootFontSize())
            let textStyle = {
                color: this.textColor,
                fontSize
            }
            return {
                ...this._attrs,
                animation: true,
                backgroundColor: this._attrs.contentBg,
                // tooltip: {
                //     textStyle,
                //     backgroundColor: "#fff",
                // },
                textStyle,
                legend: {
                    textStyle: {
                        color: this.textColor,
                    },
                    itemHeight: fontSize,
                    itemWidth: fontSize * 1.6,
                },
                grid: {
                    left: '3%',
                    right: '1%',
                    bottom: '3%',
                    containLabel: true,
                },
            }
        }
    },
    methods: {
        // 获取图表初始化属性
        getInitAttrs () {
            let { use3d } = Object.assign({}, this.defaultSetting, this.customSetting)
            return use3d ? [] : [null, { renderer: 'svg' }]
        },
        async init () {
            // 获取 customSetting 中的自定义渲染图表
            let { getOptions = null } = this.customSetting || {}
            let chartConfig = {
                getOptions,
            }
            // 如果自定义不存在
            if (!getOptions || typeof getOptions !== 'function') {
                // 获取默认图表
                try {
                    let tempObj = await require(`@/components/frame/Panel/BlockContent/items/PChart/opts/${this.type}.js`)
                    this.defaultSetting = tempObj.default || {}
                    chartConfig.getOptions = tempObj.default?.getOptions
                } catch (e) {
                    console.error(`
                            【检查是否存在此默认图表js，文件：@/components/frame/Panel/BlockContent/items/PChart/opts/${this.type}.js】
                            1. 文件路径找不到 / 路径错误
                            2. 文件内容编写有误
                            3. 查看下面的详细错误信息
                        `)
                    console.error(e)
                }
            }
            if (typeof chartConfig.getOptions === 'function') {
                let data = await this.loadData()
                this.myChart = echarts.init(this.$refs.chart, ...this.getInitAttrs())
                let tempOptions = await chartConfig.getOptions.call(this, data)
                if (!tempOptions) {
                    this.isError = true
                    return
                }
                this.chartOptions = Merge_Obj(this.defaultChartAttrs, { type: this.type, ...tempOptions })
                // 合并自定义配置的属性
                if (Is_Object(this.customSetting.customOptions)) {
                    this.chartOptions = Merge_Obj(this.chartOptions, this.customSetting.customOptions)
                }
                // 执行自定义渲染前方法
                let diy_beforeRender = this.customSetting.beforeRender
                if (typeof diy_beforeRender === 'function') {
                    diy_beforeRender.call(this)
                }
                // 渲染图表
                this.myChart.setOption(this.chartOptions, true)
                // 执行自定义渲染后方法
                let diy_afterRender = this.customSetting.afterRender
                if (typeof diy_afterRender === 'function') {
                    diy_afterRender.call(this)
                }
            }
        },
        resize () {
            this.myChart && this.myChart.resize()
        },
        // 搜索
        async search (params = {}) {
            let data = await this.loadData(params)
            let tempOptions = await this.allSetting.getOptions.call(this, data)
            this.chartOptions = {
                ...this.chartOptions,
                ...tempOptions
            }
            if (!tempOptions) {
                this.isError = true
                return
            } else if (this.isError) {
                this.isError = false
                this.$nextTick(() => {
                    this.myChart = echarts.init(this.$refs.chart, ...this.getInitAttrs())
                    this.myChart.setOption(this.chartOptions, true)
                })
            } else {
                this.myChart.setOption(this.chartOptions, true)
            }
        },
    },
    mounted () {
        this.init()
        window.addEventListener('resize', () => {
            this.resize()
        })
    },
    // 执行组件销毁前方法，方便取消监听
    beforeDestroy () {
        // 执行自定义渲染后方法
        let diy_beforeDestroy = this.customSetting.beforeDestroy
        if (typeof diy_beforeDestroy === 'function') {
            diy_beforeDestroy.call(this)
        }
    },
    watch: {
        // 监听左侧菜单更新
        '$store.state.app.sidebar.opened': {
            immediate: true,
            deep: false,
            handler (newVal) {
                let temp_time = Date.now()
                setTimeout(() => {
                    if (temp_time - this.resizeTime > 300) {
                        this.$set(this, 'resizeTime', temp_time)
                        this.resize()
                    }
                }, 300)
            }
        },
    }
}
</script>

<style lang="scss" scoped>
.p-echart {
    width: 100%;
    height: 100%;
}
</style>