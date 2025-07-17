
<script>
import screenfull from 'screenfull'
// const fullScreenName = 'fullScreen-fill'

export default {
    data () {
        return {
            iconName: {
                full: 'fullScreen-fill',
                out: 'outFullScreen-fill',
            }
        }
    },
    mounted () {
        this.init()
    },
    beforeDestroy () {
        this.destroy()
    },
    methods: {
        toggle () {
            screenfull.toggle()
        },
        change () {
            let item = this.operateList.find(i => i.id === 'openFull')
            item.name =  item.name === this.iconName.full ? this.iconName.out : this.iconName.full
        },
        init () {
            screenfull.on('change', this.change)
        },
        destroy () {
            screenfull.off('change', this.change)
        },
    }
}
</script>

<style>

</style>