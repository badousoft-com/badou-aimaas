<!--
 * @Description: 块设置弹窗 -内容设置
-->
<template>
    <div class="block-design-content">
        <transition name="fade">
            <module-form
                v-if="formVisible"
                noTitle
                ref="moduleForm"
                v-bind="module"
                :mdCode="mdCode"
                :fieldList.sync="_fieldList"
                :field="field"
                :detailId="singleContentId"
                :customSetting="customSetting"
                :elseAttrs="{ ...elseAttrs, field, fns: {get: getValue , set: changeValue} }"
                class="right-form__main">
            </module-form>
        </transition>
        <!-- 组合内容时展示 -->
        <div class="pad-10" v-if="contentType === '1'">
            <bd-button
                v-for="(i, index) in settingBtns"
                :key="index"
                v-bind="i"
                @click="exeMethod(i)">
            </bd-button>
            <!-- 多内容 -->
            <contents-edit
                ref="contentsEdit"
                class="marT-10"
                :tableData.sync="_contents"
                @row-dblclick="(row) => {dialogType = 'edit'; doEditContent(row)}">
            </contents-edit>
            <!-- 添加/编辑多内容时的弹窗 -->
            <bd-dialog
                :visible.sync="visible"
                append-to-body
                :title="dialogTitle"
                :before-close="beforeClose"
                :handlerList="dialogBtns">
                <module-form
                    v-if="dialogFormVisible"
                    ref="dialogModuleForm"
                    v-bind="module"
                    :mdCode="mdCode"
                    :detailId="dialogField.id"
                    noTitle
                    :fieldList.sync="dialogFieldList"
                    :isView="dialogType === 'view'"
                    :customSetting="customSetting"
                    :elseAttrs="{ ...elseAttrs, field: dialogField, fns: { get: getValue , set: changeValue } }"
                    class="right-form__main">
                </module-form>
            </bd-dialog>
        </div>
        <!-- 引用内容弹窗 -->
        <share-table-tab
            :visible.sync="quoteDialogVisible"
            :tabActive.sync="activeName"
            :tabsData="tabsData"
            optionResPath="bean"
            title="引用内容"
            @submit="quoteContent">
        </share-table-tab>
    </div>
</template>

