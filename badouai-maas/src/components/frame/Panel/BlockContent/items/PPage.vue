<!--
 * @FilePath: @/components/frame/Panel/BlockContent/items/PPage.vue
 * @Description: 面板 - 页面
-->
<template>
    <div
        v-if="!isFail"
        :class="{'module-page': moduleCom}"
        class="p-page">
        <template v-if="type">
            <!-- 引入自定义页面 -->
            <div v-if="!mdCode" :id="comId"></div>
            <!-- 引入模型页面 -->
            <template v-else>
                <component
                    class="wh100"
                    :is="moduleCom"
                    :mdCode="mdCode"
                    v-bind="moduleAttrs"
                    :permissionBtnList="permissionBtnList"
                    fullHeight>
                </component>
            </template>
        </template>
        <template v-else>
            <iframe
                class="wh100"
                :src="pageURL"
                frameborder="0">
            </iframe>
        </template>
    </div>
    <no-data v-else height="50%"></no-data>
</template>

<script>
import Vue from 'vue'
import NoData from '@/components/frame/NoData'
import ModuleListCode from '@/components/frame/Module/ModuleListCode'
import ModuleEditCode from '@/components/frame/Module/ModuleEditCode'
import ModuleListTree from '@/components/frame/Module/ModuleListTree/index.vue'
import ModuleEditTree from '@/components/frame/Module/ModuleEditTree/index.vue'
import ButtonPermissions from '@/views/module/mixins/ButtonPermissions.vue'
import { Get_Default_SearchParam } from '@/service/module'
import router from '@/router'
import store from '@/store'
export default {
    name: 'p-page',
    mixins: [ButtonPermissions],
    components: {
        NoData,
        ModuleListCode,
        ModuleEditCode,
        ModuleListTree,
        ModuleEditTree
    },
    inheritAttrs: false,
    props: {
        // 引用的页面路劲
        pageURL: {
            type: String,
            default: ''
        },
        // 剩余属性变量
        otherAttrs: {
            type: Object,
            default: () => {}
        },
    },
    data () {
        return {
            // 引用的页面类型（1：项目内部页面，0：外部链接）
            type: 0,
            // 使用内部组件的页面id
            comId: '',
            mdCode: '',
            modulePath: '',
            // 是否获取页面失败
            isFail: false,
            pageInstance: null,
        }
    },
    computed: {
        vRouterPath () {
            return this.pageURL
        },
        // 模型组件名称
        moduleCom () {
            // 判断是否为模型列表、查看页面
            if (this.modulePath) {
                let modulePathDic = {
                    '/module/stander/list': 'module-list-code',
                    '/module/stander/edit': 'module-edit-code',
                    '/module/tree/list': 'module-list-tree',
                    '/module/tree/edit': 'module-edit-tree'
                }
                return modulePathDic[this.modulePath]
            }
            return ''
        },
        // 传递给模型的属性
        moduleAttrs () {
            let url_info = this.pageURL.split('?')
            // 将路径中所含的参数传递给组件
            let temp_params = url_info[1] || ''
            let params = {}
            temp_params.split('&').forEach(p => {
                if (p) {
                    let p_arr = p.split('=')
                    params[p_arr[0]] = p_arr[1]
                }
            })
            let query = Get_Default_SearchParam(params)
            return {
                defaultParamsObj: query ?
                    { defaultSearchParam: query['defaultSearchParam']} : {},
                searchParam: query?.searchParam || {}
            }
        },
        // 最终使用主题
        themeInfo () {
            let themeAttrs = this.otherAttrs.theme || {}
            let customTheme = this?.customSetting || {}
            return {
                ...themeAttrs,
                ...customTheme
            }
        },
    },
    methods: {
        // 初始化模型参数
        initModuleParams () {
            let arr = this.pageURL.split('/')
            this.modulePath = arr.slice(0, -2).join('/')
            this.mdCode = arr[arr.length - 2]
        },
        async init () {
            // 外部链接0/内部页面1
            this.type = this.pageURL.indexOf('http') === 0 ? 0 : 1
            if (this.type && this.pageURL) { // 内部组件
                // 判断是否为模型列表、查看页面
                if (this.pageURL.indexOf('/module/') === 0) {
                    this.initModuleParams()
                    return
                }
                try {
                    let url_info = this.pageURL.split('?')
                    let pageVue = await require(`@/views${url_info[0]}`).default
                    pageVue.router = router
                    pageVue.store = store
                    const Instance = Vue.extend(pageVue)
                    // 将路径中所含的参数传递给组件
                    let temp_params = url_info[1] || ''
                    let params = {}
                    temp_params.split('&').forEach(p => {
                        if (p) {
                            let p_arr = p.split('=')
                            params[p_arr[0]] = p_arr[1]
                        }
                    })
                    // 创建实例，挂载指定DOM
                    this.pageInstance = new Instance({
                        // 传递数据给组件
                        propsData: {
                            // 当前页面作用域
                            scope: this,
                            params,
                            theme: this.themeInfo,
                            pRoutePath: this.pageURL,
                            ...this.$attrs
                        }
                    }).$mount(`#${this.comId}`)
                    this.isFail = false
                } catch (error) {
                    console.error(`
                    【检查文件${this.pageURL}】
                    1. 文件路径找不到 / 路径错误
                    2. 文件内容编写有误
                    3. 查看下面的详细错误信息
                    `)
                    console.error(error)
                    this.isFail = true
                }
            } else { // 外部页面

            }
        },
        search () {
            typeof this.pageInstance?.search === 'function' && this.pageInstance.search()
        },
        resize () {
            typeof this.pageInstance?.resize === 'function' && this.pageInstance.resize()
        },
    },
    mounted () {
        this.comId = 'comId' + Math.round(Math.random() * 1000)
        this.$nextTick(() => {
            this.init()
        })
    },
    beforeDestroy () {
        // 由于挂载页面无法监听组件被销毁，所以将其放到挂载页面的methods方法中定义，手动调用
        typeof this.pageInstance?.beforeDestroy === 'function' && this.pageInstance.beforeDestroy()
    },
}
</script>

<style lang="scss" scoped>
.p-page {
    height: 100%;
    overflow: auto;
}
.module-page {
    overflow: hidden;
}
.wh100 {
    width: 100%;
    height: 100%;
}
</style>