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
import java.util.stream.Collectors;

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
 *
 * 当要替换的内容非常多的时候，就需要逐步对文件进行替换制作
 * 即 文件有状态，就是程序在制作时保留上一次的记忆。两个要素，唯一标识和存储
 * 我们采用了ID做为唯一标识，同时又将ID做为目录对过程文件进行了存储
 * 要对制作生成的文件进行状态保存，每次在前一次的基础上进行制作
 */
public class TemplateMakerV2 {
    public static void main(String[] args) {
        //测试下makeTemplate方法
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "dong-generator-demo-projects/acm-template-ftl-maker";
        String inputFilePath = "src/com/dong/acm/MainTemplate.java";

        //模型信息（第一次）
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
//        modelInfo.setFieldName("outputText");
//        modelInfo.setType("String");
//        modelInfo.setDefaultValue("sum result = ");

        // 替换内容
        String searchStr = "Sum: ";

//        long id = makeTemplate(null, meta, originProjectPath, modelInfo, inputFilePath, searchStr);
//        System.out.println(id);

        //模型信息（第二次）把类名进行封装，变成动态的
        modelInfo.setFieldName("className");
        modelInfo.setType("String");
        modelInfo.setDefaultValue("MainTemplate");

        String newSearchStr = "MainTemplate";

        long id = makeTemplate(1861340258429804544L, meta, originProjectPath, modelInfo, inputFilePath, newSearchStr);

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
     * @param fileInputPath
     * @param searchStr
     * @return
     */
    private static long makeTemplate(Long id, Meta newMeta, String originProjectPath, Meta.ModelConfig.ModelInfo modelInfo, String fileInputPath, String searchStr){
        // 如果ID存在，说明是首次制作，需要生成新的ID
        if (id == null){
            id = IdUtil.getSnowflakeNextId();
        }

        // 指定原始项目路径
        String projectPath = System.getProperty("user.dir");
        //String originProjectPath = new File(projectPath).getParent() + File.separator + "dong-generator-demo-projects/acm-template-ftl-maker";

        //复制目录，此处判断是否首次制作，没有ID为首次，有ID为追加
        //生成ID
        //long id = IdUtil.getSnowflakeNextId();
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
            //只有当首次制作的时候才需要复制原文件，后续制作只需要使用临时目录的就行
            FileUtil.copy(originProjectPath, templatePath, true);
        }


        // 第一步，准备基本信息（等待参数传入）
        // 项目基本信息，名称、描述
//        String name = "acm-template-generator";
//        String description = "ACM 示例模板生成器";

        // 文件信息
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        System.out.println(sourceRootPath);
        // 路径处理（win）（此处已经使用临时目录，所以不需要再转换）
//        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
        // 待处理的原始文件（做为参数传入）
//        String fileInputPath = "src/com/dong/acm/MainTemplate.java";
        // 要生成的模板文件（同路径下）
        String fileOutputPath = fileInputPath + ".ftl";

        // 输出模型参数，就是要替换的内容（做为参数modelInfo传入）
//        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
//        modelInfo.setFieldName("outputText");
//        modelInfo.setType("String");
//        modelInfo.setDefaultValue("sum result = ");

        // 二 进行文件替换
        // 此处同理，如果要对一个文件进行多次模板制作，需要判断ftl文件是否已经存在，已存在就在已存在文件的基础上制作即可。
        String fileInputAbsolutePath = sourceRootPath + File.separator + fileInputPath;
        String fileOutputAbsolutePath = sourceRootPath + File.separator + fileOutputPath;

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
        //输出模板文件
        FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);

        // 三，生成元数据配置文件，此处同理，非首次制作，就不需要输入完整的元信息。
        // 当前要处理的文件
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        // 配置文件目录
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        //判断配置文件是否存在，看是新制作还是追加
        if (FileUtil.exist(metaOutputPath)){
            //追加
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            //追加配置参数
            // 文件列表
            List<Meta.FileConfig.FileInfo> fileInfoList = oldMeta.getFileConfig().getFiles();
            fileInfoList.add(fileInfo);
            //模型
            List<Meta.ModelConfig.ModelInfo> modelInfoList = oldMeta.getModelConfig().getModels();
            modelInfoList.add(modelInfo);
            //去除重复，重复制作时配置会重复
            oldMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            oldMeta.getModelConfig().setModels(distinctModels(modelInfoList));

            //更新文件
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(oldMeta), metaOutputPath);
        } else {
//            // 首次创建，构造配置 使用参数中newMeta
//            Meta meta = new Meta();
//            meta.setName(name);
//            meta.setDescription(description);

            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);
            // sourceRootPath
            fileConfig.setSourceRootPath(sourceRootPath);
            // 文件列表
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
            fileInfoList.add(fileInfo);

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

}
