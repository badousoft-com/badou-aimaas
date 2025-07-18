<template>
    <div class="s-tip" v-if="tipList.length === 0">
        <div v-if="ratio" class="s-tip-section">
            分辨率必须为<span class="bold">{{ratio}}</span>
        </div>
        <div v-if="suffix.length > 0" class="s-tip-section">
            支持<span class="bold">{{suffix}}</span>格式文件
        </div>
        <div class="s-tip-section">
            <span v-if="maxSize" >文件大小不超过<span class="bold">{{maxSize}}</span></span>
        </div>
        <div class="s-tip-section" v-html="limitObj.tip"></div>
    </div>
    <!-- 自定义传入的文字提示 通过传入tipList属性 -->
    <div class="s-tip" v-else>
        <div class="s-tip-section" v-for="(i, index) in tipList.split(',')" :key="index">{{i}}</div>
    </div>
</template>
<script>
export default {
    name: 'use-tip',
    components: {},
    props: {
        // 提示信息的列表,如'不可以A,不可以B'
        tipList: {
            type: String
        },
        // 分辨率，值如'300*400'
        ratio: {
            type: String
        },
        // 文件后缀，如'png,jpg'
        suffix: {
            type: String,
            default: ''
        },
        // 文件最大，如'10MB'
        maxSize: {
            type: [String, Number]
        },
        // 限制信息
        limitObj: {
            type: Object,
            default: () => {}
        }
    }
}
</script>
<style lang='scss' scoped>
$tipMarB: 6px;
.s-tip {
    flex: 1;
    padding: 0 6px;
    font-size: $fontS;
    color: $fontCS;
    display: flex;
    flex-direction: column;
    justify-content: center;
    .s-tip-section {
        line-height: $font;
        margin-bottom: $tipMarB;
        &:last-of-type {
            margin-bottom: 0;
        }
    }
}
</style>