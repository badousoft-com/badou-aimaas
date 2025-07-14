<template>
    <div class="main-panel" style="height: 100%;">
        <iframe id=“frame” :src=url
            style="width: 100%;border: 0px;margin:0;height: 100%;"></iframe>
        <div class="pointer returnBottom" title="返回" @click="back">
            <bd-icon name="back"></bd-icon>
        </div>
    </div>
</template>

<script>

export default {
    name: '',
    data () { // 定义页面变量
        return {
            url: "",
        }
    },
    methods: {
        back() {
            this.pushPage({
                path: `/module/stander/list/maas_fine_tuning_modeln/placeholder`,
            })
        }
    },
    created () { 
        this.url = atob(this.$route?.query.url)
    }
}
</script>

<style scoped>
.returnBottom {
    font-size: 50px;
    color:white;
    position: absolute;
    bottom: 30px;
    right: 0;
    transform: translateX(-50%) translateY(-50%);
    background: black;
    border-radius: 50%;
    opacity: 0.8;
    transition: all 0.3s;
}
</style>