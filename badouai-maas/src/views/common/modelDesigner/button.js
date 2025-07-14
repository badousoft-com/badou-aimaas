// id: 事件id
// name: 按钮名称
// icon： 图标名称
// click： 点击事件【考虑：不使用handle,可能还会有其他事件，例如hover等，拓展性考虑使用click】
let defaultButtons = [
    {
        id: 'cancel',
        name: '取消',
        icon: 'cancel',
        type: 'warning',
        click: function () {
            this.moduleEditCancel()
        }
    }, {
        id: 'save',
        name: '确定',
        icon: 'save',
        type: 'primary',
        click: function (btnObj, event) {
            // this: bd-module-edit组件作用域
            let formRef = this.$refs?.moduleForm?.$refs?.edit
            if (!formRef) return
            let _this = this
            formRef.validateForm().then((res) => {
                // this：指向pageSetting所在作用域

                // 处理附件的值转化，右侧产出单附件格式为数组，但左侧需要为对象
                this.transformSingleAttach().update.call(this, res)
                // 获取当前所点击字段的索引
                let [ configIndex, fieldIndex ] = _this.activeFieldPos
                // 获取当前所点击的字段
                let fieldObj = _this.fieldsList[fieldIndex]
                // 抛物线动画
                let aimEl = this.$refs?.[this.getRefId(configIndex, fieldIndex)][0]
                this.throwAnimate(event, aimEl).then(() => {
                    // 将已更改的值覆盖原来的值
                    let fieldValue = { ...fieldObj,  ...res}
                    _this.$set(_this.fieldsList, fieldIndex, fieldValue)
                    this.mainScope.customSetting.isChangeInfo[this.mdCode] = true
                    this.$message.success('左侧数据已更新')
                    // _this.moduleEditCancel()
                })
            })
        }
    }
]
export default defaultButtons