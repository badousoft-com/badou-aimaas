// 流程定义编辑页面
<template>
    <div class="page-flow">
        <tree-page-show
            :isShow="true"
            :title="pageTitle">
            <template v-slot:left>
                <bd-tree
                    ref="bdTree"
                    :data="treeData"
                    :node-key="nodeKey"
                    :treeIconFun="treeIconFun"
                    :props="{
                        label: treeProp.labelKey,  // 使用数据下指定属性名作为节点展示名称
                        rootIdName: treeProp.rootIdName,  // 数据下指定属性名作为父节点
                        treeDataPath: '',  // 指定请求接口后节点数组数据所在路径
                        isLeaf: isLeaf  // 判断是否为叶子节点（末节点）
                    }"
                    default-expand-all
                    :request-config="treeRequestConfig"
                    :currentNodeKey.sync="currentNodeKey"
                    @setCurrentNode="setCurrentNode">
                </bd-tree>
            </template>
            <template v-slot:right>
                <div class="page-flow__btn padding defaultBg b-b" v-if="btnList && btnList.length > 0">
                    <bd-button
                        class=""
                        v-for="(i, index) in btnList"
                        :key="index"
                        v-bind="i"
                        @click="handleClick(i)">
                    </bd-button>
                </div>
                <transition name="fade">
                    <module-edit-code
                        v-if="isShowForm"
                        ref="form"
                        class="page-flow__form"
                        :mdCode="mdCode"
                        :detailId="detailId"
                        :defaultParamsObj="defaultParamsObj"
                        hideBtn
                        noTitle
                        @afterSave="afterSaveForm">
                    </module-edit-code>
                </transition>
            </template>
            <!-- 判断长度，至少子元素数组要有两条数据排序才有意义 -->
            <template
                v-if="currentNodeSortList && currentNodeSortList.length > 1"
                v-slot:foldBtnFront>
                <span title="调整选中节点下的子节点排序">
                    <bd-icon
                        class="mar-r"
                        name="configure"
                        @click="showChangeSortDialog">
                    </bd-icon>
                </span>
            </template>
        </tree-page-show>
        <!-- 排序弹窗 -->
        <!-- <bd-dialog
            title>
        </bd-dialog> -->
        <el-dialog
            v-drag-dialog="{ hasDrag }"
            custom-class="bd-dialog noUsePaddding"
            v-setDialogSize = "{
                width: changeSortDialogInfo.width,
                height: changeSortDialogInfo.height,
                visibleStatus: sortDialogVisible,
                disabled: hasDrag}"
            :destroy-on-close="true"
            :close-on-click-modal="false"
            :visible.sync="sortDialogVisible">
            <!-- 弹窗-标题模块 -->
            <div slot="title" class="bd-dialog-title">
                <bd-icon name="pillar-fill" class="pillar fill"></bd-icon>
                <span>{{changeSortDialogInfo.title}}</span>
            </div>
            <!-- 弹窗-正文内容模块 -->
            <div class="bd-dialog__body font0">
                <!-- 左侧参考模块 -->
                <div class="bd-dialog__body--left bd-dialog__body--part">
                    <div class="bd-dialog__body--part--main" :title="changeSortDialogInfo.left.tip">
                        <div class="bd-dialog__body--part--title">{{changeSortDialogInfo.left.title}}</div>
                        <div
                            v-for="(i, index) in currentNodeSortList"
                            :key="index"
                            class="bd-dialog__body--part--item">
                            {{i.name}}
                        </div>
                    </div>
                </div>
                <!-- 右侧拖拽模块 -->
                <div class="bd-dialog__body--right bd-dialog__body--part">
                    <div class="bd-dialog__body--part--main" :title="changeSortDialogInfo.right.tip">
                        <div class="bd-dialog__body--part--title">
                            <bd-icon name="exchange"></bd-icon>
                            {{changeSortDialogInfo.right.title}}
                        </div>
                        <draggable
                            v-model="copy_currentNodeSortList"
                            v-bind="dragOptions"
                            @update="updateSort">
                            <transition-group>
                                <!-- 注意：在使用draggable时，发现key不能结合index使用，排序完会出现页面闪动的效果 -->
                                <div
                                    v-for="i in copy_currentNodeSortList"
                                    :key="i.name + i.uuid"
                                    class="bd-dialog__body--part--item"
                                    :class="{'is-change': i.isChange}">
                                    {{i.name}}
                                </div>
                            </transition-group>
                        </draggable>
                    </div>
                </div>
            </div>
            <!-- 按钮区域 -->
            <div
                v-if="sortBtnList && sortBtnList.length !== 0"
                slot="footer">
                <!-- isLoad：状态表示该按钮项存在loading状态，点击时会启用 -->
                <bd-button
                    v-for="(i, index) in sortBtnList"
                    :key="index"
                    v-bind="i"
                    @click='handleClick(i)'>
                </bd-button>
            </div>
        </el-dialog>
    </div>
