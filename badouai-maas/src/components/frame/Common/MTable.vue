<template>
    <!-- sign：使用了el-table -->
    <el-table
        :data="tableData"
        style="width: 100%">
        <el-table-column
            v-if="isSelection"
            type="selection"
            :width="selectionWidth">
        </el-table-column>
        <el-table-column
            v-if="hasIndex"
            type="index"
            :label="fieldDataIndex.label"
            :width="fieldDataIndex.width || indexWidth">
        </el-table-column>
        <template v-for="i in fieldDataMain">
            <el-table-column
                :prop="i.name"
                :label="i.label"
                :width="i.width"
                :min-width="minWidth">
            </el-table-column>
        </template>
    </el-table>
    
</template>

<script>
import GlobalConst from '@/service/global-const'
export default {
    components: {},
    props: {
        // 是否为可选表格
        isSelection: {
            type: Boolean,
            default: false
        },
        // 表格数据
        tableData: {
            type: Array,
            default: []
        },
        // 表格字段数据
        fieldData: {
            type: Array,
            default: []
        }
    },
    computed: {
        // 获取表格列最小宽度
        minWidth () {
            return GlobalConst.table.minWidth
        }
    },
    data () {
        return {
            indexWidth: 60,
            selectionWidth: 55, 
            fieldDataIndex: {},
            hasIndex: false,
            fieldDataMain: []
        }
    },
    methods: {

    },
    mounted () {
        if (this.fieldData.findIndex(i => i.name === 'index') > -1) {
            this.hasIndex = true
            this.fieldDataIndex = this.fieldData.find(i => i.name === 'index')
            this.fieldDataMain = this.fieldData.filter(i => i.name !== 'index')
        } else {
            this.hasIndex = false
            this.fieldDataMain = this.fieldData
        }
    }
}
</script>

<style lang="scss" scoped>

</style>
