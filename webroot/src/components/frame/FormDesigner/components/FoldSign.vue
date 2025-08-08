<template>
    <div class="fold-sign">
        <div
            :style="borderDirColor"
            class="fold-sign-icon"
            >
        </div>
    </div>
</template>
<script>
const Direction = ['top', 'right', 'bottom', 'left']
export default {
    name: 'fold-sign',
    components: {},
    data () { // 定义页面变量
        return {
        }
    },
    props: {
        // 三角方向：left,right， 默认为right
        direction: {
            type: String,
            default: 'right'
        }
    },
    computed: {
        // 获取边框颜色style对象
        borderDirColor () {
            return Direction.reduce((res, item) => {
                // 边框默认已带色，需要设置非当前边的其他三边为透明色，形成三角型折叠图标
                if (Direction.includes(item) && item !== this.direction) {
                    res[`border-${item}-color`] = 'transparent'
                }
                // 设置水平位置
                res[this.direction] = '0'
                return res
            }, {})
        }
    },
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
@import '../theme/index.scss';
.fold-sign {
    position: relative;
    display: inline-block;
    width: 8px;
    height: 20px;
    .fold-sign-icon {
        width: 0;
        height: 0;
        border: 6px solid;
        border-color: #000;
        position: absolute;
        top: 50%;
        transform: translateY(-50%);
    }
    
}
</style>