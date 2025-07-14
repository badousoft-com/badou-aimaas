<script>
import Preview from '../ImagePicker/Preview'
import globalStyle from '@/styles/theme.scss'
export default {
    components: {
        Preview
    },
    render (h) {
        // 获取props属性： 
        //     isImg: 是否为图片
        //     icon-文件图标
        //     url-文件地址
        //     signSize-展示尺寸
        let { isImg, icon = 'attachLogo', url, signSize } = this.$attrs
        let iconSize = signSize && Number(signSize) - 5
        if (isImg) {
            // 当前为图片后缀
            return h(
                'preview', {
                    props: {
                        size: signSize,
                        value: [{url}]
                    }
                }
            )
        }
        // 非图片后缀
        return h(
            'bd-icon', {
                props: {
                    name: icon,
                    value: [{url}]
                },
                class: {
                    'noCursor': true
                },
                style: {
                    'width': `${iconSize}px`,
                    'height': `${iconSize}px`,
                    'vertical-align': `-${(iconSize - globalStyle.font) / 10}em`
                }
            }
        )
    },
    computed: {},
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        
    }
}
</script>
<style lang='scss' scoped>

</style>