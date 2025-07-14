<!--
 * @FilePath: @/components/frame/Panel/PanelFilter/expands/FTag.vue
 * @Description: 搜索组件拓展 - tag 形式的搜索
-->
<template>
    <div class="f-tag">
        <span
            v-for="o in tempInfo.options"
            :key="o.id"
            @click="handlerTag(o)"
            class="tag_item">
            <el-tag :effect="tempInfo.value.includes(o.id) ? 'dark' : 'plain'">{{o.text || '--'}}</el-tag>
        </span>
    </div>
</template>

<script>
export default {
    props: {
        field: {
            type: Object,
            require: true
        },
    },
    computed: {
        tempInfo: {
            get () {
                return this.field
            },
            set (val) {
                this.$emit('update:field', val)
            }
        }
    },
    methods: {
        handlerTag (option) {
            this.$set(this.tempInfo, 'value', option.id)
            this.$emit('search')
        }
    }
}
</script>

<style lang="scss" scoped>
.f-tag {
    .tag_item {
        margin-right: $space;
        cursor: pointer;
        &:last-child {
            margin-right: 0;
        }
    }
}
</style>