<!--
 * @Description: 用户选择块弹窗
-->
<template>
    <div class="select-block">
        <bd-dialog
            :visible.sync="_visible"
            title="选择添加块"
            :handlerList="btnList">
            <el-table :data="data">
                <el-table-column type="index" width="50" label="#"></el-table-column>
                <el-table-column
                    v-for="(i, i_index) in tableColumn"
                    :key="i_index"
                    v-bind="i">
                    <template slot-scope="scope">
                        <!-- 预览图 -->
                        <template v-if="i.prop === 'previewImg' && hasValue(scope.row[i.prop])">
                            <preview
                                ref="preview"
                                :value="[{url: previewUrl + scope.row[i.prop]}]">
                            </preview>
                        </template>
                        <!-- 操作按钮 -->
                        <template v-else-if="i.prop === 'setting'">
                            <bd-button
                                v-if="!selectIndexs[scope.$index]"
                                v-bind="settingBtn"
                                @click="handleSelect(scope.$index, true)">
                            </bd-button>
                            <bd-button
                                v-else
                                name="取消"
                                type="danger"
                                icon="wrong"
                                v-bind="settingBtn"
                                @click="handleSelect(scope.$index, false)">
                            </bd-button>
                        </template>
                        <template v-else>
                            {{hasValue(scope.row[i.prop]) ? scope.row[i.prop] : defaultCellValue}}
                        </template>
                    </template>
                </el-table-column>
            </el-table>
        </bd-dialog>
    </div>
</template>

<script>
import Dialog from '@/components/frame/Dialog/index.vue'
import GlobalConst from '@/service/global-const'
import { Has_Value } from '@/utils'
import { Down_File_Url } from '@/api/frame/panel/user'
import Preview from '@/components/frame/Common/MForm/components/items/File/ImagePicker/Preview'
import { Deep_Clone } from '@/utils/clone'
export default {
    name: 'select-block-dialog',
    components: {
        Preview,
        [Dialog.name]: Dialog,
    },
    props: {
        data: {
            type: Array,
            default: () => []
        },
        // 弹窗出现控制参数
        visible: {
            type: Boolean,
            default: false
        },
    },
    computed: {
        _visible: {
            get () {
                return this.visible
            },
            set (val) {
                this.$emit('update:visible', val)
            }
        },
    },
    data () {
        let that = this
        return {
            // 按钮
            btnList: [
                {
                    id: 'submit', name: '确定', icon: 'save', loading: false, type: 'primary',
                    click: function (btnObj) {
                        that.submit(btnObj)
                    }
                },
            ],
            // 默认cell值
            defaultCellValue: GlobalConst.table.value,
            hasValue: Has_Value, // TODO:没看到有使用。待删除
            // 表格列信息
            tableColumn: [
                { prop: 'previewImg', label: '预览图' },
                { prop: 'name', label: '名称' },
                { prop: 'setting', label: '操作' }
            ],
            // 图片预览地址
            previewUrl: Down_File_Url,
            // 操作按钮
            settingBtn: { name: '添加', type: 'success', icon: 'add', size: 'mini', loading: false },
            selectIndexs: []
        }
    },
    methods: {
        exeMethod (btn, params) {
            if (typeof btn.click === 'function') {
                btn.click(params, btn)
            } else if (typeof this[btn.click] === 'function') {
                this[btn.click](params, btn)
            }
        },
        // 点击底部的确定按钮
        submit () {
            let result = this.data.filter((o, o_index) => {
                return this.selectIndexs[o_index]
            })
            this.$emit('submit', Deep_Clone(result))
        },
        handleSelect (index, value) {
            this.$set(this.settingBtn, 'loading', true)
            setTimeout(() => {
                this.$set(this.selectIndexs, index, value)
                this.$set(this.settingBtn, 'loading', false)
            }, 300)
        }
    },
    watch: {
        _visible (newVal) {
            if (newVal) {
                this.selectIndexs = []
            }
        }
    }
}
</script>

<style lang="scss" scoped>

</style>