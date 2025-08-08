<template>
    <el-button
        v-waves
        @click="handleClick"
        :type="type"
        :loading="loading"
        :disabled="disabled"
        :plain="plain"
        v-bind="_attrs"
        class="bd-btn bd-button"
        :class="{
            'is-text': type === 'text',
            'is-disabled': disabled,
        }">
        <bd-icon
            v-if="!loading && _icon"
            v-bind="_icon"
            :fillIcon="fillIcon"
            class="bd-btn__icon">
        </bd-icon>
        <span class="bd-btn__text">
            <slot>{{name}}</slot>
        </span>
    </el-button>
</template>
<script>
// 通用按钮方案
const Common_Btns = require('@/components/frame/Icon/commonBtns.json')
import { Get_Icon_Obj } from '@/service/icon.js'
import waves from '@/directive/waves/index.js' 
export default {
    name: 'bd-button',
    // 禁止父级传入属性作为当前组件的根标签属性
    inheritAttrs: false,
    components: {},
    directives: {
        // 水波纹指令
        waves
    },
    props: {
        // 按钮类型
        // primary / success / warning / danger / info / text
        type: {
            type: String,
        },
        // 按钮名称
        name: {
            type: String
        },
        // 按钮图标名称/按钮对象
        //     传入值为字符串时：表示图标名称
        //     传入值为对象时：含有名称，颜色等属性的对象
        icon: {
            type: [String, Object]
        },
        // 是否加载中状态
        loading: {
            type: Boolean,
            default: false
        },
        // 是否禁用状态
        disabled: {
            type: Boolean,
            default: false
        },
        // 按钮背景色
        background: {
            type: String,
        },
        // 是否使用列表线性按钮（朴素按钮）
        plain: {
            type: Boolean,
            default: false
        },
        // 是否使用面性的icon图标
        fillIcon: {
            type: Boolean,
            default: false
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
        _attrs () {
            // 去除click属性事件(避免click事件内容渲染在html上)，剩余属性传递给el-button
            let { click, ...attrs } = this.$attrs
            return attrs
        },
        _icon () {
            return Get_Icon_Obj(this.icon)
        }
    },
    methods: { // 定义函数
        /**
         * 获取点击节点所对应的按钮对象
         * @param {Element} target 点击节点DOM对象
         * @param {Number} level 向上遍历的层级，默认最高往上找10级
         */
        getBtnNode (target, level = 10) {
            // 判断node节点是否为button，也就是目标核心返回的节点
            if (target.nodeName === 'BUTTON') {
                return target
            }
            // 遍历10级依旧找不到，直接return
            if (level === 0) return
            // level降1，递归向上查找父级节点是否为button
            return this.getBtnNode(target.parentNode, level - 1)
        },
        handleClick ($event) {
            this.$emit('click', $event)
            // 点击节点后，取消按钮的选中焦点状态
            this.getBtnNode($event.target)?.blur()
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
.bd-btn::v-deep {
    &.is-text {
        &.is-loading {
            &::before {
                // 当按钮使用文本展示的时候，加载状态是不需要背景色模块的
                background: unset;
            }
        }
    }
    &.is-disabled {
        .bd-icon {
            cursor: not-allowed !important;
        }
    }
}
</style>