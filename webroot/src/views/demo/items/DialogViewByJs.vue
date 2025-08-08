<template>
    <div class="padding bg-white">
        js触发打开dialog-表单查看
        <el-button type="primary" @click="$router.go(-1)">返回demo目录</el-button><br>
        -------------------------------------------------------------------------<br/>
        <el-button type="primary" @click="showDialogForm">通过js逻辑弹出表单查看，不需要引入dialog跟form标签</el-button>
    </div>
</template>
<script>
    const formId = 'formHello'
    export default {
        components: {
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
                    { groupName: '总标题', type: 'text', label: '普通标签', name: 'common', value: '1222' },
                    { type: 'text', label: '占比变宽标签', name: 'largePer', value: '占比变宽标签', columnPer: 16 },
                    { type: 'roadmap', label: '路线图2', name: 'roadmap2', value: 'Rows,dew,dew,dew', columnPer: 24 },
                    { type: 'hidden', label: '隐藏元素', name: 'hidden', value: '1222' },
                    { type: 'text', label: '密码', name: 'password', value: null },
                    { type: 'text', label: '不可编辑', name: 'noEdit', value: '不可编辑' },
                    { type: 'text', label: '字数限制', name: 'numLint', value: '字数限制' },
                    { type: 'text', label: '前置元素', name: 'frontEl', value: '12' },
                    { type: 'text', label: '后置元素', name: 'endEl', value: '后置元素' },
                    { type: 'text', label: '必填标签', name: 'requireEl', value: '12' },
                    { type: 'text', label: '非必填检验', name: 'noReVaEl', value: null },
                    { type: 'text', label: '必填带校验', name: 'reVaEl', value: '1065502552@qq.com' }, 
                    { type: 'select', label: '下拉框', name: 'select', value: '1' },
                    { type: 'richText', label: '富文本', name: 'richText', value: '<p>观察输出结果发现，<span style="color: #ff0000;">number, string, undefined, function，boolean</span>类型均能通过typeof方法判断，</p>', columnPer: 24, isOneLine: true},
                ],
                btnList: [
                    {
                        name: '取消',
                        type: 'danger',
                        icon: 'cancel',
                        click: function () {
                            this.$dialog.close()
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
                    // 表单是否为查看状态
                    isView: true,
                    id: formId,
                    title: this.formInfo.title,
                    labelWidth: this.formInfo.labelWidth,
                    columnNum: this.formInfo.columnNum,
                    dataList: this.formHelloList,
                    handlerList: this.btnList,
                })
            },
            popPage () {
                this.$router.go(-1)
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