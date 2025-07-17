import { StartEndSet, FileSet } from '@/components/frame/Common/MForm/frameSet/index'
import { Has_Value } from '@/utils'
import { Get_Switch_Val } from '@/service/switch'
import Vue from 'vue'
import { Get_Async_Options_OnEachItem } from '@/service/base-service'
import GlobalConst from '@/service/global-const'
import { Get_Today, Get_Year } from '@/utils/time'

// 定义请求头
const BASEURL = process.env.VUE_APP_BASE_API
// 方便el-form自定义校验函数时使用以下变量，定义以下变量
let _detailId = ''  // 详情数据id
let _mdCode = ''  // 模型编码

/**
 * 将接口字段转化为本地表单需要的字段数组（例如属性名修改等操作）
 * @param {Array} fieldList 字段数组列表
 * @param {Object} dicObj 数据字典集合对象
 * @param {Object} option 转化的配置项
 */
export async function Change_To_Base_Form (fieldList, dicObj, option) {
    let {
        isView = false, // 是否查看状态下使用,默认值false
        mdCode,  // 模型编码
        detailId, // 数据详情id
    } = option || {}
    // 查看模式时执行下述逻辑：先将字段类型转化
    // 查看返回的类型是viewType，需要对应转化成对应的type方便前端表单组件使用
    if (isView) fieldList = changeFieldAttr (fieldList)
    // 表单字段（属性）转化
    let _result = await ChangeField(fieldList, dicObj, isView, mdCode, detailId)
    // 进一步根据表单值进行特殊化处理
    _result = await UpdateFieldValue(_result, isView, detailId)
    return _result
}

/**
 * 将模型字段转化为适配表单组件的字段数据【核心为属性转换】
 * @param {Array} fieldList 字段数组列表
 * @param {Object} dicObj 数据字典集合对象
 * @param {Boolean} isView 是否查看页面
 * @param {String} mdCode 模型编码
 * @param {String} detailId 数据详情id
 * @return {Array} resultList 表单组件可用字段数组
 */
export async function ChangeField (fieldList, dicObj, isView, mdCode, detailId) {
    // 更新给页面全局变量
    _detailId = detailId
    _mdCode = mdCode
    let resultList = fieldList
    // 定义结果列表
    // let resultList = []
    // 将自定义属性添加回字段对象中（代码搬迁到src/service/module.js的Update_FieldList_Value内）
    // resultList = addCustomAttr(fieldList, isView)
    // 请求获取options数据对应更新相关字段对象（可能存在异步操作）
    resultList = await Get_Async_Options_OnEachItem(resultList, dicObj)
    // 将接口字段对象相关属性-> 修正为 ->本地表单组件需要的属性
    resultList = suitFormAttr(resultList, dicObj, { mdCode, id: detailId })
    return resultList
}

/**
 * 将字段的自定义配置对象下的所有属性更新到字段对象本身
 * @param {Array} fieldList:字段数组
 */
