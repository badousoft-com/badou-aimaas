<template>
    <div class="project-menu">
        <div class="user-info" :class="{'hideUser': isCollapse}">
            <el-popover
                v-if="openSwitchRole"
                :ref="chooseRoleRef"
                placement="top-start"
                trigger="click"
                width="100"
                popper-class="pad-0 marT-5 marL--3"
                @show="showRoleStatus = true"
                @hide="showRoleStatus = false">
                <div class="s-role">
                    <div
                        v-for="(i, index) in temp_roleCodeList"
                        :key="index"
                        class="s-role__part">
                        <el-button
                            :type="i.isHover || i.code === roleCode ? 'primary' : ''"
                            :plain="i.code === roleCode?'plain':''"
                            @click="changeRole(i)"
                            @mouseover.native="setEffectLight(i)"
                            @mouseout.native="setEffectLight(i, false)"
                            class="w-per-100 s-role__name">
                            <bd-icon
                                :class="{ 'o-0': i.code !== roleCode }"
                                name="dArrowRight">
                            </bd-icon>
                            {{i.name}}
                            <span v-if="i.code == roleCode">[当前]</span>
                            <span v-if="i.isChanging" class="warningC">[切换中...]</span>
                        </el-button>
                    </div>
                </div>
                <div slot="reference">
                    <img class="headImg pointer" :src="makeImg(avatar, 'img')" :onerror="DEFAULT_AVATAR" alt="Img">
                    <div class="userName text-o-1 pointer" :title="_userName">
                        {{_userName}}
                        <i
                            v-if="_userName"
                            :class="{ 'is-top' : showRoleStatus }"
                            class="role-select-icon el-icon-caret-top">
                        </i>
                    </div>
                </div>
            </el-popover>
            <div v-else>
                <img class="headImg pointer" :src="makeImg(avatar, 'img')" :onerror="DEFAULT_AVATAR" alt="Img">
                <div class="userName text-o-1 pointer" :title="_userName">
                    {{_userName}}
                    <i
                        v-if="_userName"
                        :class="{ 'is-top' : showRoleStatus }"
                        class="role-select-icon el-icon-caret-top">
                    </i>
                </div>
            </div>
        </div>
        <div class="first-menu font0">
            <div class="opeToggleSider d-ib v-t pointer" @click="toggleSideBar">
                <bd-icon :name="isCollapse?operateIconList[0]:operateIconList[1]"></bd-icon>
            </div>
            <div class="menu-list bd-nav d-ib h-per-100" id="scrollDiv" :class="Is_Mobile()? 'is-mobile' : ''" @mouseleave="handleScrollDivLeave">
                <template v-if="_currentShowPermissionRoutes.length !== 0">
                    <div v-for="(i, index) in _currentShowPermissionRoutes"
                        ref="firMenuItem"
                        @mouseenter="hoverInMenuStyle(i, index)"
                        @mouseleave="hoverOutMenuStyle(i, index)"
                        :key="index"
                        class="menu-list-item bd-nav-item d-ib"
                        :class="{'is-active': i.isActive}"
                        @click="changeFirMenu(i, index)">
                        <bd-icon :name="i.icon"></bd-icon>
                        <span class="bold">{{i.name}}</span>
                    </div>
                </template>
                <div class="bd-nav-bar-wrap" :style="bdNavBarWrapStyle" :class="widthChangeClass">
                    <span
                        class="bd-nav-bar"
                        :style="bdNavBarStyle">
                    </span>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import { Change_User_Role } from '@/api/frame/user'
import { Is_Mobile } from '@/utils/browser.js'
import { mapGetters } from 'vuex'
import store from '@/store'
import { S_Storage } from '@/utils/storage'

