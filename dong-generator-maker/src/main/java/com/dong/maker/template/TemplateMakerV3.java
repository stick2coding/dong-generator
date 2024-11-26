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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * V3特点
 * 比如要对其中一个字段进行替换，但是要同时替换一个目录下的所有文件，就需要调整方法，如果是目录，进行遍历替换
 */
public class TemplateMakerV3 {
    public static void main(String[] args) {
        //测试下makeTemplate方法
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "dong-generator-demo-projects/springboot-init-master";
        //同时对多个输入路径下的文件进行替换测试
        String inputFilePath1 = "src/main/java/com/yupi/springbootinit/common";
        String inputFilePath2 = "src/main/java/com/yupi/springbootinit/controller";
        List<String> inputFilePathList = Arrays.asList(inputFilePath1, inputFilePath2);

        //模型信息（第一次）
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("BaseResponse");
        modelInfo.setType("String");
        modelInfo.setDefaultValue("BaseResponse1");

        // 替换内容
        String searchStr = "BaseResponse";

        long id = makeTemplate(null, meta, originProjectPath, modelInfo, inputFilePathList, searchStr);
        System.out.println(id);

        //模型信息（第二次）把类名进行封装，变成动态的
//        modelInfo.setFieldName("className");
//        modelInfo.setType("String");
//        modelInfo.setDefaultValue("MainTemplate");
//
//        String newSearchStr = "MainTemplate";
//
//        long id = makeTemplate(1861340258429804544L, meta, originProjectPath, modelInfo, inputFilePath, newSearchStr);

        System.out.println(id);

    }

    /**
     * 通过使用model.fieldName进行去重
     * @param modelInfoList
     * @return
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList) {
        //先转成MAP,fieldName 做为 key ,原对象做为value, 然后遍历的同时判断map是否存在，存在就保留新的即可，最后用所有的value转成list即可
        // o -> o 就是 原对象做为参数，然后输出源对象
        // （e, r） -> r 就是判断逻辑，e就是exist，r是replacement,表示遇到重复的数据保留新的数据
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(
                modelInfoList.stream().collect(
                        Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName,o -> o, (e, r) -> r)
                ).values()
        );
        return newModelInfoList;
    }

    /**
     * 文件配置去重，通过使用inputPath进行去重
     * @param fileInfoList
     * @return
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList) {
        //先转成MAP,inputPath 做为 key ,原对象做为value, 然后遍历的同时判断map是否存在，存在就保留新的即可，最后用所有的value转成list即可
        // o -> o 就是 原对象做为参数，然后输出源对象
        // （e, r） -> r 就是判断逻辑，e就是exist，r是replacement,表示遇到重复的数据保留新的数据
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(
                fileInfoList.stream().collect(
                        Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r)
                ).values()
        );
        return newFileInfoList;
    }

    /**
     * 制作模板
     * //整理要用的参数
     * // Meta\originRootPath\id\fileInputPath\modelInfo\searchStr
     * @param id
     * @param newMeta
     * @param originProjectPath
     * @param modelInfo
     * @param InputFilePathList
     * @param searchStr
     * @return
     */
    private static long makeTemplate(Long id, Meta newMeta, String originProjectPath, Meta.ModelConfig.ModelInfo modelInfo, List<String> InputFilePathList, String searchStr){
        // 如果ID存在，说明是首次制作，需要生成新的ID
        if (id == null){
            id = IdUtil.getSnowflakeNextId();
        }

        // 指定原始项目路径
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
            //只有当首次制作的时候才需要复制原文件，后续制作只需要使用临时目录的就行
            FileUtil.copy(originProjectPath, templatePath, true);
        }
        //拿到项目的绝对路径
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        //需要处理进行转义（win）
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
        // 更新，此处如果一次对多个输入路径进行处理，就需要将参数调整为list
        for (String InputFilePath : InputFilePathList){
            String inputFileAbsolutePath = sourceRootPath + File.separator + InputFilePath;
            System.out.println("资源绝对路径：" + sourceRootPath);
            //如果输入路径是个目录，就需要遍历对文件进行操作（需要绝对路径）
            if(FileUtil.isDirectory(inputFileAbsolutePath)){
                List<File> fileList = FileUtil.loopFiles(inputFileAbsolutePath);
                for (File file : fileList){
                    // 可能会出现一次对很多文件进行模板制作，抽取抽象方法来使用
                    Meta.FileConfig.FileInfo fileInfo = makeTemplateFile(sourceRootPath, modelInfo, file, searchStr);
                    newFileInfoList.add(fileInfo);
                }
            }else {
                Meta.FileConfig.FileInfo fileInfo = makeTemplateFile(sourceRootPath, modelInfo, new File(inputFileAbsolutePath), searchStr);
                newFileInfoList.add(fileInfo);
            }
        }


        // 配置文件目录
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        //判断配置文件是否存在，看是新制作还是追加
        if (FileUtil.exist(metaOutputPath)){
            //追加
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            //追加配置参数
            // 文件列表
            List<Meta.FileConfig.FileInfo> fileInfoList = oldMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);
            //模型
            List<Meta.ModelConfig.ModelInfo> modelInfoList = oldMeta.getModelConfig().getModels();
            modelInfoList.add(modelInfo);
            //去除重复，重复制作时配置会重复
            oldMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            oldMeta.getModelConfig().setModels(distinctModels(modelInfoList));

            //更新文件
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(oldMeta), metaOutputPath);
        } else {
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);
            // 文件列表
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
            fileInfoList.addAll(newFileInfoList);

            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            modelInfoList.add(modelInfo);

            // 输出元信息文件
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);
        }

        return id;
    }

    /**
     * 生成模板文件，并返回元信息中的文件配置
     *
     * 这里可以接受参数，文件路径，要制作的文件，要替换的内容，模型信息
     * 由于我们在前置方法中遍历所有要进行制作的文件时，拿到的是文件的绝对路径，因此需要直接传文件，然后再改为相对路径
     * @param sourceRootPath
     * @param modelInfo
     * @param inputFile
     * @param searchStr
     * @return Meta.FileConfig.FileInfo
     */
    private static Meta.FileConfig.FileInfo makeTemplateFile(String sourceRootPath, Meta.ModelConfig.ModelInfo modelInfo, File inputFile, String searchStr){
        // 获取绝对路径 win系统下需要进行转义
        String fileInputAbsolutePath = inputFile.getAbsolutePath();
        fileInputAbsolutePath = fileInputAbsolutePath.replaceAll("\\\\", "/");
//        System.out.println("当前文件的绝对路径为：" + fileInputAbsolutePath);
        // 要生成的模板文件（同路径下）
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        //转相对路径
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String fileOutputPath = fileInputPath + ".ftl";

        String fileContent = null;
        if (FileUtil.exist(fileOutputAbsolutePath)){
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        }else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }
        // 要替换的内容
        String replacement = String.format("${%s}", modelInfo.getFieldName());
        // 替换，生成新内容
        String newFileContent = StrUtil.replace(fileContent, searchStr, replacement);

        // 三，生成元数据配置文件，此处需要文件的相对路径
        // 当前要处理的文件
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());

        //需要增加一个对比功能，如果生成的文件和原来的文件一样，则说明该文件在代码不需要模板，代码生成应标记为静态生成
        if (newFileContent.equals(fileContent)){
            // 输出路径 = 输入路径
            fileInfo.setOutputPath(fileInfo.getInputPath());
            fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
        } else {
            //输出模板文件
            fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }

        return fileInfo;
    }

}
