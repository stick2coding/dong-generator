package com.dong.maker.generator.file;

import com.dong.maker.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 主生成器
 */
public class FileGenerator {

    public static void main(String[] args) throws TemplateException, IOException {
        DataModel dataModel = new DataModel();
        dataModel.setAuthor("test");
        dataModel.setLoop(false);
        dataModel.setOutputText("sum result:");
        doGenerate(dataModel);

    }

    public static void doGenerate(Object model) throws TemplateException, IOException {
        //获取项目路径
        String projectPath = System.getProperty("user.dir");
        System.out.println(projectPath);
        //获取根文件
        File parentFile = new File(projectPath).getParentFile();

        //静态文件目录输入路径
        String staticInputPath = new File(parentFile, "dong-generator-demo-projects/acm-template").getAbsolutePath();
        //静态文件输出的路径（当前项目下）
        String staticOutputPath = projectPath;
        //静态文件生成
        StaticFileGenerator.copyFilesByHutool(staticInputPath, staticOutputPath);

        //动态文件目录输入路径
        String dymaicInputPath = projectPath + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String dymaiconPath = projectPath + File.separator + "acm-template/src/com/dong/acm/MainTemplate.java";

        //动态文件生成
        DynamicFileGenerator.doGenerate(dymaicInputPath, dymaiconPath, model);

    }
}
