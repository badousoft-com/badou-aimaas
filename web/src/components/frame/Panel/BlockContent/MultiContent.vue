<!--
 * @FilePath: @/components/frame/Panel/BlockContent/MultiContent.vue
 * @Description: 多内容
-->
<template>
    <div class="multi-content">
        <!-- 轮播展示 -->
        <el-carousel
            v-if="switchType === '1'"
            class="carousel-box h-per-100"
            :interval="switchTime">
            <el-carousel-item
                v-for="(c, c_index) in contentList"
                :key="c_index">
                <bd-content-item
                    :ref="c.refName"
                    :contentInfo="c"
                    v-bind="_attrs"
                    class="content-item_box h-per-100">
                </bd-content-item>
            </el-carousel-item>
        </el-carousel>
        <!-- 组合/tab展示 -->
        <div v-else-if="switchType === '3'" class="h-per-100">
            <el-tabs
                v-model="tabActive"
                @tab-click="tabClick"
                class="multi-content_tab">
                <el-tab-pane
                    v-for="(c, c_index) in contentList"
                    :key="c_index"
                    :label="c.name"
                    :name="String(c_index)"
                    class="h-per-100">
                    <span slot="label">
                        <bd-icon v-if="c.iconAttrs" v-bind="c.iconAttrs" class="title-icon"></bd-icon>
                        {{c.name}}
                    </span>
                    <bd-content-item
                        :ref="c.refName"
                        :contentInfo="c"
                        :showTitle="false"
                        v-bind="_attrs"
                        class="content-item_box h-per-100">
                    </bd-content-item>
                </el-tab-pane>
            </el-tabs>
        </div>
        <!-- 平铺展示 -->
        <div v-else class="tile-show h-per-100">
            <div
                v-for="(g, g_index) in groupContents"
                :key="g_index"
                :style="{height: `calc(${g.groupHeight}% - ${ g_index !== groupContents.length - 1 ? spaceSize : 0}px)`}"
                class="group_box">
                <bd-content-item
                    v-for="(c, c_index) in g.data"
                    :key="c_index"
                    :ref="c.refName"
                    :contentInfo="c"
                    v-bind="_attrs"
                    :style="{
                        width: `calc(${c._width}% - ${spaceSize}px`,
                        height: c._height ? `calc(${c._height}% - ${spaceSize}px)` : '100%',
                        marginRight: `${spaceSize}px`,
                        marginBottom: `${spaceSize}px`,
                    }"
                    :class="{'show-shadow': showShadow}"
                    class="content-item_box h-per-100">
                </bd-content-item>
            </div>
        </div>
    </div>
</template>

