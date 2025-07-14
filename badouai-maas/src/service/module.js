import { Merge_List } from '@/utils/list'
import { Has_Value, Null_Judge, Remove_Space } from '@/utils'
import { request } from '@/service/request'
import GlobalConst from '@/service/global-const'
import Vue from 'vue'
import { Download } from '@/utils/file'
import { Export_Url, Valid_Unique } from '@/api/frame/common'
import Validate_Rules from '@/service/validate-rules'
import { Get_Full_Url } from './url'
import { Is_Object } from '@/utils/data-type'

/**
 * 获取模型的专属样式类名(基于模型编码)
 * @param {String} mdCode 模型编码
 * @param {Number} type 模型类型 1-模型列表/2-模型表单
 * @param {Boolean} isView 是否查看模式（type为2：表单时使用）
 * @returns {String} 专属样式类名 'a,b'
 */
function Get_Module_ClassName (mdCode, type, isView = false) {
    if (!(mdCode && type)) return []
    // 定义结果值
    let _result = ''
    switch (type) {
        // 模型列表/树型列表
        case 1:
            _result = `${mdCode}_module_list`
            break
        // 模型表单
        case 2:
            // 定义获取表单状态
            let _formState = !isView ? 'edit' : 'view'
            _result = `${mdCode}_module_form  ${mdCode}_module_${_formState}`
            break
        default:
            // do something you like
    }
    return _result
}

/**
 * 校验唯一的函数
 * @param {Object} rule 规则
 * @param {String} value 值
 * @param {Function} callback 检验结果的回调函数
 * @param {Object} fieldItem 字段对象
 */
function Value_Unique_Fun (rule, value, callback, fieldItem) {
    // this: 指向mForm/index.vue
    let that = this
    if (!Has_Value(value)) {
        callback()
    } else {
        // ！！！注意！！！
        // 在实践过程中，校验触发的时机为change与blur时会导致校验函数多次触发，暂时不管，对请求类的校验可能会有多次请求的发出
        // ！！！！！！！
        let _params = {
            ...(that.formRuleParams || {}),
            key: rule?.field,
            value: value
        }
        Valid_Unique(_params).then(res => {
            if (!res) {
                callback()
            }
            // 存储初始值。接下来开始处理数据类型的值提醒时转化为文本
            let initvValue = value
            // 该场景一般值为字符串
            if (value && typeof value === 'string') {
                // 获取文本赋值给的字段：valueFieldText
                // 获取选项：options
                // 获取选项中的id键：valueFieldIdSrc
                // 获取选项中的text键：valueFieldTextSrc
                let { valueFieldText, options, valueFieldIdSrc, valueFieldTextSrc } = fieldItem
                // 如果存在获取文本赋值给的字段，则直接取就行
                if (valueFieldText) {
                    value = fieldItem[valueFieldText]
                } else if (options && options.length > 0) {
                    // 检查options是否存在值，存在则表示需要处理为文本
                    let _idName = valueFieldIdSrc || GlobalConst.dicOption.idName
                    let _textName = valueFieldTextSrc || GlobalConst.dicOption.textName
                    value =
                        value
                            .split(GlobalConst.separator) // 值可能选中多个，所以先拆分
                            .map(i => options.find(j => j[_idName] === i)?.[_textName]) // 各自匹配找回文本
                            .filter(i => i) // 过滤掉空的
                            .join(GlobalConst.separator) // 重新组合文本数据
                }
            }
            // 使用最初值，避免中间的处理导致值变成空了
            callback(new Error(`【${value || initvValue}】已存在`))
        }).catch(err => {
            console.log(`唯一校验失败`)
        })
    }
}

/**
 * 获取字段校验规则rules
 * @param {Object} fieldItem 字段对象
 * @param {String} ruleWord 校验规则关键词字符串
 * @returns
 */
