import GlobalConst from '@/service/global-const'
import Setting from '@/components/frame/FormDesigner/setting'
import { Get_TimeStamp } from '@/utils/time'
import { Get_UUID, Has_Value } from '@/utils'
// 定义属性键名
const AttrName = Setting.Attr_Name

/**
 * 根据指定键名获取值
 * @param {Object} option 配置项对象
 * @param {String} name 键名
 * @param {*} defaultVal 默认值
 * @returns 返回默认值
 */
function getVal (option, name, defaultVal) {
    if (option && name && name in option) {
        return option[name]
    }
    // 若传了默认值defaultVal，则使用该值返回
    if (arguments.length === 3) return defaultVal
    return ''
}
/**
 * 更新字段属性-通过传入的option对象属性数据
 * @param {Array} list 字段数组（数组下项为字段对象）
 * @param {Object} option option对象数据
 */
function setItemValByOption (list, option) {
    if (!option) return
    list.forEach(i => {
        i.value = getVal(option, i.name, i.value)
    })
}

/**
 * 获取字段键option数组
 * @param {Object} option 配置项
 */
function getNameOption (option) {
    let _nameOptionVal = getVal(option, 'nameOption') || []
    return _nameOptionVal.map(i => ({ id: i, text: i }))
}

/**
 * 在指定字段后添加字段数据
 * @param {String} fieldName 指定的字段键名
 * @param {Array} fieldList 字段数组
 */
function addField (fieldName, fieldList) {
    if (!(fieldName && fieldList && fieldList.length > 0)) return
    // 获取指定字段所在的下角标
    let aimFieldIndex = this[AttrName].findIndex(i => i.name === fieldName)
    if (!~aimFieldIndex) return
    // 在指定的字段后面，添加字段进行展示
    this[AttrName].splice(aimFieldIndex + 1, 0, ...fieldList)
}


// 定义表单类型需要统一添加的字段数组函数
function operateFun (typeName, option) {
    /**处理展示字段的编辑类型 ***********************************/
    /**********************************************************/
    // 获取type类型字段对象
    let _typeField = this[AttrName].find(i => i.name === Setting.Name_EditType)
    // 获取标签字段对象
    let _labelField = this[AttrName].find(i => i.name === 'label')
    // 获取表单项类型值type，label值
    let _currentTypeObj = Setting.Form_Option_Data.find(i => i.attrClassName === typeName)
    if (!_currentTypeObj) {
        console.error(`表单设计器：匹配不到表单项：${typeName}`)
        return
    }
    // 获取表单类型键: _type
    // 获取类型名称：_label
    // 获取该类型的默认属性值对象：defaultAttr
    let { optionId: _type, label: _label, defaultAttr } = _currentTypeObj
    // 更新type字段的值
    _typeField.value = _type
    // 更新label字段的值
    _labelField.value = _label
    // 更新类型字段对象，用于展示当前的编辑类型
    Object.assign(_typeField, {
        type: 'select',
        options: Setting.Form_Option_Data
                    .filter(i => !i[Setting.Name_IsNotFormItem])
                    .map(i => ({
                        id: i.optionId,
                        text: i.label
                    }))
    })
    /**为字段添加自定义配置项 ***********************************/
    /**********************************************************/
    this[AttrName].push(
        { type: 'addressBook', name: 'roleChoose', label: '角色选择', value: '', addressType: '64-0-40-64', valueFieldIdSrc: 'value', valueFieldTextSrc: 'name', valueFieldText: 'roleName' },
        { type: 'code', name: 'customOptions', label: '自定义配置项', value: '', useTip: 'JSON格式，形如{"disabled":true}' }
    )
    
    /**为字段赋值（回显） ***************************************/
    // 结合每个表单项类型下的属性默认值，获取最新的值数据_option
    let _option = Object.assign({}, defaultAttr, option)
    // 处理下多选类型，表单设计器中默认有多选选项时一般为选中多选，如果有多选状态，但是字段数据又没有多选属性字段，则配置为不开启多选
    // 一般为以前的表单设计器的数据，所以需要过渡处理，避免之前的模型数据在每次表单设计器保存后就会被修改成开启多选
    if (this[AttrName].find(i => i.name === 'multiple') &&
        !('multiple' in _option)) {
        _option.multiple = false
    }
    setItemValByOption(this[AttrName], _option)
    // 设置带有切换功能的表单项，有值的时候为开启状态
    let columnPerField = this[AttrName].find(i => i.name === 'columnPer')
    columnPerField.switchStatus = Has_Value(columnPerField.value)
}

