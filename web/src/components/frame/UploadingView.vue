<!--
 * @FilePath: @/components/frame/UploadingView.vue
 * @Description: 正在上传中的文件展示
-->
<template>
    <el-popover
        placement="bottom-start"
        width="310"
        :trigger="isWarning ? 'manual' : 'hover'"
        v-model="isWarning">
        <div slot="reference" title="正在上传的附件信息" class="history-box">
            <el-badge
                :hidden="!uploadingList.length"
                :value="uploadingList.length"
                :max="99"
                class="uploading-badge">
                <bd-icon :name="icon"></bd-icon>
            </el-badge>
        </div>
        <div 
            class="uploading-view h-records-box marginB"
            :class="{'is-hide':!uploadingList.length}">
            <template v-if="uploadingList.length">
                <el-scrollbar class="hiddenX h-records-content">
                    <ul
                        class="h-records-list">
                        <li
                            v-for="(f, f_index) in uploadingList"
                            :key="f_index"
                            class="h-records-item">
                            <div class="flex-1">
                                <span class="h-records-index">{{f_index + 1}}. </span>
                                <div class="h-records-text">{{f.name || '未命名文件'}}</div>
                            </div>
                            <span class="flex-none">{{f.done | getProgress(f)}}</span>
                        </li>
                    </ul>
                </el-scrollbar>
                <div class="warning-tip">
                    <bd-icon name="tip"></bd-icon>
                    此时关闭浏览器将会丢失以上上传中附件
                </div>
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
export default {
    components: {
        NoData,
    },
    props: {
        icon: {
            type: String
        }
    },
    data: () => ({
        isWarning: false,
        // 时间函数
        timer: null,
    }),
    computed: {
        ...mapGetters(['uploadingList'])
    },
    filters: {
        getProgress (value, item) {
            let all = item.all
            return (value / all * 100).toFixed(2) + '%'
        }
    },
    methods: {
        handleWindowClose (event) {
            // 这里可以执行一些逻辑，例如弹窗提示用户是否确认关闭
            // alert('当前窗口存在还未提交成功的文件，确认关闭吗？')
            if (this.uploadingList?.length) {
                event = event || window.event
                this.isWarning = true
                this.timer = setTimeout(() => {
                    this.isWarning = false
                }, 5000)
                // 取消关闭
                event.preventDefault()
                // Chrome需要返回一个值来触发弹窗提示
                if (event) {
                    event.returnValue = ''
                }
                return ''
            }
        }
    },
    mounted () {
        // 监听浏览器窗口关闭事件
        window.addEventListener('beforeunload', this.handleWindowClose)
    },
    destroyed () {
        clearTimeout(this.timer)
        window.removeEventListener('beforeunload', this.handleWindowClose)
    },
}
</script>

<style lang='scss' scoped>
.uploading-badge::v-deep {
    .el-badge__content {
        transform: scale(0.7);
        left: 12px;
        top: -4px;
        right: auto;
    }
}
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
            // padding-right: 5px;
            .h-records-item {
                width: 100%;
                padding: 5px;
                cursor: pointer;
                display: flex;
                align-items: top;
                .flex-1 {
                    flex: 1
                }
                .flex-none {
                    flex: none;
                }
                .h-records-index {
                    width: 24px;
                    vertical-align: top;
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
    .warning-tip {
        color: $danger;
        text-align: center;
    }
    .h-noData {
        height: 100%;
    }
}
</style>