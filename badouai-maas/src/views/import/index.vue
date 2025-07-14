// 默认按钮导入自定义弹窗页面
<template>
    <div class="import-page">
        <div class="s-part">
            <div class="s-part__title">
                <bd-icon name="point-fill" class="point fill"></bd-icon>
                上传文件
            </div>
            <div class="s-part__content">
                <bd-attach
                    :limit="1"
                    :showHandler="false"
                    suffix="json,jsonl,xls,xlsx"
                    :maxSize="0"
                    :value="fileList"
                    lineNum="1"
                    :attachTemplateId="_templateUrl">
                </bd-attach>
            </div>
        </div>
        <div class="s-part">
            <div class="s-part__title">
                <span>
                    <bd-icon name="point-fill" class="point fill"></bd-icon>
                    导入结果
                </span>
                <span v-if="content"  class="s-part__clear" @click="content = ''">
                    <bd-icon name="delete"></bd-icon>
                    <span>清除日志</span>
                </span>
            </div>
            <div class="s-part__content">
                <div
                    v-html="content"
                    :class="getTipClass()"
                    class="s-part__content-result">
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import Attach from '@/components/frame/Common/MForm/components/items/File/Attach/index.vue'
import ScopeMixin from '@/components/frame/ScopeMixin'
import { Download_Import_Url, Import_Url } from '@/api/frame/common'
export default {
    components: {
        [Attach.name]: Attach
    },
    mixins: [ScopeMixin],
    data () { // 定义页面变量
        return {
            // 附件列表
            fileList: [],
            // 导入结果
            content: '<span class="fontCS fontS">暂无导入结果</span>',
            // 定义提示状态
            status: null,
        }
    },
    props: {
        // 模型编码
        mdCode: {
            type: String,
            default: ''
        },
        // 接口地址
        url: {
            type: String,
        },
        // 模版地址
        templateUrl: {
            type: String,
        },
        // 接口参数
        keyProps: {
            type: Object
        }
    },
    computed: {
        // 获取导入模版url
        _templateUrl () {
            if (this.templateUrl) return this.templateUrl
            return Download_Import_Url(this.mdCode)
        }
    },
    methods: { // 定义函数
        /**
         * 获取提示样式类
         */
        getTipClass () {
            if (!this.status) return
            let tipStyle = {
                success: 'successC',
                fail: 'dangerC'
            }
            return tipStyle[this.status] || ''
        },
        /**
         * 点击导入按钮
         * @param {Object} btnObj 按钮对象
         * @param {Object} that 列表页面作用域
         */
        clickImport (btnObj, that) {
            // 判断用户是否选择了文件
            if (this.fileList.length === 0) {
                // alert提示
                this.$message.error('请选择导入的文件')
                return
            }
            let _url = this.url || Import_Url(this.mdCode)
            // 导入接口参数
            let fileObj = {
                attach: this.fileList[0].raw, // 将要导入的文件File
                fieldValue: this.keyProps &&
                            typeof this.keyProps === 'object' &&
                            JSON.stringify(this.keyProps) || '', // 默认为''
            }
            // 更新按钮加载状态
            btnObj.loading = true
            // 提交接口
            this.post(_url, fileObj).then((res) => {
                // 赋值接口返回数据
                if (res && res.hasOk) {
                    this.content = res.message || '导入成功'
                    this.status = 'success'
                    // 刷新列表
                    that.searchReset()
                } else {
                    let _content = `导入失败：${res?.message || '原因未知'}`
                    this.content = _content
                    this.status = 'fail'
                    console.error(_content)
                }
            }).finally(() => {
                // 更新按钮加载状态
                btnObj.loading = false
            })
        },
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
.import-page {
    padding: $padding * 2;
    .s-part {
        margin-bottom: $space * 2;
        &:last-of-type {
            margin-bottom: 0;
        }
        .s-part__title {
            padding: $padding 0;
            // margin-bottom: $space;  // 标题与内容区相隔太远了
            font-weight: bold;
            color: $fontCL;
        }
        .s-part__content {
            .s-part__content-result {
                padding: $padding;
                width: 100%;
                height: 180px;
                overflow: auto;
                background: rgba(238, 238, 238, 0.4);
                border-radius: 4px;
                box-shadow: 1px 1px 4px 0px rgba(0,0,0,.15) inset;
            }
        }
        .s-part__clear {
            cursor: pointer;
            font-size: $fontS;
            font-weight: 400;
            float: right;
            margin-top: 6px;
            transition: all .03s;
            &:hover {
                color: $warning;
            }
        }
    }
}
</style>