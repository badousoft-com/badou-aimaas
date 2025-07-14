
// 永久存储：基于cookies用户凭证的模式，确保新开标签页共享session不需再登录
// let session = localStorage
// 临时存储
// let session = sessionStorage
function getStorageObj (storage) {
    return {
        // 2023-02-14：合并setItem与setObj的功能
        set: function (key, val) {
            let _fun = val && typeof val === 'object' ? this.setObj : this.setItem
            _fun(key, val || '')
        },
        // 2023-02-14：合并getItem与getObj的功能
        get: function (key) {
            let _val = storage.getItem(key)
            try {
                _val && (_val = JSON.parse(_val))
            } catch (e) {
                console.log(`字符串转对象失败：${_val}值为单纯字符串无法转换，或者是对象字符串真的转化失败`)
            }
            return _val
        },
        // 2023-02-14：将removeItem与clear结合
        clear: function (key) {
            // 传入key时则清除指定缓存
            if (key) {
                this.removeItem(key)
                return
            }
            // 没有传入key则表示清除所有缓存
            storage.clear()
        },
        // 删除以key变量值开头的缓存变量数据,若
        //     key为'apk'，
        //     缓存数据对象为：{ aa:1, apk_a: 2, apk_b: 3, apk_c: 4 }
        //     则删除对象中的apk_a,apk_b,apk_c
        removeItemStartWith: function (key) {
            if (!key) return
            // 获取当前session下所有的数据项
            let storageKeys = Object.keys(storage)
            if (storageKeys.length === 0) return
            // 获取匹配的数据项，然后删除项
            storageKeys.filter(i => i.startsWith(key)).forEach(i => {
                storage.removeItem(i)
            })
        },

        // ！！！！！推荐使用set代替这里setItem的用法
        setItem: function (key, val) {
            try {
                storage.setItem(key, val)
            } catch (e) {
                /* eslint-disable */
                if (e.code === QUOTA_EXCEEDED_ERR_CODE) {
                    console.error('storage已达到容量阙值，请检查是否对应删除')
                }
                /* eslint-disable */
            }
        },
        // ！！！！！推荐使用get代替这里getItem的用法
        getItem: function (key) {
            return storage.getItem(key)
        },
        // ！！！！！推荐使用set代替这里setObj的用法
        setObj: function (key, obj) {
            let val = JSON.stringify(obj)
            storage.setItem(key, val)
        },
        // ！！！！！推荐使用get代替这里getObj的用法
        getObj: function (key) {
            let obj = storage.getItem(key)
            let result = null
            try {
                result = JSON.parse(obj)
            } catch (e) {
                console.error(`JSON数据转化失败`)
            }
            return result
        },
        // ！！！！！推荐使用remove代替这里removeItem的用法
        removeItem: function (key) {
            storage.removeItem(key)
        },
    }
}
let L_Storage = getStorageObj(localStorage)
let S_Storage = getStorageObj(sessionStorage)

// import { S_Storage } from '@/utils/storage'
export {
    // 永久存储
    L_Storage,
    // 会话存储
    S_Storage
}