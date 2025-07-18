/*
 * @FilePath: @/service/attach/index.js
 * @Description: 附件相关操作
 */

/**
 * 判断类型后缀是否为图片格式
 * @param {String} suffix 文件后缀
 * @return {Boolean} true-是图片 / false-非图片
 */
import GlobalConst from '@/service/global-const'
import { Download } from '@/utils/file'
import Vue from 'vue'
const Is_Img = function (suffix) {
    if (!suffix) return false
    // 正则去掉后缀格式的【符号.】
    suffix = '.' + suffix.replace(/\./g, '')
    // 获取常用图片格式
    let commonImgType = GlobalConst.imgTypeList
    return commonImgType.findIndex(i => i.toUpperCase() === suffix.toUpperCase()) > -1
}

// 定义文件类型集合
const Attach_Type_Data = {
    img: GlobalConst.imgTypeList,
    word: GlobalConst.wordTypeList,
    ppt: GlobalConst.pptTypeList,
    excle: GlobalConst.excleTypeList,
    pdf: GlobalConst.pdfTypeList,
    video: GlobalConst.videoTypeList,
    audio: GlobalConst.audioTypeList,
}

/**
 * 根据传入文件名或后缀获取文件住分类型
 * @param {String} name 含有文件后缀的文件名或者后缀名
 * @returns {String} 所属的文件类型
 */
function Get_Attach_Type (name) {
    if (!name) return
    let _suffix = null
    let _index = name.lastIndexOf('.')
    if (!~_index) {
        _suffix = `.${name}`
    } else {
        _suffix = name.slice(_index)
    }
    // 到此处_suffix值为'.pdf'
    let _result = 'others'
    try {
        Object.keys(Attach_Type_Data).forEach(i => {
            if (Attach_Type_Data[i]
                .map(i => i.toUpperCase())
                .includes(_suffix.toUpperCase())) {
                _result = i
                throw 'end'
            }
        })
    } catch (_) {
        // do something
    }
    return _result
}

/**
 * 获取文件图标
 * @param {String} suffix:文件类型
 * @return {String} 图标名称
 */
const Get_Icon = function (suffix) {
    // 定义获取默认的文件图标名称
    let icon = GlobalConst.icon.file
    if (!suffix || suffix.constructor !== String) {
        return icon
    }
    // 去除文件格式中的【符号.】
    suffix = '.' + suffix.replace(/\./g, '').toLowerCase()
    // 定义文件类型与图标关系
    let _data = {
        word: GlobalConst.wordTypeList, // word文档
        ppt: GlobalConst.pptTypeList, // ppt保存类型
        excel: GlobalConst.excleTypeList, // excle保存类型
        pdf: GlobalConst.pdfTypeList, // pdf保存类型
        videoF: GlobalConst.videoTypeList, // 视频类型
        audio: GlobalConst.audioTypeList, // 音频类型
        CAD: GlobalConst.CADTypeList, // CAD类型
        rar: GlobalConst.rarTypeList, // 压缩包类型
        textF: GlobalConst.plainTextTypeList, // 纯文本类型
    }
    try {
        Object.keys(_data).forEach(i => {
            // 找到匹配的文件后缀，使用类型图标
            if (_data[i].includes(suffix)) {
                // 更新结果
                icon = i
                // 终止循环
                throw 'end'
            }
        })
    } catch (_) {
        // do something
    }
    // 返回结果图标
    return icon
}

/**
 * 下载文件
 * @param {Object} {name, url, size, progress} 文件对象的name属性 / url属性 / size:文件大小(B) / progress下载进度回调函数
 */
const Download_Ser = function (option) {
    return new Promise((resolve, reject) => {
        // 下载文件
        Download(option).then(res => {
            // status: 下载状态  true-成功 / false-失败
            // message: 提示信息
            let { status, message } = res
            if (!status) {
                Vue.prototype.$message.warning(message)
            }
            resolve(res)
        })
    })
}

/**
 * 下载附件(涵盖结合页面的动画属性)
 * @param {Object} {name, url} 文件对象的name属性与url属性
 */
function Download_Item_Animation (item) {
    if (!item) return
    // 当前正在下载，则停止当前操作，等待下载完成
    if (item.downloading) return
    // 设置下载状态启动
    item.downloading = true
    // 设置初始进度
    item.downloadPercent = 0.1
    Download_Ser({
        ...item,
        /**
         * 下载进度回调函数
         * @param percent {Number} 进度
         * @param loaded {Number} 已下载量
         * @param total {Number} 数据总量
         */
        progress: (percent, loaded, total) => {
            // 更新已下载-占比数
            item.downloadPercent = percent
            if (percent === 100) {
                setTimeout(() => {
                    // 当下载完成后，取消下载状态，更新下载占比为0
                    item.downloading = false
                    item.downloadPercent = 0
                }, 500)
            }
        }
    })
}

export {
    // 根据传入的文件后缀判断是否为图片
    Is_Img,
    Is_Img as IsImg, // TODO 即将废弃
    // 根据传入的文件后缀返回对应使用的缩略图标
    Get_Icon,
    Get_Icon as GetIcon,  // TODO 即将废弃
    // 下载文件
    Download_Ser,
    // 下载附件(涵盖结合页面的动画属性)
    Download_Item_Animation,
    // 定义文件类型集合
    Attach_Type_Data,
    // 根据传入文件名或后缀获取文件住分类型
    Get_Attach_Type,
}