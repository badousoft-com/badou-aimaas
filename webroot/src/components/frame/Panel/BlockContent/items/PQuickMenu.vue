<!--
 * @FilePath: @/components/frame/Panel/BlockContent/items/QuickMenu.vue
 * @Description: 快捷菜单
-->
<template>
    <div class="quick-menu pad-10">
        <quick-enter
            v-bind="_menuInfo"
            :list="menuList"
            class="quick-menu-enter">
        </quick-enter>
    </div>
</template>

<script>
import QuickEnter from '@/components/frame/QuickEnter'
export default {
    inheritAttrs: false,
    components: {
        QuickEnter
    },
    props: {
        chartOptions: {
            type: String,
            default: ''
        },
        otherAttrs: {
            type: Object,
            default: () => {}
        }
    },
    data () {
        return {
        }
    },
    computed: {
        // 最终使用主题
        themeInfo () {
            let themeAttrs = this.otherAttrs.theme || {}
            let customTheme = this?.customSetting || {}
            return {
                ...themeAttrs,
                ...customTheme
            }
        },
        _chartOptions () {
            let res = {}
            try {
                res = this.chartOptions ? JSON.parse(this.chartOptions) : {}
            } catch (error) {
                res = {}
            }
            return res
        },
        _menuInfo () {
            let res = this._chartOptions.menuInfo || {}
            if (!res.colorList) {
                res.colorList = this.themeInfo?.color || []
            }
            return res
        },
        menuList () {
            let res = null
            if (!this._menuInfo.data) {
                console.error(`
                    请给快捷菜单配置对应的menuInfo.data 菜单列表！步骤如下：
                    【编辑块 - 内容配置 - 内容信息 - 图表配置 内编辑】
                    data 数组 的参考格式如下
                `)
                console.log(`
                    { icon: 'shouwendengji1', text: '收文登记', url: '/module/flow/flowVerify/SWXXGL/add/0' },
                    { icon: 'neibubanwendengji', text: '内部办文登记', url: '/module/flow/flowVerify/NBBWGL/add/0' },
                    { icon: 'fawendengji', text: '发文登记', url: '/module/flow/flowVerify/FW/add/0' },

                `)
            } else {
                res = this._menuInfo.data
            }
            return res
        }
    }
}
</script>

<style lang="scss" scoped>
.quick-menu::v-deep {
    padding-top: 0;
    .quick-menu-enter {
        .enter-item {
            .enter-icon {
                font-size: calc(#{$fontL} + 8px);
            }
        }
    }
}
</style>
