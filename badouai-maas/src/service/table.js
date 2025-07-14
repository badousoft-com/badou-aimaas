import { Has_Value, Remove_Space } from '@/utils'
import GlobalConst from './global-const'

/**
 * 获取表格的列合并所占格子span，返回格式为[1,1],表示当前格子占1行1列，[0,0]表示不占格子
 * @param {Object} el-table列表返回的单个格子的属性项
 *      @param {Object} row 当前行
 *      @param {Object} column 当前列
 *      @param {Number} rowIndex 当前行号
 *      @param {Number} columnIndex 当前列号
 * @param {Array} data 列表数据(目前看计算格子所占是需要数据的前后进行对比的，所以是需要总数据的)
 * @param {Object} option 配置项
 *      @param {String} column 【目前暂不支持】需要处理列合并的字段，多个字段以逗号隔开如'a,b'；
 *      @param {String} row 需要处理行合并的字段，多个字段以逗号隔开如'a,b' 【默认支持按值相同进行合并，若后续需要按不同规则传入合并，则按照'a:same, b:sum',表示a字段按照值一样就行合并，b字段按照相加和进行行合并，目前暂未支持仅先提出设想】
 */
function Get_Table_Span ({ row, column, rowIndex, columnIndex }, data, option) {
    // 获取需要合并的列或者行数据
    let { column: columnField, row: rowField } = option
    if (rowField) {
        // 获取数据源长度
        let _len = data && data.length || 0
        // 如果数据总条数没有超过1,则不需要使用合并(合并至少两条数据)，直接return即可
        if (!(_len > 1)) return
        // 获取需要处理的字段键名数组，如['a','b']
        let _keyList = Remove_Space(rowField).split(GlobalConst.separator)
        let _key = column.property
        if (!_keyList.includes(_key)) return
        // 定义获取字段值
        let _val = row[_key]
        // 如果项不存在值，则直接返回一个格子回去。默认无值的不进行合并
        if (!Has_Value(_val)) {
            return [1, 1]
        } else {
            // 列的上一格存在且上一格的值与当前的值一致时，直接返回[0, 0]
            //     默认规则：当列合并时，优先给初始合并格赋予最大格子，其他人的格子不占位
            //     所以同值的情况，格子非首位时，不需要占据格子
            if (data[rowIndex - 1] &&
                _val === data[rowIndex - 1][_key]) {
                return [0, 0]
            } else {
                // 上一格子不存在或者上一个格子值与当前格子值不同时，当前格子为首位
                //     接下来需要判断从当前格子开始，下格子有多少个与之相同的

                // 当下格子不存在时，标志着不需要与别人合并了，则直接返回一个格子位即可
                if (!data[rowIndex + 1]) {
                    return [1, 1]
                } else {
                    // 若下格子存在

                    // 从当前位置截取源数据，获取与当前格子值不同的下角标
                    let _restData = data.slice(rowIndex + 1)
                    let _diffIndex = _restData.findIndex(i => i[_key] !== _val)
                    // 若下所有格子中没有存在不同值的，即全部与当前格子值相同
                    if (!~_diffIndex) {
                        return [_restData.length + 1, 1]
                    } else {
                        return [_diffIndex + 1, 1]
                    }
                }
            }
        }
    }
    if (columnField) {
        alert('暂未支持行合并')
    }
}

// import { Get_Table_Span } from '@/service/table'
export {
    // 获取表格的列合并所占格子span，返回格式为[1,1],表示当前格子占1行1列，[0,0]表示不占格子
    Get_Table_Span,
}