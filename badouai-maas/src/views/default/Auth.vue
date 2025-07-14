<!--
 * @FilePath: @/views/default/Auth.vue
 * @Description: 免登陆过渡页面
-->
<template>
    <div class="auth-container">
        <img src="/public/static/image/loadingResource.gif" alt="" srcset="">
    </div>
</template>

<script>
import { SSO_Get_Token } from '@/api/frame/user'
import CryptoUtils from '@/service/crypto-utils.js'
export default {
    methods: {
        loadAuth (userId) {
            return new Promise(resolve => {
                let timestamp = Date.now()
                let params = {
                    userId: CryptoUtils.aesEncrypt(`${userId}!@!${timestamp}`),
                    timestamp
                }
                SSO_Get_Token(params).then(res => {
                    if (!res?.result) {
                        resolve()
                    } else {
                        resolve(res.message || '')
                    }
                })
            })
        }
    },
    async created () {
        // 获取路由参数
        let routeParams = this.$route.query || {}
        let authToken = ''
        if (!routeParams.userId) {
            console.error('缺少userId')
        } else {
            authToken = await this.loadAuth(routeParams.userId)
        }
        // 判断是否已拿到具体的值
        if (!authToken) {
            let routerQuery = routeParams.redirect ? { redirect: routeParams.redirect } : {}
            this.$confirm('登录验证失败！', '提示', {
                confirmButtonText: '跳转登录页',
                cancelButtonText: '取消',
            }).then(() => {
                this.$router.replace({
                    path: `/login`,
                    routerQuery
                })
            })
            return
        }
        this.$store.commit('user/SET_TOKEN', authToken)
        this.$router.replace({
            path: routeParams.redirect || '/'
        })
    }
}
</script>

<style lang="scss" scoped>
.auth-container {
    width: 100%;
    height: 100%;
    background-color: $primary;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>