<script>
import ContentItem from '@/components/frame/Panel/BlockContent/ContentItem.vue'
import GlobalConst from '@/service/global-const'
export default {
    inheritAttrs: false,
    components: {
        [ContentItem.name]: ContentItem,
    },
    props: {
        // 内容列表
        blockInfo: {
            type: Object,
            default: () => {}
        },
        // 切换类型（轮播展示：1，并列展示：2，组合tab展示：3）
        switchType: {
            type: String,
            default: '1'
        },
        theme: {
            type: Object,
            default: () => {}
        }
    },
    data: () => ({
        // tab 当前项
        tabActive: '0'
    }),
    computed: {
        // 间隔边距
        spaceSize () {
            if (this.theme.noSpace) {
                return 0
            } else {
                return parseFloat(this.theme.spaceSize || GlobalConst.panel.spaceSize) * 2
            }
        },
        // 需要传递给内容组件的属性
        _attrs () {
            return {
                showTitle: this.blockInfo.showContentTitle,
                theme: this.theme,
                ...this.$attrs,
            }
        },
        // 内容列表
        contentList () {
            let res = (this.blockInfo?.contentList || []).filter(o => !o.isHide).map(i => {
                let customSetting = {}
                if (i.chartOptions) {
                    try {
                        customSetting = JSON.parse(i.chartOptions || '') || {}
                    } catch (error) {
                        console.log(error)
                    }
                }
                return Object.assign({}, i, customSetting)
            })
            return res
        },
        // 切换时间
        switchTime () {
            return Math.max(3000, this.blockInfo.pitchTime)
        },
        // 组别内容
        groupContents () {
            // 组别对象（方便组装）
            let groupInfo = {}
            // 自定义配置的内容总高度
            let customH = 0
            this.contentList.forEach(c => {
                let contentItem = {
                    ...c,
                    // 内容宽度
                    _width: eval(c.contentRatio) * 100,
                }
                if(!contentItem._width || String(contentItem._width) === 'NaN') {
                    contentItem._width = 1 / 3 * 100
                }
                // 如果当前组别还没有值
                if (!groupInfo[c.chartsGroup]) {
                    groupInfo[c.chartsGroup] = {
                        // 组别名称
                        groupName: c.chartsGroup,
                        // 组别高度百分比
                        groupHeight: c.groupHeight,
                        // 组别内容数据
                        data: [contentItem]
                    }
                    customH += (c.groupHeight || 0)
                } else {
                    groupInfo[c.chartsGroup].data.push(contentItem)
                }
            })
            // 将组别对象转化成数组
            let groupList = Object.values(groupInfo)
            // 找出组别高度为0的组别（需自动计算的组别）
            let autoHeightGroup = groupList.filter(o => !o.groupHeight)
            // 剩余高度
            let residueH = !(100 - customH) ? 100 : (100 - customH)
            groupList.forEach(g => {
                // 组别高度重新赋值（需自动计算的组别高度）
                if (!g.groupHeight) {
                    g.groupHeight = residueH / autoHeightGroup.length
                }
                // 判断当前组别中的内容是否存在换行现象（占行比例之和>1）
                let contentWArr = g.data.map(o => o._width)
                // 占行比例总和
                let numW = Math.ceil(eval(contentWArr.join('+')) / 100)
                if (numW > 1) {
                    g.data = g.data.map(d => {
                        d._height = 1 / numW * 100
                        return d
                    })
                } 
            })
            return groupList
        },
        // 是否展示内容阴影
        showShadow () {
            return this.theme.showContentShadow
        },
    },
    methods: {
        // tab 被选中时触发
        tabClick (e) {
            let activeIndex = Number(e.name)
            let contentItem = this.contentList[activeIndex]
            if (contentItem) {
                let refName = contentItem.refName
                let scope = this.$refs[refName]?.[0] || {}
                if (typeof scope.resize === 'function') {
                    setTimeout(() => {
                        scope.resize()
                    }, 100)
                }
            }
        },
        /**
         * @description: 批量调用内容中的方法
         * @param {String} fnName：方法名
         * @param {Object} params：需传递的参数
         * @param {Array} refNames：内容的refName数据
         */
        handlerContents (fnName, params = {}, refNames = []) {
            if (!fnName) return
            let _refs = this.$refs || {}
            // 内容 的 refName 必须以 content 开头
            for (const _refName in _refs) {
                if (_refName.indexOf('content') === 0) {
                    // 获取到内容的ref
                    let contentRef = _refs[_refName][0] ? _refs[_refName][0] : _refs[_refName]
                    // 1. 当refNames不存在（调用所有）；2. refNames中存在该作用域
                    if (!refNames.length || ~refNames.indexOf(_refName)) {
                        typeof contentRef[fnName] === 'function' && contentRef[fnName](params)
                    }
                }
            }
        },
    }
}
</script>

<style lang="scss" scoped>
.multi-content::v-deep {
    width: 100%;
    height: 100%;
    background-color: var(--content-bg);
    overflow: hidden;
    // 平铺展示
    .tile-show {
        padding: 0 0 $padding $padding;
        .group_box {
            display: flex;
            flex-wrap: wrap;
            margin-top: 10px;
            .content-item_box {
                display: inline-flex;
                height: 100%;
                background-color: var(--content-bg);
                border-radius: $borderRadius;
            }
        }
        .show-shadow {
            box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.1);
        }
    }
    // tab 展示
    .multi-content_tab {
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        .el-tabs__header {
            flex: none;
            .el-tabs__nav-scroll {
                padding-left: $padding;
            }
        }
        .el-tabs__content {
            flex: 1;
        }
        // .title-icon {
        //     color: var(--primary-color);
        // }
    }
}
</style>