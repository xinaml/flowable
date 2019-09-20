package com.bonade.service;

import com.bonade.dto.TaskDTO;
import com.bonade.to.AttachmentTO;
import com.bonade.utils.BeanUtil;
import com.bonade.vo.AttachmentVO;
import com.bonade.vo.CommentVO;
import com.bonade.vo.TaskVO;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Attachment;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-18 18:09]
 * @Description: [任务 ]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@Service
public class TaskSerImpl implements TaskSer {
    @Autowired
    private HistoryService historyService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ProcessSer processSer;
    @Override
    public Map<String, Object> finishedPage(TaskDTO dto) {
        HistoricTaskInstanceQuery query = historyService
                .createHistoricTaskInstanceQuery().finished();
        query.taskAssignee(dto.getUserId());
        Map<String, Object> results = new HashMap<>();
        results.put("total", query.count());
        List<HistoricTaskInstance> htiList = query
                .orderByHistoricTaskInstanceStartTime()
                .desc()
                .listPage(dto.getStart(), dto.getRows());
        List<TaskVO> list = BeanUtil.copyProperties(htiList, TaskVO.class);
        results.put("list", list);
        return results;
    }

    @Override
    public Map<String, Object> unFinishedPage(TaskDTO dto) {
        TaskQuery query = taskService.createTaskQuery();
        query.taskAssignee(dto.getUserId());
        Map<String, Object> results = new HashMap<>();
        results.put("total", query.count());
        List<Task> tasks = query
                .orderByTaskCreateTime()
                .desc()
                .listPage(dto.getStart(), dto.getRows());
        List<TaskVO> list = BeanUtil.copyProperties(tasks, TaskVO.class);
        results.put("list", list);
        return results;
    }

    @Override
    public Map<String, Object> claimPage(TaskDTO dto) {
        TaskQuery query = taskService.createTaskQuery()
                .taskCandidateUser(dto.getUserId())
                .taskCandidateGroupIn(dto.getGroupIds());
        Map<String, Object> results = new HashMap<>();
        results.put("total", query.count());
        List<Task> tasks = query
                .orderByTaskCreateTime()
                .desc()
                .listPage(dto.getStart(), dto.getRows());
        List<TaskVO> list = BeanUtil.copyProperties(tasks, TaskVO.class);
        results.put("list", list);
        return results;
    }

    @Override
    public void claim(String taskId, String userId) {
        taskService.claim(taskId, userId);
    }

    @Override
    public void unClaim(String taskId) {
        taskService.unclaim(taskId);
    }

    @Override
    public void turn(String taskId, String userId) {
        taskService.setAssignee(taskId, userId);
    }

    @Override
    public List<CommentVO> commentList(String taskId) {
        List<Comment> comments = taskService.getTaskComments(taskId);
        return BeanUtil.copyProperties(comments, CommentVO.class);
    }

    @Override
    public void addComment(String taskId, String message) {
        Task task = taskService.createTaskQuery().taskId(taskId)
                .singleResult();
        taskService.addComment(taskId, task.getProcessInstanceId(), message);
    }

    @Override
    public void delComment(String commentId) {
        taskService.deleteComment(commentId);
    }

    @Override
    public List<AttachmentVO> attachmentList(String taskId) {
        List<Attachment> attachments = taskService.getTaskAttachments(taskId);
        return BeanUtil.copyProperties(attachments, AttachmentVO.class);
    }

    @Override
    public void addAttachment(AttachmentTO to) {
        Task task = taskService.createTaskQuery().taskId(to.getTaskId())
                .singleResult();
        taskService.createAttachment(to.getAttachmentType(), task.getId(), task.getProcessInstanceId(), to.getName(), to.getDescription(), to.getUrl());
    }

    @Override
    public void delAttachment(String attachmentId) {
        taskService.deleteAttachment(attachmentId);
    }

    @Override
    public Map<String, Object> variable(String taskId) {
        return taskService.getVariables(taskId);
    }

    @Override
    public void complete(String taskId, Map<String, Object> map) {
        map = map != null ? map : Maps.newHashMap();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("任务已完成或任务不存在！");
        }
        try {
            taskService.complete(taskId, map);
        } catch (FlowableException e) {
            String msg = e.getMessage();
            if (msg.indexOf("Unknown property used") != -1) {
                msg = StringUtils.substringAfterLast(msg, "${");
                throw new RuntimeException("请设置任务变量：" + msg.substring(0, msg.length() - 1));
            } else {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void completeTaskAndAddComment(String taskId, String message, Map<String, Object> map) {
        complete(taskId, map);
        addComment(taskId, message);
    }

    @Override
    public void startAndComplete(String userId, String key,String businessKey, Map<String, Object> map) {
        ProcessInstance instance = processSer.startByKey(userId,key,businessKey,map);
        Task task = taskService.createTaskQuery().processInstanceId(instance.getId()).orderByProcessInstanceId().desc().singleResult();
        complete(task.getId(),map);

    }

    @Override
    public void delTask(String taskId, boolean cascade) {
        taskService.deleteTask(taskId, cascade);
    }

    @Override
    public void addCandidateUser(String taskId, String userId) {
        taskService.addCandidateUser(taskId, userId);
    }
}
