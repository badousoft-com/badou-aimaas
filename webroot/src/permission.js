import router from './router'
import store from './store'
import { Message, MessageBox } from 'element-ui'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import { getToken } from '@/service/auth' // get token from cookie
// import getPageTitle from '@/utils/get-page-title'
import Set_Page_Favicon from '@/utils/set-page-favicon'
// 根据类型返回资源路径函数
import returnSrc from '@/service/return-src'
import { Add_History, Get_History } from '@/service/history'

NProgress.configure({ showSpinner: false }) // NProgress Configuration

const whiteList = ['/login', '/auth'] // no redirect whitelist
// 定义变量：主要用于拦截使用addRoutes时触发beforeEach
let allowInRouter = true

router.beforeEach(async (to, from, next) => {
    if (!allowInRouter) return
    // 当CAS登录成功回调时会将token带在路由参数里面，需要将其存到store里面，然后删除路由上的token参数
    if (to.query?.token) {
        store.commit('user/SET_TOKEN', to.query.token)
        delete to.query.token
    }
    // 一样的路由地址设置不处理跳转
    if (to.fullPath === from.fullPath) {
        return
    }
    // 是否前往修改密码页面
    function toModifyPwd () {
        const userInfo = store.getters.userInfo || {}
        if (String(userInfo.izFirstLogon) === '1' || String(userInfo.needChangePwd) === '1') { // 判断是否首次登录/密码过期
            // 更改密码的路由path
            let ModifyPwdPath = '/personal/ModifyPwd'
            let tip = String(userInfo.izFirstLogon) === '1' ? '首次登录' : '密码过期'
            // 当所跳转的路由不在白名单上 && 不是跳转修改密码的页面
            if (whiteList.indexOf(to.path) === -1 && to.path !== ModifyPwdPath) {
                MessageBox.confirm(`${tip}！请先修改密码`, '提示', {
                    confirmButtonText: '确定',
                    showCancelButton: false,
                    type: 'warning'
                }).then(() => {
                    // 需前往更改密码页
                    next({ path: ModifyPwdPath, replace: true })
                })
                return true
            }
        }
        // 不需要执行前往更改密码的操作
        return false
    }

    // start progress bar
    NProgress.start()
    if (!store.state.settings.hasSettingStatus) {
        await store.dispatch('settings/getProjectSetting')
        let tempToken = getToken()
        tempToken && store.commit('user/SET_TOKEN', tempToken)
    }
    // set page title
    document.title = store.state.settings.projectSetting.rootTitle || '开发平台'
    // console.log(`hasToken: ${hasToken}`)
    // 判断token是否失效:项目没有使用token,所以这里不需要
    if (store.getters.token) {
        if (to.path === '/login') {
            // 登录后还跳去登录，则强制返回首页
            next({ path: '/' })
            NProgress.done()
        } else {
            // 当来源路由与目标路由都需要缓存时，清空旧的缓存树
            //     此时不能将目标路由添加进缓存时，当standerList跳standerList时会被解析为始终保留页面缓存
            //     到时会出现页面请求混乱，疯狂请求的情况
            //     合适的实际：后面通过router.after事件中将目标路由添加到缓存树中
            if (isKeepAlive(to) && isKeepAlive(from)) {
                // 通过状态管理库事件将【缓存树】清空
                store.commit('settings/CLEAR_KEEPALIVE_LIST')
            }
            const hasGetUserInfo = store.getters.name
            if (hasGetUserInfo) {
                // 需要添加时间函数，为缓存树生效提供缓冲时间
                setTimeout(() => {
                    if (toModifyPwd()) return
                    next()
                })
            } else {
                try {
                    // 设置项目标签页favicon-修改为此位置，避免重复请求
                    Set_Page_Favicon(returnSrc(store.state.settings.projectSetting.favicon, 'favicon'))
                    // 获取用户信息
                    await store.dispatch('user/getInfo')
                    // 获取该用户下菜单路由(权限管理)
                    let menuRoutes = await store.dispatch('user/getUserRoutes')
                    let routerList = await store.dispatch('permission/generateRoutes', menuRoutes ? menuRoutes : [])
                    let route404 = {
                        path: '*',
                        redirect: '/404'
                    }
                    // 使用addRoutes时会触发beforeEach，添加状态位allowInRouter
                    allowInRouter = false
                    router.addRoutes(routerList)
                    router.addRoutes([route404])
                    // 获取当前活跃一级菜单索引
                    let routesActiveIndex = store.getters.routesActiveIndex
                    // 如果当前活跃菜单索引 >= 总菜单数  ======》 设置活跃项索引为0
                    routesActiveIndex >= menuRoutes.length && await store.dispatch('permission/setRoutesActiveIndex',  0)
                    // 添加完毕时重置状态，允许逻辑进beforeEach
                    allowInRouter = true
                    // 需要添加时间函数，为缓存树生效提供缓冲时间
                    setTimeout(() => {
                        // 如果当前用户还未修改密码，需要修改密码后方能跳转
                        if (toModifyPwd()) return
                        // 使用replace:true,避免刷新当前页面时重复添加路由,导致后退失效
                        next({...to, replace: true})
                    })
                } catch (error) {
                    console.error(`
                    =======================================================================================
                    =======================出现这个说明问题被捕获了,需要关掉try/catch才能看到问题 -wjx=============
                    =======================================================================================
                    `)
                    console.error(`路由守卫逻辑报错日志：${error}`)
                    // remove token and go to login page to re-login
                    await store.dispatch('user/resetToken')
                    Message.error({message: error || 'Has Error'})
                    // 需要添加时间函数，为缓存树生效提供缓冲时间
                    setTimeout(() => {
                        next(`/login?redirect=${to.path}`)
                    })
                    NProgress.done()
                }
            }
        }
    } else {
        // 设置项目标签页favicon-修改为此位置，避免重复请求
        Set_Page_Favicon(returnSrc(store.state.settings.projectSetting.favicon, 'favicon'))
        /* has no token */
        if (whiteList.indexOf(to.path) !== -1) {
            // in the free login whitelist, go directly
            // 需要添加时间函数，为缓存树生效提供缓冲时间
            setTimeout(() => {
                // 如果当前用户还未修改密码，需要修改密码后方能跳转
                if (toModifyPwd()) return
                next()
            })
        } else {
            // other pages that do not have permission to access are redirected to the login page.
            next(`/login?redirect=${to.path}`)
            NProgress.done()
        }
    }
})
// 全局守卫：进入页面之后
router.afterEach((to, from) => {
    // finish progress bar
    NProgress.done()
    // 进入页面后若需要缓存，调用vuex状态管理库更新【缓存树】
    if (isKeepAlive(to)) {
        store.commit('settings/SET_KEEPALIVE_LIST', to.name)
    }
    setTimeout(() => {
        setHistory(to, from)
    })
})

