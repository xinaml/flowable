package com.bonade.dto;

import com.bonade.dto.base.PageDTO;
import lombok.Data;

/**
 * 流程定义
 */
@Data
public class ProcessDTO extends PageDTO {
    /**
     * 默认查所有版本,true时查询最后的版本
     */
    private Boolean lastVersion;
    /**
     * 默认空时查所有流程，
     * true时查可用流程，
     * false时查不可用流程
     */
    private Boolean enable;


}
