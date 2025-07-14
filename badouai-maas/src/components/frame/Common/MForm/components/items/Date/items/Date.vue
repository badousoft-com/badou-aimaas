<template>
    <div>
        <template v-if="!isView">
            <el-date-picker
                :ref="name"
                v-model="tempValue"
                v-bind="_$attrs"
                v-on="_$listeners">
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
import dayjs from 'dayjs'
require('dayjs/locale/zh-cn') // 可将周开始设置为星期一
export default {
    name: 'datePicker',
    mixins: [DateAttr],
    components: {},
    props: {
        // 非范围选择时的输入占位内容
        placeholder: {
            type: String
        },
        // 范围选择时开始日期的占位内容
        startPlaceholder: {
            type: String
        },
        // 范围选择时结束日期的占位内容
        endPlaceholder: {
            type: String
        },
        // 显示类型
        // 当前类型下可选：date/week/month/year/dates/daterange/monthrange
        type: {
            type: String,
            default: 'date'
        },
        // 显示在输入框中的格式
        format: {
            type: String
        },
        // 绑定值的格式。不指定则绑定值为 Date 对象
        valueFormat: {
            default: String,
        },
        // 范围选择时选中日期所使用的当日内具体时刻
        // 字符串数组：数组，长度为 2，每项值为字符串，形如12:00:00，第一项指定开始日期的时刻，第二项指定结束日期的时刻，不指定会使用时刻 00:00:00
        defaultTime: {
            type: Array
        },
        // 在范围选择器里取消两个日期面板之间的联动
        unlinkPanels: {
            type: Boolean,
            default: false
        },
        // 输入时是否触发表单的校验
        validateEvent: {
            type: Boolean,
            default: true
        }
    },
    data () {
        return {
        }
    },
    computed: {
        // 获取周日期
        weekDate () {
            if (!(this.value && this.type === 'week')) return {}
            let _value = dayjs(this.value).locale('zh-cn')
            // 组装周的开始与结束日期，用于展示
            return {
                start: _value.startOf('week').format('YYYY-MM-DD'),
                end: _value.endOf('week').format('YYYY-MM-DD')
            }
        },
        // 更新组件属性
        _attrObj () {
            let showFormat = null
            let valueFormat = null
            let placeholder = '请选择日期'
            let startPlaceholder = '开始日期'
            let endPlaceholder = '结束日期'
            switch (this.type) {
                case 'week':
                    showFormat = `${this.weekDate.start} 至 ${this.weekDate.end}`
                    valueFormat = 'yyyy-MM-dd'
                    placeholder = '请选择周'
                    break
                case 'month':
                    showFormat = 'yyyy年M月'
                    valueFormat = 'yyyy-MM'
                    placeholder = '请选择月'
                    break
                case 'year':
                    showFormat = 'yyyy年'
                    valueFormat = 'yyyy'
                    placeholder = '请选择年'
                    break
                case 'dates':
                    placeholder = '请选择日期'
                    valueFormat = 'yyyy-MM-dd'
                    break
                case 'daterange':
                    placeholder = '请选择日期'
                    valueFormat = 'yyyy-MM-dd'
                    break
                case 'monthrange':
                    placeholder = '请选择月份'
                    valueFormat = 'yyyy-MM'
                    startPlaceholder = '开始月份'
                    endPlaceholder = '结束月份'
                    break
                case 'date':
                default:
                    // default
                    showFormat = 'yyyy-MM-dd'
                    valueFormat = 'yyyy-MM-dd'
            }
            // 同步考虑传入的prop属性，并以其【优先级高】进行逻辑处理
            return {
                format: this.format || showFormat,
                valueFormat: this.valueFormat || valueFormat,
                placeholder: this.placeholder || placeholder,
                startPlaceholder: this.startPlaceholder || startPlaceholder,
                endPlaceholder: this.endPlaceholder || endPlaceholder
            }
        },
        _$attrs () {
            let _res = {
                ...(this.$attrs || {}), // 处理未显示写出来的elementUI组件的属性
                ...(this.$props || {}),
                // 这个放置最后意味着获取this._attrObj 时必须考虑this.$props中的属性值以返回最终确定值
                // this._attrObj不能放置在this.$props之前，因为某些默认配置会被props中属性覆盖
                ...this._attrObj 
            }
            if (this.type === 'week') {
                // 设置周以星期一开始
                _res.pickerOptions = Object.assign({}, { firstDayOfWeek: 1}, _res.pickerOptions)
            }
            return _res
        },
        // // 单项数据流原因，不可直接修改值，需要用临时值做过度使用
        tempValue: {
            get () {
                if (!this.value) return this.value
                // 编辑页面处理
                if (!this.isView) {
                    if (this.type === 'week') {
                        // 组件使用值为实际值+1天，设置如此才能正常回显
                        return this.addDays(this.value, 1)
                    }
                    return this.changeDataLayout(this.value)
                }
                // 查看页面处理
                let _value = null
                switch (this.type) {
                    case 'week':
                        // 为周时，查看页面显示周的起始日-到-周的结束日
                        _value = `${this.value} 至 ${this.addDays(this.value, 6)}`
                        break
                    case 'month':
                        _value = `${this.value}月`
                        break
                    case 'year':
                        _value = `${this.value}年`
                        break
                    case 'dates':
                        _value = this.value
                                .split(GlobalConst.form.valueSeparator)
                                .join(`${GlobalConst.form.showSeparator} `)
                        break
                    case 'daterange':
                    case 'monthrange':
                        _value = this.value
                                .split(GlobalConst.form.valueSeparator)
                                .join(this.rangeSeparator)
                        break
                    case 'date':
                    default:
                        // do something default
                        _value = this.value
                }
                return _value
            },
            set (val) {
                // 当前为周时，获取到的日期比周的起始多一天，需要减去1天才是周的起始日
                this.type === 'week' && (val = this.addDays(val, -1))
                // 更新组件值
                this.$emit('input', this.changeDataLayout(val))
            }
        }
    },
    methods: {
        /**
         * 从指定日期开始，获取几天后的日期
         * @param {Date} date 指定基础日期
         * @param {Number} num 间隔天数
         * @returns {Date} 指定天数后的日期
         */
        addDays (date, num) {
            if (!date) return
            return dayjs(date).add(num, 'day').format('YYYY-MM-DD')
        },
    },
    mounted () {
    }
}
</script>
<style lang='scss' scoped>

</style>