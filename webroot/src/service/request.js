import axios from 'axios'
import { MessageBox, Message, Notification } from 'element-ui'
import store from '@/store'
import qs from 'qs'
import { Get_Full_Url, Separate_Url } from '@/service/url'

import { Get_Header_Safe_Params } from '@/service/safe/request'
import { getToken } from '@/service/auth'
import GlobalConst from '@/service/global-const'
import { Null_Judge } from '@/utils'

import { Get_Data_By_Path } from '@/utils'
import { Upload_Fragment_Files } from './attach/upload'

// 操作提醒事件间隔,warnTime时间间隔后允许再次提醒,避免多个接口同时报错带来一系列提醒
const warnTime = 1000

// TODO: 业务改动(让报错不弹出窗口提示) START
let showTip = true
function setShowTipFlag (oldFlag, newFlag) {
    if (newFlag !== undefined) {
        showTip = newFlag
    } else {
        showTip = oldFlag
    }
}
// 业务改动(让报错不弹出窗口提示) END

// 提醒窗口是否正在运行 false-当前无活跃提醒窗
let tipRunStatus = false

// 项目中拥有明确接口地址使用,默认不需要带请求地址域名部分
export function request (requestObj, reqConfig = {}, resConfig = {}) {
    // TODO: 业务改动(让报错不弹出窗口提示) 新增代码 start
    setShowTipFlag(showTip, requestObj.showTip)
    // 业务改动(让报错不弹出窗口提示) end

    return new Promise((resolve, reject) => {
        requestObj.url = Get_Full_Url(requestObj.url)
        finalRequest(requestObj, reqConfig, resConfig)
            .then(res => resolve(res))
            .catch(err => {
                console.log(`finalRequest:err--${err}`)
                reject(err)
            })
    })
}

