<template>
  <card class="card-stats">
    <div class="row">
      <div class="col-5" v-if="$slots.header">
        <div class="icon-big text-center">
          <slot name="header"></slot>
        </div>
      </div>
      <div class="col-7" v-if="$slots.content || title">
        <div class="numbers">
          <slot name="content">
            <p v-if="subTitle" class="card-category">{{subTitle}}</p>
            <h4 v-if="title" class="card-title">{{title}}</h4>
          </slot>
        </div>
      </div>
    </div>
    <div class="stats" slot="footer" v-if="$slots.footer">
      <slot name="footer"></slot>
    </div>
  </card>
</template>
<script>
  import Card from './Card.vue'
  export default {
    name: 'stats-card',
    components: {
      Card
    },
    props: {
      title: String,
      subTitle: String
    }
  }

</script>
<style>

</style>
