<!--
 * @Description: 页面常用组件 - 快捷入口
-->
<template>
    <ul class="quick-enter">
        <li
            v-for="(i, i_index) in list"
            :key="i_index"
            @click="toPage(i)"
            :class="{'vertical-direction': direction === 'vertical'}"
            class="enter-item">
            <el-tooltip v-if="i.title" :content="i.title" effect="light">
                <span class="tooltip-box">
                    <bd-icon
                        class="enter-icon"
                        :name="i.icon"
                        v-bind="i.iconAttrs">
                    </bd-icon>
                    <span class="enter-text" v-bind="i.textAttrs">{{i.text}}</span>
                </span>
            </el-tooltip>
            <template v-else>
                <bd-icon
                    class="enter-icon"
                    :name="i.icon"
                    v-bind="i.iconAttrs"
                    :style="{color: getColorByIndex(i_index)}">
                </bd-icon>
                <span class="enter-text" v-bind="i.textAttrs">{{i.text}}</span>
            </template>
        </li>
    </ul>
</template>

<script>
export default {
    name: 'quick-enter',
    props: {
        list: {
            type: Array,
            require: true
        },
        colorList: {
            type: Array,
            default: []
        },
        // 文字与图标方向，支持值 vertical （垂直）默认横向
        direction: {
            type: String,
            default: ''
        }
    },
    data () {
        return {
        }
    },
    computed: {
        _colorList () {
            if (this.colorList && this.colorList.length) {
                return this.colorList
            }
            return this.colorScheme('default')
        }
    },
    methods: {
        toPage (item) {
            if (typeof item.click === 'function') {
                item.click(item)
            } else if (item.url) {
                this.pushPage({
                    path: item.url,
                    title: item.text
                })
            }
        },
        getColorByIndex (index) {
            let len = this._colorList.length
            let resIndex = index % len
            return this._colorList[resIndex]
        }
    }
}
</script>

<style lang="scss" scoped>
.quick-enter {
    display: flex;
    flex-wrap: wrap;
    .enter-item {
        display: inline-block;
        margin-top: 10px;
        margin-right: 20px;
        cursor: pointer;
        .enter-icon {
            font-size: 18px;
            color: $primary;
            vertical-align: middle;
        }
        .enter-text {
            vertical-align: middle;
        }
        &.vertical-direction {
            display: flex;
            flex-direction: column;
            align-items: center;
            .enter-text {
                display: inline-block;
                width: 100%;
            }
        }
    }
}
ul,
li {
    list-style: none;
    margin: 0;
    padding: 0;
}
</style>