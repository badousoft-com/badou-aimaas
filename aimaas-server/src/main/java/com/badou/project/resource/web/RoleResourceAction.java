package com.badou.project.resource.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.badou.brms.base.support.BaseAction;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.resource.service.IRoleResourceService;

/**
 * 菜单相关操作类
 * @author xiaowange
 *
 */
@RestController
public class RoleResourceAction extends BaseAction {

	@Autowired
	private IRoleResourceService roleResourceService ;
	
	/**
	 * 删除菜单接口
	 * @return returnBean 删除结果集
	 */
	@RequestMapping
	public JsonReturnBean delete() {
		JsonReturnBean returnBean = new JsonReturnBean() ;
		String id = request.getParameter("id") ;
		returnBean = roleResourceService.deleteResource(id);
		return returnBean ;
	}
}
