<template>
    <text-transfer-input
        v-model="tempValue"
        :beforeDblclick="beforeDblclick"
        class="relation-item-name">
        <!-- 主表标识 -->
        <template v-if="isMainForm" v-slot:text-prev>
            <div class="warningSign">主</div>
        </template>
    </text-transfer-input>
</template>
<script>
import TextTransferInput from '@/components/frame/Common/MForm/components/items/TextTransferInput'
export default {
    name: 'relation-item-name',
    components: {
        [TextTransferInput.name]: TextTransferInput
    },
    props: {
        // 值
        value: {
            type: String
        },
        // 是否为主表
        isMainForm: {
            type: Boolean,
            default: false
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
        tempValue: {
            get () {
                return this.value
            },
            set (val) {
                this.$emit('input', val)
            }
        }
    },
    methods: { // 定义函数
        /**
         * 双击前事件，主表不允许修改编辑
         */
        beforeDblclick () {
            if (this.isMainForm) {
                this.$alert('此处不允许修改主表名称，请前往主表的【模型管理】，修改[模型名称]值')
                return
            }
            return true
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