<template>
    <div>
        <el-dialog
            v-drag-dialog="{hasDrag}"
            :id="id"
            :custom-class="'bd-dialog ' + (scrollVal !== 0 ? 'has-title-shadow' : '')"
            v-setDialogSize = "{ 
                width: width || dialogWidth, 
                height: height || dialogHeight, 
                visibleStatus:visibleStatus,
                isAutoFix: isAutoFix,
                disabled: hasDrag }"
            @close="handleClose"
            :before-close="beforeCloseDialog"
            :destroy-on-close = "true"
            :close-on-click-modal="false"
            :visible.sync="visibleStatus">
            <div
                v-if="title"
                slot="title"
                class="bd-dialog-title">
                <bd-icon name="pillar-fill" class="pillar fill"></bd-icon>
                <span>{{title}}</span>
            </div>
            <!-- <el-scrollbar
                wrap-class="scrollbar-wrapper"
                class="bd-dialog__body"> -->
            <div class="bd-dialog__body"  @scroll="scrollDialogBody">
                <m-dialog-item
                    class="h-per-100"
                    v-if="dialogItem"
                    v-bind="dialogItem"
                    noTitle
                    :ref="id">
                </m-dialog-item>
            </div>
            <div
                slot="footer"
                v-if="btnList.length !== 0">
                <!-- isLoad：状态表示该按钮项存在loading状态，点击时会启用 -->
                <bd-button
                    v-for="(i, index) in btnList"
                    :key="index"
                    v-bind="i"
                    @click='exeMethod(i)'>
                </bd-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import GlobalConst from '@/service/global-const'
import MForm from '@/components/frame/Common/MForm/index.vue'
import MDialogItem from '@/components/frame/Common/MDialog/components/MDialogItem'
import iconHandler from '@/components/frame/Icon/index.js'
import setDialogSize from '@/directive/set-dialog-size'
import DragDialogMixin from '@/components/frame/Dialog/DragMixin'
import { ShowStatus } from '@/service/module'

