<!--
 * 模型设计器 - 编辑新增模块：关联关系
-->
<template>
    <div class="relation">
        <div class="form-title">
            <bd-icon name="pillar-fill" class="pillar fill"></bd-icon>
            <span>关联字段</span>
            <bd-button
                icon="add-fill"
                type="primary"
                plain
                @click="addRelation">
                添加关联
            </bd-button>
            <bd-button
                v-if="showList.length > 0"
                icon="view-fill"
                type="success"
                plain
                @click="showDragLayout">
                简易可视化排版
            </bd-button>
            <!-- <bd-button
                v-if="showList.length > 1"
                icon="exchange-fill"
                type="success"
                plain
                @click="sortRelation">
                快速排序
            </bd-button> -->
        </div>
        <div class="relation-content">
            <div v-if="showList.length"  ref="group" class="relation-card-group">
                <div class="card-box">
                    <div
                        v-for="(r, r_index) in showList"
                        :key="r.u_id"
                        :style="{'order': r.priority}"
                        :class="{'is-last': isLast(r, showList) }"
                        class="relation-card-item">
                        <triangle-status
                            :is-close="true"
                            @click="delectRelation(r, r_index)">
                        </triangle-status>
                        <!-- 内容 -->
                        <div class="relation-form-box">
                            <bd-skeleton :loading="!r.isFormStartRender" :rows="3">
                                <module-form
                                    noTitle
                                    ref="moduleForm"
                                    v-bind="r.module"
                                    :columnNum="3"
                                    labelWidth="126px"
                                    :fieldList.sync="r.value"
                                    :mainScope="mainScope"
                                    class="content-form__main padR-10"
                                    :isFormStartRender.sync="r.isFormStartRender">
                                </module-form>
                            </bd-skeleton>
                        </div>
                    </div>
                </div>
            </div>
            <no-data v-else height="300"></no-data>
        </div>
        <!-- 可视化关联排版 -->
        <bd-dialog
            class="font"
            :outScope="scope"
            title="可视化排版"
            :visible.sync="showLayoutDialog"
            :destroy-on-close="true"
            width="90%">
            <template v-slot:operate-btn>
                <el-popover
                    placement="right"
                    width="400"
                    trigger="click">
                    <div class="fontS">
                        <div class="font bold">说明：</div>
                        <div>
                            1. 从上往下区域划分为5块，依次为A、B、C、D、E区域<br/>
                            2. 【B区域】与【D区域】为Tab区，区别为是否放置了主表<br/>
                            3. 【按住】模块可进行拖动与放置<br/>
                            4. 【双击】模块或者tab名称可进行修改<br/>
                        </div>
                        <br/>
                        <div class="font bold dangerC">注意：</div>
                        <div class="warningC">
                            1. 主表一般只放置在A或B两块区域，并且相对位置要排第一<br/>
                            2. 主表的模块名称不允许修改<br/>
                            3. 主表所在Tab其Tab名不允许修改<br/>
                            4. 弹窗底部的保存没走保存接口，只会更新页面数据
                        </div>
                    </div>
                    <bd-button slot="reference" icon="configureH" type="text" title="使用说明书">使用说明书</bd-button>
                </el-popover>
            </template>
            <div class="s-layout">
                <div class="s-layout__content" v-if="layoutData">
                    <draggable
                        v-model="layoutData[dragName.mainDefault]"
                        v-bind="dragOptions"
                        class="s-group"
                        tag="div"
                        @end="endDrag">
                        <div
                            class="s-item can-drag"
                            v-for="(i, i_index) in layoutData[dragName.mainDefault]"
                            :key="dragName.mainDefault + i_index">
                            <relation-item-name v-bind="i" v-model="i.name"></relation-item-name>
                        </div>
                        <div class="padding drag-tip" slot="header" v-if="layoutData[dragName.mainDefault].length === 0">
                            可放置A区域
                        </div>
                    </draggable>
                    <span class="fontS fontC">含主表的Tab区 （主表若没有放在此区域，则该区自动转化为纯关联Tab区）</span>
                    <draggable
                        v-model="layoutData[dragName.mainTab]"
                        v-bind="dragOptions"
                        handle=".allow-drag"
                        :class="{
                            'needPadding': layoutData[dragName.mainTab].length === 0
                        }"
                        class="s-group s-group-tab"
                        tag="div"
                        @end="endDrag">
                        <div
                            class="s-item can-drag noCursor"
                            v-for="(i, i_index) in layoutData[dragName.mainTab]"
                            :key="dragName.mainTab + i_index">
                            <relation-item-name v-model="i.tabName" class="s-item-tabName"></relation-item-name>
                            <draggable
                                class="s-group"
                                tag="div"
                                v-model="i.children"
                                v-bind="dragOptions"
                                @end="endDrag">
                                <div
                                    v-for="(j, j_index) in i.children"
                                    :key="dragName.mainTab + j_index"
                                    :name="j.name"
                                    class="s-item can-drag">
                                    <relation-item-name v-bind="j" v-model="j.name"></relation-item-name>
                                </div>
                            </draggable>
                        </div>
                        <div class="padding drag-tip" slot="header"  v-if="layoutData[dragName.mainTab].length === 0">
                            可放置B区域
                        </div>
                    </draggable>
                    <draggable
                        v-model="layoutData[dragName.defaultB]"
                        v-bind="dragOptions"
                        class="s-group"
                        tag="div"
                        @end="endDrag">
                        <div
                            class="s-item can-drag"
                            v-for="(i, i_index) in layoutData[dragName.defaultB]"
                            :key="dragName.defaultB + i_index"
                            :name="i.name">
                            <relation-item-name v-bind="i" v-model="i.name"></relation-item-name>
                        </div>
                        <div class="padding drag-tip" slot="header" v-if="layoutData[dragName.defaultB].length === 0">
                            可放置C区域
                        </div>
                    </draggable>
                    <span class="fontS fontC">纯关联组成的Tab区</span>
                    <draggable
                        v-model="layoutData[dragName.linkTab]"
                        v-bind="dragOptions"
                        :class="{
                            'needPadding': layoutData[dragName.linkTab].length === 0
                        }"
                        class="s-group s-group-tab"
                        tag="div"
                        @end="endDrag">
                        <div
                            class="s-item can-drag noCursor"
                            v-for="(i, i_index) in layoutData[dragName.linkTab]"
                            :key="dragName.linkTab + i_index">
                            <relation-item-name v-model="i.tabName" class="s-item-tabName"></relation-item-name>
                            <draggable
                                class="s-group"
                                tag="div"
                                v-model="i.children"
                                v-bind="dragOptions"
                                @end="endDrag">
                                <div
                                    v-for="(j, j_index) in i.children"
                                    :key="dragName.linkTab + j_index"
                                    class="s-item can-drag">
                                    <relation-item-name v-bind="j" v-model="j.name"></relation-item-name>
                                </div>
                            </draggable>
                        </div>
                        <div class="padding drag-tip" slot="header" v-if="layoutData[dragName.linkTab].length === 0">
                            可放置D区域
                        </div>
                    </draggable>
                    <draggable
                        v-model="layoutData[dragName.defaultC]"
                        v-bind="dragOptions"
                        class="s-group"
                        tag="div"
                        @end="endDrag">
                        <div
                            class="s-item can-drag"
                            v-for="(i, i_index) in layoutData[dragName.defaultC]"
                            :key="dragName.defaultC + i_index">
                            <relation-item-name v-bind="i" v-model="i.name"></relation-item-name>
                        </div>
                        <div class="padding drag-tip" slot="header"  v-if="layoutData[dragName.defaultC].length === 0">
                            可放置E区域
                        </div>
                    </draggable>
                </div>
            </div>
            <template v-slot:footer>
                <bd-button icon="save" type="primary" @click="saveLayout">保存</bd-button>
                <bd-button icon="cancel" type="danger" @click="showLayoutDialog = false">取消</bd-button>
            </template>
        </bd-dialog>
    </div>
