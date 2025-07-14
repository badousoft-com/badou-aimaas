// ！！！！！需要全局引入的指令才需要填写在这里，框架已经全局引入当前文件
// 第一步：import XX from './xx/xx'
// 第二步：utilsObj中添加属性

import Vue from 'vue'
import { Is_Safari } from './browser'

// 放置在这里的函数将注册为全局函数
let utilsObj = {
    // 工具类名：对应引入的函数
    Is_Safari
}
// 挂载上述方法到vue原型下，方便全局调用
for (let [key, value] of Object.entries(utilsObj)) {
    Vue.prototype[key] = value
}