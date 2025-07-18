/** 根据菜单树的数据构造出两个模块。1-项目菜单（点击可跳转对应界面）， 2-路由树
 * 旧逻辑：遍历菜单树上面的路由，添加到路由数组中，但菜单树上存在重复的路由（类似模型页面路由），最后也都被重复添加进路由树
 * 更改：初始化路由设置为模型路由总和 modelRoutes，并且取消实际模型[list.vue]中的动态添加路由操作，统一在这里处理好路由
 *      projectRouterList = 初始路由 = 模型路由
 *      对需要添加到projectRouterList中的路由进行判断，满足 (！==模型路由 && ！== projectRouterList中已存在对象)
 */
import modelRoutes from '@/router/modules/model'
import { Is_External_Url } from '@/utils/validate/index'
// TODO 测试页面模块
const notFoundComponent = () => import('@/views/default/404.vue')
// 初始化数据
let routerList = modelRoutes[0].children
// 初始化权限按钮
let btnPermissionInfo = []

/** 根据菜单树的数据构造出两个模块。1-项目菜单（点击可跳转对应界面）， 2-路由树
 * @export
 * @param {*} menuTree: 接口初始菜单树
 */
export default function MakeRouteAndMenu (menuTree) {
    // 遍历操作menuTree中的节点数据，并且生成路由数据
    dealRoutesList(menuTree)
    // 操作上面步骤之后，将结果数据返回
    return {
        // 权限路由树
        routerList: modelRoutes,
        // 左侧菜单树
        menuList: menuTree,
        // 菜单权限按钮
        buttons: btnPermissionInfo
    }
}

/** 操作初始传入菜单树数据
 * @param {*} list：Array
 * @param {String} name: 父级显示名称
 */
function dealRoutesList (list, name = '') {
    list.forEach(i => {
        // 处理每个节点数据
        makeRouteItem(i, name)
        if (i.children?.length > 0) {
            // 递归调用
            dealRoutesList(i.children, i.menuPathName)
        }
    })
}

/** 操作每个节点对象数据；构造数据结构
 *  数据节点分为两种： 一种是目录，一种是实际业务页面，通过item.component是否存在值进行判断
 *      --员工
 *        -- 员工信息
 *        -- 员工工资
 *      接口返回的菜单树数据结构类似上面举例：这里员工就只是一个目录，不需要处理路由，
 *      下面员工信息与工资是具体页面，有指定路由地址，就需要特殊处理
 *  需要知道：
 *      菜单树的初始数据path因为参杂其他数据没法直接使用，path初始值会使用component的值，此时不带任何参数
 *          path: /list
 *      路由要的path是： /list/:mdCode/:symbol   (list后为页面需要接收的参数的形参)
 *      点击菜单需要的path是： /list/M11/213  (list后为页面需要接收的参数的实际值)
 *      函数的作用就是朝着这个方向去修改，用初始菜单数据构造出
 *          1. 左侧菜单树， 点击要有 /list/M11/213
 *          2. 路由树，添加进路由确保格式为 /list/:mdCode/:symbol
 * @param {*} item： Object-节点数据对象
 * @param {String} menuPathName: 父级显示名称
 */
