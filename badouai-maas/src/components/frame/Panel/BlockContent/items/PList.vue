<!--
 * @FilePath: @/components/frame/Panel/BlockContent/items/PList.vue
 * @Description: 面板列表
-->
<template>
    <div
        :style="tableStyle"
        :class="{'use-custom-theme': userTheme, 'p-module_list': mdCode}"
        class="p-list h-per-100">
        <module-list-code
            v-if="mdCode"
            ref="moduleListCode"
            :mdCode="mdCode"
            :defaultParamsObj="{}"
            fullHeight>
        </module-list-code>
        <view-box
            v-else
            v-loading="loading"
            v-bind="boxAttrs">
            <m-list
                v-if="isReady"
                ref="pListRef"
                v-bind="tableAttrs"
                :paramsBeforeRequest="paramsBeforeRequest"
                stripe
                :afterListRender="afterListRender"
                @afterRequest="afterRequest"
                v-on="listEvent"
                :class="{'hidden-table': loading}"
                class="custom-list_box">
            </m-list>
        </view-box>
    </div>
</template>

<script>
import ReportCommonAttrs from './mixins/ReportCommonAttrs.vue'
import MList from '@/components/frame/Common/MList/index.vue'
import ModuleListCode from '@/components/frame/Module/ModuleListCode'
import { Has_Value } from '@/utils'
export default {
    mixins: [ReportCommonAttrs],
    inheritAttrs: false,
    components: {
        MList,
        ModuleListCode
    },
    props: {
        usePagination: {
            type: Boolean,
            default: false
        }
    },
    data: () => ({
        // 列表数据
        listData: {},
        panelRequestConfig: null,
        isReady: false,
    }),
    computed: {
        mdCode () {
            return this.customSetting?.mdCode
        },
        // 表格样式
        tableStyle () {
            let { headerBg, headerColor, border, stripeBg, cellColor, hoverBg } = this.themeInfo?.table || {}
            return {
                // 表头背景色
                '--table-header-bg': headerBg,
                // 边框颜色
                '--table-header-color': headerColor,
                // 边框颜色
                '--border-color': border,
                // 斑马线背景色
                '--stripe-bg': stripeBg,
                // 单元格颜色
                '--cell-color': cellColor,
                // 鼠标悬浮时的背景颜色
                '--hover-bg': hoverBg
            }
        },
        // 使用使用自定义配置主题色
        userTheme () {
            return Object.values(this.tableStyle).filter(o => Boolean(o)).length > 0
        },
        // 是否分页
        _usePagination () {
            let diy_usePagination = this.customSetting?.usePagination
            return Boolean(Has_Value(diy_usePagination) ? diy_usePagination : this.usePagination)
        },
        // 传入表格的属性
        tableAttrs () {
            // mList 组件传入属性
            let customTableAttrs = this.customSetting || {}
            let rootSize = parseFloat(this.getRootFontSize())
            return {
                // 接口请求地址
                url: this.getRequestConfig().url,
                // 总数路径
                totalPath: 'bean,total',
                // 列表数据路径
                optionResPath: 'bean,datas',
                // 序号列宽度
                indexWidth: rootSize * 2 + 25 + 'px',
                // 是否分页
                usePagination: this._usePagination,
                // 高度自动适应
                fullHeight: true,
                ...customTableAttrs,
                // 列表数据信息（列、表格数据）
                ...this.listData,
            }
        },
        // 自定义配置列信息，相关配置同模型
        renderField () {
            return this.customSetting?.renderField || {}
        },
        // 展示容器的属性
        boxAttrs () {
            return Object.assign({}, this.loadingAttrs, this.failAttrs)
        },
        // 列表方法
        listEvent () {
            return this.customSetting?.listEvent || {}
        }
    },
    methods: {
        search (params = {}) {
            // 是否不展示loading
            if (params?.['__hideLoading']) {
                this.showLoading = !params['__hideLoading']
                delete params['__hideLoading']
            }
            // 配置请求信息
            this.panelRequestConfig = this.getRequestConfig()
            // 搜索
            this.$refs.pListRef && this.$refs.pListRef.reset()
            this.loading = this.showLoading
        },
        // 列表接口请求完成后
        afterRequest (res) {
            let result = res?.bean || {}
            // 是否存在请求完成后的自定义事件
            if (typeof this.customSetting.afterRequest === 'function') {
                result = this.customSetting.afterRequest.call(this, res)
            }
            // 将接口返回数据存起来
            this.dataInfo = result
            // 设置表格信息
            this.setTableAttrs(result)
            // 渲染前方法
            let diy_beforeRender = this.customSetting.beforeRender
            if (typeof diy_beforeRender === 'function') {
                diy_beforeRender.call(this)
            }
        },
        afterListRender () {
            this.loading = false
        },
        // 设置列表的请求方式
        paramsBeforeRequest (listParams) {
            return Object.assign({}, listParams, this.panelRequestConfig.params)
        },
        setTableAttrs (data) {
            let fieldList = (data.columns || []).map(f => {
                let customFieldRender = this.renderField[f.field] || {}
                return {
                    ...f,
                    ...customFieldRender,
                    name: f.field,
                    label: f.title
                }
            })
            this.$set(this.listData, 'fieldList', fieldList)
        },
    },
    mounted () {
        // 获取面板的默认请求参数
        this.panelRequestConfig = this.getRequestConfig()
        this.isReady = true
    }
}
</script>

<style lang="scss" scoped>
.p-list::v-deep {
    &.p-module_list {
        overflow: hidden;
    }
    .custom-list_box {
        &.hidden-table {
            visibility: hidden;
        }
        .el-table {
            height: 100%;
            background-color: var(--content-bg);
            display: flex;
            flex-direction: column;
            .el-table__header-wrapper {
                flex: none;
            }
            .el-table__body-wrapper {
                flex: 1;
            }
        }
    }
    &.use-custom-theme {
        .el-table {
            background-color: var(--content-bg);
            border-color: var(--border-color);
            .cell {
                font-size: 1rem;
                color: var(--cell-color);
                line-height: 1rem;
            }
            th, td {
                padding: 0;
                height: 3rem;
            }
            // 表头
            th.is-leaf {
                background-color: var(--table-header-bg);
                .cell {
                    font-size: 1rem;
                    color: var(--table-header-color);
                }
            }
            .el-table__row {
                background-color: var(--content-bg);
                &:nth-child(2n) {
                    background-color: var(--stripe-bg);
                }
                &:hover {
                    td {
                        background-color: var(--hover-bg) !important;
                    }
                }
                &:last-child:hover td {
                    border-color: var(--content-bg);
                }
            }
            th.is-leaf, tr, tbody tr.el-table__row td {
                border-color: var(--border-color);
            }
            th.is-leaf, tr {
                border-right-color: rgba($color: #000000, $alpha: 0);
                &:hover {
                    border-right-color: var(--border-color);
                }
            }
            &.show-border {
                th.is-leaf, tr {
                    border-right-color: var(--border-color);
                }
            }
        }
    }
}
</style>
<style lang="scss">
.el-popover.title-popover {
    &, & * {
        font-size: calc(1rem - 2px);
    }
}
</style>