package com.bonade.dto;

import com.bonade.dto.base.PageDTO;
import lombok.Data;

import java.util.List;

@Data
public class TaskDTO extends PageDTO {
    private String userId;//用户id
    private List<String> groupIds;//组id
}
