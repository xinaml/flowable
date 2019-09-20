package com.bonade.vo;

import lombok.Data;


/**
 * @Author: [liguiqin]
 * @Date: [2019-09-17 10:52]
 * @Description: [ 流程定义]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@Data
public class ProcessVO extends BaseVO {
    private String id; //流程id
    private String name;//流程名
    private String description;//流程描述
    private Integer version;//流程版本
    private String key;//流程启动key

}
