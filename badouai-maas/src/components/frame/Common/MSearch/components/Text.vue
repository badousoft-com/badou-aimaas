<template>
    <div>
        <el-input
            v-model="showValue" 
            :placeholder="'请输入' + label"
            clearable
            :title="label"
            @change="change">
        </el-input>
    </div>
</template>
<script>
// import xx from ./xx
export default {
    name: 'search-text',
    components: {},
    props: {
        // label标签字段
        label: {
            type: String,
            default: ''
        },
        // 字段名称
        id: {
            type: String,
            default: ''
        },
        // 字段值
        value: {
            default: ''
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
        // 页面展示value
        showValue: {
            get: function () {
                // 获取父级组件传入值
                return this.value
            },
            set: function (val) {
                // 调用父级update事件进行value值更新
                this.$emit('input', val)
                // 值更新事件传给父级组件
                this.$emit('change', this.id)
            }
        }
    },
    methods: { // 定义函数
        change (value) {
            this.$emit('input', value)
            // 值更新事件传给父级组件
            this.$emit('change', this.id)
            this.$emit('search')
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>

</style>