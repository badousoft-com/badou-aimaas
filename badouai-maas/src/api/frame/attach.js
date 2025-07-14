/* ================图片附件请求相关接口================ */
import { request } from '@/service/request'

// 获取附件/图片列表
function Get_Attach_List (data) {
    return request({
        url: '/attach/action/attachsupport/listJSON.do',
        method: 'get',
        params: data
    })
}

// 获取图片/附件地址（根据attachId）
const Attach_Url_By_Id = '/attach/action/attachsupport/downloadAttach.do?attachId='

// 上传附件地址
const Upload_File_Url = '/attach/action/attachsupport/uploadFile.do'

/**
 * 根据附件id获取完整附件地址
 * @param {String} attachId 附件id
 * @returns {String} 附件地址
 */
function Get_Attach_Url (attachId) {
    return process.env.VUE_APP_BASE_API + Attach_Url_By_Id + attachId
}

// 上传单附件
function Upload_File (params) {
    return request({
        url: Upload_File_Url,
        method: 'post',
        params
    })
}

// 获取切片上传信息
function Get_Fragment_Info (data) {
    return request({
        url: '/attach/action/attachsupport/fragmentUploadFile',
        method: 'post',
        params: data,
    }, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

// 上传子切片
function Upload_Fragment_Child (data) {
    return request({
        url: '/attach/action/attachsupport/uploadSubFile',
        method: 'post',
        params: data,
    }, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

// 根据分片主ID获取为上传的详情集合
function Get_Fragment_Datil (data) {
    return request({
        url: `/attach/action/attachsupport/getWithoutUpload/${data.id}`,
        method: 'post',
        params: {},
    })
}

// import { Upload_File } from '@/api/frame/attach'
export {
    // 获取附件/图片列表
    Get_Attach_List,
    // 获取图片/附件地址（根据attachId）
    Attach_Url_By_Id,
    // 上传单附件
    Upload_File,
    // 根据附件id获取完整附件地址
    Get_Attach_Url,
    // 获取切片上传信息
    Get_Fragment_Info,
    // 上传子切片
    Upload_Fragment_Child,
    // 根据分片主ID获取为上传的详情集合
    Get_Fragment_Datil,
}