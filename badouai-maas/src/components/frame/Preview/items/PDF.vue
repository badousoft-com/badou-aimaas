<template>
    <div class="bd-pdf per-100"
        v-loading.lock="loading"
        :element-loading-background="loadingBg">
        <template v-if="realSrc">
            <pdf
                v-for="(i, index) in numPages"
                :key="index"
                :src="realSrc"
                :page="i"
                ref="pdf"
                @page-loaded="pageLoaded">
            </pdf>
        </template>
    </div>
</template>
<script>
import pdf from 'vue-pdf'
import GlobalStyle from '@/styles/frame.scss'
export default {
    name: 'bd-pdf',
    components: {
        pdf
    },
    props: {
        // 附件地址
        src: {
            type: String
        }
    },
    data () { // 定义页面变量
        return {
            // loadin背景色
            loadingBg: GlobalStyle.loadingBg,
            // pdf页数
            numPages: 0,
            // 加载状态
            loading: true,
            // 实际src值
            realSrc: null
        }
    },
    computed: {},
    methods: { // 定义函数
        // 加载页码成功后回调函数
        pageLoaded () {
            setTimeout(() => {
                // 取消加载状态
                this.loading = false
            }, 500)
        },
        // 获取pdf页数
        getNumPages() {
            this.loading = true
            this.numPages = null
            let _loadingTask = pdf.createLoadingTask({
                url: this.src,
                cMapUrl: '@/../static/pdf_cmaps/',
                cMapPacked: true
            })
            _loadingTask.promise.then(res => {
                // 更新页码
                this.numPages = res.numPages
                // 更新文件值
                this.realSrc = _loadingTask
            }).catch(err => {
                console.error('pdf 加载失败', err)
            })
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {},
    watch: {
        // 对资源地址监听，变更页数
        src: {
            immediate: true,
            handler: function (newVal) {
                if (!newVal) return
                // 获取pdf页数，渲染页面视图
                this.getNumPages()
            }
        }
    }
}
</script>
<style lang='scss' scoped>
</style>
