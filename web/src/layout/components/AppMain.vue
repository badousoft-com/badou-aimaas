<template>
    <section class="app-main">
        <el-scrollbar wrap-class="scrollbar-wrapper" class="h-per-100 hideX" :class="Is_Mobile()? 'is-mobile' : ''">
            <!-- transition需要在el-scrollbar内过渡动画才能生效 -->
            <transition name="fade-transform" mode="out-in">
                <keep-alive :include='keepAliveList' :max="1">
                    <router-view :key="key" class="children-router"/>
                </keep-alive>
            </transition>
        </el-scrollbar>
    </section>
</template>
<script>
import { mapGetters } from 'vuex'
import { Is_Mobile } from '@/utils/browser.js'
export default {
    name: 'AppMain',
    data () {
        return {
            // 判断是否为移动端
            Is_Mobile: Is_Mobile,
        }
    },
    computed: {
        // 获取状态管理库的【缓存树】
        ...mapGetters([
            'keepAliveList'
        ]),
        key () {
            // 完成解析后的 URL，包含查询参数和 hash 的完整路径
            // 存在场景，前面路径一样但查询参数不同，所以这里不能使用path，path没有查询参数
            return this.$route.fullPath
        }
    }
}
</script>

<style scoped lang="scss">
.app-main {
    height: calc(100vh - #{$appHeaderFirstMenuHeight} - #{$appHeaderHeight});
    width: 100%;
    position: relative;
    overflow: hidden;
}
.is-mobile {
    /deep/ .el-scrollbar__view {
        height: auto;
    }
}
.fixed-header + .app-main {
    padding-top: 50px;
}
.children-router {
    margin: 0 $space $space !important;
    height: calc(100vh - #{$space} - #{$appHeaderHeight} - #{$locationHeight});
    // height: calc(100% - #{$space});
    border-radius: 4px;
    overflow: auto;
    &.defaultBg {
        box-shadow: 0px 0px 3px 2px rgba(16,16,16,0.1);
    }
}
</style>

<style lang="scss">
// fix css style bug in open el-dialog
.el-popup-parent--hidden {
    .fixed-header {
        padding-right: 15px;
    }
}
</style>
