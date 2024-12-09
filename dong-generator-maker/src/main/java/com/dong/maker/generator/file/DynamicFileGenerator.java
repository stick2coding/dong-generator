package com.dong.maker.generator.file;

import cn.hutool.core.io.FileUtil;
import freemarker.cache.ClassTemplateLoader;
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
public class DynamicFileGenerator {

    /**
     * 同样的方法，此处改用相对路径
     * @param relativePath
     * @param outputPath
     * @param model
     */
    public static void doGenerate(String relativePath, String outputPath, Object model) throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        System.out.println("当前相对路径：" + relativePath);

        int lastSplitIndex = relativePath.lastIndexOf("/");
        String basePackagePath = relativePath.substring(0, lastSplitIndex);
        String templateFileName = relativePath.substring(lastSplitIndex + 1);

        // 通过类加载器，根据资源的相对路径获取（类模板加载器）
        ClassTemplateLoader templateLoader = new ClassTemplateLoader(DynamicFileGenerator.class, basePackagePath);
        //放入配置
        configuration.setTemplateLoader(templateLoader);

        //设置模板文件使用的字符集
        configuration.setDefaultEncoding("UTF-8");

        //创建模板对象，加载指定的模板
        Template template = configuration.getTemplate(templateFileName);

        //判断文件是否存在
        if (!FileUtil.exist(outputPath)){
            FileUtil.touch(outputPath);
        }

        //生成动态文件
        Writer out = new FileWriter(outputPath);
        template.process(model, out);

        //关闭文件流
        out.close();

    }

    /**
     * inputPath使用的是绝对路径
     * 抽取生成方法，将需要的输入路径和输出路径做为参数
     * @param inputPath
     * @param outputPath
     * @param mainTemplateConfig
     * @throws IOException
     * @throws TemplateException
     */
    public static void doGenerateByPath(String inputPath, String outputPath, Object mainTemplateConfig) throws IOException, TemplateException {
        //定义全局配置
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        //指定模板！配置文件集合！的路径（使用的是文件模板加载器）
        configuration.setDirectoryForTemplateLoading(new File(inputPath).getParentFile());
        //设置模板文件使用的字符集
        configuration.setDefaultEncoding("UTF-8");

        //创建模板对象，加载指定的模板
        Template template = configuration.getTemplate(new File(inputPath).getName());

        //上级目录是否存在，若不存在则新建
        String parentPath = new File(outputPath).getParentFile().getAbsolutePath();
        System.out.println(parentPath);
        if (!FileUtil.exist(parentPath)){
            FileUtil.mkdir(parentPath);
        }
        //生成动态文件
        Writer out = new FileWriter(outputPath);
        template.process(mainTemplateConfig, out);

        //关闭文件流
        out.close();
    }
}
