<template>
    <div class="address-book-dialog">
        <el-dialog
            width="950px"
            :title="config.title"
            :visible.sync="dialogVisible"
            class="scorpion">
            <div style="margin-left: 25px;">
                <!-- 流程定义信息 -->
                <div style="background-color: #f9f4f4">
                    <h5>{{config.flow.title}}</h5>
                    <p>
                        状态：
                        <span style="color: green;">{{config.flow.statusDesc}}</span>
                        &nbsp;&nbsp;&nbsp;
                        创建时间：
                        <span>{{config.flow.createDate}}</span>
                        &nbsp;&nbsp;&nbsp;
                        归档时间：
                        <span>{{config.flow.archiveDate}}</span>
                    </p>
                </div>
                <!-- 处理人颜色块说明 -->
                <div>
                    <p>
                        处理人颜色说明：
                        <el-tag color="orange">未处理</el-tag>
                        <el-tag color="green">已处理</el-tag>
                        <el-tag color="blue">超时未处理</el-tag>
                        <el-tag color="#d7d1cf">超时已处理</el-tag>
                    </p>
                </div>
                <!-- 操作按钮 -->
                <div
                    style="margin-bottom: 20px;  border-bottom: 1px solid; border-bottom-color: #ccc;">
                    <button class="btn btn-primary" @click="transfer">
                        <i class="fa fa-level-down"></i>转办
                    </button>
                    <button class="btn btn-primary" @click="jump">
                        <i class="fa fa-level-up"></i>跳转
                    </button>
                    <button class="btn btn-primary" @click="rehandle">
                        <i class="fa fa-mail-reply-all"></i>重处理
                    </button>
                    <button class="btn btn-primary" @click="traceOpinion">
                        <i class="fa fa-camera-retro"></i>流程跟踪
                    </button>
                </div>
                <!-- 流程环节列表页 -->
                <!-- sign：使用了el-table -->
                <el-table
                    ref="list"
                    :data="nodeData"
                    tooltip-effect="dark"
                    style="width: 100%"
                    @row-click="rowClick"
                    @selection-change="chooseData = $event">
                    <el-table-column type="selection" width="55"></el-table-column>
                    <el-table-column property="nodeName" label="环节" :min-width="minWidth"></el-table-column>
                    <el-table-column prop="handlerWay" :formatter="formatHandlerWay" label="环节处理方式" :min-width="minWidth"></el-table-column>
                    <el-table-column prop="viewName" label="环节视图名称" :min-width="minWidth"></el-table-column>
                    <el-table-column prop="handler" label="处理人" :min-width="minWidth">
                        <template slot-scope="scope">
                            <span v-for="h,index in scope.row.actualHandler" :key="h.wid">
                                <el-checkbox
                                    v-if="h.status === 1 || h.status === 3"
                                    :true-label="h.wid"
                                    @change="setChooseWids($event, scope.row.id, index)">
                                    <span :class="setColor(h.status)">{{h.actualHandlerName}}</span>
                                </el-checkbox>
                                <span v-else :class="setColor(h.status)">{{h.actualHandlerName}}</span>
                            </span>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            <div
                slot="footer"
                class="col-12 d-flex justify-content-center justify-content-sm-between flex-wrap"
                style="padding-left: 0;padding-right: 0;">
                <div class>
                    <p class="card-category">
                        <span>显示第 {{from + 1}} 到第 {{to}} 条记录，总共 {{total}} 条记录</span>
                    </p>
                </div>

                <l-pagination
                    class="pagination-no-border"
                    v-model="pagination.pageNo"
                    :per-page="pagination.perPageSize"
                    :total="pagination.total"
                    @input="loadNodeListJSON"
                ></l-pagination>
            </div>
        </el-dialog>

        <!-- 点击跳转后的环节列表弹出框 -->
        <el-dialog width="380px" title="环节选择" :visible.sync="nodeChooseVisible" class="scorpion">
            <el-table
                ref="nodeList"
                :data="nodeSimpleData"
                tooltip-effect="dark"
                style="width: 100%"
                @row-click="rowNodeClick"
                @selection-change="nodeChooseData = $event">
                <el-table-column type="selection" width="55"></el-table-column>
                <el-table-column property="nodeName" label="环节"></el-table-column>
            </el-table>
            <label style="font-size: 18px; margin-left: 8px; margin-top: 4px;">操作意见：</label>
            <el-input placeholder="操作意见" type="textarea" v-model="jumpRemark"></el-input>
            <span slot="footer" class="dialog-footer">
                <el-button @click="nodeChooseVisible = false">取 消</el-button>
                <el-button type="primary" @click="nodeSubmitClick">提 交</el-button>
            </span>
        </el-dialog>

        <!-- 流程跟踪 -->
        <el-dialog width="950px" title="流程跟踪" :visible.sync="traceDialogVisible" class="scorpion">
            <flowtrace-list ref="traceList" :boId="config.boId" :usePage="true"></flowtrace-list>
        </el-dialog>
    </div>
