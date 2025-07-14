<!--
 * @Description: 多内容编辑表格
-->
<template>
    <div class="contents-edit">
        <el-form ref="tableForm" :model="formData" label-width="0">
            <el-table
                class="contents-table"
                :data="formData.data"
                ref="table"
                @cell-click="cellClick"
                :row-class-name="tableRowClassName"
                v-on="$listeners">
                <el-table-column type="selection" width="55"></el-table-column>
                <el-table-column
                    v-for="(i, i_index) in _tableColumn"
                    :key="i_index"
                    v-bind="i">
                    <template slot="header">
                        <span :class="{'require': i.require}">
                            {{i.label}}
                            <template v-if="i.describe">
                                <el-tooltip class="item" effect="dark">
                                    <div slot="content" v-html="i.describe"></div>
                                    <bd-icon name="help-fill" class="font-12"></bd-icon>
                                </el-tooltip>
                            </template>
                        </span>
                    </template>
                    <template slot-scope="scope">
                        <el-form-item
                            :prop="`data.${scope.$index}.${i.prop}`"
                            :rules="i.rules || []">
                            <el-input
                                v-if="i.type === 'input'"
                                size="mini"
                                v-model="scope.row[i.prop]"
                                :placeholder="i.placeholder"
                                @blur="i.blur ? i.blur(scope.$index, scope.row) : () => {}">
                            </el-input>
                            <!-- 是否隐藏，点击事件是为了使切换switch时当前行不被选中 -->
                            <div v-else-if="i.type === 'switch'" @click.stop="() => {}">
                                <bd-switch v-model="scope.row[i.prop]" :activeValue="1" :inactiveValue="0"></bd-switch>
                            </div>
                            <template v-else>{{scope.row[i.prop]}}</template>
                        </el-form-item>
                    </template>
                </el-table-column>
            </el-table>
        </el-form>
    </div>
</template>

<script>
import Switch from '@/components/frame/Common/MForm/components/items/Switch'
import { Has_Value } from '@/utils'
export default {
    components: {
        [Switch.name]: Switch,
    },
    props: {
        tableData: {
            type: Array,
            require: true
        },
        tableColumn: {
            type: Array,
            default: null
        }
    },
    computed: {
        _tableColumn () {
            if (this.tableColumn) {
                return this.tableColumn
            } else {
                // 多内容的列就那么几个，相同的懒得重新在每个页面重新定义
                return this.contentColumn
            }
        },
        formData: {
            get () {
                return { data: this.tableData }
            },
            set (val) {
                this.$emit('update:tableData', val.data)
            }
        }
    },
    data () {
        let self = this
        let validateInteger = function (rule, value, callback) {
            if (Has_Value(value) && !/^\d+$/.test(value)) {
                callback('必须为整数')
            } else {
                callback()
            }
        }
        return {
            contentColumn: [
                {
                    prop: 'name', label: '内容名称', type: 'text',
                    // rules: [{ required: true, message: '请输入内容名称', trigger: 'blur' }]
                },
                {
                    prop: 'priority', label: '排序', type: 'input', width: 80,
                    rules: [{ validator: validateInteger, trigger: ['blur', 'change'] }]
                },
                {
                    prop: 'chartsGroup', label: '分组类别', width: 120, type: 'input',
                    blur: function (index, row) {
                        let chartsGroup = row.chartsGroup
                        self.formData.data.forEach((o, idx) => {
                            if (String(o.chartsGroup) === String(chartsGroup)) {
                                self.formData.data[idx].groupHeight = row.groupHeight
                            }
                        })
                    }
                },
                {
                    prop: 'groupHeight', label: '组别高度占总比（%）', type: 'input', width: 180,
                    describe: `填写数字，该值为相对于拖拽设置的块高度的百分比；<br/>如果值为0，由剩余高度（100 - 已被自定义的组别高度）均等分配`,
                    blur: function (index, row) {
                        let chartsGroup = row.chartsGroup
                        self.formData.data.forEach((o, idx) => {
                            if (String(o.chartsGroup) === String(chartsGroup)) {
                                self.formData.data[idx].groupHeight = row.groupHeight
                            }
                        })
                    }
                },
                {
                    prop: 'contentRatio', label: '占行比例', type: 'input', placeholder: '默认1/3', width: 120,
                    describe: `所在内容占据整行的比例，希望输入分数，最大为1；<br/>当没有值或值不合法（非数字）时，取1/3<br/>
                                规则：对相同组别的内容非终止行尽量填满行，如需换行，可通过不同组别进行设置`,
                },
                { prop: 'isHide', label: '是否隐藏', type: 'switch', width: 80 },
            ]
        }
    },
    methods: {
        // 表格td点击事件
        cellClick (row, column, cell, event) {
            this.$refs.table.toggleRowSelection(row)
        },
        // 行className，用于设置所以，以便删除编辑的时候获取
        tableRowClassName (row) {
            // 设置row对象的index
            row.row.index = row.rowIndex
        },
        // 检验表单
        checkForm () {
            return new Promise((resolve, reject) => {
                // validate 这个方法需要注意下，不同的form组件封装的方法名可能不一样
                this.$refs.tableForm.validate(valid => {
                    if (valid) {
                        // 表单的绑定值即为表格的绑定值
                        resolve(this.tableData)
                    } else {
                        reject()
                    }
                }).catch(() => {
                    reject()
                })
            })
        },
        // 获取选中的行
        getSelection () {
            return this.$refs.table.selection || []
        }
    }
}
</script>

<style lang="scss" scoped>
.contents-edit {
    .require {
        position: relative;
        &::before {
            content: '*';
            position: absolute;
            left: -10px;
            color: $danger;
        }
    }
    /deep/.el-table {
        // th, td {
        //     border-right: 1px solid #EBEEF5;
        // }
        td, .cell, .cell-text {
            width: 100%;
            min-height: 26px;
            padding-bottom: 5px;
            .el-form-item {
                margin-bottom: 0;
                .el-form-item__content {
                    width: 100% !important;
                    &>div >div {
                        line-height: 0px;
                    }
                    .el-select {
                        width: 100%;
                    }
                    .count-input {
                        .el-input__suffix {
                            display: none;
                        }
                        .el-input__inner {
                            padding-right: 5px;
                        }
                    }
                }
                .el-form-item__error {
                    top: 31px;
                    transform: scale(0.8);
                }
            }
        }
        .cell {
            overflow: visible;
        }
    }
}
</style>