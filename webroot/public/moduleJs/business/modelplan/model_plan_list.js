export default {
    beforeRender: function () {
        let modelId = this.fieldList.find(i => i.name === 'modelId')
        if (!modelId) {
            modelId.type = 'hidden'
        }
    },
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true },
        {
            id: 'copy',
            name: '方案复制',
            icon: 'edit',
            type: 'primary',
            click: function (btnObj) {
                let listPageRef = this.listPageRef()
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                btnObj.loading = true
                let url = '/project/maas/trainplan/trainplansave/copyObject'
                // 提交接口
                this.post(url, {
                    id: selection[0].id
                }).then((res) => {
                    if (res?.hasOk) {
                        this.$message.success('保存成功')
                        // 更新列表页面数据
                        listPageRef.init()
                    } else {
                        this.$message.error(`保存失败！${res.message}`)
                    }
                }).finally(() => {
                    // 设置按钮状态
                    btnObj.loading = false
                })

            }
        },
        // { 
        //     id: 'showmsg', 
        //     name: '查看运行报告', 
        //     icon: 'edit', 
        //     type: 'danger',
        //     click: function (btnObj) {
        //         let selection = this.getSelection()
        //         // 是否选择了一行数据
        //         if (selection.length !== 1) {
        //             this.$message.warning('请选择一行数据！')
        //             return
        //         }
        //                         // 定义弹窗所需按钮
        //                         let btnList = [
        //                             {
        //                                 name: '取消',
        //                                 type: 'danger',
        //                                 icon: 'cancel',
        //                                 click: function () {
        //                                     // 关闭弹窗表单
        //                                     this.$dialog.close()
        //                                 }
        //                             }, 
        //                         ]
        //         this.$dialog.init({
        //             // 弹窗内容类型
        //             type: 'standerEditCode',
        //             // 弹窗唯一标识，注意唯一标识与上面出现的函数getModelListObj的参数值保持一致
        //             id: '124',
        //             // 弹窗标题
        //             title: '查看运行报告',
        //             // 模型编码c
        //             mdCode: 'maas_fine_tuning_plan_pubr',
        //             // 根据内容自适应高度
        //             // isAutoFix: true,
        //             width: '800px',
        //             // 详情数据id
        //             detailId: selection[0].id,
        //             // 弹窗中按钮组
        //             handlerList: btnList,
        //             isView: true
        //         })
        // }
        // },
    ],
    /**
     * 字段键为height的值变更事件
     * @param {Object} formScope 表单所在页面作用域
     * @param {String} fieldName 字段键名
     * @param {*} value 字段值
     * @param {Object} fieldObj 字段对象
     * @param ...更多参数 有组件内自定义传回
     */
    serverId: function (formScope, fieldName, value, fieldObj) {
        if (value) {
            let modelId = this.fieldList.find(i => i.name === 'modelId')
            modelId.type = "dialogList"
            modelId.initDialogParams = function () {
                return {
                    defaultParamsObj: { serverId: value }
                }
            }
        } else {
            let modelId = this.fieldList.find(i => i.name === 'modelId')
            modelId.type = 'hidden'
            modelId.value = ''
        }
    },
}