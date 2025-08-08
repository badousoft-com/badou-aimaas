/*
 * @Description: 我的消息对应api
 */
import { request } from '@/service/request'

// 根据登录用户获取未读消息数
function Get_Message_Count (data) {
    return request({
        url: '/message/information/sysinformationlist/unreadCount',
        method: 'get',
        params: data
    })
}
// 将消息设置成已读
function Set_Message_Read (data) {
    return request({
        url: '/message/information/sysinformationedit/readSysInfo',
        method: 'get',
        params: data
    })
}
const Get_Msg_Info = '/message/information/sysinformationedit/getSysInfo/'

export {
    // 根据登录用户获取未读消息数
    Get_Message_Count,
    // 将消息设置成已读
    Set_Message_Read,
    Get_Msg_Info
}
