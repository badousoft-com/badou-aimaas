<template>
    <span class="running-text">
        <span class="running-text__title">{{data}}</span>
        <template v-if="animation">
            <span v-for="(i, index) in nowPointNum" :key="index">.</span>
        </template>
    </span>
</template>
<script>
const POINT_NUM = 6
export default {
    name: 'running-text',
    components: {},
    props: {
        // 展示的文本
        data: {
            type: String,
            required: true
        },
        // 加载的点数
        pointNum: {
            type: [String, Number],
            default: POINT_NUM
        },
        // 点增加的时间间隔
        timeGap: {
            type: [String, Number],
            default: 500
        },
        // 点动画
        animation: {
            type: Boolean,
            default: true
        }
    },
    data () { // 定义页面变量
        return {
            // 总点数
            limitPointNum: null,
            // 当前点数
            nowPointNum: POINT_NUM,
            // 点数动画定时器
            pointNumTimer: null
        }
    },
    computed: {
    },
    methods: { // 定义函数
        // 启动点数动画
        openPointNumtimer () {
            if (!this.animation) return
            this.pointerNumTimer = setInterval(() =>{
                if (this.nowPointNum < this.limitPointNum) {
                    this.nowPointNum ++
                } else {
                    this.nowPointNum = 0
                }
                
            }, this.timeGap)
            // 通过$once来监听定时器
            // 在beforeDestroy钩子触发时清除定时器
            this.$once('hook:beforeDestroy', () => {
                clearInterval(this.pointerNumTimer)
            })
        }
    },
    // 可访问当前this实例
    created () {
        // 获取点数
        this.limitPointNum = parseInt(this.pointNum) || POINT_NUM
        // 启动动画
        this.openPointNumtimer()
    },
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
.running-text {
    .running-text__title {
        margin-right: -2px;
    }
}
</style>