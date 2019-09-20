package com.bonade.dto;

import com.bonade.dto.base.PageDTO;
import com.bonade.type.TaskStatus;
import lombok.Data;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-19 09:32]
 * @Description: [ 流程实例]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@Data
public class ProcessInstanceDTO extends PageDTO {
    /**
     * 关联业务id
     */
    private String businessKey;
    /**
     * 发起流程的用户
     */
    private String startByUser;
    /**
     * 参与的用户
     */
    private String involvedUser;
    /**
     * 任务状态
     */
    private TaskStatus taskStatus;
    /**
     * 是否查询删除的流程，默认不查
     */
    private Boolean delete;
}