/**
 * 获取规则options
 * @param {Array} option 规则option
 * @returns 
 */
function getRulesOption (option) {
    let _result = getVal(option, Setting.Name_Field.rulesOption)
    if (_result && _result.length > 0) return _result
    return Object.keys(Setting.Rules).map(i => ({
        id: i,
        text: Setting.Rules[i]
    }))
}

/**
 * 根据字段键名，获取在数组中的下角标
 * @param {String} name 字段键名 
 * @param {Array} list 数组数据 
 * @returns {Number} 匹配下角标
 */
function getIndex (name, list) {
    if (!(name && list)) return -1
    return list.findIndex(i => i.name === name)
}

/**
 * 删除表单项下的属性数组指定的属性，调用方式为delAttr('A','B')
 * @param {String} 字段键名
 * @returns 
 */
function delAttr () {
    // 获取所有删除属性键名数组
    let nameList = Array.from(arguments)
    if (nameList.length === 0) return
    nameList.forEach(i => {
        // 获取匹配的下角标
        let _cIndex = getIndex(i, this[AttrName])
        if (~_cIndex) {
            // 匹配则删除属性项
            this[AttrName].splice(_cIndex, 1)
        }
    })
}

// 基础字段类
class BaseField {
    constructor (option) {
        this.data = [
            { type: 'hidden', name: 'fieldId', label: '字段的唯一标识', value: Get_UUID() },
            ...(new AddEditRun()).data, // 字段活跃状态
            ...(new AddGroup(option)).GetHideData(), // 添加组别信息
            { type: 'text', name: Setting.Name_EditType, label: '编辑类型', value: '' },
            ...(new AddName(option)).data, // 字段键名
            { type: 'text', name: 'label', label: '展示标签名称', value: '' },
            { type: 'text', name: 'value', label: '默认值', value: '' },
            { type: 'text', name: 'placeholder', label: '预输入文本', value: '' },
            { type: 'text', name: 'useTip', label: '字段使用提示', value: '' },    
            { type: 'number', name: 'columnPer', label: '标签输入占比(自定义)', value: null, placeholder: '请输入占比数（共24）', max: 24, min: 0, switchStatus: false },
            { type: 'switch', name: 'isOneLine', label: '独占一行', value: false },
            { type: 'switch', name: 'isBlock', label: '标签独占一行', value: false },
            { type: 'switch', name: 'isShowAllLabel', label: '优先铺满标签', value: false, useTip: '默认是固定标签宽度，超出则换行。开启此项配置后将不再固定标签宽度，直接平铺，剩余宽度才给表单编辑项' },
            { type: 'select', name: 'rules', label: '验证规则', value: '', multiple: true, options: getRulesOption(option), isCollapse: false },
            // { type: 'checkbox', name: 'operateAttr', label: '操作属性', value: '', options: [
            //     { id: 'readonly', text: '只读' },
            //     { id: 'disabled', text: '禁用' },
            //     { id: 'destroyed', text: '不提交' },
            //     { id: 'hidden', text: '隐藏' },
            // ]},
            // { type: 'switch', name: 'readonly', label: '只读', value: false },
            ...(new AddHidden(option)).data, // 添加隐藏
            { type: 'switch', name: 'disabled', label: '只读', value: false },
            { type: 'switch', name: 'destroyed', label: '不提交', value: false },
            { type: 'text', name: 'selfClass', label: '自定义class', value: '' }
        ]
    }
}

// 添加当前编辑状态
class AddEditRun {
    constructor (option) {
        this.data = [
            { type: 'hidden', name: 'editRun', value: false },
        ]
    }
}

