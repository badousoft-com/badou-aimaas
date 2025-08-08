<template>
    <div class="login-container" :class="Is_Mobile()? 'is-mobile' : ''" :style="{backgroundImage: makeImg(settingLoginBgImg, 'bg')}">
        <i class="el-icon-edit first-el-icon"></i>
        <p class="subject-title">{{settingTitle}}</p>
        <div class="login-panel" :class="Is_Mobile()? 'is-mobile' : ''">
            <div class="title-area" v-if="!isLogoImgLoadError && settingLogoImg">
                <img :src="makeImg(settingLogoImg)" alt="" @error="isLogoImgLoadError = true">
            </div>
            <el-form class="login-form" ref="loginForm" :model="loginForm" :rules="loginRules">
                <el-form-item prop="username">
                    <bd-icon name="user" class="iconSvg"></bd-icon>
                    <el-input
                        ref="username"
                        v-model="loginForm.username"
                        placeholder="请输入账户"
                        name="username"
                        type="text"
                        tabindex="1"
                        auto-complete="on">
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <bd-icon name="password" class="iconSvg"></bd-icon>
                    <el-input
                        :key="passwordType"
                        ref="password"
                        v-model="loginForm.password"
                        :type="passwordType"
                        show-password
                        placeholder="请输入密码"
                        name="password"
                        tabindex="2"
                        auto-complete="on"
                        :readonly="cancelComplete">
                    </el-input>
                    <!-- <span class="show-pwd" @click="showPwd">
                        <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
                    </span> -->
                </el-form-item>
                <el-form-item prop="imageCode" v-if="isUseVerifyCode == 1">
                    <bd-icon name="verifiCode" class="iconSvg"></bd-icon>
                    <el-input
                        ref="imageCode"
                        v-model="loginForm.imageCode"
                        placeholder="请输入验证码"
                        name="imageCode"
                        type="text"
                        tabindex="3"
                        auto-complete="on">
                    </el-input>
                    <check-code
                        ref="checkCode"
                        :auto="false"
                        :code="imageCode"
                        @refresh="getCode"/>
                </el-form-item>
                <el-button
                    :loading="loading"
                    type="primary"
                    size="large"
                    class="w-per-100"
                    @click.native.prevent="handleLogin">
                    登录
                </el-button>
                <!-- <div class="textR forgetBtn">
                    <span>忘记密码?</span>
                </div> -->
            </el-form>
        </div>
    </div>
</template>

