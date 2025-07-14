<template>
    <div class="page-setting">
        <!-- 左侧字段 -->
        <!-- 字段展示模块 -->
        <div class="left-field-contain">
            <div class="left-box">
                <el-scrollbar class="left-field-scroll" wrap-style="color: red;">
                    <el-collapse
                        class="field-card"
                        v-model="collapseValue"
                        v-for="(i, i_index) in showList" :key="i_index">
                        <el-collapse-item :name="String(i_index)">
                            <template slot="title">
                                <div class="field-card-header text-no_select padL-10" @click="i.isOpen = !i.isOpen">
                                    <h6 class="card-header-name">{{i.title}}</h6>
                                    <span class="card-header-text">（双击选中或取消，单击打开配置表）</span>
                                    <span
                                        v-if="i.hasDesigner"
                                        class="operateC bold pointer fontL d-ib"
                                        @click.stop="showFormDesigner()">
                                        前往表单设计器
                                        <bd-icon name="forward-fill"></bd-icon>
                                    </span>
                                </div>
                            </template>
                            <ul
                                v-if="i.fieldList && i.fieldList.length"
                                class="field-list">
                                <div class="marT-10">
                                    <li
                                        class="field-item d-ib marR-10 text-no_select"
                                        v-for="(j, j_index) in i.fieldList"
                                        :key="j_index"
                                        :ref="getRefId(i_index, j_index)"
                                        @click.stop="tapField(i, j, i_index, j_index)"
                                        @dblclick.stop="dblTapField(i, j.field, i_index, j_index)">
                                        <active-border
                                            class="field-item"
                                            :active="i_index === activeFieldPos[0] && j_index === activeFieldPos[1]">
                                            <div class="no-select-field text-o-1" :class="{'selected-field': parseInt(j.field[i.selected])}" :title="j.field.displayName">
                                                <span v-if="i.beforeText && Has_Value(j.field[i.beforeText])">[{{j.field[i.beforeText]}}] </span>
                                                {{j.field.displayName}}
                                            </div>
                                        </active-border>
                                    </li>
                                </div>
                            </ul>
                        </el-collapse-item>
                    </el-collapse>
                </el-scrollbar>
            </div>
        </div>

        <!-- 辅助表单设置模块 -->
        <div class="sub-setting-form-contain" :style="{'width': `${formBoxWidth}px`}" :class="{'fold-form': isFold}">
            <!-- 垫底模块，辅助表单的出现，顶开字段容器的宽度 -->
            <span></span>
        </div>
        <!-- 表单设置模块 -->
        <div class="setting-form-contain" :style="{'width': `${formBoxWidth}px`}" :class="{'fold-form': isFold}">
            <div class="form-title-box" @dblclick="changeFormW">
                <bd-icon class="setting-form-icon" name="setting"></bd-icon>
                <h6 class="form-title-text d-ib">
                    属性设置
                    <span
                        class="pointer"
                        title="点击快速复制"
                        @click="copy(_currentEntityName)">
                        {{_currentEntityName}}
                    </span>
                </h6>
                <span class="form-fold-btn floatR" @click="isFold = true">
                    <bd-icon name="unfoldHor"></bd-icon>
                </span>
            </div>
            <!-- <el-scrollbar> -->
                <!-- 表单内容 -->
                <div
                    v-if="activeFieldPos.length === 2"
                    class="form-content h-100 right-form">
                    <transition name="fade">
                        <module-form
                            v-if="isShowModuleForm"
                            noTitle
                            ref="moduleForm"
                            v-bind="formData.module"
                            :fieldList.sync="tempFormFieldList"
                            :detailId="detailId"
                            :elseAttrs="elseAttrs"
                            class="right-form__main">
                        </module-form>
                    </transition>
                    <div class="right-form__btn">
                        <bd-button
                            v-for="(i, index) in _btnList"
                            :key="index"
                            v-bind="i"
                            @click="handleClick(i, $event)"
                            class="right-form__btn__item">
                        </bd-button>
                    </div>
                </div>
            <!-- </el-scrollbar> -->
        </div>

        <!-- 表单设计器弹窗 -->
        <bd-dialog
            v-if="formDesignerVisible"
            class="font"
            :outScope="scope"
            :showCloseBtn="true"
            :visible.sync="formDesignerVisible"
            :destroy-on-close="true"
            :fullscreen="true"
            noHeader
            noFooter>
            <div>
                <form-designer
                    v-model="allFieldData"
                    :nameOption="_nameOption"
                    :rulesOption="_rulesOption"
                    :formProps.sync="formProps"
                    :saveFun="saveFun"
                    :show-close-btn="true"
                    @close="formDesignerVisible = false">
                </form-designer>
            </div>
        </bd-dialog>
    </div>
</template>

