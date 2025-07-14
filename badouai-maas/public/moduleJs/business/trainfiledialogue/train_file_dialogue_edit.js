export default {
      /**
    * @param {Array} module 模型对象数据
    */
      afterModuleJSON: function (module) {
        let dataFormat = this.$route.query.dataFormat
        let fieldList = module.fieldList
        //指令监督 只有回答的内容 屏蔽掉其他的
        if (dataFormat == '1') {
            let chosena = fieldList.find(o => o.name === 'chosena')
            chosena.type = 'hidden'
            let rejecteda = fieldList.find(o => o.name === 'rejecteda')
            rejecteda.type = 'hidden'
            let ktoTag = fieldList.find(o => o.name === 'ktoTag')
            ktoTag.type = 'hidden'
        } else if (dataFormat == '0') {
            //预训练 只有内容 屏蔽掉其他的
            let question = fieldList.find(o => o.name === 'question')
            question.display = '训练内容(content)'
            let feedback = fieldList.find(o => o.name === 'feedback')
            feedback.type = 'hidden'
            let input = fieldList.find(o => o.name === 'input')
            input.type = 'hidden'
            let chosena = fieldList.find(o => o.name === 'chosena')
            chosena.type = 'hidden'
            let rejecteda = fieldList.find(o => o.name === 'rejecteda')
            rejecteda.type = 'hidden'
            let system = fieldList.find(o => o.name === 'system')
            system.type = 'hidden'
            let ktoTag = fieldList.find(o => o.name === 'ktoTag')
            ktoTag.type = 'hidden'
        } else if (dataFormat == '50') {
            //偏好 只有问题和劣质优质回答 屏蔽掉其他的
            let feedback = fieldList.find(o => o.name === 'feedback')
            feedback.type = 'hidden'
            let input = fieldList.find(o => o.name === 'input')
            input.type = 'hidden'
            let system = fieldList.find(o => o.name === 'system')
            system.type = 'hidden'
            let ktoTag = fieldList.find(o => o.name === 'ktoTag')
            ktoTag.type = 'hidden'
        } else if (dataFormat == '53') {
            //KTO 只有问题、回复和KTO 屏蔽掉其他的
            let chosena = fieldList.find(o => o.name === 'chosena')
            chosena.type = 'hidden'
            let rejecteda = fieldList.find(o => o.name === 'rejecteda')
            rejecteda.type = 'hidden'
            let system = fieldList.find(o => o.name === 'system')
            system.type = 'hidden'
        }
        
        // 执行你的数据变更
        // do something change the module data
        // 注意：修改完之后必须将模型数据返回
        return module
    }
}