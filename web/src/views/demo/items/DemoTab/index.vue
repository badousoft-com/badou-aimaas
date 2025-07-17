<template>
    <div>
        <!-- <div class="padding">
            <el-input v-model="input"></el-input>
        </div> -->
        <!-- 这个height值必须给，http://localhost:8000/#/demo/showPageDialogByJs中的呈现才会正常<br/> -->
        <div>
            <el-input v-model="input"></el-input>
        </div>
        <bd-tabs
            class="mar-t"
            height="400px"
            v-model="activeName1"
            :data="tabData1"
            :before-leave="beforeLeave"
            @tab-change="tabChange"
            @tab-click="tabClick">
            <template v-slot:first>
                <tab1 ref="first"></tab1>    
            </template>
            <template v-slot:second>
                <tab2 ref="second"></tab2>
            </template>
            <template v-slot:third >
                <tab3 ref="third"></tab3>
            </template>
            <template v-slot:fourth>tab4</template>
            <template v-slot:right>
                <el-button type="primary">
                    <bd-icon name="save"></bd-icon>
                    <span>按钮</span>
                </el-button>
            </template>
        </bd-tabs>
        
        <!-- <bd-tabs
            v-model="activeName2"
            :data="tabData2"
            textField="label"
            idField="name">
            <template v-slot:first><div class="padding">11</div></template>
            <template v-slot:second><div class="padding">22</div></template>
            <template v-slot:third><div class="padding">33</div></template>
            <template v-slot:fourth><div class="padding">44</div></template>
            <template v-slot:right>
                <div class="primaryC bold">没事我就猫在Tab最右边</div>
            </template>
        </bd-tabs> -->
    </div>
</template>
<script>
    import BdTabs from '@/components/frame/Common/BdTabs'
    import Tab1 from './items/Tab1'
    import Tab2 from './items/Tab2'
    import Tab3 from './items/Tab3'

    import ScopeMixin from '@/components/frame/ScopeMixin'
    export default {
        components: {
            [BdTabs.name]: BdTabs,
            Tab1,
            Tab2,
            Tab3
        },
        mixins: [ScopeMixin],
        data () { // 定义页面变量
            return {
                input: '2112',
                activeName1: 'second',
                tabData1: [
                    { text: '用户管理', id: 'first', icon: 'save', unLeave: false },
                    { text: '配置管理', id: 'second', icon: 'edit',unLeave: false },
                    { text: '角色管理', id: 'third', icon: 'delete', unLeave: false },
                    { text: '定时任务补偿', id: 'fourth', icon: 'add', unLeave: false }
                ],
                activeName2: 'second',
                tabData2: [
                    { label: '用户管理', name: 'first' },
                    { label: '配置管理', name: 'second' },
                    { label: '角色管理', name: 'third' },
                    { label: '定时任务补偿', name: 'fourth' }
                ]
            }
        },
        computed: {},
        methods: { // 定义函数
            tabChange (tab, index, tabData) {
                // alert('触发tab change事件')
            },
            tabClick (tab, index, tabData, tabVueComponent) {
                // alert('触发tab change事件')
            },
            beforeLeave (activeName, oldActiveName) {
                let aim = this.$refs[oldActiveName]
                if (aim && aim.beforeLeave && typeof aim.beforeLeave === 'function') {
                    return aim.beforeLeave()
                }
                return true
            },
        },
        // 可访问当前this实例
        created () {},
        // 挂载完成，可访问DOM元素
        mounted () {}
    }
</script>
<style lang='scss' scoped>

</style>