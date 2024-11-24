package ${basePackage}.cli.command;

import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "generate", description = "生成代码", mixinStandardHelpOptions = true)
@Data
public class GenerateCommand implements Callable<Integer> {

<#list modelConfig.models as modelInfo>
    @CommandLine.Option(names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}"</#if>, "--${modelInfo.fieldName}"}, description = <#if modelInfo.description??>"${modelInfo.description}"</#if>, arity = "0..1", interactive = true, echo = true)
    private ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
</#list>


    @Override
    public Integer call() throws Exception {
        DataModel dataModel = new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        System.out.println("当前配置：" + dataModel);
        MainGenerator.doGenerate(dataModel);
        return 0;
    }
}