// 请求地址是接口数据传回,并且传回完整地址(含域名)
export function finalRequest (requestObj, selfReqConfig = {}, resConfig = {}) {
    // let { url } = requestObj // eslint语法检测：变量未使用
    // 2021-08-10考虑：最终请求不能为其自动补头，会阻挡请求本地文件与设置跨域这两个功能
    // 2022-08-13考虑：可以添加自动补头功能 TODO:等待重新开启
    //     A. 跨域请求：使用/proxy/xxx, 返回地址/proxy/xxx并进入代理
    //     B. 本地文件：使用/local/xxx, 返回地址/xxx
    //     C. 其余地址：返回 项目域名/xxx
    // 判断地址是否含有域名，没有则补齐
    // requestObj.url = Get_Full_Url(url)
    return new Promise((resolve, reject) => {
        let reqConfig = {
            withCredentials: true,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        }
        // 合并默认config与自定义config属性，更新config的值
        Object.assign(reqConfig, selfReqConfig)
        // 避免修改到源参数对象数据
        let params = Object.assign({}, requestObj.params)
        // 发送请求时设置拦截-修改请求头安全参数
        // axios.interceptors.request.use(
        //     config => {
        //         if (config.method.toUpperCase() === 'GET') {
        //             只需要处理get的，因为部分get的请求会直接通过
        //             config.headers = Object.assign({}, config.headers, Get_Header_Safe_Params(config.params))
        //         }
        //         return config
        //     }
        // )

        axios.interceptors.response.use(
            response => {
                const res = response
                // if the custom code is not 20000, it is judged as an error.
                if (res.status === 200) {
                    return res
                } else {
                    Message({
                        message: res.message || 'Error',
                        type: 'error',
                        duration: 5 * 1000
                    })
                    return Promise.reject(new Error(res.message || 'Error'))
                }
            },
            async error => {
                let errorStatus = error.response ? error.response.status : 500
                console.log('err' + error) // for debug
                if (errorStatus === 401) {
                    // 如果headers中存在casLoginUrl，说明需要进行CAS重定向。将页面重定向到CAS的登陆地址
                    if (error.response.headers?.casloginurl) {
                        location.href = error.response.headers.casloginurl
                    } else if (!tipRunStatus) {
                        tipRunStatus = true
                        setTimeout(() => {
                            tipRunStatus = false
                        }, warnTime)
                        // 根据配置项：token过期时是否询问
                        if (GlobalConst.isAskWhenOutToken) {
                            MessageBox.confirm('当前页面已过期失效,你可以继续留在当前页面,或者选择重新登录', '页面过期', {
                                confirmButtonText: '重新登录',
                                cancelButtonText: '取消',
                                type: 'warning'
                            }).then(() => {
                                store.dispatch('user/resetToken').then(() => {
                                    location.reload()
                                })
                            })
                        } else {
                            store.dispatch('user/resetToken').then(() => {
                                location.reload()
                            })
                        }
                    }
                } else if (errorStatus === 400 || errorStatus === 404) {
                    if (!tipRunStatus) {
                        tipRunStatus = true
                        setTimeout(() => {
                            tipRunStatus = false
                        }, warnTime)

                        // TODO: 业务改动(让报错不弹出窗口提示) START
                        // 新增代码 start
                        if (showTip) {
                            MessageBox.alert('请求资源找不到,请联系管理员', '资源缺失', {
                                type: 'error'
                            })
                        }
                        // 新增代码 end
                        /* // 原代码 start
                        MessageBox.alert('请求资源找不到,请联系管理员', '资源缺失', {
                            type: 'error'
                        })
                        // 原代码 end */
                        // 业务改动(让报错不弹出窗口提示) END
                    }
                } else if (errorStatus === 403) {
                    if (!tipRunStatus) {
                        tipRunStatus = true
                        setTimeout(() => {
                            tipRunStatus = false
                        }, warnTime)
                        MessageBox.alert('没有该资源的访问权限，请联系系统管理员进行授权', '权限不足', {
                            type: 'error'
                        })
                    }
                } else {
                    if (!tipRunStatus) {
                        tipRunStatus = true
                        setTimeout(() => {
                            tipRunStatus = false
                        }, warnTime)
                        // 非cookies过期，当前可继续操作项目，与cookies过期的操作提醒划分开
                        setTimeout(() => {
                            // message: `当前接口为：${requestObj.url}`,
                            Notification.error({
                                title: '服务器错误',
                                duration: 3000
                            })
                        }, 1000)
                    }
                }
                reject(error)
                return Promise.reject(error)
            }
        )
        if (requestObj.method && requestObj.method.toUpperCase() === 'POST') {
            // 设置请求头安全参数
            reqConfig.headers = Object.assign(
                {},
                reqConfig.headers,
                Get_Header_Safe_Params(params, requestObj.url),
                getToken() ? { token: getToken() } : {}
            )
            let specialInfo = makeRequestData(requestObj.params, reqConfig.headers)
            Object.keys(specialInfo.headers).forEach(key => {
                reqConfig.headers[key] = specialInfo.headers[key]
            })
            let temp_params = specialInfo.params
            switch (reqConfig.headers['Content-Type']) {
                case 'application/x-www-form-urlencoded':
                    temp_params = qs.stringify(params)
                    break
                default:
                    temp_params = specialInfo.params
            }
            axios.post(requestObj.url, temp_params, reqConfig).then(res => {
                let resData = getResData(res, resConfig)
                resolve(resData)
                uploadFragmentFile(specialInfo.fragmentFileList, resData, reqConfig, resConfig)
            }).catch(err => {
                // console.log(`post过程异常：${JSON.stringify(err)}`)
            })
        } else {
            // 合并url上参数与传入params参数，获取最终参数
            let allParams = Object.assign({}, params, Separate_Url(requestObj.url).params)
            // 设置请求头参数
            reqConfig.headers = Object.assign(
                {},
                reqConfig.headers,
                Get_Header_Safe_Params(allParams, requestObj.url),
                getToken() ? { token: getToken() } : {}
            )
            axios({
                method: 'get',
                url: requestObj.url,
                params: allParams,
                ...reqConfig
            }).then(res => {
                resolve(getResData(res, resConfig))
            })
        }
    })
}

