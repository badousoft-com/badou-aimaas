<!--
 * @FilePath: @/components/frame/Panel/PanelBlock/index.vue
 * @Description: 面板块的展示组件
-->
<template>
    <div :style="blockStyle" :class="'block-' + tempInfo.code" class="panel-block">
        <template v-if="contentList.length">
            <block-title
                ref="blockTitleRef"
                v-bind="titleAttrs"
                class="block-title_box flex-none">
                <div
                    v-if="tempInfo.showMore"
                    @click="toMore"
                    class="more-link">
                    更多
                    <bd-icon name="dArrowRight"></bd-icon>
                </div>
            </block-title>
            <div :style="contentStyle" class="block_content flex-1 d-fc">
                <panel-filter
                    v-if="blockFilter && blockFilter.length"
                    ref="blockFilterRef"
                    :filterList.sync="blockFilter"
                    :btnList="searchBtnList"
                    @search="search"
                    @reset="reset"
                    :style="filterStyle"
                    class="block-filter_box flex-none pad-10">
                </panel-filter>
                <button-list
                    v-if="btnList && btnList.length"
                    ref="btnListRef"
                    :btnList="btnList"
                    v-bind="btnAttrs"
                    @clickBtn="clickBtn"
                    class="pad-10">
                </button-list>
                <multi-content
                    v-if="isSwitch"
                    ref="multiContentRef"
                    :blockInfo="tempInfo"
                    :switchType="tempInfo.shouType"
                    :filterList="_filterList"
                    v-bind="_attrs"
                    class="flex-1">
                </multi-content>
                <template v-else>
                    <bd-content-item
                        v-for="c in contentList"
                        :key="c.id"
                        :ref="c.refName"
                        :contentInfo="c"
                        :showTitle="tempInfo.showContentTitle"
                        :filterList="_filterList"
                        v-bind="_attrs"
                        class="flex-1 o-hidden content-item_box">
                    </bd-content-item>
                </template>
            </div>
        </template>
        <!-- 边框的角 ---- start  顺序 从左上角 开始顺时针 -->
        <template v-if="useAngle">
            <div :style="angleStyle[0]" class="border-angle angle-top_left"></div>
            <div :style="angleStyle[1]" class="border-angle angle-top_right"></div>
            <div :style="angleStyle[2]" class="border-angle angle-bottom_right"></div>
            <div :style="angleStyle[3]" class="border-angle angle-bottom_left"></div>
        </template>
        <!-- 边框的角 ------ end -->
    </div>
</template>

