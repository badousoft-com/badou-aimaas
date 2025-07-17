package com.badou.project.maas.problemdata.web;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.vo.LigeruiListVO;
import com.badou.brms.dboperation.query.ICriterion;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.QueryParam;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.core.annotation.PageMdJsonStack;
import com.badou.designer.jdbc.common.annotations.BaseMdJsonStack;
import com.badou.project.maas.problemdata.service.IProblemDataService;
import com.badou.project.maas.traindataproblem.model.TrainDataProblemEntity;
import com.badou.project.maas.traindataproblem.service.ITrainDataProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonListAction;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author badousoft
 * @created 2024-05-15 17:37:11.964
 * @todo 样本数据集管理 列表实现类
 */
@Controller
public class ProblemDataListAction extends BaseCommonListAction {

    @Autowired
    private ITrainDataProblemService trainDataProblemService;
    @Autowired
    private IProblemDataService problemDataService;

    protected void exeAfterList() {
        //查询样本数据集信息
        this.pagelet.getDatas().forEach(e->{
            JSONObject result = (JSONObject)e;
//            problemDataService.find()
        });
    }


    /**
     * 获取分页列表数据
     * @return 列表分页数据
     */
    @RequestMapping
    @BaseMdJsonStack
    @PageMdJsonStack
    public LigeruiListVO<JSONObject> listLinkJSON(){
        try {
            this.exeBeforeList(false);
            ICriterion criterion = (ICriterion) request.getAttribute(Request_Criterion);
            QueryCriterion queryCriterion = (QueryCriterion)criterion;
            if (queryCriterion.getQueryParams().size()>0){
                queryCriterion.addParam(new QueryHibernatePlaceholderParam("train_file_id",((QueryCriterion) criterion).getQueryParams().get(0).getValue1(),null,QueryOperSymbolEnum.eq));
                queryCriterion.addParam(new QueryHibernatePlaceholderParam("flg_deleted",0,null, QueryOperSymbolEnum.eq));
                List<TrainDataProblemEntity> trainDataProblemEntities = trainDataProblemService.find(queryCriterion);
                List<String> filterIds = new ArrayList<>();
                trainDataProblemEntities.forEach(e->{
                    filterIds.add(e.getProblemId());
                });
                queryCriterion = new QueryCriterion();
                if (filterIds.size() != 0){
                    queryCriterion.addParam(new QueryHibernatePlaceholderParam("id",filterIds,null,QueryOperSymbolEnum.notin, QueryParam.PARAM_PLACEHOLDER_NAME));
                }
            }
            if (criterion != null) {
                pagelet = baseModuleService.findPages(queryCriterion);
            } else {
                pagelet = baseModuleService.findPages();
            }
            this.convert(pagelet);
            this.exeAfterList();
            listvo = new LigeruiListVO<>();
            listvo.setTotal(this.getPagelet().getTotalCount());
            listvo.setRows(this.getPagelet().getDatas());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage() , e );
        }
        return listvo;
    }

}
