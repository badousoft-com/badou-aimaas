<template>
    <m-form
        v-if="isReady"
        class="bd-module-form h-per-100"
        v-bind="$attrs"
        :ref="refName"
        :id="refName"
        :title="_title"
        :labelWidth="_labelWidth"
        :columnNum="Is_Mobile()? mobileColumnNum : _columnNum"
        :dataList.sync="_fieldList"
        :isView="isView"
        :detailId="detailId"
        :fieldCustom="diy_fieldCustom"
        :validateRule="diy_validateRule"
        :formRuleParams="_formRuleParams"
        :contentRender="diy_contentRender"
        v-on="$listeners"
        @change="change"
        @fieldChange="fieldChange">
        <!-- 表单标题右侧插槽 -->
        <template v-slot:titleRight>
            <slot name="titleRight"></slot>
        </template>
        <!-- 跟随在表单内容后 -->
        <template v-slot:after>
            <slot name="after"></slot>
        </template>
    </m-form>
</template>
<script>
import MForm from '@/components/frame/Common/MForm/index'
import GlobalConst from '@/service/global-const'
import { Is_Mobile } from '@/utils/browser.js'
import { Change_To_Base_Form } from './index.js'
import { EventBus } from '@/service/event-bus'
import { Jump_Drill_Url, Render_Field } from '@/service/base-service.js'
import { mapGetters } from 'vuex'
export default {
    name: "module-form",
    inheritAttrs: false,
    components: {
        MForm
    },
    props: {
        title: {
            type: String
        },
        // ref属性名称
        refName: {
            type: String,
            default: 'edit'
        },
        // 模型名称
        name: {
            type: String
        },
        // 模型编码
        mdCode: {
            type: String
        },
        // 标签宽度
        labelWidth: {
            type: [String, Number]
        },
        // 详情数据id，用于获取页面详细数据
        detailId: {
            type: String,
        },
        // 表单字段数组数据[]
        fieldList: {
            type: Array,
            default: () => []
        },
        // 是否为查看
        isView: {
            type: Boolean,
            default: false
        },
        // 数据字典集合对象
        dic: {
            type: Object,
            default: () => {}
        },
        // 自定义配置【来源于自定义js】
        customSetting: {
            type: Object,
            default: () => {}
        },
        // 详情数据id
        detailId: {
            type: String,
        },
        // 表单展示列数
        columnNum: {
            type: Number
        },
        // 额外参数:存储不属于当前组件业务的参数；数据中转站
        elseAttrs: {
            type: Object
        }
    },
    data () { // 定义页面变量
        return {
            // 数据是否准备齐全
            isReady: false,
            diy_beforeFieldChange: null,
            // 自定义-渲染前事件
            diy_beforeRender: null,
            // 自定义-表单展示列数
            diy_columnNum: null,
            // 自定义-字段change事件(即将废弃)
            diy_fieldChange: null,
            // 自定义-字段change事件(推荐)
            diy_valueChange: null,
            // 自定义-字段校验规则
            diy_validateRule: null,
            // 自定义-字段渲染与格式化
            diy_renderField: null,
            // 自定义-字段后追加DOM
            diy_appendDom: null,
            // 自定义-表单字段组件内的专属自定义属性集合
            diy_fieldCustom: null,
            // 表单内容的自定义排版
            diy_contentRender: null,
            // 判断是否为移动端
            Is_Mobile: Is_Mobile,
            // 移动端表单列数
            mobileColumnNum: GlobalConst.mobile.columnNum
        }
    },
    computed: {
        ...mapGetters([
            'formColumnNum'
        ]),
        // 组装表单唯一校验时所需要的参数
        _formRuleParams () {
            return {
                mdCode: this.mdCode, // 模型编码
                id: this.detailId, // 表单详情id
            }
        },
        // 表单标题
        _title () {
            return this.title || this.name || GlobalConst.form.title
        },
        // 表单列数
        _columnNum () {
            return this.columnNum || // 当前组件接收参数
                   this.diy_columnNum || // 模型配置的参数
                   this.formColumnNum || // 基础配置中配置的参数
                   GlobalConst.form.columnNum // 通用表单配置的参数
        },
        // 表单label宽度
        _labelWidth () {
            return `${parseInt(this.labelWidth || GlobalConst.form.labelWidth)}px`
        },
        // 字段列表
        _fieldList: {
            get () {
                return this.fieldList
            },
            set (val) {
                this.$emit('update:fieldList', val)
            }
        },
        // 表单数据转化配置项
        changeOption () {
            return {
                isView: this.isView,
                mdCode: this.mdCode,
                detailId: this.detailId,
            }
        },
    },
    methods: { // 定义函数
        /**
         * 表单字段change事件（推荐）
         */
        fieldChange () {
            // 判断是否存在自定义change事件，存在则执行
            if (!this.diy_valueChange) return
            // arguments:默认前4个参数：[表单页面作用域，字段键名，值，字段对象, ...(其他变量)]
            let _fieldChangeFun = this.diy_valueChange[arguments[1]]
            if (_fieldChangeFun && typeof _fieldChangeFun === 'function') {
                _fieldChangeFun.call(this, ...arguments)
            }
        },
        /**
         * 表单字段change事件（即将废弃）
         * @param {String} fieldName 字段键名
         * @param {String} value 字段值
         * @param {Object} fieldObj 字段对象
         * @param {Object} formScope 表单所在页面作用域
         */
        change (fieldName, value, fieldObj, formScope) {
            // 判断是否存在自定义change事件，存在则执行
            if (this.diy_fieldChange && typeof this.diy_fieldChange === 'function') {
                this.diy_fieldChange.call(this, fieldName, value, fieldObj, formScope)
            }
            // 继续抛出事件，父组件看看用不用
            this.$emit('fieldChange', fieldName, value, fieldObj, formScope, this)
        },
        /**
         * 获取格式化后字段对象数据
         * @param {Object} fieldObj 字段对象
         */
        getFormatterFieldObj (fieldObj, valueObj) {
            // 判断是否存在自定义js设置字段格式化
            // 优先级最高
            if (this.diy_renderField && this.diy_renderField[fieldObj.name]) {
                return this.diy_renderField?.[fieldObj.name]
            }
            // 获取模型配置的字段钻取url：drillUrl
            // 获取模型配置的自定义字段属性：customOptions
            let { drillUrl, customOptions } = fieldObj
            // 定义获取自定义配置项
            //     view:查看专属属性
            //     edit:编辑专属属性
            let _type = this.isView ? 'view' : 'edit'
            let _fieldSetting = null
            try {
                _fieldSetting = customOptions && JSON.parse(customOptions)?.[_type]
            } catch (e) {
                console.error(`处理customOptions数据转化发生异常`)
            }
            if (drillUrl || _fieldSetting) {
                // 获取渲染内容
                return this.getRender(drillUrl, _fieldSetting, valueObj)
            }
            return {}
        },
        /**
         * 获取渲染内容
         * @param {String} drillUrl 钻取地址
         * @param {ObjectString} fieldSetting 字段配置数据
         */
        getRender (drillUrl, fieldSetting, valueObj) {
            let render = (h, context, cellValue, fieldObj) => {
                return Render_Field.call(this, h, fieldSetting, drillUrl, cellValue, () => {
                    // 钻取跳转事件
                    Jump_Drill_Url.call(this, fieldObj, valueObj)
                })
            }
            // 返回对象数据
            return {
                render,
                // TODO:不晓得为何这里要放这个，实际接收并不会处理，后续看下
                ...(fieldSetting || {})
            }
        },
        /**
         * 将自定义js中配置的字段的追加DOM（也就是放在字段后面的dom）更新给字段对象
         * @param {Object} module 模型对象
         * @param {Object} renderField 含有自定义属性的对象
         */
        updateAppendRenderField (fieldList) {
            fieldList.forEach(i => {
                // 获取是否有配置
                let appendDom = this.diy_appendDom && this.diy_appendDom[i.name]
                // 更新给当前字段appendRender属性
                appendDom && this.$set(i, 'appendRender', appendDom.appendRender)
            })
        },
        /**
         * 将自定义js中配置的字段的自定义属性更新给字段对象
         * @param {Object} module 模型对象
         * @param {Object} renderField 含有自定义属性的对象
         */
        updateRenderField (fieldList) {
            // 定义表单对象数据，用于钻取使用
            let valueObj = {}
            // 根据字段数组更新表单对象数据
            fieldList.forEach(i => {
                valueObj[i.name] = i.value
            })
            fieldList.forEach(i => {
                let _fieldObj = this.getFormatterFieldObj(i, valueObj) || {}
                // 2021-08-16:将返回的formatter、click、render渲染到字段对象上
                _fieldObj && Object.keys(_fieldObj).forEach(j => {
                    j && this.$set(i, j, _fieldObj[j])
                })

                // 2021-08-16之前的做法，待删除
                // let {
                //     formatter, // 格式化展示
                //     click, // 点击事件,
                //     render, // 格式化+事件，可完成更复杂场景的字段自定义
                // } = _fieldObj
                // // 为字段对象添加属性formatter:自定义格式化展示
                // formatter && this.$set(i, 'formatter', formatter)
                // // 为字段对象添加属性click:自定义点击事件
                // click && this.$set(i, 'click', click)
                // // 为字段对象添加属性render: 格式化+事件，可完成更复杂场景的字段自定义
                // render && this.$set(i, 'render', render)
            })
            return module
        },
        /**
         * 使用自定义配置项数据
         * @param {Object} customSetting 自定义配置对象
         */
        updateCustomSetting (customSetting) {
            let {
                beforeFieldChange,
                beforeRender, // 页面渲染前钩子(已有表单字段数据)
                columnNum,  // 表单栅格排版列数
                fieldChange, // 字段值change事件(即将废弃)
                valueChange, // 字段值change事件(推荐)
                validateRule, // 字段校验规则
                renderField, // 自定义字段渲染与格式化
                appendDom, // 字段后追加DOM
                fieldCustom, // 表单字段组件内的专属自定义属性集合
                contentRender, // 整个表单内容的自定义排版
            } = customSetting || {}
            this.diy_beforeFieldChange = beforeFieldChange
            // 更新自定义-渲染前事件
            this.diy_beforeRender = beforeRender
            // 更新自定义-表单展示列数
            this.diy_columnNum = columnNum && parseInt(columnNum)
            // 更新自定义-字段change事件(即将废弃)
            this.diy_fieldChange = fieldChange
            // 更新自定义-字段change事件(推荐)
            this.diy_valueChange = valueChange
            // 更新自定义-字段校验规则
            this.diy_validateRule = validateRule
            // 更新自定义-字段渲染与格式化
            this.diy_renderField = renderField
            // 更新自定义-字段后追加DOM
            this.diy_appendDom = appendDom
            // 更新自定义-表单字段组件内的专属自定义属性集合
            this.diy_fieldCustom = fieldCustom
            // 更新自定义-整个表单内容的自定义排版
            this.diy_contentRender = contentRender
        },
    },
    // 可访问当前this实例
    created () {
    },
    // 挂载完成，可访问DOM元素
    async mounted () {
        // 使用自定义js配置项更新页面数据
        this.updateCustomSetting(this.customSetting)
        // 页面字段格式化前事件操作：判断是否存在自定义事件，存在则执行
        // TODO：尽量不使用
        if (this.diy_beforeFieldChange && typeof this.diy_beforeFieldChange === 'function') {
            this.diy_beforeFieldChange.call(this)
        }
        // 将接口字段转化为本地表单组件使用的字段属性
        let fieldList = await Change_To_Base_Form(this._fieldList, this.dic, this.changeOption)
        // 将传入的自定义表单数据更新到即将提交的表单字段数组中
        // 存在表单字段数组fieldList没有id项的情况，一般会从详情数据editJSON中才返回id
        // 但是编辑时不能没有id不然会被接口理解为新增，字段数据在此处统一添加id字段
        if (this.detailId) {
            // 若详情id存在则将id字段继续带过去
            fieldList.push({
                name: 'id',
                value: this.detailId,
                type: 'hidden'
            })
        }
        // 将自定义字段格式化更新给指定字段属性,只有查看可以使用renderField
        if (this.isView) {
            // 更新字段的自定义render函数展示与事件
            this.updateRenderField(fieldList)
        }
        // 更新appendDomRender（添加在字段后面的DOM）给字段对象属性
        this.updateAppendRenderField(fieldList)
        // 更新字段数组数据
        this._fieldList = fieldList
        // 将最新完整字段数据(包含值)抛回给父组件
        this.$emit('getFieldList', this._fieldList)
        // 页面渲染前事件操作：判断是否存在自定义事件，存在则执行
        if (this.diy_beforeRender && typeof this.diy_beforeRender === 'function') {
            this.diy_beforeRender.call(this, this._fieldList)
        }
        // 抛给父辈级组件使用
        this.$emit('beforeRender', this._fieldList, this)
        // 显示表单页面
        this.isReady = true
        // 表单参数已经准备齐全，下一刻即将渲染
        this.$emit('update:isFormStartRender', true)
        // 使用Vue事件总线：主要用于当前页面与挂载的自定义之间的数据交互
        // 将所有动态页面的子tab挂在当前主表作用域下
        EventBus.$on("getChildTabData", (res, name) => {
            // 更新数据
            this[name]= res
        })
    }
}
</script>
<style lang='scss' scoped>

</style>