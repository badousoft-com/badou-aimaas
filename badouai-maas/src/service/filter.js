/* 过滤器相关模块 */

import { Share_Filter as Share_Filter_Request } from '@/api/frame/filter'
import { Common_Delete } from '@/api/frame/common'
import GlobalConst from '@/service/global-const'

/**
 * 分享过滤器事件
 * @param {Array} selection 当前操作的过滤器项数组
 * @returns
 */
function Share_Filter (selection) {
    if (selection.length === 0) {
        this.$message.warning('请先选择一个过滤器进行操作')
        return
    }
    let canDelStatus = selection.every(i => i.state === '1')
    if (!canDelStatus) {
        this.$message.warning(`当前所选数据包含系统过滤器，不支持分享`)
        return
    }
    // 调用用户地址本选择转办给谁
    this.addressBook({
        title: `请选择分享过滤器的人员`,
        addressTypes: '2-0-20-2', // 新添加这个属性，从这个进行走通，尽量不影响原有属性
    }).then(data => {
        // 获取当前选中的所有地址本的名称
        let _filterNames = selection.map(i => i.name).join(GlobalConst.separator)
        // 获取当前地址本需要分享给的所有用户的名称
        let shareUserName = data.map(i => i.name).join(GlobalConst.separator)
        this.$confirm(`确定将过滤器【${_filterNames}】分享给【${shareUserName}】`, '分享过滤器', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            // 定义参数
            let params = {
                userIds: data.map(i => i.id).join(GlobalConst.separator),
                filterIds: selection.map(i => i.id).join(GlobalConst.separator)
            }
            // 执行分享请求事件
            Share_Filter_Request(params).then(res => {
                if (res?.hasOk) {
                    this.$message.success(this.getMessage(res?.message))
                } else {
                    this.$message.error(this.getMessage(res?.message, false))
                }
            })
        })
    })
}

/**
 * 过滤器的删除事件
 * @param {Array} selection 当前操作的过滤器项数组
 * @returns
 */
function Del_Filter (selection) {
    return new Promise((resolve, reject) => {
        if (selection.length === 0) {
            this.$message.warning('请先选择一个过滤器进行操作')
            return
        }
        // 只有用户自定义的过滤器可以删
        // TODO：后面等菜单支持配置将该模块给对应管理员时再开放
        let canDelStatus = selection.every(i => i.state === '1')
        if (!canDelStatus) {
            this.$message.warning(`当前所选数据包含系统过滤器，不允许删除`)
            return
        }
        // 获取当前选中的所有过滤器的名称
        let filterNames = selection.map(i => i.name).join(GlobalConst.separator)
        this.$confirm(`确定删除以下过滤器【${filterNames}】？`, '删除过滤器', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            // 定义过滤器对应的模型编码
            let filterCode = 'filter'
            let params = {
                mdCode: filterCode,
                ids: selection.map(i => i.id).join(',')
            }
            // 执行删除过滤器的请求
            Common_Delete(params).then(res => {
                if (res?.hasOk) {
                    this.$message.success(this.getMessage(res?.message))
                    resolve(true)
                } else {
                    this.$message.error(this.getMessage(res?.message, false))
                    resolve(false)
                }
            })
        })
    })
}

// 引入举例
// import {  } from '@/service/filter'
export {
    Share_Filter,
    Del_Filter
}