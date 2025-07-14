import { Separate_Url, Combine_Url } from '@/service/url'
import { Get_Header_Safe_Params } from '@/service/safe/request'
import GlobalConst from '@/service/global-const'
import { getToken } from '@/service/auth'
import { Has_Value } from '@/utils'
import Vue from 'vue'

// 定义数据容量单位类型数组
const sizeType = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
// 定义转化比例数
const transformNum = 1024

/**
 * 根据传入单位/单位简写，获取文件完整单位
 * @param {String} val 文件单位
 * @returns {String} 完整文件单位
 */
function Get_File_Unit (val) {
    if (!val) return ''
    // 转化为大写
    let _unit = val.toUpperCase()
    // 定义通用单位最后一位，'KB.MB,GB'都是B结尾
    let _commonUnit = 'B'
    // 实际场景中允许使用简写'K,M,G',因此此处判断下决定是否补齐B结尾
    if (_unit.substring(_unit.length - 1) !== _commonUnit) {
        _unit += _commonUnit
    }
    // 判断当前单位是否符合单位列表中的数据，符合则返回
    return sizeType.includes(_unit) && _unit || ''
}
/**
 * 将文件大小转化为磁盘空间大小
 * @param {Number} size
 * @param {String} unit 指定输出单位
 * @return {String} 磁盘空间容量
 * 转换效果如下： 300 => 300K； 2048 => 2KB
 */
function Bytes_To_Size (bytes, unit) {
    // 传入数据非期望数据格式，返回''。存在bytes为0的情况，此处优化为Has_Value
    if (!(Has_Value(bytes) && bytes.constructor === Number)) return ''
    // 获取传入数对应使用单位的下角标
    let index = null
    // 如果传入指定单位
    if (unit) {
        let _unit = `${unit.toUpperCase()}B`.replace(/B+/, 'B')
        index = sizeType.findIndex(i => i === _unit)
    } else {
        // 不传入单位，默认转成最合适单位
        index = sizeType.findIndex((i, index) => bytes < Math.pow(transformNum, index + 1))
    }
    // 当bytes数比sizeType中的单位还要再高N(N >= 1)阶的时候，此时index为-1
    if (index === -1) {
        // 超出当前范围则直接使用最后一个单位进行转换
        index = sizeType.length - 1
    }
    // toFixed(2): 保留小数点后两位
    // n*100/100 :去除n小数点后无实际意义的0
    return `${(bytes / (Math.pow(transformNum, index))).toFixed(2) * 100 / 100 }${sizeType[index]}`
}

/**
 * 获取大小（单位B）
 * @param {*} value 值可以为（100, '100', '100K' --> 前两个100会被解析为100B）
 * @return {Number} 容量大小（单位：B）
 */
function Get_Size (value) {
    // value中必须含有数字
    if (value && parseInt(value).constructor !== Number) {
        console.error(`调用函数Get_Size异常，使用值为${value}`)
        return
    }
    // 统一value String数据格式，为replace准备
    value = value.toString()
    // 定义获取数字部分
    let num = parseFloat(value)
    // 定义获取传入数据除开数字部分的单位
    let unit = value.replace(num, '')
    if (unit) {
        // 若存在单位，则找到对应匹配单位计算转化为单位B对应值
        let currentSizeTypeIndex = sizeType.findIndex(i => i.includes(unit.toUpperCase()))
        if (currentSizeTypeIndex !== -1) {
            // 单位类型能在sizeType中找到对应的匹配项，计算并返回
            return num * Math.pow(1024, currentSizeTypeIndex)
        }
        // sizeType中找不到此对应值，则默认单位B，直接转化为数字并返回
        return parseFloat(num)
    }
    // 不存在单位，则默认单位B，直接转化为数字并返回
    return parseFloat(num)
}

/**
 * 统一规范化文件大小进行展示
 * @param {String} value 传入尺寸
 * @returns 判断是否符合规则，符合则返回尺寸，不符合则返回
 */
