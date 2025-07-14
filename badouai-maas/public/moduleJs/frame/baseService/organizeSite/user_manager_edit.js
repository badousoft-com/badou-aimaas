export default {
    // 检验规则
    validateRule: function () {
        return {
            logonId: [
                // 1.设置type类型进行校验
                // { type: 'integer', message: `请输入整数`, trigger: ['blur', 'change'] },
                // 2. 自定义函数进行校验
                { validator: uniqueLogonId.bind(this), trigger: ['blur', 'change'] }
            ],
            code: [
                { validator: uniqueUserCode.bind(this), trigger: ['blur', 'change'] }
            ]
        }
    },
    // 表单添加额外参数，用于提交使用
    addFormData (currentNodeData) {
        return {
            departmentId: currentNodeData.id,
            departmentName: currentNodeData.name
        }
    },
    // 自定义请求表单详情数据url
    dataUrl: function () {
        return '/org/employee/employeeedit/editJSON.do'
    },
    // 获取表单值的路径（相对表单字段值formItemVal  =》 formItemVal[valuePath]指代真正值）
    valuePath: '',
    buttons: [
        {
            id: 'save',
            name: '保存',
            icon: 'save',
            type: 'primary',
            loading: false,
            click: function (btnObj) {
                // this: bd-module-edit组件作用域       
                let formRef = this.getFormRef()
                // 表单校验
                formRef.validateForm().then((res) => {
                    // 设置按钮状态-加载中
                    btnObj.loading = true
                    // 获取表单保存接口地址
                    let url = `${this.BASEURL}/org/employee/employeesave/save.do`
                    // 提交接口
                    this.post(url, res).then((res) => {
                        if (res?.hasOk) {
                            this.$message.success('保存成功')
                            // 获取保存接口返回详情
                            let resDetail = res.bean?.returnDetails
                            // 处理tab情况，同时满足以下条件时：保存表单不返回上一页，新增时子tab不展示，保存数据后才展示
                            //     1. 当前为新增表单状态
                            //     2. 接口保存后返回数据存在值
                            //     3. 存在关联tab
                            if (!this.detailId &&
                                resDetail &&
                                this.childTab &&
                                this.childTab.constructor === Array &&
                                this.childTab.length > 0) {
                                // 获取[id：详情数据id]，[mdCode:模型编码]
                                let { id, data: { mdCode } } = resDetail || { data: {} }
                                if (mdCode && id) {
                                    // 替换当前路由，使用保存后最新的详情id进行渲染
                                    this.$router.replace({
                                        name: 'standerEditTree',
                                        params: { mdCode, id }
                                    })
                                } else {
                                    // 默认情况：返回上一页
                                    this.$router.go(-1)
                                }
                            } else {
                                // 默认情况：返回上一页
                                this.$router.go(-1)
                            }
                        } else {
                            this.$message.error(`保存失败！${res.message}`)
                        }
                    }).finally(() => {
                        // 设置按钮状态
                        btnObj.loading = false
                    })
                })
            }
        }
    ]
}

/**
 * 字段校验
 * @param {Object} rule 检验规则字段对象（校验字段名称，规则等）
 * @param {*} value 校验字段的值
 * @param {*} callback 回调函数【！！成功或失败都必须执行！！】
 */
function uniqueLogonId (rule, value, callback) {
    if (!value) {
        callback()
    } else {
        let url =  this.BASEURL + '/org/employee/employeesave/uniqueLoginId.do'
        let params = {
            logonId: value,
            id: this.formRuleParams?.id
        }
        this.get(url, params).then(res => {
            if (res?.hasOk) {
                callback()
            } else {
                callback(new Error(`登录账号已经存在`)) // 提醒文本
            }
        })
    }
}
function uniqueUserCode (rule, value, callback) {
    if (!value) {
        callback()
    } else {
        let url = this.BASEURL + '/org/employee/employeesave/uniquecode.do'
        let params = {
            code: value,
            id: this.detailId
        }
        this.get(url, params).then(res => {
            if (res?.hasOk) {
                callback()
            } else {
                callback(new Error(`编号已经存在`))
            }
        })
    }
}