function Get_Validation_Rule (fieldItem, ruleWord) {
    // this: 指向mForm/index.vue
    let that = this
    // TODO：检验唯一，需要添加防抖函数，避免触发归于频繁
    let validateUnique = function (rule, value, callback) {
        Value_Unique_Fun.call(that, rule, value, callback, fieldItem)
    }
    // 校验整数函数
    let validateInteger = function (rule, value, callback) {
        if (!Has_Value(value)) {
            // 不做必填校验
            callback()
        }
        // 若输入为整数，则取整前后一致，以此作为判断
        if (parseInt(value) + '' === value + '') {
            callback()
        } else {
            callback(new Error('请输入整数'))
        }
    }
    // 校验小数函数
    let validateFloat = function (rule, value, callback) {
        if (!value) {
            // 不做必填校验
            callback()
        }
        // 若输入为小数，则取数字部分时前后一致，以此作为判断
        if (parseFloat(value) + '' === value + '') {
            callback()
        } else {
            callback(new Error('请输入数字'))
        }
    }
    // 校验保留n位小数
    let validateDecimalN = function (rule, value, callback, num = 0) {
        if (!value) {
            // 不做必填校验
            callback()
        }
        let str_value  = value + ''
        // 若输入为小数，则取数字部分时前后一致，以此作为判断
        if (parseFloat(value) + '' === str_value) {
            // 判断是否有小数点
            if (str_value.indexOf('.') !== -1) {
                // 如果小数点的位数大于校验的位置，提示报错
                let decimal_num = str_value.split('.')[1].length
                if (decimal_num > parseFloat(num)) {
                    callback(new Error(`小数后最多保留${num}位`))
                } else {
                    callback()
                }
            } else {
                callback()
            }
        } else {
            callback(new Error('请输入小数'))
        }
    }
    // "validationRule": "required,email"
    // 获取校验规则关键词列表
    let KeyRules = []
    // 判断是否存在检验规则
    if (ruleWord) {
        // 定义获取默认分隔符
        let separator = GlobalConst.form.valueSeparator
        // 判断检验规则中是否含有【;】，有则更新分隔符为【;】,处理旧版数据
        if (~ruleWord.indexOf(';')) {
            separator = ';'
        }
        // 将校验规则按照分隔符切割为数组,filter过滤掉空数据
        KeyRules = ruleWord.split(separator).filter(j => j)
    }
    let rules = []
    KeyRules.forEach(j => {
        let _ruleField = null
        switch (j) {
            case 'unique':
                _ruleField = { validator: validateUnique, trigger: ['blur', 'change'] }
                break
            case 'required':
                _ruleField = { required: true, message: `${fieldItem.label}为必填项`, trigger: ['blur', 'change'] }
                // 字段的值数据格式为数组array，使用required时必须显示传入type:'array',组件才能验证是否有值
                // 目前已知的场景，字段值为数组的情况包含以下
                //     1. 图片类型
                //     2. 附件类型
                if (fieldItem.type === 'imagePicker' || fieldItem.type === 'attach') {
                    _ruleField.type = 'array'
                }
                break
            case 'numeric':
                _ruleField = { validator: validateInteger, trigger: ['blur', 'change'] }
                break
            case 'decimal':
                _ruleField = { validator: validateFloat, trigger: ['blur', 'change'] }
                break
            case 'email':
                _ruleField = { type: 'email', message: '格式必须为邮箱', trigger: ['blur', 'change'] }
                break
            case '手机号码':
            case 'mobileNum':
                _ruleField = { validator: Validate_Rules.Check_Mobile_Num, trigger: ['change', 'blur']}
                break
            case '座机号码':
            case 'telephoneNum':
                _ruleField = { validator: Validate_Rules.Check_Telephone_Num, trigger: ['change', 'blur']}
                break
            case '电话号码':
            case 'phoneNum':
                _ruleField = { validator: Validate_Rules.Check_Phone_Num, trigger: ['change', 'blur']}
                break
            case '身份证':
            case 'IDCard':
                _ruleField = { validator: Validate_Rules.Check_IDCard, trigger: ['change', 'blur']}
                break
            default:
                // 说明：default用于默认处理逻辑，由于小数点后保留n位无法case匹配，所以放置此处
                // 小数点后保留n位
                if (j.indexOf('decimal=') !== -1) {
                    let num = j.split('=')[1]
                    _ruleField = { validator: (rule, value, callback) => validateDecimalN(rule, value, callback, num), trigger: ['blur', 'change'] }
                }
        }
        // 2022-07-05:不能添加name:j,会导致最终是校验规则多了属性name，会导致匹配失败一直提示
        _ruleField && rules.push(_ruleField)
    })
    return rules
}

/**
 * 合并按钮，返回新的按钮数组
 * @param {Array} defaultList
 * @param {Array} newList
 * @param {String} sign
 * @return {Array} 合并之后的按钮数组
 */
function Merge_Btn (defaultList, newList, sign = 'id') {
    // 如果动态js没有配置按钮，则直接使用默认按钮
    if (!newList) {
        return defaultList || []
    }
    if (newList && newList.constructor === Array) {
        // 如果动态js返回空数组，则表示清空按钮
        if (newList.length === 0) {
            return []
        }
        // 以下情况为动态js返回按钮数组
        return Merge_List(defaultList, newList, sign, { isContentMerge: true })
    }
    // 传入的自定义按钮newList格式不对，直接返回默认按钮
    return defaultList
}

/**
 * 根据对象的属性值返回是否展示页面的状态
 * @param obj 对象
 */
function ShowStatus (obj) {
    if (!obj) return false
    let signList = ['isHide', 'hidden', 'isHidden']
    let len = signList.length
    for (let i = 0; i < len; i++) {
        let sign = signList[i]
        if (obj.hasOwnProperty(sign)) {
            if (obj[sign]) {
                // 同时支持函数
                if (typeof obj[sign] === 'function') {
                    return !obj[sign]()
                }
                return false
            }
        }
    }
    return true
}

/**
 * 按钮事件触发
 * @param {Object} btnObj 按钮对象
 */
