<template>
    <div class="bd-code-mirror">
        <template v-if="!isView">
            <!-- 操作按钮 -->
            <div class="mar-b">
                <bd-button type="primary" :disabled="!showValue" @click="formatShowValue(showValue)" icon="textFormat" size="mini">格式化</bd-button>
                <bd-button type="primary" :disabled="!showValue" @click="copy" icon="copy" size="mini" >复制代码</bd-button>
            </div>
            <codemirror v-model="showValue" :options="options" ref="codeMirror"></codemirror>
        </template>
        <template v-else>
            组件codemirror查看状态暂不处理
        </template>
    </div>
</template>
<script>
import { codemirror } from "vue-codemirror"
import { Copy_To_Clip } from '@/utils/copy-clip'
const sqlFormatter = require("sql-formatter")  // https://github.com/zeroturnaround/sql-formatter
// codemirror核心样式文件
import "codemirror/lib/codemirror.css"

// 引入主题,配置后生效
import "codemirror/theme/blackboard.css"

//引入语言,配置后生效
// import 'codemirror/mode/javascript/javascript.js'
// import 'codemirror/mode/css/css.js'
// import 'codemirror/mode/xml/xml.js'
// import 'codemirror/mode/clike/clike.js'
// import 'codemirror/mode/markdown/markdown.js'
// import 'codemirror/mode/python/python.js'
// import 'codemirror/mode/r/r.js'
// import 'codemirror/mode/shell/shell.js'
// import 'codemirror/mode/sql/sql.js'
// import 'codemirror/mode/swift/swift.js'
// import 'codemirror/mode/vue/vue.js'

// 开启该样式才能有提示
// import "codemirror/addon/hint/show-hint.css"
// 设置代码提示
// import "codemirror/addon/hint/show-hint"
import "codemirror/addon/hint/sql-hint"
// 设置高亮选中行样式需要引入
import 'codemirror/addon/selection/active-line'

export default {
    name: 'bd-code-mirror',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {
        codemirror,
    },
    props: {
        // 值
        value: {
            type: String
        },
        name: {
            type: String,
            default: ''
        },
        // 是否为查看状态
        isView: {
            type: Boolean,
            default: false
        },
    },
    data() {
        return {
            // 与value值不同，该值作为显示的内容值
            showValue: '',
            // 默认配置
            options: {
                // tabSize: 4, // 缩进格式
                indentUnit: 4,
                smartIndent: false,
                theme: "blackboard", // 指定主题，对应主题库 JS 需要提前引入
                lineNumbers: true, // 是否显示行号
                mode: "sql", // 指定语言类型,如果需要编辑和显示其他语言,需要import语言js然后修改此配置
                line: true,
                styleActiveLine: true, // 高亮选中行
                // readOnly: true, // 是否为只读,如果为"nocursor" 不仅仅为只读 连光标都无法在区域聚焦
                hintOptions: {
                    completeSingle: false, // 当匹配只有一项的时候是否自动补全
                },
                lineWrapping: true, // 换行以排长行
                showCursorWhenSelecting: true, // 选择是否处于活动状态时应绘制光标
            },
        }
    },
    computed: {
        // 获取codemirror组件实例对象
        codemirror () {
            return this.$refs.codeMirror.codemirror
        },
    },
    methods: {
        /**
         * 将传入值-格式化/去格式化
         * @param {String} value 传入需要格式化的值
         * @param {Boolean} formatStatus 是否为正向格式化（为false时表示去格式化）
         */
        format (value, formatStatus = true) {
            if (!value) return ''
            if (formatStatus) {
                // 表示当前为格式化数据
                return sqlFormatter.format(value, { indent: '    ' })
            }
            // 去除换行符与多个空格
            return value.replace(/[\n|\r|\n\r]/g, ' ').replace(/[\s]{2,}/g, ' ')
        },
        // 展示值的格式化
        formatShowValue (val) {
            this.showValue = this.format(val)
        },
        // 复制
        copy () {
            Copy_To_Clip(this.value).then(() => {
                this.$message({
                    message: '复制成功',
                    type: 'success'
                })
            })
        },
        // onCmReady () {
        //     this.codemirror.on('cursorActivity', () => {
        //         this.codemirror.showHint()
        //     })
        // }
    },
    watch: {
        // 监听显示值showValue的改变：实时同步更改value的值
        // showValue的值可能是格式化后的(含大量空格与换行符)，但是value提交时不需要这些多余数据
        // 所以需要showValue对应去除空格与换行符后的值更新给value，用于表单提交
        showValue: {
            handler: function (newVal, oldVal) {
                if (!newVal) return
                // 将展示值showValue去格式化（去除多空格与换行）
                let _value = this.format(newVal, false)
                // 将去除格式化的数据传递父组件
                this.$emit('input', _value)
            }
        }
    },
    mounted () {
        // 初始进入时，将value先进行格式化展示
        this.showValue = this.value ? this.format(this.value) : ''
    }
}
</script>
<style lang='scss' scoped>
// 若显示行号则开启这里的样式，优化处理内容与行号重叠的问题
.bd-code-mirror::v-deep {
    .CodeMirror-code {
        margin-left: -2px;
        .CodeMirror-line {
            padding-left: 30px;
        }
    }
}
</style>

<style lang="scss">
</style>
