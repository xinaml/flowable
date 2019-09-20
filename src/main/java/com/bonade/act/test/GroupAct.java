package com.bonade.act.test;

import com.bonade.service.ProcessSer;
import com.bonade.service.TaskSer;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author liguiqin
 * 请假流程测试
 */
@RestController
@RequestMapping("group")
public class GroupAct {
    @Autowired
    private ProcessSer processSer;
    @Autowired
    private TaskSer taskSer;

    /**
     * 1.申请启动
     *
     * @param userId 申请人
     * @param key    启动实例key，bpmn20.xml process.id
     */
    @PostMapping("start")
    public String start(@RequestParam String userId, @RequestParam String key) {
        //启动流程
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        ProcessInstance processInstance = processSer.startByKey(userId,key, UUID.randomUUID().toString(), map);
        return "提交成功.流程Id为：" + processInstance.getId();
    }

    /**
     * 2.申请
     *
     * @param taskId 任务id
     */
    @PostMapping("apply")
    public String apply(@RequestParam String taskId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", "xiaoming");//候选人
        map.put("group", "group1");//候选组
        taskSer.complete(taskId, map);
        return "申请成功！";
    }

    /**
     * 3.任务认领
     *
     * @param taskId
     * @param userId
     * @return 可通过 TaskAct.candidate/list 查询候选人任务
     * 可通过 TaskAct.group/list 查询组任务
     * 然后认领
     */
    @PostMapping("claim")
    public String claim(String taskId, String userId) {
        taskSer.claim(taskId, userId);
        return "任务认领成功";
    }

    /**
     * 4.完成任务
     *
     * @param taskId
     * @return
     */
    @PostMapping("complete/{taskId}")
    public String complete(@PathVariable String taskId) {
        taskSer.complete(taskId,null);
        return "审批通过";
    }

}
