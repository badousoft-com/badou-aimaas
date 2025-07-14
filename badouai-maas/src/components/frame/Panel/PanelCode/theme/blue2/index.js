/*
 * @FilePath: @/components/frame/Panel/PanelCode/theme/blue2/index.js
 * @Description: 问诊看病大屏主题
 */

export default {
    // 面板主题色
    primaryColor: '#01F8FE',
    // 内容背景色
    contentBg: 'rgba(0, 0, 0, 0)',
    // 色系
    color: ['#119DFE', '#5E33FF', '#34DF65', '#FFF02A', '#EA2027', '#8E30FF', '#EC8F38'],
    // 字体颜色
    textColor: '#CCC',
    // 面板样式
    panel: {
        backgroundImage: `url(${require('./assets/background.png')})`, 
        backgroundSize: '100% 100%',
    },
    // 面板标题样式
    panelTitle: {
        height: '8vh',
        lineHeight: 1,
        backgroundImage: `url(${require('./assets/head.png')})`,
        fontSize: '3vh',
        backgroundSize: '70% auto',
        color: '#FCFCFC', /* 面板标题字体颜色 */
        letterSpacing: '2px',
    },
    block: {
        overflow: 'visible',
    },
    // 块标题样式
    blockTitle: {
        backgroundImage: `url(${require('./assets/blockTitleLeft.png')})`,
        // backgroundSize: 'auto 100%',
        position: 'absolute',
        top: '-15px',
        zIndex: 9,
        maxWidth: '180px',
        border: 'none',
        padding: '5px 45px 5px 5px',
        fontSize: '2vh',
    },
    // 块内容样式（包含块搜索条件及内容）
    blockContent: {
        padding: '20px',
        overflow: 'hidden',
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
    }
}