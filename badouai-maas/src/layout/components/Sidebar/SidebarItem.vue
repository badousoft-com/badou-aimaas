<template>
    <div v-if="!item.hidden" class="menu-wrapper">
        <template v-if="hasOneShowingChild(item.children,item) && (!onlyOneChild.children||onlyOneChild.noShowingChildren)&&!item.alwaysShow">
            <app-link v-if="onlyOneChild.meta" :to="{
                path:resolvePath(onlyOneChild.path, onlyOneChild),
                query:onlyOneChild.query,
                initPath: onlyOneChild.path}">
                <el-menu-item :index="resolvePath(onlyOneChild.path, onlyOneChild)" :class="{'submenu-title-noDropdown':!isNest}">
                    <item :icon="onlyOneChild.meta.icon||(item.meta&&item.meta.icon)" :title="onlyOneChild.meta.title" />
                </el-menu-item>
            </app-link>
        </template>
        <el-submenu v-else ref="subMenu" :index="item.code" popper-append-to-body>
            <template slot="title">
                <item v-if="item.meta" :icon="item.meta && item.meta.icon" :title="item.meta.title" />
            </template>
            <sidebar-item
                v-for="(child, index) in item.children"
                :key="index"
                :is-nest="true"
                :item="child"
                :base-path="resolvePath(child.path, child)"
                class="nest-menu">
            </sidebar-item>
        </el-submenu>
    </div>
</template>

<script>
import path from 'path'
import { Is_External_Url } from '@/utils/validate/index'
import Item from './Item'
import AppLink from './Link'
import FixiOSBug from './FixiOSBug'
import { URL_Code } from '@/utils/url-encode'

export default {
    name: 'SidebarItem',
    components: { Item, AppLink },
    mixins: [FixiOSBug],
    props: {
        // route object
        item: {
            type: Object,
            required: true
        },
        isNest: {
            type: Boolean,
            default: false
        },
        basePath: {
            type: String,
            default: ''
        }
    },
    data () {
        // To fix https://github.com/PanJiaChen/vue-admin-template/issues/237
        // TODO: refactor with render function
        this.onlyOneChild = null
        return {}
    },
    methods: {
        hasOneShowingChild (children = [], parent) {
            const showingChildren = children.filter(item => {
                if (item.hidden) {
                    return false
                } else {
                    // Temp set(will be used if only has one showing child)
                    this.onlyOneChild = item
                    return true
                }
            })
            // When there is only one child router, the child router is displayed by default
            if (showingChildren.length === 1) {
                return true
            }
            // Show parent if there are no child router to display
            if (showingChildren.length === 0) {
                this.onlyOneChild = { ...parent, noShowingChildren: true }
                return true
            }
            return false
        },
        resolvePath (routePath, obj) {
            if (Is_External_Url(routePath)) {
                return `/externalUrl/${URL_Code.encode(routePath)}`
            }
            if (Is_External_Url(this.basePath)) {
                return this.basePath
            }
            let finaPath = routePath ? path.resolve(this.basePath, routePath) : this.basePath
            if (this.hasOneShowingChild(this.item.children, this.item) &&
                (!this.onlyOneChild.children || this.onlyOneChild.noShowingChildren) &&
                !this.item.alwaysShow) {
                finaPath = this.basePath
            }
            return finaPath
        }
    },
    created () { }
}
</script>
<style lang="scss" scoped>
// 添加的原因：解决样式异常问题,可以选中菜单，但是样式穿透不了子元素
// 现在注释的原因：path相同query查询参数不同的路由会被同时标记为活跃
// .router-link-active {
//     background: $subMenuActiveBg !important;
// }

// 异常：当路由地址存在特殊符号，例如参数中有【''{}】等符号时会出现菜单异常
//     1. 点击菜单时可以跳转，但是菜单不会选中(样式呈现上无活跃状态)
//     2. 需要点击第二次，菜单才会有选中标识样式
// 观察到点击菜单时，活跃菜单多了两个样式类：【router-link-active】与【router-link-exact-active】
//     1. 使用router-link-active有bug，path相同query查询参数不同的路由会被同时标记为活跃，会同时有这个样式类
//     2. 发现router-link-exact-active只有活跃且与当前菜单地址完全一致的菜单才有，符合需求
//     3. 通过控制router-link-exact-active类下的活跃菜单样式，解决样式问题
.router-link-exact-active {
    background: $subMenuActiveBg !important;
    .el-menu-item {
        color: #fff !important;
    }
}
</style>
