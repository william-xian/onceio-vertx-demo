package cn.xian.vertxdemo.oauth;

public class OAuth2User {
	/** 
	 * 头像。
	 */
	private String avatar;
	/** 
	 * 手机号码。
	 */
	private String mobile;
	/** 
	 * 用户昵称
	 */
	private String nickName;
	/** 
	 * 电话号码。
	 */
	private String phone;
	/** 
	 * 省份名称。
	 */
	private String province;
	/** 
	 * 用户的真实姓名。
	 */
	private String realName;

	/** 
	 * 用户支付宝账号绑定的邮箱地址
	 */
	private String email;
	/** 生日 */
	private String birthday;

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
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
