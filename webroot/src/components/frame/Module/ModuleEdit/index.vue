<template>
    <div
        :class="{
            'is-view': isView,
            'has-btn': showBtnStatus,
            'defaultBg': !hasMuchPart,
            'o-auto': hasMuchPart
        }"
        class="bd-module-edit">
        <div
            v-if="isReady"
            class="bd-module-edit__main"
            :class="{
                'h-per-100': !hasMuchPart,
                'has-much-part': hasMuchPart
            }">
            <!-- 表单区分两种：第一是有关联关系时 -->
            <template v-if="_showLinkData && _showLinkData.length > 0">
                <!-- **************
                有关联关系时，关联模块的展示风格有3种：
                    1. 平级Tab ---- 与主表展示为Tab结构，第一个Tab是主表，其他Tab为关联
                    2. 子Tab ---- 主表在上，关联模块在主表下，并且以Tab结构展示
                    3. 独立模块，按模块的排序号决定放置位置

                基于以上关系，得出4种可能的排版结构（从上往下依次可能）
                    1. 主表 + n个独立模块
                    2. Tab结构，首Tab为[主表,其他关联块]，其他Tab为关联模块
                    3. n个独立模块
                    3. Tab结构，全部为关联模块
                    4. n个独立模块
                基于排版关系，将页面转化为数据格式：
                    {
                        mainDefault: {Array} [主表, 关1, 关2]
                        mainTab: {Array} [[主表，关1], [关2], [关3, 关4, 关5]]
                        defaultB: {Array} [关4, 关5, 关6]
                        linkTab: {Array} [[关1,关2], [关3], [关4, 关5, 关6]]
                        defaultC: {Array} [关6, 关7, 关8, 关9]
                    }
                ************** -->
                <template v-if="linkData.mainDefault.length > 0">
                    <module-form
                        v-bind="_$attrs"
                        :fieldList.sync="tempFieldList"
                        :class="{
                            'h-auto': !!detailId
                        }"
                        :editScope="scope"
                        class="marB s-part"
                        v-on="$listeners">
                    </module-form>
                    <child-module-edit
                        v-for="(i, index) in linkData.mainDefault.slice(1)"
                        :key="'mainDefault' + index"
                        v-bind="getChildModuleAttr(i, index)"
                        :noTitle="false"
                        :class="{
                            'marB': index !== linkData.mainDefault.slice(1).length - 1 ||
                                    index == linkData.mainDefault.slice(1).length - 1 &&
                                    linkData.mainTab.length +
                                    linkData.defaultB.length +
                                    linkData.linkTab.length +
                                    linkData.defaultC.length > 0
                        }"
                        class="defaultBg h-auto"
                        :fullHeight="!hasMuchPart && i.children.length === 1"
                        v-on="$listeners"
                        @getInitForm="(_form, baseFormScope) => setAllLinkForm(i, _form, baseFormScope)">
                    </child-module-edit>
                </template>
                <template v-if="linkData.mainTab.length > 0">
                    <bd-tabs
                        :data="linkData.mainTab"
                        v-model="activeTabName"
                        :idField="tabKey.id"
                        :textField="tabKey.text"
                        :autoHeight="hasMuchPart"
                        :before-leave="handerBeforeTabLeave"
                        :class="{
                            'marB': linkData.defaultB.length +
                                    linkData.linkTab.length +
                                    linkData.defaultC.length > 0
                        }"
                        class="defaultBg s-part"
                        @tab-click="handerTabClick"
                        @tab-change="handlerTabChange">
                        <!-- 主表数据 -->
                        <template v-slot:mainTab_0>
                            <module-form
                                :noTitle="true"
                                v-bind="_$attrs"
                                :fieldList.sync="tempFieldList"
                                :editScope="scope"
                                class="h-auto marginB"
                                v-on="$listeners">
                                <!-- 表单标题右侧插槽 -->
                                <template v-slot:titleRight>
                                    <slot name="titleRight"></slot>
                                </template>
                            </module-form>
                            <child-module-edit
                                v-for="(i, index) in linkData.mainTab[0] && linkData.mainTab[0].children && linkData.mainTab[0].children.slice(1)"
                                :ref="childRefName + 0"
                                :key="'mainTab' + 0 + index"
                                v-bind="getChildModuleAttr(i, index)"
                                class="h-auto marB"
                                :noTitle="false"
                                :fullHeight="false"
                                v-on="$listeners"
                                @getInitForm="(_form, baseFormScope) => setAllLinkForm(i, _form, baseFormScope, {tabName:'activeTabName',tabValue: 'mainTab_0'})">
                            </child-module-edit>
                        </template>
                        <template
                            v-for="(i, i_index) in linkData.mainTab.slice(1)"
                            v-slot:[i[tabKey.id]]>
                            <child-module-edit
                                v-for="(j, j_index) in i.children"
                                :ref="childRefName + (i_index + 1)"
                                :key="childRefName + (i_index + 1) + j_index"
                                :noTitle="isHideTitle(i)"
                                v-bind="getChildModuleAttr(j, j_index)"
                                :class="{
                                    'h-auto marginB': i.children.length > 1
                                }"
                                :fullHeight="!hasMuchPart && i.children.length === 1"
                                v-on="$listeners"
                                @getInitForm="(_form, baseFormScope) => setAllLinkForm(j, _form, baseFormScope, {tabName:'activeTabName',tabValue:i[tabKey.id]})">
                            </child-module-edit>
                        </template>
                    </bd-tabs>
                </template>
                <template v-if="linkData.defaultB.length > 0">
                    <child-module-edit
                        v-for="(i, index) in linkData.defaultB"
                        :key="'defaultB' + index"
                        v-bind="getChildModuleAttr(i, index)"
                        :noTitle="false"
                        :fullHeight="!hasMuchPart"
                        :class="{
                            'marB': index !== linkData.defaultB.length - 1 ||
                                    index === linkData.defaultB.length - 1 &&
                                    linkData.linkTab.length +
                                    linkData.defaultC.length > 0
                        }"
                        class="defaultBg"
                        v-on="$listeners"
                        @getInitForm="(_form, baseFormScope) => setAllLinkForm(i, _form, baseFormScope)">
                    </child-module-edit>
                </template>
                <template v-if="linkData.linkTab.length > 0">
                    <bd-tabs
                        :data="linkData.linkTab"
                        v-model="activeLinkTabName"
                        :before-leave="handerBeforeTabLeave"
                        :idField="tabKey.id"
                        :textField="tabKey.text"
                        :autoHeight="hasMuchPart"
                        :class="{
                            'marB': linkData.defaultC.length > 0
                        }"
                        class="defaultBg s-part"
                        @tab-click="handerTabClick"
                        @tab-change="handlerTabChange">
                        <template
                            v-for="(i, index) in linkData.linkTab"
                            v-slot:[i[tabKey.id]]>
                            <template
                                v-for="(j, j_index) in i.children">
                                <child-module-edit
                                    :ref="linkChildRefName + index"
                                    :key="j_index"
                                    :noTitle="isHideTitle(i)"
                                    v-bind="getChildModuleAttr(j, j_index)"
                                    :class="{
                                        'h-auto marginB': i.children.length > 1
                                    }"
                                    :fullHeight="!hasMuchPart"
                                    v-on="$listeners"
                                    @getInitForm="(_form, baseFormScope) => setAllLinkForm(j, _form, baseFormScope, {tabName:'activeLinkTabName',tabValue:i[tabKey.id]})">
                                </child-module-edit>
                            </template>
                        </template>
                    </bd-tabs>
                </template>
                <template v-if="linkData.defaultC.length > 0">
                    <child-module-edit
                        v-for="(i, index) in linkData.defaultC"
                        :key="'defaultC' + index"
                        v-bind="getChildModuleAttr(i, index)"
                        :noTitle="false"
                        :fullHeight="!hasMuchPart"
                        :class="{
                            'marB': index !== linkData.defaultC.length - 1
                        }"
                        class="defaultBg"
                        v-on="$listeners"
                        @getInitForm="(_form, baseFormScope) => setAllLinkForm(i, _form, baseFormScope)">
                    </child-module-edit>
                </template>
            </template>
            <!-- 表单区分两种：第二是没有关联关系的使用默认表单 -->
            <module-form
                v-else
                v-bind="_$attrs"
                fullHeight
                :fieldList.sync="tempFieldList"
                :editScope="scope"
                v-on="$listeners">
                <!-- 表单标题右侧插槽 -->
                <template v-slot:titleRight>
                    <slot name="titleRight"></slot>
                    <!-- 添加按钮区下的插槽位 -->
                    <render-fun v-if="titleRightSlot" :render="titleRightSlot"></render-fun>
                    <!-- 上一条/下一条的切换 -->
                    <toggle-prev-to-next
                        v-if="detailId && listInfo"
                        class="marL-10"
                        :currentIndexInTotal ="listInfo.currentIndexInTotal"
                        :total="listInfo.total"
                        @toggle="togglePrevNext">
                    </toggle-prev-to-next>
                </template>
            </module-form>
        </div>
        <!-- 按钮使用模型状态位：确保模型出现，按钮才有存在价值 -->
        <div
            class="bd-module-edit__btn"
            :class="{'fixed-bottom': fullHeight}"
            v-if="showBtnStatus">
            <template v-for="(i, index) in btnList">
                <bd-button
                    v-if="ShowStatus(i) && isFixBtnSpecAttr(i)"
                    class="bd-module-edit__btn__item"
                    :key="index"
                    v-bind="i"
                    @click="handleClick(i)">
                </bd-button>
            </template>
        </div>
    </div>
