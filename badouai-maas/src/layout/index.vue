<template>
    <div :class="classObj" class="app-wrapper">
        <!-- <div v-if="device==='mobile'&&sidebar.opened" class="drawer-bg" @click="handleClickOutside" ></div> -->
        <div class="app-header">
            <app-title>
                <template v-slot:marquee>
                    <!-- 此处填入跑马灯插槽具体内容 -->
                </template>
            </app-title>
            <app-first-menu @delayShow="delayShowSiderbar"></app-first-menu>
        </div>
        <sidebar :isShow="isShowSiderbar" class="sidebar-container"></sidebar>
        <div class="main-container">
            <div :class="{'fixed-header':fixedHeader}">
                <navbar></navbar>
            </div>
            <app-main></app-main>
        </div>
    </div>
</template>

<script>
import { Navbar, Sidebar, AppMain, AppTitle, AppFirstMenu } from './components'
import ResizeMixin from './mixin/ResizeHandler'

export default {
    name: 'Layout',
    components: {
        Navbar,
        Sidebar,
        AppMain,
        AppTitle,
        AppFirstMenu
    },
    mixins: [ResizeMixin],
    computed: {
        sidebar () {
            return this.$store.state.app.sidebar
        },
        device () {
            return this.$store.state.app.device
        },
        fixedHeader () {
            return this.$store.state.settings.fixedHeader
        },
        classObj () {
            return {
                hideSidebar: !this.sidebar.opened,
                openSidebar: this.sidebar.opened,
                withoutAnimation: this.sidebar.withoutAnimation,
                mobile: this.device === 'mobile'
            }
        }
    },
    data () {
        return {
            // 状态：是否展示左侧菜单
            isShowSiderbar: true
        }
    },
    methods: {
        /**
         * 延迟展示左侧菜单
         * 在数据切换过快时菜单出现bug，上一次的选中会在切换后导致更新数据也被选中
         */
        delayShowSiderbar () {
            this.isShowSiderbar = false
            setTimeout(() => {
                this.isShowSiderbar = true
            })
        },
        handleClickOutside () {
            this.$store.dispatch('app/closeSideBar', { withoutAnimation: false })
        }
    }
}
</script>

<style lang="scss" scoped>
@import "~@/styles/mixin.scss";
.app-header{
    width: 100%;
    position: fixed;
    top:0 ;
    z-index: 1001;
    background: #fff;
}
.app-wrapper {
    @include clearfix;
    position: relative;
    height: 100%;
    width: 100%;
    &.mobile.openSidebar{
        position: fixed;
        top: 0;
    }
}
.drawer-bg {
    background: #000;
    opacity: 0.3;
    width: 100%;
    top: 0;
    height: 100%;
    position: absolute;
    z-index: 999;
}

