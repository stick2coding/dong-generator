package com.dong.generator;

import com.dong.model.MainTemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * 动态文件生成
 */
public class DymaicGenerator {
    public static void main(String[] args) throws IOException, TemplateException {
        //先获取当前项目路径
        String projectPath = System.getProperty("user.dir");
        System.out.println(projectPath);
        //配置文件目录的路径
        String inputPath = projectPath + File.separator + "src/main/resources/templates";
        //指定输出目录路径
        String outputPath = projectPath;

        //指定配置文件名
        String templateFileName = "MainTemplate.java.ftl";
        //指定输出文件名
        String outputFileName = "MainTemplate.java";

        //创建数据模型
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("sunbin");
        mainTemplateConfig.setLoop(false);
        mainTemplateConfig.setOutputText("sum result:");

        //调用生成方法
        doGenerator(inputPath, templateFileName, outputPath, outputFileName, mainTemplateConfig);

    }

    /**
     * 抽取生成方法，将需要的输入路径和输出路径做为参数
     * @param inputPath
     * @param templateFileName
     * @param outputPath
     * @param outputFileName
     * @param mainTemplateConfig
     * @throws IOException
     * @throws TemplateException
     */
    public static void doGenerator(String inputPath, String templateFileName, String outputPath, String outputFileName, Object mainTemplateConfig) throws IOException, TemplateException {
        //定义全局配置
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        //指定模板！配置文件集合！的路径
        configuration.setDirectoryForTemplateLoading(new File(inputPath));
        //设置模板文件使用的字符集
        configuration.setDefaultEncoding("UTF-8");

        //创建爱你模板对象，加载指定的模板
        Template template = configuration.getTemplate(templateFileName);

        //生成动态文件
        Writer out = new FileWriter(outputPath + File.separator + outputFileName);
        template.process(mainTemplateConfig, out);

        //关闭文件流
        out.close();
    }
}