function Get_File_Size (value) {
    if (!value) return
    // 定义正则，判断传入数据是否符合【数字+单位?】规则
    let sizeReg = /^(\d+)([A-z]*)?$/
    // 获取匹配数据
    let _res = (value + '').match(sizeReg)
    // 没有结果，直接返回
    if (!_res) return
    // 获取数字部分
    let _size = _res[1]
    // 数字部分缺失，直接返回
    if (!_size) return
    // 获取单位部分
    let _unit = _res[2]
    // 单位不存在，则使用数字区域+默认单位kb，进行返回
    if (!_unit) return `${_size}KB`
    // 获取完整的尺寸单位
    let _realUnit = Get_File_Unit(_unit)
    if (!_realUnit) {
        // 不符合
        console.error(`传入的单位不是文件尺寸单位：${_unit}`)
        return
    }
    // 返回数字+单位
    return _size + _realUnit
}

// 文件大小比较
/**
 * 判断值sizeA是否比sizeB大，返回boolean
 * @param {*} sizeA 比较值
 * @param {*} sizeB 比较值
 * @return {Boolean} sizeA比sizeB大时返回true，反之返回false
 */
function Compare_A_large_B (sizeA, sizeB) {
    // 两个数中必须含有数字
    if (Has_Value(sizeA) && parseFloat(sizeA).constructor !== Number) return
    if (Has_Value(sizeB) && parseFloat(sizeB).constructor !== Number) return
    return Has_Value(Get_Size(sizeA)) &&
           Has_Value(Get_Size(sizeB)) &&
           Get_Size(sizeA) > Get_Size(sizeB)
}

function createObjectURL (object) {
    return (window.URL) ? window.URL.createObjectURL(object) : window.webkitURL.createObjectURL(object)
}

/**
 * 下载文件
 * @param {Object} {name, url, size, progress} 文件对象的name属性 / url属性 / size:文件大小(B) / progress下载进度回调函数
 * @param {Boolean} isStream 是否流文件，默认false,暂无使用
 */
