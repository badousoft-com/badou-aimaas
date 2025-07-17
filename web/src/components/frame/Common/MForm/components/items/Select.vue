<template>
    <div :class="selfClass">
        <template v-if="!isView">
            <el-select
                :ref="refName"
                :disabled="disabled"
                v-model="tempValue" 
                :filterable="filterable"
                :filter-method="_filterMethod"
                :remote="remote"
                :remote-method="remoteMethod"
                :allow-create="allowCreate"
                :multiple="multiple"
                :multiple-limit="limit"
                :collapse-tags="isCollapse"
                :default-first-option="defaultFirstOption"
                :placeholder="_placeholder"
                :clearable="clearable"
                :loading ="loading"
                @change="emitChange"
                @clear="emitClear"
                @blur="emitBlur"
                @focus="emitFocus"
                @visible-change="emitVisibleChange"
                v-touchBottom="touchBottom"
                popper-class="m-form-select-popper">
                <template>
                    <template v-for="(i, index) in tempOptions">
                        <el-option
                            :key="index"
                            v-if="Show_Status(i)"
                            :label="i[textName]"
                            :value="i[idName]"
                            :disabled="i.disabled">
                            <bd-icon v-if="i.icon" :name="i.icon"></bd-icon>
                            <span>{{i[textName]}}</span>
                        </el-option>
                    </template>
                    <i slot="prefix" class="el-input__icon el-icon-loading" v-if="show"></i>
                    <div
                        v-show="loading"
                        v-loading="loading"
                        element-loading-spinner="el-icon-loading"
                        class="loading-box">
                    </div>
                </template>
            </el-select>
        </template>
        <template v-else>
            <bd-icon 
                :name="iconName" 
                v-if="tempValue && iconName" 
                class="fill icon-small">
            </bd-icon>
            <span>{{tempValue | getDicText(options) | completeValue}}</span>
        </template>
    </div>
</template>
<script>
import getDicList from '@/service/get-dic'
import GlobalConst from '@/service/global-const'
import getDicText from '@/filter/get-dic-text'
import { Get_Extra_Field, Show_Status } from '@/service/module'
import { Has_Array_Data } from '@/utils/list'
import { Has_Value, Get_Data_By_Path } from '@/utils'
import { touchBottom } from '@/directive/select-touch-bottom'
import { Get_Key_Options } from './services/options'
import { Deep_Clone } from '@/utils/clone'

// 定义options下项的值键名
const Id_Name = GlobalConst.dicOption.idName
// 定义options下项的文本键名
const Text_Name = GlobalConst.dicOption.textName

