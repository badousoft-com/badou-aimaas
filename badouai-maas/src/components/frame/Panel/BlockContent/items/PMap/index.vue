<!--
 * @FilePath: @/components/frame/Panel/BlockContent/items/PMap/index.vue
 * @Description: 面板地图
-->
<template>
    <div
        v-loading="loading"
        v-bind="loadingAttrs"
        ref="chart"
        class="p-echart w-per-100">
    </div>
</template>

<script>
let echarts = require('echarts')
import ReportCommonAttrs from '../mixins/ReportCommonAttrs.vue'
import axios from 'axios'
import { Merge_Obj } from '@/utils/object'
import { Is_Object } from '@/utils/data-type'
// 默认地图名称
const DefaultMapTypeName = 'default'
export default {
    inheritAttrs: false,
    mixins: [ReportCommonAttrs],
    data: () => ({
        myChart: null,
        // 默认配置
        defaultSetting: {},
        // 实际使用到的图表配置
        chartOptions: {},
    }),
    computed: {
        defObj () {
            let res = {}
            try {
                res = JSON.parse(this.dataDef || '') || {}
            } catch (error) {
                res = {}
            }
            return res
        },
        // 所有的配置
        allSetting () {
            return Object.assign({}, this.defaultSetting, this.customSetting)
        },
        // 地图类型
        mapType () {
            return this.defObj?.mapChartType?.[0]?.fieldName || this.customSetting.mapType
        },
        // 地图json路径
        jsonUrl () {
            return this.customSetting.jsonUrl || ''
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
                tooltip: {
                    textStyle
                },
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
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
            }
        }
    },
    methods: {
        // 获取图表渲染js
        async getRenderJs (mapName) {
            try {
                let tempObj = await require(`@/components/frame/Panel/BlockContent/items/PMap/opts/${mapName}.js`)
                return tempObj.default || {}
            } catch (error) {
                return
            }
        },
        async init () {
            // 获取 customSetting 中的自定义渲染图表
            let { getOptions = null} = this.customSetting || {}
            let chartConfig = {
                getOptions,
            }
            // 如果自定义不存在
            if (!getOptions || typeof getOptions !== 'function') {
                let jsObj = await this.getRenderJs(this.mapType)
                if (!jsObj) {
                    console.warn(`${this.mapType}无固定的地图js，将使用默认地图配置`)
                    jsObj = await this.getRenderJs(DefaultMapTypeName)
                }
                this.defaultSetting = jsObj
                chartConfig.getOptions = jsObj?.getOptions
            }
            if (typeof chartConfig.getOptions === 'function') {
                // ---------- 处理 地图JSON 数据 ------ start ---------
                let mapJson = await this.requestJson()
                // 渲染地图必须需要地图json数据
                if (!mapJson) return
                let mapName = this.mapType || 'customMap'
                echarts.registerMap(mapName, mapJson)
                // ---------- 处理 地图JSON 数据 ------ end  ---------
                let data = await this.loadData()
                this.myChart = echarts.init(this.$refs.chart, null, { renderer: 'svg' })
                let tempOptions = chartConfig.getOptions.call(this, data)
                // 合并默认chart属性与自定义属性
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
        // 请求JSON数据
        async requestJson () {
            let _path = this.jsonUrl
            // 若自定义路径不存在，则选择取用默认
            if (!_path) {
                // 当前框架默认会存在两种文件夹下的json数据，除china跟world外，其余默认地图JSON数据全部存放在province下
                // 此处记录的是 非province文件夹下的地图类型
                let expTypes = ['china', 'world']
                // 获取json所在的路径
                _path = expTypes.includes(this.mapType) ? `/${this.mapType}.json` : `/province/${this.mapType}.json`
            }
            // 通过axios get方式动态获取json内容，解决json被打入主内容问题
            let _jsonData = await axios.get(`@/../static/mapJson/${_path}`)
            if (!_jsonData) {
                console.error(`
                        【检查文件/public/static/mapJson${_path}】
                        1. 文件路径找不到：请配置MapJson路径，自定义引入，自定义路径：@/../public/panel/mapJson + XXX（你配置的路径）
                        2. 文件名${this.mapType}错误
                        3. 查看下面的详细错误信息
                    `)
                return
            }
            return _jsonData.data
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
            this.myChart.setOption(this.chartOptions, true)
        }
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
}
</script>

<style lang="scss" scoped>
.p-echart {
    width: 100%;
    height: 100%;
}
</style>