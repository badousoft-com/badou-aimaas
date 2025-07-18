import frameConst from '@/setting/frame-const'
import businessConst from '@/setting/business-const'
// 默认设置项为框架设置
let GlobalConst = frameConst
// 合并框架设置与业务设置，业务设置优先级会高于框架设置
comb(GlobalConst, businessConst)

// 导出最终项目设置
export default GlobalConst

/**
 * 合并对象，更新finalObj为最新合并后对象
 * @param {*} finalObj 初始对象
 * @param {*} aimObj  需要合并目标对象
 */
function comb (finalObj, aimObj) {
    // 获取对象属性数组
    let aimAttrList = Object.keys(aimObj)
    aimAttrList.forEach(i => {
        if (Array.isArray(aimObj[i])) {
            // 对象下值非数组，直接修改，当前数组不做细节分析
            finalObj[i] = aimObj[i]
        } else if (Object.prototype.toString.call(aimObj[i]) === '[object Object]') {
            // 判断对象下值：若值为对象则继续递归
            comb(finalObj[i], aimObj[i])
        } else {
            // 对象下值非对象，直接修改
            finalObj[i] = aimObj[i]
        }
    })
}
