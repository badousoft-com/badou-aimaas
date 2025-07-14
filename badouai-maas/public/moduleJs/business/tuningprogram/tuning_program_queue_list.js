
export default {
    renderField: {
        // 字段键名
        planName: {
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
                return `<span style="color:blue" class="bold">${cellValue==null?'':cellValue}</span>`
            },
            // 点击事件（若没有可以不写，去除整块click）
            click: function (row, column, cellValue, index, fieldObj) {
                if(row.taskType == 0){
                    this.pushPage({
                        path: `/module/stander/edit/maas_fine_tuning_plan/${row.planId}`,
                        title: '微调方案查看'
                    })
                }else {

                }
            }
        }
    },
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'add', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'edit', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'delete', name: '导出', icon: 'export', type: 'primary', isHide: true },
    ]
}