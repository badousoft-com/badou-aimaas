<template>
  <div class="bootstrap-switch bootstrap-switch-wrapper bootstrap-switch-animate" :class="switchClass">
    <div class="bootstrap-switch-container" @click="triggerToggle()">
      <span class="bootstrap-switch-handle-on " :class="`bootstrap-switch-${color}`">
        <slot name="on">
            {{onText}}
        </slot>
      </span>
      <span class="bootstrap-switch-label"></span>
      <span class="bootstrap-switch-handle-off bootstrap-switch-default">
        <slot name="off">
            {{offText}}
        </slot>
      </span>
    </div>
  </div>
</template>
<script>
  export default {
    name: 'l-switch',
    props: {
      value: [Array, Boolean, Number, String],
      onText: String,
      offText: String,
      color: {
        type: String,
        default: '',
        validator: (value) => {
          return ['', 'blue', 'azure', 'green', 'orange', 'red', 'purple', 'black'].includes(value)
        }
      },
      isView: Boolean
    },
    computed: {
      switchClass () {
        let base = 'bootstrap-switch-'
        let state = this.model === '1' ? 'on' : 'off'
        let classes = base + state
        if (this.color) {
          classes = `${classes} ${base}${this.color}`
        }
        return classes
      },
      model: {
        get () {
          return this.value
        },
        set (value) {
          this.$emit('input', value)
        }
      }
    },
    methods: {
      triggerToggle () {
          if (this.isView) {
            this.model = this.value
            return
          }
          if (!this.model || this.model === '0') {
              this.model = '1'
          } else {
              this.model = '0'
          }
      }
    },
    created() {
        this.model = '0'
    }
  }
</script>
<style>
</style>
