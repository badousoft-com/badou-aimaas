import { EventBus } from '@/service/event-bus'
// id: 事件id
// name: 按钮名称
// icon： 图标名称
// click： 点击事件【考虑：不使用handle,可能还会有其他事件，例如hover等，拓展性考虑使用click】
let defaultButtons = [
    {
        id: 'back',
        name: '返回',
        icon: 'back',
        type: 'danger',
        click: function () {
            this.$router.go(-1)
        }
    },
    {
        id: 'save',
        name: '保存',
        icon: 'save',
        type: 'primary',
        loading: false,
        click: function (btnObj) {
            // this: bd-module-edit组件作用域       
            let formRef = this.getFormRef()
            // 表单校验
            formRef.validateForm().then((res) => {
                // 设置按钮状态-加载中
                btnObj.loading = true
                // 提交接口
                this.post(this._saveUrl, res).then(res => {
                    if (res?.hasOk) {
                        this.$message.success('保存成功')
                        // 获取保存接口返回详情
                        let resDetail = res.bean?.returnDetails
                        // 处理tab情况，同时满足以下条件时：保存表单不返回上一页，新增时子tab不展示，保存数据后才展示
                        //     1. 当前为新增表单状态
                        //     2. 接口保存后返回数据存在值
                        //     3. 存在关联tab
                        if (!this.detailId &&
                            resDetail &&
                            this.childTab &&
                            this.childTab.constructor === Array &&
                            this.childTab.length > 0) {
                            // 获取[id：详情数据id]，[mdCode:模型编码]
                            let { id, data: { mdCode } } = resDetail || { data: {} }
                            if (mdCode && id) {
                                // 替换当前路由，使用保存后最新的详情id进行渲染
                                this.$router.replace({
                                    name: 'standerEditTree',
                                    params: { mdCode, id }
                                })
                            } else {
                                // 默认情况：返回上一页
                                this.$router.go(-1)
                            }
                        } else {
                            // 默认情况：返回上一页
                            this.$router.go(-1)
                        }
                    } else {
                        this.$message.error(`保存失败！${res.message}`)
                    }
                }).finally(() => {
                    // 设置按钮状态
                    btnObj.loading = false
                })
            })
        }
    }
]
export default defaultButtons