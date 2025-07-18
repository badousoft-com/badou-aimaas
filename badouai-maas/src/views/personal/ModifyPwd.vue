<template>
    <div class="page-modify-pwd">
        <m-form
            class="s-form"
            :ref="formInfo.refId"
            :id="formInfo.refId"
            :title="formInfo.title"
            :labelWidth="formInfo.labelWidth"
            :columnNum="formInfo.columnNum"
            :dataList="formList">
        </m-form>
        <div class="fixBottomBtn">
            <bd-button
                v-for='(i, index) in btnList'
                :key="index"
                v-bind="i"
                @click="exeMethod(i)">
            </bd-button>
        </div>
    </div>
</template>

<script>
import MForm from '@/components/frame/Common/MForm/index.vue'
import CryptoUtils from '@/service/crypto-utils.js'
import { Update_Pwd } from '@/api/frame/user'
export default {
    components: {
        MForm
    },
    data () {
        let validatePass = (rule, value, callback) => {
            if (value === '') {
                callback(new Error('请输入密码'))
            } else {
                let hasType = 0
                let isBigEng = /[A-Z]+/
                let isSmallEng = /[a-z]+/
                let isNumber = /[0-9]+/
                let regEn = /[`~!@#$%^&*()_+<>?:"{},.\/;'[\]]/im
                if (value.match(isNumber) !== null) {
                    hasType++
                }
                if (value.match(isSmallEng) != null || value.match(isBigEng) != null) {
                    hasType++
                }
                if (value.match(regEn) != null) {
                    hasType++
                }
                if (hasType >= 2) {
                    callback()
                } else {
                    callback(new Error('密码必须包含字母、数字、特殊字符中的任意2个'))
                }
            }
        }
        let validatePassAgain = (rule, value, callback) => {
            if (value === '') {
                callback(new Error('请再次输入密码'))
            } else if (value !== this.formList[1].value) {
                callback(new Error('两次输入密码不一致!'))
            } else {
                callback()
            }
        }
        return {
            formInfo: {
                refId: 'passForm',
                title: '修改密码',
                labelWidth: '120px',
                columnNum: 1,
            },
            formList: [
                { type: 'text', label: '原密码', name: 'password', value: '', placeholder: '请输入', isPassword: true, rules: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                ] },
                { type: 'text', label: '新密码', name: 'passwordNew', value: '', placeholder: '请输入', isPassword: true, rules: [
                    { required: true, message: '请输入新密码', trigger: 'blur' },
                    { min: 8, message: '密码最少8位数', trigger: 'blur' },
                    { validator: validatePass, trigger: 'blur' }
                ] },
                { type: 'text', label: '确认新密码', name: 'passwordOk', value: '', placeholder: '请输入', isPassword: true, rules: [
                    { required: true, message: '请再次输入新密码', trigger: 'blur' },
                    { validator: validatePassAgain, trigger: 'blur' }
                ]}
            ],
            btnList: [
                { name: '保存', icon: 'save', click: this.saveForm, loading: false, type: 'primary' }
            ]
        }
    },
    methods: {
        exeMethod (itemObj) {
            // 绑定事件中的this为当前页面作用域
            // 另外将按钮对象itemObj扔回作为绑定事件的参数
            itemObj.click.call(this, itemObj)
        },
        saveForm (btnObj) {
            let formId = this.formInfo.refId
            this.$refs[formId].validateForm().then(resForm => {
                let params = {
                    password: CryptoUtils.aesEncrypt(resForm.password),
                    passwordNew: CryptoUtils.aesEncrypt(resForm.passwordNew),
                    passwordOk: CryptoUtils.aesEncrypt(resForm.passwordOk)
                }
                // 更新按钮状态
                btnObj.loading = true
                Update_Pwd(params).then(res => {
                    if (res.hasOk) {
                        this.$confirm('是否立即重新登录，使配置生效', '配置生效', {
                            confirmButtonText: '确定重新登录',
                            cancelButtonText: '取消',
                            type: 'success'
                        }).then(async () => {
                            await this.$store.dispatch('user/logout')
                            this.pushPage({
                                path: `/login`,
                                title: '登录页'
                            })
                        })
                    } else {
                        let tip = res.message || ''
                        this.$message.error(`操作失败 ${tip}`)
                    }
                }).finally(() => {
                    setTimeout(() => {
                        // 请求结束，结束按钮加载状态
                        btnObj.loading = false
                    }, 300)
                })
            })
        }
    }
}
</script>
<style scoped lang="scss">
.page-modify-pwd {
    .s-form {
        height: calc(100% - #{$footerHeight});
    }
}
</style>
