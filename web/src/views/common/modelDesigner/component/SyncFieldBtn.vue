<template>
    <div class="sync-field-btn-page">
        <div class="sync-tip">
            <p
                v-for="(i, index) in tipList"
                :key="index"
                class="fontS">
                <bd-icon :name="i.icon" :class="i.colorClass"></bd-icon>
                <span :class="i.colorClass" class="bold">{{i.partTitle}}:</span>{{i.title}}
            </p>
        </div>
    </div>
</template>
<script>
// 1.引入混淆组件
import ScopeMixin from '@/components/frame/ScopeMixin'
export default {
    name: '',
    components: {},
    // 使用混淆组件
    mixins: [ScopeMixin],
    props: {
        outScope: {
            type: Object
        }
    },
    data () { // 定义页面变量
        return {
            tipList: [
                { icon: 'tip-fill', colorClass: '', partTitle: '普通同步', title: '是指把当前字段列表-同步到数据库；若未建表则会自动建表以及同步所有字段；若已建表则已存在的字段就更新属性，不存在的字段则在表中新建' },
                { icon: 'tip-fill', colorClass: 'dangerC', partTitle: '强制同步', title: '是指把当前字段列表-直接覆盖数据库表,请谨慎操作！' },
            ]
        }
    },
    computed: {},
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {
    },
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
.sync-field-btn-page {
    padding: 10px 10px 20px;
}
</style>