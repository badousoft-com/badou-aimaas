<script>
import GlobalConst from '@/service/global-const'

// 定义options下项的值键名
const Id_Name = GlobalConst.dicOption.idName
// 定义options下项的文本键名
const Text_Name = GlobalConst.dicOption.textName
// 定义其他自定义输入项的键名
const Has_Other = GlobalConst.form.hasOtherOptionKeyName
export default {
    props: {
        // 是否展示其他选项，提供自定义输入选项内容
        // !!!使用该功能，要求模型必须配置数据来源Key为text，整个组件产出的值为文本，而非id值
        [Has_Other]: {
            type: Boolean,
            default: false
        },
    },
    data () { // 定义页面变量
        return {
            // 其他选项的数据
            initOtherCustomOption: {
                // id值
                [Id_Name]: '',
                // 文本值
                [Text_Name]: '其他',
                // 前一次选中值
                initValue: '',
            }
        }
    },
    computed: {
        _hasOther () {
            return this[Has_Other]
        },
        // 其他自定义输入的选项
        _otherCustomOption () {
            return this.filterExtra(this.tempOptions, true)
        },
    },
    methods: { // 定义函数
        /**
         * 手动填写其他项时的focus事件，此时更新存储起其他项的前一次值
         */
        focusExtra () {
            this.initOtherCustomOption.initValue = this._otherCustomOption[this.idName]
        },
        /**
         * 过滤出非其他的选项
         * @param {Array} data 所有选项(含其他)
         * @param {Boolean} isExtra 是否选出为其他项的选项
         * @returns 选出非其他项时为Array，选出其他项时为Object
         */
        filterExtra (data, isExtra = false) {
            if (!(this._hasOther && data && data.length > 1)) return data
            if (!isExtra) {
                return data.slice(0, data.length - 1) // {Array} 非其他的项集合
            }
            return data[data.length - 1] // {Object} 其他项
        },
    }
}
</script>