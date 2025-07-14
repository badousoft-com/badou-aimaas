<template>
    <div class="padding bg-white">
        js触发打开dialog-列表
        <el-button type="primary" @click="$router.go(-1)">返回demo目录</el-button><br>
        -------------------------------------------------------------------------<br/>
        <el-button type="primary" @click="showDialogList">我是一个没有感情的按钮，通过js逻辑弹出表格，不需要引入dialog跟表格·标签</el-button>
        <!-- <el-button type="primary" @click="showDialogList2">简单js最少参数调用dialog</el-button> -->
    </div>
</template>
<script>
    const listId = 'baseList'
    const listId2 = 'baseList2'
    import router from '@/router'
    export default {
        components: {
        },
        data () { // 定义页面变量
            return {               
                btnList: [
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
                        type: 'primary',
                        icon: 'save',
                        click: function () {
                            // getDialogConObj:全局封装，1为指定自定义列表，返回该对象作用域
                            let list = this.getDialogConObj(listId, 1).selection
                            alert(`当前选择的数据项：${JSON.stringify(list)}`)
                        } 
                    }
                ],
            }
        },
        computed: {},
        methods: { // 定义函数
            showDialogList () {
                
                this.$dialog.init({
                    title: '弹窗标题',
                    type: 'list',
                    // width: 1200,
                    // height: 300,
                    isSelection: true,
                    id: listId,
                    fieldList: [
                        { name: 'updateTime', label: '更新时间', minWidth: '400'  },
                        { 
                            name: 'name', 
                            label: '名称',
                            iconName: 'save',
                            iconClass: "dangerC",
                            iconStyle: {
                                color: '#000'
                            },
                            formatter: function (row, column, cellValue, index, btnObj) {
                                return `<span style="color:red">${cellValue}</span>`
                            }, 
                            handle: function (row, column, cellValue, index, btnObj) {
                                this.$dialog.close()
                                this.$router.push({path: '/demo'})
                            
                            }
                        },
                        { name: 'updatorName', label: '更新人' }
                    ],
                    handlerList: this.btnList,
                    data: [
                        { updateTime: '2017-07-02', name: `
                            王1
                            王不留行1王不留行1王不留行1王不留行1王不留行1王不留行1
                            王不留行1王不留行1王不留行1王不留行1王不留行1王不留行1
                            王不留行1王不留行1王不留行1王不留行1王不留行1王不留行1
                            王不留行1王不留行1王不留行1王不留行1王不留行1王不留行1
                            王不留行1王不留行1王不留行1王不留行1王不留行1王不留王不留行1王不留行1王不留行1王不留行1王不留行1王不留行1
                            王不留行1王不留行1王不留行1王不留行1王不留行1王不留行1
                            王不留行1王不留行1王不留行1王不留行1王不留行1王不留行1
                            王不留行1王不留行1王不留行1王不留行1王不留行1王不留行1
                            王不留行1王不留行1王不留行1王不留行1王不留行1王不留行1
                            王不留行1王不留行1王不留行1王不留行1王不留行1王不留行1
                            王不留行1王不留行1王不留行1王不留行1王不留行1王不留行1
                            王不留行1王不留行1王不留行1王不留行1王不留行1王不留行1
                            王不留行1王不留行1王不留行1王不留行1王不留行1王不留行1
                            王不留行1王不留行1王不留行1王不留行1王不留行1王不留行1
                            王不留行1王不留行1王不留行1王不留行1王不留行1王不留行1
                            王不留行1王不留行1王不留行1王不留行1王不留行1王不留行1
                            王不留行1王不留行1王不留行1王不留行1王不留行1王不留行1行1
                            王不留行1王不留行1王不留行11`, updatorName: '管理员1' },
                        { updateTime: '2017-07-02', name: '王王不留行王不留行王不留行不留行2', updatorName: '管理员2' },                 	  { updateTime: '2017-07-02', name: '王不留行3', updatorName: '管理员3' },
                        { updateTime: '2017-07-02', name: '王不留行4', updatorName: '管理员4' },
                        { updateTime: '2017-07-02', name: '王不留行1', updatorName: '管理员1' },
                        { updateTime: '2017-07-02', name: '王不留行2', updatorName: '管理员2' },                 	  { updateTime: '2017-07-02', name: '王不留行3', updatorName: '管理员3' },
                        { updateTime: '2017-07-02', name: '王不留行4', updatorName: '管理员4' },
                        { updateTime: '2017-07-02', name: '王不留行1', updatorName: '管理员1' },
                        { updateTime: '2017-07-02', name: '王不留行2', updatorName: '管理员2' },                 	  { updateTime: '2017-07-02', name: '王不留行3', updatorName: '管理员3' },
                        { updateTime: '2017-07-02', name: '王不留行4', updatorName: '管理员4' },
                        { updateTime: '2017-07-02', name: '王不留行1', updatorName: '管理员1' },
                        { updateTime: '2017-07-02', name: '王不留行2', updatorName: '管理员2' },                 	  { updateTime: '2017-07-02', name: '王不留行3', updatorName: '管理员3' },
                        { updateTime: '2017-07-02', name: '王不留行4', updatorName: '管理员4' },
                        { updateTime: '2017-07-02', name: '王不留行2', updatorName: '管理员2' },                 	  { updateTime: '2017-07-02', name: '王不留行3', updatorName: '管理员3' },
                        { updateTime: '2017-07-02', name: '王不留行4', updatorName: '管理员4' },
                        { updateTime: '2017-07-02', name: '王不留行1', updatorName: '管理员1' },
                        { updateTime: '2017-07-02', name: '王不留行2', updatorName: '管理员2' },                 	  { updateTime: '2017-07-02', name: '王不留行3', updatorName: '管理员3' },
                        { updateTime: '2017-07-02', name: '王不留行4', updatorName: '管理员4' },
                        { updateTime: '2017-07-02', name: '王不留行1', updatorName: '管理员1' },
                        { updateTime: '2017-07-02', name: '王不留行2', updatorName: '管理员2' },                 	  { updateTime: '2017-07-02', name: '王不留行3', updatorName: '管理员3' },
                        { updateTime: '2017-07-02', name: '王不留行4', updatorName: '管理员4' },
                        { updateTime: '2017-07-02', name: '王不留行1', updatorName: '管理员1' },
                        { updateTime: '2017-07-02', name: '王不留行2', updatorName: '管理员2' },                 	  { updateTime: '2017-07-02', name: '王不留行3', updatorName: '管理员3' },
                        { updateTime: '2017-07-02', name: '王不留行4', updatorName: '管理员4' },
                    ],
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