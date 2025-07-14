// 新的时代已经来临
<template>
    <div class="bd-page-list">
        <module-list-code
            ref="moduleListCode"
            :mdCode="mdCode"
            :searchParam="searchParam"
            :defaultParamsObj="defaultSearchParam"
            :isStartReList.sync="isStartReList"
            :defaultBtnList="defaultBtnList"
            :permissionBtnList="permissionBtnList"
            fullHeight
            v-bind="$attrs"
            v-on="$listeners">
        </module-list-code>
    </div>
</template>
<script>
import { Get_Default_SearchParam } from '@/service/module'
import ModuleListCode from '@/components/frame/Module/ModuleListCode'
import KeepAlive from '@/views/module/mixins/KeepAlive'
import ButtonPermissions from '@/views/module/mixins/ButtonPermissions.vue'
import { Add, Edit, View, Urging, Export, Delete } from '@/components/frame/Module/BtnBaseFun/flow-list'

// 定义流程基础按钮
const Default_Btn_List = [
    { id: 'add', click: Add, loading: false },
    { id: 'edit', click: Edit, loading: false },
    { id: 'view', click: View, loading: false },
    { id: 'urgingT', click: Urging, loading: false, name: '催办', icon: 'urgingT', priority: 20, type: 'warning' },
    { id: 'export', click: Export, loading: false },
    { id: 'delete', click: Delete, loading: false },
]

export default {
    name: 'standerList',
    // keep-alive状态混入
    mixins: [KeepAlive, ButtonPermissions],
    components: {
        [ModuleListCode.name]: ModuleListCode
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
            mdCode: null,
            defaultBtnList: Default_Btn_List
        }
    },
    computed: {
    },
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {
    },
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
// 设置页面默认铺满
.bd-page-list {
    height: 100%;
}
</style>