<!--
 * @FilePath: @/components/frame/Panel/ViewBox.vue
 * @Description: 展示容器（可传入参数，当Error时展示错误图片）
-->
<template>
    <div class="view-box">
        <slot v-if="!isError"></slot>
        <no-data v-else :text="$attrs.errorText || errorText" v-bind="$attrs"></no-data>
    </div>
</template>

<script>
import NoData from '@/components/frame/NoData.vue'
export default {
    components: {
        NoData
    },
    props: {
        isError: {
            type: Boolean,
            default: false
        },
        errorText: {
            type: String,
            default: '暂无数据'
        },
        // 失败时的图片地址
        // failImag: {
        //     type: String
        // }
    }
}
</script>

<style lang="scss" scoped>
.view-box {
    width: 100%;
    height: 100%;
}
</style>