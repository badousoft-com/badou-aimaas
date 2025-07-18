<!--
 * @FilePath: @/components/frame/Panel/BlockContent/items/PCard/items/SingleCard.vue
 * @Description: 简单的总数卡片
-->
<template>
    <div class="single-card_view">
        <img
            v-if="cardInfo.imgPath"
            :src="require(`@/assets/image${cardInfo.imgPath}`)"
            alt="" srcset=""
            class="left_img">
        <div class="right-content">
            <span>{{cardInfo.text}}</span>
            <span class="value-text_box">
                <countTo
                    :startVal="0"
                    :endVal="countEnd"
                    :duration="1500"
                    :decimal="cardInfo.decimal || '2'"
                    :style="{fontSize: baseConfig.centerTopFontSize}"
                    class="value_text regular-number">
                </countTo>
                <!-- <span v-if=""></span> -->
            </span>
        </div>
    </div>
</template>

<script>
import countTo from 'vue-count-to'
export default {
    inheritAttrs: false,
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
    computed: {
        // 卡片 value 值
        countEnd () {
            let res = Number(this.cardInfo.value || 0)
            return String(res) === 'NaN' ? 0 :res
        },
    },
    methods: {
        resize () {
            let { clientHeight, clientWidth } = this.$refs.defaultCardRef || {}
            let res = Math.min(clientHeight, clientWidth)
            res && this.$set(this, 'bgSize', res + 'px')
        },
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
.single-card_view {
    height: 100%;
    display: flex;
    font-size: 1rem;
    .left_img {
        height: 100%;
    }
    .right-content {
        display: flex;
        flex-direction: column;
        justify-content: center;
        padding-left: $padding;
        .value-text_box {
            margin-top: 5px;
            .value_text {
                color: var(--import-text-color);
                font-size: 1.7rem;
            }
        }
    }
}
</style>