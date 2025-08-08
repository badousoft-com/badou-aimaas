<template>
    <div
        :class="{
            'h-per-100': !autoHeight
        }"
        class="bd-tabs">
        <el-tabs
            ref="tabs"
            v-model="tempValue"
            :before-leave="beforeLeave"
            @tab-click="tabClick"
            v-setHeight="{
                height,
                autoHeight
            }"
            v-wheelScroll
            :class="{'has-height':height}">
            <el-tab-pane
                v-for="(i, index) in temp_data"
                :key="index"
                :name="i[_idField]"
                :label="i[_textField]"
                :disabled="i.disabled">
                <span slot="label">
                    <bd-icon v-if="i.icon" :name="i.icon" class="bd-tabs__icon"></bd-icon>
                    <span class="bd-tabs__text">{{i[_textField]}}</span>
                </span>
                <slot :name="i[_idField]"></slot>
            </el-tab-pane>
        </el-tabs>
        <div class="bd-tabs__right">
            <slot name="right"></slot>
        </div>
    </div>
</template>
<script>
const idField = 'id'
const textField = 'text'
const sign = '_'
import { Find_By_ClassName } from '@/utils/find-dom'
import { Scroll_Top } from '@/utils/animate'
export default {
    name: "bd-tabs",
    components: {},
    props: {
        // tab选中项值
        value: {
            type: String
        },
        // tab数据项数组
        data: {
            type: Array,
            default: () => []
        },
        // data传入数据中作为【字段键值】使用的字段
        idField: {
            type: String,
            default: idField
        },
        // data传入数据中作为【显示文本】使用的字段
        textField: {
            type: String,
            default: textField
        },
        height: {
            type: [String, Number]
        },
        // 是否让模块自适应高度，不使用高度限制内容为滚动
        autoHeight: {
            type: Boolean,
            default: false
        },
        // 切换标签之前的钩子，若返回 false 或者返回 Promise 且被 reject，则阻止切换。
        beforeLeave: {
            type: Function,
            default: () => Promise.resolve()
        }
    },
    directives: {
        // 设置tab面板高度
        setHeight: {
            inserted: function (el, binding) {
                setTimeout(() => {
                    // 判断是否传入高值
                    if (binding && binding.value) {
                        // 获取是否自适应高度 autoHeight
                        // 获取高度值 height
                        let { autoHeight, height } = binding.value
                        // 自适应高度，则直接结束逻辑
                        if (autoHeight) return
                        let _height = parseInt(height)
                        let contentEl = Find_By_ClassName(el, 'el-tabs__content')
                        let headerEl = Find_By_ClassName(el, 'el-tabs__header')
                        let headerElHeight = headerEl?.clientHeight || 0
                        if (_height > headerElHeight) {
                            // 计算tab_content内容高度
                            contentEl.style.height = `${_height - headerElHeight}px`
                        }
                    }
                })
            }
        },
        // tab过多时处理支持鼠标滚轮滑动
        wheelScroll: function (el, binding, vnode, oldVnode) {
            const _tabScroll = el.querySelector('.el-tabs__nav-scroll')
            _tabScroll.onmousewheel = function (event) {
                // 获取滚动距离，滚动向下滚时值为负值
                let _detail = event.wheelDelta || event.detail
                // vnode.context.wheelDic = event.wheelDelta || event.detail
                // 设置滚动距离
                _tabScroll.scrollLeft = _tabScroll.scrollLeft - _detail
                // 阻止浏览器默认事件
                event.preventDefault()
            }
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
        // 获取id标识符
        _idField () {
            return sign + idField
        },
        // 获取text标识符
        _textField () {
            return sign + textField
        },
        // 选中值
        tempValue: {
            get () {
                return this.value
            },
            set (val) {
                // 这里尽量不讨做太多其他的事件回调，当tab切换时，这里会触发两次
                // 目前怀疑是elementUI tab组件自身的bug
                this.$emit('input', val)
            }
        },
        // 获取tab数组数据
        temp_data: {
            get () {
                if (this.data && this.data.constructor === Array) {
                    return this.data.map(i => {
                        return {
                            ...i,
                            // 避免污染原有数据（字段名冲突）,加个sign标识
                            [this._idField]: i[this.idField],
                            [this._textField]: i[this.textField]
                        }
                    })
                }
                return []
            }
        }
    },
    methods: { // 定义函数
        /**
         * tab 被点击时触发（点击不一定切换成功）
         * @param {Object} vueComponent 被选中的标签 tab 实例
         */
        tabClick (vueComponent) {
            // 获取点击元素所在tab数据中下角标
            let { index } = vueComponent
            this.$emit('tab-click',
                this.temp_data[Number(index)], // 当前对象数据
                Number(index), // 下角标
                this.temp_data, // tab数据
                vueComponent) // 点击tab的实例
            // setTimeout(() => {
            //     // 方案1：
            //     let a = this.$refs.tabs
            //     let b = parseInt(a.$refs.nav.$refs.nav.style.transform.match(/translateX\((.*)px\)/)[1])
            //     let _tabScroll = a.$refs.nav.$refs.navScroll
            //     _tabScroll.scrollLeft = this.wheelDic - b
            //     // 方案2
            //     vueComponent.$el.scrollIntoView({
            //         behavior: 'smooth', // 滚动过渡
            //         inline: 'nearest'
            //     })
            // }, 300)
        }   
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {},
    watch: {
        tempValue: {
            handler: function (newVal, oldVal) {
                let index = this.temp_data.findIndex(i => i[this._idField] === newVal)
                // 这里与tab-click不同，进入这里表示切换是成功的
                // 在这里做change事件回抛，不在tempValue的set事件中（其会触发两次）
                this.$emit('tab-change',
                    this.temp_data[index],  // 当前对象数据
                    index,  // 下角标
                    this.temp_data) // tab数据
                // 当前切换成功，执行页面滚动至顶部的操作
                let tabContentEl = Find_By_ClassName(this.$refs.tabs.$el, 'el-tabs__content')
                // 使用滚动动画，切换tab时重置滚动顶部
                Scroll_Top(tabContentEl, 100)
            }
        }
    }
}
</script>
<style lang='scss' scoped>
.bd-tabs::v-deep {
    position: relative;
    .el-tabs {
        height: 100%;
        .el-tabs__header {
            background: $contentInBg;
            margin: 0;
            .el-tabs__nav-wrap {
                padding: 0 $padding;
                .el-tabs__item {
                    color: $fontCL;
                    .bd-tabs__text {
                        // font-size: $fontL;
                    }
                    .bd-tabs__icon {
                        width: 1em;
                        height: 1em;
                        vertical-align: -0.1em;
                    }
                    &.is-active {
                        color: $primary;
                        font-weight: bold;
                    }
                }
                .el-tabs__nav-prev,
                .el-tabs__nav-next {
                    display:none;
                }
                .el-tabs__nav-scroll {
                    overflow: auto;
                }
            }
            // 屏蔽原本的自动跳转能力
            .el-tabs__nav{
                &.is-top {
                    transform: unset !important;
                }
            }
        }
        &.has-height {
            .el-tabs__content {
                overflow: auto;
            }
        }
        .el-tabs__content {
            // TODO:40换成tab高度scss变量
            height: calc(100% - 40px);
            .el-tab-pane {
                height: 100%;
                overflow: auto;
            }
        }
    }
    .bd-tabs__right {
        position: absolute;
        top: 0px;
        right: $padding;
        line-height: 40px;
        .el-button {
            position: relative;
            top: -1px;
        }
    }
}
</style>