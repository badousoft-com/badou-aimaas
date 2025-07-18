<template>
    <div style="height: 100%;">
        <module-edit-code style="height: 100%;" ref="moduleEditCode" :mdCode="mdCode" :detailId="detailId" isView>
        </module-edit-code>
    </div>
</template>

<script>
import ModuleEditCode from '@/components/frame/Module/ModuleEditCode'

export default {
    components: {
        [ModuleEditCode.name]: ModuleEditCode
    },
    data() {
        return {
            mdCode: 'maas_fine_tuning_evaluation_instan',
            detailId: '402888889182610601918263b6820013'
        }
    },
    methods: {

    },
    mounted() {
    }
};
</script>