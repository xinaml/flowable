package com.bonade.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-18 15:26]
 * @Description: [历史流程实例 ]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@Data
public class HistoricProcessInstanceVO extends BaseVO {
    private String id;//实例id
    private String startUserId;//发起流程人
    private String processDefinitionName;
    @JsonFormat(pattern = DATE_FORMAT)
    private Date startTime;//流程开始时间
    @JsonFormat(pattern = DATE_FORMAT)
    private Date endTime;//流程结束时间

}
