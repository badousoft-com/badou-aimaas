// 最核心代码编辑区域: 单纯存储代码编辑器
// ace-editor组件参考：https://www.jianshu.com/p/ef6f43c44bc0
// https://www.npmjs.com/package/vue2-ace-editor

<template>
    <div>
        <ace-editor
            v-model="tempValue"
            @init="editorInit"
            @paste.native="paste"
            :options="editorOptions"
            :lang="lang"
            :theme="theme"
            :width='codeWidth'
            :height="codeHeight">
        </ace-editor>
    </div>
</template>
<script>
let AceEditor = require('vue2-ace-editor')
import { Has_Value } from '@/utils'
export default {
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {
        AceEditor
    },
    props: {
        // 就是该编辑器显示的语言类型
        // 这个类型一定要在init方法中已经定义（比如这里的json类型，就是在require("brace/snippets/json")这句话引入的）
        lang: {
            type: String,
            default: 'json'
        },
        // 主题，表现形式
        theme: {
            type: String,
            default: 'twilight'
            // chrome： 白屏风
        },
        // 配置选项
        options: {
            type: Object,
            default: () => {}
        },
        // 父组件传入值
        value: {
            type: [String, Object, Array],
        },
        // 自定义class
        selfClass: {
            type: String,
            default: ''
        },
        // 编辑区域宽度
        width: {
            type: [Number, String],
            default: '100%'
        },
        // 编辑区域高度
        height: {
            type: [Number, String],
            default: 300
        },
    },
    data () { // 定义页面变量
        return {
            //编辑框的一些配置
            defaultOptions: {
                /*vue2-ace-editor编辑器配置自动补全等*/
                enableBasicAutocompletion: true,
                enableSnippets: true,
                enableLiveAutocompletion: true /*自动补全*/,
                // readOnly: true
            },
            // 当前值
            tempValue: null,
            // 数据正确性
            isCodeRight: true,
            // 标识数据是否变更过，弹窗离开时使用
            isChange: false
        }
    },
    computed: {
        // 代码编辑区宽度
        codeWidth () {
            return this.width
        },
        // 代码编辑区高度
        codeHeight () {
            return parseInt(this.height)
        },
        // 编辑区域配置
        editorOptions () {
            return { ...this.defaultOptions, ...this.options }
        },
    },
    methods: { // 定义函数
    // 编辑器初始化配置
        editorInit () {
            // 黑色主题
            require("brace/theme/twilight")
            // // 代码片段
            require("brace/snippets/json")
            // require("brace/snippets/html")
            // require("brace/snippets/javascript")
            // require("brace/snippets/css")
            // // 代码语言
            require("brace/mode/json")
            // require("brace/mode/html")
            // require("brace/mode/javascript")  //language
            // require("brace/mode/css")
            // // 语言扩展
            // require("brace/ext/language_tools")  //language extension prerequsite...
            
        },
        /**
         * 格式化数据
         * @param {String} _value 编辑器文本值
         */
        formatVal (_value = this.tempValue) {
            // 判断值是否存在
            if (!Has_Value(_value)) {
                this.isCodeRight = true
                return ''
            }
            // 判断代码语种
            switch (this.lang) {
                case 'json':
                    try {
                        let _valueObj = _value
                        // 若传入数据不为对象，则优先转成对象，等下才好添加换行符
                        if (typeof _value !== 'object') {
                            _valueObj = JSON.parse(_value)
                        }
                        switch (typeof _valueObj) {
                            case 'object': // 包含Array与Object
                                // 添加换行符，转字符串展示
                                this.tempValue = JSON.stringify(_valueObj, null, "\t")
                                break
                            default:
                                this.tempValue = _value
                                break
                        }
                        // 代码编辑状态设置为true
                        // 这里使用了try/catch,只要能到到这一步表示在前面转化是成功的，即代码格式正确
                        this.isCodeRight = true
                    } catch (e) {
                        // 转化出现异常
                        this.isCodeRight = false
                        this.tempValue = _value
                    }
                    break
                case 'javascript':
                    this.tempValue = _value
                default:
                    // 例如'html'的情况
                    this.tempValue = _value
                    // do something you like jiu ok
            }
        },
        /**
         * 监听编辑器上的黏贴操作,当直接黏贴时，格式化代码
         */
        paste () {
            // 格式化
            this.formatVal()
        },
        // 获取代码相关配置信息
        getCodeOption () {
            // 先进行格式化，确保最新的数据以及数据的准确状态
            this.formatVal()
            return {
                // 代码格式是否准确
                codeRightStatus: this.isCodeRight,
                // 代码值
                codeValue: this.tempValue
            }
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        // 格式化代码
        this.formatVal(this.value)
        // 加载完数据之后将变更的状态设置为未变更
        this.$nextTick(() => {
            this.isChange = false
        })
    },
    watch: {
        // 监听tempValue值，当修改时更新变更状态值
        tempValue: {
            handler: function (newVal, oldVal) {
                this.isChange = true
            }
        }
    }
}
</script>
<style lang='scss' scoped>

</style>
