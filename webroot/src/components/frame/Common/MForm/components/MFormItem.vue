<template>
    <div>
        <bd-text
            v-if="formType ==='text'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-text>
        <bd-textarea 
            v-else-if="formType ==='textarea'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-textarea>
        <bd-radio 
            v-else-if="formType ==='radio'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-radio>
        <bd-checkbox 
            v-else-if="formType ==='checkbox'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-checkbox>
        <bd-select 
            v-else-if="formType ==='select'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-select>
        <bd-date
            v-else-if="formType ==='date'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-date>
        <bd-rich-text 
            v-else-if="formType ==='richText'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-rich-text>
        <bd-file
            v-else-if="formType ==='file'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-file>
        <bd-attach
            v-else-if="formType ==='attach'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-attach>
        <bd-image-picker
            v-else-if="formType ==='imagePicker'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-image-picker>
        <bd-auto-complete
            v-else-if="formType ==='autoComplete'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-auto-complete>
        <bd-cascader
            v-else-if="formType ==='cascader'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-cascader>
        <bd-address-picker
            v-else-if="formType ==='addressPicker'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-address-picker>
        <bd-color-picker
            v-else-if="formType ==='colorPicker'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-color-picker>
        <bd-number
            v-else-if="formType ==='number'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-number>
        <bd-switch
            class="form-switch"
            v-else-if="formType ==='switch'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-switch>
        <bd-code
            v-else-if="formType ==='code'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-code>
        <bd-code-mirror
            v-else-if="formType ==='codeMirror'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-code-mirror>
        <bd-icon-picker
            v-else-if="formType ==='iconPicker'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-icon-picker>
        <bd-path-picker
            v-else-if="formType ==='pathPicker'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-path-picker>
        <bd-address-book
            v-else-if="formType ==='addressBook'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-address-book>
        <bd-dialog-list
            v-else-if="formType ==='dialogList'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-dialog-list>
        <!-- 线路图测试 -->
        <bd-roadmap
            v-else-if="formType ==='roadmap'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-roadmap>
        <bd-rate 
            v-else-if="formType ==='rate'"
            v-bind="$attrs"
            v-on="$listeners">
        </bd-rate>
        <!-- 啥也没匹配到，就用text -->
        <bd-text
            v-else
            v-bind="$attrs"
            v-on="$listeners">
        </bd-text>
    </div>
</template>

<script>
import {
    Text,
    Textarea,
    Radio,
    Checkbox,
    Select,
    Date,
    RichText,
    File,
    Attach,
    ImagePicker,
    AutoComplete,
    Cascader,
    AddressPicker,
    ColorPicker,
    Number,
    Switch,
    Code,
    CodeMirror,
    IconPicker,
    PathPicker,
    AddressBook,
    DialogList,
    Roadmap,
    Rate,
} from '@/components/frame/Common/MForm/components/items/index'
export default {
    inheritAttrs: false,
    components: {
        [Text.name]: Text,
        [Textarea.name]: Textarea,
        [Radio.name]: Radio,
        [Checkbox.name]: Checkbox,
        [Select.name]: Select,
        [Date.name]: Date,
        [RichText.name]: RichText,
        [File.name]: File,
        [Attach.name]: Attach,
        [ImagePicker.name]: ImagePicker,
        [AutoComplete.name]: AutoComplete,
        [Cascader.name]: Cascader,
        [AddressPicker.name]: AddressPicker,
        [ColorPicker.name]: ColorPicker,
        [Number.name]: Number,
        [Switch.name]: Switch,
        [Code.name]: Code,
        [CodeMirror.name]: CodeMirror,
        [IconPicker.name]: IconPicker,
        [PathPicker.name]: PathPicker,
        [AddressBook.name]: AddressBook,
        [DialogList.name]: DialogList,
        [Roadmap.name]: Roadmap,
        [Rate.name]: Rate,
    },
    inheritAttrs: false,
    props: {
        formType: {
            type: String,
            default: 'text'
        }
    },
    computed: {
    },
    methods: {
    }
}
</script>

<style lang="scss" scoped>
</style>
