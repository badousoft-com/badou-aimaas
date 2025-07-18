<template>
    <div class="padding">
        <el-button @click="showDialogByPage" type="primary">测试js弹窗 挂载页面</el-button>
    </div>
</template>
<script>
    export default {
        components: {
        },
        data () { // 定义页面变量
            return {}
        },
        computed: {},
        methods: { // 定义函数
            showDialogByPage () {
                this.$pageDialog.init({
                    title: '哇咔咔',
                    pageUrl: 'demo/items/DemoTab/index',
                    outScope: this,
                    handlerList: [
                        { id: 'save', name: '获取', icon: 'save', type: 'primary', click: function (itemObj) {
                            this // 只想弹窗组件中的作用域
                            this.outScope // 若使用dialog时传入属性scope:this,则此处可获取使用弹窗的页面作用域
                            this.pageScope // 挂载页面所在的作用域
                            alert(`输入框值为：${this.pageScope.input}`)
                        }}
                    ],
                    // isAutoFix:true // 根据内容自适应
                })
            }
        },
        // 可访问当前this实例
        created () {},
        // 挂载完成，可访问DOM元素
        mounted () {
        },
    }
</script>