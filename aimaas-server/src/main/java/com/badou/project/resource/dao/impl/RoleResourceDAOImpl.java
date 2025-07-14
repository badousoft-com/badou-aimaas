package com.badou.project.resource.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.brms.system.security.vo.RoleResource;
import com.badou.project.resource.dao.IRoleResourceDAO;

/**
 * 用户角色菜单资源dao层实现类
 * @author xiaowange
 *
 */
@Repository
public class RoleResourceDAOImpl extends BaseHibernateDAO<RoleResource, Serializable>
implements IRoleResourceDAO{

	@Override
	public void deleteByResourceId(String id) {
		String sql = "delete from sys_role_resource where resource_id = '" + id + "'";
		Query query = this.getSession().createSQLQuery(sql);
		query.executeUpdate() ;
	}

	/**
	 * 根据菜单id获取有当前菜单权限的角色id集合
	 * 便于在删除菜单时刷新相应的角色id权限
	 * @param id 菜单id
	 * @return 角色id集合
	 */
	@Override
	public List<String> findRoleIdsByResourceId(String id) {
		String sql = "select role_id from sys_role_resource where resource_id = '" + id + "'";
		Query query = this.getSession().createSQLQuery(sql);
		return query.list();
	}

}
