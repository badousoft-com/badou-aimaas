<template>
    <div :class="selfClass">
        <component
            :is="_pickerType + 'Picker'"
            :format="showFormat"
            :label="label"
            v-bind="_$attrs"
            v-on="$listeners">
        </component>
    </div>
</template>
<script>
import DatePicker from './items/Date'
import TimePicker from './items/Time'
import DateTimePicker from './items/DateTime'
import GlobalConst from '@/service/global-const'
export default {
    name: 'bd-date',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {
        [TimePicker.name]: TimePicker,
        [DatePicker.name]: DatePicker,
        [DateTimePicker.name]: DateTimePicker
    },
    props: {
        // 展示格式化
        showFormat: {
            type: String
        },
        // 标签
        label: {
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
        }
    },
    computed: {
        // 获取日期类型
        _type () {
            return this.$attrs.dateType || ''
        },
        // 获取预输入文本
        _placeholder () {
            // 定义获取传入的预输入文本值
            let placeholder = this.$attrs.placeholder
            // 若存在则使用该值返回
            if (placeholder) return placeholder
            // 若标签存在，则使用标签组装预输入文本然后返回该值
            if (this.label)return GlobalConst.form.placeholder.text + this.label
            // 啥也不是返回空
            return null
        },
        // 根据日期类型，返回使用的日期组件
        _pickerType () {
            let pickerType = null
            switch (this._type) {
                case 'datetime':
                case 'datetimerange':
                    pickerType = 'dateTime'
                    break
                case 'time':
                case 'timeSelect':
                    pickerType = 'time'
                    break
                case 'date':
                case 'week':
                case 'month':
                case 'year':
                case 'dates':
                case 'daterange':
                case 'monthrange':
                default:
                    pickerType = 'date'
            }
            return pickerType
        },
        // 获取组件属性
        // 传入的组件属性中
        //     type: 与其他表单组件区分开的属性，统一为date
        //     pickerType: 时间组件类型，可选date/time/dateTime，默认为date
        //     dateType: 同一时间组件下的不同类型，具体可选值有各自的时间组件决策
        // 在实际使用中组件最终接收的是type，则需要将传入type干掉，然后dataType赋值给type,再给具体时间组件
        _$attrs () {
            // 去除type属性,placeholder属性
            let { type, placeholder, ...attrs } = this.$attrs
            // 判断是否指定某时间组件下的类型，含有则设置type
            if (this._type) attrs.type = this._type
            // 判断placeholder是否有效，有效则更新
            if (this._placeholder) attrs.placeholder = this._placeholder
            // 不含type直接返回
            return attrs
        },
        
    },
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        
    }
}
</script>
<style lang='scss' scoped>

</style>