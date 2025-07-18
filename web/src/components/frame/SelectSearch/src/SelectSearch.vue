<template>
    <div class="select-search">
        <el-input :size="size" :placeholder="placeholder" v-model="sameSearchWord" @input="searchEvent" autocomplete="on">
            <el-select v-model="searchName" slot="prepend" placeholder="请选择" @change="getSelectValue($event)">
                <el-option v-for="(i, index) in searchTypeList" :key="index" :label="i.name" :value="i.id"></el-option>
            </el-select>
            <el-button slot="append" @click="searchKeywords()">
                <bd-icon name="#bdb-sousuo"></bd-icon>
            </el-button>
        </el-input>
    </div>
</template>
<script>
export default {
    name: 'SelectSearch',
    props: {
        size: String,
        placeholder: String,
        // 搜索内容
        searchWord: '',
        // 搜索类型
        searchType: {
            type: String,
            default: '项目'
        }
    },
    computed: {

    },
    data () {
        return {
            // 搜索内容
            sameSearchWord: '',
            // 搜索类型
            searchName: '项目', // 默认选中项目
            searchTypeList: [
                {name: '项目', id: 0},
                {name: '政策', id: 1},
            ]
        }
    },
    methods: {
        searchKeywords () {
            this.pushPage({
                path: '/smartsearch',
                query: {
                    data: this.searchWord,
                    value: this.searchName
                },
                title: '智能检索'
            })
        },
        searchEvent () {
            this.$emit('search', this.sameSearchWord)
        },
        getSelectValue (id) {
            this.searchName = this.searchTypeList[id].name
            this.$emit('change', this.searchName)
        }
    },
    watch: {
        searchWord (val) {
            this.sameSearchWord = val
        },
        searchType (val) {
            this.searchName = val
        }
    }
}
</script>

<style lang="scss" scoped>
.bd-icon {
    width: 1em;
    height: 1em;
    vertical-align: -0.15em;
    fill: currentColor;
    overflow: hidden;
}
/deep/ .el-input-group__prepend div.el-select .el-input__inner {
    width: 75px;
    border-color: transparent;
    background-color: transparent;
    color: inherit;
    border-top: 0;
    border-bottom: 0;
}
/deep/ .el-input-group__append {
    border-left: 0 !important;
    border-top-left-radius: 0 !important;
    border-bottom-left-radius: 0 !important;
}
/deep/ .el-input-group__append, .el-input-group__prepend {
    background-color: #fff;
    color: #909399;
    vertical-align: middle;
    display: table-cell;
    position: relative;
    border: 1px solid $borderColor;
    border-radius: 4px;
    padding: 0 20px;
    width: 1px;
    white-space: nowrap;
}
/deep/ .el-input-group--append .el-input__inner {
    border-right: 0;
    border-top-right-radius: 0;
    border-bottom-right-radius: 0;
}
/deep/ .el-input__inner {
    border-left: 0;
}
/deep/ .el-input__inner:focus {
    border-color: $borderColor;
}
/deep/ .el-scrollbar__wrap {
    overflow: hidden;
    height: 100%;
}
/deep/ .el-input__icon:before {
    vertical-align: unset;
}
</style>
