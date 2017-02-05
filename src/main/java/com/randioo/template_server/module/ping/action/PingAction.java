package com.randioo.template_server.module.ping.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.template_server.module.ping.service.PingService;
import com.randioo.template_server.protocol.Ping.PingRequest;

@Controller
@PTAnnotation(PingRequest.class)
public class PingAction implements IActionSupport {

	@Autowired
	private PingService pingService;

	public void execute(Object data, IoSession session) {
		GeneratedMessage message = pingService.ping(session, ((PingRequest) data).getClientTimestamp());
		if (message != null) {
			session.write(message);
		}
	}
}
