<template>
    <div class="bd-roadmap" :class="[selfClass]">
        <template v-if="!isView">
            <div class="bd-roadmap-group">
                <div
                    class="bd-roadmap-item"
                    v-for="(tag, index) in tempValue.concat([''])"
                    :key="index">
                    <!-- 新增表单（添加到指定元素前面） -->
                    <div class="bd-roadmap-item__left">
                        <div class="bd-roadmap-item__left__main">
                            <!-- 横线 -->
                            <div
                                :class="{ 'is-disabled': disabled }"
                                class="bd-roadmap-item__line"></div>
                            <!-- 按钮 -->
                            <transition name="el-fade-in">
                                <bd-button
                                    :class="{'is-disabled': disabled}"
                                    class="bd-roadmap-item__addBtn"
                                    type="text"
                                    @click="handlePopableInput(index)"
                                    :disabled="disabled"
                                    icon="add-fill">
                                </bd-button>
                            </transition>
                        </div>
                    </div>
                    <!-- 输入框 -->
                    <div class="bd-roadmap-item__right">
                        <el-input
                            class="bd-roadmap-item__input"
                            v-model="tempValue[index]"
                            :style="{ width: getMinWidth(tag) }"
                            :disabled="disabled">
                        </el-input>
                        <div
                            class="bd-roadmap-item__btnClose"
                            v-if="tempValue.length > 1 || tempValue[0]"
                            @click="handleClose(index)"
                            :class="[ disabled ? 'is-disabled' : 'pointer']">
                            <bd-icon class="bd-roadmap-item__iconClose" name="multi"></bd-icon>
                        </div>
                    </div>
                </div>
            </div>
        </template>
        <template v-else>
            <bd-icon 
                :name="iconName" 
                v-if="tempValue && iconName" 
                class="fill icon-small">
            </bd-icon>
            <span>{{_viewShow | completeValue}}</span>
        </template>
    </div>
