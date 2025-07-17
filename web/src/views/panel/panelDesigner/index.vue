<!--
 * @FilePath: @/views/panel/panelDesigner/index.vue
 * @Description: 面板设计器（20220920版本）
-->
<template>
    <div class="panel-designer">
        <div class="setting-bar pad-20">
            <search-single-select
                label=""
                v-model="panelId"
                :option="panelList"
                @search="changePanel"
                defaultText="请选择面板">
            </search-single-select>
            <div class="setting-btns">
                <template v-for="(i, index) in settingBtns">
                    <bd-button
                        v-if="i.id === 'addPanel' || panelId"
                        :key="index"
                        v-bind="i"
                        @click="exeMethod(i)">
                    </bd-button>
                </template>
            </div>
        </div>
        <div
            ref="panelContentRef"
            v-loading="loading"
            class="panel-contant">
            <template v-if="panelId && blockList.length">
                <!-- 拖拽容器 -->
                <layout-drag
                    class="panel-designer-drag-box"
                    ref="dragRef"
                    :blocks.sync="blockList"
                    :style="panelStyle"
                    showPos
                    :minW="20"
                    :minH="20"
                    @resizeStop="(x, y, w, h, index) => resizeBlock(index)">
                    <div
                        class="wh-100 block-box"
                        v-for="(i, i_index) in blockList"
                        :key="i_index"
                        :slot="'layout' + i_index">
                        <div class="block-setting-btns">
                            <content-status-switch
                                v-if="i.contentType === '1'"
                                :contentList.sync="i[contentKey]"
                                @change="(content, value) => switchStatus(i_index, content, value)"
                                class="block-setting-icon">
                                <bd-icon name="view"></bd-icon>
                            </content-status-switch>
                            <span
                                class="block-setting-icon"
                                v-for="(b, b_index) in blockBtns"
                                :key="b_index"
                                @click="exeMethod(b, { block: i, index: i_index })"
                                :title="b.title">
                                <bd-icon :name="b.icon"></bd-icon>
                            </span>
                        </div>
                        <block-design-view
                            :ref="'block' + i_index"
                            :block="blockList[i_index]"
                            :panelInfo="panelInfo">
                        </block-design-view>
                    </div>
                </layout-drag>
            </template>
            <div v-else class="no-data-box">
                <no-data :src="noPanelImg" text="">
                    <p slot="text" class="custom-no-data-text textC">
                        啥都没有，请
                        <template v-if="!panelId">
                            <span v-if="panelList.length">先选择或</span>
                            <span class="add-panel-text" @click="showPanelDialog">添加</span>
                            一个面板吧！
                        </template>
                        <template v-else>
                            <span class="add-panel-text" @click="showBlockDialog">添加</span>
                            一个面板块吧！
                        </template>
                    </p>
                </no-data>
            </div>
        </div>
        <!-- 面板编辑弹窗 -->
        <panel-setting-dialog
            :visible.sync="panelSettingVisible"
            :field="panelDialogInfo"
            @submit="savePanelInfo">
        </panel-setting-dialog>

        <!-- 块编辑弹窗 -->
        <block-setting-dialog
            v-if="isOptionsReady"
            :visible.sync="blockSettingVisible"
            :field="blockInfo"
            @submit="saveBlockInfo">
        </block-setting-dialog>
    </div>
</template>

