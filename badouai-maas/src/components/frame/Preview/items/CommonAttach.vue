<template>
    <div class="bd-common-attach per-100"
        v-loading.lock="loading"
        :element-loading-background="loadingBg">
        <iframe
            :src="_src"
            frameborder="0"
            width="100%"
            height="100%"
            ref="iframe">
        </iframe>
    </div>
</template>
<script>
import { Is_External_Url } from '@/utils/validate'
import { Get_Attach_Url } from '@/api/frame/attach'
import { Get_UUID } from '@/utils'
import { Base64 } from 'js-base64'
import GlobalStyle from '@/styles/frame.scss'
export default {
    name: 'bd-common-attach',
    components: {},
    props: {
        // 附件地址
        src: {
            type: String
        },
        // 文件名
        name: {
            type: String
        }
    },
    data () { // 定义页面变量
        return {
            // loadin背景色
            loadingBg: GlobalStyle.loadingBg,
            // 加载状态
            loading: false,
        }
    },
    computed: {
        // 获取资源地址
        _src () {
            let _res = this.src
            if (!Is_External_Url(_res)) {
                // 判断是否为完整url，否则为attachId,则这里拼接为完整的URL
                _res = Get_Attach_Url(_res)
            }
            // 项目实施过程中，出现文件名同时出现中文括号与英文括号时会打不开文件，这里将所有英文括号转成中文括号
            let _name = this.name && this.name.replace(/\(/g, '（').replace(/\)/g, '）') || ''
            _res = `${_res}&fullfilename=${Get_UUID() + _name}`
            // 使用外网服务的预览能力
            _res = `${this.PREVIEW_URL}?url=${encodeURIComponent(Base64.encode(_res))}`
            return _res
        }
    },
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
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