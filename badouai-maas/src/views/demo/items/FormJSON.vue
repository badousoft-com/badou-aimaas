<template>
    <div>
        <el-button @click="showForm = !showForm">切换表单-骨架屏</el-button>
        <!-- <div class="floatR defaultBg" style="position: absolute;right: 0;z-index:2">
            <el-radio-group
                class="padding"
                v-model="formType">
                <el-radio label="edit">编辑模式</el-radio>
                <el-radio label="view">查看模式</el-radio>
            </el-radio-group>
            <el-radio-group
                class="padding"
                v-model="groupType">
                <el-radio label="oneGroup">单组</el-radio>
                <el-radio label="manyGroup">多组</el-radio>
            </el-radio-group>
            <el-button type="primary" @click="$router.go(-1)">返回demo目录</el-button><br>
        </div>  -->
        <!-- 原有数据：{{formHelloList}} -->
        <bd-skeleton form :loading="!showForm">
            <m-form
                ref="formHello"
                id="formHello"
                title="表单标题"
                labelWidth="110px"
                :columnNum="3"
                :dataList="formHelloList"
                :isView="isView"
                @fieldChange="fieldChange">
            </m-form>
        </bd-skeleton>
        <div class="fixBottomBtn">
            <el-button 
                v-for='(i, index) in opBtnList' 
                :key="index"  
                v-btnBg="i"
                type="primary"
                @click='exeMethod(i.method)'>
                <bd-icon :name="i.icon"></bd-icon>
                {{ i.name }}
            </el-button>
        </div>
    </div>
</template>

