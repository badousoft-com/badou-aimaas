<!--
 * @FilePath: @/views/module/mixins/ButtonPermissions.vue
 * @Description: 【列表页】处理按钮权限
-->
<script>
import { mapGetters } from 'vuex'
export default {
    props: {
        // 挂载页面时使用，根据此路由地址查找对应的按钮权限（不传默认使用当前路由）
        pRoutePath: {
            type: String,
            default: ''
        }
    },
    computed: {
        ...mapGetters(['permissionButtons']),
        routePath () {
            return this.pRoutePath || this.vRouterPath
        },
        // 当前页面的权限按钮
        permissionBtnList () {
            // 获取当前页面路径
            let fullPath = this.routePath || this.$route.fullPath
            let info = this.permissionButtons.find(o => o.path === fullPath || ('/' + o.path) === fullPath)
            if (!info || !info.btnList) return
            let res = info.btnList.filter(o => !o.belongForm || o.belongForm === 'list')
            if (res && res.length) {
                res = res.map(b => {
                    let resItem = {
                        ...b,
                        id: b.code,
                        title: b.remark,
                    }
                    if (resItem.customOptions) {
                        try {
                            let customOptions = JSON.parse(resItem.customOptions)
                            Object.assign(resItem, customOptions)
                        } catch (error) {
                            console.log(`${resItem.customOptions}解析错误`)
                        }
                    }
                    resItem.className = resItem.className || resItem.menuClass
                    return resItem
                })
            }
            return res
        }
    },
}
</script>