.fixed-header {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 9;
    width: calc(100% - #{$sideBarWidth});
    transition: width 0.28s;
}

.hideSidebar .fixed-header {
    width: calc(100% - 54px)
}

.mobile .fixed-header {
    width: 100%;
}

.app-wrapper::v-deep {
    .withoutAnimation {
        .main-container,
        .sidebar-container {
            transition: none;
        }
    }
    .main-container {
        min-height: calc(100% - #{$appHeaderHeight});
        transition: margin-left .28s;
        margin-left: $sideBarWidth;
        position: relative;
        top: $appHeaderHeight;
        @media screen and (max-width: $screen-middle) {
            min-height: calc(100% - #{$appHeaderHeight - $screenDis});
            top: $appHeaderHeight - $screenDis;
        }
        background: $contentOutBg;
        box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.3) inset;
    }
    .sidebar-container{
        transition: width 0.28s;
        width: $sideBarWidth !important;
        background-color: $secAllBg;
        height: calc(100% - #{$appHeaderHeight});
        position: fixed;
        font-size: 0px;
        font-weight: bold;
        top: $appHeaderHeight;
        @media screen and (max-width: $screen-middle) {
            height: calc(100% - #{$appHeaderHeight - $screenDis});
            top: $appHeaderHeight - $screenDis;
        }
        bottom: 0;
        left: 0;
        z-index: 1001;
        overflow: hidden;
        // reset element-ui css
        .horizontal-collapse-transition {
            transition: 0s width ease-in-out, 0s padding-left ease-in-out, 0s padding-right ease-in-out;
        }
        .scrollbar-wrapper {
            overflow-x: hidden !important;
        }
        .el-scrollbar__bar.is-vertical {
            right: 0px;
        }
        .el-scrollbar {
            height: 100%;
        }
        &.has-logo {
            .el-scrollbar {
                height: calc(100% - 50px);
            }
        }
        .is-horizontal {
            display: none;
        }

        a {
            display: inline-block;
            width: 100%;
            overflow: hidden;
        }

        .svg-icon {
            margin-right: 16px;
        }

        .el-menu {
            border: none;
            height: 100%;
            width: 100% !important;
        }
        & .nest-menu .el-submenu>.el-submenu__title,
        & .el-submenu .el-menu-item {
            min-width: $sideBarWidth !important;
        }
        // 控制左侧菜单(2级模块高度)
        .el-menu[role="menubar"] {
            & > .menu-wrapper {
                & > a {
                    height: $appHeaderSecHeight;
                    line-height: $appHeaderSecHeight;
                    & > .el-menu-item {
                        height: $appHeaderSecHeight;
                        line-height: $appHeaderSecHeight;
                        & * {
                            vertical-align: middle;
                        }
                    }
                }
                & > .el-submenu {
                    & > .el-submenu__title {
                        height: $appHeaderSecHeight;
                        line-height: $appHeaderSecHeight;
                    }
                }
            }
        }

        .el-menu {
            .el-icon-arrow-down:before {
                content: "\e790" !important;
            }
            .el-submenu__icon-arrow {
                font-size: 18px;
            }
            .el-menu-item {
                color: $menuText;
                height: $appThirdMenuHeight;
                line-height: $appThirdMenuHeight;
                &:hover, &:focus {
                    background-color: unset;
                }
                &.submenu-title-noDropdown {
                    &:hover {
                        background: unset;
                        color: $menuActiveText;
                    }
                }
            }
            .el-submenu {
                .el-submenu__title {
                    color: $menuText;
                    i {
                        color: $menuText;
                    }
                    &:hover {
                        background: unset;
                        color: $menuActiveText;
                        i {
                            color: $menuActiveText;
                        }
                    }
                }
                &.is-opened {
                    &.is-active {
                        & >.el-submenu__title {
                            color: $menuActiveText;
                            i {
                                color: $menuActiveText;
                            }
                        }
                    }
                }
            }
            &.el-menu--inline {
                padding: 5px 0px;
                background: $moreMenuBg;
                .el-menu-item {
                    // background: $moreMenuBg !important;
                    &:hover {
                        background: unset;
                        color: $menuActiveText;
                        i {
                            color: $menuText;
                        }
                    }
                }
                .el-submenu {
                    background: $moreMenuBg;
                }
            }

            .is-active.el-menu-item{
                background: $subMenuActiveBg !important;
                color: $menuActiveText;
            }
        }
        .el-menu--collapse .el-menu .el-submenu {
            min-width: $sideBarWidth !important;
        }
        .el-menu {
            background-color: $secMenuBg;
        }
        // 左侧菜单树高度设置
        .el-menu-item,
        .el-submenu__title {
            height: $appHeaderFirstMenuHeight;
            line-height: $appHeaderFirstMenuHeight;
        }
    }
    &.hideSidebar {
        .sidebar-container {
            width: 54px !important;
        }

        .main-container {
            margin-left: 54px;
        }

        .submenu-title-noDropdown {
            // padding: 0 !important;
            position: relative;

            .el-tooltip {
                // padding: 0 !important;

                .svg-icon {
                    margin-left: 20px;
                }
            }
        }

        .el-submenu {
            overflow: hidden;

            &>.el-submenu__title {
                // padding: 0 !important;

                .svg-icon {
                    margin-left: 20px;
                }

                .el-submenu__icon-arrow {
                    display: none;
                }
            }
        }

        .el-menu--collapse {
            .el-submenu {
                &>.el-submenu__title {
                    &>span {
                        height: 0;
                        width: 0;
                        overflow: hidden;
                        visibility: hidden;
                        display: inline-block;
                    }
                }
            }
        }
    }
    // when menu collapsed
    .el-menu--vertical {
        & >.el-menu {
            .svg-icon {
                margin-right: 16px;
            }
        }
        // the scroll bar appears when the subMenu is too long
        & >.el-menu--popup {
            max-height: 100vh;
            overflow-y: auto;

            &::-webkit-scrollbar-track-piece {
                background: #d3dce6;
            }

            &::-webkit-scrollbar {
                width: 6px;
            }

            &::-webkit-scrollbar-thumb {
                background: #99a9bf;
                border-radius: 20px;
            }
        }
    }
}
</style>
