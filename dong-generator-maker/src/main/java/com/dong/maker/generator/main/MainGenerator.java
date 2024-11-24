package com.dong.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.dong.maker.generator.JarGenerator;
import com.dong.maker.generator.ScriptGenerator;
import com.dong.maker.generator.file.DynamicFileGenerator;
import com.dong.maker.meta.Meta;
import com.dong.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        Meta meta = MetaManager.getMetaObject();
        System.out.println(meta);


        // 输出路径
        String projectPath = System.getProperty("user.dir");
        String outputPath = projectPath + File.separator + "generated" + File.separator + meta.getName();
        System.out.println("输出根目录：" + outputPath);
        if (!FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }

        //复制原始文件
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = outputPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath, sourceCopyDestPath, false);

        //读取resources文件
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();

        //读取Java包基础路径
        String basePackage = meta.getBasePackage();
        // com/dong
        String basePackagePath = StrUtil.join("/", StrUtil.split(basePackage, "."));
        String outputBaseJavaPackagePath = outputPath + File.separator + "src/main/java/" + basePackagePath;

        String inputFilePath;
        String outputFilePath;

        // modle.DataModle
        inputFilePath = inputResourcePath + "/templates/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/model/DataModel.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.ConfigCommad
        inputFilePath = inputResourcePath + "/templates/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.GenerateCommand
        inputFilePath = inputResourcePath + "/templates/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.ListCommand
        inputFilePath = inputResourcePath + "/templates/java/cli/command/ListCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.CommandExecutor
        inputFilePath = inputResourcePath + "/templates/java/cli/CommandExecutor.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // Main
        inputFilePath = inputResourcePath + "/templates/java/Main.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/Main.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        //generator.DymicaicGenerator
        inputFilePath = inputResourcePath + "/templates/java/generator/DynamicGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/DynamicGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        //generator.StaticGenerator
        inputFilePath = inputResourcePath + "/templates/java/generator/StaticGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/StaticGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        //generator.MainGenerator
        inputFilePath = inputResourcePath + "/templates/java/generator/MainGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/MainGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        //pom.xml
        inputFilePath = inputResourcePath + "/templates/pom.xml.ftl";
        outputFilePath = outputPath + File.separator + "pom.xml";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        //README.md
        inputFilePath = inputResourcePath + "/templates/README.md.ftl";
        outputFilePath = outputPath + File.separator + "README.md";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        //打包
        JarGenerator.doGenerate(outputPath);

        //封装脚本
        String shellScriptOutputFilePath = outputPath + File.separator + "generator.bat";
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        String jarPath = "target/" + jarName;
        ScriptGenerator.doGenerate(shellScriptOutputFilePath, jarPath);

        //生成精简版程序
        String distOutputPath = outputPath + "-dist";
        //新建target包用于存放jar包
        String targetDistOutputPath = distOutputPath + File.separator + "target";
        FileUtil.mkdir(targetDistOutputPath);
        String jarSourcePath = outputPath + File.separator + jarPath;
        FileUtil.copy(jarSourcePath, targetDistOutputPath, true);
        //脚本
        FileUtil.copy(shellScriptOutputFilePath, distOutputPath, true);
        //源文件
        FileUtil.copy(sourceCopyDestPath, distOutputPath, true);



    }
}
