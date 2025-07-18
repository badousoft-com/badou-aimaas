<template>
    <div class="m-card" :class="{'border': border}">
        <div class="m-card-title" v-if="title">
            <m-title
                :title="title"
                :name="icon">
                <template v-slot:right>
                    <slot name="right"></slot>
                </template>
            </m-title>
        </div>
        <div class="m-card-main">
            <!-- 卡片模块核心内容插槽 -->
            <slot></slot>
        </div>
    </div>
</template>

<script>
import MTitle from '@/components/frame/Common/MTitle'
export default {
    name: 'm-card',
    components: {
        [MTitle.name]: MTitle
    },
    props: {
        title: [String, Boolean],
        icon: {
            type: String,
            default: 'pillar-fill'
        },
        border: {
            type: Boolean,
            default: false
        }
    },
    data () {
        return {
        }
    },
    methods: {
    },
    created () {
    },
    watch: {
    },
}
</script>

<style lang="scss" scoped>
    .m-card {
        background: $contentInBg;
        border-radius: $borderRadius;
        overflow: hidden;
        box-shadow: 0px 0px 10px 0px rgba(16,16,16,0.1);
        .m-card-title {
            // // background: $primary;
            // padding: 0 $space;
            // // color: $contentInBg;
            // font-size: $fontL;
            // color: $fontCT;
            // height: $appHeaderFirstMenuHeight;
            // line-height: $appHeaderFirstMenuHeight;
            border-bottom: 1px solid $lineColor;
            .m-card-title_word {
                vertical-align: middle;
            }
            .bd-icon {
                margin: 0 -5px;
                height: 1em;
            }
            .cardBtn {
                height: $buttonHeight - 8px;
                line-height: $buttonHeight - 8px;
            }
        }
        &.border {
            border: 1px solid $lineColor;
            .m-card-main {
                .m-tab {
                    border: none !important
                }
            }
        }
    }
    // .m-card >>> .cardBtn {
    //     background: rgba($white, 0.2);
    //     border: none;
    //     color: #fff;
    //     font-weight: 600;
    // }
</style>
