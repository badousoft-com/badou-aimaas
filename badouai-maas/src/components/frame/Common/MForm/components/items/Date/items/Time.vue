<template>
    <div>
        <template v-if="!isView">
            <el-time-select
                v-if="type === 'timeSelect'"
                v-bind="_$attrs"
                v-on="_$listeners"
                :ref="name"
                v-model="tempValue">
            </el-time-select>
            <el-time-picker
                v-else
                v-bind="_$attrs"
                v-on="_$listeners"
                :ref="name"
                v-model="tempValue">
            </el-time-picker>
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
import DateAttr from './Mixin'
export default {
    name: 'timePicker',
    mixins: [DateAttr],
    components: {},
    props: {
        type: {
            type: String,
            default: ''
        },
        // 非范围选择时的输入占位内容
        placeholder: {
            type: String,
            default: '请选择时间'
        },
        // 范围选择时开始日期的占位内容
        startPlaceholder: {
            type: String,
            default: '开始时间'
        },
        // 范围选择时结束日期的占位内容
        endPlaceholder: {
            type: String,
            default: '结束时间'
        },
        // 是否为时间范围选择
        isRange: {
            type: Boolean,
            default: false
        },
        // 是否使用箭头进行时间选择
        arrowControl: {
            type: Boolean,
            default: false
        },
        // 绑定值的格式。不指定则绑定值为 Date 对象
        valueFormat: {
            type: String,
            default: 'HH:mm:ss'
        },
        // 指定时间段配置
        pickerOptions: {
            type: Object,
            default: () => {}
        }
    },
    computed: {
        _$attrs () {
            return {
                placeholder: this.placeholder,
                startPlaceholder: this.startPlaceholder,
                endPlaceholder: this.endPlaceholder,
                isRange: this.isRange,
                arrowControl: this.arrowControl,
                valueFormat: this.valueFormat,
                pickerOptions: this.pickerOptions,
                ...(this.$attrs || {}), // 处理未显示写出来的elementUI组件的属性
            }
        },
        // // 单项数据流原因，不可直接修改值，需要用临时值做过度使用
        tempValue: {
            get () {
                if (!this.value) return this.value
                // 编辑页面处理
                if (!this.isView) {
                    return this.changeDataLayout(this.value)
                }
                // 查看页面处理
                return this.value
                
            },
            set (val) {
                this.$emit('input', this.changeDataLayout(val))
            }
        }
    }
}
</script>
<style lang='scss' scoped>

</style>