<script>
import { Load_Field_Info } from '@/api/frame/desinger/field'
import ModuleForm from '@/components/frame/Module/ModuleForm/index.vue'
import Dialog from '@/components/frame/Dialog/index.vue'
import FormDesigner from '@/components/frame/FormDesigner/index.vue'
import ModuleUtils from '@/js/ModuleUtils'
import Default_Btn_List from './button'
import ActiveBorder from './component/ActiveBorder'
import { Update_FieldList_Value } from '@/service/module'
import { Merge_Btn } from '@/service/module'
import { EventBus } from '@/service/event-bus'
import { Copy_To_Clip } from '@/utils/copy-clip'
import { Deep_Clone } from '@/utils/clone'
import { Has_Value } from '@/utils'
import { Throw } from '@/utils/animate.js'
import { otherDic } from '@/views/common/modelDesigner/fieldDic'
import GlobalConst from '@/service/global-const'
import { Save_Field } from '@/api/frame/desinger/field'
import { Get_Rest_Obj } from '@/utils/object'
import { Str_To_Obj } from '@/utils/data-type-change'
export default {
    name: 'page-setting',
    components: {
        [ActiveBorder.name]: ActiveBorder,
        [ModuleForm.name]: ModuleForm,
        [Dialog.name]: Dialog,
        [FormDesigner.name]: FormDesigner
    },
    props: {
        // 主表module
        mainModule: {
            type: Object
        },
        // 模型数据id
        id: {
            type: String
        },
        mainFieldList: {
            type: Array
        },
        mainScope: null,
        // 函数：获取当前页面的作用域
        getExtendScope: {
            type: Function
        }
    },
    data () {
        return {
            // 表单属性
            formProps: {
                title: '表单设计器',
                labelWidth: 110,
                columnNum: 2
            },
            // 表单设计器弹窗展示状态
            formDesignerVisible: false,
            // 是否展示表单，点击字段时优先清除再展示，已触发watch【子组件内部watch不加deep，所以对象属性变更没响应】
            isShowModuleForm: false,
            mdCode: 'sys_module_form',
            // 表单模块展开后宽度
            formBoxWidth: 400,
            cardList: [
                /**
                 * @description: 卡片列表
                 * @param {String}：title：卡片头部名称
                 * @param {String}：id：卡片id，可辅助点击字段时，回显相对应的表单
                 * @param {String}：selected：对应卡片字段被选中的控制参数
                 * @param {String}：belong：属于配置的id
                 * @param {String}：beforeText：写在前面的文本id
                 */
                { title: '列表配置', id: 'list', selected: 'isShowInGrid', belong: '1', beforeText: 'listPriority'},
                { title: '搜索配置', id: 'search', selected: 'searchAble', belong: '2' },
                { title: '编辑表单配置', id: 'edit', selected: 'isEdit', belong: '3', hasDesigner: true },
                { title: '查看页配置', id: 'view', selected: 'isShowInPage', belong: '4'}
            ],
            // 表单是否收起
            isFold: true,
            // 页面配置信息（请求回来的，不经处理过的）
            pageConfigInfo: {},
            // 字段对象列表（编辑所要更改，保存时直接提交的数组）
            fieldsList: [],
            // 右侧模型module信息（请求回来的，不经处理过的）
            module: [],
            // 激活字段的位置信息（二维数组：从0开始）【选中的配置位置，字段位置】
            activeFieldPos: [],
            // 表单按钮
            formBtnList: [
                { id: '', name: '确 定', icon: 'submit', method: 'submit' },
                { id: '', type: 'warning', name: '取 消', icon: 'cancel', method: 'cancel' }
            ],
            // 折叠面板展开绑定值（绑定对应字符串）
            collapseValue: [],
            // 默认按钮数组
            defaultBtnList: Default_Btn_List,
            // 自定义按钮数组
            customBtnList: null,
            // 表单字段的中间变量
            // （与计算属性formFieldList属于同一个，只是根据点击/双击字段的时机进行赋值）
            tempFormFieldList: [],
            // 单击时触发的延迟函数
            timer: null,
            // 判断是否有值
            Has_Value: Has_Value,
            // 字段数组数据
            allFieldData: [],
        }
    },
    computed: {
        // 当前组件作用域
        scope () {
            return this
        },
        // 获取字段键名数组
        _nameOption () {
            return this.fieldsList.map(i => i.entityName)
        },
        // 获取校验规则数组
        _rulesOption () {
            return this.module &&
                   this.module.dic &&
                   this.module.dic.MODULE_VALIDATION_RULE_SELECT
        },
        // 获取正在操作的字段实体名
        _currentEntityName () {
            if (this.tempFormFieldList && this.tempFormFieldList.length > 0) {
                return this.tempFormFieldList.find(i => i.name === 'entityName')?.value
            }
            return ''
        },
        // 更新页面按钮数组（将自定义按钮数组与默认按钮数组进行合并）
        _btnList () {
            return Merge_Btn(this.defaultBtnList, this.customBtnList)
        },
        formFieldList () {
            let result = Update_FieldList_Value(this.formData.module.fieldList, this.formData.field, { valuePath: '' })
            return result
        },
        // 获取右侧表单详情数据id
        detailId () {
            return this.formData.field?.['formId']
        },
        // 非业务属性
        elseAttrs () {
            return {
                belong: this.formData?.field?.belong
            }
        },
        showList: {
            get () {
                /**
                 * {
                 *      title：'', 卡片头部名称
                 *      id：'', 卡片id，可辅助点击字段时，回显相对应的表单
                 *      selected：'', 对应卡片字段被选中的控制参数
                 *      belong: '', 对应表单配置属性（'1': 列表，'2': 搜索，'3': 编辑，'4': 查看）
                 *      fieldList：[
                 *          field: {}, 后台传递回来的表单的值
                 *          form： [], 表单列表
                 *      ]
                 * }
                 */
                let result = []
                let fieldsSources = this.fieldsList || []
                this.cardList.forEach(i => {
                    let item = i
                    let fieldList = []
                    fieldsSources.forEach(j => {
                        let fieldItem = {
                            field: Deep_Clone(j)
                        }
                        fieldItem.module = Deep_Clone(this.module)
                        fieldList.push(fieldItem)
                    })
                    item.fieldList = fieldList
                    result.push(item)
                })
                return result
            },
            set () {

            }
        },
        formData: {
            get () {
                if (this.activeFieldPos.length === 0) return {}
                // 获取配置，字段的索引
                let [configIndex, fieldIndex] = this.activeFieldPos
                // 点击的配置
                let seledConfig = this.showList[configIndex]
                // 点击的字段
                let seledField = seledConfig.fieldList[fieldIndex]
                return {
                    module: seledField.module,
                    field: {
                        ...seledField.field,
                        belong: this.showList[this.activeFieldPos[0]].belong
                    }
                }
            }
        },
    },
    methods: {
        // 处理单附件：数据格式转化(根据所在场景)
        transformSingleAttach () {
            // 判断字段是否为附件类型
            let isAttach = (field) => (field.editType === 'attach' || field.editType === 'attachMulti')
            return {
                /**
                 * 初始获取字段数据后，进行转化
                 *      1. null:不需要处理
                 *      2. 对象的字符串格式：需要转化为对象
                 * @param {String} Object 字段对象
                 */
                init (field) {
                    if (!(field && isAttach(field) && field.attachTemplateId)) return
                    let _attachTemplateId = field.attachTemplateId
                    if (_attachTemplateId.constructor === String) {
                        field.attachTemplateId = Str_To_Obj(_attachTemplateId)
                    }
                },
                /**
                 * 右侧表单更新回左侧时，右侧产出的单附件为数组格式，左侧需要为对象
                 * @param {Object} formRes 表单对象数据
                 */
                update (formRes) {
                    if (!(formRes && Object.keys(formRes).length > 0)) return
                    this.tempFormFieldList
                        .filter(i => i.type === 'attach')
                        .forEach(i => {
                            let _val = formRes[i.name]
                            // TODO：暂时使用此规则找出单附件
                            if (_val &&
                                _val.constructor === Array &&
                                _val.length === 1) {
                                formRes[i.name] = _val[0]
                            } else {
                                formRes[i.name] = null
                            }
                        })
                },
                /**
                 * 最终保存提交接口时，需要数据为attachId.需要取出对象数据中的attachId
                 * @param {Array} fieldList 字段数组
                 */
                save (fieldList) {
                    fieldList
                        .filter(i => isAttach(i))
                        .forEach(i => {
                            let _attachTemplateId = i.attachTemplateId
                            if (_attachTemplateId &&
                                _attachTemplateId.constructor === Object) {
                                i.attachTemplateId = _attachTemplateId.attachId
                            }
                        })
                    return fieldList
                }
            }
        },
        // 展示表单设计器
        showFormDesigner () {
            // 获取最新的字段数组
            this.allFieldData = this.get_formDesigner_fieldList()
            // 显示表单设计器
            setTimeout(() => {
                this.formDesignerVisible = true
            }, 50)
            // 去除页面配置的展开模式
            setTimeout(() => {
                this.isFold = true
            }, 1000)
        },
        /**
         * 模型设计器的保存函数
         * @param {Object} formObj 表单对象
         * @param {Array} dataList 表单项数组
         * @param {Object} formProps 表单配置数据
         */
        saveFun (formObj, dataList, formProps) {
            return new Promise((resolve, reject) => {
                // 深拷贝数组，获取新的表单项数组
                let _dataList = Deep_Clone(dataList)
                // 深拷贝源字段数组
                let _oldFieldList = Deep_Clone(this.fieldsList)
                // 转换键名，从基础表单键名更换为接口需要的键名
                this.FormatBaseFieldKey(_dataList, false)
                // 定义模型中的表单字段键名
                let _name = 'entityName'
                // let _fieldIdName = 'fieldId' // 2022-10-14废弃该想法，新增的字段没有fieldId，但可以有name匹配得了entityName，所以使用name方案
                // 定义结果集
                let result = []
                // 在模型设计器-表单设置：
                //     没有双击选中的项在表单设计器中将没有默认的模块展示供操作
                //     但在表单设计器中可能存在拖拽排序的情况，回显时候时不应修改到未双击选中的项
                //     所以需要组装一个新的字段键顺序，既满足不修改未双击元素的下角标，同时满足能同步设计器中字段的相对顺序
                // 获取表单设计器中操作的字段键名列表
                let _nameList = _dataList.map(i => i[_name])
                // 遍历旧字段数组
                this.fieldsList.forEach((i, index) => {
                    let _fieldName = i[_name]
                    // 判断当前操作的字段是否在表单设计器字段数据中
                    let isInNewList = _nameList.includes(_fieldName)
                    // 如果某项既未双击选中，同时也不在表单设计器的操作字段中
                    // 存在一种情况，一开始A字段未双击选中，进入表单设计器中后新建了字段，配置为A字段，此时是按表单设计器中的操作顺序来
                    if (!this.isEditItem(i) && !isInNewList) {
                        // 优先确保未双击选中的字段，不影响其下角标，按其旧下角标插入键名数组
                        _nameList.splice(index, 0, _fieldName)
                    }
                })
                // 已经拼接完整的字段键名数组，接下来就按照该顺序重新组装数据
                _nameList.forEach((i, i_index) => {
                    // 获取旧字段对象
                    let _oldField = _oldFieldList.find(j => j[_name] === i)
                    // 获取表单设计器中对应的字段对象
                    let _newField = _dataList.find(j => j[_name] === i)
                    // fieldId作为字段的唯一标识，数据可来源于接口或者表单设计器中动态生成，对于保存模型接口而言，表单设计器中动态生成的标识没有作用，所以此处删除，避免覆盖到原有字段的值
                    _newField && delete _newField.fieldId
                    if (!_oldField) {
                        this.$message.error(`匹配不到字段${_newField.entityName}, 请确保该字段在模型字段列表中是存在的`)
                        return
                    }
                    // 定义最终提交的字段项
                    let _item = null
                    if (!_newField) {
                        // 添加进结果集
                        _item = _oldField
                    } else {
                        // 1. 首先当从模型设计器点进表单设计器时，会更新customOptions值，去除其中的一些配置字段，具体看方法get_formDesigner_fieldList
                        // 2. customOptions传入表单设计器中时，会同步将属性全部更新给当前字段
                        // 3. 所以这里最新字段的customOptions值的取值只需要关注新字段比旧字段多出来的属性，将这些全部作为customOptions的属性值
                        delete _oldField.customOptions
                        delete _newField.customOptions
                        Object.keys(_oldField).forEach(i => {
                            if (i in _newField) {
                                // 更新字段值
                                _oldField[i] = _newField[i]
                            }
                        })
                        let _newCustomOptions = Get_Rest_Obj(_newField, _oldField) || {}
                        // 只读、只读不提交、不提交
                        if (_oldField.editType === 'text') {
                            if (_newCustomOptions.disabled && !_newCustomOptions.destroyed) {
                                _oldField.editType = 'readonly'
                                delete _newCustomOptions.disabled
                            }
                            if (_newCustomOptions.destroyed && !_newCustomOptions.disabled) {
                                _oldField.editType = 'destroyed'
                                delete _newCustomOptions.destroyed
                            }
                            if (_newCustomOptions.disabled && _newCustomOptions.destroyed) {
                                _oldField.editType = 'disabled'
                                delete _newCustomOptions.disabled
                                delete _newCustomOptions.destroyed
                            }
                            if (_newCustomOptions.dataType === 'number') {
                                _oldField.editType = 'number'
                                _newCustomOptions.dataType
                            }
                        }
                        // 删除 int, number类型的dataType
                        if (_oldField.editType === 'int' ||
                            _oldField.editType === 'number') {
                            delete _newCustomOptions.dataType
                        }
                        // 删除隐藏项的配置
                        if (_oldField.editType === 'hidden') {
                            delete _newCustomOptions.hidden
                        }
                        // 删除密码配置
                        if (_oldField.editType === 'password') {
                            delete _newCustomOptions.isPassword
                        }
                        // 提交的日期是以基础表单的类型为主的，基于此修改
                        if (_oldField.editType === 'date') {
                            if (_newCustomOptions.formEditType !== 'date') {
                                if (_newCustomOptions.dateType === 'datetime') {
                                    _oldField.editType = 'dateTime'
                                    delete _newCustomOptions.dateType
                                }
                                if (_newCustomOptions.dateType === 'year') {
                                    _oldField.editType = 'year'
                                    delete _newCustomOptions.dateType
                                }
                                if (_newCustomOptions.dateType &&
                                    _newCustomOptions.dateType.includes('range')) {
                                    _oldField.editType = _newCustomOptions.dateType.replace('range', '')
                                    delete _newCustomOptions.dateType
                                }
                            } else {
                                _oldField.editType = 'date'
                            }
                        }
                        // 复选框（只面向多复选框，目前暂不处理单复选框）
                        if (_oldField.editType === 'checkbox') {
                            _oldField.editType = _newCustomOptions.multiple ? 'checkboxlist' : 'checkbox'
                            delete _newCustomOptions.multiple
                        }
                        // 图片
                        if (_oldField.editType === 'imagePicker') {
                            _oldField.editType = _newCustomOptions.multiple ? 'img' : 'imgSingle'
                            delete _newCustomOptions.multiple
                        }
                        // 附件
                        if (_oldField.editType === 'attach') {
                            _oldField.editType = _newCustomOptions.multiple ? 'attachMulti' : 'attach'
                            delete _newCustomOptions.multiple
                        }
                        // 颜色选择
                        if (_oldField.editType === 'colorPicker') {
                            _oldField.editType = _newCustomOptions.multiple ? 'color-multiple' : 'color-single'
                            delete _newCustomOptions.multiple
                        }
                        // 弹窗选择列表
                        if (_oldField.editType === 'dialogList') {
                            _oldField.editType = _newCustomOptions.multiple ? 'selectListDataMulti' : 'selectListData'
                            delete _newCustomOptions.multiple
                        }
                        // 地址本
                        if (_oldField.editType === 'addressBook') {
                            _oldField.editType = 'addressbook'
                        }
                        // 去除冗余的配置
                        let {
                            columnPer, // 占比数
                            destroyed, // 不提交接口
                            disabled, // 不可编辑
                            hidden, // 隐藏
                            isBlock, // 标签是否独占一行
                            isShowAllLabel, // 是否显示完整标签数据
                            selfClass, // 自定义class名称
                            groupIcon, // 组别图标
                            isPassword, // 是否为密码
                            prepend, // 输入框的前置元素
                            append, // 输入框的后置元素
                            optionResPath, // 数据源options路径
                            roleChoose, // 角色选择
                            mdCode, // 数据来源为模型时
                        } = _newCustomOptions || {}
                        if (!columnPer) delete _newCustomOptions.columnPer
                        if (!destroyed) delete _newCustomOptions.destroyed
                        if (!disabled) delete _newCustomOptions.disabled
                        if (!hidden) delete _newCustomOptions.hidden
                        if (!isBlock) delete _newCustomOptions.isBlock
                        if (!isShowAllLabel) delete _newCustomOptions.isShowAllLabel
                        if (!selfClass) delete _newCustomOptions.selfClass
                        // 判断组别图标是否为通用图标，是则清除该配置
                        if (groupIcon === GlobalConst.form.groupIcon) {
                            delete _newCustomOptions.groupIcon
                        }
                        if (!isPassword) delete _newCustomOptions.isPassword
                        if (!prepend) delete _newCustomOptions.prepend
                        if (!append) delete _newCustomOptions.append
                        if (!optionResPath) delete _newCustomOptions.optionResPath
                        if (!roleChoose) delete _newCustomOptions.roleChoose
                        if (!mdCode) delete _newCustomOptions.mdCode
                        // 删除临时的编辑类型
                        delete _newCustomOptions.formEditType
                        // 配置数据来源为模型时，为了方便预览效果会动态生成mdCode,提交接口数据时需要删除该配置
                        if (_newCustomOptions &&
                            _newCustomOptions.mdCode &&
                            _newCustomOptions.mdCode === _oldField.dicName) {
                            delete _newCustomOptions.mdCode
                        }
                        // TODO：待删，之前由于数据异常，导致字段下会生成valueFieldId，所以需要删掉
                        // delete _newCustomOptions.valueFieldId
                        // 自定义配置项customOptions没值时，直接设置为null
                        if (Object.keys(_newCustomOptions).length === 0) {
                            _newCustomOptions = null
                        }
                        // 定义最终提交的字段项
                        _item = {
                            ..._oldField,
                            customOptions: _newCustomOptions && JSON.stringify(_newCustomOptions)
                        }
                    }
                    // 更新排序号
                    _item.priority = i_index * 10
                    // 添加进结果集
                    result.push(_item)
                })
                // 更新字段数组
                this.fieldsList = result
                result = this.transformSingleAttach().save(Deep_Clone(result))
                let params = {
                    moduleId: this.id,
                    fields: JSON.stringify(result),
                    isAll: false
                }
                Save_Field(params).then(res => {
                    if (res.hasOk) {
                        this.$message.success('【页面配置数据】保存成功')
                        this.allFieldData = this.get_formDesigner_fieldList()
                        resolve(true)
                    } else {
                        this.$message.error(`保存失败！${res.message || ''}`)
                        resolve(false)
                    }
                }).catch(err => {
                    this.$message.error(`保存失败！`)
                    resolve(false)
                })
            })
        },
        // 复制到黏贴板
        copy (msg) {
            Copy_To_Clip(msg).then(res => {
                this.$message.success(`复制成功:${msg}`)
            })
        },
        /**
         * 获取ref id
         * @param {Number} i_index 模块顺序下角标
         * @param {Number} j_index 字段顺序下角标
         */
        getRefId (i_index, j_index) {
            return `ref_${i_index}_${j_index}`
        },
        // 抛的动画
        throwAnimate (event, aimEl) {
            return Throw(event.target, aimEl)
        },
        /**
         * 按钮事件触发
         * @param {Object} btnObj 按钮对象
         */
        handleClick (btnObj, event) {
            // 获取事件
            let { click: handleClick } = btnObj
            if (handleClick && typeof handleClick === 'function') {
                // 执行函数，传递作用域
                handleClick.call(this, btnObj, event)
            }
        },
        moduleEditCancel () {
            this.activeFieldPos = []
            this.isFold = true
        },
        // 请求字段详细数据
        loadField () {
            return new Promise ((resolve, reject) => {
                let params = {
                    id: this.id
                }
                Load_Field_Info(params).then(res => {
                    if (res) {
                        this.pageConfigInfo = res
                        // 给列表排序号设置默认值（默认与排序号保持一致）
                        if (res.fieldList) {
                            res.fieldList.forEach(i => {
                                if (Has_Value(i.priority) && !Has_Value(i.listPriority)) {
                                    i.listPriority = i.priority
                                }
                                // 如果是搜索类型，还需要根据其搜索类型值给搜索框定义默认值
                                if (Has_Value(i.searchType) && !Has_Value(i.searchBox)) {
                                    let _dic = otherDic.searchType
                                    let obj = _dic.find(d => d.id === String(i.searchType))
                                    if (obj) {
                                        let temp_value = {
                                            id: i.entityName,
                                            name: i.displayName,
                                            ...JSON.parse(obj.value),
                                            tagName: ''
                                        }
                                        i.searchBox = JSON.stringify(temp_value)
                                    }
                                }
                                // 处理单附件：数据格式转化
                                this.transformSingleAttach().init.call(this, i)
                            })
                        }
                        this.fieldsList = this.setDefaultValue(this.module.fieldList, (res.fieldList || []))
                        // 所有的卡片默认关闭
                        this.collapseValue = []
                        // 等待字段渲染完再打开折叠窗
                        this.$nextTick(() => {
                            this.cardList.forEach((i, index) => {
                                // 顺序打开折叠
                                setTimeout(() => {
                                    // 折叠数组中加入下角标，加入则表示展开
                                    this.collapseValue.push(String(index))
                                }, 300 * index)
                            })
                        })
                    } else {
                        this.$message('请求字段列表失败')
                    }
                    resolve()
                }).catch(err=>{
                    console.error('请求字段列表失败', err)
                    resolve()
                })
            })
        },
        /**
         * @description: 点击字段
         * @param {Object} cardInfo：当前所在的卡片信息
         * @param {Object} field：字段信息
         * @param {Number} cardIndex：所点击的配置索引
         * @param {Number} fieldIndex：所点击的字段索引
         * @return {*}
         */
        tapField (cardInfo, field, cardIndex, fieldIndex) {
            // 延时函数为了使双击时，不触发单击事件
            this.activeFieldPos = [cardIndex, fieldIndex] // 标识激活字段所在坐标
            clearTimeout(this.timer)
            this.timer = setTimeout(() => {
                this.isShowModuleForm = false
                this.tempFormFieldList = Deep_Clone(this.formFieldList)
                this.$nextTick(() => {
                    this.isFold = false // 展开表单
                    this.isShowModuleForm = true
                })
            }, 300)
        },
        /**
         * @description: 双击字段
         * @param {Object} cardInfo：当前所在的卡片信息
         * @param {Object} field：字段信息
         * @param {Number} cardIndex：所点击的配置索引
         * @param {Number} fieldIndex：所点击的字段索引
         * @return {*}
         */
        dblTapField (cardInfo, field, cardIndex, fieldIndex) {
            // 清除延时函数，不触发单击事件
            clearTimeout(this.timer)
            // 字段是否被显示（显示：1，不显示：0）
            let value = field[cardInfo.selected] ? 0 : 1
            // 更改字段列表对应是否展示的值
            this.$set(this.fieldsList[fieldIndex], cardInfo.selected, value)
            this.activeFieldPos = [cardIndex, fieldIndex] // 标识激活字段所在坐标
            this.isShowModuleForm = false
            this.tempFormFieldList = Deep_Clone(this.formFieldList)
            this.$nextTick(() => {
                this.isFold = false // 展开表单
                this.isShowModuleForm = true
            })
        },
        // 更改表单宽度(暂时先这样写)
        changeFormW () {
            this.formBoxWidth = this.formBoxWidth === 400 ? 600 : 400
        },
        reset () {
            this.activeFieldPos = []
            this.isFold = true
        },
        // 根据模型变量更新页面变量数据
        updateData (module) {
            if (!module) return false
            // 获取关联tab：childTab
            // 获取自定义js配置：customSetting
            let { customSetting } = module
            if (customSetting) {
                let {
                    buttons, // 自定义功能按钮 {Array}
                    afterModuleJSON, // 获取模型数据后对数据的编辑更改
                } = customSetting
                // 更新传入的自定义按钮数组
                this.customBtnList = buttons
                // 获取模型数据后的钩子函数
                if (afterModuleJSON && typeof afterModuleJSON === 'function') {
                    this.module = afterModuleJSON.call(this, module)
                }
            }
        },
        // 根据表单的defaultValue，给没有值的字段列表设置默认值
        setDefaultValue (form, fieldList) {
            form.forEach(f => {
                // 如果defaultValue有值，需要给字段设置默认值
                if (Has_Value(f.defaultValue)) {
                    // 获取字段id
                    let name = f.name
                    fieldList.forEach(o => {
                        if (!Has_Value(o[name])) {
                            o[name] = f.defaultValue
                        }
                    })
                }
            })
            return fieldList
        },
        /**
         * 根据指定路径获取当前组件作用域下对象
         * 目标对象为this,当pathStr为'a,b'时，则最终返回this[a][b]
         * @param {String} pathStr：路径字符串
         */
        getThisObj (pathStr) {
            if (pathStr && pathStr.constructor === String) {
                // 定义获取路径数组
                let pathList = pathStr.split(',')
                let aimObj = null
                try {
                    // 更新aimObj
                    pathList.forEach(i => {
                        aimObj = this[i]
                    })
                } catch (e) {
                    return this
                }
                return aimObj
            }
            return this
        },
        /**
         * 格式化字段的键名，更新为场景所需要的字段键名
         * @param {Array} dataList
         * @param {Boolean} status 切换状态
         *                 true（默认）：将模型字段更新为基础表单字段键
         */
        FormatBaseFieldKey (dataList, status = true) {
            dataList.forEach(i => {
                if (status) {
                    i.value = i.defaultValue
                    i.name = i.entityName
                    i.label = i.displayName
                    i.placeholder = i.editTip
                    i.type = i.editType
                    i.isOneLine = i.isFullLine
                    i.groupName = i.groupName || GlobalConst.form.groupName
                    i.groupIcon = i.groupIcon || GlobalConst.form.groupIcon
                    i.rules = i.regexRule
                } else {
                    i.defaultValue = i.value
                    i.entityName = i.name
                    i.displayName = i.label
                    i.editTip = i.placeholder
                    i.editType = i.type
                    i.isFullLine = i.isOneLine
                    i.regexRule = i.rules
                    i.type = null // 初始返回均为null，重置时则也设置为null
                    i.isEdit = 1 // 从表单设计器中出来的字段表示可编辑，等同双击选中
                    delete i.value
                    delete i.name
                    delete i.label
                    delete i.placeholder
                    delete i.isOneLine
                    delete i.valueFieldId
                    delete i.rules // 删除动态生成的完整组件规则
                }
            })
        },
        // 判断表单编辑-字段项是否没有双击选中，选中表示在编辑页面接口中会返回该字段数据
        isEditItem (item) {
            if (!item) return false
            return item.isEdit && item.isEdit.toString() === '1'
        },
        // 将模型字段转化为表单设计器需要的字段数组
        get_formDesigner_fieldList () {
            let _allowEditFieldList = this.fieldsList.filter(i => this.isEditItem(i))
            // 深拷贝，获取字段数组
            let fieldList = Deep_Clone(_allowEditFieldList) || []
            fieldList.forEach(i => {
                let _newCustomOptions = {}
                try {
                    _newCustomOptions = i.customOptions && JSON.parse(i.customOptions) || {}
                } catch (e) {
                    console.error(`转化customOptions数据异常，值为${i.customOptions}`)
                }
                // 只读、只读不提交、不提交
                if (i.editType === 'readonly') {
                    i.editType = 'text'
                    _newCustomOptions.disabled = true
                }
                if (i.editType === 'destroyed') {
                    i.editType = 'text'
                    _newCustomOptions.destroyed = true
                }
                if (i.editType === 'disabled') {
                    i.editType = 'text'
                    _newCustomOptions.disabled = true
                    _newCustomOptions.destroyed = true
                }

                if (i.editType === 'date') {
                    if (_newCustomOptions.dateType === 'time') {
                        i.editType = 'time'
                        delete _newCustomOptions.dateType
                    }
                    if (_newCustomOptions.dateType === 'month') {
                        i.editType = 'month'
                        delete _newCustomOptions.dateType
                    }
                    // TODO: 待删除
                    // 废弃日期段的使用，只要配置了自定义属性endFieldName,框架会自动处理为日期段
                    // if (_newCustomOptions[GlobalConst.form.endFieldName]) {
                    //     i.editType = 'daterange'
                    // }
                }
                // TODO: 待删除
                // 废弃日期时间段的使用，只要配置了自定义属性endFieldName,框架会自动处理为日期时间段
                // if (i.editType === 'datetime' &&
                //     _newCustomOptions[GlobalConst.form.endFieldName]) {
                //     i.editType = 'datetimerange'
                // }

                // 容错处理，对日期时间的处理
                //     模型接口返回的是dateTime类型，但是表单设计器中需要的类型是datetime，在此完成转化
                if (i.editType === 'dateTime') {
                    i.editType = 'datetime'
                }
                // 复选框（只面向多复选框，目前暂不处理单复选框）
                if (i.editType === 'checkboxlist') {
                    i.editType = 'checkbox'
                    _newCustomOptions.multiple = true
                }
                // 多图片
                if (i.editType === 'img') {
                    i.editType = 'imagePicker'
                    _newCustomOptions.multiple = true
                }
                // 单图片
                if (i.editType === 'imgSingle') {
                    i.editType = 'imagePicker'
                    _newCustomOptions.multiple = false
                }
                // 多附件
                if (i.editType === 'attachMulti') {
                    i.editType = 'attach'
                    _newCustomOptions.multiple = true
                }
                // 单附件：不用处理
                // 颜色多选
                if (i.editType === 'color-multiple') {
                    i.editType = 'colorPicker'
                    _newCustomOptions.multiple = true
                }
                // 颜色单选
                if (i.editType === 'color-single') {
                    i.editType = 'colorPicker'
                    _newCustomOptions.multiple = false
                }
                // 弹窗多选列表
                if (i.editType === 'selectListData') {
                    i.editType = 'dialogList'
                    _newCustomOptions.multiple = false
                }
                // 弹窗单选列表
                if (i.editType === 'selectListDataMulti') {
                    i.editType = 'dialogList'
                    _newCustomOptions.multiple = true
                }
                // 地址本
                if (i.editType === 'addressbook') {
                    i.editType = 'addressBook'
                }
                // 更新customOption数据
                i.customOptions = JSON.stringify(_newCustomOptions)
            })
            // 转化字段键
            this.FormatBaseFieldKey(fieldList)
            return fieldList
        },
    },
    async mounted () {
        // 获取右侧表单模型module数据
        this.module =  await ModuleUtils.editModuleCfg(this.mdCode)
        // 请求字段详细数据
        this.loadField()
        // 更新页面变量
        this.updateData(this.module)
        // 获取当前页面域，传递出去给挂载当前页面的页面使用
        this.getExtendScope(this)
        // Vue事件总线
        // 此处场景说明：
        //     在模型编辑中允许tab配置自定义页面，使用vue.extend挂载页面
        //     为使外部父级能够访问vue.extend挂载的组件中的变量，此处通过事件总线方式
        //     为方便后续说明，用A代替父组件，B代替vue.extend挂载组件，进行下列说明
        //     1. 规则为$emit:发出事件 / $on:接收事件
        //     2. 当A中点击按钮时，让A发出事件, B接收到事件通知将B中数据打包
        //     3. 此时B发出事件携带B数据包，A接收事件从而获取到B中数据
        // 目前场景：(以下文件间的相互调度)
        //     1. sys_md_module.edit.js
        //     2. pageSetting.vue
        //     3. ChildModuleEdit.vue
        EventBus.$on('setPageSetting', (mountVarName) => {
            // 通知子tab所在vue页面
            EventBus.$emit('setData', this, mountVarName)
            // 通知主表
            EventBus.$emit('getChildTabData', this, mountVarName)
        })
        // TODO:实际场景中此处不会触发，可能由于挂载原因
        // 离开页面时清除事件总线
        this.$once('hook:beforeDestroy', () => {
            // 关闭弹窗（返回事件时弹窗可能未关闭）
            this.formDesignerVisible = false
            EventBus.$off('setPageSetting', {})
        })
    }
}
</script>

