package cn.xian.vertxdemo.api;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import top.onceio.core.beans.ApiMethod;
import top.onceio.core.beans.ApiPair;
import top.onceio.core.beans.BeansEden;
import top.onceio.core.mvc.annocations.Api;
import top.onceio.core.mvc.annocations.AutoApi;

@Api("onceio/")
public class OnceIOApi {

	@Api(value="apis")
	public Map<String,Object> apis(){
		Map<String,Object> apis = new HashMap<String,Object>();
		Map<String, ApiPair> api= BeansEden.get().getApiResover().getPatternToApi();
		Set<Method> methods = new HashSet<Method>();
		for(Map.Entry<String, ApiPair> entry:api.entrySet()) {
			if(methods.contains(entry.getValue().getMethod())) {
				continue;
			}
			methods.add(entry.getValue().getMethod());
			Map<String,Object> content= new HashMap<>();
			ApiPair ap = entry.getValue();
			Api apiAnno = ap.getMethod().getAnnotation(Api.class);
			List<String> methodNames = new ArrayList<>();
			for(ApiMethod am:apiAnno.method()) {
				methodNames.add(am.name());
			}
			content.put("methods",methodNames);
			content.put("brief",apiAnno.brief());
			Api parentApi = ap.getBean().getClass().getAnnotation(Api.class);
			AutoApi parentAutoApi = ap.getBean().getClass().getAnnotation(AutoApi.class);
			String prefix="";
			Map<String,Object> parent = new HashMap<>();
			parent.put("name", ap.getBean().getClass().getName().replaceAll("\\$\\$.*$", ""));
			if(parentApi != null) {
				prefix = parentApi.value();
				parent.put("brief", parentApi.brief());
			}else if(parentAutoApi != null) {
				prefix = parentAutoApi.value().getSimpleName().toLowerCase();
				parent.put("brief", parentAutoApi.brief());
			}else {
				content.put("parent", ap.getBean().getClass().getSimpleName());
			}
			content.put("api",prefix+apiAnno.value());
			parent.put("prefix", prefix);
			@SuppressWarnings("unchecked")
			Map<String,Object> root = (Map<String,Object>) apis.get(parent.get("name"));
			if(root != null) {
				@SuppressWarnings("unchecked")
				List<Map<String,Object>> subapi = (List<Map<String,Object>>)root.get("subapi");
				if(subapi == null) {
					subapi = new ArrayList<>();
					root.put("subapi", subapi);
				}
				subapi.add(content);
			} else {
				apis.put((String)parent.get("name"), parent);
			}
		}
		return apis;
	}
	
}
