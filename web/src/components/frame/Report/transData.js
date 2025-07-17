/*
 * 报表数据转化js文件
 */
// 默认存在列
const defalutTableColumn = [
    // { type: 'seq', width: 60, fixed: 'left' },
]
// 最大列数
const MaxColIndex = 100
// 将框架默认SearchBar参数） 转换 （报表/面板=>dynamicParams/dynamicValues搜索参数）
function ConversionParams (e) {
    let result = {}
    if (e && typeof e === 'string') {
        e = JSON.parse(e)
    }
    if (!e || !e.length) {
        return result
    }
    e.forEach(item => {
        // 2020/08/19 多选时间更改传参格式
        if (item.type === 'date-query') {
            result[item.name + 'StartTime'] = item.value.startTime
            result[item.name + 'EndTime'] = item.value.endTime
        } else if (item.type === 'text-query') {
            // 模糊查询：后端加 '%%'，前端不作处理
            // result[item.name] = "'%" + item.value + "%'"
            result[item.name] = item.value
        } else if (item.type === 'tree-query' && item.value && item.value.length > 0) {
            // 树节点处理
            let condition = " ( "
            condition += item.name + " like '" + item.value[0].id + "%' "
            item.value.shift()
            if (item.value && item.value.length > 0) {
                item.value.forEach(i => {
                    condition += " or " + item.name + " like '" + i.id + "%' "
                })
            }
            condition += " ) "
            result[item.name] = condition
        } else if (typeof item.value === 'string' && item.value.split(';').length > 0) {
            result[item.name] = "'" + item.value.split(';').join("','") + "'"
        } else {
            result[item.name] = item.value
        }
    })
    return result
}
// 根据表头（如A12）返回{ rowIndex: 12, columnIndex: 1 }
function GetCellIndexFromPos (pos) {
    if (!!pos) {
        let rowIndex = pos.replace(/[^0-9]/ig, '')
        let colPos = pos.replace(rowIndex, '')
        let columnIndex = 0
        if (/^[A-Z]+$/.test(colPos)) {
            for (let i = 0; i < colPos.length; i++) {
                let residualLen = colPos.length - (i + 1)
                if (residualLen === 0) {
                    columnIndex = columnIndex + (colPos[i].charCodeAt() - 64)
                } else {
                    columnIndex = columnIndex + (colPos[i].charCodeAt() - 64) * Math.pow(26, residualLen)
                }
            }
        }
        return { rowIndex: parseInt(rowIndex), columnIndex: parseInt(columnIndex) }
    }
}
// 将后台返回回来的 classInfo 转化成 styleObj and classNameArr
// 合并单元格
function SetStyleAndClassOrSpan (elementInfo, classInfo) {
    let classNameArr = []
    let styles = {}
    let span = { rowspan: 1, colspan: 1 }
    for (let key in classInfo) {
        let cellClassValue = classInfo[key]
        if (typeof cellClassValue === 'number' && key !== 'fontSize' && key !== 'borderLineType' && key !== 'retract') {
            classNameArr.push(key + cellClassValue)
        } else {
            let styleValue = classInfo[key]
            if (key === 'border') {
                let tem_borderLineType = classInfo.borderLineType
                let borderLineType = tem_borderLineType === 2 ? 'dashed' : (tem_borderLineType === 1 ? 'dotted' : 'solid')
                let borderColor = classInfo.borderLineColor || 'blue'
                let borderStr = classInfo.border
                if (!!borderStr) {
                    if (borderStr.indexOf('1') > -1) {
                        styles.borderTop = `1px ${borderLineType} ${borderColor}`
                    }
                    if (borderStr.indexOf('2') > -1) {
                        styles.borderRight = `1px ${borderLineType} ${borderColor}`
                    }
                    if (borderStr.indexOf('3') > -1) {
                        styles.borderBottom = `1px ${borderLineType} ${borderColor}`
                    }
                    if (borderStr.indexOf('4') > -1) {
                        styles.borderLeft = `1px ${borderLineType} ${borderColor}`
                    }
                    if (borderStr.indexOf('0') > -1) {
                        styles.border = `none`
                    }
                }
            } else if (key === 'fontColor') {
                styles.color = classInfo.fontColor || '#333'
            } else if (key === 'font') {
                styles.fontFamily = classInfo.font || ''
            } else if (key === 'fontSize') {
                styles.fontSize = classInfo.fontSize ? (classInfo.fontSize + 'px') : '12px'
            } else if (key === 'retract') {
                styles.textIndent = classInfo.retract + 'px'
            } else {
                styles[key] = styleValue
            }
        }
    }
    // 当开始单元格不等于结束单元格，说明有合并单元格
    if (!!elementInfo && !!elementInfo.endCellIndex && elementInfo.startCellIndex !== elementInfo.endCellIndex) {
        // 取开始、结束单元格的行列index
        let startPosObj = GetCellIndexFromPos(elementInfo.startCellIndex)
        let endPosObj = GetCellIndexFromPos(elementInfo.endCellIndex)
        let rowspan = endPosObj.rowIndex - startPosObj.rowIndex + 1
        let colspan = endPosObj.columnIndex - startPosObj.columnIndex + 1
        span = {
            rowspan: rowspan,
            colspan: colspan
        }
    }
    return { classNameArr, styles, span }
}
/**
 * @description: 根据列数获取表头名字
 * @param {Number} colIndex：第几列
 * @param {String} titleL: 辅助函数
 * @return {String}：列名
 */
