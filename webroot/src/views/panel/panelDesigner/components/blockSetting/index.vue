<!--
 * @Description: 块设置
-->
<template>
    <div class="block-setting">
        <bd-dialog
            :visible.sync="_visible"
            :title="title"
            :handlerList="btnList">
            <bd-tabs
                class="block-setting-tab"
                :data="tabData"
                v-model="activeTabName">
                <template v-for="(i, i_index) in tabData" v-slot:[i.id]>
                    <component
                        v-if="formVisible"
                        class="setting-item"
                        :ref="i.id"
                        :key="i_index"
                        :is="'block-setting-' + i.id"
                        :code="i.id"
                        :fieldList="tabFields[i.id]"
                        :mdCode="i.mdCode"
                        :module="module"
                        :field="field"
                        :elseAttrs="elseAttrs"
                        @fieldChange="fieldChange">
                    </component>
                </template>
            </bd-tabs>
        </bd-dialog>
    </div>
</template>

<script>
import ModuleForm from '@/components/frame/Module/ModuleForm/index.vue'
import MTitle from '@/components/frame/Common/MTitle'
import Base from './components/Base'
import Style from './components/Style'
import Content from './components/Content'
import Even from './components/Even'
import Share from './components/Share'
import ModuleUtils from '@/js/ModuleUtils'
import { Update_FieldList_Value } from '@/service/module'
import BdTabs from '@/components/frame/Common/BdTabs'
import Dialog from '@/components/frame/Dialog/index.vue'
import { Deep_Clone } from '@/utils/clone'
export default {
    name: 'block-setting-dialog',
    components: {
        [ModuleForm.name]: ModuleForm,
        [Dialog.name]: Dialog,
        BdTabs,
        MTitle,
        [Base.name]: Base,
        [Style.name]: Style,
        [Content.name]: Content,
        [Even.name]: Even,
        [Share.name]: Share,
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
        },
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
            return setting_str + '块'
        },
        // 各个tab的表单列表
        tabFields () {
            let result = {
                base: [],
                style: [],
                content: [...this.contentField],
                even: [],
                share: [],
            }
            this.formFieldList.forEach(item => {
                if (item.customOptions) {
                    let customOptions = JSON.parse(item.customOptions)
                    result[customOptions.tabId] = result[customOptions.tabId] || []
                    result[customOptions.tabId].push(item)
                } else {
                    result.base.push(item)
                }
            })
            return result
        },
        tabData () {
            let result = [
                { id: 'base', text: '基础设置' },
                { id: 'content', text: '内容设置', mdCode: this.contentMdCode },
                { id: 'style', text: '样式设置' },
                { id: 'even', text: '事件设置' },
            ]
            // 只有已保存过的块才会有分享tab
            if (this.field.id) {
                result.push({ id: 'share', text: '分享设置' })
            }
            return result
        },
    },
    data () {
        let that = this
        return {
            // 块mdCode
            mdCode: 'block',
            // 内容mdCode
            contentMdCode: 'content',
            // tab活跃项
            activeTabName: 'base',
            module: {},
            // 内容模型
            contentModule: {},
            // 块模型数据
            blockModule: {},
            // 模型数据字典
            dic: {},
            // 属于块的表单数据
            formFieldList: [],
            contentField: [],
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
            // 表单回显参数
            formVisible: true,
            // 额外参数，传给子组件的除了子组件该有的表单数据外的其他参数
            elseAttrs: {}
        }
    },
    methods: {
        exeMethod (obj) {
            if (typeof obj.click === 'function') {
                obj.click(obj)
            } else if (typeof this[obj.click] === 'function') {
                this[obj.click](obj)
            }
        },
        // 点击确定
        submit (btn) {
            this.checkFormAll().then(res => {
                // 所有表单校验通过才走then
                let params = {}
                res.forEach(o => {
                    params = { ...params, ...o }
                })
                this.$emit('submit', params, btn)
            }).catch((err) => {
                // 在各个表单的validateForm方法中的校验不通过时，需要reject(activeTabName)
                this.activeTabName = typeof err === 'string' ? err : this.activeTabName
            })
        },
        // 校验所有tab的表单
        checkFormAll () {
            return new Promise((resolve, reject) => {
                let list = []
                let contentRefs = this.$refs || []
                // 循环所有tab的表单校验
                for (let key in contentRefs) {
                    let item = contentRefs[key][0]
                    if (item && typeof item.validateForm === 'function') {
                        list.push(item.validateForm())
                    }
                }
                Promise.all(list).then((res) => {
                    resolve(res)
                }).catch(err => {
                    reject(err)
                })
            })
        },
        // 取消
        cancel () {
            this._visible = false
        },
        // 字段更改时（子组件需要在customSetting中配置 this.$parent.$emit('fieldChange', fielName, value)
        // 目前（2021年4月21日）只有基础设置会影响到其他tab，故只配置了基础设置的，如其他配置也需要，需自行配置
        fieldChange (fielName, value) {
            switch (fielName) {
                case 'contentType':
                    this.$set(this.elseAttrs, 'contentType', value)
                    let tempRefs = this.$refs
                    for (let key in tempRefs) {
                        let rf = tempRefs[key]?.[0] || {}
                        typeof rf.refresh === 'function' && rf.refresh()
                    }
                    break
                default:
                // 没有匹配值，则不执行任何操作
            }
        },
    },
    async mounted () {
        // 获取右侧表单模型module数据
        let blockModule = await ModuleUtils.editModuleCfg(this.mdCode)
        let contentModule = await ModuleUtils.editModuleCfg(this.contentMdCode)
        // 这些下拉请求404，暂时先这么做，后续取消
        let obj = blockModule.fieldList.find(o => o.name === 'pageId')
        obj.type = 'text'
        obj.fun = ''
        contentModule.fieldList.forEach(o => {
            // 临时操作：部分下拉框接口暂时还未部署
            if (o.name === 'reportId' && this.$parent.options.reportList) {
                o.fun = ''
                o.options = this.$parent.options.reportList.map(o => {
                    return { text: o.name, ...o }
                })
            } else if (o.name === 'filterReportId') { // 过滤器报表
                o.fun = ''
                o.viewType = ''
                o.dic = ''
                o.options = this.$parent.options.filterList.map(o => {
                    return { text: o.name, ...o }
                })
            } else if (o.name === 'type' && this.$parent.options.typeList) {
                o.fun = ''
                o.viewType = ''
                o.dic = ''
                o.options = this.$parent.options.typeList.map(o => {
                    return { text: o.name, ...o }
                })
            }
            // 标识主要内容部分
            o.customOptions = JSON.stringify({ tabId: 'main_content' })
        })
        this.contentModule = contentModule
        this.blockModule = blockModule
        this.dic = Object.assign(this.contentModule.dic || {}, (this.blockModule.dic || {}))
    },
    watch: {
        visible (newVal, oldVal) {
            if (newVal) {// 合并两个模型的数据字典
                this.module = Object.assign(Deep_Clone(this.blockModule), { dic: this.dic })
                this.contentField = Deep_Clone(this.contentModule.fieldList || [])
                // 组装边框类型下拉框样式
                let borderType = this.module.fieldList.find(o => o.name === 'borderType')
                if (borderType) {
                    borderType.type = 'select'
                    borderType.options = this.$parent.options.borderType
                }
                // formVisible 用于v-if展示moduleForm组件
                this.formVisible = true
                // 根据field对formFieldList进行重新赋值
                this.formFieldList = Update_FieldList_Value(this.module.fieldList, this.field, { valuePath: '' })
                // 获取内容类型contentType(单一内容、多内容、多图片)
                let contentType_obj = this.formFieldList.find(o => o.name === 'contentType') || {}
                this.elseAttrs.contentType = contentType_obj.value || contentType_obj.defaultValue
            } else {
                // setTimeout 确保在关闭弹窗动画前的无操作，重置操作在用户看不到的情况下进行
                setTimeout(() => {
                    // 重置activeTabName，保证再次编辑块时是在基础设置
                    this.activeTabName = 'base'
                    this.formVisible = false
                }, 200)
            }
        }
    }
}
</script>

<style lang="scss" scoped>
.block-setting-tab {
    height: 100%;
    /deep/ .el-tabs {
        height: 100%;
        .el-tabs__content {
            height: calc(100% - 40px);
            overflow-y: scroll;
        }
    }
}
</style>