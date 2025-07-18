<template>
    <div class="watch-log-page">
        <div>
            <el-row :gutter="20">
                <el-col :span="6">
                    <div>
                        <el-statistic group-separator="," :precision="2" :value="value2" :title="title"></el-statistic>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div>
                        <el-statistic title="男女比">
                            <template slot="formatter">
                                456/2
                            </template>
                        </el-statistic>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div>
                        <el-statistic group-separator="," :precision="2" decimal-separator="." :value="value1"
                            :title="title">
                            <template slot="prefix">
                                <i class="el-icon-s-flag" style="color: red"></i>
                            </template>
                            <template slot="suffix">
                                <i class="el-icon-s-flag" style="color: blue"></i>
                            </template>
                        </el-statistic>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div>
                        <el-statistic :value="like ? 521 : 520" title="Feedback">
                            <template slot="suffix">
                                <span @click="like = !like" class="like">
                                    <i class="el-icon-star-on" style="color:red" v-show="!!like"></i>
                                    <i class="el-icon-star-off" v-show="!like"></i>
                                </span>
                            </template>
                        </el-statistic>
                    </div>
                </el-col>
            </el-row>
        </div>
        <!-- 日志 -->
        <div class="log-info defaultBg">
            <div class="log-info__handlers">
                <m-form class="bd-module-form" noTitle :columnNum="3" :dataList.sync="options" @change="change">
                </m-form>
            </div>
            <div class="log-info__main">
                <div v-if="log" class="scrollBottom pointer" title="滚到底部" @click="scrollBottom">
                    <bd-icon name="download"></bd-icon>
                </div>
                <div class="pointer returnBottom" title="返回" @click="back">
                    <bd-icon name="back"></bd-icon>
                </div>
                <div ref="logPaneleRef" class="log-info__panel">
                    <running-text v-if="!log" :data="desc.start" :animation="!log && operateLog.length == 0">
                    </running-text>
                    <div v-if="operateLog.length === 0" class="log_panel">{{ log }}</div>
                    <div v-else>
                        <div v-for="(i, index) in operateLog" :key="index">
                            {{ i }}
                        </div>
                        <div v-if="currentFailNum === limitFailNum + 1" class="pointer dangerC" @click="start">
                            {{ desc.failManyTry }}
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <template v-if="!isHideEnvInstance">
            <div class="backBtn">
                <el-button @click="back" type="primary">返回</el-button>
            </div>
        </template>
    </div>
</template>
<script>
import MForm from '@/components/frame/Common/MForm/index'
import RunningText from '@/components/frame/Status/RunningText.vue'
import { Parse_Time } from '@/utils'
import { Get_Status } from "@/api/business/application/application"
import { Get_Instance } from '@/api/business/runInstance/runInstance'
import { S_Storage } from '@/utils/storage'
import ScopeMixin from '@/components/frame/ScopeMixin'
import NoData from '@/components/frame/NoData'

