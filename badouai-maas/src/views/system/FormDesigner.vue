<template>
    <div class="">
        <bd-button
            @click="formDesignerVisible = true"
            type="primary"
            class="designerOpener"
            size="large">
            启动表单设计器
        </bd-button>
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
                    :formProps.sync="formProps"
                    v-model="data"
                    :dicData="dicData"
                    :show-close-btn="true"
                    @close="formDesignerVisible = false">
                </form-designer>
            </div>
        </bd-dialog>
    </div>
</template>
<script>
import Dialog from '@/components/frame/Dialog/index.vue'
import FormDesigner from '@/components/frame/FormDesigner/index.vue'
import { Dic_Data } from '@/service/mockFormDesignerData.js'
export default {
    name: '',
    components: {
        [Dialog.name]: Dialog,
        [FormDesigner.name]: FormDesigner
    },
    data () { // 定义页面变量
        return {
            formProps: {
                title: '测试-表单设计器',
                labelWidth: 110,
                columnNum: 2
            },
            data: [],
            // data: [
            //     {
            //         'groupName': '基本信息',
            //         'groupIcon': 'point-fill',
            //         'type': 'text',
            //         'name': 'text_1649643351418',
            //         'label': '测试单行文本',
            //         'hidden': false,
            //         'disabled': false,
            //         'destroyed': false,
            //         'value': '123',
            //         'placeholder': '',
            //         'useTip': '',
            //         'columnPer': 0,
            //         'isOneLine': false,
            //         'isBlock': false,
            //         'isShowAllLabel': false,
            //         'rules': '',
            //         'selfClass': '',
            //         'isPassword': false,
            //         'prepend': '',
            //         'append': '',
            //         'customOption': ''
            //     }, {
            //         'groupName': '的我季节',
            //         'groupIcon': 'codeEditor-fill',
            //         'type': 'radio',
            //         'name': 'radio_1649643451492',
            //         'label': '单选框',
            //         'hidden': false,
            //         'disabled': false,
            //         'destroyed': false,
            //         'value': '',
            //         'placeholder': '',
            //         'useTip': '',
            //         'columnPer': null,
            //         'isOneLine': true,
            //         'isBlock': false,
            //         'isShowAllLabel': false,
            //         'rules': '',
            //         'selfClass': '',
            //         'dicName': 'ill_reason',
            //         'options': [
            //             { 'id': '1', 'text': '感冒' },
            //             { 'id': '2', 'text': '上呼吸道感染' },
            //             { 'id': '3', 'text': '耳鼻喉病' },
            //             { 'id': '4', 'text': '气管炎/肺炎' },
            //             { 'id': '5', 'text': '胃肠道疾病' },
            //             { 'id': '6', 'text': '泌尿系疾病' },
            //             { 'id': '7', 'text': '神经衰弱' },
            //             { 'id': '8', 'text': '伤害' },
            //             { 'id': '9', 'text': '牙病' },
            //             { 'id': '10', 'text': '眼病' },
            //             { 'id': '11', 'text': '心脏病' },
            //             { 'id': '12', 'text': '原因不明' }
            //         ],
            //         'customOption': ''
            //     }, {
            //         'groupName': '的我季节',
            //         'groupIcon': 'codeEditor-fill',
            //         'type': 'text',
            //         'name': 'text_1649643459602',
            //         'label': '测试单行文本2',
            //         'hidden': false,
            //         'disabled': false,
            //         'destroyed': false,
            //         'value': '',
            //         'placeholder': '',
            //         'useTip': '',
            //         'columnPer': null,
            //         'isOneLine': false,
            //         'isBlock': false,
            //         'isShowAllLabel': false,
            //         'rules': '',
            //         'selfClass': '',
            //         'isPassword': false,
            //         'prepend': '',
            //         'append': '',
            //         'customOption': ''
            //     }
            // ],
            // 表单设计器弹窗展示状态
            formDesignerVisible: true,
            dicData: Dic_Data
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
    mounted () {}
}
</script>
<style lang='scss' scoped>
.designerOpener {
    position: absolute !important;
    top: 50%;
    left: 50%;
    transform: translateX(-50%)
}
</style>