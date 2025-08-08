<!--
 * @Description: 用户面板
-->
<template>
    <div class="user-designer">
        <div class="setting-bar pad-20">
            <search-single-select
                label=""
                v-model="panelId"
                :option="panelList"
                @search="changePanel"
                defaultText="请选择面板">
            </search-single-select>
            <!-- 设置选择按钮列表 -->
            <div class="setting-btns">
                <template v-for="(i, index) in settingBtns">
                    <bd-button
                        :key="index"
                        v-bind="i"
                        @click="exeMethod(i)">
                    </bd-button>
                </template>
            </div>
        </div>
        <!-- 面板展示容器 -->
        <div ref="panelContentRef" class="panel-container" v-loading="loading">
            <template v-if="blockList.length">
                <!-- 拖拽容器 -->
                <layout-drag
                    ref="dragRef"
                    :blocks.sync="blockList"
                    showPos
                    :minW="20"
                    :minH="20"
                    :style="panelStyle"
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
                                v-show="!b.show || b.show(i)"
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
            <div class="no-data-box" v-else>
                <no-data height="200px" :src="noPanelImg" text=""></no-data>
                <p v-if="!panelId" class="custom-no-data-text textC">
                    请先
                    <span class="add-panel-text" @click="showPanelDialog">设置</span>
                    面板信息！
                </p>
                <p v-else class="custom-no-data-text textC">
                    啥都没有，请
                    <span class="add-panel-text" @click="showBlockDialog">添加</span>
                    一个面板块吧！
                </p>
            </div>
        </div>
        <!-- 选择块弹窗 -->
        <!-- <select-block-dialog
            :visible.sync="blockDialogVisible"
            :data="selectBlocks"
            @submit="addBlocks">
        </select-block-dialog> -->
        <!-- 面板编辑弹窗 -->
        <panel-setting-dialog
            :visible.sync="panelSettingVisible"
            :field="panelDialogInfo"
            @submit="savePanelInfo">
        </panel-setting-dialog>
        <!-- 分享块列表 -->
        <share-table-tab
            :visible.sync="blockDialogVisible"
            :tabActive.sync="activeName"
            :tabsData="tabsData"
            optionResPath="bean"
            title="添加块"
            @submit="addBlocks">
        </share-table-tab>
        <!-- 用户块设置弹窗 -->
        <contents-edit-dialog
            title="多内容信息设置"
            :visible.sync="contentsEditVisiable"
            :contents="contents"
            @submit="saveContentsInfo">
        </contents-edit-dialog>
    </div>
</template>

