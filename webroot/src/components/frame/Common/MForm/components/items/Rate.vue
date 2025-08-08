<template>
    <div :class="selfClass">
        <div class="bd-rate">
            <el-rate
                :allow-half="allowHalf"
                :max="max"
                :disabled="disabled || isView"
                v-model="tempValue">
            </el-rate>
        </div>
    </div>
</template>
<script>
export default {
    name: 'bd-rate',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {},
    props: {
        // 设置组件是否可编辑，默认可编辑Boolean(false)，非必须
        disabled: {
            type: Boolean,
            default: false
        },
        // 字段名
        name: {
            type: String
        },
        // 组件默认值传入
        value: {
            type: [Number, String],
            default: 0
        },
        // 标签名称
        label: {
            type: String
        },
        // 是否为查看状态
        isView: {
            type: Boolean,
            default: false
        },
        // 最大值
        max: {
            type: Number,
            default: 5
        },
        // 是否允许半选
        allowHalf: {
            type: Boolean,
            default: true
        },
        // 图标名称：用于查看页面展示
        // iconName: {
        //     type: String
        // },
        // 自定义class
        selfClass: {
            type: String,
            default: ''
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
        // 单项数据流原因，不可直接修改值，需要用临时值做过度使用
        tempValue: {
            get () {
                return Number(this.value)
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
    mounted () {
    },
}
</script>
<style lang='scss' scoped>
    .bd-rate::v-deep {
        .el-rate {
            line-height: 2;
        }
    }
</style>