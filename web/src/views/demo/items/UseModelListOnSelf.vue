<template>
    <div class="bg-white">
        <el-button type="primary" @click="$router.go(-1)">返回demo目录</el-button><br>
        -------------------------------------------------------------------------<br/>
        <!-- 使用class:noPadding是为了消除原有模型列表页面的padding -->
        <!-- mdCode: 模型编码（后台配置）
             symbol：TODO（意义不明，目前默认传placeholder）
             query: 默认查询参数对象（后台配置，前端组装为对象格式） -->
        <!-- <m-stander-list
            class="noPadding"
            :mdCode="mdCodeVal"
            :symbol="symbolVal"
            :query="queryVal">
        </m-stander-list> -->
        【第一个模块】：<br/>
        <module-list-code
            ref="moduleListCode"
            :mdCode="mdCode">
        </module-list-code>
        【第二个模块】：<br/>
        <ul class="padding warningTag margin">
            <li>额外模块：：--------------------------第一数据项</li>
            <li>额外模块：：--------------------------第二数据项</li>
            <li>额外模块：：--------------------------第三数据项</li>
        </ul>
    </div>
</template>
<script>
    import ModuleListCode from '@/components/frame/Module/ModuleListCode'
    // import MStanderList from '@/views/module/stander/List'
    export default {
        components: {
            [ModuleListCode.name]: ModuleListCode
        },
        data () { // 定义页面变量
            return {
                mdCode: 'WJX_FORM',
                // mdCodeVal: 'filter',
                // symbolVal: 'placeholder',
                // queryVal: {}  // 例如存在默认参数则为 { id: xxx }
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