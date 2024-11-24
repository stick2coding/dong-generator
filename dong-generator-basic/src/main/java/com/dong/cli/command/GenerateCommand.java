package com.dong.cli.command;

import com.dong.generator.MainGenerator;
import com.dong.model.MainTemplateConfig;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "generate", description = "生成代码", mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable<Integer> {

    /**
     * 添加配置 loop,name,outputText
     */
    @CommandLine.Option(names = {"-l", "--loop"}, description = "是否循环", arity = "0..1", interactive = true, echo = true)
    private boolean loop;

    @CommandLine.Option(names = {"-a", "-author"}, description = "作者", arity = "0..1", interactive = true, echo = true)
    private String author;

    @CommandLine.Option(names = {"-o", "--outputText"}, description = "输出文本", arity = "0..1", interactive = true, echo = true)
    private String outputText;

    @Override
    public Integer call() throws Exception {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setLoop(loop);
        mainTemplateConfig.setAuthor(author);
        mainTemplateConfig.setOutputText(outputText);
        System.out.println("当前配置：" + mainTemplateConfig);
        MainGenerator.doGenerate(mainTemplateConfig);
        return 0;
    }
}
