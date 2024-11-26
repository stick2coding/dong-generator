package com.dong.maker.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.dong.maker.meta.FileGenerateTypeEnum;
import com.dong.maker.meta.FileTypeEnum;
import com.dong.maker.meta.Meta;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 想要制作一个模板，
 * 1、原始文件（具体哪个文件要制作模板）
 * 2、基本信息（项目的一些基本信息，例如名字，描述等）
 * 3、模型参数，就是要替换的内容
 *
 * 准备好后，按照ftl文件的要求使用字符串进行替换
 *
 * 输出模板文件
 *
 * 输出meta.json配置文件
 *
 * 目前的代码，是直接在源文件中生成模板和云信息文件，会污染原项目
 * 最好是能够将原项目复制到一个临时空间，此处我们可以使用.temp（忽略.git）目录临时文件夹处理
 * 同时，每次制作的时候分配一个唯一ID做为名称，从而进行隔离
 * 通过FileUtil.copy复制目录
 * 修改sourceRootPath为复制后的值
 */
public class TemplateMaker {
    public static void main(String[] args) {
        // 指定原始项目路径
        String projectPath = System.getProperty("user.dir");
        String originRootPath = new File(projectPath).getParent() + File.separator + "dong-generator-demo-projects/acm-template-ftl-maker";

        //复制目录
        //生成ID
        long id = IdUtil.getSnowflakeNextId();
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
        }
        FileUtil.copy(originRootPath, templatePath, true);

        // 第一步，准备基本信息
        // 项目基本信息，名称、描述
        String name = "acm-template-generator";
        String description = "ACM 示例模板生成器";

        // 文件信息
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originRootPath)).toString();
        System.out.println(sourceRootPath);
        // 路径处理（win）（此处已经使用临时目录，所以不需要再转换）
//        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
        // 待处理的原始文件
        String fileInputPath = "src/com/dong/acm/MainTemplate.java";
        // 要生成的模板文件（同路径下）
        String fileOutputPath = fileInputPath + ".ftl";

        // 输出模型参数，就是要替换的内容
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("outputText");
        modelInfo.setType("String");
        modelInfo.setDefaultValue("sum result = ");

        // 二 进行文件替换
        // 读取源文件全部内容
        String fileInputAbsolutePath = sourceRootPath + File.separator + fileInputPath;
        String fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        // 要替换的内容
        String replacement = String.format("${%s}", modelInfo.getFieldName());
        // 替换，生成新内容
        String newFileContent = StrUtil.replace(fileContent, "Sum: ", replacement);

        //输出模板文件
        String fileOutputAbsolutePath = sourceRootPath + File.separator + fileOutputPath;
        FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);

        // 三，生成元数据配置文件
        // 配置文件目录
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        // 构造配置
        Meta meta = new Meta();
        meta.setName(name);
        meta.setDescription(description);

        Meta.FileConfig fileConfig = new Meta.FileConfig();
        meta.setFileConfig(fileConfig);
        // sourceRootPath
        fileConfig.setSourceRootPath(sourceRootPath);
        // 文件列表
        List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
        fileConfig.setFiles(fileInfoList);
        // 单个文件
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
        fileInfoList.add(fileInfo);

        Meta.ModelConfig modelConfig = new Meta.ModelConfig();
        meta.setModelConfig(modelConfig);
        List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
        modelConfig.setModels(modelInfoList);
        modelInfoList.add(modelInfo);

        // 输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(meta), metaOutputPath);
    }

    private static long makeTemplate(Long id){
        if (id == null){
            id = IdUtil.getSnowflakeNextId();
        }

        return id;
    }

}
