package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

<#macro generateFile indent fileInfo>
${indent}inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
<#if fileInfo.generateType == "static">
${indent}StaticGenerator.copyFilesByHutool(inputPath, outputPath);
<#else>
${indent}DynamicGenerator.doGenerate(inputPath, outputPath, model);
</#if>
</#macro>

/**
 * 核心生成器
 */
public class MainGenerator {

    /**
     * 生成
     *
     * @param model 数据模型
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerate(DataModel model) throws TemplateException, IOException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;
        // 遍历取出所有配置项
    <#list modelConfig.models as modelInfo>
        ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
    </#list>
        // 遍历文件，首先查看文件是否有前置条件，首先判断是否是文件组，如果有标记文件组，则需要先判断组条件
    <#list fileConfig.files as fileInfo>
        <#if fileInfo.groupKey??>
        //再判断是否有前置条件
        <#if fileInfo.condition??>
        //如果前置存在，则遍历组内文件，否则不处理
        if(${fileInfo.condition}){
            <#list fileInfo.files as fileInfo>
            <@generateFile fileInfo=fileInfo indent="           "/>
            </#list>
        }
        //如果前置条件不存在，则直接遍历组内文件
        <#else>
        <#list fileInfo.files as fileInfo>
        <@generateFile fileInfo=fileInfo indent="       "/>
        </#list>
        </#if>
        <#else>
        //不是文件组的直接进行前置判断
        //如果有前置，则进行判断
        <#if fileInfo.condition??>
        if(${fileInfo.condition}) {
            <@generateFile fileInfo=fileInfo indent="           "/>
        }
        <#else>
        //没有前置则正常执行
        <@generateFile fileInfo=fileInfo indent="       "/>
        </#if>
        </#if>
    </#list>
    }
}
