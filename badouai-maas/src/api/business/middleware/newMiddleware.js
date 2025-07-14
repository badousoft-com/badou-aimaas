import { request } from '@/service/request'
// 新增中间件-基础配置-获取默认的环境推荐配置
function getServerConfigurationFieldsOption () {
    return request({
        url: `/jdbc/common/basecommonlist/listJSON.do?mdCode=fbpt_k8s_server_conf`,
        method: 'get'
    })
}
// 获取中间件版本
function getComponentVersion (params) {
    return request ({url: '/project/component/componentsmanagerlist/getComponentVersion', params})
}
// 新增中间件-保存
function saveComponentManager (params) {
    return request (
        {
            url: `/project/component/componentsmanagersave/saveComponentManager`,
            method: 'post',
            params
        }, {
            headers: {
                'Content-Type': 'application/json'
            }
        })
}
// 获取明文密码
function getPwd (str = '') {
    const url = `/project/component/componentsmanagerlist/encodeAccessPwd`
    return request({
        url,
        params: { values: str },
        method: 'get'
    })
}
export {
    getServerConfigurationFieldsOption,
    saveComponentManager,
    getComponentVersion,
    getPwd
}