// 添加options数据的索引路径
class AddOptionResPath {
    constructor (option) {
        this.data = [
            { type: 'roadmap', name: 'optionResPath', label: '获取options值的索引路径', value: '', useTip: `请求options数据时获取到的数据res,可以配合这里的索引路径获取到真正的options数据，如索引路径填写了两项，值分别为'A'与'B'，则res[A][B]将会作为options数据源` },
        ]
    }
}

// 添加字段键名
class AddName {
    constructor (option) {
        // 获取字段键名数组
        let _nameOption = getNameOption(option)
        // 根据传入的可选键名数据，返回状态
        let _hasNameOption = _nameOption.length > 0
        let _fieldObj = {
            type: 'text',
            name: 'name',
            label: '字段键名',
            value: '',
            rules: [
                { required: true, message: '请输入字段键名', trigger: ['blur','change'] }
            ]
        }
        // 判断是否有传入键名可选数据
        if (_hasNameOption) {
            _fieldObj.label = _fieldObj.label + '/模型字段'
            // 设置可选数据
            _fieldObj.type = 'autoComplete'
            _fieldObj.fetchSuggestions = function (queryString, callback) {
                callback(_nameOption)
            }
        }
        this.data = [_fieldObj]
    }
}

// 添加密码
class AddPassword {
    constructor (option) {
        this.data = [
            { 
                type: 'switch', name: Setting.Name_Field.isPw, label: '是否为密码',
                value: false,
                disabled: getVal(option, Setting.Name_Field.disabled, false)
            },
        ]
    }
}
// 添加隐藏
class AddHidden {
    constructor (option) {
        this.data = [
            { type: 'switch', name: 'hidden', label: '隐藏',
                value: false,
                disabled: getVal(option, Setting.Name_Field.hiddenDisabled, false) },
        ]
    }
}

// 添加输入框具体类型
class AddDataType {
    constructor (option) {
        this.data = [
            { type: 'hidden', name: 'dataType', value: 'string' },
        ]
    }
}

// 添加日期时间相关字段
class AddDateAttr {
    constructor (option) {
        let _option = option || {}
        // 定义获取所有的日期子项数组数据
        let _dateAttrOptions = Setting.Date_Type_Option
        // 根据传入option进行过滤，获取符合条件的数据项
        Object.keys(_option).forEach(i => {
            _dateAttrOptions = _dateAttrOptions.filter(j => j[i] === _option[i])
        })
        // 取符合数据项的第一项，取出其中的展示格式，值格式
        let { showFormat, valueFormat } = _dateAttrOptions[0]
        // 定义日期类型字段
        let _dateTypeField = null
        // 定义日期-展示格式字段
        let _showFormatField = null
        // 定义日期-值格式字段
        let _valueFormatField = null
        // 目前传入option有两种，一种是type为date，另一种是传入指定id
        if (_option.type === 'date') {
            // type为date时，可下拉选择其他的日期子项类型
            _dateTypeField = { 
                type: 'select',
                name: Setting.Name_Field.dateType,
                label: '日期子类型',
                value: 'date',
                options: _dateAttrOptions,
                rules: [
                    { required: true, message: '请选择日期类型', trigger: ['blur','change'] }
                ],
                clearable: false,
            }
        } else {
            // 其他非date的日期类型，则设置隐藏，不出现不被修改
            _dateTypeField = { type: 'hidden', name: Setting.Name_Field.dateType, value: 'date' }
        }
        // 设置日期-展示格式字段
        _showFormatField = { 
            type: 'text',
            label: '展示在页面的格式',
            name: Setting.Name_Field.showFormat,
            value: showFormat,
        }
        // 设置日期-值格式字段
        _valueFormatField = { 
            type: 'text',
            label: '值格式',
            name: Setting.Name_Field.valueFormat,
            value: valueFormat,
        }
        this.data = [_dateTypeField, _showFormatField, _valueFormatField]
    }
}

