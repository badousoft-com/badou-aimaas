import { Change_To_Base_Form } from '@/components/frame/Module/ModuleForm/index.js'
import { Update_FieldList_Value } from '@/service/module'
import { Merge_List, Filter_AList_Without_BList } from '@/utils/list'
import { Has_Value } from '@/utils'
import { Delete_Field, Sync_Field, Force_Sync_Field, Save_Field } from '@/api/frame/desinger/field'
import GlobalConst from '@/service/global-const'
import { EventBus } from '@/service/event-bus'
import { Null_Judge } from '../../../../src/utils'

/**
 * 获取下一个顺序号
 * @param {Array} dataHasLoad 已有的列表数据
 * @param {Number} dic 排序间隔数
 * @returns {Number} 下一个顺序号
 */
function getNextPriority (dataHasLoad, dic = 10) {
    // 获取已显示的列表数据
    let _hasLen = dataHasLoad && dataHasLoad.length || 0
    // 若当前没有数据，则下一个排序号为0
    if (_hasLen === 0) return 0
    // 获取已有数据中的排序号组成的数组
    let priorityList = dataHasLoad.map(i => i.priority) || []
    // 获取已有排序号中最大的
    let maxPriority = Math.max.apply(null, priorityList)
    // 然后加上间隔数作为下一个排序号
    return parseInt(maxPriority || 0) + dic
}

/**
 * 保存模型字段列表
 * @returns 
 */
function saveFieldList () {
    return new Promise ((resolve) => {
        // 获取字段列表数据
        let fieldList = this.tableData
        // 执行保存字段列表api
        Save_Field({
            moduleId: this.elseAttrs.mainId,
            fields: fieldList && JSON.stringify(fieldList),
            isAll: false, // 2021-04-12修改，修复保存字段配置时会清空页面配置bug
            saveForm: false // 2021-04-12添加，修复保存字段配置时会清空页面配置bug
        }).then(res => {
            if (res?.hasOk) {
                let pageSettingName = 'pageSetting'
                EventBus.$emit("setPageSetting", pageSettingName)
                // 获取指定变量
                let loadFieldFn = this.elseAttrs?.mainScope?.getCurrentTabScope()?.[pageSettingName]?.loadField
                if (typeof loadFieldFn === 'function') {
                    // 刷新页面配置的字段
                    loadFieldFn()
                }
                // 刷新列表数据，确保每条数据id更新
                this.listPageRef().init()
                resolve(true)
            } else {
                this.$message.error('保存失败')
                resolve(false)
            }
        }).catch(e => {
            console.error('保存字段列表数据异常：', e)
            resolve(false)
        })
    })
}
/**
 * 执行同步api
 * @param {Function} syncFun 同步函数，这里主要区分普通同步函数与强制同步函数
 * @param {Object} itemObj 按钮对象
 */
function syncField (syncFun, itemObj) {
    // 执行同步函数
    syncFun({
        moduleId: this.outScope.elseAttrs.mainId
    }).then(res => {
        if (res && res.hasOk) {
            this.$message({
                type: 'success',
                message: '同步完成'
            })
        } else {
            this.$message({
                type: 'error',
                message: res?.message || '同步失败'
            })
        }
    }).catch(() => {
        this.$message({
            type: 'error',
            message: '同步失败'
        })
    }).finally(() => {
        // 取消按钮加载状态
        itemObj.loading = false 
    })
}

