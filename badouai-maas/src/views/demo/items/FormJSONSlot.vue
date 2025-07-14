<template>
    <div>
        动态json结合插槽实现复杂模块
        <el-button type="primary" @click="$router.go(-1)">返回demo目录</el-button><br>
        -------------------------------------------------------------------------<br/>
        <m-form
            ref="formHello"
            id="formHello"
            title="表单标题"
            labelWidth="110px"
            :columnNum="3"
            :dataList="formHelloList">
            <!-- sign：使用了el-table -->
            <el-table
                slot="slot1"
                ref="multipleTable"
                :data="formHelloList[0].value"
                tooltip-effect="dark"
                style="width: 100%">
                <el-table-column
                    type="selection"
                    width="55">
                </el-table-column>
                <el-table-column
                    label="日期">
                    <template slot-scope="scope">
                        <el-input v-model="scope.row.date">
                        </el-input>
                    </template>
                </el-table-column>
            </el-table>
        </m-form>
        <div class="fixBottomBtn">
            <el-button 
                v-for='(i, index) in opBtnList' 
                :key="index"  
                v-btnBg="i"
                type="primary"
                @click='exeMethod(i.method)'>
                <bd-icon :name="i.icon"></bd-icon>
                {{ i.name }}
            </el-button>
        </div>
    </div>
</template>

<script>
import MForm from '@/components/frame/Common/MForm/index.vue'

