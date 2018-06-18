package cn.xian.vertxdemo.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import top.onceio.core.annotation.OnCreate;
import top.onceio.core.annotation.Validate;
import top.onceio.core.beans.ApiMethod;
import top.onceio.core.beans.ApiPair;
import top.onceio.core.beans.BeansEden;
import top.onceio.core.db.annotation.Col;
import top.onceio.core.db.dao.DaoHolder;
import top.onceio.core.mvc.annocations.Api;
import top.onceio.core.mvc.annocations.Attr;
import top.onceio.core.mvc.annocations.AutoApi;
import top.onceio.core.mvc.annocations.Cookie;
import top.onceio.core.mvc.annocations.Header;
import top.onceio.core.mvc.annocations.Param;
import top.onceio.core.util.OReflectUtil;

@Api("/onceio")
public class OnceIOApi {
	private Map<String, Object> model = new HashMap<>();
	private Map<String, Object> apis = new HashMap<>();
	
	@OnCreate
	public void init() {
		model.put(Object.class.getName(), Object.class.getName());
		model.put(Class.class.getName(), Class.class.getName());
		model.put(Type.class.getName(), Type.class.getName());
		model.put(String.class.getName(), String.class.getName());
		model.put(Long.class.getName(), Long.class.getName());
		model.put(Integer.class.getName(), Integer.class.getName());
		model.put(Short.class.getName(), Short.class.getName());
		model.put(Double.class.getName(), Double.class.getName());
		model.put(Float.class.getName(), Float.class.getName());
		model.put(Boolean.class.getName(), Boolean.class.getName());
		model.put(Byte.class.getName(), Byte.class.getName());
		model.put(Character.class.getName(), Character.class.getName());
		model.put(Void.class.getName(), Void.class.getName());
		model.put(void.class.getName(), void.class.getName());
		model.put(long.class.getName(), long.class.getName());
		model.put(int.class.getName(), int.class.getName());
		model.put(short.class.getName(), short.class.getName());
		model.put(double.class.getName(), double.class.getName());
		model.put(float.class.getName(), float.class.getName());
		model.put(boolean.class.getName(), boolean.class.getName());
		model.put(byte.class.getName(), byte.class.getName());
		model.put(char.class.getName(), char.class.getName());
		model.put(Date.class.getName(), Date.class.getName());
		model.put(List.class.getName(), List.class.getName());
		model.put(Set.class.getName(), Set.class.getName());
		model.put(Map.class.getName(), Map.class.getName());
		model.put(Collection.class.getName(), Collection.class.getName());
		genericApis();
	}