/**
 * 返回是否缓存页面的状态
 * @param {Object} way 目标路由对象
 * @returns {Boolean} 该路由是否需要缓存数据
 */
function isKeepAlive (way) {
    // 判断路由对象属性中meta是否有配置keepAlive为true
    return way && way.meta && way.meta.keepAlive
}

/**
 * 根据所跳转的页面填充历史记录
 * @param {*} to
 * @param {*} from
 */
function setHistory (to, from) {
    let historyRecords = Get_History()
    // 获取最近一次的历史记录
    let last_history = historyRecords[0]
    // 页面使用的 title
    let real_title = last_history?.title || ''
    // 将最近的历史记录与 将要跳转的页面相对比
    if (!last_history ||
        (last_history.fullPath && to.fullPath !== last_history.fullPath) ||
        (last_history.path && to.fullPath !== last_history.path) ||
        (last_history.name && to.name !== last_history.name)) { // 符合条件，说明该路径还未存储历史记录
        // 判断当前是否是面包屑中的地址
        //   因为上面是先组装面包屑的，所以面包屑的最后一个数据，有可能是to的title
        let breadcrumbs = store?.getters?.location
        let l_breadcrumb = breadcrumbs[breadcrumbs.length - 1]
        if (l_breadcrumb && (
            (l_breadcrumb.path && to.fullPath === l_breadcrumb.path) ||
            (l_breadcrumb.name && to.name === l_breadcrumb.name))) {
            real_title = l_breadcrumb.title
            Add_History(l_breadcrumb)
        // 如果to的页面 ！== 面包屑的最后一个地址
        } else {
            // 先与之前的历史记录中的地址相互比较，看看是否有相同的页面
            // 可适用于浏览器回退、router.go(-1)等操作
            let h_item = historyRecords.find(i => {
                if (i.fullPath) return to.fullPath === i.fullPath
                if (i.path) return to.fullPath === i.path
                if (i.name) return to.name === i.name
                return false
            })
            // 使用历史记录中的title或者meta.title，作为历史记录的title
            let meta_title = to.meta?.title
            real_title = h_item ? h_item.title : meta_title
            // 如果没有title，就不添加
            real_title && Add_History({
                name: to.name,
                path: to.name,
                params: to.params,
                query: to.query,
                title: real_title
            })
        }
    }
    // 给meta.realTitle 赋值，记录此次路由使用的title
    to.meta.realTitle = real_title
}
