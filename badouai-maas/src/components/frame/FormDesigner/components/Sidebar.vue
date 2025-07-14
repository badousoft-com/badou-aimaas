<template>
    <div class="fd-sidebar">
        <el-collapse
            v-model="activeNames"
            @change="categoryFoldChange"
            class="fd-sidebar-main">
            <el-collapse-item
                v-for="(i, i_index) in _showList"
                :key="i_index"
                class="fd-sidebar-type"
                :title="i[categoryName]"
                :name="i.optionIndex">
                <el-row 
                    :gutter="5"
                    class="fd-sidebar-type__part">
                    <!-- 设置el-col标签属性span值跟随每一项，区别与以往
                    添加之后会会修改到原有组件样式（在点击或者拖拽时），因此需要在样式模块中固定好组件宽度，固定50% !important -->
                    <draggable
                        :value="i.list"
                        :group="{ name: 'description', pull: 'clone', put: false}"
                        :clone='clone'
                        :sort="false"
                        @start="(event) => dragStart(i_index, event)"
                        @end="dragEnd">
                        <el-col
                            class="fd-sidebar__item onlyComponent el-col-item"
                            :class="{
                                'isWaitOpen': j.waitOpen
                            }"
                            :span="getColumnPer(j)"
                            v-for="(j, j_index) in i.list"
                            :key="j_index"
                            @click.native="appendItem(j.optionIndex)">
                            <div class="waitOpenTip" v-if="j.waitOpen">待启</div>
                            <div class="click-show-item">
                                <!-- <bd-icon :name="j.icon"></bd-icon> -->
                                <bd-icon :name="j.icon || 'attach'" class="click-show-item__icon"></bd-icon>
                                {{j.label}}
                                <!-- 提示 -->
                                <el-tooltip 
                                    popper-class="use-tip-popper"
                                    effect="dark"
                                    trigger="hover"
                                    :content="j.tip" 
                                    placement="top-start"
                                    :offset="7">
                                    <bd-icon v-if="j.tip" name="tip-fill" class="tip-icon fontCS"></bd-icon>
                                </el-tooltip>
                            </div> 
                            <!-- 拖拽时植入编辑区域的dom标签 -->
                            <div 
                                class="drag-show-item">
                                <slot
                                    name="drag-show-item"
                                    :itemObj="j">
                                </slot>
                            </div>
                        </el-col>
                    </draggable>
                </el-row>
            </el-collapse-item>
        </el-collapse>
        <slot name="fold-show">
        </slot>
    </div>
