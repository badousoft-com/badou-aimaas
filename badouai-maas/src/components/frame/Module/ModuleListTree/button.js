import { Store_ListItems } from '@/service/list-item-ids'
import { ExportData, Get_List_History_Title } from '@/service/module.js'
let defaultButtons = [
    {
        id: 'add',
        click: function (btnObj) {
            // this:bd-module-list组件所在作用域
            pushEditPage.call(this, `/module/tree/edit/${this.mdCode}/add`, btnObj) 
        }
    }, {
        id: 'edit',
        click: function (btnObj) {
            // this: bd-module-list组件作用域
            // 获取选中列表数据
            let selection = this.getSelection()
            if (selection.length !== 1) {
                this.$message({
                    type: 'warning',
                    message: '请选择一行！'
                })
                return
            }
            // 存储列表的相关数据，用于表单详情页可切换上一页/下一页进行查看
            Store_ListItems.call(this, selection)
            pushEditPage.call(this, `/module/tree/edit/${this.mdCode}/${selection[0].id}`, btnObj, selection[0])
        }
    }, {
        id: "view",
        click: function (btnObj) {
            // this: bd-module-list组件作用域
            // 获取选中列表数据
            let selection = this.getSelection()
            if (selection.length !== 1) {
                this.$message.warning('请选择一行！')
                return
            }
            this.pushPage({
                path: `/module/view/view/${this.mdCode}/${selection[0].id}`,
                title: Get_List_History_Title.call(this, btnObj, selection[0])
            })
        }
    }, {
        id: 'export',
        click: function (btnObj) {
            ExportData.call(this, btnObj)
        }
    }, {
        id: 'delete',
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
            this.$confirm('确定删除吗？', '删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                let params = {
                    mdCode: this.mdCode,
                    ids: selection.map(i => i.id).join(',')
                }
                this.post(`${this.BASEURL}/jdbc/common/basecommondelete/delete.do`, params).then(res => {
                    if (res.hasOk) {
                        // 更新列表页面数据
                        listPageRef.init()
                        this.$message.success('删除成功')
                        // 删除回调
                        // 在树型中：右侧列表删除时需要对应删除左侧树上的相关节点
                        this.$emit('afterDelete', selection)
                    } else {
                        this.$message.error('删除失败')
                    }
                })
            })
        }
    },
]
export default defaultButtons

/**
 * 跳转编辑页面
 * @param {String} path 页面路由path
 */
function pushEditPage (path, btnObj, row) {
    // 获取选中树的节点node对象
    let currentTreeNode = this.$attrs.currentTreeNode
    // 获取节点对象数据
    let currentTreeNodeData = currentTreeNode?.data
    // 定义参数：树型默认需要传递节点数据，进入编辑用于提交
    let addFormData = {
        parentId: currentTreeNodeData?.id,
        parentName: currentTreeNodeData?.name
    }
    // 定义页面返回参数
    let backParams = {
        defaultExpandedKeys: this.$attrs?.defaultExpandedKeys?.toString(),
    }
    this.pushPage({
        path,
        query: {
            // 添加的表单数据，用于提交表单时添加此字段数据
            addFormData: encodeURIComponent(JSON.stringify(addFormData)),
            // 返回参数，用于返回到当前页面时使用
            backParams: encodeURIComponent(JSON.stringify(backParams)),
            // 节点对象数据
            currentTreeNodeData: encodeURIComponent(JSON.stringify(currentTreeNodeData)),
        },
        title: Get_List_History_Title.call(this, btnObj, row)
    })
}