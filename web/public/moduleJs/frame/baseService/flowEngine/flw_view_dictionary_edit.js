export default {
    afterModuleJSON: function (module) {
        // 设置视图管理：视图的url提供可选默认视图
        ['previewUrl', 'viewUrl', 'doneUrl', 'spreadUrl'].forEach(i => {
            // 获取url视图字段
            let field = module?.fieldList.find(j => j.name === i)
            let valueKey = 'text'
            // 设置表单编辑类型
            field.type = 'autoComplete'
            // 设置初始可选数据：框架默认提供的视图地址
            field.options = [
                { [valueKey]: '/module/flow/workEdit/${id}', desc: '审核-表单编辑' },
                { [valueKey]: '/module/flow/workView/${id}', desc: '审核-表单查看' },
                { [valueKey]: '/module/flow/tableView/${id}/1', desc: '审核-表单查看(表格排版)' },
                { [valueKey]: '/module/flow/tableView/${id}', desc: '查看-表单查看(表格排版)' },
                { [valueKey]: '/module/flow/view/${id}', desc: '查看-表单查看' },
            ]
            // 指定值键名
            field.valueKey = valueKey
            // 设置选项不过滤，始终展示
            field.autoFilter = false
        })
        // 返回模型数据
        return module
    },
}