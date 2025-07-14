import { Common_Delete } from '@/api/frame/common'
import { Event_Expand } from '@/service/event-expand'
import { Store_ListItems } from '@/service/list-item-ids'
import { ImportData, ExportData, Get_List_History_Title } from '@/service/module'
import GlobalConst from '@/service/global-const'
/**
 * 添加
 * @param {Object} btnObj 按钮对象 
 */
async function Add (btnObj) {
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'before', arguments[1])
    // this: bd-module-list组件作用域
    this.pushPage({
        path: `/module/stander/edit/${this.mdCode}/add`,
        title: Get_List_History_Title.call(this, btnObj)
    })
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
    this.pushPage({
        path: `/module/stander/edit/${this.mdCode}/${selection[0].id}`,
        title: Get_List_History_Title.call(this, btnObj, selection[0])
    })
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'after', arguments[1], selection)
}

/**
 * 查看
 * @param {Object} btnObj 按钮对象 
 */
async function View (btnObj) {
    // this: bd-module-list组件作用域
    // 获取选中列表数据
    let selection = this.getSelection()
    if (selection.length !== 1) {
        this.$message.warning('请选择一行！')
        return
    }
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'before', arguments[1], selection)
    // 存储列表的相关数据，用于表单详情页可切换上一页/下一页进行查看
    Store_ListItems.call(this, selection)
    this.pushPage({
        path: `/module/view/view/${this.mdCode}/${selection[0].id}`,
        title: Get_List_History_Title.call(this, btnObj, selection[0])
    })
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'after', arguments[1], selection)
}

/**
 * 导入
 * @param {Object} btnObj 按钮对象 
 */
async function Import (btnObj) {
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'before', arguments[1])
    ImportData.call(this, btnObj)
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'after', arguments[1])
}

/**
 * 导出
 * @param {Object} btnObj 按钮对象 
 */
async function Export (btnObj) {
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'before', arguments[1])
    ExportData.call(this, btnObj)
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'after', arguments[1])
}

/**
 * 删除
 * @param {Object} btnObj 按钮对象 
 */
async function Delete (btnObj) {
    // this: bd-module-list组件作用域
    // 获取列表页面所在作用域
    let listPageRef = this.listPageRef()
    // 获取选中列表数据
    let selection = this.getSelection()
    if (selection.length === 0) {
        this.$message.warning('至少选择一行！')
        return
    }
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'beforeConfirm', arguments[1], selection)
    this.$confirm('确定删除吗？', '删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        let params = {
            mdCode: this.mdCode,
            ids: selection.map(i => i.id).join(GlobalConst.separator)
        }
        // 注入模型js中事件节点
        await Event_Expand.call(this, 'before', arguments[1], params)
        Common_Delete(params).then(async (res) => {
            if (res?.hasOk) {
                // 更新列表页面数据
                listPageRef.init() // TODO：按道理这里应该用Array.splice删除数据就行，但考虑到上一页下一页请求数据时可能会混乱暂时使用刷新
                this.$message.success('删除成功')
                // 删除回调
                this.$emit('afterDelete', selection)
                // 注入模型js中事件节点
                await Event_Expand.call(this, 'after', arguments[1], res)
            } else {
                let defaultTip = '删除失败'
                this.$message.error(res?.message ? `${defaultTip}:${res.message}` : defaultTip)
            }
        })
    })
}

// import { Add, Edit, View, Import, Export, Delete } from '@/components/frame/Module/BtnBaseFun/list'
export {
    Add, // 添加
    Edit, // 编辑
    View, // 查看
    Import, // 导入
    Export, // 导出
    Delete, // 删除
}