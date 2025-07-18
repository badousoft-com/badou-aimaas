
export default {
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'add', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'edit', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'delete', name: '导入', icon: 'import', type: 'primary', isHide: true }
    ],
    paramsBeforeRequest: function (params) {
        // params.serverId = this.$route.params.id
        // this:指向src/components/frame/Common/MList/index.vue页面作用域
        return params
    },
    // dataUrl: function () {
    // this:指向moduleList/index.vue所在页面作用域
    // return `${this.BASEURL}/project/server/servergpulist/getServerGpu`
    // },
    renderField: {
        // 字段键名
        usedGraphicsMemory: {
            /**
             * 格式化展示
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前表格项值
             * @param {Number} index 所在行下角标
             * @param {Object} fieldObj 字段所在列的配置数据
             */
            formatter: function (row, column, cellValue, index, fieldObj) {
                // this: 作用域指代核心列表[mList/index]所在页面作用域
                return `<span>${cellValue}/${row.maxGraphicsMemory}MiB</span>`
            }
        },
        // 字段键名
        fanSpeedPer: {
            /**
             * 格式化展示
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前表格项值
             * @param {Number} index 所在行下角标
             * @param {Object} fieldObj 字段所在列的配置数据
             */
            formatter: function (row, column, cellValue, index, fieldObj) {
                // this: 作用域指代核心列表[mList/index]所在页面作用域
                return `<span>${cellValue}%</span>`
            }
        },
        // 字段键名
        serverGpuList: {
            /**
             * 格式化展示
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前表格项值
             * @param {Number} index 所在行下角标
             * @param {Object} fieldObj 字段所在列的配置数据
             */
            formatter: function (row, column, cellValue, index, fieldObj) {
                // this: 作用域指代核心列表[mList/index]所在页面作用域
                return `<span style='color: #007BFF;'>点击查看</span>`
            },        // 点击事件（若没有可以不写，去除整块click）
            click: function (row, column, cellValue, index, fieldObj) {
                let dataArray = JSON.parse(cellValue)
                let changeRow = dataArray.map(device => {
                    return {
                      name: device.name,
                      type: `${device.type} ${device.name}`,
                      currentPowerDissipation: `${device.currentPowerDissipation}/${device.maxPowerDissipation}W`,
                      fanSpeedPer: `${device.fanSpeedPer}%`,
                      usageRate: `${device.usageRate}%`,
                      usedGraphicsMemory: `${device.usedGraphicsMemory}/${device.maxGraphicsMemory}MB`,
                      temperature: `${device.temperature}℃`,
                      performanceStatus: `P${device.performanceStatus}`,
                      status: device.status,
                      orderNum: device.orderNum
                    };
                  });
                let btnList = [
                    {
                        name: '取消',
                        type: 'danger',
                        icon: 'cancel',
                        click: function () {
                            // 关闭弹窗表单
                            this.$dialog.close()
                        }
                    }
                ]
                this.$dialog.init({
                    title: '线程信息',
                    type: 'list',
                    // width: 1200,
                    // height: 300,
                    isSelection: false,
                    id: 'serverGpuHistoryList',
                    fieldList: [
                        { name: 'orderNum', label: '显卡序号', },
                        { name: 'type', label: '名称', },
                        { name: 'temperature', label: '温度', },
                        { name: 'currentPowerDissipation', label: '功耗', },
                        { name: 'usedGraphicsMemory', label: '显存', },
                        { name: 'usageRate', label: '利用率', },
                        { name: 'fanSpeedPer', label: '风扇转速', },
                        { name: 'performanceStatus', label: '性能模式.P0最高', },
                    ],
                    handlerList: btnList,
                    data: changeRow
                })
            }
        },
        // 字段键名
        threadMsg: {
            /**
             * 格式化展示
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前表格项值
             * @param {Number} index 所在行下角标
             * @param {Object} fieldObj 字段所在列的配置数据
             */
            formatter: function (row, column, cellValue, index, fieldObj) {
                // this: 作用域指代核心列表[mList/index]所在页面作用域
                return `<span style='color: #007BFF;'>点击查看</span>`
            },        // 点击事件（若没有可以不写，去除整块click）
            click: function (row, column, cellValue, index, fieldObj) {
                let btnList = [
                    {
                        name: '取消',
                        type: 'danger',
                        icon: 'cancel',
                        click: function () {
                            // 关闭弹窗表单
                            this.$dialog.close()
                        }
                    }
                ]
                this.$dialog.init({
                    title: '线程信息',
                    type: 'list',
                    // width: 1200,
                    // height: 300,
                    isSelection: false,
                    id: 'serverGpuHistoryList',
                    fieldList: [
                        { name: 'uid', label: '进程所属用户ID', },
                        { name: 'pid', label: '进程ID', },
                        { name: 'c', label: '进程的CPU占用率', },
                        { name: 'stime', label: '进程启动时间', },
                        { name: 'time', label: '进程使用的CPU时间', },
                        { name: 'cmd', label: '启动进程的命令', },
                        { name: 'ppid', label: '父进程ID', },
                        { name: 'tty', label: '进程所属的终端', },
                    ],
                    handlerList: btnList,
                    data: JSON.parse(cellValue)
                })
            }
        },
        // 字段键名
        usageRate: {
            /**
             * 格式化展示
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前表格项值
             * @param {Number} index 所在行下角标
             * @param {Object} fieldObj 字段所在列的配置数据
             */
            formatter: function (row, column, cellValue, index, fieldObj) {
                // this: 作用域指代核心列表[mList/index]所在页面作用域
                return `<span>${cellValue}%/100%</span>`
            }
        },
        // 字段键名
        temperature: {
            /**
             * 格式化展示
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前表格项值
             * @param {Number} index 所在行下角标
             * @param {Object} fieldObj 字段所在列的配置数据
             */
            formatter: function (row, column, cellValue, index, fieldObj) {
                // this: 作用域指代核心列表[mList/index]所在页面作用域
                return `<span>${cellValue}C</span>`
            }
        },
        // 字段键名
        performanceStatus: {
            /**
             * 格式化展示
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前表格项值
             * @param {Number} index 所在行下角标
             * @param {Object} fieldObj 字段所在列的配置数据
             */
            formatter: function (row, column, cellValue, index, fieldObj) {
                // this: 作用域指代核心列表[mList/index]所在页面作用域
                return `<span>P${cellValue}</span>`
            }
        },
        // 字段键名
        currentPowerDissipation: {
            /**
             * 格式化展示
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前表格项值
             * @param {Number} index 所在行下角标
             * @param {Object} fieldObj 字段所在列的配置数据
             */
            formatter: function (row, column, cellValue, index, fieldObj) {
                // this: 作用域指代核心列表[mList/index]所在页面作用域
                return `<span>${cellValue}W/${row.maxPowerDissipation}W</span>`
            }
        },
        // 字段键名
        status: {
            /**
             * 格式化展示
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前表格项值
             * @param {Number} index 所在行下角标
             * @param {Object} fieldObj 字段所在列的配置数据
             */
            formatter: function (row, column, cellValue, index, fieldObj) {
                // this: 作用域指代核心列表[mList/index]所在页面作用域
                return `<span>${row.usedGraphicsMemory > 3 ? '在用' : '空闲'}</span>`
            }
        }
    }
}