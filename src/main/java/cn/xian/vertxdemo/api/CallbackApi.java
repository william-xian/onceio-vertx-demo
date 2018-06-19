package cn.xian.vertxdemo.api;

import top.onceio.core.mvc.annocations.Api;
import top.onceio.core.mvc.annocations.Param;
import top.onceio.core.util.OLog;

@Api("/callback")
public class CallbackApi {
	
	@Api("/alipay")
	public void alipay(@Param("authCode")String authCode) {
		OLog.info(authCode);
	}
}
