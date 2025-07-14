<template>
    <div style="height: 100%;">
        <div class="main-page">
            <div class="title">
                <el-card class="box-card">
                    <div slot="header" class="clearfix">
                        <span>任务节点-显卡信息监控</span>
                        <el-button style="float: right; font-size: 15px;" type="text"
                            icon="el-icon-s-operation">切换略缩</el-button>
                    </div>
                    <div class="detail-page" style="display: none;">
                        <el-card class="box-card">
                            <div slot="header" class="clearfix">
                                <span><i class="el-icon-data-analysis"></i>显卡0(2080ti-22GB)</span>
                                <!-- <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button> -->
                            </div>
                            <el-row>
                                <el-col :span="6">
                                    <div class="box-pie" id="temperatureChart"></div>
                                </el-col>
                                <el-col :span="6">
                                    <div class="box-pie" id="powerChart"></div>
                                </el-col>
                                <el-col :span="6">
                                    <div class="box-pie" id="useChart"></div>
                                </el-col>
                                <el-col :span="6">
                                    <div class="box-pie" id="cacheChart"></div>
                                </el-col>
                            </el-row>
                        </el-card>
                        <div style="height: 40%;" id="gpuCacheChange"></div>
                    </div>
                    <div class="list-page">
                        <el-card class="box-card">
                            <div slot="header" class="clearfix">
                                <span>GPU序号: 0</span>
                                <span>运行状态: 运行中</span>
                                <!-- <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button> -->
                            </div>
                            <el-row class="list-content">
                                <el-col :span="6" class="list-content-item">
                                    <el-row>
                                        <el-col :span="24">GPU</el-col>
                                    </el-row>
                                    <el-row>
                                        <el-col :span="12">
                                            <el-progress type="dashboard" :percentage="percentage"
                                                :color="colors"></el-progress>
                                        </el-col>
                                        <el-col :span="12">
                                            <p>总量</p>
                                            <p>2080mib</p>
                                            <p>已使用</p>
                                            <p>1890mib</p>
                                        </el-col>
                                    </el-row>
                                </el-col>
                                <el-col :span="6" class="list-content-item"></el-col>
                                <el-col :span="6" class="list-content-item"></el-col>
                                <el-col :span="6" class="list-content-item"></el-col>
                            </el-row>
                        </el-card>
                    </div>
                </el-card>
            </div>
        </div>

    </div>

