package com.badou.project.maas.trainfile.event;

import com.badou.brms.base.support.page.model.Pagelet;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.QueryParam;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.designer.jdbc.common.util.IModuleEvents;
import com.badou.designer.jdbc.core.vo.BaseVO;
import com.badou.project.kubernetes.util.StringHandlerUtil;
import com.badou.project.maas.trainfile.model.TrainFileEntity;
import com.badou.project.maas.trainfile.service.ITrainFileService;
import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;
import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;
import com.badou.tools.common.util.SpringHelper;
import com.badou.tools.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.List;

@Component
public class CalcFileSizeEvent implements IModuleEvents {

    @Autowired
    private ITrainFileDialogueService trainFileDialogueService;

    /**
     * 修改，新增保存后的事件
     * @param ids 主键集合
     * @param voList 实体VO集合
     * @throws Exception 异常往上层抛出，由上层应用捕获处理
     */
    public void saveAfter(List<String> ids, List<BaseVO> voList) {
        String id = null;
        if (ids!=null && ids.size()>0){
            id = ids.get(0);
        }else {
            id = voList.get(0).getDetailMap().get("id").toString();;
        }


        ITrainFileDialogueService trainFileDialogueService = SpringHelper.getBean(ITrainFileDialogueService.class);
        ITrainFileService trainFileService = SpringHelper.getBean(ITrainFileService.class);
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("train_file_id",id,null, QueryOperSymbolEnum.eq));
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flg_deleted",0,null, QueryOperSymbolEnum.eq));
//        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted",0,null, QueryOperSymbolEnum.eq));
        List<TrainFileDialogueEntity> datas = trainFileDialogueService.find(queryCriterion);
        trainFileService.updateCount(id,datas.size());

        StringBuilder builder = new StringBuilder();

        datas.forEach(e->{
            TrainFileDialogueEntity trainFileDialogueEntity = (TrainFileDialogueEntity)e;
            builder.append(trainFileDialogueEntity.getQuestion()+trainFileDialogueEntity.getFeedback());
        });
        //计算训练集内容大小ne
        String size = null;
        try {
            byte[] bytes = builder.toString().getBytes("UTF-8");
            size = StringHandlerUtil.getNetFileSizeToMB(bytes.length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotEmpty(size)){
            TrainFileEntity trainFileEntity = trainFileService.find(id);
            trainFileEntity.setSize(size);
            if (trainFileEntity.getNumCount()==null){
                trainFileEntity.setNumCount(0);
            }
            trainFileEntity.setNumCount(datas.size());
            trainFileService.update(trainFileEntity);
        }
    }



}