function HandleClick (btnObj) {
    // 获取事件
    let { click: handleClick, functionName } = btnObj
    if (functionName) {
        if (typeof this[functionName] !== 'function') {
            this.$message.error(`找不到${functionName}方法，请检查按钮配置！`)
        } else {
            this[functionName](btnObj)
        }
        return
    }
    if (handleClick && typeof handleClick === 'function') {
        // 执行函数，传递作用域
        handleClick.call(this, btnObj)
    }
}
/**
 * 错误转化函数
 * @param {*} formData
 * @param {*} valuePath
 */
function FormatWrong (formData, valuePath) {
    if (!(formData && formData.constructor === Object)) return {}
    // 处理后台返回的数据格式异常问题 --start
    // 1. 接口返回value的可能值为字符串的undefined与null,需要优先转化格式
    // 2. 下面的表单逻辑存在一个字段获取其他字段值的情况，所以需要提前转好，不能将此段逻辑放在与字段同步的操作中
    try {
        Object.keys(formData).forEach(i => {
            let _value = ''
            if (valuePath) {
                _value = formData[i][valuePath]
                if (_value === 'undefined') formData[i][valuePath] = undefined
                if (_value === 'null') formData[i][valuePath] = null
            } else {
                _value = formData[i]
                if (_value === 'undefined') formData[i] = undefined
                if (_value === 'null') formData[i] = null
            }
        })
    } catch (e) {
        console.error(`函数FormatWrong转化异常：${e}`)
        return formData
    }
    // 处理后台返回的数据格式异常问题 --end
    return formData
}

/**
 * 使用表单详情值更新表单字段列表对象下的值
 * @param {Array} fieldList 表单字段列表数组
 * @param {Object} formData 表单值详情对象
 * @param {Object} option 配置项,目前可支持属性为以下
 *      @param {String} valuePath // 表单详情值对象值下每个字段值下属性名
 *      @param {Boolean} isView // 是否为查看状态
 */
function Update_FieldList_Value (fieldList, formData, option = {}) {
    // 传入字段数组数据格式不符时，直接返回空数组
    if (!(fieldList && fieldList.constructor === Array)) return []
    // formData没值时，那就不用更新了，直接返回当前字段列表即可
    if (!(formData && formData.constructor === Object)) return fieldList
    // 设置默认配置项
    let {
        valuePath = GlobalConst.form.valuePath, // 表单详情值对象值下每个字段值下属性名
        isView = false, // 是否为查看状态
    } = option
    // 转化错误数据：将【字符串类型的null与undefined】转成【null与undefined】
    formData = FormatWrong(formData, valuePath)
    /**
     * 根据传入字段获取对应值
     * @param {String} name 字段名
     */
    function getValue (name) {
        if (!name) return null
        // 获取字段值数据
        let _valueInfo = formData[name]
        if (!Has_Value(_valueInfo)) return null
        // 获取字段值
        let _value = valuePath ? (_valueInfo?.[valuePath]) : _valueInfo
        return _value
    }
    // 下面情况为：fieldList 与 formData均有值的情况
    //     fieldList: 表单字段数组，单个字段对象下不含值 [{name:'sex',label:'性别'}]
    //     formData: 表单字段值对象 {sex:'0'}
    let customAttrName = 'customOptions'
    fieldList.forEach(i => {
        let customAttrObj = i[customAttrName] && JSON.parse(i[customAttrName])
        if (customAttrObj && customAttrObj.constructor === Object) {
            Object.keys(customAttrObj).forEach(j => {
                Vue.set(i, j, customAttrObj[j])
            })
        }
        // 获取字段值
        let _value = getValue(i.name)
        if (!isView) {
            // 获取冗余字段值相关字段
            let { valueFieldId, valueFieldText } = i
            // 是否需要添加冗余字段相关关系
            if (valueFieldId || valueFieldText) {
                // 冗余字段相关关系
                i.extraRelations = Get_Extra_Field_Relation(i)
                // 冗余字段赋值
                let extraValueObj = {}
                Object.keys(i.extraRelations).forEach(key => {
                    extraValueObj[key] = getValue(key)
                })
                i.extraValueObj = extraValueObj
            }
            // 注：使用多冗余字段的方式，以下代码不确定有没有用，暂时注释掉
            // 判断设计器是否配置了key冗余字段，有则更新给字段对象
            // valueFieldId && (i[valueFieldId] = getValue(valueFieldId))
            // // 判断设计器是否配置了value冗余字段，有则更新给字段对象
            // valueFieldText && (i[valueFieldText] = getValue(valueFieldText))
            // 编辑状态：
            switch (i.type) {
                // 弹出框选择列表数据
                case 'selectListData':
                case 'selectListDataMulti':
                    // 弹出框在回显的时候需要通过desc获取文本，才能展示回显值
                    Vue.set(i, 'valueText', getValue(`${i.name}Desc`))
                    i.value = Has_Value(_value) ? _value : null
                    break
                default:
                    // 其余的表单类型处理，默认赋值
                    // 2022-06-27: 只处理值中有的字段，避免更改到部分有值的追加字段
                    if (i.name in formData) {
                        i.value = Has_Value(_value) ? _value : null
                    }
            }
        } else {
            // 查看状态
            switch (i.viewType) {
                case 'dic':
                    i.value = Null_Judge(_value, null)
                    i.valueText = getValue(`${i.name}Desc`) || null
                    // 查看页组装数据字典类型的下拉条件（无需再请求接口）
                    i.options = [{
                        [GlobalConst.dicOption.idName]: i.value,
                        [GlobalConst.dicOption.textName]: i.valueText
                    }]
                    break
                default:
                    i.value = Null_Judge(_value, null)
            }
        }
    })
    // 这里只做更新字段数组下值的操作，值的相关特殊逻辑不在这里处理
    return fieldList
}

