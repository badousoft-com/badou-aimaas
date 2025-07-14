// 组件核心逻辑：将代码编辑器与弹窗结合形成组件
// 1. 数组与对象数据需要展示在编辑区前提：需要转成字符串以实现换行排版，但是在提交父级组件之前又需要将字符串转回对象
// 2. 基于1的情况，在使用组件时要避免v-model实时，因为编写对象过程中转的时候会经常失败，会导致不断刷新数据源，光标重新定位
// 3. 基于以上情况，编辑代码组件只在第一次接收传入值，后面编辑内容使用另一个变量存储，不断刷新父级组件中对应变量；父级组件选择保存/关闭/复制时才可以这个最新代码数据
<template>
    <div>
        <div @click="openCodeEditor()">
            <!-- 支持自定义触发标签的编写，作为插槽传入 -->
            <slot>
                <el-button type="primary">代码编辑</el-button>
            </slot> 
        </div>
        <el-dialog
            v-drag-dialog
            :custom-class="dialogSelfClass + ' codeEditDialog'"
            :visible.sync="codeEditorVisible"
            :before-close="handleClose"
            :destroy-on-close="true"
            :close-on-click-modal="false"
            :append-to-body="true">
            <!-- 代码编辑区 -->
            <base-code-editor
                v-if="codeEditorVisible"
                ref="baseCodeEditor"
                v-bind="$attrs"
                :value="tempValue">
            </base-code-editor>
            <!-- 弹窗头部 -->
            <template v-slot:title>
                <span :class="dialogSelfClass">
                    <bd-icon :name="dialogIcon" class="fill"></bd-icon>
                    <span>{{dialogTitle}}</span>
                </span>
            </template>
             <!-- 弹窗底部  -->
            <template v-slot:footer>
                <div class="btn-list">
                    <transition name="fade">
                        <div
                            v-if="isShowErrorTip"
                            class="handlerTip"
                            id="handlerTipEl">
                            {{handlerTip}}
                        </div>
                    </transition>
                    <bd-button
                        v-for="(i, index) in _handerList"
                        v-bind="i"
                        :key="index"
                        :class="i.className"
                        @click="exeMethod(i)">
                        {{i.name}}
                    </bd-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>
