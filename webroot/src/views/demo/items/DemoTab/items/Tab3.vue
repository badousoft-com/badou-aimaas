<template>
    <div class="">
        填写数据： <el-input v-model="value"></el-input>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
        1<br/>
    </div>
</template>
<script>
    // import xx from ./xx
    export default {
        name: "",
        components: {},
        data () { // 定义页面变量
            return {
                value: ''
            }
        },
        computed: {},
        methods: { // 定义函数
            count () {
                console.log(this.num)
            },
            beforeLeave () {
                if (!this.value) {
                    alert('数据没填好')
                    return false
                }
                return true
                
            }
        },
        // 可访问当前this实例
        created () {},
        // 挂载完成，可访问DOM元素
        mounted () {
        }
    }
</script>
<style lang='scss' scoped>

</style>