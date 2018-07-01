package cn.xian.vertxdemo.model.entity;

import top.onceio.core.db.annotation.Col;
import top.onceio.core.db.annotation.Tbl;
import top.onceio.core.db.tbl.OEntity;

@Tbl
public class Topic extends OEntity{
	@Col(size=32)
	private String name;
	@Col(size=255)
	private String brief;
	@Col
	private Long genre;
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
	
}
