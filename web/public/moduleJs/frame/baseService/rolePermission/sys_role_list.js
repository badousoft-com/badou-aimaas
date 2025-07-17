// 系统管理 - 角色权限 - 角色定义
import { Set_Panel_Dialog } from '@/service/base-service'
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
    buttons: [
        {
            id: "resourceDistribute",
            name: "资源分配",
            icon : "allot",
            type : "success",
            click: function (btnObj) {
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请选择一条数据')
                    return
                }
                alert('功能待测')
                return
                this.$router.push({
                    path: `/system/rolepermission/sysRole/placeholder`,
                    query: {
                        roleId: selection[0].id
                    }
                })
            }
        }, {
            id: "userDistribute",
            name: "人员分配",
            icon: 'allot',
            type : "success",
            click: function (btnObj) {
                this.pushPage({
                    path: `/module/tree/list/userRole/placeholder`,
                    title: '角色人员分配'
                })
            }
        }, {
            id: "panelDistribute",
            name: "面板分配",
            icon: 'allot',
            type: 'success',
            click: function (btnObj) {
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择要配置面板的角色!')
                    return
                } else if (selection.length !== 1) {
                    this.$message.warning('一次只能配置一个角色的面板')
                    return
                }
                // 执行弹窗配置面板事件
                Set_Panel_Dialog.call(this, selection, { type: '10' })
            }
        }
    ]
}