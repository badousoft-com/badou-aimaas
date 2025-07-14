export default {
    paramsBeforeRequest: function (params) {
        let arrTemp = []
        // 字符串转数组
        arrTemp = JSON.parse(params.searchParam)
        // 推入当前当前用户
        if (!arrTemp.some(item => item.name === 'creator')) {
            arrTemp.push({ "name": "creator", "value": this.$store.getters.userInfo.id, "type": "text-query", "tagName": "" })
        }
        // 数组转字符串
        params.searchParam = JSON.stringify(arrTemp)
        return params
    },
    buttons: [
        { id: 'back', name: '返回', icon: 'export', type: 'danger' ,click: function (btnObj) {
            this.pushPage({
                path: `/myProject/maas_problem_data_new_center/placeholder`,
                title: '样本数据广场'
            })
           
        }},
       
    ]
}