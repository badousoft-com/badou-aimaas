<script>
import GlobalConst from '@/service/global-const'
export default {
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {},
    props: {
        // 值
        value: {
            type: [String, Date],
        },
        // 是否为查看状态
        isView: {
            type: Boolean,
            default: false
        },
        // 图标名称：用于查看页面展示
        iconName: {
            type: String,
            default: 'date-fill'
        },
        // 标签
        label: {
            type: String
        },

        // 完全只读
        readonly: {
            type: Boolean,
            default: false
        },
        // 禁用
        disabled: {
            type: Boolean,
            default: false
        },
        // 文本框可输入
        editable: {
            type: Boolean,
            default: true
        },
        // 是否显示清除按钮
        clearable: {
            type: Boolean,
            default: true
        },
        // 输入框尺寸
        size: {
            type: String,
            default: 'small'
        },
        // 对齐方式
        align: {
            type: String,
            default: 'left'
        },
        // 下拉框的类名
        popperClass: {
            type: String
        },
        // 当前时间日期选择器特有的选项
        pickerOptions: {
            type: Object,
            default: () => {}
        },
        // 选择范围时的分隔符
        rangeSeparator: {
            type: String,
            default: ' 至 '
        },
        // 可选，选择器打开时默认显示的时间
        defaultValue: {
            // 不一定为Date类型，当为年份的时候就是String了
            type: [Date, String]
        },
        // 原生属性
        name: {
            type: String
        },
        // 自定义头部图标的类名
        prefixIcon: {
            type: String,
            default: 'el-icon-time'
        },
        // 自定义清空图标的类名
        clearIcon: {
            type: String,
            default: 'el-icon-circle-close'
        },
    },
    computed: {
        _$listeners () {
            // 传入的input事件与v-model的事件不同
            // 在事件段的处理中v-model的变更会先修改值数据格式（例如数组转字符串）再传给父级
            // 但input不会做这个处理，所以这里去除input事件，通过v-model去触发input
            let { input, ...listeners } = this.$listeners || {}
            return {
                change: this._change,
                blur: this._blur,
                focus: this._focus,
                ...listeners
            }
        }
    },
    methods: { // 定义函数
        /**
         * 用户确认选定的值时触发
         * @param {*} value 组件绑定值
         */
        _change (value) {
            this.$emit('change', value)
        },
        /**
         * 当 input 失去焦点时触发
         * @param {Object} VueComponent 组件实例
         */
        _blur (VueComponent) {
            this.$emit('blur', VueComponent)
        },
        /**
         * 当 input 获得焦点时触发
         * @param {Object} VueComponent 组件实例
         */
        _focus (VueComponent) {
            this.$emit('focus', VueComponent)
        },
        /**
         * 主动方法，作为中间层调用子组件，方便父组件直接调用focus方法
         */
        focus () {
            this.name && this.$refs[this.name].focus()
        },
        /**
         * 转化数据格式
         * 组件返回的是可能数组格式，在接口提交时不方便，因此创建过渡字段
         * 组件选中值若返回数组，则转成字符串去更新字段value；若传入值含有分隔符，则转成数组
         * 这样，给时间组件确保可以要的数组格式，给接口也能以字符串格式，两全其美
         * @param {String, Array} value 传入值
         */
        changeDataLayout (value) {
            // 若无值则直接返回
            if (!value) return value
            // 定义结果值
            let _value = null
            // 判断传入值类型
            switch (value.constructor) {
                case Array:
                    // 数组则转成字符串
                    _value = value.join(GlobalConst.form.valueSeparator)
                    break
                case String:
                    // 字符串的判断是否含有分割符，有则转成数组
                    if (~value.indexOf(GlobalConst.form.valueSeparator)) {
                        _value = value.split(GlobalConst.form.valueSeparator)
                    } else {
                        _value = value
                    }
                    break
                default:
                    // 啥也不是，啥也不做
                    _value = value
            }
            return _value
        }
    }
}
</script>
<style lang='scss' scoped>

</style>