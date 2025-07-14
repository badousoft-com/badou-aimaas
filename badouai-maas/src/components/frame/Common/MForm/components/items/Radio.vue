<template>
    <div :class="selfClass" class="bd-radio">
        <template v-if="!isView">
            <el-radio-group
                v-if="tempOptions.length > 0"
                v-model="tempValue"
                :disabled="disabled">
                <el-radio 
                    v-for="(i, index) in filterExtra(tempOptions, false)" 
                    :key="index"
                    :label="i[idName]"
                    :disabled="i.disabled"
                    @click.native="clickRadioItem($event, i)">
                    {{i[textName]}}
                </el-radio>
                <!-- 其他项 -->
                <el-radio
                    v-if="_hasOther && _otherCustomOption"
                    :label="_otherCustomOption[idName]"
                    :name="name"
                    class="is-extra"
                    @click.native="clickRadioItem($event, i)">
                    <span>{{_otherCustomOption[textName]}}</span>
                    <bd-text
                        v-model="_otherCustomOption[idName]"
                        @input="inputExtra"
                        @focus="focusExtra"
                        :placeholder="'请输入' + _otherCustomOption[textName]"
                        class="d-ib">
                    </bd-text>
                </el-radio>
            </el-radio-group>
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
import getDicText from '@/filter/get-dic-text'
import GlobalConst from '@/service/global-const'
import { Get_Key_Options } from './services/options'
import { Get_Data_By_Path, Has_Value } from '@/utils'
import { Get_Extra_Field } from '@/service/module'
import Text from './Text'
import OtherCustomOption from './mixins/OtherCustomOption'

// 定义options下项的值键名
const Id_Name = GlobalConst.dicOption.idName
// 定义options下项的文本键名
const Text_Name = GlobalConst.dicOption.textName

export default {
    name: 'bd-radio',
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
        // 字段名
        name: {
            type: String,
        },
        // 组件默认值传入
        value: {
            type: [String, Boolean],
            default: ''
        },
        // 设置组件是否可编辑，默认可编辑Boolean(false)，非必须
        disabled: {
            type: Boolean,
            default: false
        },
        options: {
            type: Array,
            default: () => []
        },
        // 传入格式：{ url:'', params: {}, ... }
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
            textName: Text_Name
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
                return this.value
            },
            set (value) {
                this.$emit('input', value)
                // 抛出change事件
                let _currentOption = this.tempOptions.find(i => i.id === value)
                this.$emit('change', value, _currentOption, this.tempOptions)
                // 针对模型设计器配置的冗余字段进行逻辑处理，简单理解就是将选择数据的id，text赋给额外的字段最终进行保存
                this.updateExtraField(value, _currentOption)
            }
        },
    },
    methods: { // 定义函数
        /**
         * 填写其他项的input事件时
         */
        inputExtra () {
            let _valList = this.tempValue.split(GlobalConst.separator)
            // 获取其他项值在选中值中的下角标
            let _index = _valList.findIndex(i => i === this.initOtherCustomOption.initValue)
            // 如果不存在则退出
            if (!~_index) return
            // 获取当前手写输入的其他项值
            let _currentExtraId = this._otherCustomOption[this.idName]
            // 更新前一次值
            this.initOtherCustomOption.initValue = _currentExtraId
            _valList.splice(_index, 1, _currentExtraId)
            this.tempValue = _valList.join(GlobalConst.separator)
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
            if (this.tempValue) {
                // 获取源数据项
                let _optionIds = data.map(i => i[this.idName])
                // 判断组件值是否存在源数据项中不一致的，则需要处理成其他项
                let _extraVal =
                    this.tempValue
                        .split(GlobalConst.separator)
                        .find(i => !_optionIds.includes(i))
                if (_extraVal) {
                    this.initOtherCustomOption[this.idName] = _extraVal
                }
            }
            // 合并其他项
            return [...data, {...this.initOtherCustomOption}]
        },
        /**
         * 选项的点击事件
         * @param {Object} event 点击DOM对象
         * @param {Object} item 点击项对象
         */
        clickRadioItem (event, item) {
            // 点击项之后，这里会被触发两次，一次是input，一次是text，需要过滤掉其中一次，避免多次触发
            if (event.target.tagName === 'INPUT') return
            // 判断当前点击的项是否为已选项，是则取消已选状态
            if (this.tempValue && this.tempValue === item.id) {
                // 设置延时，这里的操作会比html的tempValue赋值快，这里更改后又会被html中的tempValue还原回去，所以设置延时
                setTimeout(() => {
                    // 清空已选值
                    this.tempValue = ''
                }, 100)
            }
        },
        /**
         * 根据请求参数设置option数据
         * @param {Object} request 请求数据对象 { url: '', params: {} }
         */
        setOptionByRequest (request) {
            if (!(request && request.url)) {
                console.error(`Radio组件使用request请求，但缺乏request下的url属性`)
                return
            }
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
        /**
         * 针对模型设计器配置的冗余字段进行逻辑处理，简单理解就是将选择数据的id，text赋给额外的字段最终进行保存
         * @param {Array} value 已选中值
         */
        updateExtraField (value, currentOption) {
            // 定义选中的数组数据
            let selection = []
            // 判断传入值是否存在值
            if (Has_Value(value)) {
                selection = [currentOption]
            }
            // 定义获取额外冗余字段
            let extraField = Get_Extra_Field({
                selection,
                extraObj: this.$attrs
            })
            // 选择数据变更时，通知父组件，更新冗余字段值
            this.$emit('updateExtraField', extraField)
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
        },
    }
}
</script>
<style lang='scss' scoped>
.bd-radio::v-deep {
    .el-radio {
        &.is-extra {
            .el-radio__label {
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