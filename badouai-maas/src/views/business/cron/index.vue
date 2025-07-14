<template>
    <div id="app" class="app">
        <vcrontab class="dialog" @fill="crontabFill" :expression="expression"></vcrontab>
    </div>
</template>

<script>
// import vcrontab from '@/utils/ontime_publish/vcrontab.umd'
import vcrontab from "@/components/business/cus-cron/index.vue";
import { S_Storage } from '@/utils/storage'
export default {
    components: { vcrontab },
    data() {
        return {
            input: "",
            expression: "",
            showCron: true
        };
    },
    methods: {
        crontabFill(value) {
            // 确定后回传的值
            this.input = value
            S_Storage.setItem('cron', this.input)
        },
        showDialog() {
            this.expression = this.input;   //传入的 cron 表达式，可以反解析到 UI 上
            this.showCron = true;
        }
    }
};
</script>

<style scoped>
    #app {
        min-width: 700px;
        margin-left: 10px;
    }

    .dialog {
        width: 700px;
    }
</style>