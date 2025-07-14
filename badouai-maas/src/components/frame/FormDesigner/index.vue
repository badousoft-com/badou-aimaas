<template>
    <div class="form-designer" ref="formDesigner">
        <!-- 头部 -->
        <fd-header :title="title">
            <template v-slot:prev_title>
                <bd-icon name="applicationDesigner-fill"></bd-icon>
            </template>
            <template v-slot:content>
                <!-- 保存时间 -->
                <span class="fontS">最新保存时间: {{Format_Date('datetime')}}</span>
            </template>
            <template v-slot:right>
                <!-- 设计器操作按钮 -->
                <span class="m-mainOperate">
                    <span
                        v-for="(i, index) in mainOperateList"
                        :key="index"
                        class="m-mainOperate-item"
                        :title="!i.disabled ? i.label : '功能未开放'">
                        <bd-button
                            :disabled="i.disabled"
                            :loading="i.loading"
                            :icon="i.icon"
                            type="text"
                            class="m-mainOperate-item__btn"
                            @click="doHandler(i)">
                            {{i.label}}
                        </bd-button>
                    </span>
                </span>
                <span>
                    <!-- 主题选择区 -->
                    <el-select v-model="themeIndex" size="mini" placeholder="请选择主题">
                        <el-option
                            v-for="(i, index) in themeList"
                            :key="index"
                            :label="i.name"
                            :value="index">
                        </el-option>
                    </el-select>
                    <!-- 关闭设计器按钮 -->
                    <bd-icon
                        v-if="showCloseBtn"
                        name="multi"
                        class="s-close"
                        title="关闭表单设计器"
                        @click="closeFormDesigner">
                    </bd-icon>
                </span>
            </template>
        </fd-header>
        <!-- 内容区 -->
        <fd-body>
            <!-- 左侧组件区 -->
            <fd-sidebar
                :data="optionData"
                :formColumnNum="_formColumnPer"
                :class="{
                    'is-fold': isSiderBarFold
                }"
                categoryName="category"
                ref="sidebarRef"
                class="padHor"
                @appendItem="appendItem"
                @click.native="isSiderBarFold = false">
                <template v-slot:fold-show>
                    <!-- <div>组件库</div> -->
                </template>
                <template v-slot:drag-show-item="slotProps">
                    <drag-show-item 
                        v-if="slotProps.itemObj"
                        :formItem="slotProps.itemObj">
                    </drag-show-item>
                </template>
            </fd-sidebar>
            <fd-content>
                <template v-slot:header-left>
                    <!-- 左侧组件区域折叠图标 -->
                    <fold-sign direction="left" @click.native="isSiderBarFold = !isSiderBarFold"></fold-sign>
                </template>
                <template v-slot:header-right>
                    <!-- 右侧配置区域折叠图标 -->
                    <fold-sign  direction="right" @click.native="isSettingFold = !isSettingFold"></fold-sign>
                </template>
                <!-- 中间顶部操作区 -->
                <fd-toolbar
                    :data="toolbarList"
                    @handler="doHandler"
                    class="padding fontS">
                    <template v-if="hasHidenField">
                        <span class="fontS marR-10 ignoreHiden">
                            <bd-switch
                                v-model="ignoreHiden"
                                class="d-ib">
                            </bd-switch>
                            忽略隐藏项
                        </span>
                    </template>
                    <template v-slot:importJSON="slot">
                        <code-editor
                            class="fd-toolbar-item"
                            v-model="importJSONData"
                            @input="saveImportJSON">
                            <template>
                                <bd-icon :name="slot.item.icon"></bd-icon>
                                <span>{{slot.item.label}}</span>
                            </template>
                        </code-editor>
                    </template>
                    <template v-slot:createJSON="slot">
                        <code-editor
                            class="fd-toolbar-item"
                            v-model="_formObjFinally">
                            <template>
                                <bd-icon :name="slot.item.icon"></bd-icon>
                                <span>{{slot.item.label}}</span>
                            </template>
                        </code-editor>
                    </template>
                    <template v-slot:createCode="slot">
                        <code-editor
                            class="fd-toolbar-item"
                            v-model="_editPageCode"
                            lang="html">
                            <template>
                                <bd-icon :name="slot.item.icon"></bd-icon>
                                <span>{{slot.item.label}}</span>
                            </template>
                        </code-editor>
                    </template>
                </fd-toolbar>
                <!-- 中间表单区 -->
                <fd-form-panel @click.native="clickFormPanel($event)">
                    <!-- {{formFieldData}}<br/> -->
                    <!-- {{currentFieldAttrList}} -->
                    <div
                        v-if="_formObj.dataList.length === 0"
                        class="dragTip"
                        :class="{'allowDropFocus': allowDropStatus}"
                        @drop="blankPanelDrop" 
                        @dragover="allowDrop"
                        @dragleave="allowDropStatus = !allowDropStatus">
                        {{allowDropStatus? tipData.overTip : tipData.noData}}
                    </div>
                    <!-- 拖拽表单生成主区域 -->
                    <fd-drag-form
                        v-else
                        v-bind="_formObj"
                        :runGroupIndex.sync="runGroupIndex"
                        :groupProps="_groupProps"
                        :allowGroupActive="isGroupTabActive"
                        :columnNum="_formColumnPer"
                        :dataList.sync="_formObj.dataList"
                        :useDragTransition="useDragTransition"
                        :ignoreHiden="ignoreHiden"
                        @setFieldEditRun="setFieldEditRun_by_itemObj"
                        @setGroupEditRun="setGroupEditRun"
                        @spliceFieldList="spliceFieldList"
                        @setFieldSort="setFieldSort"
                        @deleteItemByGroupId="deleteItemByGroupId">
                    </fd-drag-form>
                </fd-form-panel>
            </fd-content>
            <fd-setting
                :data="settingTab.list"
                v-model="settingTab.active"
                :class="{
                    'is-fold': isSettingFold
                }"
                @click.native="isSettingFold = false">
                <template v-slot:fold-show>
                    <!-- <div>设置</div> -->
                </template>
                <!-- 右侧表单属性区 -->
                <template v-slot:form>
                    <fd-form-setting
                        v-model="formSetting">
                    </fd-form-setting>
                </template>
                <!-- 右侧组别属性区 -->
                <template v-slot:group>
                    <!-- 有数据时显示 -->
                    <fd-group-setting
                        v-if="formFieldData.length > 0"
                        v-model="currentGroupFieldList">
                    </fd-group-setting>
                    <!-- 无数据时展示 -->
                    <fd-group-setting v-else :value="[]">
                    </fd-group-setting>
                </template>
                <!-- 右侧字段属性区 -->
                <template v-slot:field>
                    <fd-field-setting
                        v-model="currentFieldAttrList"
                        :ref="fieldSettingRef"
                        @fieldChange="fieldChange"
                        @fieldSwitchChange="fieldSwitchChange">
                    </fd-field-setting>
                </template>
                <!-- 右侧设备属性区 -->
                <template v-slot:device>
                    <fd-device-setting></fd-device-setting>
                </template>
            </fd-setting>
        </fd-body>
    </div>
</template>
<script>
import FdHeader from './components/Header'
import FdBody from './components/Body'
import FdSidebar from './components/Sidebar'
import FdContent from './components/content/index.vue'
import FdToolbar from './components/content/Toolbar'
import FdFormPanel from './components/content/FormPanel'
import FdDragForm from './components/content/DragForm'
import FdSetting from './components/setting/index.vue'
import FdFieldSetting from './components/setting/Field.vue'
import FdFormSetting from './components/setting/Form.vue'
import FdDeviceSetting from './components/setting/Device.vue'
import FdGroupSetting from './components/setting/Group.vue'
import CodeEditor from '@/components/frame/Common/CodeEditor'
import DragShowItem from './components/content/DragShowItem'
import Queue from './components/Queue'
import FoldSign from './components/FoldSign'

