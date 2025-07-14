package com.badou.project.maas.modelapp.web;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryCacheObject;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.modelapp.model.ModelAppEntity;
import com.badou.project.maas.modelapp.model.TalkEntity;
import com.badou.project.maas.modelapp.service.IModelAppService;
import com.badou.project.maas.modelsync.service.IModelSyncTaskService;
import com.badou.project.maas.modelwarehouse.service.IModelWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonListAction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author badousoft
 * @created 2024-05-27 11:33:46.513
 * @todo 模型应用管理 列表实现类
 */
@Controller
public class ModelAppListAction extends BaseCommonListAction {

    @Autowired
    private IModelAppService modelAppService;
    @Autowired
    private IModelSyncTaskService modelSyncTaskService;
    @Autowired
    private IModelWarehouseService modelWarehouseService;

    @RequestMapping
    public JsonReturnBean talikToAi(@RequestBody TalkEntity talkEntity){
        return JsonResultUtil.success(JSONObject.parseObject(modelAppService.talkToAi(talkEntity)));
    }

    private static DictionaryCacheObject dictionaryByCode = null;

    @PostMapping
    public JsonReturnBean getModelNewestStatus(@RequestBody ArrayList<ModelAppEntity> modelAppEntityArrayList) throws Exception {

        if (dictionaryByCode == null){
            dictionaryByCode = DictionaryLib.getDictionaryByCode(MaasConst.DIC_MODEL_RUN_STATUS);
        }

        List<JSONObject> resultArr = new ArrayList<>();
        for (ModelAppEntity modelAppEntity : modelAppEntityArrayList) {
            JSONObject jsonObject = new JSONObject();
            Integer status = modelAppService.checkModelRunningStatus(modelAppEntity, jsonObject);
            jsonObject.put("status",status);
            dictionaryByCode.getItems().forEach(e->{
                if (e.getValue().equals(status)){
                    jsonObject.put("statusDesc",e.getName());
                }
            });
            jsonObject.put("id",modelAppEntity.getId());
            jsonObject.put("allRunTime",jsonObject.getString("allRunTime"));
            resultArr.add(jsonObject);
        }
        return JsonResultUtil.success(resultArr);
    }

    @GetMapping
    public JsonReturnBean loadApiMsg(String id) {
        try {
            Object o = modelAppService.loadApiMsg(id);
            if (o instanceof String){
                return JsonResultUtil.errorMsg(o.toString());
            }
            return JsonResultUtil.success(o);
        }catch (Exception e){
            return JsonResultUtil.errorMsg(e.getMessage());
        }
    }

    @Override
    protected void exeAfterList() {
        //设置累计运行时间
        for(Object e:this.pagelet.getDatas()) {
            JSONObject result = (JSONObject) e;
            ModelAppEntity modelAppEntity = JSONObject.parseObject(result.toJSONString(),ModelAppEntity.class);
            //如果是关闭 则跳过后续的处理
            if (result != null && result.getInteger("status") != null && result.getInteger("status") == MaasConst.DOPLAN_CLOSE_STATUS) {
                continue;
            }
            try {
                result.put("status",modelAppService.checkModelRunningStatus(modelAppEntity,result));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }}

}
