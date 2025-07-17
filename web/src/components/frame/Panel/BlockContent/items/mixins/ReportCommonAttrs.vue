<!--
 * @FilePath: @/components/frame/Panel/BlockContent/items/mixins/ReportCommonAttrs.vue
 * @Description: 报表共有设置
-->
<script>
import { Pack_Data_Url } from '@/api/frame/panelApi.js'
import { request } from '@/service/request'
import { Get_Search_Type } from '@/service/module'
import ViewBox from '@/components/frame/Panel/ViewBox.vue'
export default {
    components: {
        ViewBox
    },
    props: {
        type: {
            type: String,
            default: ''
        },
        dataDef: {
            type: String,
            default: ''
        },
        customSetting: {
            type: Object,
            default: () => {}
        },
        // 搜索信息
        filterList: {
            type: Array,
            default: () => []
        },
        // 剩余属性变量
        otherAttrs: {
            type: Object,
            default: () => {}
        },
        // 块作用域
        blockScope: null,
        // 面板作用域
        panelScope: null
    },
    data: () => ({
        // 加载中动画
        loading: false,
        // 是否需要展示loading
        showLoading: true,
        // 是否出错
        isError: false,
        // 出错文本
        errorText: '暂无数据',
        // 接口返回的数据
        dataInfo: {},
    }),
    computed: {
        // 最终使用主题
        themeInfo () {
            let themeAttrs = this.otherAttrs.theme || {}
            let customTheme = this?.customSetting || {}
            return {
                ...themeAttrs,
                ...customTheme
            }
        },
        // 内容属性最终的属性
        _attrs () {
            let { color, contentBg, textColor, importTextColor } = this.themeInfo
            return {
                // 色系
                color: color || [],
                contentBg: contentBg,
                textColor: textColor || '#333',
                importTextColor
            }
        },
        primaryColor () {
            return this._attrs?.color?.[0]
        },
        // dataDef 转化出来的对象
        dataDefInfo () {
            let res = {}
            try {
                res = JSON.parse(this.dataDef || '') || {}
            } catch (error) {
                res = {}
            }
            return res
        },
        // loading 指令的属性
        loadingAttrs () {
            return this.themeInfo?.loading || {}
        },
        // 请求失败或暂无数据时展示的组件属性
        failAttrs () {
            return this.customSetting?.failAttrs || {}
        },
    },
    methods: {
        // 获取请求方法配置
        getRequestConfig (temParams = {}) {
            let { dataSourceType, reportId, filterReportId } = this.$attrs
            let dynamicValues = {}, searchParams = []
            this.filterList.filter(o => o.value).forEach(o => {
                if (o.isDynamic) {
                    dynamicValues[o.id] = o.value
                } else {
                    searchParams.push({
                        name: o.id,
                        type: Get_Search_Type(o.type) || 'exact-match',
                        value: typeof o.type === 'string' ? o.value.trim() : o.value,
                        // 当前搜索字段同时支持的字段搜索，格式如'name,sex'，输入框输入值将会作为这些字段中（或的关系）一起去查询
                        tagName: o.tagName || ''
                    })
                }
            })
            let params = {
                dataSourceId: dataSourceType === '0' ? reportId : filterReportId,
                reportShowTypeId: this.type,
                dataDef: this.dataDef || '',
                dataSourceType: dataSourceType === '0' ? 'report' : 'filter',
                params: JSON.stringify(searchParams),
                dynamicValues: JSON.stringify(dynamicValues),
                ...temParams
            }
            let url = this.customSetting?.dataUrl
            if (typeof this.customSetting.dataUrl === 'function') {
                url = this.customSetting.dataUrl.call(this)
            }
            // 是否存在请求前的自定义事件
            if (typeof this.customSetting.beforeRequest === 'function') {
                let reqestInfo = this.customSetting.beforeRequest.call(this, params, url)
                url = reqestInfo.url
                params = reqestInfo.params
            }
            return {
                url: url || Pack_Data_Url,
                method: this.customSetting?.requestMethod || 'post',
                params,
                ...(this.customSetting?.requestConfig || {})
            }
        },
        // 图表数据请求
        loadData (exeParams = {}) {
            if (exeParams?.['__hideLoading']) {
                this.showLoading = !exeParams['__hideLoading']
                delete exeParams['__hideLoading']
            }
            let rConfig = this.getRequestConfig(exeParams)
            this.loading = this.showLoading
            return new Promise((resolve, reject) => {
                request(rConfig).then(res => {
                    this.loading = false
                    let result = res?.bean || {}
                    // 是否存在请求完成后的自定义事件
                    if (typeof this.customSetting.afterRequest === 'function') {
                        result = this.customSetting.afterRequest.call(this, res)
                    }
                    this.dataInfo = result
                    resolve(result)
                }).catch(err => {
                    this.loading = false
                    this.dataInfo = {}
                    resolve({})
                    console.log(err)
                })
            })
        },
        // 获取根节点字体大小
        getRootFontSize () {
            let rootDom = document.getElementsByTagName('html')[0]
            let attrName = 'fontSize'
            return rootDom.currentStyle ? rootDom.currentStyle[attrName] : getComputedStyle(rootDom)[attrName]
        },
    }
}
</script>

<style>

</style>