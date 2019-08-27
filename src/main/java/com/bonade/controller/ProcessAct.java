package com.bonade.controller;

import org.flowable.bpmn.model.*;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程实例操作
 */
@RestController
@RequestMapping("process")
public class ProcessAct {
    @Autowired
    private RuntimeService runtimeService;
    @Resource
    private ProcessEngine processEngine;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    /**
     * 查询所有的部署定义信息
     *
     * @return
     */
    @GetMapping("list")
    public List<String> getAllProcessInstance() {
        List<String> list = new ArrayList<>();
        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion()
//                .latestVersion() //最新版本
                .desc()
                .list();
        for (ProcessDefinition pd : processDefinitions) {
            list.add("name:" + pd.getName() + ",version:" + pd.getVersion());
        }
        List<HistoricProcessInstance> processInstances = historyService
                .createHistoricProcessInstanceQuery().deleted()
                .list();
        return list;
    }

    /**
     * 删除的流程列表
     *
     * @return
     */
    @GetMapping("delete/list")
    public List<String> deleteList() {
        List<String> list = new ArrayList<>();
        List<HistoricProcessInstance> processInstances = historyService
                .createHistoricProcessInstanceQuery().deleted()
                .list();
        for (HistoricProcessInstance pi : processInstances) {
            list.add(pi.getName() + "删除原因：" + pi.getDeleteReason());
        }
        return list;
    }

    /**
     * 完成的流程列表
     *
     * @return
     */
    @GetMapping("finished/list")
    public List<String> finishedList() {
        List<String> list = new ArrayList<>();
        List<HistoricProcessInstance> processInstances = historyService
                .createHistoricProcessInstanceQuery().finished()
                .list();
        for (HistoricProcessInstance pi : processInstances) {
            list.add(pi.getProcessDefinitionName());
        }
        return list;
    }

    /**
     * 通过实例id获取当前任务执行到的步骤
     *
     * @param processInstanceId
     */
    @GetMapping(value = "detail/{processInstanceId}")
    public Map<String, Object> processDetailed(@PathVariable String processInstanceId) {
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).orderByProcessInstanceId().desc().singleResult();
        Map<String, Object> map = new HashMap<>();
        map.put("taskId", task.getId());
        map.put("处理人", task.getAssignee());
        return map;
    }

    /**
     * 挂起流程，（不影响正在运行的流程）
     *
     * @param processInstanceId
     * @return
     */
    @PostMapping(value = "suspend/{processInstanceId}")
    public String suspend(@PathVariable String processInstanceId) {
        runtimeService.suspendProcessInstanceById(processInstanceId);//
        return "流程挂起成功";
    }

    /**
     * 激活流程
     *
     * @param processInstanceId
     * @return
     */
    @PostMapping(value = "activate/{processInstanceId}")
    public String activate(@PathVariable String processInstanceId) {
        runtimeService.activateProcessInstanceById(processInstanceId);//
        return "流程激活成功";
    }

    /**
     * 删除流程
     *
     * @param processInstanceId
     * @return
     */
    @PostMapping(value = "delete/{processInstanceId}")
    public String delete(@PathVariable String processInstanceId) {
        runtimeService.deleteProcessInstance(processInstanceId, "不想要了");
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
    @PostMapping(value = "rollback")
    public String rollback(String processInstanceId, String currentActivityId, String targetActivityId) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (null != pi) {
            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(processInstanceId)
                    .moveActivityIdTo(currentActivityId, targetActivityId)
                    .changeState();
            return "回退流程成功";
        } else {
            throw new RuntimeException("任务流程已完成，不允许撤回");
        }
    }
    /**
     * 获取任务节点
     *
     * @param node   查询节点选择
     * @param taskId 任务id
     */
    @GetMapping(value = "flowNode")
    public void nextFlowNode(String node, String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ExecutionEntity ee = (ExecutionEntity)runtimeService.createExecutionQuery()
                .executionId(task.getExecutionId()).singleResult();
        // 当前审批节点
        String crruentActivityId = ee.getActivityId();
        BpmnModel bpmnModel =repositoryService.getBpmnModel(task.getProcessDefinitionId());
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
    @GetMapping(value = "diagram/{processInstanceId}")
    public void genProcessDiagram(HttpServletResponse response, @PathVariable String processInstanceId) throws Exception {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        //流程走完的不显示图
        if (pi == null) {
            return;
        }
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        String InstanceId = task.getProcessInstanceId();
        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(InstanceId).list();
        //得到正在执行的Activity的Id
        List<String> activityIds = new ArrayList<>();
        List<String> flows = new ArrayList<>();
        for (Execution exe : executions) {
            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
            activityIds.addAll(ids);
        }

        //获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows, engconf.getActivityFontName(), engconf.getLabelFontName(), engconf.getAnnotationFontName(), engconf.getClassLoader(), 1.0, false);
        OutputStream out = null;
        byte[] buf = new byte[1024];
        int legth = 0;
        try {
            out = response.getOutputStream();
            while ((legth = in.read(buf)) != -1) {
                out.write(buf, 0, legth);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }


}
