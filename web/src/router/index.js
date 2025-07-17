import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Router Modules */
// 公用路由,基础存在
import defaultRoutes from './modules/default'
// 初始需引入的业务页面
import businessRoutes from './modules/business'
// 未挂载路由数组集合(除开模型),动态需要根据权限筛选添加进项目的路由表-asyncRoutes:Array
// import asyncRoutes from './modules/async/index'
// // 模型模板页面-modelRoutes:Array
// import modelRoutes from './modules/model'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */
/* eslint-disable */

 //实例化vue的时候只挂载constantRouter
const _routes = defaultRoutes.concat(businessRoutes)
const createRouter = () => new Router({
    // mode: 'history', // require service support
    scrollBehavior: () => ({ y: 0 }),
    routes: _routes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
    const newRouter = createRouter()
    router.matcher = newRouter.matcher // reset router
}

export default router
