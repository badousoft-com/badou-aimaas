<template>
    <div class="bd-module-list-tree defaultBg">
        <tree-page-show
            :isShow="isShow"
            :title="moduleName"
            :isLoadingRight="isLoadingRight">
            <template v-slot:left>
                <bd-tree
                    ref="bdTree"
                    :node-key="nodeKey"
                    :props="{
                        label: 'name',  // 使用数据下指定属性名作为节点展示名称
                        rootIdName: 'pid',  // 数据下指定属性名作为父节点
                        treeDataPath: '',  // 指定请求接口后节点数组数据所在路径
                        isLeaf: isLeaf  // 判断是否为叶子节点（末节点）
                    }"
                    lazy
                    :defaultExpandedKeys="defaultExpandedKeys"
                    :tree-open-second-node="treeOpenSecondNode"
                    :request-config="treeRequestConfig"
                    :currentNodeKey.sync="currentNodeKey"
                    @setCurrentNode="setCurrentNode">
                </bd-tree>
            </template>
            <template v-slot:right>
                <permission-tree
                    v-if="isShowRight"
                    :ref="rightRefName"
                    :defaultBtnList="defaultBtnList"
                    :defaultParamsObj="defaultParams"
                    :show-checkbox="showCheckbox"
                    @changeLoadingStatus="changeLoadingStatus">
                </permission-tree>
            </template>
        </tree-page-show>
    </div>
