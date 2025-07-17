/*
 * @FilePath: @/components/frame/Panel/BlockContent/utils.js
 * @Description: 面板内容使用到的方法
 */
import { Str_To_Obj } from '@/utils/data-type-change.js'
import { Get_Filter_List } from '@/components/frame/Panel/js/common.js'
// 根据内容属性获取渲染内容所需要的组件
function getContentComp (data) {
    // 0：报表，1：页面，2：过滤器，4：视频
    let reportTypes = {
        '1': 'page',
        '2': 'list',
        '4': 'video',
        '5': 'quick-menu'
    }
    // 先根据内容类型对其进行判断
    if (reportTypes[data.dataSourceType]) {
        return reportTypes[data.dataSourceType]
    }
    // 剩余就是属于报表模块的内容
    switch (data.type) {
        case 'wordCloud':   // 字符云
        case 'radar':       // 雷达图
        case 'barMixLine':  // 柱线混合图
        case 'pie':         // 饼图
        case 'ring':        // 环形图
        case 'stackBar':    // 堆叠柱状图
        case 'stackArea':   // 堆叠区域图
        case 'customized':  // 自定义图表
        case 'bar':         // 柱状图
        case 'line':        // 折线
            return 'chart'
        case 'map':         // 地图
            return 'map'
        case 'totalCard':   // 总数卡片
        case 'layeredCard': // 分层卡片
            return 'card'
        case 'grid':        // 表格
            return 'list'
        case 'TEXT':        // 文本
            return 'text'
        case 'multiGraph':  // 多图
            return 'multiGraph'
        default:
            return 'chart'
    }
}

// 内容属性转化
export async function Set_Content_Attrs (contentInfo = {}) {
    let content = {
        ...contentInfo,
        // 是否使用分页功能
        usePagination: String(contentInfo.isPage) === '1',
        // 对应内容使用到的组件名称
        compName: getContentComp(contentInfo),
        // js路径
        jsUrl: contentInfo.jsUrl || '',
        // 内容自定义js（默认为图表配置的值，使一些简单的配置不需要新建js，一方面方便便捷、一方面也能较少包体积）
        customSetting: Str_To_Obj(contentInfo.chartOptions) || {},
    }
    if (contentInfo.jsUrl) {
        try {
            let jsObj = await require(`@/../public/panelJs${contentInfo.jsUrl}`)
            content.customSetting = Object.assign(content.customSetting, jsObj.default)
        } catch (e) {
            console.error(`
                    【检查文件/public/panelJs${contentInfo.jsUrl}】
                    1. 文件路径找不到 / 路径错误
                    2. 文件内容编写有误
                    3. 查看下面的详细错误信息
                `)
            console.error(e)
        }
    }
    content.filterList = await Get_Filter_List(content, 'content')
    return content
}