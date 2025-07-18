<template>
    <div class="richText" :class="selfClass">
        <template v-if="!isView">
            <disabled-board v-if="disabled"></disabled-board>
            <!-- height属性好像不起作用,目前使用style去处理 -->
            <el-tiptap
                v-model="tempValue"
                :extensions="extensions"
                :height="height"
                :style="{height: _height + 'px'}"
                lang="zh"
                @onPaste="paste">
            </el-tiptap>
        </template>
        <!-- 查看状态 -->
        <template v-else>
            <template v-if="tempValue">
                <bd-icon 
                    :name="iconName" 
                    v-if="iconName" 
                    class="fill icon-small">
                </bd-icon>
                <div class="viewSection" v-html="tempValue"></div>
            </template>
            <!-- 查看无值时 -->
            <template v-else>
                {{GlobalConst.view.value}}
            </template>
        </template>
    </div>
</template>

<script>
import Vue from 'vue'
import GlobalConst from '@/service/global-const'
import DisabledBoard from '@/components/frame/Status/DisabledBoard'
import { Save_RichText_File_URL } from '@/api/frame/index.js'
import 'element-tiptap/lib/index.css'
import codemirror from 'codemirror'; // install 'codemirror' in your own project

import 'codemirror/lib/codemirror.css'; // import base style
import 'codemirror/mode/xml/xml.js'
import {
    ElementTiptap,
    // 需要的 extensions
    Doc,
    Text,
    Paragraph,
    Heading,
    Bold,
    Underline,
    Italic,
    Strike,
    ListItem,
    BulletList,
    OrderedList,
    Link,
    Image,
    CodeBlock,
    Blockquote,
    TodoItem,
    TodoList,
    TextAlign,
    FontSize,
    FontType,
    Fullscreen,
    TextHighlight,
    TextColor,
    FormatClear,
    Table,
    TableHeader,
    TableCell,
    TableRow,
    History,
    TrailingNode,
    HardBreak,
    HorizontalRule,
    LineHeight,
    Indent,
    Iframe,
    Preview,
    CodeView,
} from 'element-tiptap'
// 定义默认最小高度
const MIN_HEIGHT = 200
/**
 * 定义提交图片的Promise函数
 * @param {File} file File文件对象
 */
