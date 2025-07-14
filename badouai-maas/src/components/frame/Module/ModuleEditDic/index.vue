<template>
    <div class="bd-module-edit-dic">
        <module-edit-code
            ref="moduleEditDic"
            :mdCode="mdCode"
            :detailId="detailId"
            :defaultParamsObj="defaultParamsObj"
            hideBtn
            @afterModule="afterModule">
        </module-edit-code>
        <div class="bd-module-edit-dic__child" v-if="dicItem_listData">
            <dic-item-table
                ref="dicItemTable"
                :fieldList="_dicItem_fieldList"
                :data="dicItem_listData"
                :item="dicItem">
            </dic-item-table>
        </div>
        <!-- 按钮使用模型状态位：确保模型出现，按钮才有存在价值 -->
        <div
            class="bd-module-edit__btn"
            :class="{'fixed-bottom': fullHeight}"
            v-if="_dicItem_fieldList">
            <bd-button
                class="bd-module-edit__btn__item"
                v-for="(i, index) in defaultBtnList"
                :key="index"
                v-bind="i"
                @click="handleClick(i)">
            </bd-button>
        </div>
    </div>
</template>
<script>
import ModuleEditCode from '@/components/frame/Module/ModuleEditCode'
import Default_Btn_List from './button'
import DicItemTable from './DicItemTable'
import ModuleUtils from '@/js/ModuleUtils'
import { Get_Common_List } from '@/api/frame/common'
import GlobalConst from '@/service/global-const'
export default {
    name: "module-edit-dic",
    inheritAttrs: false,
    components: {
        [ModuleEditCode.name]: ModuleEditCode,
        [DicItemTable.name]: DicItemTable
    },
    props: {
        // ref属性名称
        refName: {
            type: String,
            default: 'edit'
        },
        // 模型编码
        mdCode: {
            type: String
        },
        // 整个页面为表单使用，操作按钮将固定底部进行操作
        // 默认不开启
        fullHeight: {
            type: Boolean,
            default: false
        },
        // 详情数据id，用于获取页面详细数据
        detailId: {
            type: String,
        },
        // 组件传入默认请求参数
        defaultParamsObj: {
            type: Object,
            default: () => {}
        },
    },
    data () { // 定义页面变量
        return {
            // 主模型数据
            dicModule: null,
            // 表单关联数据字典项
            dicItemTab: null,
            // 数据字典明细项模型
            dicItemModule: null,
            // 数据字典明细项-列表数据
            dicItem_listData: null,
            // 数据字典明细项-单条数据
            dicItem: null,
            // 默认按钮数组
            defaultBtnList: Default_Btn_List,
        }
    },
    computed: {
        // 数据字典明细项-表头数据
        _dicItem_fieldList () {
            if (!this.dicItemModule) return
            let {
                fieldList,  // 字段数组数据
                dic,  // 数据字典集合对象
            } = this.dicItemModule
            fieldList.forEach(i => {
                // 判断字段是否含有dic属性，若有则对应在dic集合中找到对象数据字典，并更新字段对象的options值
                if (i.dic && dic && dic[i.dic]) {
                    this.$set(i, 'options', dic[i.dic] || [])
                }
            })
            return fieldList
        }
    },
    methods: { // 定义函数
        /**
         * 按钮事件触发
         * @param {Object} btnObj 按钮对象
         */
        handleClick (btnObj) {
            // 获取事件
            let { click: handleClick } = btnObj
            if (handleClick && typeof handleClick === 'function') {
                // 执行函数，传递作用域
                handleClick.call(this, btnObj)
            }
        },
        /**
         * 获取模型数据后的操作事件
         * @param {Object} module
         */
        async afterModule (module) {
            // 更新主模型数据
            this.dicModule = module
            // 获取关联模型-数据字典项模型
            this.dicItemTab = module?.childTab?.[0]
            if (!this.dicItemTab) return
            let {
                module: dicItem_mdCode, // 模型编码
                relationEntityField,  // 关联字段
            } = this.dicItemTab
            // 获取数据字典明细项模型
            this.dicItemModule = await ModuleUtils.editModuleCfg(dicItem_mdCode)
            let {
                fieldList: dicItem_fieldList, // 获取数据字典明细项-字段数据
                dic: dicItem_dic, // 数据字典集合
            } = this.dicItemModule
            // 获取数据字典明细项列表
            this.getDicItemList(dicItem_mdCode, relationEntityField, dicItem_fieldList)
            // 设置数据字典明细项-单项数据模版（新增字典项时使用该模版）
            this.dicItem = this.updateDicItem(dicItem_fieldList, dicItem_dic)
        },
        /**
         * 获取数据字典明细项列表
         * @param {String} mdCode 模型编码
         * @param {String} relationEntityField 关联字段
         * @param {Array} fieldList 字段列表数组
         */
        getDicItemList (mdCode, relationEntityField, fieldList) {
            // 定义请求参数
            let params = {
                defaultSearchParam: JSON.stringify([{
                    name: relationEntityField,
                    value: this.detailId,
                    type: GlobalConst.searchBar.type
                }]),
                mdCode,
                usePage: false
            }
            // 请求数据
            Get_Common_List(params, 'post').then(res => {
                // 获取接口的数据字典项列表数据
                let _rows = res?.Rows || []
                // 定义返回的结果数组
                let resultList = []
                // 根据接口返回的字典项列表数据，与指定字段数组fieldList，返回新的数组
                // （核心就是去除数组接口中每条数据下无用的属性字段）
                _rows.forEach(i => {
                    let _item = {}
                    fieldList.forEach(j => {
                        _item[j.name] = i[j.name]
                    })
                    resultList.push(_item)
                })
                // 设置数据字典项列表数据，页面使用
                this.dicItem_listData = resultList
            })
        },
        /**
         * 设置数据项子项对象模版，到时用于添加项时使用
         * @param {Array} fieldList: 字段数组列表
         * @param {Object} dicObj: 数据字典集合对象
         */
        updateDicItem (fieldList, dicObj) {
            // 定义返回结果对象
            let result = {}
            fieldList.forEach(i => {
                // 情况1：字段使用数据字典作为数据源
                if (i.dic &&
                    dicObj[i.dic] &&
                    dicObj[i.dic].length > 0) {
                    // 默认使用数据字典数组第一项作为默认值
                    result[i.name] = dicObj[i.dic][0]?.id
                // 情况2：字段为文本
                } else {
                    result[i.name] = ''
                }
            })
            return result
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {},
}
</script>
<style lang='scss' scoped>
.bd-module-edit-dic::v-deep {
    background: #fff;
    padding-bottom: $padding / 2;
    margin-bottom: $footerHeight;
    .bd-module-edit-dic__child {
        margin: $space;
        border-radius: $borderRadius;
    }
    .bd-module-edit__btn {
        height: $footerHeight;
        line-height: $footerHeight - 2px;
        width: 100%;
        background: #fff;
        text-align: center;
        border-top: 1px solid $lineColor;
        &.fixed-bottom {
            border-top: none;
            position: absolute;
            bottom: 0;
            left: 0;
            z-index: 2;
            box-shadow: 0px 9px 15px 3px rgba(102,102,102,0.5);
        }
    }
}
</style>