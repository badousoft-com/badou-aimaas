// 引入字段配置数据字典，用于字段更改时，表单隐藏或展示
import { dynamicForm, otherDic } from '@/views/common/modelDesigner/fieldDic'
import { Has_Value } from '@/utils'
export default {
    columnNum: 1,
    beforeRender: function () {
        if (!(this.mdCode)) return false
        // 获取当前查看配置模块id
        let _belong = this.elseAttrs?.belong || ''
        this.fieldList.forEach(i => {
            // 为字段对象添加属性_type存储原表单类型type
            // 将type全部变更值为hidden隐藏
            if (!i._type) {
                // 如果是第一次进去 i._type 肯定为undefined，
                // 但是目前出现了一个bug，多切换几次，_type有值，type为hidden，如果仍执行这个语句，则该表单将永远出不来
                // 所以暂时先这样子处理
                i._type = i.type
            }
            i.type = 'hidden'
            let attrName = 'customOptions'
            if (i.hasOwnProperty(attrName)) {
                // 获取用户自定义字段属性对象
                let customOptions = i[attrName] && JSON.parse(i[attrName])
                // 将匹配的belong配置项还原type属性，让字段显示页面
                // customOptions.belong值可能为'1', 或者 '1,2'表示能被多模块展示
                if (customOptions &&
                    customOptions.belong &&
                    customOptions.belong.includes(_belong)) {
                    i.type = i._type
                }
            }
        })
        // 此操作主要含义如：编辑类型选中的是下拉框，则数据字典等表单也需要显示出来
        // hidden 的表单不参与，原因在于可能一个表单在多个配置中都可切换显示与隐藏
        //                     如果不做现在，其他配置的表单会影响当前配置
        this.fieldList.forEach(o => {
            let {
                name: fieldName,  // 获取字段键名fieldName
                value, // 字段对应值
                type, // 表单项类型
            } = o
            if (fieldName === 'searchType') {
                let currentField = this.fieldList.find(j => j.name === 'searchBox') || {}
                !currentField.value && updateSearchBoxByType.call(this, String(value))
            } else if (fieldName !== 'searchBox' && type !== 'hidden') {
                // 根据当前操作的字段键，动态切换其相关关联字段的展示与隐藏（例如选择编辑为下拉时，出现可选数据来源的字段）
                updateFieldShowStatus.call(this, fieldName, value)
            }
        })
    },
    /**
     * 字段自定义change事件
     * @param {*} fieldName 字段名
     * @param {*} value change后对应值
     */
    fieldChange: function (fieldName, value) {
        // 根据当前操作的字段键，动态切换其相关关联字段的展示与隐藏（例如选择编辑为下拉时，出现可选数据来源的字段）
        updateFieldShowStatus.call(this, fieldName, value)
        // 除动态表单外的额外变化
        switch (fieldName) {
            // 搜索类型
            case 'searchType':
                updateSearchBoxByType.call(this, value)
                break
            // 编辑类型
            case 'editType':
                // 当选择编辑类型为指定类型时，设置默认选中独占一行
                let typeList = [ 'textarea', 'richText', 'radio']
                // 获取独占一行字段对象
                let isFullLineField = this.fieldList.find(j => j.name === 'isFullLine')
                // 判断当前选中值是否为指定值
                if (~typeList.findIndex(i => i === value)) {
                    // 设置独占一行
                    isFullLineField.value = '1'
                } else {
                    // 取消独占一行设置
                    isFullLineField.value = '0'
                }
                break
            default:
                // do default
        }
        
    }
}

/**
 * 根据当前操作的字段键，动态切换其相关关联字段的展示与隐藏（例如选择编辑为下拉时，出现可选数据来源的字段）
 * @param {String} fieldName 字段键名
 * @param {*} value 字段值
 */
function updateFieldShowStatus (fieldName, value) {
    // 判断数据字典内是否存在该字段，存在：说明需要设置表单回显与否
    if (Has_Value(dynamicForm[fieldName])) {
        // 改字段下需要展示/隐藏的表单id
        let allList = dynamicForm[fieldName].all
        // 该字段下需要展示的表单id
        let showList = dynamicForm[fieldName].data[value] || []
        allList.forEach(i => {
            let currentField = this.fieldList.find(j => j.name === i)
            if (currentField) {
                // 如果该表单id为需要展示的表单id
                if (showList.indexOf(i) > -1) {
                    this.$set(currentField, 'type', currentField._type)
                } else {
                    this.$set(currentField, 'type', 'hidden')
                }
            }
        })
        if (this.$refs[this.refName]) {
            // 需要隐藏的表单
            let hiddenList = allList.filter(o => {
                return !showList.includes(o)
            })
            // 清空错误提示文本
            this.$refs[this.refName].clearValidate(hiddenList)
        }
    }
}

/**
 * 搜索配置：根据所选择的【搜索类型】设置对应的【搜索框定义】
 * @param {String} value 搜索类型 表单值
 */
function updateSearchBoxByType (value) {
    let _dic = otherDic.searchType
    let obj = _dic.find(o => { return o.id === value })
    if (obj) {
        // 找到搜索框定义
        let currentField = this.fieldList.find(j => j.name === 'searchBox')
        let id = this.fieldList.find(j => j.name === 'entityName')?.value || ''
        let name = this.fieldList.find(j => j.name === 'displayName')?.value || ''
        // 根据选择的搜索类型，更改对应的搜索框定义
        let temp_value = {
            id,
            name,
            ...JSON.parse(obj.value),
            // 当前搜索字段同时支持的字段搜索，格式如'name,sex'，输入框输入值将会作为这些字段中（或的关系）一起去查询
            tagName: ''
        }
        currentField.value = JSON.stringify(temp_value)
    }
}