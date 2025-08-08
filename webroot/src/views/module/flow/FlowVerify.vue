// 流程-初始发起审核页面:需求如下
// boStatus没有或者为0时，可以进行编辑，发起审核
// boStatus为其他状态时，全部显示为查看状态

<template>
    <div class="bd-page-edit">
        <fix-bottom-page>
            <!-- 核心内容区域 start-->
            <module-edit-code
                v-if="isShowForm"
                :class="{
                    'h-per-100': _fullHeight
                }"
                ref="moduleEditCode"
                :mdCode="mdCode"
                :detailId="detailId"
                :defaultParamsObj="query"
                :defaultBtnList="[]"
                :customSetting="{ buttons: [] }"
                @beforeModuleRender="beforeModuleRender"
                :isView="isView"
                viewType="table">
                <template v-if="hideFlowTrace" v-slot:titleRight>
                    <flow-trace :boId="detailId" :flowId="flowId" :onlyNode="true"></flow-trace>
                </template>
            </module-edit-code>
            <flow-opinion
                v-if="isShowFlowOpinion"
                v-model="flowOpinion"
                ref="flowOpinion"
                class="mar-t">
            </flow-opinion>
            <flow-trace v-if="isShowFlowTrace && !hideFlowTrace" :boId="detailId" :flowId="flowId" class="mar-t"></flow-trace>
            <!-- 核心内容区域 end-->
            <template v-slot:footer v-if="flowId">
                <flow-router-btn
                    :flowId="flowId"
                    :saveForm="saveForm"
                    :pageScope="scope"
                    :showRouterBtn="showRouterBtn"
                    :hasSaveBtn="!isView && isDraft"
                    :btnList="flowButtons"
                    :isView="false"
                    ref="flowRouteBtnRef">
                </flow-router-btn>
            </template>
        </fix-bottom-page>
    </div>
</template>
<script>
import ModuleEditCode from '@/components/frame/Module/ModuleEditCode'
import { Save } from '@/components/frame/Module/BtnBaseFun/flow-edit'
import FixBottomPage from '@/components/frame/FixBottomPage'
import FlowRouterBtn from '@/views/module/flow/part/FlowRouterBtn'
import FlowTrace from '@/views/module/flow/part/FlowTrace'
import FlowOpinion from '@/views/module/flow/part/FlowOpinion'
import { S_Storage } from '@/utils/storage'
import GlobalConst from '@/service/global-const'

export default {
    name: 'standerEdit',
    components: {
        [ModuleEditCode.name]: ModuleEditCode,
        [FixBottomPage.name]: FixBottomPage,
        [FlowRouterBtn.name]: FlowRouterBtn,
        [FlowTrace.name]: FlowTrace,
        FlowOpinion
    },
    data () { // 定义页面变量
        return {
            // 默认按钮数组
            defaultBtnList: [],
            // 流程id
            flowId: null,
            // 保存表单函数
            saveForm: Save,
            // 页面作用域
            scope: this,
            // 设置表单是否存在，用于重新加载使用
            isShowForm: true,
            // 是否展示流程跟踪
            isShowFlowTrace: false,
            // 流程意见
            flowOpinion: '',
            // 自定义流程按钮
            flowButtons: [],
            // 是否为关联模型的新增场景，此时只存在主表还未渲染关联模块
            isLinkTabAdd: false,
            // 定义流程按钮组件域
            flowRouteBtnRef: null,
        }
    },
    computed: {
        // params参数
        params () {
            return this.$route?.params
        },
        // query参数
        query () {
            return this.$route?.query
        },
        // 模型编码
        mdCode () {
            return this.params?.mdCode
        },
        // 是否为查看状态
        isView () {
            let _isView = this.params?.isView
            if (_isView) return _isView !== '0'
            return false
        },
        // 获取是否加载动态路由按钮状态
        showRouterBtn () {
            return this.isDraft && !this.isLinkTabAdd
        },
        // 是否处于拟稿状态
        // 0-拟稿；1-发起并处理中；2-归档
        isDraft () {
            return this.params?.boStatus === '0'
        },
        // 隐藏流程跟踪的状态
        hideFlowTrace () {
            return this.params?.boStatus === '0'
        },
        // 详情id
        detailId () {
            let id = this.params?.id
            return (id !== 'add' ? id : '')
        },
        // 是否显示流程意见
        isShowFlowOpinion () {
            return GlobalConst.openOpinionInFlowVerify &&  // 编辑流程表单时是否开启意见填写
                   this.flowId &&
                   !this.isView &&
                   this.isDraft
        },
        _fullHeight () {
            // 必须满足以下情况，表单模块才铺满
            // 1. 流程意见不展示
            if (this.isShowFlowOpinion) return
            // 2. 流程跟踪不展示
            if (this.isShowFlowTrace && !this.hideFlowTrace) return
            // 模型表单以块级别，只有1块
            return !this.$refs?.moduleEditCode?.getModuleEditRef()?.hasMuchPart
        }
    },
    methods: { // 定义函数
        // 获取ModuleEditCode/index.vue组件作用域
        getModuleFormRef () {
            return this.$refs.moduleEditCode
        },
        // 获取mForm/index.vue页面作用域
        getFormRef () {
            return this.$refs.moduleEditCode?.getFormRef()
        },
        /**
         * 获取模型数据module之后的回调函数
         * 更新flowId值，用于后续请求动态路由按钮数据
         */
        beforeModuleRender (module) {
            // 由于模型表单组件内可能会嵌套表单，其他表单的beforeModuleRender回调也会抛出，导致这里的逻辑异常
            // 因为这里判断只有mdCode一致的才进行处理，避免受到其他模块的干扰
            if (this.mdCode !== module?.mdCode) return
            this.isLinkTabAdd = module.childTab?.length > 0 && !this.detailId
            // 设置自定义流程按钮（统一使用buttons，flowButton待删除）
            // 所有按钮统一使用buttons，flowButtons待删
            this.flowButtons = module?.customSetting?.buttons || module?.customSetting?.flowButtons
            // 更新流程id
            this.flowId = module.flowId
            // 更新流程跟踪组件-显示状态
            this.isShowFlowTrace = true
            this.$nextTick(() => {
                // 获取流程按钮组件作用域
                this.flowRouteBtnRef = this.$refs?.flowRouteBtnRef
            })
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        // 当前组件的逻辑入口是module请求数据结束后会回调当前的beforeModuleRender方法，需要从module中拿出flowId才能下一步
        if (GlobalConst.openOpinionInFlowVerify && this.detailId) {
            // 从存储中获取流程意见数据对象
            let flowOpinionData = S_Storage.getObj('flowOpinion')
            if (!flowOpinionData) return
            // 给流程意见赋值【预防新增保存时的重定向路由，把流程意见给刷掉】
            this.$set(this, 'flowOpinion', flowOpinionData[`flow_${this.detailId}`] || '')
        }
    }
}
</script>
<style lang='scss' scoped>

</style>