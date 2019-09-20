package com.bonade.service;


import com.bonade.dto.TaskDTO;
import com.bonade.to.AttachmentTO;
import com.bonade.vo.AttachmentVO;
import com.bonade.vo.CommentVO;

import java.util.List;
import java.util.Map;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-18 18:09]
 * @Description: [ 任务]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
public interface TaskSer {
    /**
     * 完成的任务分页
     * @param dto
     * @return
     */
    Map<String,Object> finishedPage(TaskDTO dto);

    /**
     * 待办的任务分页
     * @param dto
     * @return
     */
    Map<String,Object> unFinishedPage(TaskDTO dto);

    /**
     * 待认领任务分页
     * @param dto
     * @return
     */

    Map<String,Object> claimPage(TaskDTO dto);

    /**
     * 任务认领
     * @param taskId 认领任务id
     * @param userId 认领人
     * @return
     */
    void claim(String taskId, String userId);

    /**
     * 取消任务认领
     * @param taskId 取消认领任务id
     * @return
     */
    void unClaim(String taskId);

    /**
     * 任务转办
     * @param taskId 任务id
     * @param userId 转办给谁
     */
    void turn(String taskId, String userId);

    /**
     * 获取任务批注列表
     * @param taskId 任务id
     */
    List<CommentVO> commentList(String taskId);

    /**
     * 添加任务批注
     * @param taskId 任务id
     * @param message 批注内容
     */
    void addComment(String taskId, String message);

    /**
     * 删除任务批注
     * @param commentId 评论id
     */
    void delComment(String commentId);

    /**
     * 获取任务附件列表
     * @param taskId 任务id
     */
    List<AttachmentVO>attachmentList(String taskId);

    /**
     * 添加任务附件
     * @param to 任务id
     */
    void addAttachment(AttachmentTO to);

    /**
     * 删除任务附件
     * @param attachmentId 附件id
     */
    void delAttachment(String attachmentId);

    /**
     * 获取任务变量
     * @param taskId
     * @return
     */
    Map<String,Object> variable(String taskId);
    /**
     * 删除任务
     * @param taskId
     * @param cascade 是否级联删除
     * @return
     */
    void delTask(String taskId, boolean cascade);
 /**
     * 完成任务
     * @param taskId
     * @return
     */
    void complete(String taskId, Map<String, Object> map);
    /**
     * 完成任务并添加批注
     * @param taskId
     * @return
     */
    void completeTaskAndAddComment(String taskId, String message, Map<String, Object> map);
    /**
     * 启动流程并完成申请
     * @param userId 用户id
     * @param key 定义的key
     * @param businessKey 业务id
     * @param map 流程变量
     * @return
     */
    void startAndComplete(String userId, String key, String businessKey, Map<String, Object> map);
    /**
     * 添加候选人
     * @param taskId
     * @return
     */
    void addCandidateUser(String taskId, String userId);

}
