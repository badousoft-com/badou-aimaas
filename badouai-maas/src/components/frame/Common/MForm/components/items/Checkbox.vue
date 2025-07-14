// 组件接收值：
//     disabled: Boolean(false) - 是否可编辑
//     value: String - 值
//     name: String - 字段键名
//     options: Array([{text:'', id:''}]) - 下拉选择数组，对象下text为键，id为值,如果已知数据源则使用这个
//     request: Object({url:'', params: {}}) - options数据源，提供请求的url与params参数，如果需要请求数据则使用这个
<template>
    <div :class="selfClass" class="bd-checkbox">
        <template v-if="!isView">
            <el-checkbox-group
                v-if="tempOptions && tempOptions.length > 0"
                v-model="tempValue" 
                :disabled="disabled">
                <el-checkbox
                    v-for="(i, index) in filterExtra(tempOptions, false)" 
                    :key="index"
                    :label="i[idName]"
                    :name="name">
                    {{i[textName]}}
                </el-checkbox>
                <!-- 其他项 -->
                <el-checkbox
                    v-if="_hasOther && _otherCustomOption"
                    :label="_otherCustomOption[idName]"
                    :name="name"
                    @change="val => checkedOther = val"
                    class="is-extra">
                    <span>{{_otherCustomOption[textName]}}</span>
                    <bd-text
                        v-model="_otherCustomOption[idName]"
                        @input="inputExtra"
                        @focus="focusExtra"
                        :placeholder="'请输入' + _otherCustomOption[textName]"
                        class="d-ib">
                    </bd-text>
                </el-checkbox>
            </el-checkbox-group>
            <span v-else class="form-item-lineHeight fontCS">{{noDataTip}}</span>
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
import { Get_Key_Options } from './services/options'
import { Get_Data_By_Path } from '@/utils'
import { Get_Extra_Field } from '@/service/module'
import Text from './Text'
import OtherCustomOption from './mixins/OtherCustomOption'

// 定义options下项的值键名
const Id_Name = GlobalConst.dicOption.idName
// 定义options下项的文本键名
const Text_Name = GlobalConst.dicOption.textName

