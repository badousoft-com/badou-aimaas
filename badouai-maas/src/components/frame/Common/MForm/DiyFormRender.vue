<template>
    <render-fun
        :render="_content">
        <slot v-for="(i) in slotList" :name="i"></slot>
    </render-fun>
</template>
<script>
import RenderFun from '@/components/frame/RenderFun'
export default {
    name: 'diy-form-render',
    components: {
        [RenderFun.name]: RenderFun,
    },
    props: {
        // 自定义render函数
        render: {
            type: Function
        },
        // 表单字段键名数组（对应插槽位）
        slotList: {
            type: Array,
            default: () => []
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
        // 获取渲染内容
        _content () {
            if (!(this.render && typeof this.render === 'function')) return
            // 返回格式化文本
            return (h, context) => {
                // 根据字段键名：获取表单输入DOM
                const S = (name) => context.children.find(i => i.data?.attrs?.name === name) || {}
                // 根据字段键名，获取字段对象数据
                const D = (name) => context.children.find(i => i.data?.attrs?.name === name)?.data?.attrs?.data || {}
                return this.render.call(this, h, context, S, D)
            }
        }
    },
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>

</style>