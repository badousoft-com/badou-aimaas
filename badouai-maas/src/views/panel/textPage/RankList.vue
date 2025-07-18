<!--
    排行榜
-->
<template>
    <div class="rank-list">
        <el-scrollbar class="list-scrollbar">
            <ul class="list-content" v-loading="loading">
                <li class="rank-row borderB d-f" v-for="(i, i_index) in list" :key="i_index">
                    <div class="row-box textS-C">
                        <span class="rank-num">
                            <template v-if="i_index < icons.length && icons[i_index]">
                                <bd-icon :name="icons[i_index]"></bd-icon>
                            </template>
                            <span v-else>{{rankNum(i_index + 1)}}</span>
                        </span>
                        <div class="rank-info d-f flex-1">
                            <div class="info-left">
                                <span class="left-text" :title="i.name">{{i.name}}</span>
                            </div>
                            <div class="info-right">{{i.num}}</div>
                        </div>
                    </div>
                </li>
            </ul>
        </el-scrollbar>
    </div>
</template>

<script>
export default {
    name: 'rank-list',
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
            ]
        }
    },
    methods: {
        rankNum (i) {
            return i < 10 ? '0' + i : i
        },
        // 点击查看更多
        toGo () {
            this.$emit('toGo')
        },
        search (e) {
            if (!e) {
                this.$emit('reset')
            } else {
                function getTime (t) {
                    let d = new Date(t)
                    return d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate()
                }
                this.$emit('search', [getTime(e[0]), getTime(e[1])], e)
            }
        }
    },
    mounted () {
    }
}
</script>

<style lang="scss" scoped>
.rank-list {
    width: 100%;
    height: 100%;
    padding: 20px;
    padding-bottom: 0;
    .list-scrollbar {
        width: 100%;
        height: 100%;
    }
    // 内容 ------ start
    .list-content {
        width: 100%;
        height: 100%;
        color: #fff;
        display: table;
        border-collapse: collapse;
        .rank-row {
            width: 100%;
            display: table-row;
            .row-box {
                width: 100%;
                height: 100%;
                padding: 10px 0;
                .rank-num {
                    font-size: 20px;
                    padding: 0 10px;
                }
                .rank-info {
                    padding-right: 20px;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    .info-left {
                        flex: 1;
                        width: 10px;
                        overflow: hidden;
                        white-space: nowrap;
                        text-overflow: ellipsis;
                        .left-text {
                            display: inline;
                            word-break: break-all;
                            overflow: hidden;
                            white-space: nowrap;
                            text-overflow: ellipsis;
                        }
                    }
                    .info-right {
                        font-size: 18px;
                        white-space: nowrap;
                        margin-left: 5px;
                    }
                }
            }
        }
        // 查看更多 ---- start
        .show-mush {
            font-size: 13px;
            color: #999999;
            display: table-cell;
            vertical-align: middle;
        }
    }
    // 内容 ------ end
}
.textS-C {
    display: flex;
    align-items: center;
}
.borderB {
    border-bottom: 1px solid #00A1D9;
}
.c-p {
    cursor: pointer;
}
ul, li {
    margin: 0;
    padding: 0;
    list-style: none;
}
</style>