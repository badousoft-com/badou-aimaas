import { Standard_List, Standard_Edit, Standard_View } from '@/api/frame/module'
import { Str_To_Obj } from '@/utils/data-type-change'
import { Get_Module_Storage, Set_Module_Storage } from '@/service/module-storage'

/**
 * 获取模型数据
 * @param {String} mdCode 模型编码
 * @param {String} type 模型类型 list/edit/view
 * @param {Function} requestFun 请求函数
 * @returns
 */
function moduleStorage (mdCode, type = 'list', requestFun) {
    return new Promise((resolve, reject) => {
        // 如果没有开启模型缓存
        if (parseInt(process.env.VUE_APP_OPEN_MODULE_STORAGE) === 0) {
            // 直接请求模型数据
            requestFun({mdCode}).then(res => {
                resolve(res)
            })
        } else {
            // 定义后续存储在缓存数据中的键
            let code = `${mdCode}_${type}`
            // 获取模型缓存数据
            let moduleStorage = Get_Module_Storage(code)
            if (moduleStorage) {
                // 如果能在缓存中找到数据那就直接该缓存数据
                resolve(moduleStorage)
            } else {
                // 如果没有找到，那就直接请求数据
                requestFun({mdCode}).then(res => {
                    // 请求完数据之后缓存模型数据
                    Set_Module_Storage(code, res)
                    resolve(res)
                })
            }
        }
    })
}

function loadListCfg (mdCode) {
    return new Promise(resolve => {
        moduleStorage(mdCode, 'list', Standard_List).then(res => {
            resolve(res)
        })
    })
}

function loadEditCfg (mdCode) {
    return new Promise(resolve => {
        moduleStorage(mdCode, 'edit', Standard_Edit).then(res => {
            resolve(res)
        })
    })
}

function loadViewCfg (mdCode) {
    return new Promise(resolve => {
        moduleStorage(mdCode, 'view', Standard_View).then(res => {
            resolve(res)
        })
    })
}

// 为数组下对象添加value属性
function addValueAttr (list) {
    let resultList = list
    if (resultList && resultList.constructor === Array) {
        resultList.forEach(i => {
            // 添加value字段
            i.value = i.value || null
        })
        return resultList
    }
    return resultList
}

