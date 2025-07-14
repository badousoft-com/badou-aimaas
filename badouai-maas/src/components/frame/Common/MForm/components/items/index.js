import Text from './Text'
import Textarea from './Textarea'
import Radio from './Radio'
import Checkbox from './Checkbox'
import Select from './Select'
import Date from './Date/index'
import RichText from './RichText/index.vue'
import File from './File' // 待废弃
import Attach from './File/Attach/index'
import ImagePicker from './File/ImagePicker/index'
import AutoComplete from './AutoComplete'
import Cascader from './Cascader'
import AddressPicker from './AddressPicker/index'
import ColorPicker from './ColorPicker'
import Number from './Number'
import Switch from './Switch'
import Code from './Code'
import CodeMirror from './CodeMirror'
import IconPicker from './IconPicker'
import PathPicker from './PathPicker'
import AddressBook from './AddressBook'
import DialogList from './DialogList'
import Roadmap from './Roadmap'
import Rate from './Rate'
export {
    // 兼容旧版 --start
    // 待废弃***************************
    /**/ Text as FormText,
    /**/ Textarea as FormTextarea,
    /**/ Radio as FormRadio,
    /**/ Checkbox as FormCheckbox,
    /**/ Select as FormSelect,
    /**/ Date as FormDate,
    /**/ Date as FormDateTime,
    /**/ Date as FormTime,
    /**/ RichText as FormRichText,
    /**/ File as FormFile,
    /**/ Attach as FormAttach,
    /**/ ImagePicker as FormImage,
    /**/ Cascader as FormAutoComplete,
    /**/ Cascader as FormCascader,
    /**/ AddressPicker as FormAddressPicker,
    /**/ ColorPicker as FormColorPicker,
    /**/ Number as FormNumber,
    /**/ Switch as FormSwitch,
    /**/ Code as FormCode,
    /**/ AddressBook as FormAddressBook,
    /**/ Rate as FormRate,
    // 待废弃***************************
    // 兼容旧版 --end

    // 文本
    Text,
    // 文本域
    Textarea,
    // 单选框
    Radio,
    // 复选框
    Checkbox,
    // 下拉选择框
    Select,
    // 日期
    Date,
    // 富文本
    RichText,
    // 文件---待废弃
    File,
    // 附件
    Attach,
    // 图片
    ImagePicker,
    // 级联
    Cascader,
    // 地址选择
    AddressPicker,
    // 联想输入补齐
    AutoComplete,
    // 颜色选择
    ColorPicker,
    // 数字
    Number,
    // 开关
    Switch,
    // 代码(json)
    Code,
    // 代码(sql,python,java)
    CodeMirror,
    // 图标选择
    IconPicker,
    // 页面路径选择器
    PathPicker,
    // 地址本
    AddressBook,
    // 弹窗列表选择
    DialogList,
    // 路线
    Roadmap,
    // 评分
    Rate,
}