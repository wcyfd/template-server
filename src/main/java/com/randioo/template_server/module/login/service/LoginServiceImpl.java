package com.randioo.template_server.module.login.service;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.module.BaseService;
import com.randioo.randioo_server_base.module.login.LoginHandler;
import com.randioo.randioo_server_base.module.login.LoginModelService;
import com.randioo.randioo_server_base.net.SpringContext;
import com.randioo.randioo_server_base.utils.TimeUtils;
import com.randioo.randioo_server_base.utils.sensitive.SensitiveWordDictionary;
import com.randioo.randioo_server_base.utils.system.SystemManager;
import com.randioo.randioo_server_base.utils.template.Ref;
import com.randioo.template_server.common.ErrorCode;
import com.randioo.template_server.db.dao.RoleDao;
import com.randioo.template_server.entity.bo.Role;
import com.randioo.template_server.protocol.Entity.RoleData;
import com.randioo.template_server.protocol.Login.LoginCheckAccountRequest;
import com.randioo.template_server.protocol.Login.LoginCheckAccountResponse;
import com.randioo.template_server.protocol.Login.LoginCreateRoleRequest;
import com.randioo.template_server.protocol.Login.LoginCreateRoleResponse;
import com.randioo.template_server.protocol.Login.LoginGetRoleDataRequest;
import com.randioo.template_server.protocol.Login.LoginGetRoleDataResponse;
import com.randioo.template_server.protocol.ServerMessage.SCMessage;

@Service("loginService")
public class LoginServiceImpl extends BaseService implements LoginService {
	@Autowired 
	private SqlSessionFactory sqlSessionFactory;

	@Autowired 
	private RoleDao roleDao;

	@Autowired 
	private LoginModelService loginModelService;

	@Override
	public void init() {
		//初始化所有已经有过的帐号和昵称
		List<String> accountList = roleDao.getAllAccounts();
		for(String account:accountList){
			RoleCache.getAccountSet().add(account);
		}
		
		List<String> nameList = roleDao.getAllNames();
		for(String name:nameList){
			RoleCache.getNameSet().add(name);			
		}
		
		loginModelService.init();
		loginModelService.setLoginHandler(new LoginHandlerImpl());
	}

	private class LoginHandlerImpl implements LoginHandler {

		@Override
		public boolean checkLoginAccountCanLogin(String account, Ref<Object> canLoginErrorMessage) {
			SystemManager systemManager = SpringContext.getBean("systemManager");
			if (!systemManager.isService()) {
				canLoginErrorMessage.set(SCMessage
						.newBuilder()
						.setLoginCheckAccountResponse(
								LoginCheckAccountResponse.newBuilder().setErrorCode(ErrorCode.REJECT_LOGIN)).build());
				return false;
			}
			return true;
		}

		@Override
		public String getLoginAccount(Object loginMessage) {
			LoginCheckAccountRequest request = (LoginCheckAccountRequest) loginMessage;
			return request.getAccount();
		}

		@Override
		public GeneratedMessage isNewAccount(String account) {
			return SCMessage
					.newBuilder()
					.setLoginCheckAccountResponse(
							LoginCheckAccountResponse
									.newBuilder()
									.setErrorCode(
											RoleCache.getAccountSet().contains(account) ? ErrorCode.SUCCESS : ErrorCode.SHORT_TWO))
					.build();
		}

		@Override
		public String getCreateRoleAccount(Object createRoleMessage) {
			LoginCreateRoleRequest request = (LoginCreateRoleRequest) createRoleMessage;
			return request.getAccount();
		}

		@Override
		public boolean checkCreateRoleAccount(Object createRoleMessage, Ref<Object> checkCreateRoleAccountMessage) {
			LoginCreateRoleRequest request = (LoginCreateRoleRequest) createRoleMessage;

			String name = request.getName();
			
			//姓名不可重复
//			if (RoleCache.getNameSet().contains(name)) {
//				checkCreateRoleAccountMessage.set(SCMessage
//						.newBuilder()
//						.setLoginCreateRoleResponse(
//								LoginCreateRoleResponse.newBuilder().setErrorCode(ErrorCode.NAME_IS_AREADY_HAS))
//						.build());
//				return false;
//			}

			if (RoleCache.getAccountSet().contains(request.getAccount())) { // 判定账号是否存在
				checkCreateRoleAccountMessage.set(SCMessage
						.newBuilder()
						.setLoginCreateRoleResponse(
								LoginCreateRoleResponse.newBuilder().setErrorCode(ErrorCode.ACCOUNT_ILLEGEL)).build());
				return false;
			}
			
			if(SensitiveWordDictionary.containsSensitiveWord(name)){
				checkCreateRoleAccountMessage.set(SCMessage
						.newBuilder()
						.setLoginCreateRoleResponse(
								LoginCreateRoleResponse.newBuilder().setErrorCode(ErrorCode.NAME_SENSITIVE)).build());
				return false;
			}
			return true;
		}

