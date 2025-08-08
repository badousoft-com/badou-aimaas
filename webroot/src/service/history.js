/*
 * @Description: 历史记录 操作方法
 * @FilePath: src/service/history.js
 */
import { S_Storage } from '@/utils/storage'
import store from '@/store'

function getHistoryKey () {
    return 'HISTORY_RECORDS_' + (store?.state?.user?.userInfo?.logonId || '')
}

// 获取
export function Get_History () {
    return S_Storage.getObj(getHistoryKey()) || []
}

// 赋值
export function Set_History (data) {
    // 创建事件函数
    let newStorageEvent = document.createEvent('StorageEvent')
    S_Storage.setObj(getHistoryKey(), data)
    // 始化新创建的newStorageEvent对象的属性。
    newStorageEvent.initStorageEvent('setItem', false, false, getHistoryKey(), null, data, null, null)
    // 派发对象，以便在面包屑组件能够实时获取到
    window.dispatchEvent(newStorageEvent)
}

// 向上添加历史数据
export function Add_History (value) {
    let history = Get_History()
    history.unshift(value)
    Set_History(history)
}

// 替换/删除历史记录的一项
export function Splice_History (index = 0, value) {
    let history = Get_History() || []
    value && history.splice(index, 1, value)
    !value && history.splice(index, 1)
    Set_History(history)
}

// 移除
export function Remove_History () {
    let newStorageEvent = document.createEvent('StorageEvent')
    S_Storage.removeItem(getHistoryKey())
    // 始化新创建的newStorageEvent对象的属性。
    newStorageEvent.initStorageEvent('removeItem', false, false, getHistoryKey(), null, null, null, null)
    // 派发对象，以便在面包屑组件能够实时获取到
    window.dispatchEvent(newStorageEvent)
}

// 监听历史记录变化
export function Listen_History (fn) {
    window.addEventListener('setItem', () => {
        typeof fn === 'function' && fn()
    })
    window.addEventListener('removeItem', () => {
        typeof fn === 'function' && fn()
    })
}