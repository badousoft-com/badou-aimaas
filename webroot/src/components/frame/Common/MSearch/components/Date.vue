<template>
    <div class="">
        <m-search-option
            ref="datePopover"
            :name="label"
            :valueText="valueText"
            :isShowClear="Boolean(showValue) && isAllowClearAll"
            width="310"
            trigger="click"
            :useShowMethod="true"
            :isPopoverShow="showPopover">
            <template v-slot:panel>
                <el-radio-group v-model="radioIndex" class="page-m-radio" @change="changeRadioIndex">
                    <!-- 模块一：时间段 -->
                    <el-radio label="1">
                        <el-date-picker
                            v-model="timeRange"
                            value-format="yyyy-MM-dd"
                            type="daterange"
                            align="right"
                            unlink-panels
                            range-separator="至"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            :picker-options="pickerOptions"
                            @focus="focus($event, '1')"
                            @change="updateValue">
                        </el-date-picker>
                    </el-radio>
                    <!-- 模块2：最近几天内 -->
                    <el-radio label="2">
                        {{agoObj.frontText}}
                        <el-input 
                            class="num-input" 
                            v-model="agoObj.value"
                            @focus="focus($event, '2')"
                            @input="updateValue"
                            @change="search">
                        </el-input>
                        <el-dropdown @command="handleCommand">
                            <span class="el-dropdown-link">
                                {{showTimeText(agoObj.type)}}
                                <i class="el-icon-arrow-down el-icon--right"></i>
                            </span>
                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item
                                    v-for="(i, index) in dateTypeList"
                                    :key="index"
                                    :command="{ objName: 'agoObj', id: i.id }">
                                    {{i.text}}
                                </el-dropdown-item>
                            </el-dropdown-menu>
                        </el-dropdown>
                        {{agoObj.endText}}
                    </el-radio>
                    <!-- 模块3：超过几天之前的 -->
                    <el-radio label="3">
                        {{passObj.frontText}}
                        <el-input 
                            class="num-input" 
                            v-model="passObj.value"
                            @focus="focus($event, '3')"
                            @input="updateValue"
                            @change="search">
                        </el-input>
                        <el-dropdown @command="handleCommand">
                            <span class="el-dropdown-link">
                                {{showTimeText(passObj.type)}}
                                <i class="el-icon-arrow-down el-icon--right"></i>
                            </span>
                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item
                                    v-for="(i, index) in dateTypeList"
                                    :key="index"
                                    :command="{ objName: 'passObj', id: i.id }">
                                    {{i.text}}
                                </el-dropdown-item>
                            </el-dropdown-menu>
                        </el-dropdown>
                        {{passObj.endText}}
                    </el-radio>
                </el-radio-group>
                <!-- 操作区域 -->
                <el-button 
                    v-btnBg="i"
                    v-for="(i, index) in btnList"
                    :key="index"
                    :disabled="!_hasValue"
                    @click="exeMethod(i)">
                    <bd-icon :name="i.icon"></bd-icon>{{i.name}}
                </el-button>
            </template>
        </m-search-option>
    </div>
</template>
<script>
import MSearchOption from '@/components/frame/Common/MSearchOption'
import { Has_Value } from '@/utils/index'

