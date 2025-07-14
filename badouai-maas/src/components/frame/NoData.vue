<template>
    <div class="bd-no-data">
        <div class="bd-no-data__main">
            <div>
                <div :style="_imgStyle" class="no-data_img"></div>
                <span :class="{'none': noText}" class="bd-no-data__text">{{text}}</span>
                <slot name="text"></slot>
            </div>
        </div>
        <resize-observer @notify="resizeMore" />
    </div>
</template>
<script>
import GlobalConst from '@/service/global-const'
export default {
    name: 'bd-no-data',
    components: {},
    props: {
        // 自定义图片路径传入，网络地址
        src: {
            type: String
        },
        // 显示文本
        text: {
            type: String,
            default: GlobalConst.noData.text
        },
        // 是否不展示文本：默认false，展示
        noText: {
            type: Boolean,
            default: false
        },
        // 图片大小（相对大小，传入%）
        imgSize: {
            type: String,
            default: '60%'
        }
    },
    data () { // 定义页面变量
        return {
            imgStyle: {},
        }
    },
    computed: {
        defaultSrc () {
            return require('@/assets/image/frame/noData.png')
        },
        _src () {
            return this.src || this.defaultSrc
        },
        // 图片样式
        _imgStyle () {
            let defaultStyle = {
                backgroundImage: `url(${this._src})`,
                height: this.imgSize
            }
            return Object.assign(defaultStyle, this.imgStyle)
        } 
    },
    methods: { // 定义函数
        resizeMore (e) {
            this.imgStyle = {
                backgroundSize: e.width > e.height ? 'auto 100%' : `${this.imgSize} auto`,
            }
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
.bd-no-data {
    width: 100%;
    height: 100%;
    position: relative;
    .bd-no-data__main {
        width: 100%;
        height: 100%;
        text-align: center;
        display: table;
        font-size: $fontS;
        color: #ccc;
        &>div {
            width: 100%;
            display: table-cell;
            vertical-align: middle;
        }
        .no-data_img {
            background-image: url('~@/assets/image/frame/noData.png');
            background-size: auto 100%;
            background-repeat: no-repeat;
            background-position: center;
        }
        .bd-no-data__text {
            margin-top: -5%;
        }
    }
}
</style>