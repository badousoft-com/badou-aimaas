<template>
    <div class="location" v-if="cMenuPath.length > 0">
        <slot>
            <i class="fa fa-map-marker" :style="baseStyle"></i>
            <span v-for="(item, index) in cMenuPath" :key="index"><span v-if="index > 0"> > </span>{{item}}</span>
        </slot>
    </div>
</template>

<script>
export default {
    name: "location",
    props: ['locationPath'],
    computed: {
        cMenuPath() {
            if (this.locationPath) {
                return this.locationPath.split('-')
            }
            return []
        },
        baseStyle() {
            let style = this.$store.getters['SysStyle/baseStyle']
            return {
                color: style.backgroundColor
            }
        }
    }
}
</script>

<style scoped>
    .location {
        font-size: 14px;
        background-color: #ffffff;
        margin-bottom: 10px;
        padding: 5px 0px;
        color: #7f7f7f;
    }

    /*.fa {*/
        /*color: #2783ce;*/
    /*}*/
</style>
