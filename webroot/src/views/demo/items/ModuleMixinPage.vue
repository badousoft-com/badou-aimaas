<template>
    <!-- 设置h-auto关闭整个页面的高度限制 -->
    <div class="h-auto">
        <module-mixin
            v-if="list && list.length > 0"
            :data="list">
        </module-mixin>
    </div>
    
</template>
<script>
    import ModuleMixin from '@/components/frame/Module/ModuleMixin'
    export default {
        components: {
            [ModuleMixin.name]: ModuleMixin,
        },
        data () { // 定义页面变量
            return {
                list: [
                    // 除了type，其余参数与组件接收参数一致
                    { type: 'page', pageUrl: '/demo/items/DemoTab/index.vue' },
                    { type: 'module-list-code', mdCode: 'sys_md_module' },
                    { type: 'module-edit-code', mdCode: 'flwViewDictionary', detailId: 'ff80818167735f230167737b7733005e' },
                    { type: 'module-list-code', mdCode: 'sys_md_module' },
                    { type: 'module-edit-code', mdCode: 'flwViewDictionary', detailId: 'ff80818167735f230167737b7733005e' },
                ]
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