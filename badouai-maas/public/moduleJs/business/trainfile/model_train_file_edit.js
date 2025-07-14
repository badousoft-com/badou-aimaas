export default {
    beforeRender: function () {
        // this: 指向moduleForm/index页面所在作用域
        // 例如：this._fieldList可以拿到所有表单字段数据，你可以进行修改
        let dataFormat = this.fieldList.find(o => o.name === 'dataFormat')
        this.$route.query.dataFormat = dataFormat.value
    }
}