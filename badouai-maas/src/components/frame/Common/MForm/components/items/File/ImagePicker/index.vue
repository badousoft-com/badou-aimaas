// ***************图片基础组件***************
// 使用el-upload(选择图片展示) + 
//     v-viewer(预览) 完成图片组件
// v-viewer: 指导文章说明：https://mirari.cc/v-viewer/

// 组件的进入数据：1-String("imgSrc1, imgSrc2")  2-Array([{url:imgSrc1},{url:imgSrc2}])
// 组件的出口数据：fileList: Array([{url:imgSrc1},{url:imgSrc2}])

// TODO: 要求当前组件必须传入的值是复杂数据类型如[]，才能在选择图片的时候页面自动更新，传入null时选择图片页面将不展示
<template>
    <div
        class="m-image p-r"
        :class="[
            disabled?'is-disabled':'',
            !multiple?'single':'',
            selfClass
        ]" >
        <template v-if="!isView">
            <el-upload
                :class="{'hideAddBtn': hideAddBtn || !allowSingleAddShow }"
                class="m-image-upload"
                :ref="name"
                :name="fileId"
                :list-type="dataType"
                action=''
                :disabled="disabled"
                :multiple="multiple"
                :limit="_limit"
                :accept="GlobalConst.imgTypeList.join(GlobalConst.separator)"
                :file-list="tempValue"
                :auto-upload="false"
                :before-remove="beforeRemove"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :on-change="handleChange"
                :on-exceed="handleExceed">
                <template>
                    <i class="el-icon-plus"></i>
                    <!-- 上传提示 -->
                    <use-tip
                        :tipList="tipList"
                        :ratio="_ratio"
                        :suffix="suffix"
                        :maxSize="_maxSize"
                        :limitObj="_limitObj">
                    </use-tip>
                </template>
            </el-upload>
            <error-tip v-bind="chooseErrorFileObj"></error-tip>
        </template>
        <!-- 查看页面无数据时 -->
        <template v-else>
            <preview
                v-if="tempValue && tempValue.length > 0"
                ref="preview"
                :value="tempValue">
            </preview>
            <span v-else>{{noViewData}}</span>
        </template>
    </div>
