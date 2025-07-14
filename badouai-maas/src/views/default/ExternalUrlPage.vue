<template>
    <div class="external-url per-100"
        v-loading.lock="loading"
        :element-loading-background="loadingBg">
        <iframe
            :src="src"
            frameborder="0"
            width="100%"
            height="100%"
            ref="iframe">
        </iframe>
    </div>
</template>
<script>
import GlobalStyle from '@/styles/frame.scss'
import { URL_Code } from '@/utils/url-encode'
export default {
    data () { // 定义页面变量
        return {
            // loadin背景色
            loadingBg: GlobalStyle.loadingBg,
            // 加载状态
            loading: false,
            src: null
        }
    },
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        // 获取页面路由地址上携带的编码地址
        let _srcCode = this.$route?.params?.path
        if (!_srcCode) return
        // 解码，获取真正的页面地址
        this.src = URL_Code.decode(_srcCode)
        // 开启加载效果
        this.loading = true
        const iframe = this.$refs.iframe
        iframe.onload = () => {
            // 等待iframe加载完成后，关闭loading加载效果
            this.loading = false
        }
    }
}
</script>
<style lang='scss' scoped>

</style>