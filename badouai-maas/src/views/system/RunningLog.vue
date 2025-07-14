<template>
    <div class="log-info defaultBg">
        <div class="log-info__handlers">
            <m-form
                class="bd-module-form"
                noTitle
                :columnNum="3"
                :dataList.sync="options"
                @change="change">
            </m-form>
        </div>
        <div class="log-info__main">
            <div
                v-if="log"
                class="scrollBottom pointer"
                title="滚到底部"
                @click="scrollBottom">
                <bd-icon name="download"></bd-icon>
            </div>
            <div ref="logPaneleRef" class="log-info__panel">
                <running-text
                    v-if="!log"
                    :data="desc.start"
                    :animation="!log && operateLog.length == 0">
                </running-text>
                <div v-if="operateLog.length === 0" class="log_panel">{{log}}</div>
                <div v-else>
                    <div
                        v-for="(i, index) in operateLog"
                        :key="index">
                        {{i}}
                    </div>
                    <div
                        v-if="currentFailNum === limitFailNum + 1"
                        class="pointer dangerC"
                        @click="start">
                        {{desc.failManyTry}}
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import MForm from '@/components/frame/Common/MForm/index'
import RunningText from '@/components/frame/Status/RunningText.vue'
import { Get_System_Log } from '@/api/frame'
import { Format_Date } from '@/utils/time'

const fieldObj = {
    lineNum: 'lineNum',
    openRefresh: 'openRefresh',
    refreshTimeGap: 'refreshTimeGap'
}
export default {
    name: 'watch-log-page',
    components: {
        MForm,
        RunningText
    },
    data () { // 定义页面变量
        return {
            // 定时器
            timer: null,
            // 日志信息
            log: '',
            // 表单字段
            options: [
                { type: 'text', dataType: 'int', name: fieldObj.lineNum, label: '展示行数', value: 800 },
                { type: 'switch', name: fieldObj.openRefresh, label: '自动刷新', value: true, activeValue:true, inactiveValue:false},
                { type: 'text', dataType: 'int', name: fieldObj.refreshTimeGap, label: '刷新间隔', value: 3000 }
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
            minTimeGap: 2000
        }
    },
    computed: {
        // 获取行数
        _lineNum () {
            return this.options.find(i => i.name === fieldObj.lineNum)?.value || 0
        },
        // 获取刷新时间间隔，保底2s
        _refreshTimeGap () {
            let _timeGap = this.options.find(i => i.name === fieldObj.refreshTimeGap)?.value || this.minTimeGap
            return Math.min(_timeGap, this.minTimeGap)
        }
    },
    methods: { // 定义函数
        /**
         * 值变更事件
         * @param {String} name 字段键名
         * @param {*} value 字段值
         * @param {Object} itemObj 字段对象
         * @param {Object} itemComponent vue实例
         */
        change (name, value, itemObj, itemComponent) {
            switch (name) {
                case fieldObj.lineNum:
                    // 删除旧定时器
                    clearInterval(this.timer)
                    // 重启
                    this.start()
                    break
                case fieldObj.openRefresh:
                    // this.options.find(i => i.name === fieldObj.refreshTimeGap).type = value ? 'text' : 'hidden'
                    value ? this.openTimer() : clearInterval(this.timer)
                    break
                case fieldObj.refreshTimeGap:
                    // 删除旧定时器
                    clearInterval(this.timer)
                    // 重启
                    this.start()
                    break
                default:
            }
        },
        // 加载日志
        getLog () {
            this.loading = true
            // 由于接口的特殊性，在本地需要使用代理，所以这里根据环境区分使用api
            let _req = process.env.NODE_ENV !== 'development' ?
                Get_System_Log({}) :
                this.get(`/proxy/actuator/logfile`, {})
            _req.then(res => {
                // 只要成功就设置当前失败次数为0
                this.currentFailNum = 0
                this.operateLog = []
                if (!res) return
                // 将返回字符串进行编码，避免直接渲染时html忽略掉换行符与空格
                let resCode = encodeURIComponent(res)
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
            }).catch(() => {
                // 失败次数叠加1
                this.currentFailNum++
                // 判断当前失败重新请求次数是否已达峰值
                if (this.currentFailNum <= this.limitFailNum) {
                    this.AddoperateLog(this.desc.fail)
                    setTimeout(() => {
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
        AddoperateLog (data) {
            this.operateLog.push(`${Format_Date('datetime')}: ${data}`)
        },
        openTimer () {
            this.timer = setInterval(() => {
                // :this.loading为true时表示当前正在请求，此时不执行新的请求
                if (this.loading) return
                // 判断当前用户是否向上滚动过（表示正在浏览），此时也不请求，避免数据请求后又滚动至底部
                if (this.isUserScaning()) return
                // 达到失败次数上限的时候则不再请求，避免重复请求过多次
                if (this.currentFailNum > this.limitFailNum) {
                    // 清除请求数据的时间选择器
                    clearInterval(this.timer)
                    return
                }
                // 除开以上特殊情况，进行加载日志
                this.getLog()
            }, this._refreshTimeGap)
            // 通过$once来监听定时器
            // 在beforeDestroy钩子触发时清除定时器
            this.$once('hook:beforeDestroy', () => {
                // 清除请求数据的时间选择器
                clearInterval(this.timer)
            })
        },
        // 启动
        start () {
            // 清空操作日志
            this.operateLog = []
            // 重置当前失败请求次数
            this.currentFailNum = 0
            // 启动定时器，定时请求日志信息
            this.openTimer()
        },
        // 滚动至底部，显示最新日志
        scrollBottom () {
            let _logPanelRef = this.$refs.logPaneleRef
            if (!_logPanelRef) return
            _logPanelRef.scrollTop = _logPanelRef.scrollHeight
        },
        // 判断用户当前是否正在浏览数据，使用滚动距离做判断，如果向上滚动一定距离表示正在查看问题，此时不再请求数据，只有当用户距离底部一定阙值时才触发请求
        isUserScaning () {
            // 获取滚动面板Element对象
            let _logPanelRef = this.$refs.logPaneleRef
            if (!_logPanelRef) return
            let { clientHeight, scrollHeight, scrollTop } = _logPanelRef
            // 判断当前用户是否向上滚动过数据，表示用户正在查阅，临界阙值为100
            return scrollHeight - scrollTop - clientHeight > 100
        }
    },
    // 可访问当前this实例
    created () {},
    // 挂载完成，可访问DOM元素
    mounted () {
        this.start()
    }
}
</script>
<style lang='scss' scoped>
.log-info::v-deep {
    display: flex;
    flex-direction: column;
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
    .log-info__main {
        overflow: auto;
        padding: $padding;
        flex: 1;
        align-content: stretch;
        position: relative;
        .scrollBottom {
            position: absolute;
            bottom: 0;
            right: 0;
            transform: translateX(-50%) translateY(-50%);
            background: #fff;
            border-radius: 50%;
            opacity: .8;
            transition: all .3s;
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
            background:#000;
            color:#18f118;
            width: 100%;
            .log_panel {
                white-space: pre-wrap;
            }
        }
    }
}
</style>
