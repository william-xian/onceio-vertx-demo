package cn.xian.vertxdemo.model.entity;

import top.onceio.core.db.annotation.Col;
import top.onceio.core.db.annotation.Constraint;
import top.onceio.core.db.annotation.Tbl;
import top.onceio.core.db.tbl.OEntity;
import top.onceio.core.util.MD5Updator;

@Tbl(constraints= {@Constraint(colNames= {"dependId","deduceId","relation","comb"})})
public class NeureRelation extends OEntity{
	@Col(ref=Neure.class)
	private Long dependId;
	@Col(ref=Neure.class)
	private Long deduceId;
	@Col(size=2)
	private String relation;
	@Col
	private Long comb;
	@Col
	private Long code;
	
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
	
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	public Long getComb() {
		return comb;
	}
	public void setComb(Long comb) {
		this.comb = comb;
	}
	public Long getCode() {
		return code;
	}
	public void setCode(Long code) {
		this.code = code;
	}
	public Long generateCode() {
		MD5Updator md5 = new MD5Updator();
		md5.update(((deduceId == null) ? 0 : deduceId));
		md5.update(((dependId == null) ? 0 : dependId));
		md5.update(((relation == null) ? "" : relation));
		md5.update(((comb == null) ? 0 : comb));
		return md5.toBigInteger().longValue()&(-1>>>1);
	}
}