const fieldAdjust = {
    // 字段类型键名
    displayTypeName: 'displayType',
    // 长度键名
    lengthName: 'length',
    // 注释
    commentsName: 'comments',
    // 实体名称
    entityNameName: 'entityName',
    // 必填键名
    requiredName: 'required',
    // 函数：判断是否为时间类型的字段类型
    isTimeType: function (val) {
        return val && /date|time|year|timestamp|datetime/.test(val)
    },
    getField: function (fieldList, fieldName) {
        return fieldList.find(i => i.name === fieldName)
    },
    // 初始化数据，当字段类型有值时，去除长度的必填校验
    initData: function (dataList) {
        // 获取字段类型对象
        let _displayTypeField = fieldAdjust.getField(dataList, fieldAdjust.displayTypeName)
        // 无值，则直接返回不需要处理
        if (!fieldAdjust.isTimeType(_displayTypeField.value)) return
        // 若存在值
        // 获取长度字段
        let _lengthField = dataList.find(i => i.name === fieldAdjust.lengthName)
        // 去除长度字段的必填校验
        _lengthField.rules = _lengthField.rules.split(GlobalConst.separator)
                                               .filter(i => i !== fieldAdjust.requiredName)
                                               .join(GlobalConst.separator)
    },
    // 字段类型的值变更事件
    fieldChangeFunObj: {
        // 根据字段类型的填写值处理长度是否必填，时间单位的字段类型不需要控制长度为必填，其他情况设置长度为必填
        displayType: function (fieldName, value, fieldObj) {
            let _lenFieldName = fieldAdjust.lengthName
            // 获取当前字段所在下角标
            let _lenIndex = this.dataList.findIndex(i => i.name === _lenFieldName)
            // 获取长度字段对象
            let _lenField = this.dataList[_lenIndex]
            // 定义必填的校验名称
            let _requireRuleName = fieldAdjust.requiredName
            // 获取校验规则，数组格式
            let _rules = (_lenField.rules || '').split(GlobalConst.separator)
            // 字段类型为varchar时辅助填充长度为255
            if (value && value === 'varchar') {
                _lenField.value = 255
            }
            // 判断值存在并且值为时间格式时
            if (value && /date|time|year|timestamp|datetime/.test(value)) {
                // 判断是否存在必填校验，存在则去除必填校验
                if (!_rules.includes(_requireRuleName)) return // 直接终止
                _rules = _rules.filter(i => i !== _requireRuleName)
                // 删除长度字段的校验的时候，顺带清除校验结果，避免之前的校验结果还在
                this.clearValidate(_lenFieldName)
                // 清空长度值
                _lenField.value = null
            } else {
                if (_rules.includes(_requireRuleName)) return
                // 其余情况设置添加必填
                _rules = [..._rules, _requireRuleName]
            }
            // 为确保能时校验规则动态生效，需要使用set的方式更新数组才可以
            this.$set(this.dataList, _lenIndex, {
                ..._lenField,
                rules: _rules.join(GlobalConst.separator) || ''
            })
        },
        // 字段名称变更的时候默认赋值给注释，允许注释再编辑；可能的问题：已有注释的情况修改字段名称会同步修改注释
        displayName: function (fieldName, value, fieldObj) {
            if (!value) return
            let _commentsField = fieldAdjust.getField(this.dataList, fieldAdjust.commentsName)
            if (!_commentsField.value || value.includes(_commentsField.value)) {
                _commentsField.value = value
            }
        },
        // 列名变更的时候，默认给实体名赋值。赋值规则为'a_name' => 'aName'
        fieldName: function (fieldName, value, fieldObj) {
            if (!value) return
            let _entityNameField = fieldAdjust.getField(this.dataList, fieldAdjust.entityNameName)
            _entityNameField.value = value.replace(/(.)_(.)/g, (str, $1, $2) => $1 + $2.toUpperCase())
        }
    }
}

