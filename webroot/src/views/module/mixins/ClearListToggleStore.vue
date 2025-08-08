<script>
import { Remove_Store_ListItems } from '@/service/list-item-ids'
import GlobalConst from '@/service/global-const'
const Route_Clear_Store = function (to, from, next) {
    // 目前这里的逻辑主要用于表单页面的上一条/下一条切换
    //     存储了列表数据，这里才可以进行切换，离开页面时则需要清除存储的列表数据
    if (this.detailId) {
        let _fromId = from.params?.id
        let _toId = to.params?.id
        // 若去往的页面没有详情id，或者为add，则必须不是上一条/下一条的切换操作，则离开后直接清除缓存
        if (!_toId || _toId === GlobalConst.form.newId) {
            Remove_Store_ListItems.call(this)
        } else {
            let _fromFullPath = from.fullPath.replace(_fromId, '')
            let _toFullPath = to.fullPath.replace(_toId, '')
            // 上一条/下一条切换时，只会变更详情id，上面已经删除掉详情id，这里期望为一致，不一致则直接清除缓存
            if (_fromFullPath !== _toFullPath) {
                // 当表单切换上一条/下一条时，除了id是不一样的，其他路径是一样的，所以这里去除id后进行判断，如果跳转去的页面跟当前格式不一致，则清除之前的混缓存
                Remove_Store_ListItems.call(this)
            }
        }
    }
    // 执行next，让路由跳下去
    next()
}
export default {
    // 页面路由组件没变，路由地址更新
    beforeRouteUpdate (to, from, next) {
        Route_Clear_Store.call(this, to, from, next)
    },
    // 离开前事件
    beforeRouteLeave (to, from, next) {
        Route_Clear_Store.call(this, to, from, next)
    }
}
</script>