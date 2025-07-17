/* ================项目系统配置相关接口================ */
import { request } from '@/service/request'

// 获取项目配置数据(比如背景图,主题,项目名称等配置信息)
function Get_Project_Setting (params) {
    return request({
        url: '/system/security/logon/findSystemInfo',
        method: 'post',
        params: params
    })
}

export {
    // 获取项目配置数据(比如背景图,主题,项目名称等配置信息)
    Get_Project_Setting
}
