/**
 * 我的待办
 */
import { Deal_Work_Event } from '@/views/module/flow/part/flow-service'
import { Workitem_Valid } from '@/api/frame/flow'
export default {
    // 配置表格为单选，不可多选
    multiple: false,
    // 配置只看自己的待办数据
    dataUrl: function () {
        return `${this.BASEURL}/flow/worklist/worklistmodule/listJSON.do?mdCode=${this.mdCode}`
    },
    // 前端写法
    buttons: [
        { id: "add", isHide: true },
        { id: "edit", isHide: true },
        { id: "view", isHide: true },
        { id: "delete", isHide: true },
        { id: "export", isHide: true },
        { id: "import", isHide: true },
        {
            id: 'verify',
            name: "审核",
            icon: "transfer",
            type: 'primary',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                let selection = this.getSelection()
                if (!selection) return
                if (selection.length === 0) {
                    this.$message.warning('请先选择需要处理的数据!')
                    return
                }
                if (selection.length > 1) {
                    this.$message.warning('请选择一条数据进行处理!')
                    return
                }
                // 处理待办前，先校验当前该条数据是否已被处理
                Workitem_Valid(selection[0].id).then(res => {
                    if (res?.hasOk) {
                        // 仍为待办状态，则进行待办处理
                        Deal_Work_Event.call(this, btnObj)
                    } else {
                        // 已失效，提示待办已被处理
                        this.$message.error(this.getMessage(res?.message, false))
                        // 刷新列表数据
                        listPageRef.init()
                    }
                }) 
            }
        }
    ],
    // 设置双击数据行执行该行的审核逻辑
    dblClick: function (btnList) {
        // this:指向moduleList/index页面所在作用域
        let btnItem = btnList.find(i => i.id === 'verify')
        btnItem.click.call(this, btnItem)
    },
    renderField: {
        title: {
            formatter: function (row, column, cellValue, index, fieldObj) {
                let result = [`<span>${cellValue}</span>`]
                // 获取是否加急状态
                if (row.isCall.toString() === '1') {
                    result.unshift(`<span class="dangerTag marH-2">急</span>`)
                }
                // 获取是否退回状态
                if (row.flgBack.toString() === '1') {
                    result.unshift(`<span class="dangerTag marH-2">退</span>`)
                }
                // 组装展示文本
                return result.join('')
            },
        }
    }
}