package cn.xian.vertxdemo.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import top.onceio.core.annotation.Config;
import top.onceio.core.annotation.Def;

@Def("weiboOAuth2")
public class WeiboOAuth2 implements OAuth2 {

	public static final String AUTHORIZE_URL = "https://api.weibo.com/oauth2/authorize";
	public static final String ALIPAY_GATEWAY_URL = "https://api.weibo.com/oauth2/access_token";
	public static final String ONCEIO_GATEWAY_URL = "https://api.weibo.com/oauth2/get_token_info";
	@Config("weibo_callback")
	public String WEIBO_CALLBACK = "http://www.onceio.top/weibo/callback";
	@Config("weibo_appid")
	private String WEIBO_APPID = "284264731";
	@Config("alipay_private_key")
	private String APP_PRIVATE_KEY = "be497cad424f7d9a2763c0dbb46fcf06";
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
			String uri = ALIPAY_GATEWAY_URL + String.format("?client_id=%s&client_secret=%s&grant_type=authorization_code&redirect_uri=%s&code=%s", WEIBO_APPID,APP_PRIVATE_KEY, redirectUri,code);
			OAuth2Token token = new OAuth2Token();
			
			return token;
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	@Override
	public OAuth2User getOAuth2User(String accessToken) {
		
		return null;
	}
}
