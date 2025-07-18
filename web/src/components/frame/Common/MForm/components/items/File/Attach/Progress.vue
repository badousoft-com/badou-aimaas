<template>
    <div
        class="bd-progress"
        :style="{
            opacity: _opacity,
            height: _height,
            background: bg,
        }">
        <div
            :style="{
                width: _width,
                background: progressBg,
            }"
            class="bd-progress-percent">
        </div>
    </div>
</template>
<script>
export default {
    name: 'bd-progress',
    props: {
        // 进度值
        value: {
            type: [String, Number]
        },
        // 是否展示(占位)
        seen: {
            type: Boolean,
            default: true
        },
        // 底背景色
        bg: {
            type: String,
            default: '#dadada'
        },
        // 已加载的背景色
        progressBg: {
            type: String,
            default: '#22b061'
        },
        height: {
            type: [String, Number],
            default: 3
        }
    },
    data () {
        return {
        }
    },
    computed: {
        // 透明度
        _opacity () {
            return this.seen ? 1 : 0
        },
        // 已加载的宽度百分比
        _width () {
            return `${parseInt(this.value)}%`
        },
        _height () {
            return `${parseInt(this.height)}px`
        }
    },
    methods: {
    },
    created () {
    },
    mounted () {
    }
}
</script>
<style lang="scss" scoped>
.bd-progress {
    width: 100%;
    transition: opacity .06s;
    .bd-progress-percent {
        height: 100%;
        transition: width .3s
    }
}
</style>