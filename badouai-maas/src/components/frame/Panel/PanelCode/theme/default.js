/*
 * @FilePath: @/components/frame/Panel/PanelCode/theme/default.js
 * @Description: 框架默认主题
 */
import globalStyle from '@/styles/theme.scss'
export default {
    // 主题色
    primaryColor: globalStyle.primary,
    // 面板默认色系（要求使用十六进制，否则渐变色将会出问题）
    color: ['#2163E0', '#91CB74', '#FAC858', '#EE6666', '#73C0DE', '#3CA272', '#FC8452', '#9A60B4', '#ea7ccc'],
    // 面板内容背景色
    contentBg: '#ffffff',
    // 字体颜色
    textColor: '#333',
    // 是否展示内容容器阴影（针对多内容）
    showContentShadow: true,
    // 常规下的样式
    normal: {
        // 字体颜色
        textColor: '#333',
        // 分割线的颜色
        lineColor: 'd7d7d7',
        // 坐标轴颜色
        axisColor: '#ccc',
        // 边框颜色
        borderColor: '#999999',
        // 默认区域颜色（针对地图）
        areaColor: '#00A1D9',
        // 悬浮窗字体颜色
        tooltipTextColor: '#FFF',
        // 地图数据分段数（若为空，取其色系长度）
        pieceNum: 5,
        // 地图分段色系（若为空，取其色系）
        // pieceColor: [],
    },
    // 鼠标悬浮下的样式
    hover: {
        // 默认区域颜色（针对地图）
        areaColor: '#F7FFFD'
    },
    // loading 加载文案、图标和背景色
    // loading: {
    //     'element-loading-text': '拼命加载中',
    //     'element-loading-spinner': 'el-icon-loading',
    //     'element-loading-background':  'rgba(0, 0, 0, 0.8)'
    // }
}