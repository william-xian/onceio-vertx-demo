package cn.xian.vertxdemo.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;

import top.onceio.core.annotation.Config;
import top.onceio.core.annotation.Def;
import top.onceio.core.annotation.OnCreate;
import top.onceio.core.exception.Failed;
import top.onceio.core.util.OLog;
import top.onceio.core.util.OUtils;

@Def("alipayOAuth2")
public class AlipayOAuth2 implements OAuth2 {

	public static final String ALIPAY_AUTHORIZE_URL = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm";
	public static final String ALIPAY_GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
	public static final String ONCEIO_GATEWAY_URL = "http://www.onceio.top/alipay/gateway";
	@Config("alipay_callback")
	public String ALIPAY_CALLBACK;
	@Config("alipay_appid")
	private String ALIPAY_APPID;
	@Config("alipay_private_key")
	private String ALIPAY_PRIVATE_KEY;
	@Config("alipay_public_key")
	private String ALIPAY_PUBLIC_KEY;
	
	private AlipayClient alipayClient = null;
	@OnCreate
	public void init() {
		alipayClient = new DefaultAlipayClient(ALIPAY_GATEWAY_URL, ALIPAY_APPID, ALIPAY_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");  //获得初始化的AlipayClient
	}

	@Override
	public String authUrl() {
		try {
			String redirectUri = URLEncoder.encode(ALIPAY_CALLBACK, "UTF-8");
			String uri = ALIPAY_AUTHORIZE_URL + String.format("?app_id=%s&scope=auth_user&redirect_uri=%s", ALIPAY_APPID, redirectUri);
			return uri;
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	@Override
	public OAuth2Token getOAuth2Token(String code) {
		AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();//创建API对应的request类
		request.setGrantType("authorization_code");
		request.setCode(code);
		try {
			AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
			if(response.isSuccess()) {
				OAuth2Token token = new OAuth2Token();
				token.setUserId(response.getUserId());
				token.setAccessToken(response.getAccessToken());
				return token;		
			} else {
				Failed.throwErrorData(response.getBody(), "OAuth Failed!");
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			Failed.throwError("errcode:%s,msg:%s",e.getErrCode(),e.getErrMsg());
		}//通过alipayClient调用API，获得对应的response类
		return null;
	}

	@Override
	public OAuth2User getOAuth2User(OAuth2Token token) {
		AlipayUserInfoShareRequest request = new AlipayUserInfoShareRequest();//创建API对应的request类
		try {
			AlipayUserInfoShareResponse response = alipayClient.execute(request, token.getAccessToken());
			OAuth2User ui = new OAuth2User();
			if(response.isSuccess()) {
				ui.setAvatar(response.getAvatar());
				ui.setRealName(response.getUserName());
				ui.setNickName(response.getNickName());
				ui.setPhone(response.getPhone());
				ui.setProvince(response.getProvince());
				ui.setEmail(response.getEmail());
				return ui;
			}else {
				OLog.warn("获取支付宝用户信息失败！accessToken:%s", token.getAccessToken());
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			Failed.throwError("errcode:%s,msg:%s",e.getErrCode(),e.getErrMsg());
		}
		return null;
	}
	public static void main(String[] args) {
		AlipayOAuth2 auth = new AlipayOAuth2();
		auth.ALIPAY_APPID = "2018042902607920";
		auth.ALIPAY_CALLBACK = "http://www.onceio.top/alipay/callback";
		auth.ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAx6KnCl7s1zmzaN+6dkU9yhz7SAcpPuX7bjzlN3S/HI4RZJTXjbf1sy+WUcM4JSCkVeqENfqth1kH2scUNOhP5gwPT6UovFlfj8yrdbeYyJuMoFzUQQ58uarBy9JieGsX4mQ+xcxRwJ0AIjKdApi3QSVRFzF0jheWWkWAWD7Ym+TTVhyhY7NiDX0VY7gKUEDHIo1msL1Dp7MDV9eUPhS04kakHLOhv53JJcrJ4svyhQP+LCedoeeiPxcvNRBrbGzubkjGiRVk+HlOc1TrmSKj93o1A4uy0yWb5+x6nizJW2+G4S+6d9p2fpURe9R7XlYV1AkPkDWk3Qk1sIp9L5wCtQIDAQAB";
		auth.ALIPAY_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCJboY68lh0ciG75W/H9jIkMefaXU4h5nD2INVE1jszkANTMPfqRJ40NZeLRbxm89egd7/qZGjgWn7SH3Ba11e/EIACgJpEYfIeFa1kp4+roaGWtBpAOqyBJVapRmCY20rHMnCJk/EoRJ5lXLHfM+4CGJ2CI597U5EFpwxaF40ODXBAguZV8bxYVH6dxLI5YQVGQqo908kcR395NNjc3bq8r7UTmE0WDFtZBjd/w3xvKbdqRm2weFYOJjcLd90Vj/TOZGNR9OBslxFxhomeOcH1m0vr2EPzeSSSfcMGNLo+exh7nPRXq2jpFBlshf2wHM3mCKiwRbskJGj8evNquEN5AgMBAAECggEAQhw+ygBmeK2meKKoIQLXn/250JttyHT4Bg5JSXHczLMMRmqZ98HGcMsO+Vo0hTnrki/IzFI8+R/PPfkNrUVF2o1vZsTCcP7E36AB5xeLzz5TYxGERz1yWjXB+G/4a8+hqy0iYawfKAXKRGxQzFRIvA1nR4EnaMYMTzHKiVQm8Ijd1XpnHNBA2g7fGm1js8E1TKJmp2QNbLCqwCsSN1G9DcgZrFu82wsrP34NY+ChuUN8RGF3D5fLGwhuKLwQ6MzIWxd/ECO0WI77sReAuPH8ozgR6xm2FiUsA5gXidD/IqLHRQv9em74jpi+N+mtveazRAX8mv8Ad2LT6XCMNujDAQKBgQD8z88iBer9KPs6Hstptca0pYw/tChtlH33DV3PLDLPMjgixFoHVLs3cH85wLa3XcfyxASzr8ajsSBnJvJww2i8UjvTnBeqd3wmpFWa30MI511790VlYqShlznRTylZj3jgD+L2GmeA13Q47SW0EyArmxrhhHgvTyjr0+1Z/kFfKQKBgQCLKjdb/VWlbMXTiEaOB9eheX55I8U0tZ4LksD65Ev5hyUGcSywubMuo1DSXknWaeY/Chb0Fixnnho3f3yfpnoJwwy5Sglp5INCwoLqngGCjwokUMpnCU8DTudIV8oIvnRDLGkcYNxlucCkzuQIp4X8ttp6p2FrwPJ/UPS2pRZb0QKBgQDYWqezQElIeof1x2DhiLUS6BJMekuW69uZUBEWOYcKFro/1rYNTBAkc9wNesVjy5hQRDyZ4jYm5HEMliIpKrI5aE7W2+DM/BB9qWmxbpwZxJcWfhkfmPm3aIoMfKiwgr45Up8zaollk/1csmbv4uZHKygoE4wfQKmOmQGpYJRlcQKBgAzgax7n4fTci7F2+pBJyXn3c+xhku29oFIR0ilk1mLQTmy6LBhuFlZKZkAQ2WQtiFBs7pLHOnSb/HEGKtHa5Y/hrFu4rgZOTpbuyI5M8HbUJWwWzaObCOSVeHEJLniApuFQSqmFjN8cwOZ+/jqOxacMhEq1Eh6WS9nb88iync8BAoGBANLi4zWpqciFpZWbAxZsqcpBZtRz1cGSQSFh3i3mmKWJ7y3noAK4DGi+dElhqBoCLk+Af32EcuL/5ZG/WFrZ8T7aatfnviZNShLYCV+t0CRftlET5WByxMkx7DcrqW6Gt8WowCIdnn1b5d3co6xRmZn03J2OCNSD7XL+TfhmTC7C";
		auth.init();
		System.out.println(auth.authUrl());
		OAuth2Token token = auth.getOAuth2Token("fece868f96c0414e8d9f3b2cf1d4YD24");
		System.out.println("token-->" + OUtils.toJson(token));
		OAuth2User usr = auth.getOAuth2User(token);
		System.out.println(OUtils.toJson(usr));
	}
}
