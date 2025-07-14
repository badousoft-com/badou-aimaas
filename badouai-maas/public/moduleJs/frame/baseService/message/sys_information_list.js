/*
 * @Description: 我的消息列表
 */
import { Set_Message_Read } from '@/api/frame/message'
export default {
    buttons: [
        { id: 'add', isHide: true },
        { id: 'edit', isHide: true },
        { id: 'view', isHide: true },
        { id: 'view', isHide: true },
        { id: 'import', isHide: true },
        {
            id: 'toRead',
            icon: 'right',
            name: '转已读',
            type: 'success',
            priority: 1,
            click: function (btnObj) {
                let selection = this.getSelection()
                if (selection.length < 1) {
                    this.alert('请至少选择一行!')
                    return
                }
                let ids = selection.filter(i => i.isRead === 0).map(o => o.id)
                if (!ids.length) {
                    this.$message.warning('所选数据已全部为已读')
                    return
                }
                btnObj.loading = true
                Set_Message_Read({ids: ids.join(',')}).then(res => {
                    if (res.hasOk) {
                        this.$message.success('设置已读成功')
                        this.listPageRef().init()
                    } else {
                        this.$message.warning('操作失败')
                    }
                }).finally(() => {
                    btnObj.loading = false
                })
            }
        }
    ]
}