</template>
<script>
import DisabledBoard from '@/components/frame/Status/DisabledBoard'
import { Get_Word_Width, Remove_Space } from '@/utils'
import GlobalConst from '@/service/global-const'
export default {
    name: 'bd-roadmap',
    components: {
        [DisabledBoard.name]: DisabledBoard
    },
    props: {
        disabled: {
            type: Boolean,
            default: false
        },
        // 传入值
        value: {
            type: String,
        },
        // 字段键名
        name: {
            type: String,
            default: ''
        },
        // 是否为查看状态
        isView: {
            type: Boolean,
            default: false
        },
        // 图标名称：用于查看页面展示
        iconName: {
            type: String
        },
        // 自定义class
        selfClass: {
            type: String,
            default: ''
        }
    },
    data () {
        return {
            // 页面渲染值
            tempValue: [''],
        }
    },
    computed: {
        // 获取查看展示状态
        _viewShow () {
            return (this.value || '').split(GlobalConst.separator).join(' —— ')
        }
    },
    methods: {
        /** 
         * 更新父组件数据
         * @param {Array} valList 以数组的形式传入路线的数据
        */
        emitInput () {
            // 获取当前值
            let _val =  this.tempValue.map(i => i && Remove_Space(i))
                                      .filter(i => i)
                                      .join(GlobalConst.separator)
            // 传值父组件进行更新
            this.$emit('input', _val)
        },
        /** 
         * 删除输入框
         * @param {Number} index 传入要删除输入框的下标
        */
        handleClose (index) {
            // 不可编辑状态时直接return
            if (this.disabled) return
            if (this.tempValue.length <= 1) {
                // 最后一项点击清除时，不执行删除，只清除值
                this.tempValue.splice(index, 1, '')
            } else {
                // 删除一条数据
                this.tempValue.splice(index, 1)
            }
        },
        /** 
         * 新增输入框
         * @param {Number} index 传入要新增输入框的下标
        */
        handlePopableInput (index) {
            this.tempValue.splice(index, 0, '')
        },
        /**
         * 获取输入框宽度 
         * @param {String} tag 传入输入框的数据
        */
        getMinWidth (tag) {
            return tag && Get_Word_Width(tag, {}, 18) || '50px'
        },
        // 获取页面渲染值
        getTempValue (value) {
            // 无值时，直接使用tempValue
            if (!value) return this.tempValue
            // 获取当前页面的渲染值
            let _temp = this.tempValue.map(i => i && Remove_Space(i)).filter(i => i).join(GlobalConst.separator)
            // 若传入值与当前页面的渲染值(此时是去掉空字符串后的)一致，则使用后者，以确保能生成空的输入框
            if (value === _temp) return this.tempValue
            // 若不同，则以传入值为主
            return value.split(GlobalConst.separator)
        }
    },
    created () {
        // 获取初始渲染值
        this.tempValue = this.getTempValue(this.value)
    },
    watch: {
        // 监听tempValue
        tempValue: {
            deep: true, // 深度监听
            handler: function (newVal, oldVal) {
                // 调用父组件事件，更新值
                this.emitInput()
            }
        }
    }
}
</script>
<style lang='scss' scoped>
$_hoverColor: #e6e6e6;
.bd-roadmap::v-deep {
    .bd-roadmap-group {
        display: flex;
        justify-content: flex-start;
        align-items: center;
        overflow-y: hidden;
        overflow-x: auto;
        flex-wrap: nowrap;
        .bd-roadmap-item {
            display: flex;
            justify-content: flex-start;
            align-items: center;
            .bd-roadmap-item__left {
                position: relative;
                display: flex;
                justify-content: center;
                align-items: center;
                padding: 9px 0;
                .bd-roadmap-item__left__main {
                    display: flex;
                    justify-content: flex-start;
                    align-items: center;
                    position: relative;
                    width: 18px;
                    // 横线
                    .bd-roadmap-item__line {
                        opacity: 1;
                        border-top: 1px solid $borderColor;
                        width: 18px;
                        transition-duration: 0.5s;
                    }
                    // 按钮
                    .bd-roadmap-item__addBtn {
                        position: absolute !important;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        padding: 0px;
                        width: 18px;
                        height: 18px;
                        border-radius: 50%;
                        opacity: 0;
                        .bd-icon {
                            transform: translate(0px, 1px);
                            font-size: 14px;
                            color: $primary !important;
                        }
                    }
                }
            }
            .bd-roadmap-item__left:hover {
                .bd-roadmap-item__left__main {
                    .bd-roadmap-item__line:not(.is-disabled) {
                        opacity: 0;
                    }
                }
                .bd-roadmap-item__left__main {
                    .bd-roadmap-item__addBtn:not(.is-disabled) {
                        opacity: 1;
                        transition-duration: 0.5s;
                    }
                }
            }
            // 输入框
            .bd-roadmap-item__right {
                position: relative;
                overflow: hidden;
                .el-input__inner {
                    outline: none;
                    padding: 0 10px;
                    &:focus {
                        border-color: $borderColor;
                    }
                    &:hover:not(.is-disabled) {
                        border-color: $_hoverColor !important;
                    }
                }
                .bd-roadmap-item__btnClose {
                    opacity: 0;
                    position: absolute;
                    top: 0;
                    right: 0;
                    width: 16px;
                    height: 16px;
                    border-top-right-radius: 4px;
                    border-bottom-left-radius: 100%;
                    transition-duration: 0.5s;
                    border-top: 1px solid $_hoverColor;
                    border-right: 1px solid $_hoverColor;
                    background-color: $_hoverColor;
                    .bd-roadmap-item__iconClose {
                        transform: translate(3px, -3px);
                        font-size: 10px;
                        color: #fff;
                        transition-duration: 0.5s;
                    }
                    &:hover:not(.is-disabled) {
                        border-top: 1px solid $borderColor;
                        border-right: 1px solid $borderColor;
                        opacity: 1;
                        background-color: #ccc;
                    }
                }
                .bd-roadmap-item__input {
                    &:hover:not(.is-disabled) {
                        & + .bd-roadmap-item__btnClose {
                            opacity: 1;
                        }
                    } 
                }
            }
            // 最后一个输入框隐藏
            &:last-child {
                .bd-roadmap-item__right {
                    display: none;
                }
            }
            // 第一个和最后一个按钮
            &:first-child,
            &:last-child {
                .bd-roadmap-item__left__main {
                    .bd-roadmap-item__line {
                        display: none;
                    }
                    .bd-roadmap-item__addBtn {
                        opacity: 1;
                    }
                }
            }
            &:nth-last-child(2) {
                .bd-roadmap-item__addBtn {
                    transition-duration: 0s;
                }
            }
        }
    }
}
</style>