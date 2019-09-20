package com.bonade.service;


import com.bonade.dto.ProcessDTO;
import com.bonade.dto.ProcessInstanceDTO;
import com.bonade.vo.TaskVO;
import org.flowable.engine.runtime.ProcessInstance;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-18 18:01]
 * @Description: [ 流程]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
public interface ProcessSer {

    /**
     * 查询流程分页数据
     *
     * @param dto 查询条件
     * @return
     */
    Map<String, Object> page(ProcessDTO dto);

    /**
     * 查询流程实例分页数据
     *
     * @param dto 查询条件
     * @return
     */
    Map<String, Object> instancePage(ProcessInstanceDTO dto);

    /**
     * 通过定义的key启动流程实例
     *
     * @param userId     流程发起人
     * @param key         定义的key
     * @param businessKey 业务id
     * @param map         流程变量
     */
    ProcessInstance startByKey(String userId, String key, String businessKey, Map<String, Object> map);

    /**
     * 通过流程实例id启动流程实例
     *
     * @param userId     流程发起人
     * @param processDefinitionId 流程定义id
     * @param businessKey       业务id
     * @param map               流程变量
     */
    ProcessInstance startByProcessDefinitionId(String userId, String processDefinitionId, String businessKey, Map<String, Object> map);

    /**
     * 挂起流程-不影响正在运行的流程
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    Boolean suspend(String processInstanceId);

    /**
     * 激活流程
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    Boolean activate(String processInstanceId);

    /**
     * 回退流程
     * 不支持并行网管
     *
     * @param processInstanceId 流程实例id
     * @param currentActivityId 当前 userTaskId
     * @param targetActivityId  撤回 userTaskId
     * @return
     */
    Boolean rollback(String processInstanceId, String currentActivityId, String targetActivityId);

    /**
     * 删除流程
     *
     * @param processInstanceId 流程实例id
     * @param deleteReason      删除原因
     * @return
     */
    Boolean delete(String processInstanceId, String deleteReason);

    /* 画出未完成的流程的流程图
     * @param processInstanceId 流程实例id
     * @return
     */
    void diagram(String processInstanceId, OutputStream out);

    /**
     * 流程是否完成且通过
     *
     * @param processInstanceId
     * @return
     */
    Boolean isPass(String processInstanceId);

    /**
     * 通过业务id,或实例id 查询走过的任务历史
     * @param businessKey 业务id
     * @param processInstanceId 实例id
     * @return
     */
    List<TaskVO> historicTasks(String businessKey, String processInstanceId);

}
