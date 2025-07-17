<template>
    <div class="h-per-100 module-mixin-tab">
        <bd-tabs
            class="h-per-100"
            v-model="activeName"
            :data="data"
            :before-leave="beforeLeave"
            @tab-change="tabChange"
            @tab-click="tabClick">
            <template
                v-for="(i, index) in tempData"
                v-slot:[i.id]>
                <module-mixin
                    :data="i.children"
                    :key="index"
                    @afterModuleMixin="(module, childIndex) => afterModuleMixinTab(module, childIndex, index)">
                </module-mixin>
            </template>
        </bd-tabs>
    </div>
</template>
<script>
import ModuleMixin from './ModuleMixin'
import BdTabs from '@/components/frame/Common/BdTabs'
import { Has_Value } from '@/utils'
export default {
    name: 'module-mixin-tab',
    components: {
        [ModuleMixin.name]: ModuleMixin,
        [BdTabs.name]: BdTabs
    },
    props: {
        data: {
            type: Array,
            default: () => []
        }
    },
    data () {
        return {
            activeName: '',
        }
    },
    computed: {
        tempData () {
            let _data = this.data.map((i, index) => {
                // 更新每个tab的值，若没有传入则使用下角标字符串作为tab id值
                i.id = (Has_Value(i.id) && i.id) || index.toString()
                if (i.fullOnlyOne && i.children?.length === 1) {
                    // fullOnlyOne:true时设置只有一个模块的模块铺满
                    i.children[0].height = '100%'
                }
                // 为模块添加域字段以及获取域的方法
                i.children.forEach(j => {
                    j.scope = null
                    j.getScope = (scope) => j.scope = scope
                })
                return i
            })
            // 设置第一个tab被选中
            this.activeName = _data &&
                              _data.length > 0 &&
                              _data[0]?.id || ''
            return _data
        }
    },
    methods: {
        /**
         * 子组件的listModule获取数据后的回调事件
         * @params {Object} module 模型对象数据
         * @params {Number} childIndex 在tab数据中的下角标
         * @params {Number} tabIndex 模块对应tab的下角标
         */
        afterModuleMixinTab (module, childIndex, tabIndex) {
            // 回调给父级组件
            this.$emit.apply(this, ['afterModuleMixinTab', ...arguments])
        },
        /**
         * tab change事件
         * @params {Object} tab 当前tab对应的对象数据
         * @params {Number} index tab所处在的下角标
         * @params {Object} tabData 所有tab完整的数据
         */
        tabChange (tab, index, tabData) {
            // 回调给父级组件
            this.$emit.apply(this, ['tab-change', ...arguments])
        },
        /**
         * tab click事件
         * @params {Object} tab 当前tab对应的对象数据
         * @params {Number} index tab所处在的下角标
         * @params {Object} tabData 所有tab完整的数据
         * @params {Object} tabVueComponent 被选中的标签 tab 实例
         */
        tabClick (tab, index, tabData, tabVueComponent) {
            // 回调给父级组件
            this.$emit.apply(this, ['tab-click', ...arguments])
        },
        beforeLeave (activeName, oldActiveName) {
            let aim = this.$refs[oldActiveName]
            if (aim && aim.beforeLeave && typeof aim.beforeLeave === 'function') {
                return aim.beforeLeave()
            }
            return true
        },
    },
    created () {
    },
    mounted () {
    }
}
</script>
<style lang="scss" scoped>
.module-mixin-tab::v-deep {
    overflow: auto;
}
</style>