</template>
<script>
import { Tree_Node_List_URL } from '@/api/frame/common'
import BdTree from '@/components/frame/Common/Tree'
import TreePageShow from '@/components/frame/TreePageShow'
import { Get_Common_List, Save_URL } from '@/api/frame/common'
import ModuleEditCode from '@/components/frame/Module/ModuleEditCode'
import { Get_Module_EditJSON, Common_Delete } from '@/api/frame/common'
import draggable from 'vuedraggable'
import setDialogSize from '@/directive/set-dialog-size'
import DragDialogMixin from '@/components/frame/Dialog/DragMixin'
import MTitle from '@/components/frame/Common/MTitle'
import { Save_Node_Priority, Save_Route_Priority, Save_Node_Url } from '@/api/frame/flow'
import { Deep_Clone } from '@/utils/clone'
import { Get_UUID } from '@/utils'
import { Find_Data_In_Recursion } from '@/utils/list'
// 定义结束环节编码
const endNodeCode = 'end'
// 定义按钮权限列表
const btnPermissionList = [
    // 新增流程（暂无流程）时
    { level: 0, name: 'null', belong: 'save, back', mdCode: 'flwFlow', label: '流程名称' },
    // 选中流程时
    { level: 1, name: 'flow', belong: 'save, addNode, back', mdCode: 'flwFlow', label: '流程名称' },
    // 选中环节时（非结束环节）
    { level: 2, name: 'node', belong: 'save, addRoute, delete, back', mdCode: 'FlwNode', label: '流程环节' },
    // 选中结束环节时
    { level: 2, name: 'endNode', belong: 'save, back', mdCode: 'FlwNode', label: '流程环节' },
    // 选中路由时
    { level: 3, name: 'route', belong: 'save, delete, back', mdCode: 'FlwRoute', label: '流程路由' }
]
export default {
    // 禁止父级传入属性作为当前组件的根标签属性
    inheritAttrs: false,
    components: {
        [BdTree.name]: BdTree,
        [TreePageShow.name]: TreePageShow,
        [ModuleEditCode.name]: ModuleEditCode,
        [MTitle.name]: MTitle,
        draggable
    },
    data () { // 定义页面变量
        return {
            // 弹窗是否自行拖动过
            hasDrag: false,
            // 树型组件属性项
            treeProp: {
                labelKey: 'name',
                rootIdName: 'pid'
            },
            // 调整顺序的弹窗对象
            changeSortDialogInfo: {
                title: '调整顺序',
                left: {
                    title: '初始顺序(参考)',
                    tip: '请前往右侧内进行拖拽排序'
                },
                right: {
                    title: '拖拽调整新顺序',
                    tip: '请拖拽自行排序'
                },
                width: '800px',
                height: '70%'
            },
            // 排序弹窗是否可见
            sortDialogVisible: false,
            // 是否展示右侧表单
            isShowForm: true,
            pageTitle: '流程定义',
            // 流程树型数据
            treeData: [],
            // 流程名称
            flowName: null,
            // 流程id
            flowId: null,
            // 流程类别
            flowType: null,
            // 详情数据id
            detailId: '',
            // 模型编码
            mdCode: null,
            defaultParamsObj: null,
            // 当前选中节点
            currentNode: null,
            // 选中节点的数据对象
            currentNodeData: null,
            // 选中节点指定键名
            currentNodeKey: null,
            // 节点所处在模块：分为name-流程名称/node-环节/route-路由
            // 将根据该变量进行筛选对应按钮
            currentBelong: null,
            // 流程名称： 保存、新增环节、关闭
            // 流程环节：保存、新增路由、删除、关闭
            // 流程路由：保存、删除、关闭
            allBtnList: [
                { id: 'back', name: '返回', icon: 'back', type: 'danger', click: 'close' },
                { id: 'save', name: '保存', icon: 'save', type: 'primary', click: 'save', loading: false},
                { id: 'addNode', name: '新增环节', icon: 'add', type: 'primary', click: 'addNode' },
                { id: 'addRoute', name: '新增路由', icon: 'add', type: 'primary', click: 'addRoute' },
                { id: 'delete', name: '删除', icon: 'delete', type: 'danger', click: 'delete' }
            ],
            // 排序弹窗中的按钮事件
            sortBtnList: [
                { id: 'cancel', name: '取消', icon: 'cancel', type: 'danger', click: 'closeSortDialog', loading: false},
                { id: 'save', name: '保存新的排序', icon: 'save', type: 'primary', click: 'saveSort', loading: false}
            ],
            // 树节点键名
            nodeKey: 'id',
            // 当前选中节点下可排序的数组数据-拖拽数据，用于提交
            copy_currentNodeSortList: [],
        }
    },
    // 指令模块
    directives: {
        setDialogSize
    },
    mixins: [DragDialogMixin],
    computed: {
        // 拖拽属性
        dragOptions () {
            return {
                animation: 200,
                group: 'description',
                disabled: false,
                ghostClass: 'ghost'
            }
        },
        // 根据选中节点获取当前页面需要展示的按钮列表
        btnList () {
            if (!this.currentBelong) return
            let _belong = btnPermissionList.find(i => i.name === this.currentBelong).belong
            return this.allBtnList.filter(i => ~_belong.indexOf(i.id))
        },
        // 获取下一个即将新增环节是否初始、末尾状态
        getNodeFirstFlg () {
            // 获取环节数
            let len = this.currentNodeData?.children?.length
            // 流程下必然有结束环节，当前若环节只有结束环节，则返回1，表示新增的环节作为首发环节
            if (len === 1) return '1'
            return '0'
        },
        _treeData () {
            let _treeData = Deep_Clone(this.treeData)
            return _treeData.map(i => {
                i.children = i.children && i.children.map(j => j.name)
                return {
                    name: i.name,
                    children: i.children
                }
            })
        },
        // 获取当前选中节点下可排序的数组数据
        currentNodeSortList () {
            // 获取选中节点下子节点集合
            let _list = this.currentNodeData?.children
            if (!(_list && _list.length > 0)) return []
            // 过滤掉流程环节-结束环节（其不参与排序，固定最后）
            return _list.filter(i => i.flgRoot !== '1')
        }
    },
    methods: { // 定义函数
        /**
         * 保存右侧表单后的回调事件，这里主要是当表单修改到名称时及时更新回左侧树进行展示
         * @param {Object} obj 提交的表单数据
          */
        afterSaveForm (obj) {
            // 匹配找到对应项，更改其名称
            this.updateTreeData(this.treeData, obj.id, obj.name)
        },
        /**
         * 更新树数据
         * @param {Array} data 数组数据
         * @param {String} val 匹配的数据
         * @param {String} name 更新的数据
          */
        updateTreeData (data, val, name) {
            try {
                data.forEach(i => {
                    if (i.id === val) {
                        // 匹配到项后，更新数据
                        i.name = name
                        // forEach中使用catch实现循环的终止逻辑
                        throw 'end'
                    }
                    // 递归调用
                    this.updateTreeData(i.children, val, name)
                })
            } catch (_) {
                // do something
            }
        },
        // 关闭排序弹窗
        closeSortDialog () {
            this.sortDialogVisible = false
        },
        // 保存排序
        saveSort () {
            // 获取当前选中节点id
            let id = this.currentNodeData?.id
            // 定义排序列表
            let sortList = []
            // 定义请求地址
            let ajaxRequest = null
            // 定义请求参数
            let params = null
            switch (this.mdCode) {
                // 处理流程下的环节排序
                case 'flwFlow':
                    // 更新保存环节排序接口
                    ajaxRequest = Save_Node_Priority
                    // 更新最新排序列表
                    sortList = this.copy_currentNodeSortList.map((i, index) => ({
                        id: i.id,
                        priority: index,
                        // 是否为第一个环节
                        flgHead: index === 0 ? '1' : '0',
                        flgRoot: i.flgRoot
                    }))
                    // 更新参数
                    params = {
                        flowId: id,
                        nodeArr: JSON.stringify(sortList)
                    }
                    break
                // 处理环节下的路由排序
                case 'FlwNode':
                    // 更新保存路由排序接口
                    ajaxRequest = Save_Route_Priority
                    // 更新最新排序列表
                    sortList = this.copy_currentNodeSortList.map((i, index) => ({
                        id: i.id,
                        priority: index,
                    }))
                    // 更新参数
                    params = {
                        nodeId: id,
                        routeJson: JSON.stringify(sortList)
                    }
                    break
                default:
                    // do something you like
            }
            // 临时存储选中节点id，下面逻辑使用
            let currentNodeKey = this.currentNodeKey
            // 提交接口，更新排序
            ajaxRequest(params).then(async (res) => {
                if (res.hasOk) {
                    // 关闭排序弹窗
                    this.closeSortDialog()
                    this.$message.success('排序已更新')
                    // 这里需要先清除节点的选中
                    // 流程树数据更新之后需要通过currentNodeKey属性设置选中节点，若不先清除会导致currentNodeKey绑定失效，封转后的树组件是通过watch监听currentNodeKey的值进行改变的
                    this.currentNodeKey = null
                    // 更新流程树
                    this.treeData = await this.getFlowTree({
                        flowName: this.flowName,
                        flowId: this.flowId
                    })
                    // 树刚刚生成，直接设置选中节点会不生效，添加nextTick确保树生成再选中节点
                    this.$nextTick(() => {
                        // 树数据重新生成后，选中状态会被取消，这里重新使用之前的数据选中下
                        this.currentNodeKey = currentNodeKey
                        // 根据新生成的树，以及选中节点数据，递归获取选中节点对象数据，更新排序后的视图数据
                        this.currentNodeData = Find_Data_In_Recursion(this.treeData, this.nodeKey, currentNodeKey)
                    })
                }
            })
        },
        // 更新拖拽排序数组下的属性状态，用于显示已拖拽变更的数据
        updateSort (event) {
            this.copy_currentNodeSortList.forEach((i, index) => {
                i.isChange = i[this.nodeKey] !== this.currentNodeSortList[index][this.nodeKey]
            })
        },
        /**
         * 添加流程环节事件
         * @param {Object} btnObj 按钮对象
         */
        addNode (btnObj) {
            // 弹窗展示流程环节模型表单
            this.showDialog(btnObj, {
                signId: 'flowNodeFormId',
                title: '新增流程环节',
                mdCode: 'FlwNode',
            })
        },
        /**
         * 添加流程路由事件
         * @param {Object} btnObj 按钮对象
         */
        addRoute (btnObj) {
            // 弹窗展示流程路由模型表单
            this.showDialog(btnObj, {
                signId: 'flowRouteFormId',
                title: '新增流程路由',
                mdCode: 'FlwRoute'
            })
        },
        /**
         * 获取即将新增的数据应配置的排序号
         * @param {String} mdCode 模型编码
         * @param {Array} treeData 左侧树数据
         */
        getPriority (mdCode, treeData) {
            // 设置默认排序号
            let priority = '0'
            switch (mdCode) {
                case 'FlwNode':
                    let nodeList = treeData[0]?.children
                    let nodeLen = nodeList?.length
                    // 结束环节的排序号在新增时已经设置为最大，默认垫底
                    // 新添加的环节将获取倒数第二个的环节排序 + 1作为其排序号
                    if (nodeLen >= 2) {
                        priority = parseInt(nodeList[nodeLen - 2].priority) + 1
                    }
                    break
                case 'FlwRoute':
                    let nodeList2 = treeData[0].children.find(i => i[this.nodeKey] === this.currentNodeKey)
                    let routeList = nodeList2?.children
                    if (routeList && routeList.length > 0) {
                        // 找到最后一个路由，获取其排序号 + 1后返回作为新增数据的排序号
                        priority = parseInt(routeList[routeList.length - 1].priority) + 1
                    }
                    break
                default:
                    // do something default
            }
            return priority
        },
        /**
         * 弹窗展示模型表单
         * @param {Object} btnObj 按钮对象
         * @param {Object} 配置信息对象
         */
        showDialog (btnObj, { signId, title, mdCode}) {
            let currentPageScope = this
            this.$dialog.init({
                // 弹窗内容类型
                type: 'standerEditCode',
                // 弹窗唯一标识，注意唯一标识与上面出现的函数getModelListObj的参数值保持一致
                id: signId,
                // 弹窗标题
                title,
                // 模型编码c
                mdCode,
                // 根据内容自适应高度
                // isAutoFix: true,
                width: '800px',
                currentPageScope: this,
                // 弹窗中按钮组
                handlerList: [
                    {
                        name: '取消',
                        type: 'danger',
                        icon: 'cancel',
                        click: function () {
                            // 关闭弹窗表单
                            this.$dialog.close()
                        }
                    }, {
                        name: '保存',
                        icon: 'save',
                        type: 'primary',
                        loading: false,
                        click: function (btnObj) {
                            // getDialogConObj:全局封装，4为指定模型表单，返回该页面作用域
                            let formObj = this.getDialogConObj(signId, 4)
                            formObj.validateForm().then(formObj => {
                                btnObj.loading = true // 重置按钮加载状态
                                // 获取当前数据应该配置的排序号
                                let priority = currentPageScope.getPriority(mdCode, currentPageScope.treeData)
                                let url = Save_URL(mdCode)
                                switch (mdCode) {
                                    case 'FlwNode':
                                        // 配置流程id
                                        formObj['flowId'] = currentPageScope.currentNodeKey
                                        // 配置流程类别
                                        formObj['busiType'] = currentPageScope.flowType
                                        // 配置排序号
                                        formObj['priority'] = priority
                                        // 配置是否第一环节
                                        formObj['flgHead'] = currentPageScope.getNodeFirstFlg
                                        this.post(url, formObj).then(async res => {
                                            if (res?.hasOk) {
                                                this.$message.success('保存成功')
                                                /* 更新流程树目前两个方案 */
                                                /* 方案1：重新请求数据，但是出现bug无法选中活跃节点，暂时放弃 */
                                                // currentPageScope.treeData = await currentPageScope.getFlowTree({
                                                //     flowName: currentPageScope.flowName,
                                                //     flowId: currentPageScope.flowId
                                                // })
                                                // 树刚刚生成，直接设置选中节点会不生效，添加nextTick确保树生成再选中节点
                                                // this.$nextTick(() => {
                                                //     this.currentNodeKey = currentPageScope.currentNodeKey
                                                // })
                                                /* 方案2：保存成功后，直接取接口返回的环节数据添加到树数据中 */
                                                // 获取新增的环节数据
                                                let newFlwNode = res?.bean?.returnDetails?.data
                                                if (newFlwNode) {
                                                    // 获取流程对象
                                                    let flwNodeList = currentPageScope.treeData?.[0]?.children
                                                    // 由于结束环节是跟随流程同时生成的，新环节添加时需要注意要加在结束环节前面
                                                    flwNodeList.splice(flwNodeList.length - 1, 0, newFlwNode)
                                                }
                                                // 关闭弹窗表单
                                                this.$dialog.close()
                                            } else {
                                                this.$message.error(`保存失败！${res?.message}`)
                                                // 关闭弹窗表单
                                                this.$dialog.close()
                                            }
                                        }).finally(() => {
                                            // 设置按钮状态
                                            btnObj.loading = false
                                        })
                                        break
                                    case 'FlwRoute':
                                        // 配置排序号
                                        formObj['priority'] = priority
                                        this.post(url, formObj).then(async res => {
                                            if (res?.hasOk) {
                                                this.$message.success('保存成功')
                                                /* 更新流程树目前两个方案 */
                                                /* 方案1：重新请求数据，但是出现bug无法选中活跃节点，暂时放弃 */
                                                // currentPageScope.treeData = await currentPageScope.getFlowTree({
                                                //     flowName: currentPageScope.flowName,
                                                //     flowId: currentPageScope.flowId
                                                // })
                                                // 树刚刚生成，直接设置选中节点会不生效，添加nextTick确保树生成再选中节点
                                                // this.$nextTick(() => {
                                                //     this.currentNodeKey = currentPageScope.currentNodeKey
                                                // })
                                                /* 方案2：保存成功后，直接取接口返回的路由数据添加到对应环节下数据中 */
                                                // 获取新增的流程路由数据
                                                let newFlwRoute = res?.bean?.returnDetails?.data
                                                if (newFlwRoute) {
                                                    // 获取所有环节数组
                                                    let flwNodeList = currentPageScope.treeData[0].children
                                                    let flwNode = flwNodeList.find(i => i.id === formObj.nodeId)
                                                    if (flwNode.children) {
                                                        // 若已有流程路由数组，则直接添加
                                                        flwNode.children.push(newFlwRoute)
                                                    } else {
                                                        // 还没流程路由，则动态添加children，并加上该路由数据
                                                        this.$set(flwNode, 'children', [newFlwRoute])
                                                    }
                                                }
                                                // 关闭弹窗表单
                                                this.$dialog.close()
                                            } else {
                                                this.$message.error(`保存失败！${res?.message}`)
                                                // 关闭弹窗表单
                                                this.$dialog.close()
                                            }
                                        }).finally(() => {
                                            // 设置按钮状态
                                            btnObj.loading = false
                                        })
                                        break
                                    default:
                                        // do something you like
                                }
                                btnObj.loading = false // 重置按钮加载状态
                            })
                        }
                    }
                ],
            })
        },
        // 返回事件
        close () {
            this.$router.go(-1)
        },
        // 设置结束环节选中
        setEndNodeChoose () {
            // 获取结束环节数据，并设置给当前选中节点数据
            this.currentNodeData = this.treeData[0].children.find(i => i.flgRoot === '1')
            if (!this.currentNodeData) return
            let endNodeId = this.currentNodeData.id
            this.currentNodeKey = endNodeId
            this.currentBelong = 'endNode'
            // 更新右侧表单数据
            this.isShowForm = false
            // 更新右侧表单详情id
            this.detailId = endNodeId
            // 更新mdCode
            this.mdCode = 'FlwNode'
            this.$nextTick(() => {
                this.isShowForm = true
            })
        },
        /**
         * 设置节点选中事件
         * @param {Object} data 当前选中节点的数据
         * @param {Object} node 当前选中节点对象
         * @param {Object} nodeComponent 节点实例
         */
        setCurrentNode (data, node, nodeComponent) {
            // 如果点击的是结束环节，就直接kill当前逻辑
            if (data?.code === endNodeCode) {
                this.$message.warning('结束环节不支持编辑')
                return
            }
            // currentNode的使用仅在节点删除时使用
            this.currentNode = node
            // 当前选中节点数据对象
            this.currentNodeData = data
            // 更新操作按钮
            if (!this.detailId) {
                // 使用未生成流程时状态的按钮列表
                this.currentBelong = 'null'
            } else {
                let nodeLevel = node.level
                // 流程环节
                if (nodeLevel === 2) {
                    // 特殊处理流程环节下的结束环节
                    if (data.flgRoot === '1') {
                        this.currentBelong = 'endNode'
                    } else {
                        this.currentBelong = 'node'
                    }
                } else {
                    this.currentBelong = btnPermissionList.find(i => i.level === nodeLevel).name
                }
            }
            // 更新右侧表单数据
            this.isShowForm = false
            // 更新右侧表单详情id
            this.detailId = data?.id
            // 更新mdCode
            this.mdCode = btnPermissionList.find(i => i.level === node.level).mdCode
            this.$nextTick(() => {
                this.isShowForm = true
            })
        },
        // 执行事件
        handleClick (btnObj) {
            this[btnObj.click].call(this, btnObj)
        },
        // 保存事件
        save (btnObj) {
            let formRef = this.$refs.form.getFormRef()
            // 表单校验
            formRef.validateForm().then((formObj) => {
                // 设置按钮状态-加载中
                btnObj.loading = true
                let url = Save_URL(this.mdCode)
                switch (this.mdCode) {
                    case 'flwFlow':
                        this.post(url, formObj).then((saveFlowRes) => {
                            if (saveFlowRes?.hasOk) {
                                // 若保存成功并且当且是新增页面，则使用数据id将路由替换为编辑模式
                                if (saveFlowRes.bean?.id && this.$route.params?.id === 'add') {
                                    let flowId = saveFlowRes.bean.id
                                    let endNodeForm = {
                                        // 编码
                                        code: endNodeCode,
                                        // 环节名称
                                        name: '结束',
                                        // 非第一个环节
                                        flgHead: '0',
                                        // 是否为最后环节
                                        flgRoot: '1',
                                        // 说明
                                        memo: '结束环节',
                                        // 流程id
                                        flowId,
                                        // 环节排序
                                        priority: 999999
                                    }
                                    this.post(Save_URL('FlwNode'), endNodeForm).then((saveNodeRes) => {
                                        if (saveNodeRes?.hasOk) {
                                            this.$message.success('保存成功')
                                            let newPath = this.$route.path.replace('add', flowId)
                                            // 替换当前路由
                                            this.$router.replace(newPath)
                                        }
                                    })
                                } else {
                                    formRef.$emit('afterSave', formObj)
                                }
                            } else {
                                this.$message.error(`保存失败！${saveFlowRes?.message}`)
                            }
                        }).finally(() => {
                            // 设置按钮状态
                            btnObj.loading = false
                        }).catch(err => {
                            console.error(err)
                        })
                        break
                    case 'FlwNode':
                        // 环节保存接口不使用框架默认接口
                        url = Save_Node_Url(this.mdCode)
                        this.post(url, formObj).then(async res => {
                            if (res?.hasOk) {
                                this.$message.success('保存成功')
                                let newFlwNode = res?.bean?.returnDetails?.data
                                if (newFlwNode) {
                                    let currentObj = this.treeData[0].children.find(i => i[this.nodeKey] === this.currentNodeKey)
                                    currentObj.name = newFlwNode.name
                                }
                                formRef.$emit('afterSave', formObj)
                            } else {
                                this.$message.error(`保存失败！${res?.message}`)
                            }
                        }).finally(() => {
                            // 设置按钮状态
                            btnObj.loading = false
                        })
                        break
                    case 'FlwRoute':
                        this.post(url, formObj).then(async (res) => {
                            if (res?.hasOk) {
                                this.$message.success('保存成功')
                                formRef.$emit('afterSave', formObj)
                            } else {
                                this.$message.error(`保存失败！${res?.message}`)
                            }
                            // 设置按钮状态
                            btnObj.loading = false
                        })
                        break
                    default:
                        // do somethinfg default
                }
            })
        },
        // 删除事件（接口层面）
        delete (btnObj) {
            // 获取当前选中模块对应类型文本
            let typeWord = btnPermissionList.find(i => i.name === this.currentBelong)?.label
            if (!typeWord) return
            // 获取选中节点数据的id与name
            let { id, name } = this.currentNodeData
            // 提示框
            this.$confirm(`确定删除【${typeWord}-${name}】吗？`, '删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                // 定义参数
                let params = {
                    ids: id,
                    mdCode: this.mdCode,
                }
                // 执行删除逻辑
                Common_Delete(params).then(res => {
                    if (res.hasOk) {
                        // 获取当前选中节点的父级节点id
                        let parentId = this.currentNode?.parent?.data?.[this.nodeKey]
                        // 根据父级节点id与当前选中节点id,数据层面删除该节点，使页面更新
                        this.deleteCurrentNode(parentId, id)
                        // 删除该选中节点后，设置选中流程节点，总得有个人被命运选中
                        this.setEndNodeChoose()
                        this.$message.success('删除成功')
                    } else {
                        this.$message.error(`删除失败: ${res.message || ''}`)
                    }
                })
            })
        },
        // 删除节点事件（页面呈现层面）
        deleteCurrentNode (parentId, deleteChildId) {
            let nodeList = this.treeData[0].children
            let index = null
            switch (this.mdCode) {
                case 'FlwNode':
                    index = nodeList.findIndex(i => i[this.nodeKey] === deleteChildId)
                    nodeList.splice(index, 1)
                    break
                case 'FlwRoute':
                    let routeList = nodeList.find(i => i[this.nodeKey] === parentId).children
                    index = routeList.findIndex(i => i[this.nodeKey] === deleteChildId)
                    routeList.splice(index, 1)
                    break
                default:
                    // do something default
            }
        },
        // 获取树图标函数
        treeIconFun (node, expanded, isLeaf) {
            let level = node.level
            // 流程图标处理
            if (level === 1)  return 'processName'
            // 流程环节图标处理
            if (level === 2) {
                // 结束环节
                if (node?.data?.flgRoot === '1') return 'carryOut'
                // 普通环节的展开模式
                if (expanded) return 'folderOpen-fill'
                // 普通环节的折叠模式
                return 'folder-fill'
            }
            // 路由图标
            return 'processRoute'
        },
        /**
         * 指定节点是否为叶子节点，仅在指定了 lazy 属性的情况下生效
         * @param {Object} data 节点数据
         */
        isLeaf (data) {
            return !(data.isParent || data.hasChild)
        },
        /**
         * 获取树型请求配置
         * @param {String} nodeParentId 当前节点id值，用它去获取孩子节点
         * @param {Object} data 节点上对象数据
         * @param {Object} node 树节点对象
         */
        treeRequestConfig (nodeParentId, data, node) {
            // 定义请求配置对象
            let requestConfig = null
            // 默认请求配置对象处理
            // 定义参数
            let params = { mdCode: this.mdCode }
            // 节点存在时参数添加节点属性
            nodeParentId && (params.nodeId = nodeParentId)
            // 更新请求配置对象
            requestConfig = {
                url: Tree_Node_List_URL,  // 树型列表默认请求接口
                params
            }
            return requestConfig
        },
        /**
         * 获取流程环节-树节点
         * @param {String} flowId 流程id
         */
        getFlowNode (flowId) {
            return new Promise((resolve, reject) => {
                let defaultSearchParam = [{name: 'flowId', value: flowId, type: 'exact-match'}]
                let params = {
                    mdCode: 'FlwNode',
                    defaultSearchParam: JSON.stringify(defaultSearchParam)
                }
                Get_Common_List(params, 'post').then(res => {
                    // 获取接口的数据字典项列表数据
                    resolve(res?.Rows || [])
                })
            })
        },
        /**
         * 获取指定流程环节下的流程路由
         * @param {String} nodeId 流程环节id
         */
        getFlowRoute (nodeId) {
            return new Promise((resolve, reject) => {
                let defaultSearchParam = [{name: 'nodeId', value: nodeId, type: 'exact-match'}]
                let params = {
                    mdCode: 'FlwRoute',
                    defaultSearchParam: JSON.stringify(defaultSearchParam)
                }
                Get_Common_List(params, 'post').then(res => {
                    // 获取接口的数据字典项列表数据
                    resolve(res?.Rows || [])
                })
            })
        },
        /**
         * @param {String} flowName 流程名称
         * @param {String} flowId 流程id
         */
        async getFlowTree ({flowName, flowId}) {
            return new Promise(async (resolve, reject) => {
                // 定义初始树节点，只含流程名称
                let result = { name: flowName, id: flowId }
                // 获取流程环节树
                let nodeList = await this.getFlowNode(flowId)
                // 判断流程环节是否存在，若存在
                if (nodeList.length > 0) {
                    // 存在子节点，创建children属性
                    result.children = nodeList
                    let promiseList = []
                    // 遍历流程环节，分别获取环节下的流程路由
                    result.children.forEach(i => {
                        promiseList.push(this.getFlowRoute(i.id))
                    })
                    Promise.all(promiseList).then(res => {
                        // 更新流程环节
                        result.children.forEach((i, i_index) => {
                            let item = res?.[i_index] || []
                            if (item.length > 0) {
                                i.children = item
                            }
                        })
                        resolve([result])
                    })
                } else {
                    resolve([result])
                }
            })
        },
        // 获取流程相关信息，组装流程树
        async getFlowInfo () {
            // 没有detailId表示当前为新增，直接模拟简单树返回
            if (!this.detailId) {
                this.treeData = [{ name: '暂无流程' }]
                // 操作显示按钮
                this.currentBelong = 'null'
                return
            } else {
                // 操作显示按钮
                this.currentBelong = 'flow'
                Get_Module_EditJSON({
                    mdCode: this.mdCode,
                    isView: false,
                    id: this.detailId
                }).then(async (res) => {
                    // 以下为编辑时，存在树
                    // 获取流程名称
                    this.flowName = res?.['name']?.value
                    // 获取流程id
                    this.flowId = res?.['id']?.value
                    // 获取流程类别
                    this.flowType = res?.['busiType']?.value
                    if (this.flowName) {
                        // 更新流程树
                        this.treeData = await this.getFlowTree({
                            flowName: this.flowName,
                            flowId: this.flowId
                        })
                        // 树刚刚生成，直接设置选中节点会不生效，添加nextTick确保树生成再选中节点
                        this.$nextTick(() => {
                            this.currentNodeKey = this.flowId
                            // 当前选中节点数据对象
                            this.currentNodeData = this.treeData[0]
                        })
                    }
                }).catch(err => {})
            }
        },
        showChangeSortDialog () {
            // 打开排序弹窗
            this.sortDialogVisible = true
            // 根据排序数组，复制出另一组可供拖拽变更的数组用于操作与提交接口
            this.copy_currentNodeSortList = Deep_Clone(this.currentNodeSortList)?.map(i => ({...i, isChange: false, uuid: Get_UUID()}))
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        // 获取mdCode与详情数据id
        let { mdCode, id } = this.$route.params
        // 更新详情数据id，id为add时表示当前是新增页面
        this.detailId = (id !== 'add') ? id : ''
        // 更新mdCode
        this.mdCode = mdCode
        // 获取流程相关信息，组装流程树
        this.getFlowInfo()
    }
}
</script>
<style lang='scss' scoped>
.page-flow::v-deep {
    .page-flow__btn {
        position: relative;
        z-index: 2;
    }
    .page-flow__form {
        height: calc(100% - #{2 * $padding} - #{$inputHeight} - 1px);
    }
    .bd-dialog {
        .bd-dialog__body {
            .bd-dialog__body--part {
                width: 50%;
                display: inline-block;
                .bd-dialog__body--part--main {
                    font-size: $font;
                    width: 80%;
                    min-width: 250px;
                    margin: auto;
                    .bd-dialog__body--part--title {
                        text-align: center;
                        padding: $padding;
                        font-weight: bold;
                        color: $fontCL;
                    }
                    .bd-dialog__body--part--item {
                        background: $primary;
                        color: $white;
                        margin: $padding / 2;
                        padding: $padding;
                        border-radius: $borderRadius;
                        &.is-change {
                            background: $success;
                        }
                    }
                }
                &.bd-dialog__body--left {
                    cursor: not-allowed;
                    .bd-dialog__body--part--main {
                        .bd-dialog__body--part--title {
                            color: $fontCS;
                        }
                        .bd-dialog__body--part--item {
                            background: rgba($primary, 0.6);
                        }
                    }
                }
                &.bd-dialog__body--right {
                    cursor: pointer;
                }
            }
        }
    }
}

</style>