</template>
<script>
import Permission from './Permission.vue'
import BdTree from '@/components/frame/Common/Tree'
import Default_Btn_List from './button'
import { Tree_Node_List_URL, Tree_List_URL } from '@/api/frame/common'
import ModuleUtils from '@/js/ModuleUtils'
import TreePageShow from '@/components/frame/TreePageShow'
export default {
    name: 'module-list-tree',
    // 禁止父级传入属性作为当前组件的根标签属性
    inheritAttrs: false,
    components: {
        [BdTree.name]: BdTree,
        [Permission.name]: Permission,
        [TreePageShow.name]: TreePageShow
    },
    props: {
        // ref属性名称
        rightRefName: {
            type: String,
            default: 'permissionTree'
        },
        // 模型编码
        mdCode: {
            type: String
        },
        // 组件传入默认请求参数
        defaultParamsObj: {
            type: Object,
            default: () => {}
        },
        // 默认不开启，只有在父级样式中设置调整bd-module-list高度时使用有效
        fullHeight: {
            type: [Boolean, String, Number],
            default: false
        },
    },
    data () { // 定义页面变量
        return {
            // 是否展示右侧模块：需要等待参数齐全才展示，避免中间参数变更时不断请求
            isShowRight: false,
            // 默认按钮列表
            defaultBtnList: Default_Btn_List,
            // 模型数据
            module: null,
            // 树节点键名
            nodeKey: 'id',
            // 定义当前树选中节点
            currentTreeNode: null,
            // 树节点请求接口数据后的对应路径找到所需数组
            treeDataPath: '',
            // 默认请求参数
            defaultParams: null,
            // 当前选中树节点唯一键值
            currentNodeKey: null,
            // 默认展开的节点的 key 的数组
            defaultExpandedKeys: [],
            // 是否正在加载页面右侧菜单树状态
            isLoadingRight: true,
            // 是否显示选择框，进行权限编辑
            showCheckbox: true,

            // 自定义-树型请求配置
            diy_treeAjaxConfig: null,
            // 自定义-配置是否请求一级节点时默认打开二级节点
            diy_treeOpenSecondNode: null,
            // 自定义-设置列表数据初始参数
            diy_defaultParams: null
        }
    },
    computed: {
        // 是否允许展示所有内容，一般为等待模型接口数据获取完毕之后才可以
        isShow () {
            return this.module?.mdCode
        },
        // 获取模型名称
        moduleName () {
            return this.module?.name || ''
        },
        _$attrs () {
            return {
                ...this.$attrs,
                ...(this.module || {})
            }
        },
        // 配置是否请求一级节点时默认打开二级节点,默认打开
        treeOpenSecondNode () {
            if (typeof this.diy_treeOpenSecondNode === 'boolean') {
                return this.diy_treeOpenSecondNode
            }
            return true
        },
        // 右侧列表数据请求地址
        url () {
            return Tree_List_URL(this.mdCode)
        }
    },
    methods: { // 定义函数
        /**
         * 切换右侧菜单树的请求状态
         * status: 状态：默认false
         */
        changeLoadingStatus (status) {
            this.isLoadingRight = status
        },
        // 获取底层树ref
        getTreeRef () {
            return this.$refs?.bdTree?.$refs?.tree
        },
        // 请求右侧列表数据
        initRightTree () {
            this.$refs[this.rightRefName]?.init()
        },
        /**
         * 树节点选中事件
         * @param {Object} data 树节点数据
         * @param {Object} node 树节点对象
         * @param {Object} nodeComponent 树节点实例
         */
        setCurrentNode (data, node, nodeComponent) {
            // 根节点-sa账号不给操作，所以关闭选择框
            this.showCheckbox = node?.key !== 'ROOT'
            // 更新选中数节点
            this.currentTreeNode = node
            // 设置默认参数
            this.setDefaultParam(data, node, nodeComponent)
            // 参数准备齐全，可以显示列表，开始请求列表数据
            if (!this.isShowRight) {
                // 修改列表显示状态位为【显示】：出现时会自动请求列表数据
                this.isShowRight = true
            } else {
                // 列表状态已显示的状态下：触发列表请求
                // $nextTick等待页面参数更新齐全，不急
                this.$nextTick(() => {
                    // 请求列表数据
                    this.initRightTree()
                })
            }
        },
        /**
         * 设置右侧列表的默认请求参数
         * @param {Object} data 树节点数据
         * @param {Object} node 树节点对象
         * @param {Object} nodeComponent 树节点实例
         */
        setDefaultParam (data, node, nodeComponent) {
            // 定义参数对象，获取当前已有参数
            let _params = this.defaultParamsObj || {}
            _params.roleId = data.id
            // 更新页面参数变量，传递给列表组件用于请求
            this.defaultParams = _params
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
            // 判断是否存在自定义配置项事件，存在则执行
            if (this.diy_treeAjaxConfig && typeof this.diy_treeAjaxConfig === 'function') {
                // this.diy_treeAjaxConfig执行结束需要返回配置对象
                requestConfig = this.diy_treeAjaxConfig.call(this, nodeParentId, data, node)
            } else {
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
            }
            return requestConfig
        },
        /**
         * 使用自定义js配置项更新页面数据
         * @param {Object} customSetting 动态js配置对象
         */
        updateCustomSetting (customSetting) {
            // 获取动态自定义js文件customSetting
            let {
                treeAjaxConfig,  // 自定义树型请求配置
                treeOpenSecondNode, // 自定义-配置是否请求一级节点时默认打开二级节点
                defaultParams, // 自定义-设置列表数据初始参数
            } = customSetting || {}
            // 更新-自定义树型请求配置
            this.diy_treeAjaxConfig = treeAjaxConfig
            // 更新-自定义配置是否请求一级节点时默认打开二级节点
            this.diy_treeOpenSecondNode = treeOpenSecondNode
            // 更新-自定义设置列表数据初始参数
            this.diy_defaultParams = defaultParams
        },
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {},
    watch: {
        mdCode: {
            immediate: true, // 立即触发监听
            handler: async function (newVal, oldVal) {
                // mdCode不存在则直接return结束
                if (!newVal) return
                // 获取模型数据
                let _module = await ModuleUtils.listModuleCfg(newVal)
                // 检查模型动态js中是否有afterModule属性
                let afterModuleJSON = _module.customSetting?.afterModuleJSON
                // afterModule：表示请求完module模型数据之后进行数据自定义更改
                if (afterModuleJSON && typeof afterModuleJSON === 'function') {
                    // 执行afterModule函数，返回更改数据后的module
                    this.module = afterModuleJSON.call(this, _module)
                } else {
                    this.module = _module
                }
                // 将模型数据抛给父级组件使用
                this.$emit('afterModule', this.module)
                // 使用自定义js配置项更新页面数据
                this.updateCustomSetting(this.module.customSetting)
            }
        },
    },
}
</script>
<style lang='scss' scoped>
</style>