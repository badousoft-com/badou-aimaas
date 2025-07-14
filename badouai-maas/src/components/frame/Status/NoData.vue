// 废弃使用
<template>
    <div class="s-noData">
        <img class="s-noData-img" :src='publicPath + "static/img/noData.png"'/>
        <div class="s-noData-text">{{text}}</div>
    </div>
</template>
<script>
import GlobalConst from '@/service/global-const'
export default {
    components: {},
    props: {
        text: {
            type: String,
            default: GlobalConst.tip.noDataTip
        }
    },
    data () { // 定义页面变量
        return {
            publicPath: process.env.BASE_URL
        }
    },
    computed: {
    },
    methods: { // 定义函数
        count () {
            console.log(this.num)
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
.s-noData {
    position: absolute;
    text-align: center;
    bottom: 0;
    left: 0;
    right: 0;
    top: 0;
    margin: auto;
    .s-noData-img {
        width: auto;
        height: 80%;
        min-height: 100px;
    }
    .s-noData-text {
        font-size: $fontS;
        color: $fontCS;
    }
}
</style>