import { Delete_Credential } from '@/api/business/accountBinding/accountBinding'

export default {
    dataUrl: function () {
        // this:指向moduleList/index.vue所在页面作用域
        return `${this.BASEURL}/project/model/modelfile/modellist/loadByInnerPath`
    },
    /**
    * 执行自定义表格行的双击事件
    * @param {Array} btnList 按钮数组
    */
    dblClick: function (btnList) {
        let selection = this.getSelection()
        this.pushPage({ 
            path: `/module/stander/list/fbpt_project_router_host/placeholder`, 
            title: '路径文件列表',
            query: { id: selection[0].id }
        })    
        // let btnItem = btnList.find(i => i.id === 'view')
        // btnItem && btnItem.click.call(this, btnItem)
    },
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { 
            id: 'edit', 
            name: '编辑', 
            icon: 'edit', 
            type: 'danger',
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }               
            }
        }
    ]
}