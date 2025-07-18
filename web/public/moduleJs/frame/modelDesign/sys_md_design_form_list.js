import { Is_Str_Empty } from '@/utils/index'
import FormDesignerBuilder from '@/components/frame/FormDesigner/dialog/index.js'
import { Save_URL } from '@/api/frame/common'
export default {
    buttons: [
        { id: 'edit', isHide: true },
        { id: 'view', isHide: true },
        { id: 'import', isHide: true },
        { id: 'export', isHide: true },
        {
            id: 'add',
            click: function (btnObj) {
                this.$prompt('', '创建表单', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    inputPlaceholder: '请输入表名',
                    inputValidator: function (val) {
                        return Is_Str_Empty(val) ? '请输入表名' : true
                    }
                }).then(({ value }) => {
                    // 获取表单保存接口地址
                    let url = Save_URL(this.mdCode)
                    this.post(url, {
                        name: value
                    }).then((res) => {
                        if (res?.hasOk) {
                            this.$message.success('新建成功')
                            // 更新列表页面数据
                            this.listPageRef().init()
                        } else {
                            this.$message.error(`新建失败！${res?.message || ''}`)
                        }
                    }).finally(() => {
                        // 设置按钮状态
                        btnObj.loading = false
                    })
                }).catch(() => {   
                })
            }
        }, 
    ],
    /**
     * @param {Array} module 模型对象数据
     */
    afterModuleJSON: function (module) {
        // 获取模型数据中的字段列表
        let { fieldList } = module
        // 添加一个字段生成多一列【操作按钮】
        fieldList.push({ 
            display: '操作', 
            name: 'operate', 
            width: '150',
            noTooltip: true
        })
        // 注意：修改完之后必须将模型数据返回
        return module
    },
    renderField: {
        operate: {
            render: function (h, context, row, column, cellValue, index, fieldObj, scope) {
                return (
                    <el-tag class="pointer" nativeOn-click={(e) => {
                        let that = this
                        // 启用弹窗
                        FormDesignerBuilder.init({
                            title: row.name,
                            data: row.data,
                            // 保存函数
                            saveFun: function (formObj, formProps) {
                                // 获取模型编码
                                let _mdCode = that.$route.params.mdCode
                                // 获取保存接口地址
                                let url = Save_URL(_mdCode)
                                let { title, dataList } = formObj
                                // 保存接口
                                that.post(url, {
                                    id: row.id,
                                    name: title,
                                    formProps: formProps && JSON.stringify(formProps),
                                    data: dataList && JSON.stringify(dataList),
                                }).then((res) => {
                                    if (res?.hasOk) {
                                        that.$message.success('保存成功')
                                    } else {
                                        that.$message.error(`保存失败！${res?.message || ''}`)
                                    }
                                })
                            }
                        })
                        e.stopPropagation() // 阻止事件冒泡，避免表格行被选中
                    }}>设计表单</el-tag>
                )
            }
        }
    }
}