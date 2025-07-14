<template>
  <div class="card card-circle-chart" :data-background-color="color">
    <div class="card-header text-center">
      <h5 class="card-title">{{title}}</h5>
      <p class="description">{{description}}</p>
    </div>
    <div class="card-content">
      <div class="chart-circle" :data-percent="percentage" ref="chart">{{percentage}}%</div>
    </div>
  </div>
</template>
<script>
  export default{
    name: 'circle-chart-card',
    props: {
      color: {
        type: String,
        default: 'blue'
      },
      percentage: {
        type: Number,
        default: 0
      },
      title: String,
      description: String
    },
    data () {
      return {
        chartId: 'no-id',
        chart: null
      }
    },
    methods: {
      updateChartId () {
        var currentTime = new Date().getTime().toString()
        var randomInt = this.getRandomInt(0, currentTime)
        this.chartId = `div_${randomInt}`
      },
      getRandomInt (min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min
      },
      renderChart (EasyPieChart) {
        /* eslint-disable no-new */
        this.chart = new EasyPieChart(this.$refs.chart, {
          lineWidth: 6,
          size: 160,
          scaleColor: false,
          trackColor: 'rgba(255,255,255,.25)',
          barColor: '#FFFFFF',
          animate: ({duration: 2000, enabled: true})
        })
      }
    },
    async mounted () {
      this.updateChartId()
      let EasyPieChart = await import('easy-pie-chart')
      this.renderChart(EasyPieChart)
    },
    watch: {
      percentage: function (newVal) {
        if (this.chart) {
          this.chart.update(newVal)
        }
      }
    }
  }
</script>
<style>
</style>
