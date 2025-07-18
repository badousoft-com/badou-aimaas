<template>
    <div class="padding bg-white">
        页面引入标签dialog-表单
        <el-button type="primary" @click="$router.go(-1)">返回demo目录</el-button><br>
        -------------------------------------------------------------------------<br/>
        <el-button type="primary" @click="showDialogForm">有感情的dialog，我是标签引入，使用的时候需要先引入dialog标签</el-button>

        <m-dialog
            :ref="formInfo.id"
            :visible.sync="isShow"
            type="form"
            :isFixedDialog = "true"
            :id="formInfo.id"
            :title="formInfo.title"
            :labelWidth="formInfo.labelWidth"
            :columnNum="formInfo.columnNum"
            :dataList="formHelloList"
            :handlerList="btnList">
        </m-dialog>
    </div>
</template>
<script>
    const formId = 'formHello'
    import MDialog from '@/components/frame/Common/MDialog/index.vue'
    export default {
        components: {
            MDialog
        },
        data () { // 定义页面变量
            return {
                isShow: false,
                formInfo: {
                    id: formId,
                    title: '表单标题',
                    labelWidth: '110px',
                    columnNum: 3
                },
                formHelloList: [
                    { groupName: '总标题', type: 'text', label: '普通标签', name: 'common', value: '1222', placeholder: '请输入' },
                    { type: 'text', label: '占比变宽标签', name: 'largePer', value: '12', placeholder: '请输入', columnPer: 16 },
                    { type: 'hidden', label: '隐藏元素', name: 'hidden', value: '1222' },
                    { type: 'text', label: '密码', name: 'password', value: null, placeholder: '请输入密码', isPassword: true },
                    { type: 'text', disabled: true, label: '不可编辑', name: 'noEdit', value: '12', placeholder: '请输入' },
                    { type: 'text', label: '字数限制', name: 'numLint', value: '12', placeholder: '请输入', maxlength: 10 },
                    { type: 'text', label: '前置元素', name: 'frontEl', value: '12', placeholder: '请输入', prepend: 'Http://' },
                    { type: 'text', label: '后置元素', name: 'endEl', value: '12', placeholder: '请输入', append: '.com' }
                ],
                btnList: [
                    {
                        name: '取消',
                        icon: 'cancel',
                        type: 'danger',
                        click: () => {
                            this.$confirm('取消将不保存表单数据, 是否继续?', '提示', {          
                                confirmButtonText: '确定',          
                                cancelButtonText: '取消',          
                                type: 'warning'
                            }).then(() => {          
                                // 关闭弹窗表单
                                this.isShow = false
                            }).catch(() => {          
                                // 取消关闭        
                            })
                        }
                    }, { 
                        name: '保存',
                        type: 'primary',
                        icon: 'save',
                        loading: false,
                        click: (btnObj) => {
                            btnObj.loading = true
                            let formObj = this.$refs[formId].$refs[formId].$refs[formId]
                            setTimeout(() => {
                                formObj.validateForm().then(res => {
                                    btnObj.loading = false
                                    alert(JSON.stringify(res))
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
                this.isShow = true
            },
        },
        // 可访问当前this实例
        created () {},
        // 挂载完成，可访问DOM元素
        mounted () {}
    }
</script>
<style lang='scss' scoped>

</style>