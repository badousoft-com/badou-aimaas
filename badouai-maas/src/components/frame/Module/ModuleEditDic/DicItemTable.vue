<template>
    <div class="dic-item-table">
        <bd-button v-if="item" @click="addItem" type="primary" icon="add" class="marginB">
            新增子项
        </bd-button>
        <el-form
            ref="form"
            :model="dicForm"
            :rules="dicFormRules"
            class="dic-item-form has-border">
            <el-table
                ref="list"
                :data="data">
                <template slot="header" slot-scope="scope">
                    <span>
                        <span v-if="i.required" class="dangerC">*</span>
                        {{scope.column.label}}
                    </span>
                </template>
                <template v-for="(i, index) in _fieldList">
                    <el-table-column
                        :key="index"
                        :prop="i.name"
                        :label="i.label">
                        <template slot-scope="scope">
                            <el-form-item
                                :prop="i.name + scope.$index">
                                <bd-select 
                                    v-if="i.type==='select'"
                                    :placeholder="i.label"
                                    :name="i.name"
                                    v-model="data[scope.$index][i.name]"
                                    :options="i.options">
                                </bd-select>
                                <bd-text
                                    v-else
                                    :placeholder="i.label"
                                    :name="i.name"
                                    v-model="data[scope.$index][i.name]">
                                </bd-text>
                            </el-form-item>
                        </template>
                    </el-table-column>
                </template>
                <el-table-column :label="operateField.label">
                    <template slot-scope="scope">
                        <bd-button icon="add" type="primary" @click="addItem(scope.$index)"></bd-button>
                        <bd-button icon="delete" type="danger" @click="delItem(scope)"></bd-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-form>
    </div>
</template>
<script>
import MList from '@/components/frame/Common/MList/index.vue'
import { Text, Select } from '@/components/frame/Common/MForm/components/items/index'
import { Deep_Clone } from '@/utils/clone'
export default {
    name: "dic-item-table",
    components: {
        MList,
        [Text.name]: Text,
        [Select.name]: Select
    },
    props: {
        // 表头字段数据
        fieldList: {
            type: Array,
            default: () => []
        },
        // 列表数据
        data: {
            type: Array,
            default: () => []
        },
        // 数据项对象
        item: {
            type: Object
        }
    },
    data () { // 定义页面变量
        return {
            // 操作列字段对象
            operateField: { label: '操作', name: 'operate' }
        }
    },
    computed: {
        // 表头字段数组
        _fieldList () {
            return this.fieldList.filter(i => i.type !== 'hidden').map(i => {
                // 追加字段label
                i.label = i.display
                return i
            })
        },
        // _allFieldList () {
        //     if (!this._fieldList) return
        //     let result = JSON.parse(JSON.stringify(this._fieldList))
        //     result.push(this.operateField)
        //     return result
        // },

        // 数据字典表单对象
        dicForm () {
            // 定义返回结果对象
            let resultForm = {}
            // 表格中同一列的字段都是编辑区，需要进行校验，因此在name上不能使用一致的名称，这里将使用
            // 字段名 + 下角标 构造出不同的字段以便用于表单检验；同步html上面的使用也以此类推
            this._fieldList.forEach((i, i_index) => {
                this.data.forEach((j, j_index) => {
                    resultForm[i.name + j_index] = j[i.name]
                })
            })
            // 返回基于表格生成的完整表单对象
            return resultForm
        },
        // 数据字典表单校验规则
        // 同上： 同步更新【字段名 + 下角标】组成的字段的校验规则
        // 目前只对必填校验，有风险，后续再看吧，总体需求待进一步确认
        dicFormRules () {
            let resultRules = {}
            this._fieldList.forEach((i, i_index) => {
                this.data.forEach((j, j_index) => {
                    if (i.required) {
                            resultRules[i.name + j_index] = [
                            // trigger中需要添加change，当点击el-input快捷清空按钮时数据会清除，此时不会触发blur失焦，所以需要通过change来触发
                            { required: true, message: `请输入${i.label}`, trigger: ['blur', 'change'] }
                        ]
                    }
                })
            })
            return resultRules
        }
    },
    methods: { // 定义函数
        // 数据字典项-列表-表单校验
        validateForm () {
            return new Promise((resolve, reject) => {
                // 检验表单
                this.$refs.form.validate((valid) => {
                    if (valid) {
                        // 这里留意：
                        //     为使表格字段能校验，之前已经将表格字段组装成表单字段用于检验
                        //     当校验成功时，这个时候返回的是列表数据，接口需要的是列表数组数据
                        resolve(this.data)
                    } else {
                        resolve()
                    }
                })
            })
            
        },
        /**
         * 返回唯一标识
         */
        getKey (index) {
            return `${new Date().getTime()}${index}`
        },
        // 添加项
        addItem (index) {
            // 定义数据插入的下角标
            let _index = 0
            // 是否清除校验结果
            let isClearVerify = false
            // 获取当前下角标，设置新数据插入的下角标位置
            if (typeof index === 'number') {
                _index = index + 1
                isClearVerify = true
            } else {
                // 若当前没有传入下角标，则获取数据长度作为下角标，表示在数据最后面添加数据
                _index = this.data.length
            }
            let addData = {
                ...this.item,
                // 添加状态位，方便删除时判断，如果存在该属性则直接珊不用询问，接口来的列表数据删除才询问
                ready: true
            }
            // 在this.data数组数据中插入一条数据
            this.data.splice(_index, 0, Deep_Clone(addData))
            if (isClearVerify) {
                // 如果不是在最后一条添加数据时会存在问题
                //     插在中间的新数据，添加数据的同时所有字段校验会被同时触发，因此需要清除所有校验
                // 方法不是最佳
                this.$nextTick(() => this.$refs.form.clearValidate())
            }
        },
        /**
         * 删除项
         * @params {Object} {row, $index} 
         *              row: 删除项对象数据
         *              $index: 删除项所在下角标
         */
        delItem ({row, $index}) {
            // row.ready为true则标识该数据数据非接口，是当前新增的，可以直接删，不需要询问
            if (row.ready) {
                this.data.splice($index, 1)
                return
            }
            // 获取数据字典项的名称name
            let { name } = row
            this.$confirm(`此操作将删除【${name}】, 是否继续?`, '提示', {          
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                // 执行删除事件
                this.data.splice($index, 1)
            }).catch(() => {})
            
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
.dic-item-form::v-deep {
    &.has-border {
        border: 1px solid $lineColor;
        border-radius: $borderRadius;
        overflow: hidden;
    }
    .el-form-item {
        margin-bottom: 0px;
        .el-form-item__error {
            top: $inputHeight - $font;
        }
        .el-select {
            // 解决文本框与输入框水平线不对齐问题
            vertical-align: bottom;
        }
    }
}
</style>