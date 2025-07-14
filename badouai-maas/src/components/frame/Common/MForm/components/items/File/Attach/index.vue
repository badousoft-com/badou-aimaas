// 变更设计图说明： 文件回显默认显示【下载图标】与【删除图标】，图片类的点击小图之后可以弹出预览查看
<template>
    <div 
        class="lh-n m-attach"
        :class="[
            isView?'is-view':'',
            disabled?'is-disabled':'',
            selfClass
        ]">
        <template v-if="showType==='card'">
            <div class="file-choose" :ref="name" >
                <div class="lside-add" v-if="!(!_limitObj.status && limitChooseHide)">
                    <div class="_main p-r">
                        <disabled-board v-if="disabled"></disabled-board>
                        <template v-if="!disabled">
                            <!-- @drop.prevent后面不加事件是因为达到峰值时不需要再添加
                                加上该方法是以防文件拖拽到页面 -->
                            <div
                                v-if="!_limitObj.status"
                                class="tip-shadow"
                                @dragover.prevent="dragover"
                                @dragleave.prevent="dragleave"
                                @drop.prevent
                                @mouseover="computedHeight($event, true)"
                                @mouseleave="delClassShow">
                                <bd-icon name="tip" class="noCursor noEvents"></bd-icon>
                                <div class="noCursor noEvents">{{_limitObj.tip}}</div>
                            </div>
                            <!-- 兼容ie9,10,；使用label for属性指向input file对应id属性，手动触发input file事件 -->
                            <label 
                                v-else 
                                class="tip-shadow pointer chooseFile" 
                                :for="fileId"
                                @dragover.prevent="dragover"
                                @dragleave.prevent="dragleave"
                                @drop.prevent="drop"
                                @mouseover="computedHeight($event, true)"
                                @mouseleave="delClassShow">
                                <span class="noEvents">将文件拖至此处，或点击上传</span>
                            </label>
                        </template>
                        <div class="s-add">
                            <bd-icon name="plus"></bd-icon>
                            <div>上传附件</div>
                        </div>
                        <!-- 上传提示 -->
                        <use-tip
                            :tipList="tipList"
                            :suffix="suffix"
                            :maxSize="_maxSize"
                            :limitObj="_limitObj">
                        </use-tip>
                    </div>
                    <div v-if="_attachTemplateUrl" class="fontS">
                        【若无模版，请先<span @click="downloadTemplate" class="primaryC pointer bold">下载模版</span>】
                        <i v-if="isDownloadTemplate" class="el-icon-loading primaryC"></i>
                    </div>
                </div>
                <div class="rside-show">
                    <div
                        v-if="_showDownloadAll"
                        class="download-all-btn pointer d-ib"
                        title="一键下载"
                        @click="downloadAllOnline">
                        <span class="download-all-text">在线附件【共{{_onlineAttach.length}}份】</span>
                        <bd-icon name="import-fill"></bd-icon>
                    </div>
                    <div class="_main">
                        <!-- transition-group的子项不允许使用index作为key值 -->
                        <!-- ！！！！一旦动画出现异常，请查看这里
                            1. 要求key值不一样
                            2. 直接使用index作为key值是不行的
                            3. 直接使用对象i下的属性i.url也不行，猜想初始都会是字符串'[Object Object]'
                            4. 使用时需要添加对象判断，在对象的情况下再使用对象下属性 i && i.url -->
                        <!-- <transition-group name="list" tag="div" class="itemGroup"> -->
                        <div class="itemGroup">
                            <div 
                                v-for="(i, index) in tempValue"
                                :key="index + (i && i.url)"
                                class="item"
                                :style="{width: _itemWidth}"
                                v-loading="i.uploading">
                                <div class="_main">
                                    <div class="item-info">
                                        <div class="item-status" :class="'is-'+ i.status">
                                            <i class="el-icon-upload-success el-icon-check"></i>
                                        </div>
                                        <div
                                            class="item-sign"
                                            @dblclick="preview(i)">
                                            <attach-sign
                                                :isImg="i.isImg"
                                                :icon="i.icon"
                                                :url="i.url"
                                                :signSize="signSize">
                                            </attach-sign>
                                        </div>
                                        <div
                                            class="item-label text-o-1"
                                            @dblclick="preview(i)">
                                            <div class="text-o-1 fontCL item-label-name" :title="i.name">{{i.name}}</div>
                                            <div class="fontCS">{{i.sizeDesc}}</div>
                                        </div>
                                        <div class="item-handle">
                                            <div v-if="showHandler && hasPreview(i)&& iViewShow" class="item-handle-btn pointer s-download">
                                                <bd-icon name="view" @click="preview(i)"></bd-icon>
                                            </div>
                                            <div v-if="showHandler" class="item-handle-btn pointer s-download">
                                                <bd-icon name="download" @click="download(i)"></bd-icon>
                                            </div>
                                            <div class="item-handle-btn pointer s-remove p-r"  v-if="!disabled">
                                                <bd-icon name="delete" @click="handleDelete(i, index)"></bd-icon>
                                            </div>
                                        </div>
                                    </div>
                                    <bd-progress
                                         v-if="iViewShow"
                                        :value="i.downloadPercent"
                                        :seen="i.downloading">
                                    </bd-progress>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <error-tip v-bind="chooseErrorFileObj"></error-tip>
        </template>
        <!-- 列表信息模式展示 -->
        <template v-else-if="showType==='list-info'">
            <div 
                v-for="(i, index) in tempValue"
                :key="i.url + index"
                class="list-info-item">
                <span class="list-info-item__handler" v-if="showHandler">
                    <bd-icon name="view" @click="preview(i)" class="preview operateC"></bd-icon>
                    <bd-icon name="download" @click="download(i)" class="download operateC"></bd-icon>
                </span>
                <span class="list-info-item__name fontC" @click="preview(i)">{{i.name}}</span>
            </div>
        </template>
        <attach-table
            v-else
            class="lh-n"
            :isView="isView"
            v-model="tempValue"
            :labelFor="fileId"
            :chooseErrorFileObj="chooseErrorFileObj"
            @download="download"
            @handleDelete="handleDelete"
            @deleteItem="deleteItem">
            <error-tip v-bind="chooseErrorFileObj"></error-tip>
        </attach-table>
        <input
            v-if="!disabled"
            type="file" 
            :id="fileId"
            :multiple="multiple"
            class="none"
            @change="changeInput"/>
        <!-- 选取文件错误弹窗 -->
        <!-- <el-dialog class="bd-dialog lh-n"  :visible.sync="fileErrorVisible">
            <div slot="title" class="bd-dialog-title">
                <bd-icon name="pillar-fill" class="pillar fill"></bd-icon>
                收货地址
            </div>
        </el-dialog> -->
        <span v-if="isView && !(tempValue && tempValue.length>0)">
            {{GlobalConst.view.value}}
        </span>
    </div>
