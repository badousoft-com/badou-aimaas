// 表单项-【文件类】类型的逻辑处理
//     1. 目前处理类型为  1-图片Image；  2-附件Attach
//     2. 神奇的地方，表单回显时的数据来源有两个地方
//         2.1： 获取详情接口的表单数据中对应字段值（id值）==>获取到一条数据
//         2.2： 通过表单详情id，字段名称请求指定接口获取到列表数据
//         要么是2.1方式有值，要么是2.2方式有值，唉
//     3. 这类文件的删除需要为提交表单插入一个deleteAttach字段，所有的删除数据都得组装到里面，最终再一起提交

/**
 * 获取文件地址
 * @param {String} attachId 值可能为附件id或者为完整附件地址
 * @returns 
 */
function getFileUrlByAttachId (attachId) {
    if (!attachId) return
    // 判断是否为完整链接地址
    return !Is_External_Url(attachId) ? Get_Attach_Url(attachId) : attachId
}
/**
 * 【接口文件对象属性】转化成【符合当前附件组件的属性】
 * @param {Object} 附件对象
 */
function transferData (i) {
    if (!(i && i.constructor === Object)) return
    return {
        ...i,
        // 设置url属性
        url: getFileUrlByAttachId(i.attachId),
        // 设置文件名属性
        name: i.attachName,
        // 设置文件尺寸属性
        size: i.attachSize,
        // 大小描述文本：如10Kb
        sizeDesc: Bytes_To_Size(i.attachSize),
        // 文件类型
        type: i.fileType,
        // 文件状态：已提交服务器；本地新增的status：ready
        status: 'success',
        // 设置文件后缀
        suffix: i.attachSuffix,
        // 是否为图片
        isImg: Is_Img(i.attachSuffix),
        // 文件使用图标
        icon: GetIcon(i.attachSuffix),
        // 上传中状态
        uploading: false,
        // 是否下载状态
        downloading: false,
        // 下载进度
        downloadPercent: 0,
    }
}

/**
 * 根据数据id与文件类型获取对应字段的附件列表
 * 这里奇怪的点是：fileType需要用字段名作为值才可以，奇奇怪怪（有事找后台）
 * @param {String} attachId 表单数据id
 * @param {String} fileType 目前用的是字段的name属性
 */
function getAttachList (attachId, fileType) {
    return new Promise((resolve, reject) => {
        // 请求附件文件列表
        Get_Attach_List({
            resourceId: attachId,
            fileType: fileType
        }).then(res => {
            resolve(res?.Rows || [])
        }).catch(err => {
            resolve([])
        })
    })
}

/**
 * 根据字段对象，返回需要提交的文件与需要删除的文件
 * @param {Array} value: 新增的文件列表
 * @param {Array} deleteAttach：删除的文件列表
 * @return {Object} {
 *                     _value: {File Array} 提交的文件
 *                     _deleteAttach：{String} 删除的文件资源id字符串 'xx1,xx2,xx3'
 *                  }
 */
function dealAttach ({value, deleteAttach}) {
    let _value = null
    let _deleteAttach = null
    // 2021-08-20修改注销了下面逻辑：将附件/图片的value全部开放，不再限制只展示新增的，这样表单组件的值才合理
    //           而且如果为附件/图片设置了校验，回显时应该字段有值才不会触发校验，原有的会为空（新增为0）导致触发校验
    //           这块的逻辑交给postFile方法中再去执行

    // if (value && value.constructor === Array && value.length > 0) {
    //     // ready类型的表示非接口数据，是当前新选择添加的数据
    //     let data = value.filter(i => i.status === 'ready').map(i => i.raw)
    //     if (data.length > 0) {
    //         _value = data
    //     }
    // }
    _value = value
    if (deleteAttach &&
        deleteAttach.constructor === Array &&
        deleteAttach.length > 0) {
        // 框架图片请求参数配置
        let frameImgSrc_param = '?attachId='
        // 分隔符
        let separator = ','
        // 定义获取删除图片值,最终格式为'imgSrc1, imgSrc2'
        let deleteVal = deleteAttach.map(({url}) => {
            return url.substring(url.indexOf(frameImgSrc_param) + frameImgSrc_param.length)
        }).join(separator)
        _deleteAttach = deleteVal
    }
    return {
        _value,
        _deleteAttach
    }
}

import { Is_Img, GetIcon } from '@/service/attach/index.js'
import { Bytes_To_Size } from '@/utils/file'
import { Get_Attach_List, Get_Attach_Url } from '@/api/frame/attach'
import { Is_External_Url } from '@/utils/validate'
export default {
    // 初始化-展示表单
    initForm: function () {
        
    },
    // 编辑回显-展示表单
    editForm: {
        // 【接口文件对象属性】转化成【符合当前附件组件的属性】
        transferData,
        /**
         * 根据表单详情数据接口获取值数据
         * @param {*} fieldObj 表单项对象
         */
        getSingleImg: function ({value}) {
            if (value) {
                // 图片/附件的只返回id，需要只行拼接数据格式作为值再返回，期望为[{url:'src1'}]
                return [
                    { url: getFileUrlByAttachId(value) }
                ]
            }
            return []
        },
        // TODO:需要后台处理，统一这里返回的接口字段，与list.do同步
        transferSingleAttach: function (itemObj) {
            if (!(itemObj && itemObj.constructor === Object)) return null
            return transferData(itemObj)
        },
        /**
         * 根据特定接口获取表单项值
         * @param {*} itemList 所有表单项集合
         * @param {*} detailId 表单详情id
         */
        getList: function ({name}, detailId) {
            return new Promise((resolve, reject) => {
                // 接口请求附件列表数据
                getAttachList(detailId, name).then(res => {
                    if (res && res.constructor === Array) {
                        // 获取接口数据，transferData进行数据属性转化
                        let result = res.map(i => transferData(i))
                        resolve(result)
                        return
                    }
                    resolve([])
                })
            })
        }
    },
    resultForm: {
        /**
         * 结果处理-用于提交前表单对象处理
         *     1. 更新字段值
         *     2. 检查是否有删除项，有的话更新给全局的删除对象deleteAttachObject
         *          这里会将所有字段的删除信息更新给一个字段，后面updateDeleteAttach一次性更新到提交表单
         * @param {Object} item: 表单项
         * @param {Object} formObject 用于提交接口的最终表单对象
         * @param {Object} deleteAttachObject 删除文件的对象
         */
        updateItem: function (item, formObject, deleteAttachObject) {
            // 图片/附件： 新增时值为Array[File]
            //        删除时值为{deleteAttach: {'字段名'：'attachId1, attachId2'}}, attachId为附件资源id
            let { _value, _deleteAttach } = dealAttach(item)
            // 赋值
            formObject[item.name] = _value
            if (_deleteAttach) {
                // 存在删除数据时统一更新deleteAttach对象的值，最后统一更新表单deleteAttach字段
                deleteAttachObject[item.name] = _deleteAttach
            }
            return formObject
        },
        /**
         * 将删除文件的总对象更新到提交表单
         * @param {Object} formObject 提交的表单数据对象
         * @param {Object} deleteAttachObject 整个表单的删除对象deleteAttachObject
         */
        updateDeleteAttach: function (formObject, deleteAttachObject) {
            try {
                // 删除对象存在值时才更新提交的表单
                if (Object.keys(deleteAttachObject).length > 0) {
                    formObject['deleteAttach'] = JSON.stringify(deleteAttachObject)
                }
            } catch (e) {
                console.error(`执行updateDeleteAttach异常：` + e)
            }
            return formObject
        }
    }
}


