import { L_Storage } from '@/utils/storage'
const store = require('@/store')

function getTokenKey () {
    // 获取系统编码
    let systemCode = store?.default?.state?.settings?.projectSetting?.systemCode || ''
    return `${systemCode}_token`
}

export function getToken () {
    return L_Storage.getItem(getTokenKey())
}

export function setToken (token) {
    return L_Storage.setItem(getTokenKey(), token || '')
}

export function removeToken () {
    return L_Storage.removeItem(getTokenKey())
}