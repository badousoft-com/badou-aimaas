<template>
    <div class="">

    </div>
</template>
<script>
    // import xx from ./xx
    export default {
        name: "",
        components: {},
        data () { // 定义页面变量
            return {
                num: 1,
            }
        },
        computed: {},
        methods: { // 定义函数
            count () {
                console.log(this.num)
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