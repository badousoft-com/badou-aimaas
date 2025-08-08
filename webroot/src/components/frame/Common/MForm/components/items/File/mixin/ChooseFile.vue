<script>
import { Get_File_Size, Bytes_To_Size, Compare_A_large_B } from '@/utils/file'
import { Is_Img, GetIcon } from '@/service/attach/index.js'
import { Remove_Space, Get_UUID, Has_Value } from '@/utils/index'
import { Upload_File, Get_Attach_Url } from '@/api/frame/attach'
import { Get_Full_Url } from '@/service/url'
import GlobalConst from '@/service/global-const'

// 纯原生input取文件，file对象转
class FileUtil {
    constructor (File) {
        // file对象,构造与图片组件一致的属性，raw指向file对象
        this.raw = File
        // 获取对象下相关属性
        let { name, size, type, status, url } = File
        // 文件名称
        this.name = name
        // 文件大小
        this.size = size
        // 文件大小描述（单位转换版）
        this.sizeDesc = Bytes_To_Size(size)
        // 文件类型
        this.type = type
        // 文件状态 ready-准备提交服务器；success-已提交服务器（回显）
        this.status = status || 'ready'
        // 文件后缀(不含.符号)
        this.suffix = name.substring(name.lastIndexOf('.') + 1)
        // 文件地址
        this.url = window.URL.createObjectURL(File)
        // 是否为图片
        this.isImg = Is_Img(this.suffix) || false
        // 文件使用图标
        this.icon = GetIcon(this.suffix)
        // 上传中状态
        this.uploading = false
        // 是否下载状态
        this.downloading = false
        // 下载进度
        this.downloadPercent = 0
    }
}

// el-upload选择后的文件对象(组件已作一层处理)处理
class ElUploadUtil {
    constructor (File) {
        // File对象下已有属性：{
        //     name: 'xxx.doc',
        //     raw: File,
        //     size: 10429,
        //     status: 'ready',
        //     url: 'blob:http://xxx'
        // }
        let { name, raw, size, status, url } = File
        // 文件file对象
        this.raw = raw
        // 文件名称
        this.name = name
        // 文件大小
        this.size = size
        // 文件类型(这里的type要从raw中取出)
        this.type = raw.type
        // 文件状态 ready-准备提交服务器；success-已提交服务器（回显）
        this.status = status
        // 文件后缀(不含.符号)
        this.suffix = name.substring(name.lastIndexOf('.') + 1)
        // 文件地址
        this.url = url
        // 是否为图片
        this.isImg = Is_Img(this.suffix) || false
        // 文件使用图标
        this.icon = GetIcon(this.suffix)
        // 上传中状态
        this.uploading = false
        // 是否下载状态
        this.downloading = false
        // 下载进度
        this.downloadPercent = 0
    }
}

