<!--
 * @FilePath: @/components/frame/Panel/BlockContent/items/PCard/items/DefaultCard.vue
 * @Description: 默认卡片样式
-->
<template>
    <div ref="defaultCardRef" class="default-card">
        <bd-icon
            name="cardBg"
            :style="{fill: colorRgba(bgColor, 0.03), fontSize: bgSize}"
            class="card-bg noCursor">
        </bd-icon>
        <div class="card-left_box flex-v">
            <span
                :style="iconStyle"
                class="left-icon_box">
                <bd-icon :name="cardInfo.icon"></bd-icon>
            </span>
            <span class="marT-10">{{cardInfo.text}}</span>
        </div>
        <div @click="toLink" class="card-right_box">
            <countTo
                :startVal="0"
                :style="rightStyle"
                :endVal="countEnd"
                :duration="2000"
                :decimal="cardInfo.decimal || '0'"
                :class="{'is-link': cardInfo.url}">
            </countTo>
        </div>
        <div class="footer-side">
            <div class="line" :style="{background: bgColor}"></div>
        </div>
    </div>
</template>

<script>
import { Hex_To_RGBA } from '@/utils/color'
import countTo from 'vue-count-to'
export default {
    components: {
        countTo,
    },
    props: {
        // 卡片展示信息
        cardInfo: {
            type: Object,
            default: () => {}
        },
        // 基础配置
        baseConfig: {
            type: Object,
            default: () => {}
        },
    },
    data: () => ({
        // 色值转换
        colorRgba: Hex_To_RGBA,
        // 背景图片大小
        bgSize: '',
        // 当前自适应的时间戳（菜单更改时，图表需要resize，此参数用于防抖）
        resizeTime: 0,
    }),
    computed: {
        // 背景图片颜色
        bgColor () {
            return this.cardInfo?.rightStyle?.color || this.cardInfo?.iconStyle?.color
        },
        iconStyle () {
            let { color } = this.cardInfo.iconStyle
            return {
                ...this.cardInfo.iconStyle,
                backgroundColor: this.colorRgba(color, 0.2)
            }
        },
        rightStyle () {
            let { color } = this.cardInfo.iconStyle || bgColor || ''
            return {
                ...this.cardInfo.rightStyle,
                color: this.cardInfo.url ? color : ''
            }
        },
        // 卡片 value 值
        countEnd () {
            let res = Number(this.cardInfo.value || 0)
            return String(res) === 'NaN' ? 0 :res
        }
    },
    methods: {
        resize () {
            let { clientHeight, clientWidth } = this.$refs.defaultCardRef || {}
            let res = Math.min(clientHeight, clientWidth)
            res && this.$set(this, 'bgSize', res + 'px')
        },
        toLink () {
            if (!this.cardInfo.url) return
            this.pushPage({
                path: this.cardInfo.url,
                title: this.cardInfo.text
            })
        }
    },
    mounted () {
        window.addEventListener('resize', () => {
            this.resize()
        })
        this.$nextTick(() => {
            this.resize()
        })
    },
    watch: {
        // 监听左侧菜单更新
        '$store.state.app.sidebar.opened': {
            immediate: true,
            deep: false,
            handler (newVal) {
                let temp_time = Date.now()
                setTimeout(() => {
                    if (temp_time - this.resizeTime > 300) {
                        this.$set(this, 'resizeTime', temp_time)
                        this.resize()
                    }
                }, 300)
            }
        },
    }
}
</script>

<style lang="scss" scoped>
.default-card {
    height: 100%;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: space-between;
    .card-bg {
        position: absolute;
        right: 0;
        bottom: 0px;
        fill: rgba($primary, 0.05);
        z-index: 1;
    }
    .flex-v {
        display: flex;
        flex-direction: column;
        align-items: center;
    }
    .card-left_box {
        padding: $padding;
        .left-icon_box {
            padding: 5px;
            border-radius: 50%;
        }
    }
    .card-right_box {
        padding: $padding;
        padding-left: 0;
        font-size: 24px;
        z-index: 5;
        .is-link {
            cursor: pointer;
            text-decoration: underline;
        }
    }
    .footer-side {
        width: calc(100% - 20px);
        height: 4px;
        background: rgba(204, 204, 204, 0.5);
        position: absolute;
        left: 10px;
        bottom: 10px;
        .line {
            width: 40%;
            height: 100%;
        }
    }
}
</style>