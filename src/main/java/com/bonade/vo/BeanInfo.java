package com.bonade.vo;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-19 09:41]
 * @Description: [ ]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@Data
public class BeanInfo {
    public BeanInfo(Object source, Object target) {
        this.source = source;
        this.target = target;
    }

    /**
     * 目标源对象
     */
    private Object source;
    /**
     * 目标对象
     */
    private Object target;

    /**
     * 过滤属性
     */
    private String[] excludes;

    /**
     * 只查属性
     */
    private String[] includes;
    /**
     * 目标类属性
     */
    private List<Field> sourceFields;
    /**
     * 源类属性
     */
    private List<Field> targetFields;

    public Class getTargetClass() {
        return this.getTarget().getClass();
    }

}