/**
 * 获取接口响应数据
 * @param {Object} res 完整响应数据
 * @param {Object} { isGetAllRes: 是否返回完整数据 }
 */
function getResData (res, { isGetAllRes }) {
    if (!(res && res.constructor === Object)) return
    // res:响应数据
    // res.data：核心数据（常用）
    return isGetAllRes ? res : res.data
}

// 组装请求参数
function makeRequestData (params, headers) {
    if (!params) return { params: {}, headers: {} }
    // 对值处理，null或者undefined更换为''
    Object.keys(params).forEach(key => {
        // Null_Judge：等同空值合并运算符
        params[key] = Null_Judge(params[key], '')
        if (!(params[key] instanceof Array)) {
            // 优先更新参数（使用formData形式传参可能会出现值为数组的情况）
            // Null_Judge：作用等同空值合并运算符
            params[key] = Null_Judge(params[key], '')
        }
    })
    // 最终返回参数
    let res = { params: params, headers: {} }
    // 是否使用formData
    let useFormData = headers['Content-Type'] === 'multipart/form-data'
    let { openFragmentUpload, fragmentUploadLimit = 100 } = store.state.settings?.projectSetting || {}
    // let openFragmentUpload = true, fragmentUploadLimit = 3
    // 创建formData对象
    let formData = new FormData()
    // 分片上传附件列表
    let fragmentFileList = []
    // 附件参数处理（符合分片条件：添加进预分片数组，否则：添加进formData）
    // attachInfo格式为 { key: File对象 }
    function dealFile (key, attach, otherParams) {
        // 如果存在附件，则使用formData格式提交参数
        if (attach instanceof File) useFormData = true
        // 是否开启分片上传 && 当前文件大小是否已达到分片上传限制大小
        if (openFragmentUpload &&
            attach && attach.size &&
            attach.size >= fragmentUploadLimit * Math.pow(1024, 2)) {
            fragmentFileList.push({
                file: attach,
                fieldKey: key,
                index: 0,
                ...otherParams
            })
        } else {
            // 用于处理附件上传,数组形式传过来,然后拆分成多个
            formData.append(key, attach)
        }
    }
    Object.keys(params).forEach(key => {
        if (params[key] instanceof Array) {
            let allFileList = params[key]
            if (allFileList && allFileList.constructor === Array) {
                if (allFileList.length > 0) {
                    if (allFileList.some(i => i.url && i.status)) useFormData = true
                    // ready类型的表示非接口数据，是当前新选择添加的数据,过滤掉已提交的数据
                    let _addFiles = allFileList.filter(i => i.status === 'ready').map(i => {
                        // 判断是否存在attachId，该标识为上传服务器后才可获得的标识，表示资源已上传过则此处直接使用attachId进行提交
                        // 没有则使用file对象进行提交
                        return ('attachId' in i && i.attachId) ? i.attachId : i.raw
                    })
                    _addFiles.forEach((e, e_index) => {
                        dealFile(key, e, { index: e_index })
                    })
                } else {
                    delete params[key]
                }
            }
        } else {
            // 再更新formData
            // formData.append(key, params[key])
            dealFile(key, params[key])
        }
    })
    // 如果当前表单包含附件，使用formData形式返回
    if (useFormData) {
        res = {
            params: formData,
            fragmentFileList,
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }
    }
    return res
}

// 分片上传附件
function uploadFragmentFile (fileList, formRes, reqConfig = {}, resConfig = {}) {
    // 没有需要分片上传的附件，无需执行
    if (!fileList.length) return
    // 获取resourceId
    let idPath = resConfig.idPath || 'bean,id'
    let resourceId = Get_Data_By_Path(formRes, idPath)
    // 判断是否存在resourceId
    if (!resourceId) {
        Message({
            message: '附件上传需要表单接口返回id',
            type: 'error',
            duration: 5 * 1000
        })
        console.error(`所上传的附件中存在达到分片上传条件的数据！
                    ${reqConfig.url}表单提交接口需要返回表单详情id，参数路径为res,${idPath}`)
    }
    Upload_Fragment_Files(fileList, { resourceId })
}
