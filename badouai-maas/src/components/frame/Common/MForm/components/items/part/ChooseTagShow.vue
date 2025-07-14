<template>
    <div class="choose-tag">
        <div class="choose-tag--main p-r">
            <disabled-board v-if="disabled"></disabled-board>
            <div
                class="choose-tag--input">
                <template v-if="data && data.length > 0">
                    <el-tag
                        v-for="(i, index) in data"
                        :key="index"
                        class="choose-tag--item"
                        closable
                        :disable-transitions="true"
                        @close="deleteItem(i)">
                        {{getTagName(i)}}
                    </el-tag>
                </template>
                <div v-else class="choose-tag--tip"  @click="showDialog">{{placeholder}}</div>
            </div>
        </div>
        <bd-button
            :disabled="disabled"
            class="mar-l"
            name="选择"
            icon="add"
            type="primary"
            @click="showDialog">
        </bd-button>
    </div>
</template>
<script>
import GlobalConst from '@/service/global-const'
import DisabledBoard from '@/components/frame/Status/DisabledBoard'
export default {
    name: 'choose-tag',
    components: {
        [DisabledBoard.name]: DisabledBoard,
    },
    props: {
        data: {
            type: Array,
            default: []
        },
        // 是否不可编辑状态
        disabled: {
            type: Boolean,
            default: false
        },
        idKey: {
            type: String,
            default: GlobalConst.dicOption.idName
        },
        textKey: {
            type: String,
            default: GlobalConst.dicOption.textName
        },
        placeholder: {
            type: String,
            default: '请选择'
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {},
    methods: { // 定义函数
        // 获取已选数据每项的展示文本
        getTagName (item) {
            return item?.[this.textKey]
        },
        deleteItem (item) {
            // 获取删除项所在下角标
            let _index = this.data.findIndex(i => i[this.idKey]=== item[this.idKey])
            // 删除该条数据
            this.data.splice(_index, 1)
            // 删除事件回调给父级组件
            //     item: 当前删除项
            //     this.data: 删除后的数据值
            this.$emit('delete', item, this.data)
        },
        showDialog () {
            this.$emit('showDialog')
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
$item-space: 4px;
.choose-tag {
    display: flex;
    .choose-tag--main {
        border-radius: $borderRadius;
        border: 1px solid $inputBorderColor;
        background: #fff;
        flex: 1;
        &:hover {
            border-color: $inputBorderColor_hover;
        }
        .choose-tag--input {
            margin-right: - $item-space;
            margin-bottom: - $item-space;
            padding: $item-space;
            .choose-tag--tip {
                line-height: $inputHeight - 2 * $item-space;
                padding-left: 15px - $item-space;
                color: $placeholderC;
                font-size: 13px;
            }
            .choose-tag--item {
                margin-right: $item-space;
                margin-bottom: $item-space;
                &.el-tag--small {
                    height: 22px;
                }
            }
        }
    }
}
</style>