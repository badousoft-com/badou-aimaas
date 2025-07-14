<!--
 * @Description: 使用el-table 绘制的排行榜
-->
<template>
    <div id="tableRank" class="table-rank">
        <el-table
            :data="list"
            header-row-class-name="table-rank-title"
            row-class-name="table-rank-row">
            <el-table-column
                type="index"
                width="60vw">
                <template slot-scope="scope">
                    <template v-if="scope.$index < icons.length && icons[scope.$index]">
                        <bd-icon class="rank-icon" :name="icons[scope.$index]"></bd-icon>
                    </template>
                    <span class="usual-rank-num" v-else>
                        {{scope.$index + 1}}
                        <span class="num-bg-box">
                            <img class="num-bg" :src="numImg" alt="" srcset="">
                        </span>
                    </span>
                </template>
            </el-table-column>
            <el-table-column
                v-for="(c, c_index) in columns"
                :key="c_index"
                v-bind="c">
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
export default {
    name: 'table-rank',
    props: {
        loading: {
            type: Boolean,
            default: false
        },
        // 展示排行榜前几位
        max: {
            type: Number,
            default: 10
        },
        // 排行榜对应展示图标（如果，某个位置没有值，则默认展示数字）
        // 如['guanjun', , 'jijun']，则排行为2的行展示数字 02
        icons: {
            type: Array,
            default: () => ['No.1', 'No.2', 'No.3']
        },
        // list: {
        //     type: Array,
        //     require: true
        // }
    },
    data () {
        return {
            list: [
                { name: '榜一XXXX', num: '24567' },
                { name: '我是第二名', num: '6798' },
                { name: '第三名在这', num: '6790' },
                { name: 'XXX市XXX区XXX分店', num: '4322' },
                { name: 'XXX市XXX区XXX分店', num: '2323' },
                { name: 'XXX市XXX区XXX分店', num: '1389' },
                { name: 'XXX市XXX区XXX分店', num: '1389' },
                { name: 'XXX市XXX区XXX分店', num: '1256' },
                { name: 'XXX市XXX区XXX分店', num: '1249' },
                { name: 'XXX市XXX区XXX分店', num: '555' },
            ],
            columns: [
                { label: '店铺名称', prop: 'name', align: 'left' },
                { label: ' 年度销售额(元)', prop: 'num' },
            ],
            // tableH: 100,
            numImg: require('@/components/frame/Panel/PanelCode/theme/bluePlane/assets/numBg.png'),
        }
    },
    methods: {
        setTableH () {
            this.$nextTick(() => {
                let boxH = document.getElementById('tableRank')?.clientHeight || 100
                let titleH = document.getElementsByClassName('table-rank-title')?.[0]?.clientHeight || 0
                this.tableH = boxH - titleH
            })
        }
    },
    mounted () {
        // window.onresize = () => {
        //     this.setTableH()
        // }
        // this.setTableH()
    }
}
</script>

<style lang="scss" scoped>
.table-rank::v-deep {
    height: 100%;
    // 排行榜表格总体样式 ----- start
    .el-table {
        background-color: rgba($color: #000000, $alpha: 0);
        border-color: rgba($color: #119DFE, $alpha: 0.2);
        .cell {
            color: #fff;
            font-size: 1.5vh;
        }
        th .cell {
            font-size: 1.6vh;
        }
        tbody tr:hover>td, tbody tr.hover-row>td {
            background-color: rgba($color: #000000, $alpha: 0) !important;
        }
        th.is-leaf, td, tr {
            border-color: rgba($color: #119DFE, $alpha: 0.2) !important;
        }
        &::before {
            background-color: rgba($color: #000000, $alpha: 0);
        }
        .el-table__body-wrapper {
            .is-left, .is-left .cell {
                text-align: left;
                // padding-left: 1vw;
            }
        }
    }
    // 标题
    .table-rank-title, .el-table thead th {
        background-color: rgba($color: #119DFE, $alpha: 0.24);
    }
    // 行
    .table-rank-row {
        background-color: rgba($color: #000000, $alpha: 0);
        &:hover {
            background-color: rgba($color: #000000, $alpha: 0) !important;
        }
    }
    // 排行榜表格总体样式 ----- end

    // 排行数除icon外的样式
    .usual-rank-num {
        display: inline-block;
        width: 100%;
        height: 100%;
        font-size: 0.6vw;
        text-align: center;
        position: relative;
        .num-bg-box {
            width: 100%;
            height: 100%;
            display: flex;
            justify-content: center;
            align-content: center;
            position: absolute;
            left: 0;
            top: 0;
            .num-bg {
                // width: 1.2vw;
                object-fit: cover;
            }
        }
    }
    .rank-icon {
        font-size: 1.2vw;
    }
}
</style>