import Vue from 'vue'
import SvgIcon from '@/components/frame/Icon/index.vue' // svg component

// register globally
Vue.component('bd-icon', SvgIcon)

const req = require.context('./svg', true, /\.svg$/)
const requireAll = requireContext => requireContext.keys().map(requireContext)
requireAll(req)