const fieldObj = {
    lineNum: 'lineNum',
    openRefresh: 'openRefresh',
    refreshTimeGap: 'refreshTimeGap'
}
export default {
    name: 'watch-log-page',
    components: {
        MForm,
        RunningText,
        [NoData.name]: NoData
    },
    mixins: [ScopeMixin],
    data() { // 定义页面变量
        return {
            // 应用ID
            appId: this.$route?.query?.appId || this.$options?.propsData?.appId || '',
            // appId: this.$route.query?.appId ? this.$route.query?.appId :  this.$options.propsData.appId,
            // 环境ID
            envId: '',
            // 环境信息
            envList: [],
            // 实例信息
            instanceList: [],
            // 实例ID
            instanceId: '',
            // 是否显示环境、实例选择框
            isHideEnvInstance: this.$options.propsData.isHideEnvInstance || false,
            // 日志栏信息参数
            // 定时器
            interval: null,
            // 计时器
            timer: null,
            //激活Key
            activeKey: this.$options.propsData.activeKey || false,
            // 日志信息
            log: '',
            // 表单字段
            options: [
                { type: 'text', dataType: 'int', name: fieldObj.lineNum, label: '展示行数', value: 2000 },
                { type: 'switch', name: fieldObj.openRefresh, label: '自动刷新', value: true, activeValue: true, inactiveValue: false },
                { type: 'text', dataType: 'int', name: fieldObj.refreshTimeGap, label: '刷新间隔(秒)', value: 3 }
            ],
            // 操作描述
            desc: {
                start: '准备获取日志',
                fail: '获取日志失败',
                reTry: '尝试重新获取日志',
                failManyTry: '多次请求失败，已自动关闭请求，点击此行可重新请求'
            },
            // 当前已失败的次数
            currentFailNum: 0,
            // 失败重触发次数的限制最高次数
            limitFailNum: 6,
            // 操作日志
            operateLog: [],
            // 是否正在请求中
            loading: false,
            // 刷新时间间隔最小
            minTimeGap: 1
        }
    },
    computed: {
        // 获取行数
        _lineNum() {
            return this.options.find(i => i.name === fieldObj.lineNum)?.value || 0
        },
        // 获取刷新时间间隔，保底1s
        _refreshTimeGap() {
            let _timeGap = this.options.find(i => i.name === fieldObj.refreshTimeGap)?.value || this.minTimeGap
            return Math.max(_timeGap, this.minTimeGap)
        }
    },
    methods: { // 定义函数
        // 获取当前项目下的环境信息
        async getStatus() {
        },
        // 获取当前环境下的运行实例
        async getInstanceData() {
            // let res = await Get_Status({ id: this.$route.query.id })

            // this.instanceList = res.Rows
            // if (this.isHideEnvInstance) {
            //     // 如果环境、实例选择框被隐藏，则使用传入的实例id
            //     this.instanceId = this.$options.propsData.id
            // } else {
            //      // 删除旧定时器
            //      clearInterval(this.interval)
            //     // 运行实例被点击时开始加载日志
            //     this.start()
            //     // // 打开页面时，自动获取第一个环境的第一个实例的日志
            //     // if (this.instanceList.length > 0) {
            //     //     this.instanceId = this.instanceList[0].id
            //     //     // 删除旧定时器
            //     //     clearInterval(this.interval)
            //     //     // 运行实例被点击时开始加载日志
            //     //     this.start()
            //     // }
            //     // if (this.instanceList.length === 0) {
            //     //     this.operateLog = []
            //     //     this.log = ''
            //     //     clearInterval(this.interval)
            //     //     clearTimeout(this.timer)
            //     //     this.log = '不存在运行中的实例!请发布应用!'
            //     //     return
            //     // }
            // }

        },
        // 下拉框选择改变
        selectChange() {
            this.getInstanceData()
        },
        // 运行实例item被点击
        instanceClick(id) {
            this.instanceId = id
            // 删除旧定时器
            clearInterval(this.interval)
            // 运行实例被点击时开始加载日志
            this.start()
        },
        /**
         * 值变更事件
         * @param {String} name 字段键名
         * @param {*} value 字段值
         * @param {Object} itemObj 字段对象
         * @param {Object} itemComponent vue实例
         */
        change(name, value, itemObj, itemComponent) {
            switch (name) {
                case fieldObj.lineNum:
                    // 删除旧定时器
                    clearInterval(this.interval)
                    // 重启
                    this.start()
                    break
                case fieldObj.openRefresh:
                    // this.options.find(i => i.name === fieldObj.refreshTimeGap).type = value ? 'text' : 'hidden'
                    value ? this.openTimer() : clearInterval(this.interval)
                    break
                case fieldObj.refreshTimeGap:
                    // 删除旧定时器
                    clearInterval(this.interval)
                    // 重启
                    this.start()
                    break
                default:
            }
        },
        // 加载日志
        getLog() {
            this.loading = true
            // 请求行数
            let _lineNum = this._lineNum
            // 请求时间间隔
            let _refreshTimeGap = this._refreshTimeGap
            let _req = Get_Status({ id: this.instanceId, logType: '0', size: _lineNum, flushTime: _refreshTimeGap })

            _req.then(res => {
                // 只要成功就设置当前失败次数为0
                this.currentFailNum = 0
                this.operateLog = []
                this.log = ''
                if (res.bean.doStatus == 3 || res.bean.doStatus == 2) {
                    this.log = res.bean.planMsg
                    // return
                }
                if (res.bean) {
                    if (res.bean === null) {
                        this.log = '该应用暂无日志记录！'
                        return
                    }
                    // 将返回字符串进行编码，避免直接渲染时html忽略掉换行符与空格
                    let resCode = encodeURIComponent(res.bean.planMsg)
                    // 定义编码后的换行符（等下要限制输出行数）
                    let lineFeedCode = '%0A'
                    // 根据换行符获取所有行数据数组
                    let resList = resCode.split(lineFeedCode)
                    // 获取日志数据长度
                    let len = resList.length
                    // 根据指定的输出行数要求，获取数组数据
                    if (len > this._lineNum) {
                        resList = resList.slice(len - this._lineNum + 1, len)
                    } else {
                        resList = resList.slice(0, len)
                    }
                    // 将数组数据组成字符串，然后再解码回去，最终将值更新给日志字段进行展示
                    this.log = decodeURIComponent(resList.join(lineFeedCode))
                    // 关闭正在请求的状态，允许下一次请求进行
                    this.loading = false
                    setTimeout(() => {
                        // 滚动至底部显示最新日志
                        this.scrollBottom()
                    })
                } else {
                    this.log = res.bean.planMsg
                }
            }).catch(() => {
                // 失败次数叠加1
                this.currentFailNum++
                // 判断当前失败重新请求次数是否已达峰值
                if (this.currentFailNum <= this.limitFailNum) {
                    this.AddoperateLog(this.desc.fail)
                    this.timer = setTimeout(() => {
                        this.AddoperateLog(`第${this.currentFailNum}次${this.desc.reTry}`)
                    }, 500)
                }
                // 关闭正在请求的状态，允许下一次请求进行
                this.loading = false
            })
        },
        /**
         * 添加操作日志
         * @params {String} data 日志内容
         */
        AddoperateLog(data) {
            this.operateLog.push(data)
        },
        openTimer() {
            this.interval = setInterval(() => {
                // :this.loading为true时表示当前正在请求，此时不执行新的请求
                if (this.loading) return
                // 判断当前用户是否向上滚动过（表示正在浏览），此时也不请求，避免数据请求后又滚动至底部
                if (this.isUserScaning()) return
                // 达到失败次数上限的时候则不再请求，避免重复请求过多次
                if (this.currentFailNum > this.limitFailNum) {
                    // 清除请求数据的时间选择器
                    clearInterval(this.interval)
                    return
                }
                if (this.activeKey != '') {
                    if (S_Storage.getItem(this.activeKey) == 'close') {
                        // 清除请求数据的时间选择器
                        clearInterval(this.interval)
                        clearTimeout(this.timer)
                        //清理缓存
                        S_Storage.removeItem(this.activeKey)
                        this.loading = true
                        return
                    }
                }
                // 除开以上特殊情况，进行加载日志
                this.getLog()
            }, this._refreshTimeGap * 1000)
            // 通过$once来监听定时器
            // 在beforeDestroy钩子触发时清除定时器
            this.$once('hook:beforeDestroy', () => {
                // 清除请求数据的时间选择器
                clearInterval(this.interval)
                clearTimeout(this.timer)
            })
        },
        // 启动
        start() {
            // 清空操作日志
            this.operateLog = []
            this.log = ''
            // 重置当前失败请求次数
            this.currentFailNum = 0
            // 启动定时器，定时请求日志信息
            this.openTimer()
        },
        // 滚动至底部，显示最新日志
        scrollBottom() {
            let _logPanelRef = this.$refs.logPaneleRef
            if (!_logPanelRef) return
            _logPanelRef.scrollTop = _logPanelRef.scrollHeight
        },
        // 判断用户当前是否正在浏览数据，使用滚动距离做判断，如果向上滚动一定距离表示正在查看问题，此时不再请求数据，只有当用户距离底部一定阙值时才触发请求
        isUserScaning() {
            // 获取滚动面板Element对象
            let _logPanelRef = this.$refs.logPaneleRef
            if (!_logPanelRef) return
            let { clientHeight, scrollHeight, scrollTop } = _logPanelRef
            // 判断当前用户是否向上滚动过数据，表示用户正在查阅，临界阙值为100
            return scrollHeight - scrollTop - clientHeight > 100
        },
        back() {
            this.pushPage({
                path: `/module/stander/list/maas_fine_tuning_modeln/placeholder`,
            })
        }
    },
    // 可访问当前this实例
    created() {
        this.getStatus()
    },
    // 挂载完成，可访问DOM元素
    mounted() {
        this.instanceId = this.$route?.query?.id
        this.start()
    },
    beforeDestroy() {
    },
    backyyy() {
        // this.$router.back()
        console.log(666);
    }
}
</script>
<style lang='scss' scoped>
.watch-log-page {
    overflow: hidden;
    padding: 10px;
    height: 100%;
    // display: flex;
    // flex-direction: column;
    // height: 100%;
}

