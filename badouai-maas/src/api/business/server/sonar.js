import { request } from '@/service/request'

// 获取应用是否有 sonar 配置，根据这个配置显示按钮
function Get_Has_Sonar_Config_Authority (data) {
    return request({
        url: '/project/jenkins/sonaredit/getHasSonarConfigAuthority',
        method: 'post',
        params: data
    })
}

// 开启 sonar 代码检查
function Enable_Sonar (data) {
    return request({
        url: '/project/jenkins/sonaredit/enableSonar',
        method: 'post',
        params: data
    })
}

// 关闭 sonar 代码检查
function Disable_Sonar (data) {
    return request({
        url: '/project/jenkins/sonaredit/disableSonar',
        method: 'post',
        params: data
    })
}

export {
    Get_Has_Sonar_Config_Authority,
    Enable_Sonar,
    Disable_Sonar
}