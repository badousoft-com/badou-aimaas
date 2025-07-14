/*
 * @Description: 可视化面板 - 块设置 - 内容设置
 */
import { Has_Value } from '@/utils'
import { Down_File_Url, Get_Report_Setting, Upload_File } from '@/api/frame/panel/designer'
import { Update_FieldList_Value } from '@/service/module'
import { Is_Array } from '@/utils/data-type'
import GlobalConst from '@/service/global-const.js'
// 动态可变化的表单
let temp_dynamicForm = {
    // 数据源类型
    dataSourceType: {
        all: [],
        data: { // dataSourceType 为对应值时，才会出现的表单
            '0': ['type', 'reportName', 'reportId', 'staticsFieldName', 'yAxisUnit', 'yDisplayName', 'yFieldName', 'xDisplayName', 'xFieldName'], // 报表
            '1': ['pageId', 'pageURL', 'pageUrl'], // 页面
            '2': ['filterReportId', 'type'], // 过滤器
            '4': ['videoURL', 'videoUrl', 'videoWidth', 'videoHeight'], // 视频
        }
    },
    // pageId: {
    //     all: [],
    //     data: {
    //         custom: []
    //     }
    // },
    // 报表类型
    type: {
        all: [],
        data: {
            '5': ['mapType', 'yAxisUnit'],
        }
    },
    // 是否展示更多
    __block__showMore: {
        all: [],
        data: {
            '1': ['__block__moreUrl'],
            'true': ['__block__moreUrl'],
        }
    }
}
let getDynamicForm = function () {
    let result = temp_dynamicForm
    Object.keys(result).forEach(field_key => {
        let field_dic = result[field_key]
        let all = []
        Object.keys(field_dic.data).forEach(data_key => {
            let temp = field_dic.data[data_key]
            all.push(...temp)
        })
        field_dic.all = Array.from(new Set(all))
    })
    return result
}
let dynamicForm = getDynamicForm()
/**
 * @description: 请求选择报表后，应显示的表单数据
 * @param {Object} params：请求动态表单传参
 * @param {Object} value：表单值
 * @return {Array} 表单数组
 */
let loadReportSetting = function (params, value = {}) {
    return new Promise((resolve, reject) => {
        Get_Report_Setting(params).then(res => {
            if (res.hasOk) {
                let form_list = res.bean || []
                let temp_form = form_list.map(o => {
                    let item = {
                        ...o,
                        name: `!diy!${o.dimensionCode}!diy!`,   // 表单字段名，加入特殊符号，防止与其他字段重复
                        realName: o.dimensionCode,              // 真实的表单字段名
                        label: o.dimensionName,                 // 表单label
                        value: value[o.dimensionCode] || '',    // 表单字段值
                        groupName: '内容信息',                  // 组别名称
                        customOptions: JSON.stringify({ tabId: 'main_content' })    // 自定义配置
                    }
                    switch (o.type) {
                        case 'select':
                        case 'multi_select':
                            item.options = o.contents.map(i => {
                                return { id: i.fieldName, text: i.displayName }
                            })
                            item._type = item.type
                            item.multiple = o.type === 'multi_select' && !item.maxSelectCount
                            item.type = 'select'
                            break
                        default:
                            item.type = 'text'
                        // 默认类型为输入框
                    }
                    return item
                })
                resolve(temp_form)
            } else {
                // this.$message.warning('请求表单失败')
                resolve([])
            }
        }).catch(err => {
            // this.$message.warning('请求表单失败')
            resolve([])
        })
    })
}
/**
 * @description: 根据某表单的（reportDynamicForm里面的表单）的值与参数，给reportDynamicForm增加或减少表单（过滤器用）
 * @param {String} fielaName：依据的表单name
 * @param {String} fielaValue：依据的表单value
 * @param {Array} reportDynamicForm：动态表单数组
 * @param {Object} value：表单初始值对象
 * @return {Array}：更改后的表单数组
 */
