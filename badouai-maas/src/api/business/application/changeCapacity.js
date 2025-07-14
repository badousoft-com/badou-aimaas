import { request } from '@/service/request'

// 新增/编辑扩/缩容配置
function Save_AppReplicas (data) {
    return request(
        {
            url: '/project/project/replicas/projectappreplicasedit/saveAppReplicas',
            method: 'post',
            params: data
        },
        {
            withCredentials: true,
            headers: {
                'Content-Type': 'application/json'
            }
        })
}

export {
    Save_AppReplicas,
}