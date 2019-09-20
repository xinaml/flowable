package com.bonade.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-17 10:52]
 * @Description: [ 任务]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@Data
public class TaskVO extends BaseVO {
    private String id;//任务id
    private String name;//任务名
    private String assignee;//任务执行人
    private String processInstanceId;//流程id
    private Map<String, Object> processVariables;//流程变量
    @JsonFormat(pattern = DATE_FORMAT)
    private Date createTime;//任务创建时间
    @JsonFormat(pattern = DATE_FORMAT)
    private Date claimTime;//任务认领时间
    private String description;//描述


}
