<template>
    <div class="file-view padding">
        <!-- 图片 -->
        <bd-image-picker
            v-if="type === 'img'"
            v-loading="loading"
            v-model="dataList"
            :isView="true">
        </bd-image-picker>
        <!-- 附件 -->
        <bd-attach
            v-else-if="type === 'attach'"
            v-loading="loading"
            v-model="dataList"
            :isView="true"
            class="attach-part">
        </bd-attach>
    </div>
</template>
<script>
import { Attach, ImagePicker } from '@/components/frame/Common/MForm/components/items/index'
export default {
    name: '',
    components: {
        [Attach.name]: Attach,
        [ImagePicker.name]: ImagePicker
    },
    props: {
        // type目前可取值：img / attach
        type: {
            type: String
        },
        // 附件数据
        data: {
            type: [Array, Promise],
        },
    },
    data () { // 定义页面变量
        return {
            // 页面展示数据
            dataList: [],
            // 加载数据状态
            loading: false
        }
    },
    // 可访问当前this实例
    async created () {
        if (!this.data) return
        // 判断传入数据类型，数据则直接赋值即可
        if (this.data.constructor === Array) {
            this.dataList = this.data
            return
        }
        // 传入为Promise，则获取其resolve结果值
        if (this.data.constructor === Promise) {
            // 启动加载状态
            this.loading = true
            // 赋值
            this.dataList = await this.data
            // 关闭加载状态
            this.loading = false
        }
    },
}
</script>
<style lang='scss' scoped>
.file-view::v-deep {
    .attach-part {
        min-height: 40px;
    }
}
</style>