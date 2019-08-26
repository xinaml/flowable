package com.bonade.controller;

import org.flowable.engine.TaskService;
import org.flowable.engine.task.Attachment;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务相关
 */
@RestController
@RequestMapping("task")
public class TaskAct {
    @Autowired
    private TaskService taskService;

    /**
     * 任务委托
     *
     * @param taskId
     * @param userId
     * @return
     */
    @PostMapping(value = "change")
    public String change(String taskId, String userId) {
        taskService.setAssignee(taskId, userId);
        return "任务委托成功";
    }


    /**
     * 任务认领
     *
     * @param taskId
     * @param userId
     * @return
     */
    @PostMapping(value = "claim")
    public String claim(String taskId, String userId) {
        taskService.claim(taskId, userId);
        return "任务认领成功";
    }

    /**
     * 任务列表
     */
    @GetMapping(value = "/list")
    public List<String> list(@RequestParam String userId) {
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(userId)
                .orderByTaskCreateTime()
                .desc()
                .list();
        List<String> results = new ArrayList<>();
        for (Task task : tasks) {
            results.add("taskId:" + task.getId() + ",processInstanceId:" + task.getProcessInstanceId() + " task:" + task.getName());
        }
        return results;
    }

    /**
     * 组任务列表
     */
    @GetMapping(value = "/group/list")
    public List<String> groupList(@RequestParam String group) {
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateGroup(group)
                .orderByTaskCreateTime()
                .desc()
                .list();
        List<String> results = new ArrayList<>();
        for (Task task : tasks) {
            results.add("taskId:" + task.getId() + ",processInstanceId:" + task.getProcessInstanceId() + " task:" + task.getName());
        }
        return results;
    }

    /**
     * 组任务及个人任务列表
     */
    @GetMapping(value = "/groupAndAssignee/list")
    public List<String> groupList(@RequestParam String group, @RequestParam String userId) {
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(userId)
                .taskCandidateGroup(group)
                .orderByTaskCreateTime()
                .desc()
                .list();
        List<String> results = new ArrayList<>();
        for (Task task : tasks) {
            results.add("taskId:" + task.getId() + ",processInstanceId:" + task.getProcessInstanceId() + " task:" + task.getName());
        }
        return results;
    }

    /**
     * 任务持有
     *
     * @param userId
     * @return
     */
    @GetMapping(value = "/owner/list")
    public List<String> ownerList(@RequestParam String userId) {
        List<Task> list = taskService.createTaskQuery().taskOwner(userId).list();
        List<String> results = new ArrayList<>();
        for (Task task : list) {
            results.add("taskId:" + task.getId() + ",processInstanceId:" + task.getProcessInstanceId() + " task:" + task.getName());
        }
        return results;
    }

    /**
     * 删除任务
     *
     * @param taskId
     * @return
     */
    @PostMapping(value = "delete/{taskId}")
    public String delete(@PathVariable String taskId) {
        taskService.deleteTask(taskId);
        return "删除任务成功";
    }


    /**
     * 添加附件
     *
     * @param taskId
     * @return
     */
    @PostMapping(value = "add/attachment")
    public String addAttachment(String taskId, String url) {
        // 查找任务
        Task task = taskService.createTaskQuery().taskId(taskId)
                .singleResult();
//        String attachmentType, String taskId, String processInstanceId, String attachmentName, String attachmentDescription,  String url
        // 设置输入流为任务附件
        taskService.createAttachment("PDF", task.getId(), task.getProcessInstanceId(), "附件名",
                "这个附件描述", url);
        return "添加附件成功";
    }

    /**
     * 添加评论
     *
     * @param taskId
     * @param comment
     * @return
     */
    @PostMapping(value = "add/comment")
    public String addComment(String taskId, String comment) {
        // 查找任务
        Task task = taskService.createTaskQuery().taskId(taskId)
                .singleResult();
        taskService.addComment(task.getId(), task.getProcessInstanceId(), comment);
        return "添加评论成功";
    }

    /**
     * 获取任务评论
     *
     * @param taskId
     * @return
     */
    @GetMapping(value = "comments/{taskId}")
    public List<Comment> getComments(@PathVariable String taskId) {
        List<Comment> comments = taskService.getTaskComments(taskId);
        return comments;
    }

    /**
     * 获取任务附件
     *
     * @param taskId
     * @return
     */
    @GetMapping(value = "attachment/{taskId}")
    public List<Attachment> getAttachment(@PathVariable String taskId) {
        List<Attachment> attachments = taskService.getTaskAttachments(taskId);
        return attachments;
    }


}
