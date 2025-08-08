<!--
 * 设计器：环节处理
-->
<template>
    <div class="flow-handle" v-loading="loading">
        <div class="form-title">
            <bd-icon name="pillar-fill" class="pillar fill"></bd-icon>
            <span>环节处理事件</span>
            <span class="marL-10 add-btn" @click="addFlow()">
                <bd-icon name="add-fill"></bd-icon>
            </span>
        </div>
        <div class="flow-content">
            <el-scrollbar class="flow-scroll" ref="scrollRef" v-if="showList.length">
                <transition-group name="list" tag="div">
                    <div
                        class="flow-item mar-10"
                        v-for="(r, r_index) in showList" :key="r.u_id">
                        <div class="flow-hover">
                            <module-form
                                noTitle
                                ref="moduleForm"
                                v-bind="r.module"
                                :fieldList.sync="r.value"
                                :elseAttrs="elseAttrs"
                                :mainScope="mainScope"
                                class="content-form__main padR-10">
                            </module-form>
                            <div class="btn-list">
                                <bd-icon class="add-btn marR-10" name="add-fill" @click="addFlow(r_index)"></bd-icon>
                                <bd-icon class="del-btn" name="multi" @click="delFlow(r_index)"></bd-icon>
                            </div>
                        </div>
                    </div>
                </transition-group>
            </el-scrollbar>
            <no-data v-else height="300"></no-data>
        </div>
    </div>
</template>

