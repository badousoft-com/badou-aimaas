<!--
 * @Description: 流程意见
 * @FilePath: /src/views/module/flow/part/FlowOpinion.vue
-->
<template>
    <div class="flow-opinion">
        <bd-form
            :ref="sign"
            :id="sign"
            :title="noTitle ? '' : '处理意见'"
            :columnNum="1"
            :dataList="showData"
            @change="change">
            <template slot="opinionBtn">
                <div class="d-ib floatL">
                    <bd-button
                        v-for="(i, index) in handlerList"
                        :key="index"
                        v-bind="i"
                        @click="exeMethod(i)"
                        class="optinion-btn">
                    </bd-button>
                </div>
            </template>
        </bd-form>
    </div>
</template>

<script>
import MForm from '@/components/frame/Common/MForm/index'
import GlobalConst from '@/service/global-const'
import { Save_Common_Opinion, Del_Common_Opinion } from '@/api/frame/flow'
import { Get_User_Opinions } from '@/api/frame/user'
// 定义表单字段键对象
let submitObj = {
    commonOpinion: 'commonOpinion', // 常用意见字段
    opinion: 'opinion', // 当前填写意见
}
// 定义事件处理对象
let handleObj = {
    saveOpinion: 'saveOpinion', // 保存为我的常用意见键
    delOpinion: 'delOpinion', // 删除常用意见键
    clearOpinion: 'clearOpinion' // 清空当前所填写意见
}
export default {
    components: {
        [MForm.name]: MForm
    },
    props: {
        noTitle: {
            type: Boolean,
            default: false
        },
        value: {
            type: String,
            default: ''
        }
    },
    data () {
        return {
            // ref标识
            sign: 'submitFlow',
            // 页面展示的字段数据
            showData: [
                { type: 'textarea', name: submitObj.opinion, label: '意见填写', placeholder: '请输入意见，或从下面【常用意见】中快速选择', value: '' },
                { type: 'select', name: submitObj.commonOpinion, label: '常用意见', value: null, options: [], destroyed: true, columnPer: 12, filterable: false },
                { type: 'slot', name: 'opinionBtn', label: '', columnPer: 12, destroyed: true },
            ],
            // 操作事件
            handlerList: [
                { id: handleObj.saveOpinion, name: '保存为常用意见', type: 'primary', icon: 'save', size: 'mini', click: 'saveOpinion', loading: false },
                { id: handleObj.delOpinion, name: '删除常用意见', type: 'danger', icon: 'delete', size: 'mini', click: 'delOpinion', loading: false },
                { id: handleObj.clearOpinion, name: '清空意见', type: 'warning', icon: 'close', size: 'mini', click: 'clearOpinion' }
            ],
        }
    },
    computed: {
        // 获取id键
        idKey: () => GlobalConst.dicOption.idName,
        // 获取text键
        textKey: () => GlobalConst.dicOption.textName,
        // 获取常用意见字段Obj
        _commonOpinionField () {
            return this.showData && this.showData.find(i => i.name === submitObj.commonOpinion)
        },
        // 获取当前意见字段Obj
        _opinionField () {
            return this.showData && this.showData.find(i => i.name === submitObj.opinion)
        },
    },
    methods: {
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
            if (btnObj && btnObj.click) {
                if (typeof btnObj.click === 'function') {
                    btnObj.click.call(this, btnObj)
                } else if (typeof this[btnObj.click] === 'function') {
                    this[btnObj.click].call(this, btnObj)
                }
            }
        },
        /**
         * 保存为我的常用意见
         * @params {Object} btnObj 当前操作按钮对象
         */
        saveOpinion (btnObj) {
            if (!this._opinionField.value) {
                this.$message.warning('请先填写好意见')
                return
            }
            // 获取意见框输入的意见文本值
            let _optionVal = this._opinionField.value
            // 根据输入的意见文本值，在常用意见中进行匹配
            let _currentOption = this._commonOpinionField.options.find(i => i.text === _optionVal)
            // 如果常用中已有
            if (_currentOption) {
                let { id, text } = _currentOption
                this._commonOpinionField.value = id
                this.$message.success(`【${text}】已存在常用意见`)
                return
            }
            // 常用中不存在，则调用接口保存为常用意见
            let _params = { opinion: this._opinionField.value }
            // 设置按钮状态为加载中
            btnObj.loading = true
            // 保存通用意见
            Save_Common_Opinion(_params).then(res => {
                if (res && res?.hasOk && res.bean) {
                    // 获取意见保存后对应的id与文本
                    let { id, opinion } = res.bean
                    if (id && opinion) {
                        // 将新增的常用意见追加进常用意见字段options中
                        this._commonOpinionField.options.splice(0, 0, {
                            [this.idKey]: id,
                            [this.textKey]: opinion
                        })                        // 设置新增的常用意见回显在常用意见字段中为选中状态
                        this._commonOpinionField.value = id
                    }
                    this.$message.success(`保存成功${opinion ? (':已将【' + opinion + '】保存为常用意见') : ''}`)
                } else {
                    this.$message.error(`保存失败${res?.message ? (': ' + res?.message) : ''}`)
                }
                // 关闭按钮加载状态
                btnObj.loading = false
            })
        },
        /**
         * 删除常用意见
         * @params {Object} btnObj 当前操作按钮对象
         */
        delOpinion (btnObj) {
            // 定义获取通用意见字段的值
            let currentCommonOptionValue = this._commonOpinionField.value
            if (!currentCommonOptionValue) {
                this.$message.warning('请先从下面的【常用意见】下拉选择要删除的意见项!')
                return
            }
            // 获取当前选中的通用意见值所在options中的下角标
            let currentCommonOptionIndex = this._commonOpinionField.options.findIndex(i => i[this.idKey] === currentCommonOptionValue)
            // 根据下角标获取对应的文本
            let opinionText = this._commonOpinionField.options[currentCommonOptionIndex][this.textKey]
            this.$confirm(`确定删除【常用意见：${opinionText}】吗？`, '删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                // 执行删除通用意见的接口
                Del_Common_Opinion({
                    ids: currentCommonOptionValue
                }).then(res => {
                    if (res && res?.hasOk) {
                        // 常用意见列表中删除该项
                        this._commonOpinionField.options.splice(currentCommonOptionIndex, 1)
                        // 删除常用意见选中值的字段值
                        this._commonOpinionField.value = null
                        this.$message.success('删除成功')
                    } else {
                        this.$message.error(`删除失败${res?.message ? (': ' + res?.message) : ''}`)
                    }
                })
            })
        },
        /**
         * 清空意见
         * @params {Object} btnObj 当前操作按钮对象
         */
        clearOpinion (btnObj) {
            // 清空当前意见字段的值
            this._opinionField.value = ''
            // 清空当前通用意见字段的值
            this._commonOpinionField.value = null
        },
        // 提交意见
        submit () {
            return new Promise((resolve, reject) => {
                this.$refs[this.sign].validateForm().then(res => {
                    resolve(res)
                }).then(err => {
                    reject(err)
                })
            })
        }
    },
    mounted () {
        // 请求常用审批意见
        Get_User_Opinions().then(res => {
            if (res && res.hasOk) {
                if (!res.bean) return
            }
            let temp_obj = this.showData.find(o => o.name === submitObj.commonOpinion)
            temp_obj.options = res.bean.map(i => ({
                ...i,
                [this.textKey]: i.opinion
            })) || []
        })
    },
    watch: {
        '_opinionField.value': {
            handler (value) {
                this.$emit('input', value)
            }
        },
        value: {
            immediate: true,
            handler (val) {
                this._opinionField.value = val
            }
        }
    }
}
</script>

<style lang="scss" scoped>
.flow-opinion {
    .optinion-btn {
        padding: 7px 10px;
    }
}
</style>