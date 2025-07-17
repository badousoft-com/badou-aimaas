<!--
 * @Description: 系统管理 - 八斗工具 - 图标库
-->
<!--
 * @TIPS
 * 当前页面的可用图标列表，依赖于icons.json这份文件。
 * 该文件的初始数据源，在阿里图标库下载下来的图标包中的demo_index.html的js中，执行下面的代码块，可以得到。
 * 后续的增加图标，先在指定路径下增加.svg文件，再到icons.json文件中增加 key-value（例如 "import": "导入"），即可。
 * 注意区分 frame / business
-->
<template>
    <div class="main defaultBg">
        <div class="content symbol">
            <div class="content-head">
                <h2 class="dangerC">Tip:使用这里的框架图标时不需要添加前缀【#bd-】</h2>
                <code class="language-html pointer" title="点击可复制" @click="copyExample">标签使用方式： &ltbd-icon name="{{exampleIcon}}"&gt&lt/bd-icon&gt</code>
                <el-input placeholder="请输入要搜索的图标名称" class="search" v-model="keywords" clearable></el-input>
            </div>
            <el-collapse v-model="colInfo.active">
                <el-collapse-item v-for="(col, index) in colInfo.data" :key="index" :name="col.value" :title="col.label">
                    <ul class="icon_lists dib-box">
                        <li class="dib" v-for="(icon, index) in search(keywords, icons[col.value])" :key="index" @click="copy(icon)">
                            <bd-icon class="icon svg-icon" :name="icon.code"></bd-icon>
                            <div class="name" @click.stop="emptyFun">{{ icon.name }}</div>
                            <div class="code-name">
                                <div class="m-copyboard" :class="{ success: !!icon.copyRun }">{{ copyTip[icon.copyRun] }}</div>
                                <span>{{ icon.code }}</span>
                            </div>
                        </li>
                    </ul>
                </el-collapse-item>
            </el-collapse>

            <div class="article markdown">
                <h2 id="symbol-">Symbol 引用</h2>
                <hr />

                <p>这是一种全新的使用方式，应该说这才是未来的主流，也是平台目前推荐的用法。 这种用法其实是做了一个 SVG 的集合，与另外两种相比具有如下特点：</p>
                <ul>
                    <li>支持多色图标了，不再受单色限制。</li>
                    <li>
                        通过一些技巧，支持像字体那样，通过
                        <code>font-size</code>
                        ,
                        <code>color</code>
                        来调整样式。
                    </li>
                    <li>兼容性较差，支持 IE9+，及现代浏览器。</li>
                    <li>浏览器渲染 SVG 的性能一般，还不如 png。</li>
                </ul>
            </div>
        </div>
    </div>
</template>

<script>
import { Copy_To_Clip } from '@/utils/copy-clip'
import iconHandler from '@/components/frame/Icon/index.js'
import { Is_Array, Is_Object } from '@/utils/data-type'

const iconJson = require('./icons.json')
const tempIcons = {
    all: new Array(),
}
Object.keys(iconJson.frame).forEach(code => {
    if (iconHandler.hasIcon(code)) {
        tempIcons.all.push({
            code: code,
            name: iconJson.frame[code],
            copyRun: 0
        })
    }
})
Object.keys(iconJson.business).forEach(code => {
    if (iconHandler.hasIcon(code)) {
        tempIcons.all.push({
            code: code,
            name: iconJson.business[code],
            copyRun: 0
        })
    }
})
export default {
    data () {
        return {
            exampleIcon: 'stop',
            icons: tempIcons,
            // 折叠面板
            colInfo: {
                active: ['all'],
                data: [{ label: '总图标', value: 'all' }]
            },
            copyTip: ['复制代码', '复制成功'],
            // 搜索框
            keywords: '',
            search_json: {}
        }
    },
    methods: {
        /**
         * @description: 空函数
         */
        emptyFun () {},
        /**
         * @description: 复制代码
         * @param {Object} icon icon
         */
        copy (icon) {
            // let clickObj = e.target
            const input = document.createElement('input')
            document.body.appendChild(input)
            input.setAttribute('value', icon.code)
            input.select()
            if (document.execCommand('Copy')) {
                document.execCommand('Copy')
                icon.copyRun = 1
                setTimeout(() => {
                    icon.copyRun = 0
                }, 1500)
            }
            // 删除过渡元素,避免dom存在无用标签
            document.body.removeChild(input)
        },

        /**
         * 传入数据，搜索关键字，模糊匹配。
         * 如果传入的是对象，那么搜索的是值，如果传入的是json，那么搜索的是里面的name
         * 建议放在 v-for 里面使用，因为它会直接返回数据
         *
         * @param {keyword} p1 要搜索的关键词，建议在 data() 里面定义一个空字符串，用v-model绑定它并把它传给该函数
         * @param {data} p2 一个数据，可以是对象，数组，json格式的数据
         * @return {data} 返回值描述，不输入关键字就返回传入的数据本身，输入关键字返回搜索后的数据
         */
        search (keyword, data) {
            let list = []
            // 检测是否为对象数据，如果是就变成数组
            while (Is_Object(data)) {
                let newData = Object.values(data)
                // 不搜索就返回整个转化后的数组
                if (!keyword) {
                    return newData
                } else {
                    newData.forEach(item => {
                        if (item.includes(keyword)) {
                            list.push(item)
                        }
                    })
                    return list
                }
            }
            // 检测是否为数组
            while (Is_Array(data)) {
                // 然后再检测是不是json
                if (Is_Object(data[0])) {
                    // 是josn，进入函数
                    if (!keyword) {
                        // 不搜索就返回整个 josn
                        return data
                    } else { // 返回匹配后的新json
                        let newJson = []
                        data.forEach(item => {
                            // 这里根据name和code值找的
                            if (item.name.includes(keyword) || item.code.includes(keyword)) {
                                newJson.push(item)
                            }
                        })
                        return newJson
                    }
                }
                // 是数组，进入函数
                if (!keyword) {
                    // 不搜索就返回整个数组
                    return data
                } else {
                    data.forEach(item => {
                        if (item.includes(keyword)) {
                            list.push(item)
                        }
                    })
                    return list
                }
            }
        },
        // 复制例子按钮
        copyExample () {
            let _copyVal = `<bd-icon name="${this.exampleIcon}"></bd-icon>`
            Copy_To_Clip(_copyVal).then(res => {
                this.$message.success(`复制成功`)
            })
        }
    },
    mounted () {}
}
</script>

