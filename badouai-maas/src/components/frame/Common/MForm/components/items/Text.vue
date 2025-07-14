<template>
    <div :class="selfClass">
        <template v-if="!isView">
            <!-- 文本控制数字难
            数字转文本简单
            因此这里属性以数字为主，使用v-model.number -->
            <!-- TODO: 这里使用来两个el-input的问题：v-model.number的存在导致一开始输入的
                如果是数字则无法再输入中文，暂且使用另一个el-input,不添加number；需要
                找到更合理的方案完成这一块逻辑 -->
            <el-input
                v-if="_isNumber"
                :ref="refName"
                type="number"
                :name="name"
                :disabled="disabled"
                v-model.number="tempValue"
                :placeholder="_placeholder"
                :show-password="isPassword"
                :maxlength="maxlength"
                :autocomplete="autocomplete"
                :show-word-limit="Boolean(maxlength)"
                :clearable="clearable"
                @keyup.enter.native="enter"
                v-bind="$attrs"
                v-on="_$listeners"> 
                <!-- 默认设置清除按钮，当存在数值长度限制时，去除清除，避免展示异常 -->
                <template slot="prepend" v-if="prepend">{{prepend}}</template>
                <template slot="append" v-if="append">{{append}}</template>
            </el-input>
            <el-input
                v-else
                :ref="refName"
                class="count-input"
                :style="{ '--paddingRight': Boolean(maxlength) ? `${6 * String(maxlength).length * 2 + 25}px`: '20px' }"
                type="text"
                :name="name"
                :disabled="disabled"
                v-model="tempValue"
                :placeholder="_placeholder" 
                :show-password="isPassword"
                :maxlength="maxlength"
                :autocomplete="autocomplete"
                :show-word-limit="Boolean(maxlength)"
                :clearable="clearable"
                @keyup.enter.native="enter"
                v-bind="$attrs"
                v-on="_$listeners"> 
                <!-- 默认设置清除按钮，当存在数值长度限制时，去除清除，避免展示异常 -->
                <template slot="prepend" v-if="prepend">{{prepend}}</template>
                <template slot="append" v-if="append">{{append}}</template>
            </el-input>
        </template>
        <template v-else>
            <bd-icon 
                v-if="tempValue && _icon"
                v-bind="_icon"
                class="fill icon-small">
            </bd-icon>
            <span>{{tempValue | completeValue}}</span>
        </template>
    </div>
