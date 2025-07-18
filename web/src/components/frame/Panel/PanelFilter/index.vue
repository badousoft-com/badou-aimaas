<!--
 * @FilePath: @/components/frame/Panel/PanelFilter.vue
 * @Description: 面板搜索条件
-->
<template>
    <div
        v-if="customFilterList.length || moduleFilterList.length"
        class="panel-filter">
        <!-- 自定义搜索组件 -->
        <div
            v-if="customFilterList.length"
            class="custom-filter_box">
            <component
                v-for="(c, c_index) in customFilterList"
                :key="c_index"
                :is="'f-' + c.filterType"
                :field.sync="customFilterList[c_index]"
                @search="search"
                @reset="resetAndSearch"
                v-on="_listeners"
                class="custom-filter_item">
            </component>
        </div>
        <!-- 使用与列表模型搜索组件 -->
        <m-search
            ref="moduleSearchRef"
            v-if="moduleFilterList.length"
            :data="moduleFilterList"
            :btnList="[]"
            @search="search"
            @reset="resetAndSearch"
            v-on="_listeners"
            class="bd-module-list__search d-ib">
        </m-search>
        <bd-button
            v-for="(i, index) in _btnList"
            :key="index"
            v-bind="i"
            @click="exeMethod(i)"
            class="marginB">
        </bd-button>
    </div>
</template>

<script>
import { Merge_Btn, Show_Status } from '@/service/module'
import MSearch from '@/components/frame/Common/MSearch/index'
import FTag from './expands/FTag'
export default {
    // 禁止父级传入属性作为当前组件的根标签属性
    inheritAttrs: false,
    components: {
        MSearch,
        FTag
    },
    props: {
        // 搜索表单
        filterList: {
            type: Array,
            default: () => []
        },
        // 页面作用域
        scope: {
            type: Object
        },
        // 自定义按钮
        btnList: {
            type: Array
        },
    },
    data: () => ({
        defaultBtnList: [
            { id: 'search', name: '搜索', icon: 'search', type: 'primary', click: 'search' },
            { id: 'reset', name: '重置', icon: 'reset', type: 'warning', click: 'resetAndSearch' }
        ]
    }),
    computed: {
        // 模型搜索条件
        moduleFilterList: {
            get () {
                return this.filterList.filter(f => !f.isCustom && !f.isHide)
            },
            set () {}
        },
        // 自定义搜索条件
        customFilterList: {
            get () {
                return this.filterList.filter(f => f.isCustom && !f.isHide)
            },
            set () {}
        },
        // 按钮
        _btnList () {
            return Merge_Btn(this.defaultBtnList, this.btnList).filter(i => Show_Status(i))
        },
        _listeners () {
            let { search, reset, ...otherEvents } = this.$listeners
            return otherEvents
        }
    },
    methods: {
        exeMethod (obj) {
            // 当click直接为函数体时
            if (typeof obj.click === 'function') {
                obj.click.call(this.scope, obj)
                return
            }
            // 当click为字符串函数名时
            if (typeof this[obj.click] === 'function') {
                this[obj.click]()
                return
            }
        },
        // 搜索
        search () {
            this.$emit('search')
        },
        // 重置并搜索
        resetAndSearch () {
            // 日期选项重置提交父组件reset value值之外，还需要额外清除下拉面板中的数据
            let moduleSearchRef = this.$refs.moduleSearchRef || {}
            if (typeof moduleSearchRef.clearAllDate === 'function') {
                moduleSearchRef.clearAllDate()
            }
            this.filterList.forEach(i => {
                this.$set(i, 'value', i.defaultValue) 
            })
            this.$emit('reset')
        }
    }
}
</script>

<style lang="scss" scoped>
.panel-filter {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    .custom-filter_item {
        vertical-align: top;
        margin-right: $space;
        margin-bottom: $space;
    }
}
</style>