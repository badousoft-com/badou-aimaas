package com.badou.project.maas.problemdatadetail.event;

import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.designer.jdbc.common.util.IModuleEvents;
import com.badou.designer.jdbc.core.vo.BaseVO;
import com.badou.project.maas.problemdatadetail.model.ProblemDataDetailHisEntity;
import com.badou.project.maas.problemdatadetail.service.IProblemDataDetailHisService;
import com.badou.tools.common.util.SpringHelper;

import java.util.List;

public class CheckUniqueEvent implements IModuleEvents {

    /**
     * 修改，新增保存后的事件
     * @param ids 主键集合
     * @param voList 实体VO集合
     * @throws Exception 异常往上层抛出，由上层应用捕获处理
     */
    @Override
    public void saveAfter(List<String> ids, List<BaseVO> voList) throws Exception {
        //同2一个样本集问答 对应的历史数据 序号是不能重复的
        if (voList!=null && voList.size()>0){
            Object problemDataId = voList.get(0).getDetailMap().get("problem_data_id");
            Object orderNo = voList.get(0).getDetailMap().get("order_no");
            if (problemDataId == null || orderNo == null){
                throw new Exception("绑定失败!");
            }
            String pdataId = problemDataId.toString();
            IProblemDataDetailHisService problemDataDetailHisService = SpringHelper.getBean(IProblemDataDetailHisService.class);
            QueryCriterion queryCriterion = new QueryCriterion();
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("problemDataId",pdataId,null, QueryOperSymbolEnum.eq));
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("orderNo",Integer.parseInt(orderNo.toString()),null, QueryOperSymbolEnum.eq));
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted",0,null, QueryOperSymbolEnum.eq));
            List<ProblemDataDetailHisEntity> problemDataDetailHisEntities = problemDataDetailHisService.find(queryCriterion);
            if (problemDataDetailHisEntities.size()>1){
                throw new Exception("该排序号已存在数据!");
            }
        }
    }

}
