package com.badou.project.maas.tuningmodeln.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.core.annotation.BDInterfaceStack;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.modelapp.service.IModelAppService;
import com.badou.project.maas.modelsync.service.IModelSyncTaskService;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;
import com.badou.project.maas.modelwarehouse.service.IModelWarehouseService;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.maas.tuningmodeln.service.ITuningModelnService;
import com.badou.project.mq.CalcLogMqReceiver;
import com.badou.tools.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonEditAction;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @author badousoft
 * @created 2024-04-30 16:20:58.82
 * @todo 微调模型管理 编辑实现类
 */
@Controller
public class TuningModelnEditAction extends BaseCommonEditAction {
    @Autowired
    private ITuningModelnService tuningModelnService;
    @Autowired
    private CalcLogMqReceiver calcLogMqReceiver;
    @Autowired
    private IModelWarehouseService modelWarehouseService;
    @Autowired
    private IModelAppService modelAppService;
    @Autowired
    private IModelSyncTaskService modelSyncTaskService;

    /**
     * 把微调成功的任务 通过上下架转成可用于微调的模型
     * @param id 任务主键
     * @param type 处理类型 0.上架 1.下架
     * @return
     */
    @PostMapping
    public JsonReturnBean coverTunModel(String id,Integer type,String pubVersion,String pubMsg){
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("id",id,null, QueryOperSymbolEnum.eq));
        List<TuningModelnEntity> tuningModelnEntities = tuningModelnService.find(queryCriterion);
        if (StringUtils.isEmpty(id) || tuningModelnEntities.size() == 0){
            return JsonResultUtil.errorMsg("无效的数据!请联系管理员!");
        }
        TuningModelnEntity tuningModelnEntity = tuningModelnEntities.get(0);
        if (tuningModelnEntity.getDoStatus() == null){
            tuningModelnEntity.setDoStatus(MaasConst.DOPLAN_WAIT_STATUS);
        }
        if (tuningModelnEntity!=null && tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_SUCCESS_STATUS){
            try {
                return tuningModelnService.coverTunModel(tuningModelnEntity,type,pubVersion,pubMsg);
            }catch (Exception e){
                e.printStackTrace();
                return JsonResultUtil.errorMsg(e.getMessage());
            }
        }else {
            return JsonResultUtil.errorMsg("只有微调成功的模型才可以上架");
        }
    }

    @PostMapping
    @BDInterfaceStack(name = "检查模型是否需要同步",describe = "检查模型是否需要同步")
    public JsonReturnBean checkModelSync(@RequestBody ModelWarehouseEntity modelWarehouseEntity){
        if (modelWarehouseEntity.getStartNeedGpum() == 0 ){
            return JsonResultUtil.errorMsg("模型未设置启动需要显存!");
        }
        JsonReturnBean jsonReturnBean = modelSyncTaskService.calcModelSyncStatus(modelWarehouseEntity);
        //不需要同步
        if (!jsonReturnBean.isHasOk()){
            //失败 返回信息
            return jsonReturnBean;
            //检查到需要同步模型 发到前端 启动确认框
        }
        //需要同步
        if (jsonReturnBean.isHasOk() && "同步模型前置确认".equals(jsonReturnBean.getMessage())){
            return jsonReturnBean;
        }
        //进入无效的操作
        return JsonResultUtil.error();
    }

    @PostMapping
    public JsonReturnBean startModel(@RequestBody ModelWarehouseEntity modelWarehouseEntity){
        try {

//            //查询该模型是否重复
//            List<ModelAppEntity> modelAppEntities = modelAppService.checkSameTask(modelWarehouseEntity.getId());
//            if (modelAppEntities.size()>0){
//                return JsonResultUtil.errorMsg("该方案存在其他模型任务.是否覆盖?");
//            }
//            查询该模型是否已经启动
            boolean startmodel = tuningModelnService.startmodel(modelWarehouseService.find(modelWarehouseEntity.getId()),null);
            if(startmodel==false){
                return JsonResultUtil.errorMsg("创建失败!请联系管理员!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResultUtil.errorMsg(e.getMessage());
        }
        return JsonResultUtil.success();
    }

    @PostMapping
    public JsonReturnBean startPlanModel(String port,String modelId,String modelName,String planModelId){
        return JsonResultUtil.success();
    }

    @PostMapping
    public JsonReturnBean closeTask(String id){
        try {
            String result = tuningModelnService.closeTask(id);
            if(StringUtils.isNotEmpty(result)){
                return JsonResultUtil.errorMsg(result);
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.errorMsg("作废异常!请联系管理员!");
        }
        return JsonResultUtil.success();
    }

    @GetMapping
    public JsonReturnBean getModelBuildStatus(@RequestParam String id){
        try {
            Map<String, List> status = calcLogMqReceiver.getStatus(id);
            if(JsonResultUtil.isNull(status)){
                return JsonResultUtil.error();
            }
            return JsonResultUtil.success(status);
        } catch (ParseException e) {
            e.printStackTrace();
            return JsonResultUtil.error();
        }
    }

}
