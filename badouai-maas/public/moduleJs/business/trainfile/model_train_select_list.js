export default {
    paramsBeforeRequest: function (params) {
        // this:指向src/components/frame/Common/MList/index.vue页面作用域
        return params
    },
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true }
    ],
    renderField: {
        upStatusDesc: {
            /**
             * @param {Object} h 渲染函数参数对象
             * @param {Object} context 对象（包含props,children,slots,scopedSlots,parent,listeners,injection）
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前字段值
             * @param {Number} index 该行下角标
             * @param {Object} fieldObj 字段对象
             * @param {Object} scope 表格项对象
             */
            render: function (h, context, row, column, cellValue, index, fieldObj, scope) {
                let upStatus = row.upStatus
                if (upStatus == null || upStatus == 0 ) {
                    return (<span>未启用</span>)
                } else if (upStatus == 1) {
                    return (<span style="color:green">启用</span>)
                }else{
                    return (<span style="color:red">未识别</span>)
                }
            }
        },
        name: {
             /**
             * 格式化展示
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前表格项值
             * @param {Number} index 所在行下角标
             * @param {Object} fieldObj 字段所在列的配置数据
             */
             formatter: function (row, column, cellValue, index, fieldObj) {
                // this: 作用域指代核心列表[mList/index]所在页面作用域
                    return `<span style="color:#0373ce">${cellValue}</span>`
            },
            click: function (row, column, cellValue, index, fieldObj) {
                window.open(`/#/module/stander/edit/maas_train_file/${row.id}`, '_blank');
            }
        }
    },
}