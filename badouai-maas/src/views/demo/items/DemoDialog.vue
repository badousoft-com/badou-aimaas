<template>
    <div class="demo-dialog">
        <bd-button @click="showDialog()" type="primary" class="op">测试使用框架BdDialog</bd-button><br/>
        <bd-dialog
            title="这是新建的弹窗组件"
            :outScope="scope"
            :visible.sync="dialogVisible"
            :handlerList="handlerList">
            <!-- 尽量不使用title的插槽 -->
            <el-form :model="form">
                <el-form-item label="活动名称" :label-width="formLabelWidth">
                    <el-input v-model="form.name" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="活动区域" :label-width="formLabelWidth">
                    <el-select v-model="form.region" placeholder="请选择活动区域">
                    <el-option label="区域一" value="shanghai"></el-option>
                    <el-option label="区域二" value="beijing"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <el-table :data="gridData">
                <el-table-column property="date" label="日期" width="150"></el-table-column>
                <el-table-column property="name" label="姓名" width="200"></el-table-column>
                <el-table-column property="address" label="地址"></el-table-column>
            </el-table>
            <!-- footer的插槽 -->
            <!-- <template v-slot:footer>
                <bd-button icon="save" type="primary">测试按钮</bd-button>
                <bd-button icon="cancel" type="danger">取消</bd-button>
            </template> -->
        </bd-dialog>
    </div>
</template>
<script>
    import Dialog from '@/components/frame/Dialog/index.vue'
    export default {
        components: {
            [Dialog.name]: Dialog
        },
        data () { // 定义页面变量
            return {
                form: {
                    name: '',
                    region: '',
                },
                formLabelWidth: '120px',
                dialogVisible: false,
                gridData: [
                    {
                        date: '2016-05-02',
                        name: '王小虎',
                        address: '上海市普陀区金沙江路 1518 弄'
                    }, {
                        date: '2016-05-04',
                        name: '王小虎',
                        address: '上海市普陀区金沙江路 1518 弄'
                    }, {
                        date: '2016-05-01',
                        name: '王小虎',
                        address: '上海市普陀区金沙江路 1518 弄'
                    }, {
                        date: '2016-05-03',
                        name: '王小虎',
                        address: '上海市普陀区金沙江路 1518 弄'
                    }, {
                        date: '2016-05-04',
                        name: '王小虎',
                        address: '上海市普陀区金沙江路 1518 弄'
                    }, {
                        date: '2016-05-01',
                        name: '王小虎',
                        address: '上海市普陀区金沙江路 1518 弄'
                    }, {
                        date: '2016-05-03',
                        name: '王小虎',
                        address: '上海市普陀区金沙江路 1518 弄'
                    }, {
                        date: '2016-05-04',
                        name: '王小虎',
                        address: '上海市普陀区金沙江路 1518 弄'
                    }, {
                        date: '2016-05-01',
                        name: '王小虎',
                        address: '上海市普陀区金沙江路 1518 弄'
                    }, {
                        date: '2016-05-03',
                        name: '王小虎',
                        address: '上海市普陀区金沙江路 1518 弄'
                    }
                ],
                handlerList: [
                    { id: 'save', name: '保存', type: 'primary', icon: 'save', click: function () {
                        // this: 只想弹窗组件中的作用域
                        // this.outScope: 若使用dialog时传入属性scope:this,则此处可获取使用弹窗的页面作用域
                        // this.pageScope：挂载页面所在的作用域
                        alert(`表单数据：${JSON.stringify(this.outScope.form)}`)
                    } }
                ]
            }
        },
        computed: {
            // 当前组件作用域
            scope () {
                return this
            },
        },
        methods: { // 定义函数
            showDialog () {
                this.dialogVisible = true
            },
            open () {
            }
        },
        // 可访问当前this实例
        created () {},
        // 挂载完成，可访问DOM元素
        mounted () {}
    }
</script>
<style lang='scss' scoped>
.demo-dialog {
    .op {
        margin-top: 2%;
    }
}
</style>