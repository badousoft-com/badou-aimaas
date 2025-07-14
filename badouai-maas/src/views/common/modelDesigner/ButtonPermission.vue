<!--
 * @FilePath: @/views/common/modelDesigner/ButtonPermission.vue
 * @Description: 按钮权限
-->
<template>
    <div v-loading="loading" class="button-permission_container">
        <el-collapse
            class="permission-item_collapse"
            v-model="collapseValue"
            v-for="(s, s_index) in showForm" :key="s.type">
            <el-collapse-item :name="String(s_index)">
                <template slot="title">
                    <div class="form-title">
                        <!-- <bd-icon name="pillar-fill" class="pillar fill"></bd-icon> -->
                        <span>{{s.title}}</span>
                        <bd-button
                            icon="add-fill"
                            type="text"
                            plain
                            :loading="s.loading"
                            @click.stop="addBtn(s, s_index)"
                            class="add_btn">
                            添加按钮
                        </bd-button>
                        <bd-button
                            icon="allot"
                            type="text"
                            plain
                            @click.stop="publishBtnToMenu()"
                            class="add_btn">
                            全部发布
                        </bd-button>
                    </div>
                </template>
                <div v-if="!loading" :ref="s.type + 'groupRef'" class="relation-card-group">
                    <div v-for="(m, m_index) in s.data" :key="m_index" class="button-permission_content">
                        <div class="setting-list_box">
                            <span @click="publishBtnToMenu(m)" title="发布按钮到菜单" class="publish-btn_box">
                                <bd-icon name="allot"></bd-icon>
                            </span>
                            <span v-if="!m.notDelete" @click="deleteBtn(m, m_index, s)" title="删除按钮" class="delete-btn_box">
                                <bd-icon name="multi"></bd-icon>
                            </span>
                        </div>
                        <bd-skeleton :loading="!m.isFormStartRender" :rows="3">
                            <module-form
                                noTitle
                                ref="moduleForm"
                                v-bind="m.module"
                                labelWidth="126px"
                                :fieldList.sync="m.fieldList"
                                :mainScope="mainScope"
                                :isFormStartRender.sync="m.isFormStartRender"
                                @beforeRender="(fieldList, formScope) => beforeFormRender(fieldList, s, m, formScope)"
                                class="content-form__main padR-10">
                            </module-form>
                        </bd-skeleton>
                    </div>
                </div>
            </el-collapse-item>
        </el-collapse>
    </div>
</template>

