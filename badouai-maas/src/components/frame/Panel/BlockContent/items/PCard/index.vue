<!--
 * @FilePath: @/components/frame/Panel/BlockContent/items/PCard/index.vue
 * @Description: 面板 - 卡片
-->
<template>
    <div
        v-loading="loading"
        v-bind="loadingAttrs"
        :style="{'--space-size': spaceSize + 'px', ...(cardInfo.style || {})}"
        :class="{'is-multi': cardList.length > 1}"
        class="p-card h-per-100">
        <div
            v-for="(c, c_index) in cardList"
            :key="c_index"
            :style="itemStyle"
            :class="{'no-shadow': closeShadow}"
            class="card_item">
            <template v-if="!componentPath">
                <component
                    ref="cardItemRef"
                    :is="cardName"
                    :cardInfo="c"
                    :baseConfig="_baseConfig"
                    :data="cardData"
                    v-bind="diy_attrs">
                </component>
            </template>
            <div v-else :id="'cardItem-' + c_index"></div>
        </div>
    </div>
</template>

<script>
import Vue from 'vue'
import ReportCommonAttrs from '../mixins/ReportCommonAttrs.vue'
import DefaultCard from './items/DefaultCard.vue'
import SingleCard from './items/SingleCard.vue'
import GlobalConst from '@/service/global-const'
import { Is_Array, Is_Object } from '@/utils/data-type'
export default {
    inheritAttrs: false,
    mixins: [ReportCommonAttrs],
    components: {
        DefaultCard,
        SingleCard,
    },
    data: () => ({
        cardList: [],
        // 接口返回的数据
        cardData: null
    }),
    computed: {
        // 卡片名称：根据此参数，决定使用item中的哪个卡片组件
        cardName () {
            return this.cardInfo?.cardName || 'defaultCard'
        },
        // 自定义卡片信息
        cardInfo () {
            return this.customSetting?.cardInfo || {}
        },
        // 动态组件路径
        componentPath () {
            return this.cardInfo.componentPath || ''
        },
        // 基础样式等设置
        _baseConfig () {
            let res = this.cardInfo?.baseConfig || {}
            Object.keys(this.dataDefInfo).forEach(key => {
                let value = this.dataDefInfo[key]?.[0]?.fieldName || ''
                res[key] = res[key] || value
            })
            res.leftColor = res.leftColor || this.themeInfo.primaryColor
            return res
        },
        // 左侧图标样式
        leftIconStyle () {
            let { leftColor, leftWidth, leftTop } = this._baseConfig
            let res = {
                color: leftColor,
                fontSize: leftWidth ? leftWidth + 'px' : '',
                marginTop: leftTop ? leftTop + '%' : '',
                // backgroundColor: this.colorRgba(leftColor, 0.2)
            }
            return res
        },
        // 右侧样式
        rightStyle () {
            let { centerTopFontSize } = this._baseConfig
            let res = {
                fontSize: centerTopFontSize ? centerTopFontSize + 'px' : '',
                color: this._baseConfig.callback ? this._baseConfig.leftColor : ''
            }
            return res
        },
        // 每个卡片样式
        itemStyle () {
            let { width, height, itemStyle = {} } = this.cardInfo
            let space = 0
            if (this.cardList.length > 1) {
                space = this.spaceSize
            }
            return {
                width: `calc(${width || '100%'} - ${space}px)`,
                height: `calc(${height || '100%'} - ${space}px)`,
                ...itemStyle
            }
        },
        // 间隔边距
        spaceSize () {
            return parseFloat(this.themeInfo.spaceSize || GlobalConst.panel.spaceSize)
        },
        // 是否展示卡片阴影
        closeShadow () {
            return this.cardInfo?.closeShadow || false
        },
        // 需要传递给自定义组件的参数
        diy_attrs () {
            return {
                ...this.$attrs,
                blockScope: this.blockScope,
                panelScope: this.panelScope,
                customSetting: this.customSetting
            }
        }
    },
    methods: {
        search (params = {}) {
            // 数据请求
            this.loadData(params).then(res => {
                this.cardData = res
                this.setCardList(res)
                // 判断是否为自定义组件
                if (this.componentPath) {
                    // 挂载自定义组件
                    this.cardList.forEach((c, c_index) => {
                        let comId = 'cardItem-' + c_index
                        let _params = {
                            ...this.diy_attrs,
                            cardInfo: c,
                            baseConfig: this._baseConfig,
                            data: this.cardData,
                        }
                        this.mountComVue(comId, _params)
                    })
                }
            })
        },
        // 配置总数卡片列表
        setCardList (data) {
            if (Is_Object(data)) {
                // if (Object.keys(data).length) {
                if (this.cardInfo.list) {
                    this.cardList = this.cardInfo.list.map(c => {
                        let { iconStyle = {}, rightStyle = {} } = c
                        return {
                            ...c,
                            iconStyle: {
                                ...this.leftIconStyle,
                                ...iconStyle,
                            },
                            rightStyle: {
                                ...this.rightStyle,
                                ...rightStyle
                            },
                            value: (c.dataKey ? data[c.dataKey] : c.value) || 0
                        }
                    })
                } else {
                    // 基于现有数据更改的，如返回数据跟踪不一样，需做对应更改
                    /** 20220917 返回数据格式：
                     * {centerBottomText: "待处理事项",centerTopText: 0}
                     */
                    let item = {
                        icon: this._baseConfig.leftClassName,
                        value: data.centerBottomText,
                        text: data.centerTopText,
                        url: this._baseConfig.callback,
                        iconStyle: this.leftIconStyle,
                        rightStyle: this.rightStyle,
                        ...this.cardInfo,
                    }
                    this.cardList = [item]
                }
                // }
            } else if (Is_Array(data)) {
                this.cardList = data
            }
        },
        // 重新计算尺寸
        resize () {
            if (!this.$refs.cardItemRef) return
            for (let i = 0; i < this.$refs.cardItemRef.length; i++) {
                let el = this.$refs.cardItemRef[i]
                typeof el.resize === 'function' && el.resize()
            }
        },
        // 挂载自定义组件
        async mountComVue (comId, params) {
            try {
                let pageVue = await require(`@/components/business${this.componentPath}`).default
                const Instance = Vue.extend(pageVue)
                // 创建实例，挂载指定DOM
                new Instance({
                    // 传递数据给组件
                    propsData: {
                        // 当前页面作用域
                        scope: this,
                        ...params,
                    }
                }).$mount(`#${comId}`)
            } catch (error) {
                console.error(`
                    【检查文件@/components/business${this.componentPath}】
                    1. 文件路径找不到 / 路径错误
                    2. 文件内容编写有误
                    3. 查看下面的详细错误信息
                `)
                console.log(error)
            }
        }
    },
    mounted () {
        this.search()
    }
}
</script>

<style lang="scss" scoped>
.p-card {
    display: flex;
    flex-wrap: wrap;
    .card_item {
        display: inline-block;
        height: 100%;
    }
    &.is-multi {
        // 有点疑虑，待再检查
        padding: calc(var(--space-size) / 2);
        padding-left: 0;
        overflow: scroll;
        .card_item {
            box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.1);
            margin-left: var(--space-size);
            margin-top: var(--space-size);
            border-radius: $borderRadius;
            &.no-shadow {
                box-shadow: none;
            }
        }
    }
}
</style>