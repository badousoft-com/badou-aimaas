package com.badou.project.maas.modelfile.web;

import cn.hutool.core.text.StrBuilder;
import com.alibaba.fastjson.JSONObject;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonListAction;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author badousoft
 * @created 2024-04-01 17:57:13.932
 * @todo 模型文件管理 列表实现类
 */
@Controller
public class ModelListAction extends BaseCommonListAction {

    /**
     * 从内部加载数据
     * @return
//     */
    @RequestMapping
    public JSONObject loadByInnerPath() throws InterruptedException, ApiException, IOException {
//        V1PodList v1PodList = CreateClientUtil.build239().getCoreV1Api().listNamespacedPod("ai-controlller",
//                null, null, null,null,null,null,null,null,null,null);
//        JSONObject jsonObject = new KubernetesExecHandlerImpl().execCommandOnce(v1PodList.getItems().get(0).getMetadata().getName(),
//                "ai-controlller", CreateClientUtil.build239(), new String[]{"ls","-l","/model_path"});
//        System.out.println(jsonObject);
//        JSONObject jsonObject1 = coverMsg(
//                jsonObject.getString("msg")
//        );
        List<JSONObject> result = new ArrayList<>();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("name","pytorch_model-00002-of-00007.bin");
        jsonObject1.put("fileSize","640MB");
        result.add(jsonObject1);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Rows",result);
        return jsonObject;
    }

    private static JSONObject coverMsg(String result){
        JSONObject mainResult = new JSONObject();
        String toal = "";
        String[] split = result.split("\n");
        List<JSONObject> data = new ArrayList<>();
        String[] fields = new String[]{"quanxian","tag","count","count1","fileSize","date_month","date_day","date_time","name"};
        for(String s:split){
            if(s.contains("total")){
                String[] s1 = s.split(" ");
                toal = s1[1];
                continue;
            }
            JSONObject jsonObject = new JSONObject();
            String[] s1 = s.split(" ");
            StrBuilder strBuilder = new StrBuilder();
            int n = 0;
            for(String spResult:s1){
                if(!"".equals(spResult)){
                    if(fields[n].equals("fileSize")){
                        long l = Long.parseLong(spResult);
//                        spResult = ResourceUtil.getNetFileSizeDescription(l);
                    }
                    jsonObject.put(fields[n],spResult);
                    n++;
                }
            }
            n = 0;
            data.add(jsonObject);
        }
        mainResult.put("Total",toal);
        mainResult.put("Rows",data);
        return mainResult;
    }

}
