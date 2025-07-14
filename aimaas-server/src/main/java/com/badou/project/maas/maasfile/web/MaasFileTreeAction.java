package com.badou.project.maas.maasfile.web;

import com.badou.designer.jdbc.common.annotations.BaseMdJsonStack;
import com.badou.designer.jdbc.common.web.BaseCommonTreeAction;
import com.badou.designer.jdbc.core.vo.BaseTreeVO;
import com.badou.tools.common.util.ParamUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Scope("prototype")
public class MaasFileTreeAction extends BaseCommonTreeAction {

    /**
     * 树加载事件
     * @return
     * @throws Exception
     */
    @RequestMapping
    @BaseMdJsonStack
    @ApiOperation(value = "树加载事件")
    @Override
    public List<BaseTreeVO> tree(){
        try {
            //树节点ID
            String nodeId = ParamUtils.getParameter(request, "nodeId");
            String defaultParams = ParamUtils.getParameter(request, "defaultParams");
            defaultParams = "[{\"name\":\"treeLevel\",\"value\":\"0\",\"type\":\"exact-match\",\"tagName\":\"\"}]";
            //初始化树结构
            if(StringUtils.isBlank(nodeId)){
                results = baseCommonService.queryTree(nodeId, defaultParams, true);
            }else{//加载树节点
                results = baseCommonService.queryTree(nodeId, defaultParams, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage() , e );
        }
        return results;
    }


}
