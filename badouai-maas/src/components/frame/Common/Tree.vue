<template>
    <div>
        <el-tree
            class="bd-tree"
            ref="tree"
            :data.sync="tempData"
            :props="props"
            :load="loadNode"
            :lazy="lazy"
            :node-key="nodeKey"
            :show-checkbox="showCheckbox"
            :default-expand-all="defaultExpandAll"
            :current-node-key="currentNodeKey"
            :highlight-current="hignLightCurrent"
            :default-expanded-keys="_defaultExpandedKeys"
            :expand-on-click-node="expandOnClickNode"
            @node-expand="nodeExpand"
            @node-collapse="nodeCollapse"
            @node-click="nodeClick"
            
            :draggable="dragable"
            :allow-drop="allowDrop"
            :allow-drag="allowDrag"
            @node-drag-start="handleDragStart"
            @node-drag-enter="handleDragEnter"
            @node-drag-leave="handleDragLeave"
            @node-drag-over="handleDragOver"
            @node-drag-end="handleDragEnd"
            @node-drop="handleDrop">
            <div
                slot-scope="{ node, data }"
                :title="node.label">
                <bd-icon
                    :name="getTreeIcon(node, node.expanded, node.isLeaf)"
                    class="primary"></bd-icon>
                <span>{{node.label}}</span>
                <!-- <span>{{data.id}}</span> -->
            </div>
        </el-tree>
    </div>
    
