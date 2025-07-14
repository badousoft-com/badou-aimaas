<template>
    <div class="tree-model">
        <template v-if="isShow">
            <!-- 左侧模块 -->
            <div
                class="list-tree__l-side box-shadow"
                :class="{'is-fold': isFoldTree}">
                <!-- 左侧-标题区域 -->
                <div class="lside-title pointer">
                    <span class="lside-title__word text-o-1">{{title}}</span>
                    <div v-if="!isFoldTree">
                        <slot name="foldBtnFront"></slot>
                    </div>
                    
                    <bd-icon :name='!isFoldTree?"unfoldHor":"foldHor"'  @click="toggleTree"></bd-icon>   
                </div>
                <div class="lside-title-fold">
                    <span
                        class="lside-title-fold__word"
                        v-for="(i, index) in title"
                        :key="index">
                        {{i}}
                    </span>
                </div>
                <!-- 左侧-树区域 -->
                <el-scrollbar class="lside-tree" wrap-class="scrollbar-wrapper">
                    <slot name="left"></slot>
                </el-scrollbar>
            </div>
            <!-- 右侧-列表模块 -->
            <div class="list-tree__r-side box-shadow" v-loading="isLoadingRight">
                <slot name="right"></slot>
            </div>
        </template>
    </div>
</template>
<script>
// import xx from ./xx
export default {
    name: "tree-page-show",
    components: {},
    props: {
        title: {
            type: String
        },
        // 是否允许展示内容
        isShow: {
            type: [Boolean, String],
            default: false
        },
        // 是否正在加载页面右侧状态
        isLoadingRight: {
            type: Boolean,
            default: false
        }
    },
    data () { // 定义页面变量
        return {
            // 是否折叠左侧树
            isFoldTree: false,
        }
    },
    computed: {},
    methods: { // 定义函数
        toggleTree () {
            this.isFoldTree = !this.isFoldTree
        },
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
.tree-model {
    display: flex;
    height: 100%;
    max-width: 100%;
    background: $contentOutBg;
    padding: 4px;
    .list-tree__l-side {
        position: relative;
        width: 250px;
        transition-timing-function: cubic-bezier(0.98, 0, 0.22, 0.98);
        transition-duration: 0.4s;
        transition-property: width;
        border-radius: $borderRadius;
        background: $contentInBg;
        overflow: hidden;
        display: flex;
        flex-direction: column;
        border-top: none;
        flex: 0 0 auto;
        .lside-title {
            justify-content: space-between;
            display: flex;
            padding: $space;
            background: $primary;
            color: $contentInBg;
            flex-shrink: 0;
            .lside-title__word {
                position: relative;
                // display: inline-block;
                left: 0;
                flex-basis: 200px;
                transition-timing-function: cubic-bezier(0.98, 0, 0.22, 0.98);
                transition-duration: 0.5s;
                transition-property: all;
            }
        }
        .lside-title-fold {
            .lside-title-fold__word {
                position: absolute;
                z-index: 1;
                color: $primary;
                width: 26px;
                font-size: 16px;
                margin: 0px auto;
                text-align: center;
                writing-mode: horizontal-tb;
                transition: all 0.4s;
                line-height: 24px;
                left: -24px;
                @for $i from 1 through 10 {
                    &:nth-child(#{$i}) {
                        top: 40px + 10px + ($i - 1) * 26;
                    }
                }
            }
        }
        .lside-tree {
            height: 100%;
            position: relative;
            left: 0;
            transition-timing-function: cubic-bezier(0.98, 0, 0.22, 0.98);
            transition-duration: 0.6s;
            transition-property: left;
        }
        &.is-fold {
            width: 40px;
            .lside-title {
                .lside-title__word {
                    left: -250px;
                    flex-basis: 0;
                }
            }
            .lside-title-fold {
                .lside-title-fold__word {
                    left: 6px;
                    @for $i from 1 through 10 {
                        &:nth-child(#{$i}) {
                            transition-delay: $i * 0.12s;
                        }
                    }
                }
            }
            .lside-tree {
                left: -250px;
                position: relative;
            }
        }
    }
    .list-tree__r-side {
        overflow-x: hidden;
        flex: 1 1 0;
        margin-left: $space;
        border-radius: $borderRadius;
        background: $contentInBg;
    }
}
</style>