/**
 * 获取搜索配置数据（核心在于获取下拉options数据）
 * @param {Array} baseFieldList:基础字段数据
 * @param {Array} fieldList:基础字段数据（指定展示形式）
 */
function GetSearchFieldList (baseFieldList, fieldList) {
    // 定义数组结果列表
    let resultList = []
    return new Promise((resolve, reject) => {
        if (baseFieldList &&
            baseFieldList.constructor === Array &&
            baseFieldList.length > 0) {
            // 定义承诺列表
            let promiseList = []
            baseFieldList.forEach(i => {
                // 合并对象数据，生成新对象
                // 2022-01-19：找到匹配数据一般使用id即可，这里再使用name主要用于处理出现重复id的情况，避免匹配的一直是重复的第一个id
                let aimField = fieldList.find(j => j.id === i.id && j.name === i.name) || {}
                // 自动分页
                let customOptions = i.customOptions ? JSON.parse(i.customOptions) : {}
                let item = {...i, ...aimField, ...customOptions}
                // 不纠结option多种数据来源，组件只设置option字段，并且只接收列表数据
                // 初始是接口地址的需要先请求获取数据后再传入，这样即使值采用函数方式（例如默认取第一个）表示的也能第一时间获取
                // 整体search中，需要优先请求所有带url的option（注意是异步请求），获取数据后再进行渲染
                promiseList.push(setOption(item))
            })
            // 所有option使用接口地址的进行异步请求数据，并返回生成最新searchbar数据
            Promise.all(promiseList).then(res => {
                res.forEach(i => {
                    // 定义并赋值变量
                    let { option, obj } = i
                    // 删除对象下多余属性 isExtended, url, data, selectType，生成新对象itemObj
                    let { isExtended, data, selectType, ...itemObj } = {
                        // 枚举所有属性，后面重复出现的属性将覆盖这里，放置后面的优先级高
                        ...obj,
                        // 字段名
                        id: obj.id,
                        // 可扩展的,决定展示的位置（是否作为右侧更多中的选项状态，这一状态不会受后续逻辑影响，放在更多中就是更多中，无关选中与否）
                        isExtend: !!obj.isExtended,
                        // 已选中展示的；扩展项默认初始不选中
                        isSelect: !obj.isExtended,
                        // 字段文本名称
                        name: obj.name,
                        // 搜索类型
                        type: obj.selectType,
                        // 初始值,重置时将value字段值变更为该字段值
                        defaultValue: Null_Judge(obj.defaultValue, null),
                        // 值
                        value: Null_Judge(setValue(option, obj.value) || obj.defaultValue, null),
                        // searchbar中展示排序字段
                        sortIndex: obj.sortIndex,
                        // 是否分页加载
                        autoPagination: obj.autoPagination,
                        // 数据源url
                        url: obj.url,
                    }
                    { `避免ESLint报检查错误定义变量没使用,此处使用此变量：` + isExtended + data + selectType }
                    // option数据源
                    if (option) {
                        itemObj['option'] = option
                    }
                    // 处理日期类型的搜索，是否配置了可选时间不超过当前
                    if (obj.limitToNow) {
                        itemObj.pickerOptions = {
                            disabledDate (time) {
                                return time.getTime() > Date.now()
                            }
                        }
                    }
                    // 添加搜索条件子项
                    resultList.push(itemObj)
                })
                // 返回结果集
                resolve(resultList)
            })
        } else {
            resolve(resultList)
        }
    })
}
/**
 * 设置搜索组件的option值（根据url或者options数据）
 * @param {Object} itemObj：搜索项对象数据
 */
function setOption (itemObj) {
    let {
        url, // 获取字段项url
        data: options,  // 获取字段项中的数据源
        autoPagination
    } = itemObj
    return new Promise((resolve, reject) => {
        // 情况1. 当前字段使用的是提供的options数据源
        if (options &&
            options.constructor === Array &&
            options.length > 0) {
            resolve({
                option: addOptionStatus(options) || [],
                obj: itemObj
            })
        // 情况2. 当前字段选择开启自动分页
        } else if (autoPagination) {
            resolve({
                obj: itemObj
            })
        // 情况3. 当前字段使用的是提供的数据源的url，需要请求获取
        } else if (url) {
            request({
                url: url,
                method: 'post',
                params: {}
            }).then(res => {
                // 判断是否存在options值路径，存在则更新获取最终的options值
                if (itemObj.optionResPath && res) {
                    res = res[itemObj.optionResPath]
                }
                resolve({
                    option: addOptionStatus(res || []),
                    obj: itemObj
                })
            }).catch(err => {
                resolve({
                    option: [],
                    obj: itemObj
                })
            })
        // 情况4： 啥也不是，直接返回并且不需要option字段
        } else {
            resolve({
                obj: itemObj
            })
        }
    })
}

