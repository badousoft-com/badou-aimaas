<template>
    <bd-dialog
        class="font"
        :outScope="scope"
        :visible.sync="formDesignerVisible"
        :destroy-on-close="true"
        :fullscreen="true"
        noHeader
        noFooter>
        <div>
            <form-designer
                :title.sync="title"
                :formProps.sync="formProps"
                v-model="data"
                :dicData="dicData"
                :show-close-btn="true"
                :saveFun="saveFun"
                @close="formDesignerVisible = false">
            </form-designer>
        </div>
    </bd-dialog>
</template>
<script>
import Dialog from '@/components/frame/Dialog/index.vue'
import FormDesigner from '@/components/frame/FormDesigner/index.vue'
export default {
    name: 'formDesigner-dialog',
    components: {
        [Dialog.name]: Dialog,
        [FormDesigner.name]: FormDesigner
    },
    props: {
        // 标题
        title: {
            type: String
        },
        // 表单属性
        formProps: {
            type: Object
        },
        // 表单字段数据
        data: {
            type: Array,
            default: () => []
        },
        saveFun: {
            type: Function
        },
        // 关闭事件
        closeEvt: {
            type: Function
        },
        // 弹窗标识
        sign: {
            type: String
        },
        restParams: {
            type: Object
        }
    },
    data () { // 定义页面变量
        return {
            // 表单设计器弹窗展示状态
            formDesignerVisible: true,
            dicData: {}
        }
    },
    computed: {
        // 当前组件作用域
        scope () {
            return this
        },
    },
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {},
    watch: {
        formDesignerVisible: {
            handler: function (newVal, oldVal) {
                // 监听弹窗关闭事件
                if (!newVal) {
                    // 触发弹窗实例清除事件
                    this.closeEvt(this.sign)
                }
            }
        }
    },
}
</script>
<style lang='scss' scoped>
.designerOpener {
    position: absolute !important;
    top: 50%;
    left: 50%;
    transform: translateX(-50%);
}
</style>