function find26Code (colIndex = 0, titleL = '') {
    if (colIndex < 26) {
        titleL = titleL + String.fromCharCode(65 + colIndex)
        return titleL
    } else {
        // 积
        let fac = parseInt(colIndex / 26) > 0 ? parseInt(colIndex / 26) - 1 : 0
        if (fac >= 26) {
            this.find26Code(fac - 26, titleL + 'A')
        } else {
            // 余数
            let remainder = colIndex % 26
            titleL = titleL + String.fromCharCode(65 + fac) + String.fromCharCode(65 + remainder)
            return titleL
        }
    }
}
/**
 * @description: 新增表格数据项
 * @param {Number}} n：结束位置
 * @param {Array} data：组装的原数组
 * @return {Array}：组装完成的数据
 */
function findList (n = 1, data = []) {
    let start = data.length
    if (n > start) {
        for (let i = start; i < n; i++) {
            data[i] = {}
        }
    }
    return data
}
/**
 * @description: 新增列或者更改列名
 * @param {Number} col：新增的最大列数，或需要更改列名的index
 * @param {String} columnName：更改的列名
 * @param {Array} data：原数据
 * @return {Array}：更改或新增完成的数据
 */
function setColumn (col = 0, columnName, data = []) {
    let len = data.length - defalutTableColumn.length
    if (columnName) {
        data[col - 1].field = columnName
        return data
    }
    if (col <= len || col >= MaxColIndex) {
        return data
    } else {
        let colObj = {}
        if (col > 10 || len > 10) {
            colObj.width = 120
        }
        for (let i = len; i < col; i++) {
            colObj.title = find26Code(i)
            colObj.field = find26Code(i)
            data.push(JSON.parse(JSON.stringify(colObj)))
        }
    }
    return data
}
/**
 * @description: 组装report所需要的表格数据
 * @param {Object} data：原数据
 * @return {Object}：
 *          classInfoList, class数组
 *          stylesList, 样式数组
 *          spanInfoList, 合并信息
 *          rowInfoList, 行信息（宽度等）
 *          elementInfoList, 原elementInfo
 *          tableColumn, 列数组
 *          tableData, 数据
 *          indexColInfo, 序列行信息
 *          freezeInfo: data.freezeInfo  冻结单元格信息
 */
