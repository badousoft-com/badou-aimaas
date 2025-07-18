<template>
    <div :class="selfClass">
        <template v-if="!isView">
            <!-- textarea rows属性至少为2，为1样式有问题，并且1与普通input输入一致，那就没必要使用textarea -->
            <el-input
                :ref="_refName"
                type="textarea"
                v-model="tempValue" 
                :disabled="disabled"
                :placeholder="_placeholder"
                :rows="_rows"
                :autosize="autoSizeFun"
                :maxlength="maxlength"
                :show-word-limit="Boolean(maxlength)"
                :class="{'noResize': !noResize}"
                v-bind="$attrs"
                v-on="$listeners">
            </el-input>
        </template>
        <template v-else>
            <bd-icon 
                :name="iconName" 
                v-if="tempValue && iconName" 
                class="fill icon-small">
            </bd-icon>
            <span>{{tempValue | completeValue}}</span>
        </template>
    </div>
</template>
<script>
import GlobalConst from '@/service/global-const'
import { Get_UUID, Has_Value } from '@/utils'
export default {
    name: 'bd-textarea',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {},
    data () { // 定义页面变量
        return {
        }
    },
    props: {
        name: {
            type: String
        },
        // 设置组件是否可编辑，默认可编辑Boolean(false)，非必须
        disabled: {
            type: Boolean,
            default: false
        },
        // 组件默认值传入
        value: {
            type: String,
            default: ''
        },
        // 标签名称
        label: {
            type: String,
            default: GlobalConst.form.label
        },
        // 预输入文本
        placeholder: {
            type: String
        },
        // 最大允许输入长度
        maxlength: {
            type: Number
        },
        // // 最小行数
        // minRows: {
        //     type: Number,
        //     default: this.rows
        // }, 
        // // 最大行数
        // maxRows: {
        //     type: Number,
        //     default: GlobalConst.textarea.maxRows
        // },

        // 根据输入内容自动撑高高度,可传入Boolean或Object，如{ minRows: 2, maxRows: 6 }
        autoSize: {
            type: [Boolean, Object],
            default: true
        },
        // 文本域高度。注意只有当autoSize为Boolean值且为false时，rows值才可以生效
        rows: {
            type: [Number, String],
            default: GlobalConst.textarea.rows
        },
        // 是否显示可自由拖动尺寸按钮
        noResize: {
            type: Boolean,
            default: false
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
    computed: {
        // 获取实例键名
        _refName () {
            return Get_UUID() + this.name || ''
        },
        // 获取展示行数
        _rows () {
            // 添加Math.abs做绝对值处理，避免负数
            return Has_Value(this.rows) && Math.abs(parseInt(this.rows)) ||
                    GlobalConst.textarea.rows
        },
        // 默认预输入文本
        _placeholder () {
            return this.placeholder || (GlobalConst.form.placeholder.text + this.label)
        },
        // 单项数据流原因，不可直接修改值，需要用临时值做过度使用
        tempValue: {
            get () {
                return this.value
            },
            set (val) {
                this.$emit('input', val)
            }
        },
        // 根据输入内容自动撑高高度
        autoSizeFun () {
            // 设置了el-textarea属性autoSize为true时，rows属性会失效，需要做处理
            if (this.autoSize) {
                if (this.autoSize.constructor === Boolean) {
                    // 传入autoSize属性为boolean值且为true时，使用rows属性
                    return { 
                        minRows: this._rows
                    }
                }
                if (this.autoSize.constructor === Object) {
                    // 当传入autoSize属性为对象时，则使用该对象进行渲染
                    return this.autoSize
                }
            }
            return this.autoSize
        },
    },
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
    },
    watch: {
        // 监听行数
        _rows () {
            // autoSize不为false的情况，动态修改rows页面将不会变化，需要手动调整视图
            this.$nextTick(() => {
                let _ref = this.$refs[this._refName]
                // 重新调整文本域
                _ref && _ref.resizeTextarea()
            })
        }
    }
}
</script>
<style lang='scss' scoped>
</style>