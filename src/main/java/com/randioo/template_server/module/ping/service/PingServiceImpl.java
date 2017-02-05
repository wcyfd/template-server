package com.randioo.template_server.module.ping.service;

import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.module.BaseService;
import com.randioo.template_server.protocol.Ping.PingResponse;
import com.randioo.template_server.protocol.ServerMessage.SCMessage;

@Service("pingService")
public class PingServiceImpl extends BaseService implements PingService {

	@Override
	public GeneratedMessage ping(IoSession session, long clientTimestamp) {
		return SCMessage
				.newBuilder()
				.setPingResponse(
						PingResponse.newBuilder().setClientTimestamp(clientTimestamp)
								.setServerTimestamp(System.currentTimeMillis())).build();
	}

}
