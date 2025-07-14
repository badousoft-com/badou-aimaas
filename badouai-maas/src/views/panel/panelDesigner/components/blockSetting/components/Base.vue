<!--
 * @LastEditTime: 2022-09-20 15:55:06
 * @Description: 块设置弹窗 - 基础设置
-->
<template>
    <div class="block-design-base">
        <module-form
            noTitle
            ref="moduleForm"
            :fieldList.sync="_fieldList"
            :detailId="field.id"
            v-bind="module"
            :customSetting="customSetting"
            class="right-form__main">
        </module-form>
    </div>
</template>

<script>
import ModuleForm from '@/components/frame/Module/ModuleForm/index.vue'
import customSetting from './js/base'
export default {
    name: 'block-setting-base',
    components: {
        [ModuleForm.name]: ModuleForm,
    },
    inheritAttrs: false,
    props: {
        fieldList: {
            type: Array,
            default: () => []
        },
        module: {
            type: Object,
            default: () => {}
        },
        elseAttrs: {
            type: Object,
            default: () => {}
        },
        code: {
            type: String,
            default: ''
        },
        field: {
            type: Object,
            default: () => { }
        },
    },
    computed: {
        _fieldList: {
            get () {
                return this.fieldList
            },
            set (val) {
                this.$emit('update:fieldList', val)
            }
        }
    },
    data () {
        return {
            customSetting,
        }
    },
    methods: {
        validateForm () {
            return new Promise((resolve, reject) => {
                this.$refs.moduleForm.$refs.edit.validateForm().then((res) => {
                    resolve(res)
                }).catch(() => {
                    reject(this.code)
                })
            })
        }
    },
}
</script>

<style lang="scss" scoped>

</style>