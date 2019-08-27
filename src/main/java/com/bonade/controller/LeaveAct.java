package com.bonade.controller;

import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * 请假流程测试
 */
@RestController
@RequestMapping("leave")
public class LeaveAct {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;


    /**
     * 请假申请启动
     * @param userId 申请人
     */
    @PostMapping(value = "start")
    public String start(@RequestParam String userId) {
        //启动流程
        Authentication.setAuthenticatedUserId("lgq");//设置任务发起人
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave", map);
        Authentication.setAuthenticatedUserId(null);//防止多线程的时候出问题。
        return "提交成功.流程Id为：" + processInstance.getId();
    }
    /**
     * 请假申请
     * @param day 请假天数
     */
    @PostMapping(value = "apply")
    public String start( @RequestParam Integer day,@RequestParam String taskId) {
        //启动流程
        HashMap<String, Object> map = new HashMap<>();
        if(day>=3){
            map.put("bossId","boss");
        }else {
            map.put("masterId","master");
        }
        map.put("day", day);
        taskService.complete(taskId, map);
        return "请假申请成功！";
    }

    /**
     * 完成任务
     *
     * @param taskId 任务ID
     */
    @PostMapping(value = "complete")
    public String pass(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("流程不存在！");
        }
        //通过审核
        HashMap<String, Object> map = new HashMap<>();
        map.put("ceoId", "ceo");
        map.put("msg", "同意");
        taskService.complete(taskId, map);
        return "流程通过";
    }

    /**
     * 拒绝
     */
    @PostMapping(value = "reject")
    public String reject(String taskId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("msg", "不同意");
        taskService.complete(taskId, map);
        return "拒绝流程";
    }




}
