import { Copy_Module, Create_Code_Url } from '@/api/frame/desinger/field'
import { Save_Resource_And_Role } from '@/api/frame/user'
import { Refresh_Module_Cache } from '@/api/frame/module'
import { Download } from '@/utils/file'
import { Copy_Serve } from '@/service/base-service'
export default {
    renderField: {
        code: {
            render: function (h, context, row, column, cellValue, index, fieldObj, scope) {
                // 定义通用列表地址
                let commonListUrl = `/module/stander/list/${cellValue}/placeholder`
                // 定义流程列表地址
                let flowListUrl = `/module/flow/list/${cellValue}/placeholder`
                return (
                    <span>
                        {cellValue}<br/>
                        <span
                            class='primaryTag pointer'
                            vOn:click_stop_prevent={() => {Copy_Serve(commonListUrl)}}
                            title="复制模型列表地址">通</span>
                        <span
                            class='warningTag pointer'
                            vOn:click_stop_prevent={() => {Copy_Serve(flowListUrl)}}
                            title="复制流程列表地址">流</span>
                    </span>
                )
            }
        }
    },
    buttons: [
        { id: 'view', isHide: true },
        { 
            id: 'copy',
            icon: 'copy',
            name: '复制',
            type: 'primary',
            loading: false,
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择一个模型')
                    return
                }
                if (selection.length > 1) {
                    this.$message.warning('一次只能选择一个模型')
                    return
                }
                this.$confirm('即将复制该模块，请稍后自行更改模块名称等信息 !', '复制', {
                    confirmButtonText: '确定复制',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    btnObj.loading = true
                    Copy_Module({ moduleId: selection[0].id }).then(res => {
                        if (res && res.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                        }
                    }).finally(() => {
                        btnObj.loading = false
                    })
                })
            }
        }, {
            id: 'forward',
            icon: 'forward',
            name: '生成代码',
            type: 'primary',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择一个模型')
                    return
                }
                if (selection.length > 1) {
                    this.$message.warning('一次只能选择一个模型')
                    return
                }
                // 定义表单id
                let formId = 'deliverCode'
                // 弹窗展示需要加载的字段
                this.$dialog.init({
                    type: 'form',
                    id: formId,
                    title: '生成代码选择框',
                    isAutoFix: true,
                    columnNum: 1,
                    dataList: [
                        { type: 'text', name: 'entityName', label: '实体类名', value: 'xxx', placeholder: '输入xxx,后端会自动xxxEntity', rules: [
                            { required: true, message: '请输入实体类名', trigger: ['blur', 'change'] }
                        ]},
                        { type: 'text', name: 'pkgName', label: '包名', value: 'com.badou.project.xxx.xxx', placeholder: '建议为: com.badou.project.**.**', rules: [
                            { required: true, message: '请输入包名', trigger: ['blur', 'change'] }
                        ]},
                        { type: 'select', name: 'genType', label: '生成代码类型', value: 'BADOUD_EFAULT_4_CRUD', options: [
                            { id: 'BADOUD_EFAULT_3_CRUD', text: '3.0版本代码' },
                            { id: 'BADOUD_EFAULT_4_CRUD', text: '4.0版本代码' },
                            { id: 'BADOUMAVENMODULE', text: 'maven模块工程' },
                        ], rules: [
                            { required: true, message: '请输入包名', trigger: ['blur', 'change'] }
                        ]}
                    ],
                    handlerList: [
                        {
                            name: '取消',
                            icon: 'cancel',
                            type: 'danger',
                            click: function () {
                                // 关闭弹窗表单
                                this.$dialog.close()
                            }
                        }, { 
                            name: '确认生成',
                            icon: 'forward',
                            type: 'primary',
                            loading: false,
                            click: function (btnObj) {
                                // getDialogConObj:全局封装，2为指定自定义表单，返回该对象作用域
                                let formRef = this.getDialogConObj(formId, 2)
                                // 表单校验
                                formRef.validateForm().then((res) => {
                                    // 设置按钮状态-加载中
                                    btnObj.loading = true
                                    // 定义参数
                                    let params = {
                                        ...(res || {}),
                                        id: selection[0].id
                                    }
                                    // 下载文件
                                    Download({
                                        url: Create_Code_Url,
                                        params,
                                        method: 'post'
                                    }).then(({status, message}) => {
                                        if (status) {
                                            this.$message.success('操作成功')
                                            this.$dialog.close()
                                        } else {
                                            this.$message.error(message || '操作失败')
                                        }
                                    }).finally(() => {
                                        // 切换按钮加载状态
                                        btnObj.loading = false
                                    })
                                })
                            }
                        }
                    ],
                })
            }
        }, {
            isHide: false,
            id: 'createMenu',
            icon: 'document-fill',
            name: '生成菜单',
            type: 'primary',
            click: function (btnObj) {
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择一个模型')
                    return
                }
                if (selection.length > 1) {
                    this.$message.warning('一次只能选择一个模型')
                    return
                }
                let { code, tableName } = selection[0]
                // 定义弹窗唯一标识
                let signId = 'createMenu'
                // 定义弹窗所需按钮
                let btnList = [
                    {
                        name: '取消',
                        type: 'danger',
                        icon: 'cancel',
                        click: function () {
                            // 关闭弹窗表单
                            this.$dialog.close()
                        }
                    }, { 
                        name: '保存',
                        icon: 'save',
                        type: 'primary',
                        loading: false,
                        click: function (btnObj) {
                            // getDialogConObj:全局封装，4为指定模型表单，返回该页面作用域
                            let formObj = this.getDialogConObj(signId, 4)
                            formObj.validateForm().then(data => {
                                btnObj.loading = true // 按钮加载状态
                                // 保存数据
                                Save_Resource_And_Role(data).then(res => {
                                    this.$message.success(`成功生成菜单`)
                                    // 关闭弹窗表单
                                    this.$dialog.close()
                                }).catch(() => {
                                    this.$message.error(this.getMessage(res?.message, false))
                                }).finally(() => {
                                    btnObj.loading = false // 重置按钮加载状态
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
                    title: '生成菜单',
                    // 模型编码
                    mdCode: 'SysResourceMd',
                    // 根据内容自适应高度
                    isAutoFix: true,
                    // 弹窗中按钮组
                    handlerList: btnList,
                    customSetting: {
                        beforeRender: function () {
                            let that = this
                            function setFieldValue (name, value) {
                                let _nameField = that._fieldList.find(i => i.name === name)
                                _nameField && (_nameField.value = value)
                            }
                            // 组装路径下拉数据
                            let urlField = that._fieldList.find(i => i.name === 'url')
                            if (urlField) {
                                urlField.options = [
                                    { id: `/module/stander/list/${code}/placeholder`, text: '通用列表' },
                                    { id: `/module/flow/list/${code}/placeholder`, text: '流程列表' },
                                    { id: `/module/tree/list/${code}/placeholder`, text: '树形列表' },
                                ]
                            }
                            // 使用表中文名称默认作为菜单名称
                            setFieldValue('name', tableName)
                            // 设置编码
                            setFieldValue('code', code)
                            // 设置菜单地址
                            setFieldValue('url', `/module/stander/list/${code}/placeholder`)
                            // 设置图标
                            setFieldValue('bigIconPath', 'singleFile')
                        }
                    }
                })
            }
        }, {
            id: 'refresh',
            icon: 'refresh',
            name: '刷新缓存',
            type: 'warning',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择一个模型')
                    return
                }
                // 定义承诺列表
                let promiseList = []
                // 添加请求数据
                selection.forEach(i => {
                    promiseList.push(
                        Refresh_Module_Cache({
                            mdCode: i.code
                        })
                    )
                })
                // 等待请求全部结束
                Promise.all(promiseList).then((res) => {
                    res.forEach((i, index) => {
                        let { hasOk, message } = i
                        let _result = message || hasOk ? '成功' : '失败'
                        let _type = hasOk ? 'success' : 'error'
                        // 提示结果：使用时间函数，避免同一时间结果相互遮挡的情况
                        setTimeout(() => {
                            this.$message({
                                type: _type,
                                message: `模型【${selection[index].code}】刷新缓存：${_result}`
                            })
                        }, 0 + index * 300)
                    })
                })
            }
        }
    ]
}