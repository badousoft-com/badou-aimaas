import dialogVue from './index.vue'
import router from '@/router'
import store from '@/store'
import { Get_New_Name } from '@/utils'

// 定义所有实例总和
let AllInstanceEl = new Map()

const MDialog = {}
MDialog.install = function (Vue, option) {
    if (typeof window !== 'undefined' && window.Vue) {
        Vue = window.Vue
    }
    let dialog = null
    const initInstance = (dataOption) => {
        // 使用vue.extend的组件无法获取$router,$route,$store，需要在这里塞进去
        const MDialogInstance = Vue.extend({...dialogVue, router, store}) // 创建构造器（子类）
        // 创建实例
        // propsData只能作用在props上，组件中没有声明的属性则无法获取更新数据
        dialog = new MDialogInstance({propsData: dataOption})
        // 手动挂载，此时模版渲染为文档之外元素
        let dialogEl = dialog.$mount()
        // 插入文档
        document.body.appendChild(dialogEl.$el)

        // 获取已有的map集合
        let _elList = Array.from(AllInstanceEl.keys())
        // 根据已有的，获取新的实例名称
        let _name = Get_New_Name(_elList)
        // 存储实例
        AllInstanceEl.set(_name, dialog)
    }
    const close = () => {
        try {
            // 定义实例索引键
            let _fdElKey = null
            // 定义弹窗实例
            let _dialog = null
            // 判断当前实例是否存在弹窗实例
            let hasInstanceMap = AllInstanceEl.size !== 0
            if (hasInstanceMap) {
                // 更新实例键名
                _fdElKey = Array.from(AllInstanceEl.keys()).pop()
                // 更新弹窗实例
                _dialog = AllInstanceEl.get(_fdElKey)
            } else {
                _dialog = dialog
            }
            if (!_dialog) return
            let vm = _dialog.$mount()
            let dialogEl = vm.$el
            document.body.removeChild(dialogEl)
            _dialog.$destroy()
            vm = null
            dialog = null
            // 删除已有实例
            if (hasInstanceMap) {
                // 删除实例总和中的数据
                AllInstanceEl.delete(_fdElKey)
            }
        } catch (e) {
            console.log(`执行this.$dialog.close()报错，报错日志${e}`)
        }
    }
    Vue.prototype.$dialog = {
        init (option) {
            return new Promise((resolve, reject) => {
                try {
                    let allOption = {}
                    if (typeof option === 'object') {
                        // 传递参数中默认添加dialog的触发属性，设置visible值为true
                        option.visible = true
                        // 设置弹窗中内容的参考父级类名，用以计算固定内容高度
                        option.parentElClass = 'el-dialog__body'
                        // 隐藏弹窗页面下的原有组件功能按钮
                        option.hideBtn = true
                        // 传递关闭函数
                        option.closeFun = close
                        // 将option各属性拷贝给dialog对象
                        Object.assign(allOption, option, {option: option})
                    }
                    initInstance(allOption)
                    // 给父组件调用时需要查看的话用的，跟传递子组件逻辑没什么关联
                    resolve(option)
                } catch (e) {
                    console.log(`启用全局注册弹窗异常：${JSON.stringify(e)}`)
                    reject(e)
                }
            })
            
        },
        close () {
            return new Promise((resolve, reject) => {
                try {
                    // 关闭事件
                    close()
                    resolve()
                } catch (e) {
                    // console.log(`关闭全局注册弹窗异常：${JSON.stringify(e)}`)
                    reject(e)
                }
            })
        }
    }
}
export default MDialog