/* ================数据字典请求相关接口================ */
import { request } from '@/service/request'
import GlobalConst from '@/service/global-const'

// 获取数据字典
function Get_Dic_List (url, data) {
    return request({
        url: url || GlobalConst.dic.url,
        method: 'get',
        params: data
    })
}

export {
    // 获取数据字典
    Get_Dic_List
}