<script>
// 校验整数函数
let validateInteger = function (rule, value, callback) {
    if (!value) {
        // 不做必填校验
        callback()
    }
    // 若输入为整数，则取整前后一致，以此作为判断
    if (parseInt(value) + '' === value + '') {
        callback()
    } else {
        callback(new Error('请输入整数'))
    }
}
let seePhoneNum = function (rule, value, callback) {
    if (!value) {
        // 不做必填校验
        callback()
    }
    let { isVerify, message } = Verify_Mobile_Num(value)
    if (!isVerify) {
        callback(new Error(message))
    } else {
        callback()
    }
}
let changeFun = function (val) {
    alert(`当前选中值为：${JSON.stringify(val)}`)
    // this: 指向组件所在页面作用域
}
import { Verify_Mobile_Num } from '@/utils/validate/checkConcatNum'
import MForm from '@/components/frame/Common/MForm/index.vue'
import CodeEditor from '@/components/frame/Common/CodeEditor'
import Skeleton from '@/components/frame/Skeleton'
const options = [
    { id: '1', text: '方式一' },
    { id: '2', text: '方式二' },
    { id: '3', text: '方式三' },
    { id: '4', text: '方式四' },
    { id: '5', text: '方式五' }
]
const defaultImgUrl = 'http://ksp.badousoft.com/bd_front_guidebook/images/action.png'
export default {
    components: {
        MForm,
        CodeEditor,
        [Skeleton.name]: Skeleton
    },
    data() {
        return {
            // 表单展示模型：编辑或查看
            formType: 'edit',
            // 组类型
            groupType: 'oneGroup',
            // 是否展示form表单
            showForm: true,
            // 设置字段属性destroyed：true的字段将被视为不纳入最后表单数据
            formHelloList: [
                { type: 'roadmap', label: '路线图1', name: 'roadmap1', value: null},
                { type: 'roadmap', label: '路线图2', name: 'roadmap2', value: 'Rows,dew,dew,dew', columnPer: 24, disabled: true},
                { type: 'select', label: '单选', name: 'radioX1', value: '', optionResPath: 'Rows',idFieldKey: 'id', textFieldKey: 'name',request: {
                    url: '/filter/filtermodule/listJSON.do?mdCode=filter', 
                    params: {}
                }},
                // { type: 'text', name: 'text', label: '文本框', value: '我是一个超级长超级长的测试文本', placeholder: '请输入文本', maxlength: 30, isView: true },
                // { type: 'text', name: 'noLabel', value: null, placeholder: '请输入文本'},
                // { type: 'text', name: 'stringInt', label: '字符串整数', value: 255, placeholder: '请输入整数', rules: [
                //     { validator: validateInteger, trigger: ['blur','change']}
                // ] },
                { type: 'autoComplete', label: '输入建议文本', name: 'autoComplete1', value: null, valueKey: 'text', placeholder: '请输入', options: [
                    { text: '文本一' },
                    { text: '文本一2' },
                    { text: '文本二' },
                    { text: '文本二2' }
                ] },
                { type: 'autoComplete', label: '输入建议(请求版)', name: 'autoComplete2', value: null, valueKey: 'text', placeholder: '请输入', options: [], request: {
                    url: '/common/commoninterface/listDicJSON', 
                    params: {
                        dicCode: 'ill_reason'
                    }
                }},
                // { type: 'text', dataType: 'int', name: 'int', label: '整数', value: 255, placeholder: '请输入整数' },
                // { type: 'text', dataType: 'number', name: 'number', label: '小数', value: 11.11, placeholder: '请输入小数' },
                // { type: 'text', name: 'readonly', label: '只读', value: '测试只读数据', disabled: true },
                // { type: 'text', name: 'password', label: '密码', value: '147258369',isPassword: true },
                // { type: 'textarea', name: 'textarea', label: '文本域', value: '这里是超级无敌宇宙长的文本这是是超级长的宇宙文本好了这就是我最后要说的了' },
                // { type: 'codeMirror', name: 'codeMirror', label: 'sql语句', value: 'select * from', columnPer: 24 },
                // { type: 'iconPicker', name: 'iconPicker', label: '图标选择', value: 'save' },
                // { type: 'pathPicker', name: 'pathPicker', label: '页面路径选择', value: null, addPath: [
                //     { id: '/module/aa' },
                //     { id: 'module/bb' },
                //     { id: '/cc/bb' },
                // ], columnPer: 24 },
                // { type: 'date', name: 'date1', label: '日期', value: '2021-02-22' },
                // { type: 'date', name: 'year', dateType: 'year', label: '年', value: '2022' },
                // { type: 'date', name: 'year1', dateType: 'year', label: '年份', value: '2021', showFormat:'yyyy年哈哈'},
                // { type: 'date', name: 'month', dateType: 'month', label: '月', value: '3' },
                // { type: 'date', name: 'week', dateType: 'week', label: '周', value: '2021-04-05' },
                // { type: 'date', name: 'dates', dateType: 'dates', label: '多日期', value: '2021-04-11,2021-04-14' },
                // { type: 'date', name: 'daterange', dateType: 'daterange', label: '日期范围', value: '2021-04-08,2021-05-04' },
                // { type: 'date', name: 'monthrange', dateType: 'monthrange', label: '月范围', value: '2021-04,2022-01' },
                // { type: 'date', name: 'time', dateType: 'time', label: '时间', value: '12:03:12' },
                // { type: 'date', name: 'datetime', dateType: 'datetime', label: '日期时间', value: '2021-04-09 00:02:00' },
                // { type: 'date', name: 'datetimerange', dateType: 'datetimerange', label: '日期时间范围', value: '2021-04-06 00:00:00,2021-05-03 00:00:00' },
                // // 时间段
                // { type: 'date', name: 'startDate', dateType: 'daterange', label: '拆分两个日期', value: null, endFieldName: "endDate"},
                // { type: 'hidden', name: 'endDate', value: null },
                // { type: 'date', name: 'startDatetime', dateType: 'datetimerange', label: '拆分两个时间', value: null, endFieldName: "endDatetime"},
                // { type: 'hidden', name: 'endDatetime', value: null },

                // { type: 'select', name: 'select', label: '下拉单选', value: '1', placeholder: '请选择', options: options },
                // { type: 'select', name: 'multiSelect', multiple: true, label: '下拉多选', value: '1,2', placeholder: '请选择', options: options },
                
                { type: 'radio', name: 'radio', label: '单选框', value: '1', options: options },
                { type: 'checkbox', name: 'checkbox', label: '复选框', value: '1,2', options: options },
                { type: 'colorPicker', name: 'colorPicker', label: '单选颜色', value: '#CCC' },
                { type: 'colorPicker', name: 'MultiColorPicker', label: '多选颜色', value: '#CCC,#ff4566,rgba(0,0,0,1)', multiple: true,showAlpha:true },
                { type: 'imagePicker', name: 'image', label: '单图', value: [{url:defaultImgUrl}], multiple: false, columnPer: 24 },
                { type: 'imagePicker', name: 'multiImage', label: '多图', value: [{url:defaultImgUrl}, {url:defaultImgUrl}], columnPer: 24 },
                { type: 'attach', name: 'attach', label: '单附件', value: [{name: '火柴人1号', url:defaultImgUrl}], maxSize:'10mb', selfClass: 'aass', multiple: false },
                { type: 'attach', name: 'multiAttach', label: '多附件', value: [{name: '火柴人1号', url:defaultImgUrl, isImg: true},{name: '火柴人2号', url:defaultImgUrl, isImg: true}], multiple: true },

                { type: 'switch', name: 'switch', label: '开关', value: null, activeValue:'1', inactiveValue:'0', activeText: '是', inactiveText: '否'},
                { type: 'addressPicker', name: 'addressPicker', label: '省市区选择', value: '河北省-秦皇岛市-海港区' },
                { type: 'code', name: 'code', label: '代码', value: '{ "aa": 123 }' },
                { type: 'richText', name: 'richText', label: '富文本', value: "<p>哈哈</p><p style=\"text-align: center;\"><strong>哈哈居中</strong></p><p style=\"text-align: right;\"><strong>哈哈最右</strong></p>" },







                { type: 'colorPicker', label: '颜色单选', name: 'color1', value: '',groupName: '哇咔咔' },
                { type: 'colorPicker', label: '颜色多选', name: 'color2', value: '', multiple: true, columnPer: 16 },
                { type: 'colorPicker', label: '颜色多选透明', name: 'color3', value: '', multiple: true, showAlpha: true },
                { type: 'text', label: '这个一个普普通通标签', name: 'common', value: '1222', placeholder: '请输入' },
                { type: 'text', label: '占比2/3标签', name: 'largePer', value: '12', placeholder: '请输入', columnPer: 16 },
                { type: 'text', label: '当标签特别特别长或者内容需要区域大时的时候可以考虑使用这种，让标签与内容各占块', name: 'block1', value: '12122', isBlock: true },
                { type: 'text', label: '当标签特别特别长或者内容需要区域大时的时候，标签页面内容很长，但是不希望输入框的内容全部铺满时。现在输入框占12等份（总24等份）', name: 'blockAndPer', value: '12122', isBlock: true, columnPer: 12 },
                { type: 'text', label: '标签与内容一起独占一行', name: 'oneLine', value: '标签与内容一起独占一行，按照标准标签宽度排版', isOneLine: true },
                { type: 'text', label: '标签与内容一起独占一行(占比)', name: 'oneLine', value: '标签与内容一起独占一行，按照标准标签宽度排版（占比）', isOneLine: true, columnPer: 16 },
                { type: 'text', label: '标签与内容一起独占一行，标签全部展示', name: 'oneLineAsShowAllLabel', value: '标签与内容一起独占一行，标签内容全部展示，输入内容剩余空间自动铺满', isOneLine: true, isShowAllLabel: true },
                { type: 'text', label: '标签与内容一起独占一行, 标签全部展示,只铺满16等份（共24）', name: 'oneLine', value: '标签与内容一起独占一行, 标签全部展示,只铺满16等份（共24）', isOneLine: true, isShowAllLabel: true, columnPer: 16 },
                // 隐藏元素
                { type: 'text', label: '隐藏元素', name: 'hiddenByAttr', value: '1222', hidden: true},
                // isAssociated:是否使用关联字段，添加该属性会将两个关联字段合并处理
                { type: 'select', name: 'optionsOrigin', isAssociated: true, destroyed: true, label: '来源', options: [
                    { type: 'code', name: 'request1', label: '请求', value: { url: '', params: {} } },
                    { type: 'code', name: 'options1', label: '数组', value: [{ id:'1',text: '选项' }] },               
                ]},
                

                
                { type: 'code', label: '代码编辑器', name: 'code', value: '{"aa":11, "bb":{"cc":22,"dd":33}}',lang:'json'},
                { type: 'code', label: 'js编辑器', name: 'jsCode', value: null, lang: 'javascript'},
                { type: 'hidden', label: '隐藏元素', name: 'hidden', value: '1222' },
                { type: 'switch', label: '默认开关', name: 'switch', value: false },
                { type: 'switch', label: '开关', name: 'switch', value: null, activeValue: '1', inactiveValue: '0' },
                { type: 'number', label: '上报数目', name: 'numnber', value: 0, step: 1 },
                { type: 'cascader', label: '级联', name: 'cascader1', value: '', separator: '+', options: [{
                    value: 'zhinan',
                    label: '指南',
                    children: [{
                        value: 'shejiyuanze',
                        label: '设计原则',
                        children: [
                            { value: 'yizhi', label: '一致' }, 
                            { value: 'fankui', label: '反馈'}, 
                            { value: 'xiaolv', label: '效率' }, 
                            { value: 'kekong', label: '可控' }]
                    }, {
                        value: 'daohang',
                        label: '导航',
                        children: [
                            { value: 'cexiangdaohang', label: '侧向导航' }, 
                            { value: 'dingbudaohang', label: '顶部导航' }
                        ]
                    }]
                }, {
                    value: 'zujian',
                    label: '组件',
                    children: [{
                        value: 'basic',
                        label: 'Basic',
                        children: [
                            { value: 'layout', label: 'Layout 布局' }, 
                            { value: 'color', label: 'Color 色彩' }, 
                            { value: 'typography',label: 'Typography 字体'}, 
                            { value: 'icon', label: 'Icon 图标' }, 
                            { value: 'button', label: 'Button 按钮'
                        }]
                    },{
                        value: 'notice',
                        label: 'Notice',
                        children: [{
                            value: 'alert',
                            label: 'Alert 警告'
                        }, {
                            value: 'loading',
                            label: 'Loading 加载'
                        }, {
                            value: 'message',
                            label: 'Message 消息提示'
                        }, {
                            value: 'message-box',
                            label: 'MessageBox 弹框'
                        }, {
                            value: 'notification',
                            label: 'Notification 通知'
                        }]
                    }]
                }, {
                    value: 'ziyuan',
                    label: '资源',
                    children: [{
                        value: 'axure',
                        label: 'Axure Components'
                    }, {
                        value: 'sketch',
                        label: 'Sketch Templates'
                    }, {
                        value: 'jiaohu',
                        label: '组件交互文档'
                    }]
                }] },
                { type: 'addressPicker', label: '省市区', name: 'distPicker1', value: '天津市-天津市-和平区' },
                // { type: 'addressPicker', label: '省市区开关联', name: 'distPicker2', value: '天津市-天津市-和平区', relatedField: true, change: changeFun },
                // { type: 'addressPicker', label: '省市区配关联', name: 'distPicker3', value: '天津市-天津市-和平区', relatedField: 'province11-city11-area11' },
                // // 开启省市区关联字段-默认字段
                // { type: 'hidden', name: 'province', value: '' },
                // { type: 'hidden', name: 'city', value: '' },
                // { type: 'hidden', name: 'area', value: '' },
                // // 开启省市区关联字段-指定字段
                // { type: 'hidden', name: 'province11', value: '' },
                // { type: 'hidden', name: 'city11', value: '' },
                // { type: 'hidden', name: 'area11', value: '' },
                // { type: 'text', label: '文本', name: 'textText', value: '34', placeholder: '请输入内容', maxlength: undefined, rules: [
                //     { required: true, message: `请输入内容`, trigger: 'blur' }
                // ] },
                // { type: 'text', label: '文本-数字', name: 'textInterge', value: 32, dataType: 'number', placeholder: '请输入内容', maxlength: undefined, rules: [
                //     { type: 'integer', message: `请输入整数`, trigger: 'blur' }
                // ] },
                // { type: 'text', label: '密码', name: 'password', value: null, placeholder: '请输入密码', isPassword: true, moduleClass: 'modulePassword', selfClass: 'selfPassword' },
                { type: 'text', disabled: true, label: '不可编辑', name: 'noEdit', value: '这是不可编辑', placeholder: '请输入' },
                // { type: 'text', label: '字数限制', name: 'numLint', value: '12', placeholder: '请输入', maxlength: 10 },
                // { type: 'text', label: '前置元素', name: 'frontEl', value: '12', placeholder: '请输入', prepend: 'Http://' },
                // { type: 'text', label: '后置元素', name: 'endEl', value: '12', placeholder: '请输入', append: '.com' },
                // { type: 'text', label: '必填标签', name: 'requireEl', value: '', placeholder: '请输入', rules: [
                //     { required: true, message: '请输入必填标签', trigger: 'blur' },
                // ] },
                // { type: 'text', label: '最小长度4', name: 'requireElMin', value: '', placeholder: '请输入', rules: [
                //     { required: true, message: '请输入', trigger: 'blur' },
                //     { min: 4, message: '最小长度为4', trigger: 'blur' },
                // ] },
                // { type: 'text', label: '指定长度6', name: 'requireElLen', value: '', placeholder: '请输入', rules: [
                //     { required: true, message: '请输入', trigger: 'blur' },
                //     { min: 6, message: '指定长度为6', trigger: 'blur' },
                // ] },
                // { type: 'text', label: '最大长度8', name: 'requireElMax', value: '', placeholder: '请输入', rules: [
                //     { required: true, message: '请输入', trigger: 'blur' },
                //     { max: 8, message: '最大长度为8', trigger: 'blur' },
                // ] },
                // { type: 'text', label: '指定角色范围内选取内容', name: 'requireRange', value: 'admin', placeholder: '请输入', rules: [
                //     { required: true, message: '请输入', trigger: 'blur' },
                //     { type: 'enum', enum: ['admin', 'user', 'guest'], message: '角色只能在admin/user/guest中选择' }
                // ] },
                // { type: 'text', label: '手机号码检验', name: 'phoneNumVerify', value: '', placeholder: '请输入手机号', rules: [
                //     { validator: seePhoneNum, trigger: ['blur']}
                // ] },
                // { type: 'text', label: '非必填检验', name: 'noReVaEl', value: null, placeholder: '请输入邮箱', rules: [
                //     { type: 'email', message: '请输入邮箱', trigger: 'blur' },
                // ] },
                // { type: 'text', label: '必填带校验', name: 'reVaEl', value: '1065502552@qq.com', placeholder: '请输入邮箱', rules: [
                //     { required: true, message: '必填项', trigger: 'blur' },
                //     { type: 'email', message: '请输入邮箱', trigger: 'blur' }
                // ] }, 
                // { type: 'textarea', label: '文本域', name: 'textarea', value: '12', placeholder: '请输入', maxlength: 40 },
                { type: 'select', label: '下拉单选', name: 'select', value: '1', placeholder: '请选择xxx', options: [
                    { label: '下拉1', value: '1' },
                    { label: '下拉2', value: '2' },
                    { label: '下拉3', value: '3' },
                ] },
                { type: 'select', label: '下拉单选-请求对象', name: 'selectRequest', value: '', placeholder: '请选择', request: {
                    url: '/common/commoninterface/listDicJSON', 
                    params: {
                        dicCode: 'model_manage_type'
                    }
                }},
                // { type: 'select', multiple: true, label: '下拉多选', name: 'multiSelect', value: '1;2', placeholder: '请选择多个', options: [
                //     { value: '1', label: '黄金糕'},
                //     { value: '2', label: '双皮奶'},
                //     { value: '3', label: '蚵仔煎'},
                //     { value: '4', label: '龙须面'},
                //     { value: '5', label: '北京烤鸭'}
                // ]},
                // { type: 'select', multiple: true, label: '下拉多选限2', isCollapse: false, name: 'multiSelect2', value: [], placeholder: '请选择多个', options: [
                //     { value: '1', label: '黄金糕'},
                //     { value: '2', label: '双皮奶'},
                //     { value: '3', label: '蚵仔煎'},
                //     { value: '4', label: '龙须面'},
                //     { value: '5', label: '北京烤鸭'}
                // ]},
                // { type: 'radio', status: 1, label: '单选框', name: 'radio', value: '1', options: [
                //     { text: '单选1', id: '1' },
                //     { text: '单选2', id: '2' },
                //     { text: '单选3', id: '3' },
                // ], columnPer: 12},
                // { type: 'checkbox', label: '复选框', name: 'checkbox', value: ['1'], options: [
                //     { text: '复选1', id: '1' },
                //     { text: '复选2', id: '2' },
                //     { text: '复选3', id: '3' },
                // ], columnPer: 12},

                // { type: 'date', label: '年月日-时分秒', name: 'date3', value: null, placeholder: '请选择年月日-时分秒', columnPer: 8, dateType: 'datetime', showFormat: 'yyyy-MM-dd HH:mm:ss', valueFormat: 'yyyy-MM-dd HH:mm:ss'},
                // { type: 'date', label: '年月日-日期框', name: 'date1', value: null, placeholder: '请选择日期', columnPer: 8, dateType: 'date', showFormat: 'yyyy-MM-dd', valueFormat: 'yyyy-MM-dd'},
                // { type: 'date', label: '年-日期框', name: 'date2', value: null, placeholder: '请选择日期', columnPer: 8, dateType: 'year', showFormat: 'yyyy年', valueFormat: 'yyyy'},
                
                // { type: 'date', label: '选择日期', value: '2020-10-22', name: "_date" },
                // { type: 'time', label: '选择时间', value: '08:50:20', name: '_time' },
                // { type: 'datetime', label: '选择日期时间', value: '2020-10-23 08:50:00', name: '_datetime' },
                // { type: 'datetime', dataType: 'datetimerange', label: '选择日期时间段', value: null, name: '_datetimerange' },
                
                // { type: 'autoComplete', label: '输入建议文本', name: 'autoComplete', value: null, placeholder: '请输入' },
                // { type: 'imagePicker', label: '单图', name: 'image_single', value: null, isOneLine: true },
                // { type: 'attach', label: '附件', name: 'attach', value: [], multiple: true, isOneLine: true,
                //     limit: 3,
                //     value: [
                //         // { name: '可行性研究报告.docx', size: 708732, suffix: '.doc',isImg: false, url: 'https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2182979850,539087046&fm=26&gp=0.jpg'},
                //         // { name: '测试图片1.jpg', size: 42322, suffix: '.png',isImg: true, url: 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1605194782207&di=0ebfb418859a7ff5824c5a3e685e7273&imgtype=0&src=http%3A%2F%2Fattachments.gfan.com%2Fforum%2F201503%2F19%2F211608ztcq7higicydxhsy.jpg'},
                //         // { name: '哇啦啦.xlsx', size: 232423, suffix: '.xlsx',isImg: false, url: 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1605194841844&di=e83312435098a31f0fb2af1a091f0820&imgtype=0&src=http%3A%2F%2Fattach.bbs.miui.com%2Fforum%2F201308%2F23%2F220651x9b0h4kru904ozre.jpg'},
                //         // { name: '展示风采.ppt', size: 4321423, suffix: '.ppt',isImg: false, url: 'https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1588620919,359805583&fm=26&gp=0.jpg'},
                //         // { name: '测试图片2.jpg', size: 46767, suffix: '.jpg',isImg: true, url: 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1605194875517&di=533d0150db580c8546a1d98bab7823be&imgtype=0&src=http%3A%2F%2Fattach.bbs.miui.com%2Fforum%2F201205%2F15%2F152011zser9o5oa9ee9xx6.jpg'},
                //         // { name: '成功展示.pdf', size: 2342435, suffix: '.pdf',isImg: false, url: 'https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3313838802,2768404782&fm=26&gp=0.jpg'},
                //         // { name: '啥也不是', size: 2453, suffix: '.dsa',isImg: false, url: 'https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2759603483,1319363293&fm=26&gp=0.jpg'},
                //     ],
                //     multiple: false
                // },
                // { type: 'imagePicker', label: '多图', name: 'image_multi', value: [], multiple: true, isOneLine: true },
                // { type: 'imagePicker', label: '多图限制3张', name: 'image_multi3', value: [], multiple: true, limit: 3, isOneLine: true },
                // { type: 'richText', label: '富文本', name: 'richText', value: '', columnPer: 24, isOneLine: true, selfClass: "selfRichText", hideLabel: true },
            ],
            
            opBtnList: [
                { name: '返回', icon: 'back', method: this.popPage, params: '' },
                { name: '保存', icon: 'save', method: this.saveForm, params: '' },
                { name: '清除校验', icon: 'save', method: this.clearValidate, params: '' }
            ]
        }
    },
    computed: {
        // 页面是否为查看页面
        isView () {
            return this.formType === 'view'
        }
    },
    methods: {
        fieldChange () {
            arguments
        },
        exeMethod (handleClick) {
            if (typeof handleClick === 'function') {
                handleClick()
                return
            } else {
                alert('函数不存在')
            }
        },
        saveForm () {
            this.$refs.formHello.validateForm().then(res => {
                alert(JSON.stringify(res))
            })
        },
        clearValidate () {
            // this.$refs.formHello.clearValidate('requireEl')
            this.$refs.formHello.clearValidate()
        },
        popPage () {
            this.$router.go(-1)
        }
    },
    watch: {
        'groupType': {
            handler: function (newVal, oldVal) {
                // kill表单组件，避免后续更改不生效
                this.showForm = false
                // 设置组别属性
                if (newVal === 'manyGroup') {
                    this.formHelloList[0].groupName = '第一分组'
                } else {
                    delete this.formHelloList[0].groupName
                }
                // 重新生成表单组件
                setTimeout(() => {
                    this.showForm = true
                })
            }
        }
    }
}
</script>

<style scoped>

</style>




