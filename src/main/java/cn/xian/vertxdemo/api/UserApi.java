package cn.xian.vertxdemo.api;

import java.util.HashMap;
import java.util.Map;

import cn.xian.vertxdemo.holder.AccountHolder;
import cn.xian.vertxdemo.model.entity.Account;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import top.onceio.core.annotation.Using;
import top.onceio.core.mvc.annocations.Api;
import top.onceio.core.mvc.annocations.Param;

@Api("/user")
public class UserApi {

	@Using
	private AccountHolder accountHolder;
	@Using
	private EventBus eb;
	
	@Api("/signup/{account}")
	public Account signup(@Param("account") String account, @Param("passwd") String passwd) {
		Account entity = new Account();
		entity.setAccount(account);
		entity.setPasswd(passwd);
		accountHolder.insert(entity);
		return entity;
	}
	
	@Api("/config/")
	public void conf(@Param("arg") String arg,HttpServerRequest req) {
		eb.publish("chat.msg", arg);
		Map<String,Object> map = new HashMap<>();
		map.put("arg", arg);
		map.put("val", req.getHeader(arg));
		req.response().end(Json.encode(map));
	}
	@Api("/send/{msg}")
	public String conf(@Param("msg") String msg) {
		eb.publish("chat.msg", msg);
		return "OK";
	}
}
