package com.bonade.act;

import com.bonade.dto.TaskDTO;
import com.bonade.service.TaskSer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-17 10:45]
 * @Description: [任务认领 ]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@RequestMapping("task")
@RestController
public class ClaimAct {
    @Autowired
    private TaskSer taskSer;

    /**
     * 候选人任务列表，需认领
     */
    @GetMapping(value = "claim/list")
    public Map<String, Object> candidateList(TaskDTO dto) {
        return taskSer.claimPage(dto);
    }

    /**
     * 任务认领
     *
     * @param taskId 任务id
     * @param userId 用户id
     * @return
     * @desc 当任务分配给了某一组人员时
     */
    @PostMapping(value = "claim")
    public String claim(String taskId, String userId) {
        taskSer.claim(taskId, userId);
        return "任务认领成功";
    }

    /**
     * 取消任务认领
     *
     * @param taskId 任务id
     * @return
     * @desc 取消任务认领
     */
    @PostMapping(value = "claim/cancel/{taskId}")
    public String cancelClaim(@PathVariable String taskId) {
        taskSer.unClaim(taskId);
        return "任务认领取消成功";
    }
}