<script>
import { Is_Str_Empty } from '@/utils/index'
import { Is_Mobile } from '@/utils/browser.js'
import CheckCode from '@/components/frame/Common/CheckCode'
import { mapGetters } from 'vuex'
import { Get_Verification_Code } from '@/api/frame/index'
export default {
    name: 'Login',
    components: {
        CheckCode,
    },
    computed: {
        ...mapGetters([
            'settingTitle',
            'settingLogoImg',
            'settingLoginBgImg',
            'settingHeadBgImg',
            'isUseVerifyCode'
        ]),
        // 是否需要取消自动填充
        cancelComplete () {
            return String(process.env.VUE_APP_CLOSE_COMPLETE) === '1' && !this.isPwReady
        }
    },
    data () {
        window.imgCode = ''
        window.getCode = null
        const validateUsername = (rule, value, callback) => {
            if (value.length === 0) {
                callback(new Error('用户名无效,请重新输入'))
            } else {
                callback()
            }
        }
        const validatePassword = (rule, value, callback) => {
            if (value.length < 6) {
                callback(new Error('密码不少于6位'))
            } else {
                callback()
            }
        }
        const validateImageCode = (rule, value, callback) => {
            if (Is_Str_Empty(value)) {
                callback(new Error('请输入图形验证码'))
            }
            if (value.toUpperCase() !== window.imgCode.toUpperCase()) {
                // 刷新验证码
                window.getCode()
                // 验证码输入错误,重置验证码
                callback(new Error('图形验证码错误'))
            } else {
                callback()
            }
        }
        return {
            isPwReady: false,
            // logo图片是否加载失败-状态
            isLogoImgLoadError: false,
            imageCode: '',
            loginForm: {
                username: '',
                password: '',
                imageCode: '',
                uuid: ''
            },
            loginRules: {
                username: [{ required: true, trigger: 'blur', validator: validateUsername }],
                password: [{ required: true, trigger: 'blur', validator: validatePassword }],
                imageCode: [{ required: true, trigger: 'blur', validator: validateImageCode }]
            },
            loading: false,
            passwordType: 'password',
            redirect: undefined,
            // 判断是否为移动端
            Is_Mobile: Is_Mobile,
        }
    },
    watch: {
        $route: {
            handler: function (route) {
                // 判断是否存在重定向模块,登录成功之后将跳转到指定位置,一般用于token失效后重新登录回到之前浏览页面
                // route.query可能值为 {
                //     redirect: '',
                //     其他参数键: 值
                // }
                // 需要将redirect属性与其他属性拼装返回完整重定向地址-->makeAllQueryParams函数作用
                this.redirect = route.query && this.makeAllQueryParams(route.query)
            },
            immediate: true
        }
    },
    methods: {
        // 根据路由query参数对象拼装完整路径
        makeAllQueryParams (routeQueryObject) {
            // 获取重定向地址--此时未获取其余参数
            let redirect = routeQueryObject.redirect
            // 删除数据源中reirect属性,剩下均为其余参数
            delete routeQueryObject['redirect']
            // 获取参数键数组
            let keyList = Object.keys(routeQueryObject)
            keyList.forEach((i, index) => {
                // 拼装完整重定向地址(含参数)
                redirect += `&${i}=${routeQueryObject[i]}`
            })
            return redirect
        },
        // 图形校验码绘制成功后,回调函数赋值
        getCode (data) {
            // 有回调值表示当前为验证码组件自动生成验证码
            if (data) {
                window.imgCode = data
                this.imageCode = data
            } else {
                // 验证码来自接口
                Get_Verification_Code({}).then(res => {
                    if (res) {
                        let { code, uuid } = res
                        window.imgCode = code || ''
                        this.imageCode = code || ''
                        this.$set(this.loginForm, 'uuid', uuid)
                    }
                })
            }
        },
        showPwd () {
            if (this.passwordType === 'password') {
                this.passwordType = ''
            } else {
                this.passwordType = 'password'
            }
            this.$nextTick(() => {
                this.$refs.password.focus()
            })
        },
        handleLogin () {
            this.loading = true
            setTimeout(() => {
                this.$refs.loginForm?.validate(valid => {
                    if (valid) {
                        this.loginForm.username = this.loginForm.username.trim()
                        let form = {
                            username: this.loginForm.username && this.loginForm.username.trim(),
                            password: this.loginForm.password,
                            CAPTCHA: this.loginForm.imageCode && this.loginForm.imageCode.trim(),
                            uuid: this.loginForm.uuid
                        }
                        this.$store.dispatch('user/login', form).then((res) => {
                            if (res.status) {
                                this.$router.push({ path: this.redirect || '/' })
                            } else {
                                // 登录失败时，重新获取验证码
                                this.getCode()
                                this.$message({
                                    message: res.message,
                                    type: 'warning'
                                })
                            }
                        }).catch((err) => {
                            console.error(err)
                        }).finally(() => {
                            setTimeout(() => {
                                this.loading = false
                            }, 1000)
                        })
                    } else {
                        this.loading = false
                        return false
                    }
                })
            })
        }
    },
    created () {
        // 获取验证码
        this.getCode()
    },
    mounted () {
        window.getCode = this.getCode
        // 注册键盘事件
        this.keyDown({ 'Enter': this.handleLogin })
    },
    beforeRouteEnter (from, to, next) {
        next(vm => {
            // 通过 `vm` 访问组件实例// 延迟设置密码框可编辑，使其不自动填充密码框
            setTimeout(() => {
                vm.isPwReady = true
            }, 10)
            // 继续当前
            next()
        })
    },
}
</script>
<style lang="scss" scoped>
$power: 8px;
$pad: 3 * $power;
.first-el-icon {
    position: absolute;
    opacity: 0;
}
.iconSvg {
    margin: 0px 10px;
}
.login-container {
    background-color: #2e3138;
    // background: url("../../assets/project/bg.png");
    background-size: cover;
    background-attachment: fixed;
    background-repeat: no-repeat;
    min-height: 100%;
    width: 100%;
    overflow: hidden;
    .subject-title {
        padding: 10px 30px;
        font-size: 28px;
        letter-spacing: 1px;
        color: #fff;
        animation: sliceTop 0.9s ease ;
    }
    .login-panel {
        width: 24%;
        background: #fff;
        min-width: 400px;
        margin: auto;
        margin-top: 7%;
        border-radius: 4px;
        padding: 4 * $power $pad $pad $pad;
        &.is-mobile {
            width: 90%;
            min-width: 0;
            .el-input {
                width: 100%;
            }
        }
        .title-area {
            width: 100%;
            text-align: center;
            margin-bottom: $pad;
            img {
                height: 80px;
            }
        }
        .login-form {
            margin: auto;
            .el-form-item {
                border-bottom: 1px solid #eee;
                color: #999;
                margin-bottom: $pad;
                &:last-of-type {
                    margin-bottom: 6 * $power;
                }
            }
            .forgetBtn {
                padding: 10px 0px;
                color: #666;
                font-size: 12px;
                cursor: pointer;
            }
            .show-pwd {
                position: absolute;
                right: 10px;
                top: 0px;
                font-size: 16px;
                color: #eee;
                cursor: pointer;
                user-select: none;
            }
        }
    }
    @keyframes sliceTop {
        0% {
            margin-top: 2em;
        }
        100% {
            margin-top: 1em;
        }
    }
}

</style>

<style lang="scss">
.login-container {
    .el-input {
        display: inline-block;
        height: 30px;
        width: 85%;
        input {
            border: 0px;
            border-radius: 0px;
            padding: 5px;
            height: 30px;
        }
    }
}
</style>