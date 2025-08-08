<template>
    <div class="padding bg-white">
        js触发打开dialog-表单
        <el-button type="primary" @click="$router.go(-1)">返回demo目录</el-button><br>
        -------------------------------------------------------------------------<br/>
        <el-button type="primary" @click="showDialogForm">我是一个没有感情的按钮，通过js逻辑弹出表单，不需要引入dialog跟form标签</el-button>
        <el-button type="primary" @click="showDialogForm2">简单js最少参数调用dialog</el-button>
        <el-button type="primary" @click="showDialogForm3">调用dialog-简单表单</el-button>
    </div>
</template>
<script>
    const formId = 'formHello'
    const formId2 = 'formHello2'
    export default {
        components: {
        },
        data () { // 定义页面变量
            return {
                isShow: false,
                formInfo: {
                    id: formId,
                    title: '表单总标题',
                    labelWidth: '110px',
                    columnNum: 3
                },
                formHelloList: [
                    { groupName: '模块标题1',type: 'text', label: '普通标签', name: 'common', value: '1222', placeholder: '请输入' },
                    { groupName: '模块标题1',type: 'text', label: '占比变宽标签', name: 'largePer', value: '12', placeholder: '请输入', columnPer: 16 },
                    { groupName: '模块标题1',type: 'hidden', label: '隐藏元素', name: 'hidden', value: '1222' },
                    { groupName: '模块标题1',type: 'text', label: '密码', name: 'password', value: null, placeholder: '请输入密码', isPassword: true, rules: [
                        { required: true, message: '必填项', trigger: 'blur' }
                    ] },
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
                    { type: 'text', label: '必填带校验', name: 'reVaEl', value: null, placeholder: '请输入邮箱', rules: [
                        { required: true, message: '必填项', trigger: 'blur' },
                        { type: 'email', message: '请输入邮箱', trigger: 'blur' }
                    ] }, 
                    { type: 'textarea', label: '文本域', name: 'textarea', value: '12', placeholder: '请输入', maxlength: 40 },
                    { type: 'richText', label: '富文本', name: 'richText', value: '', columnPer: 24, isOneLine: true},
                ],
                btnList: [
                    {
                        name: '取消',
                        icon: 'cancel',
                        type: 'danger',
                        click: function () {
                            this.$confirm('取消将不保存表单数据, 是否继续?', '提示', {          
                                confirmButtonText: '确定',          
                                cancelButtonText: '取消',          
                                type: 'warning'
                            }).then(() => {          
                                // 关闭弹窗表单
                                this.$dialog.close()
                            }).catch(() => {          
                                // 取消关闭        
                            })
                        }
                    }, {
                        name: '保存',
                        icon: 'save',
                        type: 'primary',
                        loading: false, 
                        click: function (btnObj) {
                            btnObj.loading = true
                            // 设置时间函数模拟请求时间间隔，正式使用可以去除
                            setTimeout(() => {
                                // getDialogConObj:全局封装，2为指定自定义表单，返回该对象作用域
                                let formObj = this.getDialogConObj(formId, 2)
                                formObj.validateForm().then(res => {
                                    alert(JSON.stringify(res))
                                }).finally(() => {
                                    btnObj.loading = false
                                })
                            }, 1000)
                            
                        } 
                    }
                ],
            }
        },
        computed: {},
        methods: { // 定义函数
            showDialogForm () {
                this.$dialog.init({
                    type: 'form',
                    id: formId,
                    title: this.formInfo.title,
                    labelWidth: this.formInfo.labelWidth,
                    columnNum: this.formInfo.columnNum,
                    dataList: this.formHelloList,
                    handlerList: this.btnList,
                })
            },
            showDialogForm2 () {
                this.$dialog.init({
                    id: formId2,
                    title: this.formInfo.title,
                    dataList: this.formHelloList,
                    handlerList: this.btnList,
                })
            },
            showDialogForm3 () {
                this.$dialog.init({
                    id: formId2,
                    title: this.formInfo.title,
                    // 高度根据内容自适应
                    isAutoFix: true,
                    dataList: [
                        { type: 'text', label: '普通标签', name: 'common', value: '1222', placeholder: '请输入' },
                        { type: 'text', label: '占比变宽标签', name: 'largePer', value: '12', placeholder: '请输入', columnPer: 16 }
                    ],
                    handlerList: this.btnList,
                })
            }
        },
        // 可访问当前this实例
        created () {},
        // 挂载完成，可访问DOM元素
        mounted () {}
    }
</script>
<style lang='scss' scoped>

</style>