package cn.xian.vertxdemo.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class OAuth2Utils {
	public static final String QQ_AUTHORIZE_URL= "https://graph.qq.com/oauth2.0/authorize";
    public static final String QQ_ACCESSTOKEN_URL= "https://graph.qq.com/oauth2.0/token";
    public static final String QQ_GET_OPEN_ID_URL = "https://graph.qq.com/oauth2.0/me";
    public static final String QQ_GET_USER_INFO_URL = "https://graph.qq.com/user/get_user_info";	
	
	public static final String WX_AUTHORIZE_URL= "https://open.weixin.qq.com/connect/qrconnect";
	public static final String WX_ACCESSTOKEN_URL= "https://api.weixin.qq.com/sns/oauth2/access_token";
	public static final String WX_VALID_ACCESSTOKEN_OPENID_URL= "https://api.weixin.qq.com/sns/auth";
	public static final String WX_GET_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo";
	
	public static final String SINA_AUTHORIZE_URL= "https://api.weibo.com/oauth2/authorize";
	public static final String SINA_ACCESSTOKEN_URL= "https://api.weibo.com/oauth2/access_token";
	public static final String SINA_GET_TOKEN_INFO = "https://api.weibo.com/oauth2/get_token_info";
	public static final String SINA_GET_USER_INFO_URL = "https://api.weibo.com/2/users/show.json";
	

	protected static String QQ_APPID = "1105016782";
	protected static String QQ_APPKEY = "ZYrzVL7Edc9D09oQ";
	
	protected static String QQ_WEBID = "101368776";
	protected static String QQ_WEBKEY = "5008cd6a0b8fa20758b929f6009869f7";
	protected static String QQ_WEB_CALLBACK =" http://www.danglaoshi.net/auth/oauth/qq/notify";
	
	/*微信APPID*/
    protected static String WX_APPID="wxfd75c4b4521fbdb7";
    /*微信APPKEY*/
    protected static String WX_APPKEY="fef931291d6bc22b2d513e847e57c0d9";
    
    /*微信APPID*/
    protected static String WX_MP_APPID="wx493792e22e2e5b7d";
    /*微信APPKEY*/
    protected static String WX_MP_APPKEY="";
	
	/*新浪微博APPID*/
    protected static String SINA_APPID="2290709841";
    /*新浪微博APPKEY*/
    protected static String SINA_APPKEY="bf9f2363eb640b0d162ffd430c3d07ed";
    
    public static String getQQAuthorizationCode(String clientId,String redirectUri,String state,String scope,String display) {
    	try {
			redirectUri = URLEncoder.encode(redirectUri, "UTF-8");
	    	String uri = QQ_AUTHORIZE_URL + String.format("?response_type=code&client_id=%s&redirect_uri=%s&state=%s", clientId,redirectUri,state);
	    	if(scope != null) {
	    		uri = uri + "&scope="+scope;
	    	}
	    	if(display != null) {
	    		uri = uri + "&display="+display;
	    	}
	    	return uri;
		} catch (UnsupportedEncodingException e) {
		}
    	return null;
    }    
    public static void main(String[] args) {
    	System.out.println(getQQAuthorizationCode(QQ_WEBID,QQ_WEB_CALLBACK,"mx",null,null));
    }
}
