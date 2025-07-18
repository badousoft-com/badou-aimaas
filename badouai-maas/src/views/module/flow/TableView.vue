// 下一环节：处理人审核页面
<template>
    <div class="bd-page-edit">
        <fix-bottom-page
            class="p-left">
            <!-- 核心内容区域 start-->
            <module-edit-code
                v-if="isShowForm"
                ref="moduleEditCode"
                :mdCode="mdCode"
                :detailId="detailId"
                :defaultParamsObj="query"
                :defaultBtnList="[]"
                :isView="isView"
                viewType="table"
                :customSetting="{ buttons: [] }"
                @beforeRender="beforeRender"
                @beforeModuleRender="beforeModuleRender">
            </module-edit-code>
            <flow-opinion
                v-if="isSubmitOpinion"
                ref="flowOpinion"
                class="mar-t">
            </flow-opinion>
            <flow-trace :boId="_flowItem.boId" :flowId="flowId" class="mar-t"></flow-trace>
            <!-- 核心内容区域 end-->
            <template v-slot:footer>
                <flow-router-btn
                    :flowId="flowId"
                    :worklistId="worklistId"
                    :workItem="_flowItem"
                    :pageScope="scope"
                    :showRouterBtn="verifyStatus"
                    :showOptinionBtn="showOptinionBtn"
                    :beforeApproveRender="beforeApproveRender"
                    :btnList="flowButtons"
                    :hasSaveBtn="false"
                    ref="flowRouteBtnRef">
                </flow-router-btn>
            </template>
        </fix-bottom-page>
        <div
            v-if="attachList.length > 0"
            class="p-right"
            :class="{'is-fold': isFold}">
            <!-- <m-card title="附件查看"></m-card> -->
            <!-- {{attachList}} -->
            <div class="rside-title pointer">
                <bd-icon
                    :name="!isFold ? 'unfoldHor' : 'foldHor'"
                    @click="toggleFold()"
                    class="fold-btn">
                </bd-icon>
                <div v-show="isShow" class="rside-title-text">{{attachViewTitle}}</div>
                <div class="rside-title-text_fold" @click="toggleFold(false)">
                    <span
                        v-for="(i, i_index) in attachViewTitle"
                        :key="i_index"
                        class="rside-title-text-item">
                        {{i}}
                    </span>
                </div>
            </div>
            <div class="module-form-box" @click="toggleFold(false)">
                <m-form
                    v-show="isShow"
                    class="bd-module-form"
                    title=""
                    :columnNum="1"
                    :ignoreColumnPer="true"
                    :dataList.sync="attachList"
                    :isView="isView">
                </m-form>
            </div>
        </div>
    </div>
</template>
<script>
import WorkVerify from '@/views/module/flow/part/WorkVerify'
import MCard from '@/components/frame/Common/MCard'
import { EventBus } from '@/service/event-bus'
import MForm from '@/components/frame/Common/MForm/index'
import globalStyle from '@/styles/theme.scss'

export default {
    mixins: [WorkVerify],
    components: {
        [MCard.name]: MCard,
        MForm
    },
    computed: {
        // 获取左侧表单模块宽度
        _leftWidth () {
            if (this.attachList.length > 0) {
                return '70%'
            }
            return '100%'
        },
        // 获取右侧附件模块宽度
        _rightWidth () {
            return `calc(${100 - parseInt(this._leftWidth)}% - ${globalStyle.space})`
        }
    },
    data () {
        return {
            // 定义附件列表信息
            attachList: [],
            // 附件列表是否收缩
            isFold: false,
            // 附件表单是否可见
            isShow: true,
            attachViewTitle: '附件查看',
            // 是否查看状态
            isView: true
        }
    },
    methods: {
        // 收缩切换
        toggleFold (value) {
            this.isFold = arguments.length ? value : !this.isFold
            let t = 0
            if (!this.isFold) { // 如果是切换成展开状态
                t = 300
            }
            setTimeout(() => {
                this.isShow = !this.isFold
            }, t)
        },
        beforeRender (fieldList, scope) {
            // 获取所有的附件字段数据(判断规则：有附件字段，并且字段下得有值)
            let attachList = fieldList.filter(i => i.type === 'attach' && i.value && i.value.length > 0)
            // 如果符合条件的附件不存在，则直接return，页面上也不需要渲染右侧专门的附件查看模块
            if (attachList.length === 0) return
            // 更新表单项，去除所有的附件（不在左侧进行展示）
            scope._fieldList = fieldList.filter(i => i.type !== 'attach')
            attachList.forEach(i => {
                i.groupName = i.label || '附件'
                // 隐藏标签
                i.hideLabel = true
                // 展示类型使用列表信息模式
                i.showType = 'list-info'
                // 模拟长文本
                // i.value.forEach(j => {
                //     j.name = j.name + '这是一段超级长的文本你知道吗'
                // })
            })
            // 发布事件，通知更新给页面附件字段
            EventBus.$emit('updateAttachList', attachList)
        }
    },
    mounted () {
        // 事件订阅，等待有人发布时更新字段attachList
        EventBus.$on('updateAttachList', (data) => {
            this.attachList = data
        })
        // 离开页面时清除事件总线
        this.$once('hook:beforeDestroy', () => {
            EventBus.$off('updateAttachList', {})
        })
    }
}
</script>
<style scoped lang="scss">
.bd-page-edit {
    display: flex;
    font-size: 0;
    .p-left {
        font-size: $font;
        display: inline-block;
        flex: 1;
        min-width: 70%;
    }
    .p-right {
        width: 300px;
        overflow: hidden;
        font-size: $font;
        border-radius: $borderRadius;
        margin-left: $space;
        height: calc(100% - 44px);
        display: inline-block;
        background: $contentInBg;
        vertical-align: top;
        transition-timing-function: cubic-bezier(0.98, 0, 0.22, 0.98);
        transition-duration: 0.4s;
        transition-property: width;
        .rside-title {
            font-weight: 600;
            font-size: $fontL;
            padding: 7px;
            border-bottom: 1px solid #eee;
            position: relative;
            .fold-btn {
                margin-right: 5px;
                color: $primary;
                vertical-align: middle;
            }
            .rside-title-text {
                position: absolute;
                // display: inline-block;
                left: 42px;
                top: 10px;
                flex-basis: 200px;
                transition-timing-function: cubic-bezier(0.98, 0, 0.22, 0.98);
                transition-duration: 0.5s;
                transition-property: all;
            }
            .rside-title-text_fold {
                width: 100%;
                position: absolute;
                top: 0px;
                left: 0px;
                .rside-title-text-item {
                    position: absolute;
                    z-index: 1;
                    text-align: center;
                    writing-mode: horizontal-tb;
                    transition: all 0.4s;
                    right: -30px;
                    @for $i from 1 through 10 {
                        &:nth-child(#{$i}) {
                            top: 40px + 10px + ($i - 1) * 26;
                        }
                    }
                }
            }
        }
        &.is-fold {
            width: 40px;
            .rside-title-text_fold {
                .rside-title-text-item {
                    right: 10px;
                    @for $i from 1 through 10 {
                        &:nth-child(#{$i}) {
                            transition-delay: $i * 0.12s;
                        }
                    }
                }
            }
        }
        .module-form-box {
            height: calc(100% - #{$formTitleHeight});
            .bd-module-form {
                height: 100%;
            }
        }

    }
}

</style>