<template>
    <div>
        <div class="opinion-form marT-10">
            <div class="opinion-title">
                <bd-icon name="point-fill" class="point fill"></bd-icon>
                处理意见
            </div>
            <flow-opinion
                v-model="flowOpinion"
                noTitle>
            </flow-opinion>
        </div>
        <bd-form
            v-if="isShowForm"
            :ref="sign"
            :id="sign"
            noTitle
            :openNoFieldTip="false"
            showGroupTitle
            :columnNum="1"
            :dataList="fieldList"
            @change="change">
            <!-- 组名的插槽 -->
            <template v-slot:[groupNameSet.nextInfo]>
                <div class="mar-l d-ib">
                    <bd-button
                        v-for="(i, index) in nextNodeDealer"
                        :key="index"
                        type="text"
                        class="pointer "
                        v-bind="i"
                        @click="exeMethod(i)">
                        <span class="pointer hover-underline">{{i.name}}</span>
                    </bd-button>
                </div>
            </template>
            <!-- 组名的插槽 -->
            <template v-slot:[groupNameSet.spread]>
                <div class="mar-l d-ib">
                    <bd-button
                        v-for="(i, index) in spreadDealer"
                        :key="index"
                        type="text"
                        class="pointer "
                        v-bind="i"
                        @click="exeMethod(i)">
                        <span class="pointer hover-underline">{{i.name}}</span>
                    </bd-button>
                </div>
            </template>
            <!-- 字段插槽：选择下一环节处理人插槽 -->
            <template v-slot:[submitObj.handler]="{data: formItem}">
                <div v-if="formItem.value" class="df-c tag-box">
                    <el-tag
                        v-for="(i, index) in formItem.value.split(_valueSeparator)"
                        :key="index"
                        type="warning"
                        closable
                        @close="delFormSelValue(formItem, i)"
                        :disable-transitions="true"
                        class="marR-5">
                        {{getOptionText(i, formItem.options)}}
                    </el-tag>
                </div>
            </template>
            <!-- 字段插槽：选择传阅人插槽 -->
            <template v-slot:[submitObj.spread]="{data: formItem}">
                <div v-if="formItem.value" class="df-c tag-box">
                    <el-tag
                        v-for="(i, index) in formItem.value.split(_valueSeparator)"
                        :key="index"
                        type="warning"
                        closable
                        @close="delFormSelValue(formItem, i)"
                        :disable-transitions="true"
                        class="marR-5">
                        {{getOptionText(i, formItem.options)}}
                    </el-tag>
                </div>
            </template>
        </bd-form>
    </div>
