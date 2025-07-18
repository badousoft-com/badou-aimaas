<template>
    <div class="padding bg-white">
        js触发打开dialog-表单
        <el-button type="primary" @click="$router.go(-1)">返回demo目录</el-button><br>
        -------------------------------------------------------------------------<br/>
        <el-button type="primary" @click="showDialogModelForm">我是一个没有感情的按钮，通过js逻辑弹出模型表单，不需要引入dialog跟form标签</el-button>
    </div>
</template>
<script>
    // import xx from ./xx
    export default {
        components: {},
        data () { // 定义页面变量
            return {
            }
        },
        computed: {},
        methods: { // 定义函数
            showDialogModelForm () {
                // 定义弹窗唯一标识
                let signId = 'helloModelForm'
                // 定义弹窗所需按钮
                let btnList = [
                    {
                        name: '取消',
                        type: 'danger',
                        icon: 'cancel',
                        click: function () {
                            // 关闭弹窗表单
                            this.$dialog.close()
                        }
                    }, { 
                        name: '保存',
                        icon: 'save',
                        type: 'primary',
                        loading: false,
                        click: function (btnObj) {
                            // getDialogConObj:全局封装，4为指定模型表单，返回该页面作用域
                            let formObj = this.getDialogConObj(signId, 4)
                            formObj.validateForm().then(res => {
                                btnObj.loading = true // 重置按钮加载状态
                                alert(JSON.stringify(res))
                                btnObj.loading = false // 重置按钮加载状态
                            })
                        } 
                    }
                ]
                this.$dialog.init({
                    // 弹窗内容类型
                    type: 'standerEditCode',
                    // 弹窗唯一标识，注意唯一标识与上面出现的函数getModelListObj的参数值保持一致
                    id: signId,
                    // 弹窗标题
                    title: '弹出模型表单标题',
                    // 模型编码c
                    mdCode: 'wjx_text2',
                    // 根据内容自适应高度
                    // isAutoFix: true,
                    width: '800px',
                    // 详情数据id
                    // detailId: '36dbdb8b765a4684a6b718df729a81b1c66529c19c1049db8092a9294dd75103',
                    // 弹窗中按钮组
                    handlerList: btnList,
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