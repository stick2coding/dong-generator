package com.dong.maker.meta;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.io.FileUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public class MetaValidator {

    public static void doValidateAndFill(Meta meta) {
        //基础信息校验及默认值
        //name
        String name = meta.getName();
        if (StrUtil.isBlank(name)){
            name = "dong-generator";
            meta.setName(name);
        }

        //description
        String description = meta.getDescription();
        if (StrUtil.isBlank(description)){
            description = "我的代码生成器";
            meta.setDescription(description);
        }

        //author
        String author = meta.getAuthor();
        if (StrUtil.isBlank(author)){
            author = "dongdong";
            meta.setAuthor(author);
        }

        //basePackage
        String basePackage = meta.getBasePackage();
        if (StrUtil.isBlank(basePackage)){
            basePackage = "com.dong";
            meta.setBasePackage(basePackage);
        }

        //version
        String version = meta.getVersion();
        if (StrUtil.isBlank(version)){
            version = "1.0.0";
            meta.setVersion(version);
        }

        //createTime
        String createTime = meta.getCreateTime();
        if (StrUtil.isBlank(createTime)){
            createTime = DateUtil.now();
            meta.setCreateTime(createTime);
        }

        //fileConfig 校验和默认值
        Meta.FileConfig fileConfig = meta.getFileConfig();
        if(fileConfig != null){
            //sourceRootPath 必填
            String sourceRootPath = fileConfig.getInputRootPath();
            if (StrUtil.isBlank(sourceRootPath)){
                throw new MetaException("sourceRootPath is required");
            }
            //inputRootPath: .source + sourceRootPath的最后一个层级路径
            String inputRootPath = fileConfig.getInputRootPath();
            String defaultInputRootPath = ".source" + File.separator + FileUtil.getLastPathEle(Paths.get(inputRootPath)).getFileName().toString();
            if (StrUtil.isEmpty(inputRootPath)){
                fileConfig.setInputRootPath(defaultInputRootPath);
            }

            //outputRootPath: 默认当前路径下 generated
            String outputRootPath = fileConfig.getOutputRootPath();
            String defaultOutputRootPath = "generated";
            if (StrUtil.isEmpty(outputRootPath)){
                fileConfig.setOutputRootPath(defaultOutputRootPath);
            }

            //fileConfigType: 默认dir
            String fileConfigType = fileConfig.getType();
            String defaultType = FileTypeEnum.DIR.getValue();
            if (StrUtil.isEmpty(fileConfigType)){
                fileConfig.setType(defaultType);
            }

            //fileInfo 默认值
            List<Meta.FileConfig.FileInfo> fileInfoList = fileConfig.getFiles();
            if (CollectionUtils.isNotEmpty(fileInfoList)){
                for (Meta.FileConfig.FileInfo fileInfo : fileInfoList){
                    //如果是文件组类型，则无需校验
                    String fileType = fileInfo.getType();
                    if (FileTypeEnum.GROUP.getValue().equals(fileType)){
                        continue;
                    }
                    //inputPath 必填
                    String inputPath = fileInfo.getInputPath();
                    if (StrUtil.isBlank(inputPath)){
                        throw new MetaException("inputPath is required");
                    }

                    //outputPath: 默认等于inputPath
                    String outputPath = fileInfo.getOutputPath();
                    if (StrUtil.isEmpty(outputPath)){
                        fileInfo.setOutputPath(inputPath);
                    }

                    //type：默认inputPath有文件后缀，则为file，否则为dir
                    String type = fileInfo.getType();
                    if (StrUtil.isEmpty(type)){
                        String suffix = FileUtil.getSuffix(inputPath);
                        if (StrUtil.isBlank(suffix)){
                            fileInfo.setType(FileTypeEnum.DIR.getValue());
                        }else {
                            fileInfo.setType(FileTypeEnum.FILE.getValue());
                        }
                    }

                    //generateType: 如果inputPath文件结尾有.ftl，则为dynamic，否则默认 static
                    String generateType = fileInfo.getGenerateType();
                    if (StrUtil.isEmpty(generateType)){
                        String suffix = FileUtil.getSuffix(inputPath);
                        if (StrUtil.equals(suffix, "ftl")){
                            fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
                        }else {
                            fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
                        }
                    }
                }
            }
        }

        //modelConfig 校验和默认值
        Meta.ModelConfig modelConfig = meta.getModelConfig();
        if (modelConfig != null){
            List<Meta.ModelConfig.ModelInfo> modelInfoList = modelConfig.getModels();
            if (CollectionUtils.isNotEmpty(modelInfoList)){
                for (Meta.ModelConfig.ModelInfo modelInfo : modelInfoList){
                    // fieldName 输出路径默认值，必填
                    String fieldName = modelInfo.getFieldName();
                    if (StrUtil.isBlank(fieldName)){
                        throw new MetaException("fieldName is required");
                    }

                    // type 默认string
                    String type = modelInfo.getType();
                    if (StrUtil.isEmpty(type)){
                        modelInfo.setType(ModelTypeEnum.STRING.getValue());
                    }
                }
            }
        }


    }
}
