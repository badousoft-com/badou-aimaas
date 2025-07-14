<!--
 * @Description: 块设置弹窗 - 分享设置
-->
<template>
    <div class="block-design-share">
        <module-form
            noTitle
            ref="moduleForm"
            v-bind="module"
            :detailId="field.id"
            :fieldList.sync="_fieldList"
            class="right-form__main">
        </module-form>
        <div v-show="isShare" class="marT--20">
            <m-form
                ref="formShare"
                id="formShare"
                title=""
                labelWidth="110px"
                :columnNum="3"
                :dataList="formShareList">
                <template slot="choiceShareBtn">
                    <bd-button
                        v-for="b in btnList"
                        :key="b.id"
                        v-bind="b"
                        @click="exeMethod(b)">
                    </bd-button>
                </template>
            </m-form>
            <!-- 分享块：显示要分享的对象  -->
            <template v-if="shareType === 'block'">
                <div
                    v-if="shareBlockValue.length"
                    class="share-object-box marT--10"
                    :style="{'--label-width--': '110px'}">
                    <label class="share-object-label">分享对象</label>
                    <div class="d-ib share-block-tags marL-10">
                        <el-tag
                            v-for="(t, t_index) in shareBlockValue"
                            :key="t.id"
                            class="tags-item"
                            type="success"
                            closable
                            @close="delBlockShare(t_index)">
                            {{t.name}}
                        </el-tag>
                    </div>
                </div>
            </template>
            <!-- 分享内容：显示分享的内容+对应分享卡片（以内容为单位） -->
            <ul
                v-else
                class="waterfall content-list-box"
                :style="{ '--col-num--': contentColNum }">
                <li
                    class="content-item"
                    v-for="(c, c_index) in contentList"
                    :key="c_index"
                    @click="switchContentCheck(c, c_index)">
                    <div class="content-title">{{c.name}}</div>
                    <div class="share-tags" v-loading="c.loading">
                        <div v-if="!c.shareList || !c.shareList.length" class="textC marT-10">暂无分享人</div>
                        <template v-else>
                            <el-tag
                                v-for="(t, t_index) in c.shareList"
                                :key="t_index"
                                class="tags-item marT-10 marL-5"
                                type="success"
                                closable
                                @close.stop="delContentShare(t, t_index, c, c_index)">
                                {{t.name}}
                            </el-tag>
                            <!-- <el-tag class="tags-item marT-10" type="info" closable>标签三</el-tag>
                            <el-tag class="tags-item marT-10" type="warning" closable>标签四</el-tag>
                            <el-tag class="tags-item marT-10" type="danger" closable>标签五</el-tag> -->
                        </template>
                    </div>
                    <div class="item-status" :class="{'is-checked': c.checked}">
                        <i class="el-icon-upload-success el-icon-check"></i>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
