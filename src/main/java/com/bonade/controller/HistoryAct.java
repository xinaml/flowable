package com.bonade.controller;

import com.alibaba.fastjson.JSON;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 历史相关
 * * HistoricProcessInstance包含有关当前和过去的流程实例的信息。
 * * HistoricVariableInstance包含过程变量或任务变量的最新值。
 * * HistoricActivityInstance包含有关活动（进程中的节点）的单个执行的信息。
 * * HistoricTaskInstance包含有关当前和过去（已完成和已删除）任务实例的信息。
 * * HistoricIdentityLink包含关于任务和流程实例上当前和过去身份链接的信息。
 * * HistoricDetail包含与历史流程实例，活动实例或任务实例相关的各种信息。
 */
@RestController
@RequestMapping("history")
public class HistoryAct {
    @Autowired
    private HistoryService historyService;


    /**
     * 所有实例列表
     *
     * @return
     */
    @GetMapping(value = "list")
    public String list() {
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().list();
        return JSON.toJSONString(list);
    }

    /**
     * 获取所有任务及完成的任务
     *
     * @param userId
     * @return
     */
    @GetMapping("tasks")
    public Map<String, Object> findHistoryTask(@RequestParam String userId) {
        Map<String, Object> results = new HashMap<>();
        List<HistoricTaskInstance> finishedList = historyService
                .createHistoricTaskInstanceQuery()
                .taskAssignee(userId)
                .finished()
                .list();
        List<HistoricTaskInstance> unFinishList = historyService
                .createHistoricTaskInstanceQuery()
                .taskAssignee(userId).unfinished()
                .list();

        results.put("finishedList", "已完成任务数：" + finishedList.size());
        results.put("unFinishList", "未完成任务数：" + unFinishList.size());
        return results;
    }

    /**
     * 已完成任务
     *
     * @param userId
     * @return
     */
    @GetMapping("finished/list")
    public Map<String, Object> finishedTask(@RequestParam String userId) {
        Map<String, Object> results = new HashMap<>();
        List<HistoricTaskInstance> finishedList = historyService
                .createHistoricTaskInstanceQuery()
                .taskAssignee(userId)
                .finished()
                .list();
        for (HistoricTaskInstance taskInstance : finishedList) {
            HistoricProcessInstance instance = historyService
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(taskInstance.getProcessInstanceId()).singleResult();
            String str =instance.getProcessDefinitionName()+ " ,任务：" + taskInstance.getName() + ",时间：" + taskInstance.getStartTime() + "-" + taskInstance.getEndTime();
            results.put(taskInstance.getProcessInstanceId(), str);
        }
        return results;
    }


}
