<!--
 * @Description: 流程按钮的另一种展现形式，在弹窗上以tab的形式展示
 * @FilePath: src/views/module/flow/part/FlowRouterTabs/index
-->
<template>
    <div class="flow-router-tabs-containter">
        <el-tabs v-model="activeTab" tab-position="left" class="router-btn-tabs">
            <template v-for="(i, i_index) in _tabList">
                <el-tab-pane
                    :key="i_index"
                    :label="i.name"
                    :name="i.id">
                    <render-page
                        v-if="i.url"
                        v-bind="i"
                        :ref="'ref_' + i.id">
                    </render-page>
                </el-tab-pane>
            </template>
        </el-tabs>
    </div>
</template>

<script>
import ScopeMixin from '@/components/frame/ScopeMixin'
import RenderPage from './RenderPage.vue'
import { Start_Submit, Next_Submit } from '@/api/frame/flow'
export default {
    // 混淆：当前组件会被其他页面动态引入调用，需要该混淆类实现作用域的共享
    mixins: [ScopeMixin],
    components: {
        RenderPage
    },
    props: {
        // 按钮组件的this
        outScope: {
            type: Object,
            default: () => {}
        },
        mainScope: {
            type: Object,
            default: () => {}
        },
        // 需要展示在弹窗上的流程按钮
        flowBtnList: {
            type: Array,
            default: () => []
        },
        worklistId: {
            type: String,
            default: ''
        },
        boId: {
            type: String,
            default: ''
        },
        beforeApproveRender: {
            type: Function,
            default: null
        },
        elseAttr: {
            type: Object,
            default: () => {}
        }
    },
    data () {
        return {
            activeTab: '',
            tempBtnList: []
        }
    },
    computed: {
        _tabList () {
            let result = this.tempBtnList.map(o => {
                let _config = !this.worklistId ? {
                    boId: this.boId, // 业务实例id
                    routeId: o.routeId, // 路由id
                } : {
                    worklistId: this.worklistId, // 待办id
                    routeId: o.routeId, // 路由id
                }
                return {
                    ...this.elseAttr,
                    ...o,
                    url: o.pageUrl || '/module/flow/part/SubmitFlow',
                    config: _config,
                    submit: !o.pageUrl ? this.nextFlow : o.submit,
                    isCreateNode: !this.worklistId, // 是否为发起环节
                }
            })
            return result
        }
    },
    methods: {
        submit (dialogBtn) {
            // 寻找对应的流程tab信息
            let flowItem = this._tabList.find(o => o.id === this.activeTab)
            if (typeof flowItem.submit === 'function') {
                flowItem.submit.call(this, dialogBtn, flowItem)
            } else {
                // 获取挂载页面的作用域
                let pageRef = this.$refs['ref_' + this.activeTab][0].pageScope
                if (typeof pageRef.submit === 'function') {
                    pageRef.submit(dialogBtn, flowItem, this)
                } else {
                    console.error(`应该在所嵌入的页面${flowItem.url}或定义按钮时，加入submit事件`)
                }
            }
        },
        /**
         * @description: 提交下一环节审批（默认按钮点击提交）
         * @param {Object} dialogBtn 弹窗底部点击的按钮对象
         * @param {Object} flowItem 当前tab包含的流程等信息
         */
        nextFlow (dialogBtn, flowItem) {
            let that = this
            flowItem.config.boId = flowItem.config.boId || dialogBtn.boId || ''
            let submitRequest = !this.worklistId ? Start_Submit : Next_Submit
            let successTip = `操作成功，${!this.worklistId ? '已发起审核' : '已提交下一环节审核'}`
            // 获取挂载页面的作用域
            let pageRef = this.$refs['ref_' + this.activeTab][0].pageScope
            // 检验弹窗表单并返回表单值
            pageRef.$refs.submitFlow.validateForm().then((submitRes) => {
                // 设置按钮状态-加载中
                dialogBtn.loading = true
                // 发起流程
                submitRequest({
                    ...submitRes,
                    ...flowItem.config,
                }).then(res => {
                    if (res && res.hasOk) {
                        that.$message.success(successTip)
                        that.$pageDialog.close()
                        pageRef.$router.go(-1)
                    } else {
                        this.$message.error(`操作失败！${res?.message || ''}`)
                    }
                    // 设置按钮状态
                    dialogBtn.loading = false
                })
            })
        }
    },
    mounted () {
        this.tempBtnList = this.flowBtnList
        typeof this.beforeApproveRender === 'function' && this.beforeApproveRender.call(this)
        this.activeTab = this.tempBtnList?.[0]?.id
    }
}
</script>
<style lang="scss" scoped>
.flow-router-tabs-containter::v-deep {
    width: 100%;
    height: 100%;
    .router-btn-tabs {
        height: 100%;
        .el-tabs__content, .el-tab-pane {
            height: 100%;
        }
    }
}
</style>
