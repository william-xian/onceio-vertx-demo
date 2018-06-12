package cn.xian.vertxdemo.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import top.onceio.core.beans.ApiMethod;
import top.onceio.core.beans.ApiPair;
import top.onceio.core.beans.BeansEden;
import top.onceio.core.db.dao.DaoHolder;
import top.onceio.core.mvc.annocations.Api;
import top.onceio.core.mvc.annocations.Attr;
import top.onceio.core.mvc.annocations.AutoApi;
import top.onceio.core.mvc.annocations.Cookie;
import top.onceio.core.mvc.annocations.Header;
import top.onceio.core.mvc.annocations.Param;
import top.onceio.core.util.OReflectUtil;

@Api("onceio/")
public class OnceIOApi {

	@Api(value = "apis")
	public Map<String, Object> apis() {
		Map<String, Object> apis = new HashMap<String, Object>();
		Map<String, ApiPair> api = BeansEden.get().getApiResover().getPatternToApi();
		Map<Object, Set<Method>> beanToMethods = new HashMap<>();
		for (Map.Entry<String, ApiPair> entry : api.entrySet()) {
			Method method = entry.getValue().getMethod();
			Object bean = entry.getValue().getBean();
			Set<Method> methods = beanToMethods.get(bean);
			if (methods == null) {
				methods = new HashSet<>();
				beanToMethods.put(bean, methods);
			}
			if (methods.contains(method)) {
				continue;
			}
			methods.add(method);
			Map<String, Object> content = new HashMap<>();
			ApiPair ap = entry.getValue();
			Api apiAnno = method.getAnnotation(Api.class);
			if (apiAnno == null) {
				continue;
			}
			content.put("methodName", method.getName());
			Class<?> genType = null;
			Type t = null;
			if(bean.getClass().isAssignableFrom(DaoHolder.class)) {
				t = DaoHolder.class.getTypeParameters()[0];
				genType = OReflectUtil.searchGenType(DaoHolder.class, bean.getClass(), t);
			}
			Map<String, Object> params = resoveParams(method,t,genType);
			content.put("params", params);
			Map<String, Object> returnType = resovleType(method,t,genType);
			content.put("returnType", returnType);

			List<String> methodNames = new ArrayList<>();
			for (ApiMethod am : apiAnno.method()) {
				methodNames.add(am.name());
			}
			content.put("methods", methodNames);
			content.put("brief", apiAnno.brief());
			Api parentApi = ap.getBean().getClass().getAnnotation(Api.class);
			AutoApi parentAutoApi = ap.getBean().getClass().getAnnotation(AutoApi.class);
			String prefix = "";
			Map<String, Object> parent = new HashMap<>();
			parent.put("name", ap.getBean().getClass().getName().replaceAll("\\$\\$.*$", ""));
			if (parentApi != null) {
				prefix = parentApi.value();
				parent.put("brief", parentApi.brief());
			} else if (parentAutoApi != null) {
				prefix = parentAutoApi.value().getSimpleName().toLowerCase();
				parent.put("brief", parentAutoApi.brief());
			} else {
				content.put("parent", ap.getBean().getClass().getSimpleName());
			}
			content.put("api", prefix + apiAnno.value());
			parent.put("prefix", prefix);
			@SuppressWarnings("unchecked")
			Map<String, Object> root = (Map<String, Object>) apis.get(parent.get("name"));
			if (root != null) {
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> subapi = (List<Map<String, Object>>) root.get("subapi");
				if (subapi == null) {
					subapi = new ArrayList<>();
					root.put("subapi", subapi);
				}
				subapi.add(content);
			} else {
				apis.put((String) parent.get("name"), parent);
			}
		}
		return apis;
	}

	private Map<String, Object> resovleType(Method method, Type t, Class<?> genType) {
		Map<String, Object> params = new HashMap<>();
		if(method.getGenericReturnType().equals(t)) {
			resoveClass(params, method.getReturnType().getName(), genType,method.getGenericReturnType());
		} else {
			resoveClass(params, "", method.getReturnType(),method.getGenericReturnType());	
		}
		return params;
	}

	private Map<String, Object> resoveParams(Method method, Type t, Class<?> genType) {
		Map<String, Object> params = new HashMap<>();
		for (int i = 0; i < method.getParameterCount(); i++) {
			Parameter param = method.getParameters()[i];
			Class<?> paramType = method.getParameterTypes()[i];
			Type genericType = method.getGenericParameterTypes()[i];
			Map<String, Object> paramInfo = new HashMap<>();
			String pname = null;
			String psrc = null;
			do {
				Param pAnno = param.getAnnotation(Param.class);
				if (pAnno != null) {
					pname = pAnno.value();
					psrc = "Param";
					break;
				}
				Header hAnno = param.getAnnotation(Header.class);

				if (hAnno != null) {
					pname = hAnno.value();
					psrc = "Header";
					break;
				}
				Cookie cAnno = param.getAnnotation(Cookie.class);

				if (cAnno != null) {
					pname = cAnno.value();
					psrc = "Cookie";
					break;
				}
				Attr aAnno = param.getAnnotation(Attr.class);
				if (aAnno != null) {
					pname = aAnno.value();
					psrc = "Attr";
					break;
				}
			} while (false);

			if (pname != null) {
				if (pname.equals("")) {
					if(t != null && t.equals(paramType)) {
						resoveClass(params, "", genType,genericType);
					}else {
						resoveClass(params, "", paramType,genericType);	
					}
				} else {
					params.put(pname, paramInfo);
					paramInfo.put("source", psrc);

					if(t != null && t.equals(paramType)) {
						resoveClass(paramInfo, "", genType,genericType);
					}else {
						resoveClass(paramInfo, "", paramType,genericType);
					}
				}
			} else {
				params.put(param.getName(), paramInfo);
			}
		}
		return params;
	}
	public void resoveClass(Map<String, Object> result, String name, Class<?> type,Type genericType) {
		if (OReflectUtil.isBaseType(type) || type.isInterface() || type.getName().startsWith("java")) {
			result.put(name, genericType.getTypeName());
		} else {
			Map<String, Object> subType;
			if (!name.equals("")) {
				subType = new HashMap<>();
				result.put(name, subType);
				result.put("type", type.getName());
			} else {
				subType = result;
			}
			for (Class<?> clazz = type; clazz != null && !OReflectUtil.isBaseType(clazz); clazz = clazz.getSuperclass()) {
				for (Field field : clazz.getDeclaredFields()) {
					if(Modifier.isStatic(field.getModifiers())) {
						continue;
					} 
					resoveClass(subType, field.getName(), field.getType(),field.getGenericType());
				}
			}

		}
	}

}