function addCustomAttr (fieldList) {
    // TODO-start：借用设计器中的className字段构造配置自定义属性，等后台提供相关字段再修改为指定字段
    // let selfAttrName = 'className'
    // 真正的自定义属性字段
    let customAttrName = 'customOptions'
    if (fieldList.length > 0) {
        // i[selfAttrName]：自定义传入属性，数据格式为{key1:val1, key2:val2}
        // 执行遍历将自定义属性加到表单字段属性上面去
        fieldList.forEach(i => {
            // =========================================>TODO：即将废弃
            // let configObj = i[selfAttrName] && JSON.parse(i[selfAttrName])
            // if (configObj && configObj.constructor === Object) {
            //     Object.keys(configObj).forEach(j => {
            //         i[j] = configObj[j]
            //     })
            // }
            // 该字段已做造数据用，为避免干扰查看数据，删除吧
            // delete i[selfAttrName]
            // ========================================>
            let customAttrObj = i[customAttrName] && JSON.parse(i[customAttrName])
            if (customAttrObj && customAttrObj.constructor === Object) {
                Object.keys(customAttrObj).forEach(j => {
                    Vue.set(i, j, customAttrObj[j])
                })
            }
            // TODO:test
            // if (i.display === '文本框' || i.label === '文本框') {
            //     // 呈现方案-文字颜色
            //     // Vue.set(i, 'selfClass', 'primaryC')
            //     // 呈现方案-标签
            //     Vue.set(i, 'selfClass', 'warningTag')

            //     // ========================图标使用方案
            //     // 直接传入图标名称值
            //     // Vue.set(i, 'iconProps', 'point-fill')
            //     // 传入图标对象-名称
            //     // Vue.set(i, 'iconProps', { name:'point-fill' })
            //     // 传入图标-名称-危险色
            //     // Vue.set(i, 'iconProps', { name:'point-fill', className: 'dangerC' })
            //     // 传入图标-名称-任意指定色
            //     // Vue.set(i, 'iconProps', { name:'point-fill', color: 'green' })
            // }
        })
    }
    // 返回表单字段数据{Array}
    return fieldList
}


/**
 * 接口字段更新为框架中表单组件对应的字段属性
 * @param {Array} fieldList 表单字段数组
 * @param {Object} dicObj 数据字典对象集合
 */
