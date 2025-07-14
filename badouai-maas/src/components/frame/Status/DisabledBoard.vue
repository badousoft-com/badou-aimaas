<template>
    <!-- 禁止操作的遮罩板 -->
    <div class="disabled-board"></div>
</template>
<script>
export default {
    name: 'disabled-board',
}
</script>
<style lang='scss' scoped>
.disabled-board {
    cursor: not-allowed;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 2; // 常规使用1，富文本需要使用2才可以
    width: 100%;
    height: 100%;
    background: $formDisabledBg;
}
</style>