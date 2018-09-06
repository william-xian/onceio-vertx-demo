package cn.xian.vertxdemo.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.xian.vertxdemo.holder.NeureHolder;
import cn.xian.vertxdemo.holder.NeureRelationHolder;
import cn.xian.vertxdemo.model.entity.Neure;
import cn.xian.vertxdemo.model.entity.NeureRelation;
import top.onceio.core.annotation.Using;
import top.onceio.core.beans.ApiMethod;
import top.onceio.core.db.dao.Page;
import top.onceio.core.db.dao.tpl.Cnd;
import top.onceio.core.db.dao.tpl.Tpl;
import top.onceio.core.mvc.annocations.Api;
import top.onceio.core.mvc.annocations.Cookie;
import top.onceio.core.mvc.annocations.Param;

@Api("/neure_relation")
public class NeureApi {

	@Using
	private NeureHolder neureHolder;
	@Using
	private NeureRelationHolder neureRelationHolder;
	
	private static final String splitPattern = ">|<|=|:|!|,|;|\n";

	private int saveNeures(Long creatorId, Long topicId, String relation, Map<String, Neure> nameToNeure) {
		String[] neures = relation.split(splitPattern);
		int cnt = 0;
		Cnd<Neure> cnd = new Cnd<>(Neure.class);
		cnd.in(neures).setName(Tpl.USING_S);
		cnd.and().eq().setCreatorId(creatorId);
		if(topicId != null) {
			cnd.and().eq().setTopicId(topicId);
		}
		cnd.setPagesize(neures.length);
		Page<Neure> exists = neureHolder.find(cnd);
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
		cnt = neureHolder.batchInsert(news);
		return cnt;
	}
	
	
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
	@Api(value="/push", method=ApiMethod.POST)
	public int push(@Cookie("userId")Long creatorId,@Param("topicId")Long topicId,@Param("relation")String relation) {
		if(!relation.endsWith("\n")) {
			relation = relation + "\n";
		}
		Map<String,Neure> nameToNeure = new HashMap<>();
		int cnt = saveNeures(creatorId,topicId,relation, nameToNeure);
		if(!nameToNeure.isEmpty()) {
			Pattern pattern = Pattern.compile(splitPattern);
			Matcher matcher = pattern.matcher(relation);
			int last = 0;
			List<List<String>> left = new ArrayList<>();
			List<List<String>> right = new ArrayList<>();
			List<String> group = new ArrayList<>();
			List<List<String>> cur = left;
			String rel = null;
			Set<String> rels = new HashSet<>();
			rels.addAll(Arrays.asList(">","<","!",":","="));
			List<NeureRelation> nrs = new ArrayList<>();
			while(matcher.find()) {
				String a = relation.substring(last, matcher.start());
				last = matcher.end();
				group.add(a);
				String opt = relation.substring(matcher.start(), matcher.end());
				if(rels.contains(opt)) {
					rel=opt;
					cur.add(group);
					group = new ArrayList<>();
					if(rel.equals("<")) {
						cur = right;
						right = left;
						left = cur;
					}
					cur = right;
					group = new ArrayList<>();
					cur.add(group);
				}
				if(opt.equals(",")) {
				} else if(opt.equals(";")) {
					cur.add(group);
					group =new ArrayList<>();
				} else if(opt.equals("\n")) {
					String dname = right.get(0).get(0);
					Neure deduced = nameToNeure.get(dname);
					for (List<String> grp : left) {
						long comb = deduced.getId();
						for (String name : grp) {
							Neure n = nameToNeure.get(name);
							comb = comb * 63 + n.getId();
						}
						for (String name : grp) {
							NeureRelation nr = new NeureRelation();
							Neure n = nameToNeure.get(name);
							nr.setDependId(n.getId());
							nr.setDeduceId(deduced.getId());
							nr.setComb(comb);
							nr.setRelation(rel);
							nrs.add(nr);
						}
						/** 双向推导 */
						if (opt.equals("!") || opt.equals("=") || opt.equals(":")) {
							for (String name : grp) {
								NeureRelation nr = new NeureRelation();
								Neure n = nameToNeure.get(name);
								nr.setDeduceId(n.getId());
								nr.setDependId(deduced.getId());
								nr.setComb(comb);
								nr.setRelation(rel);
								nrs.add(nr);
							}
						}
						
					}
				}
			}
			
			neureRelationHolder.batchInsert(nrs);
		}
		return cnt;
	}
	
