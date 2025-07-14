/**
 * 面板列表选择弹窗
 *
 */
import Vue from 'vue'

const Constructor = Vue.extend(require('./PanelChooseList.vue').default);

/**
 * 返回默认的 options 配置
 */
function getDefaultConfig() {
    return {
        title: "用户默认面板设置", // 弹窗标题
        type: '20',
        id: null
    }
}

export default function (config) {
    let resolve
    function callback(data) {
        resolve(data)
    }

    let component = new Constructor({
        data: {
            config: Object.assign({}, getDefaultConfig(), config),
            callback: callback,
        }
    }).$mount()
    document.querySelector('body').appendChild(component.$el)
    return new Promise(r => resolve = r)
}
