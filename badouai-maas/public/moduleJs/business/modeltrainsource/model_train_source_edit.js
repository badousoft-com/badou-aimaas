export default {
    saveUrl: function (mdCode) {
        // this:指向moduleEdit/index.vue页面所在作用域
        return `${this.BASEURL}/project/maas/modeltrain/traindatasourcesave/createTrainData?mdCode=${mdCode}`
    },
    dataUrl: function () {
        // this:指向moduleList/index.vue所在页面作用域
        return `${this.BASEURL}/project/maas/modeltrain/traindatasourceedit/editJSON.do`
    },
    beforeRender: function () {
        let doParams = this._fieldList.find(i => i.name === 'trainContent')
        if(!doParams.value){
            let trainContent = '{"conversations": [{"role": "user","content": "请输入您的问题"},{"role": "assistant","content": "请输入您希望回复的内容"}]}\r\n{"conversations": [{"role": "user","content": "修改content里面的内容作为训练内容"},{"role": "assistant","content": "不要使用自带的默认值请修改"}]}'
            doParams.value = trainContent
        }
    },
}
