import { Has_Value } from '@/utils'
export default {
    buttons: [
        { id: 'add', isHide: true },
        { id: 'edit', isHide: true },
        { id: 'delete', isHide: true }
    ],
    renderField: {
        logLevelDesc: {
            formatter: function (row, column, cellValue, index, vue) {
                // 获取日志级别logLevel，日志级别描述logLevelDesc
                let { logLevel, logLevelDesc } = row
                if (!Has_Value(logLevelDesc)) return
                let className = ''
                // 根据级别定义主题颜色类型
                switch (logLevel) {
                    case 0: className = 'successC'
                        break
                    case 1: className = 'warningC'
                        break
                    case 2: className = 'dangerC'
                        break
                    default:
                        // do something you like
                }
                // 返回对应富文本标签
                return `<span class="${className}">${logLevelDesc}</span>`
            }
        }
    }
}