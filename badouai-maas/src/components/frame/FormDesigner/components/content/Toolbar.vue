// 当前操作栏操作对象： 全部为编辑表单数据
//     撤销 && 重做： 操作中间内容面板的表单数据的新增与删除，以及位置移动
//     清空： 删除表单列表数据，保留表单属性
//     预览： 查看当前配置的表单页面
//     生成JSON: 查看生成表单的数据
//     生成代码：生成vue页面级别代码
<template>
    <div class="fd-toolbar textR">
        <slot></slot>
        <slot
            v-for="i in _seeList"
            :name="i.id"
            :item="i">
            <div
                :key="i.id"
                :class="{'is-disabled': i.disabled}"
                class="fd-toolbar-item"
                @click="handlerFun(i)"
                :title="i.title">
                <bd-icon :name="i.icon"></bd-icon>
                <span>{{i.label}}</span>
            </div>
        </slot>
        <el-dropdown>
            <span class="el-dropdown-link fontS">
                更多操作<i class="el-icon-caret-bottom"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                    v-for="(i, index) in _moreList"
                    :key="index">
                    <slot
                        :name="i.id"
                        :item="i">
                        <div
                            :key="i.id"
                            class="fd-toolbar-item"
                            @click="handlerFun(i)"
                            :title="i.title">
                            <bd-icon :name="i.icon"></bd-icon>
                            <span>{{i.label}}</span>
                        </div>
                    </slot>
                </el-dropdown-item>
            </el-dropdown-menu>
        </el-dropdown>
    </div>
</template>
<script>
// import xx from ./xx
export default {
    name: 'fd-toolbar',
    components: {},
    props: {
        data: {
            type: Array,
            default: () => []
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
        // 可视列表项
        _seeList () {
            return this.data.filter(i => !this.isMore(i))
        },
        // 隐藏的更多项
        _moreList () {
            return this.data.filter(i => this.isMore(i))
        }
    },
    methods: { // 定义函数
        /**
         * 是否为隐藏的更多项
         * @param {Object} item 操作项
         */
        isMore (item) {
            return item && item.isMore
        },
        /**
         * 执行事件函数
         * @param {Object} item 操作项
         */
        handlerFun (item) {
            let { disabled } = item
            if (!disabled) {
                // 当按钮生效时，触发回调
                this.$emit('handler', item)
            } 
        },
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
@import '../../theme/index.scss';
.fd-toolbar {
    border-bottom: 1px solid $lineColor;
    .fd-toolbar-item {
        cursor: pointer;
        color: $fd-primary-simple;
        display: inline-block;
        margin-right: 3/2 * $space;
        &:hover {
            color: $fd-primary;
        }
        &.is-disabled {
            cursor: not-allowed;
            color: #ccc;
            .bd-icon {
                cursor: not-allowed;
            }
        }
    }
}
</style>