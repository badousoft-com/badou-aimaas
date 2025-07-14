<template>
    <div class="m-colorPicker"
        :class="[
            !multiple?'single':'',
            disabled?'is-disabled':'',
            isView?'is-view':'',
            selfClass
        ]">
        <template v-if="!isView">
            <transition-group name="list">
                <!-- 单个模块 -->
                <!-- transition-group的子项不允许使用index作为key值 -->
                <div 
                    v-for="(i, index) in tempValue" 
                    :key="compName + index"
                    class="m-colorPicker__item el-input__inner p-r">
                    <disabled-board v-if="disabled"></disabled-board>
                    <div class="main">
                        <!-- 1.小色块 -->
                        <div 
                            class="s-square" 
                            :style="{'background':i}">
                            <el-color-picker
                                class="unseen"
                                v-model="tempValue[index]"
                                :predefine="_predefine || defaultPredefineColors"
                                :show-alpha="showAlpha"
                                :color-format="colorFormat"
                                :popper-class="`remove-clear ${popperClass || ''}`"
                                @change="updateItem($event, index)">
                            </el-color-picker>
                        </div>
                        <!-- 2. 色值文本 -->
                        <div class="s-word" @click="copyVal(i, $event)">{{i}}</div>
                        <!-- 3. 删除按钮 -->
                        <div class="s-del" @click="delItem(index)">
                            <bd-icon name="delete"></bd-icon>
                        </div>
                    </div>
                </div>
                <!-- 新增按钮 -->
                <bd-button
                    :disabled="!isEnabledAddBtn"
                    :key="compName + 'AddBtn'"
                    type="primary"
                    class="s-add p-r"
                    :class="{'single': !multiple}">
                    <el-color-picker
                        class="unseen"
                        v-model="defaultColor"
                        :disabled="!isEnabledAddBtn"
                        :predefine="_predefine || defaultPredefineColors"
                        :show-alpha="showAlpha"
                        :color-format="colorFormat"
                        :popper-class="`remove-clear ${popperClass || ''}`"
                        @change="addItem">
                    </el-color-picker>
                    <bd-icon name="add"></bd-icon>
                    <span>{{btnTitle}}</span>
                </bd-button>
            </transition-group>
        </template>
        <!-- 查看模块 -->
        <template v-else>
            <template v-if="tempValue && tempValue.length > 0">
                <div 
                    v-for="(i, index) in tempValue" 
                    :key="index"
                    class="m-colorPicker__item">
                    <div class="main">
                        <!-- 1.小色块 -->
                        <div class="s-square" :style="{'background':i}"></div>
                        <!-- 2. 色值文本 -->
                        <div class="s-word">
                            {{i}}
                            <span v-if="tempValue && tempValue.length > 1">{{showSeparator}}</span>
                        </div>
                    </div>
                </div>
            </template>
            <template v-else>
                <span>{{GlobalConst.view.value}}</span>
            </template>
        </template>
    </div>
