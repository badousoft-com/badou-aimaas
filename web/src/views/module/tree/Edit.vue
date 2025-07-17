<template>
    <div class="bd-page-edit">
        <module-edit-tree
            ref="moduleEditCode"
            :mdCode="mdCode"
            :detailId="detailId"
            :defaultParamsObj="query"
            :addFormData="addFormData"
            :backParams="backParams"
            :currentTreeNodeData="currentTreeNodeData"
            fullHeight>
        </module-edit-tree>
    </div>
</template>
<script>
import ModuleEditTree from '@/components/frame/Module/ModuleEditTree/index.vue'
import ClearListToggleStore from '../mixins/ClearListToggleStore'
export default {
    mixins: [ClearListToggleStore],
    components: {
        [ModuleEditTree.name]: ModuleEditTree
    },
    data () { // 定义页面变量
        return {
        }
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
        },
        // 详情id
        detailId () {
            let id = this.params?.id
            if (id !== 'add') {
                return id
            }
            return ''
        },
        // 添加表单数据
        addFormData () {
            if (this.query) {
                return JSON.parse(decodeURIComponent(this.query.addFormData)) || {}
            }
            return {}
        },
        // 定义页面返回时携带参数
        backParams () {
            if (this.query) {
                return JSON.parse(decodeURIComponent(this.query.backParams)) || {}
            }
            return {}
        },
        // 定义树型选中节点
        currentTreeNodeData () {
            if (this.query) {
                return JSON.parse(decodeURIComponent(this.query.currentTreeNodeData)) || {}
            }
            return {}
        }
    },
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
    },
}
</script>
<style lang='scss' scoped>

</style>