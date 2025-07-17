/* 公用路由, 项目初始存在路由 */

import Layout from '@/layout'

const routes = [
    {
        path: '/login',
        component: () => import(/* webpackChunkName: "login" */ '@/views/default/Login')
    }, {
        path: '/404',
        component: () => import('@/views/default/404')
    }, {
        path: '/auth',
        component: () => import('@/views/default/Auth')
    },
    {
        path: '/',
        component: Layout,
        redirect: '/admin/overview',
        children: [
            {
                path: 'admin/overview',
                component: () => import(/* webpackChunkName: "base" */ '@/views/default/Overview')
            }, {
                // 用于测试例子
                path: '/text',
                component: () => import(/* webpackChunkName: "base" */ '@/views/default/Text'),
                meta: {
                    title: '测试页面'
                }
            }, {
                // 用于测试例子
                path: '/formDesigner',
                component: () => import(/* webpackChunkName: "base" */ '@/views/system/FormDesigner.vue'),
                meta: {
                    title: '表单设计器'
                }
            }, {
                path: '/Personal/UserData',
                component: () => import(/* webpackChunkName: "user" */ '@/views/personal/UserData')
            }, {
                path: '/system/RolePermission/:mdCode/:symbol',
                component: () => import('@/views/system/RolePermission')
            }, {
                path: '/default/iconTool/Base',
                component: () => import('@/views/default/iconTool/Base')
            }, {
                path: '/default/iconTool/Business',
                component: () => import('@/views/default/iconTool/Business')
            }, {
                path: '/personal/UserAvatar',
                component: () => import('@/views/personal/UserAvatar')
            }, {
                path: '/personal/ModifyPwd',
                component: () => import('@/views/personal/ModifyPwd')
            }, {
                path: '/system/runningLog',
                component: () => import('@/views/system/RunningLog')
            }, {
                path: '/default/GenerateKey',
                component: () => import('@/views/default/GenerateKey')
            },
            // 面板
            { // 使用新组件拖拽
                path: '/panel/NewPanelEdit',
                component: () => import('@/views/panel/NewPanelEdit')
            }, { // 查看页重构测试模块
                path: '/panel/PanelDesigner',
                component: () => import('@/views/panel/panelDesigner/index.vue')
            }, { // 面板预览
                path: '/panel/view/:panelId',
                component: () => import('@/views/panel/View')
            }, { // 用户面板
                path: '/panel/userDesigner',
                component: () => import('@/views/panel/userDesigner/index.vue')
            },
            // 报表
            {
                path: '/report/stander/:reportCode/:symbol',
                component: () => import('@/views/report/Stander')
            },
            // 外部链接展示页面
            {
                path: '/externalUrl/:path',
                component: () => import('@/views/default/ExternalUrlPage')
            },
            // 刷新跳板页面
            {
                path: '/refresh',
                component: () => import('@/views/default/Refresh')
            }
        ]
    },
    // demo例子路由
    {
        path: '/demo',
        component: Layout,
        children: [
            {
                path: '/',
                component: () => import('@/views/demo/index')
            }, {
                path: 'showPageDialogByJs',
                component: () => import('@/views/demo/items/ShowPageDialogByJs.vue')
            }, {
                path: 'tab',
                component: () => import('@/views/demo/items/DemoTab/index')
            }, {
                path: 'moduleMixinTab',
                component: () => import('@/views/demo/items/ModuleMixinTabPage')
            }, {
                path: 'moduleMixin',
                component: () => import('@/views/demo/items/ModuleMixinPage')
            }, {
                path: 'dialog',
                component: () => import('@/views/demo/items/DemoDialog')
            }, {
                path: 'formJSON',
                component: () => import('@/views/demo/items/FormJSON')
            }, {
                path: 'formJSONSlot',
                component: () => import('@/views/demo/items/FormJSONSlot')
            }, {
                path: 'formStatic',
                component: () => import('@/views/demo/items/FormStatic')
            }, {
                path: 'dialogFormByImport',
                component: () => import('@/views/demo/items/DialogFormByImport')
            }, {
                path: 'dialogFormByJs',
                component: () => import('@/views/demo/items/DialogFormByJs')
            }, {
                path: 'useModelFormOnSelf',
                component: () => import('@/views/demo/items/UseModelFormOnSelf')
            }, {
                path: 'dialogModelFormByJs',
                component: () => import('@/views/demo/items/DialogModelFormByJs')
            },
            // 列表模块
            {
                path: 'list',
                component: () => import('@/views/demo/items/List')
            }, {
                path: 'dialogListByJs',
                component: () => import('@/views/demo/items/DialogListByJs')
            }, {
                path: 'useModelListOnSelf',
                component: () => import('@/views/demo/items/UseModelListOnSelf')
            }, {
                path: 'dialogModelListByJs',
                component: () => import('@/views/demo/items/DialogModelListByJs')
            },
            // 查看模块
            {
                path: 'viewStatic',
                component: () => import('@/views/demo/items/ViewStatic')
            }, {
                path: 'viewModel',
                component: () => import('@/views/demo/items/ViewModel')
            }, {
                path: 'dialogViewByJs',
                component: () => import('@/views/demo/items/DialogViewByJs')
            }, {
                path: 'dialogModelViewByJs',
                component: () => import('@/views/demo/items/DialogModelViewByJs')
            }
        ]
    }
]

export default routes