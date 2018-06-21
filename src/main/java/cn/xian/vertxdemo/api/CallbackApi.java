package cn.xian.vertxdemo.api;

import top.onceio.core.mvc.annocations.Api;
import top.onceio.core.mvc.annocations.Param;
import top.onceio.core.util.OLog;

@Api("/callback")
public class CallbackApi {
	
	@Api("/alipay")
	public void alipay(@Param("app_id")String appId,@Param("source")String source,@Param("app_auth_code")String authCode) {
		OLog.info("app_auth_code : "+authCode);
	}
}