</template>
<script>
import { finalRequest } from '@/service/request'
import { Get_Full_Url } from '@/service/url'
import { Sort_List } from '@/utils/list.js'
export default {
    name: "bd-tree",
    components: {},
    props: {
        // 展示数据
        data: {
            type: Array
        },
        // 配置选项
        props: {
            type: Object
        },
        // 是否懒加载子节点，需与 load 方法结合使用
        lazy: {
            type: Boolean,
            default: false
        },
        // 每个树节点用来作为唯一标识的属性，整棵树应该是唯一的
        nodeKey: {
            type: String
        },
        // 当前选中的节点
        currentNodeKey: {
            type: String
        },
        // 是否在点击节点的时候展开或者收缩节点， 默认值为 true，如果为 false，则只有点箭头图标的时候才会展开或者收缩节点。
        // 默认值设置值为false表示
        //     点击节点时不会触发折叠或打开，可以在节点上挂事件
        //     只有点击三角形才会折叠
        expandOnClickNode: {
            type: Boolean,
            default: false
        },
        // 请求数据的配置项
        requestConfig: {
            type: Function
        },
        // 是否高亮当前选中节点
        hignLightCurrent: {
            type: Boolean,
            default: true
        },
        // 配置是否请求一级节点时默认打开二级节点,默认打开
        treeOpenSecondNode: {
            type: Boolean,
            default: true
        },
        // 默认展开的节点的 key 的数组
        defaultExpandedKeys: {
            type: Array,
            default: () => []
        },
        // 节点是否可被选择
        showCheckbox: {
            type: Boolean,
            default: false
        },
        // 是否默认展开所有节点
        defaultExpandAll: {
            type: Boolean,
            default: false
        },
        // 获取图标的函数
        treeIconFun: {
            type: Function
        },
        // 是否开启拖拽节点功能
        dragable: {
            type: Boolean,
            default: false
        },
        // 判断节点能否被拖拽 Function(node)
        allowDrop: {
            type: Function
        },
        // 拖拽时判定目标节点能否被放置。
        // Function(draggingNode, dropNode, type)
        // type 参数有三种情况：
        //     prev:放置在目标节点前
        //     inner:插入至目标节点
        //     next:放置在目标节点后
        allowDrag: {
            type: Function
        }
    },
    data () { // 定义页面变量
        return {
            // 树型数据
            tempData: [],
            // 用于存储所有懒加载节点数据，便于后面手动刷新节点数据（当子节点新增或者删除时）
            loadNodeObj: {}
        }
    },
    computed: {
        _defaultExpandedKeys: {
            get () {
                return this.defaultExpandedKeys
            },
            set (val) {
                this.$emit('update:defaultExpandedKeys', [...new Set(val)])
                this.$emit('expandChange', [...new Set(val)])
            }
        },
        // 获取父节点字段键名
        rootIdName () {
            return this.props?.rootIdName
        },
        // 接口获取节点数据时：获取数据源的路径
        treeDataPath () {
            return this.props?.treeDataPath
        },
        /**
         * 获取节点图标
         * @param {Boolean} expanded 节点是否为展开状态
         * @param {Boolean} isLeaf 节点是否为叶子节点
         */
        getTreeIcon (node, expanded, isLeaf) {
            // 判断是否传入获取图标的函数
            if (this.treeIconFun && typeof this.treeIconFun === 'function') {
                // 执行传入获取图标的函数
                return this.treeIconFun
            } else {
                return (node, expanded, isLeaf) => {
                    // 当前为叶子节点
                    if (isLeaf) return 'file'
                    // 非叶子节点：展开状态
                    if (expanded) return 'folderOpen-fill'
                    // 非叶子节点：折叠状态
                    return 'folder-fill'  
                }
            }
        }
    },
    methods: { // 定义函数
        /**
         * 加载子树数据的方法，仅当 lazy 属性为true 时生效
         * @param {Object} node: node节点对象
         * @param {Fucntion} resolve: 回调
         */
        loadNode (node, resolve) {
            // 保存当前节点数据，方便手动刷新时调用
            node.data?.id && (this.loadNodeObj[node.data.id] = node)
            // 定义获取当前节点【作为父节点】id
            // node.level === 0说明是当前树的根节点，根节点没有父节点所以值为空
            let nodeParentId = node.level !== 0 ? node.data[this.nodeKey] : ''
            // 获取请求数据的地址参数
            let { url, ...option } = this.requestConfig(nodeParentId, node?.data, node) || {}
            if (!url) return false
            // 判断地址是否含有域名，没有则补齐
            url = Get_Full_Url(url)
            finalRequest({
                url,
                method: 'get',
                ...option
            }).then(res => {
                // 获取接口返回数据-再按照指定路径treeDataPath提取所需数据
                let _res = this.treeDataPath? res[this.treeDataPath] : res
                if (!_res) return
                // 加载根节点
                if (!nodeParentId) {
                    // 定义根节点
                    let ROOT = null
                    if (_res.constructor === Array) {
                        // 目前接口会返回一个数组，所以需要根据每条数据是否含有父节点来判断哪一条是根节点数据
                        let _len = _res.length // 获取返回数据长度
                        // 情况1：啥也没有，返回空数组
                        if (_len === 0) return resolve([])
                        // 情况2：如果只有一条那就不判断了，就把它当节点数据
                        if (_len === 1) {
                            ROOT = _res[0]
                        // 情况2：如果多条就根据是否有父节点值来找到根节点（根节点是没有父节点的）
                        } else {
                            // 根节点是最顶层是没有父节点的，按照此规则找出根节点
                            ROOT = this.rootIdName && _res?.find(i => !i[this.rootIdName])
                        }
                    } else if (_res.constructor === Object) {
                        // 最终指向若为对象，则理解为直接获取到了根节点
                        ROOT = _res
                    } else {
                        return resolve([])
                    }
                    if (ROOT) {
                        // 定义获取节点数据id
                        let ROOT_NODEKEY = ROOT[this.nodeKey]
                        // 若设置默认打开二级节点，则将根节点数据加入默认展开项中，则会自动展开，请求二级节点
                        this.treeOpenSecondNode && this._defaultExpandedKeys.push(ROOT_NODEKEY)
                        // 先手动将数据挂到节点上(原本应该等待【return resolve([ROOT])】执行)，再调用事件；
                        node.data = ROOT
                        // 选中节点事件
                        this.setCurrentNode (ROOT, node)
                        return resolve([ROOT])
                    }
                } else {
                    // 列表排序
                    if (_res.constructor === Array) _res = Sort_List(_res, 'priority')
                    // 加载子节点
                    return resolve(_res)
                }
            })
        },
        // 刷新当前选中节点数据（由于懒加载树组件只会加载一次，当子数据新增或者删除时，需要手动重新load一遍数据，确保树上的数据与列表数据一致）
        refreshCurrentNodeData () {
            let _node = this.loadNodeObj[this.currentNodeKey]
            if (!_node) return
            // 执行树节点懒加载方法，重新获取节点数据
            // 添加数据时，使树刷新节点数据
            _node.loaded = false
            _node.expand()
        },
        /**
         * 节点被展开时触发的事件【点击三角形时】
         * @params {Object} data: 传递给 data 属性的数组中该节点所对应的对象
         * @params {Object} node: node节点对象
         * @params {Object} nodeComponent: node节点组件
         */
        nodeExpand (data, node, nodeComponent) {
            this._defaultExpandedKeys.push(data[this.nodeKey])
        },
        /**
         * 节点被关闭时触发的事件
         * @params {Object} data: 传递给 data 属性的数组中该节点所对应的对象
         * @params {Object} node: node节点对象
         * @params {Object} nodeComponent: node节点组件
         */
        nodeCollapse (data, node, nodeComponent) {
            let resultList = this._defaultExpandedKeys.filter(i => i !== data[this.nodeKey])
            this._defaultExpandedKeys = [...new Set(resultList)]
        },
        /**
         * 节点被点击时的回调
         * @params {Object} data: 传递给 data 属性的数组中该节点所对应的对象
         * @params {Object} node: node节点对象
         * @params {Object} nodeComponent: node节点组件
         */
        nodeClick (data, node, nodeComponent) {
            this.setCurrentNode (data, node, nodeComponent)
            // 保存当前节点数据，方便手动刷新时调用
            node.data?.id && (this.loadNodeObj[node.data.id] = node)
        },
        /**
         * 节点选中事件
         * @params {Object} data: 传递给 data 属性的数组中该节点所对应的对象
         * @params {Object} node: node节点对象
         * @params {Object} nodeComponent: node节点组件
         */
        setCurrentNode (data, node, nodeComponent) {
            // 设置选中节点的唯一键值
            this.$emit('update:currentNodeKey', data[this.nodeKey])
            // 事件抛出：设置当前选中节点对象
            this.$emit('setCurrentNode', data, node, nodeComponent)
        },
        /**
         * 节点开始拖拽时触发的事件
         * @param {Object} 被拖拽节点dragNode
         * @event {Object} 节点event
         */
        handleDragStart (dragNode, event) {},
        /**
         * 节点开始拖拽时触发的事件
         * @param {Object} dragNode
         */
        handleDragEnter (dragNode, enterNode, event) {},
        handleDragLeave () {},
        handleDragOver () {},
        handleDragEnd () {},
        handleDrop () {}
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
    },
    watch: {
        data: {
            immediate: true,
            handler: function (newVal, oldVal) {
                if (newVal && newVal.constructor === Array) {
                    this.tempData = newVal
                }
            }
        },
        // el-tree下current-node-key属性bug
        //     current-node-key初始设置值则可以显示高亮，但是通过js动态设置则无效
        //     需要结合setCurrentKey使用，确保最终的选中节点高亮效果
        currentNodeKey (nodeKey) {
            this.$refs?.tree?.setCurrentKey(nodeKey)
        },
    },
    activated () {
        // 刷新当前操作节点下的数据
        this.refreshCurrentNodeData()
    }
}
</script>
<style lang='scss' scoped>
</style>