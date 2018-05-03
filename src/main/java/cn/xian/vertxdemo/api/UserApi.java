package cn.xian.vertxdemo.api;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import top.onceio.core.mvc.annocations.Api;
import top.onceio.core.mvc.annocations.Param;

@Api("/user")
public class UserApi {
	
	@Api("/signup/{username}")
	public Map<String,Object> signup(@Param("username") String username, @Param("passwd") String passwd) {
		Map<String,Object> map = new HashMap<>();
		map.put("username", username);
		map.put("passwd", passwd);
		return map;
	}
	
	@Api("/config/")
	public void conf(@Param("arg") String arg,HttpServerRequest req) {
		Map<String,Object> map = new HashMap<>();
		map.put("arg", arg);
		map.put("val", req.getHeader(arg));
		req.response().end(Json.encode(map));
	}
}
