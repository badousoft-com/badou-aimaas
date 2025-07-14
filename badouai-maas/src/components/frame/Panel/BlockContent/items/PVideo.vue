<!--
 * @FilePath: @/components/frame/Panel/BlockContent/items/PVideo.vue
 * @Description: 面板 - 视频
-->
<template>
    <div class="p-video h-per-100" ref="pVideo">
        <video class="video" :src="videoURL" controls="controls" :style="{height: boxH + 'px'}"></video>
    </div>
</template>

<script>
export default {
    inheritAttrs: false,
    props: {
        videoURL: {
            type: String,
            require: true
        }
    },
    data () {
        return {
            boxH: 0,
        }
    },
    methods: {
        resize () {
            this.$nextTick(() => {
                this.boxH = this.$refs.pVideo.clientHeight
            })
        }
    },
    mounted () {
        this.resize()
    }
}
</script>

<style lang="scss" scoped>
.p-video {
    display: flex;
    align-items: flex-end;
    .video {
        width: 100%;
        // height: 100%;
    }
}
</style>