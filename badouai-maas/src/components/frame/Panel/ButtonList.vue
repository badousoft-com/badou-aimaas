<!--
 * @FilePath: @/components/frame/Panel/ButtonList.vue
 * @Description: 按钮列表
-->
<template>
    <div v-if="btnList && btnList.length" class="button-list_container">
        <div class="button-list">
            <bd-button
                v-for="(i, index) in btnList"
                class="panel-button_item"
                :key="index"
                :plain="true"
                :fillIcon="true"
                v-bind="i"
                @click="handleClick(i)">
            </bd-button>
        </div>
        <div class="btn-fixed-right-slot">
            <render-fun v-if="diy_btnFixedRightSlot" :render="diy_btnFixedRightSlot"></render-fun>
        </div>
        <div class="btn-fixed-bottom-slot">
            <render-fun v-if="diy_btnNextLineSlot" :render="diy_btnNextLineSlot"></render-fun>
        </div>
    </div>
</template>

<script>
import RenderFun from '@/components/frame/RenderFun'
export default {
    // 禁止父级传入属性作为当前组件的根标签属性
    inheritAttrs: false,
    components: {
        [RenderFun.name]: RenderFun,
    },
    props: {
        btnList: {
            type: Array,
            default: () => []
        },
        // 按钮栏最右侧固定区域
        btnFixedRightSlot: {
            type: Function,
            default: null
        },
        // 按钮栏下面区域
        btnNextLineSlot: {
            type: Function,
            default: null
        },
        // 父级作用域
        parentScope: {
            type: Object,
            default: null
        }
    },
    data: () => ({
        // 按钮右侧插槽
        diy_btnFixedRightSlot: null,
        // 按钮下方插槽
        diy_btnNextLineSlot: null,
    }),
    methods: {
        handleClick (btnObj) {
            this.$emit('clickBtn', btnObj)
        },
        // 获取插槽
        async getSlot (fnName, slotKey) {
            if (this[fnName] && typeof this[fnName] === 'function') {
                this[slotKey] = await this[fnName].call(this.parentScope || this)
            }
        },
        refreshSlot (refreshKey) {
            if (refreshKey) {
                this.getSlot(refreshKey, `_${refreshKey}`)
            } else {
                this.getSlot('btnFixedRightSlot', 'diy_btnFixedRightSlot')
                this.getSlot('btnNextLineSlot', 'diy_btnNextLineSlot')
            }
        }
    },
    mounted () {
        this.refreshSlot()
    }
}
</script>

<style lang="scss" scoped>
.button-list_container {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    align-items: center;
    .button-list {
        flex: none;
    }
    .btn-fixed-right-slot {
        flex: 1;
        text-align: right;
    }
    .btn-fixed-bottom-slot {
        flex: none;
        width: 100%;
        margin-top: 10px;
    }
}
</style>