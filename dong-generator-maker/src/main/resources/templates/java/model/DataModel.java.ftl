package ${basePackage}.model;

import lombok.Data;

<#macro generateModel indent modelInfo>
<#if modelInfo.description??>
${indent}/**
${indent}* ${modelInfo.description}
${indent}*/
</#if>
${indent}public ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
</#macro>

/**
* 数据模型
*/
@Data
public class DataModel {
<#list modelConfig.models as modelInfo>

    <#--先判断有无分组，如果有分组，就要创建内部类 -->
    <#if modelInfo.groupKey??>
    /**
    * ${modelInfo.groupName}
    */
    public ${modelInfo.type} ${modelInfo.groupKey} = new ${modelInfo.type}();

    /**
    * ${modelInfo.description}
    */
    @Data
    public static class ${modelInfo.type}{
    <#-- 依次打印分组内的命令 -->
    <#list modelInfo.models as modelInfo>
        <@generateModel indent="      " modelInfo=modelInfo/>
    </#list>
    }
    <#else>
    <#-- 没有分组就正常输出 -->
    <@generateModel indent="      " modelInfo=modelInfo/>
    </#if>

</#list>
}