function suitFormAttr (fieldList, dicObj, params) {
    let resultList = fieldList
    // 定义接口字段与前端组件字段之间的转换规则
    // 以下格式为【接口字段，前端组件字段】
    let commonTransList = [
        // 预输入文本字段【更换】
        ['editTip', 'placeholder'], 
        // 标签字段【更换】
        ['display', 'label'],
        // 字段是否独占一行【更换】
        ['isFullLine', 'isOneLine']
    ]
    resultList.forEach((i, index) => {
        try {
            // 组装指定options下的id、文本展示的字段键
            let { valueFieldIdSrc, valueFieldTextSrc } = i
            let idFieldKey = valueFieldIdSrc
            let textFieldKey = valueFieldTextSrc
            // 更新【通用型】字段为适应前端的字段名称
            commonTransList.forEach(j => {
                if (i.hasOwnProperty(j[0])) {
                    Vue.set(i, j[1], i[j[0]])
                    Vue.delete(i, j[0])
                }
            })
            // 更新【特殊性】字段为适应前端表单组件对应属性
            // ******************************************
            // 判断表单类型是否为整数
            if (i.type === 'int') {
                Vue.set(i, 'type', 'text')
                Vue.set(i, 'dataType', 'int')
            }
            // 判断表单类型是否为数字（整数、小数等等数字组成）
            if (i.type === 'number') {
                Vue.set(i, 'type', 'text')
                Vue.set(i, 'dataType', 'number')
            }
            // 判断表单项类型是否为时间
            if (i.type === 'date') {
                Vue.set(i, 'type', 'date')
                Vue.set(i, 'dateType', i.dateType || 'date')
            }
            // 判断表单项类型是否为时间
            // TODO:2022-04-11:出现大小写两种情况，一起处理掉
            if (i.type === 'datetime' || i.type === 'dateTime' ) {
                Vue.set(i, 'type', 'date')
                Vue.set(i, 'dateType', i.dateType || 'datetime')
            }
            // 判断表单项类型是否为年份
            if (i.type === 'year') {
                Vue.set(i, 'type', 'date')
                Vue.set(i, 'dateType', 'year')
            }
            // 判断表单类型是否为只读
            if (i.type === 'readonly') {
                Vue.set(i, 'type', 'text')
                Vue.set(i, 'disabled', true)
            }
            // 判断表单类型是否为只读不提交
            if (i.type === 'disabled') {
                Vue.set(i, 'type', 'text')
                Vue.set(i, 'disabled', true)
                Vue.set(i, 'destroyed', true)
            }
            // 判断表单类型是否为不提交
            if (i.type === 'destroyed') {
                Vue.set(i, 'type', 'text')
                Vue.set(i, 'destroyed', true)
            }
            // 判断表单类型是否为密码
            if (i.type === 'password') {
                Vue.set(i, 'type', 'text')
                Vue.set(i, 'isPassword', true)
            }
            // 判断表单类型是否为下拉框
            if (i.type === 'select') {
                // do something you like
                idFieldKey && Vue.set(i, 'idFieldKey', idFieldKey)
                textFieldKey && Vue.set(i, 'textFieldKey', textFieldKey)
            }
            // 判断表单类型是否为级联框
            if (i.type === 'cascader') {
                idFieldKey && Vue.set(i, 'idFieldKey', idFieldKey)
                textFieldKey && Vue.set(i, 'textFieldKey', textFieldKey)
            }
            // 判断表单类型是否为单选框
            if (i.type === 'radio') {
                // 设置默认铺满一行
                // Vue.set(i, 'columnPer', i.columnPer || 24)
                idFieldKey && Vue.set(i, 'idFieldKey', idFieldKey)
                textFieldKey && Vue.set(i, 'textFieldKey', textFieldKey)
            }
            // 判断表单类型是否为开关
            // if (i.type === 'switch') {
            //     // 获取数据字典[是与否]
            //     let yes_or_no = dicObj['COMMON_YES_OR_NO']
            //     // 若数据字典不存在则，则修改为文本，逻辑停止
            //     if (!yes_or_no) {
            //         Vue.set(i, 'type', 'text')
            //         return
            //     }
            //     // 将数据字典中对立值更新到对象属性
            //     Vue.set(i, 'activeValue', yes_or_no.find(i => i.text === '是')?.id)
            //     Vue.set(i, 'inactiveValue', yes_or_no.find(i => i.text === '否')?.id)
            // }
            // 判断表单类型是否为复选框
            if (i.type === 'checkboxlist') {
                Vue.set(i, 'type', 'checkbox')
                idFieldKey && Vue.set(i, 'idFieldKey', idFieldKey)
                textFieldKey && Vue.set(i, 'textFieldKey', textFieldKey)
            }
            // 判断表单类型是否为颜色单选/多选
            if (i.type === 'color-single' ) {
                Vue.set(i, 'type', 'colorPicker')
                // 设置单选
                Vue.set(i, 'multiple', false)
            }
            // 设计器配置：多选占比：{ "columnPer": 16 }
            if (i.type === 'color-multiple') {
                Vue.set(i, 'type', 'colorPicker')
                // 设置多选
                Vue.set(i, 'multiple', true)
                // 设置默认铺满一行
                // Vue.set(i, 'columnPer', i.columnPer || 24)
            }
            // 判断表单类型是否为图片单选/多选
            if (i.type === 'imgSingle') {
                Vue.set(i, 'type', 'imagePicker')
                // 设置单选
                Vue.set(i, 'multiple', false)
                // 构造字段deleteAttach，存放删除，图片/附件的都得这么处理
                Vue.set(i, 'deleteAttach', [])
                // 若检验存在，则检验错误提示设置在左侧，排版会好看些
                i.validationRule && Vue.set(i, 'wrongTipSite', 'left')
            }
            // 判断表单类型是否为图片多选
            if (i.type === 'img') {
                Vue.set(i, 'type', 'imagePicker')
                // 设置多选
                Vue.set(i, 'multiple', true)
                // 设置默认铺满一行
                // Vue.set(i, 'columnPer', i.columnPer || 24)
                // 构造字段deleteAttach，存放删除，图片/附件的都得这么处理
                Vue.set(i, 'deleteAttach', [])
                // 若检验存在，则检验错误提示设置在左侧，排版会好看些
                i.validationRule && Vue.set(i, 'wrongTipSite', 'left')
            }
            // 判断表单类型是否为附件单选
            if (i.type === 'attach') {
                Vue.set(i, 'type', 'attach')
                // 设置单选
                Vue.set(i, 'multiple', false)
                // 选择数限制1
                Vue.set(i, 'limit', 1)
                // 构造字段deleteAttach，存放删除，图片/附件的都得这么处理
                Vue.set(i, 'deleteAttach', [])
                // 若检验存在，则检验错误提示设置在左侧，排版会好看些
                i.validationRule && Vue.set(i, 'wrongTipSite', 'left')
            }
            // 判断表单类型是否为附件多选
            if (i.type === 'attachMulti') {
                Vue.set(i, 'type', 'attach')
                // 设置多选
                Vue.set(i, 'multiple', true)
                // 构造字段deleteAttach，存放删除，图片/附件的都得这么处理
                Vue.set(i, 'deleteAttach', [])
                // 若检验存在，则检验错误提示设置在左侧，排版会好看些
                i.validationRule && Vue.set(i, 'wrongTipSite', 'left')
            }
            // 判断表单类型是否为附件多选-表格形式
            // if (i.type === 'attachList') {
            //     i.type = 'attach'
            //     i.showType = 'list'
            //     // 设置多选
            //     i.multiple = true
            //     // 设置默认铺满一行
            //     i.columnPer = i.columnPer || 24
            //     // 构造字段deleteAttach，存放删除，图片/附件的都得这么处理
            //     i.deleteAttach = []
            // }

            // 弹出框列表单选
            if (i.type === 'selectListData') {
                i.type = 'dialogList'
                Vue.set(i, 'multiple', false)
            }
            // 弹出框列表多选
            if (i.type === 'selectListDataMulti') {
                i.type = 'dialogList'
                Vue.set(i, 'multiple', true)
            }
            
            // 判断表单类型是否为地址本
            if (i.type === 'addressbook') {
                Vue.set(i, 'type', 'addressBook')
            }
            // 处理拥有【起始-->终止】关系的字段，例如时间段
            StartEndSet.initForm(i, resultList)
            // 设置字段校验规则
            Vue.set(i, 'rules', i.validationRule)
        } catch (e) {
            console.error(`接口表单字段与表单组件字段转化异常：${e}`)
        }
    })
    return resultList
}
/**
* 将data:[{value: '${getFirst}'}]中的value字段配的函数转成真正代表值
* @param {Array} data 字段数组
*/
function transferRealValue (value, field) {
    // 过滤出i.value为字符串类型的
    if (!(value && typeof value === 'string')) return value
    // 判断字符串中是否含有`${xxx}`这样格式的数据
    let _matchArr = value.match(/\$\{.*?}/g)
    if (!_matchArr) return value
    _matchArr.forEach(j => {
        // j的数据格式为'${xxx}'
        // 通过正则获取${xxx}中的值xxx
        let _key = j.replace(/\$\{(.*)}/, '$1')
        // 根据关键字，处理对应逻辑
        switch (_key) {
            // 当键为getFirst时，表示获取字段属性下options的第一条数据的id值，完整替换
            case 'getFirst':
                let _firstValue = field.options?.[0]?.[GlobalConst.dicOption.idName] || null
                value = value.replace(j, _firstValue)
                break
            // 设置选中最后一项值
            case 'getLast':
                let _lastValue = field.options?.[field.options.length - 1]?.[GlobalConst.dicOption.idName] || null
                value = value.replace(j, _lastValue)
                break
            // 设置当前日期为默认值
            case 'currentDate':
                let _cDate = Get_Today()
                value = value.replace(j, _cDate)
                break
            // 设置当前年份为默认值
            case 'currentYear':
                value = value.replace(j, Get_Year())
                break
            // 设置上一年年份为默认值
            case 'lastYear':
                value = value.replace(j, Get_Year() - 1)
                break
            // 设置下一年年份为默认值
            case 'nextYear':
                value = value.replace(j, Get_Year() + 1)
                break
            default:
                // do something
        }
    })
    return value
}

