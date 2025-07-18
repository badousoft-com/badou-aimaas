<template>
    <div>
        <template v-if="!isView">
            <el-date-picker
                v-bind="_$attrs"
                v-on="_$listeners"
                :ref="name"
                v-model="tempValue">
            </el-date-picker>
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
import DateAttr from './Mixin'
export default {
    name: 'dateTimePicker',
    mixins: [DateAttr],
    components: {},
    props: {
        // 非范围选择时的输入占位内容
        placeholder: {
            type: String,
            default: '请选择日期时间'
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
        // 是否使用箭头进行时间选择
        timeArrowControl: {
            type: Boolean,
            default: false
        },
        // 显示类型
        // 当前类型下可选：datetime/datetimerange
        type: {
            type: String,
            default: 'datetime'
        },
        // 显示在输入框中的格式
        format: {
            type: String,
            default: 'yyyy-MM-dd HH:mm:ss'
        },
        // 绑定值的格式。不指定则绑定值为 Date 对象
        valueFormat: {
            default: String,
            default: 'yyyy-MM-dd HH:mm:ss'
        },
        // 选中日期后的默认具体时刻
        // 非范围选择时：形如12:00:00的字符串；范围选择时：数组，长度为 2，每项值为字符串，形如12:00:00，第一项指定开始日期的时刻，第二项指定结束日期的时刻。不指定会使用时刻 00:00:00
        // 非范围选择时：string / 范围选择时：string[]
        defaultTime: {
            type: [Array, String]
        },
        // 在范围选择器里取消两个日期面板之间的联动
        unlinkPanels: {
            type: Boolean,
            default: false
        }
    },
    computed: {
        _$attrs () {
            return {
                ...(this.$props || {}),
                ...(this.$attrs || {}), // 处理未显示写出来的elementUI组件的属性
            }
        },
        // 获取时间类型
        _type () {
            return this._$attrs && this._$attrs.type
        },
        // // 单项数据流原因，不可直接修改值，需要用临时值做过度使用
        tempValue: {
            get () {
                if (!this.value) return this.value
                // 编辑页面处理
                if (!this.isView) return this.changeDataLayout(this.value)
                // 查看页面处理
                let _value = null
                switch (this._type) {
                    case 'datetimerange':
                        _value = this.value
                                .split(GlobalConst.form.valueSeparator)
                                .join(this.rangeSeparator)
                        break
                    case 'datetime':
                    default:
                        // do something default
                        _value = this.value
                }
                return _value
                
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