export default {
    buttons: [
        { id: 'delete', isHide: true },
        { id: 'edit', isHide: true },
        { 
            id: 'add',
            name: '新增',
            type: 'primary',
            icon: 'add',
            click: function (btnObj) {
                let listScope = this
                this.get(`${this.BASEURL}/jdbc/common/basecommonedit/editModule.do`, {
                    mdCode: this.mdCode
                }).then(async (res) => {
                    if (res) {
                        let { moduleFields, dic } = res
                        const formId = 'newField'
                        let formInfo = {
                            id: formId,
                            title: '新增字段',
                            labelWidth: '110px',
                            columnNum: 3
                        }
                        // 将字段列表转化为数组
                        let _fieldList = JSON.parse(moduleFields)
                        // 将字段列表与值结合
                        _fieldList = Update_FieldList_Value(_fieldList, null)
                        let dataList = await Change_To_Base_Form(_fieldList, dic && JSON.parse(dic))
                        // 获取下个排序号
                        let _priority = getNextPriority(this.tableData)
                        // 将排序号更新给新增的这条数据
                        dataList.find(i => i.name === 'priority').value = _priority
                        let btnList = [
                            {
                                name: '取消',
                                icon: 'cancel',
                                type: 'danger',
                                click: function () {
                                    // 关闭弹窗表单
                                    this.$dialog.close()
                                }
                            }, 
                            {
                                id: 'save',
                                name: '保存',
                                type: 'primary',
                                icon: 'save',
                                // btnObj:当前按钮对象
                                click: function (btnObj) {
                                    let formRef = this.$refs[formId].$refs[formId]
                                    formRef.validateForm().then((res) => {
                                        listScope.tableData.push(res)
                                        setTimeout(() => {
                                            this.$dialog.close()
                                        })
                                    })
                                }
                            }
                        ]
                        this.$dialog.init({
                            type: 'form',
                            id: formId,
                            title: formInfo.title,
                            labelWidth: 140,
                            columnNum: formInfo.columnNum,
                            width: '80%',
                            dataList: dataList,
                            handlerList: btnList,
                            // 字段值变更的操作
                            fieldChangeFunObj: fieldAdjust.fieldChangeFunObj
                        })
                    }
                })
            }
        }, {
            id: 'loadData',
            name: '加载',
            icon: 'import',
            type: 'primary',
            loading: false,
            click: function (btnObj) {
                let that = this
                // 启动按钮加载状态
                btnObj.loading = true
                // 第一步：获取主表中相关数据
                // 获取主表字段数据
                let mainFieldList = this.elseAttrs?.mainFieldList
                if (!mainFieldList) return
                let dataSourceId = mainFieldList.find(i => i.name === 'dataSourceId').value
                let dbTable = mainFieldList.find(i => i.name === 'dbTable').value
                this.post(`${this.BASEURL}/module/design/moduledesignedit/loadDatabaseField.do`, {
                    dataSourceId,
                    dbTable
                }).then(res => {
                    if (!res?.hasOk) {
                        let _errorTip = `加载字段失败` + (res?.message && `:${res.message}` || '')
                        this.$message.warning(_errorTip)
                        console.error(_errorTip)
                        return
                    }
                    // 获取准备加载的数据
                    let _newData = JSON.parse(res?.bean) || []
                    // 定义判断的属性字段
                    let judgeAttrName = 'fieldName'
                    // 定义字段展示键名
                    let displayName = 'displayName'
                    // 对比已有的，获取即将添加的新字段数组
                    //     ignoreCase: true 表示忽略大小写进行匹配（只要字段统一转大写后一致就认为是一致）
                    let addFieldList = Filter_AList_Without_BList(_newData, this.tableData, judgeAttrName, { ignoreCase: true }) || []
                    // 若需要另外加载的字段数据为空
                    if (addFieldList.length === 0) {
                        this.$message.success('当前字段为最新，无可加载字段')
                        return
                    }
                    // 获取需要另外加载的字段的键，作为下面的表单checkbox值，表示初始字段全选中
                    let addFieldList__value = addFieldList.map(i => i[judgeAttrName]).join(GlobalConst.form.valueSeparator) || null
                    // 构造下面的表单checkbox的option选项值
                    let addFieldList__option = addFieldList.map(i => ({
                        id: i[judgeAttrName],
                        // 文本默认展示：键名：(标签名)
                        text: i[judgeAttrName] + (i[displayName] ? `(${i[displayName]})` : '')
                    }))
                    // 定义表单id
                    let formId = 'chooseField'
                    // 定义表单项键名
                    let addFieldName = 'addField'
                    // 弹窗展示需要加载的字段
                    this.$dialog.init({
                        type: 'form',
                        id: formId,
                        title: '选择加载的字段',
                        isAutoFix: true,
                        columnNum: 1,
                        dataList: [{
                            type: 'checkbox',
                            name: addFieldName,
                            value: addFieldList__value,
                            options: addFieldList__option
                        }],
                        handlerList: [
                            {
                                name: '取消',
                                icon: 'cancel',
                                type: 'danger',
                                click: function () {
                                    // 关闭弹窗表单
                                    this.$dialog.close()
                                }
                            },
                            { 
                                name: '确认添加',
                                icon: 'save',
                                type: 'primary',
                                click: function (btnObj) {
                                    // getDialogConObj:全局封装，2为指定自定义表单，返回该对象作用域
                                    let formObj = this.getDialogConObj(formId, 2)
                                    formObj.validateForm().then(res => {
                                        // 获取需要加载的字段，格式为'a,b'
                                        let _addField = res[addFieldName]
                                        if (!_addField) {
                                            this.$message.warning('请选择需要加载的字段')
                                        }
                                        // 定义结果集
                                        let _result = []
                                        // 根据指定字段的键获取需要加载字段的数组
                                        _addField.split(GlobalConst.form.valueSeparator)
                                                .forEach(i => {
                                                    _result.push(addFieldList.find(j => i === j[judgeAttrName]))
                                                })

                                        // 获取下一个排序号
                                        let _nextPriority = getNextPriority(that.tableData)
                                        // 遍历赋值排序号
                                        addFieldList.forEach((i, index) => {
                                            i.priority = _nextPriority + index * 10
                                        })
                                        // 过渡id项，框架默认初始会有id项，加载时不需要再加载多一条id
                                        // _newData = _newData.filter(i => i.fieldName !== 'id')
                                        // 合并规则：判断添加的每一条数据是否已存在列表，已存在则不处理
                                        // this.tableData = this.tableData.concat(_newData)
                                        // 将加载数据合并进已有数据
                                        that.tableData = Merge_List(that.tableData, _result, judgeAttrName, { isContentFirst: true })
                                        this.$message({
                                            message: '加载完成',
                                            type: 'success'
                                        })
                                        // 关闭弹窗表单
                                        this.$dialog.close()
                                    })
                                }
                            }
                        ],
                    })
                }).finally(() => {
                    // 更新按钮状态 - 停止加载
                    btnObj.loading = false
                })
            }
        }, {
            isHide: false,
            id: 'sync',
            name: '同步数据库',
            icon: 'move-fill',
            type: 'warning',
            click: function (btnObj) {
                let that = this
                this.$pageDialog.init({
                    title: '同步数据库', // 弹窗标题
                    pageUrl: 'common/modelDesigner/component/SyncFieldBtn', // 弹窗显示的页面路径，会拼接src/views
                    outScope: this, // 默认这样写即可，不要问
                    isAutoFix: true,
                    noCancel: true,
                    handlerList: [ // 操作的按钮列表
                        { name: '普通同步', icon: 'synchro', type: 'primary', loading: false, click: function (itemObj) {
                            itemObj.loading = true
                            saveFieldList.call(that).then(res => {
                                if (!res) {
                                    this.$message.error('字段保存失败，普通同步未开始')
                                    itemObj.loading = false
                                    return
                                }
                                syncField.call(this, Sync_Field, itemObj)
                            })
                            // this // 只想弹窗组件中的作用域
                            // this.outScope // 若使用dialog时传入属性outScope:this,则此处可获取使用弹窗的页面作用域
                            // this.pageScope // 挂载页面所在的作用域
                            
                        }},
                        { name: '强制同步', icon: 'synchro', type: 'danger', loading: false, click: function (itemObj) {
                            this.$confirm('此操作将使用【当前字段列表】直接覆盖【数据库字段】, 是否继续?', '强制同步', {
                                confirmButtonText: '确定',
                                cancelButtonText: '取消',
                                type: 'warning'
                            }).then(() => {
                                itemObj.loading = true
                                saveFieldList.call(that).then(res => {
                                    if (!res) {
                                        this.$message.error('字段保存失败，强制同步未开始')
                                        itemObj.loading = false
                                        return
                                    }
                                    syncField.call(this, Force_Sync_Field, itemObj)
                                })
                                // this // 只想弹窗组件中的作用域
                                // this.outScope // 若使用dialog时传入属性outScope:this,则此处可获取使用弹窗的页面作用域
                                // this.pageScope // 挂载页面所在的作用域
                            }).catch(() => {
                            })
                        }},
                    ]
                })
            }
        }, {
            id: 'fastAddId',
            name: 'id字段快捷生成',
            icon: 'add',
            type: 'success',
            click: function (btnObj) {
                if (this.tableData &&
                    this.tableData.find(i => i.entityName.toUpperCase() === 'ID')) {
                    this.$message.warning(`当前已存在id字段，请勿重复添加`)
                    return
                }
                this.tableData.unshift({
                    accuracy: null,
                    comments: 'id',
                    defaultValue: null,
                    displayName: 'id',
                    displayType: 'varchar',
                    entityName: 'id',
                    fieldName: 'id',
                    id: null,
                    isDbField: '1',
                    isUnique: '1',
                    length: 255,
                    nullable: '0',
                    primaryKey: '1',
                    priority: 0,
                    showInEntity: '0',
                })
            }
        }
    ],
    afterModuleJSON: function (module) {
        let { fieldList } = module
        fieldList.push({ 
            display: '操作', 
            name: 'operate', 
            width: '150', 
            noTooltip: true,
            fixed: 'right'
        })
        return module
    },
    renderField: {
        operate: {
            /**
             * 
             * @param {Object} h 渲染函数参数对象
             * @param {Object} context 
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前字段值
             * @param {Number} index 该行下角标
             * @param {Object} fieldObj 字段对象
             * @param {Object} scope 表格项对象
             */
            render: function (h, context, row, column, cellValue, index, fieldObj, scope) {
                return (
                    <span>
                        <bd-button icon="edit" type="primary" nativeOn-click={(e) => {
                            this.editFun(scope)
                            e.stopPropagation() // 阻止事件冒泡，避免表格行被选中
                        }}></bd-button>
                        <bd-button icon="delete" type="danger" nativeOn-click={(e) => {
                            this.delFun(row, scope)
                            e.stopPropagation() // 阻止事件冒泡，避免表格行被选中
                        }}></bd-button>
                    </span>
                )
            }
        },
    },
    createFun: {
        getBtn (signId) {
            return [
                {
                    id: 'cancel',
                    name: '取消',
                    icon: 'cancel',
                    type: 'danger',
                    click: function () {
                        // 关闭弹窗表单
                        this.$dialog.close()
                    }
                },
                {
                    id: 'save',
                    name: '保存',
                    icon: 'save',
                    type: 'primary',
                    loading: false,
                    // btnObj:当前按钮对象
                    click: function (btnObj) {
                        // this表示module_edit.vue页面所在作用域
                        let formRef = this.$refs[signId].$refs[signId].$refs.editRef.$refs.mainForm.$refs.moduleForm
                        let _this = this
                        formRef.validateForm().then(function (res) {
                            // alert(`表单对象为：${JSON.stringify(res)}`)
                            // 启用按钮加载状态-兼容4.0需要优先判断该按钮对象
                            if (btnObj && btnObj.isLoad !== undefined) {
                                btnObj.isLoad = true
                            }
                            let url = `${process.env.VUE_APP_BASE_API}/jdbc/common/basecommonsave/saveIncludeFile.do?mdCode=${_this.mdCode}`
                            _this.post(url, res).then((res) => {
                                if (res.hasOk) {
                                    _this.alert('保存成功', 'success')
                                    pageScope.$refs.moduleList.$refs[`list_${pageScope.module.code}`].init()
                                } else {
                                    _this.alert(`保存失败！${res.message}`)
                                }
                                setTimeout(() => {
                                    _this.$dialog.close()
                                })
                            }).finally(() => {
                                // 兼容4.0需要优先判断该按钮对象
                                if (btnObj && btnObj.isLoad !== undefined) {
                                    // 请求结束，结束按钮加载状态
                                    btnObj.isLoad = false
                                }
                            })
                        })
                    }
                }
            ]
        },
        /**
         * 
         * @param {Object} cellScope 表格单元格scope对象
         */
        editFun (cellScope) {
            // this: bd-table-list组件作用域
            // 获取列表页面作用域
            let listScope = this
            // 获取数据下角标
            let index = cellScope.$index
            this.get(`${this.BASEURL}/jdbc/common/basecommonedit/editModule.do`, {
                mdCode: this.elseAttrs?.mdCode
            }).then(async (res) => {
                if (res) {
                    let { moduleFields, dic } = res
                    const formId = 'editField'
                    let formInfo = {
                        id: formId,
                        title: '编辑字段',
                        labelWidth: '110px',
                        columnNum: 3
                    }
                    let _fieldList = JSON.parse(moduleFields)
                    let _formData = JSON.parse(JSON.stringify(cellScope.row))
                    _fieldList.forEach(i => {
                        i.value = Null_Judge(_formData[i.name], null)
                    })
                    // 获取数据字典集合
                    let dicList = dic && JSON.parse(dic)
                    // 获取弹窗表单字段数组
                    let dataList = await Change_To_Base_Form(_fieldList, dicList, {
                        mdCode: 'sys_module_field',
                        detailId: _formData['id']
                    })
                    // 检查数据，若字段类型为时间类型，去除长度的必填校验（只需要编辑时处理，新增无值不需要处理）
                    fieldAdjust.initData(dataList)
                    let btnList = [
                        {
                            id: 'cancel',
                            name: '取消',
                            icon: 'cancel',
                            type: 'danger',
                            click: function () {
                                // 关闭弹窗表单
                                this.$dialog.close()
                            }
                        }, {
                            id: 'save',
                            name: '确定保存',
                            icon: 'save',
                            type: 'primary',
                            // btnObj:当前按钮对象
                            click: function (btnObj) {
                                // this表示module_edit.vue页面所在作用域
                                let formRef = this.$refs[formId].$refs[formId]
                                let _this = this
                                formRef.validateForm().then((res) => {
                                    // 这里的使用列表行数据给表单，表单编辑完了再给列表展示
                                    // 特殊情况：列表上数据字典的字段使用的是xxxDesc字段进行展示，因此表单中字段（涉及数据字典的）修改值的同时还需要考虑把对应desc属性也修改下
                                    dataList.filter(i => i.options && i.options.length > 0)
                                            .forEach(j => {
                                                res[`${j.name}Desc`] = j.options.find(k => k.id === j.value)?.text
                                            })
                                    // 将编辑后的表单对象数据与表格行对象合并，获得最新行数据
                                    let _row = {...listScope.temp_data[index], ...(res || {})}
                                    // 更新表格数据
                                    listScope.temp_data.splice(index, 1, _row) 
                                    setTimeout(() => {
                                        _this.$dialog.close()
                                    })
                                })
                            }
                        }
                    ]
                    this.$dialog.init({
                        type: 'form',
                        id: formId,
                        // 弹窗内容过多时使用
                        // isFixedDialog: true,
                        title: formInfo.title,
                        labelWidth: formInfo.labelWidth,
                        columnNum: formInfo.columnNum,
                        dataList,
                        handlerList: btnList,
                        // 字段值变更的操作
                        fieldChangeFunObj: fieldAdjust.fieldChangeFunObj,
                    })
                }
            })
        },
        delFun (row, scope) {
            this.$confirm(`此操作将删除${row.fieldName || ''}该字段, 是否继续?`, '提示', {          
                confirmButtonText: '确定删除',          
                cancelButtonText: '取消',          
                type: 'error'
            }).then(() => {
                // 存在id，表示字段数据来源于接口，删除时使用接口删除
                if (row.id) {
                    Delete_Field({ id: row.id }).then(res => {
                        if (res && res.hasOk) {
                            // 获取数据下角标
                            let index = scope.$index
                            // 删除数据项
                            this.temp_data.splice(index, 1)
                        } else {
                            this.$message.error('删除失败')
                        }
                    }).catch(err => {
                        this.$message.error('删除失败')
                    })
                // 不存在id，表示该字段是当前新增，直接从数据源删除即可
                } else {
                    // 获取数据下角标
                    let index = scope.$index
                    // 删除数据项
                    this.temp_data.splice(index, 1)
                }
            }).catch(() => {})
        }
    },
    // 是否启动分页
    isUsePagination: false
}