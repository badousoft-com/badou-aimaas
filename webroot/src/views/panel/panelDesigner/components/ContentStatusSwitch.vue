<!--
 * @FilePath: @/views/panel/panelDesigner/components/ContentStatusSwitch.vue
 * @Description: 内容状态设置
-->
<template>
    <el-popover
        width="200"
        trigger="hover">
        <ul class="switch_content">
            <li
                v-for="(c, c_index) in _contentList"
                :key="c_index"
                class="switch_item">
                <el-checkbox
                    v-model="c.isHide"
                    :true-label="0"
                    :false-label="1"
                    @change="(e) => handlerChange(c, e)">
                    {{c.name}}
                </el-checkbox>
            </li>
        </ul>
        <template slot="reference">
            <slot></slot>
        </template>
  </el-popover>
</template>

<script>
export default {
    props: {
        // 内容列表
        contentList: {
            type: Array,
            require: true
        }
    },
    computed: {
        _contentList: {
            get () {
                return this.contentList
            },
            set (val) {
                this.$emit('update:contentList', val)
            }
        }
    },
    methods: {
        handlerChange (content, value) {
            this.$set(content, 'isHide', value)
            this.$emit('change', content, value)
        }
    }
}
</script>

<style lang="scss" scoped>
.switch_content {
    max-height: 240px;
    overflow: scroll;
    .switch_item {
        padding: 5px 0;
    }
}
ul, li {
    margin: 0;
    padding: 0;
    list-style: none;
}
</style>