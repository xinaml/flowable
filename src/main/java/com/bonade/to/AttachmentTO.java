package com.bonade.to;

import lombok.Data;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-19 16:24]
 * @Description: [ ]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@Data
public class AttachmentTO {
    private String taskId; //任务id
    private String name;//附件名
    private String description;//附件描述
    private String url;//附件链接
    private String attachmentType;//附件类型
}