import ModuleForm from '@/components/frame/Module/ModuleForm/index.vue'
import MForm from '@/components/frame/Common/MForm/index.vue'
import { Load_Share_Content, Get_Content_Share, Get_Block_Share } from '@/api/frame/panel/designer'
const rangeOptions = [
    { id: 'all', text: '全部' },
    { id: '1', text: '部门', code: '2-0-0-2' },
    { id: '2', text: '角色', code: '1-0-1-1' },
    { id: '3', text: '人员', code: '2-0-20-2' }
]
const shareTypeOptions = [
    { id: 'block', text: '块' },
    { id: 'content', text: '内容' }
]
export default {
    name: 'block-setting-share',
    components: {
        [ModuleForm.name]: ModuleForm,
        MForm,
    },
    inheritAttrs: false,
    props: {
        fieldList: {
            type: Array,
            default: () => []
        },
        module: {
            type: Object,
            default: () => { }
        },
        elseAttrs: {
            type: Object,
            default: () => { }
        },
        code: {
            type: String,
            default: ''
        },
        field: {
            type: Object,
            default: () => { }
        }
    },
    computed: {
        _fieldList: {
            get () {
                return this.fieldList
            },
            set (val) {
                this.$emit('update:fieldList', val)
            }
        },
        shareRange () {
            let result = ''
            let shareRange = this.formShareList.find(o => o.name === 'shareRange')
            if (shareRange) {
                result = shareRange.value || ''
            }
            return result
        },
        isShare () {
            let result = false
            let obj = this.fieldList.find(o => o.name === 'isShare')
            if (obj) {
                result = obj.value && obj.value !== '0'
            }
            return result
        },
        // 分享类型：内容content或块block
        shareType () {
            let result = 'block'
            let obj = this.formShareList.find(o => o.name === 'shareType')
            if (obj) {
                result = obj.value || 'block'
            }
            return result
        },
        btnList () {
            let result = []
            let choiceObject = { id: 'choiceShare', name: '添加分享对象', icon: 'my-fill', loading: false, type: 'primary', click: 'choiceShare' }
            if (this.shareRange && this.shareRange !== 'all') {
                result.push(choiceObject)
            }
            return result
        }
    },
    data () {
        return {
            formShareList: [
                { type: 'radio', name: 'shareType', label: '分享类型', value: 'block', options: shareTypeOptions, columnPer: 24 },
                { type: 'select', label: '分享范围', name: 'shareRange', value: '', options: rangeOptions },
                { type: 'slot', status: 1, label: '', name: 'choiceShareBtn' }
            ],
            // 地址本被选中的数据（经过去重）
            shareBlockValue: [],
            // 内容列表瀑流列数
            contentColNum: 4,
            // 内容列表
            contentList: [
                { id: '1', name: '内容第一块' },
                { id: '2', name: '内容第2块' },
                { id: '3', name: '内容第3块' },
                { id: '4', name: '内容第4块' },
            ],
            // 是否已经请求过内容分享人
            isLoadedContent: false,
        }
    },
    methods: {
        exeMethod (btn) {
            if (typeof btn.click === 'function') {
                btn.click(btn)
            } else if (typeof this[btn.click] === 'function') {
                this[btn.click](btn)
            }
        },
        // 数组去重
        arrayRemoval (data, value) {
            let temp_arr = data.filter(item => {
                let filterIndex = value.findIndex(o => o.id === item.id)
                if (filterIndex === -1) {
                    return true
                } else {
                    return false
                }
            })
            return value.concat(temp_arr)
        },
        // 选择分享人
        choiceShare (btn) {
            let choiceRange = rangeOptions.find(o => o.id === this.shareRange)
            let addressType = choiceRange?.code || ''
            if (!addressType) {
                this.$message.warning('缺少地址本参数')
                return
            }
            if (this.shareType === 'content') {
                let isFrag = this.contentList.some(o => o.checked)
                if (!isFrag) {
                    this.$message.warning('请选择点击下列卡片选择分享内容')
                    return
                }
            }
            // this.addressType有值时值格式为：2-1-20-2
            this.addressBook({
                // 传入标题
                title: choiceRange.text ? `${choiceRange.text}选择` : `地址本选择`,
                addressTypes: addressType, // 新添加这个属性，从这个进行走通，尽量不影响原有属性
            }).then(res => {
                // res数据格式:[{id:'222', name:'负责人', value: '20!@负责人!@222'}]
                if (this.shareType === 'block') {
                    this.shareBlockValue = this.arrayRemoval(res, this.shareBlockValue)
                } else {
                    this.contentList.forEach((item, index) => {
                        if (item.checked) {
                            let temp_item = item
                            // 如果有选择地址本的话，将选择全部的选项给去掉
                            item.shareList = (item.shareList || []).filter(o => o.id !== '*')
                            temp_item.shareList = this.arrayRemoval(res, item.shareList)
                            this.$set(this.contentList, index, temp_item)
                        }
                    })
                }
            }).catch(err => {
                this.$message.error('请求失败，请联系管理员')
            })
        },
        // 删除块分享对象
        delBlockShare (index) {
            let value = this.shareBlockValue?.[index]?.value || ''
            // 如果删除的是选中值全部，则要将分享单位的值重置
            if (value === '*') {
                let obj = this.formShareList.find(o => o.name === 'shareRange')
                if (obj) {
                    this.$set(obj, 'value', '')
                }
            }
            this.shareBlockValue.splice(index, 1)
        },
        // 删除内容分享对象
        delContentShare (sharePerson, personIndex, content, contentIndex) {
            let shareList = this.contentList[contentIndex].shareList
            shareList.splice(personIndex, 1)
            this.$set(this.contentList[contentIndex], 'shareList', shareList)
            // 视图不更新，没办法了
            this.$forceUpdate()
        },
        // 更改内容的选中状态
        switchContentCheck (content, index) {
            this.$set(content, 'checked', !Boolean(content.checked))
            if (this.shareRange === 'all' && this.shareType === 'content' && content.checked) {
                content.shareList = [{ id: '*', value: '*', name: '全部人员' }]
            }
        },
        // 基础设置中的内容类型发生更改时执行refresh的方法
        refresh () {
            let shareTypeIndex = this.formShareList.findIndex(o => o.name === 'shareType')
            if (shareTypeIndex !== -1) {
                let shareType = this.formShareList[shareTypeIndex]
                // 只有内容此块的内容类型为多内容时才可以分享内容
                if (this.elseAttrs.contentType !== '1') {
                    shareType.value = 'block'
                    shareType.disabled = true
                } else {
                    shareType.disabled = false
                }
                this.$set(this.formShareList, shareTypeIndex, shareType)
            }
        },
        // 获取分享信息
        getShareInfo () {
            return {
                shareType: this.shareType,
                shareBlock: this.shareBlockValue || [],
                shareContent: this.contentList || []
            }
        },
        // 表单校验
        validateForm () {
            return new Promise((resolve, reject) => {
                this.$refs.moduleForm.$refs.edit.validateForm().then((res) => {
                    let result = Object.assign(res, { '__shareInfo__': this.getShareInfo() })
                    resolve(result)
                }).catch(() => {
                    this._fieldList.length ? reject(this.code) : resolve({})
                })
            })
        },
        // 请求可分享的内容
        getContentList () {
            let blockId = this.field.id
            Load_Share_Content({ blockId }).then(res => {
                if (res && res.hasOk) {
                    this.$set(this, 'contentList', res.bean || [])
                } else {
                    this.$message.error(res.tip || res.message || '请求分享内容列表失败')
                }
            })
        },
        // 获取块的分享对象（回显）
        getBlockShare () {
            let blockId = this.field.id
            Get_Block_Share({ blockId }).then(res => {
                if (res && res.length) {
                    this.$set(this, 'shareBlockValue', res || [])
                }
            })
        },
        // 获取已分享内容对象
        getContentShare (contentId) {
            return new Promise((resolve, reject) => {
                Get_Content_Share({ contentId }).then(res => {
                    if (res && res.length) {
                        resolve(res || [])
                    } else {
                        resolve([])
                    }
                }).catch(err => {
                    resolve([])
                })
            })
        },
        async newContentShare (content, index) {
            content.loading = true
            content.shareList = await this.getContentShare(content.id)
            content.loading = false
            this.$set(this.contentList, index, content)
        }
    },
    mounted () {
        // 获取可分享的内容列表
        this.getContentList()
        // 已分享块列表
        this.getBlockShare()
        this.refresh()
    },
    watch: {
        shareRange (val) {
            if (val === 'all') {
                // 全选时的value
                let value = [{ id: '*', value: '*', name: '全部人员' }]
                if (this.shareType === 'block') {
                    this.$set(this, 'shareBlockValue', value)
                } else {
                    this.contentList.forEach((item, index) => {
                        if (item.checked) {
                            let temp_item = item
                            temp_item.shareList = value
                            this.$set(this.contentList, index, temp_item)
                        }
                    })
                }
            }
        },
        shareType (val) {
            // 第一次切换成内容的时候，根据内容id请求内容分享对象
            if (val === 'content' && !this.isLoadedContent) {
                this.isLoadedContent = true
                this.contentList.forEach((item, index) => {
                    this.newContentShare(item, index)
                })
            }
        }
    }
}
</script>

