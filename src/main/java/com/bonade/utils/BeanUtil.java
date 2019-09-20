package com.bonade.utils;

import com.bonade.vo.BeanInfo;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-19 09:38]
 * @Description: [ ]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
public class BeanUtil {
    /**
     * 复制列表对象属性
     *
     * @param sources  转换对象源列表
     * @param target   目标类
     * @param excludes 过滤字段
     * @return List<TARGET>目标对象列表
     * @throws RuntimeException 反射复制属性类异常,时间格式转换异常
     */
    public static <TARGET, SOURCE> List<TARGET> copyProperties(Collection<SOURCE> sources, Class target, String... excludes) {
        if (null != sources && sources.size() > 0) {
            try {
                Object o_source = sources.iterator().next();
                Object o_target = target.newInstance();
                BeanInfo beanInfo = getBeanInfo(o_source, o_target);
                beanInfo.setExcludes(excludes);
                return copyList(sources, beanInfo);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return new ArrayList<>(0);


    }

    /**
     * @param source   源对象
     * @param target   目标类
     * @param <TARGET> 目标对象
     * @param excludes 过滤属性
     * @return 目标对象
     * @throws RuntimeException 反射复制属性类异常,时间格式转换异常
     */
    public static <TARGET, SOURCE> TARGET copyProperties(SOURCE source, Class target, String... excludes) {
        if (null != source) {
            try {
                Object o_target = target.newInstance();
                BeanInfo beanInfo = getBeanInfo(source, o_target);
                beanInfo.setExcludes(excludes);
                o_target = handlerCopyFields(beanInfo);
                return (TARGET) o_target;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return null;

    }


    /**
     * 对象属性复制
     *
     * @param source   源对象
     * @param target   目标对象
     * @param excludes 过滤字段
     * @throws RuntimeException 反射复制属性类异常,时间格式转换异常
     */
    public static void copyProperties(Object source, Object target, String... excludes) {
        try {
            if(null!=source){
                BeanInfo beanInfo = getBeanInfo(source, target);
                beanInfo.setExcludes(excludes);
                handlerCopyFields(beanInfo);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private static <TARGET, SOURCE> List<TARGET> copyList(Collection<SOURCE> sources, BeanInfo beanInfo) throws Exception {
        List<TARGET> targets = new ArrayList(sources.size());
        for (SOURCE source : sources) {
            Object target = beanInfo.getTargetClass().newInstance();
            beanInfo.setSource(source);
            beanInfo.setTarget(target);
            target = handlerCopyFields(beanInfo);
            targets.add((TARGET) target);
        }
        return targets;
    }


    /**
     * 处理反射类及复制属性
     *
     * @throws Exception
     */
    private static Object handlerCopyFields(BeanInfo beanInfo) throws Exception {
        String[] excludes = beanInfo.getExcludes();
        String[] includes = beanInfo.getIncludes();
        Object source = beanInfo.getSource();
        Object target = beanInfo.getTarget();
        List<Field> s_fields = beanInfo.getSourceFields(); //源类属性列表
        List<Field> t_fields = beanInfo.getTargetFields();//目标类属性列表
        for (Field t_field : t_fields) {
            if (null != excludes) {
                boolean has_ex = excludeField(excludes, t_field);
                if (has_ex) {
                    continue;
                }
            }
            if (null != includes) {
                boolean is_in = includeField(includes, t_field);
                if (!is_in) {
                    continue;
                }
            }
            for (Field s_field : s_fields) {
                if (t_field.getName().equals(s_field.getName()) && t_field.getType() == s_field.getType()) { //同名属性
                    t_field.setAccessible(true);
                    s_field.setAccessible(true);
                    Object s_val = s_field.get(source);
                    if (null == s_val) {
                        break;
                    }
                    if ("String".equals(s_val.getClass().getSimpleName())) {
                        if (StringUtils.isBlank(s_val.toString())) {
                            break;
                        }
                    }

                    t_field.set(target, s_val);
                    break;
                }
            }
        }
        return target;

    }

    private static boolean excludeField(String[] excludes, Field field) {
        boolean has_ex = false;
        for (String exclude : excludes) {
            if (exclude.equals(field.getName())) {
                has_ex = true;
                break;
            }
        }
        return has_ex;
    }

    private static boolean includeField(String[] includes, Field field) {
        for (String include : includes) {
            if (include.equals(field.getName())) { //过滤除id外的所有属性
                return true;
            }

        }
        return false;
    }


    private static BeanInfo getBeanInfo(Object source, Object target) {
        BeanInfo beanInfo = new BeanInfo(source, target);
        Class s_clazz = source.getClass();
        Class t_clazz = target.getClass();
        List<Field> s_fields = getFields(s_clazz); //源类属性列表
        List<Field> t_fields = getFields(t_clazz);//目标类属性列表
        beanInfo.setTargetFields(t_fields);
        beanInfo.setSourceFields(s_fields);
        return beanInfo;
    }

    private static List<Field> getFields(Class clazz) {
        List<Field> fields = new ArrayList<>();
        while (null != clazz) { //数据源类所有属性（包括父类）
            fields.addAll(Arrays.asList(clazz.getDeclaredFields())); //源对象属性
            clazz = clazz.getSuperclass();
            if (Object.class.equals(clazz) || null == clazz) {
                break;
            }
        }
        return fields;
    }

}
