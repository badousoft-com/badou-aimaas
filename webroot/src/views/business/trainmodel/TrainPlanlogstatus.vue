<template>
    <!-- display: flex; -->

    <div style="overflow: scroll;height: 900px;
            flex-wrap: wrap;
            justify-content: space-between;">
        <div class="box-pie" ref="lossChart"></div>
        <div class="box-pie" ref="lrChart"></div>
        <div class="box-pie" ref="epochChart"></div>
        <div class="box-pie" ref="gradNormChart"></div>
        <div class="box-pie" ref="etaChart"></div>
        <div class="box-pie" ref="dataTimeChart"></div>
        <div class="box-pie" ref="memoryChart"></div>
        <div class="pointer returnBottom" title="返回" @click="back">
            <bd-icon name="back"></bd-icon>
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
            options: [
                {
                    refs: 'lossChart',
                    echarts: {},
                    dataField: 'losss_data',
                    timeField: 'losss_time',
                    title: {
                        text: 'loss(总损失曲线图)',
                        subtext: '通常是分类损失和边界框回归损失的总和.越小训练效果越好',
                        padding: [10, 0, 100, 170],
                        textStyle: {
                            fontSize: 20,
                        }
                    },
                    grid: {
                        left: '10%',
                        bottom: '30%',
                    },
                    xAxis: {
                        type: "category",
                        data: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
                        axisLabel: {
                            interval: 0,
                            rotate: 40,
                        },

                    },
                    yAxis: {
                        type: "value",
                        backgroundStyle: {
                            color: "rgba(0,0,255)"
                        },
                    },
                    series: [
                        {
                            data: [120, 200, 150, 80, 70, 110, 130],
                            type: "bar",
                            showBackground: true,
                            // backgroundStyle: {
                            //     color: "blue",
                            // },
                            itemStyle: {
                                normal: {
                                    color: 'blue'
                                }
                            },
                            //上面显示数值
                            // label: {
                            //     show: true, //开启显示
                            //     position: 'top', //在上方显示
                            //     // formatter: '{c}%',//显示百分号
                            //     textStyle: { //数值样式
                            //         color: 'black',//字体颜色
                            //         fontSize: 8//字体大小
                            //     }
                            // }

                        },
                    ],
                    tooltip: {
                        trigger: 'item',
                        axisPointer: {
                            type: 'shadow'
                        },
                        backgroundColor: '#fff', // 悬浮框背景色
                        borderColor: '#000', // 悬浮框边框颜色
                        borderWidth: 1, // 悬浮框边框宽度
                        textStyle: { // 悬浮框文字样式
                            color: '#000',
                            fontSize: 12
                        }
                    }
                },
                {
                    refs: 'etaChart',
                    echarts: {},
                    dataField: 'etas_data',
                    timeField: 'etas_time',
                    title: {
                        text: 'eta(剩余时间)',
                        subtext: '通常是分类损失和边界框回归损失的总和.越小训练效果越好',
                        padding: [10, 0, 100, 170],
                        textStyle: {
                            fontSize: 20,
                        }
                    },
                    grid: {
                        left: '10%',
                        bottom: '30%'
                    },
                    xAxis: {
                        type: "category",
                        data: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
                        axisLabel: {
                            interval: 0,
                            rotate: 40,
                        },

                    },
                    yAxis: {
                        type: "time",
                    },
                    series: [
                        {
                            data: ['2024-04-06 10:01:25', '2024-04-06 10:01:27', '2024-04-06 10:02:26', '2024-04-06 10:03:25', '2024-04-06 10:05:25',
                                '2024-04-06 10:07:25', '2024-04-06 11:09:25'],
                            type: "bar",
                            showBackground: true,
                            backgroundStyle: {
                                color: "rgba(180, 180, 180, 0.2)",
                            },
                            itemStyle: {
                                normal: {
                                    color: 'blue'
                                }
                            }
                        },
                    ],
                },
                {
                    refs: 'dataTimeChart',
                    echarts: {},
                    dataField: 'data_times_data',
                    timeField: 'data_times_time',
                    title: {
                        text: 'data_time(数据加载所需的时间)',
                        subtext: '以秒为单位。这表示在每个迭代中，模型花费了多少时间来加载训练数据',
                        padding: [10, 0, 100, 170],
                        textStyle: {
                            fontSize: 20,
                        }
                    },
                    grid: {
                        left: '10%',
                        bottom: '30%'
                    },
                    xAxis: {
                        type: "category",
                        data: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
                        axisLabel: {
                            interval: 0,
                            rotate: 40,
                        },

                    },
                    yAxis: {
                        type: "value",
                    },
                    series: [
                        {
                            data: [120, 200, 150, 80, 70, 110, 130],
                            type: "bar",
                            showBackground: true,
                            backgroundStyle: {
                                color: "rgba(180, 180, 180, 0.2)",
                            },
                            itemStyle: {
                                normal: {
                                    color: 'blue'
                                }
                            }
                        },
                    ],
                },
                {
                    refs: 'lrChart',
                    echarts: {},
                    dataField: 'lrs_data',
                    timeField: 'lrs_time',
                    title: {
                        text: 'lr(学习率)',
                        subtext: '(learning rate),用于控制模型参数在每次迭代更新时的步长大小。这个值通常会随着训练的进行而逐渐调整.',
                        padding: [10, 0, 100, 170],
                        textStyle: {
                            fontSize: 20,
                        }
                    },
                    grid: {
                        left: '10%',
                        bottom: '30%'
                    },
                    xAxis: {
                        type: "category",
                        data: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
                        axisLabel: {
                            interval: 0,
                            rotate: 40,
                        },

                    },
                    yAxis: {
                        type: "value",
                    },
                    series: [
                        {
                            data: [120, 200, 150, 80, 70, 110, 130],
                            type: "bar",
                            showBackground: true,
                            backgroundStyle: {
                                color: "rgba(180, 180, 180, 0.2)",
                            },
                            itemStyle: {
                                normal: {
                                    color: 'blue'
                                }
                            }
                        },
                    ],
                },
                {
                    refs: 'epochChart',
                    echarts: {},
                    dataField: 'epoch_data',
                    timeField: 'epoch_time',
                    title: {
                        text: 'epoch(训练过程接中数据将被轮回多少次)',
                        subtext: '训练过程中当一个完整的数据集通过了神经网络一次并且返回了一次，这个过程称为一个epoch，网络会在每个epoch结束时报告关于模型学习进度的调试信息',
                        padding: [10, 0, 100, 170],
                        textStyle: {
                            fontSize: 20,
                        }
                    },
                    grid: {
                        left: '10%',
                        bottom: '30%'
                    },
                    xAxis: {
                        type: "category",
                        data: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
                        axisLabel: {
                            interval: 0,
                            rotate: 40,
                        },

                    },
                    yAxis: {
                        type: "value",
                    },
                    series: [
                        {
                            data: [120, 200, 150, 80, 70, 110, 130],
                            type: "bar",
                            showBackground: true,
                            backgroundStyle: {
                                color: "rgba(180, 180, 180, 0.2)",
                            },
                            itemStyle: {
                                normal: {
                                    color: 'blue'
                                }
                            }
                        },
                    ],
                },
                {
                    refs: 'memoryChart',
                    echarts: {},
                    dataField: 'memorys_data',
                    timeField: 'memorys_time',
                    title: {
                        text: 'memory(内存占用情况)',
                        subtext: '通常以 MB 或 GB 为单位，表示模型当前使用的内存大小',
                        padding: [10, 0, 100, 170],
                        textStyle: {
                            fontSize: 20,
                        }
                    },
                    grid: {
                        left: '10%',
                        bottom: '30%'
                    },
                    xAxis: {
                        type: "category",
                        data: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
                        axisLabel: {
                            interval: 0,
                            rotate: 40,
                        },

                    },
                    yAxis: {
                        type: "value",
                    },
                    series: [
                        {
                            data: [120, 200, 150, 80, 70, 110, 130],
                            type: "bar",
                            showBackground: true,
                            backgroundStyle: {
                                color: "rgba(180, 180, 180, 0.2)",
                            },
                            itemStyle: {
                                normal: {
                                    color: 'blue'
                                }
                            }
                        },
                    ],
                },
                {
                    refs: 'gradNormChart',
                    echarts: {},
                    dataField: 'grad_norms_data',
                    timeField: 'grad_norms_time',
                    title: {
                        text: 'grad_norm(梯度的范数)',
                        subtext: '(gradient_norm),用于衡量梯度的大小，通常用于监控梯度爆炸或梯度消失的情况',
                        padding: [10, 0, 100, 170],
                        textStyle: {
                            fontSize: 20,
                        }
                    },
                    grid: {
                        left: '10%',
                        bottom: '30%'
                    },
                    xAxis: {
                        type: "category",
                        data: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
                        axisLabel: {
                            interval: 0,
                            rotate: 40,
                        },

                    },
                    yAxis: {
                        type: "value",
                    },
                    series: [
                        {
                            data: [120, 200, 150, 80, 70, 110, 130],
                            type: "bar",
                            showBackground: true,
                            backgroundStyle: {
                                color: "rgba(180, 180, 180, 0.2)",
                            },
                            itemStyle: {
                                normal: {
                                    color: 'blue'
                                }
                            }
                        },
                    ],
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
        this.loadChartData()
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
                        if (this.timer == null) {
                            this.timer = setInterval(() => {
                                this.loadChartData()
                            }, 2000);
                        }
                    }
                } else {
                    let defaultTip = '服务未启动!'
                    this.$message.error(res?.message ? `${defaultTip}:${res.message}` : defaultTip)
                }
            })
        },
        getchart() {
            this.options.forEach((item, index) => {
                item.tooltip = {
                    trigger: 'item',
                    axisPointer: {
                        type: 'shadow'
                    },
                    backgroundColor: '#fff', // 悬浮框背景色
                    borderColor: '#000', // 悬浮框边框颜色
                    borderWidth: 1, // 悬浮框边框宽度
                    textStyle: { // 悬浮框文字样式
                        color: '#000',
                        fontSize: 12
                    }
                }
                // 引用chart并初始化
                item.chart = echarts.init(this.$refs[item.refs]);
                // 使用刚指定的配置项和数据显示图表。
                item.chart.setOption(item)
                //自适应
                window.addEventListener("resize", () => {
                    item.chart.resize();
                });
            })

        },
        back() {
            this.pushPage({
                path: `/module/stander/list/maas_fine_tuning_modeln/placeholder`,
                title: '上一页'
            })
        }
    },
};
</script>
<style scoped>
.box-pie {
    height: 300px;
    flex: 1 0 calc(50% - 10px);
}

.returnBottom {
    position: absolute;
    top: 6%;
    right: 0;
    transform: translateX(-50%) translateY(-50%);
    background-color: #c0c4cc;
    border-radius: 50%;
    opacity: 0.8;
    transition: all 0.3s;

    .bd-icon {
        font-size: 20px;
        width: 3em;
        height: 3em;
    }

    &:hover {
        opacity: 1;
    }
}
</style>