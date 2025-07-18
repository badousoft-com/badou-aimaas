/*
 * @FilePath: @/components/frame/Panel/PanelCode/theme/bluePlane/index.js
 * @Description: 蓝色平面主题
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
        backgroundImage: `url(${require('./assets/head.png')})`,
        fontSize: '3vh',
        backgroundSize: '70% auto',
        color: '#FCFCFC', /* 面板标题字体颜色 */
    },
    loading: {
        // 设定图标类名
        // 'element-loading-spinner': 'el-icon-loading',
        // 背景色值
        'element-loading-background': 'rgba(12, 46, 107, 0.85)'
    },
    // 块样式
    // block: {
    //     // boxShadow: 'inset 0 0 12px #195f91',
    //     marginTop: '90px',
    //     height: 'calc(100% - 90px)',
    // },
    // blockFilter: {
    //     position: 'absolute',
    //     top: '-90px'
    // },
    // 块的四个角
    angleList: ['top_left', 'top_right', 'bottom_right', 'bottom_left'],
    // 角样式（可对象，可数组）
    angleStyle: {
        width: '18px',
        height: '18px'
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