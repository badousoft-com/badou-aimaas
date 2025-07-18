// index.vue核心列表组件中会重复使用当前模块，因此抽出成组件
<template>
    <span class="cell-value text-o-2">
        <bd-icon  
            v-if="iconObj.name" 
            :name="iconObj.name"
            class="fill icon-small"
            :class="[iconObj.type, iconObj.class]"
            :style="iconObj.style">
        </bd-icon>
        <!-- class-pointer手势类，当事件存在时使用 -->
        <span
            :class="{'pointer': click}"
            class="w-space-pre"
            @click.stop="columnClick()"
            v-html="value">
        </span>
    </span>
</template>
<script>
export default {
    components: {},
    props: {
        // 字段项数据模块
        data: {
            type: Object,
            default: () => {}
        },
        // 操作作用域
        scope: {
            type: Object
        },
        // 展示值
        value: {}
    },
    data () { // 定义页面变量
        return {}
    },
    computed: {
        // 判断是否为sarafi浏览器，做兼容处理
        isSafari () {
            return Is_Safari()
        },
        // 获取图标信息
        icon () {
            return this.data?.icon
        },
        // 获取字段操作事件
        click () {
            return this.data?.click
        },
        /**
         * 获取图标对象
         * @return {Object} 图标对象 {
         *     @param {String} name 图标名称
         *     @param {String} class 图标类名
         *     @param {Object} style 图标样式对象
         * }
         */
        iconObj () {
            if (!this.icon) {
                return {}
            }
            // 获取数据类型
            let _val = this.icon.constructor
            let iconObj = {
                name: '',
                class: '',
                type: '',
                style: {}
            }
            switch (_val) {
                case String:
                    iconObj.name = _val
                    break
                case Object:
                    // 浅拷贝更新iconObj值
                    Object.assign(iconObj, this.icon)
                    break
                default:
                    // do something
            }
            return iconObj
        }
    },
    methods: { // 定义函数
        /** 
         * 字段操作函数
        */
        columnClick () {
            this.$emit('columnClick', this.data, this.scope)
        },
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>

</style>