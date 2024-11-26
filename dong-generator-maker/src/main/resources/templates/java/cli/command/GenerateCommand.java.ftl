package ${basePackage}.cli.command;

import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;

<#--生成选项-->
<#macro generateOption indent modelInfo>
${indent}@CommandLine.Option(names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}"</#if>, "--${modelInfo.fieldName}"}, description = <#if modelInfo.description??>"${modelInfo.description}"</#if>, arity = "0..1", interactive = true, echo = true)
${indent}private ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
</#macro>

<#--生成二级命令调用-->
<#macro generateCommand indent modelInfo>
${indent}System.out.println("请输出${modelInfo.groupName}配置...");
${indent}CommandLine commandLine = new CommandLine(${modelInfo.type}Command.class);
${indent}commandLine.execute(${modelInfo.allArgsStr});
</#macro>


@CommandLine.Command(name = "generate", description = "生成代码", mixinStandardHelpOptions = true)
@Data
public class GenerateCommand implements Callable<Integer> {

<#list modelConfig.models as modelInfo>
    <#--有分组-->
    <#if modelInfo.groupKey??>
    <#--需要创建内部命令类-->
    /**
    * ${modelInfo.groupName}
    */
    static DataModel.${modelInfo.type} ${modelInfo.groupKey} = new DataModel.${modelInfo.type}();

    <#--内部命令类-->
    @CommandLine.Command(name = "${modelInfo.groupKey}")
    @Data
    public static class ${modelInfo.type}Command implements Runnable {
    <#--依次输出组内的命令变量-->
    <#list modelInfo.models as subModelInfo>
        <@generateOption indent="        " modelInfo=subModelInfo/>
    </#list>

        <#--run方法，赋值-->
        @Override
        public void run(){
            <#list modelInfo.models as subModelInfo>
            ${modelInfo.groupKey}.${subModelInfo.fieldName} = ${subModelInfo.fieldName};
            </#list>
        }
    }
    <#else>
    <#--没有分组，正常输出即可-->
    <@generateOption indent="    " modelInfo=modelInfo/>
    </#if>
</#list>

    <#--生成调用方法-->
    @Override
    public Integer call() throws Exception {
        <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        <#if modelInfo.condition??>
        if(${modelInfo.condition}){
            <@generateCommand indent="            " modelInfo=modelInfo/>
        }
        </#if>
        </#if>
        </#list>

        DataModel dataModel = new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        dataModel.${modelInfo.groupKey} = ${modelInfo.groupKey};
        </#if>
        </#list>
        System.out.println("当前配置：" + dataModel);
        MainGenerator.doGenerate(dataModel);
        return 0;
    }
}
