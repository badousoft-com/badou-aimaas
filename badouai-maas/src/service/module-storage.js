import { L_Storage } from '@/utils/storage'
import GlobalConst from '@/service/global-const'
import { Bytes_To_Size } from '@/utils/file'

// 定义版本键名
const VERSION_KEY = 'version'

/**
 * 更新缓存的版本号
 * @param {String} version 传入的版本号
 * @returns
 */
function Update_Version (version) {
    if (!version) return
    // 获取缓存中的版本号
    let _oldVersion = L_Storage.getItem(VERSION_KEY)
    // 判断版本号是否有变更
    if (!_oldVersion || (_oldVersion !== version)) {
        // 清除旧的模型缓存数据
        Clear_Module_Storage()
        // 存储新的版本号
        L_Storage.setItem(VERSION_KEY, version)
    }
}

// 获取当前的版本号
function Get_Version () {
    return L_Storage.getItem(VERSION_KEY)
}

// 定义存储在storage中的键名，将所有的模型都存储在该键之下，方便管理
function Get_Module_Key () {
    let _projectVersion = Get_Version()
    return (_projectVersion ? `${_projectVersion}_` : '') + 'moduleStorage'
}

/**
 * 设置module模型缓存
 * @param {String} code 存储的属性键名
 * @param {obj} data 存储的对象数据
 */
function Set_Module_Storage (code, data) {
    // 获取模型存储总对象
    let moduleStorage = L_Storage.getObj(Get_Module_Key())
    // 判断当前是否存在模型存储总对象
    if (!moduleStorage) {
        // 如果不存在，那么新增个对象，并且设置当前存储的数据作为其属性
        moduleStorage = { [code]: data }
        // 更新-整个模型存储对象存储
        // 这里赌一下，我不信一个模型足以承包模型缓存阙值，所以不对当前数据量做判断
        L_Storage.setObj(Get_Module_Key(), moduleStorage)
    } else {
        // 已存在，则更新模型存储对象数据
        moduleStorage[code] = data
        let _storageByte = stringToByte(JSON.stringify(moduleStorage)).length
        if (parseFloat(Bytes_To_Size(_storageByte, 'MB')) > parseFloat(GlobalConst.moduleStorageMax)) {
            // 清除这个对象下所有属性，只保留最新的缓存模型
            moduleStorage = { [code]: data }
        }
        // 存储模型缓存数据
        L_Storage.setObj(Get_Module_Key(), moduleStorage)
    }
}

/**
 * 获取module模型缓存
 * @param {String} code 存储的属性键名
 * @returns
 */
function Get_Module_Storage (code) {
    // 获取整个模型存储对象-再根据传入属性键名获取数据
    return L_Storage.getObj(Get_Module_Key())?.[code]
}

/**
 * 清空module模型缓存
 */
function Clear_Module_Storage () {
    // 删除整个模型存储对象数据
    L_Storage.removeItem(Get_Module_Key())
    // 清除版本号
    L_Storage.removeItem(VERSION_KEY)
}

/**
 * 字符串转byte数组
 * @param {String} str 字符串数据
 * @returns
 */
function stringToByte (str) {
    // var bytes = new Array()
    let bytes = new Array()
    let len = null
    let c = null
    len = str.length
    for (let i = 0; i < len; i++) {
        c = str.charCodeAt(i)
        if (c >= 0x010000 && c <= 0x10FFFF) {
            bytes.push(((c >> 18) & 0x07) | 0xF0)
            bytes.push(((c >> 12) & 0x3F) | 0x80)
            bytes.push(((c >> 6) & 0x3F) | 0x80)
            bytes.push((c & 0x3F) | 0x80)
        } else if (c >= 0x000800 && c <= 0x00FFFF) {
            bytes.push(((c >> 12) & 0x0F) | 0xE0)
            bytes.push(((c >> 6) & 0x3F) | 0x80)
            bytes.push((c & 0x3F) | 0x80)
        } else if (c >= 0x000080 && c <= 0x0007FF) {
            bytes.push(((c >> 6) & 0x1F) | 0xC0)
            bytes.push((c & 0x3F) | 0x80)
        } else {
            bytes.push(c & 0xFF)
        }
    }
    return bytes
}

// 导入实例：
// import {  } from '@/service/module-storage'
export {
    // 更新版本号
    Update_Version,
    Set_Module_Storage,
    Get_Module_Storage,
    Clear_Module_Storage
}