<script>
import LayoutDrag from './components/LayoutDrag'
import BlockDesignView from './components/BlockDesignView'
import globalStyle from '@/styles/theme.scss'
import SearchSingleSelect from '@/components/frame/Common/MSearch/components/SingleSelect'
import NoData from '@/components/frame/NoData'
import PanelSetting from './components/PanelSetting'
import BlockSetting from './components/blockSetting/index'
import ContentStatusSwitch from './components/ContentStatusSwitch.vue'
import {
    Find_Block_Settings_Info,
    User_Layouts,
    Delete_Block,
    Save_Panel_Pos,
    Save_Panel_Info,
    Save_Block,
    Down_File_Url,
    Change_Hide_Status,
    Save_Block_Share,
    Save_Content_Share,
    Upload_File
} from '@/api/frame/panel/designer'
import { Deep_Clone } from '@/utils/clone'
import PanelConsts from '@/views/panel/js/consts'
import { Get_Panel_Config } from '@/components/frame/Panel/PanelCode/utils.js'
import Panel_Theme_Consts from '@/views/panel/js/theme.js'
import GlobalConst from '@/service/global-const.js'
import { Is_Array } from '@/utils/data-type'
export default {
    components: {
        LayoutDrag,
        BlockDesignView,
        SearchSingleSelect,
        [PanelSetting.name]: PanelSetting,
        [BlockSetting.name]: BlockSetting,
        NoData,
        ContentStatusSwitch
    },
    data () {
        return {
            loading: false,
            noPanelImg: require('@/assets/image/frame/noPanel.png'),
            // 面板id
            // panelId: '4028e381783539cf0178353ba9d20013',
            panelId: '',
            // 当前选中面板信息
            panelInfo: {},
            // 当前面板块信息
            blockList: [],

            // 面板弹窗绑定信息
            panelDialogInfo: {},
            // 正在编辑中的面板块信息
            blockInfo: {},
            // 主题引用js文件
            themeJsObj: {},
            panelSettingVisible: false,
            blockSettingVisible: false,
            // 面板块信息
            blocks: [
                // { w: 300, h: 300, x: 0, y: 100 },
                // { w: 200, h: 500, x: 350, y: 0 },
                // { w: 300, h: 100, x: 700, y: 300 },
            ],
            // 全局的样式文件
            globalStyle: globalStyle,
            // 下拉选择框
            options: {},
            // 面板列表
            panelList: [],
            // 块设置按钮
            blockBtns: [
                { id: 'settingBlock', title: '设置块', click: 'showBlockDialog', icon: 'setting' },
                { id: 'deleteBlock', title: '删除块', click: 'delBlock', icon: 'delete' },
            ],
            // 面板设置按钮列表
            settingBtns: [
                { id: 'savePos', name: '保存位置', click: 'handlerSavePos', icon: 'save', type: 'success' },
                { id: 'addBlock', name: '添加块', click: 'showBlockDialog', icon: 'add', type: 'primary' },
                { id: 'editPanel', name: '设置面板', click: 'showPanelDialog', icon: 'setting', type: 'warning' },
                { id: 'addPanel', name: '添加面板', click: 'showPanelDialog', icon: 'plus', type: 'primary' },
                { id: 'view', name: '面板预览', click: 'viewPanel', icon: 'view', type: 'success' },
            ],
            // 初始块位置宽度信息
            defaultBlock: { col: 0, row: 0, sizex: 30, sizey: 300 },
            // 面板模板
            panelTemplate: '',
            // 预览文件地址
            downFileUrl: Down_File_Url,
            // 面板内容所对应的key
            contentKey: GlobalConst.panel.contentKey
        }
    },
    computed: {
        // 需要传递给块组件的属性
        _attrs () {
            let { color, contentBg, primaryColor, textColor } = Object.assign({}, this.panelInfo.theme || {}, this.panelInfo?.customSetting || {})
            return {
                // 色系
                color,
                // 背景色
                contentBg,
                // 主题色
                primaryColor,
                // 字体颜色
                textColor,
                // 主题
                theme: this.panelInfo.theme,
                ...this.$attrs
            }
        },
        panelStyle () {
            let themeStyle = this.panelInfo?.theme?.panel || {}
            let customStyle = {}
            if (this.panelInfo.backgroundImg) {
                // let img = require()
                customStyle.backgroundImage = `url(${Down_File_Url + this.panelInfo.backgroundImg})`
            }
            let varStyle = {
                '--content-bg': this._attrs.contentBg,
                '--primary-color': this._attrs.primaryColor,
                '--text-color': this._attrs.textColor,
            }
            return Object.assign(varStyle, themeStyle, customStyle)
        },
        // 下拉列表是否已经请求回来
        isOptionsReady () {
            return this.options.borderType && Object.keys(this.options).length > 2
        },
    },
    methods: {
        exeMethod (btn, params) {
            if (typeof btn.click === 'function') {
                btn.click(params, btn)
            } else if (typeof this[btn.click] === 'function') {
                this[btn.click](params, btn)
            }
        },
        // 面板盒子尺寸
        boxSize () {
            let { clientWidth, clientHeight } = this.$refs.panelContentRef || {}
            return {
                width: clientWidth,
                height: clientHeight
            }
        },
        async init () {
            // 重置两个弹窗的visible，主要是为了调试保存的时候，不至于出现弹窗空白现象
            this.panelSettingVisible = false
            this.blockSettingVisible = false
            // 请求下拉信息
            this.loadOptions()
            // 获取面板下拉列表
            await this.getPanelList()
            // 获取面板id
            if (this.panelList.length) {
                this.panelId = this.panelList?.[0]?.id || ''
                // this.panelId = '4028815b87560c2601876055b76e0017'
                this.getBlocksData()
            }
        },
        // 请求面板数据
        async getBlocksData (panelId = this.panelId) {
            if (!panelId) {
                console.error('缺少必要的panelId')
                return
            }
            let params = {
                isWorkBeanch: true,
                panelId: this.panelId,
            }
            this.$set(this, 'blockList', [])
            let panelConfig = await Get_Panel_Config(params)
            this.$set(this, 'panelInfo', panelConfig.panelInfo)
            // 边框类型下拉条件
            let themeName = panelConfig.panelInfo.pageId || 'default'
            this.$set(this.options, 'borderType', Panel_Theme_Consts.themeBorderInfo[themeName])
            // 获取当前拖拽区域宽高
            let clientWidth = this.boxSize().width
            this.$set(this, 'blockList', panelConfig.blockList.map(b => {
                // 组装位置信息
                let posInfo = {
                    w: b.sizex / 100 * clientWidth,
                    h: b.sizey,
                    y: b.row, // 即top
                    x: b.col / 100 * clientWidth,  // 即left
                }
                return Object.assign(b, posInfo)
            }))
        },
        // 重新请求数据
        async reload (panelId = this.panelId, scrollTop) {
            this.loading = true
            await this.getBlocksData(panelId)
            this.loading = false
            if (arguments.length > 1 && this.$refs.dragRef && this.$refs.dragRef.$el) {
                if (scrollTop === -1) {
                    scrollTop = this.getMaxPosBlock().max_y
                }
                this.$refs.dragRef.$el.scrollTop = scrollTop
            }
        },
        // 获取位置最大的块信息
        getMaxPosBlock () {
            if (!this.blockList.length) {
                return {}
            }
            // 找出距离容器底部最近的块
            let max_yh = Math.max.apply(Math, this.blockList.map(function (o) {
                return (o.y || 0) + (o.h || 0)
            }))
            let block_max_yh = this.blockList.find(function (o) {
                return (o.y || 0) + (o.h || 0) === max_yh
            })
            // 找出在block_max_yh块右侧的所有块
            let blocks_max_yh_right = this.blockList.filter(o => {
                return o.x > block_max_yh.x && (o.y + o.h) >= block_max_yh.y
            }) || []
            let temp_x_list = [block_max_yh, ...blocks_max_yh_right].map(function (o) {
                return (o.x || 0) + (o.w || 0)
            })
            let max_x = Math.max.apply(Math, temp_x_list)
            // // 找出最大的位置信息，以确定当前块放置位置
            // let max_y = Math.max.apply(Math, this.blockList.map(function (o) { return (o.y || 0) + (o.h || 0) }))
            // // 同等max_y高度下最大x轴的位置信息
            // let max_x = Math.max.apply(Math, this.blockList.filter(f => { return (f.y || 0) + (f.h || 0) === max_y }).map(function (o) { return (o.x || 0) + (o.w || 0) }))
            // // 最大位置块数据
            // let max_box = this.blockList.find(f => { return (f.y || 0) + (f.h || 0) === max_y && (f.x || 0) + (f.w || 0) === max_x })
            return { max_y: block_max_yh.y, max_yh, max_x }
        },
        // 请求下拉框数据
        loadOptions () {
            Find_Block_Settings_Info().then(res => {
                Object.keys(res).forEach(key => {
                    this.$set(this.options, key, res[key])
                })
            }).catch(() => {
                this.$message.error('请求下拉列表失败')
            })
        },
        // 请求面板列表
        getPanelList () {
            return new Promise((resolve, reject) => {
                User_Layouts().then(res => {
                    if (!res) {
                        resolve([])
                    } else {
                        this.panelList = res.map(o => {
                            return {
                                ...o,
                                text: o.name
                            }
                        })
                        resolve(this.panelList || [])
                    }
                }).catch(err => {
                    resolve([])
                })
            })
        },
        async handlerSavePos (params, btn) {
            btn.loading = true
            let resp = await this.savePosition()
            btn.loading = false
            if (resp.hasOk) {
                this.$message.success('保存成功')
                this.loading = true
                await this.getBlocksData()
                this.loading = false
            } else {
                this.$message.warning(resp.message || '保存失败')
            }
        },
        // 保存面板位置信息
        savePosition (parmas = {}, btn = {}) {
            return new Promise((resolve, reject) => {
                let boxW = this.boxSize().width
                let params = this.blockList.map(o => {
                    return {
                        id: o.id,
                        col: o.x / boxW * 100,
                        row: o.y,
                        sizex: o.w / boxW * 100,
                        sizey: o.h,
                        maxsizex: 100
                    }
                })
                Save_Panel_Pos({ panelId: this.panelId, block: JSON.stringify(params) }).then(res => {
                    resolve(res)
                }).catch(err => {
                    resolve({hasOk: false})
                })
            })
        },
        // 预览面板
        viewPanel () {
            let routeUrl = this.$router.resolve({
                path: `/panel/view/${this.panelId}`,
            })
            window.open(routeUrl.href, '_blank')
        },
        // 添加面板（弹出面板弹窗）
        async showPanelDialog (params, btn = {}) {
            if (btn.id === 'addPanel') {
                this.panelDialogInfo = {}
            } else {
                this.panelDialogInfo = Deep_Clone(this.panelInfo)
            }
            this.panelSettingVisible = true
        },
        // 保存面板
        async savePanelInfo (params, btn) {
            this.$set(btn, 'loading', true)
            // params = {
            //     ...this.panelDialogInfo,
            //     ...params
            // }
            // 如果背景图片有数据，先保存背景图片，拿到attachId
            params.backgroundImg = await this.getSingleImgId('backgroundImg', params, this.panelInfo)
            Save_Panel_Info(params).then(res => { // 保存面板
                this.$set(btn, 'loading', false)
                if (res.hasOk) {
                    this.panelSettingVisible = false
                    this.$message.success('保存成功')
                    this.panelId = res.bean.id
                    this.getPanelList() // 重新请求面板下拉列表
                    this.reload()
                } else {
                    this.$message.warning(res.message || '保存失败')
                }
            }).catch(() => {
                this.$set(btn, 'loading', false)
            })
        },
        /**
         * @description: 从表单组件中，获取单图片id，若图片还未上传到服务器，先上传后拿到id
         * @param {String} key：需获取id的图片key
         * @param {Object} dataForm：表单组件规则验证通过后返回回来的数据
         * @param {Object} params：初始传递给表单组件的数据值
         * @return {String}：图片id
         */
        async getSingleImgId (key, dataForm, params) {
            // 获取表单绑定数据中被删除的附件对象
            let deleteAttach = {}
            if (dataForm.deleteAttach) {
                deleteAttach = JSON.parse(dataForm.deleteAttach)
            }
            // 如果拿到是一个文件对象，先上传图片，才能拿到图片id
            if (dataForm[key] && dataForm[key][0] && dataForm[key][0].raw instanceof File) {
                let img_res = await Upload_File({ file: dataForm[key][0].raw })
                if (img_res.attachId) {
                    return img_res.attachId
                }
            } else {
                // deleteAttach[key] 有值，说明图片已被删除
                if (deleteAttach[key]) {
                    return ''
                }
                // 表单附件会返回数组，如果拿到是字符串，说明这是初始传入的数据
                if (typeof dataForm[key] === 'string') {
                    return JSON.stringify(dataForm[key]) === '[]' ? '' : dataForm[key]
                } else if (Is_Array(dataForm[key])) {
                    // 如果是数组，因为是单图，所以
                    // 1. 如果是新增,那么在判断文件对象的时候已经被拦截掉了
                    // 2. 如果是替换,那么同理也会有新增，所以执行不到这里
                    // 3. 未更改，会执行到这里，所以直接返回原来的图片id即可
                    return params[key]
                }
            }
        },
        // 保存块信息
        async saveBlockInfo (params, btn) {
            // 先把现有的块位置进行保存，放置，重新请求后位置发生错乱
            this.savePosition()
            // 如果选中分享
            if (String(params.isShare) === '1') {
                let shareInfo = params.__shareInfo__
                this.saveShare(shareInfo)
            }
            delete params.__shareInfo__
            let boxW = this.boxSize().width
            // 当前编辑块的位置及宽高信息
            let posInfo = this.defaultBlock
            if (JSON.stringify(this.blockInfo) === '{}') { // 新增
                let { max_y, max_x, max_yh } = this.getMaxPosBlock()
                if (max_yh) { // 块的高度不能为0，所以，如果max_yh不存在，则说明没有块
                    // 新增插入块的宽度
                    let new_box_w = this.defaultBlock.sizex * boxW / 100
                    let space = 10 // 新增块与现有块的间隙
                    if ((boxW - max_x - new_box_w) >= space) {  // 最大位置块仍有空间存放当前需加入的面板时
                        posInfo.col = (max_x + space) / boxW * 100
                        posInfo.row = max_y
                    } else {  // 最大块右侧没有空间，另起一行进行存放
                        posInfo.col = 0
                        posInfo.row = max_yh + space
                    }
                }
            } else { // 编辑
                posInfo = { // 顺手将当前编辑块的位置宽高信息保存
                    col: this.blockInfo.x / boxW * 100,
                    row: this.blockInfo.y,
                    sizex: this.blockInfo.w / boxW * 100,
                    sizey: this.blockInfo.h,
                }
            }
            params = {
                ...this.blockInfo,
                ...params,
                ...posInfo,
                newBlock: JSON.stringify(this.blockInfo) === '{}',
                contentsStr: JSON.stringify(params.contents),
                maxsizex: 100, // maxsizex 在这个版本中固定为100，相当于%的作用
            }
            delete params[this.contentKey]
            this.$set(btn, 'loading', true)
            // 预览图
            params.previewImg = await this.getSingleImgId('previewImg', params, this.blockInfo)
            // 块背景图
            params.backgroundImg = await this.getSingleImgId('backgroundImg', params, this.blockInfo)
            // 获取当前的滚动条高度，以便重新请求块信息后，将滚动条重置为当前编辑处
            let tem_scroll_top = this.$refs?.dragRef?.$el?.scrollTop
            PanelConsts.Not_Save.forEach(key => {
                delete params[key]
            })
            let request_params = {
                // layoutId: this.panelId,
                panelId: this.panelId,
                isNew: params.newBlock ? 1 : 0,
                block: JSON.stringify(params),
            }
            Save_Block(request_params).then(res => {
                this.$set(btn, 'loading', false)
                if (res.hasOk) {
                    this.blockSettingVisible = false
                    this.$message.success('保存成功')
                    this.reload(this.panelId, JSON.stringify(this.blockInfo) !== '{}' ? tem_scroll_top : -1)
                } else {
                    this.$message.warning(res.tip || res.message || '保存失败')
                }
            }).catch(() => {
                this.$set(btn, 'loading', false)
            })
        },
        // 保存分享信息
        saveShare (data) {
            if (!this.blockInfo.id) {
                console.error('分享缺少必要的blockId')
                return
            }
            // 分享参数
            let params = {}
            // 接口函数
            let fn = Save_Block_Share
            // 保存块分享信息
            if (data.shareType === 'block') {
                let shareTargetArr = data.shareBlock.map(o => o.value)
                params = {
                    blockId: this.blockInfo.id,
                    shareTargetObject: shareTargetArr.join(',')
                }
            } else { // 保存内容分享信息
                fn = Save_Content_Share
                let shareArr = data.shareContent.map(item => {
                    let shareTargetArr = (item.shareList || []).map(o => o.value)
                    let result = {
                        contentId: item.id,
                        shareTargetObject: shareTargetArr.join(',') || ''
                    }
                    return result
                })
                params = {
                    shareArr: JSON.stringify(shareArr)
                }
            }
            fn(params).then(res => {
                if (res.hasOk) {
                    this.$message.success('分享成功')
                }
            })
        },
        // 打开块编辑面板
        showBlockDialog (params, btn = {}) {
            if (btn.id === 'settingBlock') { // 设置块
                this.blockInfo = this.blockList[params.index]
            } else {  // 添加块
                this.blockInfo = {}
            }
            this.blockSettingVisible = true
        },
        // 删除面板块
        delBlock (info) {
            this.$confirm('确认删除该面板块吗，删除后不可恢复！', '提示', {
                confirmButtonText: '确 定',
                cancelButtonText: '取 消',
                type: 'warning'
            }).then(() => {
                // 获取当前的滚动条高度，以便重新请求块信息后，将滚动条重置为当前编辑处
                let tem_scroll_top = this.$refs?.dragRef?.$el?.scrollTop
                Delete_Block({ panelId: this.panelId, blockId: info.block.id }).then(res => {
                    if (res.hasOk) {
                        this.$message.success('删除成功')
                        this.reload(this.panelId, tem_scroll_top)
                    } else {
                        this.$message.error(res.tip || res.message || '删除失败')
                    }
                })
            })
        },
        // 更改当前展示面板
        changePanel (e) {
            let oldId = e?.old?.value || ''
            let newId = e?.new?.value || ''
            if (oldId !== newId) {
                this.getBlocksData(this.panelId)
            }
        },
        // 块的大小更改
        resizeBlock (index) {
            // 内容自适应
            this.$refs['block' + index][0].resize()
        },
        // 刷新块数据
        refreshBlock (index) {
            this.$refs['block' + index][0].refresh()
        },
        // 更改块的显示隐藏状态
        switchStatus (blockIndex, content, status) {
            let block = this.blockList[blockIndex]
            let params = {
                panelId: this.panelId,
                blockId: block.id,
                contentId: content.id,
                isHide: status,
            }
            Change_Hide_Status(params).then(res => {
                this.$refs['block' + blockIndex][0].refresh()
            })
        },
    },
    mounted () {
        this.init()
    }
}
</script>

