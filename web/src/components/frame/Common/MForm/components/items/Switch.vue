<template>
    <div :class="selfClass">
        <template v-if="!isView">
            <el-switch
                v-model="tempValue"
                :active-color="activeColor"
                :inactive-color="inactiveColor"
                :active-text="activeText"
                :inactive-text="inactiveText"
                :active-value="yes"
                :inactive-value="no"
                :disabled="disabled"
                v-bind="$attrs"
                @change="change">
            </el-switch>
        </template>
        <template v-else>
            <bd-icon 
                :name="iconName" 
                v-if="tempValue && iconName" 
                class="fill icon-small">
            </bd-icon>
            <span>{{(tempValue ? activeText : inactiveText) | completeValue}}</span>
        </template>
    </div>
</template>
<script>
import { YES, NO, Get_Switch_Val } from '@/service/switch'
import globalStyle from '@/styles/theme.scss'
export default {
    name: 'bd-switch',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {},
    props: {
        // 设置组件是否可编辑，默认可编辑Boolean(false)，非必须
        disabled: {
            type: Boolean,
            default: false
        },
        // 字段名
        name: {
            type: String
        },
        // 组件默认值传入
        // 这里Boolean要放在String后面，放前面的话值传入''时会默认为Boolean的true
        value: {
            type: [String, Boolean, Number],
            default: NO.Boolean
        },
        // 选中文本
        activeText: {
            type: String,
        },
        // 未选中文本
        inactiveText: {
            type: String
        },
        // switch 打开时的背景色
        activeColor: {
            type: String,
            default: globalStyle.primary
        },
        // switch 关闭时的背景色
        inactiveColor: {
            type: String
        },
        // TODO（无法使用）:2022-06-23暂时废弃，switch 打开时的值
        activeValue: {
            type: [Boolean, String, Number],
            default: 1
        },
        // TODO（无法使用）:2022-06-23暂时废弃，switch 关闭时的值
        inactiveValue: {
            type: [Boolean, String, Number],
            default: 0
        },
        // 标签名称
        label: {
            type: String
        },
        // 是否为查看状态
        isView: {
            type: Boolean,
            default: false
        },
        // 图标名称：用于查看页面展示
        iconName: {
            type: String
        },
        // 自定义class
        selfClass: {
            type: String,
            default: ''
        }
    },
    data () { // 定义页面变量
        return {
            // 定义：是
            yes: YES.Boolean,
            // 定义：否
            no: NO.Boolean,
        }
    },
    computed: {
        // 单项数据流原因，不可直接修改值，需要用临时值做过度使用
        tempValue: {
            get () {
                // 获取switch实际使用值，目前主要为true/false
                return Get_Switch_Val(this.value)
            },
            set (val) {
                this.$emit('input', val)
            }
        }
    },
    methods: { // 定义函数
        change (newVal) {
            this.$emit('change', newVal)
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
    },
}
</script>
<style lang='scss' scoped>

</style>