import { Get_Route_Array } from '@/api/frame/desinger/field'
export default {
    columnNum: 3,
    /**
     * 字段自定义change事件
     * @param {*} fielaName 字段名
     * @param {*} value change后对应值
     */
    fieldChange: function (fielaName, value) {
        if (this.$attrs?.mainScope?.customSetting?.isChangeInfo) {
            this.$attrs.mainScope.customSetting.isChangeInfo[this.mdCode] = true
        }
    }
}