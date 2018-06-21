package cn.xian.vertxdemo.api;

import top.onceio.core.mvc.annocations.Api;
import top.onceio.core.mvc.annocations.Param;

@Api("/callback")
public class CallbackApi {
	
	@Api("/alipay")
	public String alipay(@Param("app_auth_code")String authCode) {
		return authCode;
	}
}
