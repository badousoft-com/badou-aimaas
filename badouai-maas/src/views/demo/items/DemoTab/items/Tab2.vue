<template>
    <div class="padding bg-white">
        <m-list
            border
            :showPagination="false"
            :fieldList="fieldList"  
            :isSelection="true"
            :data.sync="_tableData">
        </m-list>
    </div>
</template>
<script>
    import { finalRequest } from '@/service/request'
    import MList from '@/components/frame/Common/MList/index.vue'

    export default {
        components: {
            MList
        },
        data () { // 定义页面变量
            return {
                params: {
                    mdCode: 'qw_template',
                },
                paginationNameObj: {
                    no: 'pageNo',
                    size: 'perPageSize'
                },
                list: [],
                fieldRequest: {},
                fieldList: [
                    { name: 'date', label: '更新时间'},
                    { name: 'name', label: '名称', icon: {
                        name: 'edit',
                        type: 'warning'

                    }},
                    { name: 'address', label: '地址', width: 200 },
                    { name: 'family', label: '户籍'}
                ],
                tableData: [
                    {
                        date: '2016-05-03',
                        name: '王小虎1王小虎2王小虎3王小虎4王小虎5王小虎6王小虎7王小虎8王小虎9王小虎10',
                        address: '上海市普陀区金沙江路 1516 弄',
                        family: '广东佛山',
                        remark: '块备注'
                    }, {
                        date: '2016-05-04',
                        name: '王小虎1王小虎2王小虎3王小虎4王小虎5王小虎6王小虎7王小虎8王小虎9王小虎10',
                        address: '上海市普陀区金沙江路 1517 弄',
                        family: '广东佛山',
                        remark: '备注点东西'
                    }, {
                        date: '2016-05-01',
                        name: '王小虎',
                        address: '上海市普陀区金沙江路 1519 弄',
                        family: '广东湛江',
                        remark: '备注'
                    }
                ],
            }
        },
        computed: {
            url () {
                return `${this.BASEURL}/jdbc/common/basecommonlist/listJSON.do?mdCode=classify`
            },
            fieldUrl () {
                return `${this.BASEURL}/project/financebigdata/bibalance/citybibalancelist/columnDisplay.do`
            },
            _tableData: {
                get () {
                    return this.tableData
                },
                set (val) {
                    this.$emit('update:data', val)
                }
            }
        },
        methods: { // 定义函数
        },
        // 可访问当前this实例
        created () {},
        // 挂载完成，可访问DOM元素
        mounted () {
            this.fieldRequest = {
                url: `${this.BASEURL}/project/financebigdata/bibalance/citybibalancelist/columnDisplay.do`,
                params: { mdCode: 'city_bi_balance' }
            }
        }
    }
</script>
<style lang='scss' scoped>

</style>