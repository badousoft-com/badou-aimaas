<template>
    <div
        v-if="_isShowMenu"
        class="rc-body">
        <div class="rc-menu">
            <div
                v-for="(i, index) in _btnList"
                :key="index"
                class="rc-menu-group">
                <div
                    v-for="(j, j_index) in i.list" 
                    :key="j_index"
                    :class="[allowOpClassName, 'rc-menu-item']"
                    @click.stop="itemClick(j)">
                    <div :class="[allowOpClassName, 'rc-icon']">
                        <bd-icon
                            v-if="j.icon"
                            v-bind="_icon(j.icon)"
                            :fillIcon="false"
                            :class="[allowOpClassName, 'bd-btn__icon']">
                        </bd-icon>
                    </div>
                    <div :class="[allowOpClassName, 'rc-text']">
                        {{j.name}}
                        <span :class="[allowOpClassName, 'fontCS']">:当前行</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import { HandleClick, Show_Status } from '@/service/module'
import { List_Filter_Group_ByAttr } from '@/utils/list'
import { Get_Icon_Obj } from '@/service/icon.js'
export default {
    name: 'right-menu',
    components: {},
    props: {
        // 操作按钮数组
        btnList: {
            type: Array,
            default: () => []
        },
        // 按钮事件的作用域
        btnScoped: {
            type: Object
        },
    },
    data () { // 定义页面变量
        return {
            // 定义用于元素识别的类
            allowOpClassName: 'allow-op'
        }
    },
    computed: {
        // 获取实际展示的按钮数组
        _btnList () {
            if (!(this.btnList && this.btnList.length > 0)) return []
            // 过滤出含有rightMenu值为truthy且显示的按钮
            let _res = this.btnList.filter(i => i.rightMenu && Show_Status(i))
            if (_res.length === 0) return []
            // 对按钮进行分组，rightMenu可以传入Boolean或String，String时表示为组名
            return List_Filter_Group_ByAttr(_res, 'rightMenu', 'base')
        },
        // 根据传入的按钮数组判断是否展示右键菜单
        _isShowMenu () {
            return this._btnList.length !== 0
        },
        _icon () {
            return ((icon) => {
                return Get_Icon_Obj(icon)
            })
        }
    },
    methods: { // 定义函数
        /**
         * 执行选项操作
         * @param {Object} item 按钮项
         */
        itemClick (item) {
            // 关闭当前窗口
            this.close()
            // 执行事件
            HandleClick.call(this.btnScoped, item)
        },
        // 关闭右键菜单
        close () {
            // 这里的延时主要是等获取列表selection，避免关的太快获取不到selection
            setTimeout(() => {
                this.$emit('close')
            }, 30)
        },
        /**
         * 鼠标事件
         * 目前鼠标滚轮与鼠标左键事件都会触发当前
         */
        operateEvent (event) {
            // 判断当前操作的区域是否非右键弹窗区域，若不是则关闭弹窗
            if (!event?.target?.className?.includes(this.allowOpClassName)) {
                // 页面滚动时清除右键菜单，避免位置混淆
                this.close()
            }
        },
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        // 监听鼠标滚动事件
        window.addEventListener('wheel', this.operateEvent, true)
        window.addEventListener('mousedown', this.operateEvent, true)
        // 组件销毁时移除监听
        this.$once('hook:beforeDestroy', () => {
            window.removeEventListener('wheel', this.operateEvent, true)
            window.removeEventListener('mousedown', this.operateEvent, true)
        })
    }
}
</script>
<style lang='scss' scoped>
$borderColor: #e2e2e2;
.rc-body {
    width: 200px;
    position: fixed;
    border: 1px solid $borderColor;
    z-index: 9999999;
    box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.5);
    font-size: $fontS;
    color: $fontC;
    background: #FAFAFA;
    .rc-menu {
        display: flex;
        flex-direction: column;
        align-items: center;
        .rc-menu-group {
            display: flex;
            flex-direction: column;
            align-items: center;
            width: 100%;
            border-bottom: 1px solid $borderColor;
            padding: 5px 0;
        }
        .rc-menu-group:last-child {
            border-bottom: none;
        }
        .rc-menu-item {
            padding: $borderRadius;
            display: flex;
            align-items: center;
            width: 100%;
            cursor: default;
            .rc-icon {
                width: 20%;
                text-align: center;
            }
            .rc-text {
                flex: 1;
            }
        }
        .rc-menu-item:hover {
            background: $borderColor;
        }
    }
}
</style>