</template>
<script>
import { Copy_To_Clip } from '@/utils/copy-clip'
import globalStyle from '@/styles/theme.scss'
import GlobalConst from '@/service/global-const'
import { Get_Color_List } from '@/utils/color'
import DisabledBoard from '@/components/frame/Status/DisabledBoard'
// 预定义颜色方案
const DefaultPredefineColors = [
    globalStyle.primary,
    globalStyle.danger,
    globalStyle.success,
    globalStyle.warning,
    globalStyle.operate
]
export default {
    name: 'bd-color-picker',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {
        [DisabledBoard.name]: DisabledBoard,
    },
    data () { // 定义页面变量
        return {
            compName: 'colorPicker',
            GlobalConst,
            // 新增按钮文本
            btnTitle: '新增颜色',
            // 默认色值
            defaultColor: globalStyle.primary,
            // 设置值分隔符
            separator: ',',
            // 设置展示分隔符
            showSeparator: ';'
        }
    },
    props: {
        disabled: {
            type: Boolean,
            default: false
        },
        // 可能传入值形式 [1,2] 或者 '1,2'
        value: {
            type: [Array, String],
            default: () => []
        },
        name: {
            type: String,
            default: ''
        },
        // 是否多选
        multiple: {
            type: Boolean,
            default: false
        },
        // 限制数
        limit: {
            type: Number,
            default: 0
        },
        // 是否支持透明度选择
        showAlpha: {
            type: Boolean,
            default: false
        },
        // 值颜色的格式
        // 可选：hsl / hsv / hex / rgb
        // hex（show-alpha 为 false）/ rgb（show-alpha 为 true）
        colorFormat: {
            type: String,
        },
        // ColorPicker 下拉框的类名
        popperClass: {
            type: String
        },
        // 预定义颜色
        predefine: {
            type: Array,
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
    computed: {
        // 单项数据流原因，不可直接修改值，需要用临时值做过度使用
        tempValue: {
            get () {
                // 组件页面展示需要类型为Array，如果值为字符串则需要转化为数组格式
                if (this.value && !(this.value instanceof Array)) {
                    return Get_Color_List(this.value, this.separator)
                }
                return this.value || []
            },
            set (val) {
                // 更新父组件数据
                this.updateFather(val)
            }
        },
        // 是否展示新增按钮，满足两条件
        isEnabledAddBtn () {
            if (this.disabled) return
            // 条件1： 必须为编辑状态
            return !this.isView && (
                // 条件2： 多选且没有限制数 或者
                (this.multiple && (!this.limit )) || 
                // 条件2： 多选且数目小于限制数 或者
                (this.multiple && (this.limit && this.tempValue.length < this.limit)) ||
                // 条件2：单选且数目小于1
                (!this.multiple && this.tempValue.length < 1)
            )
        },
        // 预选色方案
        _predefine () {
            return this.predefine || DefaultPredefineColors
        }
    },
    methods: { // 定义函数
        // 更新父组件数据
        updateFather (valList) {
            this.$emit('input', valList.join(this.separator))
        },
        /**
         * 添加颜色
         * @param [String] val: 颜色值
         */
        addItem (val) {
            this.tempValue.push(val)
            // 更新父组件数据
            this.updateFather(this.tempValue)
        },
        /**
         * 更新颜色
         * @param [String] val: 颜色值
         * @param [Number] index: 下角标顺序
         */
        updateItem (val, index) {
            this.tempValue.splice(index, 1, val)
            // 更新父组件数据
            this.updateFather(this.tempValue)
        },
        /**
         * 删除颜色
         * @param [Number] index: 下角标顺序
         */
        delItem (index) {
            this.tempValue.splice(index, 1)
            // 更新父组件数据
            this.updateFather(this.tempValue)
        },
        /**
         * 复制颜色色号
         * @param [String] val: 颜色值
         */
        copyVal (val, event) {
            Copy_To_Clip(val).then(() => {
                let copyTip = document.createElement("span")
                let defaultCss = `
                    position: absolute;
                    z-index: 9999;
                    transition: all 1s;
                    color: ${globalStyle.success};
                `
                // 设置input value属性值
                // copyArea.setAttribute("value", message)  // input标签使用
                copyTip.innerHTML = '复制成功'  // textarea标签使用
                copyTip.className = 'copy-color-tip'
                copyTip.style.cssText = `
                    ${defaultCss}
                    top: ${event.clientY - 20}px;
                    left: ${event.clientX}px;
                    `
                document.body.appendChild(copyTip)
                setTimeout(() => {
                    copyTip.style.cssText = `
                        ${defaultCss}
                        top: ${event.clientY - 40}px;
                        left: ${event.clientX}px;
                        opacity: 0;
                        `
                }, 30)
                setTimeout(() => {
                    // 提醒结束，删除元素（回收，不然页面dom会生成许多辣鸡无用元素）
                    document.body.removeChild(copyTip)
                    // 过渡动画设置的时间是1s，所以这里删除元素的事件在1000ms后
                }, 1000)
            })
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
$padSize: 4px;
$mainHeight: $inputHeight - 2 * $padSize - 2px;
$squareSize: 20px;
.m-colorPicker::v-deep {
    min-height: $inputHeight;
    line-height: normal;
    margin-bottom: -$space;
    // 单选设置不换行，当区域过小时，按钮会换行，删除动画会展示异常
    &.single {
        display: flex;
        flex-wrap: nowrap;
        span {
            flex-shrink: 0;
        }
    }
    .s-add {
        vertical-align: top;
        &.is-disabled {
            .el-color-picker__trigger {
                cursor: not-allowed;
            }
        }
    }
    .m-colorPicker__item {
        display: inline-block;
        height: $inputHeight;
        line-height: $inputHeight;
        border-radius: $borderRadius;
        padding: $padSize 0;
        margin-right: $space;
        margin-bottom: $space;
        width: auto;
        // overflow: auto;
        overflow: hidden;
        cursor: default;
        // 放置添加按钮，可以不用处理最后一个的margin-right
        // &:last-of-type {
        //     margin-right: 0px;
        // }
        .main {
            height: 100%;
            color: $fontC;
            .s-square {
                display: inline-block;
                width: $squareSize;
                height: $squareSize;
                border: 1px solid #f5f5f5;
                border-radius: $borderRadius;
                margin: 0 $padSize 0 $space;
                margin-top: ($mainHeight - $squareSize)/2;
                position: relative;
                overflow: hidden;
            }
            .s-word {
                font-size: $font - 1px;
                min-width: 58px;
                height: $mainHeight;
                line-height: $mainHeight;
                display: inline-block;
                vertical-align: top;
            }
            .s-del {
                cursor: pointer;
                display: inline-block;
                height: $mainHeight;
                line-height: $mainHeight;
                color: $fontCS;
                font-size: $fontS;
                vertical-align: top;
                padding: 0px $padSize;
                margin-left: $padSize / 2;
                border-left: 1px solid $lineColor;
                &:hover {
                    color: $danger;
                }
            }
        }
    }
    .unseen {
        &.el-color-picker {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            opacity: 0;
            // 这个是核心触发按钮，需要设置铺满以便按钮区能任意点击都能触发
            .el-color-picker__trigger {
                width: 100%;
                height: 100%;
            }
        }
    }
    &.is-disabled {
        .m-colorPicker__item {
            border-color: $borderColor !important;
        }
    }
    // 查看模式
    &.is-view {
        .m-colorPicker__item {
            padding: 0;
            .s-square {
                margin-left: 0;
                margin-right: 0;
            }
            .s-word {
                color: $fontCT;
            }
        }
    }
}

// slide-fade动画------------start
.list-enter-active {
    transition: all 1s;
}
.list-leave-active {
    transition: all 0.4s;
}
.list-enter, .list-leave-to {
    opacity: 0;
    transform: translateY(20px);
}
// slide-fade动画------------end
</style>

<style lang='scss'>
// ColorPicker 颜色选择器下拉框------------start
.remove-clear {
    .el-color-dropdown__btns {
        .el-color-dropdown__link-btn {
            display: none !important;
        }
    }
}
// ColorPicker 颜色选择器下拉框------------end
</style>