// 添加日期范围相关配置
class AddDateRange {
    constructor (option) {
        this.data = [
            { type: 'select', name: GlobalConst.form.endFieldName, label: '结束字段', value: null, options: getNameOption(option) }
        ]
    }
}

// 添加前置后置元素
class AddPositive {
    constructor (option) {
        this.data = [
            { type: 'text', name: 'prepend', label: '前置元素', value: '' },
            { type: 'text', name: 'append', label: '后置元素', value: '' },
        ]
    }
}

// 添加是否支持多选
class AddMultiple {
    constructor (option) {
        this.data = [
            { type: 'switch', name: 'multiple', label: '是否多选', value: true,
              disabled: getVal(option, Setting.Name_Field.multipleDisabled, false) },
        ]
    }
}

// 添加options选项相关字段
class AddOptions {
    constructor (option) {
        this.data = [
            { type: 'select', name: Setting.Name_Field.dataSource, label: '数据来源', value: '1',
              options: option && option.dicData && option.dicData.MODULE_EDIT_SOURCE },
            // { type: 'select', name: 'dicName', label: '数据字典编码', value: 'ill_reason',
            //   options: option && option.dicData && option.dicData.ALLL_DIC_CODE },
            { type: 'text', name: Setting.Name_Field.dicName, label: '数据字典/模型编码', value: '' },
            { type: 'hidden', name: Setting.Name_Field.options, value: [] },
            { type: 'text', name: Setting.Name_Field.dataBean, label: '数据来源bean', value: '' },
            { type: 'text', name: Setting.Name_Field.dataFunction, label: '数据来源bean方法', value: '' },
            // 添加options数据的索引路径
            ...new AddOptionResPath(option).data,
        ]
    }
}

// 添加冗余赋值字段
class AddExtraField {
    constructor (option) {
        this.data = [
            { type: 'text', name: 'valueFieldId', label: '保存Key值给', value: '' },
            { type: 'text', name: 'valueFieldText', label: '保存Value给', value: '' },
        ]
    }
}

// 添加数据源
class AddOptionsAndSetting {
    constructor (option) {
        this.data = [
            ...new AddOptions(option).data,
            { type: 'text', name: 'valueFieldIdSrc', label: '源数据Key值', value: '' },
            { type: 'text', name: 'valueFieldTextSrc', label: '源数据Value值', value: '' },
            // 添加冗余赋值字段
            ...new AddExtraField(option).data,
        ]
    }
}

// 添加组别信息
class AddGroup {
    constructor (option) {
        this.data = [
            { type: 'hidden', name: Setting.GroupId, value: '',
              [Setting.IsGroup]: true
            },
            { type: 'text', name: 'groupName', label: '分组名称', value: GlobalConst.form.groupName, placeholder: '请输入组名', useTip: '当表单只有一组时，则不展示此处组名，使用表单名展示',
              [Setting.IsGroup]: true
            },
            { type: 'iconPicker', name: 'groupIcon', label: '分组图标', value: GlobalConst.form.groupIcon, 
              [Setting.IsGroup]: true
            },
        ]
    }
    // 获取组别键名
    getKey () {
        return this.data.reduce((result, item) => {
            result[item.name] = ''
            return result
        }, {})
    }
    // 获取所有字段数据（包括隐藏字段）
    GetHideData () {
        return this.data.map(i => {
            i.hidden = true
            return i
        })
    }
}

// 添加文件类型配置（包含图片与附件）
class AddFileSetting {
    constructor (option) {
        this.data = [
            { type: 'text', name: 'suffix', label: '支持文件格式', value: '', placeholder: '可输入"jpg,png"' },
            // { type: 'select', name: 'fileWrongTipType', label: '错误提示方案', value: 'none', options: [
            //     { id: 'dialog', text: '弹窗' },
            //     { id: 'notification', text: '通知' },
            //     { id: 'none', text: '文本提醒' },
            // ]},
            { type: 'text', name: 'maxSize', label: '限制文件大小', value: null, placeholder: '可输入"5MB"' },
            { type: 'text', dataType: 'int', name: 'limit', label: '限制上传数', value: null },
        ]
    }
}

