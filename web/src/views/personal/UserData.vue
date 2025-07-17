<template>
    <div class="page-user-data">
        <m-form
            class="s-form"
            :ref="formInfo.refId"
            :id="formInfo.refId"
            :title="formInfo.title"
            :labelWidth="formInfo.labelWidth"
            :columnNum="formInfo.columnNum"
            :dataList="formList">
            <template v-slot:end>
                <span class="module-storage fontC">模型缓存</span>
                <bd-button icon="delete" type="danger" @click="clearModuleStorage">清除所有模型缓存数据</bd-button>
            </template>
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
import { mapGetters } from 'vuex'
import GlobalConst from '@/service/global-const'
import { Update_Info } from '@/api/frame/user'
import { Clear_Module_Storage } from '@/service/module-storage'
export default {
    components: {
        MForm
    },
    directives: {
        // 设置页面高度
        setHeight: {
            inserted: function (el, binding) {
                el.children[0].style.height = `${el.clientHeight  - 44}px`
            }
        }
    },
    data () {
        return {
            formInfo: {
                refId: 'form',
                title: '修改资料',
                labelWidth: '89px',
                columnNum: GlobalConst.form.columnNum
            },
            formList: [
                { type: 'hidden', name: 'id', value: '' },
                { type: 'text', label: '登录账号', name: 'logonId', value: '', disabled: true, placeholder: '请输入' },
                { type: 'text', label: '编码', name: 'code', value: '', disabled: true, placeholder: '请输入' },
                { type: 'text', label: '邮箱', name: 'email', value: '', placeholder: '请输入', rules: [
                    { type: 'email', message: '请输入正确邮箱', trigger: 'blur' },
                ] },
                { type: 'text', label: '名称', name: 'name', value: '', placeholder: '请输入', rules: [
                    { required: true, message: '请输入名称', trigger: 'blur' },
                ] },
                { type: 'text', label: '电话', name: 'tel', value: '', placeholder: '请输入' },
                { type: 'text', label: 'QQ', name: 'qq', value: '', placeholder: '请输入' }
            ],
            btnList: [
                { name: '保存', icon: 'save', click: this.saveForm, loading: false, type: 'primary'}
            ]
        }
    },
    computed: {
        ...mapGetters([
            'userInfo'
        ])
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
                // 更新按钮状态
                btnObj.loading = true
                Update_Info(resForm).then(res => {
                    if (res.hasOk) {
                        this.$store.commit('user/SET_USER_INFO', resForm)
                        this.$message.success('操作成功')
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
        },
        // 清除模型缓存
        clearModuleStorage () {
            Clear_Module_Storage()
            this.$message.success('模型数据缓存已清除')
        }
    },
    created () {
        // 初始设置
        this.formList.forEach(i => {
            i.value = this.userInfo[i.name] || ''
        })
    }
}
</script>
<style lang="scss" scoped>
.page-user-data {
    .s-form {
        height: calc(100% - #{$footerHeight});
    }
    .module-storage {
        margin: 0 $padding 0 2 * $padding
    }
}
</style>
