<template>
    <div class="textTransferInput">
        <!-- 输入框区 -->
        <bd-text
            v-if="isEdit"
            ref="text"
            v-model="tempValue"
            class="s-input"
            @enter="enter"
            @blur="blur">
        </bd-text>
        <!-- 文本区 -->
        <div
            v-else
            @dblclick="dblclick"
            class="s-text">
            <!-- 文本前插槽 -->
            <slot name="text-prev"></slot>
            {{tempValue}}
        </div>
    </div>
</template>
<script>
import Text from './Text'
export default {
    name: 'text-transfer-input',
    components: {
        [Text.name]: Text
    },
    props: {
        // 值
        value: {
            type: String,
            required: true,
        },
        // 双击前事件
        beforeDblclick: {
            type: Function
        }
    },
    data () { // 定义页面变量
        return {
            // 是否编辑状态
            isEdit: false,
            // 添加当前是否刚按了enter状态
            isEnterStatus: false,
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
        }
    },
    methods: { // 定义函数
        /**
         * 切换编辑与文本状态的事件
         */
        toggleEdit () {
            this.isEdit = !this.isEdit
        },
        // 键盘enter事件
        enter () {
            // 设置键盘enter状态为true，避免触发blur中的逻辑
            this.isEnterStatus = true
            // 切换状态
            this.toggleEdit()
        },
        // 输入框失焦事件
        blur () {
            // 失焦时存在两种情况：1.按了enter   2.点击了其他区域
            //     处理逻辑，如果是enter，就走enter逻辑处理，不再走失焦blur逻辑
            if (this.isEnterStatus) {
                this.isEnterStatus = false
                return
            }
            this.toggleEdit()
        },
        /**
         * 文本双击事件
         */
        dblclick () {
            // 判断是否传入双击前事件
            if (this.beforeDblclick && typeof this.beforeDblclick === 'function') {
                if (!this.beforeDblclick()) return
            }
            // 切换状态
            this.toggleEdit()
            this.$nextTick(() => {
                // 设置聚焦状态
                this.$refs.text.focus()
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
</style>