let setFilterForm = function (fielaName, fielaValue, reportDynamicForm = [], value = {}) {
    // 如果是请求后台接口渲染的动态表单中的数据更改，并且报表类型为过滤器
    let dataSourceType = (this.fieldList.find(o => o.name === 'dataSourceType'))?.value || ''
    // 判断更改表单是否为 reportDynamicForm 中的表单
    let formItem = reportDynamicForm.find(o => o.name === fielaName)
    // 更改表单为动态报表、报表类型为过滤器
    if (formItem && dataSourceType === '2') {
        // 后台返回回来的类型为 multi_select，需要将value作为一个表单添加进 reportDynamicForm 中
        if (formItem._type === 'multi_select') {
            // 获取选中值
            let value_arr = Is_Array(fielaValue) ? fielaValue : []
            // 选中值可能为逗号分割的字符串，需要将其转化成数组
            if (typeof fielaValue === 'string') {
                value_arr = fielaValue.split(',')
            }
            value_arr = value_arr.map(v => {
                return /\!diy\!(.+)\!diy\!/.test(v) ? v : `!diy!${v}!diy!`
            })
            // 循环数据，判断是否添加或删除表单
            formItem.options.forEach(i => {
                let diyName = `!diy!${i.id}!diy!`
                // 判断用动态表单是否已展示此条数据
                let index = reportDynamicForm.findIndex(r => r.name === diyName)
                // 判断此条数据是否 应为 需展示的数据
                let s_index = value_arr.indexOf(diyName)
                if (~index) { // 动态表单已有此条数据
                    if (!~s_index) { // 此条数据应不展示
                        // 移除此数据表单
                        reportDynamicForm.splice(index, 1)
                    }
                } else {  // 动态表单未有此条数据
                    if (~s_index) { // 此条数据应展示
                        reportDynamicForm.push({ // 添加数据
                            name: diyName,
                            realName: i.id,
                            label: i.text,
                            type: 'select',
                            groupName: '内容信息',
                            value: value[i.id] || '',
                            originField: fielaName,
                            options: [
                                { id: 'sum', text: 'sum' },
                                { id: 'count', text: 'count' }
                            ]
                        })
                    }
                }
            })
        }
    }
    return reportDynamicForm
}
// 设置内容动态表单
// isDefault：处于初始化状态，并非实际的用户操作表单值更改
let setDynamicForm = async function (isDefault) {
    let dataSourceType = (this.fieldList.find(o => o.name === 'dataSourceType'))?.value || ''
    let reportIndex = this.fieldList.findIndex(o => o.name === 'reportId')
    let typeIndex = this.fieldList.findIndex(o => o.name === 'type')
    let filterReportIdIndex = this.fieldList.findIndex(o => o.name === 'filterReportId')
    let params = {}
    // 判断数据源类型为报表，并且只有数据源类型、报表、报表类型都有值时，才触发请求
    if (dataSourceType === '0' && reportIndex !== -1 && typeIndex !== -1 &&
        this.fieldList[reportIndex].value && this.fieldList[typeIndex].value) {
        // 组装请求参数
        params = {
            dataSourceId: this.fieldList[reportIndex].value,
            dataSourceType: 'report',
            reportShowTypeId: this.fieldList[typeIndex].value
        }
    // 判断数据源类型为过滤器，并且只有数据源类型、过滤器、报表类型都有值时，才触发请求
    } else if (dataSourceType === '2' && filterReportIdIndex !== -1 && typeIndex !== -1 &&
        this.fieldList[filterReportIdIndex].value && this.fieldList[typeIndex].value) {
        // 组装请求参数
        params = {
            dataSourceId: this.fieldList[filterReportIdIndex].value,
            dataSourceType: 'filter',
            reportShowTypeId: this.fieldList[typeIndex].value
        }
    }
    if (JSON.stringify(params) === '{}') {
        // 如果不符合上述（报表或过滤器并该有的值有值），清除动态表单，还原插入index
        this.elseAttrs.fns.set('reportDynamicForm', [])
        this.$parent.insertIndex = 0
    } else {
        // 如果符合条件，需要组装请求接口参数
        let dataDefStr = this.elseAttrs.field.dataDef || this.elseAttrs.field[GlobalConst.panel.contentKey]?.[0]?.dataDef
        let value = {}
        if (isDefault && dataDefStr) {
            let dataDef = JSON.parse(dataDefStr)
            Object.keys(dataDef).forEach(key => {
                value[key] = dataDef[key].map(o => o.oper || o.fieldName).join(',')
            })
        }
        // this.isReady = false
        let insertIndex = Math.max(reportIndex, typeIndex)
        let reportDynamicForm = await loadReportSetting(params, value)
        reportDynamicForm.forEach(item => {
            setFilterForm.call(this, item.name, item.value, reportDynamicForm, value)
        })
        if (typeof this.elseAttrs.fns.set === 'function') {
            this.elseAttrs.fns.set('insertIndex', insertIndex)
            this.elseAttrs.fns.set('reportDynamicForm', reportDynamicForm)
        }
        // this.isReady = true
    }
}
// 已经触发上传的图片UID列表，防止重复上传相同图片
let __uid_list = []
export default {
    beforeRender: function () {
        __uid_list = [] // 初次进入表单，重置__uid_list
        let elseAttrs_field = this.elseAttrs.field || {}
        if (this.elseAttrs.contentType === '0') {
            // 单一内容：设置隐藏或显示表单
            let field = {
                ...elseAttrs_field,
                ...(elseAttrs_field?.[GlobalConst.panel.contentKey]?.[0] || {})
            }
            // 这块有些许问题：要么没有默认值，要么回显（点击编辑后再点击新增）有问题
            // if (Object.keys(field).length) {
            this.fieldList = Update_FieldList_Value(this.fieldList, field, { valuePath: '' })
            // }
        }
        // 给块信息表单回显赋值
        if (Object.keys(elseAttrs_field).length) {
            this.fieldList.filter(o => {
                let customOptions = {}
                if (o.customOptions) {
                    customOptions = JSON.parse(o.customOptions)
                }
                return customOptions.tabId !== 'main_content'
            }).map(o => {
                let name = o.name.replace('__block__', '')
                if (Has_Value(elseAttrs_field[name])) {
                    o.value = String(elseAttrs_field[name])
                }
                return o
            })
        }
        switch (this.elseAttrs.contentType) {
            case '2':  // 多图片
                if (elseAttrs_field[GlobalConst.panel.contentKey]) {  // 图片回显
                    let multiImageField = this.fieldList.find(o => o.name === 'multiImage')
                    let contents = elseAttrs_field[GlobalConst.panel.contentKey]
                    let value = []
                    contents.forEach(o => {
                        if (Has_Value(o.multiImage)) {
                            value.push({ url: Down_File_Url + o.multiImage })
                        }
                    })
                    multiImageField && this.$set(multiImageField, 'value', value)
                }
                break
            case '0': // 单一内容
            case '1': // 组合内容
                let all_default_hidden_form = []  // 所有默认需要隐藏的表单
                Object.keys(dynamicForm).forEach(o => {
                    all_default_hidden_form.push(...dynamicForm[o].all)
                })
                // 第一次循环，将所有需要隐藏的表单的type = 'hidden'
                this.fieldList.forEach((o, o_index) => {
                    if (all_default_hidden_form.indexOf(o.name) !== -1) {
                        if (!o._type) {
                            // 如果是第一次进去 i._type 肯定为undefined，
                            // 但是目前出现了一个bug，多切换几次，_type有值，type为hidden，如果仍执行这个语句，则该表单将永远出不来
                            // 所以暂时先这样子处理
                            o._type = o.type
                        }
                        o.type = 'hidden'
                    }
                })
                this.fieldList.forEach(o => {
                    // 此操作主要含义如：编辑类型选中的是下拉框，则数据字典等表单也需要显示出来
                    // hidden 的表单不参与，原因在于可能一个表单在多个配置中都可切换显示与隐藏，其他hidden配置的表单会参与计算
                    if (o.type !== 'hidden') {
                        // isDefault 参数，说明是否是在初始化时
                        this.customSetting.fieldChange.call(this, o.name, o.value, { isDefault: true })
                    }
                })
                break
            default:
            // 默认
        }
    },
    /**
     * 字段自定义change事件
     * @param {*} fielaName 字段名
     * @param {*} value change后对应值
     */
    fieldChange: async function (fielaName, value, field = {}) {
        //  当前更改值为报表类型时，需要根据选择的报表类型，请求对应接口，加载出对应报表的分类、系列等表单信息
        //  需要请求的有三个条件：
        // 1、数据源类型dataSourceType为报表'0'或过滤器'2'
        // 2、报表reportId有值（报表）
        // 3、报表类型type有值
        // 4、过滤器名称/过滤器报表filterReportId有值（过滤器）
        switch (fielaName) {
            // 所以当这三个参数进行更改时，都需要选择隐藏或显示表单
            case 'dataSourceType':
            case 'reportId':
            case 'filterReportId':
            case 'type':
                // isDefault 为true，说明处于初始化设值状态，并非实际的用户操作表单更改
                setDynamicForm.call(this, Boolean(field.isDefault))
                break
            case 'multiImage':  // 多图时，需要逐个上传图片
                let changeValue = this.elseAttrs.fns.set
                changeValue && changeValue('isUploading', true)
                for (let i = 0; i < value.length; i++) {
                    let item = value[i]
                    if (!item.attachId && item.raw && __uid_list.indexOf(item.uid) === -1) {
                        __uid_list.push(item.uid)
                        let img_res = await Upload_File({ file: item.raw })
                        if (img_res.attachId) {
                            item.attachId = img_res.attachId
                            item.raw.attachId = img_res.attachId
                        }
                    }
                }
                changeValue && changeValue('isUploading', false)
                break
            default:
                // 获取后台渲染动态表单
                let reportDynamicForm = this.elseAttrs.fns.get('reportDynamicForm') || []
                reportDynamicForm = setFilterForm.call(this, fielaName, value, reportDynamicForm, {})
                this.elseAttrs.fns.set('reportDynamicForm', reportDynamicForm)
        }
        // 判断数据字典内是否存在该字段，存在：说明需要设置表单回显与否
        if (Has_Value(dynamicForm[fielaName])) {
            // 改字段下需要展示/隐藏的表单id
            let allList = dynamicForm[fielaName].all
            // 该字段下需要展示的表单id
            let showList = dynamicForm[fielaName].data[value] || []
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
    },
}