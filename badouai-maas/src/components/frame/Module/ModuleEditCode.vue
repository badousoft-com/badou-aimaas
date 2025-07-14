// 新的时代已经来临
<template>
    <div
        :style="{height: _height}"
        :class="[
            { 
                'is-view': isView,
                'h-per-100': fullHeight,
            },
            'moduleEditCode_' + mdCode,
            getModuleClassName(mdCode, 2, isView)
        ]"
        class="bd-module-edit-code border-radius">
        <!-- 上面使用p-r是因为在关联中，表单保存的按钮使用absolute，需要限制住 -->
        <!-- 不能加p-r，会影响表单按钮底部的固定渲染 -->
        <bd-skeleton :loading="!isFormStartRender">
            <module-edit
                :ref="refName"
                v-if="isReady"
                v-bind="_$attrs"
                :mdCode="mdCode"
                :detailId="real_detailId || detailId"
                :formData="formData"
                v-on="$listeners"
                :isFormStartRender.sync="isFormStartRender">
                <!-- 表单标题右侧插槽 -->
                <template v-slot:titleRight>
                    <slot name="titleRight"></slot>
                </template>
            </module-edit>
        </bd-skeleton>
    </div>
</template>
<script>
import ModuleEdit from '@/components/frame/Module/ModuleEdit/index'
import ModuleUtils from '@/js/ModuleUtils'
import { finalRequest } from '@/service/request'
import { Get_Module_ClassName } from '@/service/module'
import { Get_Module_EditJSON } from '@/api/frame/common'
import frameConst from '@/service/global-const'
import Skeleton from '@/components/frame/Skeleton'
import { Get_Full_Url } from '@/service/url'
import ScopeMixin from '@/components/frame/ScopeMixin'
export default {
    name: 'module-edit-code',
    inheritAttrs: false,
    components: {
        [ModuleEdit.name]: ModuleEdit,
        [Skeleton.name]: Skeleton
    },
    // 使用混淆组件
    mixins: [ScopeMixin],
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
        },
        // 设置高度
        height: {
            type: [String, Number]
        },
        // 开启后，将会设置
        fullHeight: {
            type: Boolean,
            default: false
        },
        // 组件传入默认请求参数
        defaultParamsObj: {
            type: Object,
        },
    },
    data () { // 定义页面变量
        return {
            // 模型对象数据
            module: null,
            // 模型表单值-详情数据
            formData: null,
            // 数据准备状态
            isReady: false,
            // 自定义-表单字段值所在路径
            diy_valuePath: null,
            // 真实的表单数据id，新增为'',编辑时为数据id
            real_detailId: null,
            // 所有请求结束，数据结构组装完成，表单开始渲染
            isFormStartRender: false,
            // 获取模型专属类名的函数
            getModuleClassName: Get_Module_ClassName,
        }
    },
    computed: {
        // 获取高度值
        _height () {
            if (!this.height) return
            if (!parseFloat(this.height)) return
            if (~this.height.toString().indexOf('%')) {
                // 含有%的情况构造%数据返回
                return `${parseFloat(this.height)}%`
            }
            // 其他情况一律按px处理
            return `${parseFloat(this.height)}px`
        },
        _$attrs () {
            return {
                isView: this.isView,  // 是否查看模式
                ...(this.module || {}), // 模型解构后属性
                fullHeight: this.fullHeight,
                ...this.$attrs,  // 父组件传入属性（未声明在props部分)
                // 合并两者的customSetting
                customSetting: Object.assign({}, this.module.customSetting, this.$attrs?.customSetting)
            }
        },
        // 2022-10-24之前: 若当前this.detailId有值且为placeholder，则返回true，后续对应进行特殊处理
        // 2022-10-24: 对于编辑而言，应该是只有当detailId有值且满足一定条件时，存在以下两情况
        //     1. 新增时，临时使用add作为detailId值
        //     2. editJSON接口可以在参数id无值时，defaultSearchParams有值时，也能查询数据
        // 2022-10-25: detailId满足条件为真表单id
        //     1. 32位(旧版，多为系统模型)
        //     2. 64位(目前业务模型)
        //     3. busi模型，id值与code同步，只要不为add或者placeholder都判定为真id
        noValidDetailId () {
            if (!this.detailId) return true
            let _len = this.detailId.length
            return !(
                _len === 32 ||
                _len === 64 ||
                this.mdCode === 'busi' && this.detailId !== 'add' && this.detailId !== 'placeholder')
        },
    },
    methods: { // 定义函数
        // 获取moduleEdit/index.vue页面所在作用域
        getModuleEditRef () {
            return this.$refs?.[this.refName]
        },
        // 获取最底层表单作用域
        getFormRef () {
            return this.getModuleEditRef()?.$refs?.[this.refName]?.$refs?.[this.refName]
        },
        /**
         * 获取表单详情值数据
         * @param {Function} diy_dataUrl 自定义请求表单值详情接口
         */
        getFormData (diy_dataUrl) {
            return new Promise((resolve, reject) => {
                let params = {
                    mdCode: this.mdCode,
                    isView: this.isView
                }
                // editJSON支持使用defaultSearchParams,判读是否存在，存在则作为参数
                if (this.defaultParamsObj) {
                    Object.keys(this.defaultParamsObj).forEach(i => {
                        params[i] = this.defaultParamsObj[i]
                    })
                }
                // detailId有效时才添加作为参数
                if (!this.noValidDetailId) {
                    // 添加id参数
                    params = { ...params, id: this.detailId }
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
                    // 若当前的detailId无实际意义，需要从返回表单数据中找出id赋给当前的detailId
                    if (this.noValidDetailId) {
                        // 更新detailId
                        this.setDetailId(res)
                    }
                    resolve(res)
                }).catch(err => {
                    resolve()
                    console.log(err)
                })
            })
        },
        /**
         * 设置detaildId
         * @param {Object} formData 表单详情数据
         */
        setDetailId (formData) {
            if (!(formData && formData.constructor === Object)) return
            // 获取id字段值
            let idFieldValue = formData['id']
            // 若id字段值为字符串，且不存在指定值路径，则取该值
            if (idFieldValue &&
                this.diy_valuePath === '' &&
                idFieldValue.constructor === String) {
                // 更新new_detaildId
                this.real_detailId = idFieldValue
                return
            }
            // 获取id字段值所在路径
            let valuePath = this.diy_valuePath || frameConst.form.valuePath
            // 若id字段值idFieldValue为对象，且指定了值路径valuePath，则取idFieldValue[valuePath]
            if (idFieldValue &&
                valuePath &&
                idFieldValue.constructor === Object) {
                idFieldValue = idFieldValue[valuePath]
                // 值不存在，当场退役
                if (!idFieldValue) return
                // 更新new_detaildId
                this.real_detailId = idFieldValue
            }
        },
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
                    valuePath,  // 自定义字段值对应的路径
                } = Object.assign({}, _module?.customSetting, this.$attrs?.customSetting)
                // 自定义更改模型数据
                if (afterModuleJSON && typeof afterModuleJSON === 'function') {
                    _module = afterModuleJSON.call(this, _module)
                }
                // 更新-自定义字段值对应的路径
                this.diy_valuePath = valuePath
                // 更新模型数据
                this.module = _module
                // 将模型数据抛给父级组件使用
                this.$emit('afterModule', this.module)
                // 判断是否存在默认参数
                let _hasDefaultParamsObj = this.defaultParamsObj  && Object.keys(this.defaultParamsObj).length > 0
                if (this.detailId || _hasDefaultParamsObj) {
                    // 获取表单值-详情数据
                    let _formData = await this.getFormData(dataUrl) || {}
                    // 自定义-获取表单详情数据回调事件
                    if (afterEditJSON && typeof afterEditJSON === 'function') {
                        _formData = afterEditJSON.call(this, _formData)
                    }
                    // 更新模型表单值-详情数据
                    this.formData = _formData
                }
                // 将模型渲染前的事件抛出给父级组件使用
                this.$emit('beforeModuleRender', this.module, this.formData)
                // 数据准备齐全，可以下一环节
                this.isReady = true
            }
        },
    }
}
</script>
<style lang='scss' scoped>
</style>