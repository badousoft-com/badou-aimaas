<!--
 * @FilePath: @/components/frame/Panel/BlockContent/items/PText.vue
 * @Description: 面板 - 文本类型
-->
<template>
    <div
        v-loading="loading"
        v-bind="loadingAttrs"
        @click="handlerClick"
        :class="{'can-click': clickInfo}"
        class="p-text">
        <div
            v-html="htmlStr || textInfo.text || ''"
            :style="textInfo.style || {}">
        </div>
    </div>
</template>

<script>
import ReportCommonAttrs from './mixins/ReportCommonAttrs.vue'
export default {
    mixins: [ReportCommonAttrs],
    inheritAttrs: false,
    data: () => ({
        htmlStr: ''
    }),
    computed: {
        textInfo () {
            return this.customSetting?.textInfo || {}
        },
        // 文本点击信息
        clickInfo () {
            return this.textInfo?.click
        }
    },
    methods: {
        init () {
            if (this.textInfo.text) return
            this.loadData().then(res => {
                this.htmlStr = res?.data || ''
            })
        },
        handlerClick () {
            // 刷新
            let refreshList = this.clickInfo?.refresh || []
            if (refreshList.length) {
                typeof this.panelScope.refresh === 'function' && this.panelScope.refresh(refreshList)
            }
            // 搜索
            let searchList = this.clickInfo?.search?.list || []
            if (searchList.length) {
                let searchParams = this.clickInfo?.search?.params || {}
                typeof this.panelScope.search === 'function' && this.panelScope.search(searchParams, searchList)
            }
        }
    },
    mounted () {
        this.init()
    }
}
</script>

<style lang="scss" scoped>
.can-click {
    cursor: pointer;
}
</style>