export default {
    components: {
        MForm,
    },
    data() {
        return {
            /**
             * 表单子项对象属性说明
             * type: 类型，默认text
             * disabled: true-不可编辑，默认false
             * label: 标签名称
             * name: 字段名
             * value: 字段值
             * placeholder: 预输入文本
             * columnPer: 内容占比（一行24，默认占比6(4列)，支持选择：1,2,3,4,6,8,12,24），其余数组特殊定制
             * isPassword: 是否密码框 默认false
             * isBlock: label与输入域是否各自单独一行  默认-falsse
             * rules：校验规则,Array类型【检验规则有两种，不必填但是填就需要满足规则；必填】
             *        rules数组规则中只要含有require:true,页面就会展示必填星号
             * maxlength: 最长输入字数，填写展示的时候会在input输入框显示字数范围提醒
             * prepend/append: 作为input中的前置或后置内容，例如单位
             * options: radio(作为选项传入数据源)、select等
             * isOneLine：是否独自一行，注意这里只限制排版，不限制内容，也就是设置独自一行，但是内容块所占比例由内容占比决定columnPer
             *            例如姓名输入模块，设置可以单独一行，但是内容我可以设置只占6等分（6/24），不会铺满并且这一行不会再有其他模块
             */
            formHelloList: [
                { type: 'slot', status: 1, label: '插槽1', name: 'slot1', value: [
                    { date: '2016-05-03'}, 
                    { date: '2016-05-02'}], columnPer: 24, isOneLine: true},
                { type: 'text', label: '普通标签', name: 'common', value: '1222', placeholder: '请输入' },
                { type: 'text', label: '占比变宽标签', name: 'largePer', value: '12', placeholder: '请输入', columnPer: 16 },
                { type: 'hidden', label: '隐藏元素', name: 'hidden', value: '1222' },
                { type: 'text', label: '密码', name: 'password', value: null, placeholder: '请输入密码', isPassword: true },
                { type: 'text', disabled: true, label: '不可编辑', name: 'noEdit', value: '12', placeholder: '请输入' },
                { type: 'text', label: '字数限制', name: 'numLint', value: '12', placeholder: '请输入', maxlength: 10 },
                { type: 'text', label: '前置元素', name: 'frontEl', value: '12', placeholder: '请输入', prepend: 'Http://' },
                { type: 'text', label: '后置元素', name: 'endEl', value: '12', placeholder: '请输入', append: '.com' },
                { type: 'text', label: '必填标签', name: 'requireEl', value: '12', placeholder: '请输入', rules: [
                    { required: true, message: '请输入', trigger: 'blur' },
                ] },
                { type: 'text', label: '非必填检验', name: 'noReVaEl', value: null, placeholder: '请输入邮箱', rules: [
                    { type: 'email', message: '请输入邮箱', trigger: 'blur' },
                ] },
                { type: 'text', label: '必填带校验', name: 'reVaEl', value: '1065502552@qq.com', placeholder: '请输入邮箱', rules: [
                    { required: true, message: '必填项', trigger: 'blur' },
                    { type: 'email', message: '请输入邮箱', trigger: 'blur' }
                ] }, 
                { type: 'textarea', label: '文本域', name: 'textarea', value: '12', placeholder: '请输入', maxlength: 40 },
                { type: 'radio', status: 1, label: '单选框', name: 'radio', value: '1', options: [
                    { label: '单选1', value: '1' },
                    { label: '单选2', value: '2' },
                    { label: '单选3', value: '3' },
                ], columnPer: 12},
                { type: 'checkbox', label: '复选框', name: 'checkbox', value: ['1'], options: [
                    { label: '复选1', value: '1' },
                    { label: '复选2', value: '2' },
                    { label: '复选3', value: '3' },
                ], columnPer: 12},
                { type: 'select', label: '下拉单选', name: 'select', value: '1', placeholder: '请选择xxx', options: [
                    { label: '下拉1', value: '1' },
                    { label: '下拉2', value: '2' },
                    { label: '下拉3', value: '3' },
                ] },
                { type: 'select', label: '下拉单选-请求对象', name: 'selectRequest', value: '', placeholder: '请选择', request: {
                    url: '/common/commoninterface/listDicJSON', 
                    params: {
                        dicCode: 'model_manage_type'
                    }
                }},
                { type: 'select', multiple: true, label: '下拉多选', name: 'multiSelect', value: [], placeholder: '请选择多个', options: [
                    { value: '1', label: '黄金糕'},
                    { value: '2', label: '双皮奶'},
                    { value: '3', label: '蚵仔煎'},
                    { value: '4', label: '龙须面'},
                    { value: '5', label: '北京烤鸭'}
                ]},
                { type: 'select', multiple: true, label: '下拉多选-请求对象', name: 'multiSelectRequest', value: [], placeholder: '请选择多个', request: {
                    url: '/common/commoninterface/listDicJSON', 
                    params: {
                        dicCode: 'model_manage_type'
                    }
                }},
                { type: 'date', label: '年月日-日期框', name: 'date1', value: null, placeholder: '请选择日期', columnPer: 8, dateType: 'date', showFormat: 'yyyy年MM月dd日', valueFormat: 'yyyy-MM-dd'},
                { type: 'date', label: '年-日期框', name: 'date2', value: null, placeholder: '请选择日期', columnPer: 8, dateType: 'year', showFormat: 'yyyy年', valueFormat: 'yyyy'},
                { type: 'autoComplete', label: '输入建议文本', name: 'autoComplete', value: null, placeholder: '请输入' },
                // { type: 'slot', status: 1, label: '插槽2', name: 'slot2', value: '898989', columnPer: 8 },
                { type: 'file', dataType: 'text', label: '文件', name: 'allFile', value: [], columnPer: 12, isMultiple: true, limit: 3, isOneLine: true },
                { type: 'file', dataType: 'picture-card', label: '图片', name: 'pictFile', value: [], columnPer: 12, isMultiple: true, limit: 3, isOneLine: true },
                { type: 'richText', label: '富文本', name: 'richText', value: '', columnPer: 24, isOneLine: true},
            ],
            
            
            opBtnList: [
                { name: '返回', icon: 'back', method: 'popPage', params: '' },
                { name: '保存', icon: 'save', method: 'saveForm', params: '' }
            ]
        }
    },
    computed: {
    },
    methods: {
        exeMethod (id) {
            this[id]()
        },
        saveForm () {
            this.$refs.formHello.validateForm().then(res => {
                alert(JSON.stringify(res))
            })
        },
        popPage () {
            this.$router.go(-1)
        }
    }
}
</script>

<style scoped>

</style>




