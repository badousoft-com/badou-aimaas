
<template>
    <!-- eslint-disable vue/require-component-is -->
    <component v-bind="linkProps(to)">
        <slot />
    </component>
</template>

<script>
import { Is_External_Url } from '@/utils/validate/index'

export default {
    props: {
        to: {
            type: Object,
            required: true
        }
    },
    methods: {
        /**
         * @param initPath 菜单配置的初始url
         * @param path 组装后的url
         * @param query 查询参数对象
         * 注：如果是外部链接，这里initPath则指配置的外部链接地址，path是组装后的用于项目内展示的页面地址
         */
        linkProps ({ initPath, path, query }) {
            if (Is_External_Url(initPath)) {
                // 这里配置需要动态化决策如何处理外部链接

                // 方案1：新开标签
                // return {
                //     is: 'a',
                //     href: initPath,
                //     target: '_blank',
                //     rel: 'noopener'
                // }

                // 方案2：内容区打开
                return {
                    is: 'router-link',
                    to: { path },
                }
            }
            if (query) {
                return {
                    is: 'router-link',
                    to: {path: path, query: query},
                }
            }
            return {
                is: 'router-link',
                to: {path: path}
            }
        }
    }
}
</script>
