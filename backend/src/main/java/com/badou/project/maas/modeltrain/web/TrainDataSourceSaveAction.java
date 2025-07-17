package com.badou.project.maas.modeltrain.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.modeltrain.model.TrainDataSourceEntity;
import com.badou.project.maas.modeltrain.service.ITrainDataSourceService;
import com.badou.project.maas.mongo.model.TrainData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

/**
 * @author badousoft
 * @created 2024-04-07 15:39:08.379
 * @todo 训练集数据 保存实现类
 */
@Controller
public class TrainDataSourceSaveAction extends BaseCommonSaveAction {

    @Autowired
    private ITrainDataSourceService trainDataSourceService;

    public static void main(String[] args) {
        String test = "[{\"conversations\": [{\"role\": \"user\",\"content\": \"测试文本111\"},{\"role\": \"assistant\",\"content\": \"测试文本111.请修改或者新增对象内容不要使用默认值.\"},{\"role\": \"tool\",\"name\": \"sendHttpRequest\",\"parameters\": {  \"method\": \"GET\", \"url\": \"https://www.baidu.com/s?wd=chatGLM3\"},\"observation\": \"Status Code: 200. Response: 200\"}, {\"role\": \"user\",\"content\": \"有没有”一点点“奶茶？我想喝这一家的。\"},{\"role\": \"assistant\",\"content\": \"距离2公里有一家”一点点“奶茶，走吧。\"},{\"role\": \"user\",\"content\": \"你帮我点一杯柠檬茶，不要加冰。\"},{\"role\": \"assistant\",\"content\": \"你去找位置坐着等我。\"}]}\n" +
                "{\"conversations\": [{\"role\": \"user\",\"content\": \"测试文本222\"},{\"role\": \"assistant\",\"content\": \"您好,该文本为测试文本222\"},{\"role\": \"tool\",\"name\": \"sendHttpRequest\",\"parameters\": {  \"method\": \"GET\", \"url\": \"https://www.baidu.com/s?wd=chatGLM3\"},\"observation\": \"Status Code: 200. Response: 200\"}, {\"role\": \"user\",\"content\": \"2杯，一杯不加冰的。\"},{\"role\": \"assistant\",\"content\": \"40元请付款，请稍等，很快就可以弄好。\"}, {\"role\": \"user\",\"content\": \"请问好了没有，已经5分钟了。\"},{\"role\": \"assistant\",\"content\": \"快了，正在给你打包。您的柠檬茶，请慢用。\"}, {\"role\": \"user\",\"content\": \"谢谢。\"},{\"role\": \"assistant\",\"content\": \"不客气。\"}]}\n]";
        JSONArray objects = JSONObject.parseArray(test);
        System.out.println(objects);
    }

    @PostMapping
    public JsonReturnBean createTrainData(TrainDataSourceEntity trainDataSourceEntity) {
        String trainContent = trainDataSourceEntity.getTrainContent();
        if (Objects.isNull(trainContent)) {
            return JsonResultUtil.errorData();
        }
        /**
         * 有效的训练集实例.注意 为了方便处理 增加[]当成json数组处理.然后再返回实际结果的时候 要给正确的格式
         * {"conversations": [{"role": "user","content": "测试文本111"},{"role": "assistant","content": "测试文本111.请修改或者新增对象内容不要使用默认值."},{"role": "tool","name": "sendHttpRequest","parameters": {  "method": "GET", "url": "https://www.baidu.com/s?wd=chatGLM3"},"observation": "Status Code: 200. Response: 200"}, {"role": "user","content": "有没有”一点点“奶茶？我想喝这一家的。"},{"role": "assistant","content": "距离2公里有一家”一点点“奶茶，走吧。"},{"role": "user","content": "你帮我点一杯柠檬茶，不要加冰。"},{"role": "assistant","content": "你去找位置坐着等我。"}]}
         * {"conversations": [{"role": "user","content": "测试文本222"},{"role": "assistant","content": "您好,该文本为测试文本222"},{"role": "tool","name": "sendHttpRequest","parameters": {  "method": "GET", "url": "https://www.baidu.com/s?wd=chatGLM3"},"observation": "Status Code: 200. Response: 200"}, {"role": "user","content": "2杯，一杯不加冰的。"},{"role": "assistant","content": "40元请付款，请稍等，很快就可以弄好。"}, {"role": "user","content": "请问好了没有，已经5分钟了。"},{"role": "assistant","content": "快了，正在给你打包。您的柠檬茶，请慢用。"}, {"role": "user","content": "谢谢。"},{"role": "assistant","content": "不客气。"}]}
         */
        try {
            JSONArray conversations = JSONObject.parseArray("["+trainDataSourceEntity.getTrainContent()+"]");
            if (conversations.size() == 0) {
                return JsonResultUtil.errorMsg("请保证至少有一个数据!");
            }
            for (int i = 0; i < conversations.size(); i++) {
                JSONArray conversation = conversations.getJSONObject(i).getJSONArray("conversations");
                if (conversation.size() == 0) {
                    return JsonResultUtil.errorMsg("第" + i + 1 + "个conversations数组必须至少有一个内容!比如: {\"role\": \"user\", \"content\": \"你好\"}, {\"role\": \"assistant\", \"content\": \"xxx\"}");
                }
                String[] fiels = new String[]{"role", "content"};
                for (int k = 0; k < conversation.size(); k++) {
                    JSONObject row = conversation.getJSONObject(k);
                    //必须全部都拥有这些字段 否则不合法!
                    for (int j = 0; j < fiels.length; j++) {
                        // 当前的json不包含该字段
                        if (!row.containsKey(fiels[j])) {
                            if ("tool".equals(row.get("role")) && "content".equals(fiels[j])) {
                                //如果当前角色为role 允许没有内容
                                continue;
                            }
                            return JsonResultUtil.errorMsg("第" + (i + 1) + "个conversations中的第" + (k + 1) + "个对象缺少" + fiels[j] + "字段!请补上");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResultUtil.errorMsg("无效的json格式!请用{\"conversations\": [{\"role\": \"user\", \"content\": \"你好\"}, {\"role\": \"assistant\", \"content\": \"xxx\"}]}");
        }

        TrainData trainData = new TrainData();
        BeanUtils.copyProperties(trainDataSourceEntity, trainData);
        trainDataSourceService.addTrainDataSource(trainData);
        return JsonResultUtil.success(trainData);
    }

    @Override
    protected void exeBeforeSave() throws Exception {
    }

}