/**
 * 为数组数据中的对象添加状态位，后续用于状态标记
 */
function addOptionStatus (list, status = 'isSelect') {
    if (!(list && list.constructor === Array)) return []
    // 浅拷数组
    let _list = [...list]
    _list.forEach(i => {
        i[status] = false
    })
    return _list
}

/**
 * 设置搜索组件项的值（根据value是否使用函数）
 * @params [Array] option:数据列表项
 * @params [String] method:函数名或者值
 */
function setValue (option, value) {
    let _value = ''
    switch (value) {
        case 'getFirst':
            _value = option?.[0].id || ''
            break
        case 'getLast':
            _value = (option.length > 0 && option[option.length - 1].id) || ''
            break
        case 'getCurrentYear':
            _value = new Date().getFullYear().toString()
            break
        case 'getLastYear':
            _value = (new Date().getFullYear() - 1).toString()
            break
        default:
            _value = value || ''
    }
    return _value
}

/**
 * 将页面路径携带的参数-转化为-列表接口需要使用的参数
 * xxx?aa=22  ==>   defaultSearchParam:[{name:'aa',value:22,type: GlobalConst.searchBar.type}],用于列表请求参数
 * @param {Object} queryObj 路径请求参数
 * 框架列表接口会接收两个参数，注意这里区别
 *      1.searchParam：搜索栏专用，用于过滤数据，可不传
 *      2.defaultSearchParam：业务请求默认请求参数（来源于页面路径携带的参数，若有值则列表请求必须携带该参数）
 */
function Get_Default_SearchParam (queryObj) {
    if (!queryObj) return
    // 获取参数数组
    let queryParamsList = Object.keys(queryObj)
    // 不存在参数，直接返回
    if (queryParamsList.length === 0) return
    // 定义列表默认请求参数
    let defaultSearchParam = []
    // 定义搜索栏组件的默认请求参数
    let searchParam = {}
    // 遍历对象，转化为列表接口需要请求的参数
    queryParamsList.forEach(i => {
        // 这里处理两种类型：
        // 第一种是明确的搜索栏参数：searchParam
        // 第二种是其余类型的参数，都处理为：defaultSearchParam
        if (i === 'searchParam') {
            // 从这里逻辑可知，只接收一个searchParam,注意传入值不要分散写
            try {
                // 进行数据格式转化将地址栏的【字符串对象】转成【对象】，由于可能转失败所以使用try/catch
                searchParam = queryObj[i] && JSON.parse(queryObj[i])
                // searchParam = {'text':'44'}
            } catch (e) {
                console.error(`搜索参数JSON.parse失败，传入转化值为：${queryObj[i]}`)
            }
        } else {
            defaultSearchParam.push({ name: i, value: queryObj[i], type: GlobalConst.searchBar.type })
        }
    })
    return {
        // 用于模型列表页的搜索栏组件默认值
        searchParam: searchParam,
        // 用于模型列表页固定的请求列表参数
        defaultSearchParam: JSON.stringify(defaultSearchParam)
    }
}

/**
 * 获取导入/导出的相关配置
 * @param {Number} type 类型  1-导入 2-导出
 * @returns {Object} 配置对象数据
 */
