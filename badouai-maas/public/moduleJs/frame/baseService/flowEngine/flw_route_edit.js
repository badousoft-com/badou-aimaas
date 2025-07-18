export default {
    afterModuleJSON: function (module) {
        let nodeIdField = module.fieldList?.find(i => i.name === 'nodeId')
        if (!nodeIdField.value) {
            let _value = this.$attrs?.currentPageScope?.currentNodeKey
            nodeIdField.value = _value
        }
        // 执行你的数据变更
        // do something change the module data
        // 注意：修改完之后必须将模型数据返回
        return module
    },
}
