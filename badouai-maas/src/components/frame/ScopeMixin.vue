<script>
// import xx from ./xx
export default {
    props: {
        getScope: {
            type: Function
        }
    },
    mounted () {
        // 将页面作用域传回弹窗中变量接受，方便弹窗域调用自定义页面域
        this &&
        this.getScope &&
        typeof this.getScope === 'function' &&
        this.getScope(this)
    }
}
</script>