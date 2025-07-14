package com.badou.project.maas.trainfiledialogue.web;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.problemdata.model.ProblemDataEntity;
import com.badou.project.maas.problemdata.service.IProblemDataService;
import com.badou.project.maas.problemdatadetail.model.ProblemDataDetailEntity;
import com.badou.project.maas.problemdatadetail.service.IProblemDataDetailService;
import com.badou.project.maas.trainfiledialoguedetail.model.TrainFileDialoguedetailEntity;
import com.badou.project.maas.trainfiledialoguedetail.service.ITrainFileDialoguedetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonListAction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author badousoft
 * @created 2024-05-16 14:39:38.124
 * @todo 训练集文件对话 列表实现类
 */
@Controller
public class TrainFileDialogueListAction extends BaseCommonListAction {

    @Autowired
    private ITrainFileDialoguedetailService trainFileDialoguedetailService;
    @Autowired
    private IProblemDataService problemDataService;
    @Autowired
    private IProblemDataDetailService problemDataDetailService;
    @Override
    protected void exeAfterList() {
//        problemDataDetailEntities.forEach(e->{
//            TrainFileDialoguedetailEntity trainFileDialoguedetailEntity = new TrainFileDialoguedetailEntity();
//            trainFileDialoguedetailEntity.setDialogueId("a461429be13346e282fb1ecc0408fbb53bf0a28a8cae4289b3590420221bf5f2");
//            trainFileDialoguedetailEntity.setTreeUniqueId(UUID.randomUUID().toString());
//            trainFileDialoguedetailEntity.setFlgDeleted(0);
//            trainFileDialoguedetailEntity.setQuestion(e.getQuestion());
//            trainFileDialoguedetailEntity.setContentCount(problemDataDetailEntities.size());
//            trainFileDialoguedetailEntity.setFeedback(e.getFeedback());
//            trainFileDialoguedetailEntity.setProblemDetailData(e.getId());
//            //数据转成训练集
//            trainFileDialoguedetailService.createEntity(trainFileDialoguedetailEntity);
//        });

        List<Object> datas = this.getPagelet().getDatas();
        if(JsonResultUtil.arrayNotEmpty(datas)){
            datas.forEach(e->{
                JSONObject result = (JSONObject)e;
                QueryCriterion queryCriterion = new QueryCriterion();
                queryCriterion.addParam(new QueryHibernatePlaceholderParam("dialogue_id",result.getString("id"),null,QueryOperSymbolEnum.eq));
                List<TrainFileDialoguedetailEntity> trainFileDialoguedetailEntities = trainFileDialoguedetailService.find(queryCriterion);
                List<TrainFileDialoguedetailEntity> newaa = new ArrayList<>();
                if(trainFileDialoguedetailEntities.size()>500){
                    for (int i = 0; i <500 ; i++) {
                        newaa.add(trainFileDialoguedetailEntities.get(i));
                    }
                }
                result.put("children",newaa);
                result.put("treeUniqueId",UUID.randomUUID().toString());
            });
        }
    }

}
