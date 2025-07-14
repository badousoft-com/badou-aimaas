
/*
 * 字段配置数据字典，用于字段更改时，表单隐藏或展示
 * {
 *  所操作的表单id：{
 *          all: []，存放所有需要展示的表单id，即下面的数组，所有可能出现的值
 *          data: {
 *                  下拉框可能的值：[]，// 需要展示的表单id
 *                  }
 *      }
 * }
 */
let temp_dynamicForm = {
    // 列表展示类型
    showType: {
        all: [],
        data: {
            // 文本
            text: [],
            // 数据字典值
            dic: ['dataSource', 'dicName'],
            // 日期
            date: [],
            // 时间
            dateTime: [],
            // 金额
            money: [],
            // 附件
            attach: [],
            // 百分比
            percent: [],
            // 单图
            imgSingle: [],
            // 多图片
            img: [],
            // 时间(精确到分钟)
            minute: [],
            // 富文本
            richText: [],
            // 关联表记录数
            count: ['moduleLinkId'],
            // 模型关联字段
            linkModule: []
        }
    },
    // 编辑类型
    editType: {
        all: [],
        data: {
            text: [],
            select: ['dataSource', 'dicName', 'dataBean', 'dataFunction', 'valueFieldId', 'valueFieldText', 'valueFieldIdSrc', 'valueFieldTextSrc'],
            autoComplete: ['dataSource', 'dicName', 'dataBean', 'dataFunction'],
            date: [], // 日期
            dateRange: [], // 时间段
            password: [],
            checkbox: [], // 开关、单复选框
            radio: ['dataSource', 'dicName', 'dataBean', 'dataFunction', 'valueFieldId', 'valueFieldText', 'valueFieldIdSrc', 'valueFieldTextSrc'],
            hidden: [],
            checkboxlist: ['dataSource', 'dicName', 'dataBean', 'dataFunction', 'valueFieldId', 'valueFieldText', 'valueFieldIdSrc', 'valueFieldTextSrc'], // 复选框
            readonly: [], // 不可编辑文本框
            textarea: [],
            cascader: ['valueFieldId', 'valueFieldText'],
            addressPicker: ['valueFieldId', 'valueFieldText'],
            attach: ['valueFieldId', 'valueFieldText', 'valueFieldIdSrc', 'valueFieldTextSrc', 'attachTemplateId'], // 单附件
            attachMulti: ['attachTemplateId'], // 多附件
            addressbook: ['dataSource', 'dicName', 'dataBean', 'dataFunction', 'addressType', 'valueFieldId', 'valueFieldText', 'valueFieldIdSrc', 'valueFieldTextSrc'],
            img: [], // 多图片
            selectListData: ['dataSource', 'dicName', 'dataBean', 'dataFunction', 'valueFieldId', 'valueFieldText', 'valueFieldIdSrc', 'valueFieldTextSrc'], // 弹出框列表单选
            selectListDataMulti: ['dataSource', 'dicName', 'dataBean', 'dataFunction', 'valueFieldId', 'valueFieldText', 'valueFieldIdSrc', 'valueFieldTextSrc'], // 弹出框列表多选
            year: [],
            imgSingle: [],
            'color-single': [],
            richText: [],
        }
    }
}
// 给 temp_dynamicForm 组装 all
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
module.exports = {
    dynamicForm: getDynamicForm(),
    // 其他类型的数据字典
    otherDic: {
        searchType: [
            {
                id: '0',
                text: '下拉多选框',
                componentName: 'search-multiple-select',
                value: '{"value":null,"defaultText":"全部","isExtended":false,"selectType":0}'
            }, {
                id: '1',
                text: '模糊搜索框',
                componentName: 'search-text',
                value: '{"value":"","isExtended":false,"selectType":1}'
            }, {
                id: '5',
                text: '时间选择器',
                componentName: 'search-date',
                value: '{"value":"","defaultText":"全部","isExtended":false,"selectType":5,"formate":"yyyy-MM-dd","afterTime":null,"beforeTime":null,"startTime":null,"endTime":null,"type":0,"measurement":null}'
            }, {
                id: '6',
                text: '数字区间',
                componentName: 'search-num-range',
                value: '{"value":null,"minNum":null,"maxNum":null,"defaultText":"全部","isExtended":false,"selectType":6}'
            }, {
                id: '9',
                text: '下拉单选框',
                componentName: 'search-single-select',
                value: '{"value":null,"defaultText":"全部","isExtended":false,"selectType":9}'
            }, {
                id: '11',
                text: '年选择器',
                componentName: 'search-year',
                value: '{"value":null,"isExtended":false,"selectType":11}'
            }, {
                id: '12',
                text: '年月选择器',
                componentName: 'search-year-month',
                value: '{"value":null,"isExtended":false,"selectType":12}'
            }, {
                id: '15',
                text: '复选框',
                componentName: 'search-checkbox',
                value: '{"value":null,"isExtended":false,"selectType":15,"width":"100%"}'
            }
        ]
    }
}