// 针对特殊字段对值再做进一步处理
export function UpdateFieldValue (fieldList, isView, detailId) {
    return new Promise((resolve, reject) => {
        // 更新表单字段的value值 
        if (!(fieldList && fieldList.length > 0)) resolve([])
        // 定义承诺列表
        let promiseList = []
        // 目前只有多图类型与多附件类型需要请求列表数据接口，找出并添加进承诺列表
        fieldList.forEach((i) => {
            switch (i.type) {
                case 'imagePicker':
                    if (i.multiple) {
                        promiseList.push(FileSet.editForm.getList(i, detailId))
                    } else {
                        promiseList.push(null)
                    }   
                    break
                case 'attach':
                    if (i.multiple) {
                        promiseList.push(FileSet.editForm.getList(i, detailId))
                    } else {
                        promiseList.push(null)
                    }
                    break
                default:
                    promiseList.push(null)
            }
        })
        Promise.all(promiseList).then(res => {
            fieldList.forEach((i, index) => {
                // 若接口-字段对象中含有该字段属性，则需要将其值更新给value字段值
                let defaultValueName = 'defaultValue'
                // 判断设计器是否设置默认值，更新字段value值，若字段对象含有值，则权重大于默认值
                let _val = null
                if (Has_Value(i.value)) {
                    // 如果有值，优先使用值
                    _val = i.value
                } else if (Has_Value(i[defaultValueName])) {
                    // 无值，则判断是否有默认值，有则使用默认值
                    // 模型设计器：字段默认值允许使用函数，这里判断是否存在函数，将函数转为真正对应的值
                    _val = transferRealValue(i[defaultValueName], i)
                    // 默认值可能为函数，所以也需要将函数值更新得默认值字段
                    i[defaultValueName] = _val
                }
                Vue.set(i, 'value', _val)
                // 数据格式关卡，表单类型为整数或者小数类型的数据确保转为number，避免表单最终校验失败
                if (i.dataType === 'int' || i.dataType === 'number') {
                    // 获取转化为number的值
                    let _value = parseFloat(i.value)
                    // 判断是否有值的情况，比如0为有值
                    i.value =  Has_Value(_value)? _value : null
                }
                switch(i.type) {
                    case 'imagePicker': // 图片
                        if (!i.multiple) {
                            // 单图
                            Vue.set(i, 'value', FileSet.editForm.getSingleImg(i))
                        } else {
                            // 多图片
                            Vue.set(i, 'value', res[index] || [])
                        }
                        break
                    case 'attach': // 附件
                        if (!i.multiple) {
                            // 单附件               
                            let _value = FileSet.editForm.transferSingleAttach(i.value)
                            Vue.set(i, 'value', _value ? [_value] : [])
                        } else {
                            // 多附件
                            Vue.set(i, 'value', res[index] || [])
                        }
                        break
                    case 'switch': // 开关
                        // TODO: 两种情况
                        //     1. switch下值可能为字符串，'1','2'对应是否
                        //     2. 更多为Boolean，true，false对应是否
                        //     情况1下数字需要转成字符串，情况2下数字需要转成boolean
                        //     这里先统一处理成Boolean类型，情况1的场景目前还比较少你，等后面出现了再确认需求进行
                        Vue.set(i, 'value', Get_Switch_Val(i.value))
                        break
                    default:
                        // 判断如果是text文本，在查看模式时，若有值且有配置前置或后置，则展示时需要加上
                        if (Has_Value(i.value) &&
                            isView &&
                            !('type' in i) &&
                            (i.prepend || i.append)) {
                            Vue.set(i, 'value', `${i.prepend || ''}${i.value}${i.append || ''}`)
                        }
                        // do something you like
                }
                // 将详情接口数据更新对应字段值
                    // 这里处理个特殊逻辑：与后台约定框架的数据字典值都为字符串
                    // 回显的表单字段值受数据库字段格式影响可能为int，此情况会导致匹配不上数据字典，回显不了
                    // 此处需要判断，若字段含有options（代表使用数据字典)则将值转化为字符串，对应数据字典
                if (i.options &&
                    i.options.length > 0 &&
                    i.value !== null) {
                    // 字段使用数据字典且当前回显有值，则更新值为字符串
                    Vue.set(i, 'value', String(i.value))
                }
                // 两个字段组成起始-终止段展示（类似时间段），需要将值合并更新给初始字段用于展示
                StartEndSet.editForm(i, fieldList)
            })
            resolve(fieldList)
        })
    })
}

