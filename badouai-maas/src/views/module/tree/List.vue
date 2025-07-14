// 新的时代已经来临
<template>
    <div class="bd-page-list">
        <module-list-tree
            ref="moduleListTree"
            :mdCode="mdCode"
            :searchParam="searchParam"
            :defaultParamsObj.sync="defaultSearchParam"
            :isStartReList.sync="isStartReList"
            :permissionBtnList="permissionBtnList"
            fullHeight
            v-bind="$attrs"
            v-on="$listeners">
        </module-list-tree>
    </div>
</template>
<script>
import { Get_Default_SearchParam } from '@/service/module'
import ModuleListTree from '@/components/frame/Module/ModuleListTree/index.vue'
import KeepAlive from '@/views/module/mixins/KeepAlive'
import ButtonPermissions from '@/views/module/mixins/ButtonPermissions.vue'
export default {
    name: 'standerListTree',
    // keep-alive状态混入
    mixins: [KeepAlive, ButtonPermissions],
    components: {
        [ModuleListTree.name]: ModuleListTree
    },
    data () { // 定义页面变量
        return {
            // isStartReList 管理在当前mixins中的keppAlive组件
            // 路由params参数
            params: null,
            // 路由query参数
            query: null,
            // 搜索栏参数
            searchParam: null,
            // 固定列表请求参数
            defaultSearchParam: null,
            // 模型编码
            mdCode: null
        }
    },
    computed: {
    },
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        // params参数
        this.params = this.$route?.params
        // query参数
        this.query = Get_Default_SearchParam(this.$route?.query)
        // 搜索栏参数
        this.searchParam = this.query?.searchParam || {}
        // 固定列表请求参数
        this.defaultSearchParam = this.query ?
            { defaultSearchParam: this.query['defaultSearchParam']} : {}
        // 模型编码
        this.mdCode = this.params?.mdCode
    }
}
</script>
<style lang='scss' scoped>
.bd-page-list {
    .bd-module-list-tree {
        height: 100%;
    }
}
</style>