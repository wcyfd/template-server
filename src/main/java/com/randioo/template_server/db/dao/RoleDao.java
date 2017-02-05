package com.randioo.template_server.db.dao;

import java.util.List;

import com.randioo.randioo_server_base.annotation.MybatisDaoAnnotation;
import com.randioo.template_server.entity.bo.Role;

@MybatisDaoAnnotation
public interface RoleDao {
	/**
	 * 新建用户
	 * 
	 * @param role
	 */
	public void insertRole(Role role);

	/**
	 * 获得所有玩家
	 * 
	 * @return
	 */
	public List<Role> getAllRole();

	public Role getRoleByAccount(String account);

	public void updateRole(Role role);

	Role getRoleById(int id);

	List<String> getAllNames();

	List<String> getAllAccounts();
}