		@Override
		public SqlSessionFactory getSqlSessionFactory() throws SQLException {
			return sqlSessionFactory;
		}

		@Override
		public Object createRole(SqlSession conn, Object createRoleMessage) {
			LoginCreateRoleRequest request = (LoginCreateRoleRequest) createRoleMessage;

			// 用户数据
			Role role = roleInit(request.getAccount(), conn, request.getName());

			// 加入role缓存
			RoleCache.putNewRole(role);

			return SCMessage.newBuilder()
					.setLoginCreateRoleResponse(LoginCreateRoleResponse.newBuilder().setErrorCode(ErrorCode.SUCCESS))
					.build();
		}

		@Override
		public GeneratedMessage getRoleData(Ref<RoleInterface> ref) {
			Role role = (Role) ref.get();
			
			

			RoleData.Builder roleDataBuilder = RoleData.newBuilder().setRoleId(role.getRoleId())
					.setName(role.getName());
			

			return SCMessage
					.newBuilder()
					.setLoginGetRoleDataResponse(
							LoginGetRoleDataResponse.newBuilder().setErrorCode(ErrorCode.SUCCESS)
									.setServerTime(TimeUtils.getNowTime()).setRoleData(roleDataBuilder)).build();
		}

		@Override
		public String getRoleDataAccount(Object createRoleMessage) {
			LoginGetRoleDataRequest request = (LoginGetRoleDataRequest) createRoleMessage;
			return request.getAccount();
		}

		@Override
		public boolean getRoleObject(Ref<RoleInterface> ref, Object createRoleMessage,
				Ref<Object> errorMessage) {
			LoginGetRoleDataRequest request = (LoginGetRoleDataRequest) createRoleMessage;
			Role role = getRoleByAccount(request.getAccount());
			if (role == null) {
				errorMessage
						.set(SCMessage.newBuilder()
								.setLoginGetRoleDataResponse(LoginGetRoleDataResponse.newBuilder().setErrorCode(30103))
								.build());
				return false;
			}

			ref.set(role);

			return true;
		}

		@Override
		public boolean connectingError(Ref<Object> errorConnectingMessage) {
			errorConnectingMessage
					.set(SCMessage
							.newBuilder()
							.setLoginGetRoleDataResponse(
									LoginGetRoleDataResponse.newBuilder().setErrorCode(ErrorCode.IN_LOGIN)).build());
			return true;
		}

	}

	/**
	 * 初始化用户数据
	 * 
	 * @param account
	 * @param conn
	 * @return
	 */
	private Role roleInit(String account, SqlSession sqlSession, String name) {
		// 创建用户
		Role role = new Role();
		role.setAccount(account);

		// String name = this.getRandowName();
		role.setName(name);

		
		
		roleDao.insertRole(role);
		//mybais不能在事务未提交的过程中就获得自增主键，所以只好先commit一次
		sqlSession.commit();

		
		return role;
	}

	

	@Override
	public void loginRoleModuleDataInit(Role role) {
		// 将数据库中的数据放入缓存中
		RoleCache.putRoleCache(role);

		
	}

	@Override
	public Object getRoleData(Object requestMessage, IoSession ioSession) {
		return loginModelService.getRoleData(requestMessage, ioSession);
	}

	@Override
	public Object creatRole(Object msg) {
		return loginModelService.creatRole(msg);
	}

	@Override
	public Object login(Object msg) {
		return loginModelService.login(msg);
	}

	@Override
	public Role getRoleById(int roleId) {
		Role role = (Role) RoleCache.getRoleById(roleId);
		if (role == null) {
			role = roleDao.getRoleById(roleId);
			if (role == null) 
				return role;
			this.loginRoleModuleDataInit(role);
		}
		return role;
	}

	@Override
	public Role getRoleByAccount(String account) {
		Role role = (Role) RoleCache.getRoleByAccount(account);
		if (role == null) {
			role = roleDao.getRoleByAccount(account);
			if (role == null)
				return role;
			this.loginRoleModuleDataInit(role);
		}

		return role;
	}
}
