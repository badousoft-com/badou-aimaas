<template>
    <div class="bd-icon-picker">
        <template v-if="!isView">
            <div
                :class="{
                    'has-icon': tempValue
                }"
                class="bd-icon-picker__icon"
                @click="setFocus">
                <disabled-board v-if="disabled"></disabled-board>
                <bd-icon :name="tempValue || 'plus'"></bd-icon>
            </div>
            <bd-select
                :disabled="disabled"
                class="bd-icon-picker__text"
                ref="select"
                v-model="tempValue"
                :options="iconList">
            </bd-select>
        </template>
        <!-- 查看状态：待优化 -->
        <template v-else>
            <bd-icon :name="tempValue"></bd-icon>
        </template>
    </div>
</template>
<script>
import DisabledBoard from '@/components/frame/Status/DisabledBoard'
const iconJSON = require('@/views/default/iconTool/icons.json')
import Select from './Select'
export default {
    name: 'bd-icon-picker',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {
        [Select.name]: Select,
        [DisabledBoard.name]: DisabledBoard,
    },
    props: {
        // 图标值
        value: {
            type: String
        },
        name: {
            type: String,
            default: ''
        },
        // 是否不可编辑
        disabled: {
            type: Boolean,
            default: false
        },
        // 是否为查看状态
        isView: {
            type: Boolean,
            default: false
        },
    },
    data () { // 定义页面变量
        return {
            noIconObj: {
                id: 'noIcon',
                text: '无图标'
            }
        }
    },
    computed: {
        tempValue: {
            get () {
                return this.value
            },
            set (val) {
                // 若当前选择无图标，则返回null值回去
                if (val === this.noIconObj.id) {
                    this.$emit('input', null)
                    return
                }
                // 默认情况，将更新的值传给父组件
                this.$emit('input', val)
            }
        },
        // 获取图标列表数据
        iconList () {
            // 定义结果集
            let resultList = []
            Object.keys(iconJSON).forEach(i => {
                Object.keys(iconJSON[i]).forEach(j => {
                    resultList.push({
                        id: j,
                        text: `${iconJSON[i][j]}____${j}`,
                        icon: j
                    })
                })
            })
            // 添加一个无图标的选项，用于清空图标
            resultList.unshift({
                id: this.noIconObj.id,
                text: this.noIconObj.text,
                icon: null
            })
            return resultList
        }
    },
    methods: { // 定义函数
        // 使下拉框选中
        setFocus () {
            if (this.disabled) return
            this.$refs.select.focus()
        },
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
    }
}
</script>
<style lang='scss' scoped>
$iconMarR: 4px;
.bd-icon-picker {
    display: flex;
    .bd-icon-picker__icon {
        width: $inputHeight;
        height: $inputHeight;
        background: #fff;
        border: 1px solid $inputBorderColor;
        border-radius: $borderRadius;
        display: inline-block;
        vertical-align: top;
        position: relative;
        margin-right: $iconMarR;
        .bd-icon {
            position: absolute;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
            margin: auto;
            fill: $inputBorderColor;
        }
        &:hover {
            border-color: $inputBorderColor_hover;
            .bd-icon {
                fill: $inputBorderColor_hover;
            }
        }
        &.has-icon {
            .bd-icon {
                fill: $primary;
            }
        }
    }
    .bd-icon-picker__text {
        flex: 1;
        width: calc(100% - #{$inputHeight} - #{$iconMarR} - 4px);
        display: inline-block;
    }
    
}
</style>