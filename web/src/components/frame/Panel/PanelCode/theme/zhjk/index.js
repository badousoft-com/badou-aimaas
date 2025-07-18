/*
 * @FilePath: @/components/frame/Panel/PanelCode/theme/zhjk/index.js
 * @Description: 智慧监控大屏的面板主题
 */
export default {
    // 面板主题色
    primaryColor: '#01F8FE',
    // 内容背景色
    contentBg: 'rgba(0, 0, 0, 0)',
    // 色系
    color: ['#48E5E5', '#FFD31C', '#3254DD', '#BEE5FB', '#EA2027', '#3CD495', '#3CD495'],
    // 字体颜色
    textColor: '#FFFFFF',
    // 重点字体颜色
    importTextColor: '#FFD31C',
    // 块的padding值，用于块与块之间的间隔
    spaceSize: 0,
    // 面板样式
    panel: {
        backgroundImage: `url(${require('./assets/background.png')})`, 
        backgroundSize: '100% 100%',
    },
    // 面板标题样式
    panelTitle: {
        height: '6vh',
        backgroundImage: `url(${require('./assets/head.png')})`,
        fontSize: '3vh',
        backgroundSize: '100% auto',
        color: '#FCFCFC', /* 面板标题字体颜色 */
        letterSpacing: '2px',
    },
    block: {
        overflow: 'visible',
        backgroundImage: `url(${require('./assets/blockBg.png')})`,
    },
    showContentShadow: false,
    // 块标题样式
    blockTitle: {
        backgroundImage: `url(${require('./assets/blockTitleLeft.png')})`,
        backgroundSize: 'auto 100%',
        fontSize: '1.5rem',
        padding: '0px 0px 0.8rem 0.6rem',
        borderBottom: 'none',
        lineHeight: '1',
        // position: 'absolute',
        // top: '-15px',
        // zIndex: 9,
        // border: 'none',
        // padding: '5px 45px 5px 5px',
    },
    // 块内容样式（包含块搜索条件及内容）
    blockContent: {
        // padding: '20px',
        overflow: 'hidden',
    },
    loading: {
        // 设定图标类名
        // 'element-loading-spinner': 'el-icon-loading',
        // 背景色值
        'element-loading-background': 'rgba(12, 46, 107, 0.85)'
    },
    // 表格样式
    table: {
        // 表头背景色
        headerBg: '#032478',
        // 边框颜色
        headerColor: '#57FAF3',
        // 边框颜色
        border: 'rgba(0, 0, 0, 0)',
        // 斑马线背景色
        stripeBg: '#0B2C5A',
        // 单元格颜色
        cellColor: '#fff',
        // 鼠标悬浮时的背景颜色
        hoverBg: '#244675',
    },
    echart: {
        textColor: 'rgba(255, 255, 255, 0.64)'
    }
}