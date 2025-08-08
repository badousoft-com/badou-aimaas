<!--
 * @FilePath: @/components/frame/Panel/PanelCode/index.vue
 * @Description: 面板页面展示块的中转站
-->
<template>
    <div
        ref="panelViewRef"
        v-loading="diy_loading"
        :style="styleVar"
        :class="[{'loading-panel_box': diy_loading}, `panel-theme_${panelInfo.pageId}`, 'panel-' + panelInfo.code]"
        class="panel-code per-100">
        <panel-title
            v-if="showTitle"
            ref="panelTitleRef"
            v-bind="titleAttrs"
            class="flex-none">
        </panel-title>
        <panel-filter
            v-if="panelFilter && panelFilter.length"
            ref="panelFilterRef"
            :filterList.sync="panelFilter"
            :btnList="searchBtnList"
            @search="search"
            @reset="reset"
            class="panel-filter_box flex-none">
        </panel-filter>
        <button-list
            v-if="btnList && btnList.length"
            ref="btnListRef"
            :btnList="btnList"
            v-bind="btnAttrs"
            @clickBtn="clickBtn"
            class="pad-10">
        </button-list>
        <div
            v-if="blockList.length"
            :class="{'scale-out-center': showExitAnimation}"
            class="panel-block_box flex-1">
            <div
                v-for="b in blockList"
                :key="b.id"
                :style="getBlockStyle(b)"
                :class="getBlockClassName(b)"
                class="h-per-100 block-item">
                <bd-panel-block
                    :ref="b.refName"
                    :blockInfo="b"
                    :filterList="panelFilter"
                    v-bind="_attrs"
                    class="w-per-100 h-per-100">
                </bd-panel-block>
            </div>
        </div>
    </div>
</template>

