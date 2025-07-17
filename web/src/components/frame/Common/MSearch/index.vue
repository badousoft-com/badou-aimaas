<template>
    <!-- 这里加类p-r设置相对主要是为了做搜索区右下角的插槽位，绝对定位限制在右下角 -->
    <div class="m-search p-r" v-if="dataList.length > 0">
        <div class="s-l" :class="{'isCover': extendList.length === 0}">     
            <div
                class="m-search-item"
                v-for="(i, index) in dataList"
                :key="index"
                :style="getStyle(i)"
                v-if="i.isSelect">
                <component
                    :is="i.componentName"
                    v-bind="i"
                    :ref="i.id"
                    :label="i.name"
                    v-model="i.value"
                    @search="(...argument) => compSearch(i.type, ...argument)"
                    @change="change">
                </component>
            </div>
            <!-- 当搜索栏子项存在时才显示操作按钮 -->
            <div class="d-ib" v-if="dataList.length > 0 && _btnList.length > 0">
                <template v-for="(i, index) in _btnList">
                    <bd-button
                        v-if="!i.hidden"
                        class="marginB"
                        :key="index"
                        v-bind="i"
                        @click="exeMethod(i)">
                    </bd-button>
                </template>
                <slot></slot>
            </div>
            <!-- 右下角固定 -->
            <div class="posi-r-b">
                <slot name="fixedRight"></slot>
            </div>
        </div>
        <div class="s-r" v-if="extendList.length !== 0">
            <el-popover
                placement="bottom-end"
                popper-class="m-popover-checkbox"
                trigger="hover">
                <el-checkbox
                    v-for="(i, index) in extendList" 
                    :key="index"
                    v-model="i.isSelect"
                    @change="changeExtendItem">
                    {{i.name}}
                </el-checkbox>
                <div class="s-label-primary" slot="reference">
                    更多
                    <i class="el-icon-arrow-down"></i>
                </div>
            </el-popover>
        </div>
    </div>
