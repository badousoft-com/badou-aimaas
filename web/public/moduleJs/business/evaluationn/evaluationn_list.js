
export default {
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true },
        {
            id: 'import_data', name: '导入问答数据', icon: 'import', type: 'primary',isHide:true,
            click: function (btnObj) {
                // let mdCode = this.mdCode
                // // 获取列表页面所在作用域
                // this.pushPage({
                //     path: `/module/stander/list/${mdCode}/placeholder`,
                //     title: '微调方案编辑'
                // })
            }
        }
    ]
}