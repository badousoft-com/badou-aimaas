<template>
    <div class="fd-form-panel">
        <div class="fd-form-panel__content">
            <slot></slot>
        </div>
    </div>
</template>
<script>
// import xx from ./xx
export default {
    name: 'fd-form-panel',
    components: {},
    data () { // 定义页面变量
        return {
        }
    },
    computed: {},
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
@import '../../theme/index.scss';
.fd-form-panel {
    padding: $padding;
    .fd-form-panel__content {
        // background: $fd-drag-tipPanel;
        border: 1px dashed #666;
        height: 100%;
        overflow: auto;
    }
}
</style>