</template>
<script>
import ChildModuleEdit from './items/ChildModuleEdit'
import TogglePrevToNext from './items/TogglePrevToNext'
import BdTabs from '@/components/frame/Common/BdTabs'
import ModuleForm from '@/components/frame/Module/ModuleForm/index.vue'
import { Merge_Btn } from '@/service/module'
import { Deep_Clone } from '@/utils/clone'
import { ShowStatus, HandleClick, Update_FieldList_Value } from '@/service/module'
import RenderFun from '@/components/frame/RenderFun'
import { Has_Value } from '@/utils'
import { Sort_List } from '@/utils/list'
import GlobalConst from '@/service/global-const'
import { Get_Btn_ClickFun } from '@/service/event-expand'
import { Save, SaveAndNew, Back } from '@/components/frame/Module/BtnBaseFun/edit'
import { Save_URL } from '@/api/frame/common'
import { Get_ListItems, Update_Current_ListItem } from '@/service/list-item-ids'
// 定义默认的编辑按钮
const Default_Edit_Btn_List = [
    { id: 'back', click: Back, loading: false },
    { id: 'save', click: Save, loading: false },
]
// 定义默认的查看按钮
const Default_View_Btn_List = [
    { id: 'back', click: Back, loading: false },
]
const MainSlotName = 'main'
export default {
    name: "module-edit",
    inheritAttrs: false,
    components: {
        [BdTabs.name]: BdTabs,
        [ChildModuleEdit.name]: ChildModuleEdit,
        [ModuleForm.name]: ModuleForm,
        [TogglePrevToNext.name]: TogglePrevToNext,
        [RenderFun.name]: RenderFun,
    },
    props: {
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
        // 详情数据id，用于获取页面详细数据
        detailId: {
            type: String,
        },
        // 表单值详情数据
        formData: {
            type: Object
        },
        // 添加的表单数据
        addFormData: {
            type: Object,
        },
        // 字段数组
        fieldList: {
            type: Array,
            default: () => []
        },
        // 自定义配置【来源于自定义js】
        customSetting: {
            type: Object,
            default: () => {}
        },
        // 表单关联模块数组
        childTab: {
            type: Array,
            default: () => []
        },
        // 是否为查看
        isView: {
            type: Boolean,
            default: false
        },
        // 组件传入默认请求参数
        defaultParamsObj: {
            type: Object,
            default: () => {}
        },
        // 默认按钮列表
        defaultBtnList: {
            type: Array
        },
        // 整个页面为表单使用，操作按钮将固定底部进行操作
        // 默认不开启
        fullHeight: {
            type: Boolean,
            default: false
        },
        // 隐藏表单按钮状态
        hideBtn: {
            type: Boolean,
            default: false
        },
        // 按钮固定位置 top-顶部  bottom-底部，默认底部
        btnPlacement: {
            type: String,
            default: 'bottom'
        },
        // 当前表单模型是否作为关联的子模块
        isInLink: {
            type: Boolean,
            default: false
        }
    },
    data () { // 定义页面变量
        return {
            // 列表数据信息(主要用于表单的上一条/下一条的切换)
            listInfo: null,
            // tab相关键
            tabKey: {
                id: 'tabId',
                text: 'tabName',
            },
            // 关联-编辑表单的下角标键名
            linkEditIndexName: 'linkEditIndex',
            // 关联-编辑表单: 总和
            allLinkEditForm: [],
            // 判断是否展示的函数
            ShowStatus: ShowStatus,
            // 操作按钮数组
            btnList: [],
            // 按钮事件
            handleClick: HandleClick,
            // 表单字段列表
            tempFieldList: [],
            // 数据是否准备齐全
            isReady: false,
            // 标题右侧插槽
            titleRightSlot: null,

            // 主表插槽名
            mainSlotName: MainSlotName,
            // 主表+关联模块 组成Tab中:tab子组件通用名
            childRefName: 'childModuleEdit',
            // 关联模块 组成Tab中:tab子组件通用名
            linkChildRefName: 'linkChildModuleEdit',

            // 主表+关联模块 组成Tab中的选中项
            activeTabName: `mainTab_0`,
            // 关联模块 组成Tab中的选中项
            activeLinkTabName: 'linkTab_0',

            // 自定义保存接口地址函数
            diy_saveUrl: null,
            // 自定义功能按钮
            diy_buttons: null,
            // 自定义tab切换离开前事件
            diy_beforeTabLeave: null,
            // 自定义tab点击事件(点击不一定切换成功)
            diy_tabClick: null,
            // 自定义tab切换事件
            diy_tabChange: null,
            // 自定义-获取表单字段值的属性路径
            diy_valuePath: GlobalConst.form.valuePath,
            // 自定义-是否开启【保存后新增】按钮
            diy_openBtnSaveAndAdd: false,
            // 自定义-标题右侧插槽
            diy_titleRightSlot: null,
        }
    },
    computed: {
        // 当前组件作用域
        scope () {
            return this
        },
        // 主要用于关联模块场景：判断当前页面总共有多少个模块，以决策背景色
        hasMuchPart () {
            // 新增页，则只展示主表，肯定没有多个模块
            if (!this.detailId) return
            if (!(this._showLinkData && this._showLinkData.length > 0)) return
            // 只有一种情况满足当前只有一个大模块，则是所有关联配置均为平级Tab形式
            //     showStyle:展示风格 1-平级Tab(默认) / 2-子Tab / 3-按顺序展示
            return !this._showLinkData.every(i => !i.showStyle || i.showStyle === 1)
        },
        // 返回按钮显示状态
        showBtnStatus () {
            return !this.hideBtn && this.btnList.length > 0
        },
        // 获取当前组件模块作用域
        getScope () {
            return this
        },
        _mainCode () {
            return this.formData?.code?.value
        },
        // 获取模型设计器中：对模型表单的配置信息
        _formOption () {
            let formOptionStr = this.$attrs && this.$attrs.formOption
            if (formOptionStr) {
                return JSON.parse(formOptionStr) || {}
            }
            return {}
        },
        _$attrs () {
            return {
                ...this.$attrs, // 父组件传入属性（未声明在props部分）
                name: this.name, // 模型名称
                ref: this.refName, // 模型ref名称
                mdCode: this.mdCode,  // 模型编码
                detailId: this.detailId, // 模型详情数据id
                isView: this.isView,  // 是否查看模式
                customSetting: this.customSetting,  // 模型自定义配置项集合
                formData: this.formData, // 表单详情数据
                ...this._formOption, // 模型设计器中配置的表单属性
            }
        },
        // 表单标题
        title () {
            return this.name || GlobalConst.form.title
        },
        // 获取所有展示的关联模块
        _showLinkData () {
            // 判断当前模型是否有关联数据
            if (!this.childTab || this.childTab.length === 0) return
            // showTab:是否展示 0-不展示 1-展示，这里进行过滤选出展示的
            let _res = this.childTab.filter(i => `${i.showTab}` === '1')
            return _res.length > 0 && _res
        },
        // 关联tab数据
        linkData () {
            // 判断是否有关联数据
            if (!(this._showLinkData && this._showLinkData.length > 0)) return
            let _showLinkArr = this._showLinkData
            let _res = {
                mainDefault: [], // 放置主表+任意关联模块 [主表, 关联1, 关联2]
                mainTab: [], // 含主表的Tab(二维结构)，格式如[[主表，关联1], [关联2], [关联3, 关联4, 关联5]]
                defaultB: [], // 默认模块集合B  [关联7, 关联8, 关联9]
                linkTab: [], // 关联Tab(二维结构),格式如[[关联1,关联2], [关联3], [关联4, 关联5, 关联6]]
                defaultC: [], // 默认模块集合C [关联6, 关联7, 关联8, 关联9]
            }
            // 定义主表
            let _mainForm = {
                id: MainSlotName,
                text: this.title,
                [this.tabKey.text]: this.title,
            }
            
            // 存在详情id时才显示子tab，新增只显示主表
            if (this.detailId) {
                // 若存在关联模块配置的是平级Tab(showStyle无值或者为1),则主表需作为Tab展示，与其他关联模块成Tab结构
                if (this._showLinkData.some(i => !i.showStyle || i.showStyle && parseInt(i.showStyle) === 1)) {
                    this.addTabItem(_res.mainTab, _mainForm, { type: 'mainTab', isUnshift: true})
                } else {
                    // 不存在平级Tab的关联模块时，则主表放置在mainDefault作为独立模块
                    this.addTabItem(_res.mainDefault, _mainForm, { isUnshift: true })
                }
                _showLinkArr.forEach((i, index) => {
                    let _title = i.title || i.moduleName
                    // 定义关联模块
                    let _linkItem = {
                        ...i,
                        id: index.toString(),
                        // TODO：2022-01-25修复，此处应使用title（模型关联配置的展示名称），后面在删除i.moduleName
                        text: _title,
                        // 所属Tab名称
                        [this.tabKey.text]: i.tabName || _title,
                        name: i.moduleName,
                        mdCode: i.module,
                        relationEntityField: i.relationEntityField,
                    }
                    // showStyle:展示风格 1-平级Tab(默认) / 2-子Tab / 3-按顺序展示
                    switch (i.showStyle) {
                        case 2:
                            // 处理子Tab
                            this.addTabItem(_res.linkTab, _linkItem, { type: 'linkTab' })
                            break
                        case 3:
                            // // 当使用按顺序展示时，从上往下模块为：
                            //     mainDefault: [主表, 关联1, 关联2]
                            //     mainTab:[[主表, 关联3], [关联4], [关联5, 关联6]]
                            //     defaultB:[关联7, 关联8, 关联9]
                            //     linkTab:[[关联10, 关联11], [关联12], [关联13, 关联14, 关联15]]
                            //     defaultC:[关联16, 关联17, 关联18, 关联19]

                            // 按顺序的模块总共有3块可能放置的地方，mainDefault/defaultB/defaultC，下面对应三种情况
                            if ([..._res.mainTab, ..._res.defaultB,  ..._res.linkTab,  ..._res.defaultC].length === 0) {
                                _res.mainDefault.push(_linkItem)
                            } else if ([..._res.linkTab,  ..._res.defaultC].length === 0) {
                                _res.defaultB.push(_linkItem)
                            } else {
                                _res.defaultC.push(_linkItem)
                            }
                            break
                        case 1:
                        default:
                            // 默认值：平级Tab的处理
                            this.addTabItem(_res.mainTab, _linkItem,  { type: 'mainTab' })
                    }
                })

                // 添加模块顺序，方便后面表单校验时按照此顺序从上往下进行校验，不要出现校验先滚动底下的表单又滚动上面的表单校验
                let _tempLinkArr = []
                // 从上往下模块为：
                //     mainDefault: [主表, 关联1, 关联2]
                //     mainTab:[[主表, 关联3], [关联4], [关联5, 关联6]]
                //     defaultB:[关联7, 关联8, 关联9]
                //     linkTab:[[关联10, 关联11], [关联12], [关联13, 关联14, 关联15]]
                //     defaultC:[关联16, 关联17, 关联18, 关联19]
                // 接下来按照该顺序进行处理
                _tempLinkArr.push(..._res.mainDefault.filter(i => i.id !== MainSlotName))
                _res.mainTab.forEach(i => {
                    _tempLinkArr.push(...i.children.filter(j => j.id !== MainSlotName))
                })
                _tempLinkArr.push(..._res.defaultB)
                _res.linkTab.forEach(i => {
                    _tempLinkArr.push(...i.children)
                })
                _tempLinkArr.push(..._res.defaultC)

                // 按顺序添加模块后，为模块添加下角标
                _tempLinkArr.forEach((i, index) => {
                    i[this.linkEditIndexName] = index + 1
                })

            } else {
                // 添加主tab--主表模块
                _res.mainDefault = [_mainForm]
            }
            return _res
        },
        // 主表+关联模块 组成Tab中的选中下角标
        activeTabIndex () {
            if (this.linkData?.mainTab?.length > 0 && this.activeTabName) {
                return this.linkData.mainTab.findIndex(i => i[this.tabKey.id] === this.activeTabName)
            }
            return 0
        },
        // 关联模块 组成Tab中的选中下角标
        activeLinkTabIndex () {
            if (this.linkData?.linkTab?.length > 0 && this.activeLinkTabName) {
                return this.linkData.linkTab.findIndex(i => i[this.tabKey.id] === this.activeLinkTabName)
            }
            return 0
        },
        /**
         * 存储初始按钮，不包含自定义传入的按钮，方便在自定义按钮事件中去获取默认按钮中的事件进行再改造
         */
        _defaultBtnList () {
            // 定义结果集
            let _result = null
            // 判断是否传入初始默认按钮
            if (this.defaultBtnList &&
                this.defaultBtnList.constructor === Array) {
                _result = [...(this.defaultBtnList || [])]
            } else {
                // 根据是否编辑状态，对应获取编辑页面的按钮与查看页面的按钮
                _result = [...(!this.isView ? Default_Edit_Btn_List : Default_View_Btn_List || [])]
            }
            // 如果是编辑页，且当前开启了【保存并新增】的按钮选项，则添加该按钮
            if (!this.isView && this.diy_openBtnSaveAndAdd) {
                _result.push({
                    id: 'saveAndNew',
                    click: SaveAndNew,
                    loading: false,
                    name: '保存后新增',
                    icon: 'add',
                    type: 'primary'
                })
            }
            return _result
        },
        // 获取保存接口的url
        _saveUrl () {
            // 判断是否传入自定义获取保存接口地址的函数
            if (this.diy_saveUrl && typeof this.diy_saveUrl === 'function') {
                return this.diy_saveUrl.call(this, this.mdCode)
            }
            // 返回默认的表单保存接口地址
            return Save_URL(this.mdCode)
        },
    },
    methods: { // 定义函数
        /**
         * 是否隐藏tab下模块的标题
         * @param {Object} tabItem tab模块对象
         */
        isHideTitle (tabItem) {
            // 获取tab名称：tabName
            // 获取tab下的模块数组： children
            let { tabName, children } = tabItem
            // 当tab下仅有一个模块，且模块的名称与tab名称一致时，则不显示模块名称
            if (children &&
                children.length === 1 &&
                children[0].title === tabName) {
                return true
            }
            return false
        },
        /**
         * 切换上一条/下一条
         * @param {Number} status 1/-1
         */
        togglePrevNext (status) {
            // 更新页面数据
            Update_Current_ListItem.call(this, status ? 1 : -1)
        },
        /**
         * 在tab列表中添加模块数据
         * @param {Array} tabList tab列表数据,数据结构为[{name:'A',children:[{模块1},{模块2}]},{name:'B',children:[{模块3},{模块4}]}]
         * @param {Object} addItem 添加的模块对象
         * @param {Option} option 配置项
         *      @param {Boolean} isUnshift 是否加在列表前面 默认为false
         *      @param {String} type 可选值为mainTab / linkTab
         */
        addTabItem (tabList, addItem, option) {
            let { tabName, showStyle = 1 } = addItem
            let { 
                isUnshift = false, // 用于判断数据是在前面加还是后面加
                type, // 用于生成tabId
            } = option || {}
            let _tabIndex = tabList.findIndex(i => i.tabName === tabName && i.showStyle === showStyle)
            let _operate = isUnshift ? 'unshift' : 'push'
            // 若能匹配到，则添加数据
            if (~_tabIndex) {
                tabList[_tabIndex].children[_operate](addItem)
                return
            }
            // 若不能匹配到，则新增个模块加入
            tabList[_operate]({
                [this.tabKey.text]: tabName,
                showStyle,
                children: [addItem]
            })
            // 更新tab下模块属性
            if (type) {
                tabList.forEach((i, index) => i[this.tabKey.id]= `${type}_${index}`)
            }
        },
        /**
         * allLinkEditForm主要用于存储所有关联编辑表单以便提交接口时使用，当前函数主要用于收集关联的编辑表单信息
         *      1. 后续使用此内容判断关联表单是否变更过，以便减少不必要的保存接口请求
         *      2. 只判断关联表单，主表在后续保存的逻辑中将始终保存，不检查是否变更
         * @param {Object} linkItem 关联模块对象
         * @param {Object} formObj 表单值对象数据
         * @param {Object} formScope 表单基础页面mform/index所在的作用域
         * @param {Object} option 配置项
         *      @param {String} tabName 所属tab模块的变量名
         *      @param {String} tabValue tab对应值
         */
        setAllLinkForm (linkItem, formObj, formScope, option) {
            // 由于校验多个表单出错时，页面滚动到对应错误字段是有顺序的，需要按照从上到下，所以关联数据在维护的时候会添加字段linkEditIndexName(按页面排版的从上到下，从左到右) 
            this.allLinkEditForm = [
                {
                    form: formObj, // 表单值对象
                    editScope: formScope?.$attrs?.editScope, // 表单对应的moduleEdit/index.vue页面作用域
                    formScope: formScope, // 基础表单mform/index.vue页面作用域
                    [this.linkEditIndexName]: linkItem[this.linkEditIndexName],
                    ...option, // 其余配置项
                },
                ...this.allLinkEditForm
            ].sort((a, b) => a[this.linkEditIndexName] > b[this.linkEditIndexName])
        },
        /**
         * 判断按钮是否符合显示规则(根据属性isEdit / isView)
         * @param {Object} btnObj 按钮对象
         */
        isFixBtnSpecAttr (btnObj) {
            let _btnAttr = {
                isEdit: 'isEdit', // 是否只展示在编辑页面
                isView: 'isView', // 是否只显示在查看页面
            }
            // 判断按钮属性中是否含有isEdit属性
            if (_btnAttr.isEdit in btnObj && btnObj[_btnAttr.isEdit]) {
                return !this.isView
            }
            // 判断按钮属性中是否含有isView属性
            if (_btnAttr.isView in btnObj && btnObj[_btnAttr.isView]) {
                return this.isView
            }
            // 其他情况，默认返回true
            return true
        },
        /**
         * 这里核心的面向模型js中调用的，获取基础事件，所以只在最初始默认的按钮事件中匹配
         * 场景:需要重写按钮事件，但核心的逻辑又是一致的只是修改少量代码时就可以通过当前函数快速获取默认事件，执行后再添加自己的少量业务代码
         * @param {String} id 函数id
         */
        getBaseBtnFun (id) {
            return Get_Btn_ClickFun(id, this._defaultBtnList)
        },
        /**
         * 获取传递给child-module-edit组件的属性集合
         * @param {Object} item 关联模块数据对象
         * @param {Number} index 模块所处下角标
         */
        getChildModuleAttr (item, index) {
            return {
                isView: this.isView,
                index,
                childData: item,
                mainFieldList: this.tempFieldList,
                mainScope: this.getScope,
                mainId: this.detailId,
                // mainMdCode: this._mainCode, TODO:不知道为什么使用这个而不使用this.mdCode
                mainMdCode: this.mdCode,
                title: item.text, // 模块标题
                showType: item.showType,
                moduleName: item.name,
                mdCode: item.mdCode,
                entityField: item.entityField,
                relationEntityField: item.relationEntityField,
                ...this._formOption,  // 模型设计器中配置的表单属性
            }
        },
        // 2021-06-25变更，原先是放在data变量中，通过mounted更新btnList,现在放置计算属性computed
        //     原因：当按钮数据this.defaultBtnList处理延迟时间时传入时，组件在mounted中已经处理完成，mounted无法再次触发；所以需要放置计算属性随时根据传入this.defaultBtnList进行变化
        
        // 2021-07-01变更，放置computed导致按钮的click事件中的loading失效，需要放置function中，再由其他逻辑调用
        // 获取按钮数组
        async getBtnList () {
            // 定义按钮结果集
            let result = Deep_Clone(this._defaultBtnList)
            // 判断是否存在自定义按钮
            if (this.diy_buttons) {
                // 1. 支持传入函数，函数需return回最终的按钮数据
                if (this.diy_buttons.constructor === Function) {
                    // 执行函数
                    result = await this.diy_buttons.call(this, result)
                }
                // 2. 支持直接传入按钮组，会自行将默认与自定义按钮进行合并
                if (this.diy_buttons.constructor === Array) {
                    // 合并按钮
                    result = Merge_Btn(result, this.diy_buttons)
                }
            }
            // 当前处于关联子模块的场景，只留出允许在关联下展示的按钮，其他按钮不展示
            if (this.isInLink) {
                result = result.filter(i => i.isInLink)
            }
            // 按钮排序key
            let sortKey = GlobalConst.button.sortKey
            // 获取指定按钮排序规则（根据id）
            let btnAttrs = GlobalConst.button.editAttrs
            // 其他按钮默认排序
            let defaultIndex = GlobalConst.button.sortIndex
            result = result.map(b => {
                if (btnAttrs[b.id]) {
                    b = {
                        ...btnAttrs[b.id],
                        ...b
                    }
                }
                b[sortKey] = Has_Value(b[sortKey]) ? b[sortKey] : defaultIndex
                return b
            })
            result = Sort_List(result, sortKey)
            // 返回最终按钮组数据
            return result || []
        },
        // [主表+关联模块]组成Tab: 获取当前所在tab页面作用域
        //     由于Tab下的多个模块并没有一个父级包裹，所以这里优化规则：
        //         当tab下只有一个模块时，则返回该模块作用域(Object：模块1域)
        //         当tab下有多个模块时，则返回多个模块作用域(Array[模块1域,模块2域])
        getCurrentTabScope () {
            if (this.childTab.length === 0) return
            // 定义结果集
            let _result = []
            // 若当前为mainTab的首个Tab活跃选中，则优先添加主表域
            if (this.activeTabIndex === 0) {
                _result.push(this.$refs[this.refName])
            }
            // 添加mainTab活跃Tab下的所有关联模块
            _result.push(...(this.$refs[this.childRefName + this.activeTabIndex] || []))
            // 若活跃Tab下只存在一个模块域，则返回该域，反之返回多模块域的数组
            return _result.length === 1 ? _result[0] : _result
        },
        // [关联模块]组成Tab中: 获取当前所在tab页面作用域
        getLinkCurrentTabScope () {
            if (this.childTab.length === 0) return
            let _result = this.$refs[this.linkChildRefName + this.activeLinkTabIndex]
            // 若活跃Tab下只存在一个模块域，则返回该域，反之返回多模块域的数组
            return _result.length === 1 ? _result[0] : _result
        },
        // 获取表单所在作用域，以便按钮事件中可以操作
        // 【非常重要！！！】，需要此处写暴露给外面用，避免页面结构发生变化，js中获取表单逻辑也需要变更
        getFormRef () {
            let scope = this.$refs?.[this.refName]?.$refs?.[this.refName]
            return scope
        },
        /**
         * 使用自定义js配置项更新页面数据
         * @param {Object} customSetting 动态js配置对象
         */
        updateCustomSetting (customSetting) {
            // 获取动态自定义js文件customSetting
            let {
                saveUrl, // 自定义保存接口地址函数
                buttons,  // 自定义-功能按钮 {Array}
                beforeTabLeave, // 自定义-tab切换前事件
                tabClick, // 自定义-tab点击事件
                tabChange, // 自定义-tab切换事件
                valuePath, // 自定义-获取表单字段值的属性路径
                openBtnSaveAndAdd, // 自定义-是否开启【保存后新增】按钮
                titleRightSlot, // 自定义-标题右侧插槽
            } = customSetting || {}
            // 更新-自定义保存接口地址函数
            this.diy_saveUrl = saveUrl
            // 更新-自定义功能按钮
            this.diy_buttons = buttons
            // 更新-自定义tab切换离开前事件
            this.diy_beforeTabLeave = beforeTabLeave
            // 自定义tab点击事件(点击不一定切换成功)
            this.diy_tabClick = tabClick
            // 自定义tab切换事件
            this.diy_tabChange = tabChange
            // 自定义-获取表单字段值的属性路径
            this.diy_valuePath = valuePath
            // 自定义-是否开启【保存后新增】按钮
            this.diy_openBtnSaveAndAdd = openBtnSaveAndAdd
            // 自定义-标题右侧插槽
            this.diy_titleRightSlot = titleRightSlot
        },
        /**
         * tab切换离开前事件
         * @param {String} activeName 期望去的tab的name值
         * @param {String} oldActiveName 当前所在tab的name值
         */
        handerBeforeTabLeave (activeName, oldActiveName) {
            // 若当前为查看模式，直接允许切换
            if (this.isView) return true
            // 判断是否存在自定义tab切换离开前事件
            if (this.diy_beforeTabLeave &&
                typeof this.diy_beforeTabLeave === 'function') {
                // 存在自定义事件，执行函数，diy_beforeTabLeave函数返回值为以下两种
                // 1. Boolean(false时表示不切换)
                // 2. Promise(reject时表示不切换)
                return this.diy_beforeTabLeave.call(this, activeName, oldActiveName)
            } else {
                // 执行默认事件：处理表单新增状态时
                if (!this.isView && !this.detailId) {
                    activeName && this.$message({
                        message: '请先保存数据',
                        type: 'warning'
                    })
                    return false
                }
            }
            return true
        },
        /**
         * tab 被点击时触发，注意点击不一定代表tab切换成功
         * @param {Object} activeVueComponent
         */
        handerTabClick (currentTab, index, tabData, tabVueComponent) {
            // 判断是否存在自定义tab点击事件
            if (this.diy_tabClick && typeof this.diy_tabClick === 'function') {
                // 执行函数
                this.diy_tabClick.apply(this, arguments)
            }
        },
        /**
         * tab切换变更事件
         * @param {String} value 当前tab选中项的值
         * @param {Array} tabData tab数组数据
         */
        handlerTabChange (currentTab, index, tabData) {
            if (this.diy_tabChange && typeof this.diy_tabChange === 'function') {
                // 执行函数
                this.diy_tabChange.apply(this, arguments)
                return
            }
            // 非自定义tab-change事件则执行下面逻辑
            this.$emit('tabChange', currentTab, index, tabData)
        },
        /**
         * 渲染页面的插槽区
         */
        renderSlot () {
            // 渲染标题栏右侧插槽（模型js传入）
            this.getTitleRightSlot()
        },
        // 获取标题栏右侧插槽
        async getTitleRightSlot () {
            if (!(this.diy_titleRightSlot && typeof this.diy_titleRightSlot === 'function')) return
            this.titleRightSlot = await this.diy_titleRightSlot.call(this)
        },
    },
    // 可访问当前this实例
    created () {
    },
    // 挂载完成，可访问DOM元素
    async mounted () {
        // 获取缓存的列表数据(用于上一条/下一条切换)
        this.listInfo = await Get_ListItems.call(this)
        // 使用自定义js配置项更新页面数据
        this.updateCustomSetting(this.customSetting)
        // 更新按钮列表
        this.btnList = await this.getBtnList()
        // 使用接口表单具体值更新给【字段数组】对象值
        let changeOption = { isView: this.isView }
        if (this.diy_valuePath !== null && this.diy_valuePath !== undefined) {
            changeOption = { ...changeOption, valuePath: this.diy_valuePath }
        }
        // 定义表单值
        let _formData = Object.assign({}, this.formData)
        // 处理添加的表单值
        if (this.addFormData) {
            // 同时将添加的表单值更新进表单值中，以添加的表单值优先级为高
            let {
                valuePath = GlobalConst.form.valuePath, // 表单详情值对象值下每个字段值下属性名
            } = changeOption
            // 将传入的自定义表单数据更新到即将提交的表单字段数组中
            Object.keys(this.addFormData).forEach(i => {
                // 获取匹配项
                let currentField = this.fieldList.find(j => j.name === i)
                let _value = this.addFormData[i]
                // 存在匹配字段，则直接更新其值
                if (currentField) {
                    currentField.value = _value
                } else {
                    // 不存在则在字段数组中插入一条数据，隐藏状态
                    // 必须使用unshift，避免覆盖已有字段的变更导致不生效
                    this.fieldList.unshift({
                        name: i,
                        value: this.addFormData[i],
                        type: 'hidden'
                    })
                }
                // 判断值路径是否存在
                _formData[i] = valuePath ? { [valuePath]: _value } : _value
            })
        }
        // 将表单字段详情值 更新到 表单字段列表上
        this.tempFieldList = Update_FieldList_Value(this.fieldList, _formData, changeOption)
        this.isReady = true
        // 开始渲染页面的插槽区
        this.renderSlot()
    }
}
</script>
<style lang='scss' scoped>
.bd-module-edit::v-deep {
    border-radius: $borderRadius;
    height: 100%;
    &.has-btn {
        height: calc(100% - #{$footerHeight});
    }
    .bd-module-edit__main {
        .bd-tabs {
            .el-tabs {
                .el-tabs__content {
                    .el-tab-pane {
                        .bd-child-module-edit {
                            &:last-child {
                                margin-bottom: 0;
                            }
                        }
                    }
                }
            }
        }
        &.has-much-part {
            overflow: auto;
            .s-part:last-child {
                margin: 0 !important;
            }
        }
    }

    .bd-module-edit__btn {
        height: $footerHeight;
        line-height: $footerHeight - 2px;
        width: 100%;
        background: #fff;
        text-align: center;
        border-top: 1px solid $lineColor;
        &.fixed-bottom {
            border-top: none;
            position: absolute;
            bottom: 0;
            left: 0;
            z-index: 2;
            box-shadow: 0px 9px 15px 3px rgba(102,102,102,0.5);
        }
    }
    .after-main-form {
        .bd-form-part {
            .m-title {
                font-size: $font;
            }
        }
    }
}
</style>