</template>
<script>
import draggable from 'vuedraggable'
import { List_Filter_Group_ByAttr } from '@/utils/list'
import GlobalConst from '@/service/global-const'
import { Get_Form_ColumnNum } from '@/service/deal-form'
export default {
    name: 'fd-sidebar',
    components: {
        draggable
    },
    props: {
        data: {
            type: Array,
            default: () => []
        },
        // 单表单项-占比份数
        formColumnNum: {
            type: [String, Number]
        },
        // 分组的键名
        categoryName: {
            type: String
        }
    },
    data () { // 定义页面变量
        return {
            // 当前正在操作的下角标
            runDragIndex: [0, 0],
            // 活跃展开的组件分类项
            activeNames: []
        }
    },
    computed: {
        // 组装基于页面展示结构的数据，根据组名转化为二维数据进行展示
        _showList () {
            let _data = this.data.map((i, index) => ({
                ...i, // 原有对象数据项
                ...(new i.attrClass().list.reduce((item, j) => {
                    item[j.name] = j.value
                    return item
                }, {})),
                editRun: false, // 是否正在编辑的状态
                optionIndex: index, // 当前组件项在一维数组下所在下角标
                
            }))
            // 获取所有分类的optionIndex属性，用于等下设置展开的活跃项分类
            let result = List_Filter_Group_ByAttr(_data, this.categoryName, '', {
                groupFieldKeys: ['optionIndex']
            })
            // 默认只取前两种分类进行打开，其他的呈现为折叠状态  
            this.activeNames = result.map(i => i.optionIndex).slice(0, 2)
            return result
        }
    },
    methods: { // 定义函数
        /**
         * 分类折叠的change事件
         * @param {Array} val 当前已打开的类别
         */
        categoryFoldChange (val) {
            // do something
        },
        /**
         * 获取当前项所占比份数
         * @param {Number, String} 单项占比份数
         */
        getColumnPer (item) {
            let { columnPer, isOneLine } = item
            // 优先级：字段占比数 > 表单设置占比数 > 全局表单设置占比数

            // 判断字段自身是否有占比数，优先使用其
            if (isOneLine) return GlobalConst.form.gridNum
            if (columnPer) return parseInt(columnPer)
            // 判断当前表单是否设置列总数，按列总数计算占比份数
            if (this.formColumnNum) return Get_Form_ColumnNum(this.formColumnNum)
            // 按全局表单列表，计算占比份数
            return Get_Form_ColumnNum(GlobalConst.form.columnNum)
        },
        /**
         * 获取操作项在一维数据中的下角标----根据【组别下角标】与【组别中排序号】
         */
        getOptionIndex (categoryIndex, itemIndex) {
            // 二维数组中获取
            return this._showList[categoryIndex].list[itemIndex].optionIndex
        },
        // 拖拽开始事件
        // 初始时编辑面板为空，从组件拖动过去时会触发编辑面板的放置事件，最后需要重新回到当前组件来调用该拖拽组件，这里需要个标识才能找到
        // 结合appendItemByDragIndex方法完成添加事件
        dragStart (categoryIndex, evt) {
            // 更新当前正在拖拽的下角标
            this.runDragIndex = [categoryIndex, evt.oldIndex]
        },
        // 拖拽结束事件(松手状态)
        // 1. 拖拽不一定有效，需要确保拖拽到的区域为编辑区
        dragEnd (evt) {
            if (evt.to.className &&
                evt.to.className.includes('drag-group')) {
                // 只有有内容的表单区域才会触发进入这里
                this.appendItemByDragIndex(evt.newIndex)
            }
        },
        // 通过拖拽组件下角标完成数据添加操作
        appendItemByDragIndex (insertIndex) {
            let _itemIndex = this.getOptionIndex.apply(this, this.runDragIndex)
            this.appendItem(_itemIndex, insertIndex)
        },
        appendItem (optionItemIndex, insertIndex) {
            // 调用父级添加项方法
            //     optionItemIndex: 左侧操作的项的下角标
            //     insertIndex：即将插入中间内容区的位置下角标
            this.$emit('appendItem', optionItemIndex, insertIndex)
        },
        clone (item) {
        },
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
@import '../theme/index.scss';
// 标题模块高
$titleHeight: 30px;
.fd-sidebar::v-deep {
    .fd-sidebar-main {
        position: relative;
        border: none;
        .fd-sidebar-type {
            .el-collapse-item__header {
                height: $titleHeight;
                line-height: $titleHeight;
                color: $fd-sidebar-color;
                background: unset;
                font-size: $fontS;
                font-weight: bold;
            }
            .el-collapse-item__wrap {
                background-color: unset;
                .el-collapse-item__content {
                    padding-bottom: 8px;
                    .fd-sidebar-type__part {
                        .fd-sidebar__item {
                            position: relative;
                            width: 50% !important;
                            .waitOpenTip {
                                display: none;
                                background: $danger;
                                color: #fff;
                                font-size: $fontS;
                                line-height: $fontS;
                                padding: 1px;
                                position: absolute;
                                right: 2px;
                                top: 0px;
                            }
                            .click-show-item {
                                border: 1px dashed rgba(0, 0, 0, 0);
                                font-size: $fontS;
                                padding: $padding - 2px;
                                margin-bottom: $padding / 2;
                                cursor: move;
                                white-space: nowrap;
                                background: $fd-sidebar-part;
                                color: $fd-sidebar-part-color;
                                line-height: 1;
                                transition: all 0.1s;
                                &:hover {
                                    border: 1px dashed $fd-primary;
                                    color: $fd-primary;
                                }
                                .click-show-item__icon {
                                    opacity: .7;
                                }
                            }
                            .drag-show-item {
                                display: none;
                            }
                            &.isWaitOpen {
                                opacity: .6;
                                cursor: not-allowed;
                                -webkit-user-drag: none;
                                .waitOpenTip {
                                    display: inline-block;
                                } 
                            }
                        }
                        margin-bottom: 10px;
                        &:last-child {
                            margin-bottom: 0;
                        }
                    }
                }
            }
        }
    }
}
</style>