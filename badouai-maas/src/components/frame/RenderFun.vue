<script>
export default {
    name: 'render-fun',
    // 设置为函数式组件,渲染开销会低很多
    functional: true,
    props: {
        // 渲染内容
        render: {
            type: Function
        }
    },
    render (createElement, context) {
        // context.props: 获取传入props集合
        // context.props.render：获取传入的render属性
        let renderFun = context.props.render
        if (renderFun && typeof renderFun === 'function') {
            // 执行函数并返回
            return renderFun(createElement, context)
        }
    }
}
</script>
<style lang='scss' scoped>

</style>