function SetReportInfo (data) {
    return new Promise((resolve, reject) => {
        let classArr = data.classArr
        let classInfoList = [], stylesList = [], spanInfoList = [],
            rowInfoList = [], elementInfoList = [],
            tableColumn = [], tableData = [],
            indexColInfo = {}
        data.elementArr.forEach((item, i_ele) => {
            let elementInfo = item.elementInfo
            let rowIndex = parseInt(elementInfo.startCellIndex.replace(/[^0-9]/ig, '')) // 从1开始
            if (parseInt(rowIndex) >= 0) { // 排除了非数字的情况
                let startPosObj = GetCellIndexFromPos(elementInfo.startCellIndex)
                let endPosObj = GetCellIndexFromPos(elementInfo.endCellIndex)
                let columnIndex = endPosObj.columnIndex || startPosObj.columnIndex || elementInfo.columnIndex
                tableData = findList(Math.max(rowIndex, endPosObj.rowIndex), tableData)
                tableColumn = setColumn(Math.max(columnIndex, startPosObj.columnIndex), '', tableColumn)
                // 如果有columnName有值的话，将原来的列field设置为columnName的值
                if (tableColumn[startPosObj.columnIndex - 1].field !== elementInfo.columnName && elementInfo.columnName) {
                    tableColumn = setColumn(startPosObj.columnIndex, elementInfo.columnName, tableColumn)
                }
                let colTilte = elementInfo.columnName || tableColumn[startPosObj.columnIndex - 1].field
                // 如果识别到ccolTitle的值为null或undefined，则使用columnIndex的所对应的列值
                if (!colTilte) {
                    colTilte = find26Code(columnIndex)
                }
                let obj = tableData[rowIndex - 1] || {}
                obj[colTilte] = elementInfo.content || ''
                let classInfo = SetStyleAndClassOrSpan(item.elementInfo, (classArr[elementInfo.classInfoId] || {}))
                // 单元格所含class
                classInfoList[rowIndex - 1] = classInfoList[rowIndex - 1] || {}
                classInfoList[rowIndex - 1][colTilte] = classInfo.classNameArr
                // 单元格所含style
                stylesList[rowIndex - 1] = stylesList[rowIndex - 1] || {}
                stylesList[rowIndex - 1][colTilte] = classInfo.styles
                // 单元格所含element
                elementInfoList[rowIndex - 1] = elementInfoList[rowIndex - 1] || {}
                elementInfoList[rowIndex - 1][colTilte] = item.elementInfo
                // 单元格行属性
                if (item.elementInfo.height) {
                    rowInfoList[rowIndex - 1] = rowInfoList[rowIndex - 1] || {}
                    rowInfoList[rowIndex - 1].height = item.elementInfo.height + 'px'
                }

                if (item.elementInfo.width) {
                    // 单元格列属性
                    let col_obj = tableColumn[startPosObj.columnIndex - 1]
                    col_obj.width = item.elementInfo.width
                    tableColumn[startPosObj.columnIndex - 1] = col_obj
                }
                // 当span.colspan>1||span.rowspan>1时，说明存在合并单元格，则需要将除最小单元格外的其他单元格，全部设置span = { rowspan: 0, colspan: 0 }
                if (classInfo.span.rowspan > 1 || classInfo.span.colspan > 1) {
                    // 单元格合并信息span
                    spanInfoList[rowIndex - 1] = spanInfoList[rowIndex - 1] || {}
                    spanInfoList[rowIndex - 1][colTilte] = classInfo.span
                    for (let i = startPosObj.rowIndex; i <= endPosObj.rowIndex; i++) {
                        for (let j = startPosObj.columnIndex; j <= endPosObj.columnIndex; j++) {
                            if (i !== startPosObj.rowIndex || j !== startPosObj.columnIndex) {
                                let temp_field = tableColumn[parseInt(j - 1)].field
                                spanInfoList[parseInt(i) - 1] = spanInfoList[parseInt(i) - 1] || {}
                                spanInfoList[parseInt(i) - 1][temp_field] = { rowspan: 0, colspan: 0 }
                            }
                        }
                    }
                }
                // 序列列数据
                if (elementInfo.isIndex == 1) {
                    indexColInfo = { field: colTilte, start_row: rowIndex + classInfo.span.colspan - 1 }
                }
                tableData[rowIndex - 1] = JSON.parse(JSON.stringify(obj))
            }
        })
        Promise.all(tableData).then((res) => {
            let result = {
                classInfoList,
                stylesList,
                spanInfoList,
                rowInfoList,
                elementInfoList,
                tableColumn,
                tableData,
                indexColInfo,
                freezeInfo: data.freezeInfo
            }
            resolve(result)
        }).catch(() => {
            reject({})
        })
    })
}
export {
    ConversionParams,
    SetStyleAndClassOrSpan,
    GetCellIndexFromPos,
    SetReportInfo
}