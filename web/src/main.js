// 兼容ie浏览器
import '@babel/polyfill'
if (Number.parseInt === undefined) Number.parseInt = window.parseInt
if (Number.parseFloat === undefined) Number.parseFloat = window.parseFloat
import Vue from 'vue'
import LightBootstrap from './light-bootstrap-main'
import ElementUI from 'element-ui'
// 去除elementUI样式文件引入
// import 'element-ui/lib/theme-chalk/index.css'

import Config from './config/config.js'
import NewConfig from './config/new_config.js'
// global css
import '@/styles/index.scss'
import App from './App'
import store from './store'
import router from './router'
import CustomValidateRules from './js/CustomValidateRules/index'
import SelectSearch from '@/components/frame/SelectSearch/index'
import Card from '@/components/frame/Cards/index'
// bd-button组件
import Button from '@/components/frame/Common/Button/index'
// permission control
import '@/permission'
// 全局注册dialog(针对模型)js触发
import MDialog from '@/components/frame/Common/MDialog/index.js'
// 全局注册dialog(针对动态挂载页面)js触发
import Dialog from '@/components/frame/Dialog/index.js'
import VueClipboard from 'vue-clipboard2'


// 引入全局-指令
import '@/directive/global'
// 引入全局-过滤器
import '@/filter/global'
// 引入全局-工具
import '@/utils/global'
// 引入全局-服务类
import '@/service/global'

// 引入图标
// 引入字体svg格式文件
// import '@/assets/svgFont/frame/iconfont.js' // 即将废弃
// import '@/assets/svgFont/business/iconfont.js' // 即将废弃
import '@/icons'

// 监听窗口变化 --start
import 'vue-resize/dist/vue-resize.css'
import VueResize from 'vue-resize'
Vue.use(VueResize)
// 监听窗口变化 --end
Vue.use(MDialog) // 针对模型
Vue.use(Dialog) // 针对自定义挂载的页面
Vue.use(VueClipboard)
Vue.use(ElementUI, {
    size: 'small'
})
// Vue.prototype.$ELEMENT = { size: 'small', zIndex: 3000 }

Vue.use(Config)
Vue.use(NewConfig)
Vue.use(LightBootstrap)
Vue.use(CustomValidateRules)
import shell from 'vue-shell'
Vue.use(shell);

// 注册全局组件 --start
const components = [
    SelectSearch,
    Card,
    Button
]
components.forEach(component => {
    Vue.component(component.name, component)
})
// 注册全局组件 --end

Vue.config.productionTip = false
new Vue({
    el: '#app',
    router,
    store,
    render: h => h(App)
})
