<template>
    <div class="card-count">
        <div class="m-count">
            <div class="m-count-item" 
                v-for="(i, index) in countList" 
                :key="index"
                :style="{'width': _width}">
                <template v-if="countList.length > 0">
                    <bd-icon class="card-bg" name="cardBg"></bd-icon>
                    <div class="header-side">
                        <div class="l-side">
                            <div class="iconArea" 
                                :style="{background: colorRgba(i.bgColor, 0.2), color: i.bgColor}">
                                <bd-icon :name="i.iconName"></bd-icon>
                            </div>
                            <span class="itemlabel">{{i.label}}</span>
                        </div>
                        <div class="r-side">
                            <countTo :startVal='0' :endVal='i.num' :duration='2000'></countTo>
                        </div>
                    </div>
                    <div class="footer-side">
                        <div class="line" :style="{background: i.bgColor}"></div>
                    </div>
                </template>
                
            </div>

            <div class="m-count-item default" 
                v-for="(i, index) in default_countList" 
                :key="index"
                v-if="countList.length === 0">
                <bd-icon class="card-bg" name="cardBg"></bd-icon>
                <div class="header-side">
                    <div class="l-side">
                        <div class="iconArea" ></div>
                        <span class="itemlabel"></span>
                    </div>
                    <div class="r-side"></div>
                </div>
                <div class="footer-side"></div>
            </div>

        </div>
    </div>
</template>

<script>
import countTo from 'vue-count-to'
import globalStyle from '@/styles/theme.scss'
import { Hex_To_RGBA } from '@/utils/color'
export default {
    name: 'CardCount',
    components: {
        countTo
    },
    props: {
        list: Array,
        type: {}
    },
    data () {
        return {
            countList: [],
            default_countList: 4,
            colorList: ['default', '#F9BD16', '#8A7AFB', '#F95554'],
            colorRgba: Hex_To_RGBA
        }
    },
    computed: {
        _width () {
            let num = this.countList.length
            if (num > 0) {
                return `${parseFloat(1 / num) * 100}%`
            }
            return ''
        }
        // colorList () {
        //     let colorScheme = []
        //     let baseColorList = [globalStyle.primary, globalStyle.warning, globalStyle.operate, globalStyle.danger]
        //     switch (this.type) {
        //         case 'theme-full':
        //             break
        //         case 'theme-gradient':
        //             break
        //         default:
        //             colorScheme =  baseColorList
        //     }
        // }
    },
    methods: {
    },
    watch: {
        list: {
            immediate: true,
            handler (newVal, oldVal) {
                // 模拟实际请求页面渲染效果
                setTimeout(() => {
                    this.countList = newVal
                    this.countList.forEach((i, index) => {
                        this.$set(i, 'bgColor', this.colorList[index])
                    })
                }, 1000)
                
            }
            
        }
    },
}
</script>

<style lang="scss" scoped>
    .m-count {
        display: flex;
        .m-count-item {
            position: relative;
            flex: 1;
            padding: $space $space $space $space+2px;
            min-height: 110px;
            margin-right: $space;
            background: $contentInBg;
            border-radius: $borderRadius;
            white-space: nowrap;
            transition: all .2s cubic-bezier(.645, .045, .355, 1);
            &:hover {
                box-shadow: 0 6px 16px -8px rgba(0, 0, 0, .08), 
                            0 9px 28px 0 rgba(0, 0, 0, .05), 
                            0 12px 48px 16px rgba(0, 0, 0, .03);
            }
            &:first-child {
                .header-side {
                    .iconArea {
                        background: rgba($primary, 0.2);
                        color: $primary;
                    }
                }
                .footer-side {
                    .line {
                        background: $primary;
                    }
                }
            }
            &:last-child {
                margin-right: 0;
            }
            .card-bg {
                position: absolute;
                right: 0;
                bottom: 0;
                font-size: 122px;
                fill: rgba($primary, 0.05);
            }
            .header-side {
                display: flex;
                align-items: center;
                justify-content: space-between;
                .l-side {
                    flex-grow: 1;
                    padding-right: 10px;
                    max-width: 110px;
                    .iconArea {
                        width: 40px;
                        height: 40px;
                        position: relative;
                        font-size: 20px;
                        border-radius: 50%;
                        .bd-icon {
                            position: absolute;
                            top: 0;
                            right: 0;
                            bottom: 0;
                            left: 0;
                            margin: auto;
                        }
                    }
                    .itemlabel {
                        font-size: 18px;
                        font-weight: bold;
                        margin-top: 8px;
                        display: block;
                        margin-bottom: 14px;
                        min-height: 20px;
                    }
                }
                .r-side {
                    font-size: 42px;
                    color: $fontCL;
                }
            }
            .footer-side {
                width: 100%;
                height: 4px;
                background: rgba(204, 204, 204, 0.5);
                .line {
                    width: 40%;
                    height: 100%;
                }
            }
            &.default {
                .l-side {
                    .iconArea {
                        background: #eee;
                    }
                    .itemlabel {
                        background: #eee;
                    }
                }
                .r-side {
                    height: 60px;
                    width: 60px;
                    background: #eee;
                }
            }

        }

    }
</style>
