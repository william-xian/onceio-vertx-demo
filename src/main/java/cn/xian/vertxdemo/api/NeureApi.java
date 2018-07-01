package cn.xian.vertxdemo.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.xian.vertxdemo.holder.NeureHolder;
import cn.xian.vertxdemo.holder.NeureRefenceHolder;
import cn.xian.vertxdemo.model.entity.Neure;
import top.onceio.core.annotation.Using;
import top.onceio.core.db.dao.Page;
import top.onceio.core.db.dao.tpl.Cnd;
import top.onceio.core.db.dao.tpl.Tpl;
import top.onceio.core.mvc.annocations.Api;
import top.onceio.core.mvc.annocations.Cookie;
import top.onceio.core.mvc.annocations.Param;

@Api("/neure")
public class NeureApi {

	@Using
	private NeureHolder neureHolder;
	@Using
	private NeureRefenceHolder neureRefenceHolder;
	
	private static final String splitPattern = ">|<|=|:|!|,|;|\n";
	
	/**
	 * 六种关系九种操作 以行为单位
		1. 推导 A > B 
		例：有钱 > 买喜欢的东西 > 开心
		
		2. 依赖 A < B 
		例：有钱 <  辛苦工作 < 不开心
		
		3. 相悖 A ! B 
		例：开心 ! 不开心
		可以理解为 A和B 都会生成未知不确定东西
		
		4. 经验 A : B 
		例： 有人穿裙子 : 杏子熟了
		夏天到了 > 杏子熟了
		夏天到了 > 天热了 > 姑娘们穿裙子
		很多时候我们认定某种等价关系但不知其中缘由
		
		5. 等价 A = B 
		例：西红柿 = 番茄
		
		6. 条件 A，B，C > D
		例： 米,蛋,西红柿 > 蛋炒饭
		
		7. 并列（多输入 ）A;B;C > D
		例： 飞机;火车;步行 > 北京
		
		8. 依赖组合 D < A，B，C
		例：喜欢的人 < 高，富，帅
		
		9. 依赖并列 D < A;B
		例： 君子 < 文，质; 坦荡荡;务本
	 */
	@Api("/push")
	public void push(@Cookie("userId")Long creatorId,@Param("topicId")Long topicId,@Param("relation")String relation) {
		String[] neures = relation.split(splitPattern);
		if(neures.length > 0) {
			Cnd<Neure> cnd = new Cnd<>(Neure.class);
			cnd.in(neures).setName(Tpl.USING_S);
			cnd.and().eq().setCreatorId(creatorId);
			if(topicId != null) {
				cnd.and().eq().setTopicId(topicId);
			}
			cnd.setPagesize(neures.length);
			Page<Neure> exists = neureHolder.find(cnd);
			Map<String,Neure> nameToNeure = new HashMap<>();
			for(Neure n:exists.getData()) {
				nameToNeure.put(n.getName(), n);
			}
			List<Neure> news = new ArrayList<>();
			for(String n:neures) {
				String name = n.trim();
				if(!name.equals("") && !nameToNeure.containsKey(name)) {
					Neure e = new Neure();
					e.setName(name);
					e.setCreatorId(creatorId);
					e.setTopicId(topicId);
					news.add(e);
					nameToNeure.put(name, e);
				}
			}
			neureHolder.batchInsert(news);
			
			Pattern pattern = Pattern.compile(splitPattern);
			
			Matcher matcher = pattern.matcher(relation);
			
			//TODO
			while(matcher.find()) {
				String opt = relation.substring(matcher.start(), matcher.end());
				if(opt.equals("\n")) {
					
				}
			}
		}
		
	}
}