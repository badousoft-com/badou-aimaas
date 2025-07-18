/**
 * 我的待阅
 */
import { Deal_Work_Event } from '@/views/module/flow/part/flow-service'
import { Spread_Status_Change } from '@/api/frame/flow'
import GlobalConst from '@/service/global-const'
export default {
    buttons: [
        { id: "add", isHide: true },
        { id: "edit", isHide: true },
        { id: "delete", isHide: true },
        { id: "export", isHide: true },
        { id: "import", isHide: true },
        {
            id: 'view',
            name: "查看",
            icon: "view",
            type: 'primary',
            click: function (btnObj) {
                // 执行处理事件
                Deal_Work_Event.call(this, btnObj)
            }
        }, {
            id: 'change',
            name: "转为已阅",
            icon: "transfer",
            type: 'success',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择需要转为已阅的数据!')
                    return
                }
                // 将待阅转为已阅
                Spread_Status_Change({
                    wids: selection.map(i => i.id).join(GlobalConst.separator)
                }).then(res => {
                    if (res?.hasOk) {
                        // 更新列表页面数据
                        listPageRef.init()
                        this.$message.success(this.getMessage(res?.message))
                    } else {
                        this.$message.error(this.getMessage(res?.message, false))
                    }
                })
            }
        }
    ],
    // 设置双击数据行执行该行的审核逻辑
    dblClick: function (btnList) {
        // this:指向moduleList/index页面所在作用域
        let btnItem = btnList.find(i => i.id === 'view')
        btnItem.click.call(this, btnItem)
    },
    // 配置只看自己的已办数据
    dataUrl: function () {
        return `${this.BASEURL}/flow/worklist/worklistmodule/listJSON.do?mdCode=${this.mdCode}`
    },
}