export default {
    name: 'AppHeader',
    data () {
        return {
            //  白条样式
            bdNavBarStyle: {
                width: 0
            },
            //  白条容器样式
            bdNavBarWrapStyle: {
                left: 0,
                width: 0
            },
            isFirstEnter: true, // 是否第一次进入
            hoverModel: {},  // 用于记录的上一次被hover 元素对应的信息
            widthChangeClass: '', // 宽度变化时 根据情况使用的类
            operateIconList: ['unfoldHor', 'foldHor'],
            // 角色列表
            temp_roleCodeList: [],
            // 是否显示角色选择列表框
            showRoleStatus: false,
            // 选择角色框Ref名称
            chooseRoleRef: 'chooseRole',
            // 判断是否为移动端
            Is_Mobile: Is_Mobile,
        }
    },
    props: {
    },
    computed: {
        ...mapGetters([
            'sidebar',
            'permissionRoutes',
            'name',
            'avatar',
            'homeUrl',
            'roleCode',
            'roleCodeList',
            'userInfo',
        ]),
        isCollapse () {
            return !this.sidebar.opened
        },
        // 是否开启切换角色
        openSwitchRole () {
            let openSwitchRole = this.$store.state?.settings?.projectSetting?.openSwitchRole
            return openSwitchRole && this.temp_roleCodeList.length > 1
        },
        // 当前用户角色对象
        _currentRole () {
            if (this.roleCodeList.length === 0) return
            if (!this.roleCode) return
            let _currentRole = this.roleCodeList.find(i => i.code === this.roleCode)
            return _currentRole
        },
        // 当前用户对应角色名称
        _currentRoleName () {
            return this._currentRole && this._currentRole.name
        },
        // 当前用户对应角色编码
        _currentRoleCode () {
            return this._currentRole && this._currentRole.code
        },
        // 渲染的当前用户名：角色名+用户名
        _userName () {
            return [
                this._currentRoleName || '',
                this.name || ''
            ].filter(i => i).join('-')
        },
        // 当前显示列表
        _currentShowPermissionRoutes () {
            let _permissionRoutes = []
            let slectProjectId = S_Storage.getItem('projectId')
            if (slectProjectId) {
                // 有选中的项目
                _permissionRoutes = this.permissionRoutes
            } else {
                // 无选中的项目
                _permissionRoutes = this.permissionRoutes.filter(item => item.code !== 'GCGL')
            }
            return _permissionRoutes
        }
    },
    methods: {
        /**
         * 设置悬浮色
         * @param {Object} item 角色对象数据
         * @param {Boolean} lightStatus 悬浮色状态
         */
        setEffectLight (item, lightStatus = true) {
            item.isHover = lightStatus
        },
        /**
         * 切换角色
         * @param {Object} item 角色对象数据
         */
        changeRole (item) {
            // 获取当前点击的角色编码code
            // 获取对应的首页地址indexUrl
            // 获取对应的角色名称name
            let { code, name } = item
            // 没有变更，直接停止
            if (this.roleCode === code) {
                this.$message.success('已处在当前角色，无须切换')
                return
            }
            // 设置状态为切换中
            item.isChanging = true
            // 执行切换角色接口，获取最新角色下token
            Change_User_Role(code).then(res => {
                if (res && res.hasOk && res.bean) {
                    // 关闭选择角色框
                    this.$refs[this.chooseRoleRef]?.doClose()
                    this.$message.success(`成功切换角色为【${name}】,即将更新页面,请稍后`)
                    // 关闭切换中的状态
                    item.isChanging = false
                    // 清除原有用户相关数据，使后续逻辑的locatin.reload能否触发重新获取菜单数据与用户信息
                    store.dispatch('user/removeTokenUserFoot').then(() => {
                        // 设置新的token
                        store.commit('user/SET_TOKEN', res.bean)
                        // 同个地址，则重新加载页面，使userInfo重新请求
                        location.reload()
                    })
                } else {
                    this.$message.error(res?.message ? res.message : '切换角色失败')
                }
            })
        },
        toggleSideBar () {
            this.$store.dispatch('app/toggleSideBar')
        },
        changeFirMenu (item, index) {
            // 执行正常菜单跳转
            this.$store.dispatch('app/openSider', { withoutAnimation: false })
            this.$store.dispatch('permission/setRoutesActiveIndex', index)
            // 延迟展示：处理菜单切换bug
            this.$emit('delayShow')
            if (item.path) {
                this.pushPage({
                    path: item.path,
                    title: item.name
                })
            }
        },
        hoverInMenuStyle (obj, index) {
            let item = this.$refs.firMenuItem[index]
            let left = item.offsetLeft
            let width = item.offsetWidth
            if (!this.hoverModel.width || this.isFirstEnter) {
                this.widthChangeClass = ''
                this.isFirstEnter = false
                this.bdNavBarWrapStyle = {
                    left: `${left}px`,
                    width: `${width}px`
                }
            } else {
                //  相邻状态下hover到相邻位置，根据是变大，还是变小 设置不同的类 使用不同的样式
                if (Math.abs(index - this.hoverModel.index) === 1) {
                    if (this.hoverModel.width - width > 0) {
                        this.widthChangeClass = 'big-to-small'
                    }
                    if (this.hoverModel.width - width < 0) {
                        this.widthChangeClass = 'small-to-big'
                    }
                }
                this.bdNavBarWrapStyle = {
                    left: `${left}px`,
                    width: `${width}px`
                }
            }
            //  记录本次hover
            this.hoverModel = {
                width,
                index
            }
            this.bdNavBarStyle = {
                width: `${width}px`
            }
        },
        hoverOutMenuStyle () {
            this.widthChangeClass = ''
            this.bdNavBarStyle = {
                width: 0
            }
        },
        //  移出了容器 下次再进入 视为第一次进入
        handleScrollDivLeave () {
            this.isFirstEnter = true
        }
    },
    created () { },
    mounted () {
        // 通过鼠标处理一级菜单的水平滚动 --start
        let scrollDiv = document.getElementById('scrollDiv')
        // let element =  document.documentElement
        // if (navigator.userAgent.indexOf("Chrome") > 0) {
        //     element = document.body
        // }
        scrollDiv.addEventListener('DOMMouseScroll', handler, false)
        scrollDiv.addEventListener('mousewheel', handler, false)
        function handler (event) {
            let detail = event.wheelDelta || event.detail
            let moveForwardStep = 1
            let moveBackStep = -1
            let step = 0
            if (detail > 0) {
                step = moveForwardStep * 100
            } else {
                step = moveBackStep * 100
            }
            scrollDiv.scrollLeft -= step
            if (parseInt(scrollDiv.scrollLeft) < 0) {
                scrollDiv.scrollLeft = 0
            }
        }
        // 通过鼠标处理一级菜单的水平滚动 --end
        this.temp_roleCodeList = (this.roleCodeList || []).map(i => ({
            ...i,
            isChanging: false, // 是否切换中
            isHover: false, // 是否鼠标悬浮状态
        }))
    }
}
</script>

