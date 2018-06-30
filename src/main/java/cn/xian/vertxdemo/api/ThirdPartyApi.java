package cn.xian.vertxdemo.api;

import java.util.UUID;

import cn.xian.vertxdemo.holder.AccountHolder;
import cn.xian.vertxdemo.holder.UserinfoHolder;
import cn.xian.vertxdemo.model.constant.AccountGenre;
import cn.xian.vertxdemo.model.entity.Account;
import cn.xian.vertxdemo.model.entity.Userinfo;
import cn.xian.vertxdemo.model.vo.UserToken;
import cn.xian.vertxdemo.oauth.OAuth2;
import cn.xian.vertxdemo.oauth.OAuth2Token;
import cn.xian.vertxdemo.oauth.OAuth2User;
import top.onceio.core.annotation.Using;
import top.onceio.core.db.dao.tpl.Cnd;
import top.onceio.core.db.dao.tpl.UpdateTpl;
import top.onceio.core.mvc.annocations.Api;
import top.onceio.core.mvc.annocations.Param;
import top.onceio.core.util.Tuple2;

@Api
public class ThirdPartyApi {

	@Using("alipayOAuth2")
	private OAuth2 alipayOAuth2;
	@Using("weiboOAuth2")
	private OAuth2 weiboOAuth2;
	@Using
	private AccountHolder accountHolder;
	@Using
	private UserinfoHolder userinfoHolder;	
	

	
	private Tuple2<UserToken,Userinfo> getUserinfo(OAuth2 auth, String authCode) {
		OAuth2Token token = auth.getOAuth2Token(authCode);
		if(token != null) {
			Cnd<Account> cnd = new Cnd<>(Account.class);
			Account account = accountHolder.fetch(null, cnd);
			if(account != null) {
				account.setEnv(System.currentTimeMillis()+"");
				UpdateTpl<Account> tpl = new UpdateTpl<>(Account.class);
				tpl.set().setEnv(account.getEnv());
				accountHolder.updateByTpl(tpl);
				Userinfo ui = userinfoHolder.get(account.getId());
				return initUserinfo(auth, token, account,ui);
			}else {
				account = new Account();
				account.setAccount(token.getUserId());
				account.setPasswd(UUID.randomUUID().toString().replaceAll("-", ""));
				account.setGenre(AccountGenre.ALIPLAY);
				account.setEnv(System.currentTimeMillis()+"");
				accountHolder.insert(account);
				return initUserinfo(auth,token, account, null);
			}
		}
		return null;
	}
	
	
	private Tuple2<UserToken,Userinfo> initUserinfo(OAuth2 auth, OAuth2Token token,Account account,Userinfo ui) {
		UserToken userToken = new UserToken();
		userToken.setAccessToken(account.getAccessToken());
		userToken.setUserId(account.getId());
		if(ui != null) {
			return new Tuple2<>(userToken, ui);
		} else {
			OAuth2User oau = auth.getOAuth2User(token);
			if(oau != null) {
				ui = new Userinfo();
				ui.setId(account.getId());
				ui.setAvatar(oau.getAvatar());
				ui.setEmail(oau.getEmail());
				ui.setNickname(oau.getNickName());
				ui.setPhone(oau.getPhone());
				ui.setProvince(oau.getProvince());
				ui.setRealName(oau.getRealName());
				userinfoHolder.insert(ui);
				return new Tuple2<>(userToken,ui);
			}
		}
		return null;
	}
	@Api("/alipay/authurl")
	public String alipayAuthUrl() {
		return alipayOAuth2.authUrl();
	}
	@Api("/alipay/callback")
	public Tuple2<UserToken,Userinfo> alipayCallbak(@Param("app_id")String appId,@Param("source")String source,@Param("auth_code")String authCode) {
		return getUserinfo(alipayOAuth2,authCode);

	}
	@Api("/weibo/authurl")
	public String weiboAuthUrl() {
		return weiboOAuth2.authUrl();
	}
	@Api("/weibo/callback")
	public Tuple2<UserToken,Userinfo> weiboCalback(@Param("code")String authCode) {
		return getUserinfo(weiboOAuth2,authCode);

	}
}
