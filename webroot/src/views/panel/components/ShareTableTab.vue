<!--
 * @Description: 分享table组件
 * 特点：多个tab共用一个tableColumn，搜索等（除了tab名与数据源不一致外，，其他的相同）
 * 目前（2021/07/21）还没有分页
-->
<template>
    <div class="select-block">
        <bd-dialog
            :visible.sync="_visible"
            append-to-body
            :title="title"
            :handlerList="btnList">
            <bd-tabs
                class="h-per-100"
                v-model="_tabActive"
                :data="tempTabsData">
                <template
                    v-for="t in tempTabsData"
                    v-slot:[t.id]>
                    <div :key="t.id">
                        <bd-search
                            v-if="t.filterList && t.filterList.length"
                            class="padT-10 marL-10"
                            :data="t.filterList"
                            @search="searchFun"
                            @reset="resetFun">
                        </bd-search>
                        <el-table :data="t.data">
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
                                            v-if="!selectIndexs[t.id][scope.$index]"
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
                    </div>
                </template>
            </bd-tabs>
        </bd-dialog>
    </div>
</template>

<script>
import Dialog from '@/components/frame/Dialog/index.vue'
import GlobalConst from '@/service/global-const'
import { Has_Value } from '@/utils'
import { Down_File_Url } from '@/api/frame/panel/user'
import Preview from '@/components/frame/Common/MForm/components/items/File/ImagePicker/Preview'
import BdTabs from '@/components/frame/Common/BdTabs'
import { Deep_Clone } from '@/utils/clone'
import BdSearch from '@/components/frame/Common/MSearch/index'
export default {
    name: 'share-table-dialog',
    components: {
        Preview,
        [Dialog.name]: Dialog,
        [BdTabs.name]: BdTabs,
        [BdSearch.name]: BdSearch
    },
    props: {
        tabsData: {
            type: Array,
            default: () => []
        },
        tableColumn: {
            type: Array,
            default: () => {
                return [
                    { prop: 'previewImg', label: '预览图' },
                    { prop: 'name', label: '名称' },
                    { prop: 'setting', label: '操作' }
                ]
            }
        },
        // 弹窗出现控制参数
        visible: {
            type: Boolean,
            default: false
        },
        // res返回路径
        optionResPath: {
            type: String,
            default: ''
        },
        tabActive: {
            type: String,
            default: ''
        },
        title: {
            type: String,
            default: '添加'
        }
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
        _tabActive: {
            get () {
                return this.tabActive
            },
            set (val) {
                this.$emit('update:tabActive', val)
            }
        }
    },
    data () {
        let that = this
        return {
            // tabsData: [
            //     { text: '用户管理', id: 'first', request: '', params: {  } },
            //     { text: '配置管理', id: 'second', request: '', params: {  } },
            // ],
            tempTabsData: [],
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
            hasValue: Has_Value,
            // 图片预览地址
            previewUrl: Down_File_Url,
            // 操作按钮
            settingBtn: { name: '添加', type: 'success', icon: 'add', size: 'mini', loading: false },
            selectIndexs: {},
            filterList: [
                { id: 'name', name: '名称', value: '', type: 1, isSelect: true },
            ],
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
        submit (btn) {
            let result = []
            let that = this
            this.tempTabsData.forEach(item => {
                result = result.concat(item.data.filter((o, o_index) => {
                    return that.selectIndexs[item.id][o_index]
                }))
            })
            this.$emit('submit', Deep_Clone(result), btn)
        },
        handleSelect (index, value) {
            this.$set(this.settingBtn, 'loading', true)
            setTimeout(() => {
                let activeTabSelect = this.selectIndexs[this.tabActive]
                activeTabSelect[index] = value
                this.$set(this.selectIndexs, this.tabActive, activeTabSelect)
                this.$set(this.settingBtn, 'loading', false)
            }, 100)
        },
        // 请求列表数据
        loadData (fn, params) {
            if (typeof fn !== 'function') {
                console.log('tabsData 中的 request 需要传入一个函数')
                return
            }
            return new Promise((resolve, reject) => {
                fn(params).then(res => {
                    resolve(this.getDataByPath(res, this.optionResPath) || [])
                }).catch(err => {
                    reject([])
                })
            })
        },
        // 请求获取数据，设置数据
        async setTabData (tabObj, index, params = {}) {
            let defaultParmas = tabObj.params || {}
            let tempParams = Object.assign(params, defaultParmas)
            tabObj.data = await this.loadData(tabObj.request, tempParams)
            this.$set(this.tempTabsData, index, tabObj)
        },
        // 数据初始化
        init () {
            // let tempTabsData = Deep_Clone(this.tabsData) || []
            // this.tempTabsData = tempTabsData.map(tabObj => {
            //     return Object.assign(tabObj, {
            //         filterList: tabObj.filterList || Deep_Clone(this.filterList),
            //         data: []
            //     })
            // })
            this.tempTabsData = Deep_Clone(this.tabsData) || []
            this.tempTabsData.forEach((tabObj, index) => {
                this.setTabData(tabObj, index)
            })
        },
        /**
         * 返回对象下指定路径下的数据，若path为'A.B'，则返回res[A][B]
         * @param {*} res  输入数据
         * @param {String} path：数据下路径
         * @return {*} 返回指定路径下数据
         */
        getDataByPath (res, path) {
            // 两者中若存在一个无值的，则直接返回初始数据
            if (!(res && path)) {
                return res
            }
            // 若res不为对象（Object或者Array），则path存在没意义，直接返回
            if (typeof res !== 'object') {
                return res
            }
            // 存储初始数据，在后续步骤中出错时方便直接返回
            let _res = JSON.parse(JSON.stringify(res))
            try {
                // 获取数据所在路径数组
                let pathList = path.split('.')
                // 按照指定路径获取所选数据
                pathList.forEach(i => {
                    res = res[i]
                })
            } catch (e) {
                console.error(`当前操作数据为${JSON.stringify(res)}, 提供的路径为${path},请检查路径是否异常`)
                return _res
            }
            return res
        },
        // 搜索
        searchFun () {
            let activeIndex = this.tempTabsData.findIndex(o => o.id === this.tabActive)
            if (~activeIndex) {
                let params = {}
                this.tempTabsData[activeIndex].filterList.forEach(({ id, value }) => {
                    if (Has_Value(value)) {
                        params[id] = value
                    }
                })
                this.setTabData(this.tempTabsData[activeIndex], activeIndex, params)
            }
        },
        // 重置
        resetFun () {
            let activeIndex = this.tempTabsData.findIndex(o => o.id === this.tabActive)
            if (~activeIndex) {
                this.tempTabsData[activeIndex].filterList.forEach(i => i.value = null)
                this.setTabData(this.tempTabsData[activeIndex], activeIndex, {})
            }
        },
    },
    mounted () {
        this.init()
    },
    watch: {
        _visible (newVal) {
            if (newVal) {
                this.tabsData.forEach(item => {
                    this.selectIndexs[item.id] = []
                })
            }
        }
    }
}
</script>

<style lang="scss" scoped>

</style>