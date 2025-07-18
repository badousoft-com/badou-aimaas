import { request } from '@/service/request'


//获取微调的状态
function Get_Status (data) {
    return request({
        url: '/project/maas/tuningmodeln/tuningmodelnlist/getModelStatus',
        method: 'get',
        params: data
    })
}

//获取微调的状态
function Get_App_Logs (data) {
    return request({
        url: '/project/maas/modelapp/modelappsave/getAppLogs',
        method: 'post',
        params: data
    })
}


export {
    Get_Status,
    Get_App_Logs
}