<script>
import ModuleForm from '@/components/frame/Module/ModuleForm/index.vue'
import NoData from '@/components/frame/NoData'
import ModuleUtils from '@/js/ModuleUtils'
import { Deep_Clone } from '@/utils/clone'
import { Load_Field_Info, Get_Route_Array, Save_Flow } from '@/api/frame/desinger/field'
import { EventBus } from '@/service/event-bus'
import { Update_FieldList_Value } from '@/service/module'
export default {
    components: {
        NoData,
        [ModuleForm.name]: ModuleForm
    },
    computed: {
        // showList () {
        //     let result = []
        //     this.flowList.forEach(item => {
        //         let temp = {}
        //         Object.keys(item).forEach(i => {
        //             temp[i] = {
        //                 value: item[i]
        //             }
        //         })
        //         result.push({
        //             u_id: item.u_id,
        //             module: Deep_Clone(this.module),
        //             value: temp,
        //         })
        //     })
        //     return result
        // },
        elseAttrs () {
            return {
                flowId: this.flowId || this.scope?.mainFormData?.flowId?.value
            }
        }
    },
    props: {
        // 主表module
        mainModule: {
            type: Object
        },
        // 模型数据id
        id: {
            type: String
        },
        mainFieldList: {
            type: Array
        },
        mainScope: null
    },
    data () {
        return {
            mdCode: 'sys_module_flow',
            module: {},
            // 流程
            flowList: [],
            flowId: '',
            showList: [],
            loading: false,
            isReadyOptions: false,
        }
    },
    methods: {
        // 获取唯一值（transition-group 的key 必须唯一，否则list移开的样式将发生错乱）
        getUID: function () {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                let r = Math.random() * (16 | 0)
                let v = c === 'x' ? r : (r & 0x3 | 0x8)
                return v.toString(16)
            })
        },
        /**
         * @description: 添加环节处理事件
         * @param {Number} addIndex：添加位置
         */
        addFlow (addIndex = this.showList.length - 1) {
            let add_obj = {
                u_id: this.getUID(),
                value: Deep_Clone(this.module.fieldList),
                module: Deep_Clone(this.module),
            }
            this.showList.splice(addIndex + 1, 0, add_obj)
            this.setPageChange()
            // 添加完后，滚动条将移动到最底部
            this.$nextTick(() => {
                this.$refs.scrollRef.wrap.scrollTop = this.$refs['scrollRef'].wrap.scrollHeight
            })
        },
        /**
         * @description: 删除环节处理事件
         * @param {Number} delIndex：删除所有索引
         */
        delFlow (delIndex) {
            this.$confirm('确定删除此关系?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.showList.splice(delIndex, 1)
                this.setPageChange()
                this.$message({
                    type: 'success',
                    message: '删除成功!'
                })
            })
        },
        // 设置当前tab为已修改、未保存状态
        setPageChange () {
            if (this.mainScope?.customSetting?.isChangeInfo) {
                this.mainScope.customSetting.isChangeInfo[this.mdCode] = true
            }
        },
        // 验证表单（单个）
        checkForm (formRef) {
            return new Promise((resolve, reject) => {
                formRef.validateForm().then((res) => {
                    resolve(res)
                }).catch(() => {
                    reject()
                })
            })
        },
        // 一次性验证所有表单
        checkFormAll () {
            return new Promise((resolve, reject) => {
                let list = []
                let moduleFormRef = this.$refs?.moduleForm || []
                for (let i = 0; i < moduleFormRef.length; i++) {
                    let item = moduleFormRef[i]
                    // 获取表单ref
                    let editRef = item?.$refs?.edit
                    list.push(this.checkForm(editRef))
                }
                Promise.all(list).then((res) => {
                    resolve(res)
                }).catch(() => {
                    reject()
                })
            })
        },
        // 请求流程信息
        loadFlowInfo () {
            Load_Field_Info({ id: this.id }).then(res => {
                this.loading = false
                if (res) {
                    this.flowId = res.flowId
                    let flowList = res.flowList || []
                    this.showList = []
                    flowList.forEach(item => {
                        // let temp = {}
                        // Object.keys(item).forEach(i => {
                        //     temp[i] = {
                        //         value: item[i]
                        //     }
                        // })
                        let value = Update_FieldList_Value(Deep_Clone(this.module.fieldList), item, { valuePath: '' })
                        this.showList.push({
                            u_id: this.getUID(),
                            value,
                            module: Deep_Clone(this.module),
                        })
                    })
                } else {
                    this.$message.error('请求环节处理信息失败')
                }
            }).catch(err => {
                this.loading = false
                this.$message.error('请求环节处理信息失败')
            })
        },
        // 根据flowId 更改路由名称下拉框
        refreshByFlowId (flowId = this.flowId) {
            if (flowId !== this.flowId) {
                this.flowId = flowId
                // 流程已发生更改，清空之前填写的环节处理信息
                this.$set(this, 'showList', [])
                // 将已更改的环节处理信息存起来
                // 否则，如果只更改了模型管理的流程配置信息，而没有保存
                // 更改当前的路由名称下拉框数据
                Save_Flow({ moduleId: this.id, flows: JSON.stringify([]) })
                // 获取已更改流程的路由下拉信息
                this.getFlowList(flowId)
            }
        },
        // 获取路由列表
        async getFlowList (flowId = this.flowId) {
            if (!flowId || !this.module?.fieldList) return
            let field_obj = this.module.fieldList.find(o => o.name === 'routeId')
            if (field_obj) {
                this.isReadyOptions = true
                let options = await this.loadRouteArray({ flowId: flowId })
                this.$set(field_obj, 'options', options)
                // Get_Route_Array({ flowId: flowId }).then(res => {
                //     if (res.hasOk) {
                //         this.$set(field_obj, 'options', res.bean)
                //     }
                // })
            }
        },
        // 请求路由列表
        loadRouteArray (params) {
            return new Promise((resolve, reject) => {
                Get_Route_Array(params).then(res => {
                    if (res.hasOk) {
                        resolve(res.bean)
                    } else {
                        resolve([])
                    }
                })
            })
        },
        /**
         * 根据指定路径获取当前组件作用域下对象
         * 目标对象为this,当pathStr为'a,b'时，则最终返回this[a][b]
         * @param {String} pathStr：路径字符串
         */
        getThisObj (pathStr) {
            if (pathStr && pathStr.constructor === String) {
                // 定义获取路径数组
                let pathList = pathStr.split(',')
                let aimObj = null
                try {
                    // 更新aimObj
                    pathList.forEach(i => {
                        aimObj = this[i]
                    })
                } catch (e) {
                    return this
                }
                return aimObj
            }
            return this
        }
    },
    async mounted () {
        this.loading = true
        // 获取流程模型数据
        this.module = await ModuleUtils.editModuleCfg(this.mdCode)
        // 获取路由名称下拉框数据
        // 写在这，而非js文件的原因在于：
        // js文件：每进入一次beforeRender，就需要请求一次，有多少个表单初始就会请求多少次，添加表单时也会触发多次更改
        // 在这里书写，初始无论有多少个表单，仅需要请求一次即可
        await this.getFlowList(this.mainScope?.formData?.flowId?.value)
        // 请求流程信息
        this.loadFlowInfo()
        // Vue事件总线
        // 此处场景说明：
        //     在模型编辑中允许tab配置自定义页面，使用vue.extend挂载页面
        //     为使外部父级能够访问vue.extend挂载的组件中的变量，此处通过事件总线方式
        //     为方便后续说明，用A代替父组件，B代替vue.extend挂载组件，进行下列说明
        //     1. 规则为$emit:发出事件 / $on:接收事件
        //     2. 当A中点击按钮时，让A发出事件, B接收到事件通知将B中数据打包
        //     3. 此时B发出事件携带B数据包，A接收事件从而获取到B中数据
        // 目前场景：(以下文件间的相互调度)
        //     1. sys_md_module.edit.js
        //     2. FlowHandle.vue
        //     3. ChildModuleEdit.vue
        EventBus.$on('setFlowData', (mountVarName) => {
            // 通知子tab所在vue页面
            EventBus.$emit('setData', this, mountVarName)
            // 通知主表
            EventBus.$emit('getChildTabData', this, mountVarName)
        })
        // 离开页面时清除事件总线
        this.$once('hook:beforeDestroy', () => {
            EventBus.$off('setFlowData', {})
        })
    },
    watch: {
        flowId: {
            deep: true,
            immediate: false,
            handler: async function () {
                // 获取路由名称下拉框数据
                // 写在这，而非js文件的原因在于：
                // js文件：每进入一次beforeRender，就需要请求一次，有多少个表单初始就会请求多少次，添加表单时也会触发多次更改
                // 在这里书写，初始无论有多少个表单，仅需要请求一次即可
                if (!this.isReadyOptions) {
                    await this.getFlowList(this.mainScope?.formData?.flowId?.value)
                }
            }
        }
    }
}
</script>

