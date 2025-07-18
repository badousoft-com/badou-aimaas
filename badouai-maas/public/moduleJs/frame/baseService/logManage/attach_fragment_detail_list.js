/*
 * @FilePath: /public/moduleJs/frame/baseService/logManage/attach_fragment_detail_list.js
 * @Description: 附件分片列表
 */
export default {
    renderField: {
        fileSize: {
            formatter: function (row, column, cellValue, index, fieldObj) {
                // this: 作用域指代核心列表[mList/index]所在页面作用域
                let size = cellValue
                try {
                    size = parseFloat((Number(cellValue) / 1024 / 1024).toFixed(2)) + 'M'
                } catch (err) {
                }
                return `<div class="successTag">${size}</div>`
            },
        }
    }
}