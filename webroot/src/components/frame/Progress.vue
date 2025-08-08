<template>
  <div class="progress" :style="`height: ${height}px`">
    <div v-if="values.length === 0" class="progress-bar"
         :class="computedClasses"
         role="progressbar"
         :aria-valuenow="value"
         aria-valuemin="0"
         aria-valuemax="100"
         :style="`width: ${value}%;`">
        <slot>
          <span v-if="showText">
             {{value}}%
          </span>
        </slot>
    </div>
    <div v-else
          v-for="progress in values" class="progress-bar"
          :key="progress.value"
         :class="progressClasses(progress)"
         :style="`width: ${progress.value}%;`">
        <span v-if="progress.text">
           {{progress.text}}
        </span>
    </div>
  </div>
</template>
<script>
  export default {
    name: 'l-progress',
    props: {
      striped: Boolean,
      showText: Boolean,
      animated: Boolean,
      height: {
        type: Number,
        default: 4
      },
      type: {
        type: String,
        default: 'default'
      },
      value: {
        type: Number,
        default: 0,
        validator: (value) => {
          return value >= 0 && value <= 100
        }
      },
      values: {
        type: Array,
        default: () => []
      }
    },
    computed: {
      computedClasses () {
        return [`bg-${this.type}`, {'progress-bar-striped': this.striped}, {'progress-bar-animated': this.animated}]
      }
    },
    methods: {
      progressClasses (progress) {
        return [`bg-${progress.type}`, {'progress-bar-striped': progress.striped}, {'progress-bar-animated': progress.animated}]
      }
    }
  }
</script>
<style>
</style>
