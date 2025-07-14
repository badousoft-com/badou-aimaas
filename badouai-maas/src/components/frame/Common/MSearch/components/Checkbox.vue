<template>
    <div class="search-checkbox">
        <div class="search-checkbox__label fontCL">{{label}}:</div>
        <div class="search-checkbox__content">
            <el-checkbox-group
                v-if="tempOptions && tempOptions.length > 0"
                v-model="tempValue" 
                @change="change">
                <el-checkbox
                    v-for="(i, index) in tempOptions" 
                    :key="index"
                    :label="i.id"
                    :name="i.id">
                    {{i.text}}
                </el-checkbox>
            </el-checkbox-group>
            <span v-else class="form-item-lineHeight fontCS">{{noDataTip}}</span>
        </div>
    </div>
</template>
<script>
import GlobalConst from '@/service/global-const'
export default {
    name: 'search-checkbox',
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
        },
        // 下拉数据源数组
        option: {
            type: Array,
            default: () => []
        },
        // 无数据的提示
        noDataTip: {
            type: String,
            default: GlobalConst.form.noDataTip
        },
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
        // 页面展示value
        tempValue: {
            get: function () {
                // 组件需要接收值类型为Array，如果值为字符串则需要转化为数组格式
                if (!this.value) return []
                return this.value.split(GlobalConst.form.valueSeparator)
            },
            set: function (val) {
                // 获取选中值（字符串格式）
                let _val = val.join(GlobalConst.form.valueSeparator)
                // 调用父级update事件进行value值更新
                this.$emit('input', _val)
                // 值更新事件传给父级组件
                this.$emit('change', this.id)
            }
        },
        tempOptions () {
            return this.option
        }
    },
    methods: { // 定义函数
        change (value) {
            // 获取选中值（字符串格式）
            let _val = value.join(GlobalConst.form.valueSeparator)
            this.$emit('input', _val)
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
.search-checkbox::v-deep {
    display: flex;
    align-items: center;
    .search-checkbox__label {
        padding-right: $padding;
    }
    .search-checkbox__content {
        flex: 1;
    }
}
</style>