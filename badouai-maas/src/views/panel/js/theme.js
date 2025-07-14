/*
 * @FilePath: @/views/panel/js/theme.js
 * @Description: 主题
 */
export default {
    // 面板主题列表 { text: 主题名称, id: 主题code }
    // 其中 code 的值为 @/components/frame/Panel/PanelCode/theme 的主题目录名称
    themeList: [
        { text: '智慧监控大屏', id: 'zhjk' },
        { text: '蓝色大屏', id: 'bluePlane' },
        { text: '蓝色主题2', id: 'blue2' },
        { text: '白底', id: 'white' },
    ],
    // 主题边框信息
    themeBorderInfo: {
        default: [
            { text: '实线', id: 'solid' },
            { text: '虚线', id: 'dashed' },
            { text: '点', id: 'dotted' },
            { text: '双线', id: 'double' },
            { text: '3D 凹槽边框', id: 'groove' },
            { text: '3D 垄状边框', id: 'ridge' },
            { text: '3D inset 边框', id: 'inset' },
            { text: '3D outset 边框', id: 'outset' },
        ],
        // key 为 Theme_List 中的id，即主题目录的名称
        bluePlane: [
            // text 为border显示值（自行定义），id 为 主题目录下/borderTheme/边框图片名
            { text: '561*313型无角背景', id: 'noAngle' },
            { text: '768*400型边框', id: 'centerB' },
            { text: '768*542型边框', id: 'centerT' },
            { text: '533*699型边框', id: 'left' },
            { text: '533*550型边框', id: 'rightB' },
            { text: '533*391型边框', id: 'rightT' },
        ],
        blue2: [
            { text: '主要内容', id: 'primary' },
            { text: '267*159型边框', id: 'center' },
            { text: '529*533型边框', id: 'center2' },
            { text: '734*548型边框', id: 'left_bottom' },
            { text: '734*728型边框', id: 'left_top' },
        ],
        white: [
            { text: '实线', id: 'solid' },
            { text: '虚线', id: 'dashed' },
            { text: '点', id: 'dotted' },
            { text: '双线', id: 'double' },
            { text: '3D 凹槽边框', id: 'groove' },
            { text: '3D 垄状边框', id: 'ridge' },
            { text: '3D inset 边框', id: 'inset' },
            { text: '3D outset 边框', id: 'outset' },
        ],
        zhjk: [
            { text: '实线', id: 'solid' },
            { text: '虚线', id: 'dashed' },
            { text: '点', id: 'dotted' },
            { text: '双线', id: 'double' },
            { text: '3D 凹槽边框', id: 'groove' },
            { text: '3D 垄状边框', id: 'ridge' },
            { text: '3D inset 边框', id: 'inset' },
            { text: '3D outset 边框', id: 'outset' },
        ],
    }
}