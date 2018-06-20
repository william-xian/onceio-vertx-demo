package cn.xian.vertxdemo.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import cn.xian.vertxdemo.holder.AccountHolder;
import cn.xian.vertxdemo.holder.UserinfoHolder;
import cn.xian.vertxdemo.model.entity.Account;
import cn.xian.vertxdemo.model.entity.Userinfo;
import cn.xian.vertxdemo.utils.MD5;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import top.onceio.core.annotation.Config;
import top.onceio.core.annotation.Using;
import top.onceio.core.db.dao.tpl.Cnd;
import top.onceio.core.exception.Failed;
import top.onceio.core.mvc.annocations.Api;
import top.onceio.core.mvc.annocations.Param;

@Api("/user")
public class UserApi {
	@Using
	private AccountHolder accountHolder;
	@Using
	private UserinfoHolder userinfoHolder;
	@Using
	private EventBus eb;
	@Config("alipay_appid")
	private String ALIPAY_APPID;
	@Config("alipay_callback")
	private String ALIPAY_CALLBACK;
	
	public static final String ALIPAY_SIGNIN_FORMAT = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=%s&scope=get_user_info&redirect_uri=%s&state=init";
	
	@Api("/signup")
	public Account signup(@Param("account") String account, @Param("passwd") String passwd) {
		Cnd<Account> cnd = new Cnd<>(Account.class);
		cnd.eq().setAccount(account);
		Account entity = accountHolder.fetch(null, cnd);
		if(entity != null) {
			Failed.throwError("账户：%s　已经存在！", account);
		}
		entity = new Account();
		entity.setAccount(account);
		entity.setPasswd(MD5.encode(passwd));
		accountHolder.insert(entity);
		return entity;
	}	
	@Api("/signin")
	public Userinfo signin(@Param("account") String account, @Param("passwd") String passwd) {
		Cnd<Account> cnd = new Cnd<>(Account.class);
		cnd.eq().setAccount(account);
		Account entity = accountHolder.fetch(null, cnd);
		if(entity == null || entity.getPasswd().equals(MD5.encode(passwd))) {
			Failed.throwError("用户不存在或者密码不正确！");
		}
		Userinfo ui = userinfoHolder.get(entity.getRefId());
		return ui;
	}
	
	@Api("/alientry")
	public String signinentry() {
		try {
			String redirect_uri = URLEncoder.encode(ALIPAY_CALLBACK,"UTF8");
			return String.format(ALIPAY_SIGNIN_FORMAT, ALIPAY_APPID,redirect_uri);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
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
