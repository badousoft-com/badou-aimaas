export default {
    dataUrl: function () {
        // this:指向moduleList/index.vue所在页面作用域
        return `${this.BASEURL}/project/maas/problemdata/problemdatacenterlist/listJSON?mdCode=maas_problem_data_new_center`
    },
    /**
   * 执行自定义表格行的双击事件
   * @param {Array} btnList 按钮数组
   */
    dblClick: function (btnList) {
        // this:指向moduleList/index页面所在作用域

        // 举例：双击执行查看按钮的事件
        // let btnItem = btnList.find(i => i.id === 'view')
        // btnItem && btnItem.click.call(this, btnItem)
    },
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'add', name: '导出', icon: 'export', type: 'primary', isHide: true },

        { id: 'view', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'delete', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'edit', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true },
        {
            id: 'back', name: '返回', icon: 'back', type: 'primary', click: function (btnObj) {
                this.$router.go(-1)
            }

        },
        { id: 'coverTrainData', name: '转成训练集', icon: 'import', type: 'primary', isHide: true },
        {
            id: 'match-import', name: '训练集匹配导入', icon: 'import', type: 'primary', isHide: true
        },
        {
            id: 'create-train', name: '智能推荐生成', icon: 'export', type: 'primary', isHide: true
        },
        {
            id: 'fork', name: 'fork', icon: 'export', type: 'primary', isHide: true,
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请至少选择一个样本数据集!')
                    return
                }
                let ids = []
                for (const i of selection) {
                    ids.push(i.id)
                }
                btnObj.loading = true
                this.post('/project/maas/problemdata/problemdatasave/exportTrainFile', {
                    id: id
                }, {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }).then((res) => {
                    if (res?.hasOk) {
                        this.$dialog.close()
                        this.$message.success('导入成功!')
                    } else {
                        console.log("保存数值失败!")
                    }
                }).finally(() => {
                    btnObj.loading = false
                })
            }
        }

    ]

}