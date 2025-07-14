/*
 * @FilePath: @/components/frame/Panel/PanelCode/utils.js
 * @Description: 面板使用到的方法
 */
import { Load_Panel_And_Block } from '@/api/frame/panel/view.js'
import { Get_Panel_Theme } from '@/components/frame/Panel/PanelCode/theme/index.js'
import { Get_Filter_List } from '@/components/frame/Panel/js/common.js'

// 请求面板与块信息
function loadPanelAndBlock (params) {
    return new Promise((resolve, reject) => {
        Load_Panel_And_Block(params).then(res => {
            resolve(res || {})
        }).catch(() => {
            resolve({})
        })
    })
}

// 获取面板一些配置信息
export async function Get_Panel_Config (params) {
    let _panelConfig = await loadPanelAndBlock(params)
    let { blockData = [], layoutPanel = {} } = _panelConfig || {}
    // 这方法只处理面板的配置，块、内容的配置留给各自的组件处理
    let panelInfo = {
        ...layoutPanel,
        jsUrl: layoutPanel.jsPath,
        // js自定义内容
        // customSetting: {
        //     ...GlobalConst.panel
        // },
        // 面板筛选条件
        filterList: null,
    }
    // 如果存在面板js
    if (layoutPanel.jsPath) {
        try {
            let jsObj = await require(`@/../public/panelJs${layoutPanel.jsPath}`)
            panelInfo.customSetting = Object.assign(panelInfo.customSetting || {}, jsObj.default || {})
        } catch (e) {
            console.error(`
                    【检查文件/public/panelJs${layoutPanel.jsPath}】
                    1. 文件路径找不到 / 路径错误
                    2. 文件内容编写有误
                    3. 查看下面的详细错误信息
                `)
            console.error(e)
        }
    }
    // 默认主题
    let defaultTheme = await Get_Panel_Theme()
    // 自定义主题
    let customTheme = {}
    if (panelInfo.pageId) {
        customTheme = await Get_Panel_Theme(panelInfo.pageId)
    }
    panelInfo.theme = Object.assign({}, defaultTheme, customTheme)
    // 面板过滤器
    panelInfo.filterList = await Get_Filter_List(panelInfo, 'panel')
    let blockList = blockData.map(b => ({
        ...b,
        refName: b.refName || (b.code ? `block_${b.code}` : `block_${b.id}`),
    }))
    return {
        panelInfo,
        blockList
    }
}