function makeRouteItem (item, menuPathName) {
    // 判断是页面路由还是目录-true为页面路由
    if (item.component) {
        // 获取path上相关参数
        let pathParams = processParams(item.params)
        // 非第三方url
        if (!Is_External_Url(item.path)) {
            // 有些地址有/,有些地址没有/，统一处理去掉/
            item.path = delPathSign(item.component)
        }
        // 构造路由path： 形如/list/:mdCode/:symbol
        let routerPath = item.path + pathParams.routerParams
        // 判断路由列表中是否已存在该路由：详细考虑可以查看当前js最顶部文字说明
        if (existPath(routerPath)) {
            let _routerObj = null
            let component = null
            try {
                if (process.env.NODE_ENV === 'development') {
                    // 开发环境一次性加载，避免多路由热更新缓慢
                    component = require(`@/views/${item.path}.vue`).default
                } else {
                    // 打包环境，按照路由懒加载拆分页面
                    component = () => import(`@/views/${item.path}.vue`)
                    // 当上面路由找不到时会使用下面模块，这里不写到时404页面会出不来
                    // 由于import是使用懒加载，只有在使用的时候才会加载该页面，
                    // 因此import时会直接使用页面地址，即使页面地址不存在也不会报错，
                    // 也无法使用404替换当真正使用的时候就会找不到页面而报错，因此需要
                    // 再这里多添加404，找不到时会可以使用404页面
                    component = notFoundComponent
                }
            } catch (e) {
                component = notFoundComponent
            }
            // 构造需要添加到路由数据中的对象数据
            _routerObj = {
                path: routerPath,
                component: component
            }
            // 添加进路由数据列表（非项目路由树）
            routerList.push(_routerObj)
        }
        // 非第三方url
        if (!Is_External_Url(item.path)) {
            // 构造页面具体path /list/M11/213
            item.path = `/${item.path}${pathParams.realParams}`
        }
        // 构造页面需要的请求参数：请求参数会在使用的时候构造成/list/M11/213?otherParams1=1&otherParams2=2
        item.query = pathParams.query
        // 结合查询参数拼接path：/list/M11/213?otherParams1=1&otherParams2=2
        let queryParamsList = item.query && Object.keys(item.query)
        if (queryParamsList.length > 0) {
            let queryParams = '?'
            // 遍历拼接查询参数成?otherParams1=1&otherParams2=2
            queryParamsList.forEach(i => {
                queryParams += `${i}=${item.query[i]}`
            })
            // 更新path属性
            item.path += queryParams
        }
        setPermissionBtns(item, item.path)
    }
    // TODO 待删 -----简易模拟icon start
    // let icon = item.icon
    // switch (item.name) {
    //     case '首页工作台':
    //         icon = 'homeWorkbench-fill'; break
    //     case '系统管理':
    //         icon = 'setting-fill'; break
    //     case '可视化面板':
    //         icon = 'view-fill'; break
    //     case '接口信息管理':
    //         icon = 'view-fill'; break
    //     case '面板门户':
    //         icon = ''; break
    //     case '图表管理':
    //         icon = 'chart-fill'; break
    //     case '组织机构':
    //         icon = 'organization-fill'; break
    //     case '日志管理':
    //         icon = ''; break
    //     case '角色权限':
    //         icon = 'safe-fill'; break
    //     case '系统配置':
    //         icon = 'systemConfiguration-fill'; break
    //     case '报表设计器':
    //         icon = 'reportDesigner-fill'; break
    //     case '图标库':
    //         icon = 'iconLibrary-fill'; break
    //     case '前端demo':
    //         icon = ''; break
    //     case '定时任务管理':
    //         icon = 'timedTaskManage-fill'; break
    //     case '个人中心':
    //         icon = 'my-fill'; break
    //     case '我的资料':
    //         icon = 'document-fill'; break
    //     case '首页':
    //         icon = 'home-fill'; break
    //     case '我的头像':
    //         icon = 'myAvatar-fill'; break
    //     case '修改密码':
    //         icon = 'password-fill'; break
    //     default:
    //         icon = icon
    // }
    // item.icon = icon
    // TODO 待删 -----简易模拟icon end

    // 配置页面路由与目录的名称与图标
    item.meta = {
        title: item.name,
        icon: item.icon
    }
    // 构造名称列表，用于面包屑展示
    item.menuPathName = menuPathName ? `${menuPathName}-${item.name}` : item.name
}

// 根据路由路径添加路由权限按钮
function setPermissionBtns (routeItem, path) {
    // 查找是否已存在相同的菜单
    let bIndex = btnPermissionInfo.findIndex(b => b.path === path)
    if (~bIndex) {  // 已存在：替换权限按钮
        btnPermissionInfo[bIndex].btnList = routeItem.btnList || []
    } else {        // 不存在：添加权限按钮
        btnPermissionInfo.push({
            path: path,
            btnList: routeItem.btnList
        })
    }
}

/**
 * 判断路由列表中是否已存在该条数据
 *
 * @param {*} path
 * @returns Boolean
 */
function existPath (path) {
    let status = routerList.findIndex(i => i.path === path) === -1
    return status
}

/** 干掉字符串中首个斜杆,统一路径格式
 *  所有路由都挂靠在LayoutComponent中，都作为其子页面，children中配置path不需要前缀‘/’
 *
 * @param {*} path path路径
 * @returns
 */
function delPathSign (path) {
    if (path.slice(0, 1) === '/') {
        return path.slice(1)
    }
    return path
}

/**
 * 处理URL上的参数
 * @param {String} urlParams 格式为：param1=xxx&param2=xxx 的字符串
 * @return {{routerParams: string, realParams: string}}
 */
function processParams (urlParams) {
    let query = {}
    urlParams.split('&').forEach(p => {
        let arr = p.split('=')
        if (arr[0]) {
            query[arr[0]] = arr[1]
        }
    })
    let routerParams = ''
    let realParams = ''
    // 如果参数中带有模型编码(mdCode)，把模型编码作为URL参数，以便区分不同的模型页面
    if (query.mdCode) {
        routerParams = '/:mdCode/:symbol'
        let symbol = query.symbol || 'placeholder'
        realParams = `/${query.mdCode}/${symbol}`
        delete query.mdCode
        delete query.symbol
    }
    return {
        routerParams,
        realParams,
        query
    }
}