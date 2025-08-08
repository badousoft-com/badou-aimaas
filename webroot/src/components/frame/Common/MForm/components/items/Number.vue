<template>
    <div :class="selfClass">
        <template v-if="!isView">
            <div
                :class="{
                    'is-disabled': disabled
                }"
                class="bd-input-number">
                <span
                    class="bd-input-number__decrease"
                    @click="addNum(false)">
                    <bd-icon name="subtract" size="small"></bd-icon>
                    <disabled-board v-if="isDisabledDecrease" :title="'最小值:' + _min"></disabled-board>
                </span>
                <span
                    class="bd-input-number__increase"
                    @click="addNum(true)">
                    <bd-icon name="plus" size="small"></bd-icon>
                    <disabled-board v-if="isDisabledIncrease"  :title="'最大值:' + _max"></disabled-board>
                </span>
                <div class="bd-input">
                    <input
                        :ref="inputRef"
                        v-model.number="tempValue"
                        class="bd-input__inner"
                        type="text"
                        @keydown.prevent.down="addNum(false)"
                        @keydown.prevent.up="addNum(true)"
                        @blur="blurEvt($event)"
                        @focus="focusEvt($event)">
                </div>
                <disabled-board v-if="disabled"></disabled-board>
            </div>
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
import { Has_Value } from '@/utils'
import DisabledBoard from '@/components/frame/Status/DisabledBoard'
const Step = 1
export default {
    name: 'bd-number',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {
        [DisabledBoard.name]: DisabledBoard
    },
    props: {
        // 设置组件是否可编辑，默认可编辑Boolean(false)，非必须
        disabled: {
            type: Boolean,
            default: false
        },
        // 组件默认值传入
        value: {
            type: [Number, String],
        },
        // 标签名称
        label: {
            type: String,
            default: GlobalConst.form.label
        },
        // 设置计数器允许的最小值
        min: {
            type: [Number, String],
            default: -Infinity
        },
        // 设置计数器允许的最大值
        max: {
            type: [Number, String],
            default: Infinity
        },
        // 计数器步长
        step: {
            type: [Number, String],
            default: Step
        },
        // 是否开放允许使用
        isOpen: {
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
            inputRef: 'inputRef',
        }
    },
    computed: {
        // 获取最小值，默认为无穷小-Infinity
        _min () {
            if (!this.min) return -Infinity
            let _val = Number(this.min)
            return Has_Value(_val) ? _val : -Infinity
        },
        // 获取最大值，默认为无穷大Infinity
        _max () {
            if (!this.max) return Infinity
            let _val = Number(this.max)
            return Has_Value(_val) ? _val : Infinity
        },
        // 获取当前是否可增的状态
        isDisabledIncrease () {
            if (!Has_Value(this.tempValue)) return
            if (this.tempValue >= this._max ) {
                // 当输入值大于最大值时，重置值为最大值
                this.tempValue = this._max
                return true
            }
            return false
        },
        // 获取当前是否可减的状态
        isDisabledDecrease () {
            if (!Has_Value(this.tempValue)) return
            if (this.tempValue <= this._min ) {
                // 当输入值小于最小值时，重置值为最小值
                this.tempValue = this._min
                return true
            }
            return false
        },
        // 获取计数器步长
        _step () {
            // 无值，则返回默认值
            if (!this.step) return Step
            // 获取到数字类型值
            let _val = Math.abs(Number(this.step || 0) || 0)
            // 增量最少为1
            if (_val === 0) return Step
            return _val
        },
        // 默认预输入文本
        defaultPlaceholder () {
            return this.placeholder + this.label
        },
        // 单项数据流原因，不可直接修改值，需要用临时值做过度使用
        tempValue: {
            get () {
                return Has_Value(this.value) ? Number(this.value) : null
            },
            set (val) {
                let _val = Has_Value(val)? Number(val) : null
                _val = Has_Value(_val) ? _val : null
                this.$emit('input', _val)
                this.$emit('change', _val)
            }
        }
    },
    methods: { // 定义函数
        /**
         * 操作值事件
         * @param {Boolean} addStatus 增 / 减状态
         */
        addNum (addStatus) {
            // 获取当前值
            let _currentVal = Number(this.tempValue || 0) || 0
            if (addStatus && !this.isDisabledIncrease) {
                // 若当前增，并且可增，则添加值
                this.tempValue = _currentVal + this._step
            }
            if (!addStatus && !this.isDisabledDecrease) {
                // 若当前减，并且可减，则减去值
                this.tempValue = _currentVal - this._step
            }  
        },
        // 失焦-回调事件
        blurEvt (event) {
            this.$emit('blur', event)
        },
        // 聚焦-回调事件
        focusEvt (event) {
            this.$emit('focus', event)
        },
        // 主动-聚焦事件
        focus () {
            this.$refs[this.inputRef].focus()
        },
        // 主动-选择事件
        select () {
            this.$refs[this.inputRef].select()
        },
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        
    },
}
</script>
<style lang='scss' scoped>
.bd-input-number {
    position: relative;
    display: inline-block;
    width: 130px;
    line-height: 30px;
    @mixin creaseIcon {
        position: absolute;
        z-index: 1;
        top: 1px;
        height: auto;
        text-align: center;
        background: #F5F7FA;
        color: #606266;
        cursor: pointer;
        width: 32px;
        font-size: 13px;
        &:hover {
            color: $primary;
        }
    }
    .bd-input-number__decrease {
        @include creaseIcon;
        left: 1px;
        border-radius: $borderRadius 0 0 $borderRadius;
        border-right: 1px solid $borderColor;
    }
    .bd-input-number__increase {
        @include creaseIcon;
        right: 1px;
        border-radius: 0 $borderRadius $borderRadius 0;
        border-left: 1px solid $borderColor;
    }
    .bd-input {
        display: block;
        position: relative;
        width: 100%;
        font-size: 13px;
        .bd-input__inner {
            height: 32px;
            line-height: 32px;
            padding-left: 39px;
            padding-right: 39px;
            text-align: center;
            -webkit-appearance: none;
            background: #fff;
            border-radius: $borderRadius;
            border: 1px solid #DCDFE6;
            box-sizing: border-box;
            color: #606266;
            display: inline-block;
            font-size: inherit;
            outline: none;
            transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
            width: 100%;
        }
    }
    .bd-input-number__decrease:hover:not(.is-disabled) ~ .bd-input .bd-input__inner:not(.is-disabled),
    .bd-input-number__increase:hover:not(.is-disabled) ~ .bd-input .bd-input__inner:not(.is-disabled),
    .bd-input__inner:hover:not(.is-disabled) {
        border-color: $primary;
    }
}
</style>