import MakeRouteAndMenu from '@/service/make-route'
import { L_Storage } from '@/utils/storage'
import Vue from 'vue'
import router from '@/router'

const state = {
    routes: [],
    routesActiveIndex: L_Storage.getItem('routesActiveIndex') ? L_Storage.getItem('routesActiveIndex') : 0,
    // 权限按钮
    buttons: [],
}

const mutations = {
    SET_ROUTES: (state, routes) => {
        routes.forEach(i => {
            Vue.set(i, 'isActive', false)
        })
        state.routes = routes
    },
    SET_ROUTES_ACTIVE: (state, index) => {
        // 设置活跃一级菜单index
        state.routesActiveIndex = index
        // storage存储，避免刷新后选中状态消失
        L_Storage.setItem('routesActiveIndex', index)
        // 关闭所有一级菜单活跃状态
        state.routes.forEach(i => {
            Vue.set(i, 'isActive', false)
        })
        // 设置当前一级菜单为活跃状态
        let activeFir = state.routes[index]
        activeFir.isActive = true
        // 如果一级菜单本身是个地址，则进行跳转
        // todo：这里可能要用fullPath，待测
        if (activeFir.path) {
            router.push({
                path: activeFir.path,
                query: activeFir.query
            })
        }
    },
    CLEAR_ROUTES_ACTIVE: (state) => {
        state.routesActiveIndex = 0
    },
    // 设置权限按钮
    SET_BUTTONS: (state, buttons) => {
        state.buttons = buttons || []
    }
}

const actions = {
    /**
     * 根据菜单树的数据构造出两个模块。1-项目菜单（点击可跳转对应界面）， 2-路由树
     * @param {*} menuTree： 接口菜单树数据
     * @returns
     */
    generateRoutes ({ commit }, menuTree) {
        return new Promise(resolve => {
            let { routerList, menuList, buttons } = MakeRouteAndMenu(menuTree)
            // 存储菜单树(权限路由)
            commit('SET_ROUTES', menuList)
            commit('SET_BUTTONS', buttons)
            resolve(routerList)
        })
    },
    setRoutesActiveIndex ({ commit }, index) {
        return new Promise(resolve => {
            // 设置第index个一级菜单下的所有菜单
            commit('SET_ROUTES_ACTIVE', index)
            resolve()
        })
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}