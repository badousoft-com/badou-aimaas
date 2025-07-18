<template>
    <div
        :class="{
            'is-view': isView,
            'h-per-100': fullHeight
        }"
        class="bd-child-module-edit">
        <!-- 编辑表单 -->
        <template v-if="showType === 0">
            <!-- <div class="padding">暂不支持</div> -->
            <component
                :is="componentId"
                ref="moduleEditCode"
                :title="title"
                :noTitle="noTitle"
                :hideBtn="false"
                :isInLink="true"
                :isView="isView"
                btn-placement="top"
                :mdCode="mdCode"
                :defaultParamsObj="defaultParamsObj"
                :addFormData="_addFormData"
                :fullHeight="fullHeight"
                :class="_linkClass"
                :style="!fullHeight && { height: 'auto' }"
                v-on="$listeners">
            </component>
        </template>
        <!-- 模型列表 -->
        <template v-if="showType === 1">
            <module-list-code
                ref="list"
                :title="title"
                :noTitle="noTitle"
                :defaultBtnList="_defaultBtnList"
                :mdCode="mdCode"
                :defaultParamsObj="defaultParamsObj"
                :elseAttrs="elseAttrs"
                :fullHeight="true"
                :class="_linkClass"
                :style="!fullHeight && { height: listHeight }">
            </module-list-code>
        </template>
        <!-- 模型查看 -->
        <template v-if="showType === 2">
            <component
                isView
                :is="componentId"
                ref="moduleEditCode"
                :title="title"
                :noTitle="noTitle"
                :hideBtn="false"
                :isInLink="true"
                :mdCode="mdCode"
                :defaultParamsObj="defaultParamsObj"
                :addFormData="_addFormData"
                :fullHeight="fullHeight"
                :class="_linkClass"
                :style="!fullHeight && { height: 'auto' }">
            </component>
        </template>
        <!-- 自定义页面 -->
        <template v-if="showType === 4">
            <div class="per-100" :id='_key'></div>
        </template>
    </div>
</template>
<script>
import ModuleListCode from '@/components/frame/Module/ModuleListCode'
import Get_Default_Buttons from './button'
import Vue from 'vue'
import router from '@/router'
import store from '@/store'
import { EventBus } from '@/service/event-bus'
import GlobalConst from '@/service/global-const'
export default {
    name: 'child-module-edit',
    inheritAttrs: false,
    components: {
        [ModuleListCode.name]: ModuleListCode,
        // 子组件引入父组件，这里使用动态异步引入
        'module-edit-code': () => import('@/components/frame/Module/ModuleEditCode')
    },
    props: {
        // 专属唯一标识
        index: {
            type: [Number, String]
        },
        // 子tab对象数据
        childData: {
            type: Object
        },
        // 展示类型
        showType: {
            type: Number,
            default: 1
        },
        // 主表单的id
        mainId: {
            type: String
        },
        // 模型编码
        mdCode: {
            type: String
        },
        // 模型名称
        moduleName: {
            type: String,
            default: '子模块'
        },
        // 模块展示名称
        title: {
            type: String
        },
        // 主表字段数据
        mainFieldList: {
            type: Array,
            default: () => []
        },
        // 主表所在作用域
        mainScope: {
            type: Object
        },
        // 主业务模型编码
        mainMdCode: {
            type: String
        },
        // 实体字段名
        entityField: {
            type: String
        },
        // 关联的实体字段名
        relationEntityField: {
            type: String
        },
        // 是否为查看状态
        isView: {
            type: Boolean,
            default: false
        },
        // 是否干掉标题
        noTitle: {
            type: Boolean,
            default: true
        },
        // 是否铺满
        fullHeight: {
            type: Boolean,
            default: true
        }
    },
    data () { // 定义页面变量
        return {
            // 模型列表的高度
            listHeight: null,
            componentId: 'module-edit-code',
            // 表格数据
            data: [],
            eventBus: EventBus,
            // 动态装载的页面作用域
            extendScope: {}
        }
    },
    computed: {
        // 获取关联模块的页面样式类
        _linkClass () {
            return `link__${this.mainMdCode}__${this.mdCode}`
        },
        // 默认按钮列表
        _defaultBtnList () {
            // 20221104:去除按钮过滤的逻辑，将所有按钮传入，内部自己会进行过滤
            return (Get_Default_Buttons(this.isView) || [])
        },
        _key () {
            return `mountId${this.index}`
        },
        // 获取关联字段值
        _entityFieldValue () {
            // 获取字段
            let _field = (this.mainFieldList || []).find(i => i.name === this.entityField)
            if (!_field) {
                let errorTip = `关联字段${this.relationEntityField}在主表中找不到，请检查字段名是否正确,以及在主表中是否存在该字段`
                console.error(errorTip)
                this.$message.warning(errorTip)
                return
            }
            // 返回字段值，作为关联字段的值
            return _field.value
        },
        elseAttrs () {
            return {
                mdCode: this.mdCode, // 模型编码
                mainId: this.mainId, // 主表id
                moduleName: this.moduleName, // 子模块模型名称
                mainFieldList: this.mainFieldList, // 主表字段列表
                mainScope: this.mainScope, // 主表所在作用域
                relationEntityField: this.relationEntityField, // 实体关联名称
                entityFieldValue: this._entityFieldValue, //关联字段具体值
            }
        },
        // 请求默认参数
        defaultParamsObj () {
            let defaultSearchParam = `[{"name":"${this.relationEntityField}","value":"${this._entityFieldValue}","type":"${GlobalConst.searchBar.type}"}]`
            return { defaultSearchParam }
        },
        _addFormData () {
            return {
                [this.relationEntityField]: this._entityFieldValue,
            }
        }
    },
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    async mounted () {
        // 根据设备可视高度，动态计算模型列表的展示高度
        this.listHeight = `${document.body.clientHeight - 200}px`
        // 获取自定义页面【关联关系可配置自定义页面】
        let { pageUrl } = this.childData
        // 存在自定义页面，则加载页面数据挂载到当前模块
        if (pageUrl) {
            let pageVue = await require(`@/views/${pageUrl}`).default
            const Instance = Vue.extend({
                ...pageVue, // 
                router, // 路由模块，必须传入，挂载页面才能使用router模块
                store, // 状态管理库，同上
            })
            // 创建实例，挂载指定DOM
            new Instance({
                // 传递数据给组件
                propsData: {
                    // 主表字段数据
                    mainFieldList: this.mainFieldList,
                    // 主表所在作用域
                    mainScope: this.mainScope,
                    // 主业务模型编码
                    mainMdCode: this.mainMdCode,
                    // 主表id
                    id: this.mainId,
                    // 当前页面作用域
                    scope: this,
                    // 传递给转载页面，获取装载页面内的作用域
                    getExtendScope: (scope) => this.extendScope[this._key] = scope
                }
            }).$mount(`#${this._key}`)
        }
        // 使用Vue事件总线：主要用于当前页面与挂载的自定义之间的数据交互
        EventBus.$on("setData", (res, name) => {
            // 更新数据
            this[name]= res
        })
    }
}
</script>
<style lang='scss' scoped>
.bd-child-module-edit {
    .bd-module-list {
        height: 100%;
    }
}
</style>