<template>
    <div v-if="status">
        <template v-for="(i, index) in _message">
            <div
                :key="index + i"
                class="fontS warningC errorTip">
                <bd-icon name="tip-fill"></bd-icon>
                <span v-html="i"></span>
            </div>
            <br :key="index"/>
        </template>
    </div>
</template>
<script>
export default {
    name: 'error-tip',
    props: {
        // 是否显示错误提示语
        status: {
            type: Boolean,
            default: false
        },
        // 错误提示语
        //     1. 传入String，常规提示展示
        //     2. 传入数组，多行文本进行展示
        message: {
            type: [String, Array],
            default: ''
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
        // 获取信息
        _message: {
            get () {
                if (this.message) {
                    if (this.message.constructor === String) {
                        return [this.message]
                    }
                    if (this.message.constructor === Array) {
                        return this.message
                    }
                }
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
.errorTip {
    margin-top: 4px;
    display: inline-block;
    background: #F9F9F9;
    padding: 2px;
    border-radius: $borderRadius;
}
</style>