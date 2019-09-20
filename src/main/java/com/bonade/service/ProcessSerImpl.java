package com.bonade.service;


import com.bonade.dto.ProcessDTO;
import com.bonade.dto.ProcessInstanceDTO;
import com.bonade.type.TaskStatus;
import com.bonade.utils.BeanUtil;
import com.bonade.vo.HistoricProcessInstanceVO;
import com.bonade.vo.ProcessVO;
import com.bonade.vo.TaskVO;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-18 18:01]
 * @Description: [流程 ]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@Service
public class ProcessSerImpl implements ProcessSer {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private TaskService taskService;
    @Resource
    private ProcessEngine processEngine;


    @Override
    public Map<String, Object> page(ProcessDTO dto) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        if (null != dto.getLastVersion()) {//版本
            query = dto.getLastVersion() ? query.latestVersion() : query.active();
        }
        if (dto.getEnable() != null) {//可用的流程,挂起的流程
            query = dto.getEnable() ? query.active() : query.suspended();

        }
        Map<String, Object> results = new HashMap<>(2);
        results.put("total", query.count());
        List<ProcessDefinition> processDefinitions = query.orderByProcessDefinitionVersion()
                .desc()
                .listPage(dto.getStart(), dto.getRows());
        List<ProcessVO> list = BeanUtil.copyProperties(processDefinitions, ProcessVO.class);
        results.put("list", list);
        return results;
    }

    @Override
    public Map<String, Object> instancePage(ProcessInstanceDTO dto) {
        List<HistoricProcessInstance> processInstances = null;
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(dto.getBusinessKey())
                .involvedUser(dto.getInvolvedUser())
                .startedBy(dto.getStartByUser());
        if (dto.getTaskStatus() != null) {
            if (dto.getTaskStatus() == TaskStatus.FINISHED) {
                query.finished();
            } else if (dto.getTaskStatus() == TaskStatus.UNFINISH) {
                query.unfinished();
            }
        }
        if (dto.getDelete() != null && dto.getDelete()) {
            query.deleted();
        }
        Map<String, Object> results = new HashMap<>(2);
        results.put("total", query.count());
        processInstances = query.listPage(dto.getStart(), dto.getRows());
        List<HistoricProcessInstanceVO> list = BeanUtil.copyProperties(processInstances, HistoricProcessInstanceVO.class);
        results.put("list", list);
        return results;
    }

    @Override
    public ProcessInstance startByKey(String userId, String key, String businessKey, Map<String, Object> map) {
        try {
            Authentication.setAuthenticatedUserId(userId);//设置任务发起人
            ProcessInstance instance = runtimeService.startProcessInstanceByKey(key, businessKey, map);
            Authentication.setAuthenticatedUserId(null);//防止多线程的时候出问题。
            return instance;
        } catch (FlowableObjectNotFoundException e) {
            throw new RuntimeException("流程key不存在");
        }
    }

    @Override
    public ProcessInstance startByProcessDefinitionId(String userId, String processDefinitionId, String businessKey, Map<String, Object> map) {
        try {
            Authentication.setAuthenticatedUserId(userId);//设置任务发起人
            ProcessInstance instance = runtimeService.startProcessInstanceById(processDefinitionId, businessKey, map);
            Authentication.setAuthenticatedUserId(null);//防止多线程的时候出问题。
            return instance;
        } catch (FlowableObjectNotFoundException e) {
            throw new RuntimeException("流程实例id不存在");
        }
    }

    @Override
    public Boolean suspend(String processInstanceId) {
        try {
            runtimeService.suspendProcessInstanceById(processInstanceId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean activate(String processInstanceId) {
        try {
            runtimeService.activateProcessInstanceById(processInstanceId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean rollback(String processInstanceId, String currentActivityId, String targetActivityId) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (null != pi) {
            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(processInstanceId)
                    .moveActivityIdTo(currentActivityId, targetActivityId)
                    .changeState();
            return true;
        } else {
            throw new RuntimeException("任务流程已完成，不允许撤回");
        }
    }

    @Override
    public Boolean delete(String processInstanceId, String deleteReason) {
        try {
            runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("删除实例错误");
        }
    }

    @Override
    public void diagram(String processInstanceId, OutputStream out) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (pi == null) {//流程走完的不显示图
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
        byte[] buf = new byte[1024];
        int length = 0;
        try {
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Boolean isPass(String processInstanceId) {
        HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .finished()
                .singleResult();
        return null != hpi ;
    }

    @Override
    public List<TaskVO> historicTasks(String businessKey, String processInstanceId) {
        List<HistoricTaskInstance> taskInstances = historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .processInstanceId(processInstanceId)
                .orderByTaskCreateTime()
                .asc()
                .list();
        return BeanUtil.copyProperties(taskInstances, TaskVO.class);
    }
}
