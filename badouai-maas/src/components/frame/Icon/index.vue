// 图标的使用规则：
//     1. 一级菜单，二级菜单使用面性图标
//     2. 三级以及之后的菜单不加图标（TODO）
//     3. 按钮图标使用线性图标
<template>
    <div
        v-if="isExternal"
        :style="styleExternalIcon"
        class="svg-external-icon svg-icon"
        v-on="$listeners">
    </div>
    <svg
        v-else
        class="pad-h-1"
        :class="svgClass"
        :style="_svgStyle"
        aria-hidden="true" 
        v-on="$listeners">
        <use :xlink:href="iconName" />
    </svg>
    
</template>
<script>
import GlobalConst from '@/service/global-const'
import { Is_External_Url } from "@/utils/validate/index"
import iconHandler from '@/components/frame/Icon/index.js'

export default {
    name: "BdIcon",
    props: {
        // 图标名称
        name: {
            type: String
        },
        // 样式类名
        className: {
            type: String,
            default: ''
        },
        // 图标填充色
        color: {
            type: String,
            default: ''
        },
        // 图标尺寸： large / middle  / small
        size: {
            type: [String, Number],
            default: ''
        },
        // 是否使用面性的icon图标
        fillIcon: {
            type: Boolean,
            default: false
        },
        // 是否显示手势
        cursor: {
            type: String,
            default: 'inherit'
        }
    },
    data () {
        return {
            // 默认图标名称
            icon: 'default-fill'
        }
    },
    computed: {
        // 获取图标样式
        _svgStyle () {
            let res = {}
            this.color && (res['fill'] = this.color)
            this.cursor && (res['cursor'] = this.cursor)
            return res
        },
        isExternal () {
            return Is_External_Url(this.name)
        },
        iconName () {
            // 获取icon名称
            let name = this.name || this.icon
            /**
             * 默认图标的处理
             * 当不存在该图标文件时，为其显示默认图标。
             * 旧版的图标使用方法，不做处理，该有的有，该没的没。
             */
            if (!name.includes(GlobalConst.icon.prefix) &&
                !name.includes('bd ') &&
                !name.includes('iconfont')) {
                // console.log('new way:', name)
                // 判断是否需要转换成面性图标 ，以及icon是否已经是面性图标了
                if (this.fillIcon && !name.includes('-fill')) {
                    // 给icon拼接上字符串转换成面性图标
                    name = name + '-fill'
                }
                // 当项目里不存在该icon图标时
                if (!iconHandler.hasIcon(name)) {
                    // 是否为面性图标
                    if (this.fillIcon) {
                        // 去除面性图标字符串，使用线性图标
                        name = name.replace('-fill', '')
                        // 再次判断，当线性图标也不存在于项目时
                        if (!iconHandler.hasIcon(name)) {
                            // 使用默认图标
                            name = this.icon
                        }
                    } else { // 不是面性图标
                        name = this.icon
                    }
                }
                name = GlobalConst.icon.prefix + name
            }

            return name
        },
        svgClass () {
            return `bd-icon ${this.className} ${this.size? 'icon-' + this.size : ''}`
        },
        styleExternalIcon () {
            return {
                mask: `url(${this.name}) no-repeat 50% 50%`,
                "-webkit-mask": `url(${this.name}) no-repeat 50% 50%`
            };
        }
    }
};
</script>

<style scoped lang="scss">
// 让图标左右有点间距，避免与文字一起使用时贴的太近
.pad-h-1 {
    padding: 0px 1px;
}
$size: 1.4em;
.bd-icon {
    width: $size;
    height: $size;
    vertical-align: -0.3em;
    fill: currentColor;
    overflow: hidden;
    cursor: inherit; // 图标默认不应该有手势，有些图标只做展示功能
    &.icon-middle {
        width: 2 * $size;
        height: 2 * $size;
    }
    &.icon-large {
        width: 6 * $size;
        height: 6 * $size;
    }
    &.icon-small {
        width: $size - 0.3em;
        height: $size - 0.3em;
        vertical-align: -0.2em;
    }
    &.icon-mini {
        width: $size - 0.6em;
        height: $size - 0.6em;
        vertical-align: -0.1em;
    }
    &.fill { fill: $primary; }
    // 遍历生成主题相关类
    @each $themeType, $themeColor in (primary, $primary), 
                                (warning, $warning), 
                                (danger, $danger), 
                                (success, $success){
        &.#{$themeType} { 
            fill: $themeColor; 
        }
        &.#{$themeType}C { 
            fill: $themeColor; 
        }
    }
}
.svg-external-icon {
    background-color: currentColor;
    mask-size: cover !important;
    display: inline-block;
}
</style>