function Get_Import_And_Export_Prop (type, setting) {
    // 定义导入类型
    let _importKey = 1
    // 定义导出类型
    let _exportKey = 2
    // 传入类型异常，直接返回
    if (type !== _importKey && type !== _exportKey) return {}
    // 判断当前传入是否为导入类型
    let _isImport = type === _importKey
    // 根据类型设置关键字段键与文本
    let _current = {
        operateName: _isImport ? '导入' : '导出',
        key: _isImport ? 'import' : 'export'
    }
    // 定义返回的结果集
    let _result = {}
    // 定义自定义的配置项（对象）
    let diy_props = this[`diy_${_current.key}Props`]
    // 定义url键
    let _urlKey = 'url'
    // 定义标题键
    let _titleKey = 'title'
    // 定义模版url(目前面向导入)
    let _templateUrlKey = 'templateUrl'
    // 定义参数(目前面向导入)
    let _keyKey = 'key'
    // bd规则:多属性优先级 高于 单属性
    if (diy_props) {
        switch (diy_props.constructor) {
            case Object:
                _result = diy_props
                break
            case Function:
                let _res = diy_props.call(this, this.mdCode)
                _result = _res && _res.constructor === Object && _res || {}
                break
            default:
                // do something
        }
    }
    // 以传入的setting优先级为最高，优先级排序
    //     1. 传入setting对象配置
    //     2. 模型js中独立配置的diy_props属性集合
    //     3. 模型js中独立配置的单独属性
    setting && (_result = Object.assign(setting, _result))
    /**
     * 获取域下的变量
     * @param {Object} scope 域
     * @param {String} name 域中的变量名
     * @returns {*}
     */
    function getVariable (scope, name) {
        if (!scope) return
        if (name in scope) {
            // 返回域中指定键名变量
            return scope[name]
        }
    }
    // 定义获取url值
    let diy_url = _result[_urlKey] || getVariable(this, `diy_${_current.key}Url`)
    // 定义获取标题值
    let diy_title = _result[_titleKey] || getVariable(this, `diy_${_current.key}Title`)
    // 定义模版url(目前面向导入)
    let diy_templateUrl = _result[_templateUrlKey] || getVariable(this, `diy_${_current.key}TemplateUrl`)
    // 定义参数(目前面向导入)
    let diy_key = _result[_keyKey] || getVariable(this, `diy_${_current.key}Key`)
    // 处理获取接口地址
    if (diy_url) {
        let _url = null
        switch (diy_url.constructor) {
            case String:
                _url = diy_url
                break
            case Function:
                _url = diy_url.call(this, this.mdCode)
                break
            default:
                // do something
        }
        // 更新url属性
        _result[_urlKey] = _url
    }
    // 定义获取标题值
    if (diy_title) {
        let _title = null
        switch (diy_title.constructor) {
            case String:
                _title = diy_title
                break
            case Function:
                _title = diy_title.call(this, this.name)
                break
            default:
                // do something
        }
        // 更新title属性
        _result[_titleKey] = _title
    }
    // 定义模版(导入专用)
    if (diy_templateUrl) {
        let _templateUrl = null
        switch (diy_templateUrl.constructor) {
            case String:
                _templateUrl = diy_templateUrl
                break
            case Function:
                _templateUrl = diy_templateUrl.call(this, this.mdCode)
                break
            default:
                // do something
        }
        // 更新templateUrl属性
        _result[_templateUrlKey] = _templateUrl
    }
    // 定义参数(导入专用)
    if (diy_key) {
        let _key = null
        switch (diy_key.constructor) {
            case Object:
                _key = diy_key
                break
            case Function:
                _key = diy_key.call(this, this.mdCode)
                break
            default:
                // do something
        }
        // 更新key参数属性
        _result[_keyKey] = _key
    }
    // 判断标题是否存在，不存在则使用默认的标题
    if (!_result[_titleKey]) {
        _result[_titleKey] = `${_current.operateName}${this.name}数据`
    }
    // 返回结果值
    return _result
}

/**
 * 导出数据
 * @param {Object} btnObj 按钮对象
 * @param {Object} setting 配置项
 *      @param {Function, String} url 自定义导入接口
 *      ...其他的配置项
 */