const UploadImg = function (file) {
    return new Promise((resolve) => {
        Vue.prototype.post(
            // 获取保存图片附件的接口
            Save_RichText_File_URL('uploadimage'),
            // 参数
            {upfile: file}).then(res => {
            // 成功的时候将服务器图片地址返回
            resolve(Vue.prototype.BASEURL + res.url)
        }).catch(() => {
            console.error(`富文本-图片保存异常`)
        })
    })
}
export default {
    name: 'bd-rich-text',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {
        'el-tiptap': ElementTiptap,
        [DisabledBoard.name]: DisabledBoard,
    },
    props: {
        value: {
            type: String,
            default: ''
        },
        // 是否不可编辑
        disabled: {
            type: Boolean,
            default: false
        },
        // 富文本高度值
        height: {
            type: [String, Number],
        },
        // 富文本初始高度
        initHeight: {
            type: [String, Number],
            default: MIN_HEIGHT
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
    computed: {
        // 单项数据流原因，不可直接修改值，需要用临时值做过度使用
        tempValue: {
            get () {
                return this.value
            },
            set (val) {
                this.$emit('input', val)
            }
        },
        // 获取富文本高度
        _height () {
            if (!this.height) return MIN_HEIGHT
            return Math.max(parseInt(this.height), MIN_HEIGHT)
        },
    },
    data () {
        return {
            extensions: [
                new Doc(), // 必须项
                new Text(), // 必须项
                new Paragraph(), // 必须项
                new Heading({ level: 6 }), // 标题
                new Bold({ bubble: true }), // 加粗 bubble: true 在气泡菜单中渲染菜单按钮
                new Underline({ bubble: true, menubar: false }), // 下划线 bubble: true, menubar: false 在气泡菜单而不在菜单栏中渲染菜单按钮
                new Italic({ bubble: true }), // 斜体
                new Strike({ bubble: true }), // 删除线
                new ListItem(), // 使用列表必须项
                new BulletList({ bubble: true }), // 无序列表
                new OrderedList({ bubble: true }), // 有序列表
                new Link(), // 链接
                new Image({
                    /**
                     * 设置选择本地图片后，提交图片转成服务器地址再回显于富文本中
                     * @param {File} file 选择的文件
                     */
                    uploadRequest: UploadImg
                }), // 图片
                new Iframe(),  // 插入视频
                new CodeBlock({ bubble: true }), // 代码块
                new Blockquote(), // 引用
                new TodoItem(), // 任务列表必须项
                new TodoList(), // 任务列表
                new TextAlign({ bubble: true }), // 文本对齐方式
                // 黏贴文本功能的正常使用与这里字体大小调整功能为互斥，需要自行评估取舍
                new FontSize({ 
                    bubble: true
                }), // 字号
                new FontType({ bubble: true }), // 字体
                new TextHighlight({ bubble: true }), // 文本高亮
                new TextColor({ bubble: true }), // 文本颜色
                new FormatClear({ bubble: true }), // 清除格式
                new Table({ resizable: true }), // 表格
                new TableHeader(), // 表格必须项
                new TableCell(), // 表格必须项
                new TableRow(), // 表格必须项
                new History(), // 撤销
                new TrailingNode(), // 重做
                new HardBreak(), // 分割线
                new HorizontalRule(), // 行距
                new LineHeight(), // 增加缩进
                new Indent(), // 减少缩进
                new CodeView({
                    codemirror,
                    codemirrorOptions: {
                        lineNumbers: true,
                        lineWrapping: true,
                        tabSize: 2,
                        tabMode: 'indent',
                        mode: 'text/html',
                    }
                }),  // 查看源代码
                new Preview(),  // 预览
                new Fullscreen(), // 全屏
            ],
            GlobalConst,
        }
    },
    methods: {
        /**
         * 黏贴到富文本中的图片处理（注意：核心处理的是图片）
         */
        paste ({ editor }) {
            if (!event?.clipboardData?.types) return
            // 获取黏贴过来的数据类型与数据
            let { types, files } = event.clipboardData
            // 黏贴过来的资源规则：
            //     若黏贴为本地图片，则types为['Files']
            //     若黏贴为远程资源，则types为['text/html', 'Files']
            //     若黏贴为其他文件, 则types为['text/plain', 'text/html']
            // 综合分析资源types为['Files']为图片，只需判断这个做处理，其他类型放掉，使用默认的黏贴逻辑
            if (JSON.stringify(types) !== '["Files"]') return
            // 定义承诺列表
            let _promiseList = []
            // 使用Array.from格式转化
            Array.from(files).forEach(i => {
                // 添加图片资源提交接口
                _promiseList.push(UploadImg(i))
            })
            Promise.allSettled(_promiseList).then(res => {
                // 图片保存服务器后
                let content = res.reduce((total, item) => {
                    let { status, value } = item
                    if (status === 'fulfilled') {
                        // 使用远程文件服务地址拼接img标签
                        total += `<img src="${value}" width="200"/>`
                    }
                    return total
                }, '')
                // 将元素插入活跃后光标处
                this.innerDOM(content)
            })
        },
        /**
         * 将元素添加到光标活跃处
         * @param {String} content dom元素String格式
         */
        innerDOM (content) {
            let sel = window.getSelection()
            if (!(sel.rangeCount > 0)) return
            const range = sel.getRangeAt(0) // 获取选择范围
            range.deleteContents() // 删除选中的内容
            const el = document.createElement('div') // 创建一个空的div外壳
            el.innerHTML = content // 设置div内容为我们想要插入的内容。
            const frag = document.createDocumentFragment() // 创建一个空白的文档片段，便于之后插入dom树
            const node = el.firstChild
            const lastNode = frag.appendChild(node)
            range.insertNode(frag) // 设置选择范围的内容为插入的内容
            const contentRange = range.cloneRange() // 克隆选区
            contentRange.setStartAfter(lastNode) // 设置光标位置为插入内容的末尾
            contentRange.collapse(true) // 移动光标位置到末尾
            sel.removeAllRanges() // 移出所有选区
            sel.addRange(contentRange) // 添加修改后的选区
        }
    }
}
</script>

<style scoped lang="scss">
.richText::v-deep {
    .el-tiptap-editor--fullscreen {
        z-index: 99999999;
    }
    // 处理表单标题阴影无法遮住富文本的情况 --start
    position: relative;
    // 不能添加z-indx，会导致全屏失效
    // z-index: 0;
    .el-tiptap-editor {
        border-bottom: 1px solid #ebeef5;
        border-radius: 4px;
        overflow: hidden;
        // 屏蔽选中文本时的悬浮工具条
        .el-tiptap-editor__menu-bubble {
            display: none !important;
        }
        .el-tiptap-editor__menu-bar {
            padding: 4px;
            background: #f7f7f7;
            .el-tiptap-editor__command-button {
                $size: 24px;
                width: $size;
                height: $size;
                color: #6d6d6d; // 原有颜色过于抢眼，会显示富文本与其他编辑类型看起来风格不一致
            }
        }
        .el-tiptap-editor__content {
            padding: $padding + 6px $padding;
            // 扩大可使用光标区域
            .ProseMirror {
                height: 100%;
            }
        }
        .el-tiptap-editor__footer {
            display: none;
        }
    }
    // 处理表单标题阴影无法遮住富文本的情况 --end
    .viewSection {
        border: 1px solid $lineColor;
        padding: $padding;
        border-radius: $borderRadius;
        background: $contentInBg;
        p {
            margin: 0;
            padding: 0;
        }
    }
}
</style>