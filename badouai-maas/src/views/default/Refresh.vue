<script>
// import xx from ./xx
export default {
    beforeRouteEnter (to, from, next) {
        next (vm => {
            // 跳到该路由页面后，再替换为from.path来源路径
            vm.$router.replace(from)
        })
    }
}
</script>