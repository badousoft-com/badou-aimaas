package com.badou.project.maas.traindataproblem.web;

import com.alibaba.fastjson.JSONArray;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.QueryParam;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.designer.jdbc.common.util.ParamsConvertUtil;
import com.badou.project.maas.traindataproblem.model.TrainDataProblemEntity;
import com.badou.project.maas.traindataproblem.service.ITrainDataProblemService;
import com.badou.tools.common.util.ParamUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonListAction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author badousoft
 * @created 2024-08-30 10:41:35.751
 *  训练集样本集 列表实现类
 */
@Controller
public class TrainDataProblemListAction extends BaseCommonListAction {

    @Autowired
    private ITrainDataProblemService trainDataProblemService;

    /**
     * 搜索参数，如果是导入的搜索则需要把参数用UTF-8重新decode
     * @param isExport 是否导出
     * @throws Exception 异常由上层应用捕获处理
     */
    protected void exeBeforeList(Boolean isExport) throws Exception {
        super.exeBeforeList(isExport);
        String defaultSearchParam = ParamUtils.getParameter(request, "defaultSearchParam",null);
        if (StringUtils.isEmpty(defaultSearchParam)){
            throw new Exception("服务器错误");
        }
        String trainFileId = JSONArray.parseArray(ParamUtils.getParameter(request, "defaultSearchParam", null)).getJSONObject(0).getString("value");
        QueryCriterion queryCriterion = (QueryCriterion)request.getAttribute(Request_Criterion);
        QueryCriterion linkQuery = new QueryCriterion();
        linkQuery.addParam(new QueryHibernatePlaceholderParam("trainFileId",trainFileId,null,QueryOperSymbolEnum.eq));
        linkQuery.addParam(new QueryHibernatePlaceholderParam("flgDeleted",0,null,QueryOperSymbolEnum.eq));
        List<TrainDataProblemEntity> trainDataProblemEntities = trainDataProblemService.find(linkQuery);
        List<String> dataIds = new ArrayList<>();
        for (TrainDataProblemEntity trainDataProblemEntity : trainDataProblemEntities) {
            dataIds.add(trainDataProblemEntity.getProblemId());
        }
        if(dataIds.size() == 0){
            return;
        }
        queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("id", dataIds, null, QueryOperSymbolEnum.in, QueryParam.PARAM_PLACEHOLDER_NAME));
        request.setAttribute(Request_Criterion,queryCriterion);
    }

}
