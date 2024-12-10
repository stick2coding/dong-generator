package com.dong.web.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.dong.web.manager.CosManager;
import com.dong.web.mapper.GeneratorMapper;
import com.dong.web.model.entity.Generator;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ClearCosJob {

    @Resource
    private CosManager cosManager;

    @Resource
    private GeneratorMapper generatorMapper;

    @XxlJob("clearCosJobHandler")
    private void clearCosJobHandler() throws Exception{
        log.info("清理文件任务开始...");
        //首先删除用户上传的模板文件
        cosManager.deleteDir("/generator_make_template/");

        //找到已删除的生成器
        List<Generator> generatorList = generatorMapper.listGeneratorWithDelete();

        // /generator_dist/1862071556895084545/OR2haw3D-test1208.zip
        // 需要去除前面的斜杠
        List<String> keyList = generatorList.stream()
                .map(Generator::getDistPath)
                .filter(StrUtil::isNotBlank)
                .map(distPath -> distPath.substring(1))
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(keyList)) {
            log.info("清理文件任务结束，无生成器产物包数据删除...");
            return;
        }
        cosManager.deleteObjects(keyList);
        log.info("清理文件任务结束...");
    }

    private void deleteDir(String dir) {

    }

}
