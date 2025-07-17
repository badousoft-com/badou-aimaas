import { request } from '@/service/request'

// 获取运行实例列表
function Save_K8s_Config (data) {  
    return request({
        url: '/project/server/k8sserverconfedit/saveK8sConfig',
        method: 'post',
        params: data
    })
}

export {
    Save_K8s_Config
}