	private void genericApis() {
		apis.clear();
		apis.put("model", model);
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
			Map<String, Object> params = resoveParams(bean, method);
			content.put("params", params);
			Map<String, Object> returnType = resovleType(bean, method);
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
				prefix = "/"+parentAutoApi.value().getSimpleName().toLowerCase();
				parent.put("brief", parentAutoApi.brief());
			}
			if(!apiAnno.value().equals("")) {
				content.put("api", apiAnno.value());
			}else {
				content.put("api", "/"+method.getName());
			}
			parent.put("api", prefix);
			
			
			@SuppressWarnings("unchecked")
			Map<String, Object> root = (Map<String, Object>) apis.get(parent.get("name"));
			if (root == null) {
				apis.put((String) parent.get("name"), parent);
				root = parent;
			}
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> subapi = (List<Map<String, Object>>) root.get("subapi");
			if (subapi == null) {
				subapi = new ArrayList<>();
				root.put("subapi", subapi);
			}
			subapi.add(content);
		}

	}

	private Map<String, Object> resovleType(Object bean, Method method) {
		Class<?> genType = null;
		Type t = null;
		if (DaoHolder.class.isAssignableFrom(bean.getClass())) {
			t = DaoHolder.class.getTypeParameters()[0];
			genType = OReflectUtil.searchGenType(DaoHolder.class, bean.getClass(), t);
		}
		Map<String, Object> params = new HashMap<>();
		if (method.getGenericReturnType().equals(t)) {
			resoveClass(bean, params, ":type", genType, method.getGenericReturnType());
		} else {
			resoveClass(bean, params, ":type", method.getReturnType(), method.getGenericReturnType());
		}
		return params;
	}

	private Map<String, Object> resoveParams(Object bean, Method method) {
		Map<String, Object> params = new HashMap<>();
		for (int i = 0; i < method.getParameterCount(); i++) {
			Parameter param = method.getParameters()[i];
			Validate validate = param.getAnnotation(Validate.class);
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
					resoveClass(bean, params, ":type", paramType, genericType);
				} else {
					params.put(pname, paramInfo);
					paramInfo.put("source", psrc);
					resovleValidator(paramInfo,pname,validate,null);
					resoveClass(bean, paramInfo, ":type", paramType, genericType);
				}
			} else {
				params.put(param.getName(), paramInfo);
			}
		}
		return params;
	}
	
	private void resovleValidator(Map<String,Object> result,String name, Validate validate,Col col) {
		if(col != null && validate == null) {
			if(col.nullable() == false) {
				result.put(name+":nullable", col.nullable());
			}
			if(!col.pattern().equals("")) { 
				result.put(name+":pattern", col.pattern());	
			}
			if(!col.valRef().equals(void.class)) {
				result.put(name+":valRef", col.valRef().getName());
			}
		}
		if(validate != null) {
			if(validate.nullable() == false) {
				result.put(name+":nullable", validate.nullable());
			}
			if(!validate.pattern().equals("")) { 
				result.put(name+":pattern", validate.pattern());	
			}
			if(!validate.valRef().equals(void.class)) {
				result.put(name+":valRef", validate.valRef().getName());
			}
		}
	}

	public void resoveClass(Object bean, Map<String, Object> result, String name, Class<?> type, Type genericType) {
		Map<String, Object> subType;
		if (!name.equals("")) {
			subType = new HashMap<>();
			result.put(name, subType);
		} else {
			subType = result;
		}
		if (!type.equals(genericType) && bean != null && DaoHolder.class.isAssignableFrom(bean.getClass())) {
			Type t = DaoHolder.class.getTypeParameters()[0];
			Class<?> genType = OReflectUtil.searchGenType(DaoHolder.class, bean.getClass(), t);
			if (genericType.getTypeName().equals("T")) {
				result.put(name, genType.getName());
			} else {
				result.put(name, genericType.getTypeName().replace("<T>", "<" + genType.getName() + ">"));
			}
			resoveModel(genType.getTypeName(), genType);
			resoveModel(genericType.getTypeName(), type);
		} else {
			result.put(name, genericType.getTypeName());
			resoveModel(genericType.getTypeName(), type);
		}
	}

	public void resoveModel(String name, Class<?> type) {
		if (model.containsKey(name)) {
			return;
		}
		if (type.getName().startsWith("java")) {
			model.put(name, type.getName());
		} else {
			Map<String, Object> result = new HashMap<>();
			model.put(name, result);
			for (Class<?> clazz = type; clazz != null
					&& !OReflectUtil.isBaseType(clazz); clazz = clazz.getSuperclass()) {
				for (Field field : clazz.getDeclaredFields()) {
					if (Modifier.isStatic(field.getModifiers())) {
						continue;
					}
					result.put(field.getName(), field.getGenericType().getTypeName());
					resoveModel(field.getGenericType().getTypeName(), field.getType());
					Validate validate = field.getAnnotation(Validate.class);
					Col col = field.getAnnotation(Col.class);
					resovleValidator(result,field.getName(),validate,col);
				}
			}
		}
	}

	@Api(value = "/apis")
	public Map<String, Object> apis() {
		return apis;
	}
}
