<!--
 * @Author: your name
 * @Date: 2021-01-28 11:03:24
 * @LastEditTime: 2021-02-04 11:02:02
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: \badoufFrameWork-front-elementUI\src\components\frame\Report\index.vue
-->
<template>
    <div class="report-box" v-loading="loading">
        <report-tabs
            :data="tabsList"
            v-model="activeTab"
            :useTab="reportData.length > 1">
            <template v-for="(i, i_index) in tabsList" v-slot:[i_index.toString()]>
                <report-list
                    v-if="reportData[i_index].tableColumn && reportData[i_index].tableColumn.length"
                    :key="i_index"
                    :data="reportData[i_index]"
                    :isSelection="listJsObj.showSelection"
                    :listJsObj="listJsObj"
                    @current-change="handleCurrentChange"
                    @size-change="handleSizeChange">
                </report-list>
            </template>
        </report-tabs>
    </div>
</template>

<script>
import ReportTabs from './ReportTabs'
import ReportList from './ReportList'
import { GetReportDetailByCode } from '@/api/frame/report/index'
import GlobalConst from '@/service/global-const'
import { SetReportInfo } from '@/components/frame/Report/transData'
export default {
    name: 'report-view',
    components: {
        ReportTabs,
        ReportList,
    },
    props: {
        // 报表code
        reportCode: {
            type: String,
            required: true
        },
        // 请求dynamicParams参数
        dynamicParams: {
            type: String,
            default: ''
        },
        // 是否需要分页
        isPagination: {
            type: Boolean,
            default: true
        },
    },
    data () {
        return {
            tabsList: [],
            activeTab: 'tab0',
            // 列最小宽度
            minColWidth: 120,
            reportData: [{
                // 列
                tableColumn: [],
                // 数据
                tableData: [],
                // 序列信息{field: '', start_row: 0}
                indexColInfo: {},
                // 表格样式信息（每行为一个对象，对象中的key对应单元格，其中value为对象）
                classInfoList: [],
                // 表格样式信息（每行为一个对象，对象中的key对应单元格，其中value为对象）
                stylesList: [],
                // 单元格所包含的信息（每行为一个对象，对象中的key对应单元格，其中value为对象）
                elementInfoList: [],
                // 单元格所合并信息（每行为一个对象，对象中的key对应单元格，其中value为对象）
                spanInfoList: [],
                // 行属性列表（主要是高度）
                rowInfoList: [],
                // 冻结行列
                freezeInfo: {},
                // 分页信息
                pagination: {
                    perPageSize: GlobalConst.pagination.size,
                    pageNo: 1,
                    perPageOptions: GlobalConst.pagination.sizes,
                    total: 0
                },
            }],
            // 请求回来的数据
            dataSource: {
                sheetArr: []
            },
            // 当前tab索引
            sheetIndex: 0,
            loading: false,
            // js文件里的对象
            listJsObj: {},
        }
    },
    methods: {
        /**
         * @description: 设置对应sheetIndex下的tableData数据
         * @param {Number} n：需要设置tableData的最大长度
         * @param {*} sheetIndex
         */        
        findList (n = 1, sheetIndex = this.sheetIndex) {
            let len = this.reportData[sheetIndex]?.tableData?.length || 0
            for (let i = len; i < n; i++) {
                this.$set(this.reportData[sheetIndex].tableData, i, {})
            }
        },
        /**
         * @description: 
         * @param {Object} data_table：请求回来的表格对象
         * @param {*} sheetIndex：data_table 的索引
         * @param {*} sheetIndex_change：当前需要更改的sheetIndex（有时候，只需要更改其中一个sheet数据）
         */        
        async getReportInfo (data_table, sheetIndex, sheetIndex_change) {
            let _this = this
            // sheetIndex_change === -1 所有的sheet数据都需要重新组装
            // sheetIndex_change === sheetIndex 只需要更改索引为sheetIndex_change的sheet
            if (sheetIndex_change === -1 || sheetIndex_change === sheetIndex) {
                let info = {}
                // 组装数据的主要成分是elementArr，若其不存在，则没有必要继续
                if (!data_table.elementArr || !data_table.elementArr.length) {
                    this.findList(20)
                } else {
                    // 获取表格渲染所需要的数据
                    info = await SetReportInfo(data_table)
                }
                _this.reportData[sheetIndex] = {
                    ..._this.reportData[sheetIndex],
                    tableColumn: [],
                    tableData: [],
                    indexColInfo: {},
                    ...info
                }
                // 需要当前tab的视图
                if(sheetIndex === this.sheetIndex) {
                    this.$forceUpdate()
                }
            }
        },
        /**
         * @description: 将数据放到表格里
         * @param {Number} sheetIndex_change：需要更新数据的sheetIndex，为-1时，则全部更新
         */
        async setDataToTable (sheetIndex_change) {
            let _this = this
            let data_source = this.dataSource
            await data_source.sheetArr.forEach((data_table, sheetIndex)=>{
                this.getReportInfo(data_table, sheetIndex, sheetIndex_change)
            })
            Promise.all(_this.reportData).then(res => {
                // 渲染后事件（js文件加载）
                _this.listJsObj.afterRender && _this.listJsObj.afterRender.call(_this, _this.reportData[_this.sheetIndex])
            })
        },
        /**
         * @description: 获取或重置当前tab的报表分页信息
         * @param {Number} sheetIndex
         * @param {Boolean} isReset：是否重置分页信息
         * @return {Object}：分页信息
         */        
        getORresetPaginationInfo (sheetIndex = this.sheetIndex, isReset) {
            let result = {}
            // 判断是否需要分页，如果不需要的话，不传分页参数
            if (this.isPagination) {
                result = {
                    pageNo: 1,
                    perPageSize: GlobalConst.pagination.size
                }
                // 如果当前sheet存在分页信息，优先使用
                if (this.reportData[sheetIndex] && this.reportData[sheetIndex].pagination) {
                    if (isReset) { // 是否需要重置分页信息
                        this.reportData[sheetIndex].pagination.perPageSize = GlobalConst.pagination.size
                        this.reportData[sheetIndex].pagination.pageNo = 1
                    }
                    result = {
                        pageNo: this.reportData[sheetIndex].pagination.pageNo,
                        perPageSize: this.reportData[sheetIndex].pagination.perPageSize
                    }
                }
            }
            return result
        },
        // 请求报表数据
        async initReportDetail (isReset, sheetIndex_change = -1) {
            let _this = this
            this.loading = true
            let params = {
                reportCode: this.reportCode,
                dynamicParams: this.dynamicParams,
                ...this.getORresetPaginationInfo(this.sheetIndex, isReset)
            }
            // 正在请求中的按钮为不可点击状态
            _this.$parent.btnLoading = true
            await GetReportDetailByCode(params).then((res) => {
                if (!!res.hasOk) {
                    _this.dataSource = res.bean
                    res.bean.sheetArr.forEach((item, index)=>{
                        // sheetIndex_change === -1 所有的sheet数据都需要重新组装
                        // sheetIndex_change === sheetIndex 只需要更改索引为sheetIndex_change的sheet
                        if (sheetIndex_change === -1 || index === sheetIndex_change) {
                            // 设置sheet默认值
                            this.reportData[index] = this.reportData[index] || {tableColumn: [], tableData: [], indexColInfo: {} }
                            if (!this.reportData[index].pagination) {
                                this.reportData[index].pagination = {
                                    perPageSize: GlobalConst.pagination.size,
                                    pageNo: 1,
                                    perPageOptions: GlobalConst.pagination.sizes,
                                    total: 0
                                }
                            }
                            this.$set(this.tabsList, index, { text: item.sheetInfo.name, id: 'tab' + index })
                            let total = this.dataSource.sheetArr.length ? item.sheetInfo.Total : 0
                            this.$set(this.reportData[index].pagination, 'total', total)
                        }
                    })
                    // 请求返回值所携带的js与组装sheet数据
                    _this.initJsORdata(res.bean.reportInfo.reportJS, sheetIndex_change)
                } else {
                    _this.alert(res.message || '获取数据失败')
                }
                _this.loading = false
                _this.$parent.btnLoading = false
                // _this.closeLoading()
            }).catch((err) => {
                _this.loading = false
                _this.$parent.btnLoading = false
                _this.$message({
                    message: err.message || '请求失败',
                    type: 'warning'
                })
            })
        },
        // 组装js对象数据与表格数据
        async initJsORdata (reportJS, sheetIndex_change) {
            this.listJsObj = this.defaultJs || {}
            // reportJS = '/company_income_detail_approve.report.js'
            if (reportJS) {
                // this.listJsObj = await this.loadScript(reportJS)
                // 兼容IE 将后台报表js移动到前端
                let temp_js = {}
                try {
                    temp_js = await require(`@/../public/reportJs${reportJS}`).default
                } catch (error) {
                    console.error(`public/reportJs${reportJS}错误：`, error)
                }
                this.listJsObj = { ...this.defaultJs, ...temp_js }
                // 将js传入按钮与默认按钮相结合
                if (this.listJsObj.buttons) {
                    let defaultBtns = this.$parent.btnList.filter(btn => {
                        return this.listJsObj.buttons.findIndex(item => item.id === btn.id) === -1
                    })
                    this.$parent.btnList = [...defaultBtns, ...this.listJsObj.buttons]
                }
                // js设置右上角文字
                if (this.listJsObj.description) {
                    this.$parent.description = this.listJsObj.description
                }
                // js设置导出文件名
                if (this.listJsObj.exportFileName) {
                    this.$parent.fileName = this.listJsObj.exportFileName
                }
            }
            // 组装数据
            this.setDataToTable(sheetIndex_change)
        },
        // ------------------------------------------------- 以下是分页 -----------------------------------------------------
        // 每页显示数更改事件
        handleSizeChange (val) {
            this.reportData[this.sheetIndex].pagination.pageNo = 1  // 每页显示数更改时，跳转回第一页（请求一次接口）
            this.reportData[this.sheetIndex].pagination.perPageSize = val
            this.initReportDetail(false, this.sheetIndex)
        },
        // 当前页码更改事件
        handleCurrentChange (val) {
            this.reportData[this.sheetIndex].pagination.pageNo = val
            this.initReportDetail(false, this.sheetIndex)
        },
    },
    mounted () {
    },
    watch: {
        // dynamicParams 请求数据参数更改
        dynamicParams: {
            deep: true,
            immediate: false,
            handler: function (newVal, oldVal) {
                // 经过请求搜索框数据后，再不济dynamicParams也会传'{}'，可使用其来判断是否已加载了模型信息
                !!newVal && this.initReportDetail(true)
            }
        },
        // 当前活动tab
        activeTab: {
            deep: true,
            immediate: true,
            handler: function (newVal, oldVal) {
                // 根据当前活动tab组装sheetIndex
                let value = newVal.replace('tab', '')
                this.sheetIndex = Number(value)
            }
        }
    }
}
</script>

<style lang="scss" scoped>
.report-box {
    min-height: 300px;
}
</style>