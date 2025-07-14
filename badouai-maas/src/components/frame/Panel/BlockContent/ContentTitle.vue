<!--
 * @FilePath: @/components/frame/Panel/BlockContent/ContentTitle.vue
 * @Description: 内容标题
-->
<template>
    <div class="content-title">
        <render-fun v-if="contentRender" :render="renderFn('contentRender')"></render-fun>
        <div v-else class="title_view">
            <bd-icon v-bind="_iconAttrs" class="title-icon marR-5"></bd-icon>
            <span>{{name}}</span>
        </div>
        <div
            @click="handlerRightClick"
            :class="{'pointer': rightClick}"
            class="content-title_slot">
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
        name: {
            type: String,
            default: ''
        },
        // 图标属性
        iconAttrs: {
            type: Object,
            default: () => {}
        },
        // 内容作用域
        contentScope: null,
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
        }
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
        },
        // 标题图标传递属性
        _iconAttrs () {
            let attrs = this.iconAttrs || {}
            if (!attrs.hasOwnProperty('name')) {
                attrs.name = 'countChart'
            }
            return attrs
        }
    },
    methods: {
        // 右侧点击事件的回调
        handlerRightClick () {
            if (typeof this.rightClick === 'function') {
                this.rightClick.call(this.contentScope)
            }
        },
        // 自定义渲染事件（转化）
        renderFn (name) {
            if (typeof this[name] !== 'function') return
            return (h, context) => {
                return this[name].call(this.contentScope, h, context)
            }
        },
    }
}
</script>

<style lang="scss" scoped>
.content-title {
    display: flex;
    align-items: center;
    width: 100%;
    padding: $padding;
    background-color: var(--content-bg);
    border-bottom: 2px solid #eee;
    .title-icon {
        color: var(--primary-color);
    }
    .title_view {
        flex: 1;
    }
    .content-title_slot {
        flex: none;
    }
}
</style>