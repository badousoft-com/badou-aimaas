<template>
    <div class="bd-page-edit-dic">
        <module-edit-dic
            ref="moduleEditDic"
            :mdCode="mdCode"
            :detailId="detailId"
            :defaultParamsObj="query"
            fullHeight>
        </module-edit-dic>
    </div>
</template>
<script>
import ModuleEditDic from '@/components/frame/Module/ModuleEditDic/index.vue'
export default {
    components: {
        [ModuleEditDic.name]: ModuleEditDic
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
        }
    },
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