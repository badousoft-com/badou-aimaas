// 优化按钮触发代码编辑器组件弹窗 -> 触发按钮样式设置为代码块形式
<template>
    <div
        class="bd-form-code"
        :class="{
            selfClass,
            'is-view': isView
        }">
        <template>
            <disabled-board v-if="disabled"></disabled-board>
            <code-editor
                v-model="tempValue"
                :disabled="disabled"
                :isView="isView"
                v-bind="$attrs">
                <template>
                    <div class="showCodeBtn pointer">
                        {{ tempValue || _placeholder }}
                    </div>
                </template>
            </code-editor>
        </template>
    </div>
</template>
<script>
import CodeEditor from '@/components/frame/Common/CodeEditor'
import DisabledBoard from '@/components/frame/Status/DisabledBoard'
import GlobalConst from '@/service/global-const'
export default {
    name: 'bd-code',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {
        CodeEditor,
        [DisabledBoard.name]: DisabledBoard
    },
    data () { // 定义页面变量
        return {
        }
    },
    props: {
        // 组件默认值传入
        value: {
            required: true
        },
        // 是否不可编辑
        disabled: {
            type: Boolean,
            default: false
        },
        // 预输入文本
        placeholder: {
            type: String,
        },
        // 是否为查看状态
        isView: {
            type: Boolean,
            default: false
        },
        // 标签名称
        label: {
            type: String
        },
        // 自定义class
        selfClass: {
            type: String,
            default: ''
        }
    },
    computed: {
        // 单项数据流原因，不可直接修改值，需要用临时值做过度使用
        tempValue: {
            get () {
                return this.value
            },
            set (val) {
                this.$emit('input', val)
            }
        },
        // 默认预输入文本
        _placeholder () {
            if (this.label) return GlobalConst.form.placeholder.text + this.label
            return this.placeholder || GlobalConst.form.placeholder.text
        },
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
.bd-form-code::v-deep {
    .showCodeBtn {
        background:#2d2d2d;
        color:#828282 !important;
        border-radius: $borderRadius;
        padding: 0 $padding;
        height: $inputHeight;
        line-height: $inputHeight;
        overflow: auto;
    }
    &.is-view {
        .showCodeBtn {
            // 不再固定高度，让内容自动撑
            height: auto;
        }
    }
}
</style>