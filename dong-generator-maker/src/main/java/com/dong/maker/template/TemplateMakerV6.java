package com.dong.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.dong.maker.meta.FileGenerateTypeEnum;
import com.dong.maker.meta.FileTypeEnum;
import com.dong.maker.meta.Meta;
import com.dong.maker.template.enums.FileFilterRangeEnum;
import com.dong.maker.template.enums.FileFilterRuleEnum;
import com.dong.maker.template.model.FileFilterConfig;
import com.dong.maker.template.model.TemplateMakerFileConfig;
import com.dong.maker.template.model.TemplateMakerModelConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * V4特点
 * V3中对文件生成模板时，会对指定目录下的所有文件进行操作
 * 那么是否可以先对文件进行过滤，最后只处理符合要求的文件
 * 可以增加过滤规则
 * V5
 * 增加了生成元信息的时候，对同一批次的文件进行分组
 * List<FileInfoConfig> 一组文件放在一组中
 * V6
 * 增加对同一批次的模型配置信息进行分组
 * List<ModelInfoConfig> 一组模型放一组配置
 */
public class TemplateMakerV6 {
    public static void main(String[] args) {
        //测试下makeTemplate方法
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "dong-generator-demo-projects/springboot-init-master";

        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        //文件及规则
        List<TemplateMakerFileConfig.FileInfoConfigV2> fileInfoConfigList = new ArrayList<>();
        //同时对多个输入路径下的文件进行替换测试
        // 文件1
        String inputFilePath1 = "src/main/java/com/yupi/springbootinit/aop";
        // 文件1 规则
        List<FileFilterConfig> fileFilterConfigList1 = new ArrayList<>();
        FileFilterConfig fileFilterConfig = new FileFilterConfig();
        fileFilterConfig.setRange(FileFilterRangeEnum.FILE_NAME.getValue());
        fileFilterConfig.setRule(FileFilterRuleEnum.CONTAINS.getValue());
        fileFilterConfig.setValue("Base");
        fileFilterConfigList1.add(fileFilterConfig);
        //构建fileInfoConfig
        TemplateMakerFileConfig.FileInfoConfigV2 fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfigV2();
        fileInfoConfig1.setFilePath(inputFilePath1);
        fileInfoConfig1.setFileFilterConfigList(fileFilterConfigList1);

        // 文件2
        String inputFilePath2 = "src/main/java/com/yupi/springbootinit/controller";
        //构建fileInfoConfig
        TemplateMakerFileConfig.FileInfoConfigV2 fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfigV2();
        fileInfoConfig2.setFilePath(inputFilePath2);

        // 添加到集合中
        fileInfoConfigList.add(fileInfoConfig1);
        fileInfoConfigList.add(fileInfoConfig2);
        templateMakerFileConfig.setFileInfoConfigList(fileInfoConfigList);

        // 分组信息
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
        fileGroupConfig.setGroupName("BaseResponse");
        fileGroupConfig.setGroupKey("testGroup1");
        fileGroupConfig.setCondition("baseResponseTrue");
        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);


        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = new ArrayList<>();
        //模型信息（第一次）
//        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig.setFieldName("BaseResponseFieldName");
        modelInfoConfig.setType("String");
        modelInfoConfig.setDefaultValue("BaseResponseDefaultValue");
        modelInfoConfig.setReplaceText("BaseResponse");
        modelInfoConfigList.add(modelInfoConfig);

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("codeFieldName");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDefaultValue("code");
        modelInfoConfig1.setReplaceText("code");
        modelInfoConfigList.add(modelInfoConfig1);

        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        modelGroupConfig.setGroupKey("testModelGroupKey");
        modelGroupConfig.setGroupName("groupName");
        modelGroupConfig.setCondition("codeTrue");

