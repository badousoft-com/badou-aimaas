<template>
    <div class="padding bg-white">
        <el-button type="primary" @click="$router.go(-1)">返回demo目录</el-button><br>
        -------------------------------------------------------------------------<br/>
        【第一个模块】：<br/>
        <module-edit-code
            ref="moduleEditCode"
            :mdCode="mdCode"
            :detailId="detailId">
        </module-edit-code>
        【第二个模块】：<br/>
        <ul class="padding warningTag margin">
            <li>额外模块：：--------------------------第一数据项</li>
            <li>额外模块：：--------------------------第二数据项</li>
            <li>额外模块：：--------------------------第三数据项</li>
        </ul>
    </div>
</template>
<script>
    import ModuleEditCode from '@/components/frame/Module/ModuleEditCode'
    export default {
        components: {
            [ModuleEditCode.name]: ModuleEditCode
        },
        data () { // 定义页面变量
            return {
                mdCode: 'WJX_FORM',
                detailId: ''
            }
        },
        computed: {},
        methods: { // 定义函数
        },
        // 可访问当前this实例
        created () {},
        // 挂载完成，可访问DOM元素
        mounted () {}
    }
</script>
<style lang='scss' scoped>

</style>