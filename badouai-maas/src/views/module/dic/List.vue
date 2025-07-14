// 新的时代已经来临
<template>
    <div class="bd-page-list">
        <module-list-code
            ref="moduleList"
            :mdCode="mdCode"
            :defaultParamsObj="query"
            :defaultBtnList="defaultBtnList"
            fullHeight>
        </module-list-code>
    </div>
</template>
<script>
import ModuleListCode from '@/components/frame/Module/ModuleListCode'
export default {
    components: {
        [ModuleListCode.name]: ModuleListCode
    },
    data () { // 定义页面变量
        return {
            defaultBtnList: [{
                id: 'add',
                name: '新增',
                icon: 'add',
                type: 'primary',
                click: function (btnObj) {
                    // this: bd-module-list组件作用域
                    this.pushPage({
                        path: `/module/dic/edit/${this.mdCode}/add`,
                        title: '新增数据字典'
                    })
                }
            }, {
                id: 'edit',
                name: '修改',
                icon: 'edit',
                type: 'warning',
                click: function (btnObj) {
                    // this: bd-module-list组件作用域
                    // 获取选中列表数据
                    let selection = this.getSelection()
                    if (selection.length !== 1) {
                        this.$message({
                            type: 'warning',
                            message: '请选择一行！'
                        })
                        return
                    }
                    this.pushPage({
                        path: `/module/dic/edit/${this.mdCode}/${selection[0].id}`,
                        title: `编辑【${selection[0].name}】数据字典`
                    })
                }
            }, {
                id: 'delete',
                name: '删除',
                icon: 'delete',
                type: 'danger',
                click: function (btnObj) {
                    // this: bd-module-list组件作用域
                    // 获取列表页面所在作用域
                    let listPageRef = this.listPageRef()
                    // 获取选中列表数据
                    let selection = this.getSelection()
                    if (selection.length === 0) {
                        this.$message({
                            type: 'warning',
                            message: '至少选择一行！'
                        })
                        return
                    }
                    this.$confirm('确定删除吗？', '删除', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).then(() => {
                        let params = {
                            mdCode: this.mdCode,
                            ids: selection.map(i => i.id).join(',')
                        }
                        this.post(`${this.BASEURL}/jdbc/common/basecommondelete/delete.do`, params).then(res => {
                            if (res.hasOk) {
                                // 更新列表页面数据
                                listPageRef.init()
                                this.$message.success('删除成功')
                                // 删除回调
                                this.$emit('afterDelete', selection)
                            } else {
                                this.$message.error('删除失败')
                            }
                        })
                    })
                }
            }]
        }
    },
    computed: {
        // params参数
        params () {
            return this.$route?.params
        },
        // query参数
        query () {
            return this.$route?.query
        },
        // 模型编码
        mdCode () {
            return this.params?.mdCode
        }
    },
    methods: { // 定义函数
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
.bd-page-list {
    .bd-module-list {
        height: 100%;
    }
}
</style>