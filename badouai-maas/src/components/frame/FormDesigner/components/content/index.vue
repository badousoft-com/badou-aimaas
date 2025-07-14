<template>
    <div class="fd-content">
        <div class="header-left">
            <slot name="header-left"></slot>
        </div>
        <div class="header-right">
            <slot name="header-right"></slot>
        </div>
        <slot></slot>
    </div>
</template>
<script>
// import xx from ./xx
export default {
    name: 'fd-content',
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
.fd-content {
    position: relative;
    .header-sign-posi {
        position: absolute;
        top: 50%;
        transform: translateY(-50%);
    }
    .header-left {
        @extend .header-sign-posi;
        left: 0;
    }
    .header-right {
        @extend .header-sign-posi;
        right: 0;
    }
}
</style>