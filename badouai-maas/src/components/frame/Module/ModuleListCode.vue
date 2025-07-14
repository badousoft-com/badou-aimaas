// 新的时代已经来临
<template>
    <div
        class="bd-module-list-code border-radius"
        :class="[
            { 
                'h-per-100': fullHeight,
            },
            getModuleClassName(mdCode, 1)
        ]"
        :style="{'height': _height}">
        <module-list
            v-if="module"
            ref="moduleList"
            v-bind="_$attrs"
            :mdCode="mdCode"
            :data.sync="tableData"
            :parentHeight="styleHeight"
            v-on="$listeners">
        </module-list>
    </div>
</template>
<script>
import ModuleList from '@/components/frame/Module/ModuleList/index'
import ModuleUtils from '@/js/ModuleUtils'
import { Get_Module_ClassName } from '@/service/module'
import ScopeMixin from '@/components/frame/ScopeMixin'
export default {
    name: 'module-list-code',
    inheritAttrs: false,
    components: {
        [ModuleList.name]: ModuleList
    },
    // 使用混淆组件
    mixins: [ScopeMixin],
    props: {
        // 模型编码
        mdCode: {
            type: String
        },
        // 设置页面全铺满，设置true为开启
        fullHeight: {
            type: Boolean,
            default: false
        },
        // 高度样式
        height: {
            type: String
        }
    },
    data () { // 定义页面变量
        return {
            // 模型对象数据
            module: null,
            // 列表数据
            tableData: [],
            // 自定义样式高度
            styleHeight: 0,
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
            let _resAttrs = {
                ...this.$attrs,
                ...(this.module || {}),
                fullHeight: this.fullHeight,
                // 合并两者的customSetting
                customSetting: Object.assign({}, this.module.customSetting, this.$attrs?.customSetting)
            }
            // 处理是否隐藏按钮栏--start
            // 定义关键字段键名
            let hideBtnName = 'hideBtn'
            // 判断传入的模型配置中是否含有hideBtn
            if (_resAttrs?.customSetting &&
                hideBtnName in _resAttrs.customSetting) {
                // 提取hideBtn，作为子组件从传入值设置按钮栏的显示隐藏   
                _resAttrs[hideBtnName] = _resAttrs.customSetting[hideBtnName]
                // 已经使用，该字段没作用可删除
                delete _resAttrs.customSetting[hideBtnName]
            }
            // 处理是否隐藏按钮栏--end
            return _resAttrs
        }
    },
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        this.$nextTick(() => {
            this.styleHeight = this.$el?.getBoundingClientRect().height
        })
    },
    watch: {
        mdCode: {
            immediate: true, // 立即触发监听
            handler: async function (newVal, oldVal) {
                // mdCode不存在则直接return结束
                if (!newVal) return 
                // 获取模型数据
                let _module = await ModuleUtils.listModuleCfg(newVal)
                // 检查模型动态js中是否有afterModule属性
                let afterModuleJSON = this.$attrs?.customSetting?.afterModuleJSON || _module?.customSetting?.afterModuleJSON
                // afterModule：表示请求完module模型数据之后进行数据自定义更改
                if (afterModuleJSON && typeof afterModuleJSON === 'function') {
                    // 执行afterModule函数，返回更改数据后的module
                    this.module = afterModuleJSON.call(this, _module)
                } else {
                    this.module = _module
                }
                // 将模型数据抛给父级组件使用
                this.$emit('afterModule', this.module)
            }
        },
    }
}
</script>
<style lang='scss' scoped>
</style>