/**
 * 将由fromSign与toSign包裹着的字符串转成obj中对应属性变量
 *     【value】:  '这是一串数据${data}'
 *     【obj】:   {data:11}
 *     【fromSign】:  '${'
 *     【toSign】:  '}'
 *     最后会返回'这是一串数据11'
 * @param {Object} obj 含有字符串变量对应的值的对象
 * @param {String} val 需要转化的数据
 * @param {String} fromSign 转化起始标识符
 * @param {String} toSign 转化结束标识符
 */
function transfer_template_to_variable (obj, val, fromSign, toSign) {
    if (!val) return false
    // 获取转化起始标识符下角标
    let from_index = val.indexOf(fromSign)
    // 若存在转化标识符，进行下面处理
    if (from_index !== -1) {
        // 获取起始标识符前面的内容，保留
        var result = val.substring(0, from_index)
        // 使用起始标识符后面的内容 更新 val,用于继续识别
        val = val.substring(from_index + fromSign.length, val.length)
        // 在val中找出结束标识符
        let to_index = val.indexOf(toSign)
        // 获取到变量字符串
        let field = val.substring(0, to_index)
        // 将变量字符串转成对应变量值，更新val
        val = result + obj[field] || field + val.substring(to_index + to.length, val.length)
        // 前面的步骤完成去除标识符 + 转化变量这一步，继续递归，转化下一组标识符
        return transfer_template_to_variable (obj, val, fromSign, toSign)
    } else {
        // 不存在则直接返回
        return val
    }
}

