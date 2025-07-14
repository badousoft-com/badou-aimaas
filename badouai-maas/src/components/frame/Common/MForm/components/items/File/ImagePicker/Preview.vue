<template>
    <viewer
        ref="viewer"
        :images="value"
        class="m-previewImg"
        @inited="inited"> 
        <div
            v-for="(i, index) in value"
            :key="index"
            class="previewImg"
            :style="getImgSizeStyle">
            <img
                class="imgContain"
                :style="getImgSizeStyle"
                :alt="i.name || ' '"
                :src="i.url"/>
            <div class="showPreviewBtn" @click="showPreview(index)">
                <bd-icon name='search'></bd-icon>
            </div>
        </div>
    </viewer>
</template>
<script>
import Vue from 'vue'
import 'viewerjs/dist/viewer.css'
import Viewer from 'v-viewer'
Vue.use(Viewer, {
    // 默认设置图片预览图层层级为最高
    defaultOptions: {
        zIndex: 9999,
        // 自定义标题
        title: (image, imageData) => {
            return `${image.alt}(${imageData.naturalWidth} × ${imageData.naturalHeight})`
        },
        // 设置自定义样式类作用于预览组件顶级标签
        className: 'bd-viewer'
    },
    
})
const defaultSize = 100
export default {
    components: {},
    props: {
        // 期望数据格式为[{url:""}]
        value: {
            type: Array,
            default: () => []
        },
        size: {
            type: [Number,String],
            default: defaultSize
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
        getImgSizeStyle () {
            let size = `${Number(this.size || defaultSize)}px`
            return {
                width: size,
                height: size
            }
        }
    },
    methods: { // 定义函数
        // 初始化
        inited (viewer) {
            this.$viewer = viewer
        },
        // 添加预览窗口的关闭回调事件
        addCloseCallback () {
            // 定义作用域，方便click事件中使用
            let _this = this
            // 根据预览窗口样式类获取元素
            let _allViewers = document.getElementsByClassName('viewer-canvas')
            if (_allViewers && _allViewers.length > 0) {
                for (let i = 0; i < _allViewers.length; i++) {
                    // 绑定事件
                    _allViewers[i].onclick = function (event) {
                        if (!event) return
                        // 若当前点击的是非图片的区域，下一秒则会关闭弹窗，此时抛出关闭事件给父级组件
                        if (event.target.className.includes('viewer-canvas')) {
                            _this.$emit('close')
                        }
                    }
                }
            }
        },
        // 展示预览模块组件
        showPreview (index) {
            // 调用v-viewer组件实现预览
            //     v-viewer指导文章说明：https://mirari.cc/2017/08/27/Vue%E5%9B%BE%E7%89%87%E6%B5%8F%E8%A7%88%E7%BB%84%E4%BB%B6v-viewer%EF%BC%8C%E6%94%AF%E6%8C%81%E6%97%8B%E8%BD%AC%E3%80%81%E7%BC%A9%E6%94%BE%E3%80%81%E7%BF%BB%E8%BD%AC%E7%AD%89%E6%93%8D%E4%BD%9C/
            this.$viewer.index = index
            this.$viewer.show()
            // 添加预览窗口的关闭回调事件
            this.addCloseCallback()
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
$pic_size: 100px;
// 查看状态下可见
.m-previewImg {
    display: inline-block;
    margin-top: -$padding;
    margin-right: -$padding;
    .previewImg {
        width: $pic_size;
        height: $pic_size;
        margin: $padding $padding 0 0;
        position: relative;
        display: inline-block;
        overflow: hidden;
        border: 1px solid $lineColor;
        border-radius: $borderRadius;
        background: $contentInBg;
        
        .showPreviewBtn {
            opacity: 0;
            position: absolute;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
            background: rgba(0, 0, 0, 0.4);
            color: #fff;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s;
            .bd-icon {
                position: relative;
                top: 0;
                transform: translateY(0);
                transition: all 0.6s;
            }
        }
        &:hover {
            .showPreviewBtn {
                opacity: 1;
                .bd-icon {
                    transform: translateY(-50%);
                    top: 50%;
                }
            }
        }
    }
}
</style>
<style lang="scss">
.bd-viewer {
    .viewer-title {
        // 使标题底部不被遮挡部分
        line-height: 1.2;
    }
}

</style>