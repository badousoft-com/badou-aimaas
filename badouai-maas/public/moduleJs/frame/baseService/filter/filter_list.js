/**
 * 过滤器
 */
import { Change_Filter_State } from '@/api/frame/filter'
import { S_Storage } from '@/utils/storage'
import { Share_Filter, Del_Filter } from '@/service/filter'
export default {
    buttons: [
        { id: "add", isHide: true },
        { id: "edit", isHide: true },
        { id: "view", isHide: true },
        { id: "export", isHide: true },
        { id: "import", isHide: true },
        { id: "delete", isHide: true },
        { 
            id: "jump",
            name: "跳转过滤器页面",
            icon: "allot",
            type: 'primary',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                // let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择一个过滤器进行操作')
                    return
                }
                if (selection.length > 1) {
                    this.$message.warning('请只选择一个过滤器进行操作')
                    return
                }
                // 获取选中的过滤器id， 对应的页面地址pageUri
                let {
                    id,
                    pageUri
                } = selection[0]
                // 存储过滤器
                S_Storage.setItem('currentFilterId', id)
                // 设置延时，跳转页面
                setTimeout(() => {
                    this.$router.push({
                        path: pageUri
                    })
                }, 300)
            }
        }, { 
            id: "change",
            name: "切换过滤器类型",
            icon: "exchange",
            type: 'primary',
            click: function (btnObj) { 
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择一个过滤器进行操作')
                    return
                }
                if (selection.length > 1) {
                    this.$message.warning('请只选择一个过滤器进行操作')
                    return
                }
                // 定义初始
                let _from = null
                // 定义修改后
                let _to = null
                // 定义状态
                let _state = null
                // 定义数据字典
                let stateDic = {
                    '0': '系统过滤器',
                    '1': '自定义过滤器'
                }
                // 获取过滤器id,类型状态status
                let { id, state } = selection[0]
                // 判断当前类型是什么，对应更新字段文本
                if (state.toString() === '1') {
                    _from = stateDic['1']
                    _to = stateDic['0']
                    _state = '0'
                } else {
                    _from = stateDic['0']
                    _to = stateDic['1']
                    _state = '1'
                }
                this.$confirm(`确定将过滤器类型由【${_from}】更换为【${_to}】`, '切换过滤器类型', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    Change_Filter_State({
                        id,
                        state: _state
                    }).then(res => {
                        if (res?.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success(this.getMessage(res?.message))
                        } else {
                            this.$message.error(this.getMessage(res?.message, false))
                        }
                    })
                }).catch(() => {
                    // 取消当前操作     
                })
            }
        }, {
            id: 'share',
            name: '分享',
            icon: 'share',
            type: 'primary',
            click: function (btnObj) {
                // 获取列表页面所在作用域
                // let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                Share_Filter.call(this, selection)
            }
        }, {
            id: 'newDelete',
            name: '删除',
            icon: 'delete',
            type: 'danger',
            click: function (btnObj) {
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                // 删除逻辑
                Del_Filter.call(this, selection).then(res => {
                    if (!res) return
                    // 更新列表页面数据
                    listPageRef.init()
                })
            }
        }
    ],
    // 设置双击数据行执行该行的审核逻辑
    dblClick: function (btnList) {
        // this:指向moduleList/index页面所在作用域
        let btnItem = btnList.find(i => i.id === 'jump')
        btnItem.click.call(this, btnItem)
    },
    dataUrl: function () {
        return `${this.BASEURL}/filter/filtermodule/listJSON.do?mdCode=${this.mdCode}`
    },
    /**
     * @param {Array} module 模型对象数据
     */
    afterModuleJSON: function (module) {
        // 获取模型数据中的字段列表
        let { fieldList } = module
        // 添加一个字段生成多一列【操作按钮】
        fieldList.push({ 
            display: '类型', 
            name: 'type', 
            noTooltip: true
        })
        // 注意：修改完之后必须将模型数据返回
        return module
    },
    renderField: {
        // 过滤器类型
        type: {
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
                let stateDesc = row.state === '0' ? '系统' : '用户自定义'
                let _class = row.state === '0' ? 'dangerC' : 'primaryC'
                return (
                    <span>
                        <bd-icon name="point-fill" class={_class + ' icon-small'}></bd-icon>
                        <span class={_class}>{stateDesc}</span>
                    </span>
                )
            }
        }
    }
}