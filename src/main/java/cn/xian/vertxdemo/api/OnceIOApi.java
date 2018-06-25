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
	/** 特殊字段 */
	public final static String TYPE = ":type";
	public final static String NULLABLE = ":nullable";
	public final static String PATTERN = ":pattern";
	public final static String VALREF = ":valRef";
	public final static String MODEL = "model";
	public final static String API = "api";
	public final static String SUBAPI = "subapi";
	public final static String STDAPI = "stdapi";
	public final static String NAME = "name";
	public final static String SOURCE = "source";
	public final static String HTTP_METHODS = "methods";
	public final static String BRIEF = "brief";
	public final static String RETURNTYPE = "returnType";
	public final static String METHODNAME = "methodName";
	public final static String PARAMS = "params";
	
	private Map<String, Object> model = new HashMap<>();
	private Map<String, Object> apis = new HashMap<>();
	
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
		apis.put(MODEL, model);
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
			methods.add(method);
		} 
		for(Map.Entry<Object, Set<Method>> entry:beanToMethods.entrySet()){
			Object bean = entry.getKey();
			Class<?> beanClass = bean.getClass();
			String name = beanClass.getName().replaceAll("\\$\\$.*$", "");
			@SuppressWarnings("unchecked")
			Map<String, Object> parent = (Map<String, Object>) apis.get(name);
			if (parent == null) {
				parent = new HashMap<>();
				parent.put(NAME, name);
				apis.put(name, parent);
				String prefix = "";
				Api parentApi = beanClass.getAnnotation(Api.class);
				AutoApi parentAutoApi = beanClass.getAnnotation(AutoApi.class);
				if (parentApi != null) {
					prefix = parentApi.value();
					parent.put(BRIEF, parentApi.brief());
				} else if (parentAutoApi != null) {
					prefix = "/"+parentAutoApi.value().getSimpleName().toLowerCase();
					parent.put(BRIEF, parentAutoApi.brief());
				}
				parent.put(API, prefix);
			}
			for(Method method : entry.getValue()) {
				Map<String, Object> content = new HashMap<>();
				Api apiAnno = method.getAnnotation(Api.class);
				if (apiAnno == null) {
					continue;
				}
				content.put(METHODNAME, method.getName());
				Map<String, Object> params = resoveParams(bean, method);
				content.put(PARAMS, params);
				Map<String, Object> returnType = resovleType(bean, method);
				content.put(RETURNTYPE, returnType);

				List<String> methodNames = new ArrayList<>();
				for (ApiMethod am : apiAnno.method()) {
					methodNames.add(am.name());
				}
				content.put(HTTP_METHODS, methodNames);
				content.put(BRIEF, apiAnno.brief());

				if(method.getDeclaringClass().equals(DaoHolder.class)) {
					content.put(STDAPI, true);
				}
				if(!apiAnno.value().equals("")) {
					content.put(API, apiAnno.value());
				}else {
					content.put(API, "/"+method.getName());
				}
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> subapi = (List<Map<String, Object>>) parent.get(SUBAPI);
				if (subapi == null) {
					subapi = new ArrayList<>();
					parent.put(SUBAPI, subapi);
				}
				subapi.add(content);
			}

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
			resoveClass(bean, params, TYPE, genType, method.getGenericReturnType());
		} else {
			resoveClass(bean, params, TYPE, method.getReturnType(), method.getGenericReturnType());
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
					resoveClass(bean, params, TYPE, paramType, genericType);
				} else {
					params.put(pname, paramInfo);
					paramInfo.put(SOURCE, psrc);
					resovleValidator(paramInfo,pname,validate,null);
					resoveClass(bean, paramInfo, TYPE, paramType, genericType);
				}
			} else {
				params.put(param.getName(), paramInfo);
			}
		}
		return params;
	}
	
	private void resovleValidator(Map<String,Object> result,String name, Validate validate,Col col) {
		if (col != null && validate == null) {
			if (col.nullable() == false) {
				result.put(name + NULLABLE, col.nullable());
			}
			if (!col.pattern().equals("")) {
				result.put(name + PATTERN, col.pattern());
			}
			if (!col.valRef().equals(void.class)) {
				result.put(name + VALREF, col.valRef().getName());
			}
		}
		if (validate != null) {
			if (validate.nullable() == false) {
				result.put(name + NULLABLE, validate.nullable());
			}
			if (!validate.pattern().equals("")) {
				result.put(name + PATTERN, validate.pattern());
			}
			if (!validate.valRef().equals(void.class)) {
				result.put(name + VALREF, validate.valRef().getName());
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
		if(model.isEmpty()) {
			init();
		}
		return apis;
	}
}
