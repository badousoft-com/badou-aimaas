<template>
    <el-popover
        placement="bottom-start"
        width="200"
        trigger="hover"
        popper-class="padR-0">
        <div slot="reference" title="历史记录" class="history-box">
            <bd-icon :name="icon"></bd-icon>
        </div>
        <div 
            class="h-records-box marginB"
            :class="{'is-hide':!historyRecords.length}">
            <template v-if="historyRecords.length">
                <el-scrollbar class="hiddenX h-records-content">
                    <ul
                        class="h-records-list">
                        <li
                            v-for="(h, h_index) in _historyRecords"
                            :key="h_index"
                            @click="toHistory(h, h_index)"
                            class="h-records-item">
                            <span class="h-records-index">{{h_index + 1}}. </span>
                            <div class="h-records-text">{{h.title || '未命名地址'}}</div>
                            <span @click.stop="deleteHistoryItem(h_index)" class="h-delete-item-btn marL-5">
                                <bd-icon name="cancel-fill"></bd-icon>
                            </span>
                        </li>
                    </ul>
                </el-scrollbar>
                <div @click="clearHistory" class="h-clear-btn">清除历史记录</div>
            </template>
            <div v-else class="h-noData">
                <no-data></no-data>
            </div>
        </div>
    </el-popover>
</template>
<script>
import { mapGetters } from 'vuex'
import NoData from '@/components/frame/NoData'
import { Get_History, Listen_History, Remove_History, Splice_History } from '@/service/history'
import GlobalConst from '@/service/global-const'
export default {
    name: 'history-foot',
    components: {
        NoData,
    },
    props: {
        icon: {
            type: String
        }
    },
    data () { // 定义页面变量
        return {
            // 历史记录
            historyRecords: Get_History() || [],
        }
    },
    computed: {
        ...mapGetters([
            'location'
        ]),
        // 用于展示的历史记录数据
        _historyRecords () {
            // 展示最大长度
            let maxLen = GlobalConst.history.maxLength
            return this.historyRecords.slice(0, maxLen)
        }
    },
    methods: { // 定义函数
        // 点击历史记录
        toHistory (item) {
            this.pushPage(item)
        },
        // 清除历史记录
        clearHistory () {
            Remove_History()
        },
        // 清除单个历史记录
        deleteHistoryItem (index) {
            Splice_History(index)
        },
    },
    // 可访问当前this实例
    created () {
        Listen_History(() => {
            let history = Get_History() || []
            this.$set(this, 'historyRecords', history)
        })
    },
    // 挂载完成，可访问DOM元素
    mounted () {},
}
</script>
<style lang='scss' scoped>
$historyContentHeight: 200px;
.h-records-box::v-deep  {
    height: $historyContentHeight;
    // 处理显示内容为空时滚动条可以滚动的bug
    &.is-hide {
        .el-scrollbar__view {
            height: 100%;
            overflow: hidden;
        }
    }
    .el-scrollbar__wrap {
        width: 100%;
    }
    .h-records-content {
        height: calc(#{$historyContentHeight} - 2px);
        overflow: auto;
        &>.is-vertical {
            display: none;
        }
        .h-records-list {
            margin: 0;
            padding: 0;
            padding-right: 5px;
            .h-records-item {
                width: 100%;
                padding: 5px;
                cursor: pointer;
                display: flex;
                align-items: top;
                .h-records-index {
                    width: 24px;
                }
                .h-records-text {
                    display: inline-block;
                    width: calc(100% - 40px);
                }
                .h-delete-item-btn {
                    display: inline-block;
                    visibility: hidden;
                    line-height: 0.8;
                    font-size: 10px;
                }
                &:hover {
                    background-color: rgba($color: $primary, $alpha: 0.1);
                    color: $primary;
                    .h-delete-item-btn {
                        visibility: visible;
                        color: rgba($color: $primary, $alpha: 0.5);
                        &:hover {
                            color: $primary;
                        }
                    }
                }
            }
        }
    }
    .h-clear-btn {
        color: $primary;
        text-align: center;
        cursor: pointer;
    }
    .h-noData {
        height: 100%;
    }
}
</style>