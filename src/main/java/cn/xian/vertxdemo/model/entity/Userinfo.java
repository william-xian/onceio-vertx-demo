package cn.xian.vertxdemo.model.entity;

import top.onceio.core.db.annotation.Col;
import top.onceio.core.db.annotation.Tbl;
import top.onceio.core.db.tbl.OEntity;

@Tbl(extend=Account.class)
public class Userinfo extends OEntity {
	@Col(size=255)
	private String avatar;
	/** 
	 * 用户昵称。
	 */
	@Col(size=20)
	private String nickname;
	/** 
	 * 电话号码。
	 */
	@Col(size=20)
	private transient String phone;
	/** 
	 * 省份名称。
	 */
	@Col(size=20)
	private String province;
	/** 
	 * 用户的真实姓名。
	 */
	@Col(size=20)
	private transient String realName;

	/** 
	 * 用户支付宝账号绑定的邮箱地址
	 */
	@Col(size=20)
	private String email;
	@Col(size=16)
	private String birthday;

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

}
