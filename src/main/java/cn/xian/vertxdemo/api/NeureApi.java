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

import org.apache.log4j.Logger;

import cn.xian.vertxdemo.holder.NeureHolder;
import cn.xian.vertxdemo.holder.NeureRelationHolder;
import cn.xian.vertxdemo.holder.TopicHolder;
import cn.xian.vertxdemo.model.entity.Neure;
import cn.xian.vertxdemo.model.entity.NeureRelation;
import cn.xian.vertxdemo.model.entity.Topic;
import top.onceio.core.annotation.Using;
import top.onceio.core.beans.ApiMethod;
import top.onceio.core.db.dao.Page;
import top.onceio.core.db.dao.tpl.Cnd;
import top.onceio.core.db.dao.tpl.SelectTpl;
import top.onceio.core.db.dao.tpl.Tpl;
import top.onceio.core.exception.Failed;
import top.onceio.core.mvc.annocations.Api;
import top.onceio.core.mvc.annocations.Header;
import top.onceio.core.mvc.annocations.Param;
import top.onceio.core.util.MD5Updator;
import top.onceio.core.util.OUtils;

@Api("/neure_relation")
public class NeureApi {
	private static final Logger LOGGER = Logger.getLogger(NeureApi.class);
	@Using
	private NeureHolder neureHolder;
	@Using
	private TopicHolder topicHolder;
	@Using
	private NeureRelationHolder neureRelationHolder;
	
	private static final Pattern splitPattern = Pattern.compile(">|<|=|:|!|,|;|\n");
	private static final Set<String> RELS = new HashSet<>(Arrays.asList(">","<","!",":","="));

