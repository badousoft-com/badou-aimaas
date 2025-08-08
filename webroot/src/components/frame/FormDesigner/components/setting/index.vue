<template>
    <div class="fd-setting">
        <bd-tabs
            v-model="activeName"
            :data="data"
            @tab-change="tabChange"
            @tab-click="tabClick"
            class="fd-setting-main">
            <template
                v-for="i in data"
                v-slot:[i.id]>
                <slot :name="i.id"></slot>
            </template>
        </bd-tabs>
        <slot name="fold-show">
        </slot>
    </div>
</template>
<script>
import BdTabs from '@/components/frame/Common/BdTabs'
export default {
    name: 'fd-setting',
    components: {
        [BdTabs.name]: BdTabs
    },
    props: {
        data: {
            type: Array,
            default: () => []
        },
        value: {
            type: String
        }
    },
    data () { // 定义页面变量
        return {
        }
    },
    computed: {
        activeName: {
            get () {
                return this.value
            },
            set (val) {
                this.$emit('input', val)
            }
        }
    },
    methods: { // 定义函数
        // tab变更事件
        tabChange () {
            this.$emit('tab-change', ...arguments)
        },
        // tab点击事件，注意点击不一定会变更
        tabClick () {
            this.$emit('tab-click', ...arguments)
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
    }
}
</script>
<style lang='scss' scoped>
@import '../../theme/index.scss';
.fd-setting::v-deep {
    .el-tabs__header {
        .el-tabs__item {
            color: $fd-sidebar-color;
            &.is-active {
                color: $fd-primary;
            }
            &:hover {
                color: $fd-primary-hover;
            }
        }
        .el-tabs__active-bar {
            background: $fd-primary;
        }
    }
    .el-tab-pane {
        position: relative;
        .bd-form {
            .el-col {
                padding: 0px;
            }
            background: $fd-sidebar;
            .secForm {
                background: $fd-sidebar;
                .el-form-item__label {
                    color: $fd-sidebar-color !important;
                }
            }
        }
        .s-noData-text {
            color: $fd-sidebar-color;
        }
    }
}
</style>