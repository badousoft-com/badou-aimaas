package com.badou.project.maas.tuningmodeln.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.project.common.webparams.util.DateUtil;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.maas.tuningmodeln.service.ITuningModelnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonListAction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author badousoft
 * @created 2024-04-30 16:20:58.82
 * @todo 微调模型管理 列表实现类
 */
@Controller
@Slf4j
public class TuningModelnListAction extends BaseCommonListAction {

   @Autowired
   private ITuningModelnService tuningModelnService;

   @PostMapping
   public JsonReturnBean getModelNewestStatus(@RequestBody ArrayList<String> ids) throws Exception {
      List<JSONObject> resultArr = new ArrayList<>();
      for (String id : ids) {
         TuningModelnEntity tuningModelnEntity = tuningModelnService.find(id);
         //实际启动时间 = 现在时间 - 开始时间
         if (tuningModelnEntity == null || tuningModelnEntity.getStartTime() == null){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("costTime","");
            resultArr.add(jsonObject);
            continue;
         }
         String timeDiff = DateUtil.getTimeDiff(tuningModelnEntity.getStartTime(), new Date());
         JSONObject jsonObject = new JSONObject();
         jsonObject.put("costTime",timeDiff);
         jsonObject.put("evaTotalCount",tuningModelnEntity.getEvaTotalCount());
         jsonObject.put("evaTotalScore",tuningModelnEntity.getEvaTotalScore());
         jsonObject.put("doStatus",tuningModelnEntity.getDoStatus());
         jsonObject.put("doStatusDesc", DictionaryLib.getItemName(MaasConst.DIC_MODEL_STATUS,tuningModelnEntity.getDoStatus()));
         jsonObject.put("shelvesStatus", tuningModelnEntity.getShelvesStatus());
         jsonObject.put("shelvesStatusDesc", DictionaryLib.getItemName(MaasConst.DIC_SHELVES_STATUS,tuningModelnEntity.getShelvesStatus()));
         jsonObject.put("id",id);
         resultArr.add(jsonObject);
         tuningModelnEntity.setCostTime(timeDiff);
      }
      return JsonResultUtil.success(resultArr);
   }

   @PostMapping
   public JsonReturnBean buildOrGetTenorBoard(@RequestParam String id){
      try {
         //检查是否存在服务 如果存在则直接返回访问链接
         return tuningModelnService.buildTaskStatusPanel(0, tuningModelnService.get(id));
      } catch (Exception e) {
      }
      return JsonResultUtil.errorMsg("未读取到有效的日志!请确保成功服务成功运行、正常结束运行");
   }

   @GetMapping
   public JsonReturnBean getModelStatus(@RequestParam String id){
      //先尝试获取微调发布的应用 如果有 则是通过微调方式得到应用 获取那个模型的客户端
      QueryCriterion queryCriterion = new QueryCriterion();
      queryCriterion.addParam(new QueryHibernatePlaceholderParam("id",id,null, QueryOperSymbolEnum.eq));
      queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted",0,null, QueryOperSymbolEnum.eq));
      List<TuningModelnEntity> tuningModelnEntities = tuningModelnService.find(queryCriterion);
      if(JsonResultUtil.arrayEmpty(tuningModelnEntities)){
         return JsonResultUtil.errorData();
      }
      TuningModelnEntity tuningModelnEntity = tuningModelnEntities.get(0);
      TuningModelnEntity result = null;
      try {
         String oldServerId = tuningModelnEntity.getServerId();
         if (oldServerId.split(",").length > 1){
            //当多机多卡任务时 一般第一个服务器就是主服务器 一般只需要选择查看主服务器的日志即可 大部分信息都在主节点
            JSONArray jsonArray = JSONArray.parseArray(tuningModelnEntity.getExecGpuCard());
            tuningModelnEntity.setServerId(jsonArray.getJSONObject(0).getJSONObject("k8sServerConfEntity").getString("id"));
         }
         KubernetesApiClient defaultClient = FileControllerService.getCustomClient(tuningModelnEntity.getServerId());
         if (oldServerId.split(",").length > 1) {
            tuningModelnEntity.setServerId(oldServerId);
         }
         result = tuningModelnService.getPodLogsUnUpdate(defaultClient, tuningModelnEntity.getWorkSpace(), tuningModelnEntity.getCode(), tuningModelnEntity, true);
         return JsonResultUtil.success(result);
      } catch (Exception e) {
         e.printStackTrace();
         return JsonResultUtil.errorMsg("未读取到有效的日志!请确保成功服务成功运行、正常结束运行");
      }
   }

}
