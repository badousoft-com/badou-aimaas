<template>
    <div
        v-if="title && allowShowTitle"
        class="m-title"
        :class="{'scroll-shadow': showShadow }">
        <div class="m-title__word">
            <bd-icon :name="icon" class="pillar fill"></bd-icon>
            <span>{{title}}</span>
        </div>
        <div class="m-title-right">
            <slot name="right"></slot>
        </div>
    </div>
</template>
<script>
export default {
    name: "m-title",
    components: {},
    props: {
        icon: {
            type: String,
            default: 'pillar-fill'
        },
        // 标题
        title: {
            type: [String, Boolean],
            default: false
        },
        // 显示阴影层，常用于滚动场景时，与可滚动元素的划分展示
        showShadow: {
            type: Boolean,
            default: false
        }
    },
    data () { // 定义页面变量
        return {
            // 是否允许标题显示
            allowShowTitle: false,
        }
    },
    computed: {},
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        // 设置下标题延时展示：避免动画卡顿
        setTimeout(() => {
            this.allowShowTitle = true
        }, 100)
        
    }
}
</script>
<style lang='scss' scoped>
.m-title {
    display: flex;
    align-items: center;
    height: $formTitleHeight;
    line-height: $formTitleHeight;
    padding: 0px $padding;
    .m-title__word {
        flex: 1 0 auto;
        font-size: $fontL;
        font-weight: 600;
        color: $fontCL;
    }
    .m-title-right {
        display: flex;
    }
}
</style>