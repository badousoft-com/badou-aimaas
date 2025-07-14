<template>
    <div class="demo-form">
        <el-radio-group
            class="padding defaultBg floatR"
            v-model="formType">
            <el-radio label="edit">编辑模式</el-radio>
            <el-radio label="view">查看模式</el-radio>
        </el-radio-group>
        <!-- {{dataForm}} -->
        <!-- 
        如果有标题，添加has-title类，页面计算滚动时才会算入标题
        如果需要页面铺满，添加h-per-100,页面会自动铺满
        可以直接设置高度样式 style="height:200px" -->
        <div class="bg-white bd-form-part firForm has-title">
            <m-title
                class="b-b"
                :title="mainTitle">
            </m-title>
            <el-form
                class="bd-form"
                :inline="true"
                :model="dataForm" 
                :rules="formRules"
                :class="{'is-view': isView}"
                v-fixedFormWidth="labelWidth">
                <!-- <div class="secForm bd-form__group">
                    <div class="title">
                        <bd-icon name="point-fill" class="point fill"></bd-icon>
                        第一表单组别
                    </div>
                    <el-row>
                        <el-col :span="8">
                            <el-form-item prop="sex">
                                <span slot="label" title="性别" class="s-label">性别性别</span>
                                <bd-text v-model="dataForm.sex" :isView="isView"></bd-text>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item prop="height">
                                <span slot="label" title="身高" class="s-label">身高身高身高</span>
                                <bd-text v-model="dataForm.height" :isView="isView"></bd-text>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item prop="name">
                                <span slot="label" title="姓名" class="s-label">姓名吧</span>
                                <bd-text v-model="dataForm.name" :isView="isView"></bd-text>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="8">
                            <el-form-item prop="num">
                                <span slot="label" title="数字" class="s-label">数字数字</span>
                                <bd-text v-model="dataForm.num" :isView="isView"></bd-text>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item prop="way">
                                <span slot="label" title="方式" class="s-label">方式</span>
                                <bd-text v-model="dataForm.way" :isView="isView"></bd-text>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div> -->
                <div class="secForm bd-form__group">
                    <div class="title">
                        <bd-icon name="point-fill" class="point fill"></bd-icon>
                        第二表单组别（全类型demo）
                    </div>
                    <el-row>
                        <el-col :span="8">
                            <el-form-item prop="text">
                                <span slot="label" title="文本框" class="s-label">文本框</span>
                                <bd-text
                                    v-model="dataForm.text"
                                    placeholder="请输入文本"
                                    :maxlength="30"
                                    icon="save"
                                    :isView="isView">
                                </bd-text>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item prop="int">
                                <span slot="label" title="整数" class="s-label">整数</span>
                                <bd-text
                                    v-model="dataForm.int"
                                    dataType="int"
                                    placeholder="请输入整数"
                                    :isView="isView">
                                </bd-text>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item prop="number">
                                <span slot="label" title="小数" class="s-label">小数</span>
                                <bd-text
                                    v-model="dataForm.number"
                                    dataType='number'
                                    placeholder='请输入小数'
                                    :isView="isView">
                                </bd-text>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="8">
                            <el-form-item prop="readonly">
                                <span slot="label" title="只读" class="s-label">只读</span>
                                <bd-text
                                    v-model="dataForm.readonly"
                                    :disabled="true"
                                    :isView="isView">
                                </bd-text>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item prop="password">
                                <span slot="label" title="密码" class="s-label">密码</span>
                                <bd-text
                                    v-model="dataForm.password"
                                    :isPassword="true"
                                    :isView="isView">
                                </bd-text>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item prop="textarea">
                                <span slot="label" title="文本域" class="s-label">文本域</span>
                                <bd-textarea
                                    v-model="dataForm.textarea"
                                    placeholder="请输入文本"
                                    :isView="isView">
                                </bd-textarea>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="8">
                            <el-form-item prop="date">
                                <span slot="label" title="日期" class="s-label">日期</span>
                                <bd-date
                                    v-model="dataForm.date"
                                    :isView="isView">
                                </bd-date>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item prop="datetime">
                                <span slot="label" title="日期时间" class="s-label">日期时间</span>
                                <bd-date
                                    v-model="dataForm.datetime"
                                    dateType="datetime"
                                    :isView="isView">
                                </bd-date>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item prop="datetimerange">
                                <span slot="label" title="日期时间范围" class="s-label">日期时间范围</span>
                                <bd-date
                                    v-model="dataForm.datetimerange"
                                    dateType="datetimerange"
                                    :isView="isView">
                                </bd-date>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="24">
                            <el-form-item prop="imagePicker">
                                <span slot="label" title="图片" class="s-label">图片</span>
                                <bd-image-picker
                                    v-model="dataForm.imagePicker"
                                    name="imagePicker"
                                    :isView="isView"
                                    @change="imagePickerChange">
                                </bd-image-picker>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
            </el-form>
        </div>
    </div>
</template>
<script>
    import MTitle from '@/components/frame/Common/MTitle'
    import fixedFormWidth from '@/directive/fixed-form-width'
    import {
        Text,
        Textarea,
        Radio,
        Checkbox,
        Select,
        Date,
        RichText,
        File,
        Attach,
        ImagePicker,
        Cascader,
        AddressPicker,
        ColorPicker,
        Number,
        Switch,
        Code,
        AddressBook
    } from '@/components/frame/Common/MForm/components/items/index'
    export default {
        name: "",
        components: {
            [MTitle.name]: MTitle,
            [Text.name]: Text,
            [Textarea.name]: Textarea,
            [Radio.name]: Radio,
            [Checkbox.name]: Checkbox,
            [Select.name]: Select,
            [Date.name]: Date,
            [RichText.name]: RichText,
            [File.name]: File,
            [Attach.name]: Attach,
            [ImagePicker.name]: ImagePicker,
            [Cascader.name]: Cascader,
            [AddressPicker.name]: AddressPicker,
            [ColorPicker.name]: ColorPicker,
            [Number.name]: Number,
            [Switch.name]: Switch,
            [Code.name]: Code,
            [AddressBook.name]: AddressBook
        },
        // 指令区域
        directives: {
            // 设置表单标签宽度
            fixedFormWidth
        },
        data () { // 定义页面变量
            return {
                mainTitle: 'demo表单标题展示',
                dataForm: {
                    sex: '这是性别嗯嗯',
                    height: '这是身高嗯嗯',
                    name: '这是姓名嗯嗯',
                    num: '这是编号讷讷',
                    way: '这是我的方式',

                    text: '我是一个超级长超级长的测试文本',
                    int: 255,
                    number: 11.11,
                    readonly: '测试只读数据',
                    password: '147258369',
                    imagePicker: []

                },
                formType: 'edit',
                // 标签宽度
                labelWidth: '110px',
                // 校验规则
                formRules: {
                    sex: [{ required: true, message: '请输入sex', trigger: 'blur' }],
                    height: [{ required: true, message: '请输入height', trigger: 'blur' }],
                    name: [{ required: true, message: '请输入name', trigger: 'blur' }],
                    num: [{ required: true, message: '请输入num', trigger: 'blur' }],
                    way: [{ required: true, message: '请输入way', trigger: 'blur' }],
                }
            }
        },
        computed: {
            isView () {
                return this.formType === 'view'
            }
        },
        methods: { // 定义函数
            imagePickerChange (fieldName, value) {
            }
        },
        // 可访问当前this实例
        created () {},
        // 挂载完成，可访问DOM元素
        mounted () {}
    }
</script>
<style lang='scss' scoped>
.demo-form {
    height: calc(100% - 44px);
}
</style>