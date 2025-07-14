<template>
    <div>
        <el-dialog
            v-drag-dialog="{ hasDrag }"
            :title="_title"
            :visible.sync="dialogVisible"
            :custom-class="_customClass"
            :close-on-click-modal="closeOnClickModal"
            :append-to-body="appendToBody"
            v-setDialogSize = "{ 
                width: width || dialogWidth, 
                height: height || dialogHeight,
                visibleStatus: dialogVisible,
                isAutoFix: isAutoFix,
                fullscreen: temp_fullscreen,
                disabled: hasDrag }"
            @close="handleClose"
            :fullscreen="temp_fullscreen"
            v-bind="$attrs"
            v-on="$listeners">
            <!-- 标题区域插槽 -->
            <template v-slot:title>
                <div class="dialog__header_main">
                    <div class="header-left">
                        <slot name="header-left">
                            <!-- 左侧标题插槽区 -->
                            <slot name="title">
                                <div v-if="_title" class="bd-dialog-title">
                                    <bd-icon :name="icon" class="pillar fill"></bd-icon>
                                    <span>{{_title}}</span>
                                </div>
                            </slot>
                            <slot name="afterTitle"></slot>
                        </slot>
                    </div>
                    <div class="header-center">
                        <slot name="header-center"></slot>
                    </div>
                    <div  class="header-right">
                        <slot name="header-right">
                            <slot name="operate-btn"></slot>
                            <bd-button
                                v-if="!fullscreen && !noFullButton"
                                class="bd-dialog-btn__fullScreen"
                                :icon="!temp_fullscreen?'fullScreen':'outFullScreen'"
                                type="text" 
                                @click="temp_fullscreen = !temp_fullscreen">
                            </bd-button>
                        </slot>
                    </div>
                </div>
            </template>
            <!-- 内容插槽 -->
            <div class="bd-dialog__body" v-loading="loading" @scroll="scrollDialogBody">
                <slot>
                    <div id="diyPage" ref="diyPage"></div>
                </slot>
            </div>
            <!-- 底部插槽 -->
            <template v-slot:footer>
                <slot name="footer" v-if="!noFooter">
                    <bd-button
                        v-for="(i, index) in btnList"
                        :key="index"
                        v-bind="i"
                        @click='exeMethod(i)'>
                    </bd-button>
                </slot>
            </template>
        </el-dialog>
    </div>
