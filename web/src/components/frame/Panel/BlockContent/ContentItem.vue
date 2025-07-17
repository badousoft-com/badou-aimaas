<!--
 * @FilePath: @/components/frame/Panel/BlockContent/ContentItem.vue
 * @Description: 面板块渲染内容前的过渡组件
-->
<template>
    <div :style="contentStyle" class="content-item">
        <content-title
            v-if="_showTitle"
            ref="contentTitleRef"
            v-bind="titleAttrs"
            class="content-title_box">
        </content-title>
        <!-- {{contentInfo.name}} - {{tempInfo.compName}} -->
        <panel-filter
            v-if="contentFilter && contentFilter.length"
            ref="contentFilterRef"
            :filterList.sync="contentFilter"
            :btnList="searchBtnList"
            @search="search"
            @reset="reset"
            class="content-filter_box flex-none pad-10">
        </panel-filter>
        <button-list
            v-if="btnList && btnList.length"
            ref="btnListRef"
            :btnList="btnList"
            v-bind="btnAttrs"
            @clickBtn="clickBtn"
            class="flex-none pad-10">
        </button-list>
        <component
            v-if="tempInfo.compName"
            ref="contentItemRef"
            :is="'p-' + tempInfo.compName"
            :otherAttrs="$attrs"
            :filterList="_filterList"
            :blockScope="blockScope"
            :panelScope="panelScope"
            v-bind="tempInfo"
            class="item_view">
        </component>
    </div>
</template>

<script>
import { Set_Content_Attrs } from './utils'
import ContentTitle from './ContentTitle.vue'
import PanelFilter from '@/components/frame/Panel/PanelFilter/index.vue'
import ButtonList from '@/components/frame/Panel/ButtonList.vue'
import { Get_BtnList } from '@/components/frame/Panel/js/common.js'
import {
    PChart,
    PMap,
    PQuickMenu,
    PVideo,
    PPage,
    PMultiGraph,
    PList,
    PCard,
    PText,
} from './items/index'
export default {
    name: 'bd-content-item',
    inheritAttrs: false,
    components: {
        ContentTitle,
        PanelFilter,
        ButtonList,
        PChart,
        PMap,
        PQuickMenu,
        PVideo,
        PPage,
        PMultiGraph,
        PList,
        PCard,
        PText,
    },
    props: {
        contentInfo: {
            type: Object,
            default: () => {}
        },
        // 是否展示标题
        showTitle: {
            type: Boolean,
            default: false
        },
        // 搜索信息
        filterList: {
            type: Array,
            default: () => []
        },
        // 块作用域
        blockScope: null,
        // 面板作用域
        panelScope: null
    },
    data: () => ({
        // 此页面可以操作的内容对象
        tempInfo: {},
        // 按钮列表
        btnList: [],
    }),
    computed: {
        contentFilter: {
            get () {
                return this.tempInfo.filterList || null
            },
            set (val) {}
        },
        _filterList () {
            return [
                ...this.filterList,
                ...this.contentFilter
            ]
        },
        // 自定义搜索栏按钮
        searchBtnList () {
            return this.tempInfo?.customSetting?.searchBtns
        },
        // 是否展示标题
        _showTitle () {
            let customSetting = this.tempInfo.customSetting || {}
            if (customSetting.hasOwnProperty('showTitle')) {
                return customSetting.showTitle
            }
            return this.showTitle
        },
        // 传入标题的属性
        titleAttrs () {
            // 自定义js配置的title属性，具体可设置值可查看contentTitle.vue文件
            let customTitleAttrs = this.tempInfo?.customSetting?.title || {}
            // 主题定义的标题样式
            let themeTitleStyle = this.$attr?.theme?.contentTitle || {}
            // 自定义配置的主题样式
            let customStyle = customTitleAttrs.style || {}
            return {
                ...this.$attr,
                panelScope: this.panelScope,
                blockScope: this.blockScope,
                contentScope: this,
                name: this.tempInfo.name,
                ...customTitleAttrs,
                style: Object.assign({}, themeTitleStyle, customStyle)
            }
        },
        // 需要传递给按钮的属性
        btnAttrs () {
            let { btnFixedRightSlot, btnNextLineSlot, btnStyle } = this.tempInfo?.customSetting || {}
            // 按钮样式 ======== 合并主题【块按钮样式】与【自定义按钮样式】
            let contentBtnStyle = this.$attr?.theme?.contentBtnStyle || {}
            return {
                btnFixedRightSlot,
                btnNextLineSlot,
                style: btnStyle || contentBtnStyle,
                parentScope: this
            }
        },
        // 内容容器样式
        contentStyle () {
            let customSettingStyle = this.tempInfo?.customSetting?.contentStyle || {}
            let themeStyle = this.$attrs?.theme?.contentItemStyle || {}
            let res = Object.assign({}, themeStyle, customSettingStyle)
            return res
        },
    },
    methods: {
        // 按钮点击事件
        clickBtn (btnObj) {
            // 获取事件
            let { click: handleClick } = btnObj
            if (handleClick && typeof handleClick === 'function') {
                // 执行函数，传递作用域
                handleClick.call(this, btnObj)
            }
        },
        async init () {
            this.tempInfo = await Set_Content_Attrs(this.contentInfo)
            // 组装按钮
            this.btnList = await Get_BtnList.call(this, this.tempInfo?.customSetting?.buttons)
        },
        // 搜索条件重置
        resetFilter () {
            this.tempInfo.filterList.forEach(o => {
                this.$set(o, 'value', o.defaultValue)
            })
        },
        search () {
            typeof this.$refs.contentItemRef.search === 'function' && this.$refs.contentItemRef.search(...arguments)
        },
        resize () {
            typeof this.$refs.contentItemRef.resize === 'function' && this.$refs.contentItemRef.resize(...arguments)
        },
        reset () {
            this.resetFilter()
            this.search()
        }
    },
    mounted () {
        this.init()
    }
}
</script>

<style lang="scss" scoped>
.content-item {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    .content-title_box {
        flex: none;
    }
    .item_view {
        flex: 1;
    }
    .flex-none {
        flex: none;
    }
}
</style>