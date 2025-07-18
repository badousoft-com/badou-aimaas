// 公用路由, 项目初始存在路由
import Layout from '@/layout'

const routes = [
    {
        path: '/',
        component: Layout,
        children: [
            {
                name: 'appInstanceController',
                path: 'app/instance/controller',
                component: () => import('@/views/business/appInstance/appController.vue'),
                meta: {
                    // 标记是否含有列表，用于控制列表切换页面时toolTip停留的bug，
                    // 所有含有列表的页面都需要添加此标识
                    hasList: false,
                    // 是否需要缓存页面
                    keepAlive: false
                }
            },
        ]
    },
    {
        path: '/trainbody',
        component: () => import('@/views/business/traindata/TrainDataBody.vue'),
    },
    {
        path: '/tuningmonitoring',
        component: () => import('@/views/business/tuningmonitoring/index'),
    },
    {
        path: '/planmodelchat',
        component: () => import('@/views/business/plammodel/PlanModelChat.vue'),
    },
    {
        path: '/modelapplog',
        component: () => import('@/views/business/modelapp/ModelAppLog.vue'),
    },
    {
        path: '/trainplanlogstatus',
        component: () => import('@/views/business/trainmodel/TrainPlanlogstatus.vue'),
    },
    {
        path: '/tunmodelevaresult',
        component: () => import('@/views/business/tunModel/evaResultPage.vue'),
    },
    {
        path: '/aiMaasScope',
        component: () => import('@/views/business/aiMaasScope/index'),
    },
    {
        name: 'customview',
        path: 'module/view/view/:mdCode/:id',
        component: () => import('@/views/module/view/View')
    },
    //api接口页面
    {
        path: '/apipages',
        component: () => import(/* webpackChunkName: "login" */ '@/views/business/apipages/index.vue')
    },
    //AI聊天界面
    {
        path: '/aichat',
        component: () => import(/* webpackChunkName: "login" */ '@/views/business/modelapp/AIChat.vue')
    },
    //TensorBoard监控页面
    {
        path: '/tensorboardview',
        component: () => import(/* webpackChunkName: "login" */ '@/views/business/tensorboard/index.vue')
    },
    // GPU监控平台
    {
        path: '/trainplangpucardstatus',
        component: () => import('@/views/business/trainmodel/TrainPlancardstatus.vue'),
    },
    {
        path: '/',
        component: Layout,
        children: [

        ]
    },
    {
        path: '/',
        component: Layout,
        children: [

        ]
    }
    // 2. 如果最终想配成访问地址为 /demo/demoChild 的页面 (希望配置有父级目录)
    // {
    //     path: '/demo',
    //     component: Layout,
    //     children: [
    //         {
    //             path: 'demoChild',
    //             component: () => import('@/views/business/Test')
    //         }
    //     ]
    // }
]

export default routes