export default {
    name: 'bd-select',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    // 组件
    components: {},
    // 过滤器
    filters: {
        getDicText
    },
    // 自定义指令
    directives: {
        touchBottom
    },
    props: {
        // 字段名
        name: {
            type: String,
        },
        // 可能传入值形式 [1,2] 或者 '1,2'
        value: {
            type: [String, Number, Array],
        },
        // 设置组件是否可编辑，默认可编辑Boolean(false)，非必须
        disabled: {
            type: Boolean,
            default: false
        },
        // 是否支持多选
        multiple: {
            type: Boolean,
            default: false
        },
        // 多选时限制选择数， 0则默认不限制
        limit: {
            type: Number,
            default: 0
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
        // 组件下拉数据源，若已知，可直接传入，若需要接口动态请求，请看下一个request参数
        // 数据格式为[{text:'', id:''}]
        // 2022-10-13：需求变更，由于可以通过optionResPath配置获取真正的options数据，所以options支持传入Object
        options: {
            type: [Array, Object],
            default: () => []
        },
        // 当下拉数据来源接口时使用，使用这个则不需要使用options属性
        // 数据格式为｛url: '', params: {}｝
        request: {
            type: Object,
        },
        // 是否开启自动分页功能
        autoPagination: {
            type: Boolean,
            default: false
        },
        // 接口数据地址，若optionResPath为'A,B'，则获取的数据为res[A][B]
        optionResPath: {
            type: String,
            default: ''
        },
        // 响应数据中数据总数字段所在路径，若totalPath为'A,B'，则获取总数数目为res[A][B]
        totalPath: {
            type: String,
            default: 'Total'
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
        // 是否为查看状态
        isView: {
            type: Boolean,
            default: false
        },
        // 是否可搜索
        filterable: {
            type: Boolean,
            default: true
        },
        // 自定义options过滤函数
        // TODO：2022-06-02，在实际调用过程：filterMethod函数中修改options时会导致select输入框中填写内容时需要输入两次才能生效以及一些输入异常
        filterMethod: {
            type: Function
        },
        // （TODO:需要后端支持）是否为远程搜索
        remote: {
            type: Boolean,
            default: false
        },
        // （TODO:需要后端支持）远程搜索方法
        remoteMethod: {
            type: Function,
        },
        // 是否允许用户创建新条目，需配合 filterable 使用
        allowCreate: {
            type: Boolean,
            default: false
        },
        // 在输入框按下回车，选择第一个匹配项。需配合 filterable 或 remote 使用
        defaultFirstOption: {
            type: Boolean,
            default: true
        },
        // 多选时是否以文本折叠展示,默认不折叠false
        isCollapse: {
            type: Boolean,
            default: false
        },
        // 2021-07-13修改为true:默认允许清空，避免单选数据之后无法删除数据
        // 选项是否可清空
        clearable: {
            type: Boolean,
            default: true
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
        show: {
            type: Boolean,
            default: false
        }
    },
    data () { // 定义页面变量
        return {
            // 临时options数据，用于页面使用，避免改动prop报错
            tempOptions: [],
            // 数据加载状态
            loading: false,
            // 全部数据是否已请求完毕
            isDone: false,
            // 分页参数
            pageInfo: {
                perPageSize: GlobalConst.select.perPageSize,
                pageNo: 1
            },
            // 值键
            idName: Id_Name,
            // 文本键
            textName: Text_Name,
            // 历史值列表
            historyValues: [],
            // 判断是否展示的函数
            Show_Status: Show_Status
        }
    },
    computed: {
        // 配置项
        _setting () {
            return {
                name: this.name,
                [Id_Name]: this.idFieldKey,
                [Text_Name]: this.textFieldKey
            }
        },
        // 获取下拉options值的路径；当分页时，则默认路径为Rows，表示res.Rows
        _optionResPath () {
            return this.optionResPath ||
                   this.autoPagination ? 'Rows' : ''
        },
        _filterMethod () {
            return this.filterMethod
            // return this.filterMethod || this.defaultFilterMethod
        },
        refName () {
            if (this.name) return this.name
            return 'bdSelect' 
        },
        // 默认预输入文本
        _placeholder () {
            return this.placeholder || (GlobalConst.form.placeholder.select + this.label)
        },
        // 单项数据流原因，不可直接修改值，需要用临时值做过度使用
        tempValue: {
            get () {
                if (!this.isView) {
                    // 非查看模式
                    if (this.multiple) {
                        // 多选时，组件需要接收值类型为Array，如果值为字符串则需要转化为数组格式
                        if (this.value && !(this.value instanceof Array)) {
                            return (this.value + '').split(GlobalConst.form.valueSeparator)
                        }
                        return this.value || []
                    }
                }
                // 避免value值是Number类型，期望的是String
                let _val = (Has_Value(this.value) && this.value + '') || this.value
                // 完善历史变更值
                this.historyValues.unshift(_val)
                return _val
            },
            set (val) {
                if (this.multiple) {
                    this.$emit('input', val.join(GlobalConst.form.valueSeparator))
                } else {
                    this.$emit('input', val)
                }
            }
        }
    },
    methods: { // 定义函数
        /**
         * TODO:暂未使用：默认的过滤函数，使匹配项排在前面
         * @param {String} val 输入值
         */
        defaultFilterMethod (val) {
            // 定义结果集，包含匹配项与未匹配项
            let result = [[], []]
            this.tempOptions.forEach(i => {
                let _text = i[this.textName] || ''
                // 判断项与输入值是否模糊匹配
                if (~_text.indexOf(val)) {
                    // 匹配则添加进匹配项数组中
                    result[0].push(i)
                } else {
                    // 不匹配则添加进非匹配数组中
                    result[1].push(i)
                }
            })
            // 获取匹配项数组与非匹配项数组
            let [suitList, elseList] = result
            // 更新options
            this.tempOptions = Deep_Clone([...suitList, ...elseList])
        },
        /**
         * 选中值发生变化时触发
         */
        emitChange (value) {
            this.$nextTick(() => {
                let _currentOption = this.tempOptions.find(i => i.id === value)
                this.$emit('change', value, _currentOption, this.tempOptions, {
                    // 旧值
                    oldValue: this.historyValues[1]
                })
                // 针对模型设计器配置的冗余字段进行逻辑处理，简单理解就是将选择数据的id，text赋给额外的字段最终进行保存
                this.updateExtraField(value)
            })
        },
        /**
         * 针对模型设计器配置的冗余字段进行逻辑处理，简单理解就是将选择数据的id，text赋给额外的字段最终进行保存
         * @param {Array} value 已选中值
         */
        updateExtraField (value) {
            // 定义选中的数组数据
            let selection = []
            // 判断传入值是否存在值
            if (Has_Value(value)) {
                // value有两种情况，若当前组件为多选，则['a','b'];若组件单选，则'a'
                // value值需要统一构造为['a','b']格式
                if (value.constructor !== Array) {
                    // value值为'a'，构造为['a']
                    value = (value + '').split(GlobalConst.separator)
                }
                if (Has_Array_Data(value)) {
                    // 根据value值组装完整的id/text属性的对象数组
                    selection = value.map(i => {
                        let _idKey = this.idName
                        let _textKey = this.textName
                        let _item = this.tempOptions.find(j => j[_idKey] === i)
                        return {
                            ..._item,
                            [_idKey]: i,
                            [_textKey]: _item?.[_textKey]
                        }
                    })
                }
            }
            // 定义获取额外冗余字段
            let extraField = Get_Extra_Field({
                selection,
                extraObj: this.$attrs
            })
            // 选择数据变更时，通知父组件，更新冗余字段值
            this.$emit('updateExtraField', extraField)
        },
        /**
         * 下拉框出现/隐藏时触发
         */
        emitVisibleChange (visibleStatus) {
            this.$emit('visible-change', visibleStatus)
        },
        /**
         * 可清空的单选模式下用户点击清空按钮时触发
         */
        emitClear () {
            this.$emit('clear')
        },
        // 当 input 失去焦点时触发
        emitBlur (event) {
            this.$emit('blur', event)
        },
        // 当 input 获得焦点时触发
        emitFocus (event) {
            this.$emit('focus', event)
        },
        /**
         * 使 input 失去焦点，并隐藏下拉框
         */
        blur () {
            this.$refs[this.refName].blur()
        },
        /**
         * 使 input 获取焦点
         */
        focus () {
            this.$refs[this.refName].focus()
        },
        // 滚动条触底函数
        touchBottom () {
            /**
             * 不需要请求的条件
             * 1. 如果没有开启分页
             * 2. 没有传url参数
             * 3. 数据已在请求中
             * 4. 数据已全部加载完毕
             */
            if (!this.autoPagination ||
                !this.request ||
                this.loading ||
                this.isDone) {
                return
            }
            // 页码+1
            this.pageInfo.pageNo ++
            this.initOption()
        },
        /**
         * 根据请求参数设置option数据
         * @param {Object} request 请求数据对象 { url: '', params: {} }
         */
        setOptionByRequest (request) {
            // 定义请求对象
            let _request= {
                params: {},
                method: 'post',
                ...(request || this.request),
            }
            if (!_request.url) {
                console.error(`Select组件使用request请求，但缺乏request下的url属性`)
                return
            }
            // 设置加载loading状态
            this.loading = true
            // 此处分两个情况，常规请求与分页请求
            // 1. 此处为分页请求
            if (this.autoPagination) {
                // 更新请求参数
                _request.params = { ..._request.params, ...this.pageInfo}
                // 请求数据字典列表
                getDicList(_request).then(res => {
                    // 更新数据-总条数
                    let total = parseInt(Get_Data_By_Path(res, this.totalPath)) || 0
                    // 根据传入的路径获取最终的数组数据
                    let data = Get_Data_By_Path(res, this._optionResPath) || []
                    // 是否已请求完成（根据返回回来的数组长度 < 请求的每页数）
                    this.isDone = this.tempOptions.length + data.length >= total
                    // 设置下延时，避免请求响应快时出现loading又消失，导致页面闪动
                    setTimeout(() => {
                        // 更新选项options
                        this.tempOptions = this.tempOptions.concat(Get_Key_Options(data, this._setting))
                        // 关闭加载状态
                        this.loading = false
                    }, 400)
                    
                }).catch(err => {
                    // 关闭加载状态
                    this.loading = false
                    console.error(`Select组件请求数据字典失败：${err}`)
                })
            } else {
                // 2. 常规请求
                getDicList(_request).then(res => {
                    let _res = res || []
                    // 根据传入的路径获取最终的数组数据
                    let data = Get_Data_By_Path(_res, this._optionResPath)
                    // 更新选项options
                    this.tempOptions = Get_Key_Options(data, this._setting)
                }).catch(err => {
                    this.tempOptions = []
                    console.error(`Select组件请求数据字典失败：${err}`)
                }).finally(() => {
                    this.loading = false
                })
            }
        },
        /**
         * 根据option设置option数据
         * @param {Array} option option数据 [{id:'',text:''}]
         */
        setOptionByOption (option) {
            this.tempOptions = option && option.constructor === Array ?
                                    Get_Key_Options(option, this._setting) : []
        },
        // 初始化设置option数据
        initOption () {
            // 判断是否传入请求参数
            if (this.request) {
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
        // 2021-08-10添加：解决回显时如果不显式change值，那么冗余字段赋值事件没触发的问题，导致冗余字段值丢失
        //     所以添加value值的立即监听，有值时执行配置的冗余字段逻辑
        value: {
            immediate: true,
            handler: function (newVal, oldVal) {
                // 需求1：带immediate:true的watch，会优先created触发，所以这里先设置tempOptions，方便后面使用
                // 需求2：需要优先判断tempOptions是否存在值，避免选中值时直接执行下面逻辑反而被options覆盖，而出现选中一次值后tempOptions被清空的问题
                if (!(this.tempOptions && this.tempOptions.length > 0)) {
                    this.tempOptions = this.options || []
                }
                // 处理冗余字段值
                this.updateExtraField(newVal)
            }
        },
        // 监听请求对象是否存在，存在即使用其请求options数据
        request: {
            deep: true,
            handler: function (newVal, oldVal) {
                // 若当前是分页请求，则在请求地址参数变更是需要优先清空已有数据，避免叠加旧数据
                if (this.autoPagination) {
                    this.tempOptions = []
                }
                this.setOptionByRequest(newVal)
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
.loading-box {
    width: 100%;
    height: 36px;
}
</style>