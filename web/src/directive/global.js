// ！！！！！需要全局引入的指令才需要填写在这里，框架已经全局引入当前文件
// 第一步：import XX from './xx/xx'
// 第二步：directiveObj中添加属性

import Vue from 'vue'
import returnBtnBg from './return-btn-bg'

let directiveObj = {
    // 指令名：对应引入的函数
    'btnBg': returnBtnBg,
}
for (let [key, value] of Object.entries(directiveObj)) {
    // 注册全局指令
    Vue.directive(key, value)
}