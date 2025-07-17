<template>
    <div class="padding bg-white">
        <el-button type="primary" @click="$router.go(-1)">返回demo目录</el-button><br>
        -------------------------------------------------------------------------<br/>
        <el-button @click="showTable()" type="primary">点击测试弹窗-模型列表</el-button>
        <el-button @click="showTable2()" type="primary">点击测试弹窗-模型列表2</el-button>
    </div>
</template>
<script>
    const id = 'modelListId'
    import GlobalConst from '@/service/global-const'
    export default {
        components: {},
        data () { // 定义页面变量
            return {
            }
        },
        computed: {},
        methods: { // 定义函数
            showTable () {
                this.$dialog.init({
                    type: 'standerListCode',
                    id: id,
                    title: '模型表格标题',
                    mdCode: 'wjx_form_0427',
                    handlerList: [
                        {
                            name: '取消',
                            type: 'danger',
                            icon: 'cancel',
                            handler: function () {
                                // 关闭弹窗表单
                                this.$dialog.close()
                            }
                        }, { 
                            name: '保存',
                            type: 'primary',
                            icon: 'save',
                            handler: function () {
                                // getDialogConObj:全局封装，3为指定模型列表，返回该页面作用域
                                let aimRef = this.getDialogConObj(id, 3)
                                let selection = aimRef.selection
                                alert(`选择的列表数据为：${JSON.stringify(selection)}`)
                            } 
                        }
                    ],
                    fullHeight: true
                })
            },
            showTable2 () {
                this.$dialog.init({
                    type: 'standerListCode',
                    id: id,
                    title: '模型表格标题',
                    mdCode: 'wjx_form_0427',
                    customSetting: {
                        multiple: true
                    },
                    handlerList: [
                        {
                            name: '取消',
                            type: 'danger',
                            icon: 'cancel',
                            handler: function () {
                                // 关闭弹窗表单
                                this.$dialog.close()
                            }
                        }, { 
                            name: '保存',
                            type: 'primary',
                            icon: 'save',
                            handler: function () {
                                // getDialogConObj:全局封装，3为指定模型列表，返回该页面作用域
                                let aimRef = this.getDialogConObj(id, 3)
                                let selection = aimRef.selection
                                alert(`选择的列表数据为：${JSON.stringify(selection)}`)
                            } 
                        }
                    ],
                    fullHeight: true,
                    // 搜索栏参数格式：
                    // searchParam: {aa:1},
                    // 默认请求参数格式：
                    // defaultParamsObj: {
                    //     defaultSearchParam: JSON.stringify([{name: 'idd', value: '2121', type: GlobalConst.searchBar.type}])
                    // }
                })
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