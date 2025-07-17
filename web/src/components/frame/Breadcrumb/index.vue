<template>
    <div class="breadcrumb">
        <bd-icon name="location-fill" class="mainSvgColor"></bd-icon>
        <el-breadcrumb class="app-breadcrumb" separator="/">
            <transition-group name="breadcrumb">
                <!-- transition-group的子项不允许使用index作为key值 -->
                <el-breadcrumb-item v-for="(item, index) in location" :key="item && item.title + index">
                    <span v-if="!item.path" class="no-redirect">{{item.title}}</span>
                    <a v-else @click.prevent="handleLink(item.path)">{{item.title}}</a>
                </el-breadcrumb-item>
            </transition-group>
        </el-breadcrumb>
        
    </div>
    
</template>

<script>
import { mapGetters } from 'vuex'
import pathToRegexp from 'path-to-regexp'
import { L_Storage } from '@/utils/storage'
import store from '@/store'

export default {
    components: {
    },
    data() {
        return {
            levelList: [],
            // 是否找到当前点击的匹配菜单，用于面包屑展示
            /**
             * 为什么需要这个字段
             * 1. 路径面包屑的展示依赖于路由菜单，只有存在与路由菜单中的路径匹配才可以展示
             * 2. 一旦由这些页面进入新增编辑等的页面，在路由菜单中就找不到
             * 3. 所以逻辑处理为：存储上一次的面包屑，匹配不到就使用历史面包屑
             * 4. 遍历的时候当找到匹配时使用该字段终止查找，避免其他设置再次干扰
             */
            hasFind: false,
        }
    },
    computed: {
        ...mapGetters([
            'permissionRoutes',
            'routesActiveIndex',
            'location'
        ]),
    },
    watch: {
        $route (to, from, next) {
            this.getBreadcrumb()
        }
    },
    created () {
        let menuCrumbs = L_Storage.getObj('menuCrumbs')
        if (menuCrumbs) {
            this.levelList = menuCrumbs
            store.commit('settings/SET_LOCATION', this.levelList)
        }
        this.getBreadcrumb()
    },
    methods: {
        addBreadcrumbItem () {
            this.levelList.push({
                path: '/module/stander/list/djMeetingInfo_zhongxinzulilunxuexi/placeholder?searchParam=%5B%7B"name"%3A"typeCode","type"%3A"exact-match","value"%3A"zhongxinzulilunxuexi"%7D%5D&resourceId=402881696bf8fdfe016c42e32a040dd1',
                title: '新增'
            })
        },
        handleLink (path) {
            this.$router.replace({path: path})
        },
        /**
         * @description: 递归匹配菜单页面
         * @param {Array} list 菜单列表
         */
        findMenuListByPath (list) {
            for (let i = 0; i < list.length; i++) {
                const item = list[i]
                if (item.path === this.$route.path) {
                    this.makeLevelList(item.menuPathName)
                    return true
                }
                if (this.findMenuListByPath(item.children)) {
                    return true
                }
            }
            return false
        },
        makeLevelList (namePath) {
            this.levelList = namePath.split('-').map(i => {
                return {
                    title: i,
                    path: '',
                    allowJump: false
                }
            })
            this.levelList[this.levelList.length - 1].path = this.$route.fullPath
            this.hasFind = true
            // this.addBreadcrumbItem()
        },
        getBreadcrumb() {
            // 获取当前活跃的一级菜单下面的子级模块
            let activeRootMenu = this.permissionRoutes[this.routesActiveIndex]
            // 获取活跃一级菜单名称
            let menuPathName = activeRootMenu.menuPathName

            // 匹配是否为当前一级菜单页面，否则，递归匹配当前一级菜单下的子级菜单页面
            if (activeRootMenu.path === this.$route.path) {
                this.makeLevelList(menuPathName)
            } else if (!this.findMenuListByPath(activeRootMenu.children)) {
                // 否则，循环递归匹配其它一级菜单页面
                for (let i = 0; i < this.permissionRoutes.length; i++) {
                    const firstItem = this.permissionRoutes[i]
                    if (firstItem.path === this.$route.path) {
                        this.makeLevelList(firstItem.menuPathName)
                        break
                    }
                    if (this.findMenuListByPath(firstItem.children)) {
                        break
                    }
                }
            }

            if (this.hasFind) {
                // 存储菜单面包屑
                store.commit('settings/SET_LOCATION', this.levelList)
                L_Storage.setObj('menuCrumbs', this.levelList)
                this.hasFind = false
            }
        },
        isDashboard(route) {
            const name = route && route.name
            if (!name) {
                return false
            }
            return name.trim().toLocaleLowerCase() === 'Dashboard'.toLocaleLowerCase()
        },
        pathCompile(path) {
            // To solve this problem https://github.com/PanJiaChen/vue-element-admin/issues/561
            const { params } = this.$route
            var toPath = pathToRegexp.compile(path)
            return toPath(params)
        },
    }
}
</script>

<style lang="scss" scoped>
.breadcrumb::v-deep {
    width: 100%;
    .mainSvgColor {
        color: $primary;
        width: 1.5em;
        height: 1.5em;
        margin-right: 5px;
    }
    .app-breadcrumb.el-breadcrumb {
        display: inline-block;
        font-size: 14px;
        
        .no-redirect {
            color: #97a8be;
            cursor: text;
        }
    }
    .el-breadcrumb__item {
        .el-breadcrumb__separator {
            display: none;
        }
        &:last-child {
            &:after {
                content: "" !important;
            }
        }
        &:after {
            content: ">";
            padding: 0px 6px;
        }
    }
    .el-breadcrumb__inner a,
    .el-breadcrumb__inner.is-link {
        color: #333;
    }
    .history-box {
        padding: 0 10px;
        color: $primary;
    }
}

</style>
