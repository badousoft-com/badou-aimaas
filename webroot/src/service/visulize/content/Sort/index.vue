<template>
    <div>
        <div>
            <draggable
                class="list-group"
                tag="ul"
                v-model="tempData"
                v-bind="dragOptions"
                @start="drag = true"
                @end="drag = false"
                @update="updateSort">
                <transition-group
                    type="transition"
                    :name="!drag ? 'flip-list' : null">
                    <li class="list-group-item"
                        v-for="(i, index) in tempData"
                        :key="index + 1">
                        <triangle-status
                            :text="index + 1"
                            :width="10"
                            type="primary"
                            class="list-group-item__status">
                        </triangle-status>
                        {{ i[_displayNameKey] }}
                    </li>
                </transition-group>
            </draggable>
        </div>
    </div>
</template>

<script>
import TriangleStatus from '@/components/frame/Status/TriangleStatus'
import { Deep_Clone } from '@/utils/clone'
import draggable from 'vuedraggable'
export default {
    inheritAttrs: false,
    name: 'sort-view',
    components: {
        draggable,
        [TriangleStatus.name]: TriangleStatus,
    },
    props: {
        // 数据
        data: {
            type: Array,
            default: () => []
        },
        // 展示名称的键名
        displayNameKey: {
            type: String
        },
        // 传入Boolean： true表示返回的数组中会含有最新排序字段，false表示不会返回新的排序字段
        // 传入String：表示在数组中指定属性用作排序字段，排序后新的序号更新该字段值
        priority: {
            type: [Boolean, String],
            default: true
        },
        // 序号步进数，默认为1，即出来的数组中项的排序序号分别为1,2,3
        priorityStep: {
            type: [Number, String],
            default: 1
        }
    },
    data () {
        return {
            drag: false,
            tempData: [],
        }
    },
    computed: {
        // 获取排序字段键名
        _priority () {
            if (!this.priority) return
            return typeof this.priority === 'string' ? this.priority : '__priority'
        },
        // 获取模块展示名称的键名
        _displayNameKey () {
            return this.displayNameKey || 'displayName'
        },
        dragOptions () {
            return {
                animation: 200,
                group: 'description',
                disabled: false,
                ghostClass: 'ghost',
            }
        },
    },
    methods: {
        /**
         * 添加与更新排序字段
         */
        addPriority (data) {
            if (!this._priority) return data
            return data.map((i, index) => ({
                ...i,
                [this._priority]: (index + 1) * parseInt(this.priorityStep)
            }))
        },
        // 模块排序变更的回调事件
        updateSort () {
            this.tempData = this.addPriority(this.tempData)
        }
    },
    created () {
        // 深拷贝
        this.tempData = this.addPriority(Deep_Clone(this.data))
    }
}
</script>

<style lang="scss" scoped>
.flip-list-move {
    transition: transform 0.5s;
}
.no-move {
    transition: transform 0s;
}
.ghost {
    opacity: 0.5;
    background: #c8ebfb;
}
.list-group-item {
    position: relative;
    cursor: move;
    list-style-type: none;
    display: inline-block;
    padding: 10px;
    background: #fff;
    color: $primary;
    border: 1px solid  $primary;
    border-radius: $borderRadius;
    margin: 0 10px 10px 0;
    .list-group-item__status {
        top: -1px;
        right: -1px;
        border-top-right-radius: $borderRadius;
    }
}
</style>