<template>
    <div :class="{'has-logo':showLogo}">
        <logo v-if="showLogo" :collapse="isCollapse"></logo>
        <el-scrollbar wrap-class="scrollbar-wrapper">
            <!-- router属性是后续添加的，为了解决点击菜单bug(菜单使用el-navMenu，bug为只能通过点击才能标记活跃项)
                只有点击图标与文字才能切换活跃状态，左右两侧空白区域点击无法切换显示活跃状态，但两种点击都能实现页面跳转
                合理情况应该是通过匹配页面path，实现状态活跃的切换，因此添加router属性
                router属性说明：是否使用 vue-router 的模式，启用该模式会在激活导航时以 index 作为 path 进行路由跳转 -->
            <transition name="fade">
                <el-menu
                    v-if="isShow"
                    :style="{'--leftMenuBg': 'url('+makeImg(leftMenuBgImg, 'img')+')'}"
                    :default-active="activeMenu"
                    :collapse="isCollapse"
                    :unique-opened="true"
                    :collapse-transition="false"
                    class="menu-part"
                    mode="vertical">
                    <!-- 菜单栏选中的工程信息模块 -->
                    <el-card class="box-card" v-if="projectName && routesActiveIndex==1">
                        <div :class="!isCollapse ? 'card-body-open' : 'card-body-close'">
                            <div class="card-title">
                                <el-tooltip
                                    v-show="isCollapse"
                                    class="item"
                                    effect="dark"
                                    :content="projectName"
                                    placement="right">
                                    <bd-icon name="building" class="card-title-icon"></bd-icon>
                                </el-tooltip>
                                <bd-icon name="building" v-show="!isCollapse" class="card-title-icon"></bd-icon>
                                <span v-show="!isCollapse" class="card-title-text">{{projectName}}</span>
                            </div>
                            <div class="card-id" v-show="!isCollapse">
                                <span class="card-id-text">{{projectCode}}</span>
                            </div>
                        </div>
                    </el-card>
                    <!-- 返回我的项目 -->
                    <sidebar-item
                        ref="subMenu"
                        v-if="projectName && routesActiveIndex==1"
                        :item="goBackMyProjectObj"
                        :base-path="goBackMyProjectObj.path"
                        @click.native="gobackClick">
                    </sidebar-item>
                    <sidebar-item
                        ref="menuItem"
                        v-for="(route, index) in _children"
                        :key="index"
                        :item="route"
                        :base-path="route.path"
                        @mouseover.native="hoverInMenuStyle(route, index)"
                        @mouseout.native="hoverOutMenuStyle(route, index)">
                    </sidebar-item>
                    <div
                        class="bd-nav-bar-ver"
                        :style="firMenuStyle"
                        @mouseover="hoverInMenuStyle()">
                    </div>
                </el-menu>
            </transition>
        </el-scrollbar>
    </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Logo from './Logo'
import SidebarItem from './SidebarItem'
import { S_Storage } from '@/utils/storage'

