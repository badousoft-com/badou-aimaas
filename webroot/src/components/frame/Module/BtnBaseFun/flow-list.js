
import { Get_List_History_Title } from '@/service/module.js'
import { Export, Delete as Base_Delete } from '@/components/frame/Module/BtnBaseFun/list'
import { Has_Value } from '@/utils'
import { Event_Expand } from '@/service/event-expand'
import GlobalConst from '@/service/global-const'
import { Urge_By_BoId } from '@/api/frame/flow'

const updateFlowMessage = '所选信息包含已发起的信息，请重新选择'

/**
 * 添加
 * @param {Object} btnObj 按钮对象 
 */
async function Add (btnObj) {
    // this:bd-module-list组件所在作用域
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'before', arguments[1])
    // 跳转页面
    this.pushPage({
        // 最后的0是代表boStatus值，表示当前为拟稿，可请求路由动态按钮
        path: `/module/flow/flowVerify/${this.mdCode}/add/0`,
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
    await Event_Expand.call(this, 'afterSelect', arguments[1], selection)
    // 获取id，boStatus
    let { id, boStatus } = selection[0]
    if (!Has_Value(boStatus)) {
        this.$message.warning(`该条流程数据异常，不允许操作`)
        console.error(`boStatus期望的值为0或者1，此时值为${boStatus}`)
        return
    }
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'before', arguments[1], selection)
    // boStatus：0的状态为拟稿，只有拟稿状态才为编辑，其他全部为查看模式
    let isView = boStatus.toString() !== '0' ? 1 : 0
    this.pushPage({
        // 最后的0是代表boStatus值，表示当前为拟稿，可请求路由动态按钮
        path: `/module/flow/flowVerify/${this.mdCode}/${id}/${boStatus}/${isView}`,
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
    // this:bd-module-list组件所在作用域
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
    // 获取id，boStatus
    let { id, boStatus } = selection[0]
    this.pushPage({
        path: `/module/flow/flowVerify/${this.mdCode}/${id}/${boStatus}/1`,
        title: Get_List_History_Title.call(this, btnObj, selection[0])
    })
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'after', arguments[1], selection)
}

/**
 * 催办
 * @param {Object} btnObj 按钮对象 
 */
async function Urging (btnObj) {
    // this:bd-module-list组件所在作用域
    // 获取列表页面所在作用域
    let listPageRef = this.listPageRef()
    // 获取选中列表数据
    let selection = this.getSelection()
    if (selection.length === 0) {
        this.$message({
            type: 'warning',
            message: '请选择需要催办的流程数据'
        })
        return
    }
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'beforePrompt', arguments[1], selection)
    this.$prompt(`填写您的催办意见`, `催办`, {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
    }).then(async ({ value }) => {
        let params = {
            boIds: selection.map(i => i.id).join(GlobalConst.separator),
            opinion: value // 意见框
        }
        // 注入模型js中事件节点
        await Event_Expand.call(this, 'beforePrompt', arguments[1], params)
        // 执行转办的逻辑
        Urge_By_BoId(params).then(async res => {
            if (res?.hasOk) {
                // 更新列表页面数据
                listPageRef.init()
                this.$message.success(this.getMessage(res?.message))
                // 注入模型js中事件节点
                await Event_Expand.call(this, 'after', arguments[1], res)
            } else {
                this.$message.error(this.getMessage(res?.message, false))
            }
        })
    }).catch((err) => {
        // 取消输入时进入当前逻辑 或当前逻辑错误
        console.error(err)
    })
}

/**
 * 删除
 * @param {Object} btnObj 按钮对象 
 */
async function Delete (btnObj) {
    Base_Delete.call(this, btnObj, {
        beforeConfirm: (resolve, selection) => {
            // 已发起的数据不允许更改
            let failStatus = selection.some(i => parseInt(i.boStatus) !== 0)
            if (failStatus) {
                this.$message.warning(updateFlowMessage)
            } else {
                // 允许删除
                resolve()
            }
        }
    })
}

// import { Add, Edit, View, Urging, Export, Delete } from '@/components/frame/Module/BtnBaseFun/flow-list'
export {
    Add, // 添加
    Edit, // 编辑
    View, // 查看
    Urging, // 催办
    Export, // 导出
    Delete, // 删除
}