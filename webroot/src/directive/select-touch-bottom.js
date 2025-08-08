/*
 * @Description: el-select 触底指令
 * @FilePath: /src/directive/select-touch-bottom.js
 */
export function touchBottom (el, binding) {
    // 获取element-ui定义好的scroll盒子
    const SELECTWRAP_DOM = el.querySelector('.el-select-dropdown .el-select-dropdown__wrap')
    SELECTWRAP_DOM && SELECTWRAP_DOM.addEventListener && SELECTWRAP_DOM.addEventListener('scroll', function () {
        const CONDITION = this.scrollHeight - this.scrollTop <= this.clientHeight
        if (CONDITION) {
            binding.value()
        }
    })
}