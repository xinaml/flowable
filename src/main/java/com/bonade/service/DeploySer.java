package com.bonade.service;

import com.bonade.dto.DeployDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-19 11:52]
 * @Description: [流程部署 ]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
public interface DeploySer {

    /**
     * 部署的流程分页
     *
     * @param dto
     * @return
     */
    Map<String, Object> page(DeployDTO dto);

    /**
     * 部署流程通过xml文件
     *
     * @param processName
     * @param file
     * @return
     */
    void deployToFile(String processName, MultipartFile file);

    /**
     * 部署流程通过压缩包
     *
     * @param file
     * @return
     */
    void deployToZip(String processName, MultipartFile file);

    /**
     * 删除流程
     *
     * @param deployId 部署id
     * @param cascade 是否级联删除
     * @return
     */
    void delete(String deployId, boolean cascade);
}
