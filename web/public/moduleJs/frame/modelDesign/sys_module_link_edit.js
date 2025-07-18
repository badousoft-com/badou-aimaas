import { Load_Field_For_Table } from '@/api/frame/desinger/field'
import { Has_Value } from '@/utils'
export default {
    columnNum: 2,
    beforeRender: function () {
        // 优化目标模型的下拉展示文本
        let _relatedModuleId = getField(this._fieldList, 'relatedModuleId')
        _relatedModuleId.options = _relatedModuleId.options.map(i => ({
            ...i,
            text: `${i.code}[${i.text}]`
        }))

        // 获取【类型】字段
        let _type = getField(this._fieldList, 'type')
        // 设置类型options下的（多对多）为禁用状态，该场景功能未完善
        _type.options.find(i => i.id === '3').disabled = true
        // 动态设置【展示类型】
        setField_ShowType_Options(this._fieldList)
        // 动态设置【所属Tab名称】
        toggle_TabName.call(this, this._fieldList)

    },
    beforeFieldChange: function () {
        // 给字段名赋值请求下拉框url
        let field_obj = this.fieldList.find(o => o.name === 'fieldId')
        if (field_obj) {
            field_obj.fun = '/module/design/moduledesignedit/loadTableField.do?moduleId=' + this.$attrs.mainScope.detailId
        }
        // 找到关联表字段所在表单
        let fieldName_obj = this.fieldList.find(o => o.name === 'relatedField')
        let relatedModuleId = this.fieldList.find(o => o.name === 'relatedModuleId')?.value
        // 如果关联表有值，需要请求赋值关联表字段下拉框
        if (fieldName_obj && relatedModuleId) {
            // 给表单赋值下拉框
            fieldName_obj.fun = '/module/design/moduledesignedit/loadTableField.do?moduleId=' + relatedModuleId
        }
    },
    /**
    * 字段自定义change事件
    * @param {*} fielaName 字段名
    * @param {*} value change后对应值
    */
    fieldChange: function (fielaName, value) {
        if (this.$attrs?.mainScope?.customSetting?.isChangeInfo) {
            this.$attrs.mainScope.customSetting.isChangeInfo[this.mdCode] = true
        }
        switch (fielaName) {
            // 关联表
            case 'relatedModuleId':
                // 找到关联表字段所在表单
                let field_obj = this.fieldList.find(o => o.name === 'relatedField')
                field_obj.value = ''
                // 给表单赋值下拉框
                Load_Field_For_Table({ moduleId: value }).then(res => {
                    if (res) {
                        this.$set(field_obj, 'options', res)
                    }
                })
                break
            case 'type':
            case 'isEdit':
                // 动态设置【展示类型】
                setField_ShowType_Options(this.fieldList)
                break
            case 'showStyle':
                // 动态设置【所属Tabmingcheng】是否显示
                toggle_TabName.call(this, this.fieldList)
                break
            default:
            // do default
        }
    }
}

/**
 * 根据【类型】【是否编辑】确认【展示类型】的可选数据项
 *      1. 数据字典
 *          类型：1(一对一) / 2(一对多) / 3(多对多) 
 *          是否编辑：0(否) / 1(是)
 *          展示类型：0(编辑表单) / 1(列表) / 2(查看表单)
 *      2. 规则
 *          1. 类型选择1v1时，展示类型可选项只显示【编辑表单/查看表单】
 *              是否编辑选择是时，展示类型显示为【编辑表单】
 *              是否编辑选择否时，展示类型显示为【查看表单】
 *          2. 类型选择1vN时，展示类型可选项只显示【列表】
 *      3. TODO: 是否编辑与展示类型需求感觉还是有问题，在1对1的场景时是互相牵制的
 * @param {*} type 
 * @param {*} isEdit 
 */
