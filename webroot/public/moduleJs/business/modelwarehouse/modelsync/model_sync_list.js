export default {
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'add', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'edit', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'delete', name: '导出', icon: 'export', type: 'primary', isHide: true },
        {
            id: 'log',
            name: '日志',
            icon: 'logManage',
            type: 'primary',
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                // if (selection[0].status == 0){
                //     this.$message.warning('任务未启动.请先等待任务启动')
                //     return
                // }
                const { href } = this.$router.resolve({
                    path: `/modelapplog`,
                    query: {
                        id: selection[0].id,
                        reload: 'maas_model_warehouse_main',
                        modelSyncFlag: true
                    }
                });
                window.open(href, '_blank');
            }
        },
        {
            id: 'cancel',
            name: '取消',
            icon: 'logManage',
            type: 'danger',
            click: function (btnObj) {
                let listPageRef = this.listPageRef()
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                if(selection[0].status !=1){
                    this.$message.warning('任务未处于处理状态！')
                    return
                }
                btnObj.loading = true
                this.post('/project/maas/modelsync/modelsynctasksave/closeTask', {
                    id: selection[0].id
                }).then((res) => {
                    if (res?.hasOk) {
                        // 更新列表页面数据
                        listPageRef.init()
                        this.$message.success('取消任务成功!')
                    } else {
                        this.$message.error("'取消任务失败!请联系管理员!")
                    }
                }).finally(() => {
                    btnObj.loading = false
                })
            }
        },
    ],
    // dataUrl: function () {
    //     return `${this.BASEURL}/project/maas/modelwarehouse/modelwarehouselist/listJSON?mdCode=maas_model_warehouse_main`
    // },
}
