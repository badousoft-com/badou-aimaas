<!--
 * @FilePath: @/components/frame/Panel/PanelBlock/BlockTitle.vue
 * @Description: 块标题
-->
<template>
    <div v-if="showTitle" class="block-title">
        <render-fun v-if="contentRender" :render="renderFn('contentRender')"></render-fun>
        <div v-else class="title_view">
            <span
                :style="iconAttrs.style || {}"
                class="title_icon marR-5">
                <bd-icon v-if="iconAttrs.name" v-bind="iconAttrs"></bd-icon>
            </span>
            <span>{{name}}</span>
        </div>
        <div
            @click="handlerRightClick"
            :class="{'c-p': rightClick}"
            class="block-title_slot">
            <div v-if="rightFormat" v-html="rightHtml"></div>
            <render-fun v-else-if="rightRender" :render="renderFn('rightRender')"></render-fun>
            <slot v-else></slot>
        </div>
    </div>
</template>

<script>
import RenderFun from '@/components/frame/RenderFun'
export default {
    inheritAttrs: false,
    components: {
        [RenderFun.name]: RenderFun,
    },
    props: {
        // 是否展示标题
        showTitle: {
            type: Boolean,
            default: true
        },
        // 图标属性
        iconAttrs: {
            type: Object,
            default: () => {}
        },
        // 标题名称
        name: {
            type: String,
            default: ''
        },
        // 块作用域
        blockScope: null,
        // 面板作用域
        panelScope: null,
        // 右侧自定义配置文本
        rightFormat: {
            type: [Function, String]
        },
        // 点击右侧执行的回调
        rightClick: {
            type: Function
        },
        // 右侧自定义渲染
        rightRender: {
            type: Function
        },
        // 内容自定义渲染
        contentRender: {
            type: Function
        },
    },
    computed: {
        rightHtml () {
            switch (typeof this.rightFormat) {
                case 'function':
                    return this.rightFormat.call(this)
                case 'string':
                    return this.rightFormat
                default:
                    return ''
            }
        }
    },
    methods: {
        // 右侧点击事件的回调
        handlerRightClick () {
            if (typeof this.rightClick === 'function') {
                this.rightClick.call(this.blockScope)
            }
        },
        // 右侧自定义渲染事件（转化）
        renderFn (keyName) {
            if (typeof this[keyName] !== 'function') return
            return (h, context) => {
                return this[keyName].call(this.blockScope, h, context)
            }
        },
    }
}
</script>

<style lang="scss" scoped>
.block-title {
    display: flex;
    align-items: center;
    color: var(--text-color);
    background-color: var(--content-bg);
    border-bottom: 2px solid var(--primary-color);
    padding: 10px 15px;
    background-repeat: no-repeat;
    background-size: 100% 100%;
    top: -30px;
    .title_view {
        flex: 1;
        .title_icon {
            color: var(--primary-color);
        }
    }
    .block-title_slot {
        flex: none;
    }
    .c-p {
        cursor: pointer;
    }
}
</style>