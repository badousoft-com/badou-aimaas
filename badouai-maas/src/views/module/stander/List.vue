// 新的时代已经来临
<template>
    <div class="bd-page-list">
        <module-list-code
            ref="moduleListCode"
            :mdCode="mdCode"
            :searchParam="searchParam"
            :defaultParamsObj="defaultSearchParam"
            :isStartReList.sync="isStartReList"
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
            keyInfo: 1,
            timer:''
        }
    },
    computed: {
    },
    methods: { // 定义函数
        refresh () {
            this.keyInfo += 1
        }
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
        // 列表数据定时刷新
        if (this.mdCode === 'fbpt_template_project_app') {
            if (this.timer) {
                clearInterval (this.timer)
            } else {
                this.timer = setInterval (() => {
                    let search = this.$refs.moduleListCode.$refs.moduleList.search
                    search()
                }, 10000)
            }
        }
        // if (this.timer) {
        //     clearInterval (this.timer)
        // } else {
        //     this.timer = setInterval (() => {
        //         let search = this.$refs.moduleListCode.$refs.moduleList.search
        //         search()
        //     }, 10000)
        // }
    },
    destroyed () {
        clearInterval (this.timer)
    }
}
</script>
<style lang='scss' scoped>
// 设置页面默认铺满
.bd-page-list {
    height: 100%;
}
</style>