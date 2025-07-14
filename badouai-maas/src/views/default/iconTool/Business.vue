
<!--
2. 更新图标文件的时候只需要换掉标注的代码,换成新的图标文件中对应的svg模块内容,通过搜索"字体文件更新"可以当前页面找到标注
3. 替换关键代码,在当前界面搜索
    将<div class="code-name">更换为下面
    <div class="code-name"><div class="m-copyboard" @click="copy">{{ copyTip[0] }}</div>
    更换所有匹配项
3.完成三部操作即可完成图标文件的更新
-->
<template>
    <div class="main defaultBg">
        <div class="content symbol">
            <h2 class="dangerC">请优先下载属于自己项目的业务图标库</h2>
<code class="language-html">
    （即将废弃）旧版标签使用方式： &ltbd-icon name="#bdb-shanchu"&gt&lt/bd-icon&gt  【需要添加#bdb前缀】
</code><br/>
<code class="language-html">
    （推荐使用）新版标签使用方式： &ltbd-icon name="report-pie-active"&gt&lt/bd-icon&gt  【不需要添加前缀，直接使用关键词】
</code>
            <!-- ==================字体文件更新只需要更换这里 --start======================== -->
            <ul class="icon_lists dib-box">
                <li class="dib">
                    <svg class="icon svg-icon" aria-hidden="true">
                        <use xlink:href="#bd-report-pie-active"/>
                    </svg>
                    <div class="name">统计数</div>
                    <div class="code-name"><div class="m-copyboard" @click="copy">{{ copyTip[0] }}</div>#bd-report-pie-active</div>
                </li>
            </ul>
            <!-- ==================字体文件更新只需要更换这里 --end========================== -->
            <div class="article markdown">
                <h2 id="symbol-">Symbol 引用</h2>
                <hr />

                <p>
                    这是一种全新的使用方式，应该说这才是未来的主流，也是平台目前推荐的用法。
                    这种用法其实是做了一个 SVG 的集合，与另外两种相比具有如下特点：
                </p>
                <ul>
                    <li>支持多色图标了，不再受单色限制。</li>
                    <li>
                        通过一些技巧，支持像字体那样，通过
                        <code>font-size</code>,
                        <code>color</code> 来调整样式。
                    </li>
                    <li>兼容性较差，支持 IE9+，及现代浏览器。</li>
                    <li>浏览器渲染 SVG 的性能一般，还不如 png。</li>
                </ul>
                <p>使用步骤如下：</p>
                <h3 id="-">挑选相应图标并获取类名，应用于页面：</h3>
                <code class="language-html">
                    （推荐使用）新版标签使用方式： &ltbd-icon name="report-pie-active"&gt&lt/bd-icon&gt  【不需要添加前缀，直接使用关键词】
                </code>
            </div>
        </div>
    </div>
</template>

<script>
import GlobalConst from '@/service/global-const'
export default {
    data () {
        return {
            copyTip: ['复制代码', '复制成功']
        }
    },
    methods: {
        /** 将对象值塞进黏贴板
         * @params {Object} e: 点击需要复制的对象
         * tip: 使用原生写法,只支持文本域复制,所以需要构造input标签过渡进行设值
         */
        copy (e) {
            let clickObj = e.target
            const input = document.createElement('input')
            document.body.appendChild(input)
            // e.target.parentNode.innerText为需要复制的值
            let value = clickObj.parentNode.innerText
            value = value.slice(value.indexOf('#') - 1).replace(GlobalConst.icon.prefix, '')
            input.setAttribute('value', value)
            input.select()
            if (document.execCommand('Copy')) {
                document.execCommand('Copy')
                clickObj.classList.add('success')
                clickObj.innerText = this.copyTip[1]
                setTimeout(() => {
                    clickObj.classList.remove('success')
                    clickObj.innerText = this.copyTip[0]
                }, 1500)
            }
            // 删除过渡元素,避免dom存在无用标签
            document.body.removeChild(input)
        }
    },
    created () {}
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

.icon_lists {
    width: 100% !important;
    overflow: hidden;
    *zoom: 1;
}

.icon_lists li {
    width: $width;
    margin-bottom: 10px;
    margin-right: 20px;
    text-align: center;
    list-style: none !important;
    cursor: default;
    display: inline-block;
    vertical-align: top;
    font-size: 14px;
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
    font-size: $width - 20px;
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
</style>

