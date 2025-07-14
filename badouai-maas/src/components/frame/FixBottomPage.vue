<template>
    <div class="fix-bottom-part" :class="fixBottomClass">
        <div v-if="!noContent" class="fix-bottom-part__main b-r"  :style="mainStyle">
            <slot>这里放置核心展示内容</slot>
        </div>
        <div class="fix-bottom-part__footer" :calss="{'isShawdow': noShawdow}" :style="footerStyle">
            <slot name="footer"></slot>
        </div>
    </div>
</template>
<script>
import globalStyle from '@/styles/theme.scss'
export default {
    name: "fix-bottom-page",
    components: {},
    props: {
        // 底部高度
        height: {
            type: String,
            default: globalStyle.footerHeight
        },
        noContent: {
            type: Boolean,
            default: false,
        },
        noShawdow: {
            type: Boolean,
            default: true,
        },
        fixBottomClass: {
             type: String,
            default: '',
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
        // 获取主内容样式对象
        mainStyle () {
            return {
                height: `calc(100% - ${this.height})`,
            }
        },
        // 获取底部内容样式对象
        footerStyle () {
            return {
                height: this.height
            }
        }
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
.fix-bottom-part {
    height: 100%;
    .fix-bottom-part__main {
        overflow: auto;
    }
    .fix-bottom-part__footer {
        background: $contentInBg;
        width: 100%;
        border-top: none;
        position: absolute;
        bottom: 0;
        left: 0;
        z-index: 1;
    }
    .isShawdow {
        box-shadow: 0px 9px 15px 3px rgba(102, 102, 102, .5);
    }
}
</style>