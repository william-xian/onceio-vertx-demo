package cn.xian.vertxdemo.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.gson.JsonObject;

import cn.xian.vertxdemo.utils.HttpUtils;
import top.onceio.core.annotation.Config;
import top.onceio.core.annotation.Def;
import top.onceio.core.util.OUtils;

@Def("weiboOAuth2")
public class WeiboOAuth2 implements OAuth2 {
	public static final String WEIBO_API_BASE = "https://api.weibo.com/oauth2";
	public static final String AUTHORIZE_URL = WEIBO_API_BASE + "/authorize";
	@Config("weibo_callback")
	public String WEIBO_CALLBACK;
	@Config("weibo_appid")
	private String WEIBO_APPID;
	@Config("weibo_app_secret")
	private String WEIBO_APP_SECRET;
/*	@Using
	private Vertx vertx;
	private HttpClient httpClient;
	@OnCreate()
	private void init() {
		httpClient = vertx.createHttpClient();
	}*/
	
	@Override
	public String authUrl() {
		try {
			String redirectUri = URLEncoder.encode(WEIBO_CALLBACK, "UTF-8");
			String uri = AUTHORIZE_URL + String.format("?client_id=%s&response_type=code&redirect_uri=%s", WEIBO_APPID, redirectUri);
			return uri;
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	@Override
	public OAuth2Token getOAuth2Token(String code) {
		try {
			String redirectUri = URLEncoder.encode(WEIBO_CALLBACK, "UTF-8");
			String param = String.format("client_id=%s&client_secret=%s&grant_type=authorization_code&redirect_uri=%s&code=%s", WEIBO_APPID,WEIBO_APP_SECRET, redirectUri,code);
			String json = HttpUtils.post(WEIBO_API_BASE+"/access_token",param);
			JsonObject jobj = OUtils.gson.fromJson(json, JsonObject.class);
			OAuth2Token token = new OAuth2Token();
			token.setAccessToken(jobj.get("access_token").getAsString());
			json = HttpUtils.post(WEIBO_API_BASE+"/get_token_info", "access_token=" + token.getAccessToken());
			jobj = OUtils.gson.fromJson(json, JsonObject.class);
			token.setUserId(jobj.get("uid").getAsString());
			return token;
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	@Override
	public OAuth2User getOAuth2User(OAuth2Token token) {
		String uri = String.format("https://api.weibo.com/2/users/show.json?access_token=%s&uid=%s", token.getAccessToken(),token.getUserId());
		String json = HttpUtils.get(uri);
		JsonObject jobj = OUtils.gson.fromJson(json, JsonObject.class);
		OAuth2User usr = new OAuth2User();
		usr.setAvatar(jobj.get("avatar_large").getAsString());
		usr.setNickName(jobj.get("screen_name").getAsString());
		usr.setProvince((jobj.get("province").getAsString()));
		return usr;
	}
	
	
	public static void main(String[] args) {
		WeiboOAuth2 auth = new WeiboOAuth2();
		auth.WEIBO_CALLBACK = "http://www.onceio.top/weibo/callback";
		auth.WEIBO_APPID =  "284264731";
		auth.WEIBO_APP_SECRET = "be497cad424f7d9a2763c0dbb46fcf06";
		System.out.println(auth.authUrl());
		OAuth2Token token = auth.getOAuth2Token("851820016ffc7be584878d2451d9df47");
		System.out.println(OUtils.toJson(token));
		OAuth2User usr = auth.getOAuth2User(token);
		System.out.println(OUtils.toJson(usr));
	}
}