// 添加附件专有配置:这里是展示字段showType会与模型设计器的showType起反应，暂时去除
// class AddAttachSetting {
//     constructor (option) {
//         this.data = [
//             { type: 'select', name: 'showType', label: '展示类型', value: 'card', options: [
//                 { id: 'card', text: '卡片' },
//                 { id: 'list', text: '列表' },
//             ]},
//         ]
//     }
// }

// 表单类
class Form {
    constructor (option) {
        this[AttrName] = [
            { type: 'text', name: 'id', label: '标识id', value: `form_${Get_TimeStamp()}`, placeholder: '请输入标识id' },
            { type: 'text', name: 'title', label: '标题', value: GlobalConst.form.title, placeholder: '请输入标题' },
            { type: 'text', name: 'labelWidth', label: '标签宽度', value: GlobalConst.form.labelWidth, placeholder: '请输入标签宽度' },
            { type: 'text', dataType: 'int', name: 'columnNum', label: '单行列数', value: GlobalConst.form.columnNum, placeholder: '请输入单行列数', max: 12, min: 0 },
            // { type: 'hidden', name: 'dataList', value: [] }
        ]
        /**为字段赋值（回显） ***************************************/
        /**********************************************************/
        setItemValByOption(this[AttrName], option)
    }
}


// 其他表单子项类
class Text {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
            // 添加密码
            ...(new AddPassword().data),
            // 添加前后置
            ...(new AddPositive().data),
        ]
        // !!!!!!
        // 为什么这里要区分_option下的会编辑的属性与固定配置
        //     当你拖动新的表单项时，希望使用的是默认配置
        //     当你切换已有的表单项类型时，希望使用的是默认配置
        //     但是当你是修改时，此时使用的是已有数据进行渲染
        //     所以：固定的属性一定是固定是，不可修改，就放在传入的option后；其余页面可编辑的属性就放在option前，确保传入的option优先级高于默认配置
        // !!!!!!
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            // ...
        }
        // 执行公共操作
        operateFun.call(this, 'Text', _option)
    }
}
class Password {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
            // 添加密码
            ...(new AddPassword({
                [Setting.Name_Field.disabled]: true
            }).data),
        ]
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            [Setting.Name_Field.isPw]: true,
        }
        // 执行公共操作
        operateFun.call(this, 'Password', _option)
    }
}
class Int {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
            // 添加文本类型
            ...(new AddDataType().data),
            // 添加前后置
            ...(new AddPositive().data),
        ]
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            [Setting.Name_Field.dataType]: 'int',
        }
        // 执行公共操作
        operateFun.call(this, 'Int', _option)
    }
}
class Number {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
            // 添加文本类型
            ...(new AddDataType().data),
            // 添加前后置
            ...(new AddPositive().data),
        ]
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            [Setting.Name_Field.dataType]: 'number',
        }
        // 执行公共操作
        operateFun.call(this, 'Number', _option)
    }
}
// 定义一个隐藏类型
class Hidden {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField({
                ...option,
                [Setting.Name_Field.hiddenDisabled]: true,
            }).data),
        ]
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            [Setting.Name_Field.hidden]: true,
        }
        // 执行公共操作
        operateFun.call(this, 'Hidden', _option)
    }
}
class Textarea {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
            { type: 'number', name: 'rows', label: '行数', value: GlobalConst.textarea.rows },
            { type: 'switch', name: 'autoSize', label: '自动内容撑高高度', value: true },
            { type: 'switch', name: 'noResize', label: '显示可自由拖动尺寸', value: false }
        ]
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            // ...
        }
        // 执行公共操作
        operateFun.call(this, 'Textarea', _option)
    }
}
class Switch {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 执行公共操作
        operateFun.call(this, 'Switch', option)
        // 删除placeholder属性
        delAttr.call(this, Setting.Name_Field.placeholder)
    }
}
class Radio {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 添加字段来源--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            ...(new AddOptionsAndSetting(option).data),
        ])
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            // ...
        }
        // 执行公共操作
        operateFun.call(this, 'Radio', _option)
        // 删除placeholder属性
        delAttr.call(this, Setting.Name_Field.placeholder)
    }
}
class Checkbox {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 添加字段来源--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            // 固定为多选
            ...(new AddMultiple({[Setting.Name_Field.multipleDisabled]: true}).data),
            ...(new AddOptionsAndSetting(option).data),
        ])
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            // ...
        }
        // 执行公共操作
        operateFun.call(this, 'Checkbox', _option)
        // 删除placeholder属性
        delAttr.call(this, Setting.Name_Field.placeholder)
    }
}

