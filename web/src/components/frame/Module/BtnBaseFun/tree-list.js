import { Get_List_History_Title } from '@/service/module'
import { Store_ListItems } from '@/service/list-item-ids'
import { View, Import, Export, Delete } from '@/components/frame/Module/BtnBaseFun/list'
import { Event_Expand } from '@/service/event-expand'

/**
 * 添加
 * @param {Object} btnObj 按钮对象 
 */
 async function Add (btnObj) {
    // this:bd-module-list组件所在作用域
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'before', arguments[1])
    // 跳转页面
    pushEditPage.call(this, `/module/tree/edit/${this.mdCode}/add`, btnObj)
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'after', arguments[1])
}

/**
 * 编辑
 * @param {Object} btnObj 按钮对象 
 */
async function Edit (btnObj) {
    // this: bd-module-list组件作用域
    // 获取选中列表数据
    let selection = this.getSelection()
    if (selection.length !== 1) {
        this.$message({
            type: 'warning',
            message: '请选择一行！'
        })
        return
    }
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'before', arguments[1], selection)
    // 存储列表的相关数据，用于表单详情页可切换上一页/下一页进行查看
    Store_ListItems.call(this, selection)
    // 跳转页面
    pushEditPage.call(this, `/module/tree/edit/${this.mdCode}/${selection[0].id}`, btnObj, selection[0])
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'after', arguments[1], selection)
}

/**
 * 跳转编辑页面
 * @param {String} path 页面路由path
 */
function pushEditPage (path, btnObj, row) {
    // 获取选中树的节点node对象
    let currentTreeNode = this.$attrs.currentTreeNode
    // 获取节点对象数据
    let currentTreeNodeData = currentTreeNode?.data
    // 定义参数：树型默认需要传递节点数据，进入编辑用于提交
    let addFormData = {
        parentId: currentTreeNodeData?.id,
        parentName: currentTreeNodeData?.name
    }
    // 定义页面返回参数
    let backParams = {
        defaultExpandedKeys: this.$attrs?.defaultExpandedKeys?.toString(),
    }
    this.pushPage({
        path,
        query: {
            // 添加的表单数据，用于提交表单时添加此字段数据
            addFormData: encodeURIComponent(JSON.stringify(addFormData)),
            // 返回参数，用于返回到当前页面时使用
            backParams: encodeURIComponent(JSON.stringify(backParams)),
            // 节点对象数据
            currentTreeNodeData: encodeURIComponent(JSON.stringify(currentTreeNodeData)),
        },
        title: Get_List_History_Title.call(this, btnObj, row)
    })
}

// import { Add, Edit, View, Import, Export, Delete } from '@/components/frame/Module/BtnBaseFun/tree-list'
export {
    Add, // 添加
    Edit, // 编辑
    View, // 查看
    Import, // 导入
    Export, // 导出
    Delete, // 删除
}