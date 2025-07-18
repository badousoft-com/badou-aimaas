// 查看的新时代也来了：查看的逻辑大部分与编辑结合
<template>
    <div
        :class="{'is-view': isView}"
        class="bd-page-edit">
        <module-edit-code
            isView
            ref="moduleEditCode"
            :mdCode="mdCode"
            :detailId="detailId"
            :defaultParamsObj="query"
            fullHeight>
        </module-edit-code>
    </div>
</template>
<script>
import ModuleEditCode from '@/components/frame/Module/ModuleEditCode'
import ClearListToggleStore from '../mixins/ClearListToggleStore'
export default {
    mixins: [ClearListToggleStore],
    components: {
        [ModuleEditCode.name]: ModuleEditCode
    },
    data () { // 定义页面变量
        return {
            // 是否查看模式
            isView: true
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
