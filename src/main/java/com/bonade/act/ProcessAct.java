package com.bonade.act;

import com.bonade.dto.ProcessDTO;
import com.bonade.dto.ProcessInstanceDTO;
import com.bonade.service.ProcessSer;
import com.bonade.utils.BeanUtil;
import com.bonade.vo.TaskVO;
import org.flowable.bpmn.model.*;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-17 10:45]
 * @Description: [流程实例 ]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@RestController
@RequestMapping("process")
public class ProcessAct {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ProcessSer processSer;

    /**
     * 流程定义分页
     *
     * @return
     */
    @GetMapping("page")
    public Map<String, Object> page(ProcessDTO dto) {
        return processSer.page(dto);
    }


    /**
     * 流程实例分页列表
     *
     * @return
     */
    @GetMapping("instance/page")
    public Map<String, Object> instancePage(ProcessInstanceDTO dto) {
        return processSer.instancePage(dto);
    }

    /**
     * 通过实例id获取当前任务执行到的步骤
     *
     * @param processInstanceId
     */
    @GetMapping("detail/{processInstanceId}")
    public TaskVO processDetailed(@PathVariable String processInstanceId) {
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).orderByProcessInstanceId().desc().singleResult();
        TaskVO vo = BeanUtil.copyProperties(task, TaskVO.class);
        return vo;
    }

    /**
     * 挂起流程，（不影响正在运行的流程）
     *
     * @param processInstanceId
     * @return
     */
    @PostMapping("suspend/{processInstanceId}")
    public Boolean suspend(@PathVariable String processInstanceId) {
        return processSer.suspend(processInstanceId);//
    }

    /**
     * 激活流程
     *
     * @param processInstanceId
     * @return
     */
    @PostMapping("activate/{processInstanceId}")
    public String activate(@PathVariable String processInstanceId) {
        processSer.activate(processInstanceId);//
        return "流程激活成功";
    }

    /**
     * 删除,取消流程
     *
     * @param processInstanceId
     * @return
     */
    @PostMapping("delete/{processInstanceId}")
    public String delete(@PathVariable String processInstanceId) {
        processSer.delete(processInstanceId, "不想要了");
        return "删除流程实例成功";
    }

    /**
     * 回退流程
     * 不支持并行网管
     *
     * @param processInstanceId
     * @param currentActivityId usertaskID，定义流程节点id
     * @param targetActivityId  usertaskID，定义流程节点id
     * @return
     */
    @PostMapping("rollback")
    public Boolean rollback(String processInstanceId, String currentActivityId, String targetActivityId) {
        return processSer.rollback(processInstanceId, currentActivityId, targetActivityId);
    }

    /**
     * 获取任务节点
     *
     * @param node   查询节点选择
     * @param taskId 任务id
     */
    @GetMapping("flowNode")
    public void nextFlowNode(String node, String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ExecutionEntity ee = (ExecutionEntity) runtimeService.createExecutionQuery()
                .executionId(task.getExecutionId()).singleResult();
        // 当前审批节点
        String crruentActivityId = ee.getActivityId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(crruentActivityId);
        // 输出连线
        List<SequenceFlow> outFlows = flowNode.getOutgoingFlows();
        for (SequenceFlow sequenceFlow : outFlows) {
            //当前审批节点
            if ("now".equals(node)) {
                FlowElement sourceFlowElement = sequenceFlow.getSourceFlowElement();
                System.out.println("当前节点: id=" + sourceFlowElement.getId() + ",name=" + sourceFlowElement.getName());
            } else if ("next".equals(node)) {
                // 下一个审批节点
                FlowElement targetFlow = sequenceFlow.getTargetFlowElement();
                if (targetFlow instanceof UserTask) {
                    System.out.println("下一节点: id=" + targetFlow.getId() + ",name=" + targetFlow.getName());
                }
                // 如果下个审批节点为结束节点
                if (targetFlow instanceof EndEvent) {
                    System.out.println("下一节点为结束节点：id=" + targetFlow.getId() + ",name=" + targetFlow.getName());
                }
            }
        }
    }

    /**
     * 获取当前未走完的任务流程图
     *
     * @param processInstanceId 流程实例
     */
    @GetMapping("diagram/{processInstanceId}")
    public void diagram(HttpServletResponse response, @PathVariable String processInstanceId) throws Exception {
        processSer.diagram(processInstanceId, response.getOutputStream());
    }
    /**
     * 通过业务id或实例查询走过的任务历史
     *
     * @param businessKey 业务id
     * @param processInstanceId 实例id
     */
    @GetMapping("historicTasks")
    public List<TaskVO> historicTasks( String businessKey,String processInstanceId) throws Exception {
        return processSer.historicTasks(businessKey,processInstanceId);
    }


}
