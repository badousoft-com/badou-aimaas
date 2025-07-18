<template>
    <div class="row">
        <div class="col-md-12">
            <card>
                <div v-if="title" slot="header">
                    <h4 class="card-title">{{title}}</h4>
                </div>
                <div class="table-responsive">
                    <div class="attach-btn">
                        <button  class='btn btn-primary' @click="chooseFile">
                            <input ref="fileInput" style="display: none" type="file" multiple="multiple" @change="change"></input>
                            <i class="icon iconfont bd-upload"></i>
                            选择
                        </button>
                        <button  class='btn btn-primary' @click="deleteAttach">
                            <i class="icon iconfont bd-trash-alt-o"></i>
                            删除
                        </button>
                    </div>
                    <!-- sign：使用了el-table -->
                    <el-table 
                        class="table-striped"
                        ref="attachTable"
                        :data="attachData"
                        @selection-change="selection = $event"
                        @row-click="rowClick">
                        <el-table-column type="selection" width="55"></el-table-column>
                        <el-table-column type="index" width="60"></el-table-column>
                        <el-table-column label="附件名称" property="attachName" :min-width="minWidth">
                            <template slot-scope="scope">
                                <span class="download-btn" @click="downloadAttach(scope.row.attachId)">{{scope.row.attachName}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="上传时间" property="createTimeDesc" :min-width="minWidth"></el-table-column>
                        <el-table-column label="大小" property="attachSize" :formatter="sizeFormatter" :min-width="minWidth"></el-table-column>
                    </el-table>
                </div>
            </card>
        </div>
    </div>
</template>
<script>
import {Table, TableColumn, Button} from 'element-ui'
import GlobalConst from '@/service/global-const'

const SIZES = ['B', 'kb', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
/**
 * 把字节数转换为合适的单位
 * @param {number} value
 * @return {string}
 */
const formatterByte = value => {
    if (value === 0) return '0 B'
    let k = 1024
    let i = Math.floor(Math.log(value) / Math.log(k))
    return (value / Math.pow(k, i)).toFixed(2) + ' ' + SIZES[i]
}

/**
 * 验证文件大小是否符合最大的限制
 * @param {number} size 文件大小
 * @param {number} maxSize 最大允许的大小
 * @return {boolean} true 符合，false 不符合
 */
const checkSize = (size, maxSize) => !maxSize || maxSize >= size

/**
 * 验证文件名长度是否符合小于最大的长度限制
 * @param {string} fileName  文件名
 * @param {number} maxLength 文件名最大长度
 * @return {boolean} true 符合，false 不符合
 */
const checkFileName = (fileName, maxLength) => !maxLength || maxLength >= fileName.length

/**
 * 获取文件后缀，并转换为小写
 * @param {string} fileName 文件名
 * @return {string} 后缀，全小写
 */
const getSuffix = fileName => {
    let index = fileName.lastIndexOf('.')
    if (index == -1) return ''
    return fileName.substring(index + 1).toLowerCase()
}

/**
 * 验证文件后缀是否存在于白名单/黑名单中
 * @param {string} fileSuffix  文件后缀
 * @param {array} list   后缀的白名单/黑名单
 * @return {boolean}  true 存在，false 不存在
 */
const checkType = (fileSuffix, list) => {
    for (let key in list) {
        if (list[key].toLowerCase() === fileSuffix) {
            return true
        }
    }
    return false
}

/**
 * 组合上面 check* 的验证方法
 * @param files 需要上传的文件数组
 * @param config 上传限制的配置
 * @return {string|null} 错误信息，返回值为 null 时表示验证通过
 */
const checkFile = (files, config) => {
    for (let i = 0; i < files.length; i++) {
        let f = files[i]
        let fileName = f.name
        if (!checkSize(f.size, config.maxSize)) {
            return `文件 ${fileName} 过大，文件大小最大值为${formatterByte(config.maxSize)}`
        }

        if (!checkFileName(fileName, config.maxFileNameLengh)) {
            return `文件名 ${fileName} 过长，文件名最长为${config.maxFileNameLengh}个字符`
        }

        let suffix = getSuffix(fileName)
        if (config.whiteList && config.whiteList.length > 0) {
            if (!checkType(suffix, config.whiteList)) {
                return `不允许上传 ${fileName} 这类型的文件`
            }
        } else if (config.blackList && config.blackList.length > 0) {
            if (checkType(suffix, config.blackList)) {
                return `不允许上传 ${fileName} 这类型的文件`
            }
        }
    }
    return null
}

export default {
    name: 'attach-list',
    components: {
        [Table.name]: Table,
        [TableColumn.name]: TableColumn,
        [Button.name]: Button,
    },
    computed: {
        // 获取表格列最小宽度
        minWidth () {
            return GlobalConst.table.minWidth
        }
    },
    props: {
        value: [String, Array],
        resourceId: '',
        title: '',
        config: {
            type: Object,
            default() {
                return {
                    maxSize: null,          // 最大可上传的文件大小，字节数
                    maxFileNameLengh: null, // 文件名最大长度
                    whiteList: [],          // 文件类型白名单，允许上传的文件文件类型，填写文件的后缀，需要包含. 如 .jpg
                    // 文件类型黑名单，不允许上传的文件类型，填写文件后缀，需要包含. 如 .jpg。白名单的优先级比黑名单高，就是说
                    // 如果填写了白名单，那么黑名单不再生效，只允许上传白名单中的文件类型
                    blackList: [],

                }
            }
        }
    },
    data() {
        return {
            attachData: [],
            selection: [],
            files: {},
        }
    },
    methods: {
        chooseFile() {
            this.$refs.fileInput.click()
        },
        sizeFormatter(row, column, cellValue) {
            return formatterByte(cellValue)
        },
        change() {
            let files = this.$refs.fileInput.files
            let errMsg = checkFile(files, this.config)
            if (errMsg) {
                this.alert(errMsg)
                this.$refs.fileInput.value = ''
                return
            }
            Object.keys(files).forEach(key => {
                let tmpId = this.getTmpId()
                this.files[tmpId] = files[key]
                this.attachData.unshift({
                    attachId: tmpId,
                    attachName: files[key].name,
                    createTimeDesc: '-',
                    attachSize: files[key].size,
                    tmpRecord: true, // 临时记录
                })
            })
            this.newValue()
            this.$refs.fileInput.value = ''
        },
        getTmpId() {
            let randNum = () => (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1)
            return `${randNum()}-${randNum()}-${randNum()}-${randNum()}`
        },
        rowClick(row) {
            this.$refs.attachTable.toggleRowSelection(row)
        },
        deleteAttach() {
            let selection = this.selection
            if (selection.length === 0) {
                this.alert('至少选择一行！')
                return
            }

            this.$confirm('你确定要删除所选？', '删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                let set = new Set()
                selection.forEach(e => set.add(e.attachId))

                let attachIds = []
                this.attachData = this.attachData.filter(d => {
                    let deleteAttach = set.has(d.attachId)
                    if (deleteAttach) {
                        // 删除提交的文件
                        if (this.files[d.attachId]) delete this.files[d.attachId]
                        // 已经上传了的文件，记录下id
                        if (!d.tmpRecord) attachIds.push(d.attachId)
                    }
                    return !deleteAttach
                })
                if (attachIds.length > 0) {
                    this.$emit('removeAttach', attachIds)
                }
                this.newValue()
            })
        },
        newValue() {
            let result = Object.keys(this.files).map(key => this.files[key])
            this.$emit('input', result)
        },
        downloadAttach(attachId) {
            // TODO:待处理
        }
    },
    watch: {
        resourceId: {
            immediate: true,
            handler: function () {
                let url = `${this.BASEURL}/attach/action/attachsupport/listJSON.do`
                this.get(url, {resourceId: this.resourceId}).then(res => {
                    let data = res
                    this.attachData = data.Rows
                    // console.log(data.Rows)
                })
            }
        }
    },
}

</script>
<style scoped>

    .attach-btn {
        border-bottom: 1px solid rgba(0, 0, 0, 0.125);
        padding-bottom: 7px;
    }

    .attach-btn > span {
        font-size: 14px;
        color: #409EFF;
        cursor: pointer;
        margin-right: 5px;
    }

    .attach-btn > span:hover {
        color: #66b1ff;
    }

    .icon {
        width: 1.2em;
        height: 1.7em;
    }

    .download-btn {
        cursor: pointer;
        color: #409EFF;
    }

    .download-btn:hover {
        color: #66b1ff;
    }
</style>
