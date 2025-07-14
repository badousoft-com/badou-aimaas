import Vue from 'vue'
import { Loading, Message, MessageBox } from 'element-ui'
import axios from 'axios'
import { AddressBook, closeAddressBook } from '@/components/frame/index.js'
import { Get_Full_Url } from '@/service/url'
import { Get_Header_Safe_Params } from '@/service/safe/request'
import { getToken } from '@/service/auth'

Vue.use(Loading.directive)
export default {
    install (Vue, options) {
        Vue.prototype.BASEURL = process.env.VUE_APP_BASE_API
        Vue.prototype.PREVIEW_URL = process.env.VUE_APP_PREVIEW_URL
        /* 默认图片 */
        Vue.prototype.DEFAULT = 'this.src=""'
        Vue.prototype.$loading = Loading.service
        Vue.prototype.wrongTip = '请求数据异常,请稍后重试'
        Vue.prototype.tokenFalseTip = '会话失效,请重新登录'
        Vue.prototype.$message = Message
        Vue.prototype.$confirm = MessageBox.confirm
        Vue.prototype.$msgbox = MessageBox
        Vue.prototype.$alert = MessageBox.alert
        Vue.prototype.$prompt = MessageBox.prompt
        Vue.prototype.axios = axios
        Vue.prototype.addressBook = AddressBook
        // 临时添加，用于关闭地址本选择弹窗
        Vue.prototype.closeAddressBook = closeAddressBook
        /* 静态变量 */
        Vue.prototype.BASEDATA = {
            /* 单页请求数-large */
            perPageSize_large: 15,
            /* 单页请求数-middle */
            perPageSize: 10,
            /* 单页请求数-small */
            perPageSize_small: 5,
            /* 最高一级父组织id */
            parent_org_id: 'ROOT'
        },
        /* 关闭loading */
        Vue.prototype.loading = {}
        Vue.prototype.loadingClose = function () {
            setTimeout(() => {
                try {
                    if (Vue.prototype.loading.close) {
                        Vue.prototype.loading.close()
                    }
                } catch (e) {
                    console.error(e)
                }
            }, 1000)
        }
        /**
         * 提交文件的post请求，已跟post请求进行合并，此方法待删除，不再推荐使用
         */
        Vue.prototype.postFile = function (url, params, callback, isloading = false) {
            url = Get_Full_Url(url)
            if (!isloading) {
                Vue.prototype.loading = this.$loading({
                    lock: true,
                    text: 'Loading',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                })
            }
            let formData = new FormData()
            let config = {
                withCredentials: true,
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
            // 提前修改值
            Object.keys(params).forEach(key => {
                if (!(params[key] instanceof Array)) {
                    // 优先更新参数
                    // Vue.prototype.Null_Judge：作用等同空值合并运算符
                    params[key] = Vue.prototype.Null_Judge(params[key], '')
                }
            })
            // 设置请求头安全参数
            config.headers = Object.assign(
                {},
                config.headers,
                Get_Header_Safe_Params(params, url),
                getToken() ? { token: getToken() } : {}
            )
            Object.keys(params).forEach(key => {
                if (params[key] instanceof Array) {
                    let allFileList = params[key]
                    if (allFileList && allFileList.constructor === Array && allFileList.length > 0) {
                        // ready类型的表示非接口数据，是当前新选择添加的数据,过滤掉已提交的数据
                        let _addFiles = allFileList.filter(i => i.status === 'ready').map(i => {
                            // 判断是否存在attachId，该标识为上传服务器后才可获得的标识，表示资源已上传过则此处直接使用attachId进行提交
                            // 没有则使用file对象进行提交
                            return ('attachId' in i && i.attachId) ? i.attachId : i.raw
                        })
                        _addFiles.forEach(e => {
                            // 用于处理附件上传,数组形式传过来,然后拆分成多个
                            formData.append(key, e)
                        })
                    }
                } else {
                    // 再更新formData
                    formData.append(key, params[key])
                }
            })
            axios.post(url, formData, config).then(res => {
                callback(true, res.data)
            }).catch(error => {
                Vue.prototype.loadingClose()
                callback(false) // 失败回调
            })
        },
        /* 封装警告信息 */
        Vue.prototype.alert = function (message, type) {
            if ((type === null) || (type === '') || (type === undefined)) {
                type = 'warning'
            }
            this.$message({
                message: message,
                type: type,
                center: true
            })
        }
    }
}
