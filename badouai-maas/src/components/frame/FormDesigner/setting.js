import { Get_List_By_TwoDimen } from '@/utils/list.js'

const Name_IsNotFormItem = 'isNotFormItem'
// 定义表单项编辑类型的字段键名
const Name_EditType = 'formEditType'
// 定义日期子类型键名
const Name_DateType = 'dateType'
// 定义使用提示键名
const Name_UseTip = 'useTip'
// 定义字段属性数组的键名
const Attr_Name = 'list'

const Name_Field = {
    // 值字段
    value: 'value',
    // 禁止
    disabled: 'disabled',
    // 预输入文本
    placeholder: 'placeholder',
    setting: 'setting',
    // 展示格式字段
    showFormat: 'showFormat',
    // 值格式字段
    valueFormat: 'valueFormat',
    // 是否单独一行
    isOneLine: 'isOneLine',
    // 是否密码
    isPw: 'isPassword',
    // 文本专用【注意跟下面的单词是两个】
    dataType: 'dataType',
     // 日期专用
    dateType: 'dateType',
    // 多选
    multiple: 'multiple',
    // 禁用多选
    multipleDisabled: 'multipleDisabled',
    // 标识隐藏的属性
    hidden: 'hidden',
    // 隐藏属性是否禁用
    hiddenDisabled: 'hiddenDisabled',
    // 检验规则
    rulesOption: 'rulesOption',

    // 数据来源
    dataSource: 'dataSource',
    // 数据字典/模型编码
    dicName: 'dicName',
    // options字段
    options: 'options',
    // option的实际数组值在请求响应数据res中的路径
    optionResPath: 'optionResPath',
    // 数据来源bean
    dataBean: 'dataBean',
    // 数据来源bean方法
    dataFunction: 'dataFunction',
    // 模型编码
    mdCode: 'mdCode'
}
// 定义左侧组件项group组名
const CATEGORY = {
    base: '元件库',
    layout: '布局字段',
    high: '高级字段',
    frame: '框架专属'
}
// 定义左侧组件区数据
const Form_Option_Data_TwoDimen = [
    {
        category: CATEGORY.base,
        children: [
            { optionId: 'text', icon: 'text-d', attrClass: null, attrClassName: 'Text', label: '单行文本', defaultAttr: {[Name_Field.isPw]: false } },
            { optionId: 'textarea', icon: 'textarea-d', attrClass: null, attrClassName: 'Textarea', label: '多行文本', defaultAttr: {[Name_Field.isOneLine]: true} },
            { optionId: 'password', icon: 'password-d', attrClass: null, attrClassName: 'Password', label: '密码框', defaultAttr: {} },
            { optionId: 'int', icon: 'int-d', attrClass: null, attrClassName: 'Int', label: '整数', defaultAttr: {[Name_Field.isPw]: false }  },
            { optionId: 'number', icon: 'number-d', attrClass: null, attrClassName: 'Number', label: '数字', defaultAttr: {[Name_Field.isPw]: false} },
            { optionId: 'hidden', icon: 'hide', attrClass: null, attrClassName: 'Hidden', label: '隐藏项', defaultAttr: {} },
            { optionId: 'switch', icon: 'switch-fill-d', attrClass: null, attrClassName: 'Switch', label: '开关', defaultAttr: {} },
            { optionId: 'radio', icon: 'radio-d', attrClass: null, attrClassName: 'Radio', label: '单选框', defaultAttr: {[Name_Field.isOneLine]: true} },
            { optionId: 'checkbox', icon: 'checkbox-d', attrClass: null, attrClassName: 'Checkbox', label: '复选框', defaultAttr: {[Name_Field.isOneLine]: true} },
            { optionId: 'select', icon: 'select-d', attrClass: null, attrClassName: 'Select', label: '下拉框', defaultAttr: {} },
            { optionId: 'autoComplete', icon: 'select-d', attrClass: null, attrClassName: 'AutoComplete', label: '文本联想', defaultAttr: {} },
            { optionId: 'date', icon: 'date-d', attrClass: null, attrClassName: 'Date', label: '日期', defaultAttr: {} },
            { optionId: 'time', icon: 'time-d', attrClass: null, attrClassName: 'Time', label: '时间', defaultAttr: {} },
            { optionId: 'datetime', icon: 'time-d', attrClass: null, attrClassName: 'DateTime', label: '日期时间', defaultAttr: {} },
            { optionId: 'year', icon: 'year2-d', attrClass: null, attrClassName: 'Year', label: '年', defaultAttr: {} },
            { optionId: 'month', icon: 'month2-d', attrClass: null, attrClassName: 'Month', label: '月', defaultAttr: {} },
            { optionId: 'cascader', icon: 'textarea-d', attrClass: null, attrClassName: 'Cascader', label: '级联框', defaultAttr: {[Name_Field.multiple]: false} },
            { optionId: 'imagePicker', icon: 'img-d', attrClass: null, attrClassName: 'Img', label: '图片', defaultAttr: {[Name_Field.isOneLine]: true} },
            { optionId: 'attach', icon: 'attach-d', attrClass: null, attrClassName: 'Attach', label: '附件', defaultAttr: {[Name_Field.isOneLine]: true} },
            { optionId: 'richText', icon: 'richtext-d', attrClass: null, attrClassName: 'RichText', label: '富文本', defaultAttr: {[Name_Field.isOneLine]: true} },
            { optionId: 'addressPicker', icon: 'provinces-d', attrClass: null, attrClassName: 'AddressPicker', label: '省市区', defaultAttr: {} },
            { optionId: 'roadmap', icon: 'text-d', attrClass: null, attrClassName: 'Roadmap', label: '路径图', defaultAttr: {} },
        ]
    }, {
        category: CATEGORY.layout,
        children: [
            { optionId: 'group', icon: 'group-d', attrClass: null, attrClassName: 'Group', label: '组别' , [Name_IsNotFormItem]: true, waitOpen: false, tip: '目前只支持【点击】实现添加组别，暂不支持拖拽生成' },
            { optionId: 'column', icon: 'grid-d', attrClass: null, attrClassName: 'Column', label: '栅格' , [Name_IsNotFormItem]: true, waitOpen: true },
        ]
    }, {
        category: CATEGORY.high,
        children: [
            { optionId: 'colorPicker', icon: 'colorPicker-d', attrClass: null, attrClassName: 'ColorPicker', label: '颜色选择', defaultAttr: {[Name_Field.isOneLine]: true} },
            // { optionId: 'rate', icon: 'rate-d', attrClass: null, attrClassName: 'Rate', label: '评分', defaultAttr: {} },
            { optionId: 'daterange', icon: 'dateRange-d', attrClass: null, attrClassName: 'Daterange', label: '日期范围', defaultAttr: {} },
            { optionId: 'datetimerange', icon: 'timeRange-d', attrClass: null, attrClassName: 'DateTimerange', label: '日期时间范围', defaultAttr: {} },
            { optionId: 'code', icon: 'code-d', attrClass: null, attrClassName: 'Code', label: '代码块', defaultAttr: {[Name_Field.isOneLine]: true} },
            { optionId: 'iconPicker', icon: 'iconPicker-d', attrClass: null, attrClassName: 'IconPicker', label: '图标选择器', defaultAttr: {} },
            { optionId: 'pathPicker', icon: 'pagePicker-d', attrClass: null, attrClassName: 'PathPicker', label: '页面选择器', defaultAttr: {} },
        ]
    }, {
        category: CATEGORY.frame,
        children: [
            { optionId: 'dialogList', icon: 'dialogList-d', attrClass: null, attrClassName: 'DialogList', label: '弹窗选列表', defaultAttr: {} },
            { optionId: 'addressBook', icon: 'staffAddressBook-d', attrClass: null, attrClassName: 'AddressBook', label: '地址本', defaultAttr: {} },
            // { optionId: 'staffAddressBook', icon: 'staffAddressBook-d', attrClass: null, attrClassName: 'StaffAddressBook', label: '人员地址本' },
            // { optionId: 'departAddressBook', icon: 'departAddressBook-d', attrClass: null, attrClassName: 'DepartAddressBook', label: '部门地址本' },
        ]
    }  
]

