package com.badou.project.maas.problemdata;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class CoverJson {

    public static void main(String[] args) {
        String str = "C:\\Users\\16532\\Desktop\\数据整理\\测试4种类型的数据集.jsonl";
        JSONArray jsonArray = JSONArray.parseArray("["+FileUtil.readString(str,"UTF-8")+"]");
        JSONArray data = new JSONArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONArray conversations = jsonObject.getJSONArray("conversations");
            JSONObject row = new JSONObject();
            String value = "";
            for (int i1 = 0; i1 < conversations.size(); i1++) {
                JSONObject jsonObject1 = conversations.getJSONObject(i1);
                if (jsonObject1.getString("role").equals("user")){
                    value+=jsonObject1.getString("content");
                }else if (jsonObject1.getString("role").equals("assistant")){
                    value+=jsonObject1.getString("content");
                }
            }
            row.put("text",value);
            data.add(row);
        }
        System.out.println(data.toJSONString());
    }

}
