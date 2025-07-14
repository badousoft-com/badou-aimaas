/* ================数据字典请求相关接口================ */
import { request } from '@/service/request'

// 获取数据字典
function Get_Attach (data) {
    let url = `${process.env.VUE_APP_BASE_API}/attach/action/attachsupport/downloadAttach.do`
    return `${url}?attachId=${data}`
}

// 获取验证码
function Get_Verification_Code (data) {
    return request({
        url: '/system/security/logon/createCode',
        method: 'post',
        params: data
    })
}

// 获取系统运行日志
function Get_System_Log (data) {
    return request({
        url: '/actuator/logfile',
        method: 'get',
        params: data
    })
}

// 获取富文本保存附件的接口地址
function Save_RichText_File_URL (data) {
    return `${process.env.VUE_APP_BASE_API}/ueditor/ueditorserve/dispatcher.do?action=${data}`
}

// 生成公私钥
function Generate_Key () {
    return request({
        url: '/cfg/syscfgsafeedit/generateRsaKey',
        method: 'get'
    })
}

export {
    // 获取数据字典
    Get_Attach,
    // 获取验证码
    Get_Verification_Code,
    // 获取系统运行日志
    Get_System_Log,
    // 获取富文本保存附件的接口地址
    Save_RichText_File_URL,
    // 生成公私钥
    Generate_Key,
}