</template>

<script>
import { Load_Field_Info } from '@/api/frame/desinger/field'
import ModuleForm from '@/components/frame/Module/ModuleForm/index.vue'
import NoData from '@/components/frame/NoData'
import RelationItemName from '@/views/common/modelDesigner/component/RelationItemName'
import TriangleStatus from '@/components/frame/Status/TriangleStatus'
import ModuleUtils from '@/js/ModuleUtils'
import { Deep_Clone } from '@/utils/clone'
import { EventBus } from '@/service/event-bus'
import { Update_FieldList_Value } from '@/service/module'
import Skeleton from '@/components/frame/Skeleton'
import Visulize from '@/service/visulize/index'
import { Get_UUID } from '@/utils'
import { Sort_List } from '@/utils/list'
import { Scroll_Bottom } from '@/utils/animate'
import Dialog from '@/components/frame/Dialog/index.vue'
import draggable from 'vuedraggable'
// 定义排序字段
const PriorityName = 'priority'
// 定义判断是否主表的属性
const IsMainForm = 'isMainForm'
// 定义【展示风格】的数据字典
const ShowStyle = {
    mainTab: '1', // 平级Tab
    linkTab: '2', // 子级Tab
    priority: '3' // 按排序号位置进行展示
}
export default {
    components: {
        NoData,
        [ModuleForm.name]: ModuleForm,
        [Skeleton.name]: Skeleton,
        [TriangleStatus.name]: TriangleStatus,
        [Dialog.name]: Dialog,
        [RelationItemName.name]: RelationItemName,
        draggable
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
        mainScope: null
    },
    data () {
        return {
            // 是否展示排版拖拽窗口
            showLayoutDialog: false,
            // 排版拖拽窗初始数据
            layoutData: null,
            // 模块名称集合定义
            dragName: {
                mainDefault: 'mainDefault',
                mainTab: 'mainTab',
                defaultB: 'defaultB',
                linkTab: 'linkTab',
                defaultC: 'defaultC'
            },
            mdCode: 'sys_module_link',
            // 卡片表单模型module信息（请求回来的，不经处理过的）
            module: {},
            showList: [],
        }
    },
    computed: {
        // 当前组件作用域
        scope () {
            return this
        },
        dragOptions () {
            return {
                animation: 500,
                group: 'description',
                ghostClass: 'ghost',
                chosenClass: 'sortable-chosen',  // Class name for the chosen item
	            dragClass: 'sortable-drag',
                draggable: '.can-drag',
                scroll: true,
            }
        }
    },
    methods: {
        /**
         * 展示可视化排版窗口
         */
        showDragLayout () {
            if (!(this.showList && this.showList.length > 0)) {
                this.$message.warning('当前无可操作数据，请先添加数据')
                return
            }
            // 过滤掉不展示的关联模块
            let _showList = this.showList.filter(i => {
                let _showTabField = i.value.find(j => j.name === 'showTab')
                return _showTabField.value && parseInt(_showTabField.value) === 1
            })
            if (_showList.length === 0) {
                this.$message.warning('当前所有模块【是否展示】配置是不展示，则不进行处理')
                return
            }
            // 过滤掉没有展示名称的模块
            if (this.showList.some(i => !i.value.find(j => j.name === 'displayName')?.value)) {
                this.$message.warning('请优先填写所有模块的【展示名称】')
                return
            }

            // 获取主表的模块名称
            let _mainFormName = this.mainScope.fieldList.find(i => i.name === 'moduleName')?.value
            // 组装模块数据
            let _data = [
                { name: _mainFormName, tabName: '', [PriorityName]: 1, [IsMainForm]: true },
                ...Sort_List(this.showList, PriorityName).map((i, i_index) => {
                    let _item = i.value.reduce((res, j) => {
                        let { name, value } = j
                        res[name] = value
                        return res
                    }, {})
                    let { showStyle, tabName, displayName } = _item
                    return {
                        name: displayName, // 展示名称
                        tabName, // 所属Tab名称
                        [PriorityName]: i_index + 1, // 排序号
                        showStyle, // 展示风格
                        id: i.u_id, // 模块标识id,方便后面将值更新回来时，按照此索引匹配模块
                    }
                })
            ]
            // 到此获取到所有模块数据，接下来对关联模块数据进行分类
            this.layoutData = this.getLayoutData(_data)
            // 数据准备完毕，设置开启弹窗
            this.showLayoutDialog = true
        },
        /**
         * 根据一维的关联模块数据获取二维的模块(区分放置区域下)
         * @param {Array} data 关联数据
         */
        getLayoutData (data) {
            let _res = {
                mainDefault: [], // 放置主表+任意关联模块 [主表, 关联1, 关联2]
                mainTab: [], // 含主表的Tab(二维结构)，格式如[[主表，关联1], [关联2], [关联3, 关联4, 关联5]]
                defaultB: [], // 默认模块集合B  [关联7, 关联8, 关联9]
                linkTab: [], // 关联Tab(二维结构),格式如[[关联1,关联2], [关联3], [关联4, 关联5, 关联6]]
                defaultC: [], // 默认模块集合C [关联6, 关联7, 关联8, 关联9]
            }
            // 获取传入的模块中主表所在的下角标
            let _mainFormIndex = data.findIndex(i => i[IsMainForm])
            // 获取主表
            let _mainForm = data.splice(_mainFormIndex, 1)[0]
            // 根据模块，获取模块所属Tab名称 ，次之为展示名称
            let getTabName = (item) => item.tabName || item.name
            // 优先处理主表位置
            if (data.some(i => this.getItemShowStyle(i) === ShowStyle.mainTab)) {
                // 只要存在一个关联使用了平级Tab的展示，则主表放置在mainTab下
                this.$set(_mainForm, 'tabName', getTabName(_mainForm))
                this.$set(_mainForm, 'showStyle', ShowStyle.mainTab)
                this.addTabItem(_res.mainTab, _mainForm)
            } else {
                _res.mainDefault.push(_mainForm)
            }
            data
                .filter(i => !i[IsMainForm])
                .forEach(i => {
                    // showStyle:展示风格 1-平级Tab(默认) / 2-子Tab / 3-按顺序展示
                    switch (this.getItemShowStyle(i)) {
                        // 处理子Tab
                        case ShowStyle.linkTab:
                            this.addTabItem(_res.linkTab, i)
                            break
                        // 处理按顺序展示
                        case ShowStyle.priority:
                            // // 当使用按顺序展示时，从上往下模块为：
                            //     mainDefault: [主表, 关联1, 关联2]
                            //     mainTab:[[主表, 关联3], [关联4], [关联5, 关联6]]
                            //     defaultB:[关联7, 关联8, 关联9]
                            //     linkTab:[[关联10, 关联11], [关联12], [关联13, 关联14, 关联15]]
                            //     defaultC:[关联16, 关联17, 关联18, 关联19]

                            // 按顺序的模块总共有3块可能放置的地方，mainDefault/defaultB/defaultC，下面对应三种情况
                            if ([..._res.mainTab, ..._res.defaultB,  ..._res.linkTab,  ..._res.defaultC].length === 0) {
                                _res.mainDefault.push(i)
                            } else if ([..._res.linkTab,  ..._res.defaultC].length === 0) {
                                _res.defaultB.push(i)
                            } else {
                                _res.defaultC.push(i)
                            }
                            break
                        // 默认使用与主表同级Tab
                        case ShowStyle.mainTab:
                        default:
                            // 默认值：平级Tab的处理
                            this.addTabItem(_res.mainTab, i)
                    }
                })
            return _res
        },
        /**
         * 获取模块的展示风格值
         * @param {Object} item 关联模块
         */
        getItemShowStyle (item) {
            return item.showStyle || ShowStyle.mainTab
        },
        /**
         * 为tab二维结构的数据添加模块
         * @param {Array} tabList tab列表
         * @param {Object} addItem 需要添加的模块
         */
        addTabItem (tabList, addItem) {
            // 获取所属Tab名称
            let { name, tabName, showStyle } = addItem
            if (tabName) {
                // 在已有列表中查找是否有匹配项
                let _tabIndex = tabList.findIndex(i => i.tabName === tabName && i.showStyle === showStyle)
                // 若能匹配到，则添加数据
                if (~_tabIndex) {
                    tabList[_tabIndex].children.push(addItem)
                    return
                }
            }
            // 若不能匹配到，则新增个模块加入
            tabList.push({
                tabName: tabName || name,
                showStyle,
                children: [addItem]
            })
        },
        /**
         * 拖拽结束时触发
         *      1. 这里核心面向tab结构下的数据。拖拽的是模块对象数据，其他场景都是挪动一个对象到另一个数组下，是合理的。但是tab结构，模块都是放在单个tab下的children属性下
         *      2. 当tab结构中任意子Tab下最后一个数据被移走时，该空壳tab需要清除掉
         *      3. 当tab结构中添加了子Tab，需要将模块数据构造为tab结构
         */
        endDrag () {
            // 定义需要处理的模块：含主表Tab，关联Tab
            let tabPart = [this.dragName.mainTab, this.dragName.linkTab]
            tabPart.forEach(i => {
                this.layoutData[i].forEach((j, j_index) => {
                    if (!('children' in j)) {
                        // 按道理期望的tab场景的数据结构为
                        //     [
                        //         { tabName: 'A', children: [关联1] },
                        //         { tabName: 'B', children: [关联2, 关联3] },
                        //     ]
                        // 当拖拽一个模块独立成为子Tab时，数据会变成：（如这里的关联1）
                        //     [
                        //         { tabName: 'A', children: [] },
                        //         { ...关联1 },
                        //         { tabName: 'B', children: [关联3， 关联4] },
                        //     ]
                        // 此时就需要将这里的
                        //     { ...关联1 } 处理为 { tabName: '关联1', children: [关联1] }
                        this.layoutData[i].splice(j_index, 1, {
                            tabName: j.name,
                            children: [{
                                ...j,
                                tabName: j.name,
                            }]
                        })
                    } else {
                        // 目前的场景看，如果单个tab下的关联模块只有一个，此时则使用关联模块的展示名称，不要配置所属Tab名称
                        // 所以这里判断tab下只有一个的场景，设置所属Tab名称为模块的展示名称
                        // let _childLen = j.children.length
                        // if (_childLen === 1) {
                        //     let _item = j.children[0]
                        //     // 新生成的模块，使用自己的展示名称作为模块的所属Tab名称
                        //     j.tabName = _item.name
                        //     // 使用自己的展示名称作为其所在tab的名称
                        //     _item.tabName = _item.name
                        // }
                    }
                })
            })
            // 初始数据为：
            //     [
            //         { tabName: 'A', children: [关联1] },
            //         { tabName: 'B', children: [关联2, 关联3] },
            //     ]
            // 此时拖拽关联1独立成为一个子Tab，经过处理数据会变成
            //     [
            //         { tabName: 'A', children: [] },
            //         { tabName: '关联1', children: [关联1] },
            //         { tabName: 'B', children: [关联2, 关联3] },
            //     ]
            // 此时{ tabName: 'A', children: [] }中的children是没数据的，在页面上无效，所以需要删掉这个对象，变成
            //     [
            //         { tabName: '关联1', children: [关联1] },
            //         { tabName: 'B', children: [关联2, 关联3] },
            //     ]
            tabPart.forEach(i => {
                this.layoutData[i].forEach((j, j_index) => {
                    if ('children' in j) {
                        let _childLen = j.children.length
                        if (_childLen === 0) {
                            this.layoutData[i].splice(j_index, 1)
                            return
                        }
                    }
                })
            })
            // 这一步不能与上面的遍历同时处理，必须要等前面的属性补充完整时，再执行这里的删除逻辑，不然会修改到数据的长度导致遍历时出错
        },
        /**
         * 保存排版
         */
        saveLayout () {
            let {
                mainDefault, // 放置主表+任意关联模块 [主表, 关联1, 关联2]
                mainTab, // 含主表的Tab(二维结构)，格式如[[主表，关联1], [关联2], [关联3, 关联4, 关联5]]
                defaultB, // 默认模块集合B  [关联7, 关联8, 关联9]
                linkTab, // 关联Tab(二维结构),格式如[[关联1,关联2], [关联3], [关联4, 关联5, 关联6]]
                defaultC,
            } = this.layoutData
            // 格式化【展示风格与所属Tab名称】，并获取一维结构的关联模块数据
            let _mainDefault =
                    mainDefault.map(i => {
                        i.showStyle = ShowStyle.priority
                        i.tabName = ''
                        return i
                    })
            // 格式化【展示风格与所属Tab名称】，并获取一维结构的关联模块数据
            let _mainTab =
                mainTab
                    .reduce((res, i) => {
                        let { tabName, children } = i
                        children.forEach(i => i.tabName = tabName)
                        res.push(...children)
                        return res
                    }, [])
                    .map(i => {
                        i.showStyle = ShowStyle.mainTab
                        return i
                    })
            // 格式化【展示风格与所属Tab名称】，并获取一维结构的关联模块数据
            let _defaultB =
                    defaultB
                        .map(i => {
                            i.showStyle = ShowStyle.priority
                            i.tabName = ''
                            return i
                        })
            // 格式化【展示风格与所属Tab名称】，并获取一维结构的关联模块数据
            let _linkTab =
                    linkTab
                        .reduce((res, i) => {
                            let { tabName, children } = i
                            children.forEach(i => i.tabName = tabName)
                            res.push(...children)
                            return res
                        }, [])
                        .map(i => {
                            i.showStyle = ShowStyle.linkTab
                            return i
                        })
            // 格式化【展示风格与所属Tab名称】，并获取一维结构的关联模块数据
            let _defaultC =
                    defaultC
                        .map(i => {
                            i.showStyle = ShowStyle.priority
                            i.tabName = ''
                            return i
                        })
            // 获取一维的关联模块数据
            let _result = [
                ..._mainDefault,
                ..._mainTab,
                ..._defaultB,
                ..._linkTab,
                ..._defaultC
            ]
            if (!_result[0][IsMainForm]) {
                this.$message.warning('主表的相对位置必须为第一个模块【从上往下】')
                return
            }

            // 兼容用户错误地使用排版位置时，灵活调整位置后再保存
            // 场景1：主表放在A区域且含主表的Tab区放置了内容，则此时需要处理为
            //     1.1 含主表的Tab区转化为纯关联Tab区，与原纯关联Tab数据进行合并
            //     1.2 C区域转化为E区域数据，与E区域进行合并
            if (_mainDefault.find(i => i[IsMainForm]) && _mainTab.length > 0) {
                // 将平级Tab的关联模块设置为子级Tab模块
                _mainTab.forEach(i => i.showStyle = ShowStyle.linkTab)
                // 根据相对位置，平级Tab的数据插入在子级Tab前
                _linkTab.unshift(..._mainTab)
                // 清空同级Tab
                _mainTab = []
                // defaultB与defaultC中的关联模块的展示风格都是按顺序，所以这里不需要额外修改
                // 根据相对位置，将defaultB的数据插入到defaultC之前
                _defaultC.unshift(..._defaultB)
                // 清空defaultB
                _defaultB = []
                // 更新结果集
                _result = [
                    ..._mainDefault,
                    ..._mainTab,
                    ..._defaultB,
                    ..._linkTab,
                    ..._defaultC
                ]
            }

            // 兼容用户修改了主表所在的Tab名称，主表所在的Tab只能使用主表名称作为名称，这里还原后再保存
            let _mainForm = _result[0] // 获取主表
            let { tabName, name, showStyle } = _mainForm
            if (tabName && // 组别有名，表示在Tab中
                tabName !== name && // 按道理主表所在的Tab要求Tab名称与主表模块名称一致
                showStyle === ShowStyle.mainTab && // 主表放Tab中的话则该Tab肯定是mainTab，不是linkTab
                mainTab[0].children.length > 1 && // 同列Tab下除了主表应该要有其他模块，毕竟这里核心是修改其他模块的tabName
                mainTab[0].children[0][IsMainForm]) { // 再次确定第一列Tab下的第一个模块一定为主表
                mainTab[0].children.forEach(i => {
                    // 更新主表所在Tab的所有模块tabName，修改为主表名称才能显示时成为一个组
                    i.tabName = name
                })
            }

            // 为所有模块进行排序号的处理
            _result.forEach((i, index) => {
                i[PriorityName] = index + 1
            })
            // 到此,关联模块的数据准备完毕，接下来将数据更新到页面上的关联模块中
            this.showList.forEach(i => {
                // 根据唯一标识id找到对应的关联模块
                let _item = _result.find(j => j.id === i.u_id)
                // 获取展示名称：name
                // 获取所属Tab名称：tabName
                // 获取展示风格： showStyle
                // 获取模块排序号：priority
                let { name, tabName, showStyle, priority } = _item
                // 定义根据键名获取字段对象的方法
                let _getField = (name) => i.value.find(j => j.name === name)
                // 更新关联模块中的属性值
                _getField(PriorityName).value = priority
                _getField('displayName').value = name
                _getField('tabName').value = tabName
                _getField('showStyle').value = showStyle
                // 根据【展示风格】值设置【所属Tab名称】的隐藏/显示，风格为按顺序时是不需要显示所属Tab名称的
                this.$set(_getField('tabName'), 'hidden', showStyle === ShowStyle.priority)
                // 除了更新模块内的排序，页面上模块的展示顺序也有自己的字段，也需要更新
                i[PriorityName] = priority
            })
            // 保存完毕，隐藏可视化窗口
            this.showLayoutDialog = false
        },
        /**
         * 判断项是否为最后一个
         * @param {Object} item 项
         * @param {Array} itemList 完整项数组
         */
        isLast (item, itemList) {
            if (itemList.length === 0) return
            // 排序后截取最后一项，判断与当前项是否一致
            return Sort_List(itemList, PriorityName).pop() === item
        },
        // 添加关联关系
        addRelation () {
            let _uuId = Get_UUID()
            let _fieldList = Deep_Clone(this.module.fieldList)
            _fieldList.find(i => i.name === 'linkCode').value = `link_${_uuId}`
            this.showList.push({
                // 获取唯一值（relationList 的id 必须唯一，否则list移开的样式将发生错乱）
                u_id: _uuId,
                value: _fieldList,
                module: Deep_Clone(this.module),
                [PriorityName]: this.showList.length
            })
            this.setPageChange()
            // 页面滚动至底部新增模块处
            this.$nextTick(()=>{
                Scroll_Bottom(this.$refs.group)
            })
        },
        // 排序模块
        sortRelation () {
            // 获取模块数据
            let _data = this.showList.map(i => {
                let _displayName = i.value.find(j => j.name === 'displayName').value
                return {
                    ...i,
                    displayName: _displayName
                }
            })
            // 页面上的数据是使用flex的order属性进行排序的，启用弹窗排序时需要先获取排序后的数据再传入
            _data = Sort_List(_data, PriorityName)
            // 执行排序
            Visulize.Sort.init({
                data: _data,
                priority: PriorityName,
                displayName: 'displayName',
                displayNameLabel: '展示名称',
            })?.then(({isChange, data}) => {
                // 排序没有变化，则直接关闭窗口
                if (!isChange) {
                    Visulize.Sort.close() // 关闭弹窗
                    return
                }
                data.forEach(i => {
                    let _item = this.showList.find(j => j.u_id === i.u_id)
                    // 更新页面展示的排序
                    this.$set(_item, 'priority', i.__priority)
                    // 更新具体字段值的排序
                    _item.value.find(j => j.name === 'priority').value = i.__priority
                })
                Visulize.Sort.close() // 关闭弹窗
            })
        },
        // 删除关联关系
        delectRelation (info, i) {
            this.$confirm('确定删除此关联关系?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.showList.splice(i, 1)
                this.setPageChange()
                this.$message({
                    type: 'success',
                    message: '删除成功!'
                })
            })
        },
        // 设置当前tab为已修改、未保存状态
        setPageChange () {
            if (this.mainScope?.customSetting?.isChangeInfo) {
                this.mainScope.customSetting.isChangeInfo[this.mdCode] = true
            }
        },
        // 验证表单（单个）
        checkForm (formRef) {
            return new Promise((resolve, reject) => {
                formRef.validateForm().then((res) => {
                    resolve(res)
                }).catch(() => {
                    reject()
                })
            })
        },
        // 一次性验证所有表单
        checkFormAll () {
            return new Promise((resolve, reject) => {
                let list = []
                let moduleFormRef = this.$refs?.moduleForm || []
                for (let i = 0; i < moduleFormRef.length; i++) {
                    let item = moduleFormRef[i]
                    let editRef = item?.$refs?.edit
                    list.push(this.checkForm(editRef))
                }
                Promise.all(list).then((res) => {
                    resolve(res)
                }).catch(() => {
                    reject()
                })
            })
        },
        // 请求关联关系
        initLinkInfo () {
            Load_Field_Info({ id: this.id }).then(res => {
                if (res) {
                    let linkList = res.linkList || []
                    this.showList = []
                    linkList.forEach((i, index) => {
                        let _value = Update_FieldList_Value(Deep_Clone(this.module.fieldList), i, { valuePath: '' })
                        this.showList.push({
                            u_id: Get_UUID(),
                            value: _value,
                            module: Deep_Clone(this.module),
                            [PriorityName]: index
                        })
                    })
                } else {
                    this.$message.error('请求关联信息失败')
                }
            }).catch(err=>{
                this.$message.error('请求关联信息失败')
            })
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
        }
    },
    async mounted () {
        // 获取关联关系模型表单
        this.module =  await ModuleUtils.editModuleCfg(this.mdCode)
        // 请求关联关系详细数据
        this.initLinkInfo()
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
        //     2. Relation.vue
        //     3. ChildModuleEdit.vue
        EventBus.$on('setLinkData', (mountVarName) => {
            // 通知子tab所在vue页面
            EventBus.$emit('setData', this, mountVarName)
            // 通知主表
            EventBus.$emit('getChildTabData', this, mountVarName)
        })
        // 离开页面时清除事件总线
        this.$once('hook:beforeDestroy', () => {
            EventBus.$off('setLinkData', {})
        })
    }
}
</script>

