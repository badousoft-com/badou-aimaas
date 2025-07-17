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
    name: 'fd-form-setting',
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
        // _form () {
        //     let result = {}
        //     this.tempValue.forEach(i => {
        //         result[i.name] = i.value
        //     })
        //     return result
        // },
        tempValue: {
            get () {
                if (!this.value) return
                // switch类型的表单项依旧一行，其他类型处理为标签在上输入值在下的排版
                return this.value.map(i => {
                    i.isShowAllLabel = true
                    if (i.type !== 'switch') {
                        i.isBlock = true
                    }
                    return i
                })
            },
            set (val) {
                this.$emit('input', val)
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