<style lang="scss" scoped>
$width: 110px;
/* 页面布局 */
.main {
    padding: 0 30px;
    margin: 0 auto;
}
.helps {
    margin-top: 40px;
}

.helps pre {
    padding: 20px;
    margin: 10px 0;
    border: solid 1px #e7e1cd;
    background-color: #fffdef;
    overflow: auto;
}

/deep/ .el-collapse {
    margin-top: 10px;
    .el-collapse-item__header {
        font-size: 16px;
        font-weight: bold;
    }
}

.icon_lists {
    width: 100% !important;
    padding: 0;
    overflow: hidden;
    *zoom: 1;
}

.icon_lists li {
    width: $width;
    margin-bottom: 10px;
    margin-right: 20px;
    text-align: center;
    list-style: none !important;
    cursor: pointer;
    display: inline-block;
    vertical-align: top;
    font-size: 14px;
}

.dib {
    .name {
        cursor: auto;
    }
}

.icon_lists li .code-name {
    line-height: 1.2;
    padding: 10px 0px;
    position: relative;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    .m-copyboard {
        position: absolute;
        top: 0px;
        right: 0;
        bottom: 0;
        left: 0;
        width: 100%;
        background: rgba(255, 255, 255, 1);
        z-index: 1;
        opacity: 0;
        transition: all 0.2s;
        cursor: pointer;
        height: 35px;
        line-height: 30px;
        font-size: $font;
        border-radius: 4px;
        color: $primary;
        border: 1px solid $primary;
        &.success {
            color: $success !important;
            border: 1px solid $success !important;
            opacity: 1;
        }
    }
    &:hover {
        .m-copyboard {
            opacity: 1;
        }
    }
}

.icon_lists .icon {
    display: block;
    height: $width;
    line-height: $width;
    font-size: 30px;
    margin: 4px auto;
    color: #333;
    -webkit-transition: font-size 0.25s linear, width 0.25s linear;
    -moz-transition: font-size 0.25s linear, width 0.25s linear;
    transition: font-size 0.25s linear, width 0.25s linear;
}

.icon_lists .icon:hover {
    font-size: $width - 30px;
}

.icon_lists .svg-icon {
    /* 通过设置 font-size 来改变图标大小 */
    width: 1em;
    /* 图标和文字相邻时，垂直对齐 */
    vertical-align: -0.15em;
    /* 通过设置 color 来改变 SVG 的颜色/fill */
    fill: currentColor;
    /* path 和 stroke 溢出 viewBox 部分在 IE 下会显示
        normalize.css 中也包含这行 */
    overflow: hidden;
}

.icon_lists li .name,
.icon_lists li .code-name {
    color: #666;
}

.highlight {
    line-height: 1.5;
}

.content-head {
    position: relative;
    .search {
        width: 300px;
        position: absolute;
        bottom: 0;
        right: 0;
    }
    .language-html {
        font-weight: bold;
        font-size: 16px;
        background: #000;
        color: #fff;
        padding: 12px;
    }
}
</style>
