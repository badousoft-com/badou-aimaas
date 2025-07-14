<template>
    <div class="flow-route-btn">
        <template v-for="(i, index) in _routerBtnList">
            <bd-button
                v-if="ShowStatus(i)"
                :key="index"
                v-bind="i"
                @click="handleClick(i)">
            </bd-button>
        </template>
    </div>
</template>
<script>
import {
    Get_Flow_Routes,
    Flow_Verify,
    Get_Next_Btn,
    Get_Close_Btn } from '@/views/module/flow/part/flow-service'
import { Merge_Btn, ShowStatus } from '@/service/module'
import GlobalConst from '@/service/global-const'
import { Deep_Clone } from '@/utils/clone'
import { Spread_To_Others } from '@/api/frame/flow'
export default {
    name: 'flow-router-btn',
    components: {},
    props: {
        // 待办事项id
        worklistId: {
            type: String
        },
        // 流程id
        flowId: {
            type: String
        },
        // 业务实例id（表单数据详情id）
        boId: {
            type: String
        },
        // 传入的自定义按钮组
        btnList: {
            type: [Array, Function]
        },
        // 控制路由动态按钮是否加载
        showRouterBtn: {
            type: Boolean,
            default: false
        },
        // 控制保存按钮是否加载
        hasSaveBtn: {
            type: Boolean,
            default: true
        },
        // 组件使用的页面所在的作用域
        pageScope: {
            type: Object
        },
        // 表单保存函数
        saveForm: {
            type: Function
        },
        // 待处理数据项（待办、已办数据对象）
        workItem: {
            type: Object
        },
        // 是否执行saveForm函数的状态，isView:true表示不需要执行saveForm保存
        isView: {
            type: Boolean,
            default: true
        },
        // 过滤数据函数
        filter: {
            type: Function
        }
    },
    data () { // 定义页面变量
        return {
            // 展示在页面底部的按钮
            routerBtnList: [],
            // 全部的流程按钮（底部+下一步页面中的）
            tempBtnList: [],
            // 按钮是否可视的判断函数
            ShowStatus: ShowStatus,
        }
    },
    computed: {
        _routerBtnList () {
            /* eslint-disable vue/no-async-in-computed-properties */
            let result = []
            let type = String(this.type)
            // 弹窗上展示 流程按钮
            switch (type) {
                case '1':
                    // 筛选出放在
                    result = (this.tempBtnList || []).filter(o => {
                        return !this.isShow(o)
                    })
                    // 显示下一步按钮需要满足：1：flowId存在；2：配置showRouterBtn为true
                    this.flowId && this.showRouterBtn && this.flowBtnList.length && result.push(Get_Next_Btn())
                    break
                default:
                    result = this.tempBtnList
            }
            // 判断是否传入过滤函数，有值执行函数，返回结果
            if (this.filter && typeof this.filter === 'function') {
                return this.filter.call(this, result)
            }
            return result
            /* eslint-enable vue/no-async-in-computed-properties */
        },
        // 放在弹窗上的流程按钮列表
        flowBtnList () {
            return (this.tempBtnList || []).filter(o => {
                return this.isShow(o)
            })
        },
        // 按钮展示类型，1：弹窗上以tab的形式展现，0：在底部展示
        type () {
            return GlobalConst.flow.buttonType
        },
        // 获取实际的保存方法
        //     这个组件可接收传入的保存函数，也接收按钮函数
        _saveFun () {
            if (!(this._routerBtnList && this._routerBtnList.length > 0)) return
            return this._routerBtnList.find(i => i.id === 'save')?.click
        }
    },
    methods: { // 定义函数
        /**
         * 执行按钮事件
         * @params {Object} item 按钮对象
         */
        handleClick (item) {
            item &&
            item.constructor === Object &&
            typeof item.click === 'function' &&
            item.click.call(this.pageScope, item)
        },
        // 判断按钮是否显示（隐藏在页面的下一步内）
        isShow (btn) {
            // 固定展示在页面上的按钮id列表
            let show_btn_arr = ['close', 'save', 'back']
            return show_btn_arr.indexOf(btn.id) === -1 && !btn.inPage
        },
        // 重新组装按钮列表
        async setBtnList () {
            let routerBtnList = Deep_Clone(this.routerBtnList) || []
            let result = []
            if (this.btnList) {
                // 判断传入自定义按钮类型为数组或函数
                switch (this.btnList.constructor) {
                    case Function:
                        result = await this.btnList.call(this, routerBtnList)
                        break
                    case Array:
                    default:
                        result = Merge_Btn(routerBtnList, this.btnList)
                }
            } else {
                result = routerBtnList
            }
            // showRouterBtn为false时表示不加载流程按钮
            //     1. 不加载流程按钮：则这里自定义配置的按钮应该也需要处理成只保留【返回】与【保存】的按钮
            //     2. 当表单存在关联模型时，新增的时候期望不加载流程按钮，避免还没填写关联模块数据就发起了
            //     3. 保留疑问：自定义按钮到底是不是最后一道按钮防线，如果是那么这里的逻辑又会限制住按钮
            if (!this.showRouterBtn) {
                result = result.filter(i => ['back', 'save'].includes(i.id))
            }
            // 对按钮进行排序
            if (result) {
                let defaultIndex = GlobalConst.button.sortIndex
                this.tempBtnList = result.sort((obj1, obj2) => {
                    let priority1 = typeof obj1.priority === 'number' ? obj1.priority : defaultIndex
                    let priority2 = typeof obj2.priority === 'number' ? obj2.priority : defaultIndex
                    return priority1 - priority2
                })
            }
        },
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    async mounted () {
        // 定义按钮结果集
        let routerBtnList = []
        // 获取动态路由按钮需要满足：1：flowId存在；2：配置showRouterBtn为true
        if (this.flowId && this.showRouterBtn) {
            // 获取审核页面的动态路由
            routerBtnList = await Get_Flow_Routes({
                flowId: this.flowId,
                worklistId: this.worklistId
            })
            let len = routerBtnList.length
            // 为其设置点击方法
            routerBtnList.forEach((i, i_index) => {
                // 保存后是否需要维持当前路由
                i.notReplaceRouter = String(i[GlobalConst.flow.isNeedDialogKey]) === '0'
                // 设置按钮点击方法
                i.click = Flow_Verify
                // 将待办/已办列表页面的点击数据设置为按钮itemData属性，方便调用
                i.workItem = this.workItem
                // 设置排序顺序
                i.priority = len - i_index
            })
        }
        // 判断是否允许加载保存按钮
        if (this.hasSaveBtn) {
            routerBtnList.push({
                id: 'save',
                name: '保存',
                icon: 'save',
                type: 'primary',
                loading: false,
                click: function () {
                    return this.saveForm.call(this, ...arguments)
                }
            })
        }
        // 是否展示传阅按钮：查看页&&存在待办id时
        if (this.isView && this.worklistId) {
            // 转办：待办跟已办自己全部都看不到
            // 交办：待办给【下级员工】处理(此时自己看不到该条待办)，员工处理完了会将待办重新转给自己(可以看到待办)，自己再继续处理该条待办
            // 加签：待办给【上级领导】处理(此时自己看不到该条待办)，领导处理完了直接将待办提交下一环节不需要再给自己(此时没有待办)。领导处理提交后领导跟自己都能看到已办的数据

            // 定义待阅按钮
            let turnRead = {
                id: 'turnRead',
                icon: 'document',
                name: '传阅',
                priority: 50,
                type: 'primary',
                click: function (btnObj) {
                    // 调用用户地址本选择转办给谁
                    this.addressBook({
                        title: `请选择传阅人`,
                        addressTypes: '2-0-20-2', // 新添加这个属性，从这个进行走通，尽量不影响原有属性
                    }).then(data => {
                        btnObj.loading = true
                        Spread_To_Others({
                            // 传阅用户id
                            spreadUserIds: data.map(i => i.value).join(GlobalConst.separator),
                            wids: this.worklistId,
                        }).then(res => {
                            if (res.hasOk) {
                                this.$message.success('传阅成功')
                            } else {
                                this.$message.error(res.message || '传阅失败')
                            }
                        }).finally(() => {
                            btnObj.loading = false
                        })
                    })
                }
            }
            // 添加传阅按钮
            routerBtnList.push(turnRead)
        }
        // 固定添加关闭按钮
        routerBtnList.unshift(Get_Close_Btn())
        // 合并传入的自定义按钮数组btnLIst，并去除隐藏按钮项
        this.routerBtnList = routerBtnList
        this.$nextTick(() => {
            this.setBtnList()
        })
    },
    watch: {
        btnList: {
            deep: true,
            immediate: false,
            handler () {
                // 以便js/父组件动态增减按钮时，能够重新赋值
                this.setBtnList()
            }
        }
    }
}
</script>
<style lang='scss' scoped>
.flow-route-btn {
    height: $footerHeight;
    line-height: $footerHeight - 2px;
    text-align: center;
}
</style>