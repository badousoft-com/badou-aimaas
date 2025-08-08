<template>
    <div class="toggle-prev-to-next">
        <span class="s-toggle-text">第{{_currentIndexInTotal + 1}}条/共{{_total}}条</span>
        <span>
            <i
                class="el-icon-caret-top s-toggle-btn"
                title="上一条"
                :class="{
                    'h-c-primary': _currentIndexInTotal !== 0,
                    'fontCM': _currentIndexInTotal === 0,
                    'pointer-events-none': _currentIndexInTotal === 0
                }"
                @click="goNext()"></i>
            <i
                class="el-icon-caret-bottom s-toggle-btn"
                :class="{
                    'h-c-primary': _currentIndexInTotal !== _total - 1,
                    'fontCM': _currentIndexInTotal === _total - 1,
                    'pointer-events-none': _currentIndexInTotal === _total - 1
                }"
                title="下一条"
                @click="goNext(true)"></i>
        </span>
    </div>
</template>
<script>
// import xx from ./xx
export default {
    name: 'toggle-prev-to-next',
    components: {},
    props: {
        // 当前数据项在总数据中的排序
        currentIndexInTotal: {
            type: [Number, String]
        },
        // 总数据条数
        total: {
            type: [Number, String]
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
        _currentIndexInTotal () {
            return parseInt(this.currentIndexInTotal || 0)
        },
        _total () {
            return parseInt(this.total || 0)
        }
    },
    methods: { // 定义函数
        /**
         * 上一条/下一条切换
         * @param {Boolean} status 是否下一条
         */
        goNext (status) {
            this.$emit('toggle', status)
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
.toggle-prev-to-next {
    font-weight: 400;
    font-size: $font;
    .s-toggle-text {
        display: inline-block;
        vertical-align: bottom;
    }   
    .s-toggle-btn {
        font-size: 24px;
    }
}
</style>