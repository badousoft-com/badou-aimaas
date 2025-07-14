<!--
 * @FilePath: @/views/default/GenerateKey.vue
 * @Description: 生成密钥
-->
<template>
    <div class="generate-key_container">
        <div class="info-box">
            <div class="btn-box">
                <bd-button
                    v-for="b in btnList"
                    :key="b.id"
                    type="primary"
                    v-bind="b"
                    @click="handleClick(b)"
                    class="btn-item">
                </bd-button>
            </div>
            <ol class="tip-box">
                <li v-for="(t, t_index) in tipList" :key="t_index">
                    <p v-html="t"></p>
                </li>
            </ol>
        </div>
        <m-form
            title=""
            labelWidth="50px"
            :columnNum="1"
            :dataList="keyContent"
            class="marT-10">
        </m-form>
    </div>
</template>

<script>
import { Generate_Key } from '@/api/frame/index.js'
import { Copy_To_Clip } from '@/utils/copy-clip'
import MForm from '@/components/frame/Common/MForm/index.vue'
export default {
    components: {
        MForm
    },
    data: () => ({
        tipList: [
            '生成的公钥请<span class="dangerC bold">同步保存到前端系统的publicKey.js文件</span>中。',
            '本功能生成的公密钥系统<span class="dangerC bold">不会存储</span>，请妥善保管。若有丢失，请重新生成。',
            '生成的公密钥请<span class="dangerC bold">手动保存</span>至后台配置的指定路径下',
            '本功能生成的公密钥仅用于当前系统安全校验用途，不做其他用途。',
            '公密钥必须<span class="dangerC bold">配对使用</span>，请将本功能生成的公密钥完整保存至相关文件，不要手动换行或者增加其他分隔符。',
            '必须遵循3、4、5要求进行配置，否则一旦开启接口安全校验，系统所有接口将无法访问。',
            '每个系统初始化后，都需要生成当前系统专用公密钥。<span class="dangerC bold">禁止</span>使用默认的公密钥，否则系统安全容易被破解。'
        ],
        btnList: [
            { id: 'generate', name: '生成密钥对', click: 'generateKey', loading: false },
            { id: 'copyPublicKey', name: '复制公钥', click: 'copyPublicKey', loading: false },
            { id: 'copyPrivateKey', name: '复制私钥', click: 'copyPrivateKey', loading: false },
        ],
        // 公私钥信息
        keyInfo: {},
    }),
    computed: {
        keyContent () {
            let res = [
                { type: 'textarea', name: 'publicKey', label: '公钥', readonly: true, placeholder: '请点击生成密钥对', value: this.keyInfo.publicKey || '' },
                { type: 'textarea', name: 'privateKey', label: '私钥', readonly: true, placeholder: '请点击生成密钥对', value: this.keyInfo.privateKey || '' },
            ]
            return res
        },
    },
    methods: {
        handleClick (b) {
            typeof this[b.click] && this[b.click](b)
        },
        // 生成密钥
        generateKey (btnObj) {
            btnObj.loading = true
            Generate_Key().then(res => {
                if (res.hasOk && res?.bean) {
                    this.$set(this, 'keyInfo', res.bean || {})
                } else {
                    this.$message.error(res.tip || '生成密钥对失败，请联系系统管理员！')
                }
            }).finally(() => {
                btnObj.loading = false
            })
        },
        // 复制公钥
        copyPublicKey () {
            if (!this.keyInfo.publicKey) {
                this.$message.warning('请先点击生成密钥！')
                return
            }
            Copy_To_Clip(this.keyInfo.publicKey).then(res => {
                this.$message.success(`复制公钥成功`)
            })
        },
        // 复制私钥
        copyPrivateKey () {
            if (!this.keyInfo.privateKey) {
                this.$message.warning('请先点击生成密钥！')
                return
            }
            Copy_To_Clip(this.keyInfo.privateKey).then(res => {
                this.$message.success(`复制私钥成功`)
            })
        }
    },
}
</script>

<style lang="scss" scope>
.generate-key_container {
    .info-box {
        padding: $padding;
        background-color: $contentInBg;
        border-radius: $borderRadius;
        .tip-box {
            padding-left: 30px;
            li {
                line-height: 1.5;
            }
        }
    }
    .key-info {
        margin-top: $space;
        padding: $padding;
        background-color: $contentInBg;
        border-radius: $borderRadius;
        .key-title {
            margin: 0;
        }
    }
}
</style>