function setField_ShowType_Options (fieldList) {
    // 定义获取字段对象的函数
    let _getField = (name) => getField(fieldList, name)
    // 获取【类型】字段
    let _typeField = _getField('type')
    // 获取【是否编辑】字段
    let _isEditField = _getField('isEdit')
    // 获取【展示类型】字段
    let _showTypeField = _getField('showType')
    // 判断【类型】是否有值
    if (!(_typeField && Has_Value(_typeField.value))) {
        _showTypeField.options.forEach(i => i.hidden = false)
        return
    }
    // 定义函数：判断是否有匹配项
    let _hasMatch = (value, option) => {
        if (!(value && option && option.length > 0)) return
        return option
            .filter(i => !i.hidden)
            .find(i => parseInt(i.id) === parseInt(value))
    }
    // 定义【展示类型】的数据字典
    let _showTypeDic = {
        editForm: 0,
        list: 1,
        viewForm: 2
    }
    // 按照【类型】的值进行处理
    switch (parseInt(_typeField.value)) {
        // 当【类型】选择一对一
        case 1:
            // 判断【是否编辑】字段是否有值
            if (Has_Value(_isEditField.value)) {
                // 有值时根据【是否编辑】设置【展示类型】的可选项数据
                let _showType = parseInt(_isEditField.value) ? _showTypeDic.editForm : _showTypeDic.viewForm
                _showTypeField.options = _showTypeField.options.map(i => {
                    i.hidden = (parseInt(i.id) !== _showType)
                    return i
                })
                // 设置【展示类型】默认选中值
                _showTypeField.value = '' + _showType
            } else {
                // 【是否编辑】无值时设置【展示类型】可选项数据为表单相关
                _showTypeField.options = _showTypeField.options.map(i => {
                    i.hidden = ![_showTypeDic.editForm, _showTypeDic.viewForm].includes(parseInt(i.id))
                    return i
                })
            }
            // 判断【展示类型】值是否与可选项数据匹配，无则清空值
            if (Has_Value(_showTypeField.value) && !_hasMatch(_showTypeField.value, _showTypeField.options)) {
                _showTypeField.value = null
            }
            break
        // 当【类型】选择一对多
        case 2:
            // 设置【展示类型】的可选项数据为列表
            _showTypeField.options = _showTypeField.options.map(i => {
                i.hidden = (parseInt(i.id) !== _showTypeDic.list)
                return i
            })
            // 设置【展示类型】默认选中值
            _showTypeField.value = '' + _showTypeDic.list
            break
        case 3:
        default:
            // do something default
    }
}

/**
 * 动态设置【所属Tab名称】是否显示。只有同级Tab与平级tab时才需要展示所属Tab名称
 * @param {Array} fieldList 字段数组 
 */
function toggle_TabName (fieldList) {
    // 定义获取字段对象的函数
    let _getField = (name) => getField(fieldList, name)
    // 获取【所属Tab名称】字段
    let _tabNameField = _getField('tabName')
    // 获取【展示风格】字段
    let _showStyleField = _getField('showStyle')
    // 根据【展示风格】值进行处理，默认为同级Tab（值为1）
    switch (_showStyleField.value && parseInt(_showStyleField.value) || 1) {
        case 3:
            this.$set(_tabNameField, 'hidden', true)
            _tabNameField.value = ''
            break
        case 1:
        case 2:
        default:
            // 获取【展示名称】字段
            let _displayNameField = _getField('displayName')
            // 设置所属Tab名称字段显示
            this.$set(_tabNameField, 'hidden', false)
            // 设置所属Tab名称值
            _tabNameField.value = _tabNameField.value || _displayNameField.value
    }
} 

/**
 * 在字段列表fieldList中根据指定name获取字段对象
 * @param {Array} fieldList 字段列表
 * @param {String} name 字段键名
 * @returns 
 */
function getField (fieldList, name) {
    if (!(name &&
        fieldList &&
        fieldList.constructor === Array &&
        fieldList.length > 0)) return
    return fieldList.find(i => i.name === name)
}