<style lang="scss" scoped>
$collapse_header_height: 36px;
// 字段正在操作时的动画颜色
$field_active_color: $primary;
.page-setting::v-deep {
    // width: calc(100% - 20px);
    width: 100%;
    height: 100%;
    overflow: hidden;
    display: table;
    // 左侧的字段列表
    .left-field-contain {
        display: table-cell;
        border-spacing: 20px 30px;
        height: 100%;
        overflow: hidden;
        // 卡片容器
        .left-box {
            height: 100%;
            overflow: auto;
            // padding-bottom: 40px;
            position: relative;
            .left-field-scroll {
                position: static;
                .el-scrollbar__wrap {
                    position: static;
                    .el-scrollbar__view {
                        padding: $padding + 2px $padding;
                    }
                }
                .el-scrollbar__bar {
                    .el-scrollbar__thumb {
                        display: none;
                    }
                }
            }
        }
        // 配置卡片
        .field-card {
            background-color: $white;
            border-radius: $borderRadius;
            overflow: hidden;
            margin-bottom: 20px;
            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.22);
            // padding: 4px 4px;
            // 卡片头部
            .field-card-header {
                cursor: pointer;
                position: relative;
                // 卡片名字/标题
                .card-header-name {
                    display: inline-block;
                    font-size: $fontS;
                    color: $primary;
                    padding: 0;
                    margin: 0;
                    // 标题前面的圈圈
                    &::before {
                        content: '';
                        display: inline-block;
                        width: 6px;
                        height: 6px;
                        border-radius: 50%;
                        background-color: $primary;
                        margin-right: 5px;
                        margin-bottom: 2px;
                    }
                }
                // 卡片提示文本
                .card-header-text {
                    font-size: $fontS;
                    color: rgba($color: $primary, $alpha: 0.7);
                }
            }
            &.el-collapse {
                border: none;
                .el-collapse-item {
                    // box-shadow: 0px 0px 4px rgba(0, 0, 0, 0.12);
                    border-radius: 4px;
                    overflow: hidden;
                    .el-collapse-item__wrap {
                        border-bottom: none;
                        // background: #000;
                    }
                }
                .el-collapse-item__header {
                    color: $primary;
                    background-color: rgba($color: $primary, $alpha: 0.1);
                    height: $collapse_header_height;
                    line-height: $collapse_header_height;
                    .el-collapse-item__arrow {
                        font-weight: bold;
                    }
                }
                .el-collapse-item__content {
                    padding-bottom: 0px;
                }
            }
        }
        // 字段列表
        .field-list {
            margin: 0;
            padding: 0 10px;
            transition: height 300ms ease-in-out;
            // 单个字段
            .field-item {
                width: 120px;
                text-align: center;
                font-size: $fontS;
                cursor: pointer;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                position: relative;
                transition: all 0.3s;
                .no-select-field {
                    height: 30px;
                    padding: 3px;
                    border-radius: $borderRadius;
                    background: #F5F5F5;
                    border: 1px solid rgba(0, 0, 0, 0);
                    transition: background-color 150ms ease-in-out, border 150ms;
                    &:hover {
                        border: 1px solid rgba($primary, 0.15);
                    }
                }
                // 字段被选中的样式
                .selected-field {
                    background-color: $primary;
                    color: $white;
                    border-radius: $borderRadius;
                    transition: background-color 150ms ease-in-out;
                }
            }
        }
    }
    // 表单辅助模块
    .sub-setting-form-contain {
        display: table-cell;
        width: 400px;
        height: 100%;
        background-color: $white;
        overflow: hidden;
        transition: width 300ms ease-in-out;
        opacity: 0;
    }
    // 表单模块
    .setting-form-contain {
        width: 410px;
        height: calc(100% - #{$appHeaderHeight} - #{$footerHeight});
        background-color: $white;
        transition: width 300ms ease-in-out;
        position: fixed;
        top: $appHeaderHeight;
        right: 0;
        z-index: 2;
        box-shadow: 0 0 20px rgba(0, 0, 0, 0.32);
        // 表单标题
        .form-title-box {
            color: $primary;
            font-size: 14px;
            height: $locationHeight;
            vertical-align: middle;
            padding: $padding;
            border-bottom: 1px solid #EEEEEE;
            .form-title-text {
                font-size: 16px;
                display: inline-block;
                vertical-align: middle;
            }
            .setting-form-icon {
                margin-right: 5px;
                vertical-align: middle;
            }
            // 收缩按钮
            .form-fold-btn {
                margin-top: 2px;
            }
        }
        // 点击按钮
        .form-btn-box {
            box-shadow: none;
        }
        // 表单内容
        .right-form {
            height: calc(100% - #{$locationHeight});
            overflow: auto;
            .right-form__main {
                height: calc(100% - #{$footerHeight} + #{$padding});
                overflow: auto;
            }
            .right-form__btn {
                height: $footerHeight;
                line-height: $footerHeight - 2px;
                width: 100%;
                background: #fff;
                text-align: center;
                box-shadow: 0px 9px 15px 3px rgba(102,102,102,0.5);
                position: absolute;
                bottom: 0;
                left: 0;
                z-index: 4;
            }
        }
    }
    // 表单收起样式
    .fold-form {
        width: 0 !important;
        overflow-x: hidden;
        transition: width 300ms ease-in-out;
    }
}
ul, li {
    list-style: none;
}
h6 {
    padding: 0;
    margin: 0;
}
// 禁止文字被选中
.text-no_select {
    -moz-user-select: none;
    -o-user-select:none;
    -khtml-user-select:none;
    -webkit-user-select:none;
    -ms-user-select:none;
    user-select:none;
}
/deep/ .el-scrollbar__view {
    height: 100%;
}
</style>
