<template>
    <bd-form
        v-if="tempValue && tempValue.length > 0"
        noTitle
        :columnNum="1"
        :dataList.sync="tempValue">
    </bd-form>
    <!-- 无数据展示 -->
    <bd-no-data v-else class="noDataTip"></bd-no-data>
</template>
<script>
import BdForm from '@/components/frame/Common/MForm/index'
import NoData from '@/components/frame/NoData'
export default {
    name: 'fd-device-setting',
    components: {
        [BdForm.name]: BdForm,
        [NoData.name]: NoData
    },
    props: {
        value: {
            type: Array
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
        tempValue: {
            get () {
                if (!this.value) return
                return this.value
            },
            set (val) {
                this.$emit('input', val)
            }
        }
    },
    methods: { // 定义函数
        count () {
            console.log(this.num)
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