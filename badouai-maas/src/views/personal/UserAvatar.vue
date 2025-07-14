<template>
    <div class="padding defaultBg"  v-fullPageHeight>
        <el-row :gutter="10" class="h-per-100">
            <el-col :span="12" class="h-per-100">
                <div class="s-block">
                    <m-title title="上传头像"></m-title>
                    <div class="s-showAvatar textC">
                        <div class="d-ib s-choose">
                            <div class="textC">
                                <div class="s-choose-block">
                                    <img
                                        :src="selectImgSrc"
                                        :onerror="DEFAULT_AVATAR"
                                        alt=""
                                        class="imgCover">
                                    <div class="s-shadow"></div>
                                </div>
                                <div>
                                    <input type="file" id="imgFile" hidden @change="filePreview" value="123">
                                    <el-button class="marTop" type="primary" @click="openFile">
                                        <bd-icon name="add"></bd-icon>点击选择
                                    </el-button>
                                </div>
                            </div>
                        </div>
                        <div class="d-ib s-preview">
                            <div class="fontS fontCL">头像预览</div>
                            <div class="marVer s-preview-block">
                                <img
                                    :src="selectImgSrc"
                                    :onerror="DEFAULT_AVATAR"
                                    alt=""
                                    class="imgCover">
                            </div>
                            <div class="fontS fontCS">提示：仅支持PNG、JPG上传</div>
                        </div>
                    </div>
                </div>
            </el-col>
            <el-col :span="12" class="h-per-100">
                <div class="s-block">
                    <m-title title="我曾使用过的头像"></m-title>
                    <div class="s-history">
                        <el-scrollbar>
                            <div class="historyList">
                                <template v-if="historyList.length !== 0">
                                    <div
                                        v-for="(i, index) in historyList"
                                        :key="index"
                                        @click="useHistoryAvatar(i)"
                                        class="d-ib s-history-item">
                                        <div class="s-history-block">
                                            <div class="s-shadow-board textC">
                                                <div class="s-word">使用</div>
                                            </div>
                                            <img :src="i.url" :onerror="DEFAULT_AVATAR"  class="imgCover">
                                        </div>
                                    </div>
                                </template>
                                <template v-else>
                                    <!-- 无数据展示 -->
                                    <no-data marginTop="100"></no-data>
                                </template>
                            </div>
                        </el-scrollbar>
                    </div>
                </div>
            </el-col>
        </el-row>
        <div class="fixBottomBtn">
            <el-button
                v-for='(i, index) in btnList'
                :key="index"
                v-btnBg="i"
                :loading="i.isLoad !== undefined && i.isLoad"
                :disabled="!(attach || attachId)"
                @click='exeMethod(i)'>
                <bd-icon
                    v-if="(i.isLoad === undefined) || (i.isLoad !== undefined && !i.isLoad)"
                    :name="i.icon">
                </bd-icon>
                {{ i.name }}
            </el-button>
        </div>
    </div>
