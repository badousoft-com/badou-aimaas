<template>
    <div class="stander-report pad-10">
        <div class="report-top">
            <m-search
                class="bd-report__search"
                :data="searchFieldList"
                :btnList="searchBtn"
                @search="search"
                @reset="searchReset">
                <div class="d-ib report-btns-contain marB-10">
                    <template v-for='(i, index) in btnList'>
                        <el-button
                            :key="index"
                            v-if="!i.isHide"
                            v-btnBg="i"
                            type="primary"
                            :loading="i.loading"
                            @click='exeMethod(i)'>
                            <bd-icon v-show="!i.loading" :name="i.icon"></bd-icon> {{ i.name }}
                        </el-button>
                    </template>
                </div>
            </m-search>
        </div>
        <slot name="mid"></slot>
        <div class="report-contain padB-10">
            <div class="unit textR marR-10 marB-5">{{ getUnit }}</div>
            <report-view
                ref="reportRef"
                :reportCode="routerData.reportCode || ''"
                :dynamicParams="dynamicParams || ''">
            </report-view>
        </div>
    </div>
</template>
<script>
import MSearch from '@/components/frame/Common/MSearch/index'
import Report from '@/components/frame/Report/index'
import ModuleUtils from '@/js/ModuleUtils'
import { Has_Value } from '@/utils/index'
import { GetSearchFieldList, Get_Search_Type } from '@/service/module'
import { ConversionParams } from '@/components/frame/Report/transData'
export default {
    components: {
        MSearch,
        [Report.name]: Report
    },
    props: {
        // 单位
        unit: {
            type: String,
            default: ''
        },
        // 父组件传递进来的参数
        parentParams: {
            type: Object,
            default: () => {}
        },
        // 搜索模型mdCode
        moduleCode: {
            type: String,
            default: ''
        }
    },
    computed: {
        // 用于处理搜索栏提供的搜索参数【转化参数数组数据】
        searchParams () {
            let params = this.searchFieldList.filter(i => Has_Value(i.value)).map(i => {
                    return {
                        name: i.id,
                        // 值为字符串时trim()去除前后空格
                        value: typeof i.type === 'string' ? i.value.trim() : i.value,
                        type: Get_Search_Type(i.type)
                    }
                })
            return ConversionParams(params)
        },
        // 右上角的单位
        getUnit () {
            if (this.description) {
                return this.description
            }
            if (this.$route.query && this.$route.query.unit) {
                switch (this.$route.query.unit) {
                    case '1':
                        return '单位：万元'
                    case '2':
                        return '单位：元'
                    default:
                        return '单位：万元'
                }
            }
            return this.unit
        }
    },
    data () {
        return {
            // 搜索框模型mdCode
            filterCode: '',
            // 搜索配置数据
            searchFieldList: [],
            // 模型对象数据
            module: {},
            // 表格的默认按钮
            searchBtn: [
                { id: 'search', hidden: true },
                { id: 'reset', hidden: true }
            ],
            // 表格按钮
            btnList: [
                { id: 'export', name: '导出', icon: 'export', method: 'exportFile', params: '', type: '' },
                { id: 'reCalc', name: '重新计算', icon: 'move', method: 'reCalc', params: '', type: 'warning' }
            ],
            routerData: {},
            dynamicParams: '',
            btnLoading: false,
            // js文件设置的表格右上方的文字（优先级最高）
            description: '',
            // 导出文件名称（由report-view统一请求赋值）
            fileName: '',
        }
    },
    methods: {
        exeMethod (i) {
            if (typeof i.click === 'function') {
                i.click.call(this, i)
                return
            }
            i.method && this[i.method] && this[i.method](i)
        },
        search () {
            let obj = this.searchParams
            this.$nextTick(() => {
                // 父组件的参数直接覆盖
                // obj = Object.assign(obj, this.parentParams)
                // this.$set(this.routerData, 'dynamicParams', JSON.stringify(obj))
                this.$set(this, 'dynamicParams', JSON.stringify(obj))
                this.$emit('reportSearch', JSON.stringify(obj))
            })
        },
        searchReset () {

        },
        // 导出文件
        exportFile (btn) {
            let url = `${this.BASEURL}/report/reportinfo/reportinfoedit/exportReportByCode.do?reportCode=${this.routerData.reportCode}&dynamicParams=${this.routerData.dynamicParams}&fileName=${this.fileName}`
            window.location.href = encodeURI(url)
            // window.open(url)
        },
        // 重新计算
        reCalc (btn) {
            if (this.$refs.reportRef) {
                btn.loading = true
                this.$refs.reportRef.initReportDetail(true)
            }
        },
        async initModuleData () {
            this.module = await ModuleUtils.listModuleCfg(this.filterCode)
            // 获取完整搜索配置数据
            let temp_searchFieldList = await GetSearchFieldList(this.module.searchCondition, this.module.selectorData)
            // 将路由里面的值赋值到搜索组件中
            temp_searchFieldList.forEach(item => {
                let dynamicValue = this.routerData[item.id]
                if (dynamicValue) {
                    item.value = dynamicValue
                }
            })
            this.searchFieldList = temp_searchFieldList
            this.dynamicParams = JSON.stringify(this.searchParams)
        }
    },
    created () {
        this.routerData = this.$route.params
        if (!this.routerData.reportCode) {
            console.error('报表code必须在路由里传值')
        }
        this.routerData = { ...this.routerData, ...this.$route.query }
        // 给搜索模型参数赋值
        this.filterCode = this.moduleCode || this.$route.query.moduleCode || ''
        this.initModuleData()
    },
    watch: {
        btnLoading (val) {
            if (!val) {
                this.btnList.forEach(item => {
                    this.$set(item, 'loading', false)
                })
            }
        }
    }
}
</script>
<style lang="scss" scoped>
.stander-report {
    background: $white;
    overflow: hidden;
    .report-view {
        color: $fontCL;
        padding: 0 20px;
        padding-top: 10px;
    }
    .report-top {
        padding-bottom: 0;
    }
    .report-contain {
        padding-bottom: 0;
    }
}

</style>