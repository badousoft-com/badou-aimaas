// 非通用页面：角色权限分配
<template>
    <div class="bd-page-list">
        <module-list-tree
            ref="moduleListTree"
            :mdCode="mdCode"
            :defaultParamsObj.sync="query"
            fullHeight>
        </module-list-tree>
    </div>
</template>
<script>
import ModuleListTree from './RolePermission/index.vue'
export default {
    components: {
        [ModuleListTree.name]: ModuleListTree
    },
    data () {
        // 定义页面变量
        return {}
    },
    computed: {
        // params参数
        params () {
            return this.$route?.params
        },
        // query参数
        query () {
            return this.$route?.query
        },
        // 模型编码
        mdCode () {
            return this.params?.mdCode
        }
    },
    methods: {
        // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
.bd-page-list {
    .bd-module-list-tree {
        height: 100%;
    }
}
</style>