package com.dong.maker.template;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.dong.maker.template.model.TemplateMakerConfig;
import org.junit.Test;

import static org.junit.Assert.*;

public class TemplateMakerV8Test {

    /**
     * 制作 SpringBoot 模板
     */
    @Test
    public void makeSpringBootTemplate() {
        String rootPath = "examples/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        long id = TemplateMakerV8.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }

    @Test
    public void makeSpringBootTemplate2() {
        // 1 项目基本信息
        System.out.println("基础环境搭建...");
        String rootPath = "examples/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        long id = TemplateMakerV8.makeTemplate(templateMakerConfig);

        // 2 更换包名
        System.out.println("更换包名配置...");
        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker1.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMakerV8.makeTemplate(templateMakerConfig);
        System.out.println(id);

        // 3 是否开启生成帖子功能
        System.out.println("追加是否开启生成帖子的配置...");
        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker2.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMakerV8.makeTemplate(templateMakerConfig);

        // 4 是否开启跨域的功能，即跨域文件是否生成
        System.out.println("追加是否生成跨域配置的配置...");
        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker3.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMakerV8.makeTemplate(templateMakerConfig);

        // 5 追加接口文档的配置（测试分组模型，即先开启一个参数，再输入其他参数）
        System.out.println("追加是否生成接口文档配置的配置...");
        //先开启needDocs参数控制文件生成（示例中没有该文件，先注释）
//        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker4.json");
//        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
//        TemplateMakerV8.makeTemplate(templateMakerConfig);
        //再输入具体的接口配置参数
        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker5.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMakerV8.makeTemplate(templateMakerConfig);

        //6、追加sql配置，进行挖坑
        System.out.println("追加mysql配置...");
        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker6.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMakerV8.makeTemplate(templateMakerConfig);

        // 7、追加redis配置，但是redis的模板涉及的地方比较复杂，属于个性化定制，建议手动调整ftl文件
        // application.yml.ftl MainApplication.java.ftl pom.xml.ftl
        System.out.println("追加是否开启redis配置...");
        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker7.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMakerV8.makeTemplate(templateMakerConfig);

        // 8 是否开启es功能（注意这里只是调整了功能，并没有删除ES的配置等） 同理需要自定义ftl文件
        // PostController.java.ftl PostServiceImpl.java.ftl PostService.java.ftl application.yml
        System.out.println("追加是否开启ES的功能...");
        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker8.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMakerV8.makeTemplate(templateMakerConfig);

    }

    @Test
    public void makeSpringBootTemplate3() {
        String rootPath = "examples/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        // 是否开启跨域的功能，即跨域文件是否生成
        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker5.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMakerV8.makeTemplate(templateMakerConfig);

    }

}