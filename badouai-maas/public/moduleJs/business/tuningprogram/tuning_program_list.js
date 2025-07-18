
export default {
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true },
        {
            id: 'build',
            name: '启动微调',
            icon: 'start',
            type: 'danger',
            loading: false,
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }    
                let mdCode = this.mdCode
                btnObj.loading = true
                // 获取表单保存接口地址
                let url = `${this.BASEURL}/project/maas/tuningprogramn/tuningprogramntasksave/saveIncludeFile?mdCode=${mdCode}`
                // 提交接口
                selection[0].updateTime = selection[0].updateTime.replace('.0',"")
                selection[0].createTime = selection[0].createTime.replace('.0',"")
                this.post(url, selection[0]).then((res) => {
                    if (res?.hasOk) {
                        this.$message.success('启动成功!')
                        this.pushPage({
                            path: `/module/stander/list/maas_fine_tuning_modeln/placeholderr`,
                            title: "微调监控列表"
                        })

                    } else {
                        this.$message.error(`保存失败！${res.message}`)
                    }
                }).finally(() => {
                    // 设置按钮状态
                    btnObj.loading = false
                })
            }
        }
    ]
}