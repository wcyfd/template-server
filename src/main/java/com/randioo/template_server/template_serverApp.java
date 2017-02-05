package com.randioo.template_server;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.filter.codec.ProtocolCodecFilter;

import com.randioo.randioo_server_base.module.ServiceManager;
import com.randioo.randioo_server_base.net.ServerConfig;
import com.randioo.randioo_server_base.net.SpringContext;
import com.randioo.randioo_server_base.net.WanServer;
import com.randioo.randioo_server_base.net.protocal.protobuf.ServerMessageCodecFactory;
import com.randioo.randioo_server_base.utils.ConfigLoader;
import com.randioo.randioo_server_base.utils.db.DatabaseInitialization;
import com.randioo.randioo_server_base.utils.sensitive.SensitiveWordDictionary;
import com.randioo.randioo_server_base.utils.system.SystemManager;

/**
 * Hello world!
 *
 */
public class template_serverApp 
{
    public static void main( String[] args )
    {
    	int port = 9998;

		ConfigLoader.loadConfig("com.randioo.template_server.entity.file", "./config.zip");		
		SensitiveWordDictionary.readAll("./sensitive.txt");

		SpringContext.initSpringCtx("ApplicationContext.xml");

		// 初始化数据库
		DatabaseInitialization databaseInitialization = SpringContext.getBean("databaseInitialization");
		databaseInitialization.setDatabaseName(databaseInitialization.getDatabaseName() + port);
		databaseInitialization.initialize();

		// 服务初始化
		((ServiceManager) SpringContext.getBean("serviceManager")).initServices();

		// 服务器开关
		SystemManager systemManager = SpringContext.getBean("systemManager");
		systemManager.close();

		System.out.println("Hello template_serverApp!");

		WanServer.startServer(
				new ProtocolCodecFilter(new ServerMessageCodecFactory(Charset.forName(ServerConfig.getCharSet()))),
				new ServerHandler(), new InetSocketAddress(port));
		systemManager.open();
    }
}
