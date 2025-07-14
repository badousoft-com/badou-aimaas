<template>
    <div class="">
        <el-tabs
            v-model="activeName"
            @tab-click="handleClick"
            :before-leave="beforeLeave">
            <el-tab-pane label="用户管理" name="first">用户管理</el-tab-pane>
            <el-tab-pane label="配置管理" name="second">配置管理</el-tab-pane>
            <el-tab-pane label="角色管理" name="third">角色管理</el-tab-pane>
            <el-tab-pane label="定时任务补偿" name="fourth">定时任务补偿</el-tab-pane>
        </el-tabs>
        <!-- <module-edit-code
            style="height:400px"
            ref="moduleEditCode"
            mdCode="WJX_FORM"
            detailId="36dbdb8b765a4684a6b718df729a81b1c66529c19c1049db8092a9294dd75103">
        </module-edit-code> -->
        {{typeof age}}
        <!-- 用作数字 -->
        <el-input 
            type="number"
            v-model.number="age"
            autocomplete="off">
        </el-input>
        <br/>
        ===========================
        <br/>
        {{typeof age1}}
        <!-- 用作数字 -->
        <el-input
            type="text"
            v-model.number="age1"
            autocomplete="off">
        </el-input>
        <el-select v-model="value" placeholder="请选择">
            <el-option
            :loading="loading"
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value">
            </el-option>
        </el-select>
        <div>Text页面</div>
        <!-- <div class="A">
            <module-edit
                style="height: 400px;"
                mdCode="WJX_GOODS"
                :staticModule="module"
                :editJSON="editJSON">
            </module-edit>
        </div>  -->
    </div>
</template>
<script>
import ModuleEditCode from '@/components/frame/Module/ModuleEditCode'
    import ModuleEdit from '@/components/frame/Module/ModuleEdit/index.vue'
    export default {
        components: {
            [ModuleEdit.name]: ModuleEdit,
            [ModuleEditCode.name]: ModuleEditCode
        },
        data () { // 定义页面变量
            return {
                age: null,
                age1: null,
                options: null,
                loading: true,
                value: '3',
                activeName: 'second',


                module: {"id":"4028816776648e1801768edf58120303","code":"WJX_GOODS","name":"wjx商品模型","editedJs":"/wjx_goods_edit.js","editedJsObj":{"buttons":[{"id":"text","icon":"export","name":"测试"}],"columnNum": 1,},"childTab":[],"fields":[{"valueFieldId":"","editTip":"","validationRule":"","display":"商品名称","isFullLine":0,"className":"","type":"text","required":false,"groupName":"","isDbField":1,"unique":true,"name":"name","valueFieldText":"","fileType":""},{"valueFieldId":"","editTip":"","validationRule":"required","display":"商品种类","isFullLine":0,"className":"{\"dataType\":\"number\"}","type":"text","required":false,"groupName":"","isDbField":1,"unique":false,"name":"type","valueFieldText":"","fileType":""},{"display":"ID","name":"id","type":"hidden","priority":0,"required":false}],"dic":{}},
                editJSON: {"num":{"value":null,"fieldId":"4028816776648e1801768ee0396b030b"},"name":{"value":"测试111","fieldId":"4028816776648e1801768edfb0fa0307"},"id":{"value":"02be2e2e9b844875bcb9d9a003527008c54ba5a93c5a446080cfbdcb202bde8b","fieldId":"4028816776648e1801768edf58120304"},"type":{"value":"测试222","fieldId":"4028816776648e1801768ee006600309"}},
            }
        },
        computed: {},
        methods: { // 定义函数
            async beforeLeave (active, old) {
                return false
                // return this.isCanChangeTab()
            },
            isCanChangeTab () {
                return new Promise((resolve, reject) => {
                    setTimeout(() => {
                        resolve(true)
                    },2000)
                })
            },
            handleClick(tab, event) {
                console.log(tab, event);
            },
            count () {
                console.log(this.num)
            },
            onlyNumber(value) {
  if(value != '' && value != undefined && value != NaN){
    let val = value;
    let newValue = val.toString().replace('.', '$#$').replace(/\./g, '').replace('$#$', '.')
    return parseInt(newValue == '' ? 0 : newValue);
  }else return 0
},
        },
        // 可访问当前this实例
        created () {},
        // 挂载完成，可访问DOM元素
        mounted () {
            

            setTimeout(() => {
                this.options = [{
                value: '1',
                label: '黄金糕'
                }, {
                value: '2',
                label: '双皮奶'
                }, {
                value: '3',
                label: '蚵仔煎'
                }, {
                value: '4',
                label: '龙须面'
                }, {
                value: '5',
                label: '北京烤鸭'
                }]
                this.loading = false
            }, 3000)
        }
    }
</script>
<style lang='scss' scoped>

</style>
