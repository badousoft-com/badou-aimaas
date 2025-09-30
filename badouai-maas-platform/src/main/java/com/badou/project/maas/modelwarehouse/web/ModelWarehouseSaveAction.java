package com.badou.project.maas.modelwarehouse.web;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryCacheObject;
import com.badou.brms.dictionary.form.DictionaryItemCacheObject;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.GlobalConsts;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.exception.DataValidException;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;
import com.badou.project.maas.modelwarehouse.model.WareHouseVllmParamEntity;
import com.badou.project.maas.modelwarehouse.service.IModelWarehouseService;
import com.badou.project.maas.modelwarehouse.service.IWareHouseVllmParamService;
import com.badou.project.maas.tuningplanparams.model.TuningPlanParamsEntity;
import com.badou.tools.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

/**
 * @author badousoft
 * @created 2024-08-28 11:30:33.707
 * 模型仓库 保存实现类
 */
@Controller
public class ModelWarehouseSaveAction extends BaseCommonSaveAction {

    @Autowired
    private IWareHouseVllmParamService wareHouseVllmParamService;
    @Autowired
    private IModelWarehouseService modelWarehouseService;

    @Override
    protected void exeBeforeSave() throws Exception {
//        String deployFrameStr = this.custForm.getDetails().get("deployFrame")[0];
//        if (StringUtils.isNotBlank(deployFrameStr)) {
//            int deployFrame = Integer.parseInt(deployFrameStr);
//            if (GlobalConsts.ONE.equals(deployFrame)) {
//                //如果是官方方式 暂时不支持不实现
//                throw new Exception("暂不支持官方方式.请等待后续支持");
//            }
//        }
        String[] sizes = this.custForm.getDetails().get("size");
        if (StringUtils.isEmpty(sizes[0])){
            throw new DataEmptyException("模型大小为必填项");
        }
        if (StringUtils.isNotBlank(sizes[0]) && sizes[0].contains("cannot access")){
            throw new DataEmptyException("当前模型路径已失效.请重新选择!");
        }
        String[] customGpuCards = this.custForm.getDetails().get("customGpuCard");
        String[] customGpuCardNames = this.custForm.getDetails().get("customGpuCardName");
        String customGpuCard = customGpuCards!=null?customGpuCards[0]:null;
        String customGpuCardName = customGpuCardNames!=null?customGpuCardNames[0]:null;
//        customGpuCard: 3801fe95-178c-45c7-8f4e-1cb9c2e1b2d1=18891+22528,f74b2d2b-9d6b-4ae6-a1b8-03c8c20386ad=18889+22528,ad918e07-a94a-4119-a793-783387c1ba9b=21183+22528,ed7e4d33-265a-40f7-8b8f-30f1cfb30432=21179+22528
//        customGpuCardName: 2080 Ti-0,2080 Ti-1,2080 Ti-4,2080 Ti-7
        //自定义显卡去重
        if (StringUtils.isNotBlank(customGpuCard)){
            String[] split = customGpuCard.split(",");
            String[] split1 = customGpuCardName.split(",");
            // 使用LinkedHashSet保持插入顺序并去重
            Set<String> nameSets = new LinkedHashSet<>();
            Set<String> cardsSets = new LinkedHashSet<>();
            for (int i = 0; i < split.length; i++) {
                cardsSets.add(split[i]);
                nameSets.add(split1[i]);
            }
            this.custForm.getDetails().put("customGpuCard",new String[]{String.join(",", cardsSets)});
            this.custForm.getDetails().put("customGpuCardName",new String[]{String.join(",", nameSets)});
        }
    }

    @PostMapping
    public JsonReturnBean changeShelvesStatus(String id){
        ModelWarehouseEntity modelWarehouseEntity = modelWarehouseService.find(id);
        //如果upStatus是Null则设置0如果是1则设置0 如果是0则设置1
        if (modelWarehouseEntity.getUpStatus() == null) {
            modelWarehouseEntity.setUpStatus(GlobalConsts.ZERO);
        } else if (GlobalConsts.ZERO.equals(modelWarehouseEntity.getUpStatus())) {
            modelWarehouseEntity.setUpStatus(GlobalConsts.ONE);
        } else if (GlobalConsts.ONE.equals(modelWarehouseEntity.getUpStatus())) {
            modelWarehouseEntity.setUpStatus(GlobalConsts.ZERO);
        }
        //更新实体
        modelWarehouseService.update(modelWarehouseEntity);
        return JsonResultUtil.success();
    }

