export default {
    renderField: {
        // 字段键名
        name: {
            /**
             * 格式化展示
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前表格项值
             * @param {Number} index 所在行下角标
             * @param {Object} fieldObj 字段所在列的配置数据
             */
            formatter: function (row, column, cellValue, index, fieldObj) {
                // this: 作用域指代核心列表[mList/index]所在页面作用域
                return `<span style="color:blue">${cellValue}</span>`
            },
            // 点击事件（若没有可以不写，去除整块click）
            click: function (row, column, cellValue, index, fieldObj) {
                const { href } = this.$router.resolve({
                    path: `/module/stander/edit/maas_fine_tuning_plan/`+row.id,
                    query: {
                    }
                });
                window.open(href, '_blank');
            }
        }
    },
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true },
        {
            id: 'copy',
            name: '复制',
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
    ]
}