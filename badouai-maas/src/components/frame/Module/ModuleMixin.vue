<template>
    <div class="m-module-mixin h-per-100">
        <template v-for="(i, index) in tempData">
            <div
                :key="index"
                class="m-module-mixin__item box-shadow mar-b b-r defaultBg"
                :style="{height: i.height}">
                <!-- 通用模型列表 -->
                <module-list-code
                    v-if="i.type==='module-list-code'"
                    v-bind="i"
                    @afterModule="(module) => afterModuleMixin(module, index)">
                </module-list-code>
                <!-- 通用模型编辑 -->
                <module-edit-code
                    v-else-if="i.type==='module-edit-code'"
                    v-bind="i"
                    @afterModule="(module) => afterModuleMixin(module, index)"
                    v-on="$listeners">
                </module-edit-code>
                <!-- 自定义页面 -->
                <div
                    v-else-if="i.type==='page'"
                    :ref="getIdSign(index)"
                    :id="getIdSign(index)">
                </div>
            </div>
        </template>
    </div>
</template>
<script>
import Vue from 'vue'
import ModuleListCode from '@/components/frame/Module/ModuleListCode'
import ModuleEditCode from '@/components/frame/Module/ModuleEditCode'
import store from '@/store'
import router from '@/router'
export default {
    name: "module-mixin",
    components: {
        [ModuleListCode.name]: ModuleListCode,
        [ModuleEditCode.name]: ModuleEditCode
    },
    props: {
        data: {
            type: Array,
            default: () => []
        },
        // 每个模块的默认展示高度
        moduleHeight: {
            type: [String, Number],
            default: '500px'
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
        // 配置信息，基于不同模块的特殊配置在此处配置好，用于更新传入数据追加属性
        _setting () {
            return [
                { type: 'module-list-code', noTitle: false, fullHeight: true },
                { type: 'module-edit-code' },
            ]
        },
        tempData: {
            get () {
                // 传入数据与特殊配置项处理属性合并，并返回
                return this.data.map(i => {
                    let _setting = this._setting.find(j => j.type === i.type) || {}
                    return {
                        ...i,
                        ..._setting,
                        // height:每个模块支持传入自己的height
                        // modyleHeight：是所有模块的通用高
                        height: i.height || this.moduleHeight
                    }
                })
            }
        }
    },
    methods: { // 定义函数
        /**
         * listModule数据准备齐全后的回调事件
         * @params {Object} module 模型对象数据
         * @params {Number} index 模块的下角标
         */
        afterModuleMixin (module, index) {
            // 调用父级组件
            this.$emit('afterModuleMixin', module, index)
        },
        /**
         * 根据下角标获取id标识
         */
        getIdSign (index) {
            return `page${index}`
        },
        /**
         * 初始函数：判断是否传入自定义页面，有则动态挂载
         */
        init () {
            this.tempData.forEach(async (i, index) => {
                let idSign = this.getIdSign(index)
                let aimDom = document.getElementById(idSign)
                if (!aimDom) return
                // 获取自定义页面【关联关系可配置自定义页面】
                let {
                    type, // 模型类型
                    pageUrl, // 自定义页面地址
                    getScope, // 获取模块域的方法
                    scope, // 模块域
                    ..._otherAttr // 其他传递给模块的属性
                } = i
                // 存在自定义页面，则加载页面数据挂载到当前模块
                if (!pageUrl) return
                pageUrl = `/${pageUrl}`.replace(/\/\//, '/').replace(/\.vue/, '') + '.vue'
                let pageVue = await require(`@/views${pageUrl}`).default
                const Instance = Vue.extend({
                    ...pageVue,
                    store, // 使加载的组件内支持store的使用
                    router, // 使加载的组件内支持router的使用
                })
                // 创建实例，挂载指定DOM
                new Instance({
                    // 传递数据给组件
                    propsData: {
                        ..._otherAttr, // 传入给组件的属性
                        // 将挂载页面的作用域更新给当前页面变量pageScope,便于使用
                        getScope: getScope || ((scope) => this.pageScope = scope),
                        // 当前页面作用域
                        scope: this,
                    }
                }).$mount(`#${idSign}`)
            })
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        this.init()
    }
}
</script>
<style lang='scss' scoped>
</style>