<style lang="scss" scoped>
$partBg: #f5f5f5;
$titleHeight: 24px;
.relation::v-deep {
    height: 100%;
    position: relative;
    padding: 10px;
    .form-title {
        .bd-button {
            height: $titleHeight;
            line-height: $titleHeight;
        }

    }
    // 关联字段表单内容
    .relation-content {
        height: calc(100% - #{$titleHeight});
        padding-top: 4px;
        .relation-card-group {
            height: 100%;
            overflow: auto;
            .card-box {
                display: flex;
                flex-direction: column;
                margin: 5px;
                // 卡片列表
                .relation-card-item {
                    display: inline-block;
                    width: 100%;
                    border-radius: $borderRadius;
                    background-color: $partBg;
                    transition: all .3s ease-in-out;
                    border: 1px solid $lineColor;
                    position: relative;
                    overflow: hidden;
                    margin-bottom: 15px;
                    .relation-form-box {
                        background: $partBg;
                        .bd-form-part {
                            background: $partBg;
                            .bd-form {
                                padding: 24px $padding 0 $padding;
                                background: $partBg;
                                .bd-form__group {
                                    background: $partBg;
                                }
                            }
                        }
                        .content-form__main {
                            display: inline-block;
                            width: 100%;
                        }
                    }
                    &.is-last {
                        margin-bottom: 0;
                    }
                }
            }
        }
    }
}
.list-item {
    display: inline-block;
    margin-right: 10px;
}
.list-enter-active, .list-leave-active {
    transition: all 1s;
}
.list-enter, .list-leave-to
    /* .list-leave-active for below version 2.1.8 */ {
    opacity: 0;
    transform: translateY(30px);
}

$pad: 2 * $padding;
.s-layout::v-deep {
    background: #fff;
    padding: $padding;
    display: flex;
    .s-layout__content {
        width: 100%;
        display: flex;
        flex-direction: column;
        .s-group {
            background: #efefef;
            min-height: 50px;
            display: inline-flex;
            flex-direction: column;
            margin-bottom: $pad - 5px;
            position: relative;
            padding: 0 $pad;
            &:last-of-type {
                margin-bottom: 0;
            }
            .s-item {
                cursor: move;
                position: relative;
                min-width: 100px;
                width: fit-content;
                padding: 2px;
                border-radius: $borderRadius;
                background-color: #02a267;
                color: #fff;
                user-select: none;
                margin-bottom: $pad - 6px;
                &:last-of-type {
                    margin-bottom: 0;
                }
                .textTransferInput {
                    padding: 2px 4px;
                    .s-input {
                        .el-input__inner {
                            padding: 2px 4px;
                            height: 20px;
                            width: 100px;
                        }
                    }
                    .s-text {
                        padding: 2px 0;
                        $_signDic: 2px;
                        $_signHeight: 18px;
                        .warningSign {
                            margin: -$_signDic 2px;
                            width: $_signHeight;
                            height: $_signHeight;
                            line-height: $_signHeight - $_signDic;
                            padding: 1px;
                        }
                    }
                }
            }
            .drag-tip {
                font-size: $fontS;
                color: $fontCS;
                position: absolute;
                z-index: 0;
            }
        }
        .s-group-tab {
            display: flex;
            flex-direction: row;
            & > .s-item {
                flex: 0 0 auto;
                margin-bottom: 0;
                background: unset;
                padding: 10px;
                &:first-of-type {
                    padding-left: $pad;
                }
                .s-item-tabName {
                    color: $success;
                    padding-bottom: $padding;
                }
            }
            &.s-group {
                overflow-x: auto;
            }
            &.s-group:not(.needPadding),
            .s-group {
                padding: 0;
            }
        }
        .sortable-chosen {
            background: $warning !important;
            opacity: .8;
        }
    }
}
</style>
