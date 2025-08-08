import Vue from 'vue'
import FormDesignerPage from './index.vue'
import router from '@/router'
import store from '@/store'
import { MessageBox } from 'element-ui'
import { Get_New_Name } from '@/utils'

// 定义实例默认键名
let Sign_Name = 'base'
// 定义所有实例总和
let AllInstanceEl = new Map()

/**
 * 弹窗关闭事件
 * @param {String} name 弹窗标识名
 * @returns 
 */
function closeFun (name) {
    return new Promise((resolve, reject) => {
        // 根据标识名获取对应的弹窗实例
        if (AllInstanceEl.size === 0 ||
            name && !AllInstanceEl.has(name)) {
            resolve('没有可删除项')
            return
        }
        // 获取当前实例配置的名称键名
        let _fdElKey = name || Array.from(AllInstanceEl.keys()).pop()
        // 获取弹窗实例
        let _fdEl = AllInstanceEl.get(_fdElKey)
        if (!_fdEl) return
        // 销毁实例
        _fdEl.$destroy()
        // 删除添加的dom（避免不断弹窗，没有回收，内存溢出）
        document.body.removeChild(_fdEl.$el)
        // 删除实例总和中的数据
        AllInstanceEl.delete(_fdElKey)
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
function getInstance (sign, option) {
    let FormDesigner = Vue.extend({
        ...FormDesignerPage,
        router,
        store,
    })
    let formDesignerEl = new FormDesigner({
        propsData: {
            ...option,
            sign,
            closeEvt: closeFun,
        },
    }).$mount()
    return formDesignerEl
}

let FormDesignerBuilder = {
    // 初始化弹窗
    init: function (name, option) {
        // 支持不传name，此时需要处理下option与name对应的值
        let _name = Sign_Name
        let _option = null
        if (name && name.constructor === Object) {
            _option = name || {}
        } else {
            _name = name || Sign_Name
            _option = option || {}
        }
        return new Promise((resolve, reject) => {
            // 判断已有的实例map中是否含有参数name
            if (AllInstanceEl.has(_name)) {
                // 获取已有的name数组
                let _elList = Array.from(AllInstanceEl.keys())
                // 获取新的名称
                _name = Get_New_Name(_elList)
            }
            // 获取弹窗实例
            let fdEl = getInstance(_name, _option)
            // 插入弹窗dom
            fdEl && document.body.appendChild(fdEl.$el)
            // 存储当前的弹窗实例
            AllInstanceEl.set(_name, fdEl)
            resolve(true)
        })
    },
    // 关闭弹窗
    close: function (name) {
        return closeFun(name)
    }
}

// import FormDesignerBuilder from '@/components/frame/FormDesigner/index.js'

/******************  使用方式有两种
1. 如果场景中仅会同时弹出一个弹窗，可以不使用name名参数
    FormDesignerBuilder.init(option) // 启用
    FormDesignerBuilder.close() // 关闭
2. 如果场景中需要同时弹多个弹窗，需要传入name参数，用于控制关闭对应弹窗
    FormDesignerBuilder.init('otherName', option)
    FormDesignerBuilder.close('otherName')
******************/

export default FormDesignerBuilder