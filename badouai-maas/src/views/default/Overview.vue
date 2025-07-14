<template>
    <div class="overview">
        <panel-code
            v-if="panelId"
            :panelId="panelId">
        </panel-code>
    </div>
</template>

<script>
import PanelCode from '@/components/frame/Panel/PanelCode/index.vue'
import { mapGetters } from 'vuex'
export default {
    name: 'Overview',
    components: {
        PanelCode
    },
    // 路由守卫函数，即将进入页面前
    beforeRouteEnter (from, to, next) {
        next(vm => {
            // 通过 `vm` 访问组件实例
            let homeUrl = vm.$store.getters.homeUrl
            if (homeUrl) {
                // 当前项目存在配置的主页面，跳转该页面
                next({ path: homeUrl })
                // next()
            } else {
                // 继续当前
                next()
            }
        })
    },
    data () {
        return {
            moduleCode: '',
        }
    },
    computed: {
        ...mapGetters([
            'panelId'
        ]),
    }
}
</script>

<style lang="scss" scoped>
.overview {
    padding: 0px 2px;
}
</style>
