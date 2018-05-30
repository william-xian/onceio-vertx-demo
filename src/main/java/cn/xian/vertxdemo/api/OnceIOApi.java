package cn.xian.vertxdemo.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.xian.vertxdemo.holder.UserinfoHolder;
import cn.xian.vertxdemo.model.entity.Userinfo;
import top.onceio.core.beans.ApiMethod;
import top.onceio.core.beans.ApiPair;
import top.onceio.core.beans.BeansEden;
import top.onceio.core.mvc.annocations.Api;
import top.onceio.core.mvc.annocations.Attr;
import top.onceio.core.mvc.annocations.AutoApi;
import top.onceio.core.mvc.annocations.Cookie;
import top.onceio.core.mvc.annocations.Header;
import top.onceio.core.mvc.annocations.Param;
import top.onceio.core.util.OReflectUtil;
import top.onceio.core.util.OUtils;

@Api("onceio/")
public class OnceIOApi {

	@Api(value="apis")
	public Map<String,Object> apis(){
		Map<String,Object> apis = new HashMap<String,Object>();
		Map<String, ApiPair> api= BeansEden.get().getApiResover().getPatternToApi();
		Map<Object,Set<Method>> beanToMethods = new HashMap<>();
		for(Map.Entry<String, ApiPair> entry:api.entrySet()) {
			Method method = entry.getValue().getMethod();
			Object bean = entry.getValue().getBean();
			Set<Method> methods = beanToMethods.get(bean);
			if(methods == null) {
				methods = new HashSet<>();
				beanToMethods.put(bean, methods);
			}
			if(methods.contains(method)) {
				continue;
			}
			methods.add(method);
			Map<String,Object> content= new HashMap<>();
			ApiPair ap = entry.getValue();
			Api apiAnno = method.getAnnotation(Api.class);
			if(apiAnno == null) {
				continue;
			}
			content.put("methodName", method.getName());
			Map<String,Object> params = resoveParams(method);
			content.put("params", params);
			Map<String,Object> returnType = resovleType(method);
			content.put("returnType", returnType);
			
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

	private Map<String,Object> resovleType(Method method) {
		Map<String,Object> params = new HashMap<>();
		resoveClass(params,"",method.getReturnType());
		return params;
	}
	
	private Map<String,Object> resoveParams(Method method) {
		Map<String,Object> params = new HashMap<>();
		for(int i= 0; i < method.getParameterCount(); i++) {
			Parameter param = method.getParameters()[i];
			Class<?> paramType = method.getParameterTypes()[i];
			Map<String,Object> paramInfo = new HashMap<>();
			String pname = null;
			String psrc = null;
			do {
				Param pAnno = param.getAnnotation(Param.class);
				if(pAnno != null) {
					pname = pAnno.value();
					psrc = "Param";
					break;
				}
				Header hAnno = param.getAnnotation(Header.class);

				if(hAnno != null) {
					pname = hAnno.value();
					psrc = "Header";
					break;
				}
				Cookie cAnno = param.getAnnotation(Cookie.class);

				if(cAnno != null) {
					pname = cAnno.value();
					psrc = "Cookie";
					break;
				}
				Attr aAnno = param.getAnnotation(Attr.class);
				if(aAnno != null) {
					pname = aAnno.value();
					psrc = "Attr";
					break;
				}
			}while(false);
			
			if(pname != null) {
				if(pname.equals("")) {
					resoveClass(params,"",paramType);
				}else {
					params.put(pname, paramInfo);
					paramInfo.put("source", psrc);
					resoveClass(paramInfo,"",paramType);
				}
			}else {
				params.put(param.getName(), paramInfo);
			}
		}
		return params;
	}
	
	public void resoveClass(Map<String,Object> result,String name,Class<?> type) {
		if(OReflectUtil.isBaseType(type)) {
			result.put(name, type.getName());
		}else {
			Map<String,Object> subType;
			if(!name.equals("")) {
				subType = new HashMap<>();
				result.put(name, subType);
				result.put("type", type.getName());
			}else {
				subType = result;
			}
			
			for (Field field : type.getDeclaredFields()) {
				resoveClass(subType, field.getName(), field.getType());
			}
		}
	}
	
	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		Map<String,Object> result = new HashMap<>();
		OnceIOApi api = new OnceIOApi();
		result = api.resoveParams(UserinfoHolder.class.getMethod("findByUserinfo", Userinfo.class));
		System.out.println(OUtils.toPrettyJson(result));
	}
}
