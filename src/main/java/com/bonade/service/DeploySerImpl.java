package com.bonade.service;

import com.bonade.dto.DeployDTO;
import com.bonade.utils.BeanUtil;
import com.bonade.vo.DeployVO;
import com.bonade.vo.HistoricProcessInstanceVO;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * @Author: [liguiqin]
 * @Date: [2019-09-19 14:12]
 * @Description: [部署流程 ]
 * @Version: [1.0.0]
 * @Copy: [com.bonade]
 */
@Service
public class DeploySerImpl implements DeploySer {
    @Autowired
    private RepositoryService repositoryService;

    @Override
    public Map<String, Object> page(DeployDTO dto) {
        DeploymentQuery query = repositoryService.createDeploymentQuery().orderByDeploymenTime().desc();
        Map<String, Object> results = new HashMap<>(2);
        results.put("total", query.count());
        List<Deployment> deployments = query.listPage(dto.getStart(), dto.getRows());
        List<HistoricProcessInstanceVO> list = BeanUtil.copyProperties(deployments, DeployVO.class);
        results.put("list", list);
        return results;
    }

    @Override
    public void deployToFile(String processName, MultipartFile file) {
        if (!file.isEmpty()) {
            if(file.getOriginalFilename().endsWith("bpmn20.xml")){
                try {
                    repositoryService.createDeployment()
                            .addInputStream(file.getOriginalFilename(), file.getInputStream())
                            .name(processName)
                            .deploy();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("部署流程文件失败");
                }
            }else {
                throw new RuntimeException("文件后缀必须为bpmn20.xml");
            }

        }else {
            throw new RuntimeException("部署流程文件不能为空");
        }

    }

    @Override
    public void deployToZip(String processName, MultipartFile file) {
        if (!file.isEmpty()) {
            ZipInputStream zipInputStream = null;
            try {
                zipInputStream = new ZipInputStream(file.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("部署流程文件失败");
            }
            repositoryService
                    .createDeployment()
                    .name(processName)
                    .addZipInputStream(zipInputStream)
                    .deploy();
        }else {
            throw new RuntimeException("部署流程文件不能为空");
        }
    }

    @Override
    public void delete(String deployId, boolean cascade) {
//        cascade=true 级联删除
        repositoryService.deleteDeployment(deployId, cascade);
    }
}
