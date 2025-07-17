<template>
    <div
        :class="{
            'has-event': _hasEvent,
            'hover-animate': _hover,
            _type: true
        }"
        :style="_style"
        class="triangle-status"
        @click="handleClick()">
        <div
            :style="_childStyle"
            class="triangle-status-panel">
            <slot>
                <bd-icon v-if="_icon" :name="_icon"></bd-icon>
                <span v-if="text" class="s-text">{{text}}</span>
            </slot>
        </div>
    </div>
</template>
<script>
import globalStyle from '@/styles/theme.scss'
export default {
    name: 'triangle-status',
    components: {},
    props: {
        // 三角型的尺寸宽度
        width: {
            type: [Number, String],
            default: 15
        },
        // 传入图标
        icon: {
            type: String
        },
        // 传入文本
        text: {
            type: [Number, String]
        },
        // 类型值：primary/warning/success/danger/operate
        type: {
            type: String
        },
        // 背景色
        bg: {
            type: String
        },
        // 是否启用hover时翻转动画
        hover: {
            type: Boolean,
            default: false
        },
        // 是否为关闭按钮属性
        isClose: {
            type: Boolean,
            default: false
        },
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
        // 获取样式对象
        _style () {
            // 获取默认色：主题色
            let _color = globalStyle.primary
            // 判断是否传入type类型，有则使用类型代表的对应色
            if (this._type && this._type in globalStyle) {
                _color = globalStyle[this._type]
            }
            return {
                'border-width': this._width,
                'border-color': `${_color} ${_color} transparent transparent`
            }
        },
        // 获取zoom缩放比例，用于一些三角尺寸较小的场景下，方便调整内容进行展示
        _childStyle () {
            if (parseInt(this.width) < 14) {
                return {
                    zoom: parseFloat(parseInt(this.width) / 14)
                }
            }
        },
        // 获取展示尺寸
        _width () {
            return `${parseInt(this.width)}px`
        },
        // 是否开启hover时翻转动画
        _hover () {
            return this.isClose || this.hover
        },
        // 获取展示图标
        _icon () {
            return this.isClose && 'multi' ||
                   this.icon
        },
        // 获取类型
        _type () {
            return this.isClose && 'danger' ||
                   `${this.type}` ||
                   'primary'
        },
        // 判断是否存在事件，以渲染手势样式
        _hasEvent () {
            return !!(this.$listeners && this.$listeners.click)
        }
    },
    methods: { // 定义函数
        handleClick () {
            this.$emit('click')
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
    }
}
</script>
<style lang='scss' scoped>
.triangle-status::v-deep {
    position: absolute;
    border-width: 15px;
    border-style: solid;
    // border-color: $danger $danger transparent transparent;
    right: 0;
    top: 0;
    font-size: 12px;
    color: #fff;
    &.has-event {
        cursor: pointer;
    }
    $iconTranslate: translate(92%, -88%);
    .triangle-status-panel {
        position: absolute;
        right: 0;
        transform: $iconTranslate rotate(0deg);

        .bd-icon {
            transition: all .6s;
            transform: rotate(0deg);
        }
    }
    &.hover-animate {
        &:hover {
            .bd-icon {
                transform: rotate(180deg);
            }
        }
    }
}
</style>