	@Api("/searchDepend")
	public Map<String,Object> searchDepend(@Cookie("userId")Long creatorId,@Param("target")String target, @Param("topicIds")List<Long> topicIds) {
		Integer maxStep = 5;
		Cnd<Neure> cn = new Cnd<>(Neure.class);
		cn.and().eq().setName(target);
		Neure n = neureHolder.fetch(null, cn);
		Map<Long,List<NeureRelation>> relation = new HashMap<>();
		List<Long> ids = new ArrayList<>();
		Set<Long> trace = new HashSet<>();
		if(n != null) {
			ids.add(n.getId());
			trace.add(n.getId());
		}
		
		while(!ids.isEmpty() && (--maxStep >= 0)) {
			Cnd<NeureRelation> cndNR = new Cnd<>(NeureRelation.class);
			cndNR.and().in(ids.toArray(new Long[0])).setDeduceId(Tpl.USING_LONG);
			cndNR.setPagesize(100);
			Page<NeureRelation> page = neureRelationHolder.find(cndNR);
			ids.clear();
			for(NeureRelation nr:page.getData()) {
				if(!trace.contains(nr.getId())) {
					trace.add(nr.getId());
					ids.add(nr.getDeduceId());
					List<NeureRelation> list = relation.get(nr.getDeduceId());
					if(list == null) {
						list = new ArrayList<>();
						relation.put(nr.getDeduceId(), list);
					}
					list.add(nr);
				}
			}
		}
		
		Cnd<Neure> cnd = new Cnd<>(Neure.class);
		cnd.setPagesize(10000);
		cnd.and().in(trace.toArray(new Long[0])).setId(Tpl.USING_LONG);
		Page<Neure> all = neureHolder.find(cnd);
		Map<String,Object> result = new HashMap<>();
		result.put("relation", relation);
		result.put("neures", all.getData());
		return result;
	}
	

	@Api("/searchDeduce")
	public Map<String,Object> searchDeduce(@Cookie("userId")Long creatorId,@Param("target") String target, @Param("dependIds")List<Long> dependIds, @Param("topicIds")List<Long> topicIds) {
		Map<String,Object> result = new HashMap<>();
		Integer maxStep = 5;
		Cnd<Neure> cn = new Cnd<>(Neure.class);
		cn.and().eq().setName(target);
		Neure tagetNeure = neureHolder.fetch(null, cn);
		Map<Long,List<NeureRelation>> relation = new HashMap<>();
		List<Long> ids = new ArrayList<>();
		Set<Long> trace = new HashSet<>();
		if(tagetNeure == null) {
			return result;
		}
		ids.addAll(dependIds);
		while(!ids.isEmpty() && (--maxStep >= 0)) {
			Cnd<NeureRelation> cndNR = new Cnd<>(NeureRelation.class);
			cndNR.and().in(ids.toArray(new Long[0])).setDependId(Tpl.USING_LONG);
			cndNR.setPagesize(100);
			Page<NeureRelation> page = neureRelationHolder.find(cndNR);
			ids.clear();
			for(NeureRelation nr:page.getData()) {
				if(!trace.contains(nr.getId())) {
					trace.add(nr.getId());
					ids.add(nr.getDependId());
					List<NeureRelation> list = relation.get(nr.getDependId());
					if(list == null) {
						list = new ArrayList<>();
						relation.put(nr.getDependId(), list);
					}
					list.add(nr);
				}
			}
		}
		
		Cnd<Neure> cnd = new Cnd<>(Neure.class);
		cnd.setPagesize(10000);
		cnd.and().in(trace.toArray(new Long[0])).setId(Tpl.USING_LONG);
		Page<Neure> all = neureHolder.find(cnd);
		result.put("relation", relation);
		result.put("neures", all.getData());
		return result;
	}
	
	@Api("/deleteRelation")
	public int deleteRelations(@Cookie("userId")Long creatorId,@Param("ids")List<Long>ids) {
		Cnd<NeureRelation> cnd = new Cnd<>(NeureRelation.class);
		cnd.and().in(ids.toArray(new Long[0])).setId(Tpl.USING_LONG);
		return neureRelationHolder.delete(cnd);
	}
}