</template>
<script>
import LPagination from "@/components/frame/Pagination"
import { Dialog, Select, Option, Tag, Checkbox } from "element-ui"
import FlowTraceList from "./FlowTraceList"
import GlobalConst from '@/service/global-const'

export default {
    components: {
        LPagination,
        [Dialog.name]: Dialog,
        [Select.name]: Select,
        [Option.name]: Option,
        [Tag.name]: Tag,
        [Checkbox.name]: Checkbox,
        [FlowTraceList.name]: FlowTraceList
    },
    pageInfo: {
        type: Boolean,
        default: true
    },
    data() {
        return {
            /* 面板参数 */
            config: {},
            nodeListURL: `${this.BASEURL}/instance/node/nodeinstancelist/listJSON.do`,
            nodeData: [] /* 监控页面列表数据 */,
            /* 最外层流程监控列表页的分页 */
            pagination: {
                perPageSize: 6,
                pageNo: 1,
                perPageOptions: [10, 25, 50, 100],
                total: 0
            },
            // input 框输入的搜索数据
            queryValue: "",
            /*
             * 用户选择的数据，最终结果就是包含该对象的所有 value 值的数组
             */
            chooseData: [],
            dialogVisible: true, //对话框显示控制
            nodeChooseVisible: false /* 跳转时，节点选择弹出框的控制 */,
            traceDialogVisible: false /* 流程跟踪意见的弹出框 */,
            nodeChooseData: [] /* 跳转时，节点选择数据 */,
            nodeSimpleData: [] /* 跳转时，节点列表数据 */,
            jumpRemark: null /* 流程跳转意见 */,
            chooseWids: {
                length: 0
            }
        }
    },
    computed: {
        to () {
            let highBound = this.from + this.pagination.perPageSize
            if (this.total < highBound) {
                highBound = this.total
            }
            return highBound
        },
        from () {
            return this.pagination.perPageSize * (this.pagination.pageNo - 1)
        },
        total () {
            return this.pagination.total
        },
        // 获取表格列最小宽度
        minWidth () {
            return GlobalConst.table.minWidth
        }
    },
    methods: {
        /* 加载流程监控列表数据方法 */
        loadNodeListJSON () {
            let params = {
                fiId: this.config.fiId,
                pageNo: this.pagination.pageNo,
                perPageSize: this.pagination.perPageSize
            }
            this.post(this.nodeListURL, params).then(res => {
                this.nodeData = res.Rows
                this.pagination.total = res.Total
                /*
                 * 如果获取到的数据为空，并且当前页面不是第一页时，加载上一页的数据
                 * 删除当前页所有记录的时候会出现这个情况
                 */
                if (this.nodeData.length === 0 && this.pagination.pageNo > 1) {
                    this.pagination.pageNo--
                    this.loadNodeListJSON()
                }
            })
        },
        /* 转办 */
        transfer () {
            if (this.chooseWids.length <= 0) {
                this.alert("请先勾选需要转办的处理人!")
                return
            }
            let wids = []
            for (let k in this.chooseWids) {
                for (let kk in this.chooseWids[k]) {
                    wids.push(this.chooseWids[k][kk])
                }
            }
            let _this = this
            this.addressBook({
                type: "2",
                selectType: 20,
                singleChoose: true
            }).then(data => {
                _this
                    .$confirm(
                        "您确定要将选定的处理人转办至【" +
                            data.map(d => d.name).join(",") +
                            "】吗？",
                        "转办",
                        {
                            confirmButtonText: "确定",
                            cancelButtonText: "取消",
                            type: "warning"
                        }
                    )
                    .then(() => {
                        _this
                            .$prompt("", "转办意见填写", {
                                confirmButtonText: "确定",
                                cancelButtonText: "取消",
                                inputPlaceholder: "转办意见不可为空!",
                                inputValidator: value => {
                                    if (value === null || value.trim() === "") {
                                        return "请输入转办意见"
                                    }
                                }
                            })
                            .then(result => {
                                let params = {
                                    fiId: _this.config.fiId,
                                    wids: wids.join(","),
                                    target: data.map(d => d.value).join(","),
                                    remark: "转办意见：" + result.value.trim()
                                }
                                _this.post( `${_this.BASEURL}/instance/flow/flowinstancemonitor/transfer.do`, params)
                                    .then(res => {
                                        if (res.hasOk) {
                                            console.info(_this)
                                            _this.loadNodeListJSON()
                                            _this.chooseWids = Object.assign(
                                                {},
                                                { length: 0 }
                                            );
                                            _this.alert("操作成功", "success")
                                        } else {
                                            _this.alert("操作失败", "error")
                                        }
                                    })
                            })
                    })
                    .catch(() => {})
            })
        },
        /* 跳转 */
        jump () {
            let params = {
                fiId: this.config.fiId,
                usePage: false,
                sortName: "code",
                sortOrder: "asc"
            };
            this.post(
                `${this.BASEURL}/instance/node/nodeinstanceselectlist/listJSON.do`,
                params
            ).then(res => {
                this.nodeSimpleData = res.Rows
                this.nodeChooseVisible = true
            })
        },
        /* 跳转选择环节后点击提交 */
        nodeSubmitClick () {
            if (this.nodeChooseData.length !== 1) {
                this.alert("请选择一条!")
                return
            }
            if (!this.jumpRemark) {
                this.alert("请填写操作意见!")
                return
            }
            let params = {
                fiId: this.config.fiId,
                targetNodeId: this.nodeChooseData[0].id,
                remark: this.jumpRemark
            };
            this.post(
                `${this.BASEURL}/instance/flow/flowinstancemonitor/jump.do`,
                params
            ).then(res => {
                if (res.hasOk) {
                    this.loadNodeListJSON()
                    this.alert("操作成功", "success")
                    this.nodeChooseVisible = false
                    this.jumpRemark = null
                } else {
                    this.alert("操作失败", "error")
                }
            })
        },
        /* 重处理 */
        rehandle () {
            this.$confirm("您确定要将当前流程文档实例重处理吗?", "重处理", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    this.$prompt("", "重处理意见填写", {
                        confirmButtonText: "确定",
                        cancelButtonText: "取消",
                        inputPlaceholder: "重处理意见不可为空!",
                        inputValidator: value => {
                            if (value === null || value.trim() === "") {
                                return "请输入重处理意见"
                            }
                        }
                    }).then(result => {
                        let params = {
                            remark: "重处理意见：" + result.value.trim(),
                            fiId: this.config.fiId
                        }
                        this.post(
                            `${this.BASEURL}/instance/flow/flowinstancemonitor/rehandle.do`,
                            params
                        ).then(res => {
                            if (res.hasOk) {
                                this.loadNodeListJSON();
                                this.alert("操作成功", "success")
                            } else {
                                this.alert("操作失败", "error")
                            }
                        })
                    })
                })
                .catch(() => {})
        },
        /* 流程跟踪 */
        traceOpinion () {
            this.traceDialogVisible = true
        },
        /* 点击行设置，环节列表页的 */
        rowClick (row) {
            this.$refs.list.toggleRowSelection(row)
        },
        /* 点击行设置，点击跳转后弹出的环节选项的 */
        rowNodeClick (row) {
            this.$refs.nodeList.toggleRowSelection(row)
        },
        /* 处理方式数据格式化 */
        formatHandlerWay (row) {
            switch (row.handlerWay) {
                case 1:
                    return "串行审核"
                case 2:
                    return "并行审核"
                case 3:
                    return "会签审核"
                default:
                    return ""
            }
        },
        /* 设置复选框的值 */
        setChooseWids (e, rowId, index) {
            /* 以环节id为key，分辨每一行的复选框 */
            /* 每一行的复选框，以索引值为key，值为待办wid */
            if (!this.chooseWids[rowId]) {
                this.chooseWids[rowId] = {}
                this.chooseWids[rowId][index] = e
                this.chooseWids.length++
            } else {
                if (!this.chooseWids[rowId][index]) {
                    this.chooseWids[rowId][index] = e
                    this.chooseWids.length++
                } else if (e === false) {
                    delete this.chooseWids[rowId][index]
                    this.chooseWids.length--
                }
            }
        },
        /* 设置颜色 */
        setColor (status) {
            switch (status) {
                case 1:
                    return "orange"
                case 2:
                    return "green"
                case 3:
                    return "blue"
                case 4:
                    return "gray"
                default:
                    return ""
            }
        }
    },
    created () {
        this.loadNodeListJSON()
    }
};
</script>
<style scoped>
.orange {
    color: orange;
}
.green {
    color: green;
}
.blue {
    color: blue;
}
.gray {
    color: #938884;
}
.dialog-customize-content {
    padding: 0 25px 25px 25px;
    display: flex;
    width: 100%;
}

.tree-block {
    width: 248px;
    display: inline-block;
    border: 1px solid #efecec;
    height: 348px;
    border-radius: 4px;
    overflow: scroll;
    border-top: none;
}

.tree-block p {
    width: 100%;
    height: 35px;
    font-size: 16px;
    text-align: center;
    background-color: #409eff;
    color: white;
    border: 1px solid #efecec;
    margin-bottom: 5px;
    padding-top: 4px;
}

.dialog-transfer {
    display: inline-block;
    margin-left: 10px;
    position: relative;
}

.address-book-dialog >>> .el-dialog__footer {
    text-align: center;
}

.address-book-dialog >>> .el-collapse-item__header {
    height: 30px;
    line-height: 30px;
    padding-left: 8px;
    font-size: 15px;
}

.address-book-dialog >>> .el-collapse-item__content {
    padding-bottom: 0;
}

.address-book-dialog >>> .el-transfer__buttons {
    padding: 0 10px;
}

.address-book-dialog >>> .el-transfer-panel {
    width: 230px;
}

.address-book-dialog >>> .el-dialog__body {
    padding: 0;
}
</style>