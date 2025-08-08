// 下一环节：处理人审核页面
<template>
    <div class="bd-page-edit">
        <fix-bottom-page>
            <!-- 核心内容区域 start-->
            <module-edit-code
                ref="moduleEditCode"
                :mdCode="mdCode"
                :detailId="detailId"
                :defaultParamsObj="query"
                :defaultBtnList="[]"
                :customSetting="{ buttons: [] }"
                @beforeModuleRender="beforeModuleRender">
            </module-edit-code>
            <flow-opinion
                v-if="isShowFlowOpinion"
                v-model="flowOpinion"
                ref="flowOpinion"
                class="mar-t">
            </flow-opinion>
            <flow-trace :boId="_flowItem.boId" :flowId="flowId" class="mar-t"></flow-trace>
            <!-- 核心内容区域 end-->
            <template v-slot:footer>
                <flow-router-btn
                    :flowId="flowId"
                    :worklistId="worklistId"
                    :saveForm="saveForm"
                    :workItem="_flowItem"
                    :pageScope="scope"
                    :btnList="flowButtons"
                    :beforeApproveRender="beforeApproveRender"
                    :showRouterBtn="true"
                    :isView="isView"
                    ref="flowRouteBtnRef">
                </flow-router-btn>
            </template>
        </fix-bottom-page>
    </div>
</template>
<script>
import WorkVerify from '@/views/module/flow/part/WorkVerify'
export default {
    mixins: [WorkVerify],
    data () {
        return {
            // 是否查看状态
            isView: false
        }
    },
}
</script>