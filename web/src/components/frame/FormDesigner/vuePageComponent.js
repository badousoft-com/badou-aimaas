export default function Get_Vue_Page_Component (formData) { return `
<template>
    <div>
        <bd-form 
            :ref="formData.id"
            v-bind="formData">
        </bd-form>
        <div class="fixBottomBtn" v-fixBottom>
            <el-button 
                v-for='(i, index) in opBtnList' 
                :key="index"  
                v-btnBg="i"
                type="primary"
                @click='exeMethod(i.method)'>
                <bd-icon :name="i.icon"></bd-icon>
                {{ i.name }}
            </el-button>
        </div>
    </div>
</template>

<script>
import BdForm from '@/components/frame/Common/MForm/index.vue'
export default {
    components: {
        [BdForm.name]: BdForm
    },
    data () {
        return {
            formData: ${formData},
            opBtnList: [
                { name: '保存', icon: 'save', method: 'saveForm', params: '' },
                { name: '关闭', icon: 'close', method: 'popPage', params: '' }
            ]
        }
    },
    methods: {
        exeMethod (id) {
            this[id]()
        },
        saveForm () {
            this.$refs[this.formData.id].validateForm().then(res => {
                alert(JSON.stringify(res))
            })
        },
    }
}
</script>
<style scoped lang="scss">
</style>
`
}