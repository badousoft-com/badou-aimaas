<template>
	<div>
	    <div>
            <!-- sign：使用了el-table -->
	        <el-table
	        :data="traceData"
	        tooltip-effect="dark"
	        style="width: 100%">
	            <el-table-column
	              property="nodeName"
	              label="环节"
                  :min-width="minWidth">
	            </el-table-column>
	            <el-table-column
	              label="到达时间"
                  :min-width="minWidth">
	              <template slot-scope="scope">{{ scope.row.fmtCreateTime }}</template>
	            </el-table-column>
	            <el-table-column
	              label="处理时间"
                  :min-width="minWidth">
	              <template slot-scope="scope">{{ scope.row.fmtFinishTime }}</template>
	            </el-table-column>
	            <el-table-column
	              prop="creatorName"
	              label="处理人"
                  :min-width="minWidth">
	            </el-table-column>
	            <el-table-column
	              prop="opinion"
	              label="处理意见"
                  :min-width="minWidth">
	            </el-table-column>
	        </el-table>
	    </div>
	    <div slot="footer" v-if="usePage" class="col-12 d-flex justify-content-center justify-content-sm-between flex-wrap"
	         style="padding-left: 0;padding-right: 0;">
	        <div class="">
	            <p class="card-category">
	                <span>显示第 {{traceFrom + 1}} 到第 {{traceTo}} 条记录，总共 {{traceTotal}} 条记录</span>
	            </p>
	        </div>


	        <l-pagination class="pagination-no-border"
	                      v-model="tracePagination.pageNo"
	                      :per-page="tracePagination.perPageSize"
	                      :total="tracePagination.total"
	                      @input="loadTraceListJSON">
	        </l-pagination>
	    </div>
    </div>
</template>

<script>
    import LPagination from '@/components/frame/Pagination'
    import {Table, TableColumn} from 'element-ui'
    import GlobalConst from '@/service/global-const'

	export default {
		name: 'flowtrace-list',
        components: {
            LPagination,
            [Table.name]: Table,
            [TableColumn.name]: TableColumn,
        },
        props: {
        	boId: String,
        	usePage: Boolean
        },
        data() {
            return {
                traceData: [],  /* 流程跟踪列表页数据 */
                /* 流程跟踪的分页 */
                tracePagination: {
                    perPageSize: 6,
                    pageNo: 1,
                    perPageOptions: [10, 25, 50, 100],
                    total: 0
                }
            }
        },
        methods: {
            loadTraceListJSON() {
                let params = {
                    boid: this.boId,
                    usePage: this.usePage,
                    pageNo: this.tracePagination.pageNo,
                    perPageSize: this.tracePagination.perPageSize
                }
                this.post(`${this.BASEURL}/trace/proccesstracelist/listJSON.do`, params).then(res => {
                    this.traceData = res.Rows
                    this.tracePagination.total = res.Total
                    /*
                     * 如果获取到的数据为空，并且当前页面不是第一页时，加载上一页的数据
                     * 删除当前页所有记录的时候会出现这个情况
                     */
                    if (this.traceData.length === 0 && this.tracePagination.pageNo > 1) {
                        this.tracePagination.pageNo--
                        this.loadTraceListJSON()
                    }
                })
            }
        },
        created() {
        	this.loadTraceListJSON()
        },
        computed: {
            traceTo () {
                let highBound = this.traceFrom + this.tracePagination.perPageSize
                if (this.traceTotal < highBound) {
                    highBound = this.traceTotal
                }
                return highBound
            },
            traceFrom () {
                return this.tracePagination.perPageSize * (this.tracePagination.pageNo - 1)
            },
            traceTotal () {
                return this.tracePagination.total
            },
            // 获取表格列最小宽度
            minWidth () {
                return GlobalConst.table.minWidth
            }
        }
	}
</script>