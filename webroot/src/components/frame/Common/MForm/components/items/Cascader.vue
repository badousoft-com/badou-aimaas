<template>
    <div :class="selfClass">
        <template v-if="!isView">
            <el-cascader
                ref="cascaderRef"
                v-model="tempValue"
                :disabled="disabled"
                :placeholder="_placeholder"
                :options="tempOptions"
                :separator="showSeparator"
                :props="_attrProps"
                :clearable="clearable"
                v-bind="$attrs"
                @change="change"
                @expand-change="expandChange"
                @blur="blur"
                @focus="focus"
                @visible-change="visibleChange"
                @remove-tag="removeTag">
            </el-cascader>
            <!-- v-on="$listeners"这里不能使用，内置的@input会v-model值，导致异常 -->
        </template>
        <template v-else>
            <bd-icon 
                :name="iconName" 
                v-if="tempValue && iconName" 
                class="fill icon-small">
            </bd-icon>
            <span>{{tempValue | completeValue}}</span>
        </template>
    </div>
</template>
<script>
import GlobalConst from '@/service/global-const'
import { Get_Data_By_Path } from '@/utils'
import { Deep_Clone } from '@/utils/clone'
import { Get_Extra_Field } from '@/service/module'
// 定义options下项的值键名
const Id_Name = GlobalConst.cascader.id
// 定义options下项的文本键名
const Text_Name = GlobalConst.cascader.text
// 定义options下项的子节点children键名
const Children_Name = GlobalConst.cascader.children
export default {
    name: 'bd-cascader',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {},
    props: {
        // 设置组件是否可编辑，默认可编辑Boolean(false)，非必须
        disabled: {
            type: Boolean,
            default: false
        },
        // 组件默认值传入
        value: {
            type: String,
            default: ''
        },
        // 单个值中父级与子级的连接符
        linkSeparator: {
            type: String,
            default: GlobalConst.cascader.separator
        },
        // 展示分割符
        showSeparator: {
            type: String,
            default: GlobalConst.cascader.showSeparator
        },
        // 标签名称
        label: {
            type: String
        },
        // 预输入文本
        placeholder: {
            type: String,
            default: GlobalConst.form.placeholder.select
        },
        // 是否支持多选
        multiple: {
            type: Boolean,
            default: false
        },
        // 可选项数据源
        options: {
            type: Array,
            default: () => []
        },
        // 当下拉数据来源接口时使用，使用这个则不需要使用options属性
        // 数据格式为｛url: '', params: {}｝
        request: {
            type: Object,
        },
        // 获取options的请求地址url，优先级高于request中的url
        optionsUrl: {
            type: String,
        },
        // 接口数据地址，若optionResPath为'A,B'，则获取的数据为res[A][B]
        optionResPath: {
            type: String,
            default: ''
        },
        // 配置选项
        props: {
            type: Object,
        },
        // 指定options下的值字段键名，默认为id
        idFieldKey: {
            type: String,
            default: Id_Name
        },
        // 指定options下的文本展示的字段键名，默认为text
        textFieldKey: {
            type: String,
            default: Text_Name
        },
        // 指定options下的子节点数据的字段键名，默认为children
        childrenFieldKey: {
            type: String,
            default: Children_Name
        },
        // 选项弹窗触发方式
        trigger: {
            type: String,
            default: 'click'
        },
        // 是否严格的遵守父子节点不互相关联(允许选择任意一级)
        // 简单理解：
        //     设置false:选中父亲则所有子节点被勾选； 
        //     设置true则取消该关联，选中父亲后子节点不会被勾选
        //     设置true则即使所有子节点被选中，父亲也不会被勾选
        checkStrictly: {
            type: Boolean,
            default: false
        },
        // 选中节点只返回节点值，不携带其父级节点数据
        onlyLeaf: {
            type: Boolean,
            default: false
        },
        // TODO：关联键-键名
        relatedField: {
            type: [String, Boolean],
            default: false
        },
        // change事件
        handleChange: {
            type: Function
        },
        // 是否支持清空选项
        clearable: {
            type: Boolean,
            default: true
        },
        // 输入框中是否显示选中值的完整路径
        showAllLevels: {
            type: Boolean,
            default: true
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
        }
    },
    data () { // 定义页面变量
        return {
            // 临时options数据，用于页面使用，避免改动prop报错
            tempOptions: [],
        }
    },
    computed: {
        // 获取请求对象
        _request () {
            if (!this.request && !this.optionsUrl) return null
            return {
                ...this.request,
                url: this.optionsUrl || this.request.url 
            }
        },
        // cascader的props配置项
        _attrProps () {
            return {
                ...this.props,
                // 是否多选
                multiple: this.multiple,
                // 次级菜单的展开方式
                expandTrigger: this.trigger,
                // 指定选项标签为选项对象的某个属性值
                label: this.textFieldKey,
                // 指定选项的值为选项对象的某个属性值
                value: this.idFieldKey,
                // 指定选项的子选项为选项对象的某个属性值
                children: this.childrenFieldKey,
                // 是否严格的遵守父子节点不互相关联
                checkStrictly: this.checkStrictly
            }
        },
        // 预输入文本
        _placeholder () {
            return this.placeholder || (GlobalConst.form.placeholder.select + this.label)
        },
        // 单项数据流原因，不可直接修改值，需要用临时值做过度使用
        tempValue: {
            get () {
                if (!this.isView) {
                    if (this.value && this.value.constructor === String) {
                        if (!this.multiple) {
                            // 单选
                            if (this.onlyLeaf) {
                                // 获取节点及父辈节点
                                return this.getParentTree(this.value, this.tempOptions)
                            }
                            return this.value.split(this.linkSeparator)
                        } else {
                            // 多选
                            return this.value.split(GlobalConst.form.valueSeparator)
                                                .map(i => {
                                                return this.onlyLeaf ?
                                                        this.getParentTree(i, this.tempOptions) :
                                                        i.split(this.linkSeparator)
                            })
                        }
                    }
                    return []
                }
                // 查看状态下直接使用该值进行展示
                return this.value
            },
            set (val) {
                // 单选时返回的是['A', 'A1', 'A2']
                // 多选时返回的是[['A', 'A1', 'A2'], ['B', 'B1', 'B2']]
                this.$emit('input', this.getResultVal(val))
            }
        }
    },
    methods: { // 定义函数
        /**
         * 根据组件值获取最终需要的值
         * @param {Array} val 组件值
         */
        getResultVal (val) {
            // 单选时返回的是['A', 'A1', 'A2']
            // 多选时返回的是[['A', 'A1', 'A2'], ['B', 'B1', 'B2']]

            // 这里要注意不能修改val的值，因为其代表的是指组件的值，通过地址引用可能会不小心修改到导致逻辑异常
            let result = []
            let _val = Deep_Clone(val)
            if (!this.multiple) {
                // 单选
                result = this.onlyLeaf? [_val.pop() || ''] : [_val.join(this.linkSeparator)]
            } else {
                // 多选
                _val.forEach(i => {
                    result.push(this.onlyLeaf? (i.pop() || '') : i.join(this.linkSeparator))
                })
            }
            return result.join(GlobalConst.form.valueSeparator)
        },
        /**
         * 获取结果值对应的label
         * @param {String} 值，格式为'440000-440500-440511,440000-440500-440512'
         * @returns {String} 对应的标签值，格式为"，格式为'广东省-汕头市-金平区,广东省-汕头市-濠江区'
         */
        getResultLable (value) {
            if (!value) return ''
            let _valArr = value.split(GlobalConst.form.valueSeparator)
            let _res = []
            _valArr.forEach(i => {
                // 深拷贝数据，避免地址引用修改到原数据
                let _tempOptions = Deep_Clone(this.tempOptions)
                let _itemRes = []
                i.split(this.linkSeparator).forEach(j => {
                    // 获取label值，children值
                    let { 
                        [this._attrProps.label]: _label,
                        [this._attrProps.children]: _children
                    } = _tempOptions.find(k => k[this._attrProps.value] === j)
                    // 更新_tempOptions，用于子级数据的查找
                    _tempOptions = _children
                    // 添加当前级的标签
                    _itemRes.push(_label)
                })
                // 组装单个数据
                _res.push(_itemRes.join(this.linkSeparator))
            })
            // 组装所有数据
            return _res.join(GlobalConst.form.valueSeparator)
        },
        /**
         * 根据传入节点值获取从祖辈到该节点的数据并返回
         * @param {String} 节点value值
         * @param {Array} 树数据
         * @param {Array} 父辈到当前节点值的数据
         * @returns {Array} 返回祖辈到当前节点值的连接点数据
         */
        getParentTree (val, data, prev = []) {              
            if (!val) return []
            // 定义结果集
            let _result = null
            // 获取数据长度
            let _len = data.length
            // 遍历数据
            for (let index = 0; index < _len; index++) {
                // 获取每项数据
                let i = data[index]
                // 判断是否与传入值匹配
                if (i.value === val) {
                    // 匹配则更新结果集，返回结果
                    _result = [...prev, i.value]
                    break
                } else {
                    // 如果不匹配，则判断节点下是否有children，继续向下匹配
                    if (this.childrenFieldKey in i &&
                        i[this.childrenFieldKey] &&
                        i[this.childrenFieldKey].length > 0) {
                        let _value = this.getParentTree(val, i.children, [...prev, i.value])
                        // 注意这里如果当有值的时候才赋值，不然会导致所有情况都会赋值，那么正确答案到时也会被覆盖
                        _value && (_result = _value)
                    }
                }
            }
            // 返回结果
            return _result
        },
        // 获取选择值结果
        getResultInfo () {
            let selectNodes = this.$refs.cascaderRef.getCheckedNodes()
            console.log(selectNodes)
            let labels = [], values = []
            for (let n_index = 0; n_index < selectNodes.length; n_index++) {
                let { label, value, path, pathLabels } = selectNodes[n_index]
                if (this.onlyLeaf) { // 如果只取子节点
                    labels.push(label)
                    values.push(value)
                } else {  // 需要关联父节点
                    labels.push(pathLabels.join(this.linkSeparator))
                    values.push(path.join(this.linkSeparator))
                }
            }
            return {
                label: labels.join(GlobalConst.form.valueSeparator),
                value: values.join(GlobalConst.form.valueSeparator),
            }
        },
        /**
         * change事件
         * @param {Array} value 值
         */
        change (value) {
            // let _result = this.getResultVal(value)
            // let _label = this.getResultLable(_result)
            this.$nextTick(() => {
                // 获取选择数据对应的文本、值
                let { label: _label, value: _result } = this.getResultInfo()
                // 判断是否存在自定义change事件，存在则使用，不存在则调用默认的change事件
                if (this.handleChange && typeof this.handleChange === 'function') {
                    this.handleChange(_result, value, this.tempOptions, _label)
                } else {
                    // _result: 结果值
                    // value: 组件传回值
                    this.$emit('change', _result, value, this.tempOptions, _label)
                }
                let selectNodes = this.$refs.cascaderRef.getCheckedNodes()
                let { name, extraRelations = {}, valueFieldText } = this.$attrs
                if (name) delete extraRelations[name]
                if (this.textFieldKey) extraRelations[valueFieldText] = this.textFieldKey
                let selection = selectNodes.map(o => {
                    let item = JSON.parse(JSON.stringify(o.data))
                    if (!this.onlyLeaf) {
                        item[this.textFieldKey] = o.pathLabels.join(this.linkSeparator)
                        item[this.idFieldKey] = o.path.join(this.linkSeparator)
                    }
                    return item
                })
                // 定义获取额外冗余字段
                let extraField = Get_Extra_Field({
                    selection,
                    idKey: this.idFieldKey,
                    textKey: this.textFieldKey,
                    data: [_result, _label],
                    extraObj: {
                        ...this.$attrs,
                        extraRelations
                    }
                })
                // 选择数据变更时，通知父组件，更新冗余字段值
                this.$emit('updateExtraField', extraField)

            })
        },
        /**
         * 当展开节点发生变化时触发
         * @param {Array} parentData 各父级选项值组成的数组
         */
        expandChange (parentData) {
            this.$emit('expand-change', parentData)
        },
        /**
         * 当失去焦点时触发
         * @param {Event} event
         */
        blur (event) {
            this.$emit('blur', event)
        },
        /**
         * 当获得焦点时触发
         * @param {Event} event
         */
        focus (event) {
            this.$emit('focus', event)
        },
        /**
         * 下拉框出现/隐藏时触发
         * @param {Boolean} visibleStatus
         */
        visibleChange (visibleStatus) {
            this.$emit('visible-change', visibleStatus)
        },
        /**
         * 在多选模式下，移除Tag时触发
         * @param {*} removeVal
         */
        removeTag (removeVal) {
            this.$emit('remove-tag', removeVal)
        },
        /**
         * 根据请求参数设置option数据
         */
        setOptionByRequest () {
            // 定义请求对象
            if (!this._request?.url) {
                console.error(`Cascader组件使用request请求，但缺乏request下的url属性`)
                this.tempOptions = []
                return
            }
            // 2. 常规请求
            this.post(this.BASEURL + this._request.url, this._request.params).then(res => {
                let _res = res || []
                // 根据传入的路径获取最终的数组数据
                _res = Get_Data_By_Path(_res, this.optionResPath)
                if (_res && _res.constructor === Array) {
                    this.tempOptions = _res
                } else {
                    console.error(`Cascader组件获取的options不是Array格式，请确认是否需要配合optionResPath使用`)
                    this.tempOptions = []
                }
                    
            }).catch(err => {
                this.tempOptions = []
                console.error(`Select组件请求数据字典失败：${err}`)
            })
            
        },
        /**
         * 根据option设置option数据
         * @param {Array} option option数据 [{id:'',text:''}]
         */
        setOptionByOption (option) {
            this.tempOptions = option
        },
        // 初始化设置option数据
        initOption () {
            // 判断是否传入请求参数
            if (this._request) {
                this.setOptionByRequest()
            } else {
                this.setOptionByOption(this.options)
            }
        },
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        if (!this.isView) {
            // 初始化设置option数据
            this.initOption()
        }
    },
    watch: {
        // 监听值变化，设置关联字段值
        // tempValue: {
        //     immediate: true,
        //     handler: function (newVal, oldVal) {
        //         if (newVal) {
        //             if (this.relatedField) {
        //                 // 当前存在关联键值或者关联键字段boolean为true时，回调事件
        //                 // this.relatedField: [Boolean, String],存在值时格式为'aa-bb-cc'
        //                 // newVal: [Array] eg:['11','22', '33']
        //                 this.$emit('setRelatedField', this.relatedField, newVal)
        //             }
        //         }
        //     }
        // },
        // 监听请求对象是否存在，存在即使用其请求options数据
        _request: {
            deep: true,
            handler: function (newVal, oldVal) {
                // 存在一种情况，newVal与oldVal均为null，但此时也进入了watch，此时不需要执行setOptionByRequest函数
                if (newVal === oldVal) return
                this.setOptionByRequest()
            }
        },
        options: {
            deep: true,
            handler: function (newVal, oldVal) {
                this.setOptionByOption(newVal)
            }
        },
    }
}
</script>
<style lang='scss' scoped>

</style>