</template>
<script>
import globalStyle from '@/styles/theme.scss'
import { Find_By_ClassName } from '@/utils/find-dom'
import { mapGetters } from 'vuex'
import { Get_User_History_Avatar_List } from '@/api/frame/user'
import GlobalConst from '@/service/global-const'
import { Get_Attach } from '@/api/frame/index'
import NoData from '@/components/frame/NoData'
import MTitle from '@/components/frame/Common/MTitle'
export default {
    components: {
        NoData,
        [MTitle.name]: MTitle
    },
    directives: {
        fullPageHeight: {
            inserted: function (el, binding) {
                // 设置页面高度自适应铺满
                el.style.height = `${el.clientHeight - parseInt(globalStyle.footerHeight)}px`
                // 获取历史头像列表对象元素，设置其高度（数据过多时支持滚动）
                let historyElement = Find_By_ClassName(el, 's-history')
                // 主页面高度 - 2倍padding（上下） - 头部标题；剩下的则是列表的最大高度
                historyElement.style.height = `${el.clientHeight -
                                                2 * parseInt(globalStyle.padding) -
                                                parseInt(globalStyle.formTitleHeight)}px`
            }
        }
    },
    data () { // 定义页面变量
        return {
            selectImgSrc: '',
            attach: null,
            attachId: '',
            historyList: [],
            btnList: [
                { name: '保存', icon: 'save', click: this.save, isLoad: false}
            ]
        }
    },
    computed: {
        ...mapGetters([
            'avatar',
            'userInfo'
        ])
    },
    methods: {
        exeMethod (itemObj) {
            // 绑定事件中的this为当前页面作用域
            // 另外将按钮对象itemObj扔回作为绑定事件的参数
            itemObj.click.call(this, itemObj)
        },
        // 将选择的图片赋值给对应字段
        filePreview (e) {
            let _this = this
            let chooseFile = e.target.files[0]
            /* 图片类型正则验证 */
            let imgStr = /\.(jpg|jpeg|png|bmp|BMP|JPG|PNG|JPEG)$/
            if (!imgStr.test(chooseFile.name)) {
                this.$message({
                    type: 'warning',
                    message: '请选择JPG、PNG格式图片上传'
                })
                return
            }
            if (!e || !window.FileReader) return  // 看支持不支持FileReader
            let reader = new FileReader()
            reader.readAsDataURL(chooseFile) // 这里是最关键的一步，转换就在这里
            reader.onloadend = function () {
                _this.selectImgSrc = this.result
            }
            // 赋值文件字段
            this.attach = chooseFile
            // 清空文件id
            this.attachId = ''
        },
        openFile () {
            document.getElementById('imgFile').click()
        },
        // 渲染历史头像
        renderHistory () {
            Get_User_History_Avatar_List({
                userId: this.userInfo.id
            }).then(res => {
                let resList = res?.attachs || []
                this.historyList = resList.map(i => {
                    return {
                        id: i,
                        url: `${Get_Attach(i)}`
                    }
                })
            })
        },
        // 使用历史头像
        useHistoryAvatar ({id}) {
            this.attach = null
            this.attachId = id
            this.selectImgSrc = Get_Attach(id)
        },
        // 保存头像
        save (btnObj) {
            // 启用按钮加载状态-兼容4.0需要优先判断该按钮对象
            if (btnObj && btnObj.isLoad !== undefined) {
                btnObj.isLoad = true
            }
            let url = `${this.BASEURL}/org/employee/employeesave/uploadPhoto.do`
            this.post(url, { attach: this.attach, photoAttachId: this.attachId }).then((res) => {
                if (res.hasOk) {
                    this.userInfo.headImg = res.bean
                    // 更新用户头像数据
                    this.$store.commit('user/SET_USER_INFO', {
                        avatar: this.userInfo.headImg
                    })
                    this.$message({
                        type: 'success',
                        message: GlobalConst.message.tip.success
                    })
                    // 渲染历史头像
                    this.renderHistory()
                } else {
                    let tip = res.message || ''
                    this.alert(`${GlobalConst.message.tip.fail} ${tip}`)
                }
            }).finally(() => {
                // 兼容4.0需要优先判断该按钮对象
                if (btnObj && btnObj.isLoad !== undefined) {
                    // 请求结束，结束按钮加载状态
                    btnObj.isLoad = false
                }
            })
        },
        initAvatar () {
            this.selectImgSrc = Get_Attach(this.avatar) || ''
        }
    },
    // 可访问当前this实例
    created () {
        // 渲染历史头像
        this.renderHistory()
        // 初始化头像
        this.initAvatar()
    },
    // 挂载完成，可访问DOM元素
    mounted () {}
}
</script>
<style lang='scss' scoped>
$padBlock: 30px;
$showImgW: 200px;
$shadowDic: 2px;
@mixin circle ($sizeWidth) {
    width: $sizeWidth;
    height: $sizeWidth;
    border-radius: 50%;
    border: 1px solid $lineColor;
}
@mixin cover {
    width: 100%;
    height: 100%;
}
.s-block {
    border: 1px solid $lineColor;
    height: 100%;
    border-radius: $borderRadius;
    overflow: hidden;
}
.imgCover {
    @include cover
}
.s-showAvatar {
    padding: $padBlock 16px 0px;
    min-width: 560px;
    .s-choose {
        margin-right: 45px;
        .s-choose-block {
            width: $showImgW;
            height: $showImgW;
            overflow: hidden;
            position: relative;
            .s-shadow {
                @include circle($showImgW - $shadowDic);
                background: rgba(0, 0, 0, 0);
                box-shadow: rgba(0, 0, 0, 0.6) 0px 0px 0px 2000px;
                position: absolute;
                top: $shadowDic / 2 + 1px;
                left: $shadowDic / 2;
            }
        }
    }
    .s-preview {
        vertical-align: top;
        text-align: left;
        .s-preview-block {
            @include circle($showImgW / 2);
            overflow:hidden
        }
    }
}
$previewW: 80px;
.s-history {
    .historyList {
        padding: $padBlock 15px 0px 15px;
        .s-history-item {
            width: $previewW;
            text-align: center;
            position: relative;
            margin: 0px 10px 20px 0px;
            .s-history-block {
                @include circle($previewW - 10px);
                overflow: hidden;
                margin-bottom: 5px;
                position: relative;
                cursor: pointer;
                .s-shadow-board {
                    background: rgba(0, 0, 0, .7);
                    box-shadow: 0 0 0 2px rgba(0, 0, 0, .1);
                    cursor: pointer;
                    height: 100px;
                    left: 0;
                    position: absolute;
                    top: -100px;
                    transform: translateY(0);
                    transition: transform 300ms ease;
                    width: 100%;
                    z-index: 1;
                    .s-word {
                        color: rgba(255, 255, 255, .8);
                        position: absolute;
                        bottom: 10px;
                        left: 0;
                        right: 0;
                        margin: auto;
                    }
                }
                &:hover {
                    .s-shadow-board {
                        transform: translateY(40px)
                    }
                }
            }
        }
    }
}

</style>