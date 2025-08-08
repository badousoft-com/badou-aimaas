
/**
 * 根据id获取按钮数组中对应项下的按钮事件
 * @param {String} id 标识id
 * @param {Array} btnArr 按钮数据
 * @param {String} btnId 按钮标识符
 * @returns 获取事件
 */
function Get_Btn_ClickFun (id, btnArr, btnId = 'id') {
    if (!(id && btnArr && btnArr.constructor === Array && btnArr.length > 0)) return
    return btnArr.find(i => i[btnId] === id)?.click
}

/**
 * 确保同步执行Promise/Function事件后，再执行下一步
 * @param {String} evtName 事件关键词
 * @param {Object} evtObj 事件对象总和
 * @param {*} 其他参数
 */
async function Event_Expand () {
    return new Promise(async (resolve, reject) => {
        let [evtName, evtObj, ...busiParams] = arguments
        if (!(evtObj &&
              evtName &&
              evtName.constructor === String &&
              evtObj.constructor === Object &&
              evtName in evtObj)) {
            resolve()
            return
        }
        // 获取事项
        let _evt = evtObj[evtName]
        if (_evt && _evt.constructor === Function) {
            // 获取函数传入参数
            let _funParams = _evt.toString().replace(/\s+/g, '').match(/\((.*?)\)/)[1]
            // _funParams 可能存在的几种情况
            //     1. 含有resolve           -- 此时为异步
            //     2. 不含有resolve         -- 此时为同步
            // resolve参数若存在，必须放置第一个，所以这里判断也以此为规则
            if (_funParams && _funParams.split(',')[0] === 'resolve') {
                // 传递参数，等待事件中的resolve()执行才能下一步
                _evt.call(this, resolve, ...busiParams)
            } else {
                _evt.call(this, ...busiParams) // 此时为同步，不需要等待处理结果，可以直接resolve
                resolve()
            }
        } else {
            resolve()
        }
    })
}

// import { Event_Expand } from '@/service/event-expand'
export {
    // 根据id获取按钮数组中对应项下的按钮事件
    Get_Btn_ClickFun,
    // 确保同步执行Promise/Function事件后，再执行下一步
    Event_Expand,
}