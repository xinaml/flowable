package com.bonade.act;

import com.bonade.service.TaskSer;
import com.bonade.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-17 10:45]
 * @Description: [任务评论 ]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@RequestMapping("comment")
@RestController
public class CommentAct {
    @Autowired
    private TaskSer taskSer;

    /**
     * 获取任务批注列表
     *
     * @param taskId 任务id
     * @return
     */
    @GetMapping("list/{taskId}")
    public List<CommentVO> getComments(@PathVariable String taskId) {
        return taskSer.commentList(taskId);
    }


    /**
     * 添加批注
     *
     * @param taskId  任务id
     * @param message 批注内容
     * @return
     */
    @PostMapping("add")
    public String addComment(String taskId,String message) {
        taskSer.addComment(taskId, message);
        return "添加批注成功";
    }


    /**
     * 删除任务批注
     *
     * @param commentId 批注id
     * @return
     */
    @PostMapping("delete/{commentId}")
    public String delete(@PathVariable String commentId) {
        taskSer.delComment(commentId);
        return "删除批注成功";
    }
}
