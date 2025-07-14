/* 存放表单处理的相关函数 */
import GlobalConst from '@/service/global-const'

/**
 * 将表单列表数据按照一列24占比进行重新划分，修改数据结构，这样一行的元素不会影响下一行的展示，避免交错
 * @params dataList:[Array] 初始数据列表
 * @params columnName:[String] 字段名称
 * <el-row>                                  <el-row>
 *      <el-col :span="6"></el-col>               <el-col :span="6"></el-col>
 *      <el-col :span="18"></el-col>  --->        <el-col :span="18"></el-col>
 *      <el-col :span="6"></el-col>           </el-row>
 *      <el-col :span="18"></el-col>          <el-row>
 *  </el-row>                                     <el-col :span="6"></el-col>
 *                                               <el-col :span="18"></el-col>
 *                                           </el-row>
 */
function makeFormShowList (dataList, columnName) {
    // 定义临时存储列表数据
    let tempList = []
    // 结果集
    let resultList = []
    // 定义当前列占比数
    let columnNum = 0
    // 定义最高列占比，参照elementUI，一行占比数峰值是24比例列
    let MaxcolumnNum = 24
    let formShowList = dataList
    formShowList.forEach((i, index) => {
        let itemColumnNum = i[columnName]
        if (columnNum + itemColumnNum <= MaxcolumnNum) {
            columnNum += itemColumnNum
            tempList.push(i)
        } else {
            resultList.push(tempList)
            tempList = []
            tempList.push(i)
            columnNum = itemColumnNum
        }
        if (index === formShowList.length - 1) {
            resultList.push(tempList)
        }
    })
    return resultList
}

/**
 * 获取表单项占比份数(全部24份)
 * @param {String, Number} ratio 单行切割的块数
 * @returns
 */
function Get_Form_ColumnNum (ratio) {
    // 获取通用切割个数
    let _ratio = GlobalConst.form.columnNum
    if (ratio) {
        let _temp = parseInt(ratio)
        _temp && (_ratio = _temp)
    }
    // 返回单行占比数（按照一行24等份）
    return Math.floor(GlobalConst.form.gridNum / _ratio)
}

export {
    // 将表单列表数据按照一列24占比进行重新划分，修改数据结构，这样一行的元素不会影响下一行的展示，避免交错
    makeFormShowList,
    // 获取表单项的实际占比数
    Get_Form_ColumnNum,
}