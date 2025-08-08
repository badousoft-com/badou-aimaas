package com.badou.project.maas.problemdata;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.designer.jdbc.common.web.BaseCommonListAction;
import org.springframework.stereotype.Controller;

/**
 * @author badousoft
 * @created 2024-05-15 17:37:11.964
 * @todo 样本数据集管理 列表实现类
 */
@Controller
public class ProblemDataCenterListAction extends BaseCommonListAction {

    @Override
    protected void exeAfterList() {
        this.getPagelet().getDatas().forEach(e->{
            JSONObject v= (JSONObject)e;
            //转换主题
            Integer applicableSubject = v.getInteger("applicableSubject");
            if (applicableSubject == null){
                v.put("applicableSubjectDesc","");
            }else {
                v.put("applicableIndustryDesc", DictionaryLib.getItemName("SUBJECT",applicableSubject));
            }
            //转换行业
            Integer applicableIndustry = v.getInteger("applicableIndustry");
            if (applicableIndustry == null){
                v.put("applicableIndustryDesc","");
            }else {
                v.put("applicableIndustryDesc", DictionaryLib.getItemName("DUSTY",applicableIndustry));
            }
        });
    }

}
