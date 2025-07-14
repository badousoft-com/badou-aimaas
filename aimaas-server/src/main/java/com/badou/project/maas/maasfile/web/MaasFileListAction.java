package com.badou.project.maas.maasfile.web;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.vo.LigeruiListVO;
import com.badou.brms.dboperation.query.ICriterion;
import com.badou.core.annotation.PageMdJsonStack;
import com.badou.designer.jdbc.common.annotations.BaseMdJsonStack;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonListAction;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author badousoft
 * @created 2024-07-15 15:48:36.55
 * @todo 文件管理 列表实现类
 */
@Controller
public class MaasFileListAction extends BaseCommonListAction {

    @RequestMapping
    @BaseMdJsonStack
    @PageMdJsonStack
    @Override
    public LigeruiListVO<JSONObject> listJSON(){
        try {
            this.exeBeforeList(false);
            ICriterion criterion = (ICriterion) request.getAttribute(Request_Criterion);
            if (criterion != null) {
                pagelet = baseModuleService.findPages(criterion);
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
