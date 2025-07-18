
import { request } from '@/service/request'

export function GetReportDetailByCode (data) {
    return request({
        url: '/report/reportinfo/reportinfoedit/getReportDetailByCode.do',
        method: 'post',
        params: data
    })
}
// 重新同步
export function ReloadReport (data) {
    return request({
        url: '/project/schedule/etl/etltasksave/startIgnoreTimeByCode',
        method: 'post',
        params: data
    })
}
// 请求弹窗code
export function InitSearchCode (data) {
    return request({
        url: '/report/dataset/reportdatasetedit/getResultKeyMapByCode.do',
        method: 'get',
        params: data
    })
}