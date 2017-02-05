package com.randioo.template_server;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.navigation.Navigation;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.randioo_server_base.net.IoHandlerAdapter;
import com.randioo.randioo_server_base.utils.template.Function;
import com.randioo.template_server.entity.bo.Role;
import com.randioo.template_server.protocol.ClientMessage.CSMessage;

public class ServerHandler extends IoHandlerAdapter {

	private List<Function> actionChains = new ArrayList<>();

	public ServerHandler() {
		init();
	}

	public void init() {
		actionChains.add(new NormalAction());
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("roleId:" + session.getAttribute("roleId") + " sessionCreated");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("roleId:" + session.getAttribute("roleId") + " sessionOpened");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		System.out.println("roleId:" + session.getAttribute("roleId") + " sessionClosed");
		Role role = (Role) RoleCache.getRoleBySession(session);
		if (role != null) {
			try {
				SessionCloseHandler.manipulate(role);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

	}

	@Override
	public void exceptionCaught(IoSession session, Throwable e) throws Exception {

	}

	@Override
	public void messageReceived(IoSession session, Object messageObj) throws Exception {

		InputStream input = (InputStream) messageObj;
		input.mark(0);

		try {
			for (Function func : actionChains) {
				boolean result = (Boolean) func.apply(input, session);
				if (result)
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				input.close();
			}
		}

	}

	/**
	 * 消息事件分发
	 * 
	 * @param message
	 * @param session
	 * @author wcy 2017年1月3日
	 */
	private void actionDispatcher(GeneratedMessage message, IoSession session) {
		Map<FieldDescriptor, Object> allFields = message.getAllFields();
		for (Map.Entry<FieldDescriptor, Object> entrySet : allFields.entrySet()) {

			String name = entrySet.getKey().getName();
			if (name.equals("type")) {
				continue;
			}
			IActionSupport action = Navigation.getAction(name);
			try {
				action.execute(entrySet.getValue(), session);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("伪造的协议ID：" + name);
				session.close(true);
			}
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		if (!message.toString().contains("scFightKeyFrame")) {
			System.out.println(getMessage(message, session));
		}
	}

	private String getMessage(Object message, IoSession session) {
		Integer roleId = (Integer) session.getAttribute("roleId");
		String roleAccount = null;
		String roleName = null;
		if (roleId != null) {
			Role role = (Role) RoleCache.getRoleById(roleId);
			if (role != null) {
				roleAccount = role.getAccount();
				roleName = role.getName();
			}
		}

		return "[account:" + roleAccount + ",name:" + roleName + "] " + message;
	}

	private class NormalAction implements Function {

		@Override
		public Boolean apply(Object... objects) {
			InputStream input = (InputStream) objects[0];
			IoSession session = (IoSession) objects[1];

			boolean result = false;
			try {
				input.reset();
				CSMessage message = CSMessage.parseDelimitedFrom(input);

				if (message != null) {
					result = true;
					System.out.println(getMessage(message, session));
					actionDispatcher(message, session);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

	}

}
