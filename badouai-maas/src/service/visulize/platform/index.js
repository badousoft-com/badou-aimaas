import Vue from 'vue'
import DialogVue from './index.vue'
import { Get_New_Name } from '@/utils'
import SortBefore from '../content/Sort/before'

// 定义所有实例总和
let SortAllInstanceEl = new Map()
// 定义实例默认键名
let Sign_Name = 'base'

/**
 * 弹窗关闭事件
 * @param {String} name 弹窗标识名
 * @returns
 */
function closeFun () {
    return new Promise((resolve, reject) => {
        // 根据标识名获取对应的弹窗实例
        if (SortAllInstanceEl.size === 0) {
            resolve('没有可删除项')
            return
        }
        // 获取当前实例配置的名称键名
        let _fdElKey = Array.from(SortAllInstanceEl.keys()).pop()
        // 获取弹窗实例
        let _fdEl = SortAllInstanceEl.get(_fdElKey)
        if (!_fdEl) return
        // 销毁实例
        _fdEl.$destroy()
        // 删除添加的dom（避免不断弹窗，没有回收，内存溢出）
        document.body.removeChild(_fdEl.$el)
        // 删除实例总和中的数据
        SortAllInstanceEl.delete(_fdElKey)
        _fdEl = null
        resolve()
    })
}

/**
 * 获取实例
 * @param {String} sign 弹窗实例标识名
 * @param {Object} option 弹窗的配置数据
 * @returns
 */
function getInstance (option, sign) {
    let DialogComponent = Vue.extend(DialogVue)
    let dialogEl = new DialogComponent({
        propsData: {
            ...option,
        },
    }).$mount()
    return dialogEl
}

let Sort = {
    init: function (option) {
        // 设置排序的默认options
        option = SortBefore.before(option)
        if (!option) return
        return new Promise((resolve, reject) => {
            try {
                let _name = Sign_Name
                // 判断已有的实例map中是否含有参数name
                if (SortAllInstanceEl.has(_name)) {
                    // 获取已有的name数组
                    let _elList = Array.from(SortAllInstanceEl.keys())
                    // 获取新的名称
                    _name = Get_New_Name(_elList)
                }
                let _option = Object.assign({}, option, {
                    // 传递参数中默认添加dialog的触发属性，设置visible值为true
                    visible: true,
                    // 设置弹窗中内容的参考父级类名，用以计算固定内容高度
                    parentElClass: 'el-dialog__body',
                    // 隐藏弹窗页面下的原有组件功能按钮
                    hideBtn: true,
                    resolve: resolve,
                    closeFun: closeFun, // 关闭弹窗函数
                })
                _option.option = _option
                // 获取弹窗实例
                let fdEl = getInstance(_option, _name)
                // 插入弹窗dom
                fdEl && document.body.appendChild(fdEl.$el)
                // 存储当前的弹窗实例
                SortAllInstanceEl.set(_name, fdEl)
            } catch (e) {
                console.log(`启用全局注册弹窗异常：${JSON.stringify(e)}`)
                reject(e)
            }
        })
    },
    close: function () {
        return closeFun()
    }
}

export default Sort