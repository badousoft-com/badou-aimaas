<script>
export default {
    name: 'keep-alive',
    components: {},
    data () { // 定义页面变量
        return {
            // 是否启动刷新列表
            //     为避免从列表跳转编辑页面，编辑修改数据时列表能实时响应
            //     添加该状态执行刷新列表数据
            isStartReList: false
        }
    },
    // keep-alive 缓存的组件【激活时】调用
    activated () {
        // 缓存组件激活时，更改状态，准备重新请求列表数据
        this.isStartReList = true
    },
    // keep-alive 缓存的组件【停用时】调用
    deactivated () {
        // 缓存组件停用，重置状态
        this.isStartReList = false
    }
}
</script>
<style lang='scss' scoped>

</style>