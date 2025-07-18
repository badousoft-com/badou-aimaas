<template>
    <div class="wide-panel"
        :style="{'height': _height + 'px'}">
        <div class="title">
            <div class="title-left">
                <span>{{title}}</span>
                <span
                    v-if="subtitle"
                    :style="{'color': subtitleColor, 'fontSize': subtitleFontSize + 'px', 'fontWeight': 'normal'}">{{subtitle}}</span>
            </div>
            <div class="see-more color9">
                <!-- 如果没有传入跳转地址，则不显示查看更多 -->
                <span @click="moreClick" v-if="path">查看更多></span>
            </div>
        </div>

        <div class="content"
            :class="{ scrollbar: isScroll }"
            :style="{'height': _contentHeight + 'px'}">
            <slot>
                <div class="slot-content">
                    <!-- 无数据展示 -->
                    <bd-no-data class="noDataTip"></bd-no-data>
                </div>
            </slot>
        </div>
    </div>
</template>

<script>
import NoData from '@/components/frame/NoData'
export default {
    name: 'wide-panel',
    components: {
        [NoData.name]: NoData
    },
    props: {
        // 面板标题
        title: {
            type: String,
            default: '请通过:title传入标题'
        },
        subtitle: {
            type: String,
            default: ''
        },
        subtitleColor: {
            type: String,
            default: 'red'
        },
        subtitleFontSize: {
            type: Number,
            default: 14
        },
        // 查看更多的跳转地址
        path: {
            type: String
        },
        // 查看更多的跳转title
        pathTitle: {
            type: String
        },
        // 查看更多的跳转参数
        queryData: {
            type: Object,
            default: () => { }
        },
        // 是否开启滚动，默认开启
        isScroll: {
            type: Boolean,
            default: true
        },
        // 面板高度
        height: {
            type: String,
            default: '250'
        }
    },
    computed: {
        // 面板总高度
        _height () {
            // 最小高度为250px
            if (this.height < 250) {
                return 250
            }
            return this.height
        },
        // content高度
        _contentHeight () {
            return this.height - 60
        }
    },
    methods: {
        // 查看更多点击
        moreClick () {
            this.pushPage({ path: this.path, title: this.pathTitle || this.title, query: this.queryData })
        }
    },
}
</script>

<style lang="scss" scoped>
.wide-panel {
    width: 100%;
    padding: 10px;
    margin-bottom: 20px;
    background-color: #fff;
}
.title {
    display: flex;
    height: 40px;
    justify-content: space-between;
    .title-left {
        font-weight: bold;
        font-size: 1rem;
        padding: 10px;
    }
    .see-more {
        margin-right: 10px;
        align-self: center;
        cursor: pointer;
    }
}
.content {
    overflow: hidden;
}
// .scrollbar::-webkit-scrollbar {
//     display: none; /* Chrome Safari */
// }
.scrollbar {
    // scrollbar-width: none; /* firefox */
    // -ms-overflow-style: none; /* IE 10+ */
    overflow-x: hidden;
    overflow-y: auto;
}
.slot {
    height: 100%;
    line-height: 170px;
    background-color: rgb(236, 236, 236);
}

.slot-content {
    display: flex;
    justify-content: center;
    width: 100%;
    height: 100%;
    overflow: hidden;
    .noDataTip {
        width: 30%;
    }
}
</style>