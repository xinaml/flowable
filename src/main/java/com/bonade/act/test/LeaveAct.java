package com.bonade.act.test;

import com.bonade.service.ProcessSer;
import com.bonade.service.TaskSer;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author liguiqin
 * 请假流程测试
 */
@RestController
@RequestMapping("leave")
public class LeaveAct {
    @Autowired
    private ProcessSer processSer;
    @Autowired
    private TaskSer taskSer;


    /**
     * 1.请假申请启动
     *
     * @param userId      申请人
     * @param businessKey 关联业务id
     * @param key         启动实例key，bpmn20.xml process.id
     */
    @PostMapping("start")
    public String start(@RequestParam String userId, @RequestParam String key, String businessKey) {
        //启动流程
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        ProcessInstance processInstance = processSer.startByKey(userId,key, businessKey, map);
        return "提交成功.流程Id为：" + processInstance.getId();
    }


    /**
     * 2.请假申请
     *
     * @param day    请假天数
     * @param taskId 任务id
     */
    @PostMapping("apply")
    public String start(@RequestParam Integer day, @RequestParam String taskId) {
        //启动流程
        HashMap<String, Object> map = new HashMap<>();
        if (day >= 3) { //请假申请会根据网关条件需要 boosId或者masterId，也可不判断天数都设置boosId或者masterId
            map.put("bossId", "boss");
        } else {
            map.put("masterId", "master");
        }
        map.put("day", day);
        taskSer.complete(taskId, map);
        return "请假申请成功！";
    }

    /**
     * 3.完成任务( master,boss,ceo)
     *
     * @param taskId 任务ID
     */
    @PostMapping("complete/{taskId}")
    public String complete(@PathVariable String taskId) {

        //通过审核
        HashMap<String, Object> map = new HashMap<>();
        map.put("ceoId", "ceo");
        map.put("msg", "pass"); //网关条件
        taskSer.complete(taskId, map);
        return "审批通过";
    }

    /**
     * 3.驳回，审核不通过
     *
     * @param taskId 任务id
     */
    @PostMapping("reject/{taskId}")
    public String reject(@PathVariable String taskId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("msg", "reject");
        taskSer.complete(taskId, map);
        return "拒绝审批";
    }


}
