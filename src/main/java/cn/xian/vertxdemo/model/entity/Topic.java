package cn.xian.vertxdemo.model.entity;

import top.onceio.core.db.annotation.Col;
import top.onceio.core.db.annotation.Constraint;
import top.onceio.core.db.annotation.Tbl;
import top.onceio.core.db.tbl.OEntity;

@Tbl(constraints= {@Constraint(colNames= {"name","ownner"})})
public class Topic extends OEntity{
	@Col(size=32)
	private String name;
	@Col(size=255)
	private String brief;
	@Col
	private Long genre;
	@Col
	private Long ownner;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBrief() {
		return brief;
	}
	public void setBrief(String brief) {
		this.brief = brief;
	}
	public Long getGenre() {
		return genre;
	}
	public void setGenre(Long genre) {
		this.genre = genre;
	}
	public Long getOwnner() {
		return ownner;
	}
	public void setOwnner(Long ownner) {
		this.ownner = ownner;
	}
	
}
