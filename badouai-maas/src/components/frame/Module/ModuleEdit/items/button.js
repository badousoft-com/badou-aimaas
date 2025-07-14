// id: 事件id
// name: 按钮名称
// icon： 图标名称
// click： 点击事件【考虑：不使用handle,可能还会有其他事件，例如hover等，拓展性考虑使用click】
import { Delete } from '@/components/frame/Module/BtnBaseFun/list'
function Get_Default_Buttons (isView = false) {
    // 查看页不展示下面三个按钮【新增/修改/删除】
    if (isView) return []
    return [
        {
            id: 'add',
            click: function (btnObj) {
                // this:bd-module-list组件作用域
                // 打开弹窗，进行表单编辑（新增）
                showDialog.call(this, btnObj, null, { isView })
            }
        }, {
            id: 'edit',
            click: function (btnObj) {
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行！')
                    return false
                }
                // 获取当前选中数据id
                let detailId = selection[0].id
                // 打开弹窗，进行表单编辑（编辑）
                showDialog.call(this, btnObj, detailId, { isView })
                
            }
        }, {
            id: 'delete',
            name: '删除',
            icon: 'delete',
            type: 'danger',
            click: function (btnObj) {
                // 执行基础删除函数
                Delete.call(this, btnObj)
            }
        }
    ]
}

function showDialog (btnObj, detailId, options) {
    // 定义弹窗唯一标识
    let signId = 'newChildForm'
    let _this = this
    // 获取主表id:mainId
    // 获取关联表单字段：relationEntityField
    let { mainId, relationEntityField, entityFieldValue } = this.elseAttrs
    let { isView } = options || {}
    // 定义弹窗所需按钮
    let btnList = [
        {
            name: '取消',
            icon: 'cancel',
            type: 'warning',
            click: function () {
                // 关闭弹窗表单
                this.$dialog.close()
            }
        },
        { 
            name: '保存',
            icon: 'save',
            type: 'primary',
            isHide: isView,
            loading: false,
            click: function (_btnObj) {
                // this: MDialog根组件作用域
                // _this：bd-module-list组件作用域

                // 获取基础表单作用域
                let formRef = this.getDialogConObj(signId, 4)
                // 获取表单moduleEdit/index作用域
                let editRef = this.getDialogConObj(signId, 5)
                if (!formRef) {
                    this.$message.warning('获取表单数据异常')
                }
                formRef.validateForm().then(res => {
                    _btnObj.loading = true  // 开启按钮加载状态
                    _this.post(editRef._saveUrl,  {
                        ...res,
                        [relationEntityField]: entityFieldValue
                    }).then(res => {
                        if (res.hasOk) {
                            _this.$dialog.close()
                            _this.$nextTick(() => {
                                // _this.$refs.moduleList.listPageRef().init()
                                _this.$message.success('保存成功')
                                // 2022-04-12 修改：在关联关系中，这里需要使用的是_this,不是this
                                _this.listPageRef().init()
                            })
                            
                        } else {
                            _this.$message.error(`保存失败！${res.message}`)
                        }
                    }).finally(() => {
                        _btnObj.loading = false  // 关闭按钮加载状态
                    })
                })               
            } 
        }
    ]
    this.$dialog.init({
        // 弹窗内容类型
        type: 'standerEditCode',
        // 弹窗唯一标识，注意唯一标识与上面出现的函数getModelListObj的参数值保持一致
        id: signId,
        // 弹窗标题
        title: _this?.elseAttrs?.moduleName,
        // 模型编码c
        mdCode: _this.mdCode,
        // 详情数据id
        detailId: detailId,
        // 弹窗中按钮组
        handlerList: btnList,
        // 是否查看
        isView,
    })
}

export default Get_Default_Buttons