package com.bonade.act;

import com.bonade.service.ProcessSer;
import com.bonade.service.TaskSer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-20 14:42]
 * @Description: [ ]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@RequestMapping("contract/template")
@RestController
public class ContractTemplateAct {
    @Autowired
    private TaskSer taskSer;
    @Autowired
    private ProcessSer processSer;

    /**
     * 启动流程且自动申请模板审核
     */
    @PostMapping("apply")
    public String apply(@RequestParam String userId) {
        Map<String, Object> map = new HashMap<>();
        String[] v = {"person1", "person2", "person3", "person4"};
        map.put("assigneeList", Arrays.asList(v));
        map.put("userId", userId);
        taskSer.startAndComplete(userId,"contractTemplate","contractTemplate",map);
        return "success";
    }

    /**
     * 完成任务,一个人完成，即完成流程
     *
     * @param taskId 任务ID
     */
    @PostMapping("complete/{taskId}")
    public String complete(@PathVariable String taskId) {
        //通过审核
        HashMap<String, Object> map = new HashMap<>();
        map.put("msg", "pass"); //网关条件
        taskSer.completeTaskAndAddComment(taskId, "该任务以由xxx完成",map);
        return "审批通过";
    }

    /**
     * 驳回，审核不通过
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

    /**
     * 驳回，审核不通过
     *
     * @param processInstanceId 流程实例id
     */
    @PostMapping("cancel/{processInstanceId}")
    public String cancel(@PathVariable String processInstanceId,String deleteReason) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("msg", "reject");
        processSer.delete(processInstanceId, deleteReason);
        return "取消合同模板审核成功";
    }

    /**
     * 是否通过了流程
     *
     * @param processInstanceId 流程实例id
     */
    @GetMapping("isPass/{processInstanceId}")
    public Boolean isPass(@PathVariable String processInstanceId) {
        return processSer.isPass(processInstanceId);
    }
}