</template>
<script>
import GlobalConst from '@/service/global-const'
import UseTip from '../UseTip'
import ErrorTip from '../ErrorTip'
import Preview from './Preview'
import ChooseFile from '../mixin/ChooseFile'
import { Debounce } from '@/utils/index.js'
import { Show_Preview } from '@/components/frame/Preview/index.js'
export default {
    name: 'bd-image-picker',
    // 不将传入属性展示在标签属性上，避免错误地解读为属性
    inheritAttrs: false,
    components: {
        [UseTip.name]: UseTip,
        ErrorTip,
        Preview
    },
    mixins: [ChooseFile],
    props: {
        // 字段名
        name: {
            type: String,
        },
        value: {
            type: [Array, String],
            default: () => []
        },
        deleteAttach: {
            type: Array,
            default: () => []
        },
        // 支持的文件类型，默认为图片类型
        suffix: {
            type: String,
            // 这里放出来会导致展示可上传文件类型时显示数据过多
            // default: GlobalConst.imgTypeList.join(',')
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
        }
    },
    data () { // 定义页面变量
        return {
            GlobalConst: GlobalConst,
            dataType: 'picture-card',
            // 延时状态位：单选状态下当删除数据时会先出现添加按钮，图在消失；添加延时让图消失动画结束再出现添加按钮
            allowSingleAddShow: true,
            // 延时时间
            singleAddShowDelay: 100,
            // 查看无数据时
            noViewData: GlobalConst.view.value,
            // 当前新增时选择的文件
            currentAddFile: [],
            // 是否允许触发删除逻辑
            isAllowTriggerDel: true,
            // 选择文件触发事件
            handleChangeFun: null,
        }
    },
    computed: {
        // 单项数据流原因，不可直接修改值，需要用临时值做过度使用
        tempValue: {
            get () {
                if (this.value && !(this.value instanceof Array)) {
                    return this.value.split(GlobalConst.form.valueSeparator).map(i => {
                        return {
                            url: i
                        }
                    })
                }
                return this.value || []
            },
            set (val) {
                // TODO：这里按道理是不会触发的，值的变更是由ChooseFile组件去做的
                this.$emit('input', val)
            }
        },
        // 隐藏添加按钮的状态
        hideAddBtn () {
            // 两种状态需要隐藏添加按钮：
            //     1. 单选状态且已选数目达到1
            //     2. 多选状态且已选数目等于限制峰值数
            return (!this.multiple && this.tempValue.length >= 1) ||
            (this.multiple && this._limit && this.tempValue.length === this._limit) || false
        },
    },
    methods: { // 定义函数
        /**
         * 图片删除事件，更新父组件数据
         * @param [Object] file:当前删除图片对象
         * @param [Array] fileList:图片数组对象
         */
        handleRemove (file, fileList) {
            // 判断当前是否允许执行删除逻辑
            if (!this.isAllowTriggerDel) return
            // 单选状态下，删除唯一一条数据时延迟设置显示状态，避免展示bug【查看allowSingleAddShow字段定义说明】 --start
            if (!this.multiple) {
                this.allowSingleAddShow = false
                setTimeout(() => {
                    this.allowSingleAddShow = true
                }, this.singleAddShowDelay)
            }
            // 单选状态下，删除唯一一条数据时延迟设置显示状态，避免展示bug【查看allowSingleAddShow字段定义说明】 --end
            this.$emit('input', fileList)
            this.deleteAttach.push(file)
            // 抛出change事件给父组件
            this.$nextTick(() => {
                this.$emit('change', this.tempValue)
            })
        },
        /**
         * 图片预览事件
         * @param [Object] file: 操作预览的图片对象
         */
        handlePreview (file) {
            // 找到匹配项的下角标
            let index = this.value.findIndex(i => i.url === file.url)
            // 展示预览区域
            Show_Preview(this.tempValue, index)
        },
        handleExceed (files, fileList) {
            // 执行文件选择超出函数
            this.isOverLimit(files)
        },
        // 移除文件触发
        beforeRemove (file, fileList) {
            // 判断当前图片是否为本地所选，此类图片删除时不需要弹窗提醒
            // 只有在线图片地址需要提示是否删除
            if (file &&
                file.url &&
                file.url.includes('blob:')) {
                return true
            }
            return this.$confirm(`此操作将删除该文件${ file.name || '' }, 是否继续?`, '提示', {          
                confirmButtonText: '确定',          
                cancelButtonText: '取消',          
                type: 'warning'
            })
        },
        /**
         * 添加文件、上传文件触发
         *     【注意：删除不触发；这里没有上传的场景，所以只有添加文件时会触发当前该函数】
         *     【选择几个文件，就会触发这里的函数几次】
         * @param [file] Object:当前操作文件对象
         * @param [fileList] Array: 已有的文件列表（包含已提交 + 新增，两者有状态可标识）
         */
        handleChange (file, fileList) {
            // 这里的函数是选择图片变更后才触发的，由于图片要先判断符不符合再展示
            // 所以这里优先先删除已选照片，后面符合条件再展示
            // el-upload组件选择文件时是直接使用选择文件添加进列表，没有做任何判断
            // 这里必须将选择文件优先删除掉，等待条件符合才能添加

            // 设置不允许执行删除逻辑，即将准备删除（由于后续逻辑的handleRemove会触发删除逻辑，所里这里控制下）
            this.isAllowTriggerDel = false
            // 获取附件Ref对象
            let fileRef = this.$refs[this.name]
            // 删除已选的数据，使页面清除已选，接下来进行判断再设置显示哪些
            fileRef.handleRemove(file)
            // 还原配置，允许删除事件在接下来被调用
            this.isAllowTriggerDel = true
            // 更新当前的新选的文件列表，选择多个文件时handleChange会触发多次(组件内部是这样)，所以这里会进入多次，所以需要更新currentAddFile变量
            this.currentAddFile.push(file)
            // 执行新增文件的change事件
            this.handleChangeFun()
        },
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        // 更新值（多选文件）变更change事件，使用防抖避免多次触发
        // 获取频繁触发的函数体，等待调用
        this.handleChangeFun = Debounce(() => {
            if (!this.multiple) {
                this.allowSingleAddShow = false
            }
            // 添加前附件数据数
            let beforeAddLen = this.tempValue.length
            // 添加文件
            this.addFile(this.currentAddFile)
            // 判断是否有添加新文件：之前隐藏的添加按钮重新显示
            this.allowSingleAddShow = beforeAddLen  === this.tempValue.length
            // 清空当前已选文件变量，为下次选择新的文件时做准备
            this.currentAddFile = []
        }, 100)
    }
}
</script>
<style lang='scss' scoped>
.m-image::v-deep {
    $upload_pic_size: 100px;
    line-height: 1;
    // 单选设置不换行，当区域过小时，按钮会换行，删除动画会展示异常
    &.single {
        .m-image-upload {
            display: flex;
            .el-upload-list--picture-card {
                flex-shrink: 0;
            }
            .el-upload--picture-card {
                flex-shrink: 0;
            }
        }
    }
    .el-upload--picture-card {
        width: $upload_pic_size;
        height: $upload_pic_size;
        line-height: $upload_pic_size + 2px;
        margin-bottom: 9.5px;
        position: relative;
        margin-right: 130px;
        i {
            font-size: 20px;
        }
        .s-tip {
            height: 100%;
            position: absolute;
            top: 0;
            left: $upload_pic_size;
            text-align: left;
            width: max-content;
            pointer-events: none;
        }
    }
    .el-upload-list--picture-card {
        .el-upload-list__item {
            width: $upload_pic_size;
            height: $upload_pic_size;
            border-radius: $borderRadius;
            // outline干掉，避免动效时生成outline线条，丑
            outline: none !important;
            padding: 0 2px;
            // 组件自带了提示【按delete可以删除】，这里隐藏掉
            .el-icon-close-tip {
                display: none !important;
            }
        }
        .el-upload-list__item-thumbnail {
            // 设置图片保留原有比例，可能会缩放
            object-fit: contain;
        }
        .el-upload-list__item-status-label {
            background: $success;
        }
    }
    // 当含有类名hideAddBtn时隐藏添加按钮
    .hideAddBtn {
        .el-upload--picture-card {
            display: none;
        }
    }
    // 选择图片插入时，页面总会闪动一下，原因是elementUI组件为图片添加了一个底部margin
    // 会导致抖动，设置.el-upload--picture-card margin-bottom:9.5px，避免抖动（为什么9.5,我肉眼看到了差距是9.5px）
    // 同时此处样式为父级添加一个底部margin负值，避免影响到下一模块的距离
    .m-image-upload {
        margin-bottom: -8px;
    }
    &.is-disabled {
        .el-upload--picture-card {
            cursor: not-allowed;
            background: #F5F7FA;
            border-color:#c0ccda;
        }
        .el-upload-list--picture-card {
            .el-upload-list__item {
                cursor: not-allowed;
                .el-upload-list__item-status-label {
                    display: block;
                }
                .el-upload-list__item-name {
                    cursor: not-allowed;
                }
                .el-upload-list__item-actions {
                    cursor: not-allowed;
                    opacity: 1 !important;
                    background: $formDisabledBg !important;
                    .el-upload-list__item-preview,
                    .el-upload-list__item-delete {
                        cursor: not-allowed;
                        display: none;
                    }
                }
            }
        }
    }
}
</style>