<style lang="scss" scoped>
.panel-designer::v-deep {
    display: flex;
    flex-direction: column;
    .setting-bar {
        background-color: #fff;
        display: flex;
        justify-content: space-between;
        border-radius: 4px;
        border-bottom: 0.5px dotted $primary;
    }
    .panel-contant {
        flex: 1;
        .panel-designer-drag-box {
            background-size: 100% 100%;
        }
        .no-data-box {
            height: 100%;
            .custom-no-data-text {
                font-size: 15px;
                color: #9EA9B2;
                margin-top: -5%;
                .add-panel-text {
                    position: relative;
                    color: #ffa200;
                    font-size: 18px;
                    cursor: pointer;
                    padding: 0 8px;
                }
            }
        }
        .block-box {
            position: absolute;
            .block-setting-btns {
                position: absolute;
                top: 0px;
                right: 0px;
                .block-setting-icon {
                    display: inline-block;
                    width: 25px;
                    line-height: 25px;
                    margin: 5px;
                    margin-left: 0;
                    text-align: center;
                    border-radius: 50%;
                    position: relative;
                    z-index: 100;
                    &:hover {
                        color: #fff;
                        background-color: $primary;
                    }
                }
            }
        }
    }
    .js-title-right-slot {
        color: rgba($color: #000000, $alpha: 0);
    }
}
.wh-100 {
    width: 100%;
    height: 100%;
}
</style>