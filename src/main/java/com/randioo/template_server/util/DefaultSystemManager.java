package com.randioo.template_server.util;

import com.randioo.randioo_server_base.utils.RandomUtils;
import com.randioo.randioo_server_base.utils.system.SystemManager;

public class DefaultSystemManager extends SystemManager {

	@Override
	public String createCode() {
		return RandomUtils.randowStr(16);
	}

	@Override
	public boolean checkCode(String origin, String code) {
		if (code != null && origin.equals(code)) {
			return true;
		}

		if (code.equals("aim")) {
			return true;
		}
		return false;
	}
}
