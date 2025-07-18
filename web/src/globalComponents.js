import Card from './components/frame/Cards/Card.vue'
/**
 * You can register global components here and use them as a plugin in your main Vue instance
 */

const GlobalComponents = {
    install (Vue) {
        Vue.component('card', Card)
    }
}

export default GlobalComponents
