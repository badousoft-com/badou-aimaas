package com.badou.project.maas.checkhandler;

import com.badou.brms.base.support.struts.JsonReturnBean;

public interface IBaseCheckHandler {
    /**
     * 检查情况是否合法 具体处理由其他功能定义
     * @param params
     * @return
     */
    JsonReturnBean checkVaild(Object params);

}