</template>
<script>
import Vue from 'vue'
import setDialogSize from '@/directive/set-dialog-size'
import DragDialogMixin from './DragMixin.vue'
import { ShowStatus } from '@/service/module'
import router from '@/router'
import store from '@/store'
export default {
    name: "bd-dialog",
    inheritAttrs: false,
    components: {},
    // 指令
    directives: {
        setDialogSize,
    },
    mixins: [DragDialogMixin],
    props: {
        // 是否显示 Dialog，支持 .sync 修饰符
        visible: {
            type: Boolean,
            default: false
        },
        // Dialog 的标题
        title: {
            type: String
        },
        // Dialog 的自定义类名
        customClass: {
            type: String,
            default: ''
        },
        // 是否可以通过点击 modal 关闭 Dialog
        closeOnClickModal: {
            type: Boolean,
            default: false
        },
        // 自定义页面地址（传入时将会将动态挂载该页面作为内容显示）
        //     默认页面地址组装规则为 src/views + pageUrl + '.vue'
        //     如展示登录页地址为src/views/default/Login.vue, 则pageUrl传入值为'/default/Login'
        pageUrl: {
            type: String
        },
        // 标题栏图标
        icon: {
            type: String,
            default: 'pillar-fill'
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
        // 是否全屏
        fullscreen: {
            type: Boolean,
            default: false
        },
        // 使用当前组件的所在页面作用域
        outScope: {
            type: Object
        },
        // dialog中的操作按钮组
        handlerList: {
            type: Array,
            // default: () => []
        },
        // 即将废弃-dialog中的操作按钮组，请使用handlerList
        btnMethods: {
            type: Array,
            // default: () => []
        },
        // 隐藏取消按钮
        noCancel: {
            type: Boolean,
            default: false
        },
        // 隐藏头部
        noHeader: {
            type: Boolean,
            default: false
        },
        // 隐藏底部
        noFooter: {
            type: Boolean,
            default: false
        },
        // Dialog 自身是否插入至 body 元素上,设置成true，避免弹窗的标题受到父级元素样式影响
        appendToBody: {
            type: Boolean,
            default: true
        },
        // 当前窗口被关闭后执行的函数
        closeFun: {
            type: Function
        },
        // 不显示全屏按钮
        noFullButton: {
            type: Boolean,
            default: false
        },
    },
    data () { // 定义页面变量
        return {
            // 弹窗是否自行拖动过
            hasDrag: false,
            // 取消按钮信息
            cancelObj: {
                id: 'cancel',
                name: '取消'
            },
            // 内容区是否加载状态
            loading: false,
            // 弹窗展示状态
            dialogVisible: false,
            // 是否展示标题阴影
            showTitleShadow: false,
            // 挂载页面内的作用域（若有传入自定义页面挂载时）
            pageScope: null,
            // 是否全屏的变量
            temp_fullscreen: false,
        }
    },
    computed: {
        _title () {
            // 作为一个弹窗，目前的场景没有出现去掉标题的，所以默认都设置一个标题
            return this.title || '操作框'
        },
        // 获取自定义类
        _customClass () {
            // 类与类之间需要留一个空格
            return `bd-dialog-scope
                    bd-dialog
                    ${this.customClass} 
                    ${this.showTitleShadow ? 'has-title-shadow' : ''}
                    ${this.noHeader ? 'no-header': ''}`
        },
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
            let diyButtons = this.handlerList || this.btnMethods
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
        // 获取传递给子组件的属性
        _childAttrs () {
            let {
                visible,
                title,
                customClass,
                closeOnClickModal,
                pageUrl,
                icon,
                dialogWidth,
                width,
                dialogHeight,
                height,
                isAutoFix,
                outScope,
                handlerList,
                btnMethods,
                noFooter,
                // js中默认配置
                parentElClass,
                hideBtn,
                option,
                // 核心返回属性对象
                ...others
            } = this.$options.propsData
            return others || {}
        }
    },
    methods: { // 定义函数
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
        scrollDialogBody (event) {
            this.showTitleShadow = !!event?.target?.scrollTop
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
        // Dialog 关闭的回调
        handleClose () {
            // 判断是否存在取消按钮事件，存在则执行
            this._cancelBtn && this.getClickFun(this._cancelBtn)?.call(this, this._cancelBtn)
        },
        closeDialog () {
            // 2021-06-21变更：需要调用这个方法关闭弹窗，才会销毁元素，不然会不断创建元素没有回收
            // 以下方式废弃
            
            this.dialogVisible = false
            // 弹窗按钮关闭失效，2021-07-16之前是注释的，现在重新再开放出来
            this.$emit('update:visible', false)
            if (!(this.closeFun && typeof this.closeFun === 'function')) return
            // 由于当前的预览是通过添加dom去实现页面的，所以当关闭时需要执行当前逻辑去删除新增的dom元素，避免内存溢出
            this.closeFun()
        },
    },
    // 可访问当前this实例
    created () { },
    // 挂载完成，可访问DOM元素
    mounted () {
        // 设置全屏变量
        this.temp_fullscreen = this.fullscreen
    },
    watch: {
        // 监听路由变化：当跳转页面时关闭弹窗(避免浏览器直接输入地址跳转弹窗依旧存在的bug)
        $route () {
            this.closeDialog()
        },
        visible: {
            immediate: true,
            handler: function (newVal, oldVal) {
                this.dialogVisible = newVal
            }
        },
        // 挂载页面地址监听（是否传入需要挂载在dialog的组件页面）
        pageUrl: {
            immediate: true,
            handler: async function (newVal, oldVal) {
                if (!newVal) return
                // pageUrl数据最前面带不带【/】都支持
                let pageUrl = `/${newVal}`.replace(/\/\//, '/').replace(/\.vue/, '') + '.vue'
                setTimeout(async () => {
                    let pageVue = await require(`@/views${pageUrl}`).default
                    pageVue.router = router
                    pageVue.store = store
                    const Instance = Vue.extend(pageVue)
                    // 创建实例，挂载指定DOM
                    new Instance({
                        // 传递数据给组件
                        propsData: {
                            // 将挂载页面的作用域更新给当前页面变量pageScope,便于使用
                            getScope: (scope) => this.pageScope = scope,
                            // 当前页面作用域
                            scope: this,
                            // 使用弹窗的所在页面作用域
                            outScope: this.outScope,
                            // 传递给子组件的其余属性
                            ...this._childAttrs
                        }
                    }).$mount(`#diyPage`)
                })
            }
        }
    }
}
</script>
<style lang='scss'>
.bd-dialog-scope {
    &.no-header {
        .el-dialog__header {
            display: none;
        }
    }
    .is-fullscreen {
        &.bd-dialog {
            border-radius: 0;
        }
    }
    .el-dialog__header {
        .dialog__header_main {
            display: flex;
            width: calc(100% - 24px);
            .header-left {
                display: flex;
            }
            $_height: 18px;
            .header-center {
                flex: 1;
                padding: 0 $padding;
                height: $_height;
                line-height: $_height;
            }
            .header-right {
                .bd-button {
                    height: $_height;
                    line-height: $_height;
                    &.is-text {
                        color: #fff;
                        padding: 0;
                    }
                }
            }
        }
        
    }    
}
</style>