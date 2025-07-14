<template>
    <div class="envs-item">
        <h3 class="envs-title">{{envItem.envName}}</h3>
        <div class="envs-pod">
            <div class="envs-pod-title">Pod默认资源请求</div>
            <div class="envs-pod-cell">
                <span class="envs-cpu">CPU限额 {{envItem.podLimitCpu}}</span>
                <span>内存限额 {{envItem.podLimitMemory}}</span>
            </div>
            <div class="envs-pod-cell">
                <span class="envs-cpu">CPU预留 {{envItem.podReserveCpu}}</span>
                <span>内存预留 {{envItem.podReserveMemory}}</span>
            </div>
        </div>
        <div class="envs-res">
            <div class="envs-res-title">资源配额</div>
            <div class="envs-res-cell">
                <span class="envs-cpu">CPU限额 {{envItem.resourceLimitCpu}}</span>
                <span>内存限额 {{envItem.resourceLimitMemory}}</span>
            </div>
        </div>
    </div>
</template>

<script>

export default {
    name: 'env-item',
    props: {
        // 环境列表
        envItem: {
            style: Object
        }
    },
    data () {
        return {
            // 环境列表
            envsList: []
        }
    }
}
</script>

<style lang="scss" scoped>
.envs-item {
    width: 27%;
    min-width: 300px;
    border: 1px solid #ccc;
    padding: 0 20px 10px 20px;
    margin-left: 20px;
    margin-top: 10px;
    .envs-pod {
        border-bottom: 1px solid #ccc;
    }
    .envs-pod-title,
    .envs-pod-cell,
    .envs-res-title,
    .envs-res-cell {
        padding: 5px 0;
    }
    .envs-cpu {
        display: inline-block;
        width: 116px;
    }
}
</style>