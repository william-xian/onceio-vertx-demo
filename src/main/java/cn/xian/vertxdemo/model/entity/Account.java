package cn.xian.vertxdemo.model.entity;

import cn.xian.vertxdemo.model.constant.AccountGenre;
import top.onceio.core.db.annotation.Col;
import top.onceio.core.db.annotation.Tbl;
import top.onceio.core.db.tbl.OEntity;

@Tbl
public class Account extends OEntity {
	@Col(size = 32, nullable = true)
	private String account;
	@Col(size = 32)
	private transient String passwd;
	@Col(valRef=AccountGenre.class)
	private Integer genre;
	@Col
	private Long refId;
	
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
	public Long getRefId() {
		return refId;
	}
	public void setRefId(Long refId) {
		this.refId = refId;
	}
	
}
