package cn.xian.vertxdemo.model.entity;

import top.onceio.core.db.annotation.Col;
import top.onceio.core.db.annotation.Tbl;
import top.onceio.core.db.tbl.OEntity;

@Tbl
public class NeureRefence extends OEntity{
	@Col(ref=Neure.class)
	private Long dependId;
	@Col(ref=Neure.class)
	private Long deduceId;
	@Col
	private Long comb;
	public Long getDependId() {
		return dependId;
	}
	public void setDependId(Long dependId) {
		this.dependId = dependId;
	}
	public Long getDeduceId() {
		return deduceId;
	}
	public void setDeduceId(Long deduceId) {
		this.deduceId = deduceId;
	}
	public Long getComb() {
		return comb;
	}
	public void setComb(Long comb) {
		this.comb = comb;
	}
	
}
