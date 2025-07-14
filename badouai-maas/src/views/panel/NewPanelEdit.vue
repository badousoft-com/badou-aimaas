<template>
    <div class="panel-edit-container">
        <panel-drag :blocks="blocks"></panel-drag>
    </div>
</template>

<script>
import PanelDrag from '@/components/frame/Panel/NewPanel/Drag'
export default {
    components: {
        PanelDrag,
    },
    data () {
        return {
            blocks: [
                { w: 300, h: 300, x: 0, y: 100 },
                { w: 200, h: 500, x: 350, y: 0 },
                { w: 300, h: 100, x: 700, y: 300 },
            ]
        }
    }
}
</script>

<style lang="scss" scoped>
.panel-edit-container {
    width: calc(100% - 20px);
}
</style>