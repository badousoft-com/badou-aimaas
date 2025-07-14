<script>
import ModuleEditCode from '@/components/frame/Module/ModuleEditCode'
import FlowTrace from '@/views/module/flow/part/FlowTrace'
import { S_Storage } from '@/utils/storage'
import { HandleClick } from '@/service/module'
import MTitle from '@/components/frame/Common/MTitle'
import { Save } from '@/components/frame/Module/BtnBaseFun/flow-edit'
import FixBottomPage from '@/components/frame/FixBottomPage'
import FlowRouterBtn from '@/views/module/flow/part/FlowRouterBtn'
import FlowOpinion from '@/views/module/flow/part/FlowOpinion'
import { Spread_Status_Change } from '@/api/frame/flow'
import GlobalConst from '@/service/global-const'
export default {
    name: 'work-verify',
    components: {
        [ModuleEditCode.name]: ModuleEditCode,
        [FlowTrace.name]: FlowTrace,
        [MTitle.name]: MTitle,
        [FixBottomPage.name]: FixBottomPage,
        [FlowRouterBtn.name]: FlowRouterBtn,
        FlowOpinion
    },
    props: {
    },
    data () {
        return {
            // 按钮事件
            handleClick: HandleClick,
            // 默认按钮列表
            defaultBtnList: [],
            // 保存表单函数
            saveForm: Save,
            // 页面作用域
            scope: this,
            // 自定义流程按钮
            flowButtons: [],
            // 流程审批弹窗渲染前
            beforeApproveRender: null,
            // 设置表单是否存在，用于重新加载使用
            isShowForm: true,
            // 流程意见
            flowOpinion: '',
            // 定义流程按钮组件域
            flowRouteBtnRef: null,
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
        // 待处理事项id（待办、已办等）
        worklistId () {
            return this.params?.worklistId
        },
        // 目前只为tableView视图使用
        // 是否需要审核/是否需要加载流程路由按钮
        //     0-不加载
        //     1-加载
        verifyStatus () {
            let _verifyStatus = this.params?.verifyStatus
            return _verifyStatus && _verifyStatus.toString() === '1'
        },
        // 获取列表页面点击的待办数据对象
        _flowItem () {
            return S_Storage.getObj('flowItem')
        },
        // 处理事项id
        wid () {
            return this._flowItem?.id
        },
        // 获取流程id
        flowId () {
            return this._flowItem?.flowId
        },
        // 获取处理事项状态码
        //     1-待办
        //     2-已办
        //     4-待办:超时未处理
        //     8-待办:超时已处理
        //     16-未阅
        //     32-已阅
        status () {
            return this._flowItem?.status
        },
        // 表单模型编码
        mdCode () {
            // 获取路由上的mdCode,模型编码的来源有两个
            //     1. 流程视图中配置url，若地址栏配置了mdCode，则优先使用
            //     2. 点击流程列表后，从数据中mdCode
            let _routerMdCode = this.$route.params?.mdCode
            if (_routerMdCode) return _routerMdCode
            // 获取表单数据模型编码
            let _mdCode = this._flowItem?.busiModuleCode
            if (!_mdCode) {
                let wrongTip = '列表数据中的busiModuleCode值为空，其为模型编码，必须有值'
                this.Message.warning(wrongTip)
                return
            }
            return _mdCode
        },
        // 详情id
        detailId () {
            return this._flowItem?.boId
        },
        // 是否可以填写意见
        isSubmitOpinion () {
            if (this.flowId) {
                return this.verifyStatus || this.showOptinionBtn
            }
            return false
        },
        showOptinionBtn () {
            // return String(this.status) === '32' || String(this.status) === '16'
            // 框架后台接口未搬迁，暂时不显示
            return false
        },
        // 是否显示流程意见
        isShowFlowOpinion () {
            return GlobalConst.openOpinionInFlowVerify &&  // 编辑流程表单时是否开启意见填写
                   this.flowId
        }
    },
    methods: {
        // 获取ModuleEditCode/index.vue组件作用域
        getModuleFormRef () {
            return this.$refs.moduleEditCode
        },
        // 获取mForm/index.vue页面作用域
        getFormRef () {
            return this.getModuleFormRef()?.getFormRef()
        },
        // 获取流程按钮组件所在域
        getFlowRouteBtnRef () {
            return this.$refs?.flowRouteBtnRef
        },
        /**
         * 获取模型数据module之后的回调函数
         * 更新flowId值，用于后续请求动态路由按钮数据
         */
        beforeModuleRender (module) {
            // 由于模型表单组件内可能会嵌套表单，其他表单的beforeModuleRender回调也会抛出，导致这里的逻辑异常
            // 因为这里判断只有mdCode一致的才进行处理，避免受到其他模块的干扰
            if (this.mdCode !== module?.mdCode) return
            // 设置自定义流程按钮
            // 所有按钮统一使用buttons，flowButtons待删
            this.flowButtons = module?.customSetting?.buttons || module?.customSetting?.flowButtons
            // 流程审批弹窗渲染前
            let beforeApproveRender = module?.customSetting?.beforeApproveRender
            if (typeof beforeApproveRender === 'function') {
                this.beforeApproveRender = beforeApproveRender
            }
            this.$nextTick(() => {
                // 获取流程按钮组件作用域
                this.flowRouteBtnRef = this.$refs?.flowRouteBtnRef
            })
        }
    },
    created () {
    },
    mounted () {
        if (this.detailId) {
            // 从存储中获取流程意见数据对象
            let flowOpinionData = S_Storage.getObj('flowOpinion')
            // 给流程意见赋值【预防新增保存时的重定向路由，把流程意见给刷掉】
            if (!flowOpinionData) return
            this.$set(this, 'flowOpinion', flowOpinionData[`flow_${this.detailId}`] || '')
        }
    },
    watch: {
        status: {
            immediate: true,
            handler: function (newVal, oldVal) {
                // 这里只处理待阅，进入待阅时需要请求接口将待阅转为已阅
                //     1-待办
                //     2-已办
                //     4-待办:超时未处理
                //     8-待办:超时已处理
                //     16-未阅
                //     32-已阅
                if (!(newVal && newVal.toString() === '16')) return
                // 执行待阅转已阅接口操作
                Spread_Status_Change({
                    wids: this.wid
                }).then(res => {
                    if (res.hasOk) {
                        console.log('待阅转已阅成功')
                    } else {
                        this.Message.error(this.getMessage(res?.message || `待阅转已阅错误`))
                    }
                })
            }
        }
    }
}
</script>
<style lang="scss" scoped>
.flow-edit {
    height: calc(100% - #{$footerHeight});
    .module-edit-code {
        height: auto;
    }
}
</style>