	private int saveNeures(Long creatorId, Long topicId, String relation, Map<String, Neure> nameToNeure) {
		String[] neures = splitPattern.split(relation);
		List<String> neureNames = new ArrayList<>(neures.length);
		for(String name:neures) {
			if(!name.trim().isEmpty()) {
				neureNames.add(name.trim());
			}
		}
		int cnt = 0;
		Cnd<Neure> cnd = new Cnd<>(Neure.class);
		cnd.in(neureNames.toArray(new String[0])).setName(Tpl.USING_S);
		cnd.and().eq().setCreatorId(creatorId);
		cnd.and().eq().setTopicId(topicId);
		cnd.setPagesize(neureNames.size());
		Page<Neure> exists = neureHolder.find(cnd);
		for(Neure n:exists.getData()) {
			nameToNeure.put(n.getName(), n);
		}
		List<Neure> news = new ArrayList<>();
		for(String name:neureNames) {
			if(!nameToNeure.containsKey(name)) {
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
	public int push(@Header("userId")Long creatorId,@Param("topicId")Long topicId,@Param("relation")String relation) {
		if(creatorId == null) {
			throw new RuntimeException("CreatorId cannot be null!");
		}
		relation = relation.replaceAll("！", "!")
				.replaceAll("，", ",")
				.replaceAll("；", ";")
				.replaceAll("：", ":")
				.replaceAll("＞", ">")
				.replaceAll("＜", "<");
		if(!relation.endsWith("\n")) {
			relation = relation + "\n";
		}
		Topic topic = topicHolder.get(topicId);
		if(topic == null) {
			Failed.throwError("topicId(%s)不存在", topicId);
		} else if(!topic.getOwnner().equals(creatorId)) {
			topic.setId(null);
			topic.setOwnner(creatorId);
			topic = topicHolder.insert(topic);
		}
		
		Map<String,Neure> nameToNeure = new HashMap<>();
		int cnt = saveNeures(creatorId,topic.getId(),relation, nameToNeure);
		if(!nameToNeure.isEmpty()) {
			Matcher matcher = splitPattern.matcher(relation);
			int last = 0;
			List<List<String>> left = new ArrayList<>();
			List<List<String>> right = new ArrayList<>();
			List<String> group = new ArrayList<>();
			List<List<String>> cur = left;
			String rel = null;
			List<NeureRelation> nrs = new ArrayList<>();
			while(matcher.find()) {
				String a = relation.substring(last, matcher.start()).trim();
				last = matcher.end();
				if(!a.isEmpty()){
					group.add(a);
				}
				String opt = relation.substring(matcher.start(), matcher.end());

				if(RELS.contains(opt)) {
					rel = opt;
					cur.add(group);
					cur = right;
					group = new ArrayList<>();
					cur.add(group);
				}
				if(opt.equals(",")) {
				} else if(opt.equals(";")) {
					cur.add(group);
					group =new ArrayList<>();
					cur.add(group);
				} else if(opt.equals("\n")) {
					if(rel == null) continue;
					if(rel.equals("<")) {
						cur = right;
						right = left;
						left = cur;
					}
					if(right.size() < 0 || right.get(0).size() < 0) {
						LOGGER.error(OUtils.toPrettyJson(right));
						continue;
					}
					String dname = right.get(0).get(0);
					Neure deduced = nameToNeure.get(dname);
					if(deduced == null) {
						LOGGER.error(OUtils.toPrettyJson(right));
						continue;
					}
					for (List<String> grp : left) {
						MD5Updator md5 = new MD5Updator();
						md5.update(deduced.getId());
						for (String name : grp) {
							Neure n = nameToNeure.get(name);
							if(n != null) {
								md5.update(n.getId());
							}
						}
						md5.update(rel);
						long comb = md5.toBigInteger().longValue()&(-1>>>1);
						for (String name : grp) {
							NeureRelation nr = new NeureRelation();
							Neure n = nameToNeure.get(name);
							nr.setDependId(n.getId());
							nr.setDeduceId(deduced.getId());
							nr.setComb(comb);
							nr.setRelation(rel);
							nr.setCode(nr.generateCode());
							nrs.add(nr);
						}
					}
					if(rel.equals("<")) {
						cur = right;
						right = left;
						left = cur;
					}
					left.clear();
					right.clear();
					group.clear();
					cur = left;
					rel = null;
				}
			}

			Map<Long,NeureRelation> codes = new HashMap<>(nrs.size());
			for(NeureRelation nr:nrs) {
				codes.put(nr.getCode(), nr);
			}
			Cnd<NeureRelation> rnCnd = new Cnd<>(NeureRelation.class);
			SelectTpl<NeureRelation> tpl = new SelectTpl<>(NeureRelation.class);
			tpl.using().setCode(Tpl.USING_LONG);
			rnCnd.and().in(codes.keySet().toArray(new Long[0])).setCode(Tpl.USING_LONG);
			rnCnd.setPagesize(Integer.MAX_VALUE);
			Page<NeureRelation> exist = neureRelationHolder.find(rnCnd);
			
			for(NeureRelation nr:exist.getData()) {
				codes.remove(nr.getCode());
			}
			List<NeureRelation> newNRS = new ArrayList<>(codes.values());
			cnt += neureRelationHolder.batchInsert(newNRS);
		}
		return cnt;
	}
	
	@Api("/searchDepend")
	public Map<String,Object> searchDepend(@Header("userId")Long creatorId,@Param("target")String target, @Param("topicIds")Long[] topicIds) {
		Integer maxStep = 5;
		Cnd<Neure> cn = new Cnd<>(Neure.class);
		if(target != null && !target.trim().isEmpty()) {
			cn.and().eq().setName(target);	
		}
		cn.and().in(topicIds).setTopicId(Tpl.USING_LONG);
		SelectTpl<Neure> tpl = new SelectTpl<Neure>(Neure.class);
		tpl.using().setId(Tpl.USING_LONG);
		Page<Neure> targetN = neureHolder.findTpl(tpl,cn);
		List<NeureRelation> relations = new ArrayList<>();
		List<Long> ids = new ArrayList<>();
		Set<Long> trace = new HashSet<>();
		Set<Long> neureIds = new HashSet<>();
		for(Neure n:targetN.getData()) {
			ids.add(n.getId());
		}
		neureIds.addAll(ids);
		while(!ids.isEmpty() && (--maxStep >= 0)) {
			Cnd<NeureRelation> cndNR = new Cnd<>(NeureRelation.class);
			Cnd<NeureRelation> cndOR = new Cnd<>(NeureRelation.class);
			cndNR.and().in(ids.toArray(new Long[0])).setDeduceId(Tpl.USING_LONG);
			cndOR.and().in(ids.toArray(new Long[0])).setDependId(Tpl.USING_LONG);
			cndOR.and().in(new String[] {"!","=",":"}).setRelation(Tpl.USING_S);
			cndNR.or(cndOR);
			cndNR.setPagesize(100);
			Page<NeureRelation> page = neureRelationHolder.find(cndNR);
			ids.clear();
			for(NeureRelation nr:page.getData()) {
				if(!trace.contains(nr.getId())) {
					trace.add(nr.getId());
					ids.add(nr.getDependId());
					relations.add(nr);
				}
			}
			neureIds.addAll(ids);
		}
		
		Cnd<Neure> cnd = new Cnd<>(Neure.class);
		cnd.setPagesize(10000);
		cnd.and().in(neureIds.toArray(new Long[0])).setId(Tpl.USING_LONG);
		Page<Neure> all = neureHolder.find(cnd);
		Map<String,Object> result = new HashMap<>();
		result.put("relations", relations);
		result.put("neures", all.getData());
		return result;
	}
	
	@Api("/searchDeduce")
	public Map<String,Object> searchDeduce(@Header("userId")Long creatorId,@Param("target") String target, @Param("dependIds")List<Long> dependIds, @Param("topicIds")Long topicIds) {
		Map<String,Object> result = new HashMap<>();
		Integer maxStep = 5;
		Cnd<Neure> cn = new Cnd<>(Neure.class);
		cn.and().eq().setName(target);
		Neure targetNeure = neureHolder.fetch(null, cn);
		List<NeureRelation> relations = new ArrayList<>();
		List<Long> ids = new ArrayList<>();
		Set<Long> trace = new HashSet<>();
		Set<Long> neureIds = new HashSet<>();
		ids.addAll(dependIds);
		neureIds.addAll(ids);
		boolean find = false;
		while((!ids.isEmpty() && (--maxStep >= 0)) && !find) {
			Cnd<NeureRelation> cndNR = new Cnd<>(NeureRelation.class);
			cndNR.and().in(ids.toArray(new Long[0])).setDependId(Tpl.USING_LONG);
			cndNR.setPagesize(100);
			Page<NeureRelation> page = neureRelationHolder.find(cndNR);
			ids.clear();
			for(NeureRelation nr:page.getData()) {
				if(!trace.contains(nr.getId())) {
					trace.add(nr.getId());
					ids.add(nr.getDeduceId());
					relations.add(nr);
					if(targetNeure != null && nr.getDeduceId().equals(targetNeure.getId())) {
						find = true;
						break;
					}
				}
			}
			neureIds.addAll(ids);
		}
		Cnd<Neure> cnd = new Cnd<>(Neure.class);
		cnd.setPagesize(10000);
		cnd.and().in(neureIds.toArray(new Long[0])).setId(Tpl.USING_LONG);
		Page<Neure> all = neureHolder.find(cnd);
		result.put("relations", relations);
		result.put("neures", all.getData());
		return result;
	}
	
	@Api("/deleteRelation")
	public int deleteRelations(@Header("userId")Long creatorId,@Param("ids")Long[] ids) {
		Cnd<NeureRelation> cnd = new Cnd<>(NeureRelation.class);
		cnd.and().in(ids).setId(Tpl.USING_LONG);
		neureRelationHolder.remove(cnd);
		return neureRelationHolder.delete(cnd);
	}
}
