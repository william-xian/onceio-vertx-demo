package cn.xian.vertxdemo.model.entity;

import cn.xian.vertxdemo.model.constant.AccountGenre;
import cn.xian.vertxdemo.utils.AES;
import top.onceio.core.db.annotation.Col;
import top.onceio.core.db.annotation.Tbl;
import top.onceio.core.db.tbl.OEntity;

@Tbl
public class Account extends OEntity {
	@Col(size = 32, nullable = true)
	private String account;
	@Col(size = 32)
	private transient String passwd;
	@Col(valRef = AccountGenre.class)
	private Integer genre;
	@Col
	private Long refId;
	/**
	 * ip时间等
	 */
	@Col(size = 32)
	private transient String env;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public Integer getGenre() {
		return genre;
	}

	public void setGenre(Integer genre) {
		this.genre = genre;
	}

	public Long getRefId() {
		return refId;
	}

	public void setRefId(Long refId) {
		this.refId = refId;
	}


	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}
	public String getAccessToken() {
		return AES.encode(account, passwd + env);
	}
}