<script>
import ContentItem from '@/components/frame/Panel/BlockContent/ContentItem.vue'
import BlockTitle from './BlockTitle.vue'
import MultiContent from '@/components/frame/Panel/BlockContent/MultiContent.vue'
import PanelFilter from '@/components/frame/Panel/PanelFilter/index.vue'
import ButtonList from '@/components/frame/Panel/ButtonList.vue'
import GlobalConst from '@/service/global-const'
import { Set_Block_Attr } from './utils'
import { Get_Attach_Url } from '@/api/frame/attach'
import { Get_BtnList } from '@/components/frame/Panel/js/common.js'
export default {
    name: 'bd-panel-block',
    inheritAttrs: false,
    components: {
        [ContentItem.name]: ContentItem,
        BlockTitle,
        MultiContent,
        PanelFilter,
        ButtonList,
    },
    props: {
        // 块信息
        blockInfo: {
            type: Object,
            default: () => {}
        },
        // 搜索信息
        filterList: {
            type: Array,
            default: () => []
        },
        //主题
        theme: {
            type: Object,
            default: () => {}
        },
        panelScope: {
            type: Object,
            default: null
        }
    },
    data: () => ({
        // 此组件可操作的块配置信息，同blockInfo
        tempInfo: {},
        // 自动刷新时间函数
        reloadTimer: null,
        // 按钮列表
        btnList: [],
    }),
    computed: {
        // 主题（合并了面板主题与块主题后的此块最终主题）
        themeInfo () {
            // 面板主题
            let panelTheme = this.theme || {}
            // 块主题
            let blockTheme = this.tempInfo?.customSetting || {}
            // 父组件传递过来的一些样式变量
            let { color, contentBg, textColor, importTextColor } = this.$attrs
            let attrsTheme = { color, contentBg, textColor, importTextColor }
            Object.keys(attrsTheme).forEach(key => {
                if (!attrsTheme[key]) {
                    delete attrsTheme[key]
                }
            })
            let res = {
                ...panelTheme,
                ...attrsTheme,
                ...blockTheme
            }
            return res
        },
        // 自定义搜索栏按钮
        searchBtnList () {
            return this.tempInfo?.customSetting?.searchBtns
        },
        // 内容列表
        contentList () {
            return this.tempInfo?.contentList || []
        },
        // 需要传递给内容组件的属性
        _attrs () {
            return {
                // 面板作用域
                panelScope: this.panelScope,
                // 块作用域
                blockScope: this,
                // 主题
                theme: this.themeInfo,
                ...this.$attrs
            }
        },
        // 需要传递给按钮的属性
        btnAttrs () {
            let { btnFixedRightSlot, btnNextLineSlot, btnStyle } = this.tempInfo?.customSetting || {}
            // 按钮样式 ======== 合并主题【块按钮样式】与【自定义按钮样式】
            let blockBtnStyle = this.themeInfo.blockBtnStyle || {}
            return {
                btnFixedRightSlot,
                btnNextLineSlot,
                style: btnStyle || blockBtnStyle,
                parentScope: this
            }
        },
        // 块搜索条件
        blockFilter: {
            get () {
                return this.tempInfo.filterList || null
            },
            set (val) {}
        },
        // 需要传递给子组件的搜索数据
        _filterList () {
            return [
                ...this.filterList,
                ...this.blockFilter
            ]
        },
        // 传入标题的属性
        titleAttrs () {
            let { titleIconStyle = {}, blockTitle = {} } = this.themeInfo
            // 自定义js配置的title属性，具体可设置值可查看BlockTitle.vue文件
            let customTitleAttrs = this.tempInfo?.customSetting?.title || {}
            return {
                ...this._attrs,
                // 标题名称
                name: this.tempInfo.name,
                // 是否展示标题
                showTitle: this.tempInfo.showTitle || false,
                // 标题展示的图标（在移动端，框架图标与业务图标前缀有区分 ======= todo）
                iconAttrs: {
                    name: this.tempInfo.titleIcon,
                    style: titleIconStyle
                },
                ...customTitleAttrs,
                // 标题样式
                style: {
                    backgroundColor: this.tempInfo.blockTitleColor,
                    color: this.tempInfo.blockTitleFontColor,
                    ...blockTitle,
                    ...(customTitleAttrs.style || {})
                }
            }
        },
        // 是否切换展示
        isSwitch () {
            // 内容长度 > 1
            return this.contentList.length > 1
        },
        // 块样式
        blockStyle () {
            // 边框样式
            let borderStyle = {}
            // 主题名称
            let themeName = this.themeInfo.name
            // 获取边框的配置
            let { hasBorder, borderType, blockBorderColor, backgroundImg } = this.tempInfo
            if (hasBorder && borderType && themeName) {
                if (themeName && themeName !== 'default') {
                    try {
                        let img = require(`@/components/frame/Panel/PanelCode/theme/${themeName}/assets/borderTheme/${borderType}.png`)
                        borderStyle.backgroundImage = `url(${img})`
                    } catch (err) {
                        console.error(`获取/Panel/PanelCode/theme/${themeName}/assets/borderTheme/${borderType}.png失败！`)
                        console.log(err)
                    }
                } else {
                    borderStyle.borderStyle = borderType
                    borderStyle.borderWidth = borderType === 'double' ? '4px' : '1px'
                    borderStyle.borderColor = blockBorderColor || this.$attrs.primaryColor
                }
            }
            if (backgroundImg) {
                borderStyle.backgroundImage = `url(${Get_Attach_Url(backgroundImg)})`
            }
            // 主题设置样式
            let themeStyle = this.themeInfo?.block || {}
            let { blockStyle = {} } = this.tempInfo?.customSetting || {}
            return Object.assign({}, themeStyle, borderStyle, blockStyle)
        },
        // 搜索条件样式
        filterStyle () {
            let { blockFilter = {}, filterStyle = {} } = this.themeInfo
            return {
                ...blockFilter,
                ...filterStyle,
            }
        },
        // 块内容样式（包含搜索条件及内容）
        contentStyle () {
            let { contentStyle = {}, blockContent = {} } = this.themeInfo
            return {
                ...blockContent,
                ...contentStyle,
            }
        },
        // 是否使用角
        useAngle () {
            let { hiddenAngle, angleList } = this.themeInfo
            return this.tempInfo.hasBorder && !hiddenAngle && Boolean(angleList)
        },
        // 角的样式
        angleStyle () {
            if (!this.useAngle) return []
            let angleStyle = this.themeInfo.angleStyle
            let res = []
            let temp = Object.prototype.toString.call(angleStyle)
            if (temp === '[object Object]') {
                res.length = 4
                res.fill(angleStyle, 0, 4)
            } else if (temp === '[object Array]') {
                res = angleStyle
            }
            // 主题名称
            let themeName = this.themeInfo.name
            this.themeInfo.angleList.forEach((o, o_index) => {
                let img = require(`@/components/frame/Panel/PanelCode/theme/${themeName}/assets/angle/${o}.png`)
                res[o_index] = {
                    ...(res[o_index] || {}),
                    backgroundImage: `url(${img})`,
                }
            })
            return res
        },
        // 是否自动刷新
        autoReload () {
            // 面板已配置自动刷新 && 自动刷新的最小时间为 1s
            return this.$attrs.autoReload && this.tempInfo.reloadTime > GlobalConst.panel.minAutoRefresh
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
        /**
         * @description: 批量调用内容中的方法
         * @param {String} fnName：方法名
         * @param {Object} params：需传递的参数
         * @param {Array} refNames：内容的refName数据
         */
        handlerContents (fnName, params = {}, refNames = []) {
            if (!fnName) return
            // 为多内容
            if (this.isSwitch) {
                this.$refs.multiContentRef.handlerContents(...arguments)
            } else {
                let _refs = this.$refs || {}
                // 内容 的 refName 必须以 content 开头
                for (const _refName in _refs) {
                    if (_refName.indexOf('content') === 0) {
                        // 获取到内容的ref
                        let contentRef = _refs[_refName][0] ? _refs[_refName][0] : _refs[_refName]
                        // 1. 当refNames不存在（调用所有）；2. refNames中存在该作用域
                        if (!refNames.length || ~refNames.indexOf(_refName)) {
                            typeof contentRef[fnName] === 'function' && contentRef[fnName](params)
                        }
                    }
                }
            }
        },
        // 刷新块
        refresh (params, dealList) {
            this.handlerContents('search', {}, dealList)
        },
        // 根据尺寸重新渲染
        resize (params, dealList) {
            this.handlerContents('resize', {}, dealList)
        },
        // 重置搜索条件
        resetFilter () {
            this.tempInfo.filterList.forEach(o => {
                this.$set(o, 'value', o.defaultValue)
            })
        },
        // 重置块
        reset (params, dealList) {
            this.resetFilter()
            this.handlerContents('reset', {}, dealList)
        },
        search (params = {}, dealList) {
            this.handlerContents('search', params, dealList)
        },
        toMore () {
            if (this.tempInfo.detailUrl) {
                this.pushPage({
                    path: this.tempInfo.detailUrl,
                    title: this.tempInfo.name
                })
            }
        },
        // 自动刷新配置
        doReload () {
            if (this.autoReload) {
                let t = this.tempInfo.reloadTime
                this.reloadTimer = setInterval(() => {
                    this.search({ '__hideLoading': true })
                }, t)
            }
        },
        // 块配置初始化
        async init () {
            this.tempInfo = await Set_Block_Attr(this.blockInfo)
            this.autoReload && this.doReload()
            // 组装按钮
            this.btnList = await Get_BtnList.call(this, this.tempInfo?.customSetting?.buttons)
        }
    },
    mounted () {
        this.init()
    },
    beforeDestroy () {
        clearInterval(this.reloadTimer)
    }
}
</script>

<style lang="scss" scoped>
.panel-block {
    display: flex;
    flex-direction: column;
    border-radius: $borderRadius;
    background-color: var(--content-bg);
    background-size: 100% 100%;
    background-repeat: no-repeat;
    overflow: hidden;
    position: relative;
    .block_content {
        overflow: hidden;
    }
    // 角 ----- start
    .border-angle {
        position: absolute;
        width: 18px;
        height: 18px;
        background-size: 100% 100%;
        background-repeat: no-repeat;
        z-index: 10;
        &.angle-top_left {
            top: 0;
            left: 0;
        }
        &.angle-top_right {
            top: 0;
            right: 0;
        }
        &.angle-bottom_right {
            bottom: 0;
            right: 0;
        }
        &.angle-bottom_left {
            bottom: 0;
            left: 0;
        }
    }
    // 角 --------end
    .more-link {
        cursor: pointer;
    }
    .d-fc {
        display: flex;
        flex-direction: column;
    }
    .flex-none {
        flex: none;
    }
    .o-hidden {
        overflow: hidden;
    }
}
</style>