    @PostMapping
    public JsonReturnBean updateValue(String id, String newValue){
        WareHouseVllmParamEntity wareHouseVllmParamEntity = wareHouseVllmParamService.find(id);
        if(wareHouseVllmParamEntity!=null){
            if ("ENV_MAX-MODEL-LEN".equals(wareHouseVllmParamEntity.getCode())){
                ModelWarehouseEntity modelWarehouseEntity = modelWarehouseService.find(wareHouseVllmParamEntity.getWarehostId());
                modelWarehouseEntity.setContentLength(Double.parseDouble(newValue));
                modelWarehouseService.update(modelWarehouseEntity);
            }
            wareHouseVllmParamEntity.setValue(newValue);
            wareHouseVllmParamService.update(wareHouseVllmParamEntity);
        }
        return JsonResultUtil.success();
    }

    @Override
    protected void exeAfterSave() throws DataEmptyException, DataValidException {
        ModelWarehouseEntity modelWarehouseEntity = modelWarehouseService.find(this.custForm.getId());
        String deployFrameStr = this.custForm.getDetails().get("deployFrame")[0];
        if (StringUtils.isNotBlank(deployFrameStr)) {
            int deployFrame = Integer.parseInt(deployFrameStr);
            if (GlobalConsts.ZERO.equals(deployFrame)) {
                //如果是VLLM 增加参数
                QueryCriterion queryCriterion = new QueryCriterion();
                queryCriterion.addParam(new QueryHibernatePlaceholderParam("warehostId", this.custForm.getId(), null, QueryOperSymbolEnum.eq));
                List<WareHouseVllmParamEntity> wareHouseVllmParamEntities = wareHouseVllmParamService.find(queryCriterion);
                //当前仓库不存在VLLM参数时 则添加VLLM参数
                if (JsonResultUtil.arrayEmpty(wareHouseVllmParamEntities)) {
                    DictionaryCacheObject dictionaryByCode = DictionaryLib.getDictionaryByCode(MaasConst.VLLM_PARAMS_DIC);
                    if (JsonResultUtil.arrayNotEmpty(dictionaryByCode.getItems())) {
                        DictionaryCacheObject planParamsGroupDic = DictionaryLib.getDictionaryByCode(MaasConst.DIC_VLLM_PARAMS_GROUP);
                        Map<String, String> groupNames = new HashMap<>();
                        Map<String, String> grouCodes = new HashMap<>();
                        for (DictionaryItemCacheObject item : planParamsGroupDic.getItems()) {
                            groupNames.put(item.getValue(), item.getName());
                            grouCodes.put(item.getValue(), item.getValue());
                        }
                        List<WareHouseVllmParamEntity> insertData = new ArrayList<>();
                        dictionaryByCode.getItems().forEach(e -> {
                            //20250407 增加逻辑 如果e的编码等于ENV_MAX-MODEL-LEN 则把值赋值给实体
                            if ("ENV_MAX-MODEL-LEN".equals(e.getCode())) {
                                modelWarehouseEntity.setContentLength(Double.parseDouble(e.getValue()));
                                modelWarehouseService.update(modelWarehouseEntity);
                            }

                            WareHouseVllmParamEntity obj = new WareHouseVllmParamEntity();
                            obj.setCode(e.getCode());
                            obj.setName(e.getName());
                            obj.setExplainName(e.getRemark());
                            obj.setValue(e.getValue());
                            obj.setWarehostId(this.getCustForm().getId());
                            obj.setFlgDeleted(0);
                            obj.setUpdateTime(new Date());
                            obj.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
                            obj.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
                            obj.setGroupCode(grouCodes.get(e.getFlgDefault()+""));
                            obj.setGroupName(groupNames.get(e.getFlgDefault()+""));
                            obj.setGroupPriority(e.getPriority().intValue());
                            obj.setGroupNo(e.getFlgDefault());
                            insertData.add(obj);
                        });
                        wareHouseVllmParamService.batchCreate(insertData);
                    }
                }
            }
        }
    }


}
