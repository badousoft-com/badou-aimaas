import { Get_Data_By_Path } from '@/utils'
import { S_Storage } from '@/utils/storage'
import { finalRequest } from './request'

// 定义获取列表数据存在在缓存中的键名
const Get_Name = (mdCode) => `${mdCode}_listItemInfo`

/**
 * 存储列表数据，以便编辑时可以取出，用于实现上一条/下一条的数据切换
 * @param {Array} selection 当前选择的列表数据
 */
function Store_ListItems (selection) {
    // 获取数据总条数
    let _total = this.tableListRef.total
    // 如果总数据只有一条就没必要进行上一条/下一条的显示切换了
    if (_total === 1) return
    // 获取数据id
    let _currentRowId = selection[0].id
    // 获取当前数据在已有数据中的排序号
    let _currentIndex = this.tableData.findIndex(i => i.id === _currentRowId)
    // 获取分页信息：单页数据量，页数
    let { pageNo = 0, perPageSize = 0 } = this.finalRequestConfig.params || {}
    let _listInfo = {
        requestConfig: this.finalRequestConfig, // 列表数据请求的相关配置,值为{url,params,...}
        resPath: this.tableListRef.optionResPath, // Rows数据在接口响应数据中的路径
        total: _total, // 数据总条数
        currentIndex: _currentIndex, // 当前数据在currentDetails中对应的下角标
        currentIndexInTotal: (pageNo - 1) * perPageSize + _currentIndex, // 当前数据在总数中对应的下角标
        currentDetails: this.tableData.map(i => i.id), // 当前列表已请求的列表数据id项集合 [id1,id2,id3,id4]
    }
    // 存储数据，编辑页再取出
    S_Storage.setObj(Get_Name(this.mdCode), _listInfo)
}

/**
 * 获取存储的列表数据（编辑中使用）
 */
async function Get_ListItems () {
    let _listInfo = S_Storage.getObj(Get_Name(this.mdCode))
    if (!_listInfo) return
    // 模型列表页面由于数据量不确定，一般是使用分页避免一次性加载过多数据
    // 使用分页的问题是，只会获取到当前页的数据，无法获取全部数据，所以下面分情况讨论
    //     1. 如果当前页请求的数据就是全部的数据了，那进入编辑页之后就不需要考虑再获取其他数据的详情id了
    //     2. 如果当前不是全部，则进入编辑后，随着上一页/下一页的切换，到临界值时则需要动态请求更多数据以获取详情id
    let {
        requestConfig, // 列表数据请求的相关配置,值为{url,params,...}
        resPath, // Rows数据在接口响应数据中的路径
        total, // 数据总条数
        currentIndex,  // 当前数据在currentDetails中对应的下角标
        currentIndexInTotal, // 当前数据在总数中对应的下角标
        currentDetails, // 当前列表已请求的列表数据id项集合 [id1,id2,id3,id4]
    } = _listInfo
    // 在编辑页面，如果前面在列表存储的列表数据就是全部了，则这里就直接返回数据
    if (currentDetails.length !== total) {
        let getIds = (data) => data.map(i => i.id)
        // 若currentDetails存储的是[11-20]条的数据，当前即将展示的是第11条，则需要将1-10条也加载好，才能支持上一条按钮的点击
        if (currentIndex === 0) {
            // 当前为第一条数据时，则不需要再请求再前一条的数据，所以这里处理的是非第一条的
            //     例如单页10条，则这里处理的是第11条，第21条
            if (currentIndexInTotal !== 0) {
                // 更新页码--> 更新为上一页
                requestConfig.params.pageNo -= 1
                let _data = Get_Data_By_Path(await finalRequest(requestConfig), resPath) || []
                // 添加进当前可操作的数据中
                currentDetails.unshift(...getIds(_data))
                // 更新当前数据在可操作数据中的下角标
                _listInfo.currentIndex = currentIndex + _data.length
            }
        }
        // 若currentDetails存储的是[11-20]条的数据，当前即将展示的是第20条，则需要将21-30条也加载好，才能支持下一条按钮的点击
        if (currentDetails.length - 1 === currentIndex) {
            // 当前为最后一条数据时，则不需要再请求后一组数据，这里处理的是非最后一条的
            //     例如单页10条，总数22.则这里处理的是第10条，第20条，不需要处理第22条
            if (total - 1 !== currentIndexInTotal) {
                // 更新页码--> 更新为下一页
                requestConfig.params.pageNo += 1
                let _data = Get_Data_By_Path(await finalRequest(requestConfig), resPath) || []
                // 添加进当前可操作的数据中
                _listInfo.currentDetails.push(...getIds(_data))
            }
        }
    }
    // 更新存储信息
    S_Storage.setObj(Get_Name(this.mdCode), _listInfo)
    // 返回数据
    return Promise.resolve(_listInfo)
}

/**
 * 上一条/下一条切换时，请求更多数据
 * @param {Number} addIndex 1/-1
 */
function Update_Current_ListItem (addIndex) {
    let {
        currentIndex, // 当前数据在currentDetails中对应的下角标
        currentIndexInTotal, // 当前数据在总数中对应的下角标
        currentDetails, // 当前列表已请求的列表数据id项集合 [id1,id2,id3,id4]
    } = this.listInfo
    // 更新当前展示的数据的下角标
    this.listInfo.currentIndex = currentIndex + addIndex
    // 更新当前展示的数据的下角标
    this.listInfo.currentIndexInTotal = currentIndexInTotal + addIndex
    // 存储最新数据
    S_Storage.setObj(Get_Name(this.mdCode), this.listInfo)
    // 接下来刷新页面，使用最新的表单id刷新路由，实现展示上一个/下一个表单
    let {
        path,
        query,
        params
    } = this.$route
    if (params.id) {
        // 使用点击后的数据id替换现有的表单id
        path = path.replace(params.id, currentDetails[this.listInfo.currentIndex])
    }
    // 刷新路由
    this.$router.replace({
        path,
        query
    })
}

/**
 * 删除列表的缓存数据
 */
function Remove_Store_ListItems () {
    S_Storage.removeItem(Get_Name(this.mdCode))
}

// import { Store_ListItems } from '@/service/list-item-ids'
export {
    // 存储列表数据，以便编辑时可以取出，用于实现上一条/下一条的数据切换
    Store_ListItems,
    // 获取存储的列表数据（编辑中使用）
    Get_ListItems,
    // 上一条/下一条切换时，请求更多数据
    Update_Current_ListItem,
    // 删除列表的缓存数据
    Remove_Store_ListItems
}