<style lang='scss' scoped>
$avatarWidth: 25px;
$toggleSiderBtnWidth: 38px;
$collapseTime: 0.3s;
.project-menu {
    height: $appHeaderFirstMenuHeight;
    line-height: $appHeaderFirstMenuHeight;
    background-color: $primary;
    display: flex;
    color: #fff;
}
.user-info {
    width: $sideBarWidth;
    min-width: $sideBarWidth;
    transition: width $collapseTime;
    height: $appHeaderFirstMenuHeight;
    background: $firMenuBg;
    position: relative;
    align-items: center;
    padding: 0px $headTitlePadding;
    line-height: $appHeaderFirstMenuHeight;
    border-bottom: 1px solid rgba($white, 0.1);
    &.hideUser {
        width: $sideBarHideWidth;
        min-width: 0;
        & + .first-menu {
            width: calc(100% - #{$sideBarHideWidth});
        }
        .role-select-icon {
            display: none;
        }
    }
    .headImg {
        width: $avatarWidth;
        height: $avatarWidth;
        background: rgba($white, 0.5);
        border-radius: 50%;
        position: absolute;
        top: 0;
        bottom: 0;
        margin: auto;
        left: $headTitlePadding;
        border: 1px solid rgba($white, 0.3);
        border-radius: 50%;
    }
    .role-select-icon {
        transition: all 0.3s;
        &.is-top {
            transform: rotate(180deg);
        }
    }
}
.first-menu {
    overflow: hidden;
    width: calc(100% - #{$sideBarWidth});
    transition: width $collapseTime;
}
.userName {
    color: #fff;
    width: 100%;
    padding-left: $avatarWidth + 5px;
}
.opeToggleSider {
    background: $firMenuBg;
    padding: 0px 10px;
    margin-left: 1px;
    font-size: $font;
    width: $toggleSiderBtnWidth;
}
.is-mobile {
    overflow-x: unset !important;
    height: $appHeaderFirstMenuHeight + 5px !important;
}
.menu-list {
    font-size: $font;
    position: relative;
    width: calc(100% - #{$toggleSiderBtnWidth} - 1px);
    white-space: nowrap;
    overflow-x: hidden; // auto -> hidden
    overflow-y: hidden;
    // height: $appHeaderFirstMenuHeight + 18px;
    height: $appHeaderFirstMenuHeight;
    .menu-list-item {
        background: $firMenuBg;
        padding: 0px 25px;
        @media screen and (max-width: $screen-middle) {
            padding: 0px 18px;
        }
        margin-left: 1px;
        cursor: pointer;
        color: $menuText;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        &:hover {
            color: $menuActiveText;
        }
        &.is-active {
            background: $firMenuActiveBg;
            color: $menuActiveText;
        }
    }
}
</style>
<style lang="scss">
// 切换角色
.s-role {
    padding: 4px;
    .s-role__part {
        margin-bottom: 4px;
        &:last-of-type {
            margin-bottom: 0;
        }
        .s-role__name {
            text-align: left;
        }
    }
}
</style>