<script>
import { Get_Panel_Config } from './utils'
import PanelBlock from '@/components/frame/Panel/PanelBlock/index.vue'
import PanelFilter from '@/components/frame/Panel/PanelFilter/index.vue'
import PanelTitle from '@/components/frame/Panel/PanelCode/PanelTitle.vue'
import { Attach_Url_By_Id } from '@/api/frame/attach.js'
import GlobalConst from '@/service/global-const'
import ButtonList from '@/components/frame/Panel/ButtonList.vue'
import { Get_BtnList } from '@/components/frame/Panel/js/common.js'
export default {
    inheritAttrs: false,
    components: {
        PanelTitle,
        [PanelBlock.name]: PanelBlock,
        PanelFilter,
        ButtonList
    },
    props: {
        // 面板id
        panelId: {
            type: String,
            default: ''
        },
        // 色系
        color: {
            type: Array,
            default: () => []
        },
        // 内容背景色
        contentBg: {
            type: String,
            default: ''
        },
        // 内容主题色
        primaryColor: {
            type: String,
            default: ''
        },
    },
    data: () => ({
        loading: false,
        // 块列表
        blockList: [],
        // 面板信息
        panelInfo: {},
        // 按钮列表
        btnList: [],
        // 是否展示退出动画
        showExitAnimation: false
    }),
    computed: {
        diy_loading () {
            // 当不使用退出动画 && loading 的时候显示loading
            return !this.showExitAnimation && this.loading
        },
        // 面板搜索条件
        panelFilter: {
            get () {
                return this.panelInfo.filterList || []
            },
            set (val) {}
        },
        // 自定义搜索栏按钮
        searchBtnList () {
            return this.panelInfo?.customSetting?.searchBtns
        },
        // 需要传递给子组件的属性
        _attrs () {
            let { color, contentBg, primaryColor, textColor, importTextColor } = Object.assign({}, this.panelInfo.theme || {}, this.panelInfo?.customSetting || {})
            return {
                // 色系
                color: this.color && this.color.length ? this.color : color,
                // 背景色
                contentBg: this.contentBg ? this.contentBg : contentBg,
                // 主题色
                primaryColor: this.primaryColor ? this.primaryColor : primaryColor,
                // 字体颜色
                textColor,
                // 重要字体颜色
                importTextColor,
                // 是否自动刷新
                autoReload: String(this.panelInfo.isUseReload) === '1',
                // 主题
                theme: this.panelInfo.theme,
                // 面板作用域
                panelScope: this,
                ...this.$attrs
            }
        },
        // 面板标题所传参数
        titleAttrs () {
            // 自定义js配置的title属性，具体可设置值可查看BlockTitle.vue文件
            let customTitleAttrs = this.panelInfo?.customSetting?.title || {}
            // 主题定义的标题样式
            let themeTitleStyle = this.panelInfo?.theme?.panelTitle || {}
            // 自定义配置的主题样式
            let customStyle = customTitleAttrs.style || {}
            return {
                ...this._attrs,
                name: this.panelInfo.name,
                ...customTitleAttrs,
                style: Object.assign({}, themeTitleStyle, customStyle)
            }
        },
        // 需要传递给按钮的属性
        btnAttrs () {
            let { btnFixedRightSlot, btnNextLineSlot, btnStyle } = this.panelInfo?.customSetting || {}
            // 按钮样式 ======== 合并主题【块按钮样式】与【自定义按钮样式】
            let panelBtnStyle = this.panelInfo?.theme?.panelBtnStyle || {}
            return {
                btnFixedRightSlot,
                btnNextLineSlot,
                style: btnStyle || panelBtnStyle,
                parentScope: this
            }
        },
        // 样式变量
        styleVar () {
            let panelStyle = this.panelInfo?.theme?.panel || {}
            let customStyle = this.panelInfo?.customSetting?.style || {}
            if (this.panelInfo.backgroundImg) {
                // let img = require()
                customStyle.backgroundImage = `url(${process.env.VUE_APP_BASE_API + Attach_Url_By_Id + this.panelInfo.backgroundImg})`
            }
            let varStyle = {
                '--content-bg': this._attrs.contentBg,
                '--primary-color': this._attrs.primaryColor,
                '--text-color': this._attrs.textColor,
                '--import-text-color': this._attrs.importTextColor
            }
            return Object.assign(varStyle, panelStyle, customStyle)
        },
        // 是否展示标题
        showTitle () {
            // 获取自定义配置中的是否展示面板标题
            let customShowTitle = this.panelInfo.customSetting?.showTitle
            // 大屏类型下展示标题
            return customShowTitle || String(this.panelInfo.type) === '2'
        },
        // 是否需要适配
        isFit () {
            // 不使用适配
            let notFit = this.panelInfo.customSetting?.notFit
            if (notFit) {
                return false
            }
            // 默认大屏下适配屏幕
            return String(this.panelInfo.type) === '2'
        },
        // 块容器的最大高度（比例）
        maxBlockRow () {
            let rowArr = this.blockList.map(o => ((o.row || 0) + (o.sizey || 0)))
            return Math.max(...rowArr)
        },
        // 外边距
        spaceSize () {
            let { spaceSize } = Object.assign({}, this.panelInfo.theme || {}, this.panelInfo?.customSetting || {})
            return spaceSize || GlobalConst.panel.spaceSize
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
         * @description: 刷新面板（可单独刷新某块或内容）
         * @param {*} dealList：需要处理的refName列表，不传默认都处理
         * @return {*}
         */
        refresh (dealList) {
            let hasPanenl = !dealList || Object.prototype.toString.call(dealList) !== '[object Array]' || ~dealList.indexOf('panel')
            if (hasPanenl) {
                this.handlerBlocks('refresh', {})
            } else {
                this.handlerBlocks('refresh', {}, dealList)
            }
        },
        // 重置
        reset (dealList) {
            this.panelInfo.filterList.forEach(o => {
                this.$set(o, 'value', o.defaultValue)
            })
            let hasPanenl = !dealList || Object.prototype.toString.call(dealList) !== '[object Array]' || ~dealList.indexOf('panel')
            if (hasPanenl) {
                this.handlerBlocks('reset', {})
            } else {
                this.handlerBlocks('reset', {}, dealList)
            }
        },
        // 搜索
        search (params = {}, dealList) {
            this.handlerBlocks('search', params, dealList)
        },
        resize (params = {}, dealList) {
            this.handlerBlocks('resize', params, dealList)
        },
        /**
         * @description: 批量调用块中的方法
         * @param {String} fnName：方法名
         * @param {Object} params：需传递的参数
         * @param {Array} refNames：内容的refName数据
         */
        handlerBlocks (fnName, params, refNames = []) {
            if (!fnName) return
            let _refs = this.$refs || {}
            // 获取refNames中非block、非panel的部分
            let otherRefNames = refNames.filter(n => n.indexOf('block') === -1 && n.indexOf('panel') === -1)
            // 内容 的 refName 必须以 content 开头
            for (const _refName in _refs) {
                if (_refName.indexOf('block') === 0) {
                    // 获取到内容的ref
                    let blockRef = _refs[_refName][0] ? _refs[_refName][0] : _refs[_refName]
                    // 1. 当refNames不存在（调用所有）；2. refNames中存在该作用域
                    if (!refNames.length || ~refNames.indexOf(_refName)) {
                        typeof blockRef[fnName] === 'function' && blockRef[fnName](params)
                    } else if (otherRefNames.length) {  // 或者没有对块执行方法，传入otherRefNames，往下继续调用（在block中同步书写）
                        typeof blockRef[fnName] === 'function' && blockRef[fnName](params, otherRefNames)
                    }
                }
            }
        },
        // 请求面板及其块数据
        async init (panelId = this.panelId) {
            if (!panelId) {
                this.$message('缺少必要的面板id')
                return
            }
            if (this.blockList.length) {
                this.showExitAnimation = true
                setTimeout(() => {
                    // 最慢0.4s，需要停止退出动画
                    this.showExitAnimation = false
                }, 500)
            }
            this.loading = true
            // 请求并组装面板数据
            let panelConfig = await Get_Panel_Config({ panelId })
            let { blockList, panelInfo } = panelConfig
            // 判断面板js上是否存在 afterPanel 方法
            let diy_afterPanel = panelInfo?.customSetting?.afterPanel
            if (typeof diy_afterPanel === 'function') {
                // 有则先执行
                diy_afterPanel.call(this, panelConfig)
            }
            // 将组装面板配置后的方法提交给父组件
            this.$emit('afterPanel', panelConfig)
            this.showExitAnimation = false
            // 重置面板与块
            this.blockList = [], this.panelInfo = {}
            // 给块、面板赋值
            this.blockList = blockList, this.panelInfo = panelInfo
            // 组装按钮
            this.btnList = await Get_BtnList.call(this, this.panelInfo?.customSetting?.buttons)
            setTimeout(() => {
                this.loading = false
            })
        },
        // 获取块动画效果
        getBlockClassName (block) {
            let res = ''
            let blockCustomSettingStr = block?.blockChartOptions
            if (blockCustomSettingStr) {
                try {
                    let blockCustomSetting = JSON.parse(blockCustomSettingStr)
                    res = blockCustomSetting.className || ''
                } catch (err) {
                    res = ''
                }
            }
            if (!this.loading && block.changeType) {
                res += ` ${block.changeType}`
            }
            return res
        },
        // 获取块样式
        getBlockStyle (block) {
            // ====================== 位置信息 start =====================
            let maxsizex = block.maxsizex
            // 优先块宽度、块高度、上边距、右边距、下边距、左边距
            let containerWidth = `${block.sizex}%`
            let containerHeight = block.sizey + 'px'
            let containerLeft = `${block.col}%`
            let containerTop = block.row + 'px'
            if (maxsizex < 50) {  // 旧版本位置宽高信息计算
                containerWidth = `calc(${(block.sizex / maxsizex) * 100}% - ${(Math.round(maxsizex / block.sizex) - 1) * 10}px)`
                containerHeight = (50 * (block.sizey) - 10) + 'px'
                containerLeft = (block.col === 1 && block.sizex === maxsizex) ? 0 : `calc(${(block.col - 1) / maxsizex * 100}%  + 10px)`
                containerTop = (50 * (block.row - 1)) + 'px'
            }
            let res = {
                width: block.width || containerWidth,
                height: block.height || containerHeight,
                bottom: block.bottomDistance,
                right: block.rightDistance,
            }
            // left 的优先级比 right 高
            if (!res.right) res.left = block.leftDistance || containerLeft
            // top 的优先级比 bottom 高
            if (!res.bottom) res.top = block.topDistance || containerTop
            // 是否适配屏幕
            if (this.isFit) {
                // res.top = `${(block.topDistance ? 0 : block.row) / this.maxBlockRow * 100}% `
                // res.height = `${(block.height ? 0 : block.sizey) / this.maxBlockRow * 100}%`
                res.top = block.topDistance ? block.topDistance : `${block.row / this.maxBlockRow * 100}% `
                res.height = block.height ? block.height : `${block.sizey / this.maxBlockRow * 100}% `
            }
            // ====================== 位置信息 end =====================
            res.padding = this.spaceSize
            let blockStyle = this.panelInfo?.customSetting?.blockStyle || {}
            let blockThemeStyle = this.panelInfo?.theme?.panelBloclStyle || {}
            Object.assign(res, blockStyle, blockThemeStyle)
            return res
        }
    },
    watch: {
        panelId: {
            immediate: true,
            handler (newVal) {
                newVal && this.init()
            }
        }
    }
}
</script>

<style lang="scss" scoped>
@import './scss/blockInitAnimation.scss';
@import './scss/panelTheme.scss';
.panel-code {
    display: flex;
    flex-direction: column;
    color: var(--text-color);
    background-size: 100% 100%;
    background-repeat: no-repeat;
    font-size: 1rem;
    overflow: scroll;
    &.loading-panel_box {
        overflow: hidden;
    }
    .panel-block_box {
        position: relative;
        .block-item {
            display: flex;
            position: absolute;
            // overflow: hidden;
        }
    }
    .panel-filter_box {
        margin-top: 10px;
        margin-left: 10px;
    }
    .flex-1 {
        flex: 1;
    }
    .flex-none {
        flex: none;
    }
}

// 消失动画（慢慢缩小消失）
.scale-out-center {
	animation: scale-out-center 0.5s cubic-bezier(0.550, 0.085, 0.680, 0.530) both;
}
@keyframes scale-out-center {
    0% {
        transform: scale(1);
        opacity: 1;
    }
    100% {
        transform: scale(0);
        opacity: 1;
    }
}  

</style>