// 定义日期类型数据
const Date_Type_Option = [
    { type: 'date', id: 'date', text: '日期', showFormat: 'yyyy-MM-dd', valueFormat: 'yyyy-MM-dd' },
    { type: 'date', id: 'year', text: '年', showFormat: 'yyyy年', valueFormat: 'yyyy' },
    { type: 'date', id: 'month', text: '月', showFormat: 'yyyy年MM月', valueFormat: 'yyyy-MM' },
    { type: 'date', id: 'dates', text: '多日期', showFormat: 'yyyy-MM-dd', valueFormat: 'yyyy-MM-dd' },
    { type: 'date', id: 'week', text: '周', valueFormat: 'yyyy-MM-dd' },
    { type: 'daterange', id: 'daterange', text: '日期范围', showFormat: 'yyyy-MM-dd', valueFormat: 'yyyy-MM-dd' },
    // { type: 'date', id: 'monthrange', text: '月范围', showFormat: 'yyyy年MM月', valueFormat: 'yyyy-MM' },
    { type: 'time', id: 'time', text: '时间', showFormat: 'HH:mm:ss', valueFormat: 'HH:mm:ss' },
    { type: 'datetime', id: 'datetime', text: '日期时间', showFormat: 'yyyy-MM-dd HH:mm:ss', valueFormat: 'yyyy-MM-dd HH:mm:ss' },
    { type: 'datetime', id: 'datetimerange', text: '日期时间范围', showFormat: 'yyyy-MM-dd HH:mm:ss', valueFormat: 'yyyy-MM-dd HH:mm:ss' },
]

// 定义变量
export default {
    // 占位字段，不显示在页面，目前用于在组别中
    Place: 'place',
    // 组别属性的键名
    IsGroup: 'isGroup',
    // 组别id的键名
    GroupId: 'groupId',
    // 定义校验规则键名
    Rules: {
        required: '必填',
        numeric: '整数',
        decimal: '小数',
        email: '邮箱',
        mobileNum: '手机号码',
        telephoneNum: '座机号码',
        phoneNum: '电话号码',
        IDCard: '身份证',
    },
    // 键名名称：是否为表单项
    Name_IsNotFormItem,
    // 表单项编辑类型的字段键名
    Name_EditType,
    // 定义使用提示键名
    Name_UseTip,
    // 定义日期子类型键名
    Name_DateType,
    // 字段键名对象
    Name_Field,
    // 定义字段属性数组的键名
    Attr_Name,
    // 表单设计器可操作项数据
    Form_Option_Data: Get_List_By_TwoDimen(Form_Option_Data_TwoDimen),
    // 日期类型数据
    Date_Type_Option,
}

// import Setting from '@/components/frame/FormDesigner/setting'