<script>
import BaseCodeEditor from './BaseCodeEditor'
import DragDialogMixin from '@/components/frame/Dialog/DragMixin'
export default {
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {
        BaseCodeEditor
    },
    mixins: [DragDialogMixin],
    props: {
        // 值
        value: {
            required: true
        },
        // 是否查看
        isView: {
            type: Boolean,
            default: false
        },
        // 不可编辑
        disabled: {
            type: Boolean,
            default: false
        },
        // 弹窗标题图标
        dialogIcon: {
            type: String,
            default: 'codeEditor-fill'
        },
        // 弹窗标题
        dialogTitle: {
            type: String,
            default: '代码编辑器'
        },
        // 弹窗自定义类
        dialogSelfClass: {
            type: String,
        }
    },
    data () { // 定义页面变量
        return {
            // 是否显示错误提醒
            isShowErrorTip: false,
            // 代码编辑器的展示状态，默认不显示
            codeEditorVisible: false,
            // 代码格式错误提醒
            handlerTip: '操作失败，代码格式错误',
            // 按钮操作列表
            handerList: [
                { name: '复制', icon: 'copy', handler: this.copyCode, type: 'primary', className: 'floatL' },
                { name: '格式化', isView: false, icon: 'copy', handler: this.format, type: 'primary', className: 'floatL'  },
                { name: '保存 & 退出', isView: false, icon: 'save', handler: this.saveCode, type: 'primary' }
            ],
            // 临时值
            tempValue: null,
        }
    },
    computed: {
        // 根据是否处于查看页面决策显示的按钮
        _handerList () {
            return this.handerList.filter(i => !(this.isView && ('isView' in i) && !i.isView))
        }
    },
    methods: { // 定义函数
        // 打开编辑器
        openCodeEditor() {
            if (this.disabled) return
            this.codeEditorVisible = true
        },
        exeMethod ({handler}) {
            // 执行事件函数
            (typeof handler === 'function') && handler()
        },
        // 调用子组件-编辑器的格式化方法
        format () {
            this.$refs.baseCodeEditor.formatVal()
        },
        // 保存编辑区域的代码
        saveCode () {
            // 获取数据的正确性状态 与 数据
            let { codeRightStatus, codeValue } = this.$refs.baseCodeEditor.getCodeOption()
            // 更新当前页面-代码状态值
            this.isShowErrorTip = !codeRightStatus
            if (codeRightStatus) {
                // 将正确的值传递给父级组件
                this.$emit('input', codeValue)
                // 关闭弹窗
                this.codeEditorVisible = false
            } else {
                // 一段事件后清空提示
                setTimeout(() => {
                    this.isShowErrorTip = false
                }, 1200)
            }
        },
        // 复制代码
        copyCode () {
            let codeVal = this.$refs.baseCodeEditor.tempValue
            if (codeVal) {
                // 使用复制板的时候需要注意：若复制数据为对象，需要将对象转化为字符串
                if (typeof codeVal === 'object') {
                    codeVal = JSON.stringify(codeVal)
                }
                if (codeVal.constructor !== String) this.$message.warning('数据格式异常')
                // 复制编辑区数据
                this.copyToClip(codeVal).then(() => {
                    this.$message({
                        message: '复制成功',
                        type: 'success'
                    })
                })
            }
        },
        // 将内容添加到黏贴板
        copyToClip (message) {
            return new Promise((resolve, reject) => {
                try {
                    // 创建input元素，利用其内容进行复制进粘贴板
                    // 创建textarea元素，可以支持复制html时保留换行符；input会默认不保留换行符
                    let copyArea = document.createElement("textarea")
                    // 设置input value属性值
                    // copyArea.setAttribute("value", message)  // input标签使用
                    copyArea.value = message  // textarea标签使用
                    document.body.appendChild(copyArea)
                    // 设置选中input默认值
                    copyArea.select()
                    // 复制选中内容
                    document.execCommand("copy")
                    // 复制成功，删除input元素
                    document.body.removeChild(copyArea)
                    resolve()
                } catch (e) {
                    console.log(`copyToClip函数使用报错：${e}`)
                    reject()
                }
            })
        },

        // 代码编辑弹窗关闭事件： 执行将编辑器的值赋给组件
        // 如果使用实时进行数据更新会导致，写一点数据数据在对象与字符串转化过程会导致光标定位为第一个代码，体验不好
        // 所以为编辑器设置新的变量，在弹窗关闭之后再将编辑器值赋给当前组件
        handleClose (done) {
            // 查看时不需要关注内容是否变更进而提示保存
            if (this.isView) {
                done()
                return
            }
            // 获取数据是否修改过的状态
            let isChange = this.$refs.baseCodeEditor.isChange
            // 若数据没变更，那可以直接关闭
            if (!isChange) {
                done() // 直接关闭
                return
            }
            // 数据变更过，需要提醒是否保存
            this.$confirm('检测到未保存的内容，是否在离开页面前保存修改？', '确认信息', {          
                confirmButtonText: '继续修改',          
                cancelButtonText: '放弃修改',          
                type: 'warning',
            }).then(() => {
                // 继续修改，啥事没有
            }).catch(() => {
                // 关闭提醒文本
                this.isShowErrorTip = false
                // 关闭弹窗
                done() 
            })
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
    },
    watch: {
        // 监听弹窗打开事件
        codeEditorVisible (val) {
            // val:true标识当前为打开状态
            if (val) {
                // 把值赋给当前页面数据变量
                this.tempValue = this.value
            }
        }
    }
}
</script>
<style lang='scss' scoped>
</style>
<style lang="scss">
.codeEditDialog {
    .el-dialog__header {
        padding: 3/2 * $padding $padding;
        .el-dialog__headerbtn {
            top: 3/2 * $padding
        }
    }
    .el-dialog__body {
        padding: 0 $padding $padding;
    }
    .el-dialog__footer {
        position: relative;
        padding: $padding;
        margin-top: -$space;
    }
    .handlerTip {
        font-size: $fontS;
        color: $danger;
        display: inline-block;
        position: absolute;
        height: 20px;
        line-height: 20px;
        top: 0;
        bottom: 0;
        margin: auto;
        left: 50%;
        transform: translateX(-50%);
    }
    .btn-list {
        min-height: $buttonHeight;
    }
}
</style>