package cn.com.twoke.develop.codetemplate.template.methods;

import cn.com.twoke.develop.codetemplate.template.annotation.TemplateMethod;
import cn.com.twoke.develop.codetemplate.template.methods.impl.MapperMethod;
import cn.com.twoke.develop.codetemplate.template.methods.impl.ServiceMethod;
import cn.com.twoke.develop.codetemplate.template.methods.impl.TableFieldMethod;
import cn.com.twoke.develop.codetemplate.template.methods.impl.TableMethod;
import cn.hutool.core.util.ReflectUtil;
import freemarker.template.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TemplateMethodHandler {

    public static Map<String, TemplateMethodModelEx> templateMethods = new HashMap<>();

    static {
        register(new TableFieldMethod());
        register(new TableMethod());
        register(new MapperMethod());
        register(new ServiceMethod());
    }

    public static void register(Object bean) {
        Method[] methods = ReflectUtil.getMethods(bean.getClass());
        for (Method method : methods) {
            TemplateMethod templateMethod = AnnotationUtils.getAnnotation(method, TemplateMethod.class);
            if (null == templateMethod) continue;
            String methodName =  getMethodName(templateMethod.name(), method.getName());
            if (templateMethods.containsKey(methodName)) {
                throw new RuntimeException("模板方法名称重复：" + methodName);
            }
            // 使用jdk代理，使用method代理TemplateMethodModelEx
            TemplateMethodModelEx proxy = (TemplateMethodModelEx) Proxy.newProxyInstance(
                    bean.getClass().getClassLoader(),
                    new Class[]{TemplateMethodModelEx.class},
                   new TemplateMethodModelExInvocationHandler(methodName, bean, method)
            );
            templateMethods.put(methodName, proxy);
        }
    }

    public static void withMethods(Map<String, Object> dataModel) {
        if (null == dataModel) throw new RuntimeException("dataModel不能为空");
        dataModel.putAll(templateMethods);
    }



    private static String getMethodName(String name, String defaultValue) {
        return StringUtils.isBlank(name) ? defaultValue : name;
    }


    @AllArgsConstructor
    static class TemplateMethodModelExInvocationHandler implements InvocationHandler  {

        private final String methodName;
        private final Object proxyTarget;
        private final Method proxyMethod;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("exec") && args.length == 1 && args[0] instanceof List) {
                // 解析参数
                List<TemplateModel> templateParams = (List<TemplateModel>) args[0];
                // 转换参数类型为Java类型
                Object[] params = new Object[proxyMethod.getParameterCount()];
                Parameter[] parameters = proxyMethod.getParameters();

                for (int i = 0; i < params.length; i++) {
                    Class<?> parameterType = parameters[i].getType();
                    if (i >= templateParams.size()) {
                        Nullable annotation = parameters[i].getAnnotation(Nullable.class);
                        if (null == annotation) {
                            log.error("{}: {} 不能为空", methodName, parameters[i].getName());
                            throw new RuntimeException("参数不能为空");
                        }
                        params[i] = null;
                        continue;
                    }
                    TemplateModel templateModel = templateParams.get(i);
                    if (templateModel instanceof SimpleScalar) { // 解析字符串
                        if (String.class.isAssignableFrom(parameterType)) {
                            params[i] = ((SimpleScalar) templateModel).getAsString();
                        } else {
                            log.error("不支持参数格式 {}", parameterType);
                            throw new RuntimeException("不支持参数格式");
                        }
                    } else if (templateModel instanceof SimpleNumber) { // 解析数字
                        if (Integer.class.isAssignableFrom(parameterType)) {
                            params[i] = ((SimpleNumber) templateModel).getAsNumber().intValue();
                        } else if (Long.class.isAssignableFrom(parameterType)) {
                            params[i] = ((SimpleNumber) templateModel).getAsNumber().longValue();
                        } else if (Double.class.isAssignableFrom(parameterType)) {
                            params[i] = ((SimpleNumber) templateModel).getAsNumber().doubleValue();
                        } else if (Float.class.isAssignableFrom(parameterType)) {
                            params[i] = ((SimpleNumber) templateModel).getAsNumber().floatValue();
                        } else if (Short.class.isAssignableFrom(parameterType)) {
                            params[i] = ((SimpleNumber) templateModel).getAsNumber().shortValue();
                        } else if (Byte.class.isAssignableFrom(parameterType)) {
                            params[i] = ((SimpleNumber) templateModel).getAsNumber().byteValue();
                        } else if (BigDecimal.class.isAssignableFrom(parameterType)) {
                            params[i] = BigDecimal.valueOf(((SimpleNumber) templateModel).getAsNumber().doubleValue());
                        } else {
                            log.error("不支持参数格式 {}", parameterType);
                            throw new RuntimeException("不支持参数格式");
                        }
                    } else if (templateModel instanceof SimpleHash) {
                        if (Map.class.isAssignableFrom(parameterType)) {
                            params[i] = ((SimpleHash) templateModel).toMap();
                        } else {
                            log.error("不支持参数格式 {}", parameterType);
                            throw new RuntimeException("不支持参数格式");
                        }
                    } else if (templateModel instanceof SimpleSequence) {
                        if (List.class.isAssignableFrom(parameterType)) {
                            params[i] = ((SimpleSequence) templateModel).toList();
                        } else {
                            log.error("不支持参数格式 {}", parameterType);
                            throw new RuntimeException("不支持参数格式");
                        }
                    }  else if (templateModel instanceof TemplateBooleanModel) {
                        if (Boolean.class.isAssignableFrom(parameterType)) {
                            params[i] = ((TemplateBooleanModel) templateModel).getAsBoolean();
                        } else {
                            log.error("不支持参数格式 {}", parameterType);
                            throw new RuntimeException("不支持参数格式");
                        }
                    }
                }

                Object result = proxyMethod.invoke(proxyTarget, params);
                if (result instanceof TemplateModel) {
                    return result;
                } else if (result instanceof Boolean) {
                    if (((Boolean)result)) {
                        return TemplateBooleanModel.TRUE;
                    }
                    return TemplateBooleanModel.FALSE;
                } else if (result instanceof String) {
                    return SimpleScalar.newInstanceOrNull(String.valueOf(result));
                } else if (result instanceof Map) {
                    return new SimpleHash() {{
                        putAll((Map) result);
                    }};
                } else if (result instanceof List) {
                    SimpleSequence simpleSequence = new SimpleSequence();
                    ((List) result).forEach(simpleSequence::add);
                    return  simpleSequence;
                } else {
                    return new SimpleScalar(String.valueOf(result));
                }
            }
            // 默认返回空字符串
            return SimpleScalar.newInstanceOrNull("");
        }
    }
}
