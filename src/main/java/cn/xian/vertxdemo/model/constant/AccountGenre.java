package cn.xian.vertxdemo.model.constant;

import top.onceio.core.annotation.I18nCfg;
import top.onceio.core.annotation.I18nCfgBrief;

@I18nCfg("zh")
public class AccountGenre {
	@I18nCfgBrief("支付宝")
	public static Integer ALIPLAY = 1;
	@I18nCfgBrief("微信")
	public static Integer WECHAT = 2;
	@I18nCfgBrief("QQ")
	public static Integer QQ = 3;
	@I18nCfgBrief("微博")
	public static Integer WEIBO = 4;
}
