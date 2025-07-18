import { FormClass } from '@/components/frame/FormDesigner/class.js'
import { Get_UUID } from '@/utils'
import Setting from '../setting'
import { Get_Rest_Obj } from '@/utils/object'

/**
 * 去除字段中的过渡无用字段
 * @param {Array} list 字段数组
 * @param {Boolean} transferStatus 变更状态：true为去除，false为添加，默认是去除
 * @return {Array} 返回处理后的字段数组
 */
function Delete_Transition_Field (list, transitionStatus = true) {
    if (transitionStatus) {
        // 去除过渡字段，直接返回
        return list.map(i => {
            let { groupId, editRun, ...item } = i
            return item
        })
    } else {
        // 存储已有组名
        let groupMap = new Map()
        let _res = list.map(i => {
            // 获取组名
            let _groupName = i.groupName
            // 定义组别id
            let _groupId = null
            // 判断当前该条数据组名是否存在
            if (groupMap.has(_groupName)) {
                // 存在则直接使用其对应的组别id
                _groupId = groupMap.get(_groupName)
            } else {
                // 不存在则新建组别id，并且更新已有组名组别数据
                _groupId = Get_UUID()
                groupMap.set(_groupName, _groupId)
            }
            // 返回新的数据项
            return {
                groupId: _groupId,
                editRun: false,
                ...i
            }
        })
        return _res
    }
}

/**
 * 简单表单json（字段为对象）与复杂表单json（字段为数组）的相互转化
 * @param {Array} fieldList 字段数组
 * @param {Array} optionData 左侧操作的表单项
 * @param {Boolean} toStatus 是否从简单向复杂转化，默认是 true
 * @returns 转化后的字段数组值
 */
function SimpleForm_To_WholeForm (fieldList, optionData, toStatus = true) {
    // 定义结果值
    let _result = []
    if (toStatus) {
        // 为字段数据添加过渡属性字段
        fieldList = Delete_Transition_Field(fieldList, false)
        // 定义所有的组别名
        let groupNames = []
        fieldList.forEach(i => {
            // 在表单设计器中，表单类型使用的是Setting.Name_EditType对应的字段，这里兼容已有的数据，所以|| i.type
            let _formEditType = i[Setting.Name_EditType] || i.type
            i[Setting.Name_EditType] = _formEditType
            // 获取左侧组件对象
            let _field = optionData.find(j => j.optionId === _formEditType)
            // 获取组件对象的表单项类名
            let _fieldClass = _field && _field.attrClass
            if (!_fieldClass) {
                _fieldClass = FormClass.Text
                console.error(`找不到表单项-字段类，类名为:${_formEditType}`)
            }
            // 当添加一个表单项字段的时候，先构造一个隐藏的组别字段
            if (!groupNames.includes(i.groupName)) {
                // 添加组别字段
                let _groupField = new FormClass.Group({
                    ...this.getGroupObj(i)
                }).list
                // 设置组别字段的name名
                this.setFieldName(_groupField)
                // 添加组别字段
                _result.push(_groupField)
                // 在已有的组别名中添加当前组别名
                groupNames.push(i.groupName)
            }
            let _item = {
                ...i,
                ...(i.customOptions && JSON.parse(i.customOptions))
            }
            // 添加表单项字段
            let _fieldAttr = new _fieldClass({
                ...this._fieldClassOption,
                ..._item,
            }).list
            /******* start-组装customOption ********/
            // 传入的customOptions中含有部分有效值，取出表单上显示的字段值后，剩下的才是应该放在customOptions中的
            // 获取customOptions字段(字符串)
            let customOptionsField = _fieldAttr.find(i => i.name === 'customOptions')
            // 获取customOption对象
            let customOptionObj = i.customOptions && JSON.parse(i.customOptions)
            // 获取表单项的属性数组生成的对象
            let fieldObj = _fieldAttr.reduce((res, item) => {
                res[item.name] = item.value
                return res
            }, {})
            // 对比fieldObj，获取customOptionObj中无法匹配的项，组成对象返回
            let _restObj = Get_Rest_Obj(customOptionObj, fieldObj)
            _restObj = (_restObj && Object.keys(_restObj).length > 0) ? JSON.stringify(_restObj) : null
            // 更新customOptions值
            customOptionsField.value = _restObj
            /******* end-组装customOption ********/

            // 设置options数据
            this.setFieldOption(_fieldAttr)
            _result.push(_fieldAttr)
        })
    } else {
        let _fieldList = fieldList.map(i => i.reduce((res, item) => {
            let { name, value } = item
            res[name] = value
            return res
        }), {})
        // 去除字段中的过渡无用字段
        _result = Delete_Transition_Field(Filter_Place_Field(_fieldList))
    }
    return _result
}

/**
 * 过滤组别字段
 * @param {Array} list 表单所有字段数组
 * @returns 过滤后的表单字段数组
 */
function Filter_Place_Field (list) {
    return list.filter(i => !i[Setting.Place])
}

/**
 * 获取是否展示的状态
 * @param {Object} itemObj 操作项
 * @returns 
 */
function Show_Status (itemObj) {
    if (itemObj.hasOwnProperty('hidden') && itemObj['hidden']) return false
    if (itemObj.type === 'hidden') return false
    return true
}

// import { Get_Whole_FieldData } from '@/components/frame/FormDesigner/service/index'
export {
    // 单表单json（字段为对象）与复杂表单json（字段为数组）的相互转化
    SimpleForm_To_WholeForm,
    Delete_Transition_Field,
    // 过滤组别字段
    Filter_Place_Field,
    // 获取是否展示的状态
    Show_Status,
}