<script>
import NoData from '@/components/frame/NoData'
import LayoutDrag from '@/views/panel/panelDesigner/components/LayoutDrag'
import BlockDesignView from '@/views/panel/panelDesigner/components/BlockDesignView'
import PanelSetting from '@/views/panel/panelDesigner/components/PanelSetting'
import SearchSingleSelect from '@/components/frame/Common/MSearch/components/SingleSelect'
import ContentStatusSwitch from '@/views/panel/panelDesigner/components/ContentStatusSwitch.vue'
// import selectBlockDialog from './component/SelectBlock'
import ContentsEditDialog from './components/ContentsEditDialog'
import ShareTableTab from '@/views/panel/components/ShareTableTab'
import globalStyle from '@/styles/theme.scss'
import { Find_Share_Block } from '@/api/frame/panel/user.js'
import PanelConsts from '@/views/panel/js/consts'
import { Deep_Clone } from '@/utils/clone'
import {
    Delete_Block,
    Save_Panel_Pos,
    Save_Panel_Info,
    Save_Block,
    Down_File_Url,
    Change_Hide_Status,
    Upload_File,
    User_Layouts,
} from '@/api/frame/panel/designer'
import { Get_Panel_Config } from '@/components/frame/Panel/PanelCode/utils.js'
import GlobalConst from '@/service/global-const.js'
export default {
    components: {
        NoData,
        LayoutDrag,
        BlockDesignView,
        [PanelSetting.name]: PanelSetting,
        // [selectBlockDialog.name]: selectBlockDialog,
        ShareTableTab,
        ContentsEditDialog,
        SearchSingleSelect,
        ContentStatusSwitch,
    },
    computed: {
        // 面板编辑按钮
        settingBtns () {
            let result = [{ id: 'editPanel', name: '设置面板', click: 'showPanelDialog', icon: 'setting', type: 'warning' }]
            if (this.panelId) {
                result = [
                    { id: 'savePos', name: '保存位置', click: 'handlerSavePos', icon: 'save', type: 'success' },
                    { id: 'addPanel', name: '添加面板', click: 'showPanelDialog', icon: 'plus', type: 'primary' },
                    { id: 'editPanel', name: '设置面板', click: 'showPanelDialog', icon: 'setting', type: 'warning' },
                    { id: 'addBlock', name: '添加块', click: 'showBlockDialog', icon: 'add', type: 'primary' },
                    { id: 'view', name: '面板预览', click: 'viewPanel', icon: 'view', type: 'success' },
                ]
            }
            return result
        },
        // 设置的内容信息
        contents () {
            let res = this.blockInfo?.[this.contentKey] || []
            return res
        },
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
    },
    data () {
        return {
            // 没有块内容图片
            noPanelImg: require('@/assets/image/frame/noPanel.png'),
            loading: false,
            // 多内容设置弹窗
            contentsEditVisiable: false,
            // 块设置按钮
            blockBtns: [
                {
                    id: 'settingBlock', title: '设置内容', click: 'showContentDialog', icon: 'setting',
                    show: function (i) {
                        return i.contentType === '1'
                    }
                },
                { id: 'deleteBlock', title: '删除块', click: 'delBlock', icon: 'delete' },
            ],
            panelId: '',
            // 面板列表
            panelList: [],
            // 用户有权限的块列表
            selectBlocks: [],
            // 展示在页面上的块
            blockList: [],
            // 块选择弹窗显示参数
            blockDialogVisible: false,
            // 全局样式
            globalStyle,
            // 面板设置弹窗显示参数
            panelSettingVisible: false,
            // 面板表单初始值
            panelInfo: {},
            // 正在编辑中的面板块信息
            blockInfo: {},

            // 面板弹窗绑定信息
            panelDialogInfo: {},
            // 添加块面板数据
            tabsData: [
                { text: '我创建的', id: 'create', request: Find_Share_Block, params: { shareType: 0 } },
                { text: '分享给我的', id: 'share', request: Find_Share_Block, params: { shareType: 1 } },
            ],
            activeName: 'create',
            contentKey: GlobalConst.panel.contentKey
        }
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
        // 更改当前展示面板
        changePanel (e) {
            let oldId = e?.old?.value || ''
            let newId = e?.new?.value || ''
            if (oldId !== newId) {
                this.getBlocksData(this.panelId)
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
        showBlockDialog () {
            this.blockDialogVisible = true
        },
        // 删除面板块
        delBlock (info) {
            this.$confirm('确认删除该面板块吗！', '提示', {
                confirmButtonText: '确 定',
                cancelButtonText: '取 消',
                type: 'warning'
            }).then(() => {
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
        // 预览面板
        viewPanel () {
            let routeUrl = this.$router.resolve({
                path: `/panel/view/${this.panelId}`,
            })
            window.open(routeUrl.href, '_blank')
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
            return { max_y: block_max_yh.y, max_yh, max_x }
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
        savePosition () {
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
                Save_Panel_Pos({ block: JSON.stringify(params), panelId: this.panelId }).then(res => {
                    resolve(res)
                }).catch(err => {
                    resolve({hasOk: false})
                })
            })
        },
        // 添加展示块
        addBlocks (blocks, btn) {
            // 保存块
            btn.loading = true
            let list = []
            for (let i = 0; i < blocks.length; i++) {
                // 设置位置信息
                let item = this.setBlockPos(blocks[i])
                this.blockList.push(item)
                let tempItem = Deep_Clone(item)
                // 组装保存的参数
                let params = this.setSaveBlockParams(tempItem)
                list.push(this.saveBlockInfo(params))
            }
            Promise.all(list).then(res => {
                this.blockDialogVisible = false
                btn.loading = false
                this.$message.success('添加成功')
            }).catch(err => {
                btn.loading = false
                this.$message.warning(err)
            })
        },
        // 组装保存块的参数
        setSaveBlockParams (block) {
            let boxW = this.boxSize().width
            PanelConsts.Not_Save.forEach(key => {
                delete block[key]
            })
            let contents = block[this.contentKey] || []
            let blockParams = Object.assign(block, {
                newBlock: false,
                col: block.x / boxW * 100,
                row: block.y,
                sizex: block.w / boxW * 100,
                sizey: block.h,
                maxsizex: 100,
                [this.contentKey]: contents,
                contents,
                contentsStr: JSON.stringify(contents),
            })
            // 组装保存参数
            return {
                panelId: this.panelId,
                isNew: 0,
                block: JSON.stringify(blockParams)
            }
        },
        // 重新组装块信息
        setBlockPos (block) {
            let boxW = this.boxSize().width
            let { max_y, max_x, max_yh } = this.getMaxPosBlock()
            block = {
                ...block,
                x: 0,
                y: 0,
                w: block.sizex * boxW / 100,
                h: block.sizey,
            }
            if (block.maxsizex !== 100) {
                // 优先块宽度、块高度、上边距、右边距、下边距、左边距
                let containerWidth = ((block.sizex / block.maxsizex) * boxW) - ((Math.round(block.maxsizex / block.sizex) - 1) * 10)
                block = {
                    ...block,
                    w: containerWidth,
                    h: (50 * (block.sizey) - 10),
                }
            }
            if (max_yh) { // 块的高度不能为0，所以，如果max_yh不存在，则说明没有块
                // 新增插入块的宽度
                let new_box_w = block.w
                let space = 10 // 新增块与现有块的间隙
                if ((boxW - max_x - new_box_w) >= space) {  // 最大位置块仍有空间存放当前需加入的面板时
                    block.x = (max_x + space)
                    block.y = max_y
                } else {  // 最大块右侧没有空间，另起一行进行存放
                    block.y = max_yh + space
                }
            }
            return block
        },
        // 保存块内容
        saveBlockInfo (params) {
            return new Promise((resolve, reject) => {
                Save_Block(params).then(res => {
                    if (res.hasOk) {
                        resolve(res)
                    } else {
                        reject(res.tip || res.message || '添加失败')
                    }
                }).catch(err => {
                    reject('添加失败，请刷新后重试')
                })
            })
        },
        // 保存更改后的内容信息
        saveContentsInfo (contents, btn) {
            btn.loading = true
            let params = this.setSaveBlockParams(this.blockInfo)
            // 获取当前的滚动条高度，以便重新请求块信息后，将滚动条重置为当前编辑处
            let tem_scroll_top = this.$refs?.dragRef?.$el?.scrollTop
            this.saveBlockInfo(params).then(res => {
                btn.loading = false
                if (res.hasOk) {
                    this.$message.success('保存成功')
                    this.reload(this.panelId, tem_scroll_top)
                    this.contentsEditVisiable = false
                } else {
                    this.$message.error(res.tip || res.message || '保存失败')
                }
            }).catch(err => {
                this.$message.error('保存失败')
            })
        },
        // 打开块编辑弹窗
        showContentDialog (info, btn) {
            this.blockInfo = Deep_Clone(info.block)
            this.contentsEditVisiable = true
        },
        // 打开面板弹窗
        showPanelDialog (params, btn) {
            if (btn.id === 'addPanel') {
                this.panelDialogInfo = {}
            } else {
                this.panelDialogInfo = Deep_Clone(this.panelInfo)
            }
            this.panelSettingVisible = true
        },
        // 保存面板信息
        async savePanelInfo (params, btn) {
            // this.panelInfo = params
            // this.getThemeJs(params.pageId)
            // this.panelSettingVisible = false
            this.$set(btn, 'loading', true)
            // 如果背景图片有数据，先保存背景图片，拿到attachId
            if (params.backgroundImg && params.backgroundImg[0] instanceof File) {
                let img_res = await Upload_File({ file: params.backgroundImg[0] })
                if (img_res.attachId) {
                    // 背景图片最多只能长传一个，所以不需要在顾虑原来的backgroundImg是否有值
                    params.backgroundImg = img_res.attachId
                }
            } else {
                // 如果获取到的表单背景图片没有数据，存在两种情况
                // 1、使用原来的背景图片
                // 2、原来的背景图片已被删除（deleteAttach有值）
                let default_img = this.panelInfo.backgroundImg
                if (params.deleteAttach) {
                    let deleteAttach = JSON.parse(params.deleteAttach)
                    if (deleteAttach.backgroundImg) {
                        default_img = ''
                    }
                }
                params.backgroundImg = params.backgroundImg || default_img || ''
            }
            Save_Panel_Info(params).then(res => { // 保存面板
                this.$set(btn, 'loading', false)
                if (res.hasOk) {
                    this.panelSettingVisible = false
                    this.$message.success('保存成功')
                    // 如果返回回来的id！== this.panelId 说明是新增
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
    },
    async mounted () {
        // 获取面板下拉列表
        await this.getPanelList()
        this.panelId = this.$route.query.id ? this.$route.query.id : this.panelList?.[0]?.id
        // 请求用户可选块
        this.getBlocksData()
    }
}
</script>

<style lang="scss" scoped>
.user-designer {
    display: flex;
    height: 100%;
    flex-direction: column;
    .setting-bar {
        padding: 10px;
        background-color: #fff;
        flex: none;
        display: flex;
        .setting-btns {
            margin-left: 10px;
        }
    }
    .panel-container {
        // 32是按钮的高度，20是上下间距
        flex: 1;
        margin-top: 10px;
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
    // 暂无数据
    .no-data-box {
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        /deep/ .bd-no-data {
            height: auto;
            position: static;
            transform: translateY(0);
            margin-top: -80px !important;
        }
        .custom-no-data-text {
            font-size: 15px;
            color: #9EA9B2;
            margin-top: -40px;
            .add-panel-text {
                position: relative;
                color: #ffa200;
                font-size: 18px;
                cursor: pointer;
                padding: 0 8px;
            }
        }
    }
}
.wh-100 {
    width: 100%;
    height: 100%;
}
</style>