export default {
    components: { SidebarItem, Logo },
    props: {
        // 是否展示菜单数据
        isShow: {
            type: Boolean,
            default: true
        }
    },
    computed: {
        ...mapGetters([
            'sidebar',
            'permissionRoutes',
            'routesActiveIndex',
            'leftMenuBgImg',
        ]),
        // 活跃的二级菜单数组
        _children () {
            // 获取 到permissionRoutes 当前活跃的一项
            return (this.permissionRoutes[this.routesActiveIndex])?.children || []
        },
        activeMenu () {
            const route = this.$route
            const { meta, fullPath } = route
            // if set path, the sidebar will highlight the path you set
            if (meta.activeMenu) {
                return meta.activeMenu
            }
            // 返回完整url，包含查询参数
            return this.dealByModule(fullPath)
        },
        showLogo () {
            return this.$store.state.settings.sidebarLogo
        },
        isCollapse () {
            return !this.sidebar.opened
        },
        //  判断当前活跃的二级菜单数组中 是否有 当前激活的Menu项（也就是当前路由匹配中的一项）
        childrenHasActiveMenu () {
            let res = this._children.some(item => {
                if (item.children.length) {
                    // 二级菜单项 有children即其有多项可展开关闭 判断其内部是否有 当前激活的Menu项
                    return item.children.some(child => child.path === this.activeMenu)
                }
                return item.path === this.activeMenu
            })
            return res
        }
    },
    data () {
        return {
            firMenuStyle: {},
            hoverModel: {
                obj: null,
                index: 0
            },
            openedItemNameArr: [],
            // 选中的项目名字、ID、编码、当前用户在项目中的角色
            projectName: S_Storage.getItem('projectName'),
            projectId: S_Storage.getItem('projectId'),
            projectCode: S_Storage.getItem('projectCode'),
            CurrentProjectRole: S_Storage.getItem('CurrentProjectRole'),
            // 返回我的项目页面菜单栏
            goBackMyProjectObj: {
                code: 'FHDWDGC',
                component: '/myProject/MY_PROJECT/palceholder',
                menuPathName: '项目管理-返回到我的项目',
                icon: 'back',
                meta: {
                    icon: 'back',
                    title: '返回到我的项目'
                },
                id: 'gobackmyprojectmenuitem',
                name: '返回到我的项目',
                path: '/myProject/MY_PROJECT/palceholder'
            }
        }
    },
    methods: {
        /**
         * 左侧菜单使用的是elemntui的navMenu组件,组件中每个子菜单拥有标识index,在组件的最顶级统一控制下拉展示选中的子菜单
         * 这里默认的子菜单使用的标识为path路径,所以顶级使用的是完整的路径path进行控制
         * path是用于匹配左侧菜单的,但是由左侧菜单跳转出去的页面(例如模型编辑页面)将无法标识
         * 例如:左侧菜单树中有列表页面,会选中左侧的某个模块,但是从列表页面跳转去的编辑页以及详情页都无法对应左侧菜单任何一个,会导致无法选中
         * 实际: 从列表页面出去的编辑,详情与新增,在左侧应该对应为列表模块的选中,表示该页面是由此模块引申的
        */
        dealByModule (path) {
            // let lastIndex = path.lastIndexOf('\/')
            // let lastPath = path.slice(lastIndex + 1)
            return path
        },
        hoverInMenuStyle (obj, index) {
            // 特殊处理，避免鼠标在菜单白色选中条忽闪忽闪 --start
            if (obj) {
                this.hoverModel = {
                    obj: obj,
                    index: index
                }
            } else {
                obj = this.hoverModel.obj
                index = this.hoverModel.index
            }
            // 特殊处理，避免鼠标在菜单白色选中条忽闪忽闪 --end
            let item = this.$refs.menuItem[index].$el.offsetTop
            this.firMenuStyle = {
                'top': `${item}px`,
                'opacity': 1,
                'height': '56px'
            }
        },
        hoverOutMenuStyle (obj, index) {
            let item = this.$refs.menuItem[index].$el.offsetTop
            this.firMenuStyle = {
                'height': 0,
                'opacity': 0,
                'top': `${item + 56 / 2}px`,
                transform: 'translateY(-50%)',
            }
        },
        handleSubMenuOpen (index) {
            this.openedItemNameArr = [index]
            //  当前活跃的二级菜单数组中没有 当前激活的Menu项 时 其打开状态才做保存（二级菜单只允许打开一项）
            if (!this.childrenHasActiveMenu) {
                this._children.openedItemName = index
            }
        },
        handleSubMenuClose () {
            this.openedItemNameArr = []
            //  当前活跃的二级菜单数组中没有 当前激活的Menu项 时 其关闭状态才做保存
            if (!this.childrenHasActiveMenu) {
                this._children.openedItemName = ''
            }
        },
        // 项目管理页返回按钮被点击
        gobackClick () {
            // 执行正常菜单跳转
            this.$store.dispatch('app/openSider', { withoutAnimation: false })
            this.$store.dispatch('permission/setRoutesActiveIndex', 0)

            // 清空选中项目的数据
            S_Storage.removeItem('projectName')
            S_Storage.removeItem('projectId')
            S_Storage.removeItem('projectCode')
            S_Storage.removeItem('CurrentProjectRole')

            // 延迟展示：处理菜单切换bug
            this.$emit('delayShow')
        }
    },
    watch: {
        //  当 this._children 发生变化，也就是 一级菜单切换时
        _children: {
            deep: true,
            handler: function (val, oldVal) {
                // 当前二级菜单数组中没有 已经激活的Menu项 则使用上一次保存的打开状态
                // 当前二级菜单数组中有已经激活的Menu项，使用激活的Menu项控制二级菜单的打开状态，不使用openedItemNameArr来控制
                this.openedItemNameArr = !this.childrenHasActiveMenu ? [this._children.openedItemName] : []
                // 更改项目管理的被选中项目的信息
                this.projectName = S_Storage.getItem('projectName')
                this.projectId = S_Storage.getItem('projectId')
                this.projectCode = S_Storage.getItem('projectCode')
                this.CurrentProjectRole = S_Storage.getItem('CurrentProjectRole')
            },
            immediate: true
        }
    },
    created () { }
}
</script>
<style lang="scss" scoped>
.box-card {
    font-size: 14px;
    color: #fff;
    font-weight: 400;
    border-radius: 0;
    border: 0;
    height: 110px;
    background-color: #0864af;
    // .card-title-text {
    //     padding-left: 10px;
    // }
    .card-body-open {
        border: 1px solid #fff;
        border-radius: 8px;
        padding: 10px;
        .card-id {
            padding: 10px 0 4px 10px;
        }
    }
    .card-body-close {
        border: 0;
        border-radius: 0;
        padding: 0;
    }
}

.bd-nav-bar-ver {
    width: 4px;
    height: 0;
    background: #fff;
    position: absolute;
    z-index: 2;
    top: 0;
    transition: all 0.2s linear, transform 0.2s linear;
}

.router-link-exact-active {
    background: $subMenuActiveBg !important;
    .el-menu-item {
        color: #fff !important;
    }
}
.menu-part {
    position: relative;
    &::before {
        content: "";
        position: absolute;
        opacity: .5; // 设置透明度，避免图片颜色跳脱主题色
        bottom: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-repeat: no-repeat;
        background-position: bottom;
        // 使用var会导致悬浮时不停闪动，暂时无法动态使用背景图
        // background-image: var(--leftMenuBg);
        // background-image: url("~@/assets/image/frame/leftMenuBg.jpg")
    }
}
</style>
