/* 模型路由: 所有模型菜单都应该在这里统一管理起来 */

const routes = [
    {
        path: '/',
        component: () => import('@/layout'),
        children: [
            // stander
            {
                name: 'standerList',
                path: 'module/stander/list/:mdCode/:symbol',
                component: () => import('@/views/module/stander/List'),
                meta: {
                    // 标记是否含有列表，用于控制列表切换页面时toolTip停留的bug，
                    // 所有含有列表的页面都需要添加此标识
                    hasList: true,
                    // 是否需要缓存页面
                    keepAlive: true
                }
            }, {
                name: 'standerEdit',
                path: 'module/stander/edit/:mdCode/:id',
                component: () => import('@/views/module/stander/Edit')
            },
            // tree
            {
                name: 'standerListTree',
                path: 'module/tree/list/:mdCode/:symbol',
                component: () => import('@/views/module/tree/List'),
                meta: {
                    // 标记是否含有列表，用于控制列表切换页面时toolTip停留的bug，
                    // 所有含有列表的页面都需要添加此标识
                    hasList: true,
                    // 是否需要缓存页面
                    keepAlive: true
                }
            }, {
                name: 'standerEditTree',
                path: 'module/tree/edit/:mdCode/:id',
                component: () => import('@/views/module/tree/Edit')
            },
            // dic
            {
                path: 'module/dic/list/:mdCode/:symbol',
                component: () => import('@/views/module/dic/List')
            }, {
                path: 'module/dic/edit/:mdCode/:id',
                component: () => import('@/views/module/dic/Edit')
            },
            // view
            {
                path: 'module/view/view/:mdCode/:id',
                component: () => import('@/views/module/view/View')
            },
            // flow
            {
                path: 'module/flow/list/:mdCode/:symbol',
                component: () => import('@/views/module/flow/List')
            }, {
                // 流程定义-编辑
                path: 'module/flow/defineEdit/:mdCode/:id',
                component: () => import('@/views/module/flow/DefineEdit')
            }, {
                // 流程发起审核--旧版路由
                path: 'module/flow/flowVerify/:mdCode/:id/:boStatus',
                // path: 'module/flow/flowVerify/:mdCode/:id/:boStatus/0', // 等同这句：0-编辑
                component: () => import('@/views/module/flow/FlowVerify')
            }, {
                // 流程发起审核--新版路由，isView: 0-编辑， 1-查看
                path: 'module/flow/flowVerify/:mdCode/:id/:boStatus/:isView',
                component: () => import('@/views/module/flow/FlowVerify')
            }, {
                // 流程审核-编辑模式 【worklistId：待处理事项id（待办、已办等）】
                path: 'module/flow/workEdit/:worklistId/:mdCode?',
                component: () => import('@/views/module/flow/WorkEdit')
            }, {
                // 流程审核-查看 【worklistId：待处理事项id（待办、已办等）】
                path: 'module/flow/workView/:worklistId/:mdCode?',
                component: () => import('@/views/module/flow/WorkView')
            }, {
                // 流程审核-查看模式 【worklistId：待处理事项id（待办、已办等）】
                path: 'module/flow/view/:worklistId/:mdCode?',
                component: () => import('@/views/module/flow/View')
            }, {
                // 流程审核-查看模式-是否审核状态 【worklistId：待处理事项id（待办、已办等）】
                // verifyStatus: 决策是否请求流程路由按钮 0-不加载， 1-加载
                path: 'module/flow/tableView/:worklistId/:verifyStatus/:mdCode?',
                component: () => import('@/views/module/flow/TableView')
            }, {
                // 流程审核-查看模式 【worklistId：待处理事项id（待办、已办等）】
                // 与上一个路由的区别在于：这里默认verifyStatus为0
                path: 'module/flow/tableView/:worklistId',
                component: () => import('@/views/module/flow/TableView')
            },
            // tab
            {
                path: 'module/tab/list/:mdCodes/:symbol',
                component: () => import('@/views/module/tab/List')
            }
        ]
    }
]
export default routes