package cn.xian.vertxdemo.model.entity;

import top.onceio.core.db.annotation.Col;
import top.onceio.core.db.annotation.Tbl;
import top.onceio.core.db.tbl.OEntity;

@Tbl(extend=Userinfo.class)
public class UserWallet extends OEntity{
	@Col
	private Integer income;
	@Col
	private Integer expenditure;
	
	public Integer getIncome() {
		return income;
	}
	public void setIncome(Integer income) {
		this.income = income;
	}
	public Integer getExpenditure() {
		return expenditure;
	}
	public void setExpenditure(Integer expenditure) {
		this.expenditure = expenditure;
	}
	
	public Integer getBalance() {
		return income - expenditure;
	}
}
