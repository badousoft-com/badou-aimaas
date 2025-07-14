export default {
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'edit', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'delete', name: '导入', icon: 'import', type: 'primary', isHide: true }
    ],
    
    /* 执行自定义表格行的双击事件
    * @param {Array} btnList 按钮数组
    */
    dblClick: function (btnList) {
        let selection = this.getSelection()

        this.pushPage({ 
            path: `/module/stander/list/maas_model_filelist/placeholder`, 
            title: '路径文件列表',
            query: { id: selection[0].id }
        })    
        // let btnItem = btnList.find(i => i.id === 'view')
        // btnItem && btnItem.click.call(this, btnItem)
    },
}