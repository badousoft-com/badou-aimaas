export default {
    /**
     * 树的配置
     */
    treeAjaxConfig: function (nodeParentId, data, node) {
        if (nodeParentId) {
            return {
                url: `${this.BASEURL}/system/security/resourcetree/ptreeByUser.do`,
                params: { pid: nodeParentId }
            }
        }
        return {
            url: `${this.BASEURL}/system/security/resourcetree/getTreeNode.do`,
            params: {}
        }
    },
    /**
     * 获取数据列表请求参数
     * @param {*} params 已有参数对象
     * @param {*} data 节点数据
     * @param {*} node  节点对象
     * @param {*} nodeComponent 节点实例
     */
    defaultParams: function (params, data, node, nodeComponent) {
        return {
            ...(params || {}),
            pid: data.id || ''
        }
    },
    dataUrl: function () {
        return `${this.BASEURL}/system/security/resourcetree/listJSON.do`
    },
    buttons: [
        { id: 'view', isHide: true },
        {
            id: 'delete',
            name: '删除',
            icon: 'delete',
            type: 'danger',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('至少选择一行！')
                    return
                }
                if (selection.length > 1) {
                    this.$message.warning('一次只能删除一条数据')
                    return
                }
                this.$confirm('确定删除吗？', '删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let params = {
                        mdCode: this.mdCode,
                        id: selection[0].id
                    }
                    this.post(`${this.BASEURL}/project/resource/roleresource/delete.do`, params).then(res => {
                    	if (res?.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success('删除成功')
                            // 删除数据后的事件抛给父组件对应处理，父组件为树时可能需要刷新左侧树节点
                            this.$emit('afterDelete', selection)
                        } else {
                            this.$message.error('删除失败')
                        }
                    })
                })
            }
        }
    ]
}