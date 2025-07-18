<template>
    <div>
        <m-form-item 
            class="bdFormItem"
            v-for="(i,index) in formItemList" 
            :key="index" 
            :formItem="i"
            :style="i.style">
        </m-form-item>
        <slot></slot>    
    </div>
</template>

<script>
import MFormItem from '@/components/frame/Common/MFormItem'
export default {
    components: {
        MFormItem
    },
    props: {
        formItemList: Array
    },
    data () {
        return {
        }
    },
    methods: {
    }
}
</script>

<style lang="scss" scoped>

</style>
