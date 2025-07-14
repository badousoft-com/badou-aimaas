export default {
    /**
     * 树的配置
     */
    treeAjaxConfig: function (nodeParentId, data, node) {
        if (nodeParentId) {
            return {
                url: `${this.BASEURL}/auth/role/roletree/ptree.do`,
                params: { pid: nodeParentId }
            }
        }
        return {
            url: `${this.BASEURL}/auth/role/roletree/getTreeNodeByUser.do`,
            params: {}
        }
    },
    dataUrl: function () {
        return `${this.BASEURL}/auth/assign/roleuserassignlist/listJSON.do`
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
            roleId: data.id  || '',
            searchParam: "[]"
        }
    },
    buttons: [
        { id: "add", isHide: true },
        { id: "edit", isHide: true },
        { id: "delete", isHide: true },
        { id: "view", isHide: true },
        {
            id: "distributeUser",
            name: "分配新用户",
            type: "success",
            icon: "allot",
            click: function (btnObj) {
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 调用地址本选择
                this.addressBook({
                    type: '2',
                    selectType: 20
                }).then(data => {
                    // 获取选中树的节点node对象
                    let currentTreeNode = this.$attrs.currentTreeNode
                    // 获取节点对象数据
                    let roleData = currentTreeNode?.data
                    // 获取地址本选中数据
                    let userInfos = data.map(i => i.value).join(',')
                    // 定义参数
                    let params = {
                        roleId: roleData.id,
                        userInfos: userInfos
                    }
                    this.post(`${this.BASEURL}/auth/assign/roleuserassignsave/save.do`, params).then(res => {
                        if (res?.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success('操作成功')
                        } else {
                            this.$message.error('操作失败')
                        }
                    })
                })
            }
        },
        {
            id: "deleteUser",
            name: "删除用户",
            icon: 'delete',
            type: "danger",
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择需要删除的员工!')
                    return
                }
                this.$confirm('删除用户后该用户将不再具有该角色的权限操作，请慎重！您确定要删除选定的用户吗?', '删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let params = {
                        mdCode: this.mdCode,
                        ids: selection.map(i => i.id).join(',')
                    }
                    this.post(`${this.BASEURL}/auth/assign/roleuserassigndelete/delete.do`, params).then(res => {
                    	if (res?.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success('删除成功')
                        } else {
                            this.$message.error('删除失败')
                        }
                    })
                })
            }
        }
    ]
}