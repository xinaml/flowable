package com.bonade.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-17 10:52]
 * @Description: [ 评论内容]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@Data
public class CommentVO extends BaseVO {
    private String userId;//用户id
    @JsonFormat(pattern = DATE_FORMAT)
    private Date time;//评论时间
    private String taskId;//任务id
    private String fullMessage;//评论内容
}
