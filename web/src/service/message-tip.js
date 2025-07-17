import Vue from 'vue'
const Message_Tip = {
    warning: (tip) => {
        console.error(`
【来自框架的提示】：=======
    ${tip}
=========================`)
        Vue.prototype.$message.warning(tip)
    },
    error: (tip) => {
        console.error(`
【来自框架的提示】：=======
    ${tip}
=========================`)
        Vue.prototype.$message.error(tip)
    }
}

export {
    Message_Tip
}