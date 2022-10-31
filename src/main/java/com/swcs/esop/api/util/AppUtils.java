package com.swcs.esop.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;

/**
 * <h1>Spring 工具类</h1>
 * <p>
 * 有些地方不能通过注入的方式获取 bean，即通过 @Autowired 注解的方式。 此类提供了getBean()方法，传入一个 beanName
 * 获取对象实例。
 * </p>
 */
@Component
public class AppUtils implements ApplicationContextAware {
    private static Logger logger = LoggerFactory.getLogger(AppUtils.class);

    /**
     * spring 上下文对象
     */
    private static ApplicationContext applicationContext = null;

    private AppUtils() {
    }

    /**
     * 获取 spring 上下文对象
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 设置 spring 上下文对象
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppUtils.applicationContext = applicationContext;
    }

    /**
     * 根据对象名称，获取对象实例
     *
     * @param name 类型名称
     * @return 对象
     */
    public static Object getBean(String name) {
        try {
            return getApplicationContext().getBean(name);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return newInstance(name);
    }

    /**
     * 根据对象类型，获取对象实例
     *
     * @param clazz 类型
     * @return 对象
     */
    public static <T> T getBean(Class<T> clazz) {
        try {
            return getApplicationContext().getBean(clazz);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return newInstance(clazz);
    }

    /**
     * 根据类名生成一个实例，这个方法会利用 @Autowired 注解将属性值自动注入。
     *
     * @param className 类的完全限定名
     * @return 对象
     */
    public static Object newInstance(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return newInstance(clazz);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 根据类名生成一个实例，这个方法会利用 @Autowired 注解将属性值自动注入。
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            T bean = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                if (f.isAnnotationPresent(Autowired.class)) {
                    // 基于类型注入
                    Class<?> c = f.getType();
                    Object value = getBean(c);
                    // 允许访问private字段
                    f.setAccessible(true);
                    // 把引用对象注入属性
                    f.set(bean, value);
                } else if (f.isAnnotationPresent(Resource.class)) {
                    Resource resource = f.getAnnotation(Resource.class);
                    String name = resource.name();
                    Object value = getBean(name);
                    // 允许访问private字段
                    f.setAccessible(true);
                    // 把引用对象注入属性
                    f.set(bean, value);
                }
            }
            return bean;
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public static String getProperty(String key) {
        return getApplicationContext().getEnvironment().getProperty(key);
    }

    /**
     * 获取当前 request 对象
     *
     * 注意事项:
     * 1.在异步线程中会触发异常
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            return request;
        } catch (IllegalStateException e) {
            return null;
        }
    }

}
