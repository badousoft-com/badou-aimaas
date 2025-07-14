<template>
    <div
        v-if="dataList && dataList.length > 0"
        :class="{
            'firForm': title,
            'has-title': mainTitle,
            'h-per-100': fullHeight}"
        class="bg-white bd-form-part">
        <m-title
            class="b-b"
            :title="mainTitle"
            :show-shadow="formScrollTopDic !== 0">
            <!-- 标题右侧插槽 -->
            <template v-slot:right>
                <slot name="titleRight"></slot>
            </template>
        </m-title>
        <!-- {{dataForm}} -->
        <!-- {{_formData}} -->
        <!-- {{dataList}} -->
        <el-form
            :class="{
                'is-view': isView,
                'is-table': isView && viewType === 'table',
                'is-no-height': !(showList && showList.length > 0),
            }"
            class="bd-form"
            :inline="true"
            :model="dataForm"
            :ref="id"
            v-fixedFormWidth="labelWidth"
            @submit.native.prevent="() => {}">
            <!-- 表单内容的自定义排版 -->
            <template v-if="contentRender">
                <diy-form-render
                    :render="contentRender"
                    :slotList="dataList.map(i => i.name)">
                    <template
                        v-for="(k, k_index) in dataList"
                        v-slot:[k.name]>
                        <el-form-item
                            :data="k"
                            :name="k.name"
                            :prop="k.name"
                            :rules="formRules[k.name]"
                            :key="k_index"
                            class="content-render">
                            <m-form-item
                                :class="{'pointer d-ib': k.click}"
                                @click="fieldClick(k)"
                                :isView="k.isView || isView"
                                :formType="k.type"
                                v-bind="{...k, ...getFieldCustom(k.name)}"
                                :formData="_formData"
                                v-on="{...$listeners, ...(k.events || {})}"
                                v-model="k.value"
                                @change="fieldChange(k.name, k, ...arguments)"
                                @setRelatedField="setRelatedField"
                                @updateExtraField="(extraFieldObj) => updateExtraField(extraFieldObj,k)">
                            </m-form-item>
                        </el-form-item>
                    </template>
                </diy-form-render>
            </template>
            <!-- 默认的表单排版 -->
            <div
                v-else
                v-for="(i, i_index) in showList"
                :key="i_index"
                class="secForm bd-form__group">
                <!-- 存在组名，但该组下没有数据（i.list长度为0）,则整块不展示 -->
                <template v-if="i.list && i.list.length > 0">
                    <!-- 表单本身有大标题，如果子元素只有一个，那没必要再出现标题重复
                    小标题的作用在于出现多个子元素时区分 -->
                    <div class="title" v-if="showGroupTitle || (i.groupName && showList.length > 1)">
                        <bd-icon
                            :name="getGroupIcon(i)"
                            :class="{ 'is-small': isSmall(getGroupIcon(i)) }"
                            class="group-icon fill">
                        </bd-icon>
                        {{i.groupName}}
                        <!-- 组别右侧插槽模块：常用于按钮 -->
                        <slot :name="i.groupName"></slot>
                    </div>
                    <el-row
                        v-for="(j, j_index) in i.list"
                        :key="j_index"
                        :class="{'onlyOneColumn': i.columnNum===1}"
                        class="bd-form__group-item">
                        <template v-if="j && j.constructor === Array && j.length > 0">
                            <!-- el-col添加p-r主要是为了使用appendDOM自定义，后续若需要更改需考虑这里 -->
                            <el-col
                                v-for="(k, k_index) in j"
                                :key="k_index"
                                :span="getColumnSpan(k,i,columnNum,{ignoreColumnPer})"
                                class="p-r"
                                :class="k.moduleClass || k.className || k.fieldClass || ''">
                                <!-- wrongTipSite：校验提示出现位置 -->
                                <el-form-item
                                    :class="{
                                        'wrongTipLeft': k.wrongTipSite === 'left',
                                        'isBlock':k.isBlock, 
                                        'isShowAllLabel':k.isShowAllLabel,
                                        'hidenItemContent': _hideBySwitchStatus(k) }"
                                    :prop="k.name"
                                    :rules="formRules[k.name]">
                                    <!-- 表单label标签区域 -->
                                    <span
                                        v-if="!k.hideLabel"
                                        slot="label"
                                        :class="['s-label', k.labelClass]">
                                        <!-- 查看页和不使用tipicon情况时不显示 -->
                                        <span  
                                            :class="{'has-use-tip':k.useTip && !isView}"
                                            :style="setTextLabelMaxWidth(k.useTip && !isView)"
                                            class="s-label-area">
                                            <el-tooltip
                                                v-if="k.useTip && !isView"
                                                popper-class="use-tip-popper"
                                                effect="dark"
                                                placement="top-start"
                                                :offset="7">
                                                <div slot="content" v-html="k.useTip"></div>
                                                <bd-icon name="tip-fill" class="tip-icon"></bd-icon>
                                            </el-tooltip>
                                            <span :title="k.label" class="s-label-word">{{k.label}}</span>
                                            
                                        </span>
                                    </span>
                                    <bd-switch
                                        slot="label"
                                        v-if="'switchStatus' in k"
                                        v-model="k.switchStatus"
                                        @change="(val) => fieldSwitchChange(k.name, val, k)"
                                        class="d-ib padL-10">
                                    </bd-switch> 
                                    <!-- 表单编辑输入模块需要判断是否为插槽 -->
                                    <!-- 非插槽 -->
                                    <template>
                                        <!-- jsx语法渲染 -->
                                        <template v-if="k.render">
                                            <render-fun :render="fieldRender(k)"></render-fun>
                                        </template>
                                        <!-- 自定义格式化展示 -->
                                        <template v-else-if="k.formatter">
                                            <div
                                                :class="{'pointer d-ib': k.click}"
                                                v-html="fieldFormatter(k)"
                                                @click="fieldClick(k)"></div>
                                            <!-- <div v-html="k.formatter()" @click="k.click()"></div> -->
                                        </template>
                                        <!-- 默认展示 -->
                                        <template v-else>
                                            <m-form-item
                                                v-if="k.type !== 'slot'"
                                                :class="{'pointer d-ib': k.click}"
                                                @click="fieldClick(k)"
                                                :isView="k.isView || isView"
                                                :formType="k.type"
                                                v-bind="{...k, ...getFieldCustom(k.name)}"
                                                :formData="_formData"
                                                v-on="{...$listeners, ...(k.events || {})}"
                                                v-model="k.value"
                                                @change="fieldChange(k.name, k, ...arguments)"
                                                @setRelatedField="setRelatedField"
                                                @updateExtraField="(extraFieldObj) => updateExtraField(extraFieldObj, k)">
                                            </m-form-item>
                                            <!-- 是插槽 使用插槽渲染 -->
                                            <slot v-else :name="k.name" :data="k"></slot>
                                        </template>
                                    </template>
                                </el-form-item>
                                <!-- 字段后追加DOM（如一些填写信息，右侧会提供一些额外操作帮助填写） -->
                                <div class="form-item-append d-ib">
                                    <render-fun
                                        v-if="k.appendRender"
                                        :render="fieldAppendRender(k)">
                                    </render-fun>
                                </div>
                            </el-col>
                        </template>
                    </el-row>
                </template>
            </div>
            <slot name="end"></slot>
        </el-form>
        <slot name="after"></slot>
        <div v-if="openNoFieldTip && !(showList && showList.length > 0)" class="padding">
            暂无可显示字段
        </div>
    </div>
