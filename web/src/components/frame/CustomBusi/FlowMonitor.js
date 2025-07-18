/**
 * 流程监控弹窗
 *
 */
import Vue from 'vue'

const Constructor = Vue.extend(require('./FlowMonitor.vue').default);

/**
 * 返回默认的 options 配置
 */
function getDefaultConfig() {
    return {
        title: "流程实体监控", // 弹窗标题
        fiId: null,     /* 流程id */
        boId: null,     /* 业务实体id */
        flow: {},       /* 流程实例 */
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
