<template>
    <!-- 图片预览区 -->
    <preview
        v-if="_isImg"
        :value="_url"
        ref="img"
        @close="closePreview">
    </preview>
    <!-- pdf预览区 -->
    <bd-dialog
        v-else-if="_isPDF"
        class="font"
        :outScope="scope"
        :showCloseBtn="true"
        :visible.sync="pdfVisible"
        :destroy-on-close="true"
        :title="name || 'PDF预览'"
        :close-on-click-modal="true"
        :width="dialogSize"
        :height="dialogSize"
        noFooter>
        <bd-pdf :src="_url"></bd-pdf>
    </bd-dialog>
    <!-- 其他附件预览区 -->
    <bd-dialog
        v-else
        :showCloseBtn="true"
        :destroy-on-close="true"
        :loading="loading"
        :visible.sync="othersVisible"
        :title="name"
        :close-on-click-modal="true"
        :width="dialogSize"
        :height="dialogSize"
        noFooter>
        <bd-common-attach :src="_url" :name="name"></bd-common-attach>
    </bd-dialog>
</template>
<script>
import Preview from '@/components/frame/Common/MForm/components/items/File/ImagePicker/Preview.vue'
import Dialog from '@/components/frame/Dialog/index.vue'
import CommonAttach from './items/CommonAttach'
import PDF from './items/PDF'

const SIGN = {
    IMG: 'img',
    IMGS: 'imgs',
    PDF: 'pdf'
}
export default {
    name: 'bd-preview',
    components: {
        [Dialog.name]: Dialog,
        [PDF.name]: PDF,
        [CommonAttach.name]: CommonAttach,
        Preview,
    },
    props: {
        // 附件类型：img/imgs/pdf/others
        type: {
            type: String
        },
        // 附件地址
        url: {
            type: [String, Array]
        },
        // 附件名
        name: {
            type: String
        },
        // 图片数组专用，标识当前展示照片的下角标，默认0即第一张
        index: {
            type: [Number, String],
            default: 0
        },
        // 当前窗口被关闭后执行的函数
        closeFun: {
            type: Function
        }
    },
    data () { // 定义页面变量
        return {
            // 弹窗尺寸
            dialogSize: '90%',
            // pdf弹窗的可视变量
            pdfVisible: false,
            // 其他附件类型弹窗的可视变量
            othersVisible: false,
        }
    },
    computed: {
        // 当前组件作用域
        scope () {
            return this
        },
        // 是否图片
        _isImg () {
            return [SIGN.IMG, SIGN.IMGS].includes(this.type)
        },
        // 是否pdf
        _isPDF () {
            return this.type === SIGN.PDF
        },
        _url () {
            let _res = null
            switch (this.type) {  
                case 'img':
                    _res = [{url: this.url}]
                    break
                case 'imgs':
                    _res = this.url || []
                    break
                case 'pdf':
                    _res = this.url
                    // 设置pdf弹窗显示
                    this.pdfVisible = true
                    break
                // 处理其他附件类型
                default:
                    _res = this.url
                    // 设置其他附件类型弹窗的显示
                    this.othersVisible = true
            }
            return _res
        },
    },
    methods: { // 定义函数
        // 执行关闭函数
        exeCloseFun () {
            if (!(this.closeFun && typeof this.closeFun === 'function')) return
            // 由于当前的预览是通过添加dom去实现页面的，所以当关闭时需要执行当前逻辑去删除新增的dom元素，避免内存溢出
            this.closeFun()
        },
        // 执行关闭预览函数
        closePreview () {
            this.exeCloseFun()
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        if (this._isImg) {
            this.$refs.img.showPreview(this.index)
        }
    },
    watch: {
        // 监听pdf关闭时回调
        pdfVisible (val) {
            if (!val) {
                this.exeCloseFun()
            }
        },
        // 监听其他附件弹窗的关闭回调
        othersVisible (val) {
            if (!val) {
                this.exeCloseFun()
            }
        },
    }
}
</script>
<style lang='scss' scoped>

</style>