</template>
<script>
import RenderFun from '@/components/frame/RenderFun'
import GlobalConst from '@/service/global-const'
import MTitle from '@/components/frame/Common/MTitle'
import MFormItem from '@/components/frame/Common/MForm/components/MFormItem'
import { Switch } from '@/components/frame/Common/MForm/components/items'
import { StartEndSet, FileSet } from '@/components/frame/Common/MForm/frameSet/index'
import { List_Filter_Group_ByAttr, Has_Array_Data } from '@/utils/list'
import fixedFormWidth from '@/directive/fixed-form-width'
import { Has_Value } from '@/utils'
import { Get_Switch_Num } from '@/service/switch'
import { Get_Column_Span } from '@/service/base-service'
import { Get_Validation_Rule } from '@/service/module'
import DiyFormRender from './DiyFormRender'
export default {
    name: 'bd-form',
    components: {
        MFormItem,
        [Switch.name]: Switch,
        [MTitle.name]: MTitle,
        [RenderFun.name]: RenderFun,
        [DiyFormRender.name]: DiyFormRender
    },
    inheritAttrs: false,
    props: {
        // 表单id，用于区分唯一性，必填项（必填安全性高）
        id: {
            type: String,
            default: 'formRef'
        },
        // 表单标题
        title: {
            type: [String, Boolean],
            default: GlobalConst.form.title
        },
        // 无标题
        noTitle: {
            type: Boolean,
            default: false,
        },
        // 标签文本宽度（默认单位px）
        labelWidth: {
            type: [String, Number],
            default: GlobalConst.form.labelWidth
        },
        // 表单分组的字段名
        // 按照此字段名将【一维字段数组】转化成二维数据
        groupName: {
            type: String,
            default: 'groupName'
        },
        // 表单展示列数
        columnNum: {
            type: Number,
            default: GlobalConst.form.columnNum
        },
        // 表单项数据源--支持同个表单拆分多块进行展示，数组下每个对象为表单的一个子展示区
            // 默认数据格式[{
            //     title: '', --这一模块表单标题
            //     list: [{name: '', value: ''}], --这一模块表单项json数据
            //     labelWidth: String,  --这一模块表单的标签宽度
            //     column: Number --这一模块表单的展示列数
            // }]
        dataList: {
            type: Array,
            deafult: () => {
                return []
            }
        },
        // viewType: 'table'-表示以表格形式展示（目前只作用在查看）
        viewType: {
            type: String
        },
        // 当前是否为查看状态，默认false（表示当前为编辑状态）
        isView: {
            type: Boolean,
            default: false
        },
        // 是否铺满页面
        fullHeight: {
            type: Boolean,
            default: false
        },
        // 字段值变更事件函数对象，格式如{
        //     name: function () {},
        //     sex: function () {}
        // }
        fieldChangeFunObj: {
            type: Object
        },
        // 字段格式化
        fieldCustom: {
            type: Object
        },
        // 自定义校验
        validateRule: {
            type: Function
        },
        // 是否开启表单校验
        openValidate: {
            type: Boolean,
            default: true
        },
        // 是否展示组别标题
        showGroupTitle: {
            type: Boolean,
            default: false
        },
        // 表单检验的相关参数（目前作用域唯一校验所需要的参数）
        formRuleParams: {
            type: Object,
        },
        // 合并的组别信息字段键
        groupProps: {
            type: Array,
            default: () => []
        },
        // 是否开启无字段时的提示
        openNoFieldTip: {
            type: Boolean,
            default: true
        },
        // 是否忽略表单单项配置的占比数
        // 目前场景用于：希望一列展示时，此时不希望带有个人占比数数，而是默认全铺满，一列而下
        ignoreColumnPer: {
            type: Boolean,
            default: false
        },
        // 表单的自定义排版展示函数
        contentRender: {
            type: Function
        },
    },
    // 指令区域
    directives: {
        // 设置表单标签宽度
        fixedFormWidth
    },
    data () { // 定义页面变量
        return {
            // 表单模块滚动距离
            formScrollTopDic: 0,
            // 字段的额外关联配置字段，最终会添加回表单
            addFormField: {},
            // 获取表单项在一行中所占份数的函数
            getColumnSpan: Get_Column_Span,
        }
    },
    computed: {
        // 根据switch状态判断是否隐藏
        _hideBySwitchStatus () {
            return function (field) {
                return ('switchStatus' in field) &&
                       !field.switchStatus
            }
        },
        /**
         * 获取完整提交表单数据（包括传入的字段数组+冗余字段+自定义传入字段）
         */
        _formData () {
            return Object.assign({}, this.dataForm, this.addFormField)
        },
        // 获取标题，返回Boolean或者String{标题}
        mainTitle () {
            return !this.noTitle && this.title
        },
        showStatus (itemObj) {
            return (itemObj) => {
                if (itemObj.hasOwnProperty('hidden') && itemObj['hidden']) return false
                if (itemObj.type === 'hidden') return false
                return true
            }
        },
        // 展示列表
        showList () {
            if (!(this.dataList && this.dataList.constructor === Array)) return []
            // 定义返回结果
            let resultList = []
            // 过滤隐藏项
            let _dataList = this.dataList.filter(i => this.showStatus(i))
            // 配置一些默认项数据
            this.setDefaultSetting(_dataList)
            // 按照组别groupName进行分组
            let _groupList = List_Filter_Group_ByAttr(_dataList, this.groupName, GlobalConst.form.groupName,
                { groupFieldKeys: this.groupProps }
            )
            // 按照【行】进行字段划分，假设一个字段占比8，一行为24占比，则3个字段为一个数组
            _groupList.forEach(i => {
                let { list, ...item } = i
                this.$set(item, 'list', [])
                resultList.push(item)
            })
            _groupList.forEach((i, index) => {                 
                this.groupByColumnPer(resultList[index].list, i.list, i)
            })
            return resultList
        },
        // 表单对象
        dataForm: {
            get () {
                let formObject = {}
                // 删除项：所有attach都汇总这里，例如附件，图片
                let deleteAttach = {}
                // 设置字段属性destroyed：true的字段将被视为不纳入最后表单数据
                // let _existDataList = this.dataList.filter(i => {
                //     return !i.destroyed &&
                //            !(('switchStatus' in i) && !i.switchStatus)
                // })
                let _existDataList = this.dataList.filter(i => !i.destroyed)
                _existDataList.forEach(i => {
                    // 获取值
                    let _val = i.value
                    // 特殊类型值处理
                    switch (i.type) {
                        case 'switch':
                            _val = Get_Switch_Num(_val)
                            break
                        default:
                            // do something
                    }
                    // 更新值
                    this.$set(formObject, i.name, _val)
                })
                _existDataList.forEach(i => {
                    // 判断表单项类型，部分需要额外操作逻辑
                    switch (i.type) {
                        case 'imagePicker':
                        case 'attach':
                            // 图片/附件类型的赋值需要额外操作,将删除附件更新给deleteAttach对象
                            formObject = FileSet.resultForm.updateItem(i, formObject, deleteAttach)
                            break
                        default:
                            // do something
                    }
                    // 处理表单下的组合字段
                    formObject = StartEndSet.resultForm(i, formObject, this.dataList)
                })
                // 前面遍历字段实现删除对象deleteAttach的属性更新，使用该值更新表单属性deleteAttach
                formObject = FileSet.resultForm.updateDeleteAttach(formObject, deleteAttach)
                return formObject
            },
            set (val) {}
        },
        // 表单校验规则
        formRules: {
            get () {
                // 如果设置不开启校验，直接返回空规则
                if (!this.openValidate) return []
                let _rules = {}
                // 获取自定义的校验规则
                let _extraValidateRule = this.validateRule &&
                                       typeof this.validateRule === 'function' &&
                                       this.validateRule.call(this) || {}
                this.dataList.forEach(i => {
                    if (i.rules) {
                        // 之前的方案都是说在外面组装好规则再传入
                        // 2022-08-05：同时支持只传入关键规则词，在此处再做拼接处理
                        switch (i.rules.constructor) {
                            case Array:
                                if (i.rules.length > 0) {
                                    this.$set(_rules, i.name, i.rules || [])
                                }
                                break
                            case String:
                                this.$set(_rules, i.name, Get_Validation_Rule.call(this, i, i.rules) || [])
                                break
                            default:
                                // do something
                        }
                    }
                    // 判断当前字段是否存在自定义校验
                    if (_extraValidateRule[i.name]) {
                        // 拓展加入自定义校验规则
                        _rules[i.name] = [
                            ...(_rules[i.name] || []),
                            ...(_extraValidateRule[i.name] || [])
                        ]
                    }
                })
                // 更新自定义校验函数
                //     1. 回调添加多当前字段对象参数
                //     2. 修改函数内域为当前页面域
                let _validatorKey = 'validator'
                Object.keys(_rules).forEach(i => {
                    _rules[i].forEach(j => {
                        if (_validatorKey in j && j[_validatorKey]) {
                            let _validatorFun = j[_validatorKey]
                            // 获取字段对象
                            let _fieldItem = this.dataList.find(k => k.name === i)
                            j[_validatorKey] = (rule, value, callback) => _validatorFun.call(this, rule, value, callback, _fieldItem)
                        }
                    })
                })
                return _rules
            }
        },
        // 使用或不使用tip图标两种情况 设置textlabel的最大宽度 用于溢出显示省略号
        setTextLabelMaxWidth (isUseTipIcon) {
            return isUseTipIcon => {
                if (isUseTipIcon) {
                    let labelWidth = parseInt(this.labelWidth)
                    //  45 为 tipIcon所占区域大小及其间距 和 element-ui el-form-item lable插槽的padding-left 值
                    return { maxWidth: `${labelWidth - 45}px` }
                }
            }
        }
    },
    methods: { // 定义函数
        /**
         * 表单项的switch功能切换事件
         * @param {String} 当前切换的字段键名
         * @param {Boolean} 切换后的状态值， true/false
         * @param {Object} 切换的字段对象
         */
        fieldSwitchChange (name, switchVal, field) {
            this.$emit('fieldSwitchChange', name, switchVal, field, this.dataForm)
        },
        /**
         * 根据组别信息获取组别icon
         * @param {Object} i 组别项数据
         */
        getGroupIcon (i) {
            return i.groupIcon || GlobalConst.form.groupIcon
        },
        /**
         * 判断是否展示为小尺寸图标
         * @param {String} icon 图标名称
         */
        isSmall (icon) {
            return icon && icon.includes('point')
        },
        /**
         * 获取字段的自定义属性（后续用于不同表单字段类型的个性化属性与方法）
         * @param {String} fieldName 字段键名
         */
        getFieldCustom (fielaName) {
            return this.fieldCustom && this.fieldCustom[fielaName] || {}
        },
        /**
         * 字段值变更事件（推荐）
         * @param {String} name 字段键名
         * @param {*} value 变更值
         * @param {Object} fieldObj 字段对象
         * @param ...其余参数
         */
        fieldChange () {
            let [ name, fieldObj, value, ...restParams ] = Array.from(arguments)
            // 兼容旧版本change事件 --start
            this.change(name, value, fieldObj)
            // 兼容旧版本change事件 --end
            
            // 字段值变更的时候，手动触发字段检验规则：核心处理一些自定义组件的校验
            this.validateField(name)
            // 根据当前字段的 fieldChange 对象，控制其余表单项的显示与隐藏
            fieldObj.fieldChange && this.controlShowAndHide(value, fieldObj.fieldChange)
            // 通知父级组件change
            this.$emit('fieldChange', this, name, value, fieldObj, ...restParams)
            // 判断是否有传入的change事件函数对象，这个主要用于单纯使用表单组件时有用，可方便外部组件操作表单数据
            let _currentFieldChangeFun = this.fieldChangeFunObj?.[name]
            if (_currentFieldChangeFun && typeof _currentFieldChangeFun === 'function') {
                _currentFieldChangeFun.call(this, name, value, fieldObj, ...restParams)
            }
        },
        /**
         * @description: 根据当前字段的 fieldChange 对象，控制其余表单项的显示与隐藏
         * @param {String} value：触发更改的表单字段值
         * @param {Object} changeConfig：需更改的配置信息
         * @return {*}
         */
        controlShowAndHide (value, changeConfig) {
            // 当前（20230707）只支持到value为string类型 && 相关配置信息存在
            if (typeof value !== 'string' || !changeConfig[value]) return
            let { hiddenFields = '', showFields = '' } = changeConfig[value]
            // 隐藏的字段名
            let hiddenFieldNames = hiddenFields ? hiddenFields.split(',') : []
            // 需显示的字段名
            let showFieldNames = showFields ? showFields.split(',') : []
            this.dataList.forEach(f => {
                // 处理需隐藏的表单项
                if (hiddenFieldNames.includes(f.name)) {
                    f.__showFormType = f.__showFormType || f.type
                    f.type = 'hidden'
                }
                // 处理需展示的表单项
                if (showFieldNames.includes(f.name)) {
                    f.type = f.type === 'hidden' ? (f.__showFormType || 'text') : f.type
                }
            })
        },
        /**
         * 字段值变更事件（即将废弃）
         * @param {String} name 字段键名
         * @param {*} value 字段值
         * @param {Object} fieldObj 字段对象
         */
        change (name, value, fieldObj) {
            // 添加额外的字段
            // this.updateAddFormField.apply(this, arguments)
            // 将change事件抛出去
            this.$emit('change', name, value, fieldObj, this)
        },
        // (核心是面向单独使用mform组件)每个对象中，满足除addAttr外的属性的键值对应，则设置addAttr的附加属性
        // 目前仅设置isOneLine
        setDefaultSetting (fieldList) {
            // 设置表单项的默认规则
            let rules = [
                // 单选框
                { type: 'radio', addAttr: { isOneLine: true } },
                // 复选框
                { type: 'checkbox', addAttr: { isOneLine: true } },
                // 文本域
                { type: 'textarea', addAttr: { isOneLine: true } },
                // 颜色多选
                { type: 'colorPicker', multiple: true, addAttr: { isOneLine: true } },
                // 图片多选
                { type: 'imagePicker', multiple: true, addAttr: { isOneLine: true } },
                // 附件多选
                { type: 'attach', addAttr: { isOneLine: true } },
                // 富文本
                { type: 'richText', addAttr: { isOneLine: true } }
            ]
            /**
             * 判断当前字段对象是否符合suitFieldObj配置的规则
             * @param {Object} suitFieldObj 指定匹配的对象属性
             * @param {Object} field 字段对象
             * @return {Boolean} field符合suitFieldObj规则则返回true
             */
            function suitAttr (suitFieldObj, field) {
                // 确保field满足suitFieldObj所有属性规则，使用every
                return Object.keys(suitFieldObj).every(i => suitFieldObj[i] == field[i])
            }
            rules.forEach(i => {
                // addAttr: 符合规则后需要添加在字段中的属性对象
                // suitFieldObj： 匹配字段的属性规则
                let { addAttr, ...suitFieldObj } = i
                // 类型若没有传入isOneLine/columnPer属性，则默认独占一行
                fieldList.filter(j => suitAttr(suitFieldObj, j) &&
                                !j.hasOwnProperty('isOneLine') &&
                                !j.hasOwnProperty('columnPer')).forEach(k => {
                    // 若字段中不存在指定属性，则将addAttr中的属性添加给字段，为其设置默认属性与值
                    Object.keys(addAttr).forEach(m => {
                        this.$set(k, m, addAttr[m])
                    })
                })
            })
        },
        /**
         * 字段自定义标签与事件（render + JSX），同时处理标签与事件
         * fieldFormatter+fieldClick可处理简单场景自定义，使用fieldRender可处理更复杂的场景
         * @param {Object} item 字段对象
         */
        fieldRender (item) {
            // 定义获取字段值value
            // 定义获取字段格式化函数formatter
            let { value, render } = item
            if (!(render && typeof render === 'function')) return
            // 返回格式化文本
            return (h, context) => {
                return render.call(this, h, context, value, item)
            }
        },
        /**
         * 追加在字段展示的后面render渲染
         * @param {Object} item 字段数据对象
         */
        fieldAppendRender (item) {
            let { value, appendRender } = item
            if (!(appendRender && typeof appendRender === 'function')) return
            // 返回格式化文本
            return (h, context) => {
                return appendRender.call(this, h, context, value, item)
            }
        },
        /**
         * 字段标签自定义展示【只处理标签，可与fieldClick自定义事件结合使用】
         * @param {Object} item 字段对象
         */
        fieldFormatter (item) {
            // 定义获取字段值value
            // 定义获取字段格式化函数formatter
            let { value, formatter } = item
            if (!(formatter && typeof formatter === 'function')) return
            // 返回格式化文本
            return formatter.call(this, value, item)
        },
        /**
         * 字段自定义点击事件【只处理事件，可与fieldFormatter自定义标签展示】
         * @param {Object} item 字段对象
         */
        fieldClick (item) {
            // 定义获取字段值value
            // 定义获取字段点击函数click
            let { value, click } = item
            if (!(click && typeof click === 'function')) return
            // 执行自定义点击事件
            click.call(this, value, item)
        },
        /**
         * 将字段值处理给赋值给关联键值
         * @param [Array] key 关联键数组
         * @param [Array] value 关联值
         */
        setRelatedField (key, value) {
            key.forEach((k, index) => {
                let itemObj = this.dataList.find(i => i.name === k)
                this.$set(itemObj, 'value', value[index])  
            })
        },
        /**
         * 将列表数组按照一行占比情况再次转化数据结构
         * @param {Array} showList 最终展示的列表数据
         * @param {Array} dataList 传入的列表数据
         * @param {Object} itemObj 当前字段对象
         */
        groupByColumnPer (showList, dataList, itemObj) {
            // 定义临时存储列表数据
            let tempList = []
            // 定义当前列占比数
            let columnNum = 0
            // 定义最高列占比，参照elementUI，一行占比数峰值是24比例列
            let MaxcolumnNum = GlobalConst.form.gridNum
            dataList.forEach((i, index) => {
                // 获取当前模块的占比份数（24等份维度）
                let itemColumnNum = Get_Column_Span(i, itemObj, this.columnNum, {
                    ignoreColumnPer: this.ignoreColumnPer
                })
                // 判断当前模块是否独占一行
                if (itemColumnNum === MaxcolumnNum) {
                    // 遇到独占一行的模块，将之前的临时列表整理优先添加进结果列表
                    if (tempList.length > 0) {
                        showList.push(tempList)
                        // 清空临时数据
                        tempList = []
                        // 清空已有数
                        columnNum = 0
                    }
                    // 将独占一行的模块添加进去
                    showList.push([i])
                } else {
                    // 计算当前行添加新增模块后后是否会超出一行（占比份数 <=24则不会）
                    if (columnNum + itemColumnNum <= MaxcolumnNum) {
                        // 若不会，则更新当前一行占比份数
                        columnNum += itemColumnNum
                        // 将数据添加进当前行
                        tempList.push(i)
                    } else {
                        // 若超出，表示新增模块需要添加到下一行，前一行模块可以结束
                        showList.push(tempList) // 将当前行添加进结果列表
                        tempList = []     // 开始下一行 
                        tempList.push(i)  // 将当前模块添加进下一行
                        columnNum = itemColumnNum  // 更新下一行已有占比份数
                    }
                    // 若当前模块为最后一个，则不管满不满一行都将临时数据行也添加进结果列表
                    if (index === dataList.length - 1) {
                        showList.push(tempList)
                    }
                }
            })
        },
        // 表单内容模块滚动事件监听
        scrollForm () {
            // 获取表单dom
            let formEl = this.$refs?.[this.id]?.$el
            if (formEl) {
                // 设置表单滚动距离顶部高度
                this.formScrollTopDic = formEl.scrollTop
            } else {
                this.formScrollTopDic = 0
            }
            // 事件抛出，给父组件使用
            this.$emit('scrollForm', this.formScrollTopDic)
        },
        /**
         * 触发指定字段校验
         * 刷新校验状态，常用于值变更，但校验错误信息仍在的情况，重新触发检验检查
         * @param {String} fieldName 字段名
         */
        validateField (fieldName) {
            // 表单字段变更时可能太快导致检验失败，所以添加$nextTick等待页面加载先
            this.$nextTick(() => {
                this.$refs[this.id].validateField(fieldName)
            })
        },
        /**
         * 表单保存检验事件
         * @param {Object} option 配置项
         */
        validateForm (option) {
            return new Promise((resolve, reject) => {
                let {
                    openValidate, // 是否开启表单校验
                } = Object.assign({
                    openValidate: this.openValidate
                }, option)
                // 若开启校验
                if (openValidate) {
                    this.$refs[this.id].validate((valid) => {
                        if (valid) {
                            // 这里是动态js未来的入口，用于操作表单数据
                            resolve(this._formData)
                        } else {
                            // 滚动到校验失败的表单项模块
                            this.scrollFailFormItem()
                            reject('表单校验异常')
                        }
                    })
                } else {
                    // 不开启校验，则直接返回表单数据
                    resolve(this._formData)
                }
            })
        },
        // 跳转到校验失败的表单项
        scrollFailFormItem () {
            // 校验失败的表单项
            let failFormItem = null
            // 遍历所有表单项，判断是否有校验失败的信息，有的话就返回整个元素
            failFormItem = this.$refs[this.id].fields.find((element) => {
                return element.validateMessage
            })  
            // 若有校验失败的表单项，则滚动至该元素位置
            if (failFormItem !== null) {
                failFormItem.$el.scrollIntoView({
                    behavior: 'smooth', // 滚动过渡
                    block: 'center' // 元素位置在中间
                })
            }
        },
        /**
         * 移除表单项的校验结果。传入待移除的表单项的 prop 属性或者 prop 组成的数组，如不传则移除整个表单的校验结果
         * @params {Array | String}
         */
        clearValidate (data) {
            this.$refs?.[this.id].clearValidate(data)
        },
        /**
         * 处理冗余字段逻辑
         * @param {Object} extraFieldObj 冗余字段对象
         * @param {Object} fieldObj 当前字段对象
         */
        updateExtraField (extraFieldObj, fieldObj) {
            // 获取冗余字段数组
            let extraList = extraFieldObj && Object.keys(extraFieldObj)
            // 更新冗余字段的值
            fieldObj.extraValueObj = extraFieldObj || {}
            if (Has_Array_Data(extraList)) {
                // 获取当前操作的字段对象
                let currentField = this.dataList.find(i => i.name === fieldObj.name)
                // 遍历需要处理的冗余字段
                extraList.forEach(i => {
                    // 获取匹配表单项
                    let _field = this.dataList.find(j => j.name === i)
                    // 获取当前字段值
                    let _value = extraFieldObj[i]
                    if (_field) {
                        // 若冗余字段为表单中字段则直接更新
                        this.$set(_field, 'value', _value)
                    } else {
                        // 若不存在表单，则更新给addFormField作为属性，最终作为额外表单字段提交
                        this.$set(this.addFormField, i, _value)
                    }
                    // 同步更新，以便组件的逻辑中使用到字段对象数据
                    currentField[i] = _value
                })
            }
        },
        /**
         * 更新需要额外添加的字段
         * 模型设计器配置中：可以为字段配置额外的两个字段，分别对应id键与text键，提交表单时需要把这些额外配置的字段提取出来然后随表单一起提交
         * 该函数主要更新当前变量addFormField，最终用于表单提交
         * @param {String} name
         * @param {String} value 字段值
         * @param {Object} fieldObj 字段对象
         */
        updateAddFormField (name, value, fieldObj) {
            // 获取对象下配置的冗余字段键
            // valueFieldId：将当前字段值赋给该键
            // valueFieldText：将当前字段对应文本赋给该键
            let { valueFieldId, valueFieldText } = fieldObj
            if (valueFieldId) {
                // 存在该冗余键则更新并赋值
                this.addFormField[valueFieldId] = value
            }
            if (valueFieldText) {
                // 定义结果值
                let _result = null
                if (Has_Value(value)) {
                    // 考虑到值可能为多选的情况（多选时value为'a,b,c'），使用拆分
                    let _valueList = value.split(GlobalConst.separator)
                    let _textList = []
                    // 根据value值匹配options对应的text值
                    _valueList.forEach(i => {
                        let _item = fieldObj.options.find(j => j[GlobalConst.dicOption.idName] === i)
                        _textList.push(_item[GlobalConst.dicOption.textName])
                    })
                    // 根据分隔符合并text值，并更新给结果集
                    _result = _textList.join(GlobalConst.separator)
                }
                // 将最终结果更新给指定的冗余键，更新addFormField变量
                this.addFormField[valueFieldText] = _result
            }
        }
    },
    // 可访问当前this实例
    created () {
        this.dataList.forEach(i => {
            i.fieldChange && this.controlShowAndHide(i.value, i.fieldChange)
        })
    },
    // 挂载完成，可访问DOM元素
    mounted () {
        // 将表单初始数据抛出，父组件可接收
        setTimeout(() => {
            this.$emit('getInitForm', this._formData, this)
        }, 500)
    },
}
</script>
<style lang='scss' scoped>
// 执行与switch设置隐藏元素的相关逻辑
.hidenItemContent >>> {
    .el-form-item__content {
        display: none;
    }
}
</style>
<style>
/* tooltip 提示框样式 由于其特殊性，这么写才能有效*/
    .use-tip-popper.el-tooltip__popper {
        max-width: 306px;
    }
</style>