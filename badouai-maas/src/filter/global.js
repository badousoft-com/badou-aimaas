// ！！！！！需要全局引入的指令才需要填写在这里，框架已经全局引入当前文件
// 第一步：import XX from './xx/xx'
// 第二步：filterObj中添加属性

import Vue from 'vue'
import completeValue from './complete-value'

let filterObj = {
    // 过滤器名：对应引入的函数
    'completeValue': completeValue,
}
for (let [key, value] of Object.entries(filterObj)) {
    // 注册全局过滤器
    Vue.filter(key, value)
}