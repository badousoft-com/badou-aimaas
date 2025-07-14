package com.badou.project.resource.dao;

import java.io.Serializable;
import java.util.List;

import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.system.security.vo.RoleResource;

/**
 * 角色菜单dao层接口
 * @author xiaowange
 *
 */
public interface IRoleResourceDAO extends IBaseHibernateDAO<RoleResource, Serializable>{

	/**
	 * 根据菜单id删除角色和菜单的关联关系
	 * @param id 菜单id
	 */
	void deleteByResourceId(String id);

	/**
	 * 根据菜单id获取有当前菜单权限的角色id集合
	 * 便于在删除菜单时刷新相应的角色id权限
	 * @param id 菜单id
	 * @return 角色id集合
	 */
    List<String> findRoleIdsByResourceId(String id);
}
