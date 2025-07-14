<template>
    <div class="address-book-dialog">
        <el-dialog
            width="950px"
            :title="config.title"
            :visible.sync="dialogVisible"
            class="scorpion">
            <div>
                <div style="margin-bottom: 20px; float:right; width: 30%;">
                    <el-input placeholder="名称/编码" class="input-with-select" v-model="queryValue">
                        <el-button slot="append" icon="el-icon-search" @click="loadPanelListJSON"></el-button>
                    </el-input>
                </div>
                <!-- sign：使用了el-table -->
                <el-table
                    ref="panelTable"
                    :data="panelData"
                    tooltip-effect="dark"
                    style="width: 100%"
                    @row-click="rowClick"
                    @selection-change="chooseData = $event">
                    <el-table-column type="selection" width="55"></el-table-column>
                    <el-table-column property="name" label="名称" width="120"></el-table-column>
                    <el-table-column prop="code" label="编码" width="120"></el-table-column>
                    <el-table-column
                        prop="isDefult"
                        :formatter="formaterIsDefault"
                        label="当前是否启用"
                        width="120">
                    </el-table-column>
                    <el-table-column label="创建时间" width="120">
                        <template slot-scope="scope">{{ scope.row.createTime }}</template>
                    </el-table-column>
                    <el-table-column prop="creatorName" label="创建者" width="120"></el-table-column>
                    <el-table-column label="更新时间" width="120">
                        <template slot-scope="scope">{{ scope.row.updateTime }}</template>
                    </el-table-column>
                    <el-table-column prop="updatorName" label="更新者" width="120"></el-table-column>
                    <el-table-column prop="remark" label="备注" width="120"></el-table-column>
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
                    @input="loadPanelListJSON"
                ></l-pagination>
            </div>

            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleSubmitClick">提 交</el-button>
            </span>
        </el-dialog>
    </div>
</template>
<script>
import LPagination from "@/components/frame/Pagination"
import { Dialog, Table, TableColumn, Select, Option } from "element-ui"

export default {
    components: {
        LPagination,
        [Dialog.name]: Dialog,
        [Table.name]: Table,
        [TableColumn.name]: TableColumn,
        [Select.name]: Select,
        [Option.name]: Option
    },
    pageInfo: {
        type: Boolean,
        default: true
    },
    data() {
        return {
            /* 面板参数 */
            config: {},
            panelListURL: `${this.BASEURL}/panel/layout/layoutlist/panelListJSON.do`,
            panelData: [],
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
            dialogVisible: true //对话框显示控制
        };
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
    },
    methods: {
        loadPanelListJSON (param) {
            let params = {
                type: this.config.type,
                id: this.config.id,
                pageNo: this.pagination.pageNo,
                perPageSize: this.pagination.perPageSize
            }
            if (this.queryValue) {
                params.panelCode = this.queryValue
            }
            this.post(this.panelListURL, params).then(res => {
                this.panelData = res.Rows
                this.pagination.total = res.Total
                /*
                 * 如果获取到的数据为空，并且当前页面不是第一页时，加载上一页的数据
                 * 删除当前页所有记录的时候会出现这个情况
                 */
                if (this.panelData.length === 0 && this.pagination.pageNo > 1) {
                    this.pagination.pageNo--
                    this.loadPanelListJSON()
                }
            })
        },
        /* 格式化是否当前默认面板 */
        formaterIsDefault (row) {
            if (row.isDefult === 1) {
                return "是"
            }
            return "否"
        },
        /* 点击提交 */
        handleSubmitClick () {
            if (this.chooseData.length !== 1) {
                this.alert("请选择一个面板")
                return
            }
            this.callback(this.chooseData)
            this.dialogVisible = false
        },
        /* 点击表格选中行 */
        rowClick (row) {
            this.$refs.panelTable.toggleRowSelection(row)
        }
    },
    created () {
        this.loadPanelListJSON()
    }
};
</script>
<style scoped>
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
