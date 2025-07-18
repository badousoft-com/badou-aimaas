// 从组件列表拖拽到表单编辑区域时，有两块视图
// 1. 被拖动元素的默认视图；你拖动A，则鼠标下会带着一个透明度的A
// 2. 被拖动元素在编辑区域的视图展示，与编辑区域的dom与样式相关
//     2.1 你拖动A过去编辑区域（BBBB）,这个时候A靠近编辑区域时将将自己的dom插入编辑区域中进行展示
//     2.2 展示的样式取决于编辑区域的样式类，若组件列表与编辑区域是两个页面，A的样式定义在组件列表页面，则拖拽后A放入编辑区域，将没有样式控制
//     2.3 可以通过设置不同类名，让A在组件列表一种展示，在编辑区域一种展示
//     2.4 在2.3中不同展示可能会与数据也关联，所以可以在组件列表中写好两种状态的展示，使用各自需要的数据，再用样式类隐藏。以此来实现一个模块在拖拽过程的不同区域展示效果
<template>
    <el-form class="drag-item">
        <el-form-item
            :class="{
                'isBlock':formItem && formItem.isBlock, 
                'isShowAllLabel':formItem && formItem.isShowAllLabel}"
            :prop="formItem && formItem.name">  
            <span slot="label" 
                :title="formItem && formItem.label" 
                class="s-label">
                {{formItem && formItem.label}}
            </span>
        </el-form-item>
    </el-form>
</template>
<script>
export default {
    name: 'drag-show-item',
    components: {
    },
    props: {
        // 表单子项
        formItem: {
            type: Object,
            default: () => {}
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {},
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
@import '../../theme/index.scss';
$shakeDeg: 0.3deg;
.drag-item::v-deep {
    box-shadow: 6px 5px 6px #ababab;
    background: $fd-dragItem-Bg !important;
    animation: shake 0.2s linear infinite;
    .el-form-item__label {
        padding-left: 10px;
    }
    @keyframes shake {
        0% {
            transform: rotate(0deg)
        }
        25% {
            transform: rotate($shakeDeg)
        }
        50% { 
            transform: rotate(0deg) 
        }
        75% {
            transform: rotate(-$shakeDeg)
        }
        100% {
            transform: rotate(0deg)
        }
    }
}
</style>