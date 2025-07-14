export default {
    beforeRender: function () {
        // this.fieldList[0].value = '123'
        this.fieldList[2].value = '"请帮我根据以下内容{{bd.kncontent}}设计1个数据集，具体要求如下：\n" 1、每个数据集包含5个问题与答案\n" 2、每个问题的提问方式与回复方式要结合内容适用的对话场景，模拟提问者与回复者的角色进行提问与回复。如：消费者与企业老板之间的对话，或者是买家与卖家的对话，或者是消费者与行业专家之间的对话等等；\n" +3、数据集中的内容范文如下：\n 问题：田七有什么用途- \n答案1：田七可以化血散淤- \n答案2：田七可以一种药物- 相似问题：与用户问题相关的其他常见问题字符串请直接提供按场景和用户意图分类的问答模式的训练集，无需提供数据集的具体内容。\n4、返回结果以json格式数据返回，格式如下：[{\"question\":\"问题\",\"answer1\":\"答案1\",\"similarQuestions\":\"相似问题\"},{\"question\":\"问题\",\"answer1\":\"答案1\",\"similarQuestions\":\"相似问题2\"}]"'
        // this.fieldList[2].value = '请根据家用电器的产品质量检测知识，设计1个数据集，每组包含至少5个问题。每个数据集中的问题必须从不同的用户角色（如企业老板、车间质量负责人和企业老板的秘书）中提出，每个角色至少提出两个问题。请按照每个角色分类回答，确保每个类别至少有5个问题。回答需要根据家用电子产品的国家标准进行，并应避免使用产品名称或检测项目的非正式术语。数据集中应包含如下信息：\n- 问题：用户可能提出的关于产品质量检测的具体问题- \n答案1：标准检测项目或指标要求的回答- \n答案2：针对用户问题的额外解释或建议- 相似问题：与用户问题相关的其他常见问题字符串请直接提供按场景和用户意图分类的问答模式的训练集，无需提供数据集的具体内容。\n只返回json格式数据，数据格式：[{"question":"问题","answer1":"答案1","similarQuestions":"相似问题"},{"question":"问题","answer1":"答案1","similarQuestions":"相似问题2"}]'
        //查询助手的ID列表.然后获取第一个作为ID默认值
        this.post('/project/apihelper/apihelper/getAssistantList?assistanRolecode=creative').then((res) => {
            if(res.Rows){
                if(res.Rows.length>0){
                    this.fieldList[0].value = res.Rows[0].id
                }
            }
        }).finally(() => {
            // btnObj.loading = false
        })
    },
    buttons: [
       
    ]
}