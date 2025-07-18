// 组件接收值：
//     disabled：Boolean(false)-是否可编辑
//     value: String-值
<template>
    <div :class="selfClass">
        <template v-if="!isView">
            <el-autocomplete
                :disabled="disabled"
                v-model="tempValue"
                :fetch-suggestions="getFetchOption"
                :value-key="valueKey"
                :debounce="debounce"
                :placement="placement"
                :popperClass="popperClass"
                :trigger-on-focus="triggerOnFocus"
                :placeholder="_placeholder"
                @select="handleSelect">
                <template slot-scope="{ item }">
                    <span v-if="item.desc">【{{item.desc}}】</span>
                    <span v-if="item[valueKey]">{{ item[valueKey] }}</span>
                </template>
            </el-autocomplete>
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
import getDicList from '@/service/get-dic'
import { Has_Value, Get_Data_By_Path } from '@/utils'
export default {
    name: 'bd-auto-complete',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {},
    props: {
        disabled: {
            type: Boolean,
            default: false
        },
        value: {
            type: String,
            default: ''
        },
        label: {
            type: String,
            default: GlobalConst.form.label
        },
        // 预输入文本
        placeholder: {
            type: String,
            default: GlobalConst.form.placeholder.text
        },
        // options数据中作为值使用的键名
        valueKey: {
            type: String,
        },
        // 数据格式为｛url: '', params: {}｝
        request: {
            type: Object,
        },
        // 接口数据地址，若optionResPath为'A,B'，则获取的数据为res[A][B]
        optionResPath: {
            type: String,
            default: ''
        },
        // 输入建议数据源
        // options：[{desc}],desc字段会被特殊处理为补充说明展示在页面
        options: {
            type: Array,
            default: () => []
        },
        // 是否在输入框 focus 时显示建议列表
        triggerOnFocus: {
            type: Boolean,
            default: true
        },
        // 根据输入值过滤匹配的选项。关闭后则可选项始终不变
        autoFilter: {
            type: Boolean,
            default: true
        },
        // 获取输入建议的去抖延时
        debounce: {
            type: Number,
            default: 300
        },
        // 菜单弹出位置
        // top / top-start / top-end / bottom / bottom-start / bottom-end
        placement: {
            type: String,
            default: 'bottom-start'
        },
        // 返回输入建议的方法
        fetchSuggestions: {
            type: Function
        },
        // Autocomplete 下拉列表的类名
        popperClass: {
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
            // 临时options数据，用于页面使用，避免改动prop报错
            tempOptions: [],
        }
    },
    computed: {
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
    },
    methods: { // 定义函数
        /**
         * 获取与输入内容的匹配项数组
         * @param {String} queryString 输入值
         * @param {Function} cb 回调函数
         */
        getFetchOption (val, cb) {
            if (this.fetchSuggestions && typeof this.fetchSuggestions === 'function') {
                return this.fetchSuggestions.call(this, val, cb)
            }
            // 若自动过滤为false，则默认返回全部选项，默认按值进行过滤
            if (!this.autoFilter || !Has_Value(val)) {
                cb(this.tempOptions)
                return
            }
            // 按输入值匹配选项值返回
            cb(this.tempOptions.filter(i => {
                let _item = this.valueKey ? i[this.valueKey] : i
                return ~_item.toUpperCase().indexOf(val.toUpperCase())
            }))
        },
        handleSelect (item) {
            // console.log(item)
        },
        /**
         * 根据请求参数设置option数据
         * @param {Object} request 请求数据对象 { url: '', params: {} }
         */
        setOptionByRequest (request) {
            // 定义请求对象
            let _request= {
                params: {},
                method: 'post',
                ...(request || this.request),
            }
            if (!_request.url) {
                console.error(`AutoComplete组件使用request请求，但缺乏request下的url属性`)
                return
            }
            // 2. 常规请求
            getDicList(_request).then(res => {
                let _res = res || []
                // 根据传入的路径获取最终的数组数据
                this.tempOptions = Get_Data_By_Path(_res, this._optionResPath)
            }).catch(err => {
                this.tempOptions = []
                console.error(`AutoComplete组件请求数据字典失败：${err}`)
            }).finally(() => {
            })
        },
        /**
         * 根据option设置option数据
         * @param {Array} option option数据 [{id:'',text:''}]
         */
        setOptionByOption (option) {
            this.tempOptions = (option && option.constructor === Array) ? option : []
        },
        // 初始化设置option数据
        initOption () {
            // 判断是否传入请求参数
            if (this.request) {
                this.setOptionByRequest()
            } else {
                this.setOptionByOption(this.options)
            }
        },
    },
    // 可访问当前this实例
    created () {
        this.initOption()
    },
    // 挂载完成，可访问DOM元素
    mounted () {},
    watch: {
        // 监听请求对象是否存在，存在即使用其请求options数据
        request: {
            deep: true,
            handler: function (newVal, oldVal) {
                this.setOptionByRequest(newVal)
            }
        },
        options: {
            deep: true,
            handler: function (newVal, oldVal) {
                this.setOptionByOption(newVal)
            }
        },
    }
}
</script>
<style lang='scss' scoped>

</style>