export default {
    props: {
        // 字段名
        name: {
            type: String
        },
        // 值数据
        // 期望数据格式：[{url:'文件地址'}]
        value: {
            type: Array,
            default: () => []
        },
        disabled: {
            type: Boolean,
            default: false
        },
        // 支持的文件类型：
        // ''表示类型不限制; 若配置'jpg, png, doc, pdf'表示当前支持这4种
        suffix: {
            type: String,
            default: ''
        },
        // 文件选择错误（格式，大小，数目）时，页面交互展示方式
        // 可选值：
        //     dialog: 弹窗形式展示选择的文件列表，可以查看文件情况，选择保留或重新选择
        //     notification：使用通知，将错误文件原因逐一份悬浮出现在页面角落，符合的文件保留页面
        //     none: 直接提醒存在文件错误，符合的文件保留页面
        //     后面两种在超出可选数的情况都将放弃本次选择，需要用户重新选择
        fileWrongTipType: {
            type: String,
            default: 'none'
        },
        // 文字提示相关信息列表
        // 期望的数据格式为'提示1, 提示2, 提示3'
        tipList: {
            type: String,
            default: ''
        },
        // 限制最大文件大小
        maxSize: {
            type: [String, Number],
        },
        // 限制最多文件数
        limit: {
            type: [Number, String]
        },
        // 是否允许多选，默认允许-true
        multiple: {
            type: Boolean,
            default: true
        },
        // 一行展示个数
        lineNum: {
            type: [Number, String],
            default: 3
        },
        // 分辨率，格式如'300*400'
        ratio: {
            type: String
        },
        // 是否选择文件后立即上传
        quickUpload: {
            type: Boolean,
            default: false
        },
        // 模版
        attachTemplateId: {
            type: String
        }
    },
    data () { // 定义页面变量
        return {
            // 当前打开文件选择器选择文件错误情况
            chooseErrorFileObj: {
                status: false,
                message: ''
            },
        }
    },
    computed: {
        // 获取附件模版url
        _attachTemplateUrl () {
            if (!this.attachTemplateId) return
            if (typeof this.attachTemplateId === 'string') {
                // 若当前传入为附件id，则组装完成的附件下载地址
                if (/^[a-zA-Z0-9]*$/.test(this.attachTemplateId)) {
                    return Get_Attach_Url(this.attachTemplateId)
                }
                // 若当前传入为附件地址，则返回附件地址（Get_Full_Url支持不传域名进来，则这里会处理添加域名）
                if (~this.attachTemplateId.indexOf('/')) {
                    return Get_Full_Url(this.attachTemplateId)
                }
            }
        },
        // 动态设置定义选择文件的input的id值
        // 不可重复，必须使用字段name加以划分
        fileId () {
            return `${this.name}_fileId_${Get_UUID()}`
        },
        // 最大文件尺寸，删除空格
        _maxSize () {
            return Get_File_Size(Remove_Space(this.maxSize))
        },
        // 获取最终的分辨率，值如'300*400'
        _ratio () {
            if (!this.ratio) return null
            let _reg = new RegExp(/(\d+)\D(\d+)/,)
            if (!_reg.test(this.ratio)) return null
            return this.ratio.replace(_reg, `$1${GlobalConst.ratioSeparator}$2`)
        },
        // 文件选择数上限
        _limit () {
            // 如果设置为不可多选，那么峰值则为1
            if (!this.multiple) {
                return 1
            }
            // 没有传入值，返回0，表示不设置数目峰值
            if (!this.limit) {
                return 0
            }
            // 添加绝对值处理，避免负数
            return Math.abs(Number(this.limit)) || 0
        },
        // @return {Object} {
        //     tip: 对应的提示文本
        //     status: true/false 是否可以执行选择文件的操作
        // }
        _limitObj () {
            if (!this._limit) {
                return {
                    tip: '可上传多个文件',
                    status: true
                }
            } else {
                let status = this.tempValue.length < Number(this._limit)
                if (status) {
                    return {
                        tip: `还可上传 <span class="primaryC font bold">${this._limit - this.tempValue.length}</span> 个文件`,
                        status: true
                    }
                }
                return {
                    tip: `已达上传峰值数：${this._limit}个`,
                    status: false
                }
            }
        }
    },
    methods: { // 定义函数
        /**
         * 设置选择文件错误时情况
         * {Object/undefiend} obj 为undefined时表示传入空执行重置函数，传入对象时表示设置值
         *      Object {status, message} status表示是否显示错误提示的状态,message表示错误信息
         */
        setErrorFileObj (obj) {
            if (obj) {
                // 传入对象时根据对象赋值
                let { status, message } = obj
                this.chooseErrorFileObj.status = status
                this.chooseErrorFileObj.message = message
            } else {
                // 传入空时，表示reset，重置属性
                this.chooseErrorFileObj.status = false
                this.chooseErrorFileObj.message = ''
            }
            
        },
        isOverLimit (fileList) {
            // 获取当前选取文件数目
            let chooseLen = fileList.length
            // 判断是否存在限制数时是否超出
            if (this._limit && chooseLen + this.tempValue.length > this._limit) {
                // 剩余可选数
                let elseNum = this._limit - this.tempValue.length
                // 定义提醒信息
                let message = `最多可再上传${elseNum}个文件，本次选择了${chooseLen}个文件已超出，请重新选择`
                // 设置错误提示词
                this.setErrorFileObj({
                    message,
                    status: true
                })
                // 文件数超出时不添加数据，直接return
                return true
            }
            return false
        },
        /**
         * 添加文件
         * @param {FileList, Array} files 文件集合
         */
        async addFile (files) {
            // files格式说明
            //     图片ImagePicker过来的的参数是Array，子项为File
            //     附件Attach过来的参数是FileList，子项为File；FileList类型需要通过Array.from才能转Array
            let _files = files && Array.from(files) || []
            // 重置错误选择文件时状态与提示语
            this.setErrorFileObj()
            // 定义附件承诺列表
            let fileProList = []
            _files.forEach(i => {
                if (i.hasOwnProperty('raw') && i.raw.constructor === File) {
                    fileProList.push(this.verifyFile(new ElUploadUtil(i), this.tempValue))
                } else {
                    // new FileUtil: 根据file对象实例化返回对象
                    fileProList.push(this.verifyFile(new FileUtil(i), this.tempValue))
                }
            })
            // 定义附件列表
            let fileList = await Promise.all(fileProList)
            // 分析选择数据结果（比对文件大小限制，文件格式，文件数目限制）
            if (this.isOverLimit(fileList)) {
                return false
            }
            // 获取本次选择中的错误文件列表
            let errorList = fileList.filter(i => !i.verifyStatus)
            // 获取本次选择中的合格文件列表
            let rightList = fileList.filter(i => i.verifyStatus)
            if (errorList.length > 0) {
                // 因为这里是选中文件后触发的，当选取文件不符合时，需要遍历删除文件对象不能展示出来
                // 定义本次错误提示数组
                let errorMessageList = []
                errorList.forEach(i => {
                    let message = [`[${i.name}无法选取]`]
                    if (i.isEmptyVerify) {
                        message.push(`资源为空或者为文件夹类型`)
                    } else if (i.isFolderVerify) {
                        message.push(`当前不支持文件夹`)
                    } else if (i.isExistVerify) {
                        message.push(`文件已存在，请勿重复选择`)
                    } else if (i.failSuffixVerify) {
                        message.push(`文件格式期望为${this.suffix}`)
                    } else if (i.failRatioVerify) {
                        message.push(`分辨率期望为${this._ratio}`)
                    } else if (i.failSizeVerify) {
                        message.push(`文件太大(${i.sizeDesc || ''}期望不超${this._maxSize})`)
                    }
                    errorMessageList.push(message.join(' - '))
                })
                // 设置错误提示词
                this.setErrorFileObj({
                    message: errorMessageList,
                    status: errorList.length > 0
                })
            }
            if (rightList.length > 0) {
                // 1. 弹出遮罩窗 TODO
                // this.chooseFileList = fileList
                // this.fileErrorVisible = true
                // 2. 直接结果    
                // 加正确的图片会有问题
                if (!this.quickUpload) {
                    // 非立即上传，则直接添加
                    this.tempValue.push(...rightList)
                    this.$emit('input', this.tempValue)
                    this.$emit('change', this.tempValue)
                } else {
                    // 此时为立即上传

                    // 修改数据状态为上传中
                    rightList.forEach(i => i.uploading = true)
                    // 根据已有数据获取下个数据需要使用的下角标
                    let _initIndex = this.tempValue.length
                    // 添加数据，但是不更新字段值
                    this.tempValue.push(...rightList)
                    // 上面逻辑设置loading加载状态，下面开始提交数据
                    let _promiseList = []
                    rightList.forEach(i => {
                        _promiseList.push(Upload_File({ file: i.raw }))
                    })
                    Promise.allSettled(_promiseList).then(res => {
                        res.forEach(({ status, value }, index) => {
                            if (status === 'fulfilled') {
                                // 定义添加的文件对象
                                let _item = {
                                    ...rightList[index],
                                    ...value,
                                    // 更新url为上传后的在线地址
                                    url: Get_Attach_Url(value.attachId),
                                    // 上传中状态设置为false
                                    uploading: false
                                }
                                // 添加数据，页面展示
                                this.$set(this.tempValue, _initIndex + index, _item)
                            }
                        })
                        // 更新字段数据
                        // 注意前面只添加数据(让页面可以看到新选择的数据模块)，不更新字段
                        this.$emit('input', this.tempValue)
                        this.$emit('change', this.tempValue)
                    })
                }
            }
        },
        /**
         * 检验文件【格式，大小，数目】，并对应扩充文件对象属性
         * @param {Object} fileItem 附件对象
         * @param {Array} existFiles 已经存在的文件数据
         */
        async verifyFile (fileItem, existFiles) {
            // 获取当前文件的大小size, 文件后缀suffix, 文件名name
            let { size, suffix, name, raw, isImg } = fileItem
            // 判断文件size是否为0
            let isEmptyVerify = false
            // 判断文件是否为文件夹
            let isFolderVerify = false
            // 判断上传文件是否已经存在
            let isExistVerify = false
            // 初始定义当前文件格式是否符合标准：suffixVerify
            let failSuffixVerify = false
            // 判断文件分辨率是否符合
            let failRatioVerify = false
            // 初始定义当前文件大小是否符合标准：sizeVerify
            let failSizeVerify = false
            // 判断文件尺寸是否为0
            isEmptyVerify = size === 0
            // 判断文件大小是否合格
            if (Has_Value(this._maxSize)) {
                // 当文件设置了上传大小峰值，若超出最大值则设置尺寸校验不通过
                failSizeVerify = !Compare_A_large_B(this._maxSize, size)
            }
            // 判断文件后缀是否合格
            if (this.suffix &&
                !Remove_Space(this.suffix)
                    .split(GlobalConst.separator)
                    .map(i => i.toUpperCase().replace('.', ''))
                    .includes(suffix.toUpperCase())) {
                // 当设置了格式后缀时，去除.号设置全部大写，若不符合规定的后缀则设置后缀校验不合格
                failSuffixVerify = true
            }
            // 判断是否重复文件
            if (existFiles &&
                existFiles.constructor === Array &&
                existFiles.length > 0) {
                // 判断已选文件中是否能找出与当前所选一致的文件，存在则确定文件重复
                isExistVerify = !!existFiles.find(i => i.name === name && i.size === size)
            }
            // 判断是否文件夹
            //     *************测试数据******************************************************
            //     ** 【文件】                【size】     【type】        【arrayBuffer()】
            //     ** demo.jpg                5066         image/jpeg      false
            //     ** A文件夹(有子文件)        4096          空              true
            //     ** B文件夹(有子文件)        0             空              true
            //     ** a.ttt(特殊后缀且空)      0             空              true
            //     ** b.ipa(特殊后缀)          186206       空               true
            //     ** C文件夹(有子文件)        12888         空               false
            //     *************测试数据******************************************************
            //     文件夹raw对象的size属性可能为0，也可能为4096或其他，不能用0作为判断标准
            //     size为0：可能是资源没有或者文件夹，所以将这种情况在最前面处理，这里只处理size不为0的情况
            //     这里就只需要判断arrayBuffer使用时是否报错，决策是文件还是文件夹
            try {
                // 能够正常读取则表示是附件，反之报错导致进入catch函数则为文件夹
                await raw.slice(0, 1).arrayBuffer()
            } catch (_) {
                isFolderVerify = true
            }
            // 处理分辨率检验(主要面向图片)
            if (this._ratio && isImg) {
                failRatioVerify = !(await this.isImgRatioVerify(raw, this._ratio))
            } else {
                failRatioVerify = false
            }
            // 定义附件校验的最终结果
            let _verifyStatus = !(isEmptyVerify || failSizeVerify || failSuffixVerify || isExistVerify || isFolderVerify || failRatioVerify)
            return Promise.resolve({
                isEmptyVerify,
                failSizeVerify,
                failSuffixVerify,
                isExistVerify,
                isFolderVerify,
                failRatioVerify,
                verifyStatus: _verifyStatus, // 附件校验状态
                ...fileItem
            })
        },
        /**
         * 判断图片分辨率是否符合要求
         * @param {File} raw 图片file对象
         * @param {String} ratio 期望的分辨率 
         */
        isImgRatioVerify (raw, ratio) {
            return new Promise((resolve, reject) => {
                // 定义 filereader对象
                let reader = new FileReader()
                reader.readAsDataURL(raw)
                reader.onload = e =>{
                    const image = new Image()
                    image.src = e.target.result
                    image.onload = _ => {
                        // 获取图片的分辨率
                        let _imgRatio = image.width + GlobalConst.ratioSeparator + image.height
                        if (_imgRatio === ratio) {
                            // 分辨率一致
                            resolve(true)
                        } else {
                            resolve(false)
                        }
                    }
                }
            })
        }
    }
}
</script>
<style lang='scss' scoped>

</style>