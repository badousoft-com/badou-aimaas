<template>
    <div class="main-panel">
        <el-row style="margin-top: 8px;">
            <el-col :span="7">
                <div class="grid-content bg-purple-dark">
                    <div class="total-count">
                        <el-badge class="item" type="warning">
                            <el-button size="small">统计数量</el-button>
                        </el-badge>
                        <el-badge :value="1" class="item" type="warning">
                            <el-button size="small">system(系统)</el-button>
                        </el-badge>
                        <el-badge :value="2" class="item">
                            <el-button size="small">user(用户)</el-button>
                        </el-badge>
                        <el-badge :value="2" class="item" type="primary">
                            <el-button size="small">assistant(助理)</el-button>
                        </el-badge>
                    </div>

                </div>


            </el-col>
            <el-col :span="9" style="min-height: 12px;">
                <div class="grid-content bg-purple-light">
                    <div style="margin-top: 11px;">
                        <el-input placeholder="请输入内容" v-model="input3" class="input-with-select">
                            <!-- <el-select v-model="select" slot="prepend" placeholder="请选择">
                                <el-option label="餐厅名" value="1"></el-option>
                                <el-option label="订单号" value="2"></el-option>
                                <el-option label="用户电话" value="3"></el-option>
                            </el-select> -->
                            <el-button slot="append" icon="el-icon-search"></el-button>
                        </el-input>
                        <el-input placeholder="请输入角色" v-model="input3" class="input-with-select">
                            <el-button slot="append" icon="el-icon-search"></el-button>
                        </el-input>
                    </div>
                </div>
            </el-col>
            <el-col :span="8" style="margin-top: 11px;text-align: right;">
                <el-button type="primary"><bd-icon name="save"></bd-icon>返回</el-button>
                <el-button type="primary"><bd-icon name="save"></bd-icon>保存</el-button>
                <el-button type="primary"><bd-icon name="add"></bd-icon>增加系统内容</el-button>
                <el-button type="primary"><bd-icon name="add"></bd-icon>增加用户内容</el-button>
                <el-button type="primary"><bd-icon name="add"></bd-icon>增加助手内容</el-button>
            </el-col>
        </el-row>
        <span class="line"></span>
        <el-row>
            <el-col :span="24">
                <el-card class="box-card">
                    <div slot="header" class="clearfix">
                        <div>
                            <span><bd-icon name="rolePermission"></bd-icon>角色:system</span>
                            <el-divider direction="vertical"></el-divider>
                            <span><bd-icon name="statistical"></bd-icon>序号:1</span>
                            <el-button style="float: right;" type="danger" size="medium">删除</el-button>
                        </div>
                    </div>
                    <div>我想让你做一个导游。我会把我的位
                        置写给你，你会推荐一个靠近我的位
                        置的地方。在某些情况下，我还会告
                        诉您我将访问的地方类型。您还会向
                        我推荐靠近我的第一个位置的类以类
                        型的地方。
                    </div>
                    <!-- <div v-for="o in 4" :key="o" class="text item">
                        {{ '列表内容 ' + o }}
                    </div> -->
                </el-card>
                <el-card class="box-card">
                    <div slot="header" class="clearfix">

                        <div>
                            <span><bd-icon name="rolePermission"></bd-icon>角色:用户</span>
                            <el-divider direction="vertical"></el-divider>
                            <span><bd-icon name="statistical"></bd-icon>序号:1</span>
                            <el-button style="float: right;" type="danger" size="medium">删除</el-button>
                        </div>
                    </div>
                    <div>今天我想喝奶茶，你有什么建议吗？</div>
                    <!-- <div v-for="o in 4" :key="o" class="text item">
                        {{ '列表内容 ' + o }}
                    </div> -->
                </el-card>
                <el-card class="box-card">
                    <div slot="header" class="clearfix">

                        <div>
                            <span><bd-icon name="rolePermission"></bd-icon>角色:助理</span>
                            <el-divider direction="vertical"></el-divider>
                            <span><bd-icon name="statistical"></bd-icon>序号:1</span>
                            <el-button style="float: right;" type="danger" size="medium">删除</el-button>
                        </div>
                    </div>
                    <div>现在流行奶茶很多，不过可以看附近有什么奶茶店。</div>
                </el-card>
                <el-card class="box-card">
                    <div slot="header" class="clearfix">

                        <div>
                            <span><bd-icon name="rolePermission"></bd-icon>角色:用户</span>
                            <el-divider direction="vertical"></el-divider>
                            <span><bd-icon name="statistical"></bd-icon>序号:2</span>
                            <el-button style="float: right;" type="danger" size="medium">删除</el-button>
                        </div>
                    </div>
                    <div>角色:你帮我点一杯柠檬茶，不要加冰。</div>
                    <!-- <div v-for="o in 4" :key="o" class="text item">
                        {{ '列表内容 ' + o }}
                    </div> -->
                </el-card>
                <el-card class="box-card">
                    <div slot="header" class="clearfix">

                        <div>
                            <span><bd-icon name="rolePermission"></bd-icon>角色:助理</span>
                            <el-divider direction="vertical"></el-divider>
                            <span><bd-icon name="statistical"></bd-icon>序号:2</span>
                            <el-button style="float: right;" type="danger" size="medium">删除</el-button>
                        </div>
                    </div>
                    <div>你去找位置坐着等我。。</div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script>
export default {
    name: 'project-total'
}
</script>

<style>
/* global styles */
.el-card__header {
    height: 48px;
    padding: 14px !important;
}
</style>


<style lang="scss" scoped>
.input-with-select{
    width: 280px;
}
.total-count {
    margin-left: 10px;
}

.item {
    margin-top: 10px;
    margin-right: 10px;
}

.text {
    font-size: 14px;
}

.item {
    margin-bottom: 18px;
}

.clearfix:before,
.clearfix:after {
    display: table;
    content: "";
}

.clearfix:after {
    clear: both
}

.box-card {
    width: 100%;
}
</style>