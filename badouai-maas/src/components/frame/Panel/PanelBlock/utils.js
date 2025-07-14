/*
 * @FilePath: @/components/frame/Panel/PanelBlock/utils.js
 * @Description: 块配置方法
 */
import GlobalConst from '@/service/global-const.js'
import { Str_To_Obj } from '@/utils/data-type-change.js'
import { Get_Filter_List } from '@/components/frame/Panel/js/common.js'
// 组装整理页面所需要的块信息配置
export async function Set_Block_Attr (blockInfo) {
    // 块内容列表
    let contentList = blockInfo[GlobalConst.panel.contentKey] || []
    contentList = contentList.map(c => ({
        ...c,
        refName: c.refName || (c.code ? `content_${c.code}` : `content_${c.id}`),
    }))
    if (blockInfo.contentType === '2') {  // 当前块类型为多图
        contentList = [{
            name: blockInfo.name,
            type: 'multiGraph',     // 标识多图类型
            imgList: contentList,   // 图片
            showType: blockInfo.shouType,    // 展示方式
            interval: blockInfo.pitchTime,   // 切换间隔
        }]
    }
    let block = {
        ...blockInfo,
        // 是否展示标题
        showTitle: String(blockInfo.flgShowTitleBar) === '1',
        // 标题展示的图标
        titleIcon: blockInfo.blockIcon,
        // 块是否需要边框
        hasBorder: String(blockInfo.flgShowBorder) === '1',
        // 是否需要展示内容标题
        showContentTitle: blockInfo.contentType === '1',    // 默认多内容时展示内容标题，暂无加入自定义 ======== 20220812
        // 块高度（虽然有定义，但不使用，交给内容组件去决定）
        height: blockInfo.sizey,
        // 是否需要跳转详情按钮
        showMore: String(blockInfo.showMore) === '1' && blockInfo.moreUrl,
        // 详情url（跟PC端的有区别，======= todo）
        detailUrl: blockInfo.moreUrl,
        // 内容：内容的具体展示字段交给另外一组件整理
        contentList,
        // 块的自定义js
        jsUrl: blockInfo.jsUrl || '',
        // js自定义内容
        customSetting: Str_To_Obj(blockInfo.blockChartOptions) || {},
    }
    if (blockInfo.jsUrl) {
        try {
            let jsObj = await require(`@/../public/panelJs${blockInfo.jsUrl}`)
            block.customSetting = Object.assign(block.customSetting, jsObj.default)
        } catch (e) {
            console.error(`
                    【检查文件/public/panelJs${blockInfo.jsUrl}】
                    1. 文件路径找不到 / 路径错误
                    2. 文件内容编写有误
                    3. 查看下面的详细错误信息
                `)
            console.error(e)
        }
    }
    block.filterList = await Get_Filter_List(block, 'block')
    return block
}