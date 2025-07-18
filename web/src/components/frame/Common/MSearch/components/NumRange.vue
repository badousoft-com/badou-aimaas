<template>
    <div class="num-range">
        <m-search-option
            ref="numRangePopover"
            :name="label"
            :isShowClear="true"
            :valueText="valueText"
            :isPopoverShow="showPopover">
            <template v-slot:panel>
                <div class="num-range-panel">
                    {{rangeTextObj.front}}
                    <bd-text
                        :placeholder="_placeholder.min"
                        v-model="numRange.startNum"
                        dataType="number"
                        class="s-num"
                        @input="numInputEvent"
                        @keyup.enter.native="search">
                    </bd-text>
                    {{rangeTextObj.between}}
                    <bd-text
                        :placeholder="_placeholder.max"
                        v-model="numRange.endNum"
                        dataType="number"
                        class="s-num"
                        @input="numInputEvent"
                        @keyup.enter.native="search">
                    </bd-text>
                    {{rangeTextObj.end}}
                </div>
                <div class="s-error-tip dangerC fontS">
                    {{errorTip}}
                </div>
                <!-- 操作区域 -->
                <el-button
                    v-btnBg="i"
                    v-for="(i, index) in btnList"
                    :key="index"
                    :disabled="!!errorTip"
                    @click="exeMethod(i)">
                    <bd-icon :name="i.icon"></bd-icon>{{i.name}}
                </el-button>
            </template>
        </m-search-option>
    </div>
</template>
<script>
import MSearchOption from '@/components/frame/Common/MSearchOption'
import { Text } from '@/components/frame/Common/MForm/components/items/index'
import { Has_Value } from '@/utils'
export default {
    name: 'search-num-range',
    components: {
        MSearchOption,
        [Text.name]: Text
    },
    props: {
        // label标签字段
        label: {
            type: String
        },
        // 字段名称
        id: {
            type: String
        },
        // 字段值
        value: {},
        // 最小值
        minNum: {
            type: [String, Number],
        },
        // 最大值
        maxNum: {
            type: [String, Number],
        },
        // 值对应显示文本
        defaultText: {
            type: String,
            default: '全部'
        },
    },
    data () { // 定义页面变量
        return {
            // 错误提示
            errorTip: null,
            // 展示整块选区状态
            showPopover: false,
            // 范围区相关使用文本
            rangeTextObj: {
                front: '在',
                between: '到',
                end: '之间',
                higher: '高于',
                lower: '低于',
                startDesc: '起始值',
                endDesc: '结束值'
            },
            // 操作按钮数据
            btnList: [
                { id: 'search', name: '搜索', icon: 'search', method: 'search' }
            ],
        }
    },
    computed: {
        // 输入值范围
        numRange: {
            get () {
                return this.value || { startNum: null, endNum: null}
            },
            set (val) {
                // numRange可能存在变更，需要保留set，但不需要做什么
            }
        },
        // 显示文本
        valueText () {
            // 获取动态组装的显示文本
            return this.getValueText() || this.defaultText
        },
        // 允许输入的范围最小值
        _minNum () {
            return parseFloat(this.minNum) || null
        },
        // 允许输入的范围最大值
        _maxNum () {
            return parseFloat(this.maxNum) || null
        },
        // 输入框提示文本
        _placeholder () {
            return {
                min: Has_Value(this._minNum) ? `最低${this._minNum}` : `${this.rangeTextObj.startDesc}`,
                max: Has_Value(this._maxNum) ? `最高${this._maxNum}` : `${this.rangeTextObj.endDesc}`
            }
        },
    },
    methods: { // 定义函数
        /**
         * 起始值修改触发事件
         * @param {Number} val 起始值
         */
        numInputEvent () {
            // 获取起始值
            let _startVal = this.numRange.startNum
            // 获取结束值
            let _endVal = this.numRange.endNum
            // 1. 起始值与最小值存在时，判断起始值是否≥最小值
            if (Has_Value(this._minNum) &&
                Has_Value(_startVal) &&
                (parseFloat(_startVal) < this._minNum)) {
                this.errorTip = `${this.rangeTextObj.startDesc}必须≥${this._minNum},不能为${_startVal}`
                return
            }
            // 2. 结束值与最大值存在时，判断结束值是否≤最大值
            if (Has_Value(this._maxNum) &&
                Has_Value(_endVal) &&
                (parseFloat(_endVal) > this._maxNum)) {
                this.errorTip = `${this.rangeTextObj.endDesc}必须≤${this._maxNum},不能为${_endVal}`
                return
            }
            // 3. 起始值与结束值存在时，判断起始值是否≤结束值
            if (Has_Value(_startVal) &&
                Has_Value(_endVal) &&
                parseFloat(this.numRange.startNum) > parseFloat(this.numRange.endNum)) {
                this.errorTip = `${this.rangeTextObj.startDesc}必须≤${this.rangeTextObj.endDesc}`
                return
            }
            // 4. 输入值正常，设置错误提示为空
            this.errorTip = null
            // 抛给父级组件
            this.$emit('input', this.numRange)
        },
        exeMethod (obj) {
            this[obj.method]()
        },
        // 生成展示文本
        getValueText () {
            // 判断起始值是否存在
            let hasStartNum = Has_Value(this.numRange.startNum)
            // 判断结束值是否存在
            let hasEnd = Has_Value(this.numRange.endNum)
            // 1. 起始值与结束值同时存在时
            if (hasStartNum && hasEnd) {
                return [
                    this.rangeTextObj.front,
                    this.numRange.startNum,
                    this.rangeTextObj.between,
                    this.numRange.endNum,
                    this.rangeTextObj.end
                ].join('')
            }
            // 2. 只写了起始值
            if (hasStartNum && !hasEnd) {
                return [
                    this.rangeTextObj.higher,
                    this.numRange.startNum
                ].join('')
            }
            // 3. 只写了结束值
            if (!hasStartNum && hasEnd) {
                return [
                    this.rangeTextObj.lower,
                    this.numRange.endNum
                ].join('')
            }
            // 4. 起始值与结束值都没有写，显示默认值
            return this.defaultText
        },
        search () {
            // 输入值不合格，直接终止逻辑
            if (this.errorTip) return
            // 获取展示文本
            this.text = this.getValueText()
            // this.numRange的格式为{ startNum: '', endNum: '' }
            this.$emit('input', this.numRange)
            // 值更新事件传给父级组件
            this.$emit('change', this.id)
            // 隐藏选择面板
            this.$refs.numRangePopover.temp_isPopoverShow = false
            // 调用父级组件的search方法
            this.$emit('search', this.numRange)
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
.num-range-panel::v-deep {
    display: flex;
    align-items: center;
    .s-num {
        margin: 0 5px;
        width: 120px;
    }
}
$errorTipHeight: 24px;
.s-error-tip {
    height: $errorTipHeight;
}
</style>
