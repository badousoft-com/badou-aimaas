/*
 * @FilePath: @/components/frame/Panel/js/common.js
 * @Description: 面板公用方法
 */
import { Standard_List } from '@/api/frame/module.js'
import { GetSearchFieldList } from '@/service/module'
import GlobalConst from '@/service/global-const'

// 组装搜索数据
export async function Get_Filter_List (data, pos = 'conent') {
    // 搜索框数据的模型mdCode
    let searchModuleCode = data.searchModuleCode
    let res = []
    let cFilterList = data.customSetting?.filterList || null
    if (cFilterList) {
        res = await GetSearchFieldList(cFilterList, [])
        res = res.map(f => ({
            ...f,
            pos,
            isModule: !!f.isModule
        }))
    }
    if (searchModuleCode) {
        try {
            let moduleInfo = await Standard_List({mdCode: searchModuleCode})
            // 搜索条件信息
            let searchCondition = moduleInfo.searchCondition ? JSON.parse(moduleInfo.searchCondition) : []
            // 搜索条件字段
            let selectorData = moduleInfo.selectorData ? JSON.parse(moduleInfo.selectorData) : []
            let tempFilters = await GetSearchFieldList(searchCondition, selectorData)
            res = res.concat(tempFilters.map(f => ({
                ...f,
                pos,
                isModule: true
            })))
        } catch (err) {
            console.error(`获取${data.name}搜索项模型出错，详细报错如下：`)
            console.log(err)
        }
    }
    return res
    // 返回的参数格式/自定义需配置的格式
    // return [
    //     {
    //         type: 1, // 使用与模型列表一致的搜索条件时
    //         filterType: 'select',  // 自定义搜索条件时
    //         id: 'year',
    //         name: '年份',
    //         vaule: '',
    //         defaultValue: '', // 默认值
    //         url: '',  // 下拉数据源接口地址
    //         isModule: '1', // 是否使用模型搜索
    //         options: [  // 下拉数据源
    //             { id: '2020', value: '2020', text: '2020 年' },
    //             { id: '2021', value: '2021', text: '2021 年' },
    //             { id: '2022', value: '2022', text: '2022 年' },
    //         ]
    //     },
    // ]
}


// 获取组装按钮方法
export async function Get_BtnList (buttons) {
    let res = buttons || []
    if (typeof buttons === 'function') {
        res = await buttons.call(this)
    }
    // 组装框架按钮默认值（同模型列表展示按钮）
    let defalutBtnAttrs = GlobalConst.button.listAttrs
    res = res.map(b => {
        let defaultAttr = defalutBtnAttrs[b.id] || {}
        return {
            ...defaultAttr,
            ...b,
        }
    })
    return res
}