function ExportData (btnObj, setting) {
    // 定义列表页面作用域
    // let that = this
    // 获取选中列表数据
    let selection = this.getSelection()
    // 定义表单标识
    let formId = 'exportForm'
    // 定义请求参数
    let params = {}
    // 定义表单项值：Array格式
    let valueList = []
    // 定义表单项数据源： Array格式
    let options = []
    // 需要处理的字段名
    let dealFieldName = 'exportFields'
    // 更新参数params
    if (selection && selection.length > 0) {
        params.idslist = selection.map(i => i.id).join(',')
    }
    if (this.defaultParamsObj?.defaultSearchParam) {
        params.defaultSearchParam = this.defaultParamsObj.defaultSearchParam
    }
    if (this.searchParams && this.searchParams.length > 0) {
        params.searchParam = JSON.stringify(this.searchParams)
    }
    // 获取所有字段，使用模型module对象数据中的allFieldList属性
    // 这里的字段是同步模型-查看配置的字段，只有查看配置了显示才会出现
    let allFieldList = this.$attrs.allFieldList.filter(i => ShowStatus(i))
    if (allFieldList && allFieldList.length === 0) {
        this.$message.warning('模型查看没有配置对应字段，请联系管理员')
        return
    }
    // 定义禁止导出的字段数组
    let _exportDefined = []
    if (Remove_Space(this.diy_exportDefined)) {
        _exportDefined = Remove_Space(this.diy_exportDefined).split(GlobalConst.separator)
    }
    // 更新options，valueList数据，构造全选数据状态
    allFieldList.forEach(field => {
        let { display, name } = field
        // 判断字段是否配置了禁止导出
        if (_exportDefined.length === 0 ||
            _exportDefined.length !== 0 &&
            !_exportDefined.includes(name)) {
            options.push({ label: display, value: name })
            valueList.push(name)
        }
    })
    // 定义字段列表
    let fieldList = [
        { type: 'checkbox', name: dealFieldName, value: valueList.join(','), isShowAllLabel: true, options }
    ]
    // 定义更新字段函数的键名
    let updateFieldFunKey = 'updateFieldFun'
    // 判断是否传入自定义键名的
    if (setting && updateFieldFunKey in setting) {
        let updateFieldFun = setting[updateFieldFunKey]
        if (updateFieldFun && typeof updateFieldFun === 'function') {
            // 更新字段
            fieldList = updateFieldFun.call(this, fieldList)
        }
    }
    // 获取模型自定义配置数据
    let _setting = Get_Import_And_Export_Prop.call(this, 2, setting)
    // 定义导出地址
    let _exportUrl = Get_Full_Url(_setting.url || Export_Url(this.mdCode))
    // 定义弹窗操作按钮数组
    let btnList = [
        {
            name: '取消',
            icon: 'cancel',
            type: 'danger',
            click: function () {
                this.$dialog.close()
            }
        }, {
            name: '确定导出',
            loading: false,
            icon: 'export',
            type: 'primary',
            click: function (btnObj) {
                let formObj = this.getDialogConObj(formId, 2)
                formObj.validateForm().then(res => {
                    if (!res?.[dealFieldName]) {
                        this.$message.warning('请选择导出字段数据！')
                        return
                    }
                    // 更新参数params
                    params = Object.assign({}, params, res)
                    // 判断模型js中是否配置key，添加进参数
                    if (_setting.key) {
                        params.fieldValue = _setting.key.constructor === Object &&
                                            JSON.stringify(_setting.key)
                    }
                    // 切换按钮加载状态
                    btnObj.loading = true
                    // 下载文件
                    Download({
                        url: _exportUrl,
                        params,
                        method: 'post'
                    }).then(({status, message}) => {
                        if (status) {
                            this.$message.success('导出成功')
                            this.$dialog.close()
                        } else {
                            this.$message.error(message || '操作失败')
                        }
                    }).finally(() => {
                        // 切换按钮加载状态
                        btnObj.loading = false
                    })
                })
            }
        }
    ]
    this.$dialog.init({
        type: 'form',
        id: formId,
        isAutoFix: true, // 自适应内容
        title: _setting.title,
        columnNum: 1,
        dataList: fieldList,
        btnMethods: btnList,
        mdCode: this.mdCode,
        pageScope: this
    })
}

/**
 * 导入数据
 * @param {Object} btnObj 按钮对象
 * @param {Object} setting 配置项
 *      @param {Function, String} url 自定义导入接口
 *      @param {Object} key 导入接口提交的fieldValue值
 */
function ImportData (btnObj, setting) {
    // 获取模型自定义配置数据，合并为最终的导入配置
    let _setting = Object.assign({}, Get_Import_And_Export_Prop.call(this, 1, setting))
    // 原来打算在下面使用{..._setting}传递给import组件，但是参数中有个key字段，vue组件不允许传入key属性，又考虑到不影响已有项目使用了key的逻辑，所以这里将key改下名传入
    if ('key' in _setting) {
        _setting.keyProps = _setting.key
        delete _setting.key
    }
    // 定义列表页面作用域
    let that = this
    // 弹窗内容是自定义页面
    this.$pageDialog.init({
        pageUrl: 'import/index',
        outScope: this,
        mdCode: this.mdCode,
        ..._setting, // 配置项
        handlerList: [
            { id: 'import', name: '导入', icon: 'import', type: 'primary', loading: false, click: function (itemObj) {
                // this // 只想弹窗组件中的作用域
                // this.outScope // 若使用dialog时传入属性scope:this,则此处可获取使用弹窗的页面作用域
                // this.pageScope // 挂载页面所在的作用域
                // 调用自定义页面中的导入文件方法
                this.pageScope.clickImport(itemObj, that)
            }}
        ],
        height: 500
        // isAutoFix:true // 根据内容自适应
    })
}

/**
 * 获取冗余字段对象
 * @param {Object} option 配置项
 *     {
 *         @param {Array} selection 已选择的数组数据
 *         @param {String} idKey 指定id键
 *         @param {String} textKey 指定text键
 *         @param {Object} extraRelations 冗余字段key/valueKey的对应关系
 *         @param {Object} otherAttrs 含有冗余字段冗余字段(valueFieldId, valueFieldText)的对象
 *     }
 * @returns {Object} 含有需要提交表单的冗余字段对象
 */