<style lang="scss" scoped>
// 环节事件项占据宽度
$flowItemW: 100%;
.flow-handle {
    height: 100%;
    position: relative;
    padding: 10px;
    .form-title {
        position: absolute;
        top: 10px;
        left: 10px;
        line-height: 20px;
        z-index: 9;
        .add-btn {
            color: $success;
        }
    }
    // 事件编辑内容
    .flow-content {
        height: calc(100% - 20px);
        margin-top: 20px;
        // 滚动条
        .flow-scroll {
            /deep/ .el-scrollbar__bar {
                .el-scrollbar__thumb {
                    display: none;
                }
            }
        }
        // 编辑项
        .flow-item {
            display: inline-block;
            width: calc(#{$flowItemW} - 20px);
            margin-bottom: 0;
            position: relative;
            .flow-hover {
                border-radius: $borderRadius;
                box-shadow: 0px 0px 10px rgba(0, 0, 0, 0);
                transition: box-shadow 300ms ease-in-out;
                &:hover {
                    box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.12);
                    transition: box-shadow 300ms ease-in-out;
                }
            }
            // 表单
            .content-form__main {
                display: inline-block;
                width: calc(100% - 80px);
                /deep/ .bd-form {
                    padding-top: 15px;
                    padding-bottom: 0px;
                }
            }
            // 按钮
            .btn-list {
                font-size: 13px;
                display: inline-block;
                width: 70px;
                text-align: right;
                position: absolute;
                right: 10px;
                top: 20px;
                .add-btn {
                    color: $success;
                }
                .del-btn {
                    color: $danger;
                }
            }
        }
    }
}
.list-item {
    display: inline-block;
    margin-right: 10px;
}
.list-enter-active,
.list-leave-active {
    transition: all 1s;
}
.list-enter, .list-leave-to
    /* .list-leave-active for below version 2.1.8 */ {
    opacity: 0;
    transform: translateY(30px);
}
</style>