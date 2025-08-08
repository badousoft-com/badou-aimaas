/**
 * 我的已办
 */
import { Deal_Work_Event } from '@/views/module/flow/part/flow-service'
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