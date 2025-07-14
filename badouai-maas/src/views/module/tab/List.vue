<template>
    <div class="h-per-100">
        <module-mixin-tab
            class="h-per-100"
            :data="tabData"
            @afterModuleMixinTab="afterModuleMixinTab">
        </module-mixin-tab>
    </div>
</template>
<script>
import ModuleMixinTab from '@/components/frame/Module/ModuleMixinTab'
import GlobalConst from '@/service/global-const'
export default {
    components: {
        [ModuleMixinTab.name]: ModuleMixinTab
    },
    props: {
    },
    data () {
        return {
            tabData: []
        }
    },
    computed: {
        // params参数
        params () {
            return this.$route?.params
        },
        // 模型编码
        mdCodes () {
            return this.params?.mdCodes
        }
    },
    methods: {
        getTabData () {
            if (!this.mdCodes) return []
            // 根据切割符生成mdCode数组
            let mdCodeList = this.mdCodes.split(GlobalConst.separator)
            // 组装ModuleMixinTab需要的数据
            let _result = mdCodeList.map((i, index) => ({
                text: '加载中',
                id: index.toString(),
                // 设置为单tab下模块的铺满
                fullOnlyOne: true,
                children: [{
                    type: 'module-list-code',
                    mdCode: i,
                    fullHeight: true,
                }]
            }))
            return _result
        },
        afterModuleMixinTab (module, childIndex, tabIndex) {
            this.tabData[tabIndex].text = module.moduleName
        }
    },
    created () {
    },
    mounted () {
        this.tabData = this.getTabData()
    }
}
</script>
<style lang="scss" scoped>
</style>