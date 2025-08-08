package com.badou.project.maas.modelwarehouse.web;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryCacheObject;
import com.badou.brms.dictionary.form.DictionaryItemCacheObject;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.tuningplanparams.web.form.PlanParamsSelection;
import com.badou.project.maas.tuningplanparams.web.form.PlanParamsSelectionRow;
import com.badou.tools.common.util.StringUtils;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonListAction;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author badousoft
 * @created 2024-08-28 11:30:33.707
 *  模型仓库 列表实现类
 */
@Controller
public class ModelWarehouseListAction extends BaseCommonListAction {

    @PostMapping
    /**
     * /获取微调方案参数的选项
     */
    public JsonReturnBean getVLLMSelectOption(){
        //处理带有选项的数据
        DictionaryCacheObject selectDics = DictionaryLib.getDictionaryByCode(MaasConst.DIC_VLLM_PARAMS_SELECTIONS);
        JSONObject selectionMap = new JSONObject();
        for (DictionaryItemCacheObject item : selectDics.getItems()) {
            //处理多个
            String value = item.getValue();
            if (StringUtils.isEmpty(value)){
                logger.error(item.toString());
                return JsonResultUtil.errorMsg("加载下拉选项失败!请联系管理员!");
            }
            String[] selections = value.split(",");
            List<PlanParamsSelection> rows = new ArrayList<>();
            for (String selection : selections) {
                rows.add(buildSelectionRow(selection));
            }
            selectionMap.put(item.getCode(),rows);
        }
        JSONObject booleanMap = new JSONObject();

        // 处理带布尔类型的数据 布尔类型的 所有的下拉选项都是True和False 固定的
        DictionaryCacheObject booleanDics = DictionaryLib.getDictionaryByCode(MaasConst.DIC_VLLM_PARAMS_COMMON_BOOLEAN);
        /**
         * [{
         *                         props: {
         *                             value: 'false',
         *                             label: 'false'
         *                         }
         *                     }, {
         *                         props: {
         *                             value: 'true',
         *                             label: 'true'
         *                         }
         *                     }]
         */

        for (DictionaryItemCacheObject item : booleanDics.getItems()) {
            List<PlanParamsSelection> booleanSelections = new ArrayList<>();
            booleanSelections.add(buildSelectionRow("False"));
            booleanSelections.add(buildSelectionRow("True"));
            booleanMap.put(item.getCode(),booleanSelections);
        }
        JSONObject result = new JSONObject();
        result.put("selectionMap",selectionMap);
        result.put("booleanMap",booleanMap);
        return JsonResultUtil.success(result.toJSONString());
    }

    private PlanParamsSelection buildSelectionRow(String value){
        PlanParamsSelectionRow planParamsSelectionRow = new PlanParamsSelectionRow(value,value);
        return new PlanParamsSelection(planParamsSelectionRow);
    }

}
