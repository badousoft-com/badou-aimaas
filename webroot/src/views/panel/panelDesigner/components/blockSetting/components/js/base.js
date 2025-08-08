/*
 * @LastEditTime: 2021-05-14 16:05:08
 * @Description: 可视化面板 - 块设置 - 基础设置
 */
export default {
    /**
     * 字段自定义change事件
     * @param {*} fielaName 字段名
     * @param {*} value change后对应值
     */
    fieldChange: function (fielaName, value) {
        this.$parent.$emit('fieldChange', fielaName, value)
    },
}