<script>
import ModuleForm from '@/components/frame/Module/ModuleForm/index.vue'
import Switch from '@/components/frame/Common/MForm/components/items/Switch'
import Dialog from '@/components/frame/Dialog/index.vue'
import ShareTableTab from '@/views/panel/components/ShareTableTab'
import customSetting from './js/content'
import { Update_FieldList_Value } from '@/service/module'
import { Has_Value } from '@/utils'
import { Find_Share_Content } from '@/api/frame/panel/designer'
import ContentsEdit from '@/views/panel/components/ContentsEdit'
import GlobalConst from '@/service/global-const.js'
export default {
    name: 'block-setting-content',
    components: {
        [ModuleForm.name]: ModuleForm,
        [Switch.name]: Switch,
        [Dialog.name]: Dialog,
        ShareTableTab,
        ContentsEdit
    },
    inheritAttrs: false,
    props: {
        fieldList: {
            type: Array,
            default: () => []
        },
        module: {
            type: Object,
            default: () => { }
        },
        elseAttrs: {
            type: Object,
            default: () => { }
        },
        field: {
            type: Object,
            default: () => { }
        },
        code: {
            type: String,
            default: ''
        },
        // 模型编码
        mdCode: {
            type: String,
            default: ''
        }
    },
    computed: {
        // 字段分类（将字段按内容类型：单一内容、多内容 进行一个分组）
        fieldClassify () {
            let result = {
                other__content: [], // （tabId !== 'main_content'）
                __content: [] // （tabId === 'main_content'）
            }
            // 根据tabId，对内容编辑（__content）如：报表类型，数据源类型等内容信息
            // 其余信息（other__content）如：切换时间、展示类型等
            // ps：__content 中的内容是模型 content 中的字段
            //     other__content 中的内容是模型 block 中自定义配置项为{"tabId": "content"}的字段
            this.fieldList.forEach(o => {
                let customOptions = {}
                if (o.customOptions) {
                    customOptions = JSON.parse(o.customOptions)
                }
                let key = '__content'
                o.groupName = '内容信息'
                if (customOptions.tabId !== 'main_content') {
                    key = 'other__content'
                    o.groupName = '块信息'
                    // 拆分出块信息与内容信息，防止重复字段，在点击提交时不显示
                    if (o.name.indexOf('__block__') === -1) {
                        o.name = '__block__' + o.name
                    }
                }
                // let key = customOptions.tabId === 'main_content' ? '__content' : 'other__content'
                // o.groupName = (customOptions.tabId === 'main_content' ? '内容' : '块') + '设置'
                result[key].push(o)
            })
            return result
        },
        _fieldList: {
            get () {
                let result = []
                let key = ''
                // 组装对应内容类型的表单列表
                switch (this.contentType) {
                    case '0': // 单一内容
                        key = 'single'
                        break
                    case '1': // 多内容
                        key = 'multipleContent'
                        break
                    case '2': // 多图片
                        key = 'multipleImg'
                        break
                    default:
                        result = this.fieldList
                }
                if (key) {
                    // 表单字段大范围列表
                    let arr = ['other__content', '__content']
                    let fieldClassify = this.fieldClassify
                    // hiddenField 不展示的字段
                    // arr 中存放展示的大范围内容，先判断大范围内的表单是否需要展示
                    arr.forEach(o => {
                        if (this.hiddenField[key].indexOf(o) === -1) {
                            let filter_arr = fieldClassify[o].filter(i => {
                                // 筛选出在被确定需要展示的大范围内容中，不需要展示的内容
                                return this.hiddenField[key].indexOf(i.name) === -1
                            }) || []
                            result.push(...filter_arr)
                        }
                    })
                    // 在被隐藏的大范围内容中，需要展示的内容
                    this.visibleField[key].forEach(o => {
                        if (result.findIndex(i => i.name === o) === -1) {
                            let obj = this.fieldList.find(i => i.name === o)
                            obj && result.push(obj)
                        }
                    })
                }
                // _fieldList 里面需要到动态展示后端返回回来的表单字段（reportDynamicForm）的只有单一内容（this.contentType === '0'）
                // 多内容的在dialog中，不使用_fieldList
                this.contentType === '0' && result.splice(this.insertIndex + 1, 0, ...this.reportDynamicForm)
                return result
            },
            set (val) {
                this.$emit('update:fieldList', val)
            }
        },
        // 弹窗内容字段
        dialogFieldList: {
            get () {
                // 多内容表单字段当前（2021-05-11）全部取模型 content 中的字段
                // 但相对多内容来说，某些字段仍然不需要展示
                let hiddenFields = this.hiddenField.multipleContent
                let data = this.dialogType === 'view' ? this.viewDialogFieldList : this.fieldClassify.__content
                let result = data.filter(o => {
                    return hiddenFields.indexOf(o.name) === -1
                })
                this.contentType === '1' && result.splice(this.insertIndex + 1, 0, ...this.reportDynamicForm)
                return result
            },
            set (val) {
                this.$emit('update:fieldList', val)
            }
        },
        // 多内容时的表格数据
        _contents: {
            get () {
                return this.contents.filter(o => !o.multiImage)
            },
            set (value) {
                this.$set(this, 'contents', value)
            }
        },
        // 弹窗标题
        dialogTitle () {
            let settingStr = JSON.stringify(this.dialogField) === '{}' ? '添加' : '修改'
            return settingStr + '内容'
        },
        // 弹窗上的按钮
        dialogBtns () {
            let that = this
            let res = [
                {
                    id: 'cancel', name: '取消', icon: 'cancel', type: 'danger',
                    click: that.cancelDialog
                }
            ]
            if (this.dialogType !== 'view') {
                res.push({
                    id: 'submit', name: '确定', icon: 'save', loading: false, type: 'primary',
                    click: that.submit
                })
            }
            return res
        },
        // 单一内容时的表单详情id（即内容id）
        singleContentId () {
            return this.field?.[GlobalConst.panel.contentKey]?.[0]?.id
        }
    },
    data () {
        let self = this
        return {
            hasValue: Has_Value, // TODO 好像没看到使用，待删
            customSetting,
            // 对于不同的内容类型，应该隐藏的字段
            // other__content 指代多内容时（tabId !== 'main_content'）
            // __content 指代单内容时（tabId == 'main_content'）
            hiddenField: {
                single: [ // 单一内容
                    'name', 'chartsGroup', 'priority', 'multiImage', '__block__pitchTime', '__block__shouType',
                    'groupHeight', 'contentRatio', 'isHide',
                ],
                multipleImg: ['__content'], // 多图片
                multipleContent: ['multiImage', '__content'], // 多内容
            },
            // 不同的内容类型，应该展示的字段（优先级高于hiddenField）
            visibleField: {
                single: [], // 单一内容
                multipleImg: ['multiImage'], // 多图片
                multipleContent: [], // 多内容
            },
            // 多内容设置按钮
            settingBtns: [
                { id: 'add', size: 'mini', name: '添加', click: 'handlerAddContent', icon: 'plus', loading: false, type: 'success' },
                { id: 'view', size: 'mini', name: '查看', click: 'handlerViewContent', icon: 'view', loading: false, type: 'primary' },
                { id: 'edit', size: 'mini', name: '修改', click: 'handlerEditContent', icon: 'register', loading: false, type: 'warning' },
                { id: 'delete', size: 'mini', name: '删除', click: 'delete', icon: 'delete', loading: false, type: 'danger' },
                { id: 'quote', size: 'mini', name: '引用', click: 'showQuoteDialog', icon: 'import', loading: false, type: 'warning' },
            ],
            // 弹窗类型
            dialogType: 'view',
            viewDialogFieldList: [],
            // 是否显示弹窗
            visible: false,
            // 内容列
            contentColumn: [
                { prop: 'name', label: '内容名称' },
                { prop: 'priority', label: '排序' },
                {
                    prop: 'chartsGroup', label: '分组类别', width: 120, type: 'input',
                    blur: function (index, row) {
                        let chartsGroup = row.chartsGroup
                        self._contents.forEach((o, idx) => {
                            if (String(o.chartsGroup) === String(chartsGroup)) {
                                self._contents[idx].groupHeight = row.groupHeight
                            }
                        })
                    }
                },
                {
                    prop: 'groupHeight', label: '组别高度占总比（%）', type: 'input',
                    describe: `填写数字，该值为相对于拖拽设置的块高度的百分比；<br/>如果值为0，由剩余高度（100 - 已被自定义的组别高度）均等分配`,
                    blur: function (index, row) {
                        let chartsGroup = row.chartsGroup
                        self._contents.forEach((o, idx) => {
                            if (String(o.chartsGroup) === String(chartsGroup)) {
                                self._contents[idx].groupHeight = row.groupHeight
                            }
                        })
                    }
                },
                {
                    prop: 'contentRatio', label: '占行比例', type: 'input', placeholder: '默认1/3',
                    describe: `所在内容占据整行的比例，希望输入分数，最大为1；<br/>当没有值（为0或不填）或值不合法（非数字）时，取1/3<br/>
                                规则：对相同组别的内容非终止行尽量填满行，如需换行，可通过不同组别进行设置`,
                },
                { prop: 'isHide', label: '是否隐藏', type: 'switch' },
            ],
            // 组合内容列表
            contents: self.field[GlobalConst.panel.contentKey] || [],
            // 内容类型
            contentType: self.elseAttrs.contentType,
            // 表单展示
            formVisible: true,
            // 弹窗表单展示（用于刷新数据）
            dialogFormVisible: true,
            // 弹窗表单绑定字段值
            dialogField: {},
            // 有后台接口返回值定义的表单列表
            reportDynamicForm: [],
            // reportDynamicForm 的插入位置
            insertIndex: 0,
            // 是否仍存在正在上传中的图片（用于多图）
            isUploading: false,
            // 间隔函数
            timer: null,
            // 引用内容的弹窗tab
            tabsData: [
                { text: '我创建的', id: 'create', request: Find_Share_Content, params: { shareType: 0 } },
                { text: '分享给我的', id: 'share', request: Find_Share_Content, params: { shareType: 1 } },
            ],
            // 引用弹窗的tab：我创建的、分享给我的
            activeName: 'create',
            // 多内容引用弹窗
            quoteDialogVisible: false,
        }
    },
    methods: {
        exeMethod (obj) {
            if (typeof obj.click === 'function') {
                obj.click(obj)
            } else if (typeof this[obj.click] === 'function') {
                this[obj.click](obj)
            }
        },
        // 校验表单
        validateForm (refName = 'moduleForm') {
            let isSumit = true
            return new Promise((resolve, reject) => {
                let validateForm = function () {
                    let fn = this.$refs?.[refName]?.$refs?.edit?.validateForm
                    if (typeof fn === 'function') {
                        fn().then((res) => {
                            // 组装dataDef数据
                            if (this.reportDynamicForm.length) {
                                res.dataDef = ''
                                let dataDef = {}
                                let reportDynamicForm = this.reportDynamicForm || []
                                // 如果当前选择为过滤器类型
                                if (res.dataSourceType === '2') {
                                    reportDynamicForm = []
                                    // 过滤器的组装后的值，格式为{ originField: [{ fieldName: '', text: '', oper: '' }] }
                                    // 其中：originField为表单item（以下统一命名为item）中携带的originField
                                    //      fieldName：item.realName
                                    //      text: item.label
                                    //      oper: item.value
                                    this.reportDynamicForm.forEach(o => {
                                        // 判断是否有源表单（即此表单根据上一个表单的选择值渲染出来的）
                                        if (Has_Value(o.originField)) {
                                            if (Has_Value(o.value)) {
                                                dataDef[o.realName] = dataDef[o.realName] || []
                                                dataDef[o.realName].push({
                                                    fieldName: o.realName,
                                                    text: o.label,
                                                    oper: o.value
                                                })
                                            }
                                        // 筛选出 _type = multi_select 的表单
                                        } else {
                                            reportDynamicForm.push(o)
                                        }
                                    })
                                }
                                reportDynamicForm.forEach(o => {
                                    if (res[o.name]) {
                                        if (o.type === 'select') {  // 处理下拉表单字段
                                            dataDef[o.realName] = res[o.name].split(',').map(i => {
                                                let obj = o.options.find(j => j.id === i)
                                                return { fieldName: i, text: obj.text || '', sqlField: o.sqlField || false }
                                            })
                                            // multi_select 与 dataSourceType = 2 过滤器多选的情况在前面已做处理
                                            // if (res.dataSourceType !== '2' || o._type !== 'multi_select') {
                                            //     dataDef[o.realName] = res[o.name].split(',').map(i => {
                                            //         let obj = o.options.find(j => j.id === i)
                                            //         return { fieldName: i, text: obj.text || '' }
                                            //     })
                                            // }
                                        } else {
                                            dataDef[o.realName] = [{ sqlField: false, fieldName: res[o.name] }]
                                        }
                                    }
                                    delete res[o.name]
                                })
                                res.dataDef = JSON.stringify(dataDef)
                            }
                            if (refName === 'moduleForm') {
                                let contents = this.contents
                                let block_info = {}
                                let content_info = {}
                                Object.keys(res).forEach(key => {
                                    if (key.indexOf('__block__') === -1) {
                                        content_info[key] = res[key]
                                    } else {
                                        block_info[key.replace('__block__', '')] = res[key]
                                    }
                                })
                                if (this.contentType === '0') { // 单一内容
                                    contents = [content_info]
                                } else if (this.contentType === '2') {  // 多图片
                                    // 获取已保存的图片
                                    contents = (this.field.contents || []).filter(o => o.multiImage).map(o => {
                                        return { multiImage: o.multiImage }
                                    }) || []
                                    // 将之前已保存，当前编辑删除的图片移除列表
                                    if (res.deleteAttach) {
                                        let deleteAttach = JSON.parse(res.deleteAttach)
                                        if (deleteAttach.multiImage) {
                                            let deleteAttach_multiImage = deleteAttach.multiImage.split(',')
                                            contents = contents.filter(o => {
                                                return deleteAttach_multiImage.indexOf(o.multiImage) === -1
                                            })
                                        }
                                    }
                                    let multiImages = res.multiImage || []
                                    // 添加新增的图片
                                    multiImages.forEach(o => {
                                        if (o.attachId) {
                                            contents.push({ multiImage: o.attachId })
                                        }
                                    })
                                }
                                res = { ...block_info, contents } // 内容设置的数据一般放在contents中
                            }
                            if (refName === 'moduleForm' && this.$refs.contentsEdit) {
                                this.$refs.contentsEdit.checkForm().then(contentRes => {
                                    resolve(res)
                                }).catch(err => {
                                    reject(this.code)
                                })
                            } else {
                                resolve(res)
                            }
                        }).catch((err) => {
                            this.$message.error('请求失败，请联系管理员')
                            reject(this.code)
                        })
                    } else {
                        resolve({})
                    }
                }
                // 间隔函数的意义在于：由于块表单保存的局限性（附件的需要上传附件id，而非直接保存文件对象），需要使选择的图片全部上传完成后，在进行校验表单的操作
                // 防止在快速点击下的图片上传不完整的bug出现
                if (isSumit) {
                    isSumit = false
                    if (this.isUploading) {
                        this.timer = setInterval(() => {
                            if (!this.isUploading) {
                                validateForm.call(this)
                                clearInterval(this.timer)
                            }
                        }, 500)
                    } else {
                        validateForm.call(this)
                    }
                }
            })
        },
        // 刷新视图
        refresh () {
            this.formVisible = false
            this.$set(this, 'contentType', this.elseAttrs.contentType)
            setTimeout(() => {
                this.formVisible = true
            }, 300)
        },
        // 表格td点击事件
        cellClick (row, column, cell, event) {
            this.$refs.contentTable.toggleRowSelection(row)
        },
        // 打开弹窗、内容编辑
        doEditContent (row = {}) {
            let roleCode = this.$store?.state?.user?.userInfo?.roleCode
            if (row.isShare && roleCode !== 'SUPER') {
                this.$message.warning('只有创建者或超级管理员才能更改此内容数据')
                return
            }
            // this.dialogField = row
            this.$set(this, 'dialogField', row)
            // 将字段值赋值根据name赋值给对应表单
            this.setDialogFieldList()
            this.$nextTick(() => {
                this.showDialog()
            })
        },
        // 行className，用于设置所以，以便删除编辑的时候获取
        tableRowClassName (row) {
            // 设置row对象的index
            row.row.index = row.rowIndex
        },
        // 点击添加内容按钮
        handlerAddContent () {
            // 重置表单信息
            let formData = {}
            this.dialogFieldList.forEach(f => {
                formData[f.name] = f.defaultValue || null
            })
            this.dialogType = 'add'
            this.doEditContent(formData)
        },
        // 点击预览内容按钮
        handlerViewContent () {
            // 获取列表选中行
            let selection = this.$refs.contentsEdit.getSelection()
            if (selection.length !== 1) {
                this.$message.warning('请选择一行')
                return
            }
            // 构造查看表单数据
            this.viewDialogFieldList = this.fieldClassify.__content.map(o => {
                return {
                    ...o,
                    viewType: o.type,
                    isShowInPage: 1
                }
            })
            this.dialogType = 'view'
            this.doEditContent(selection[0])
        },
        // 点击修改内容按钮
        handlerEditContent () {
            // 获取列表选中行
            let selection = this.$refs.contentsEdit.getSelection()
            if (selection.length !== 1) {
                this.$message.warning('请选择一行')
                return
            }
            this.dialogType = 'edit'
            this.doEditContent(selection[0])
        },
        setDialogFieldList (dialogField = this.dialogField) {
            this.dialogFieldList = Update_FieldList_Value(this.dialogFieldList, dialogField, { valuePath: '' })
        },
        showDialog () {
            this.dialogFormVisible = true
            setTimeout(() => {
                this.visible = true
            }, 100)
        },
        // 删除内容
        delete () {
            let selection = this.$refs.contentsEdit.getSelection()
            if (!selection.length) {
                this.$message.warning('请选择需要删除的行')
                return
            }
            let delIndexs = selection.map(o => o.index)
            this.contents = this.contents.filter((o, index) => {
                return delIndexs.indexOf(index) === -1
            })
            this.$message.success('删除成功')
        },
        getValue (name) {
            return this[name] || ''
        },
        // 更改当前页面的参数值
        changeValue (name, value) {
            this[name] = value
        },
        // 多内容的确定
        submit () {
            this.validateForm('dialogModuleForm').then(res => {
                if (!Has_Value(res.priority)) {
                    res.priority = this.contents.length
                }
                if (this.dialogType === 'edit') {  // 编辑
                    this.contents.splice(this.dialogField.index, 1, res)
                } else if (this.dialogType === 'add') { // 新增
                    this.contents.push(res)
                }
                this.cancelDialog()
            })
        },
        // 取消弹窗
        cancelDialog () {
            this.visible = false
            setTimeout(() => {
                this.dialogFormVisible = false
                this.reportDynamicForm = []
            }, 100)
        },
        // 弹窗关闭前
        beforeClose (done) {
            done()
            this.cancelDialog()
        },
        // 更改多内容的顺序
        changePriority (index, changeIndex) {
            let current_priority = this._contents[index].priority
            let change_priority = this._contents[index + changeIndex].priority
            this._contents[index].priority = change_priority
            this._contents[index + changeIndex].priority = current_priority
            let item = this._contents.splice(index, 1)[0]
            this._contents.splice(index + changeIndex, 0, item)
        },
        // 点击引用按钮
        showQuoteDialog () {
            this.quoteDialogVisible = true
        },
        // 点击引用内容
        quoteContent (contents) {
            this.contents = this.contents.concat(contents)
            this.quoteDialogVisible = false
        },
    },
    mounted () { },
    beforeDestroy () {
        clearInterval(this.timer)
    }
}
</script>

<style lang="scss" scoped>
/deep/ .edit-content-dialog {
    .el-dialog__header {
        padding: 0;
    }
    .el-dialog__body {
        padding: 10px 20px;
    }
    .el-dialog__footer {
        text-align: center;
    }
}
// 多内容排序移动箭头
.priority-box {
    position: relative;
    .allow-box {
        width: 100%;
        height: 100%;
        color: $primary;
        position: absolute;
        top: 0;
        left: 0;
        .allow {
            vertical-align: top;
            display: inline-block;
            width: 40%;
            height: 100%;
            opacity: 0;
            &:hover {
                opacity: 1;
            }
        }
    }
}
</style>