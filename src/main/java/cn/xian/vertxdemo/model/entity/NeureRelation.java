package cn.xian.vertxdemo.model.entity;

import top.onceio.core.db.annotation.Col;
import top.onceio.core.db.annotation.Constraint;
import top.onceio.core.db.annotation.Tbl;
import top.onceio.core.db.tbl.OEntity;

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
		final int prime = 63;
		long result = 1;
		result = prime * result + ((comb == null) ? 0 : comb);
		result = prime * result + ((deduceId == null) ? 0 : deduceId);
		result = prime * result + ((dependId == null) ? 0 : dependId);
		result = prime * result + ((relation == null) ? 0 : relation.hashCode());
		return result;
	}
}
