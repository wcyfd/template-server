package com.randioo.template_server.module.login.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.template_server.module.login.service.LoginService;
import com.randioo.template_server.protocol.Login.LoginGetRoleDataRequest;

@Controller
@PTAnnotation(LoginGetRoleDataRequest.class)
public class LoginGetRoleDataAction implements IActionSupport{

	@Autowired
	private LoginService loginService;

	@Override
	public void execute(Object data, IoSession session) {
		// TODO Auto-generated method stub
		Object sc = loginService.getRoleData(data, session);
		
		if (sc != null) {
			session.write(sc);
		}

	}

}
