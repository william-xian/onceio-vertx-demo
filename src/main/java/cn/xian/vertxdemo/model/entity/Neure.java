package cn.xian.vertxdemo.model.entity;

import top.onceio.core.db.annotation.Col;
import top.onceio.core.db.annotation.Tbl;
import top.onceio.core.db.tbl.OEntity;

@Tbl
public class Neure extends OEntity{
	@Col
	private String name;
	@Col
	private String brief;
	@Col
	private String tag;
	@Col
	private Long creatorId;
	
}