<style lang="scss" scoped>
$rotateDeg: 45deg;
.content-list-box {
    width: 100%;
    padding: 0 20px;
    margin-top: -10px;
    .content-item {
        border: 1px solid #dfd9d9;
        border-radius: $borderRadius;
        margin-bottom: 10px;
        position: relative;
        overflow: hidden;
        cursor: pointer;
        -moz-page-break-inside: avoid;
        -webkit-column-break-inside: avoid;
        break-inside: avoid;
        .content-title {
            padding: 5px 10px;
            border-bottom: 1px solid #f5f5f5;
            background-color: rgba($color: $primary, $alpha: 0.7);
            color: #fff;
        }
        .share-tags {
            padding: 10px 0;
            padding-top: 0;
        }
        // 内容卡片被选中标识
        .item-status {
            display: none;
            position: absolute;
            right: -15px;
            top: -6px;
            width: 40px;
            height: 24px;
            background: $success;
            text-align: center;
            transform: rotate($rotateDeg);
            box-shadow: 0 0 2px 1px rgba(0, 0, 0, 0.2);
            i {
                color: #fff;
                font-size: $fontS;
                margin-top: 11px;
                transform: rotate(-$rotateDeg);
            }
            &.is-checked {
                display: block;
            }
        }
    }
}
.share-object-box {
    .share-object-label {
        display: inline-block;
        width: var(--label-width--);
        font-size: 14px;
        text-align: right;
    }
    .tags-item {
        margin-left: 5px;
    }
}
.marL-5 {
    margin-left: 5px;
}
.marT--10 {
    margin-top: -10px;
}
.marT--20 {
    margin-top: -20px;
}
.waterfall {
    -moz-column-count: var(--col-num--); /* Firefox */
    -webkit-column-count: var(--col-num--); /* Safari 和 Chrome */
    column-count: var(--col-num--);
    -moz-column-gap: 20px;
    -webkit-column-gap: 20px;
    column-gap: 1em;
}
ul, li {
    margin: 0;
    padding: 0;
    list-style: none;
}
</style>