class AutoComplete {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 添加字段来源--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            // 添加是否支持多选
            ...(new AddOptions(option).data),
            { type: 'text', name: 'valueKey', label: 'options指定【值】对应键名', value: GlobalConst.dicOption.textName },
            { type: 'switch', name: 'autoFilter', label: '按输入值过滤选项', value: true, useTip: '默认开启，开启后将根据输入值过滤匹配的选项。关闭后则可选项始终不变' },
        ])
        // 执行公共操作
        operateFun.call(this, 'AutoComplete', option)
    }
}

class Select {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 添加字段来源--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            // 添加是否支持多选
            ...(new AddMultiple().data),
            ...(new AddOptionsAndSetting(option).data),
        ])
        // 执行公共操作
        operateFun.call(this, 'Select', option)
    }
}
class Date {
    constructor (option) {
        let _dateType = 'date'
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 添加日期类型--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            ...(new AddDateAttr({ type: _dateType }).data),
        ])       
        let _option = {
            ...(option || {}),
        }
        // 执行公共操作
        operateFun.call(this, 'Date', _option)
    }
}
class Time {
    constructor (option) {
        let _dateType = 'time'
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 添加日期类型--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            ...(new AddDateAttr({ id: _dateType }).data),
        ])        
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            [Setting.Name_Field.dateType]: _dateType,
        }
        // 执行公共操作
        operateFun.call(this, 'Time', _option)
    }
}

class DateTime {
    constructor (option) {
        let _dateType = 'datetime'
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 添加日期类型--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            ...(new AddDateAttr({ id: _dateType }).data),
        ])       
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            [Setting.Name_Field.dateType]: _dateType,
        }
        // 执行公共操作
        operateFun.call(this, 'DateTime', _option)
    }
}

class Year {
    constructor (option) {
        let _dateType = 'year'
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 添加日期类型--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            ...(new AddDateAttr({ id: _dateType }).data),
        ])       
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            [Setting.Name_Field.dateType]: _dateType,
        }
        // 执行公共操作
        operateFun.call(this, 'Year', _option)
    }
}
class Month {
    constructor (option) {
        let _dateType = 'month'
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 添加日期类型--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            ...(new AddDateAttr({ id: _dateType }).data),
        ])          
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            [Setting.Name_Field.dateType]: _dateType,
        }
        // 执行公共操作
        operateFun.call(this, 'Month', _option)
    }
}

class Cascader {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 添加文件配置--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            // 添加是否支持多选
            ...(new AddMultiple().data),
            { type: 'text', name: 'optionsUrl', label: '数据请求地址', placeholder: '/api/demo.do', value: null },
            // 添加options数据的索引路径
            ...new AddOptionResPath(option).data,
            { type: 'switch', name: 'checkStrictly', label: '允许选择任意级', value: false, useTip: '默认是只能选择叶子节点，开启后，将可以选择任意中间节点不限制' },
            { type: 'switch', name: 'onlyLeaf', label: '选中只取当前节点值', value: false, useTip: '当选择A节点下的B节点下的C时，默认是返回A-B-C(完整路径)，开启此处配置后，则只返回C' },
            { type: 'switch', name: 'showAllLevels', label: '显示完整路径', value: true, useTip: '输入框中是否显示选中值的完整路径,关闭后，将只显示选中的节点不携带其父级' },
            { type: 'text', name: 'linkSeparator', label: '级值连接符', value: GlobalConst.cascader.separator, useTip: '当选择A节点下的B节点下的C时，配置连接符为【-】时，则返回值A-B-C' },
            { type: 'text', name: 'showSeparator', label: '级展示连接符', value: GlobalConst.cascader.showSeparator, useTip: '当选择A节点下的B节点下的C时，配置显示连接符为【/】时，则输入框中显示为A/B/C' },
            { type: 'text', name: 'idFieldKey', label: 'options选项的值键', value: GlobalConst.cascader.id },
            { type: 'text', name: 'textFieldKey', label: 'options选项的文本键', value: GlobalConst.cascader.text },
            { type: 'text', name: 'childrenFieldKey', label: 'options选项的子节点键', value: GlobalConst.cascader.children },
            // 添加冗余赋值字段
            ...new AddExtraField(option).data,
        ])
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            // ...
        }
        // 执行公共操作
        operateFun.call(this, 'Cascader', _option)
    }
}

