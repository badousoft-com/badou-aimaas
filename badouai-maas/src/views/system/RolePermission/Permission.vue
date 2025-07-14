<template>
    <div
        class="permission-tree h-per-100"
        :class="{'has-button': showBtn}">
        <div
            v-if="showBtn"
            class="permission-tree__btn padding"
            :class="{'scroll-shadow': scrollTopDic !== 0 }">
            <bd-button
                class="permission-tree__btn__item"
                v-for="(i, index) in btnList"
                :key="index"
                v-bind="i"
                @click="handleClick(i)">
            </bd-button>
        </div>
        <div
            ref="scrollEl"
            class="permission-tree__main"
            @scroll="scrollForm">
            <bd-tree
                :ref="treeRefName"
                :node-key="nodeKey"
                :props="{
                    label: 'name',  // 使用数据下指定属性名作为节点展示名称
                    isLeaf: this.isLeaf  // 判断是否为叶子节点（末节点）
                }"
                :data.sync="data"
                :show-checkbox="showCheckbox"
                default-expand-all>
            </bd-tree>
        </div>
    </div>
</template>
<script>
import { HandleClick } from '@/service/module'
import BdTree from '@/components/frame/Common/Tree'
export default {
    name: 'permission-tree',
    components: {
        [BdTree.name]: BdTree,
    },
    props: {
        // 默认按钮列表
        defaultBtnList: {
            type: Array,
            default: () => []
        },
        // 组件传入默认请求参数
        defaultParamsObj: {
            type: Object,
            default: () => {}
        },
        // 是否展示数据前面的选择框
        showCheckbox: {
            type: Boolean,
            default: true
        }
    },
    data () { // 定义页面变量
        return {
            // 按钮列表
            btnList: this.defaultBtnList,
            // 按钮事件
            handleClick: HandleClick,
            // 树数据
            data: null,
            // 树节点键名
            nodeKey: 'id',
            // 选中节点数据
            checkedKeys: [],
            // 实例名称
            treeRefName: 'bdTree',
            // 模块滚动距离顶部高度
            scrollTopDic: 0,
        }
    },
    computed: {
        showBtn () {
            // 判断数据的可选状态与操作按钮的数目
            return this.showCheckbox && this.btnList.length > 0
        },
        url () {
            return `${this.BASEURL}/auth/assign/roleresourceassignmenu/tree.do`
        },
    },
    methods: { // 定义函数
        // 获取所有选中节点【包括选中的跟半选中的】，用于接口保存使用
        //     父节点下含多个子节点，若此时只选中一个子节点，则父节点呈现为半选中状态
        //     若此时只提交子节点，不提交父节点，则后台接口不会返回父节点以及旗下所有子节点
        //     所以必须将半选中的父节点也一起提交，这样回显时接口数据才能返回父节点，对应的子节点才能展示
        getCheckedNodeKeys () {
            // 获取半选节点
            let halfCheckedKeys = this.getTreeRef().getHalfCheckedKeys()
            // 获取选中节点
            let checkedKeys = this.getTreeRef().getCheckedKeys()
            // 合并选中与半选中的节点
            return [...halfCheckedKeys, ...checkedKeys].join(',')
        },
        // 表单内容模块滚动事件监听
        scrollForm () {
            // 获取滚动元素DOM
            let scrollEl = this.$refs?.scrollEl
            if (scrollEl) {
                // 更新滚动距离顶部高度
                this.scrollTopDic = scrollEl.scrollTop
            } else {
                this.scrollTopDic = 0
            }
        },
        /**
         * 指定节点是否为叶子节点，仅在指定了 lazy 属性的情况下生效
         * @param {Object} data 节点数据
         */
        isLeaf (data) {
            return !(data.isParent || data.hasChild)
        },
        // 获取底层树ref
        getTreeRef () {
            return this.$refs[this.treeRefName]?.$refs?.tree
        },
        // 初始化方法
        init () {
            // 启动loading加载状态
            this.$emit('changeLoadingStatus', true)
            // 清空节点选中状态
            this.checkedKeys = []
            // 获取树数据
            this.post(this.url, this.defaultParamsObj).then(res => {
                this.data = res || []
                // 将树数据中选中节点添加进变量this.checkedKeys
                this.data.forEach(i => this.setCheckedKeys(i))
                // elementUI tree语法，设置节点展示为选中状态
                this.getTreeRef().setCheckedKeys(this.checkedKeys)
                // 关闭loading加载状态，显示页面
                setTimeout(() => {
                    this.$emit('changeLoadingStatus', false)
                }, 300)
            })
        },
        /**
         * 将树数据中选中节点添加进变量this.checkedKeys
         * @param {Object} data 树节点数据
         */
        setCheckedKeys (data) {
            // 要注意规则：
            //     isChecked是标识字段，当true时表示节点需要设置为选中状态
            //     设置选中只能是叶子节点【也就是节点不再含有子节点，children.length为0】
            //     因为当子节点有一个选中时，其父节点isChecked就会是true，如果此时把父节点设置为选中，
            //         在没有专门设置父子节点不互相关联时，其所有子节点会全部选中，为预防此情况，选中节点必须是叶子节点
            if (data.ischecked && data.children.length === 0) {
                // 添加叶子节点
                this.checkedKeys.push(data[this.nodeKey])
            }
            if (data.children.length > 0) {
                // 递归
                data.children.forEach(i => this.setCheckedKeys(i))
            }
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        this.init()
    }
}
</script>
<style lang='scss' scoped>
.permission-tree::v-deep {
    &.has-button {
        .permission-tree__main {
            height: calc(100% - 60px);
            overflow: auto;
        }
    }
}

</style>