<template>
    <div class="fd-header">
        <div class="fd-header-title">
            <slot name="prev_title"></slot>
            {{title}}
            <slot name="append_title"></slot>
        </div>
        <!-- 中间内容区 -->
        <div class="fd-header-content">
            <slot name="content"></slot>
        </div>
        <span class="fd-header-right">
            <slot name="right"></slot>
        </span>
    </div>
</template>
<script>
// import xx from ./xx
export default {
    name: 'fd-header',
    components: {},
    props: {
        title: {
            type: String,
            default: '表单设计器'
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
    },
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
@import '../theme/index.scss';
.fd-header::v-deep {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $padding;
    background: $fd-primary;
    color: #fff;
    overflow: hidden;
    // 去除主题下拉时的相关样式
    .el-select .el-input.is-focus .el-input__inner,
    .el-select .el-input__inner:focus {
        border: rgba(0, 0, 0, 0);
    }
    .fd-header-content {
        flex: 1;
    }
    .fd-header-right {
        display: flex;
        align-items: center;
    }
}
</style>