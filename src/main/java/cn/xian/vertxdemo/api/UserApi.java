package cn.xian.vertxdemo.api;

import java.util.HashMap;
import java.util.Map;

import cn.xian.vertxdemo.holder.AccountHolder;
import cn.xian.vertxdemo.holder.UserinfoHolder;
import cn.xian.vertxdemo.i18n.UserMessage;
import cn.xian.vertxdemo.model.entity.Account;
import cn.xian.vertxdemo.model.entity.Userinfo;
import cn.xian.vertxdemo.model.vo.UserToken;
import cn.xian.vertxdemo.utils.MD5;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import top.onceio.core.annotation.Using;
import top.onceio.core.annotation.Validate;
import top.onceio.core.beans.ApiMethod;
import top.onceio.core.db.dao.tpl.Cnd;
import top.onceio.core.exception.Failed;
import top.onceio.core.mvc.annocations.Api;
import top.onceio.core.mvc.annocations.Param;
import top.onceio.core.util.Tuple2;

@Api("/user")
public class UserApi {
	@Using
	private AccountHolder accountHolder;
	@Using
	private UserinfoHolder userinfoHolder;
	@Using
	private EventBus eb;

	@Api(value="/signup", method = ApiMethod.POST)
	public Userinfo signup(@Validate(nullable=false)@Param("account") String account,@Validate(nullable=false,pattern=".{6,}") @Param("passwd") String passwd) {
		Cnd<Account> cnd = new Cnd<>(Account.class);
		cnd.eq().setAccount(account);
		Account entity = accountHolder.fetch(null, cnd);
		if(entity != null) {
			Failed.throwError(UserMessage.USER_EXIST, account);
		}
		entity = new Account();
		entity.setAccount(account);
		entity.setPasswd(MD5.encode(passwd));
		accountHolder.insert(entity);
		Userinfo ui = new Userinfo();
		ui.setId(entity.getId());
		ui.setNickname(account);
		userinfoHolder.insert(ui);
		return ui;
	}
	@Api(value="/signin", method = ApiMethod.POST)
	public Tuple2<UserToken,Userinfo> signin(@Validate(nullable=false)@Param("account") String account, @Param("passwd") String passwd) {
		Cnd<Account> cnd = new Cnd<>(Account.class);
		cnd.eq().setAccount(account);
		Account entity = accountHolder.fetch(null, cnd);
		if(entity == null || !entity.getPasswd().equals(MD5.encode(passwd))) {
			Failed.throwError("用户不存在或者密码不正确！");
		}
		UserToken userToken = new UserToken();
		userToken.setAccessToken(entity.getAccessToken());
		userToken.setUserId(entity.getId());
		Userinfo ui = null;
		if(entity.getRefId() != null) {
			ui = userinfoHolder.get(entity.getRefId());	
		}else {
			ui = userinfoHolder.get(entity.getId());
		}
		return new Tuple2<>(userToken, ui);
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
