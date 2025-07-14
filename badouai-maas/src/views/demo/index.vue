<template>
    <div class="padding page-book">
        <h1>demo例子目录：</h1>
        <!-- <ul>
            <li><router-link :to="{ path: '/formDesigner' }"><h3>表单设计器[功能失效]</h3></router-link></li>
        </ul> -->
        <!-- ---------------------------------------------------------------------------------------------------------------------------------------------------------------- -->

        <ul>
            <li><router-link :to="{ path: '/module/flow/list/bd_tester_apply/placeholder' }"><h3>流程列表</h3></router-link></li>
            <li><router-link :to="{ path: '/demo/tab' }"><h3>基础Tab</h3></router-link></li>
            <li><router-link :to="{ path: '/demo/moduleMixinTab' }"><h3>动态组合Tab-模型与自定义页面</h3></router-link></li>
            <li><router-link :to="{ path: '/demo/moduleMixin' }"><h3>动态组合-模型与自定义页面</h3></router-link></li>
            <li><router-link :to="{ path: '/demo/dialog' }"><h3>BdDialog弹窗使用(显示引入组件)</h3></router-link></li>
            <li><router-link :to="{ path: '/demo/showPageDialogByJs' }"><h3>js弹窗-挂载自定义页面</h3></router-link></li>
        </ul>
        ----------------------------------------------------------------------------------------------------------------------------------------------------------------
        
        <ul>
            <li><router-link :to="{ path: '/demo/formStatic' }"><h3>静态表单: 用于复杂页面表单<span class="dangerC">[页面排版不规则，需要完全自定义开发时使用]</span></h3></router-link></li>
            <li><router-link :to="{ path: '/demo/formJSON' }"><h3>动态json表单：用于简单页面表单<span class="primaryC">[页面排版整齐，推荐使用]</span></h3></router-link></li>
            <li><router-link :to="{ path: '/demo/formJSONSlot' }"><h3>动态json结合插槽： 用于基础表单组件为主的含有少量复杂表单组件的表单页面</h3></router-link></li>
            <li><router-link :to="{ path: '/demo/dialogFormByImport' }"><h3>页面引入标签dialog-表单： 用于自定义页面使用弹出表单，前提需引入组件</h3></router-link></li>
            <li><router-link :to="{ path: '/demo/dialogFormByJs' }"><h3>js触发打开dialog-表单：适用于所有环境，不需引入组件，已注册为全局</h3></router-link></li>
            <li><router-link :to="{ path: '/demo/useModelFormOnSelf' }"><h3>自定义页面使用模型表单[需显式引入组件]</h3></router-link></li>
            <li><router-link :to="{ path: '/demo/dialogModelFormByJs' }"><h3>js触发dialog-模型表单</h3></router-link></li>
        </ul>
        ----------------------------------------------------------------------------------------------------------------------------------------------------------------
        <ul>
            <li><router-link :to="{ path: '/demo/list' }"><h3>静态列表 </h3></router-link></li>
            <li><router-link :to="{ path: '/demo/dialogListByJs' }"><h3>js触发打开dialog-列表：适用于所有环境，不需引入组件，已注册为全局(点击按钮会进入loading状态，避免多次点击)</h3></router-link></li>
            <li><router-link :to="{ path: '/demo/useModelListOnSelf' }"><h3>自定义页面使用模型列表[需显式引入组件]</h3></router-link></li>
            <li><router-link :to="{ path: '/demo/dialogModelListByJs' }"><h3>js触发dialog-模型列表</h3></router-link></li>
        </ul>
        ----------------------------------------------------------------------------------------------------------------------------------------------------------------
        <ul>
            <!-- <li><router-link :to="{ path: '/demo/viewStatic' }"><h3>静态表单查看 </h3></router-link></li> -->
            <li><router-link :to="{ path: '/demo/viewModel' }"><h3>模型表单查看 </h3></router-link></li>
            <li><router-link :to="{ path: '/demo/dialogViewByJs' }"><h3>js触发弹窗-通用表单查看 </h3></router-link></li>
            <li><router-link :to="{ path: '/demo/dialogModelViewByJs' }"><h3>js触发弹窗-模型表单查看 </h3></router-link></li>
        </ul>
    </div>
</template>
<script>
    // import xx from ./xx
    export default {
        components: {},
        data () { // 定义页面变量
            return {
                num: 1,
            }
        },
        computed: {},
        methods: { // 定义函数
        },
        // 可访问当前this实例
        created () {},
        // 挂载完成，可访问DOM元素
        mounted () {}
    }
</script>
<style lang='scss' scoped>
    h3 {
        display: inline-block;
        &:hover {
            color: $primary;
        }
    }
    .page-book {
        li {
            color: $fontCL;
        }
    }
</style>