export default {
    async listModuleCfg (mdCode) {
        // 定义获取列表模型数据
        let _module = await loadListCfg(mdCode)
        let module = {
            // 将原有属性填入
            ...(_module || {}),
            // 模型id
            id: _module.moduleId,
            // 模型名称
            name: _module.moduleName,
            // 模型编码
            mdCode: _module.module,
            // 列表字段数组（显示字段）
            fieldList: _module.moduleDisplay && Str_To_Obj(_module.moduleDisplay),
            // 列表字段数据（包含id）
            allFieldList: _module.moduleAllFields && Str_To_Obj(_module.moduleAllFields),
            // 可使用搜索项配置数组
            searchCondition: _module.searchCondition && Str_To_Obj(_module.searchCondition),
            // 可使用搜索项具体数据（包含更多详细数据），需要与searchCondition互补
            selectorData: _module.selectorData && Str_To_Obj(_module.selectorData),
            // 模型自定义js【js文件路径地址】
            customJs: _module.listJs,
            // 模型自定义js具体配置内容：{}
            customSetting: null,
        }
        // 若存在动态配置js，则获取js内容
        if (_module.listJs) {
            try {
                // 【旧的方式】：
                //     动态js中使用module.exports导出，这里使用require获取
                //     如果动态js中同时使用了import，本地正常，编译打包后报错，原因是语法限制import与module.exports不能同时使用
                // 【新的方式（如下）】：
                //     动态js中使用export default导出，这里使用require获取属性default
                let jsObj = await require(`../../public/moduleJs${_module.listJs}`)
                module.customSetting = jsObj.default
            } catch (e) {
                console.error(`
                    【检查文件public/moduleJs${_module.listJs}】
                    1. 文件路径找不到 / 路径错误
                    2. 文件内容编写有误
                    3. 查看下面的详细错误信息
                `)
                console.error(e)
            }
        }
        return module
    },
    async editModuleCfg (mdCode) {
        // 定义获取编辑模型数据
        let _module = await loadEditCfg(mdCode)
        let module = {
            // 将原有属性填入
            ...(_module || {}),
            // 模型id
            id: _module.moduleId,
            // 模型名称
            name: _module.moduleName,
            // 模型编码
            mdCode: _module.module,
            // 模型数据字典集合: {dic1:[],dic2:[]}
            dic: _module.dic && Str_To_Obj(_module.dic),
            // 表单字段数组数据[]
            // 因为前端表单检验组件要求字段必须有value，不然会出现填写1字段，全字段进行校验的问题，这里赋值字段属性value为null
            fieldList: _module.moduleFields && addValueAttr(Str_To_Obj(_module.moduleFields)) || [],
            // 模型关联tab数据,模型若配置字段有关联关系则返回有值
            childTab: _module.childTab || [],
            // 模型自定义js【js文件路径地址】
            customJs: _module.editedJs,
            // 模型自定义js具体配置内容：{}
            customSetting: null
        }
        // let addressbook = module.fieldList.find(i => i.type === 'addressbook')
        // addressbook && (addressbook.addressType = '2-0-20-2,2-1-0-2,1-1-1-1')
        // 若存在动态配置js，则获取js内容
        if (_module.editedJs) {
            try {
                // 【旧的方式】：
                //     动态js中使用module.exports导出，这里使用require获取
                //     如果动态js中同时使用了import，本地正常，编译打包后报错，原因是语法限制import与module.exports不能同时使用
                // 【新的方式（如下）】：
                //     动态js中使用export default导出，这里使用require获取属性default
                let jsObj = await require(`../../public/moduleJs${_module.editedJs}`)
                module.customSetting = jsObj.default
            } catch (e) {
                console.error(`
                    【检查文件public/moduleJs${_module.editedJs}】
                    1. 文件路径找不到 / 路径错误
                    2. 文件内容编写有误
                    3. 查看下面的详细错误信息
                `)
                console.error(e)
            }
        }
        return module
    },
    async viewModuleCfg (mdCode) {
        // 定义获取列表模型数据
        let _module = await loadViewCfg(mdCode)
        let module = {
            // 将原有属性填入
            ...(_module || {}),
            // 模型id
            id: _module.moduleId,
            // 模型名称
            name: _module.moduleName,
            // 模型编码
            mdCode: _module.module,
            // 模型数据字典集合: {dic1:[],dic2:[]}
            dic: _module.dic && Str_To_Obj(_module.dic),
            // 表单字段数组数据[]
            fieldList: _module.moduleFields && addValueAttr(Str_To_Obj(_module.moduleFields)) || [],
            // 模型关联tab数据,模型若配置字段有关联关系则返回有值
            childTab: _module.childTab || [],
            // 模型自定义js【js文件路径地址】
            customJs: _module.editedJs,
            // 模型自定义js具体配置内容：{}
            customSetting: null
        }
        // 若存在动态配置js，则获取js内容
        if (_module.editedJs) {
            try {
                // 【旧的方式】：
                //     动态js中使用module.exports导出，这里使用require获取
                //     如果动态js中同时使用了import，本地正常，编译打包后报错，原因是语法限制import与module.exports不能同时使用
                // 【新的方式（如下）】：
                //     动态js中使用export default导出，这里使用require获取属性default
                let jsObj = await require(`../../public/moduleJs${_module.editedJs}`)
                module.customSetting = jsObj.default
            } catch (e) {
                console.error(`
                    【检查文件public/moduleJs${_module.editedJs}】
                    1. 文件路径找不到 / 路径错误
                    2. 文件内容编写有误
                    3. 查看下面的详细错误信息
                `)
                console.error(e)
            }
        }
        return module
    }
}
