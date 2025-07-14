<!--
 * @Description: 用户面板块设置组件
-->
<template>
    <div class="contents-edit-dialog">
        <bd-dialog
            :visible.sync="_visible"
            :title="title"
            :handlerList="btnList">
            <contents-edit ref="contentsEdit" :tableData.sync="contents"></contents-edit>
        </bd-dialog>
    </div>
</template>

<script>
import Dialog from '@/components/frame/Dialog/index.vue'
import ContentsEdit from '@/views/panel/components/ContentsEdit'
export default {
    name: 'contents-edit-dialog',
    components: {
        [Dialog.name]: Dialog,
        ContentsEdit,
    },
    computed: {
        _visible: {
            get () {
                return this.visible
            },
            set (val) {
                this.$emit('update:visible', val)
            }
        },
    },
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        title: {
            type: String,
            default: ''
        },
        contents: {
            type: Array,
            require: true
        }
    },
    data () {
        let that = this
        return {
            // 按钮
            btnList: [
                {
                    id: 'submit', name: '确定', icon: 'save', loading: false, type: 'primary',
                    click: function (btnObj) {
                        that.submit(btnObj)
                    }
                }
            ],
        }
    },
    methods: {
        submit (btn) {
            this.$refs.contentsEdit.checkForm().then(() => {
                this.$emit('submit', this.contents, btn)
            })
        }
    }
}
</script>

<style lang="scss" scoped>

</style>