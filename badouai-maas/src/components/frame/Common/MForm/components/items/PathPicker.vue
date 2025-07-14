<template>
    <bd-auto-complete
        v-model="tempValue"
        :options="pathOptions"
        :valueKey="valueKey"
        v-bind="$attrs">
    </bd-auto-complete>
</template>
<script>
import AutoComplete from './AutoComplete'
import defaultRoutes from '@/router/modules/default' // 常规框架路由
import businessRoutes from '@/router/modules/business' // 业务路由
import modelRoutes from '@/router/modules/model' // 模型路由
let Value_Key = 'text'
export default {
    name: 'bd-path-picker',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {
        [AutoComplete.name]: AutoComplete
    },
    props: {
        // 图标值
        value: {
            type: String
        },
        name: {
            type: String,
            default: ''
        },
        // 自定义添加的地址数据，需为src/views/目录下
        // addPath: [{id: '/module/stander/list'}]
        addPath: {
            type: Array,
            default: () => []
        }
    },
    data () { // 定义页面变量
        return {
            valueKey: Value_Key
        }
    },
    computed: {
        tempValue: {
            get () {
                return this.value
            },
            set (val) {
                this.$emit('input', val)
            }
        },
        // 获取路径数据
        pathOptions () {
            // 获取完整路由
            let routerList = [...modelRoutes, ...defaultRoutes, ...businessRoutes]
            // 将路由数据更新给options，方便下拉选择
            return [...this.getAddPath(), ...this.getPathList([], routerList)]
        },
    },
    methods: { // 定义函数
        /**
         * 组装可选路径数组,注意：只处理明确path，含有动态参数的path将不添加在这里，因为select组件的使用不佳
         * @param {Array} pathList 结果路径数组
         * @param {Array} routerList 路由数组
         * @param {String} parentPath 父级路径
         */
        getPathList (pathList, routerList, parentPath) {
            routerList.forEach(i => {
                if (i.path) {
                    // 路径需要结合父级路径组装获取完整路径
                    let addPath = `/${parentPath || ''}/` + i.path
                    // 设置正则，将多个/统一转化为1个，避免上一步的操作导致出现多个
                    addPath = addPath.replace(/(\/)+/g, '/')
                    if (addPath &&
                        addPath !== '/' &&
                        !/https?/.test(addPath)) {
                        pathList.push({
                            [Value_Key]: addPath
                        })
                    }
                    // 递归进行操作
                    if (i.children && i.children.length > 0) {
                        this.getPathList(pathList, i.children, addPath)
                    }
                }
            })
            return pathList
        },
        // 获取自定义传入的添加的路径
        getAddPath () {
            if (!(this.addPath.constructor === Array && this.addPath.length > 0)) {
                return []
            }
            // 第一步：通过filter过滤掉无值的
            return this.addPath.filter(i => i && i.hasOwnProperty(Value_Key) && i[Value_Key])
                                // 第二步：添加路径前缀'/'
                               .map(i => {
                                   i[Value_Key] = `/${i[Value_Key]}`.replace(/(\/)+/g, '/')
                                   return i
                               })
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

</style>