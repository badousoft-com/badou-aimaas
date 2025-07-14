<!--
 * @FilePath: @/components/frame/Panel/BlockContent/items/PMultiGraph.vue
 * @Description: 多图
-->
<template>
    <view-box :isError="!_imgList.length">
        <div class="bd-multi-graph">
            <el-carousel
                v-if="showType === '1' || showType === '3'"
                :type="showType === '3' ? 'card' : ''"
                :interval="_interval"
                :class="{'carousel-box': showType === '1', 'card-carousel': showType === '3'}">
                <el-carousel-item
                    v-for="(i, i_index) in _imgList"
                    :key="i_index">
                    <div class="img-box">
                        <!-- <bd-icon class="fail-img-icon" name="imgFail"></bd-icon> -->
                        <img
                            :src="i.src"
                            alt=""
                            srcset=""
                            :onerror="failImg"
                            :style="imgStyle"
                            class="img carousel-img">
                    </div>
                </el-carousel-item>
            </el-carousel>
            <div
                v-else
                :class="{
                    'horizontal-layout': imgConfig.direction !== 'vertical',
                    'flex-wrap': imgConfig.wrap
                }"
                class="tile-img-list">
                <img
                    v-for="(i, i_index) in _imgList"
                    :key="i_index"
                    :src="i.src"
                    alt=""
                    srcset=""
                    :style="imgStyle"
                    :onerror="failImg"
                    class="img">
            </div>
        </div>
    </view-box>
</template>

<script>
import ViewBox from '@/components/frame/Panel/ViewBox.vue'
import { Attach_Url_By_Id } from '@/api/frame/attach.js'
export default {
    components: {
        ViewBox
    },
    props: {
        // 图片列表
        imgList: {
            type: Array,
            default: () => []
        },
        // 其余属性值
        otherAttrs: {
            type: Object,
            default: () => {}
        },
        // 展示形式（轮播展示：1，并列展示：2，组合展示：3）
        // ----- TODO 组合展示待确认
        showType: {
            type: String,
            default: ''
        },
        chartOptions: {
            type: Object,
            default: () => {}
        },
        // 切换间隔
        interval: {
            type: [String, Number],
            default: 5000
        }
    },
    data: () => ({
        failImg: 'this.parentElement.className="fail-img img-box";this.src="' + require('@/assets/image/frame/failImg.png') + '"'
    }),
    computed: {
        _imgList () {
            let orignImgs = this.imgList || []
            let res = orignImgs.map(o => {
                return {
                    ...o,
                    src: process.env.VUE_APP_BASE_API + Attach_Url_By_Id + o.multiImage
                }
            })
            return res
        },
        // 图片配置信息
        /**
         * imgInfo 的可配置信息
         * {
         *      direction: 图片方向，String。可能值 vertical （垂直）默认横向 ----- 仅对平铺模式下起效
         *      wrap: 是否换行，Boolean。------- 仅对平铺模式下横向方向有效
         *      width: 图片宽度，Srting。80%、100px 等
         *      height: 图片高度，String。80%、100px 等
         * }
         */
        imgConfig () {
            let res = {}
            try {
                let chartOptions = this.chartOptions ? JSON.parse(this.chartOptions) : {}
                res = chartOptions.imgInfo || {}
            } catch (error) {
                res = {}
            }
            return res
        },
        // 图片样式
        imgStyle () {
            let { width, height } = this.imgConfig
            let res = {
                width,
                height,
            }
            Object.keys(res).forEach(key => {
                if (!res[key]) {
                    delete res[key]
                }
            })
            return res
        },
        // 切换间隔
        _interval () {
            return Math.max(this.interval, 2000)
        }
    },
}
</script>

<style lang="scss" scoped>
.bd-multi-graph {
    height: 100%;
    width: 100%;
    overflow: hidden;
    // 平铺
    .tile-img-list {
        width: 100%;
        height: 100%;
        text-align: center;
        overflow: scroll;
        &.horizontal-layout {
            display: flex;
            .img {
                display: inline-block;
                margin: 0;
            }
        }
        &.flex-wrap {
            flex-wrap: wrap;
        }
        .img {
            display: block;
            max-width: 100%;
            max-height: 100%;
            margin: 0 auto;
        }
    }
    // 切换渲染
    .carousel-box {
        width: 100%;
        height: 100%;
        text-align: center;
        .img-box {
            height: 100%;
            .carousel-img {
                height: 100%;
                object-fit: cover;
            }
        }
        /deep/ .el-carousel__container {
            height: 100%;
            width: 100%;
            .el-carousel__item {
                height: 100%;
                width: 100%;
            }
        }
    }
    .card-carousel {
        .el-carousel__item {
            display: flex;
            align-items: center;
            .img-box {
                overflow: hidden;
                img {
                    width: auto;
                    height: auto;
                    max-width: 100%;
                    max-height: 100%;
                    object-fit: cover;
                }
            }
        }
    }
}
</style>