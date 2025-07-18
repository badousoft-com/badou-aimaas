export default {
    saveUrl: function (mdCode) {
        // this:指向moduleEdit/index.vue页面所在作用域
        return `${this.BASEURL}/project/maas/maasfile/maastreefilesave/saveFile?mdCode=${mdCode}`
    },
    // buttons: [
    //     {
    //         id: 'save',
    //         name: '保存',
    //         icon: 'save',
    //         type: 'primary',
    //         loading: false,
    //         click: function (btnObj) {
    //             let mdCode = this.mdCode
    //             if (this.$route.path) {
    //                 //判断路径中是否包含add关键词 如果有代表是新增
    //                 //str.indexOf("")的值为-1时表示不包含
    //                 if (this.$route.path.indexOf("add") == -1) {
    //                     this.$confirm("保存后生成实际训练集文件.生成时间以训练集数量为准!", "提示", {
    //                         iconClass: "el-icon-question",//自定义图标样式
    //                         confirmButtonText: "已了解开始生成",//确认按钮文字更换
    //                         cancelButtonText: "取消",//取消按钮文字更换
    //                         showClose: true,//是否显示右上角关闭按钮
    //                         type: "warning",//提示类型  success/info/warning/error
    //                     }).then(() => {
    //                         //确认操作
    //                         // this: bd-module-edit组件作用域       
    //                         let formRef = this.getFormRef()
    //                         // 表单校验
    //                         formRef.validateForm().then((res) => {
    //                             // // 设置按钮状态-加载中
    //                             btnObj.loading = true
    //                             // 获取表单保存接口地址
    //                             let url = `${this.BASEURL}/project/maas/traindata/traindatasave/saveIncludeFile?mdCode=${mdCode}`
    //                             // 提交接口
    //                             this.post(url, res).then((res) => {
    //                                 if (res?.hasOk) {
    //                                     this.$message.success('保存成功')
    //                                     this.$router.go(-1)
    //                                 } else {
    //                                     this.$message.error(`保存失败！${res.message}`)
    //                                 }
    //                             }).finally(() => {
    //                                 // 设置按钮状态
    //                                 btnObj.loading = false
    //                             })
    //                         })
    //                     }).catch(() => {
    //                         //取消操作
    //                     });
    //                     return
    //                 }
    //                 // this: bd-module-edit组件作用域       
    //                 let formRef = this.getFormRef()
    //                 // 表单校验
    //                 formRef.validateForm().then((res) => {
    //                     // // 设置按钮状态-加载中
    //                     btnObj.loading = true
    //                     // 获取表单保存接口地址
    //                     let url = `${this.BASEURL}/project/maas/traindata/traindatasave/saveIncludeFile?mdCode=${mdCode}`
    //                     // 提交接口
    //                     this.post(url, res).then((res) => {
    //                         if (res?.hasOk) {
    //                             this.$message.success('保存成功')
    //                             this.$router.go(-1)
    //                         } else {
    //                             this.$message.error(`保存失败！${res.message}`)
    //                         }
    //                     }).finally(() => {
    //                         // 设置按钮状态
    //                         btnObj.loading = false
    //                     })
    //                 })
    //             }

    //         }
    //     }
    // ]
}