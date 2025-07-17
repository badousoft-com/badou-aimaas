import { Set_Panel_Dialog } from '@/service/base-service'
import GlobalConst from '@/service/global-const'
import { Has_Value } from '@/utils'

export default {
    /**
     * 树的配置
     */
    treeAjaxConfig: function (nodeParentId, data, node) {
        if (nodeParentId) {
            return {
                url: `${this.BASEURL}/org/department/departmenttree/ptree.do`,
                params: { pid: nodeParentId }
            }
        }
        return {
            url: `${this.BASEURL}/org/department/departmenttree/tree.do`,
            params: {}
        }
    },
    // 按钮
    buttons: [
        {
            id: "freeze",
            name: "冻结",
            type: 'danger',
            icon: "freeze",
            click: function () {
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择需要冻结的部门!')
                    return
                }
                this.$prompt('', '您确定要冻结选定的部门吗?', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    inputPlaceholder: '部门冻结必须填写冻结意见...',
                    inputValidator: value => {
                        if (value === null || value.trim() === '') {
                            return '请输入冻结意见'
                        }
                    },
                }).then(result => {
                    let params = {
                        opinion: result.value.trim(),
                        ids: selection.map(e => e.id).join(',')
                    };
                    this.post(`${this.BASEURL}/org/department/departmentdelete/delete.do`, params).then(res => {
                        if (res.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init().then(() => {
                                // 刷新数据后重新设置操作的数据选中
                                // 设置延迟，避免页面DOM未生成
                                setTimeout(() => {
                                    // 遍历已选数据
                                    selection.forEach(i => {
                                        // 由于elementUI table组件的选中需要与页面数据完全一致，所以不能用之前的已选数据，状态已发生变更，需要根据id重新拿到当前最新对应的数据
                                        let _row = listPageRef.data.find(j => j.id === i.id)
                                        // 设置选中
                                        this.listRef().toggleRowSelection(_row)
                                    })
                                }, 300)
                            })
                            this.$message.success(this.getMessage(res?.message, true, { successTip: '冻结成功' }))
                        } else {
                            this.$message.error(this.getMessage(res?.message, false, { successTip: '冻结失败' }))
                        }
                    })
                })
            }
  		}, {
            id: "panelSetting",
            name: "设置默认面板",
            icon: "setting",
            type: 'primary',
            click: function () {
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择要配置面板的部门!')
                    return
                } else if (selection.length !== 1) {
                    this.$message.warning('一次只能配置一个部门的面板')
                    return
                }
                // 执行弹窗配置面板事件
                Set_Panel_Dialog.call(this, selection, { type: '0' })
            }
        }, {
            id: 'delete',
            name: '删除',
            icon: 'delete',
            type: 'danger',
            click : function (btnObj) {
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择需要删除的部门!')
                    return
                }
                this.$confirm('确定删除吗？', '删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let params = {
                        ids: selection.map(i => i.id).join(',')
                    }
                    this.post(`${this.BASEURL}/org/department/departmentdelete/deleteOrg`, params).then(res => {
                        if (res?.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success('删除成功')
                            // 目前频繁使用的是在树型中：右侧列表删除时需要对应删除左侧树上的相关节点
                            this.$emit('afterDelete', selection)
                        } else {
                            this.$message.error('删除失败，失败原因：' + res?.message)
                        }
                    })
                })
            }
        }
    ],
    renderField: {
        flgActiveDesc: {
            /**
             * @param {Object} h 渲染函数参数对象
             * @param {Object} context 对象（包含props,children,slots,scopedSlots,parent,listeners,injection）
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前字段值
             * @param {Number} index 该行下角标
             * @param {Object} fieldObj 字段对象
             * @param {Object} scope 表格项对象
             */
            render: function (h, context, row, column, cellValue, index, fieldObj, scope) {
                // 获取账号状态 0-冻结，1-正常
                let { flgActive } = row
                // 根据状态获取状态色类
                let colorClass = flgActive === 0 ? 'dangerC' : 'successC'
                if (Has_Value(cellValue)) {
                    return (
                        // 以下使用可参考vue JSX语法：https://cn.vuejs.org/v2/guide/render-function.html#JSX
                        // 1. return 后面使用的是(), 注意不是{}
                        // 2. return()中填写的是元素标签：如<span>，不要写成字符串的标签如'<span>'
                        // 3. return()中的元素确保存在且仅有一个根级标签，该标签下可以多个子标签
                        // 4. 变量使用单花括号表示，{value},注意与vue模版标签{{}}不同
                        // 5. 标签使用事件时，格式为： on-事件类型={() => { /** 需要处理的逻辑代码 */ }}
                        // 6. 这里可使用的组件一般是全局组件，如果一定要使用局部组件，只能将局部组件注册为全局组件，有刚好的解决方案请联系
    
                        // 场景1：封装组件：事件
                        // <bd-button icon="edit" type="primary" on-click={this.editFun}></bd-button>
                        // 场景2：封装组件：事件与阻止冒泡
                        // <bd-button icon="edit" type="primary" nativeOn-click={(e) => {
                        //     this.editFun()
                        //     e.stopPropagation() // 阻止事件冒泡，避免表格行被选中
                        // }}></bd-button>
                        // 场景3：html原生标签：事件
                        // <button vOn:click={this.editFun}>ok</button>
                        // 场景4：html原生标签：事件与阻止冒泡
                        // <button vOn:click_stop_prevent={this.editFun}>ok</button>
                        // <span>
                        //     <bd-button icon="edit" type="primary" nativeOn-click={(e) => {
                        //         this.$message.success('edit')
                        //         e.stopPropagation() // 阻止事件冒泡，避免表格行被选中
                        //     }}></bd-button>
                        //     <bd-button icon="delete" type="danger" nativeOn-click={(e) => {
                        //         this.$message.error('delete')
                        //         e.stopPropagation() // 阻止事件冒泡，避免表格行被选中
                        //     }}></bd-button>
                        // </span>
                        <span>
                            <bd-icon name='point-fill' class={colorClass + ' icon-mini' + !Has_Value(cellValue) ? 'none' : ''}></bd-icon>
                            <span class={colorClass}>{cellValue}</span>
                        </span>
                    )
                } else {
                    return (
                        <span>{GlobalConst.view.value}</span>
                    )
                }
            }
        }
    }
}