// 新的时代已经来临
<template>
    <div
        :class="[
            {
                'is-view': isView
            },
            getModuleClassName(mdCode, 2, isView)
        ]"
        class="bd-module-edit-code h-per-100">
        <module-edit
            :ref="refName"
            v-if="isReady"
            v-bind="_$attrs"
            :mdCode="mdCode"
            :detailId="detailId"
            :formData="formData"
            :defaultBtnList="defaultBtnList"
            v-on="$listeners">
        </module-edit>
    </div>
</template>
<script>
import ModuleEdit from '@/components/frame/Module/ModuleEdit/index'
import ModuleUtils from '@/js/ModuleUtils'
import { finalRequest } from '@/service/request'
import { Get_Module_ClassName } from '@/service/module'
import { Get_Module_EditJSON } from '@/api/frame/common'
import Default_Btn_List from './button'
import { Save_Tree_URL } from '@/api/frame/common'
import { Get_Full_Url } from '@/service/url'
export default {
    name: 'module-edit-tree',
    inheritAttrs: false,
    components: {
        [ModuleEdit.name]: ModuleEdit
    },
    props: {
        // ref属性名称
        refName: {
            type: String,
            default: 'edit'
        },
        // 模型编码
        mdCode: {
            type: String
        },
        // 数据详情id
        detailId: {
            type: String
        },
        // 是否为查看
        isView: {
            type: Boolean,
            default: false
        }
    },
    data () { // 定义页面变量
        return {
            // 模型对象数据
            module: null,
            // 模型表单值-详情数据
            formData: null,
            // 数据准备状态
            isReady: false,
            // 默认按钮列表
            defaultBtnList: Default_Btn_List,
            // 自定义-表单中需要追加的数据（额外参数）
            diy_addFormData: null,
            // 获取模型专属类名的函数
            getModuleClassName: Get_Module_ClassName,
        }
    },
    computed: {
        _$attrs () {
            return {
                ...this.$attrs,  // 父组件传入属性（未声明在props部分）
                isView: this.isView,  // 是否查看模式
                ...(this.module || {}), // 模型解构后属性
                addFormData: this._addFormData,
            }
        },
        // 表单额外添加参数
        _addFormData () {
            // 获取父组件传入的属性
            let { addFormData, currentTreeNodeData } = this.$attrs
            if (this.diy_addFormData && typeof this.diy_addFormData === 'function') {
                if (currentTreeNodeData) {
                    // 自定义执行获取参数操作
                    return this.diy_addFormData.call(this, currentTreeNodeData, addFormData)
                }
            }
            // 返回默认参数【树节点参数】
            return addFormData
        },
        // 获取保存接口的url
        _saveUrl () {
            let diy_saveUrl = this.module?.customSetting?.saveUrl
            // 判断是否传入自定义获取保存接口地址的函数
            if (diy_saveUrl && typeof diy_saveUrl === 'function') {
                return diy_saveUrl.call(this, this.mdCode)
            }
            // 返回默认的表单保存接口地址
            return Save_Tree_URL(this.mdCode)
        },
    },
    methods: { // 定义函数
        getFormRef () {
            return this.$refs?.[this.refName]?.$refs?.[this.refName]?.$refs?.[this.refName]
        },
        /**
         * 获取表单详情值数据
         * @param {Function} diy_dataUrl 自定义请求表单值详情接口
         */
        getFormData (diy_dataUrl) {
            return new Promise((resolve, reject) => {
                let params = {
                    id: this.detailId,
                    mdCode: this.mdCode,
                    isView: this.isView
                }
                let _request = null
                // 使用自定义地址请求表单详情值数据
                if (diy_dataUrl && typeof diy_dataUrl === 'function') {
                    let _url = diy_dataUrl.call(this)
                    // 判断地址是否含有域名，没有则补齐
                    _url = Get_Full_Url(_url)
                    _request = finalRequest({ url: _url, params, method: 'get' })
                // 使用默认请求
                } else {
                    _request = Get_Module_EditJSON(params)
                }
                // 请求响应
                _request.then(res => {
                    resolve(res)
                }).catch(err => {
                    resolve()
                    console.log(err)
                })
            })
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {},
    watch: {
        mdCode: {
            immediate: true, // 立即触发监听
            handler: async function (newVal, oldVal) {
                // mdCode不存在则直接return结束
                if (!newVal) return
                // 获取模型数据
                let _module = null
                if (!this.isView) {
                    // 当前为编辑edit页面，请求编辑模型数据
                    _module = await ModuleUtils.editModuleCfg(newVal)
                } else {
                    // 当前为查看view页面，请求查看模型数据
                    _module = await ModuleUtils.viewModuleCfg(newVal)
                }
                let {
                    afterModuleJSON, // 表示请求完module模型数据之后进行数据自定义更改
                    dataUrl,  // 自定义请求表单值-详情接口地址
                    afterEditJSON,  // 自定义请求表单详情值后的回调事件
                    addFormData, // 表单中需要追加的数据（额外参数）
                } = _module?.customSetting || {}
                // 自定义更改模型数据
                if (afterModuleJSON && typeof afterModuleJSON === 'function') {
                    _module = afterModuleJSON.call(this, _module)
                }
                // 更新自定义-表单中需要追加的数据（额外参数）
                this.diy_addFormData = addFormData
                // 更新模型数据
                this.module = _module
                // 将模型数据抛给父级组件使用
                this.$emit('afterModule', this.module)
                if (this.detailId) {
                    // 获取表单值-详情数据
                    let _formData = await this.getFormData(dataUrl) || {}
                    // 自定义-获取表单详情数据回调事件
                    if (afterEditJSON && typeof afterEditJSON) {
                        _formData = afterEditJSON.call(this, _formData)
                    }
                    // 更新模型表单值-详情数据
                    this.formData = _formData
                }
                // 数据准备齐全，可以下一环节
                this.isReady = true
            }
        },
    }
}
</script>
<style lang='scss' scoped>
</style>