class Img {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 添加文件配置--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            // 添加是否支持多选
            ...(new AddMultiple().data),
            ...(new AddFileSetting().data),
        ])
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            // 目前图片编辑组件要求传入值必须为[]，不能使用null，否则选择图片将无法展示回显
            [Setting.Name_Field.value]: []
        }
        // 执行公共操作
        operateFun.call(this, 'Img', _option)
        // 删除placeholder属性
        delAttr.call(this, Setting.Name_Field.placeholder)
    }
}
class Attach {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 添加文件配置--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            // 添加是否支持多选
            ...(new AddMultiple().data),
            ...(new AddFileSetting().data),
            // ...(new AddAttachSetting().data),
        ])
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            // ...
        }
        // 执行公共操作
        operateFun.call(this, 'Attach', _option)
        // 删除placeholder属性
        delAttr.call(this, Setting.Name_Field.placeholder, Setting.Name_Field.value)
    }
}
class RichText {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 添加文件配置--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            // 添加自定义高度值
            { type: 'number', name: 'height', label: '自定义高度', value: GlobalConst.richText.height }
        ])
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            // ...
        }
        // 执行公共操作
        operateFun.call(this, 'RichText', _option)
        // 删除placeholder属性
        delAttr.call(this, Setting.Name_Field.placeholder)
    }
}
class AddressPicker {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
            // 添加冗余赋值字段
            ...new AddExtraField(option).data,
        ]
        // 执行公共操作
        operateFun.call(this, 'AddressPicker', option)
    }
}
class Group {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
            { type: 'hidden', name: 'destroyed', value: true },
            // Setting.Place表示渲染在页面时不可见，只用于组别的占位，避免组别下没数据组别显示不出来
            { type: 'hidden', name: Setting.Place, value: true },
        ]
        // 执行公共操作
        operateFun.call(this, 'Group', option)
    }
}
class Column {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
            { type: 'hidden', name: 'destroyed', value: true },
            // Setting.Place表示渲染在页面时不可见，只用于组别的占位，避免组别下没数据组别显示不出来
            { type: 'hidden', name: Setting.Place, value: true },
        ]
        // 执行公共操作
        operateFun.call(this, 'Column', option)
    }
}
class ColorPicker {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            // 添加是否支持多选
            ...(new AddMultiple().data),
            { type: 'number', name: 'limit', label: '限制数(0为不限)', value: null },
            { type: 'switch', name: 'showAlpha', label: '支持透明度', value: false },
        ])
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            // ...
        }
        // 执行公共操作
        operateFun.call(this, 'ColorPicker', _option)
        // 删除placeholder属性
        delAttr.call(this, Setting.Name_Field.placeholder)
    }
}
class Rate {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 执行公共操作
        operateFun.call(this, 'Rate', option)
    }
}
class Daterange {
    constructor (option) {   
        let _dateType = 'daterange'
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 添加范围相关字段--在【值】字段后面添加
        addField.call(this, Setting.Name_Field.value, [
            ...(new AddDateRange(option).data),
        ])           
        // 添加日期类型--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            ...(new AddDateAttr({ id: _dateType }).data),
        ])           
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            [Setting.Name_Field.dateType]: _dateType,
        }
        // 执行公共操作
        operateFun.call(this, 'Daterange', _option)
    }
}
class DateTimerange {
    constructor (option) {
        let _dateType = 'datetimerange'
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 添加范围相关字段--在【值】字段后面添加
        addField.call(this, Setting.Name_Field.value, [
            ...(new AddDateRange(option).data),
        ])        
        // 添加日期类型--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            ...(new AddDateAttr({ id: _dateType }).data),
        ])        
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            [Setting.Name_Field.dateType]: _dateType,
        }
        // 执行公共操作
        operateFun.call(this, 'DateTimerange', _option)
    }
}
class Code {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 添加日期类型--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            { type: 'select', name: 'lang', label: '代码语言', value: 'json', options: [
                { id: 'json', text: 'json' },
                // { id: 'html', text: 'html' },
                // { id: 'javascript', text: 'javascript' },
            ]},
        ])
        let _option = {
            // 1.这里放页面上会编辑到的配置
            // 在setting.js对应表单项下defaultAttr填入配置
            // 2.这里放option
            ...(option || {}),
            // 3.这里放页面不会编辑到的配置（固定配置）
            // ...
        }
        // 执行公共操作
        operateFun.call(this, 'Code', _option)
        // 删除placeholder属性
        delAttr.call(this, Setting.Name_Field.placeholder)
    }
}
class DialogList {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
            { type: 'hidden', name: Setting.Name_Field.mdCode, value: null },
        ]
        // 添加字段来源--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            // 添加是否支持多选
            ...(new AddMultiple().data),
            ...(new AddOptionsAndSetting(option).data)
        ])
        // 执行公共操作
        operateFun.call(this, 'DialogList', option)
        // 删除placeholder属性
        delAttr.call(this, Setting.Name_Field.placeholder)
    }
}
class IconPicker {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 执行公共操作
        operateFun.call(this, 'IconPicker', option)
        // 删除placeholder属性
        delAttr.call(this, Setting.Name_Field.placeholder)
    }
}
// 定义地址本
class AddressBook {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 添加日期类型--在【使用提示】字段后面添加
        addField.call(this, 'label', [
            { type: 'select', name: 'addressType', label: '地址本类型', value: '', multiple: true,
                options: option && option.dicData && option.dicData.ADDRESS_TYPE },
        ])
        // 添加字段来源--在【使用提示】字段后面添加
        addField.call(this, Setting.Name_UseTip, [
            ...(new AddOptionsAndSetting(option).data),
        ])
        // 执行公共操作
        operateFun.call(this, 'AddressBook', option)
    }
}
class StaffAddressBook {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
            { type: 'hidden', name: 'addressType', value: '2-1-20-2' },
        ]
        // 执行公共操作
        operateFun.call(this, 'StaffAddressBook', option)
    }
}
class DepartAddressBook {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
            { type: 'hidden', name: 'addressType', value: '2-1-0-2' },
        ]
        // 执行公共操作
        operateFun.call(this, 'DepartAddressBook', option)
    }
}
class PathPicker {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 执行公共操作
        operateFun.call(this, 'PathPicker', option)
    }
}

class Roadmap {
    constructor (option) {
        this[AttrName] = [
            ...(new BaseField(option).data),
        ]
        // 执行公共操作
        operateFun.call(this, 'Roadmap', option)
    }
}

let FormClass = {
    // 表单
    Form,
    // 表单子项
    Text,
    Textarea,
    Password,
    Int,
    Number,
    Hidden,
    Switch,
    Radio,
    Checkbox,
    AutoComplete,
    Select,
    Date,
    Time,
    DateTime,
    Year,
    Month,
    Cascader,
    Img,
    Attach,
    RichText,
    AddressPicker,
    Group,
    Column,
    ColorPicker,
    Rate,
    Daterange,
    DateTimerange,
    Code,
    DialogList,
    IconPicker,
    AddressBook,
    StaffAddressBook,
    DepartAddressBook,
    PathPicker,
    Roadmap,

    AddGroup, // 组别对象信息
}
export {
    FormClass,
}