function Download ({ name, url, method = 'post', params = {}, size, progress, loadEnd }, isStream = false) {
    return new Promise((resolve, reject) => {
        // 定义面向用户的失败提醒
        let _failTip = () => {
            Vue.prototype.$message.error('文件下载失败，请联系管理员')
        }
        if (!url) {
            _failTip() // 面向用户的失败提示
            resolve({
                status: false,
                message: '文件地址 / 流文件异常，请检查'
            })
            return  // 停止程序
        }
        // 用此判断是否是本地选择的图片的地址
        if (url.includes('blob:')) {
            // 创建a标签
            let link = document.createElement('a')
            // 设置隐藏
            link.style.display = 'none'
            // 设置文件下载地址
            if (!isStream) {
                // 传入的是文件地址，直接使用
                link.href = url
            } else {
                // 传入的是文件流
                link.href = window.URL.createObjectURL(new Blob([url], { type: 'application/vnd.ms-excel' }))
            }
            // 设置下载文件所使用名称
            if (name) {
                link.download = name
            }
            // 添加该dom元素
            document.body.appendChild(link)
            // 触发点击，执行下载
            link.click()
            // 删除该dom元素回收
            document.body.removeChild(link)
            // 若当前为流文件，则释放掉blob对象
            isStream && window.URL.revokeObjectURL(link.href)
            resolve({
                status: true,
                message: '下载成功'
            })
        } else {
            let xhr = new XMLHttpRequest()
            // 定义null,根据post或get决策是否使用new FormData
            let formData = null
            xhr.open(method, url)
            if (method.toUpperCase() === 'POST') {
                formData = new FormData()
                // method为post时,将参数添加进formData对象中
                params && Object.keys(params).forEach(i => {
                    formData.append(i, params[i])
                })
            } else {
                // get的状态，合并params与url路径上的参数更新params
                let { url: _url, params: _params } = Separate_Url(url)
                params = Object.assign({}, params, _params)
                // 组装url
                url = Combine_Url(_url, params)
            }
            // 获取headers安全参数对象
            let headerParams = Get_Header_Safe_Params(params, url)
            // 检查全局的配置项中是否开启了参数加密，没有开启，则直接停止逻辑
            if (GlobalConst.openSafeRequestParams) {
                if (method.toUpperCase() === 'POST') {
                    formData.append('sign', params.sign)
                } else {
                    url = Combine_Url(url, { sign: params.sign })
                }
            }
            xhr.open(method, url)
            // 将加密参数对象下属性更新到请求headers
            headerParams && Object.keys(headerParams).forEach(i => {
                xhr.setRequestHeader(i, headerParams[i])
            })
            // 若token存在，则加入请求header中
            getToken() && xhr.setRequestHeader('token', getToken())
            // 设置响应类型
            xhr.responseType = 'blob'
            xhr.onreadystatechange = function (e) {
                if (this.status) {
                    let _statusCode = (this.status + '').slice(0, 1)
                    switch (_statusCode) {
                        case '2':
                            break
                        case '4':
                            _failTip() // 面向用户的失败提示
                            resolve({
                                status: false,
                                message: '文件找不到'
                            })
                            break
                        case '5':
                            _failTip() // 面向用户的失败提示
                            resolve({
                                status: false,
                                message: '服务器异常'
                            })
                            break
                        default:
                            // do something
                    }
                }
            }
            xhr.onload = function (e) {
                if (this.status === 200) {
                    // 获取文件流
                    let blob = this.response
                    // 定义获取文件名
                    let fileName = name
                    if (!fileName) {
                        // 若没有传入文件名，则查看后端接口是否有设置文件名
                        try {
                            // 获取response headers中文件名内容
                            let contentDisposition = xhr.getResponseHeader('Content-Disposition')
                            if (contentDisposition) {
                                // 文件名返回内容一般为"attachment; filename='test.xls'"格式
                                let _key = 'filename=' // 设置文件名关键词
                                // 根据关键词找出文件名所在下角标
                                let fileNameIndex = contentDisposition.indexOf(_key)
                                if (~fileNameIndex) {
                                    // 获取文件名并编码，更新文件名内容
                                    fileName = decodeURI(contentDisposition.substring(fileNameIndex + _key.length))
                                }
                            }
                        } catch (e) {
                            console.log(`接口暂不支持前端操作Content-Disposition：${e}`)
                        }
                    }
                    // 若没有传入文件名，接口也没有设置文件名，则停止逻辑，这里文件流需要依靠文件名后缀来生成对应文件
                    // 必须有文件名，没有则直接停止逻辑
                    if (!fileName) {
                        console.error(`文件下载失败，文件名没有传入或者后端没有为文件response设置Content-Disposition`)
                        _failTip() // 面向用户的失败提示
                        resolve({
                            status: false,
                            message: '下载异常，文件名丢失'
                        })
                        return
                    }
                    // 判断msSaveOrOpenBlob兼容IE，按道理不用也行
                    if (window.navigator.msSaveOrOpenBlob) {
                        navigator.msSaveBlob(blob, fileName)
                    } else {
                        // 模拟a标签的点击事件进行文件下载
                        let a = document.createElement('a')
                        let url = createObjectURL(blob)
                        a.href = url
                        a.download = fileName
                        document.body.appendChild(a)
                        a.click()
                        window.URL.revokeObjectURL(url)
                        setTimeout(() => {
                            resolve({
                                status: true,
                                message: '开始下载文件，请等待'
                            })
                        }, 300)
                    }
                }
            }
            // 添加下载进度
            xhr.onprogress = function (event) {
                // 判断是否传入进度函数
                if (!(progress && typeof progress === 'function' && size)) return
                // 获取已加载数据量
                let _loaded = event.loaded
                // 计算已下载的占比数
                let _percent = parseInt(_loaded / size * 100)
                // 执行函数
                progress(_percent, _loaded, size)
            }
            // 加载资源完成后的回调函数
            xhr.onloadend = function (event) {
                // 判断是否传入加载完成函数
                if (!(loadEnd && typeof loadEnd === 'function')) return
                // 执行下载完成回调函数
                loadEnd(event)
            }
            xhr.send(formData)
        }
    })
}

export {
    // 获取尺寸
    Get_File_Size,
    // 文件大小转化为磁盘空间容量大小
    Bytes_To_Size,
    // 比较文件空间容量大小 第一个参数大于第二个参数时为true
    Compare_A_large_B,
    // 根据地址与名称下载指定文件
    Download
}
