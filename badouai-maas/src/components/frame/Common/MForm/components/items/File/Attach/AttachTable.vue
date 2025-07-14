<template>
    <div class="attach-table">
        <template v-if="!isView">
            <div
                v-for='(i, index) in handlerList' 
                :key="index"
                class="d-ib handler">
                <div class="p-r">
                    <label 
                        v-if="i.for"
                        :for="labelFor"
                        class="cover-hide pointer">
                    </label>
                    <el-button
                        v-btnBg="i"
                        @click='handleMethod(i)'>
                        <bd-icon :name="i.name"></bd-icon>
                        {{ i.name }}
                    </el-button>
                </div>
            </div>
        </template>
        <!-- 默认插槽 -->
        <slot></slot>
        <m-list
            ref="mList"
            class="marT-10 border"
            :hasIndex="true"
            :isSelection="!isView"
            :showPagination="false"
            :data.sync="tempValue"
            :fieldList="_fieldList"
            noDataShowType="text">
            <template v-slot:status>
                <el-table-column :label="statusField.label" :width="statusField.width">
                    <template slot-scope="scope">
                        <span v-if="scope.row.status!=='success'"  class="fontCS">
                            <bd-icon name="point-fill" size="small"></bd-icon>
                            <span>未上传</span>
                        </span>
                        <span v-else class="successC">
                            <bd-icon name="point-fill" size="small"></bd-icon>
                            <span>已提交</span>
                        </span>
                    </template>
                </el-table-column>
            </template>
            <template v-slot:name>
                <el-table-column :label="nameField.label">
                    <template slot-scope="scope">
                        <div
                            class="primaryC pointer"
                            @click="download(scope.row)">
                            {{scope.row.name}}
                        </div>
                    </template>
                </el-table-column>
            </template>
            <template v-slot:handler>
                <el-table-column :label="handlerField.label" :width="handlerField.width">
                    <template slot-scope="scope">
                        <el-button
                            v-for="(i, index) in itemHandlerList"
                            :key="index"
                            plain
                            :type="i.type"
                            @click.stop='handleItemMethod(i, scope.row, scope.$index)'>
                            <bd-icon :name="i.icon"></bd-icon>
                            {{i.name}}
                        </el-button>
                    </template>
                </el-table-column>
            </template>
        </m-list>
    </div>
</template>
<script>
import MList from '@/components/frame/Common/MList/index'
import { Download_Ser } from '@/service/attach/index.js'
export default {
    components: {
        MList
    },
    props: {
        // 列表数据源
        value: {
            type: Array,
            default: () => []
        },
        // input id值，用于触发该input的file事件
        labelFor: {
            type: String,
        },
        // 是否为查看状态
        isView: {
            type: Boolean,
            default: false
        }
    },
    computed: {
        tempValue: {
            get () {
                return this.value
            },
            set (val) {
                this.$emit('input', val)
            }
        },
        // 获取外层的删除按钮对象数据
        getDelBtn () {
            return this.handlerList.find(i => i.id === 'delete')
        },
        // 状态项字段对象
        statusField () {
            return this.fieldList.find(i => i.name === 'status')
        },
        // 附件名称字段对象
        nameField () {
            return this.fieldList.find(i => i.name === 'name')
        },
        // 操作事件字段对象
        handlerField () {
            return this.fieldList.find(i => i.name === 'handler')
        },
        _fieldList () {
            return this.fieldList.filter(i => !this.isView || i.isView)
        }

    },
    data () { // 定义页面变量
        return {
            // 所有附件的操作事件列表（面向多个附件操作）
            handlerList: [
                { id: 'add', for: this.labelFor, name: '选择文件', type: 'primary', handler: 'add', hidden: false },
                { id: 'delete', name: '删除文件', type: 'danger', handler: 'deleteList', hidden: true }
            ],
            // 单个附件的操作事件列表（面向单个附件操作）
            itemHandlerList: [
                { id: 'download', name: '下载', icon: 'download', type: 'primary', handler: 'download' },
                { id: 'delete', name: '删除', icon: 'delete', type: 'danger', handler: 'delete' },
            ],
            // 表格表头数据数组
            fieldList: [
                { name: 'status', label: '状态', width: 100},
                { name: 'name', label: '附件名称', isView: true },
                { name: 'sizeDesc', label: '文件大小', width: 140, isView: true },
                { name: 'handler', label: '操作', width: 200}
            ],
        }
    },
    methods: { // 定义函数
        /**
         * 下载文件
         * @param {Object} {name, url} 文件对象的name属性与url属性
         */
        download ({name, url}) {
            Download_Ser({name, url})
        },
        /**
         * 面向多个附件的操作事件
         * @param {Object} btnItem 操作按钮对象
         */
        handleMethod (btnItem) {
            // 判断执行函数类型
            switch (btnItem.handler) {
                case 'add':
                    // 使用了label标签的for属性挂元素，触发事件，此处不用再写事件
                    break
                case 'deleteList':
                    // 删除n个附件事件
                    this.handleDelete()
                    break
                default:
                    // do something default
            }
        },
        /**
         * 执行单个附件的操作事件
         * @param {String} handler 执行事件类型名称
         * @param {Object} fileItem 附件对象
         * @param {Number} fileIndex 附件（附件数组中）所在下角标
         */
        handleItemMethod ({handler}, fileItem, fileIndex) {
            switch (handler) {
                case 'download':
                    // 调用父组件下载方法
                    this.$emit('download', fileItem)
                    break
                case 'delete':
                    // 调用父组件删除附件方法
                    this.$emit('handleDelete', fileItem, fileIndex)
                    break
                default:
                    // do something default
            }
        },
        /**
         * 删除多个附件事件
         */
        handleDelete () {
            // 定义列表勾选数据
            let selection = []
            try {
                // 获取勾选数据
                selection = this.$refs.mList.$refs.list.selection
            } catch (e) {
                console.error(`t获取表格选中异常`)
                return false
            }
            // 定义获取勾选数据长度
            let selectionLen = selection.length
            if (selectionLen === 0) {
                this.$message({
                    message: '请选择一条数据',
                    type: 'warning'
                })
            } else {
                this.$confirm(`此操作将删除${selection.length}个文件, 是否继续?`, '提示', {          
                    confirmButtonText: '确定',          
                    cancelButtonText: '取消',          
                    type: 'warning'
                }).then(() => {
                    let attrName = 'name'
                    // 父组件中事件为删除单个附件，这里需要遍历多个附件循环调用父组件方法
                    // 另外，删除数组项可能会导致下角标变更，所以需要逆处理数据
                    for (let i = selectionLen - 1; i >= 0; i-- ) {
                        let currentIndex = this.tempValue.findIndex(j => j[attrName] === selection[i][attrName])
                        this.tempValue.splice(currentIndex, 1)
                    }
                }).catch(() => {
                    // this.$message({
                    //     type: 'info',
                    //     message: '已取消删除'
                    // })         
                })
            }
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
.attach-table {
    .handler {
        margin-right: $space;
        &:last-child {
            margin-right: 0;
        }
        .cover-hide {
            position: absolute;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
            z-index: 1;
        }
    }
}
</style>