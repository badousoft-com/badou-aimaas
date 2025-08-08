<template>
    <div class="h-per-100 bd-skeleton">
        <div
            class="h-per-100"
            :class="{'is-hide': loading}">
            <slot></slot>
        </div>
        <div
            v-if="loading"
            class="bd-skeleton__default">
            <div class="bd-skeleton__main defaultBg">
                <div
                    class="padding"
                    :class="[
                        {
                            'bd-skeleton--blink': blink
                        }
                    ]">
                    <div class="bd-skeleton--form">
                        <div class="bd-skeleton__title"></div>
                        <div class="bd-skeleton__content">
                            <div
                                v-for="(i, index) in rows"
                                :key="index"
                                class="bd-skeleton__row">
                                <div
                                    v-for="(j, j_index) in 2"
                                    :key="j_index"
                                    class="bd-skeleton__item">
                                    <div class="bd-skeleton__label"></div>
                                    <div class="bd-skeleton__value"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
export default {
    name: 'bd-skeleton',
    components: {},
    inheritAttrs: false,
    props: {
        // 是否闪动
        blink: {
            type: Boolean,
            default: true
        },
        // 展示数据量
        rows: {
            type: Number,
            default: 6
        },
        // 是否使用骨架屏过渡展示
        loading: {
            type: Boolean,
            default: false
        }
    }
}
</script>
<style lang='scss' scoped>
// 表单标签宽度
$formLabelWidth: 100px;
// 单个项高度
$formItemHeight: 32px;
// 骨架屏背景色
$skeletonBackground: #f2f3f5;
// marginBottom
$marginBottom: 2 * $padding;
// marginLeft
$marginLeft: 20px;
// 单个模块宽度
$itemWidth: 46%;

@mixin shade {
    background: linear-gradient(90deg, hsla(0,0%,74.5%,.2) 25%, hsla(0,0%,50.6%,.24) 37%, hsla(0,0%,74.5%,.2) 63%);
    background-size: 400% 100%;
    animation: skeleton-loading 1.4s ease infinite;
}
.bd-skeleton {
    transition: all 0.06s;
    .is-hide {
        height: 0;
        opacity: 0;
    }
    .bd-skeleton__default {
        .bd-skeleton__main {
        }
        .bd-skeleton--form {
            .bd-skeleton__title {
                width: 30%;
                height: $formItemHeight;
                background: $skeletonBackground;
                margin-bottom: $marginBottom;
            }
            .bd-skeleton__content {
                font-size: 0;
                overflow: hidden;
                .bd-skeleton__row {
                    margin-bottom: $marginBottom;
                    width: calc(100% + #{$marginLeft});
                    overflow: hidden;
                    font-size: 0;
                    .bd-skeleton__item {
                        width: $itemWidth;
                        display: inline-block;
                        margin-right: 100% - 2 * $itemWidth - 2%;
                        &:last-of-type {
                            margin-right: 0;
                        }
                        .bd-skeleton__label {
                            width: $formLabelWidth;
                            display: inline-block;
                            height: $formItemHeight;
                            background: $skeletonBackground;
                        }
                        .bd-skeleton__value {
                            width: calc( 100% - #{$formLabelWidth} - #{$marginLeft});
                            margin-left: $marginLeft;
                            display: inline-block;
                            height: $formItemHeight;
                            background: $skeletonBackground;
                        }
                    }
                }
            }
        }
        .bd-skeleton--blink {
            .bd-skeleton--form {
                .bd-skeleton__title {
                    @include shade;
                }
                .bd-skeleton__content {
                    .bd-skeleton__row {
                        .bd-skeleton__item {
                            .bd-skeleton__label {
                                @include shade;
                            }
                            .bd-skeleton__value {
                                @include shade;
                            }
                        }
                    }
                }
            }
        }
    }
}

@keyframes skeleton-loading {
    0% {
        background-position: 100% 50%
    }
    to {
        background-position: 0 50%
    }
}
</style>