</template>
<script>
import AttachTable from './AttachTable'
import AttachSign from './AttachSign'
import UseTip from '../UseTip'
import ErrorTip from '../ErrorTip'
import DisabledBoard from '@/components/frame/Status/DisabledBoard'
import ChooseFile from '../mixin/ChooseFile'
import Progress from './Progress'
import { Download_Item_Animation } from '@/service/attach/index.js'
import { Show_Preview } from '@/components/frame/Preview/index.js'
import GlobalConst from '@/service/global-const'
import { Download } from '@/utils/file'

export default {
    name: 'bd-attach',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    // 混入逻辑 选择文件
    mixins: [ChooseFile],
    components: {
        AttachSign,
        AttachTable,
        [UseTip.name]: UseTip,
        // 文件选择过程错误提示模块
        [ErrorTip.name]: ErrorTip,
        [DisabledBoard.name]: DisabledBoard,
        [Progress.name]: Progress,
    },
    props: {
        // 附件展示形式 card-卡片；list-列表
        showType: {
            type: String,
            default: 'card'
        },
        // 删除的附件数据（基于已上传服务器的附件而言）
        deleteAttach: {
            type: Array,
            default: () => []
        },
        // 是否为查看状态
        isView: {
            type: Boolean,
            default: false
        },
        // 自定义class
        selfClass: {
            type: String,
            default: ''
        },
        // 是否显示操作按钮
        showHandler: {
            type: Boolean,
            default: true
        },
        // 达到上传数限制时，是否隐藏上传按钮入口. true-隐藏 / false-始终显示(默认)
        limitChooseHide: {
            type: false,
            default: false
        },
        // 多附件时是否显示下载全部在线附件的按钮
        showDownloadAll: {
            type: Boolean,
            default: false,
        },

        iViewShow: {
            type: Boolean,
            default: true,
        }
    },
    data () { // 定义页面变量
        return {
            GlobalConst,
            // 图标展示尺寸
            signSize: 35,
            // 文件选取错误dialog展示窗显示状态
            fileErrorVisible: false,
            // 当前打开文件选择器选择的文件数组
            chooseFileList: [],
            // PDF预览模块
            pdfSetting: {
                current: null,
                visible: false
            },
            // 是否正在下载模版
            isDownloadTemplate: false,
        }
    },
    computed: {
        // 页面使用值
        tempValue: {
            get () {
                if (this.value && this.value.constructor === Array) {
                    return this.value
                }
                return []
            },
            set (val) {
                // 数组的set，只有在通过$set追加数据，此时这里才能被响应
                // this.$emit('input', val)
            },
        },
        _itemWidth () {
            return `${1 / this.lineNum * 100}%`
        },
        // 获取所有附件中在线的附件
        _onlineAttach () {
            if (this.tempValue &&
                this.tempValue.constructor === Array) {
                return this.tempValue.filter(i => ~i.url.indexOf(this.BASEURL))
            }
            return []
        },
        // 是否展示一键下载的按钮
        _showDownloadAll () {
            // 单附件时，不需要展示
            if (!this.multiple) return
            // 若没有在线文件，则不需要展示
            if (!(this._onlineAttach.length > 0)) return
            // 剩余就是看配置是否开启
            return this.showDownloadAll
        }
    },
    methods: { // 定义函数
        /**
         * 下载模版
         */
        downloadTemplate () {
            // 设置正在下载模版文件
            this.isDownloadTemplate = true
            Download({
                url: this._attachTemplateUrl,
                // 下载结束后的回调函数
                loadEnd: () => {
                    // 取消下载加载状态
                    this.isDownloadTemplate = false
                }
            }).then(res => {
                if (!res.status) {
                    this.$message.warning(`模版${res.message}`)
                }
            }).catch(_ => {
                // 异常时取消下载加载状态
                this.isLoadingTemplate = false
            })
        },
        /**
         * 点击删除按钮触发，确认是否删除
         * @param {Object} 删除的附件对象
         * @param {Number} 附件所在的下角标
         */
        handleDelete (item, index) {
            // 只有在线图片地址需要提示是否删除
            if (item &&
                item.url &&
                item.url.includes('blob:')) {
                // 执行删除事件
                this.deleteItem(item, index)
                return
            }
            this.$confirm(`此操作将删除该文件${
                    item.name ? '【' + item.name + '】' : '' 
                }, 是否继续?`, '提示', {          
                confirmButtonText: '确定',          
                cancelButtonText: '取消',          
                type: 'warning'
            }).then(() => {
                // 执行删除事件
                this.deleteItem(item, index)
            }).catch(() => {
                // this.$message({
                //     type: 'info',
                //     message: '已取消删除'
                // })         
            })
        },
        /**
         * 删除文件项
         * @param {Object} item 当前文件对象
         * @param {Number} index 当前下角标
         */
        deleteItem (item, index) {
            // 删除该条数据
            this.tempValue.splice(index, 1)
            // 释放附件资源
            this.revokeObjectURL(item.url)
            this.$emit('input', this.tempValue)
            this.deleteAttach.push(item)
            // 抛出change事件给父组件
            this.$nextTick(() => {
                this.$emit('change', this.tempValue,item)
            })
        },
        /**
         * 一键下载所有在线的附件
         */
        downloadAllOnline () {
            // 遍历触发每个在线附件的下载事件
            this._onlineAttach.forEach(i => this.download(i))
        },
        /**
         * 下载文件
         * @param {Object} {name, url} 文件对象的name属性与url属性
         */
        download (item) {
            Download_Item_Animation(item)
        },
        /**
         * 判断是否渲染预览
         * @param {Object} item 文件数据项
         */
        hasPreview (item) {
            if (!item) return
            let { url, suffix } = item
            // 目前远程预览功能只支持远程文件，不支持本地选择的临时文件，所以这里判断附件地址是否含有项目域名，含有则为远程文件
            if (!~url.indexOf(this.BASEURL)) return
            // 其他情况默认都使用远程预览服务
            return true
        },
        /**
         * 判断是否渲染预览
         * @param {Object} item 文件数据项
         */
        preview (item) {
            if (!item) return
            if (!this.hasPreview(item)) return
            let { url, name } = item
            // 执行附件预览
            Show_Preview(url, name)
        },
        /**
         * 释放URL对象（该对象由createObjectURL创建）
         * 当前所有附件的地址url通过createObjectURL生成
         * URL.createObjectURL创建的url必须在适当的时机不需要用到的时候释放掉，节约开支
         * @param {String} objectURL 通过调用 URL.createObjectURL() 方法产生的 URL 对象
         */
        revokeObjectURL (objectURL) {
            window.URL.revokeObjectURL(objectURL)
        },

        /**
         * @description: 选择文件
         * @param {Event} e event
         */
        changeInput (e) {
            const files = e.target.files
            // 添加文件
            this.addFile(files)
            // 操作完将value赋空，避免选择相同文件不会触发change事件
            e.target.value = ''
        },
        /**
         * 计算设置遮罩层展示高度,拖拽上传时会设置遮罩面板高度使得放置范围更加广阔
         *  ，鼠标悬浮时则按照自动高度展示
         * @param {Object} ev 操作目标对象
         * @param {Boolean} isAutoHeight 是否根据内容自动高
         */
        computedHeight (ev, isAutoHeight) {
            // 当前文件是否可上传状态
            if (!this._limitObj.status) {
                // 若不可上传，则不再需要遮罩高度，直接auto
                ev.target.style.height = 'auto'
                return false
            }
            // 定义遮罩高度展示高度值，经过深思熟虑设置140，若不喜欢自行更改
            let shadowHeight = 140
            // 获取模块ref
            let attachRef = this.$refs[this.name]
            // 设置遮罩高度
            ev.target.style.height = !isAutoHeight ? `${shadowHeight}px` : 'auto'
        },
        // 拖拽元素悬浮在标签的事件（鼠标未松开）
        dragover (ev) {
            ev.target.classList.add('show')
            this.computedHeight(ev)
        },
        // 拖拽元素离开标签的事件（鼠标未松开）
        dragleave (ev) {
            ev.target.classList.remove('show')
        },
        // 拖拽元素放置事件
        drop (ev) {
            let files = ev.dataTransfer.files
            // 添加文件
            this.addFile(files)
            // 设置选择文件提醒遮罩层隐藏
            ev.target.classList.remove('show')
        },
        // 鼠标离开元素事件，预防措施，避免异常情况下show类没有被remove时可以通过鼠标滑出删除
        delClassShow (ev) {
            ev.target.classList.remove('show')
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
$addBtnSize: 60px;
$signSize: 35px;
.m-attach {
    .file-choose {
        display: flex;
        line-height: normal;
        .lside-add {
            width: 260px;
            padding-right: $padding;
            display: flex;
            align-items: flex-start; // 与标签、选择后的附件 在顶部对齐
            flex-direction: column;
            & > ._main {
                display: flex;
                color: $fontCS;
                font-size: $fontS;
                border-radius: $borderRadius;
                width: 100%;
                // overflow: hidden;
                .tip-shadow {
                    background: #fff;
                    position: absolute;
                    top: 0;
                    right: 0;
                    bottom: 0;
                    left: 0;
                    opacity: 0;
                    z-index: 1;
                    transition: opacity 0.3s;
                    width: 100%;
                    border: 1px dashed $primary;
                    border-radius: $borderRadius;
                    color: $primary;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    &:hover {
                        opacity: 1;
                    }
                    &.show {
                        opacity: 1;
                    }
                    &.notAllowChoose {
                        opacity: 1;
                    }
                }
                .bd-icon {
                    // 添加图标有点被压缩的感觉，设置下宽度优化下
                    width: 1.4em;
                }
                .s-add {
                    width: $addBtnSize;
                    height: $addBtnSize;
                    background: #F5F7FC;
                    line-height: normal;
                    text-align: center;
                    padding-top: ($addBtnSize - 3 * $fontS) / 2;
                    overflow: hidden;
                    cursor: pointer;
                }
            }
        }
        .rside-show {
            flex: 1;
            // 解决文件增删时出现纵向滚动条模块出现违和动作
            overflow: hidden;
            & > ._main {
                .itemGroup {
                    margin-top: -$padding;
                    margin-right: -$padding;
                    .item {
                        min-width: 190px;
                        float: left;
                        display: flex;
                        padding: $padding $padding 0 0;
                        & > ._main {
                            background: hsla($primaryHue, 57%, 96%, 1);
                            width: 100%;
                            font-size: $fontS;
                            align-items: center;
                            padding: 6px 8px;
                            border-radius: $borderRadius;
                            position: relative;
                            overflow: hidden;
                            transition: background 0.3s;
                            &:hover {
                                background: hsla($primaryHue, 57%, 93%, 1);
                            }
                            .item-info {
                                display: flex;
                                $rotateDeg: 45deg;
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
                                    box-shadow: 0 0 1pc 1px rgba(0, 0, 0, 0.2);
                                    i {
                                        color: #fff;
                                        font-size: $fontS;
                                        margin-top: 11px;
                                        transform: rotate(-$rotateDeg);
                                    }
                                    &.is-success {
                                        display: block;
                                    }
                                }
                                .item-sign {
                                    width: $signSize;
                                    height: $signSize;
                                    flex-shrink: 0;
                                    place-self: flex-end;
                                }
                                .item-label {
                                    flex-grow: 1;
                                    padding: 0 8px;
                                    .item-label-name {
                                        font-size: $font - 1px;
                                    }
                                }
                                .item-handle {
                                    height: 100%;
                                    line-height: $signSize + 5px;
                                    flex-shrink: 0;
                                    display: flex;

                                    $handle-btn-pad: 6px;
                                    .item-handle-btn {
                                        padding: 0px $handle-btn-pad;
                                        color: $fontCS;
                                        &.s-download {
                                            transition: all 0.3s;
                                            &:hover {
                                                color: $primary;
                                                // padding: 0px $handle-btn-pad - 3px;
                                                // font-size: $font;
                                            }
                                        }
                                        &.s-remove {
                                            transition: all 0.3s;
                                            &:hover {
                                                color: $danger;
                                                // padding: 0px $handle-btn-pad - 3px;
                                                // font-size: $font;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    &:after {
                        clear: both;
                    }
                }
            }
            .download-all-btn {
                color: mix($operate, $white, 75%);
                margin-bottom: 2px;
                transition: color .3s;
                font-size: $fontS;
                &:hover {
                    color: $operate;
                }
                .download-all-text {
                    margin-right: -5px;
                }
            }
        }
    }
    &.is-disabled {
        .tip-shadow {
            &:hover {
                opacity: 0 !important;
                cursor: not-allowed;
            }
        }
    }
    // 信息列表展示模式
    .list-info-item {
        margin-bottom: $space;
        &:last-child {
            margin-bottom: 0;
        }
        .list-info-item__name {
            cursor: pointer;
            &:hover {
                color: $primary;
            }
        }
        .list-info-item__handler {
            float: right;
            .bd-icon {
                &:hover {
                    background: rgba($operate, .3);
                    border-radius: 50%;
                }
            }
        }
    }
    // 查看模块
    &.is-view {
        .lside-add {
            display: none;
        }
        .rside-show {
            & > ._main {
                .itemGroup {
                    .item {
                        max-width: 240px;
                        .item-status {
                            display: none !important;
                        }
                        .item-handle {
                            .s-remove {
                                display: none;
                            }
                        }
                    }
                }
            }
        }
    }
}


// slide-fade动画------------start
.list-enter-active, .list-leave-active {
    transition: all .6s;
}
.list-enter, .list-leave-to {
    opacity: 0;
    transform: translateY(20px);
}
// slide-fade动画------------end
</style>