const dateTypeList = [
    { id: "d", text: "天" },
    { id: "w", text: "周" },
    { id: "month", text: "个月" },
    { id: "y", text: "年" }
]
export default {
    name: 'search-date',
    components: {
        MSearchOption
    },
    props: {
        // label标签字段
        label: {
            type: String,
            default: ''
        },
        // 字段名称
        id: {
            type: String,
            default: ''
        },
        // 字段值
        value: {
            default: ''
        },
        // 值对应显示文本
        defaultText: {
            type: String,
            default: '全部'
        },
        // 是否允许清空所有已选项：默认显示允许清空按钮
        isAllowClearAll: {
            type: Boolean,
            default: true
        }
    },
    data () { // 定义页面变量
        return {
            // 展示文本
            text: '',
            // elementUI 时间段选择器快捷输入
            pickerOptions: {
                shortcuts: [{
                    text: '最近一周',
                    onClick (picker) {
                        const end = new Date()
                        const start = new Date()
                        start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
                        picker.$emit('pick', [start, end])
                    }
                }, {
                    text: '最近一个月',
                    onClick (picker) {
                        const end = new Date()
                        const start = new Date()
                        start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
                        picker.$emit('pick', [start, end])
                    }
                }, {
                    text: '最近三个月',
                    onClick (picker) {
                        const end = new Date()
                        const start = new Date()
                        start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
                        picker.$emit('pick', [start, end])
                    }
                }]
            },
            dateTypeList: dateTypeList,
            // 展示整块时间选择框状态
            showPopover: false,
            // 操作按钮数据
            btnList: [
                { id: 'search', name: '搜索', icon: 'search', method: 'search' }
            ],
            // 默认选中项
            radioIndex: '',
            // 时间选择格式1-时间段
            timeRange: [],
            // 时间选择格式2-在过去的时间对象
            agoObj: {
                frontText: '在过去的',
                value: '',
                type: dateTypeList[0].id,
                endText: '之内'
            },
            // 时间选择格式3-超过的时间对象
            passObj: {
                frontText: '超过',
                value: '',
                type: dateTypeList[0].id,
                endText: '之前'
            }
        }
    },
    computed: {
        // 判断所选时间类型下是否有值
        _hasValue () {
            let hasValueStatus = false
            switch (this.radioIndex) {
                case '1':
                    // 时间短值为数组，通过判断数组长度确定值是否存在
                    hasValueStatus = this.timeRange && this.timeRange.length > 0
                    break
                case '2':
                    // Has_Value是引入的工具类函数，判断是否有值
                    hasValueStatus = Has_Value(this.agoObj.value)
                    break
                case '3':
                    // Has_Value是引入的工具类函数，判断是否有值
                    hasValueStatus = Has_Value(this.passObj.value)
                    break
                default:
                    // do something
            }
            return hasValueStatus
        },
        // 显示文本
        valueText () {
            return this.text || this.defaultText
        },
        // 页面展示value
        showValue: {
            get: function () {
                // 获取父级组件传入值
                return this.value
            },
            set: function (val) {
                // 调用父级update事件进行value值更新
                this.$emit('input', val)
                // 值更新事件传给父级组件
                this.$emit('change', this.id)
            }
        }
    },
    methods: { // 定义函数
        clear () {
            // 清空展示文本
            this.text = ''
            // 清空数据1-时间段
            this.timeRange = []
            // 清空数据2-在过去的时间对象
            this.$set(this.agoObj, 'value', '')
            this.$set(this.agoObj, 'type', dateTypeList[0].id)
            // 清空数据3-超过的时间对象
            this.$set(this.passObj, 'value', '')
            this.$set(this.passObj, 'type', dateTypeList[0].id)
            // 清空选中状态
            this.radioIndex = ''
        },
        /**
         * 根据所选时间类型，更新当前组件值与文本展示值
         * @params [String] index 所选时间类型，目前支持'1','2''3'
         */
        changeRadioIndex (index) {
            this.updateValue()
        },
        // 更新当前组件值与展示文本值
        updateValue () {
            switch (this.radioIndex) {
                case '1':
                    if (this.timeRange && this.timeRange.length > 0) {
                        this.showValue = { "dateType": "0", "startTime": this.timeRange[0], "endTime": this.timeRange[1] }
                        this.text = `${this.timeRange[0]}至${this.timeRange[1]}`
                    } else {
                        this.showValue = ''
                        this.text = ''
                    }
                    break
                case '2':
                    if (this.agoObj.value) {
                        this.showValue = { "dateType": "1", "afterTime": this.agoObj.value, "measurement": this.agoObj.type }
                        this.text = this.getValueText('agoObj')
                    } else {
                        this.showValue = ''
                        this.text = ''
                    }
                    break
                case '3':
                    if (this.passObj.value) {
                        this.showValue = { "dateType": "2", "beforeTime": this.passObj.value, "measurement": this.passObj.type }
                        this.text = this.getValueText('passObj')
                    } else {
                        this.showValue = ''
                        this.text = ''
                    }
                    break
                default:
                    // do something
            }
        },
        // 生成展示文本
        getValueText (objName) {
            return this[objName].frontText +
                    this[objName].value +
                    this.showTimeText(this[objName].type) +
                    this[objName].endText
        },
        // 根据日期单位类型（天，月，年等），返回对应文本
        showTimeText (typeCode) {
            return this.dateTypeList.find(i => i.id === typeCode).text
        },
        exeMethod (obj) {
            this[obj.method]()
        },
        // 切换日期单位类型（天，月，年）时更新值与展示文本
        handleCommand ({objName, id}) {
            // 更新对应日期类型下的日期单位类型数据
            this[objName].type = id
            // 更新值与显示文本
            this.updateValue()
            // 若切换日期单位类型（天，月，年等）时，该类型已有值，则立即触发search请求
            if (this[objName].value) {
                this.search()
            }
        },
        // 点击日期类型下的相关input时，切换当前日期类型为选中状态
        focus (event, radioIndex) {
            this.radioIndex = radioIndex
        },
        search () {
            // 隐藏时间选择面板
            this.$refs.datePopover.temp_isPopoverShow = false
            this.$emit('search')
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
.page-m-radio >>> {
    .el-radio {
        display: block;
        margin-bottom: 12px;
        .num-input {
            width: 40px;
        }
        .el-date-editor--daterange.el-input__inner {
            width: 260px;
        }
        .el-radio__label,
        .el-dropdown-link {
            font-size: $fontS !important;
        }
    }
}
</style>