export default {
    inheritAttrs: false,
    components: {
        MForm,
        MDialogItem
    },
    directives: {
        setDialogSize
    },
    mixins: [DragDialogMixin],
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        // // dialog与内容块的唯一标识，建议必须传递，以防存在多个时异常
        id: {
            type: String,
            default: 'dialogContentId'
        },
        // dialog标题
        title: {
            type: String,
            default: GlobalConst.dialog.title
        },
        // 弹窗宽度-即将废弃
        dialogWidth: {
            type: [String, Number],
            default: 0
        },
        // 弹窗宽度-推荐使用
        width: {
            type: [String, Number],
            default: 0
        },
        // 弹窗高度
        dialogHeight: {
            type: [String, Number],
            default: 0
        },
        height: {
            type: [String, Number],
            default: 0
        },
        // 是否根据内容自动撑高度
        isAutoFix: {
            type: Boolean,
            default: false
        },
        // 即将废弃-dialog中的操作按钮组，请使用handlerList
        btnMethods: {
            type: Array,
        },
        // dialog中的操作按钮组
        handlerList: {
            type: Array,
        },
        // 父组件传递给当前组件的所有属性对象(这里的父级指通过js触发弹窗的父级)
        option: {
            type: Object
        },
        // 当前窗口被关闭后执行的函数
        closeFun: {
            type: Function
        },
        // 隐藏取消按钮
        noCancel: {
            type: Boolean,
            default: false
        },
        // 弹窗状态发生更改执行的函数
        visibleChange: {
            type: Function,
        }
    },
    data () {
        return {
            // 弹窗是否自行拖动过
            hasDrag: false,
            // 弹窗展示状态
            visibleStatus: false,
            // 表单滚动值
            scrollVal: 0,
            // 取消按钮信息
            cancelObj: {
                id: 'cancel',
                name: '取消'
            }
        }
    },
    computed: {
        // 获取弹窗操作按钮组
        // TODO:兼容btnMethods参数，后续版本中将删除
        btnList () {
            // 默认显示取消按钮：用于关闭弹窗
            let cancelBtn = {
                ...this.cancelObj,
                icon: 'cancel',
                type: 'danger',
                click: this.closeDialog
            }
            // 定义返回的结果集
            let result = []
            // 获取传入的自定义按钮事件（兼容处理旧版:相同功能的其他键名）
            let diyButtons = this.handlerList || this.btnMethods || []
            // 传入的自定义按钮数据 > 0
            if (diyButtons && diyButtons.length > 0) {
                // 获取传入的自定义按钮中是否有取消cancel事件，返回其所在下角标
                let cancelIndex = diyButtons.findIndex(i => this.isCancelBtn(i))
                if (~cancelIndex) {
                    // 传入了自定义取消cancel事件，直接使用整个传入的自定义按钮作为弹窗的按钮
                    result = diyButtons
                } else {
                    // 传入按钮不含取消事件，则组装当前默认的取消事件添加进去
                    result = !this.noCancel ? [cancelBtn, ...diyButtons] : diyButtons
                }
            } else {
                // 没有传入自定义按钮，直接使用默认的取消按钮
                result = !this.noCancel ? [cancelBtn] : []
            }
            // 按钮合并,过滤不显示的按钮
            return result?.filter(i => ShowStatus(i))
        },
        // 获取取消按钮对象
        _cancelBtn () {
            return this.btnList?.find(i => this.isCancelBtn(i))
        },
        // 获取弹窗中内容需要的属性，用于传入内容组件
        dialogItem () {
            // 当前组件属性来源两个：
            // 1. 通过js触发的弹窗，属性值来源 this.option
            // 2. 组件引入的弹窗，属性值来源 $attrs + $props
            let contentAttr = {
                ...this.option,
                ...this.$attrs,
                ...this.$props,
                ...(this.$options?.propsData || {})
            }
            if (contentAttr) {
                let {
                    width, 
                    dialogWidth,
                    height,
                    dialogHeight,
                    isAutoFix, 
                    handlerList,
                    btnMethods,
                    ...dialogItem
                } = contentAttr
                return dialogItem
            }
        },
        /**
         * @description: 匹配通用按钮图标
         * @param {Object} btn 按钮
         */
        comp_codeFromBtn () {
            return function (btn) {
                return iconHandler.codeFromBtn(btn)
            }
        }
    },
    methods: {
        /**
         * 是否为取消按钮
         * @param {Object} btnObj 按钮对象
         */
        isCancelBtn (btnObj) {
            if (!btnObj) return
            let { id, name } = btnObj || {}
            // 没有id或name属性，则默认不是取消按钮
            if (!(id || name)) return
            return (id === this.cancelObj.id) || (name === this.cancelObj.name)
        },
        // 模块滚动事件监听
        scrollDialogBody (event) {
            this.scrollVal = event.target.scrollTop || 0
        },
        /**
         * 获取按钮点击事件对象
         * @param {Object} itemObj 按钮对象
         */
        getClickFun (itemObj) {
            if (!itemObj) return
            return itemObj.click || itemObj.handler || itemObj.method || itemObj.handle
        },
        exeMethod (itemObj) {
            // 绑定事件中的this为当前页面作用域
            // 另外将按钮对象itemObj扔回作为绑定事件的参数
            // 建议使用click，handle即将废弃
            let clickEvent = this.getClickFun(itemObj)
            clickEvent && clickEvent.call(this, itemObj)
        },
        handleClose (done) {
            // 判断是否存在取消按钮事件，存在则执行
            this._cancelBtn && this.getClickFun(this._cancelBtn)?.call(this, this._cancelBtn)
        },
        // 弹窗关闭前事件
        beforeCloseDialog (done) {
            // do something you like
            // and then
            done()
        },
        closeDialog () {
            // 2021-06-21变更：需要调用这个方法关闭弹窗，才会销毁元素，不然会不断创建元素没有回收
            // 以下方式废弃
            this.visibleStatus = false
            // 弹窗按钮关闭失效，2021-07-16之前是注释的，现在重新再开放出来
            this.$emit('update:visible', false)
            if (!(this.closeFun && typeof this.closeFun === 'function')) return
            // 由于当前的预览是通过添加dom去实现页面的，所以当关闭时需要执行当前逻辑去删除新增的dom元素，避免内存溢出
            this.closeFun()
        },
    },
    mounted () {
    },
    watch: {
        visible: {
            immediate: true,
            handler: function (newVal, oldVal) {
                this.visibleStatus = newVal
                if (typeof this.visibleChange === 'function') {
                    this.visibleChange.call(this, newVal)
                }
            }
        },
        // 监听路由变化：当跳转页面时关闭弹窗
        $route: {
            handler (newVal, oldVal) {
                this.$emit('update:visible', false)
                this.visibleStatus = false
            }
        }
    },
}
</script>

<style lang="scss" scoped>
</style>
// dialog生成的页面超越组件当前，只能写在非scoped中控制
<style lang="scss">
// .bd-dialog {
//     &.has-title-shadow {
//         .el-dialog__header {
//             box-shadow: 0px 0px 6px 0px rgba(16, 16, 16, 0.1) inset;
//             // 出现阴影时隐藏边框线
//             border-bottom: 0;
//         }
//     }
// }
</style>

