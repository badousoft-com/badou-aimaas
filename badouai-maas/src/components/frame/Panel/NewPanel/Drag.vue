<template>
    <div class="panel-edit">
        <!-- <div class="bg-grid bg-grid-big" style="background-size: 100px 100px;"></div>
        <div class="bg-grid" style="background-size: 20px 20px;"></div> -->
        <vue-draggable-resizable
            ref="dragBox"
            class="drag-box"
            v-for="(item,index) in blocks"
            :key="index"
            :grid="[20,20]"
            :w="item.w"
            :h="item.h"
            :x="item.x"
            :y="item.y"
            :debug="false"
            :min-width="item.minW"
            :min-height="item.minH"
            :isConflictCheck="false"
            :snap="true"
            :snapTolerance="10"
            @refLineParams="getRefLineParams"
            @resizestop="(x, y, width, height) => onResizeStop(x, y, width, height, index)"
            @dragging="(x, y) => onDrag(x, y, item.w, item.h, index)"
            @dragstop="(x, y) =>onDragStop(x, y, item.w, item.h, index)">
            <div class="btns-contain">
                <span class="btn-box" @click="delBlock(index)">
                    <bd-icon name="delete"></bd-icon>
                </span>
            </div>
            <div v-if="item.type" class="block-item">
                <div>
                    <h4>{{item.text}}</h4>
                </div>
            </div>
            <div v-else class="block padT-10">
                x：{{item.x}}
                y：{{item.y}}<br>
                w：{{item.w}}
                h：{{item.h}}
            </div>
        </vue-draggable-resizable>
        <!--辅助线-->
        <span class="ref-line v-line"
                v-for="(item, i) in vLine"
                :key="'v'+i"
                v-show="item.display"
                :style="{ left: item.position, top: item.origin, height: item.lineLength}"/>
        <span class="ref-line h-line"
                :key="'h'+j"
                v-for="(item, j) in hLine"
                v-show="item.display"
                :style="{ top: item.position, left: item.origin, width: item.lineLength}"/>
        <!--辅助线END-->
        </div>
</template>
<script>
import VueDraggableResizable  from 'vue-draggable-resizable-gorkys'
import 'vue-draggable-resizable-gorkys/dist/VueDraggableResizable.css'
export default {
    name: 'panel-drag',
    components: {
        VueDraggableResizable,
    },
    props: {
        blocks: {
            type: Array,
            require: true
        }
    },
    data () { // 定义页面变量
        return {
            vLine: [],
            hLine: []
        }
    },
  methods: {
        // 辅助线回调事件
        getRefLineParams (params) {
            const { vLine, hLine } = params
            this.vLine = vLine
            this.hLine = hLine
        },
        // 调整宽高停止后
        onResizeStop (x, y, w, h, index) {
            let obj = {...this.blocks[index], x, y, w, h}
            this.blocks[index] = obj
        },
        // 删除块
        delBlock (i) {
            this.$confirm('确认删除吗？', '提示', {
                confirmButtonText: '确 定',
                cancelButtonText: '取 消',
                type: 'warning'
            }).then(()=>{
                this.blocks.splice(i, 1)
            })
        },
        // 正在拖动位置
        onDrag (x, y, w, h, index) {
            // 获取当前拖拽容器的宽高
            let dom = document.getElementsByClassName('panel-edit')[0]
            if (dom) {
                // 网页正文全文宽，网页正文全文高，网页可见区域宽，网页可见区域高，网页正文部分上，网页正文部分左
                let {scrollWidth, scrollHeight, clientWidth, clientHeight, screenTop, screenLeft}  = dom
                // 滚动溢出高度
                if ((screenTop || 0) + clientHeight < y + h) {
                    dom.scrollTop = y + 30
                }
                // 滚动溢出宽度
                // if (clientWidth < x + w) {
                //     dom.screenLeft = x === NaN ? clientWidth : x + w + 30
                // }
            }
        },
        // 拖动位置停止后
        onDragStop (x, y) { }
    },
    // 可访问当前this实例
    created () { },
    // 挂载完成，可访问DOM元素
    mounted () { }
}
</script>

<style lang='scss' scoped>
.panel-edit {
    position: relative;
    height: 100%;
    background: #f3f3f3;
    overflow-y: scroll;
    .block {
        display: inline-block;
        width: 100%;
        height: 100%;
        background: rgba($color: $primary, $alpha: 0.4);
        text-align: center;
        color: #ffffff;
    }
    .drag-box {
        .block-item {
            margin: 10px;
        }
        // 按钮列表
        .btns-contain {
            position: absolute;
            right: 10px;
            top: 10px;
            color: $primary;
            font-size: 12px;
            visibility: hidden;
            transition: visibility 200ms ease-in-out;
            // 按钮列表
            .btn-box {
                padding: 5px;
                background-color: $white;
                border-radius: 50%;
                &:hover {
                    color: $white;
                    background-color: $primary;
                }
            }
        }
        &:hover {
            .btns-contain {
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
        background-image: linear-gradient(90deg,#aaa 1px,transparent 0),linear-gradient(0deg,#aaa 1px,transparent 0);
        background-position: -1px 0;
        opacity: .1;
        top:0;
        right:0;
        left:0;
        bottom:0;
    }
}
h4 {
    margin: 0;
}
</style>