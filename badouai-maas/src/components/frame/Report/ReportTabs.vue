<!--
 * 专属于报表的tabs，与BdTabs的区别在于，可以自有决定是否使用tab
-->
<template>
    <div class="report-tabs">
        <div class="m-tab" v-if="useTab">
            <bd-tabs
                :data="data"
                v-model="tempValue"
                :before-leave="beforeLeave"
                @tab-click="tabClick">
                <template v-for="(i, i_index) in data" v-slot:[i[idField].toString()]>
                    <div :key="i_index"><slot :name="i_index.toString()"></slot></div>
                </template>
            </bd-tabs>
        </div>
        <template v-else>
            <div v-for="(i, i_index) in data" :key="i_index">
                <slot :name="i_index.toString()"></slot>
            </div>
        </template>
    </div>
</template>

<script>
import BdTabs from '@/components/frame/Common/BdTabs'
const idField = 'id'
const textField = 'text'
export default {
    components: {
        BdTabs,
    },
    props: {
        // tab选中项值
        value: {
            type: String
        },
        // tab数据项数组
        data: {
            type: Array,
            default: () => []
        },
        // data传入数据中作为【显示文本的对应值】使用的字段
        idField: {
            type: String,
            default: idField
        },
        // data传入数据中作为【显示文本】使用的字段
        textField: {
            type: String,
            default: textField
        },
        height: {
            type: [String, Number]
        },
        // 切换标签之前的钩子，若返回 false 或者返回 Promise 且被 reject，则阻止切换。
        beforeLeave: {
            type: Function,
            default: () => Promise.resolve()
        },
        // 是否展示tab
        useTab: {
            type: Boolean,
            default: true
        }
    },
    computed: {
        // 选中值
        tempValue: {
            get () {
                return this.value
            },
            set (val) {
                this.$emit('input', val)
            }
        },
    },
    data () {
        return {
        }
    },
    methods: {
        tabClick (tab, event) {
            this.$emit('tab-click', tab, event)
        }
    },
    mounted () {
    }
}
</script>


<style lang="scss" scoped>

</style>
