<!--
 * @Description: 面板设置
-->
<template>
    <div class="panel-setting">
        <bd-dialog
            :visible.sync="_visible"
            :title="title"
            :handlerList="btnList">
            <module-form
                v-if="formVisible"
                noTitle
                ref="moduleForm"
                v-bind="module"
                :fieldList.sync="formFieldList"
                :detailId="field.id"
                class="right-form__main">
            </module-form>
            <!-- <span slot="footer" class="dialog-footer">
                <template v-for="(i, index) in btnList">
                    <bd-button
                        :key="index"
                        v-bind="i"
                        @click="exeMethod(i)">
                    </bd-button>
                </template>
            </span> -->
        </bd-dialog>
    </div>
</template>

<script>
import ModuleUtils from '@/js/ModuleUtils'
import ModuleForm from '@/components/frame/Module/ModuleForm/index.vue'
import Dialog from '@/components/frame/Dialog/index.vue'
import { Update_FieldList_Value } from '@/service/module'
import { Deep_Clone } from '@/utils/clone'
import Panel_Theme_Consts from '@/views/panel/js/theme.js'
import { Down_File_Url } from '@/api/frame/panel/designer'
export default {
    name: 'panel-setting-dialog',
    components: {
        [ModuleForm.name]: ModuleForm,
        [Dialog.name]: Dialog,
    },
    props: {
        // 弹窗出现控制参数
        visible: {
            type: Boolean,
            default: false
        },
        // 表单绑定值
        field: {
            type: Object,
            default: () => { }
        }
    },
    computed: {
        _visible: {
            get () {
                return this.visible
            },
            set (val) {
                this.$emit('update:visible', val)
            }
        },
        title () {
            let setting_str = this.field.id ? '编辑' : '添加'
            return setting_str + '面板'
        }
    },
    data () {
        let that = this
        return {
            mdCode: 'layout',
            module: {},
            formFieldList: {},
            // 按钮
            btnList: [
                {
                    id: 'cancel', name: '取消', icon: 'cancel', type: 'danger',
                    click: function (btnObj) {
                        that.$set(that, '_visible', false)
                    }
                }, {
                    id: 'submit', name: '确定', icon: 'save', loading: false, type: 'primary',
                    click: function (btnObj) {
                        that.submit(btnObj)
                    }
                }
            ],
            // 图片展示地址：后接attachId
            showImgUrl: Down_File_Url,
            formVisible: true,
        }
    },
    methods: {
        submit (btn) {
            this.$refs.moduleForm.$refs.edit.validateForm().then(res => {
                this.$emit('submit', res, btn)
            })
        },
        cancel () {
            this._visible = false
        }
    },
    async mounted () {
        // 获取右侧表单模型module数据
        let module = await ModuleUtils.editModuleCfg(this.mdCode)
        // 重定义模型表单的模板下拉条件
        let template_field = module.fieldList.find(o => o.name === 'pageId')
        if (template_field) {
            template_field.fun = ''
            template_field.dic = ''
            template_field.options = Panel_Theme_Consts.themeList
        }
        this.module = module
        // this.formFieldList = Update_FieldList_Value(this.module.fieldList, this.field, { valuePath: '' })
    },
    watch: {
        _visible (newVal, oldVal) {
            if (newVal) {
                this.formVisible = true
                this.formFieldList = Update_FieldList_Value(Deep_Clone(this.module.fieldList), this.field, { valuePath: '' })
            } else {
                setTimeout(() => {
                    this.formVisible = false
                }, 200)
            }
        }
    }
}
</script>

<style lang="scss" scoped>
/deep/ .panel-dialog {
    .el-dialog__header {
        padding: 0;
    }
    .el-dialog__body {
        padding: 10px 20px;
    }
    .el-dialog__footer {
        text-align: center;
    }
}
</style>