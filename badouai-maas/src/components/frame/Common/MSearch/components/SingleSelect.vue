<template>
    <div class="">
        <m-search-option
            :name="label"
            :isShowSearch="isShowSearch"
            :valueText="valueText"
            :isShowClear="_hasOptions && Boolean(showValue) && isAllowClearAll"
            @clear="clear"
            @scrollBottom="scrollBottom">
            <template v-slot:content="scope">
                <div class="m-single-area">
                    <div
                        v-for="item in _options.filter(i => !!i.text && i.text.includes(scope.searchOptionWord))"
                        :key="item.id"
                        class="s-item"
                        :class="{'is-select': item.id === value }"
                        @click="setActive(item, true)">
                        {{item.text}}
                    </div>
                    <div
                        v-show="loading"
                        v-loading="loading"
                        element-loading-spinner="el-icon-loading"
                        class="loading-box">
                    </div>
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
    name: 'search-single-select',
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
        valueText: {
            get: function () {
                return this.getText(this.value) || this.defaultText
            }
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
            if (!val) {
                return ''
            }
            return this.getActiveItem(val).text
        },
        /**
         * @desciption: 设置对象模块为选中状态
         * @param {object} itemObj option对象
         * @param {boolean} isUpdateSearchbar 是否触发父组件的搜索事件
         */
        setActive (itemObj, isUpdateSearchbar = false) {
            // 需要触发父组件searchbar的搜索事件
            if (isUpdateSearchbar) {
                //拼装选择器对象，将旧的选择项和新的选择项以及对应的id传到上一层change事件中，方便做下拉框监听事件
                let selectorObj = {
                    id: this.id,  // 字段名称
                    old: {
                        value: this.value,
                        text: this.valueText
                    },
                    new: {
                        value: itemObj.id,
                        text: itemObj.text
                    }
                }
                this.updateSearchbar(itemObj.id, selectorObj)
            }
        },
        /**
         * @desciption: 根据value值返回对应的option对象模块
         * @param {String} val:val值
         * @return {Object} 匹配val值的option下对象
         */
        getActiveItem (val = '') {
            return this.option.find(i => i.id === val) || false
        },
        // 清空值
        clear () {
            // 更新父组件searchbar值
            this.updateSearchbar()
        },
        // 更新父组件searchbar值以及刷新列表数据
        updateSearchbar (val = '', selectorObj = {}) {
            // 更新父组件值
            this.$emit('input', val)
            // 值更新事件传给父级组件
            this.$emit('change', this.id)
            // 触发父组件请求列表
            this.$emit('search', selectorObj)
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
                // .map(o => {
                //     return { text: o.requestUrl, id: o.id }
                // })
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
    // 可访问当前this实例
    created () {
    },
    // 挂载完成，可访问DOM元素
    mounted () {
        // 如果开启了自动分页
        if (this.autoPagination && this.url) {
            this.initOption()
        }
    },
    watch: {
        value: {
            immediate: true,
            handler: function (newVal, oldVal) {
                if (newVal) {
                    // 对于传入值的处理，只需要做渲染选中的状态，不需要根据值请求列表数据，分析如下：
                    // 1. 初始默认有值时searchbar会直接取默认值进行列表请求，所以这里不需要再次触发，只需要做简单UI处理，根据值设置被选中项的活跃状态
                    // 2. 如果是手动操作了下拉选中值，会在操作事件中触发searchbar请求最新数据，依旧会将最新值传回来，所以这里还是只做接收，不需要触发请求
                    let activeItem = this.getActiveItem(newVal)
                    if (activeItem) {
                        // 设置下拉数据的活跃选中项，false表示不需要触发父组件searchbar的请求数据函数，如果手动点击选择下拉数据时才需要触发
                        this.setActive(activeItem, false)
                    }
                }
            }
        }
    }
}
</script>
<style lang='scss' scoped>
.m-single-area {
    .s-item {
        padding: 4px 4px;
        cursor: pointer;
        
        &.is-select {
            background: $primaryLight;
            color: $primary;
        }
        &:hover {
            background: $primaryLight;
        }
    }
    .loading-box {
        width: 100%;
        height: 36px;
    }
}
</style>