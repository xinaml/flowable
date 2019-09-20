package com.bonade.act;

import com.bonade.dto.TaskDTO;
import com.bonade.service.TaskSer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-17 10:45]
 * @Description: [任务相关 ]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@RestController
@RequestMapping("task")
public class TaskAct {
    @Autowired
    private TaskSer taskSer;


    /**
     * 查询已完成的历史任务
     * desc HistoricActivityInstance包括没有分配任务执行人的网管、开始事件和结束事件
     * desc HistoricTaskInstance只包含该流程实例下跟人相关的活动
     *
     * @return
     */

    @GetMapping("finished/page")
    public Map<String, Object> finishedPage(TaskDTO taskDTO) {
        return taskSer.finishedPage(taskDTO);
    }

    /**
     * 个人任务列表
     */
    @GetMapping("unFinish/page")
    public Map<String, Object> unFinishPage(TaskDTO dto) {
        return taskSer.unFinishedPage(dto);
    }

    /**
     * 删除任务
     *
     * @param taskId
     * @return
     */
    @PostMapping("delete/{taskId}")
    public String delete(@PathVariable String taskId) {
        taskSer.delTask(taskId,true);
        return "删除任务成功";
    }

    /**
     * 任务转办
     *
     * @param taskId
     * @param userId
     * @return
     */
    @PostMapping("entrust")
    public String entrust(String taskId, String userId) {
        taskSer.turn(taskId, userId);
        return "任务转办成功";
    }

    /**
     * 流程变量
     */
    @GetMapping("variable")
    public Map<String, Object> val(@RequestParam String taskId) {
        Map<String, Object> map = taskSer.variable(taskId);
        return map;
    }

}