</template>
<script>
// 定义表单字段键对象
let submitObj = {
    handler: 'handler', // 下一环节处理人
    routeDesc: 'routeDesc', // 路由描述：路由按钮名称到环节名称
    spread: 'spreadIds', // 抄送人 / 传阅人
    opinion: 'opinion', // 当前填写意见
}
let groupNameSet = {
    write: '流程意见填写',
    nextInfo: '下一环节处理人',
    spread: '传阅人',
    router: '路由'
}
import MForm from '@/components/frame/Common/MForm/index'
import ScopeMixin from '@/components/frame/ScopeMixin'
import { Start_Option_And_Dealer, Next_Option_And_Dealer } from '@/api/frame/flow'
import GlobalConst from '@/service/global-const'
import { Deep_Clone } from '@/utils/clone'
import FlowOpinion from '@/views/module/flow/part/FlowOpinion'
export default {
    name: 'submit-flow',
    components: {
        [MForm.name]: MForm,
        FlowOpinion
    },
    // 混淆：当前组件会被其他页面动态引入调用，需要该混淆类实现作用域的共享
    mixins: [ScopeMixin],
    props: {
        // 用于请求审核前的意见与处理人的请求参数对象
        // 1. 审核首发（流程发起）：config属性如下
        //     boId: flowFormRes?.bean?.id, // 业务实例id
        //     routeId: btnObj.routeId, // 路由id
        // 2. 审核提交下一环节：config属性如下
        //     worklistId: btnObj.itemData?.id, // 待办id
        //     routeId: btnObj.routeId, // 路由id
        config: {
            type: Object,
            default: () => {}
        },
        // 是否为发起环节：环节区分为发起环节与下一环节
        isCreateNode: {
            type: Boolean,
            default: false
        },
        beforeRender: {
            type: Function,
            default: null
        },
        // 下一环节处理人地址本配置
        handlerConfig: {
            type: Object,
            default: () => {}
        },
        // 抄送人地址本配置
        spreadConfig: {
            type: Object,
            default: () => {}
        },
        opinion: {
            type: Object,
            default: () => {}
        }
    },
    computed: {
        // boId: () => this.config.boId, // 业务实例id
        // routeId: () => this.config.routeId, // 路由id
        // busiType: () => this.config.busiType, // 业务类型
        // worklistId: () => this.config.worklistId, // 待办id
        // 获取id键
        idKey: () => GlobalConst.dicOption.idName,
        // 获取text键
        textKey: () => GlobalConst.dicOption.textName,
        // 获取当前意见字段Obj
        _opinionField () {
            return this.fieldList && this.fieldList.find(i => i.name === submitObj.opinion)
        },
        // 流程意见
        flowOpinion: {
            get () {
                return this._opinionField?.value || ''
            },
            set (val) {
                this._opinionField && this.$set(this._opinionField, 'value', val)
            }
        },
        // 获取值分隔符
        _valueSeparator () {
            return GlobalConst.separator
        }
    },
    data () {
        return {
            submitObj,
            // ref标识
            sign: 'submitFlow',
            // 是否展示表单的状态：该字段用于在处理好字段数据之后再显示表单
            isShowForm: false,
            // 所有组别的名称对象集合
            groupNameSet: groupNameSet,
            // 页面展示的字段数据
            fieldList: [
                // { groupName: groupNameSet.write, type: 'select', name: submitObj.commonOpinion, label: '常用意见', value: null, options: [], destroyed: true },
                { groupName: groupNameSet.write, type: 'hidden', name: submitObj.opinion, label: '意见填写', value: null },
                { groupName: groupNameSet.nextInfo, type: 'slot', name: submitObj.handler, label: '处理人选择', value: null, options: [],
                    rules: [ { required: true, message: `请选择下一环节处理人`, trigger: 'change' } ], wrongTipSite: 'left'
                },
                { groupName: groupNameSet.spread, type: 'slot', name: submitObj.spread, label: '传阅人选择', value: null, options: [], rules: [
                    // { required: true, message: `请选择抄送人`, trigger: 'change' }
                ], wrongTipSite: 'left'},
                // { groupName: groupNameSet.router, type: 'text', name: 'routeDesc', label: '', value: null, isView: true, destroyed: true },
            ],
            // 下一环节处理人操作事件
            nextNodeDealer: [
                { id: 'reChooseHandler', name: '选择', type: 'success', icon: 'users', size: 'mini', click: this.reChooseHandler },
            ],
            // 传阅/抄送人事件
            spreadDealer: [
                { id: 'reChooseSpread', name: '选择', type: 'success', icon: 'users', size: 'mini', click: this.reChooseSpread },
            ],
            // 定义组名集合
            // groupNameSet: groupNameSet
        }
    },
    methods: {
        // 根据id 从 options 获取对应的text
        getOptionText (id, options = []) {
            let tempObj = options.find(o => o.id === id)
            if (tempObj) {
                return tempObj.name || tempObj.text
            }
            return '--'
        },
        /**
         * @description: 删除表单某选中值
         * @param {Object} formItem：表单对象，需要含有value
         * @param {String} delId：待删除的值
         */
        delFormSelValue (formItem, delId) {
            let arr = []
            if (typeof formItem.value === 'string') {
                arr = formItem.value.split(',')
            }
            let index = arr.findIndex(o => o === delId)
            if (~index) {
                arr.splice(index, 1)
            }
            this.$set(formItem, 'value', arr.join(','))
        },
        // 获取常用意见与下一环节处理人
        getOption_Dealer_Spread () {
            // 根据是否为发起环节确定使用的接口
            let _request = this.isCreateNode ? Start_Option_And_Dealer : Next_Option_And_Dealer
            _request(this.config || {}).then(res => {
                if (res && res.hasOk) {
                    if (!res.bean) return
                    // 获取下一环节处理人字段对象
                    let handler = this.fieldList.find(i => i.name === submitObj.handler)
                    // 获取路由路径描述字段对象
                    // let routeDesc = this.fieldList.find(i => i.name === submitObj.routeDesc)
                    // 将常用意见options数据字段转化成通用的数据字段类型字段
                    // this._commonOpinionField.options = res.bean?.myOpinions.map(i => ({
                    //     ...i,
                    //     [this.textKey]: i.opinion
                    // })) || []
                    // 获取下一环节信息
                    let routeSelectObj = res.bean?.routeSelect
                    if (routeSelectObj) {
                        // 设置下一环节处理人数据，可能有多个
                        handler.options = routeSelectObj?.handlers.map(i => ({
                            ...i,
                            [this.textKey]: i.name
                        })) || []
                        // 默认设置所有环节处理人数据为选中状态
                        if (handler.options && handler.options.length > 0) {
                            handler.value = handler.options.map(i => i[this.idKey]).join(GlobalConst.separator)
                        } else {
                            handler.value = null
                            // 如果没有下一环节处理人，并且hasHandlerSelect为false表示下一个环节是归档结束，此时
                            if (!routeSelectObj?.hasHandlerSelect) {
                                // 隐藏下一环节字段
                                handler.type = 'hidden'
                                // 去除下一环节字段校验
                                delete handler.rules
                                // 设置不提交下一环节处理人字段
                                handler.destroyed = true
                            }
                        }
                        // 组装路由路径描述值
                        // routeDesc.value = `${routeSelectObj.routeName} ----- ${routeSelectObj.nextNodeName}`
                    }
                    // 获取是否需要抄送的状态
                    let { needSpread, spreadHandlerList } = res.bean
                    // 获取抄送人字段
                    let spreadField = this.fieldList.find(i => i.name === submitObj.spread)
                    if (needSpread) {
                        spreadField.options = spreadHandlerList?.handlers.map(i => ({
                            ...i,
                            [this.textKey]: i.name
                        })) || []
                        if (spreadField.options && spreadField.options.length > 0) {
                            spreadField.value = spreadField.options.map(i => i[this.idKey]).join(GlobalConst.separator)
                        }
                    } else {
                        spreadField.type = 'hidden'
                        spreadField.destroyed = true
                    }
                    typeof this.beforeRender === 'function' && this.beforeRender.call(this)
                    // 组装好字段数据之后，设置表单显示
                    this.isShowForm = true
                } else {
                    let defaultTip = '操作失败'
                    this.$message.error(res?.message ? `${defaultTip}:${res.message}` : defaultTip)
                }
            })
        },
        /**
         * 表单字段值变更时的change事件
         * @params {String} name 字段键名
         * @params {*} value 字段变更后的值
         * @params {Object} fieldObj 字段对象
         */
        change (name, value, fieldObj) {
            // 判断当前的字段是常用意见字段的话
            if (fieldObj && name === submitObj.commonOpinion) {
                // 选择常用意见时将对应文本值赋值给当前意见
                this._opinionField.value = fieldObj.options.find(i => i[this.idKey] === value)[this.textKey]
                // 赋值成功后取消掉常用意见的选择，方便重新选择常用意见；常用意见的作用也只在于变更时完成赋选中文本
                // this._commonOpinionField.value = null
            }
        },
        /**
         * 执行事件
         * @params {Object} btnObj 当前操作按钮对象
         */
        exeMethod (btnObj) {
            // 执行事件
            btnObj &&
            btnObj.click &&
            typeof btnObj.click === 'function' &&
            btnObj.click(btnObj)
        },
        /** 根据地址本选择数据给处理人或传阅人设值
         * submitName：设值的name，spread 或 handler
         * data：地址本选中的值
         */
        setValueByAddressBook (submitName, data) {
            let spreadNameObj = {
                id: 'spread',  // 等待添加数据的对象id
                otherId: 'handler',  // 相比较的对象id
            }
            let handlerNameObj = {
                id: 'handler',  // 等待添加数据的对象id
                otherId: 'spread',  // 相比较的对象id
            }
            let tempNameObj = submitName === 'spread' ? spreadNameObj : handlerNameObj
            let realData = Deep_Clone(data)  // 允许添加的处理人数据
            let failData = []  // 无法添加的数据
            // 待比较的options数据
            let _options = this.fieldList.find(i => i.name === submitObj[tempNameObj.otherId])?.options || []
            // 待比较的value数据
            let compare_value = this.fieldList.find(i => i.name === submitObj[tempNameObj.otherId])?.value || ''
            if (compare_value && compare_value.length) {
                compare_value = typeof compare_value === 'string' ? compare_value.split(',') : compare_value
                let tempArr = _options.filter(o => compare_value.indexOf(o.id) !== -1)
                realData = []
                data.forEach(i => {
                    let temp = tempArr.find(o => o.id === i.id)
                    if (temp) {   // 传阅人存在此处理人
                        failData.push(i)
                    } else {
                        realData.push(i)
                    }
                })
            }
            if (failData.length) {
                let msg = `
                    <div class="marB-10">${failData.map(o => '<span style="color: #ffa200;">' + o.name + '</span>').join('、')}</div>
                    【注】：处理人与传阅人不能为同一个人
                `
                this.$alert(msg, '以下人员选择失败', {
                    confirmButtonText: '确定',
                    dangerouslyUseHTMLString: true
                })
            }
            // 获取下一环节处理人字段对象
            let handler = this.fieldList.find(i => i.name === submitObj[tempNameObj.id])
            handler.options = realData.map(i => ({
                ...i,
                [this.textKey]: i.name
            }))
            handler.value = handler.options.map(i => i[this.idKey]).join(GlobalConst.separator)
            // 检验当前字段,避免检验无效
            this.$refs[this.sign].validateField(handler.name)
        },
        // 重新选择下一环节处理人
        reChooseHandler () {
            // 调用用户地址本选择转办给谁
            this.addressBook({
                title: `请选择下一环节处理人`,
                addressTypes: '2-0-20-2', // 新添加这个属性，从这个进行走通，尽量不影响原有属性
                ...this.handlerConfig,
            }).then(data => {
                //  ======== 2022/01/12：下一步环节时，处理人和传阅人，不能是同一个人，限制处理下 ======
                this.setValueByAddressBook('handler', data)
            })
        },
        // 重新选择抄送人
        reChooseSpread () {
            // 调用用户地址本选择转办给谁
            this.addressBook({
                title: `请选择传阅人`,
                addressTypes: '2-0-20-2', // 新添加这个属性，从这个进行走通，尽量不影响原有属性
                ...this.spreadConfig,
            }).then(data => {
                this.setValueByAddressBook('spread', data)
            })
        }
    },
    created () {
    },
    mounted () {
        // 获取审核意见与下一环节处理人接口
        this.getOption_Dealer_Spread()
        let opinionText = this.opinion?.[submitObj.opinion]
        if (opinionText) {
            this._opinionField.value = opinionText
            this.$set(this, 'flowOpinion', opinionText)
        }
    }
}
</script>
<style lang="scss" scoped>
.df-c {
    height: 100%;
    display: flex;
    align-items: center;
}
.tag-box {
    flex-wrap: wrap;
    margin-top: 5px;
    margin-bottom: -5px;
    .el-tag {
        margin-bottom: 5px;
    }
}
.opinion-form {
    border-bottom: 1px solid #eee;
}
.opinion-title {
    color: $primary;
    padding-bottom: 2 * $padding;
    font-weight: bold;
    margin-left: 10px;
}
</style>