// 表单项-【起始-终止】类型的逻辑处理，例如时间段
//     0. 通常，一个表单项组件对应一个表单项字段，现在要两个字段对应一个表单组件，需要处理如下
//     1. 这类逻辑：使用两个字段维护，组合起来用于表单使用，提交时拆分出来再分别赋值提交
//     2. 设计器配置中会为【初始字段A】配置搭配的【终止字段】，用于表单显示【A至B】最终展示
//     3. 配置的关联字段为【endFieldName】，值指向终止的表单项字段
//         [
//             { name:'startTime', endFieldName:'endTime', value: ['A'], separator: ',' },
//             { name:'endTime', value:'B'}
//         ]
//     4. 表单回显时，将两个值合并赋给初始字段，将终止字段隐藏不显示页面，如下
//         [
//             { name:'startTime', endFieldName:'endTime', value: ['A,B'], separator: ',' },
//             { name:'endTime', value:'B', hidden: true}
//         ]
//     5. 初始字段对象的value会在组件切换值时更新，最终表单提交接口的时候再将值切割出来分别赋值A,B两个字段用于提交
//         例：编辑数据value为'2020-12-10, 2020-12-22'时，将拆分实现如下以提交
//         {
//             startTime: '2020-12-10',
//             endTime: '2020-12-22'
//         }
//     6. value数据格式暂定为Array，数据格式基于此进行逻辑操作


import Vue from 'vue'
import GlobalConst from '@/service/global-const'
export default {
    /**
     * 初始化-展示表单
     * @param {Object} item: 表单项
     * @param {Array} itemList： 表单项列表集合
     */
    initForm: function (item, itemList) {
        // 判断类型中是否含有段标识【endFieldName】，将会指定字段捆绑展示
        let endFieldName = GlobalConst.form.endFieldName
        if (item.hasOwnProperty(endFieldName) && item[endFieldName]) {
            // 获取所有表单项列表中的【终止字段对象】
            let endField = itemList.find(j => j.name === item[endFieldName])
            if (endField) {
                // 情况分析：
                //     1. 日期/日期时间是使用xxxrange表示时间段的，关键标志提取为range，这里处理时间类型为时间段则是在原有dateType字段值拼接range实现
                //     2. 时间是使用属性is-range表示时间段的，这里需要设置字段的属性添加is-range:true
                let dateType = item.dateType
                if (!dateType) return // 设置了结束字段，但是结束类型没有配置为时间类型，则直接return
                if (dateType !== 'time') {
                    // 日期/日期时间的处理走这里
                    let rangeSign = 'range'
                    // 判断dateType中是否含有间隔关键词，没有则自动补齐
                    dateType = ~dateType.indexOf(rangeSign) ? dateType : `${dateType}range`
                    // 更新当前时间为时间段,设计器customoption就可以不用配置
                    Vue.set(item, 'dateType', dateType)
                } else {
                    // 纯时间段的，设置字段属性is-range
                    Vue.set(item, 'is-range', true)
                }
                // 设置终止字段为隐藏属性
                Vue.set(endField, 'type', 'hidden')
                // 删除【终止字段】的组名，由【起始字段】确定【终止字段】所在组
                delete endField.groupName
                if (item.hasOwnProperty('groupName')) {
                    // 要注意：一个表单可以以多个模块展示，为方便后续逻辑，需要将两个字段处理在同个模块（通过组名一致）
                    // 【起始字段】存在组名时，同步更新【终止字段】的组名值，确保两个数据同在一个组，方便后面查找时在同一级列表数据
                    Vue.set(endField, 'groupName', item.groupName)
                }
            }
        }
    },
    /**
     * 编辑回显-处理字段含有终止字段关联（例如时间段）的情况，将起始与终止字段的值合并后赋值给起始字段（展示字段）
     * @param {Object} fieldObj 当前表单字段对象
     * @param {*} value 字段值
     * @param {Object} formDataObj 表单对象（含有所有字段属性），结构为{name:{value:'小明'},sex:{value: '男'}}
     */
    editForm: function (fieldObj, fieldList) {
        let value = fieldObj?.value
        if (!value) return false
        let endFieldName = GlobalConst.form.endFieldName
        if (fieldObj.hasOwnProperty(endFieldName) && fieldObj[endFieldName]) {
            // 获取终止字段对象
            let endField = fieldList.find(i => i.name === fieldObj[endFieldName])
            // 更新值
            fieldObj.value =  [value || '',  endField?.value || ''].filter(i => i).join(GlobalConst.form.valueSeparator)
        }
    },
    /**
     * 结果处理-用于提交前表单对象处理
     * @param {Object} item: 表单项
     * @param {Object} formObject 用于提交接口的最终表单对象
     * @param {Array} fieldList： 表单项列表集合
     */
    resultForm: function (item, formObject, fieldList) {
        // 获取组合标识名称
        let endFieldName = GlobalConst.form.endFieldName
        if (item.hasOwnProperty(endFieldName) && item[endFieldName]) {
            // 接口不允许null与undefined，只能''空字符串使用着
            let [startField, endField] = item.value?.split(GlobalConst.form.valueSeparator) || ['', '']
            // startField：初始值
            // endField：终止值
            // 更新表单项列表中的终止项下的值
            let endFieldObj = fieldList.find(k => k.name === item[endFieldName])
            if (endFieldObj) {
                // 更新字段数组中的字段值
                endFieldObj.value = endField
                // 更新用于提交的字段（结束字段）表单对象值
                formObject[item[endFieldName]] = endField
            }
            // 更新用于提交的字段（开始字段）表单对应的属性值
            formObject[item.name] = startField
            
            return formObject
        }
        return formObject
    }
}