import Vue from 'vue'
const before = function (option) {
    if (!option) {
        let errorTip = `请传入有效的配置`
        Vue.prototype.$message.warning(errorTip)
        console.error(`${errorTip}：调用Visulize.Sort.init方法时没有传入参数，期望应为Visulize.Sort.init(option), option为对象`)
        return
    }
    let {
        data,
        displayNameKey = 'displayName',
        displayNameLabel,
    } = option
    if (!(data && data.constructor === Array)) {
        let errorTip = `当前传入模块数据源格式异常`
        Vue.prototype.$message.warning(errorTip)
        console.error(`${errorTip}：Visulize.Sort.init({data})中data为数组格式`)
        return
    }
    if (!(data && data.length > 0)) {
        let errorTip = `当前传入模块数据为空`
        Vue.prototype.$message.warning(errorTip)
        console.error(`${errorTip}：确保Visulize.Sort.init({data})中data有值且长度>0`)
        return
    }
    let errorNameTimes = data.filter(i => !i[displayNameKey]).length
    if (errorNameTimes > 0) {
        Vue.prototype.$message.warning(`请确保模块的【${displayNameLabel || displayNameKey}】是有值的，目前存在${errorNameTimes}个值为空`)
        return
    }

    return {
        title: '排序(按住模块-拖动排序)',
        isAutoFix: true,
        handlerList: [ // 操作的按钮列表
            { id: 'save', name: '确定', icon: 'save', type: 'primary', click: function (itemObj) {
                // 获取排序组件页面所在作用域
                let that = this.$refs.sortView
                // 获取排序前的数据
                let _old = that.data.map(i => i[that._displayNameKey]).join()
                // 获取排序后的数据
                let _new = that.tempData.map(i => i[that._displayNameKey]).join()
                // resolve(data:Array, hasChange:Boolean)
                this.resolve({
                    isChange: _old !== _new,
                    data: that.tempData
                })
            }}
        ],
        ...option
    }
}
export default {
    before
}