</template>
<script>
import * as echarts from "echarts";
export default {
    data() {
        return {
            timer: null,
            commonColor: {
                normal: {
                    color: '#4ad2ff'
                }
            },
            percentage: 10,
            colors: [
                { color: '#f56c6c', percentage: 20 },
                { color: '#e6a23c', percentage: 40 },
                { color: '#5cb87a', percentage: 60 },
                { color: '#1989fa', percentage: 80 },
                { color: '#6f7ad3', percentage: 100 }
            ],
            options: [
                {
                    refs: 'temperatureChart',
                    title: {
                        show: true,
                        text: "GPU温度"
                    },
                    series: [
                        {
                            data: [
                                {
                                    value: 30
                                }
                            ],
                            type: 'gauge',
                            center: ['50%', '60%'],
                            startAngle: 200,
                            endAngle: -20,
                            min: 0,
                            max: 100,
                            splitNumber: 10,
                            itemStyle: {
                                color: '#FFAB91'
                            },
                            progress: {
                                show: true,
                                width: 30
                            },

                            pointer: {
                                show: false
                            },
                            axisLine: {
                                lineStyle: {
                                    width: 30
                                }
                            },
                            axisTick: {
                                distance: -45,
                                splitNumber: 5,
                                lineStyle: {
                                    width: 2,
                                    color: '#999'
                                }
                            },
                            splitLine: {
                                distance: -52,
                                length: 14,
                                lineStyle: {
                                    width: 3,
                                    color: '#999'
                                }
                            },
                            axisLabel: {
                                distance: -20,
                                color: '#999',
                                fontSize: 20
                            },
                            anchor: {
                                show: false
                            },
                            title: {
                                show: false
                            },
                            detail: {
                                valueAnimation: true,
                                width: '60%',
                                lineHeight: 30,
                                borderRadius: 8,
                                offsetCenter: [0, '-15%'],
                                fontSize: 50,
                                fontWeight: 'bolder',
                                formatter: '{value} °C',
                                color: 'inherit'
                            },
                            data: [
                                {
                                    value: 30
                                }
                            ]
                        },
                        {
                            type: 'gauge',
                            center: ['50%', '60%'],
                            startAngle: 200,
                            endAngle: -20,
                            min: 0,
                            max: 100,
                            itemStyle: {
                                color: '#FD7347'
                            },
                            progress: {
                                show: true,
                                width: 8
                            },

                            pointer: {
                                show: false
                            },
                            axisLine: {
                                show: false
                            },
                            axisTick: {
                                show: false
                            },
                            splitLine: {
                                show: false
                            },
                            axisLabel: {
                                show: false
                            },
                            detail: {
                                show: false
                            },
                            data: [
                                {
                                    value: 30
                                }
                            ]
                        }
                    ]
                },
                {
                    refs: 'powerChart',
                    title: {
                        show: true,
                        text: "GPU温度"
                    },
                    series: [
                        {
                            data: [
                                {
                                    value: 30
                                }
                            ],
                            type: 'gauge',
                            center: ['50%', '60%'],
                            startAngle: 200,
                            endAngle: -20,
                            min: 0,
                            max: 100,
                            splitNumber: 10,
                            itemStyle: {
                                color: '#FFAB91'
                            },
                            progress: {
                                show: true,
                                width: 30
                            },

                            pointer: {
                                show: false
                            },
                            axisLine: {
                                lineStyle: {
                                    width: 30
                                }
                            },
                            axisTick: {
                                distance: -45,
                                splitNumber: 5,
                                lineStyle: {
                                    width: 2,
                                    color: '#999'
                                }
                            },
                            splitLine: {
                                distance: -52,
                                length: 14,
                                lineStyle: {
                                    width: 3,
                                    color: '#999'
                                }
                            },
                            axisLabel: {
                                distance: -20,
                                color: '#999',
                                fontSize: 20
                            },
                            anchor: {
                                show: false
                            },
                            title: {
                                show: false
                            },
                            detail: {
                                valueAnimation: true,
                                width: '60%',
                                lineHeight: 30,
                                borderRadius: 8,
                                offsetCenter: [0, '-15%'],
                                fontSize: 50,
                                fontWeight: 'bolder',
                                formatter: '{value} °C',
                                color: 'inherit'
                            },
                            data: [
                                {
                                    value: 30
                                }
                            ]
                        },
                        {
                            type: 'gauge',
                            center: ['50%', '60%'],
                            startAngle: 200,
                            endAngle: -20,
                            min: 0,
                            max: 100,
                            itemStyle: {
                                color: '#FD7347'
                            },
                            progress: {
                                show: true,
                                width: 8
                            },

                            pointer: {
                                show: false
                            },
                            axisLine: {
                                show: false
                            },
                            axisTick: {
                                show: false
                            },
                            splitLine: {
                                show: false
                            },
                            axisLabel: {
                                show: false
                            },
                            detail: {
                                show: false
                            },
                            data: [
                                {
                                    value: 30
                                }
                            ]
                        }
                    ]
                },
                {
                    refs: 'useChart',
                    title: {
                        show: true,
                        text: "GPU温度"
                    },
                    series: [
                        {
                            data: [
                                {
                                    value: 30
                                }
                            ],
                            type: 'gauge',
                            center: ['50%', '60%'],
                            startAngle: 200,
                            endAngle: -20,
                            min: 0,
                            max: 100,
                            splitNumber: 10,
                            itemStyle: {
                                color: '#FFAB91'
                            },
                            progress: {
                                show: true,
                                width: 30
                            },

                            pointer: {
                                show: false
                            },
                            axisLine: {
                                lineStyle: {
                                    width: 30
                                }
                            },
                            axisTick: {
                                distance: -45,
                                splitNumber: 5,
                                lineStyle: {
                                    width: 2,
                                    color: '#999'
                                }
                            },
                            splitLine: {
                                distance: -52,
                                length: 14,
                                lineStyle: {
                                    width: 3,
                                    color: '#999'
                                }
                            },
                            axisLabel: {
                                distance: -20,
                                color: '#999',
                                fontSize: 20
                            },
                            anchor: {
                                show: false
                            },
                            title: {
                                show: false
                            },
                            detail: {
                                valueAnimation: true,
                                width: '60%',
                                lineHeight: 30,
                                borderRadius: 8,
                                offsetCenter: [0, '-15%'],
                                fontSize: 50,
                                fontWeight: 'bolder',
                                formatter: '{value} °C',
                                color: 'inherit'
                            },
                            data: [
                                {
                                    value: 30
                                }
                            ]
                        },
                        {
                            type: 'gauge',
                            center: ['50%', '60%'],
                            startAngle: 200,
                            endAngle: -20,
                            min: 0,
                            max: 100,
                            itemStyle: {
                                color: '#FD7347'
                            },
                            progress: {
                                show: true,
                                width: 8
                            },

                            pointer: {
                                show: false
                            },
                            axisLine: {
                                show: false
                            },
                            axisTick: {
                                show: false
                            },
                            splitLine: {
                                show: false
                            },
                            axisLabel: {
                                show: false
                            },
                            detail: {
                                show: false
                            },
                            data: [
                                {
                                    value: 30
                                }
                            ]
                        }
                    ]
                },
                {
                    refs: 'cacheChart',
                    title: {
                        show: true,
                        text: "GPU温度"
                    },
                    series: [
                        {
                            data: [
                                {
                                    value: 30
                                }
                            ],
                            type: 'gauge',
                            center: ['50%', '60%'],
                            startAngle: 200,
                            endAngle: -20,
                            min: 0,
                            max: 100,
                            splitNumber: 10,
                            itemStyle: {
                                color: '#FFAB91'
                            },
                            progress: {
                                show: true,
                                width: 30
                            },

                            pointer: {
                                show: false
                            },
                            axisLine: {
                                lineStyle: {
                                    width: 30
                                }
                            },
                            axisTick: {
                                distance: -45,
                                splitNumber: 5,
                                lineStyle: {
                                    width: 2,
                                    color: '#999'
                                }
                            },
                            splitLine: {
                                distance: -52,
                                length: 14,
                                lineStyle: {
                                    width: 3,
                                    color: '#999'
                                }
                            },
                            axisLabel: {
                                distance: -20,
                                color: '#999',
                                fontSize: 20
                            },
                            anchor: {
                                show: false
                            },
                            title: {
                                show: false
                            },
                            detail: {
                                valueAnimation: true,
                                width: '60%',
                                lineHeight: 30,
                                borderRadius: 8,
                                offsetCenter: [0, '-15%'],
                                fontSize: 50,
                                fontWeight: 'bolder',
                                formatter: '{value} °C',
                                color: 'inherit'
                            },
                            data: [
                                {
                                    value: 30
                                }
                            ]
                        },
                        {
                            type: 'gauge',
                            center: ['50%', '60%'],
                            startAngle: 200,
                            endAngle: -20,
                            min: 0,
                            max: 100,
                            itemStyle: {
                                color: '#FD7347'
                            },
                            progress: {
                                show: true,
                                width: 8
                            },

                            pointer: {
                                show: false
                            },
                            axisLine: {
                                show: false
                            },
                            axisTick: {
                                show: false
                            },
                            splitLine: {
                                show: false
                            },
                            axisLabel: {
                                show: false
                            },
                            detail: {
                                show: false
                            },
                            data: [
                                {
                                    value: 30
                                }
                            ]
                        }
                    ]
                },
                {
                    title: {
                        show: true,
                        text: "GPU显存变更历史"
                    },
                    refs: "gpuCacheChange",
                    // grid: { // 让图表占满容器
                    //     top: "13%",
                    //     left: "3%",
                    //     right: "0px",
                    //     bottom: "3%"
                    // },
                    grid: {
                        left: "2%",
                        containLabel: true
                    },

                    xAxis: {
                        show: false,
                        type: 'category',
                        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],

                    },
                    yAxis: {
                        type: 'value',
                        axisLabel: {
                            fontSize: 30
                        }
                    },
                    dataZoom: [
                        {
                            show: true,
                            type: 'inside', // 内置滚动条，也可以使用'slider'类型在图表外侧显示滚动条
                            start: 0,
                            end: 100 // 初始显示数据的范围比例，可以根据需要调整
                        }
                    ],
                    series: [
                        {
                            data: [820, 932, 901, 934, 1290, 1330, 1320, 3000, 30],
                            type: 'line',
                            smooth: true,
                            interval: 0,
                            label: {
                                show: true,
                                textStyle: {
                                    fontSize: 30
                                }
                            },
                            axisLabel: {
                                interval: 0,
                                rotate: 40
                            }
                        }
                    ]
                }
            ],
            first: false
            // 配置可视化图形
        };
    },
    //离开页面删除定时
    beforeDestroy() {
        clearInterval(this.timer)
        this.timer = null
    },
    mounted() {
        this.getchart()
    },
    methods: {
        loadChartData() {
            this.post(`${this.BASEURL}/project/maas/tuningmodeln/tuningmodelnedit/getModelBuildStatus.do`, { id: this.$route.query.id }).then(res => {
                if (res?.hasOk) {
                    this.options.forEach((item, index) => {
                        if (item.dataField && item.timeField) {
                            item.series[0].data = res.bean[item.dataField]
                            item.xAxis.data = res.bean[item.timeField]
                            if (this.first) {
                                item.chart.setOption(item, true)
                                window.addEventListener("resize", () => {
                                    item.chart.resize();
                                });
                            }
                        }
                    })
                    if (this.first == false) {
                        this.getchart();
                        this.first = true
                        this.timer = setInterval(() => {
                            this.loadChartData()
                        }, 10000);
                        this.$once('hook:beforeDestroy', () => {   //再通过事件监听，监听到 组件销毁 后，再执行关闭计时器。
                            clearInterval(this.timer);
                        })
                    }
                } else {
                    let defaultTip = '服务未启动!'
                    this.$message.error(res?.message ? `${defaultTip}:${res.message}` : defaultTip)
                }
            })
        },
        getchart() {
            // let c = echarts.init(this.$refs[this.options[0].refs]);

            // c.setOption(this.options[0])
            this.options.forEach((item, index) => {
                // 引用chart并初始化
                item.chart = echarts.init(document.getElementById(item.refs));
                // 使用刚指定的配置项和数据显示图表。
                item.chart.setOption(item)
                //自适应
                window.addEventListener("resize", () => {
                    item.chart.resize();
                });
            })

        },
    },
};

</script>
<style></style>