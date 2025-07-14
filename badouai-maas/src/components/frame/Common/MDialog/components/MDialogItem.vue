<template>
    <div>
        <m-form
            v-if="type ==='form'"
            class="dialog-form"
            :ref="id"
            v-bind="$attrs"
            v-on="$listeners">
        </m-form>
        <m-list
            v-if="type ==='list'"
            class="padding dialog-list"
            :ref="id"
            v-bind="$attrs"
            v-on="$listeners">
        </m-list>
        <m-stander-list
            v-else-if="type ==='standerList'"
            class="dialog-stander-list"
            :ref="id"
            v-bind="$attrs"
            v-on="$listeners">
        </m-stander-list>
        <m-stander-edit
            v-else-if="type ==='standerEdit'"
            class="dialog-stander-edit"
            :ref="id"
            v-bind="$attrs"
            v-on="$listeners">
        </m-stander-edit>
        <module-list-code
            v-else-if="type ==='standerListCode'"
            class="dialog-stander-list-code"
            :ref="id"
            v-bind="$attrs"
            v-on="$listeners">
        </module-list-code>
        <module-edit-code
            v-else-if="type ==='standerEditCode'"
            class="dialog-stander-edit-code"
            :ref="id"
            v-bind="$attrs"
            v-on="$listeners">
        </module-edit-code>
        <module-list-tree
            v-else-if="type ==='standerTreeCode'"
            class="dialog-tree-list"
            :ref="id"
            v-bind="$attrs"
            v-on="$listeners">
        </module-list-tree>
    </div>
</template>

<script>
import MForm from '@/components/frame/Common/MForm/index.vue'
import MList from '@/components/frame/Common/MList/index.vue'
import MStanderList from '@/components/frame/Module/ModuleList/index'
import MStanderEdit from '@/components/frame/Module/ModuleEdit/index'
import ModuleListCode from '@/components/frame/Module/ModuleListCode'
import ModuleEditCode from '@/components/frame/Module/ModuleEditCode'
import ModuleListTree from '@/components/frame/Module/ModuleListTree/index.vue'
export default {
    inheritAttrs: false,
    components: {
        MForm,
        MList,
        MStanderList,
        MStanderEdit,
        [ModuleListCode.name]: ModuleListCode,
        [ModuleEditCode.name]: ModuleEditCode,
        [ModuleListTree.name]: ModuleListTree
    },
    props: {
        type: {
            type: String,
            default: 'form'
        },
        id: {
            type: String,
            default: ''
        }
    }
}
</script>

<style lang="scss" scoped>
.dialog-tree-list::v-deep {
    .tree-model {
        background-color: rgba($color: #000000, $alpha: 0);
    }
}
</style>
