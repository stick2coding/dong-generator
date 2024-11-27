package com.dong.maker.template;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.dong.maker.meta.Meta;
import com.dong.maker.template.model.TemplateMakerConfig;
import com.dong.maker.template.model.TemplateMakerFileConfig;
import com.dong.maker.template.model.TemplateMakerModelConfig;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TemplateMakerV7Test {

    /**
     * 测试同配置多次生成时，强制变为静态生成
     */
    @Test
    public void testMakeTemplateBug1() {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "dong-generator-demo-projects/springboot-init-master";

        // 文件参数配置
        String inputFilePath1 = "src/main/resources/application.yml";
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfigV2 fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfigV2();
        fileInfoConfig1.setFilePath(inputFilePath1);
        templateMakerFileConfig.setFileInfoConfigList(Arrays.asList(fileInfoConfig1));

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1);
        templateMakerModelConfig.setModelInfoConfigList(modelInfoConfigList);

        long id = TemplateMakerV7.makeTemplate(1861600311439208448L, meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig);
        System.out.println(id);
    }

    /**
     * 同文件目录多次生成时，会扫描新的 FTL 文件
     */
    @Test
    public void testMakeTemplateBug2() {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "dong-generator-demo-projects/springboot-init-master";
        System.out.println("模板项目路径:" + originProjectPath);

        // 文件参数配置，扫描目录
        String inputFilePath1 = "src/main/java/com/yupi/springbootinit/common";
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfigV2 fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfigV2();
        fileInfoConfig1.setFilePath(inputFilePath1);
        templateMakerFileConfig.setFileInfoConfigList(Arrays.asList(fileInfoConfig1));

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("className");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setReplaceText("BaseResponse");
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1);
        templateMakerModelConfig.setModelInfoConfigList(modelInfoConfigList);

        long id = TemplateMakerV7.makeTemplate(1861600311439208448L, meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig);
        System.out.println(id);
    }

    /**
     * 使用 JSON 制作模板
     */
    @Test
    public void testMakeTemplateWithJSON() {
        String configStr = ResourceUtil.readUtf8Str("templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        System.out.println(templateMakerConfig);
        long id = TemplateMakerV7.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }





}