function Get_Extra_Field (option) {
    // 不能加这句判断，不然当执行删除最后一条数据时，到这里会直接return,但其实应该组装{xxx:''}用于清空值
    // if (!Has_Array_Data(selection)) return {}
    let {
        selection,
        data = [],
        idKey = GlobalConst.dicOption.idName,
        textKey = GlobalConst.dicOption.textName,
        extraObj
    } = option
    // 获取冗余字段相关关系
    let extraRelations = extraObj.extraRelations
    if (extraRelations && Is_Object(extraRelations)) {
        extraRelations = JSON.parse(JSON.stringify(extraRelations))
        // 获取冗余字段键
        let { valueFieldId, valueFieldText } = extraObj
        // 若设计器有配置以下冗余字段，则更新给结果对象（防止组件内部默认的idKey与textKey不一致）
        if (valueFieldId && idKey) extraRelations[valueFieldId] = idKey
        if (valueFieldText && textKey) extraRelations[valueFieldText] = textKey
        let res = {}
        Object.keys(extraRelations).forEach(key => {
            // 下拉数据的取值key
            let valueKey = extraRelations[key]
            res[key] = selection.map(i => i[valueKey]).join(GlobalConst.separator)
        })
        return res
    }
    // ====== 以下是老版本，待删
    if (!(idKey && textKey && extraObj)) return {}
    // _ids: 获取选中id值，字符串格式
    // _texts: 获取选中文本值，字符串格式
    let [_ids, _texts] = data || []
    if (!(_ids || _texts) && selection && selection.length > 0) {
        // 获取选中id值，字符串格式
        _ids = selection.map(i => i[idKey]).join(GlobalConst.separator)
        // 获取选中文本值，字符串格式
        _texts = selection.map(i => i[textKey]).join(GlobalConst.separator)
    }
    // 获取冗余字段键
    let { valueFieldId, valueFieldText } = extraObj
    // 定义结果对象
    let _result = {}
    // 若设计器有配置以下冗余字段，则更新给结果对象
    if (valueFieldId) _result[valueFieldId] = _ids
    if (valueFieldText) _result[valueFieldText] = _texts
    return _result
}

// 获取冗余字段key/valueKey对应关系
function Get_Extra_Field_Relation (fieldItem) {
    // 获取自定义对应关系
    let extraObj = fieldItem.extraRelations || {}
    // 获取模型设计器配置的冗余字段对应关系
    // Src 标识从下拉数据取值的key（srcKey），无Src标识的key寓意为将srcKey对应的数据为在name===key的值
    let {
        valueFieldIdSrc,
        valueFieldTextSrc,
        valueFieldId, valueFieldText
    } = fieldItem
    // valueFieldIdSrc/valueFieldTextSrc要求必须有值
    valueFieldIdSrc = valueFieldIdSrc || GlobalConst.dicOption.idName
    valueFieldTextSrc = valueFieldTextSrc || GlobalConst.dicOption.textName
    // 优先模型配置
    if (valueFieldId) extraObj[valueFieldId] = valueFieldIdSrc || extraObj[valueFieldId]
    if (valueFieldText) extraObj[valueFieldText] = valueFieldTextSrc || extraObj[valueFieldText]
    // 存在需要当前字段存储文字的形式，所以将name的冗余字段对应关系也添加进去
    if (!extraObj[fieldItem.name]) extraObj[fieldItem.name] = valueFieldIdSrc
    return extraObj
}

// 获取列表按钮跳转的历史记录title，需要call(this)
function Get_List_History_Title (btnObj, row) {
    return btnObj.name + this.name
}

/**
 * @desciption: 根据搜索类型返回搜索关键词
 * @param {Number} type:搜索类型
 * @return: {String} 搜索类型下对应的搜索词
 */
function Get_Search_Type (type) {
    let typeWord = ''
    switch (type && parseInt(type)) {
        // 下拉多选 || 其他搜索
        case 0:
        case 15:
            typeWord = 'other-query'
            break
        // 文本框 || 模糊搜索
        case 1:
            typeWord = 'text-query'
            break
        // 日期选择 || 日期搜索
        case 5:
            typeWord = 'date-query'
            break
        // 数字区间
        case 6:
            typeWord = 'number-scope-query'
            break
        // 单选框 / 精准搜索
        case 9:
            typeWord = 'exact-match'
            break
        // 通常没有type类型的表示是由其他页面传递过来的参数，其属性是不带type的
        // 一般为钻取事件携带，对数据可以使用精准搜索
        // 默认:精准搜索
        default:
            typeWord = 'exact-match'
    }
    return typeWord
}

export {
    // 合并模型按钮，并返回新的按钮数组
    Merge_Btn,
    // 根据对象的属性值返回是否展示页面的状态
    ShowStatus,
    ShowStatus as Show_Status,
    // 按钮事件触发
    HandleClick,
    // 使用表单详情值更新表单字段列表对象下的值
    Update_FieldList_Value,
    // 获取搜索配置数据（核心在于获取下拉options数据）
    GetSearchFieldList,
    // 获取默认请求参数
    Get_Default_SearchParam,
    // 模型列表导出数据事件
    ExportData,
    // 模型列表导入数据事件
    ImportData,
    // 获取冗余字段对象
    Get_Extra_Field,
    // 获取冗余字段key/valueKey对应关系
    Get_Extra_Field_Relation,
    // 获取列表按钮跳转的历史记录title，需要call(this)
    Get_List_History_Title,
    // 校验唯一的函数
    Value_Unique_Fun,
    // 获取字段校验规则rules
    Get_Validation_Rule,
    // 根据搜索类型返回搜索关键词
    Get_Search_Type,
    // 获取模型的专属样式类名(基于模型编码)
    Get_Module_ClassName
}
