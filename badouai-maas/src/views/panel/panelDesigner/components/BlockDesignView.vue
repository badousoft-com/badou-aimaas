<!--
 * @Description: 块编辑组件
-->
<template>
    <div :style="{padding: spaceSize}" class="block-design-view">
        <div class="mask_disabled_click">
        </div>
        <bd-panel-block
            ref="block"
            :blockInfo="block"
            v-bind="_attrs"
            class="block-item h-per-100">
        </bd-panel-block>
    </div>
</template>

<script>
import PanelBlock from '@/components/frame/Panel/PanelBlock/index.vue'
import GlobalConst from '@/service/global-const'
export default {
    inheritAttrs: false,
    components: {
        [PanelBlock.name]: PanelBlock,
    },
    props: {
        // 块数据
        block: {
            type: Object,
            require: true
        },
        // 面板信息
        panelInfo: {
            type: Object,
            default: () => { }
        },
    },
    computed: {
        _attrs () {
            let { color, contentBg, primaryColor, textColor, importTextColor } = Object.assign({}, this.panelInfo.theme || {}, this.panelInfo?.customSetting || {})
            return {
                // 色系
                color,
                // 背景色
                contentBg,
                // 主题色
                primaryColor,
                // 字体颜色
                textColor,
                // 重要字体颜色
                importTextColor,
                // 主题
                theme: this.panelInfo.theme,
                ...this.$attrs
            }
        },
        // 外边距
        spaceSize () {
            let { spaceSize } = Object.assign({}, this.panelInfo.theme || {}, this.panelInfo?.customSetting || {})
            return spaceSize || GlobalConst.panel.spaceSize
        }
    },
    data () {
        return {}
    },
    methods: {
        // 自适应块的宽度
        resize () {
            this.$refs.block.resize()
        },
        // 刷新块（相当于v-if）
        refresh () {
            this.$refs.block.init()
        },
        switchStatus (info) {
            this.$emit('switchStatus', info)
        },
    },
    mounted () {
    }
}
</script>

<style lang="scss" scoped>
.block-design-view {
    height: 100%;
    width: 100%;
    position: relative;
    .mask_disabled_click {
        display: inline-block;
        height: 100%;
        width: 100%;
        position: absolute;
        top: 0;
        left: 0;
        z-index: 99;
    }
}
// 内容插槽
.render-content-slot {
    z-index: 99;
    position: absolute;
    top: 0px;
    right: 0px;
    .slot-icon-btn {
        min-width: 20px;
        display: inline-block;
        min-height: 20px;
        position: relative;
    }
    // 显示时
    .show {
        color: $primary;
    }
}
</style>