<template>
    <div class="project-header">
        <img
            v-if="showHeadBg && settingHeadBgImg"
            :src="makeImg(settingHeadBgImg)"
            @error="imgToggle('showHeadBg')"
            @load="imgToggle('showHeadBg')"
            class="project-header-bg"
            alt="">
        <div class="title p-r">
            <div class="s-logo" v-if="showLogo && settingLogo">
                <img
                    class="s-logo-img"
                    :src="makeImg(settingLogo, 'img')"
                    @error="imgToggle('showLogo')"
                    @load="imgToggle('showLogo')"
                    alt="">
            </div>
            <span class="pointer" @click="pushHome">{{settingTitle}}</span>
            <!-- 走马灯插槽 -->
            <slot name="marquee"></slot>
            <!-- 通用插槽 -->
            <slot></slot>
        </div>
        <div class="project-header-r floatR d-f p-r">
            <!-- <select-search></select-search> -->
            <div class="d-ib operaIconArea">
                <div class="icon-item" v-for="(i, index) in operateList" :key="index" :title="i.title">
                    <uploading-view v-if="isUploadingPart(i)" :icon="i.name" class="pointer"></uploading-view>
                    <bd-icon v-else-if="!isHistoryPart(i)" class="operaIcon pointer" :name="i.name"  @click="operate(i)"></bd-icon>
                    <!-- 历史足迹图标特殊处理 -->
                    <history-foot v-else :icon="i.name" class="pointer"></history-foot>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import { mapGetters } from 'vuex'
import FullScreen from '@/layout/mixin/FullScreen'
import HistoryFoot from '@/components/frame/HistoryFoot'
import UploadingView from '@/components/frame/UploadingView.vue'
const iconName = 'fullScreen-fill'
const historyId = 'history'
const uploadingId = 'uploadingFile'

export default {
    name: 'AppHeader',
    mixins: [FullScreen],
    components: {
        [HistoryFoot.name]: HistoryFoot,
        UploadingView
    },
    data () {
        return {
            operateList: [
                { id: uploadingId, name: 'refresh-fill', fun: null, title: '上传中文件' },
                { id: historyId, name: 'foot-fill', fun: null, title: '历史记录' },
                { id: 'message', name: 'message-line-fill', fun: 'pushMessage', title: '我的消息' },
                // { id: 'help', name: 'help-fill', fun: null, title: '疑问' },
                { id: 'openFull', name: iconName, fun: 'toggle', title: '全屏展开' },
                { id: 'logout', name: 'logOut-fill', fun: 'logout', title: '退出登录' },
            ],
            // 是否展示头部标题背景图片
            showHeadBg: false,
            // 是否展示头部标题主logo
            showLogo: false,
        }
    },
    computed: {
        ...mapGetters([
            'settingTitle',
            'settingHeadBgImg',
            'settingLogo',
            'homeUrl'
        ])
    },
    props: {

    },
    methods: {
        /**
         * 图片加载成功/失败事件，状态值更新为相反的状态值
         * @param {String} dataName 数据键名
         */
        imgToggle (dataName) {
            this[dataName] = !this[dataName]
        },
        // 判断是否历史足迹的模块
        isHistoryPart ({ id }) {
            return id === historyId
        },
        // 判断是否上传中附件的模块
        isUploadingPart ({ id }) {
            return id === uploadingId
        },
        logout () {
            this.$confirm('确认退出当前账号?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(async () => {
                await this.$store.dispatch('user/logout')
                // this.$router.push(`/login`)
                this.pushPage({
                    path: '/login',
                    title: '登录页'
                })
                // 用户主动注销的时候,不携带当前页面路径,因为可能存在用户是要切换身份,应该要回到首页,而且不确定原有页面地址参数数据是否还有效
                // this.$router.push(`/login?redirect=${this.$route.fullPath}`)
            }).catch(() => {
                // 用户取消退出操作
            })
        },
        operate (item) {
            if (item.fun && typeof this[item.fun] === 'function') {
                this[item.fun](item)
            }
        },
        // 跳转首页
        pushHome () {
            this.pushPage({ path: this.homeUrl, title: '首页' })
        },
        // 跳转消息列表页面
        pushMessage () {
            this.pushPage({ path: '/module/stander/list/sys_information/placeholder', title: '消息列表' })
        }
    }
}
</script>

<style lang='scss' scoped>
.project-header >>> {
    position: relative;
    padding: 0 $headTitlePadding - 4px;
    height: $appHeaderTitleHeight;
    @media screen and (max-width: $screen-middle) {
        height: $appHeaderTitleHeight - $screenDis;
    }
    color: #fff;
    font-size: 30px;
    background: linear-gradient(to bottom, $primary, $appTitleGradientBg);
    // background: url("../../assets/project/topBg.png") no-repeat;
    background-repeat: no-repeat;
    background-size: 100% 100%;
    background-position: center center;
    overflow: hidden;
    .project-header-bg {
        height: 100%;
        width: 100%;
        position: absolute;
        left: 0;
        top: 0;
        z-index: 0;
    }
    .title{
        font-size: 22px;
        font-weight: 600;
        letter-spacing: 1px;
        height: $appHeaderTitleHeight;
        line-height: $appHeaderTitleHeight;
        @media screen and (max-width: $screen-middle) {
            font-size: 21px;
            height: $appHeaderTitleHeight - $screenDis;
            line-height: $appHeaderTitleHeight - $screenDis;
        }
        display: inline-block;
        .s-logo {
            display: inline-block;
            height: $appHeaderTitleHeight;
            min-width: $appHeaderTitleHeight - 10px;
            vertical-align: top;
            position: relative;
            margin-right: -4px;
            .s-logo-img {
                height: $appHeaderTitleHeight - 10px;
                width: $appHeaderTitleHeight - 10px;
                position: absolute;
                top: 0;
                bottom: 0;
                margin: auto;
            }
        }
    }
    .operaIconArea{
        display: flex;
        align-items: center;
        height: $appHeaderTitleHeight;
        .icon-item {
            text-align: center;
            min-width: 40px;
            position: relative;
            .bd-icon {
                font-size: 20px;
                transition: all 0.6s;
                vertical-align: -0.1em;
                opacity: .9;
                transform: scale(1);
                &:hover {
                    transform: scale(1.3);
                    opacity: 1;
                }
            }
        }
    }
}
</style>
