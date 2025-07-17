import router from '@/router'
import { S_Storage } from '@/utils/storage'
import { Add_History } from './history'
import { MessageBox } from 'element-ui'

const handler = {
    apply: function (target, thisArg, argumentsList) {
        let data = argumentsList?.[0] || {}
        if (!data.title) {
            let tip = `pushPage方法请传入参数【title】，否则将不会保存此页面的历史记录。当前跳转路径为：${data.path}`
            // throw new ReferenceError(tip)
            console.warn(tip)
            // 只在测试环境弹窗提示
            if (process.env.NODE_ENV === 'development') {
                MessageBox.alert(tip, '警告', {
                    type: 'warning'
                })
            }
        }
        return target
    }
}
const vaildParams = new Proxy(function (data) {
    return data
}, handler)

/*
 * @Description: 路由跳转方法
 * @FilePath: src/service/router.js
 */
/**
 *
 * @param {Object} data 跳转的数据：详细参数及其解释如下
 *  name:'',
 *  path: '',
 *  params: {},
 *  query: {},
 *
 *  title: '',  // title直接写的就用title，没写的默认用name/path找到路由对象下meta，找不到的就不加
 *  storage: {}, // 需要在路由跳转时存储（S_Storage.setObj(key, storage)）的数据，可多个
 *
 *  replace: false, // 是否为router.replace，默认 false，当值为true时，替换最前面的历史记录
 */
export function Push_Page (data) {
    let {
        name = '',
        path = '',
        params = {},
        query = {},
        title = '',
        storage,
        replace = false
    } = data
    // 没有跳转的name或者path，不跳转
    if (!name && !path) {
        return
    }
    // vaildParams(data)
    return new Promise((resolve, reject) => {
        // 如果跳转前需要缓存（即缓存有传值）
        if (Object.prototype.toString.call(storage) === '[object Object]') {
            Object.keys(storage).forEach(key => {
                S_Storage.setObj(key, storage[key])
            })
        }
        let fnStr = replace ? 'replace' : 'push'
        // 存储历史记录（如果title存在，不存在的话，添加也不知道是哪个页面的地址，无意义）
        title && Add_History(data)
        // 跳转
        router[fnStr]({
            name,
            path,
            params,
            query
        }).then(res => {
            resolve(res)
        }).catch(err => {
            reject(err)
        }).finally(() => {
            // 使用 setTimeout 等待跳转完成后的历史记录操作完成
            setTimeout(() => {
                // 获取到to页面的实际使用的title
                data.title = data.title || router.currentRoute.meta?.realTitle || ''
                // 对pushPage方法均不满足以下条件的情况进行校验提示
                //    1. 无参数title
                //    2. meta.title 没有默认值
                //    3. 历史记录中没有存储
                vaildParams(data)
            }, 500)
        })
    })
}

