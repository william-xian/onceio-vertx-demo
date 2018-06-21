package cn.xian.vertxdemo.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserUserinfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserUserinfoShareResponse;

public class AlipayOAuth2 implements OAuth2 {

	public static final String ALIPAY_AUTHORIZE_URL = "https://openauth.alipay.com/oauth2/appToAppAuth.htm";
	public static final String ALIPAY_GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
	public static final String ONCEIO_GATEWAY_URL = "http://www.onceio.top/gateway/alipay";
	public static final String ALIPAY_CALLBACK = "http://www.onceio.top/callback/alipay";
	protected static String ALIPAY_APPID = "2018042902607920";
	protected static String APP_PRIVATE_KEY = "";
	protected static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyEjRdZUK0KxwyyonkICNPI54TAvP9uQrc8/+ZkDzbWjBgUHwNiTKz7pg/RHuLqhdXi+9S9xRsqLnQtFmTe21wZ0Gk+/JDTjxHrY12snCSeQ9osGOe2R6obiJXTDxyYCbMfXxN+c+6dO4ltIUSUpE0J2R1PCuFAPunPiTGVz2b1DwfkbLAfGrKtPTY4bPRazGRX5RchcZdJ9BjOQa54di4VVN/7JLYWEeZgZKTMcbZ2OwV4jms7ONrGYtXMXGxUSyxR2HEGXTZq/cNdgw7t9qv5ExNvYxUXore5q9lpLRD47al69rPgrPRMTzkcYI2/5ikpFYuznu+tCsenEVKJ96wQIDAQAB";

	@Override
	public String authUrl() {
		try {
			String redirectUri = URLEncoder.encode(ALIPAY_CALLBACK, "UTF-8");
			String uri = ALIPAY_AUTHORIZE_URL + String.format("?app_id=%s&redirect_uri=%s", ALIPAY_APPID, redirectUri);
			return uri;
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	@Override
	public OAuth2Token getOAuth2Token(String code) {
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", ALIPAY_APPID, APP_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");  //获得初始化的AlipayClient
		AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();//创建API对应的request类
		request.setGrantType("authorization_code");
		request.setCode("4b203fe6c11548bcabd8da5bb087a83b");
		try {
			AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
			OAuth2Token token = new OAuth2Token();
			token.setUserId(response.getUserId());
			return token;
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//通过alipayClient调用API，获得对应的response类
		return null;
	}

	@Override
	public OAuth2User getOAuth2User(String accessToken) {
		AlipayClient alipayClient = new DefaultAlipayClient(ALIPAY_GATEWAY_URL, ALIPAY_APPID, APP_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");  //获得初始化的AlipayClient
		AlipayUserUserinfoShareRequest request = new AlipayUserUserinfoShareRequest();//创建API对应的request类
		try {
			AlipayUserUserinfoShareResponse response = alipayClient.execute(request, accessToken);
			OAuth2User ui = new OAuth2User();
			ui.setEmail(response.getEmail());
			ui.setNickName(response.getNickName());
			return ui;
		} catch (AlipayApiException e) {
			
		}
		return null;
	}

}
