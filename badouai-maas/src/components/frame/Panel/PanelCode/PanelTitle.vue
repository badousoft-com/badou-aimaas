<!--
 * @FilePath: @/components/frame/Panel/PanelCode/PanelTitle.vue
 * @Description: 面板标题
-->
<template>
    <div class="panel-title">
        <div class="title_view">
            {{name}}
        </div>
        <div class="panel-title-right">
            <!-- 右侧自定义内容、插槽等 -->
            <div
                @click="handlerClick('right')"
                :class="{'pointer': rightClick}"
                class="panel-title_right">
                <div v-if="rightFormat" v-html="rightHtml"></div>
                <render-fun v-else-if="rightRender" :render="renderFn('right')"></render-fun>
                <slot v-else name="right"></slot>
            </div>
            <!-- 全屏按钮 -->
            <div
                v-if="canFullScreen"
                @click="fullScreen"
                class="full-screen_btn">
                <bd-icon :name="isFull ? 'outFullScreen' : 'fullScreen'"></bd-icon>
            </div>
        </div>
        <!-- 左侧自定义内容、插槽等 -->
        <div
            @click="handlerClick()"
            :class="{'pointer': leftClick}"
            class="panel-title_left">
            <div v-if="leftFormat" v-html="leftHtml"></div>
            <render-fun v-else-if="leftRender" :render="renderFn()"></render-fun>
            <slot v-else name="left"></slot>
        </div>
    </div>
</template>

<script>
import screenfull from 'screenfull'
import RenderFun from '@/components/frame/RenderFun'
export default {
    inheritAttrs: false,
    components: {
        [RenderFun.name]: RenderFun,
    },
    props: {
        // 标题名称
        name: {
            type: String,
            default: ''
        },
        // 是否支持全屏
        canFullScreen: {
            type: Boolean,
            default: true
        },
        panelScope: {
            type: Object
        },
        // 左侧自定义配置文本
        leftFormat: {
            type: [Function, String]
        },
        // 点击左侧执行的回调
        leftClick: {
            type: Function
        },
        // 左侧自定义渲染
        leftRender: {
            type: Function
        },
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
        }
    },
    data: () => ({
        // 是否全屏
        isFull: false
    }),
    computed: {
        // 左侧html文本
        leftHtml () {
            switch (typeof this.leftFormat) {
                case 'function':
                    return this.leftFormat.call(this)
                case 'string':
                    return this.leftFormat
                default:
                    return ''
            }
        },
        // 右侧html文本
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
    },
    methods: {
        // 全屏（复制当前标签，打开新页面）
        fullScreen () {
            if (screenfull.isEnabled) {
                screenfull.toggle()
            }
        },
        screenFullChange () {
            this.isFull = screenfull.isFullscreen
            // 获取app容器的dom
            let appEl = document.getElementById('app')
            // 获取app元素下的子元素
            let boxEl = appEl.firstChild
            let classList = [...boxEl.classList]
            let c_index = classList.indexOf('panel-screen-full')
            if (this.isFull) {
                c_index === -1 && classList.push('panel-screen-full')
            } else {
                c_index !== -1 && classList.splice(c_index, 1)
            }
            boxEl.className = classList.join(' ')
            setTimeout(() => {
                this.panelScope.resize && this.panelScope.resize()
            }, 500)
        },
        // 点击事件的回调
        handlerClick (type) {
            let fn = type === 'right' ? this.rightClick : this.leftClick
            if (typeof fn === 'function') {
                fn.call(this.panelScope)
            }
        },
        // 自定义渲染事件（转化）
        renderFn (type) {
            let render = type === 'right' ? this.rightRender : this.leftRender
            if (typeof render !== 'function') return
            return (h, context) => {
                return render.call(this.panelScope, h, context)
            }
        },
    },
    mounted () {
        if (screenfull.isEnabled) {
            screenfull.on('change', this.screenFullChange)
        }
    },
    destroyed () {
        if (screenfull.isEnabled) {
            screenfull.off('change', this.screenFullChange)
        }
    }
}
</script>

<style lang="scss" scoped>
.panel-title {
    text-align: center;
    padding: 10px;
    background-position: center;
    background-repeat: no-repeat;
    position: relative;
    .panel-title-right {
        display: flex;
        align-items: center;
        position: absolute;
        right: 20px;
        top: 0px;
        // 全屏图标
        .full-screen_btn {
            color: #fff;
            font-size: 20px;
            opacity: 0.2;
            cursor: pointer;
            &:hover {
                opacity: 1;
            }
            transition: opacity 0.3 ease-in-out;
        }
    }
    .panel-title_left {
        position: absolute;
        left: 0px;
        top: 0px;
    }
}
</style>
<style lang="scss">
// 大屏全屏时的样式（与app-wrapper同一元素）
// ps：指定元素大屏会导致脱离文档流的dom被压缩到底层
.panel-screen-full {
    .app-header, .navbar, .sidebar-container {
        opacity: 0 !important;
        height: 0 !important;
        overflow: hidden !important;
    }
    .sidebar-container {
        opacity: 0 !important;
        width: 0 !important;
        overflow: hidden !important;
    }
    .main-container, .app-main, .children-router {
        margin: 0 !important;
        height: 100% !important;
        top: 0 !important;
        .children-router {
            padding: 0 !important;
        }
    }
}
</style>