.instance-nodata,
.instance {
    display: flex;
    flex-wrap: wrap;
    width: 100%;
    height: 150px;
    margin: 10px 0;
    border: 1px solid #ccc;
    border-radius: 4px;
    overflow: auto;

    .instance-item {
        display: flex;
        align-items: center;
        width: calc(32% - 6px);
        height: 80px;
        padding: 0 10px 0 20px;
        margin: 5px 9px 0 9px;
        border-radius: 4px;
        background-color: #f3f3f3;
        cursor: pointer;

        .icon {
            width: 40px;
            height: 40px;
            line-height: 40px;
            margin-right: 10px;
            border-radius: 4px;
            font-size: 20px;
            color: #fff;
            text-align: center;
            align-content: center;
            background-color: #306bff;
        }

        .instance-info-name {
            color: #000;

            p {
                margin: 5px;
            }
        }
    }
}

.instance-nodata {
    justify-content: center;

    .noDataTip {
        width: 22%;
        height: calc(100% - 56px);
        top: 50%;
    }
}

.log-info::v-deep {
    height: 100%!important;
    display: flex;
    flex-direction: column;
    height: 650px;
    flex: 1;

    .log-info__handlers {
        .bd-module-form {
            .secForm {
                .el-row {
                    &:last-child {
                        .el-col {
                            .el-form-item {
                                margin-bottom: 0;
                            }
                        }
                    }
                }
            }
        }
    }

    .returnBottom {
            position: absolute;
            top: 30px;
            right: 0;
            transform: translateX(-50%) translateY(-50%);
            background: #fff;
            border-radius: 50%;
            opacity: 0.8;
            transition: all 0.3s;

            .bd-icon {
                width: 3em;
                height: 3em;
            }

            &:hover {
                opacity: 1;
            }
        }

    .log-info__main {
        overflow: auto;
        // padding: $padding;
        flex: 1;
        align-content: stretch;
        position: relative;

        // ::-webkit-scrollbar-track-piece {
        //     background-color: rgba(0, 255, 8, 0.219);
        // }
        ::-webkit-scrollbar-thumb {
            background-color: #18f116;
        }

        // ::-webkit-scrollbar-thumb:hover {
        //     background-color: #fff;
        // }

        .scrollBottom {
            position: absolute;
            bottom: 0;
            right: 0;
            transform: translateX(-50%) translateY(-50%);
            background: #fff;
            border-radius: 50%;
            opacity: 0.8;
            transition: all 0.3s;

            .bd-icon {
                width: 3em;
                height: 3em;
            }

            &:hover {
                opacity: 1;
            }
        }

        .log-info__panel {
            height: 100%;
            overflow: auto;
            padding: $padding;
            border-radius: $borderRadius;
            background: #000;
            color: #18f118;
            width: 100%;

            .log_panel {
                white-space: pre-wrap;
            }
        }
    }

}

.envId {
    display: flex;
    justify-content: space-between;
}

.backBtn {
    margin-top: 10px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.like {
    cursor: pointer;
    font-size: 25px;
    display: inline-block;
}
</style>
