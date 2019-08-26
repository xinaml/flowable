package com.bonade.controller;

import com.alibaba.fastjson.JSON;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * 流程部署相关
 */
@RequestMapping("deploy")
@RestController
public class DeployAct {

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 获取部署流程列表
     *
     * @return
     */
    @GetMapping("list")
    public String list() {
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        List<String> results = new ArrayList<>();
        for (Deployment deployment : list) {
//            repositoryService.deleteDeployment(deployment.getId());
            results.add("id:" + deployment.getId() + " key:" + deployment.getKey() + " name:" + deployment.getName());
        }
        return JSON.toJSONString(results);
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
        if (!file.isEmpty()) {
            try {
                repositoryService.createDeployment()
                        .addInputStream(file.getOriginalFilename(), file.getInputStream())
                        .name(processName)
                        .deploy();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "部署成功";
        }
        return "部署失败";
    }

    /**
     * 部署流程
     *
     * @param file        流程的zip文件
     * @param processName 流程的名字
     * @throws IOException
     */
    @PostMapping("zip")
    public void deployToZip(File file, String processName) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        repositoryService
                .createDeployment()
                .name(processName)
                .addZipInputStream(zipInputStream)
                .deploy();
    }


    /**
     * 删除部署流程
     *
     * @param deploymentId
     * @return
     */
    @PostMapping("delete/{deploymentId}")
    public String delete(@PathVariable String deploymentId) {
        repositoryService.deleteDeployment(deploymentId, true);
        return "删除部署流程成功";
    }


}
