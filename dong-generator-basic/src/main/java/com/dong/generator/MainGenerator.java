package com.dong.generator;

import com.dong.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 主生成器
 */
public class MainGenerator {

    public static void main(String[] args) throws TemplateException, IOException {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("test");
        mainTemplateConfig.setLoop(false);
        mainTemplateConfig.setOutputText("sum result:");
        doGenerate(mainTemplateConfig);

    }

    public static void doGenerate(Object model) throws TemplateException, IOException {
        //获取项目路径
        String projectPath = System.getProperty("user.dir");
        System.out.println(projectPath);
        //获取根文件
        File parentFile = new File(projectPath).getParentFile();

        //静态文件目录输入路径
        String staticInputPath = new File(parentFile, "dong-generator.bat-demo-projects/acm-template").getAbsolutePath();
        //静态文件输出的路径（当前项目下）
        String staticOutputPath = projectPath;
        //静态文件生成
        StaticGenerator.copyFilesByRecursive(staticInputPath, staticOutputPath);

        //动态文件目录输入路径
        String dymaicInputPath = projectPath + File.separator + "src/main/resources/templates";
        String dymaiconPath = projectPath + File.separator + "acm-template/src/com/dong/acm";
        //指定动态模板
        String templateFileName = "MainTemplate.java.ftl";
        //指定动态文件生成名
        String outputFileName = "MainTemplate.java";
        //动态文件生成
        DymaicGenerator.doGenerator(dymaicInputPath, templateFileName, dymaiconPath, outputFileName, model);

    }
}
