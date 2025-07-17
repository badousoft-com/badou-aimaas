<template>
    <div style="margin: 10px;">
        <div class="uploadFiles">
            <h2 style="color: #5cb85c;">对接模型接口</h2>
        </div>
        <h3>请求URL <code class="code">{{ data.address }}</code><span
                class="label label-default">{{ data.method }}</span></h3>
        <p><strong>请求参数</strong> <span class="badge">{{ data.contentType }}</span></p>
        <table class="table table-bordered" style="border: 1px solid #ddd;">
            <!-- {"top_p":0.8,"stream":"false","max_tokens":"1000","temperature":0.8,"messages":[{"content":"你好","role":"user"}],"model":"chatglm3-b6"} -->
            <tbody>
                <tr>
                    <th>参数名</th>
                    <th>参数名称</th>
                    <th>参考参数</th>
                    <th>类型</th>
                    <!-- <th>必须</th> -->
                    <th>描述</th>
                </tr>
                <tr v-for="item in data.paramsArray">
                    <td>{{ item.code }}</td>
                    <td>{{ item.name }}</td>
                    <td>{{ item.value }}</td>
                    <td>{{ item.type }}</td>
                    <!-- <td>{{ item.require }}</td> -->
                    <td>{{ item.remark }}</td>
                </tr>
            </tbody>
        </table>
        <p><strong> {{ data.requestTitle }} </strong></p>
        <pre class="prettyprint lang-json prettyprinted" style="" v-html="data.requestContnet"></pre>
        <!-- <p><strong>请求例子</strong></p> -->
        <!-- <pre class="prettyprint lang-json prettyprinted" style=""><span class="pun">{</span>
            <span class="pln"></span><span class="str">"top_p"</span><span class="pun">:</span><span class="str">"0.8"</span><span class="pun">,</span>
            <span class="pln">
	</span><span class="str">"stream"</span><span class="pun">:</span><span class="str">"false"</span><span class="pun">,</span><span class="pln">
	</span><span class="str">"max_tokens"</span><span class="pun">:</span><span class="str">"1000"</span><span class="pun">,</span><span class="pln">
	</span><span class="str">"temperature"</span><span class="pun">:</span><span class="str">"0.8"</span><span class="pun">,</span><span class="pln">
	</span><span class="str">"messages"</span><span class="pun">:</span><span class="str">"[{"content":"你好","role":"user"}]"</span><span class="pun">,</span><span class="pln">
	</span><span class="str">"model"</span><span class="pun">:</span><span class="str">"chatglm3-b6"</span><span class="pun">,</span>
<span class="pun">}</span></pre> -->
<p><strong>{{ data.responseTitle }}</strong></p>
<pre class="prettyprint lang-json prettyprinted" style="" v-html="data.responseContnet"></pre>
        <!-- <p><strong>返回结果</strong></p> -->
        <!-- <pre class="prettyprint lang-json prettyprinted" style=""><span class="pun">{</span><span class="pln">
	</span><span class="str">"object"</span><span class="pun">:</span><span class="str">"chat.completion"</span><span class="pun">,</span><span class="pln">
	</span><span class="str">"choices"</span><span class="pun">:</span><span class="str">"[{"index":0,"message":{"role":"assistant","content":"你好!有什么我能帮助你的吗?","name":null,"function_call":null},"finish_reason":"stop"}]"</span><span class="pun">,</span><span class="pln">
	</span><span class="str">"created"</span><span class="pun">:</span><span class="str">"1724895886"</span><span class="pun">,</span><span class="pln">
	</span><span class="str">"usage"</span><span class="pun">:</span><span class="str">"{"prompt_tokens":106,"total_tokens":118,"completion_tokens":12}"</span><span class="pun">,</span>
<span class="pun">}</span></pre> -->
    </div>
</template>
<script>


export default {
    name: 'api-pages',
    components: {

    },
    props: {
        data: Object,
    },
    data() {
        return {
        }
    },
    computed: {
    },
    methods: {
    },
    // 可访问当前this实例
    created() {

    },
    // 挂载完成，可访问DOM元素
    mounted() {
    },
    beforeDestroy() {
    }
}
</script>

<style lang='scss' scoped>
.prettyprint {
    padding: 1em;
    white-space: pre-wrap;
    font-family: 'Droid Sans Mono', 'CPMono_v07 Bold', 'Droid Sans';
    font-weight: bold;
    font-size: 11pt;
    background-color: #0f0f0f;
    -moz-border-radius: 8px;
    -webkit-border-radius: 8px;
    -o-border-radius: 8px;
    -ms-border-radius: 8px;
    -khtml-border-radius: 8px;
    border-radius: 8px;
    color: #fec243;
}

.table {
    width: 100%;
    max-width: 100%;
    margin-bottom: 20px;
    text-align: center;
}

.table td {
    border: 1px solid rgb(221, 221, 221);
}

.code {
    margin: 5px;
    padding: 2px 4px;
    font-size: 90%;
    color: #c7254e;
    background-color: #f9f2f4;
    border-radius: 4px;
}

.badge {
    display: inline-block;
    min-width: 10px;
    padding: 3px 7px;
    font-size: 12px;
    font-weight: 700;
    line-height: 1;
    color: #fff;
    text-align: center;
    white-space: nowrap;
    vertical-align: middle;
    background-color: #777;
    border-radius: 10px;
}

.label {
    background-color: #777;
    display: inline;
    padding: .2em .6em .3em;
    font-size: 75%;
    font-weight: 700;
    line-height: 1;
    color: #fff;
    text-align: center;
    white-space: nowrap;
    vertical-align: baseline;
    border-radius: .25em;
}
</style>