<script>
// import MForm from '@/components/frame/Common/MForm/index.vue'
import ModuleForm from '@/components/frame/Module/ModuleForm/index.vue'
import Skeleton from '@/components/frame/Skeleton'
import GlobalConst from '@/service/global-const'
import ModuleUtils from '@/js/ModuleUtils'
import { Load_Field_Info, Publish_Btn_To_Resource } from '@/api/frame/desinger/field'
import { Is_Array } from '@/utils/data-type'
import { Update_FieldList_Value } from '@/service/module'
import { Deep_Clone } from '@/utils/clone'
import { EventBus } from '@/service/event-bus'
import { Sort_List } from '@/utils/list'
export default {
    components: {
        // MForm,
        ModuleForm,
        [Skeleton.name]: Skeleton,
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
    data: () => ({
        loading: false,
        // 页面类型标识
        pageTypeKey: 'belongForm',
        formMdCode: 'mdButton',
        // 表单模型编码
        module: null,
        // 展示表单数据
        showForm: [],
        // 折叠面板绑定值（【列表页按钮，编辑页按钮、查看页按钮】是否展开）
        collapseValue: ['0', '1', '2'],
    }),
    computed: {
        // 默认按钮
        defaultBtns () {
            // // 按钮默认值
            // let { listAttrs, editAttrs } = GlobalConst.button

            // // 列表默认按钮
            // let listBtns = Object.keys(listAttrs).map(key => ({ ...listAttrs[key], [this.pageTypeKey]: 'list', id: '', code: key }))
            // // 编辑页默认按钮
            // let editBtns = [
            //     { ...editAttrs.back, [this.pageTypeKey]: 'edit', id: '', code: 'back' },
            //     { ...editAttrs.save, [this.pageTypeKey]: 'edit', id: '', code: 'save' },
            // ]
            // // 查看页默认按钮
            // let viewBtns = [
            //     { ...editAttrs.back, [this.pageTypeKey]: 'view', id: '', code: 'back' },
            // ]
            // return listBtns.concat(editBtns, viewBtns)
            // 按钮默认值
            let { listAttrs } = GlobalConst.button// 列表默认按钮
            let listBtns = Object.keys(listAttrs).map(key => ({ ...listAttrs[key], [this.pageTypeKey]: 'list', id: '', code: key }))
            return listBtns
        },
    },
    methods: {
        // 删除按钮
        deleteBtn (btnInfo, index, group) {
            this.$confirm('确定删除此按钮吗？').then(res => {
                group.data.splice(index, 1)
            })
        },
        // 发布按钮到菜单上
        publishBtnToMenu (btnInfo) {
            if (btnInfo) {
                // 当前按钮是否已勾选展示开关
                let izShowField = (btnInfo.fieldList || []).find(o => o.name === 'izShow') || {}
                if (!izShowField.value) {
                    this.$message.warning('请先勾选展示当前按钮！')
                    return
                }
            }
            let that = this
            const formId = 'publishDialogForm'
            let mdCode = this.mainFieldList?.find(o => o.name === 'code')?.value
            this.$dialog.init({
                id: formId,
                title: `请选择【${mdCode}】模型所对应的菜单`,
                // 高度根据内容自适应
                isAutoFix: true,
                dataList: [
                    {
                        type: 'addressBook', label: '菜单选择', name: 'resourceId', value: '', addressType: '600-1-20-600',
                        valueFieldId: 'resourceId', valueFieldText: 'resourceName', columnPer: 24,
                        rules: [{ required: true, message: '请选择菜单', trigger: 'change' }],
                    },
                ],
                handlerList: [
                    {
                        name: '取消',
                        icon: 'cancel',
                        type: 'danger',
                        click () {
                            // 关闭弹窗表单
                            this.$dialog.close()
                        }
                    }, {
                        name: '确定',
                        icon: 'save',
                        type: 'primary',
                        loading: false,
                        click (btnObj) {
                            let formObj = this.getDialogConObj(formId, 2)
                            formObj.validateForm().then(formRes => {
                                let params = {
                                    resourceId: formRes.resourceId,
                                    mdCode,
                                    btnId: btnInfo?.btnId || ''
                                }
                                btnObj.loading = true
                                Publish_Btn_To_Resource(params).then(resp => {
                                    if (resp?.hasOk) {
                                        that.$message.success('发布成功')
                                        this.$dialog.close()
                                    } else {
                                        that.$message.error(resp.tip || ('发布失败！' + (resp?.message || '')))
                                    }
                                }).finally(() => {
                                    btnObj.loading = false
                                })
                            })
                        }
                    }
                ],
            })
        },
        // 更新每项按钮数据
        async updateBtnInfo (info) {
            let fieldList = await Update_FieldList_Value(Deep_Clone(this.module.fieldList), info, { valuePath: '' })
            // 按钮默认值
            let { listAttrs, editAttrs } = GlobalConst.button
            let defaultBtnIds = Object.keys(listAttrs).concat(Object.keys(editAttrs))
            return {
                btnId: info.id || '',
                module: Deep_Clone(this.module),
                fieldList,
                notDelete: info.code && defaultBtnIds.includes(info.code),
                isFormStartRender: false,
            }
        },
        // 设置展示表单数据
        async setShowForm (btnList) {
            if (!Is_Array(btnList)) return []
            this.showForm = []
            let groupObj = {}
            for (let index = 0; index < btnList.length; index++) {
                let b = btnList[index]
                // 获取按钮页面类型
                let key = b[this.pageTypeKey]
                // 按钮渲染信息
                let tempInfo = await this.updateBtnInfo(b)
                if (!groupObj[key]) groupObj[key] = [tempInfo]
                else groupObj[key].push(tempInfo)
            }
            let res = [
                { type: 'list', title: '列表页按钮', loading: false },
                // { type: 'edit', title: '编辑页按钮', loading: false },
                // { type: 'view', title: '查看页按钮', loading: false },
            ]
            this.showForm = res.map(item => {
                item.data = groupObj[item.type] || []
                return item
            })
        },
        // 添加按钮
        async addBtn (groupInfo, index) {
            groupInfo.loading = true
            if (!this.collapseValue.includes(String(index))) {
                this.collapseValue.push(String(index))
            }
            let value = await this.updateBtnInfo({ [this.pageTypeKey]: groupInfo.type })
            groupInfo.data.push(value)
            // 页面滚动至底部新增模块处
            setTimeout(() => {
                this.scrollToBoxLastChild(this.$refs[groupInfo.type + 'groupRef'][0])
                this.$set(groupInfo, 'loading', false)
            }, 500)
        },
        // 表单数据渲染前
        beforeFormRender (fieldList, groupInfo, item, formScope) {
            // 获取当前按钮所属页面类型
            let pageType = groupInfo.type
            fieldList.forEach(f => {
                // 部分表单只展示在某一页面类型下
                if (f.showCard) {
                    f.type = f.showCard === pageType ? f.type : 'hidden'
                }
                // 如果按钮所属的页面字段没有值，手动进行赋值
                if (f.name === this.pageTypeKey && !f.value) {
                    f.value = pageType
                }
            })
        },
        // 表单数据校验
        checkForm () {
            return new Promise((resolve, reject) => {
                let promiseList = []
                for (let r_index = 0; r_index < this.$refs.moduleForm.length; r_index++) {
                    let editRef = this.$refs.moduleForm[r_index]?.$refs?.edit
                    editRef.validateForm && promiseList.push(editRef.validateForm())
                }
                Promise.all(promiseList).then((res) => {
                    resolve(res)
                }).catch(() => {
                    reject()
                })
            })
        },
        // 滚动到容器最后一个子元素所在位置
        scrollToBoxLastChild (boxEl) {
            if (!boxEl) return
            // 外部（可滚动）容器的dom元素
            let containerEl = this.$el
            // 获取目标滚动元素距离顶部高度 与 元素高度
            let { offsetTop, offsetHeight } = boxEl
            // 获取所有子元素节点
            let allChild = boxEl.querySelectorAll('.button-permission_content') || []
            // 获取最后一个子元素的dom
            let lastChildEl = allChild[allChild.length - 1]
            // 最后一个子节点的高度
            let lastChildHeight = (lastChildEl?.offsetHeight || 0) + 20
            // 目标滚动高度 = 容器距离顶部高度 + 元素高度 - 元素margin值 - 最后一个节点高度
            let targetScrollTop = offsetTop + offsetHeight - 20 - lastChildHeight
            // 定义单次动画的时间（决定动画的流畅性），完成动画所需要总时间
            let eachTime = 30, duration = 500
            // 需要滚动的距离
            let needScroll = targetScrollTop - containerEl.scrollTop
            // 每个节点滚动距离
            let eachScroll = Math.floor(needScroll * eachTime / duration) || 1
            // 仍需滚动距离
            let distanceScroll = needScroll
            let moveTimer = setInterval(() => {
                // 当目标滚动高度 - 当前滚动高度 <= 每次滚动高度时，执行最后一次滚动，滚动事件结束
                if (distanceScroll <= eachScroll) {
                    // 当即将滚动至底部时，直接设置滚动到最底部，并且清除时间函数
                    containerEl.scrollTop = targetScrollTop
                    distanceScroll = 0
                    clearInterval(moveTimer)
                } else {
                    // 按照每次滚动距离设置元素
                    containerEl.scrollTop = containerEl.scrollTop + eachScroll
                    distanceScroll -= eachScroll
                }
            }, eachTime)
        },
        // 数据请求
        loadData () {
            this.loading = true
            Load_Field_Info({ id: this.id }).then(res => {
                let btnPromissionList = (res?.buttons && res.buttons.length) ? res.buttons : this.defaultBtns
                btnPromissionList = Sort_List(btnPromissionList, 'priority')
                this.setShowForm(btnPromissionList)
            }).finally(() => {
                this.$nextTick(() => {
                    this.loading = false
                })
            })
        }
    },
    async created () {
        this.loading = true
        // 获取关联关系模型表单
        this.module =  await ModuleUtils.editModuleCfg(this.formMdCode)
        this.loadData()
        // 通过事件总线，传递数据到主表
        EventBus.$on('setBtnPowerData', (mountVarName) => {
            // 通知子tab所在vue页面
            EventBus.$emit('setData', this, mountVarName)
            // 通知主表
            EventBus.$emit('getChildTabData', this, mountVarName)
        })
        // 离开页面时清除事件总线
        this.$once('hook:beforeDestroy', () => {
            EventBus.$off('setBtnPowerData', {})
        })
    },
}
</script>

<style lang="scss" scoped>
$partBg: #f5f5f5;
$titleHeight: 24px;
.button-permission_container::v-deep {
    height: 100%;
    position: relative;
    padding: $padding;
    padding-top: 0;
    overflow: scroll;
    .form-title {
        margin-bottom: 10px;
        position: relative;
        font-size: $fontS;
        color: $primary;
        padding: 0 $padding;
        margin: 0;
        // 标题前面的圈圈
        &::before {
            content: '';
            display: inline-block;
            width: 6px;
            height: 6px;
            border-radius: 50%;
            background-color: $primary;
            margin-right: 5px;
            margin-bottom: 2px;
        }
        .bd-button {
            height: $titleHeight;
            line-height: $titleHeight;
            background-color: #FFF;
            margin-left: $space;
            border-color: #0373ce;
        }
    }
    $collapse_header_height: 36px;
    .permission-item_collapse {
        margin-top: $padding;
        margin-bottom: calc(2 * #{$padding});
        border: none;
        border-radius: $borderRadius;
        box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.22);
        .el-collapse-item__header {
            color: $primary;
            background-color: rgba($color: $primary, $alpha: 0.1);
            height: $collapse_header_height;
            line-height: $collapse_header_height;
            .el-collapse-item__arrow {
                font-weight: bold;
            }
        }
    }
    // 关联字段表单内容
    .relation-card-group {
        padding: $padding;
        .button-permission_content {
            height: calc(100% - #{$titleHeight});
            padding-top: 4px;
            display: inline-block;
            width: 100%;
            border-radius: $borderRadius;
            background-color: $partBg;
            transition: all .3s ease-in-out;
            border: 1px solid $lineColor;
            position: relative;
            overflow: hidden;
            margin-bottom: $space;
            .setting-list_box {
                position: absolute;
                right: 5px;
                top: 5px;
                .delete-btn_box {
                    color: $danger;
                    cursor: pointer;
                    margin-left: $space;
                }
                .publish-btn_box {
                    color: $primary;
                    cursor: pointer;
                }
            }
            .bd-form-part {
                background: $partBg;
                .bd-form {
                    padding: 24px $padding 0 $padding;
                    background: $partBg;
                    .bd-form__group {
                        background: $partBg;
                    }
                }
            }
        }
    }
}
</style>