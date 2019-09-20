package com.bonade.act;

import com.bonade.service.TaskSer;
import com.bonade.to.AttachmentTO;
import com.bonade.vo.AttachmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-17 10:48]
 * @Description: [任务附件 ]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@RequestMapping("attachment")
@RestController
public class AttachmentAct {
    @Autowired
    private TaskSer taskSer;

    /**
     * 获取任务附件列表
     *
     * @param taskId 任务id
     * @return
     */
    @GetMapping("list/{taskId}")
    public List<AttachmentVO> getAttachment(@PathVariable String taskId) {
        return taskSer.attachmentList(taskId);
    }


    /**
     * 添加附件
     *
     * @return
     */
    @PostMapping("add")
    public String addAttachment(AttachmentTO to) {
        taskSer.addAttachment(to);
        return "添加附件成功";
    }


    /**
     * 删除任务附件
     *
     * @param attachmentId 附件id
     * @return
     */
    @PostMapping("delete/{attachmentId}")
    public String delete(@PathVariable String attachmentId) {
        taskSer.delAttachment(attachmentId);
        return "删除评论成功";
    }


}
