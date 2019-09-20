package com.bonade.act;

import com.bonade.dto.DeployDTO;
import com.bonade.service.DeploySer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-17 10:45]
 * @Description: [流程部署 ]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@RequestMapping("deploy")
@RestController
public class DeployAct {

    @Autowired
    private DeploySer deploySer;

    /**
     * 获取部署流程列表
     *
     * @return
     */
    @GetMapping("page")
    public Map<String, Object> page(DeployDTO dto) {
        return deploySer.page(dto);
    }


    /**
     * 部署流程文件
     *
     * @param file
     * @param processName 流程的名字
     * @return
     */
    @PostMapping("file")
    public String deployToFile(@RequestParam MultipartFile file, @RequestParam String processName) {
        deploySer.deployToFile(processName, file);
        return processName + ":部署成功";
    }

    /**
     * 部署流程
     *
     * @param file        流程的zip文件
     * @param processName 流程的名字
     * @throws IOException
     */
    @PostMapping("zip")
    public String deployToZip(MultipartFile file, String processName) throws IOException {
        deploySer.deployToZip(processName, file);
        return processName + ":部署成功";
    }

    /**
     * 删除部署流程
     *
     * @param deploymentId
     * @return
     */
    @PostMapping("delete/{deploymentId}")
    public String delete(@PathVariable String deploymentId) {
        deploySer.delete(deploymentId,true);
        return "删除部署流程成功";
    }


}
