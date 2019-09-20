package com.bonade.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


/**
 * @Author: [liguiqin]
 * @Date: [2019-09-17 10:52]
 * @Description: [ 流程部署]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@Data
public class DeployVO extends BaseVO {
    private String id;//部署id
    private String name;//部署名
    private String key;//部署key
    private String version;//版本
    @JsonFormat(pattern = DATE_FORMAT)
    private Date createTime;//创建时间

}
