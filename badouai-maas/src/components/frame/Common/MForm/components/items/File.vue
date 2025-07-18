// 组件接收值：
//     disabled: Boolean(false) - 是否可编辑
//     value: Array - 初始为[],回显时为 [{name: 'food.jpg', url: 'https://xxx.cdn.com/xxx.jpg'}]
//     name: String - 组件字段名
//     dataType: String - 文件上传类型（text/picture/picture-card）
//     multiple: Boolean - 是否多选（多选）
//     limit: Number - 上传总数限制数目
// 其余说明
//     action：上传地址（这里默认手动上传，不直接上传）
//     auto-upload： false-默认设置不自动上传
//     on-preview：点击已上传文件的事件
//     on-remove：文件移除的事件
//     before-remove：文件移除前的事件
//     on-change：文件change事件
//     on-exceed：文件超出上传文件个数时触发事件
<template>
    <div :class="selfClass">
        <template v-if="!isView">
            <el-upload
                :ref="name"
                :list-type="dataType"
                action=''
                :multiple="multiple"
                :limit="limit"
                :file-list="tempValue"
                :auto-upload="false"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :before-remove="beforeRemove"
                :on-change="handleChange"
                :on-exceed="handleExceed">
                <template v-if="!isPictureCard">
                    <el-button size="small" type="primary">点击上传</el-button>
                    <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
                </template> 
                <template v-else>
                    <i class="el-icon-plus"></i>
                </template>
            </el-upload>
            <template v-if="isPictureCard">
                <el-dialog :visible.sync="dialogVisible">
                    <img width="100%" :src="dialogImageUrl" alt="">
                </el-dialog>
            </template>
        </template>
        <template v-else>
            <template v-if="!isPictureCard">
                <div v-for="(i, index) in value" :key="index">
                    <span class="hoverC" >
                        <i class="el-icon-document"></i>
                        <a :href="i.url" :download="i.name" target="_blank">{{i.name}}</a>
                    </span> 
                </div>
            </template>
            <template v-else>
                <div class="padTop">
                    <el-image
                        v-for="(i, index) in value"
                        :key="index"
                        class="marR-10 preImgShow"
                        :src="i.url"
                        :preview-src-list="preList(i)">
                    </el-image>
                </div>
            </template>
        </template>
    </div>
</template>
<script>
export default {
    components: {},
    data () { // 定义页面变量
        return {
            dialogImageUrl: '',
            dialogVisible: false,
        }
    },
    props: {
        value: {
            type: Array,
            default: () => []
        },
        name: {
            type: String,
            default: ''
        },
        // dataType： 默认text表示文件
        dataType: {
            type: String,
            default: 'text'
        },
        multiple: {
            type: Boolean,
            default: true
        },
        limit: {
            type: Number,
            default: 10
        },
        // 是否为查看状态
        isView: {
            type: Boolean,
            default: false
        },
        // 图标名称：用于查看页面展示
        iconName: {
            type: String
        },
        // 自定义class
        selfClass: {
            type: String,
            default: ''
        }
    },
    computed: {
        isPictureCard () {
            return this.dataType === 'picture-card'
        },
        // 单项数据流原因，不可直接修改值，需要用临时值做过度使用
        tempValue: {
            get () {
                return this.value
            },
            set (val) {
                this.$emit('input', val)
            }
        }
    },
    methods: { // 定义函数
        preList (obj) {
            let imgList = []
            imgList.push(obj.url)
            return imgList
        },
        handleRemove (file, fileList) {
            console.log(file, fileList)
        },
        handlePreview (file) {
            if (this.isPictureCard) {
                this.dialogImageUrl = file.url
                this.dialogVisible = true
            } else {
                console.log(file.url)
            }
        },
        handleExceed (files, fileList) {
            this.$message.warning(`当前限制选择 3 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`)
        },
        beforeRemove (file, fileList) {
            return this.$confirm(`确定移除 ${ file.name }？`)
        },
        handleChange (file, fileList) {
            let uploadRef = this.$refs[this.name]
            this.$emit('input', fileList)
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
    }
}
</script>
<style lang='scss' scoped>
    .preImgShow {
        width: 100px;
        height: 100px;
    }
</style>