<template>
    <div>
        <bd-button @click="save" type="primary">获取混淆中的表单模块数据</bd-button>
        <module-mixin-tab
            style="height:450px"
            :data="tabData">
        </module-mixin-tab>
    </div>
</template>
<script>
import ModuleMixinTab from '@/components/frame/Module/ModuleMixinTab'
export default {
    components: {
        [ModuleMixinTab.name]: ModuleMixinTab
    },
    props: {
    },
    data () {
        return {
            tabData: [
                { text: '用户管理', icon: 'save', children: [
                    { type: 'page', pageUrl: '/demo/items/DemoTab/index'}
                ] },
                { text: '配置管理', icon: 'edit', fullOnlyOne: true, children: [
                    { type: 'module-list-code', mdCode: 'sys_md_module'}
                ] },
                { text: '角色管理', icon: 'delete', children: [
                    { type: 'module-list-code', mdCode: 'sys_md_module' },
                    { type: 'module-edit-code', mdCode: 'flwViewDictionary', detailId: 'ff80818167735f230167737b7733005e' }
                ] },
                { text: '定时任务补偿', icon: 'add', fullOnlyOne: true, children: [
                    { type: 'module-edit-code', mdCode: 'flwViewDictionary', detailId: 'ff80818167735f230167737b7733005e', noTitle: true }
                ] },
            ]
        }
    },
    methods: {
        save () {
            // 获取模块的域
            let formScope = this.tabData[3].children[0].scope
            formScope.getFormRef().validateForm().then(res => {
                alert(`最后一个表单的数据为${JSON.stringify(res)}`)
            })
        }
    },
    created () {
    },
    mounted () {
    }
}
</script>
<style lang="scss" scoped>
</style>