export default {
    name: 'bd-checkbox',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    mixins: [OtherCustomOption],
    components: {
        [Text.name]: Text
    },
    filters: {
        getDicText
    },
    props: {
        // 字段键名
        name: {
            type: String,
            default: ''
        },
        // 可能传入值形式 [1,2] 或者 '1,2'
        value: {
            type: [Array, String],
            default: () => []
        },
        // 是否不可编辑
        disabled: {
            type: Boolean,
            default: false
        },
        options: {
            type: Array,
            default: () => []
        },
        // 传入格式：{ url:'', params: {} }
        request: {
            type: Object,
        },
        // 接口数据地址，若optionResPath为'A,B'，则获取的数据为res[A][B]
        optionResPath: {
            type: String,
            default: ''
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
        // 无数据的提示
        noDataTip: {
            type: String,
            default: GlobalConst.form.noDataTip
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
            // 值键
            idName: Id_Name,
            // 文本键
            textName: Text_Name,
            // 是否已勾选其他项
            checkedOther: false,
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
        // 单项数据流原因，不可直接修改值，需要用临时值做过度使用
        tempValue: {
            get () {
                if (!this.isView) {
                    // 这里有个特别处理逻辑，当存在其他项(自定义输入)时,在没输入什么时勾选时值为'',此时也需要被这里处理为[''],而不是[]，所以这里要区分null/undefined 与 ''的区别
                    if (this.value === null || this.value === undefined) {
                        return []
                    }
                    if (this.value instanceof Array) {
                        return this.value
                    }
                    if (!this.value && !this.checkedOther) {
                        return []
                    }
                    if (this.value.constructor === String) {
                        // 组件需要接收值类型为Array，如果值为字符串则需要转化为数组格式
                        return this.value.split(GlobalConst.form.valueSeparator)
                    }
                    return this.value || []
                }
                return this.value
            },
            set (value) {
                // 获取选中值（字符串格式）
                let _val = value.join(GlobalConst.form.valueSeparator)
                this.$emit('input', _val)
                // 获取当前选中项，由于存在多个，所以为数组格式
                let _currentOption = value.map(i => {
                    return this.tempOptions.find(j => j.id === i)
                })
                // 抛出change事件
                this.$emit('change', _val, _currentOption, this.tempOptions)
                // 针对模型设计器配置的冗余字段进行逻辑处理，简单理解就是将选择数据的id，text赋给额外的字段最终进行保存
                this.updateExtraField(_val, _currentOption)
            }
        },
    },
    methods: { // 定义函数
        /**
         * 填写其他项的input事件时
         */
        inputExtra () {
            // 获取其他项值在选中值中的下角标
            let _index = this.tempValue.findIndex(i => i === this.initOtherCustomOption.initValue)
            // 如果不存在则退出
            if (!~_index) return
            // 获取当前手写输入的其他项值
            let _currentExtraId = this._otherCustomOption[this.idName]
            // 更新前一次值
            this.initOtherCustomOption.initValue = _currentExtraId

            // 这里有点异常，使用splice不能触发tempValue的set，只能通过整体赋值
            // 更新组件的最终值
            let _temp = [...this.tempValue]
            _temp.splice(_index, 1, _currentExtraId)
            this.tempValue = _temp
        },
        /**
         * 获取最终的可选项数据
         * @param {Array} data 初始选项数据
         */
        addOtherCustomOption (data) {
            if (!(
                this._hasOther &&
                data &&
                data.length > 0 )) return data
            // 存在其他自定义项时
            if (this.tempValue && this.tempValue.length > 0) {
                // 获取源数据项
                let _optionIds = data.map(i => i[this.idName])
                // 判断组件值是否存在源数据项中不一致的，则需要处理成其他项
                let _extraVal = this.tempValue.find(i => !_optionIds.includes(i))
                if (_extraVal) {
                    this.initOtherCustomOption[this.idName] = _extraVal
                }
            }
            // 合并其他项
            return [...data, {...this.initOtherCustomOption}]
        },
        /**
         * 针对模型设计器配置的冗余字段进行逻辑处理，简单理解就是将选择数据的id，text赋给额外的字段最终进行保存
         * @param {Array} value 已选中值
         */
        updateExtraField (value, currentOption) {
            // 定义获取额外冗余字段
            let extraField = Get_Extra_Field({
                selection: currentOption,
                extraObj: this.$attrs
            })
            // 选择数据变更时，通知父组件，更新冗余字段值
            this.$emit('updateExtraField', extraField)
        },
        /**
         * 根据请求参数设置option数据
         * @param {Object} request 请求数据对象 { url: '', params: {} }
         */
        setOptionByRequest (request) {
            // 请求数据字典列表
            getDicList(request).then(res => {
                let _res = res || []
                // 根据传入的路径获取最终的数组数据
                let data = Get_Data_By_Path(_res, this.optionResPath)
                let _options = data && data.constructor === Array ? Get_Key_Options(data, this._setting) : []
                // 判断是否需要处理其他项
                this.tempOptions = this.addOtherCustomOption(_options)
            }).catch(err => {
                this.tempOptions = []
                console.error(`Radio组件请求数据字典失败：${err}`)
            })
        },
        /**
         * 根据option设置option数据
         * @param {Array} option option数据 [{id:'',text:''}]
         */
        setOptionByOption (option) {
            let _options = option && option.constructor === Array ? Get_Key_Options(option, this._setting) : []
            // 判断是否需要处理其他项
            this.tempOptions = this.addOtherCustomOption(_options)
        },
        // 初始化设置option数据
        initOption () {
            // 判断是否传入请求参数
            if (this.request) {
                this.setOptionByRequest(this.request)
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
        // 监听option请求参数
        request: {
            deep: true,
            handler: function (newVal, oldVal) {
                this.setOptionByRequest(newVal)
            }
        },
        // 监听传入的option数据
        options: {
            deep: true,
            handler: function (newVal, oldVal) {
                this.setOptionByOption(newVal)
            }
        }
    }
}
</script>
<style lang='scss' scoped>
.bd-checkbox::v-deep {
    .el-checkbox {
        &.is-extra {
            .el-checkbox__label {
                .el-input {
                    $height: 24px;
                    input {
                        height: $height;
                        line-height: $height;
                    }
                }
            }
        }
    } 
}
</style>