import { Dic_Data } from '@/service/mockFormDesignerData.js'
import { Scroll_Top } from '@/utils/animate'
import themeList from './theme'
import { Has_Value, Get_UUID, Get_Data_By_Path } from '@/utils'
import { Get_TimeStamp, Format_Date } from '@/utils/time'
import { FormClass } from './class'
import { Deep_Clone } from '@/utils/clone'
import Get_Vue_Page_Component from './vuePageComponent'
import GlobalConst from '@/service/global-const'
import { List_Filter_Group_ByAttr } from '@/utils/list'
import Setting from './setting'
import { SimpleForm_To_WholeForm, Filter_Place_Field, Delete_Transition_Field, Show_Status } from './service/index'
import { Switch } from '@/components/frame/Common/MForm/components/items'
import getDicList from '@/service/get-dic'
import { Get_Column_Span } from '@/service/base-service'

// 定义右侧配置项键名
const SETTING_TAB_NAME = {
    field: 'field',
    form: 'form',
    device: 'device',
    group: 'group'
}
export default {
    name: 'form-designer',
    components: {
        [FdHeader.name]: FdHeader,
        [FdBody.name]: FdBody,
        [FdSidebar.name]: FdSidebar,
        [FdContent.name]: FdContent,
        [FdToolbar.name]: FdToolbar,
        [FdFormPanel.name]: FdFormPanel,
        [FdDragForm.name]: FdDragForm,
        [FdSetting.name]: FdSetting,
        [FdFieldSetting.name]: FdFieldSetting,
        [FdFormSetting.name]: FdFormSetting,
        [FdDeviceSetting.name]: FdDeviceSetting,
        [FdGroupSetting.name]: FdGroupSetting,
        [DragShowItem.name]: DragShowItem,
        [FoldSign.name]: FoldSign,
        [Switch.name]: Switch,
        CodeEditor,
    },
    mixins: [
        // 操作队列记录
        Queue,
    ],
    props: {
        // 设计器标题
        title: {
            type: String
        },
        // 表单属性对象
        formProps: {
            type: Object
        },
        // 是否展示关闭按钮，默认不展示
        showCloseBtn: {
            type: Boolean,
            default: false
        },
        // 字段数据值
        value: {
            type: Array,
            default: () => []
        },
        // 字段键名option数据
        nameOption: {
            type: Array,
            default: () => []
        },
        // 校验规则option数据
        rulesOption: {
            type: Array,
            default: () => []
        },
        // 保存函数
        saveFun: {
            type: Function
        },
        // 是否传入完整的表单数据
        //     1. 简单：表单项为object
        //     2. 完整：表单项为数组，数组下项为Object
        isWhole: {
            type: Boolean,
            default: false
        },
    },
    data () { // 定义页面变量
        return {
            // 定义右侧-表单设置区tab键名
            tabFormName: SETTING_TAB_NAME.form,
            // 定义右侧-组别设置去tab键名
            tabGroupName: SETTING_TAB_NAME.group,
            // 定义右侧-字段设置区tab键名
            tabFieldName: SETTING_TAB_NAME.field,
            // 定义右侧-设备设置区tab键名
            tabDeviceName: SETTING_TAB_NAME.device,
            // 定义右侧tab区数据
            settingTab: {
                list: [
                    { text: '表单属性', id: SETTING_TAB_NAME.form },
                    { text: '组别', id: SETTING_TAB_NAME.group },
                    { text: '字段属性', id: SETTING_TAB_NAME.field },
                    // { text: '设备', id: SETTING_TAB_NAME.device }
                ],
                // 设置项活跃tab，默认展示表单属性
                active: SETTING_TAB_NAME.form
            },
            // 主题列表
            themeList: themeList,
            // 已选主题方案
            themeIndex: 0,
            // 定义操作提示
            tipData: {
                noData: '从左侧拖拽或者点击来添加元素',
                overTip: '松开鼠标放置元素'
            },
            // 在空消息板上放置的状态：用于状态修改
            allowDropStatus: false,
            // 表单对象信息
            formSetting: [],
            // 字段区域refName
            fieldSettingRef: 'fieldSetting',
            tempDicData: Dic_Data,
            // 当前活跃的组别下角标,-1表示没有活跃项
            runGroupIndex: -1, 
            // 当前活跃的组别字段数组，用于右侧组别信息展示
            currentGroupFieldList: new FormClass.AddGroup().data,
            // 拖拽交换位置释放鼠标时是否启用动画
            useDragTransition: false,
            // 导入的json数据
            importJSONData: '',
            // 左侧组件数据
            optionData: [],
            // 日期格式化函数
            Format_Date: Format_Date,
            // 核心表单源数据
            formFieldData: [],
            // 定义内容区操作按钮
            mainOperateList: [
                // 属性说明：
                //     disabled: 按钮是否生效：例如撤销与重做需要满足一定场景才可以使用；不可能用时灰度展示
                //     handler: 事件名称
                { id: 'save', label: '发布', icon: 'send', handler: 'handlePublish', disabled: true },
                { id: 'save', label: '保存', icon: 'save', handler: 'handleSave', disabled: false, loading: false },
                { id: 'preview', label: '预览', icon: 'preview-d', handler: 'handlePreview', disabled: false },
            ],
            // 左侧组件库是否为折叠状态
            isSiderBarFold: false,
            // 右侧tab配置项面板是否为折叠状态
            isSettingFold: false,
            // 是否忽略隐藏项
            ignoreHiden: false,
        }
    },
    computed: {
        // 表单类型Class选项配置
        _fieldClassOption () {
            return {
                // 字段键名数组
                nameOption: this.nameOption,
                // 校验规则数组
                rulesOption: this.rulesOption,
                // 数据字典集合
                dicData: Dic_Data
            }
        },
        // 当前表单是否含有隐藏字段
        hasHidenField () {
            if (!(this._formObj && this._formObj.dataList)) return
            return this._formObj.dataList.filter(i => !Show_Status(i)).length > 0
        },
        // 获取完整的表单属性数据
        _formProps: {
            get () {
                // this.formProps中支持传入title，当前组件也可单独传入title，这里需要合并属性
                let temp_formProps = Object.assign({}, this.formProps)
                // 判断是否传入title，优先使用该标题
                if (this.title) {
                    temp_formProps.title = this.title
                }
                return temp_formProps
            },
            set (val) {
                // 更新表单属性对象
                this.$emit('update:formProps', val)
                // 更新标题
                this.$emit('update:title', val && val.title)
            }
        },
        // 判断当前是否tab为组别tagb
        isGroupTabActive () {
            return this.settingTab.active === this.tabGroupName
        },
        // 获取内容区展示的表单字段数组
        real_formFieldData () {
            if (this.formFieldData) {
                // 过滤不展示的字段，注意这里的不展示不是值hidden为true的，这一类字段在设计时是需要显示出来的
                //    这里的字段是指通过Setting.Place设置的，为true的，在设计阶段也不需要展示出来的模块
                return this.formFieldData.filter(i => {
                    let placeField = this.getField(i, Setting.Place)
                    return !(placeField && placeField.value)
                })
            }
            return []
        },
        // 获取当前正在操作的字段的属性数组
        currentFieldAttrList: {
            get () {
                let _fieldList = this.getFieldByAttr('editRun', true)
                if (!_fieldList) {
                    // 如果当前没有活跃的字段项，然后右侧又是字段tab为活跃，则此时设置选中为form Tab
                    if (this.settingTab.active === SETTING_TAB_NAME.field) {
                        this.settingTab.active = SETTING_TAB_NAME.form
                    }
                    return
                }
                return _fieldList
            }
        },
        // 获取组别信息字段键（Array格式）
        _groupProps () {
            return this.currentGroupFieldList.map(i => i.name)
        },
        // 获取当前所有组别【去重后】的的id值（Array格式）
        _allGroupId () {
            let groupIds = this.formFieldData.map(i => {
                let _groupIdField = this.getField(i, Setting.GroupId)
                return _groupIdField && _groupIdField.value
            })
            return Array.from(new Set(groupIds))
        },
        // 获取当前正在操作的组别id
        _currentGroupId () {
            return this._allGroupId &&
                   this._allGroupId.length > 0 &&
                   this._allGroupId[this.runGroupIndex]
        },
        // 获取当前正在操作的组别对象数据
        _currentGroupObj () {
            return this.currentGroupFieldList.reduce((result, i) => {
                let { name, value } = i
                result[name] = value
                return result
            }, {})
        },
        // 获取内容区的表单属性与字段数组
        _formObj: {
            get () {
                // 定义结果值
                let result = {}
                // 遍历获取表单基础属性，更新给结果值
                this.formSetting.forEach(i => {
                    i.name && (result[i.name] = i.value)
                })
                // 设置组别相关信息
                result.groupProps = this._groupProps
                // 设置属性字段数组
                result.dataList = Deep_Clone(this.formFieldData).map(i => {
                    let _itemObj = {}
                    i.filter(j => !j.destroyed).forEach(k => {
                        let value = k.value
                        // 初始默认将值赋给字段
                        _itemObj[k.name] = value
                        switch (k.name) {
                            // 如果是自定义配置项
                            case 'customOptions':
                                let _customOption = null
                                try {
                                    _customOption = value && JSON.parse(value)
                                } catch (e) {
                                    console.error(`customOption值转化失败: 传入值为${value}`)
                                }
                                if (_customOption) {
                                    // 将配置项数据更新给字段对象
                                    Object.assign(_itemObj, _customOption)
                                }
                            default:
                                // do something
                        }
                    })
                    return _itemObj
                })
                // !!!!!!!!!!!
                // 这里组装的数据是面向基础表单的
                // !!!!!!!!!!!
                result.dataList.forEach(i => {
                    // 根据设计器中的编辑类型，转化成基础表单需要的表单类型
                    i.type = this.getType(i[Setting.Name_EditType])
                })
                return result
            },
            set (val) {

            }
        },
        // 去除组别占位字段，返回真正需要渲染时的字段数据
        _formObjFinally () {
            let { dataList, ...formData } = this._formObj
            if (!dataList) return {}
            // 过滤组别字段数据，删除字段对象下的冗余配置属性
            formData.dataList = Delete_Transition_Field(Filter_Place_Field(dataList))
            // 清除options
            formData.dataList.forEach(i => {
                if ('options' in i) {
                    delete i.options
                }
            })
            return formData
        },
        // 获取当前表单使用的单项占比份数
        _formColumnPer () {
            return parseInt(this._formObj && this._formObj.columnNum || 0)
        },
        // 生成代码的对象
        _editPageCode: {
            get () {
                return Get_Vue_Page_Component(JSON.stringify(this._formObjFinally, null, 4))
            },
            set (val) {}
        },
        // 定义内容区操作按钮
        toolbarList () {
            return [
                // 属性说明：
                //     disabled: 按钮是否生效：例如撤销与重做需要满足一定场景才可以使用；不可能用时灰度展示
                //     handler: 事件名称
                { id: 'unDo', label: '撤销', icon: 'unDo-d', handler: 'handleUnDo', disabled: this.queue.list.length <= 1 },
                { id: 'reDo', label: '重做', icon: 'reDo-d', handler: 'handleReDo', disabled: this.queue.unDoList.length === 0 },
                { id: 'clear', label: '清空', icon: 'clear-d', handler: 'handleClear', disabled: this.formFieldData.length === 0 },
                { id: 'importJSON', label: '导入JSON', icon: 'importJSON-d', handler: 'importJSON', disabled: true, isMore: true },
                { id: 'createJSON', label: '生成JSON', icon: 'getJSON2-d', handler: 'createJSON', disabled: false, isMore: true },
                { id: 'createCode', label: '生成代码', icon: 'getCode-d', handler: 'createCode', disabled: false, isMore: true },
            ]
        }
    },
    methods: { // 定义函数
        /**
         * 切换表单项生效与否的change事件
         * @param {String} 切换的字段键名
         * @param {Boolean} 切换后的值 true/false
         * @param {Object} 切换字段对象数据
         * @param {Object} 切换字段所在的表单数据
         */
        fieldSwitchChange (name, switchVal, field, formObj) {
            // 获取表单项数据（Array,子项为属性对象
            let fieldList = this.formFieldData.find(i => i.find(j => j.name === 'name').value === formObj.name)
            // 获取操作的属性项对形象
            let columnPerField = fieldList.find(i => i.name === name)
            // 判断是否为占比份数
            if (name === 'columnPer') {
                // 若切换为true
                if (switchVal) {
                    // 获取所在字段对象值
                    let field = this.currentFieldAttrList.reduce((res, item) => {
                        let { name, value } = item
                        res[name] = value
                        return res
                    }, {})
                    // 打开配置后，需要动态计算默认应该展示的份数,而不是直接从0开始
                    columnPerField.value = Get_Column_Span(field, {}, this._formColumnPer)
                } else {
                    // 关闭时，清空自定义的份数值
                    columnPerField.value = null
                }
            }
        },
        /**
         * 根据设计器中的表单编辑类型 获取 基础表单的编辑类型
         * @param {String} editType 设计器中的表单编辑类型
         */
        getType (editType) {
            let _type = editType
            switch (_type) {
                // 解决表单类型-数字展示异常的问题，展示成inputNumber格式的异常
                case 'number':
                    _type = 'text'
                    break
                case 'time':
                case 'datetime':
                case 'year':
                case 'month':
                case 'daterange':
                case 'datetimerange':
                    _type = 'date'
                    break
                default:
                    // do something
            }
            return _type
        },
        /**
         * 保存导入json数据的回调事件
         * @param {String} data 字段列表数据json(String格式)
         */
        saveImportJSON (data) {
            let jsonObj = null
            // 转化json数据，获取对象
            try {
                jsonObj = data && JSON.parse(data)
            } catch (e) {}
            if (!jsonObj) return
            if (!('dataList' in jsonObj)) return
            // 获取字段数据
            let _dataList = jsonObj.dataList
            // 更新表单数据（将简单json表单转成完整配置字段数据的表单）
            this.formFieldData = SimpleForm_To_WholeForm.call(this, _dataList, this.optionData)
            // 将操作添加进操作队列历史，【撤销/重做】逻辑将使用该数据
            this.addDoQueue()
        },
        /**
         * 点击表单面板事件
         * @param {Object} event 点击节点对象
         */
        clickFormPanel (event) {
            // 限制只能父级触发，避免子元素触发
            if (event.currentTarget.className === 'fd-form-panel') {
                // 点击内容区除开组别区域的空白区域时,取消组别的活跃状态
                this.settingTab.active = this.tabFormName
            } 
        },
        /**
         * 根据组别id删除对应组别下的数据项
         * @param {String} groupId 组别id
         * @param {Number} groupIndex 组别下角标
         */
        deleteItemByGroupId (groupId, groupIndex) {
            // 根据传入的删除组别id-groupId，过滤掉删除的数据项
            this.formFieldData = this.formFieldData.filter(i => this.getField(i, Setting.GroupId).value !== groupId)
            // 设置tab切换为展示【组别属性】栏
            this.settingTab.active = SETTING_TAB_NAME.group
            // 添加延时，使计算属性_allGroupId的值在上面formFieldData修改的时候能及时更新
            setTimeout(() => {
                // 判断删除后是否无组别数据
                if (this._allGroupId.length === 0) {
                    this.runGroupIndex = -1
                } else {
                    // 如果删除的是组别中排序最后一个，那删除后就取前一个为活跃项
                    if (groupIndex === this._allGroupId.length) {
                        this.runGroupIndex = groupIndex - 1
                    } else {
                        // 删除非最后一个，默认取前一个为活跃项
                        this.runGroupIndex = groupIndex
                    }
                }
            })
            // 将操作添加进操作队列历史，【撤销/重做】逻辑将使用该数据
            this.addDoQueue()
        },
        /**
         * TODO：更新字段options数据值
         */
        fieldChange () {
            // 获取函数所有参数值
            let _funParams = Array.from(arguments)
            // 获取参数
            //     @param {Object} formScope 表单所在页面作用域
            //     @param {String} fieldName 字段键名
            //     @param {*} value 字段值
            let [formScope, fieldName, value, ...formItemParams] = _funParams
            // 处理【编辑类型】变更的逻辑
            if (fieldName === Setting.Name_EditType && value) {
                // 获取当前切换的表单编辑类型，找到在左侧表单项中对应的项下角标
                let optionItemIndex = this.optionData.findIndex(i => i.optionId === value)
                if (!~optionItemIndex) return
                // 获取当前操作的主字段在中间核心表单中的下角标
                let insertIndex = this.formFieldData.findIndex(i => i.find(j => j.name === 'name').value === formScope.dataForm.name)
                // 获取当前操作的字段对象详情值
                let fieldItemData = this._formObj.dataList.find(i => i.name === formScope.dataForm.name)
                
                /**
                 * 特殊处理：处理数据来源：有值时=====start
                 */
                let _optionItem = this.optionData.find(i => i.optionId === value)
                if (_optionItem && new _optionItem.attrClass().list.find(i => i.name === 'dataSource')?.value) {
                    // 当编辑类型切换触发这里逻辑时，需要等待右侧所有字段的变更，以便拿到所有变更后的字段数据
                    setTimeout(() => {
                        // 当前右侧字段总对象
                        let _currentField = this.getFieldByAttr('name', formScope.dataForm.name)
                        this.setFieldOption(_currentField, false)
                    }, 800)
                }
                /** 处理数据来源：有值时=====end */

                /**
                 * 特殊处理：处理日期类型=====start
                 *      判断当前切换的是否是【编辑类型】并且是【日期相关类型】
                 *      是则清空【展示值格式showFormat】与【值格式valueFormat】
                 *      避免切换日期类型时继承了之前的格式值导致错误
                 */
                let dateField = Setting.Date_Type_Option.find(i => i.id === value)
                if (dateField) {
                    let { showFormat, valueFormat } = dateField
                    let _showFormat = Setting.Name_Field.showFormat
                    let _valueFormat = Setting.Name_Field.valueFormat
                    // 设置值
                    fieldItemData[_showFormat] = showFormat
                    fieldItemData[_valueFormat] = valueFormat
                }
                /** 处理日期类型=====end */

                // 根据找到的左侧对应的组件项，生成新的字段对象数据
                let _fieldValObj = {
                    ...this._fieldClassOption,
                    ...fieldItemData,
                    [Setting.Name_EditType]: value
                }
                // 删除customOptions
                delete _fieldValObj.customOptions
                // 获取切换类型前的旧值oldValue
                let [fieldObj, currentOption, options, {oldValue}] = formItemParams
                fieldObj, currentOption, options // eslint规则占位，此句暂无意义
                // 如果旧值为隐藏，则删除隐藏的属性，避免切换类型后默认勾选了隐藏属性
                if (oldValue === 'hidden') delete _fieldValObj.hidden
                // 获取表单项类attrClass
                // 获取表单项的默认属性值对象defaultAttr
                let { attrClass, defaultAttr } = this.optionData[optionItemIndex]
                // 切换表单项类型时以表单项的默认属性值为主,不以值为主，所以defaultAttr放后面
                _fieldValObj = Object.assign({}, _fieldValObj, defaultAttr)
                // 获取切换后的表单项
                let attrItem = new attrClass(_fieldValObj).list
                // 添加到核心内容区表单
                this.formFieldData.splice(insertIndex, 1, attrItem)
            }
            // 处理【date时子类型切换】变更的逻辑
            if (fieldName === Setting.Name_Field.dateType) {
                // 根据切换的date 子类型数据，获取该类型对应的数据项
                let _currentDateType = Setting.Date_Type_Option.find(i => i.id === value)
                // 获取该类型下的展示格式showFormat，值格式valueFormat
                let { showFormat = '', valueFormat = '' } = _currentDateType || {}
                // 获取操作的字段
                let _currentField = this.formFieldData.find(i => i.find(j => j.name === 'name').value === formScope.dataForm.name)
                // 获取该字段下的展示格式字段对象
                let _showFormatField = _currentField.find(i => i.name === 'showFormat')
                // 获取该字段下的值格式字段对象
                let _valueFormatField = _currentField.find(i => i.name === 'valueFormat')
                // 更新值
                _showFormatField.value = showFormat
                _valueFormatField.value = valueFormat
            }
            // options相关字段的变更触发
            // !!!!!数据来源选择为模型时，需要配置数据Bean，所以设计器不可见，如果没配置Bean可能页面也不可见
            if (fieldName === Setting.Name_Field.dataSource ||
                fieldName === Setting.Name_Field.dataBean ||
                fieldName === Setting.Name_Field.dataFunction ||
                fieldName === Setting.Name_Field.dicName) {
                // 当前右侧字段总对象
                let _currentField = this.getFieldByAttr('name', formScope.dataForm.name)
                // 设置变更属性所在的字段对象
                this.setFieldOption(_currentField, true)
            }
        },
        /**
         * 设置字段属性
         * @param {Array} fieldAttr 字段下属性数组
         * @param {Boolean} isChange 当前是否change字段而触发的
         */
        setFieldOption (fieldAttr, isChange = false) {
            // 获取【数据来源】字段
            let _dataSourceField = this.getField(fieldAttr, Setting.Name_Field.dataSource)
            if (!_dataSourceField) return
            // 获取【数据来源Bean】字段对象
            let _dataBeanField = this.getField(fieldAttr, Setting.Name_Field.dataBean)
            // 获取【数据来源Bean方法】字段对象
            let _dataFunctionField = this.getField(fieldAttr, Setting.Name_Field.dataFunction)
            // 获取【数据字典/模型编码】字段对象
            let _dicNameField = this.getField(fieldAttr, Setting.Name_Field.dicName)
            // options字段
            let _optionsField = this.getField(fieldAttr, Setting.Name_Field.options)
            // optionResPath路径
            let _optionResPathField = this.getField(fieldAttr, Setting.Name_Field.optionResPath)
            let that = this
            /**
             * 设置实际的options(options值通过接口返回时可能为对象，需要再进一步获取对象下的属性值作为实际options值)
             * @param {Array, Object} optionsObj 请求获取到的options数据对象
             */
            function setRealOption (optionsObj) {
                // 只有当路径有值，且获取到的options数据是对象时，执行获取最终options的逻辑
                if (_optionResPathField?.value &&
                    optionsObj &&
                    optionsObj.constructor === Object) {
                    _optionsField.value = Get_Data_By_Path(optionsObj, _optionResPathField.value)
                } else {
                    _optionsField.value = optionsObj
                }
            }
            // 定义隐藏字段方法
            function toggleHide (field, status = true) {
                if (!status) {
                    that.$set(field, 'hidden', false)
                } else {
                    field.value = ''
                    that.$set(field, 'hidden', true)
                }
            }
            // 清空options数据
            _optionsField.value = []
            // 判断数据来源是否有值，无值则隐藏相关属性字段
            if (!_dataSourceField.value) {
                toggleHide(_dataBeanField)
                toggleHide(_dataFunctionField)
                toggleHide(_dicNameField)
            } else {
                // 获取数据来源的值
                let _dataSourceVal = _dataSourceField.value.toString()
                // 模型
                if (_dataSourceVal === '3') {
                    // 若当前正在执行change字段值，则提示
                    if (isChange) {
                        this.$alert(`【数据来源】选择【模型】时，在当前设计器的预览状态下是无法看到选项数据，可以前往实际的编辑页面进行查看`, `提示`)
                    }
                    toggleHide(_dicNameField, false) // 显示数据字典编码
                    toggleHide(_dataBeanField, false) // 显示数据来源Bean
                    toggleHide(_dataFunctionField) // 显示数据来源Bean方法
                    // 获取编辑类型字段
                    let _editTypeField = this.getField(fieldAttr, Setting.Name_EditType)
                    // 判断是否为弹窗列表-编辑类型
                    if (_editTypeField.value === 'dialogList') {
                        // 获取模型编码字段
                        let _mdCodeField = this.getField(fieldAttr, Setting.Name_Field.mdCode)
                        // 将模型编码字段的值同步为数据字典/模型编码值
                        _mdCodeField.value = _dicNameField.value
                    } else {
                        if (Has_Value(_dataFunctionField.value) && Has_Value(_dicNameField.value)) {
                            // 暂时不做TODO
                        }
                    }
                // 自定义
                } else if (_dataSourceVal === '2') {
                    toggleHide(_dicNameField) // 隐藏数据字典编码
                    toggleHide(_dataBeanField) // 隐藏数据来源Bean方法
                    toggleHide(_dataFunctionField, false) // 显示数据来源Bean方法
                    if (_dataFunctionField.value) {
                        getDicList({
                            url: _dataFunctionField.value
                        }).then(res => {
                            // 设置options值
                            setRealOption(res)
                        })
                    }
                // 数据字典
                } else if (_dataSourceVal === '1') {
                    toggleHide(_dicNameField, false) // 显示数据字典编码
                    toggleHide(_dataBeanField) // 隐藏数据来源Bean
                    toggleHide(_dataFunctionField) // 隐藏数据来源Bean方法
                    if (_dicNameField.value) {
                        getDicList(_dicNameField.value).then(res => {
                            // 设置options值
                            setRealOption(res)
                        })
                    }
                }
            }
        },
        /**
         * 根据字段键名与值，从核心表单数组this.formFieldData中找到对应项
         * @param {String} _attrName 字段键名
         * @param {*} _val 字段对应值
         * @param {Object} option 配置项
         *      {
         *           hasIndex: 是否需要返回索引，配置此将返回对象，属性含index与item
         *      }
         */
        getFieldByAttr (_attrName, _val, option) {
            if (!_attrName) return
            // 获取匹配字段与值的模块所处下角标
            let _index = this.formFieldData.findIndex(i => i.find(j => j.name === _attrName).value === _val)
            if (!~_index) return
            // 获取是否需要返回索引，默认不返回
            let {
                hasIndex
            } = Object.assign({
                hasIndex: false,
            }, option)
            // 获取匹配项
            let _item = this.formFieldData[_index]
            // 不返回是否需要返回匹配项的下角标，则返回匹配项
            if (!hasIndex) return _item
            // 返回对象；
            return {
                index: _index,
                item: _item
            }
        },
        // 关闭表单设计器
        closeFormDesigner () {
            this.$emit('close')
        },
        // 允许将数据/元素放置到其他元素中
        allowDrop (ev) {
            // 空数据面板高亮状态
            this.allowDropStatus = true
            ev.preventDefault()
        },
        // 空白内容板面drop时触发
        blankPanelDrop (ev) {
            ev.preventDefault()
            this.$refs.sidebarRef.appendItemByDragIndex()
            // 恢复空数据面板状态
            this.allowDropStatus = false
        },
        // 按照函数名称执行指定函数
        doHandler (item) {
            this[item.handler].call(this, item)
        },
        // 获取组别名称,总规则如下：
        //     默认生成组别名如下：基本信息，组别1，组别2，组别3
        //     1. 无组别时，使用系统提供的基础组别名称 GlobalConst.form.groupName
        //     2. 从第二个组别开始，默认使用【GlobalConst.form.nextGroupName + 数字(逐级加1)】,如组别1，此时再新建组别，那默认就是组别2
        getGroupName () {
            // 当前无组别时，默认返回组别名称
            if (this.formFieldData.length === 0) return GlobalConst.form.groupName
            // 定义下一组别的名称正则
            let nextNameReg = new RegExp('^' + GlobalConst.form.nextGroupName + '[0-9]+' )
            // 获取所有组别名称，再根据下一组别正则匹配，获取到所有使用下一组别规则生成的组别名，方便等下+1
            let nextNames = this.formFieldData.map(i => {
                let _groupField = i.find(j => j.name === 'groupName')
                if (_groupField) return _groupField.value
                return ''
            }).filter(i => i && nextNameReg.test(i))
            // 判断当前是否还没有下一组别名，则返回下一组别名+1，如组别1
            if (nextNames.length === 0) {
                return `${GlobalConst.form.nextGroupName}1`
            }
            // 获取组别中符合下一组别规则的组名，提取出组别名中的索引号
            let groupIndexList = nextNames.map(i => i.replace(GlobalConst.form.nextGroupName, ''))
            // 返回索引号+1的下一级别默认足别名
            return GlobalConst.form.nextGroupName + (Math.max(...groupIndexList) + 1)
        },
        /**
         * 添加数据项进内容区
         * @param {Number} optionItemIndex 添加的数据来源与左侧数组的下角标，用于取出数据
         * @param {Number} insertIndex 在内容区数组中即将插入的下角标位置，用于插入数据中
         * @param {Number} delNum 删除数
         */
        appendItem (optionItemIndex, insertIndex, delNum = 0) {
            // 从左侧数据中获取正在操作的数据项
            let _optionItem = this.optionData[optionItemIndex]
            // 实例类，生成实例字段对象
            let attrItem = new _optionItem.attrClass(this._fieldClassOption).list
            // 定义当前表单数据长度
            let isNoData = this.formFieldData.length === 0
            // 定义当前添加项是否为组别字段
            let isGroupField = this.getField(attrItem, Setting.Name_EditType).value === 'group'
            // 定义获取组别对象信息的函数
            function getGroupInfoFun () {
                return {
                    groupName: this.getGroupName(),
                    groupId: Get_UUID(), // 获取组别id
                }
            }
            // 当前添加项的场景分为以下
            // 1. 添加的是组别字段，则添加一个组别字段（占位不显示，方便组别呈现）
            // 2. 添加的是非组别字段
            //     2.1 判断当前表单数据是否为空，为空则先添加组别，再添加字段
            //     2.2 不为空时，在对应操作的组别中添加字段

            // 判断当前操作的是否为组别字段
            if (isGroupField) {
                // 是组别字段，更新当前操作字段的组别信息
                this.updateItemGroupField(attrItem, getGroupInfoFun.call(this))
                // 动态设置字段键名
                this.setFieldName(attrItem)
                // 添加组别字段（占位，不显示）
                this.formFieldData.push(attrItem)
                // 设置tab切换为展示【组别属性】栏
                this.settingTab.active = SETTING_TAB_NAME.group
            } else {
                // 判断当前表单数据是否有值
                if (isNoData) {
                    // 获取组别对象数据
                    let _groupObj = getGroupInfoFun.call(this)
                    // 获取组别字段
                    let _group = new FormClass.Group(_groupObj).list
                    // 动态设置字段键名
                    this.setFieldName(_group)
                    // 更新字段的组别信息
                    this.updateItemGroupField(attrItem, _groupObj)
                    // 设置当前为活跃项
                    this.setItemEditRun(attrItem)
                    // 动态设置字段键名
                    this.setFieldName(attrItem)
                    // 添加新的组别字段，再添加操作字段
                    this.formFieldData.push(_group, attrItem)
                } else {
                    this.updateItemGroupField(attrItem, this._currentGroupObj)
                    // 设置当前为活跃项
                    this.setItemEditRun(attrItem)
                    // 动态设置字段键名
                    this.setFieldName(attrItem)
                    // 深拷当前的字段列表数据
                    let _dataList = Deep_Clone(this._formObj.dataList)
                    // 根据组别划分为二维数组
                    let _dataListByGroup = List_Filter_Group_ByAttr(_dataList, 'groupName')
                    // 在组别下构造字段
                    //     startIndex: 组别下的起始下角标（这里下角标是相对一维时的排序）
                    //     endIndex: 组别下的已有数据的结束下角标（同上）
                    _dataListByGroup.forEach((i, index) => {
                        // 第一块：起始为0，结束为组别下的数据长度
                        if (index === 0) {
                            i.startIndex = 0
                            i.endIndex = i.list.length - 1
                        } else {
                            // 后续模块，起始下角标为前一组别的结束下角标开始算起
                            //          结束下角标依旧为起始+当前组别下数据长度
                            let { endIndex } = _dataListByGroup[index - 1]
                            i.startIndex = endIndex + 1
                            i.endIndex = endIndex + 1 + i.list.length - 1
                        }
                    })
                    // 获取当前操作模块的起始下角标
                    let { startIndex, endIndex } = _dataListByGroup[this.runGroupIndex]
                    // 获取插入的下角标顺序
                    //     insertIndex有值表示是通过drop方式添加的指定了放置的下角标，没有表示是通过点击添加的
                    let _insertIndex = (Has_Value(insertIndex) ? insertIndex + startIndex : endIndex + 1)
                    // 添加数据
                    this.formFieldData.splice(_insertIndex, delNum, attrItem)
                }
            }
            // 更新当前活跃组别的字段信息
            this.currentGroupFieldList.forEach(i => {
                i.value = attrItem.find(j => j.name === i.name).value
            })
            // 根据当前操作的数据更新活跃的组别下角标
            this.runGroupIndex = this.getGroupIndex_byItem(attrItem)
            // 将操作添加进操作队列历史，【撤销/重做】逻辑将使用该数据
            this.addDoQueue()
        },
        /**
         * 根据字段获取所在组别索引
         * @param {Array} item 字段
         */
        getGroupIndex_byItem (item) {
            // 获取字段所在组别的id
            let _groupId = this.getField(item, Setting.GroupId).value
            // 获取字段所在组别索引
            return this._allGroupId.findIndex(i => i === _groupId) || 0
        },
        /**
         * 根据字段键名获取字段对象数据
         * @param {Array} list 源数组数据
         * @param {String} name 匹配的值
         * @param {Object} option 配置项
         *     {
         *          attrName: 匹配的属性名
         *          hasIndex: 是否需要返回匹配项的下角标
         *     }
         */
        getField (list, name, option) {
            let {
                attrName,
                hasIndex
            } = Object.assign({
                attrName: 'name',
                hasIndex: false
            }, option)
            if (!(list && list.length > 0 && name)) return {}
            let _index = list.findIndex(i => i[attrName] === name)
            if (!~_index) return 
            if (!hasIndex) return list[_index]
            return {
                index: _index,
                item: list[_index]
            }
        },
        /**
         * 设置内容区的活跃项
         * @param {Array} item 表单项(属性数组),item为空时表示取消所有选中活跃状态
         */
        setItemEditRun (item) {
            // 设置其他字段的活跃状态为false
            this.formFieldData.forEach(i => {
                i.find(j => j.name === 'editRun').value = false
            })
            if (!item) return
            // 设置当前字段的活跃状态为true
            item.find(j => j.name === 'editRun').value = true
            // 设置tab切换为展示【字段属性】栏
            this.settingTab.active = SETTING_TAB_NAME.field
            // 字段属性滚动重置回顶部
            setTimeout(() => {
                let fieldSettingVueComp = this.$refs[this.fieldSettingRef]
                fieldSettingVueComp &&
                fieldSettingVueComp.$parent &&
                // 滚动回顶部
                Scroll_Top(fieldSettingVueComp.$parent.$el, 100)
            })
        },
        // 设置组别活跃
        setGroupEditRun () {
            // 设置tab切换为展示【组别属性】栏
            this.settingTab.active = SETTING_TAB_NAME.group
        },
        /**
         * 更新字段的组别属性
         * @param {Array} itemFieldList 字段
         * @param {Object} groupObj 组别对象数据
         */
        updateItemGroupField (itemFieldList, groupObj) {
            // 获取键数组
            let groupFieldList = Object.keys(groupObj)
            // 遍历更新字段属性数据
            groupFieldList.forEach(i => {
                let _itemField = this.getField(itemFieldList, i)
                _itemField.value = groupObj[i]
            })
        },
        /**
         * 操作表单列表 - 复制子项 / 删除子项
         * @param [Number] index 操作数组下角标
         * @param [Boolean] status 操作类型状态  0-新增 / 1-删除 (类比Array.splice语法)
         * @param [Object] item 操作对象
         */
        setFieldEditRun_by_itemObj (item) {
            let _fieldAttr = this.formFieldData.find(i => i.find(j => j.name === 'name').value === item.name)
            this.setItemEditRun(_fieldAttr)
        },
        spliceFieldList (status, name) {
            // 获取匹配项所处的下角标与项
            let { index, item } = this.getFieldByAttr('name', name, { hasIndex: true })
            // 0-复制，1-删除
            if (status === 0) {
                // 复制对象数据
                let copyItem = this.copyItem(item)
                // 插入数据 index+1表示在当前数据位置，再其之后追加数据
                this.formFieldData.splice(index + 1, 0, copyItem)
                // 设置延时，使复制生成的数据项成为选中活跃项
                setTimeout(() => {
                    this.setItemEditRun(copyItem)
                    // 将操作添加进操作队列历史，【撤销/重做】逻辑将使用该数据
                    this.addDoQueue()
                })
            } else {
                // 删除对象数据
                this.formFieldData.splice(index, 1)
                // 删除当前活跃项时，活跃状态需要切换
                // 1. 当前已无数据，不设置活跃状态
                // 2. 使用删除项的下一条数据为活跃状态
                // 3. 若删除项为最后一项，则往前寻找前一项数据作为活跃项
                setTimeout(() => {
                    // 获取当前剩余数据长度
                    let listLen = this.formFieldData.length
                    // 当删除某条数据后，剩余数据不为空时，需要将数据项的活跃选中状态赋予其他某一数据
                    if (listLen > 0) {
                        // index为原数组中删除数据的下角标，当该值与删除后的数组长度一致时说明删除项为最后一项
                        let _item = listLen === index ?
                                    // 删除项无后续项，设置前一项被选中
                                    this.formFieldData[index - 1] :
                                    // 默认使用删除项的后一项，设置其活跃状态被激活
                                    this.formFieldData[index] 
                        // 判断当前操作字段是否为组别字段
                        if (this.getField(_item, Setting.Name_EditType).value !== 'group') {
                            this.setItemEditRun(_item)
                        } else {
                            // 删除数据后数组为空时, 设置tab切换为展示【表单属性】栏
                            this.settingTab.active = SETTING_TAB_NAME.form
                        }
                    } else {
                        // 删除数据后数组为空时, 设置tab切换为展示【表单属性】栏
                        this.settingTab.active = SETTING_TAB_NAME.form
                    }
                    // 将操作添加进操作队列历史，【撤销/重做】逻辑将使用该数据
                    this.addDoQueue()
                })
            }
        },
        /**
         * 复制表单子项
         */
        copyItem (item) {
            // 设置复制标识
            let addSign = '_copy_'
            // 获取时间戳
            let timeSign = (new Date()).getTime()
            let _itemName = this.getField(item, 'name').value
            // 判断字符串中是否含有addSign指定标识符
            let copyIndex = _itemName.indexOf(addSign)
            // 复制生成新的标识键，原值 + '_copy_' + 时间戳
            let _sign = copyIndex > 0 ?
                        _itemName.substring(0, copyIndex) + addSign + timeSign :
                        _itemName + addSign + timeSign
            // 深拷贝对象
            let _item = Deep_Clone(item)
            // 使用新的标识键更新对象数据
            let _copyItemName = this.getField(_item, 'name')
            _copyItemName.value = _sign
            // 返回新对象
            return _item
        },
        /**
         * 操作表单列表 - 拖拽子项调换顺序
         * @param {Array} nameList 字段名称数组
         * @param {String} name 当前字段键名
         * @param {Object} groupInfo 组别信息
         */
        setFieldSort (nameList, name, groupInfo) {
            // 定义结果值
            let result = []
            // 临时存储表单字段数据，用于等下变更且避免被覆盖
            let tempData = Deep_Clone(this.formFieldData)
            // 根据nameList字段顺序，重新生成一个字段数组
            nameList.forEach(i => {
                // 根据键名获取字段数据
                let _item = tempData.find(j => j.find(k => k.name === 'name').value === i)
                // 找出当前变更的项
                if (groupInfo && i === name) {
                    // 修改项的组别信息为拖动后的组别信息
                    Object.keys(groupInfo).forEach(m => {
                        this.getField(_item, m).value = groupInfo[m]
                    })
                }
                // 添加进结果值
                result.push(_item)
            })
            // 更新当前字段数组
            this.formFieldData = result
            // 将操作添加进操作队列历史，【撤销/重做】逻辑将使用该数据
            this.addDoQueue()
        },
        /**
         * 设置动态name
         * @param {Array} item 表单项(属性数组)
         */
        setFieldName (item) {
            // 获取name字段对象
            let _nameField = this.getField(item, 'name')
            // 获取类型字段type对象
            let _typeField = this.getField(item, Setting.Name_EditType)
            if (!(_nameField && _typeField)) {
                console.error('字段以及编辑类型匹配不到')
                return
            }
            // 动态组装值
            let sign = `${_typeField.value}_${Get_TimeStamp()}`
            // this.$set(obj, 'COMP_SIGN', sign)
            // 更新键名
            _nameField.value = sign
        },
        // 执行清空编辑表单面板
        handleClear () {
            this.$confirm(`确定清空数据`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                // 清除子表单项下属性列表数据，不清除表单模块信息
                this.formFieldData = []
                // 设置tab切换为展示【字段属性】栏
                this.settingTab.active = SETTING_TAB_NAME.form
                // 将操作添加进操作队列历史，【撤销/重做】逻辑将使用该数据
                this.addDoQueue()
            }).catch(() => {
                // 取消当前操作     
            })
            
        },
        handlePublish () {
            alert('发布功能未开放')
        },
        // 保存表单数据函数
        handleSave (item) {
            // 获取字段数组
            let { dataList } = this._formObjFinally
            // 判断是否传入保存函数
            if (this.saveFun && typeof this.saveFun === 'function') {
                // 设置加载中
                item.loading = true
                // 执行传入的保存方法
                this.saveFun.call(this, this._formObjFinally, dataList, this._formProps).finally(() => {
                    // 取消加载状态
                    item.loading = false
                })
            } else {
                // 设置加载中
                item.loading = true
                setTimeout(() => {
                    // 更新数据
                    this.$emit('input', dataList)
                    // 取消加载状态
                    item.loading = false
                    this.$message.success('保存成功')
                }, 1000)
            }
        },
        // 执行预览方法
        handlePreview () {
            // 获取数据
            let { 
                id,
                title,
                labelWidth,
                columnNum,
                dataList,
            } = this._formObj
            // 定义按钮事件
            let btnList = [
                {
                    name: '取消',
                    icon: 'cancel',
                    type: 'danger',
                    click: function () {
                        this.$confirm('取消将不保存表单数据, 是否继续?', '提示', {          
                            confirmButtonText: '确定',          
                            cancelButtonText: '取消',          
                            type: 'warning'
                        }).then(() => {          
                            // 关闭弹窗表单
                            this.$dialog.close()
                        }).catch(() => {          
                            // 取消关闭        
                        })
                    }
                }, { 
                    name: '测试保存数据',
                    icon: 'save',
                    type: 'primary',
                    loading: false,
                    click: function (btnObj) {
                        btnObj.loading = true
                        // 设置时间函数模拟请求时间间隔，正式使用可以去除
                        setTimeout(() => {
                            // getDialogConObj:全局封装，2为指定自定义表单，返回该对象作用域
                            let formObj = this.getDialogConObj(id, 2)
                            formObj.validateForm().then(res => {
                                this.$alert(`当前保存数据为：${JSON.stringify(res)}`)
                            }).finally(() => {
                                btnObj.loading = false
                            })
                        }, 300)
                        
                    } 
                }
            ]
            // 弹窗显示表单
            this.$dialog.init({
                type: 'form',
                id: id,
                title: title,
                labelWidth: labelWidth,
                columnNum: columnNum,
                // 使用深拷，避免数据编辑-地址引用导致原有数据变更
                // 过滤占位字段，根据关键属性Setting.Place
                dataList: Deep_Clone(dataList).filter(i => !i[Setting.Place]),
                groupProps: this._groupProps,
                handlerList: btnList,
            })
        },
        /**
         * 根据字段对象获取组别信息
         * @param {Object} fieldObj 字段对象数据
         * @returns {Object} 组别对象数据
         */
        getGroupObj (fieldObj) {
            if (!fieldObj) return {}
            return this._groupProps.reduce((res, item) => {
                res[item] = fieldObj[item]
                return res
            }, {})
        },
        // 监听【ctrl + S】按钮，执行保存数据的逻辑
        saveContentByKeyDown (e) {
            let formDesignerEl = this.$refs.formDesigner?.getBoundingClientRect()
            // 判断元素是否在可视区域有宽值与高值
            //     有则表示当前已打开，可以执行保存逻辑
            //     没有则表示未打开，不执行保存，并且删除键盘监听（这一步主要预防未正常关闭组件时对监听事件的处理）
            if (formDesignerEl && formDesignerEl.width && formDesignerEl.height) {
                let key = window.event.keyCode ? window.event.keyCode : window.event.which
                // 判断当前是否同时按住【Ctrl + S】
                if (key === 83 && e.ctrlKey) {
                    // 获取保存按钮对象数据
                    let item = this.mainOperateList.find(i => i.handler === 'handleSave')
                    // 执行保存逻辑
                    this.handleSave(item)
                    // 取消默认操作：常规的浏览器保存事件是保存当前的html
                    e.preventDefault()
                }
            } else {
                document.removeEventListener('keydown', this.saveContentByKeyDown)
            }
        },
        getOptionData () {
            let _data = Deep_Clone(Setting.Form_Option_Data)
            return _data.map(i => {
                i.attrClass = FormClass[i.attrClassName] || null
                return i
            })
        },
        // 设置表单配置数据
        setFormSetting () {
            // 根据传入的表单属性，生成右侧的表单tab数据
            this.formSetting = new FormClass.Form(this._formProps).list || []
        },
        // 设置表单字段数据
        setFormFieldList () {
            // 判断是否传入完整表单数据（配置项为数组格式）
            if (this.isWhole) {
                this.formFieldData = this.value
                return
            }
            if (!(this.value &&
                this.value.constructor === Array &&
                this.value.length > 0)) return
            // 根据传入的表单json数据，组装完整的表单字段数据
            this.formFieldData = SimpleForm_To_WholeForm.call(this, Deep_Clone(this.value), this.optionData)
            // 将操作添加进操作队列历史，【撤销/重做】逻辑将使用该数据
            this.addDoQueue()
            if (this.formFieldData && this.formFieldData.length > 0) {
                // 设置当前默认的活跃组别面板
                this.runGroupIndex = 0
            }
        },
    },
    // 可访问当前this实例
    created () {
        // 设置左侧组件项数据
        this.optionData = this.getOptionData()
        // 设置最右侧的表单对象属性信息
        this.setFormSetting()
        // 设置完整的表单展示数据
        this.setFormFieldList()
    },
    // 挂载完成，可访问DOM元素
    mounted () {
        // 监听【ctrl + S】按钮，执行保存数据的逻辑
        document.addEventListener('keydown', this.saveContentByKeyDown)
    },
    beforeDestroy() {
        // 去除监听【ctrl + S】按钮
        document.removeEventListener('keydown', this.saveContentByKeyDown)
    },
    watch: {
        // 切换主题
        themeIndex: {
            immediate: true,
            handler: function (newVal, oldVal) {
                let theme = themeList[newVal || 0]
                let rootEl = document.querySelector(':root')
                // 根据主题方案设置颜色
                rootEl.style.cssText = `
                    --primary: ${theme.primary};
                    --primaryHover: ${theme.primaryHover};
                    --primaryShadow: ${theme.primaryShadow};
                    --primaryShadowHover: ${theme.primaryShadowHover};
                    --primarySimple: ${theme.primarySimple};
                    --sidebar: ${theme.sidebar};
                    --sidebarPart: ${theme.sidebarPart};
                    --sidebarPartColor: ${theme.sidebarPartColor};
                    --dragTipPanel: ${theme.dragTipPanel};
                    --dragItemBg: ${theme.dragItemBg};
                    --sidebarColor: ${theme.sidebarColor};
                `
            }
        },
        // 监听当前活跃的组别id
        _currentGroupId: {
            handler: function (newVal, oldVal) {
                if (!(this.formFieldData && this.formFieldData.length > 0)) return
                // 获取当前活跃组别下的任意一项字段
                let currentItem = this.formFieldData.find(i => this.getField(i, Setting.GroupId).value === newVal)
                if (!currentItem) return
                // 更新给this.currentGroupFieldList，用于右侧展示活跃组别字段信息
                this.currentGroupFieldList.forEach(i => {
                    let _field = currentItem.find(j => j.name === i.name)
                    _field && (i.value = _field.value)
                })
            }
        },
        // 当前右侧展示的组别字段；核心是当右侧组别信息修改时触发这里逻辑，用于更新给组别下的字段数据
        currentGroupFieldList: {
            // 深度监听，涉及数组属性值变更
            deep: true,
            handler: function (newVal, oldVal) {
                if (!newVal) return
                // 获取最新的组别对象数据
                let _groupObj = newVal.reduce((result, item) => {
                    let { name, value } = item
                    result[name] = value
                    return result
                }, {})
                this.formFieldData.forEach(i => {
                    let groupIdField = this.getField(i, Setting.GroupId)
                    // 筛选与当前更改的组别信息对应组别id的字段，更新他们的组别属性数据
                    if (groupIdField.value === _groupObj[Setting.GroupId]) {
                        let groupKeys = Object.keys(_groupObj)
                        groupKeys.forEach(k => {
                            let item = this.getField(i, k)
                            item && (item.value = _groupObj[k])
                        })
                    }
                })
            }
        },
        // 监听右侧表单配置项数据的变更
        formSetting: {
            deep: true,
            handler: function (newVal, oldVal) {
                // 获取最新的表单配置项数据
                let _formProp = (newVal || []).reduce((res, item) => {
                    let { name, value } = item
                    res[name] = value
                    return res
                }, {})
                this._formProps = Object.assign(this._formProps, _formProp)
            }
        }
    }
}
</script>
<style lang='scss' scoped>
@import './theme/index.scss';
$fd-header-height: 40px;
$fd-toolbar-height: 40px;
$sidebar-width: 240px;
$setting-width: 240px;
$content-minWidth: 600px;
.form-designer::v-deep {
    min-width: 1280px;
    .fd-header {
        .fd-header-title {
            min-width: $sidebar-width;
        }
        display: flex;
        height: $fd-header-height;
        .s-close {
            vertical-align: super;
            margin: 0 $padding;
            color: #fff;
            font-weight: bold;
            &:hover {
                transform: rotate(180deg);
                transition: all 0.6s;
            }
        }
        .m-mainOperate {
            padding: 0 16px;
            .m-mainOperate-item {
                min-width: 70px;
                display: inline-block;
                &:last-child {
                    margin-right: 0;
                }
                .m-mainOperate-item__btn {
                    color: #fff;
                }
            }
        }
    }
    .fd-body {
        height: calc(100vh - #{$fd-header-height});
        overflow: hidden;
        display: flex;
        .fd-sidebar {
            width: $sidebar-width;
            background: $fd-sidebar;
            position: relative;
            flex-shrink: 0;
            height: 100%;
            overflow: auto;
            transition: all .2s ease;
            .fd-sidebar-main {
                right: 0;
                transition: right .2s ease
            }
            &.is-fold {
                width: 20px;
                overflow: hidden;
                padding: 0;
                .fd-sidebar-main {
                    right: $sidebar-width;
                }
            }
        }
        .fd-content {
            border-left: 1px solid $lineColor;
            border-right: 1px solid $lineColor;
            flex: 1 1 auto;
            min-width: $content-minWidth;
            height: 100%;
            .fd-toolbar {
                height: $fd-toolbar-height;
            }
            .fd-form-panel {
                height: calc(100% - #{$fd-toolbar-height});
                overflow: auto;
                .fd-form-panel__content {
                    width: 100%;
                    height: 100%;
                    .fd-drag-form {
                        background: #fff;
                        .secForm {
                            background: $fd-drag-tipPanel;
                            border: 1px dashed $fd-primary-simple;
                            .title {
                                padding-bottom: 0;
                            }
                        }
                    }
                    .dragTip {
                        font-size: 24px;
                        color: #ccc;
                        text-align: center;
                        padding-top: 30%;
                        padding-bottom: 30%;
                        height: 100%;
                        background: $fd-drag-tipPanel;
                    }
                }
            }
        }
        .fd-setting {
            width: $setting-width;
            background: $fd-sidebar;
            flex-shrink: 0;
            height: 100%;
            transition: width .2s ease;
            .fd-setting-main {
                left: 0;
                transition: right .2s ease
            }
            &.is-fold {
                width: 20px;
                overflow: hidden;
                padding: 0;
                .fd-setting-main {
                    right: $setting-width;
                }
            }
        }
        .bd-form {
            .secForm {
                .el-row {
                    width: 100% !important;
                }
            }
        }
    }
}
</style>