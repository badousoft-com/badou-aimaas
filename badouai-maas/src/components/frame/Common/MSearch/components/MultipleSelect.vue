<template>
    <div class="multiple-select">
        <m-search-option
            :name="label"
            :isShowSearch="isShowSearch"
            :valueText="valueText"
            :isShowClear="_hasOptions && Boolean(showValue) && isAllowClearAll"
            @clear="clear"
            @scrollBottom="scrollBottom">
            <template v-slot:content="scope">
                <el-checkbox-group
                    v-model="checkList"
                    @change="setActive">
                    <el-checkbox
                        class="d-b"
                        v-for="item in _options.filter(i => i.text.includes(scope.searchOptionWord))"
                        :key="item.id"
                        :title="item.text"
                        :label="item.id">
                        {{item.text}}
                    </el-checkbox>
                </el-checkbox-group>
                <div
                    v-show="loading"
                    v-loading="loading"
                    element-loading-spinner="el-icon-loading"
                    class="loading-box">
                </div>
            </template>
        </m-search-option>
    </div>
</template>
<script>
import MSearchOption from '@/components/frame/Common/MSearchOption'
import { Get_Data_By_Path } from '@/utils/index'
import { request } from '@/service/request'
import GlobalConst from '@/service/global-const'

export default {
    name: 'search-multiple-select',
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
        // 下拉数据源数组
        option: {
            type: Array,
            default: () => []
        },
        // 多选数据的分割符号，用于最终值转换与值还原
        divisionMark: {
            type: String,
            default: ';'
        },
        // 是否允许清空所有已选项：默认显示允许清空按钮
        isAllowClearAll: {
            type: Boolean,
            default: true
        },
        // 是否开启自动分页功能
        autoPagination: {
            type: Boolean,
            default: false
        },
        // 下拉数据源接口地址
        url: {
            type: String,
            default: ''
        },
        // 接口数据地址，若optionResPath为'A,B'，则获取的数据为res[A][B]
        optionResPath: {
            type: String,
            default: ''
        }
    },
    data () { // 定义页面变量
        return {
            checkList: [],
            // 分页参数
            pageInfo: {
                perPageSize: GlobalConst.select.perPageSize,
                pageNo: 1
            },
            loading: false,
            // 自动分页请求回来的下拉数据
            tempOption: [],
            // 全部数据是否已请求完毕
            isDone: false
        }
    },
    computed: {
        valueText () {
            return this.getText(this.value) || this.defaultText
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
        },
        // 是否展示搜索
        isShowSearch () {
            return this._options.length > 0
        },
        // 是否有可选数据
        _hasOptions () {
            return this._options.length > 0
        },
        // 下拉数据源
        _options () {
            if (!this.autoPagination) {
                return this.option
            } else {
                return this.tempOption
            }
        }
    },
    methods: { // 定义函数
        // 文本的展示需要关联value，这样value的变更才会直接影响展示文本内容
        getText (val = '') {
            if (!val) return ''
            return this.getChooseText(this.getChooseValList(val))
        },
        getChooseValList (val) {               
            if (!val) return ''
            return val.split(this.divisionMark)
        },
        getChooseText (valList) {
            if (!valList) return ''
            let textList = []
            valList.forEach(i => {
                this.option.forEach(j => {
                    if (i === j.id) {
                        textList.push(j.text)
                    }
                })
            })
            return textList.join(this.divisionMark)
        },
        // setText (list) {
        //     // 定义选中文本
        //     let textList = []
        //     list.forEach(i => {
        //         this.option.forEach(j => {
        //             if (i === j.id) {
        //                 textList.push(j.text)
        //             }
        //         })
        //     })
        //     // 更新展示文本
        //     this.text = textList.join(this.divisionMark)
        // },
        setActive (val, isUpdateSearchbar = false) {
            let tempValue = val.join(this.divisionMark)
            // this.setText(val)
            // 更新值
            this.updateSearchbar(tempValue)
        },
        // 更新父组件searchbar值以及刷新列表数据
        updateSearchbar (val = '') {
            // 更新父组件值
            this.$emit('input', val)
            // 值更新事件传给父级组件
            this.$emit('change', this.id)
            // 触发父组件请求列表
            this.$emit('search', val)
        },
        // 清空值
        clear () {
            // 重置所有状态
            this.checkList = []
            // 更新父组件searchbar值
            this.updateSearchbar()
        },
        // 请求下拉数据
        initOption () {
            let params = this.pageInfo
            this.loading = true
            request({
                url: this.url,
                method: 'post',
                params,
            }).then(res => {
                this.loading = false
                let data = Get_Data_By_Path(res, this.optionResPath)
                this.tempOption = this.tempOption.concat(data)
                // 是否已请求完成（根据返回回来的数组长度 < 请求的每页数）
                this.isDone = res.Rows.length < this.pageInfo.perPageSize
            }).catch(err => {
                this.loading = false
            })
        },
        // 滚动条触底函数
        scrollBottom () {
            /**
             * 不需要请求的条件
             * 1. 如果没有开启分页
             * 2. 没有传url参数
             * 3. 数据已在请求中
             * 4. 数据已全部加载完毕
             */
            if (!this.autoPagination ||
                !this.url ||
                this.loading ||
                this.isDone) {
                return
            }
            // 页码+1
            this.pageInfo.pageNo ++
            this.initOption()
        }
    },
    mounted () {
        // 如果开启了自动分页
        if (this.autoPagination && this.url) {
            this.initOption()
        }
    },
    watch: {
        value: {
            immediate: true,
            handler: function (newVal) {
                // 这里只处理默认传入值的展示
                if (newVal) {
                    this.checkList = newVal.split(this.divisionMark)
                } else {
                    // 重置所有状态
                    this.checkList = []
                }
            }
        }
    }
}
</script>
<style lang='scss' scoped>
.multiple-select {
    .loading-box {
        width: 100%;
        height: 36px;
    }
}
</style>