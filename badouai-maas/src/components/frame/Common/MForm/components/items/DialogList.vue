<template>
    <div :class="selfClass" class="bd-dialog-list">
        <template v-if="!isView">
            <choose-tag
                :data="selection"
                :id-key="idKey"
                :text-key="textKey"
                :disabled="disabled"
                @showDialog="showDialog">
            </choose-tag>
        </template>
        <template v-else>
            查看状态：TODO
        </template>
    </div>
</template>
<script>
import ChooseTag from '@/components/frame/Common/MForm/components/items/part/ChooseTagShow'
import GlobalConst from '@/service/global-const'
import { Unique_Array, Get_Same_Array, Filter_AList_Without_BList } from '@/utils/list'
import { Get_Extra_Field } from '@/service/module'
export default {
    name: "bd-dialog-list",
    inheritAttrs: false,
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {
        [ChooseTag.name]: ChooseTag
    },
    props: {
        // 设置组件是否可编辑，默认可编辑Boolean(false)，非必须
        disabled: {
            type: Boolean,
            default: false
        },
        // 字段名
        name: {
            type: String,
        },
        // 值
        value: {
            type: String,
        },
        valueText: {
            type: String
        },
        // 是否不可编辑
        disabled: {
            type: Boolean,
            default: false
        },
        // 是否支持多选
        multiple: {
            type: Boolean,
            default: true
        },
        // 组件使用标签名称，非必须
        label: {
            type: String,
            default: GlobalConst.form.label
        },
        // 预输入文本，非必须
        placeholder: {
            type: String
        },
        // 模型编码：当前组件主要面向获取模型列表
        mdCode: {
            type: String
        },

        // 是否为查看状态
        isView: {
            type: Boolean,
            default: false
        },
        // 图标名称：用于查看页面展示
        iconName: {
            type: String
        },
        // 自定义class
        selfClass: {
            type: String,
            default: ''
        },
        // 初始弹窗参数函数
        initDialogParams: {
            type: Function
        }
    },
    data () { // 定义页面变量
        return {
            selection: [],
            // 弹窗列表是否已准备
            isDialogReady: false,
        }
    },
    computed: {
        // 可选数组数据中指定对象属性作为返回键
        idKey () {
            return this.$attrs.valueFieldIdSrc || GlobalConst.dicOption.idName
        },
        // 可选数组数据中指定属性作为返回文本值
        textKey () {
            return this.$attrs.valueFieldTextSrc || GlobalConst.dicOption.textName
        },
        // 弹窗初始化参数
        _initDialogParams () {
            if (!(this.initDialogParams && typeof this.initDialogParams === 'function')) return {}
            let _params = this.initDialogParams.call(this, this.$attrs) || {}
            // 判断是否传入默认参数defaultParamsObj,有则需要组装下数据格式
            if (!(_params.defaultParamsObj && Object.keys(_params.defaultParamsObj).length > 0)) return _params
            // 获取默认参数
            let _defaultParams = _params.defaultParamsObj
            // 定义结果集
            let _result = []
            // 组装默认参数
            Object.keys(_defaultParams).forEach(i => {
                _result.push({ name: i, value: _defaultParams[i], type: GlobalConst.searchBar.type })
            })
            // 更新参数
            _params.defaultParamsObj = { defaultSearchParam: JSON.stringify(_result)}
            return _params
        },
        // 冗余字段所包含的selection
        extraSelection () {
            let res = []
            // 获取冗余字段相关关系、冗余字段值
            let { extraRelations, extraValueObj } = this.$attrs
            // 冗余字段的值信息
            let tempExtraValue = {}
            // 循环冗余字段值信息，将其转化为数值格式
            Object.keys(extraValueObj).forEach(key => {
                tempExtraValue[key] = extraValueObj[key] ? extraValueObj[key].split(GlobalConst.separator) : []
            })
            tempExtraValue[this.name].forEach((i, index) => {
                let item = {}
                Object.keys(extraValueObj).forEach(key => {
                    item[extraRelations[key]] = tempExtraValue[key][index]
                })
                res.push(item)
            })
            return res
        }
    },
    methods: { // 定义函数
        // 弹窗中的列表数据配置项
        customSetting () {
            let that = this
            return {
                // 去除按钮事件（防止钻取跳转），这里只做列表数据选择
                buttons: [],
                // 列表数据是否支持多选
                multiple: that.multiple,
                // 列表渲染设置数据选中状态
                afterListRender: function () {
                    if (!(that.selection && that.selection.length > 0)) return
                    that.isDialogReady = false
                    // 获取当前列表数据与已选中数据中匹配的数据
                    let sameList = Get_Same_Array(JSON.parse(JSON.stringify(that.selection)), this.temp_data, that.idKey)
                    sameList.forEach(i => {
                        // 设置回显，匹配的数据设置为选中
                        i && this.listRef.toggleRowSelection(i, true)
                    })
                    that.isDialogReady = true
                },
                // 当选择项发生变化时会触发该事件
                selectionChange: function (selection) {
                    if (that.isDialogReady &&
                        !that.multiple &&
                        that.selection &&
                        that.selection.length > 0 &&
                        selection &&
                        selection.length > 0) {
                        // 当单选时，选择新的数据之前清除前面的数据
                        that.selection = []
                    }
                }
            }
        },
        // 弹窗选择数据
        showDialog () {
            if (!this.mdCode) {
                this.$message.warning(`请传入有效的mdCode参数`)
                return
            }
            const id = 'dialogListId'
            let that = this
            this.$dialog.init({
                type: 'standerListCode',
                id: id,
                title: '选择数据',
                mdCode: this.mdCode,
                customSetting: this.customSetting(),
                // 自定义参数,这里放置的参数会传递给子组件
                ...this._initDialogParams,
                handlerList: [
                    {
                        name: '取消',
                        type: 'danger',
                        icon: 'cancel',
                        handler: function () {
                            // 关闭弹窗表单
                            this.$dialog.close()
                        }
                    },
                    { 
                        name: '保存',
                        type: 'primary',
                        icon: 'save',
                        handler: function () {
                            // getDialogConObj:全局封装，3为指定模型列表，返回该页面作用域
                            let aimRef = this.getDialogConObj(id, 3)
                            // 定义获取当前列表中匹配的选中项数据
                            let suitSelection = Get_Same_Array(aimRef.tableData, that.selection, that.idKey)
                            // 获取列表最终选择的数据
                            let newSelection = aimRef.selection
                            // 对比获取取消选中的数据项(基于之前已选)
                            let delSelection = Filter_AList_Without_BList(suitSelection, newSelection, that.idKey)
                            // 获取之前已选数据（这一步主要是去除前一次已选的数据在这一次中被取消选中的数据）
                            let oldRestSelection = that.selection.filter(i => !delSelection.some(j => j[that.idKey] === i[that.idKey]))
                            // 更新页面-选中项数据变量
                            that.selection = Unique_Array([...oldRestSelection, ...newSelection], that.idKey)
                            // 注：selection 监听存在相同功能，疑似重复
                            // let { selectId, selectText } = that.getIdAndText(that.selection)
                            // // 将选中数据更新给父级value
                            // that.$emit('input', selectId)
                            // 关闭弹窗
                            this.$dialog.close()
                        } 
                    }
                ],
                // 设置在弹窗中的内容为铺满状态
                fullHeight: true
            })
        },
        getIdAndText (list) {
            let nameObj = { idName: 'selectId', textName: 'selectText' }
            if (!(list && list.constructor === Array && list.length > 0)) return { [nameObj.idName]: null, [nameObj.textName]: null }
            let _idList = []
            let _textList = []
            list.forEach(i => {
                i[this.idKey] && _idList.push(i[this.idKey])
                i[this.textKey] && _textList.push(i[this.textKey])
            })
            return {
                [nameObj.idName]: _idList.join(GlobalConst.separator) || null,
                [nameObj.textName]: _textList.join(GlobalConst.separator) || null
            }

        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {},
    watch: {
        // 监听selection值
        selection: {
            deep: true,
            handler: function (newVal, oldVal) {
                // 获取冗余字段关联关系
                let extraObj = this.$attrs.extraRelations || null
                // 获取当前字段的字段名称
                // 针对模型设计器配置的冗余字段进行逻辑处理，简单理解就是将选择数据的id，text赋给额外的字段最终进行保存
                // 选择数据变更时，更新当前组件值
                let _val = (newVal || []).map(i => i[extraObj[this.name] || this.idKey]).join(GlobalConst.separator)
                // 定义获取额外冗余字段
                let extraField = Get_Extra_Field({
                    selection: newVal,
                    idKey: this.idKey,
                    textKey: this.textKey,
                    extraObj: this.$attrs,
                })
                // 选择数据变更时，通知父组件，更新冗余字段值
                this.$emit('updateExtraField', extraField)
                this.$emit('input', _val)
                // 通知父级：字段change事件
                this.$emit('change', _val, newVal, extraField)
            }
        },
        value: {
            immediate: true,
            handler: function (val) {
                // 根据value与valueText构造已选择的数据进行展示
                if (val) {
                    // 定义id数组
                    let _idList = val.split(GlobalConst.separator)
                    let { valueFieldText, valueFieldId, extraValueObj } = this.$attrs
                    // 如果当前字段名称（name）与保存文本到字段（valueFieldText）一致，idList使用this.$attrs[valueFieldId]
                    if (valueFieldText === this.name) {
                        _idList = (extraValueObj[valueFieldId] || '').split(GlobalConst.separator)
                    }
                    // 定义id对应文本数组
                    let _textList = this.valueText.split(GlobalConst.separator)
                    // 定义结果集
                    let _result = []
                    // 遍历构造已选数组数据
                    _idList.forEach((k, k_index) => {
                        // 获取当前选中
                        // selection 中是否存在相同的项（当非回显时，selection存储的是列表的值，数据比较全）
                        let item = this.selection.find(s => s[this.idKey] === k)
                        let extraValueItem = this.extraSelection.find(s => s[this.idKey] === k)
                        let targetItem = item || extraValueItem || { [this.idKey]: k, [this.textKey]: _textList[k_index] || k }
                        _result.push(targetItem)
                    })
                    // 更新，进行页面展示
                    this.selection = _result
                } else {
                    this.selection = []
                }
            }
        }
    }
}
</script>
<style lang='scss' scoped>
$item-space: 4px;
.bd-dialog-list {
    .bd-dialog-list_edit {
        display: flex;
        .bd-dialog-list_edit--main {
            border-radius: $borderRadius;
            border: 1px solid $inputBorderColor;
            flex: 1;
            .bd-dialog-list_edit--input {
                margin-right: - $item-space;
                margin-bottom: - $item-space;
                padding: $item-space;
                .bd-dialog-list_edit--tip {
                    line-height: $inputHeight - 2 * $item-space;
                    padding-left: 15px - $item-space;
                    color: $placeholderC;
                    font-size: 13px;
                }
                .bd-dialog-list_edit--item {
                    margin-right: $item-space;
                    margin-bottom: $item-space;
                    &.el-tag--small {
                        height: 22px;
                    }
                }
            }
        }
    }
}
</style>