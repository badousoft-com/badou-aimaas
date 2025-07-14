<!--
    面板拖拽组件
-->
<template>
    <div class="layout-drag">
        <!-- <div class="bg-grid bg-grid-big" style="background-size: 100px 100px;"></div>
        <div class="bg-grid" style="background-size: 20px 20px;"></div> -->
        <vue-draggable-resizable
            :ref="'dragBox' + index"
            class="drag-box"
            v-for="(item, index) in blocks"
            :key="index"
            :grid="[gridSize, gridSize]"
            :w="item.w"
            :h="item.h"
            :x="item.x"
            :y="item.y"
            :debug="false"
            :min-width="item.minW || minW"
            :min-height="item.minH || minH"
            :isConflictCheck="false"
            :snap="true"
            :snapTolerance="gridSize - 5"
            @refLineParams="getRefLineParams"
            @resizestop="(x, y, w, h) => onResizeStop(x, y, w, h, index)"
            @resizing="(x, y, w, h) => onResize(x, y, w, h, index)"
            @dragging="(x, y) => onDrag(x, y, item.w, item.h, index)"
            @dragstop="(x, y) =>onDragStop(x, y, item.w, item.h, index)">
            <slot :name="'layout'+ index" :layout="{ data: item, index }"></slot>
            <div class="pos-text" v-if="showPos">
                <!-- 宽：{{parseFloat(item.w.toFixed(2))}}，
                高：{{parseFloat(item.h.toFixed(2))}}，
                top：{{parseFloat(item.y.toFixed(2))}}，
                left：{{parseFloat(item.x.toFixed(2))}} -->
                <span
                    v-for="p in posTipList"
                    :key="p.id"
                    class="pos-item">
                    <span>{{ p.label }}：</span>
                    <div contenteditable @blur="e => posInputBlur(e, p, index)" class="pos-number_input">{{ parseFloat(item[p.id].toFixed(2)) }}</div>
                    <span class="space"></span>
                </span>
            </div>
        </vue-draggable-resizable>
        <!--辅助线-->
        <span
            class="ref-line v-line"
            v-for="(item, i) in vLine"
            :key="'v'+i"
            v-show="item.display"
            :style="{left: item.position, top: item.origin, height: item.lineLength}">
        </span>
        <span
            class="ref-line h-line"
            :key="'h'+j"
            v-for="(item, j) in hLine"
            v-show="item.display"
            :style="{top: item.position, left: item.origin, width: item.lineLength}">
        </span>
        <!--辅助线END-->
        </div>
</template>
<script>
import VueDraggableResizable from '@/views/panel/components/vue-draggable-resizable/index.vue'
import '@/views/panel/components/vue-draggable-resizable/index.css'
export default {
    name: 'panel-drag',
    components: {
        VueDraggableResizable,
    },
    inheritAttrs: false,
    props: {
        // 块
        blocks: {
            type: Array,
            require: true
        },
        // 是否hover显示宽高
        showPos: {
            type: Boolean,
            default: false
        },
        // 网格大小
        gridSize: {
            type: Number,
            default: 10
        },
        // 块最小宽度
        minW: {
            type: Number,
            default: 100
        },
        // 块最小宽度
        minH: {
            type: Number,
            default: 100
        }
    },
    data () { // 定义页面变量
        return {
            vLine: [],
            hLine: [],
            // 位置提示信息
            posTipList: [
                { id: 'w', label: '宽', emit: 'resizeStop' },
                { id: 'h', label: '高', emit: 'resizeStop' },
                { id: 'y', label: 'top', emit: 'dragStop' },
                { id: 'x', label: 'left', emit: 'dragStop' }
            ]
        }
    },
    methods: {
        // 辅助线回调事件
        getRefLineParams (params) {
            const { vLine, hLine } = params
            this.vLine = vLine
            this.hLine = hLine
        },
        // 删除块
        delBlock (i) {
            this.$confirm('确认删除吗？', '提示', {
                confirmButtonText: '确 定',
                cancelButtonText: '取 消',
                type: 'warning'
            }).then(() => {
                this.blocks.splice(i, 1)
            })
        },
        // 更新blocks的值
        updateBlocks (x, y, w, h, index) {
            let blocks = this.blocks
            blocks[index] = { ...blocks[index], x, y, w, h, index }
            this.$emit('update:blocks', blocks)
        },
        // 正在拖动位置
        onDrag (x, y, w, h, index) {
            this.updateBlocks(x, y, w, h, index)
        },
        onResize (x, y, w, h, index) {
            this.updateBlocks(x, y, w, h, index)
        },
        // 调整宽高停止后
        onResizeStop (x, y, w, h, index) {
            this.updateBlocks(x, y, w, h, index)
            this.$emit('resizeStop', x, y, w, h, index)
        },
        // 拖动位置停止后
        onDragStop (x, y, w, h, index) {
            this.updateBlocks(x, y, w, h, index)
            this.$emit('dragStop', x, y, w, h, index)
        },
        posInputBlur (e, posInfo, index) {
            let value = parseFloat(e.target.innerHTML)
            if (String(value) !== 'NaN') {
                this.$set(this.blocks[index], posInfo.id, parseFloat(value.toFixed(2)))
                this.$emit('update:blocks', this.blocks)
                let { x, y, w, h } = this.blocks[index]
                this.$emit(posInfo.emit, x, y, w, h, index)
                this.$refs['dragBox' + index][0].setBoxAttrs(w, h, y, x)
            }
        }
    },
    // 可访问当前this实例
    created () { },
    // 挂载完成，可访问DOM元素
    mounted () { }
}
</script>

<style lang='scss' scoped>
.layout-drag {
    position: relative;
    height: 100%;
    background: #f3f3f3;
    overflow-y: scroll;
    .drag-box {
        // 底部的位置情况说明
        .pos-text {
            display: flex;
            visibility: hidden;
            color: #fff;;
            max-width: calc(100% - 20px);
            background-color: rgba($color: #000000, $alpha: 0.7);
            border-radius: 4px;
            padding: 5px;
            position: absolute;
            bottom: 0px;
            right: 0px;
            z-index: 99;
            .pos-item {
                display: flex;
                .pos-number_input {
                    display: inline-block;
                    width: fit-content;
                    word-break: keep-all;
                }
                .space {
                    padding-left: 10px;
                }
            }
        }
        &:hover {
            .pos-text {
                visibility: visible;
            }
        }
    }

    // 校对线
    .ref-line {
        display: inline-block;
        background-color: #219187;
    }
    // 网格
    .bg-grid {
        position: absolute;
        background-image: linear-gradient(90deg, #aaa 1px, transparent 0), linear-gradient(0deg, #aaa 1px, transparent 0);
        background-position: -1px 0;
        opacity: .1;
        top: 0;
        right: 0;
        left: 0;
        bottom: 0;
    }
}
h4 {
    margin: 0;
}
</style>