</template>
<script>
import SearchText from './components/Text'
import SearchNumRange from './components/NumRange'
import SearchSingleSelect from './components/SingleSelect'
import SearchMultipleSelect from './components/MultipleSelect'
import SearchDate from './components/Date'
import SearchYear from './components/Year'
import SearchYearMonth from './components/YearMonth'
import SearchCheckbox from './components/Checkbox'
import { otherDic } from '@/views/common/modelDesigner/fieldDic'
import { Show_Status, Merge_Btn } from '@/service/module'
import { Sort_List } from '@/utils/list'
export default {
    name: 'bd-search',
    components: {
        [SearchText.name]: SearchText,
        [SearchNumRange.name]: SearchNumRange,
        [SearchSingleSelect.name]: SearchSingleSelect,
        [SearchMultipleSelect.name]: SearchMultipleSelect,
        [SearchDate.name]: SearchDate,
        [SearchYear.name]: SearchYear,
        [SearchYearMonth.name]: SearchYearMonth,
        [SearchCheckbox.name]: SearchCheckbox,
    },
    data () { // 定义页面变量
        return {
        }
    },
    props: {
        // 搜索栏集合数据
        // [{
        //     id: 字段名称
        //     isMulti: 是否为更多项
        //     name: 字段label
        //     type: 搜索类型
        //     optionUrl: option地址，用于请求option数据（下拉数据）
        //     option: option数据
        // }]
        data: {
            type: Array,
            default: () => []
        },
        // 默认展示文本
        selectValueText: {
            type: String,
            default: "全部"
        },
        // 单选下拉联动事件集合Array
        selectChange: {
            type: Array,
            default: () => []
        },
        btnList: {
            type: Array
        },
        // 列表页面作用域（组件被使用时的的页面作用域）
        listScope: {
            type: Object
        }
    },
    data () {
        return {
            defaultBtnList: [
                { id: 'search', name: '搜索', icon: 'search', type: 'primary', click: this.search },
                { id: 'reset', name: '重置', icon: 'reset', type: 'warning', click: this.resetAndSearch }
            ]
        }
    },
    computed: {
        sortList () {
            return Sort_List(this.data, 'sortIndex')
        },
        // 获取全部搜索项集合（实现排序，展示集合在前，更多集合在后）
        showList () {
            return this.sortList.filter(i => !i.isExtend)
        },
        // 获取更多中搜索项集合
        extendList () {
            return this.sortList.filter(i => i.isExtend)
        },
        dataList () {
            // 通过isHide控制展示
            let temp = this.showList.concat(this.extendList).filter(i => !i.isHide) || []
            let result = []
            temp.forEach(o => {
                let isFlag = result.find(r => r.id === o.id)
                if (isFlag) return
                // 处理组件名，用于component的is属性使用
                o.componentName = otherDic.searchType.find(i => parseInt(i.id) === parseInt(o.type))?.componentName || 'search-text'
                result.push(o)
            })
            return result
        },
        // 按钮
        _btnList () {
            return Merge_Btn(this.defaultBtnList, this.btnList).filter(i => Show_Status(i))
        }
    },
    methods: { // 定义函数
        // 获取样式对象
        getStyle (item) {
            let _result = {}
            if ('width' in item) {
                _result.width = parseFloat(item.width) +
                                (item.width.includes('%') ? '%' : 'px')
            }
            return _result
        },
        /**
         * 搜索组件值变更事件
         * @param {String} fieldName 字段键名
         */
        change (fielaName) {
            this.$emit('change', fielaName)
        },
        // 更多项中选中状态变更函数，主要用于取消选项时，去除该选项数据并且刷新列表
        changeExtendItem (chooseStatus) {
            // 当执行取消更多中的选项时触发以下逻辑，此时chooseStatus值为false：因为取消选中
            if (!chooseStatus) {
                // 由于组件没有提供多余参数，无法知道这个变更的更多项是那一项，所以需要将更多项中没有被选中的全部重置
                this.data.filter(i => i.isExtend && !i.isSelect).forEach(i => {
                    // defaultValue字段为初始值字段，直接使用该值进行重置
                    this.$set(i, 'value', i.defaultValue)
                })
                // 重置值之后，通知请求列表刷新当前页面数据
                this.search()
            }
            // 更多选项的展示会影响搜索组件的高度，需要提示重新检查列表固定高度的设值
            this.$emit('changeSearchPosition')
        },
        /**
         * 组件抛回的search函数，这里单选需要特殊处理
         */
        compSearch () {
            // 获取搜索类型参数 type
            // 获取其他参数 otherParams
            let [type, ...otherParams] = Array.from(arguments)
            if (parseInt(type) === 9) {
                // 处理单选类型
                this.singleSearch(...otherParams)
            } else {
                this.search(...otherParams)
            }
        },
        //下拉框监听事件，用于实现联动
        singleSearch (selectorObj) {
            let { id: id, old: oldSelector, new: newSelector } = selectorObj
            //暴露出自定义的钩子事件。可在js中自定义指定的下拉框监听事件
            if (this.selectChange.length > 0) {
                let clickChange = this.selectChange.find(item => {
                    return item.name === id + 'Change'
                })
                // 问题：clickChange 和 clickChange.click 为空的时候，无法向下 this.search()
                // 解决：添加判断
                if (clickChange && 
                    clickChange.click &&
                    typeof clickChange.click === 'function') {
                    clickChange.click.call(this, oldSelector, newSelector)
                    // 问题：下拉框选中值修改为了 newValue，但是搜索的时候，搜索条件是 oldValue
                    // 解决：将 search() 放在 changeSingleSelect 方法内执行
                    return
                }
            }
            // 没有监听事件的普通下拉框直接执行 search()
            this.search()
        },
        /**
         * 改变单选框属性值
         * @param target[String]        目标ID
         * @param properties[String]    属性名
         * @param val[Object]           期望值
         * @returns {*}
         */
        changeSingleSelect (target, properties, val) {
            if (!target) return
            this.dataList.forEach((item, index) => {
                if (item.id === target) {
                    this.$set(this.dataList[index], properties, val)
                }
            })
            this.search()
        },
        /**
         * 执行动态函数
         */
        exeMethod (obj) {
            // 当click直接为函数体时
            if (typeof obj.click === 'function') {
                obj.click.call(this.listScope, obj)
                return
            }
            // 当click为字符串函数名时
            if (typeof this[obj.click] === 'function') {
                this[obj.click]()
                return
            }
        },
        search () {
            this.$emit('search')
        },
        // 以键值对的形式返回整个搜索栏值
        getSearchVal () {
            if (!(this.dataList && this.dataList.length > 0)) return {}
            return this.dataList.reduce((_obj, i) => {
                _obj[i.id] = i.value
                return _obj
            }, {})
        },
        resetAndSearch () {
            // 日期选项重置提交父组件reset value值之外，还需要额外清除下拉面板中的数据
            this.clearAllDate()
            // 重置搜索条件值
            this.$emit('reset')
        },
        clearAllDate () {
            // 先使用isSelect状态判断是否展示，选出展示的模块进行处理
            let dateList = this.data.filter(i => i.isSelect).
                                    filter(i => i.type === 5)
            try {
                dateList.forEach(i => {
                    let refList = this.$refs[i.id] || []
                    if (refList.length === 0) {
                        this.$message({
                            message: '找不到日期搜索组件，请自行检查',
                            type: 'error'
                        })
                        throw 'end' 
                    }
                    if (refList.length > 1) {
                        this.$message({
                            message: '日期搜索组件存在同名字段，请自行检查',
                            type: 'error'
                        })
                        throw 'end'
                    }
                    if (refList.length === 1) {
                        refList[0].clear()
                    }
                })
            } catch (e) {
                console.log(e)
            }
        },
        // 检测重复id项数据，并给予相应提示
        vaildRepeat () {
            // id 项信息，某id的表单名称
            // 如：name: ['商品名称', 商品id]
            let idInfo = {}
            this.data.forEach(o => {
                idInfo[o.id] = idInfo[o.id] || []
                idInfo[o.id].push(o.name)
            })
            // 存在重复表单id
            let repeatIds = Object.keys(idInfo).filter(key => {
                return idInfo[key].length > 1
            })
            if (repeatIds.length) {
                let msg = ''
                repeatIds.forEach(k => {
                    msg += `<div>【${k}】：${idInfo[k].join('、')}</div>`
                })
                this.$alert(msg, '搜索栏配置出现重复id，请检查数据', {
                    dangerouslyUseHTMLString: true
                })
            }
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
    },
    watch: {
        // 监听搜索框数据长度：截止2022/01/18，主要是为了检测是否存在相同id搜索项
        'dataList.length': {
            deep: true,
            immediate: true,
            handler (newVal) {
                if (newVal) {
                    this.vaildRepeat()
                }
            }
        }
    }
}
</script>
<style lang='scss' scoped>
$moreBtnWidth: 70px;
.m-search {
    font-size: 0;
    .m-search-item {
        display: inline-block;
        width: $searchBarItemWidth;
        vertical-align: top;
        margin-right: $space;
        margin-bottom: $space;
    }
    .s-l {
        display: inline-block;
        vertical-align: top;
        font-size: $font;
        width: calc(100% - #{$moreBtnWidth});
        &.isCover {
            width: 100%;
        }
    }
    .s-r {
        display: inline-block;
        vertical-align: top;
        font-size: $fontS;
        letter-spacing: 2px;
        text-align: right;
        cursor: pointer;
        width: $moreBtnWidth;
    }
    .posi-r-b {
        position: absolute;
        right: 0;
        bottom: 0;
    }
}
</style>