package com.badou.project.resource.service;

import java.io.Serializable;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.system.security.vo.RoleResource;

/**
 * 用户菜单权限service接口类
 * @author xiaowange
 *
 */
public interface IRoleResourceService extends IBaseSpringService<RoleResource, Serializable>{

	/**
	 * 删除菜单
	 * @param ids
	 * @return JsonReturnBean 接口结果返回对象
	 */
	JsonReturnBean deleteResource(String id);

}
