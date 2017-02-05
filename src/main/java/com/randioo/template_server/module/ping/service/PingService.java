package com.randioo.template_server.module.ping.service;

import org.apache.mina.core.session.IoSession;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.module.BaseServiceInterface;

public interface PingService extends BaseServiceInterface{
	public GeneratedMessage ping(IoSession session,long clientTimestamp);
}
