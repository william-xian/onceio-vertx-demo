package cn.xian.vertxdemo.oauth;

public interface OAuth2 {
	String authUrl();
	OAuth2Token getOAuth2Token(String code);
	OAuth2User getOAuth2User(String accessToken);
}