</template>
<script>
import GlobalConst from '@/service/global-const'
import { String_To_Number } from '@/utils/data-type-change'
import { Debounce, Has_Value } from '@/utils/index'
import { Get_Icon_Obj } from '@/service/icon.js'
export default {
    name: 'bd-text',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {
    },
    inheritAttrs: false,
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
        value: {
            type: [String, Number],
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
        // 目前使用new-password时方可以使浏览器默认不填入账号密码
        // 原生属性，自动补全   on / off
        autocomplete: {
            type: String,
            default: 'new-password'
        },
        // 是否为密码
        isPassword: {
            type: Boolean,
            default: false
        },
        // 是否可清空
        // 若使用了maxlength则不要设置这个为true，样式会有问题
        clearable: {
            type: Boolean,
            default: false // 设置成true的话会有鼠标悬浮时有抖动效果，另外与字数限制的提示搭配不友好
        },
        // 前置元素
        prepend: {
            type: String,
            default: ''
        },
        // 后置元素
        append: {
            type: String,
            default: ''
        },
        // 是否为查看状态
        isView: {
            type: Boolean,
            default: false
        },
        // 限制输入数据的格式
        //     int: 只能输入整数，数据格式为Number
        //     number：只能输入数字类型,数据格式为Number
        //     string: 字符串，数据格式为String
        dataType: {
            type: String,
            default: 'string'
        },
        // (推荐使用)图标名称：isView为true时生效
        icon: {
            type: [Object, String]
        },
        // (即将废弃，推荐使用icon属性)图标配置项
        // {
        //     @param {String} name: 图标名称
        //     @param {String} className: 图标类名
        //     @param {String} color: 图标颜色
        //     @param {String} position: 图标位置（文字左还是文字右，默认textLeft）
        // }
        // 当接收string时为直接传入name值
        iconProps: {
            type: [Object, String]
        },
        // 自定义class,相对整个组件
        selfClass: {
            type: String,
            default: ''
        },
        // 是否开启防抖
        isDebounce: {
            type: Boolean,
            default: true
        },
        // input防抖计时器时间
        duration: {
            type: [String, Number],
            default: 1000
        },
    },
    data () { // 定义页面变量
        return {
            refName: 'text',
            debounceFun: null, // input事件防抖函数
            temp: '', // 存储的输入值，用来判断是否出现了小数点后0无效问题
        }
    },
    computed: {
        // 返回图标对象
        _icon () {
            return Get_Icon_Obj(this.icon || this.iconProps)
        },
        // 是否为数字类型：包含整数或小数
        _isNumber () {
            return this.dataType === 'int' || this.dataType === 'number'
        },
        // 默认预输入文本
        _placeholder () {
            return this.placeholder || (GlobalConst.form.placeholder.text + this.label)
        },
        // 修改$listeners里的方法
        _$listeners () {
            if (!this.$listeners) return
            let { input, ...others } = this.$listeners
            // 重写input监听器，添加防抖和类型修改代码
            input = (val) => {
                let _val = null
                switch (this.dataType) {
                    // 输入数字（小数，整数等）
                    case 'number':
                        // 判断是否有值，无值则使用null，避免空字符串影响number数据
                        _val = Has_Value(String_To_Number(val)) ? String_To_Number(val) : null
                        break
                    // 输入整数
                    case 'int':
                        _val = Has_Value(val) ? parseInt(val) : null
                        break
                    // 输入字符串
                    case 'string':
                    default:
                        // 其他情况：默认都将输入值转成字符串
                        _val = Has_Value(val) ? ('' + val) : ''
                }
                // 存储字符串类型的输入值
                this.temp = val
                this.$emit('input', _val)
                // 添加change事件
                this.$emit('change', _val)
                // 是否需要开启防抖
                if (this.isDebounce) {
                    this.debounceFun(_val)
                }
            }
            return {
                input,
                ...others
            }
        },
        // 单项数据流原因，不可直接修改值，需要用临时值做过度使用
        tempValue: {
            get () {
                if (this.dataType === 'number') {
                    if (Has_Value(this.temp) &&
                        Has_Value(this.value) &&
                        this.temp !== this.value.toString()) {
                        // 不一致则说明是小数点后无效0问题，如：2.00、1.0
                        return this.temp
                    }
                }
                if (this._isNumber) {
                    let _val = String_To_Number(this.value)
                    return  Has_Value(_val) ? _val : null
                }
                return this.value
            },
            set (val) {
                // 在_$listeners中的input事件去实现
            }
        }
    },
    methods: { // 定义函数
        // 设置聚焦
        focus () {
            this.$refs[this.refName]?.focus()
        },
        // 设置失焦
        blur () {
            this.$refs[this.refName]?.blur()
        },
        // 设置文本选择
        select () {
            this.$refs[this.refName]?.select()
        },

        // ====================================

        // 抛出键盘enter事件
        enter () {
            this.$emit('enter')
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        // 定义防抖input监听器需要emit抛出的事件
        this.debounceFun = Debounce(function (value) {
            // 当Text组件需要使用防抖时，通过@inputDebounce来获取防抖的输入值
            this.$emit('debounce', value, this.name)
        }, this.duration)
    },
}
</script>
<style lang='scss' scoped>
.count-input {
    /deep/ .el-input__inner {
        // 根据传入的变量，设置输入框的padding-right以修复部分字体被限制字数覆盖的bug
        padding-right: var(--paddingRight);
    }
}
</style>