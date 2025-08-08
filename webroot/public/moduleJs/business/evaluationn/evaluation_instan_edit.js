
export default {
    renderField: {
        totalScore: {
            /**
             * 格式化展示
             * @param {*} value 当前字段值
             * @param {Object} fieldObj 字段对象
             */
            formatter: function (value, fieldObj) {
                if(!value){
                    value = 0
                }
                let qustionCount = this.dataList.find(i => i.name === "qustionCount")
                let targetScore = 0
                if(qustionCount.value && qustionCount.value!= 0){
                    targetScore = qustionCount.value*1
                }
                
                // this: 作用域指代核心表单[mform/index]所在页面作用域
                return `<span>${value}/${targetScore}</span>`
            }
        },
        totalScore: {
            /**
             * 格式化展示
             * @param {*} value 当前字段值
             * @param {Object} fieldObj 字段对象
             */
            formatter: function (value, fieldObj) {
                if(!value){
                    value = 0
                }
                let qustionCount = this.dataList.find(i => i.name === "qustionCount")
                let targetScore = 0
                if(qustionCount.value && qustionCount.value!= 0){
                    targetScore = qustionCount.value*1
                }
                
                // this: 作用域指代核心表单[mform/index]所在页面作用域
                return `<span>${value}/${targetScore}</span>`
            }
        },
        qustionCount: {
            /**
             * 格式化展示
             * @param {*} value 当前字段值
             * @param {Object} fieldObj 字段对象
             */
            formatter: function (value, fieldObj) {
                if(!value){
                    value = 0
                }
                let execCount = this.dataList.find(i => i.name === "execCount")
                if(!execCount.value){
                    execCount.value = 0
                }
                // this: 作用域指代核心表单[mform/index]所在页面作用域
                return `<span>${execCount.value}/${value}</span>`
            }
        }       
    }
}