/**
 * 判断字段是否展示页面
 * @param {field} Object 字段对象
 */
function isFieldShow (field) {
    // 定义标识是否展示的字段属性名称
    let showAttr = 'isShowInPage'
    // 判断字段对象中含有该字段且值为1时进行展示，返回true
    if (field.hasOwnProperty(showAttr) &&
        field[showAttr] === 1) {
        return true
    }
    return false
}
/**
 * 更改字段属性
 * @param {Array} _fieldList 字段数组
 */
function changeFieldAttr (_fieldList) {
    // 定义字段type类型
    let defaultType = 'text' // 默认type
    let hiddenType = 'hidden' // 隐藏type
    _fieldList.forEach(i => {
        if (!isFieldShow(i)) {
            // 字段不展示，设置其为隐藏
            Vue.set(i, 'type', hiddenType)
        } else {
            let selfType = i.viewType // 自身viewType
            if (selfType) {
                switch (selfType) {
                    case 'dateTime': // 时间
                    case 'date': // 日期
                        Vue.set(i, 'type', 'date')
                        break
                    case 'dic': // 数据字典
                        // 所有的数据字典在后台完成转化后返回文本值，在src\service\module.js中已完成options的组装
                        Vue.set(i, 'type', 'select')
                        break
                    // 颜色选择
                    case 'colorPicker':  
                        Vue.set(i, 'type', 'colorPicker')
                        break
                    default:
                        Vue.set(i, 'type', selfType || defaultType)
                        // do something
                }
            }
        }
        
    })
    return _fieldList
}