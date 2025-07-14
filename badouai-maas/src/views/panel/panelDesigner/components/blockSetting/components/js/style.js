/*
 * @LastEditTime: 2021-04-22 16:16:31
 * @Description: 可视化面板 - 块设置 - 样式设置
 */
import { Has_Value } from '@/utils'
// 动态可变化的表单
let temp_dynamicForm = {
    flgShowTitleBar: {
        all: [],
        data: {
            '0': [],
            '1': ['blockIcon', 'blockTitleColor', 'blockTitleFontColor']
        }
    },
    flgShowBorder: {
        all: [],
        data: {
            '0': [],
            '1': ['borderType', 'blockBorderColor']
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
export default {
    beforeRender: function () {
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
            // hidden 的表单不参与，原因在于可能一个表单在多个配置中都可切换显示与隐藏，如果不做现在，其他配置的表单会影响当前配置
            if (o.type !== 'hidden') {
                this.customSetting.fieldChange.call(this, o.name, o.value)
            }
        })
    },
    /**
     * 字段自定义change事件
     * @param {*} fielaName 字段名
     * @param {*} value change后对应值
     */
    fieldChange: function (fielaName, value) {
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