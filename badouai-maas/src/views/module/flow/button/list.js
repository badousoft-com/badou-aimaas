import { ExportData, Get_List_History_Title } from '@/service/module.js'
import { Urge_By_BoId } from '@/api/frame/flow'
import GlobalConst from '@/service/global-const'
import { Has_Value } from '@/utils'

let updateFlowMessage = '所选信息包含已发起的信息，请重新选择'
// id: 事件id
// name: 按钮名称
// icon： 图标名称
// click： 点击事件【考虑：不使用handle,可能还会有其他事件，例如hover等，拓展性考虑使用click】
let defaultButtons = [
    {
        id: 'add',
        click: function (btnObj) {
            // this: bd-module-list组件作用域
            this.pushPage({
                // 最后的0是代表boStatus值，表示当前为拟稿，可请求路由动态按钮
                path: `/module/flow/flowVerify/${this.mdCode}/add/0`,
                title: Get_List_History_Title.call(this, btnObj)
            })
        }
    }, {
        id: 'edit',
        click: function (btnObj) {
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
            // 获取id，boStatus
            let { id, boStatus } = selection[0]
            if (!Has_Value(boStatus)) {
                this.$message.warning(`该条流程数据异常，不允许操作`)
                console.error(`boStatus期望的值为0或者1，此时值为${boStatus}`)
                return
            }
            // boStatus：0的状态为拟稿，只有拟稿状态才为编辑，其他全部为查看模式
            let isView = boStatus.toString() !== '0' ? 1 : 0
            this.pushPage({
                // 最后的0是代表boStatus值，表示当前为拟稿，可请求路由动态按钮
                path: `/module/flow/flowVerify/${this.mdCode}/${id}/${boStatus}/${isView}`,
                title: Get_List_History_Title.call(this, btnObj, selection[0])
            })
        }
    }, {
        id: 'view',
        click: function (btnObj) {
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
            // 获取id，boStatus
            let { id, boStatus } = selection[0]
            this.pushPage({
                path: `/module/flow/flowVerify/${this.mdCode}/${id}/${boStatus}/1`,
                title: Get_List_History_Title.call(this, btnObj, selection[0])
            })
        }
    }, {
        id: 'urgingT',
        name: '催办',
        icon: 'urgingT',
        priority: 20,
        type: 'warning',
        click: function (btnObj) {
            // this: bd-module-list组件作用域
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
            this.$prompt(`填写您的催办意见`, `催办`, {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
            }).then(({ value }) => {
                let params = {
                    boIds: selection.map(i => i.id).join(GlobalConst.separator),
                    opinion: value // 意见框
                }
                // 执行转办的逻辑
                Urge_By_BoId(params).then(res => {
                    if (res?.hasOk) {
                        // 更新列表页面数据
                        listPageRef.init()
                        this.$message.success(this.getMessage(res?.message))
                    } else {
                        this.$message.error(this.getMessage(res?.message, false))
                    }
                })
            }).catch((err) => {
                // 取消输入时进入当前逻辑 或当前逻辑错误
                console.error(err)
            })
        }
    }, {
        id: 'export',
        click: function (btnObj) {
            ExportData.call(this, btnObj)
        }
    }, {
        id: 'delete',
        click: function (btnObj) {
            // this: bd-module-list组件作用域
            // 获取列表页面所在作用域
            let listPageRef = this.listPageRef()
            // 获取选中列表数据
            let selection = this.getSelection()
            if (selection.length === 0) {
                this.$message.warning('至少选择一行！')
                return
            }
            // 已发起的数据不允许更改
            let failStatus = selection.some(i => parseInt(i.boStatus) !== 0)
            if (failStatus) {
                this.$message.warning(updateFlowMessage)
                return
            }
            this.$confirm('确定删除吗？', '删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                let params = {
                    mdCode: this.mdCode,
                    ids: selection.map(i => i.id).join(',')
                }
                this.post(`${this.BASEURL}/jdbc/common/basecommondelete/delete.do`, params).then(res => {
                    if (res?.hasOk) {
                        // 更新列表页面数据
                        listPageRef.init()
                        this.$message.success('删除成功')
                        // 删除回调
                        this.$emit('afterDelete', selection)
                    } else {
                        this.$message.error('删除失败')
                    }
                })
            })
        }
    }
    // , {
    //     id: 'verify',
    //     name: '提交审核',
    //     icon: 'transfer',
    //     type: 'primary',
    //     click: function (btnObj) {
    //         alert('发起审核')
    //     }
    // }
]
export default defaultButtons