<!--
 * @Description: 动态渲染页面
 * @FilePath: views/module/flow/part/FlowRouterTabs/RenderPage.vue
-->
<template>
    <div class="render-page">
        <div :id="id" ref="pageRef"></div>
    </div>
</template>

<script>
import Vue from 'vue'
import router from '@/router'
import store from '@/store'
import ScopeMixin from '@/components/frame/ScopeMixin'
export default {
    // 混淆：当前组件会被其他页面动态引入调用，需要该混淆类实现作用域的共享
    mixins: [ScopeMixin],
    props: {
        url: {
            type: String,
            default: ''
        }
    },
    data () {
        return {
            id: '',
            // 挂载页面内的作用域（若有传入自定义页面挂载时）
            pageScope: null,
        }
    },
    methods: {
        async init () {
            if (this.url) { // 内部组件
                try {
                    let url_info = this.url.split('?')
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
                    new Instance({
                        // 传递数据给组件
                        propsData: {
                            // 将挂载页面的作用域更新给当前页面变量pageScope,便于使用
                            getScope: (scope) => this.pageScope = scope,
                            // 当前页面作用域
                            scope: this,
                            params,
                            ...this.$attrs
                        }
                    }).$mount(`#${this.id}`)
                    this.isFail = false
                } catch (error) {
                    console.error(`
                    【检查文件${this.url}】
                    1. 文件路径找不到 / 路径错误
                    2. 文件内容编写有误
                    3. 查看下面的详细错误信息
                    `)
                    console.error(error)
                    this.isFail = true
                }
            }
        },
    },
    mounted () {
        this.id = 'id_' + Math.round(Math.random() * 1000)
        this.$nextTick(() => {
            this.init()
        })
    }
}
</script>

<style lang="scss" scoped>
.render-page {
    width: 100%;
    height: 100%;
    overflow: auto;
}
</style>