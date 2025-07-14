<!--
 * @FilePath: @/views/panel/View2.vue
 * @Description: 面板查看页（新）
-->
<template>
    <div class="panel-view">
        <panel-code
            v-if="panelId"
            :panelId="panelId">
        </panel-code>
    </div>
</template>

<script>
import PanelCode from '@/components/frame/Panel/PanelCode/index.vue'
export default {
    components: {
        PanelCode
    },
    data: () => ({
        panelId: ''
    }),
    mounted () {
        if (this.$route.params.panelId) {
            this.panelId = this.$route.params.panelId
        }
    }
}
</script>

<style lang="scss" scoped>
.panel-view {
    height: 100%;
    width: 100%;
}
</style>