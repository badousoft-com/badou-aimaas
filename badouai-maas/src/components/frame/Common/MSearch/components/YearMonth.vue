<template>
    <div class="">
        <bd-date
            type="date"
            dateType="month"
            v-model="tempValue"
            :placeholder="_placeholder"
            v-bind="$attrs">
        </bd-date>
    </div>
</template>
<script>
import BdDate from '@/components/frame/Common/MForm/components/items/Date/index.vue'
import GlobalConst from '@/service/global-const'
export default {
    name: 'search-year-month',
    components: {
        [BdDate.name]: BdDate
    },
    props: {
        // label标签字段
        label: {
            type: String
        },
        // 字段名称
        id: {
            type: String
        },
        // 字段值
        value: {
            type: String
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
        // 获取预输入文本
        _placeholder () {
            if (!this.label) return
            return GlobalConst.form.placeholder.select + this.label
        },
        tempValue: {
            get () {
                return this.value
            },
            set (val) {
                this.$emit('input', val)
                // 值更新事件传给父级组件
                this.$emit('change', this.id)
                this.$emit('search')
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