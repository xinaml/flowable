package com.bonade.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-17 10:52]
 * @Description: [附件 ]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@Data
public class AttachmentVO implements Serializable {
    private String taskId; //任务id
    private String name;//附件名
    private String description;//附件描述
    private String url;//附件链接
    private String attachmentType;//附件类型
}