        templateMakerModelConfig.setModelInfoConfigList(modelInfoConfigList);
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);

        long id = makeTemplate(1861587383960948736L, meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig);
        System.out.println(id);
    }

    /**
     * 通过使用model.fieldName进行去重
     * 新增 也是需要按分组进行对modelInfo就行分组，同文件那边的方法
     * @param modelInfoList
     * @return
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList) {
        //先找到有分组信息的，按照分组生成map
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyModelInfoListMap = modelInfoList
                .stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey));

        // 同组的modelInfo进行合并
        Map<String, Meta.ModelConfig.ModelInfo> groupKeyModelInfoMergedMap = new HashMap<>();
        // 遍历进行合并
        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKeyModelInfoListMap.entrySet()){
            List<Meta.ModelConfig.ModelInfo> tempModelInfoList = entry.getValue();
            //组内列表去重
            List<Meta.ModelConfig.ModelInfo> groupDistinctModelInfoList = new ArrayList<>(tempModelInfoList.stream()
                    .flatMap(modelInfo -> modelInfo.getModels().stream())
                    .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)).values());
            //放到带有组信息的新的modelInfo中
            Meta.ModelConfig.ModelInfo newModelInfo = new Meta.ModelConfig.ModelInfo();
            newModelInfo.setModels(groupDistinctModelInfoList);
            newModelInfo.setGroupKey(entry.getKey());
            groupKeyModelInfoMergedMap.put(entry.getKey(), newModelInfo);
        }

        //将整理后的分组的列表放在一起
        List<Meta.ModelConfig.ModelInfo> resultList = new ArrayList<>(groupKeyModelInfoMergedMap.values());

        // 先找到没有分组的放在一起
        List<Meta.ModelConfig.ModelInfo> noGroupModelInfoList = modelInfoList
                .stream()
                .filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                .collect(Collectors.toList());
        // 去重
        List<Meta.ModelConfig.ModelInfo> noGroupDistinctModelInfoList = new ArrayList<>(noGroupModelInfoList.stream().collect(
                Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)
        ).values());

        //加入到结果中
        resultList.addAll(noGroupDistinctModelInfoList);

//        //先转成MAP,fieldName 做为 key ,原对象做为value, 然后遍历的同时判断map是否存在，存在就保留新的即可，最后用所有的value转成list即可
//        // o -> o 就是 原对象做为参数，然后输出源对象
//        // （e, r） -> r 就是判断逻辑，e就是exist，r是replacement,表示遇到重复的数据保留新的数据
//        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(
//                modelInfoList.stream().collect(
//                        Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName,o -> o, (e, r) -> r)
//                ).values()
//        );
        return resultList;
    }

    /**
     * 文件配置去重，通过使用inputPath进行去重
     * @param fileInfoList
     * @return
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList) {
        //将fileInfoList内的配置分为有分组和无分组
        //有分组的，如果分组相同，可以进行合并，不同分组同时保留
        //创建新的文件配置列表，把合并后的列表添加进来
        //将剩下没有分组的添加进来

        //有分组，按分组划分，分组做为key，列表做为values
        Map<String, List<Meta.FileConfig.FileInfo>> groupkeyFileInfoListMap = fileInfoList
                .stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));

        //同组内进行合并
        Map<String, Meta.FileConfig.FileInfo> groupKeyFileInfoMergedMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupkeyFileInfoListMap.entrySet()) {
            List<Meta.FileConfig.FileInfo> tempFileInfoList = entry.getValue();
            //首先先取出所有fileInfo,由于分组中的文件放在fileInfo.files中，所以需要先取出来，然后再去重（去重同原来的方法）
            List<Meta.FileConfig.FileInfo> groupDistinctFileInfoList = new ArrayList<>(tempFileInfoList.stream()
                    .flatMap(fileInfo -> fileInfo.getFiles().stream())
                    .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r)).values() );

            //去重后的files要放在新的fileInfo中
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setFiles(groupDistinctFileInfoList);
            String groupKey = entry.getKey();
            groupKeyFileInfoMergedMap.put(groupKey, newFileInfo);
        }

        //所有的同组列表合并后，就把全部取出来放在结果列表中
        List<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(groupKeyFileInfoMergedMap.values());

        // 将没有分组的文件（一样要去重）先放在一起
        List<Meta.FileConfig.FileInfo> noGroupFileInfoList = fileInfoList
                .stream()
                .filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());
        //去重
        List<Meta.FileConfig.FileInfo> noGroupDistinctFileInfoList = new ArrayList<>(noGroupFileInfoList.stream().collect(
                Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r)
        ).values());
        //将没有分组去重后的放到结果中
        resultList.addAll(noGroupDistinctFileInfoList);

        //先转成MAP,inputPath 做为 key ,原对象做为value, 然后遍历的同时判断map是否存在，存在就保留新的即可，最后用所有的value转成list即可
        // o -> o 就是 原对象做为参数，然后输出源对象
        // （e, r） -> r 就是判断逻辑，e就是exist，r是replacement,表示遇到重复的数据保留新的数据
//        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(
//                fileInfoList.stream().collect(
//                        Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r)
//                ).values()
//        );
//        return newFileInfoList;
        return resultList;
    }

    /**
     * 制作模板
     * //整理要用的参数
     * // Meta\originRootPath\id\fileInputPath\modelInfo\searchStr
     * 增加文件及其过滤规则，符合规则的文件才生成模板
     * @param id
     * @param newMeta
     * @param originProjectPath
     * @param modelInfo
     * @param searchStr
     * @return
     */
    private static long makeTemplate(Long id, Meta newMeta, String originProjectPath, TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig){
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

        //更新 首先要先对文件按照过滤规则进行过滤
        for (TemplateMakerFileConfig.FileInfoConfigV2 fileInfoConfig : templateMakerFileConfig.getFileInfoConfigList()){
            String inputFilePath = fileInfoConfig.getFilePath();
            String inputFileAbsolutePath = inputFilePath;
            //这里如果是相对路径，需要转绝对路径
            if (!inputFilePath.startsWith(sourceRootPath)){
                inputFileAbsolutePath = sourceRootPath + File.separator + inputFilePath;
            }

            //过滤，并获取过滤后的文件列表
            List<File> fileList = FileFilter.doFilter(inputFileAbsolutePath, fileInfoConfig.getFileFilterConfigList());
            //过滤后直接都是文件，直接进行过滤即可
            for (File file : fileList){
                // 可能会出现一次对很多文件进行模板制作，抽取抽象方法来使用
                Meta.FileConfig.FileInfo fileInfo = makeTemplateFile(sourceRootPath, file, templateMakerModelConfig);
                newFileInfoList.add(fileInfo);
            }
        }

        //判断是否有分组信息
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if (fileGroupConfig != null){
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();
            String condition = fileGroupConfig.getCondition();

            //新增分组信息
            Meta.FileConfig.FileInfo groupFileInfo = new Meta.FileConfig.FileInfo();
            groupFileInfo.setGroupKey(groupKey);
            groupFileInfo.setGroupName(groupName);
            groupFileInfo.setCondition(condition);
            // 将上面过滤后的newFileInfoList 文件元信息放在组内
            groupFileInfo.setFiles(newFileInfoList);
            //然后将当前的整组信息放到新的 fileInfoList 中
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);
        }

        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();
        //这里对传入的模型配置进行处理，并转成Meta.ModelConfig.ModelInfo，然后还要进行分组处理
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = templateMakerModelConfig.getModelInfoConfigList();
        List<Meta.ModelConfig.ModelInfo> tempModelInfoList = modelInfoConfigList.stream().map(modelInfoConfig -> {
            Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelInfoConfig, modelInfo);
            return modelInfo;
        }).collect(Collectors.toList());
        System.out.println("modelInfoList:" + tempModelInfoList);
        // 是否增加组信息
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if (modelGroupConfig != null){
            //有分组需要前置一层组信息
            String groupKey = modelGroupConfig.getGroupKey();
            String groupName = modelGroupConfig.getGroupName();
            String condition = modelGroupConfig.getCondition();
            Meta.ModelConfig.ModelInfo groupModelInfo = new Meta.ModelConfig.ModelInfo();
            groupModelInfo.setGroupKey(groupKey);
            groupModelInfo.setGroupName(groupName);
            groupModelInfo.setCondition(condition);
            groupModelInfo.setModels(tempModelInfoList);
            newModelInfoList.add(groupModelInfo);
        }else{
            // 没有分组正常加入
            newModelInfoList.addAll(tempModelInfoList);
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
            //模型（此处修改接收最新的模型配置，已经经过分组处理）
            List<Meta.ModelConfig.ModelInfo> modelInfoList = oldMeta.getModelConfig().getModels();
            modelInfoList.addAll(newModelInfoList);
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
            modelInfoList.addAll(newModelInfoList);

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
     * 支持一次对多个模型参数进行字符串替换
     * @param sourceRootPath
     * @param inputFile
     * @return Meta.FileConfig.FileInfo
     */
    private static Meta.FileConfig.FileInfo makeTemplateFile(String sourceRootPath, File inputFile, TemplateMakerModelConfig templateMakerModelConfig){
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

        // 新增 拿到分组信息（待替换的配置）
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        // 这里新增一个newFileContent是为了生成文件后和源文件进行对比，看是否有差异，如果没差异，说明是静态的，不需要挖坑生成模板
        String newFileContent = fileContent;
        String replacement;
        //判断有无分组
        if (modelGroupConfig != null){
            //有分组，要对分组内的模型替换时增加前缀 ${groupKey.fieldName}
            for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModelInfoConfigList()) {
                String groupKey = modelGroupConfig.getGroupKey();
                replacement = String.format("${%s.%s}", groupKey, modelInfoConfig.getFieldName());
                newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(), replacement);
            }
        }else{
            // 没有分组，就逐个替换即可
            for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModelInfoConfigList()) {
                replacement = String.format("${%s}", modelInfoConfig.getFieldName());
                newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(), replacement);
            }
        }

//        // 要替换的内容
//        String replacement = String.format("${%s}", modelInfo.getFieldName());
//        // 替换，生成新内容
//        String newFileContent = StrUtil.replace(fileContent, searchStr, replacement);

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
