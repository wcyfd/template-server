package com.randioo.template_server.module.gm.service;

import java.util.Collection;
import java.util.Iterator;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.module.BaseService;
import com.randioo.randioo_server_base.utils.system.Platform;
import com.randioo.randioo_server_base.utils.system.Platform.OS;
import com.randioo.randioo_server_base.utils.system.SignalTrigger;
import com.randioo.randioo_server_base.utils.system.SystemManager;
import com.randioo.randioo_server_base.utils.template.Function;
import com.randioo.template_server.SessionCloseHandler;
import com.randioo.template_server.entity.bo.Role;

@Service("gmService")
public class GmServiceImpl extends BaseService implements GmService {

	@Autowired private SystemManager systemManager;

	@Override
	public void init() {		
		Function function = new Function(){

			@Override
			public Object apply(Object... params) {
				systemManager.close();
				
				System.out.println("port close");	
				System.out.println("start save");			
				
				everybodyOffline();
				
				System.out.println("save complete");
				
				System.exit(0);
				return null;
			}
			
		};
		
		//命令关闭信号
		try {
			System.out.println(Platform.getOS());
			if (Platform.getOS() == OS.WIN)
				SignalTrigger.setSignCallback("INT", function);
			else if (Platform.getOS() == OS.LINUX)
				SignalTrigger.setSignCallback("ABRT", function);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 所有人下线
	 * 
	 * @author wcy 2016年12月9日
	 */
	private void everybodyOffline() {
		// 所有人下线
		Collection<IoSession> allSession = SessionCache.getAllSession();
		Iterator<IoSession> it = allSession.iterator();
		while (it.hasNext()) {
			it.next().close(true);
		}

		for (RoleInterface roleInterface : RoleCache.getRoleMap().values()) {
			try {
				SessionCloseHandler.manipulate((Role) roleInterface);
			} catch (Exception e) {
				System.out.println("Role: " + roleInterface.getRoleId() + " saveError!");
				e.printStackTrace();
			}
		}
	}

	
}
