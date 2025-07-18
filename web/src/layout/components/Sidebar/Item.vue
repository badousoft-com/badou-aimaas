<script>
export default {
    name: 'MenuItem',
    functional: true,
    props: {
        icon: {
            type: String,
            default: ''
        },
        title: {
            type: String,
            default: ''
        }
    },
    render (h, context) {
        const { icon, title } = context.props
        const vnodes = []
        // if (icon) {
        vnodes.push(<bd-icon name={icon} class="bdMenuIcon"></bd-icon>)
        // }
        if (title) {
            vnodes.push(<span slot='title'>{(title)}</span>)
        }
        return vnodes
    }
}
</script>
<style lang="scss">
    .bdMenuIcon {
        margin-right: 4px;
    }
</style>
