package com.randioo.template_server.module.login.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.template_server.module.login.service.LoginService;
import com.randioo.template_server.protocol.Login.LoginCreateRoleRequest;

@Controller
@PTAnnotation(LoginCreateRoleRequest.class)
public class LoginCreateRoleAction implements IActionSupport {

	@Autowired
	private LoginService loginService;

	@Override
	public void execute(Object data, IoSession session) {
		Object sc = loginService.creatRole(data);
		if (sc != null) {
			session.write(sc);
		}
		
	}

}
