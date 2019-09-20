package com.bonade.act.test;

import com.bonade.service.ProcessSer;
import com.bonade.service.TaskSer;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

/**
 * 会签测试
 */
@RestController
@RequestMapping("more")
public class MoreAct {
    @Autowired
    private ProcessSer processSer;
    @Autowired
    private TaskSer taskSer;

    /**
     * 1.资金申请启动
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
     * 2.资金申请
     *
     * @param taskId 任务id
     */
    @PostMapping("apply")
    public String start(@RequestParam String taskId) {
        HashMap<String, Object> map = new HashMap<>();
        //定义的人员列表4人
        String[] v = {"person1", "person2", "person3", "person4"};
        map.put("assigneeList", Arrays.asList(v));
        taskSer.complete(taskId, map);
        return "资金申请成功！";
    }

    /**
     * 只要有person 连个人同意则任务多人审核通过，如果要全部人通过，去掉条件：${nrOfCompletedInstances/nrOfInstances >= 0.50}
     * 3.完成任务( person1,person2,person3，person4，ceo)
     * nrOfInstances 实例总数
     * nrOfActiveInstances 当前还没有完成的实例
     * nrOfCompletedInstances 已经完成的实例个数
     *
     * @param taskId 任务ID
     */
    @PostMapping("complete/{taskId}")
    public String complete(@PathVariable String taskId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("ceoId", "ceo");
        taskSer.complete(taskId, map);
        return "审批通过";
    }
}
