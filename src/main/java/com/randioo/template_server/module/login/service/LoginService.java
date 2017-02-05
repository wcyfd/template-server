package com.randioo.template_server.module.login.service;

import org.apache.mina.core.session.IoSession;

import com.randioo.randioo_server_base.module.BaseServiceInterface;
import com.randioo.template_server.entity.bo.Role;

public interface LoginService extends BaseServiceInterface{

	/**
	 * 初始化数据
	 * @param role
	 * @author wcy 2016年12月7日
	 */
	public void loginRoleModuleDataInit(Role role);
	
	Object getRoleData(Object requestMessage, IoSession ioSession);

	Object creatRole(Object msg);

	Object login(Object msg);

	/**
	 * 通过id获取玩家
	 * @param roleId
	 * @return
	 * @author wcy 2017年1月10日
	 */
	public Role getRoleById(int roleId);

	/**
	 * 通过帐号获得玩家
	 * @param account
	 * @return
	 * @author wcy 2017年1月10日
	 */
	public Role getRoleByAccount(String account);

}
