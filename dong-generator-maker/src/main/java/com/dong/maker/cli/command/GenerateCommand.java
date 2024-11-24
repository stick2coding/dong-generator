package com.dong.maker.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.dong.maker.generator.file.FileGenerator;
import com.dong.maker.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "generate", description = "生成代码", mixinStandardHelpOptions = true)
@Data
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
        DataModel dataModel = new DataModel();
//        dataModel.setLoop(loop);
//        dataModel.setAuthor(author);
//        dataModel.setOutputText(outputText);
        BeanUtil.copyProperties(this, dataModel);
        System.out.println("当前配置：" + dataModel);
        FileGenerator.doGenerate(dataModel);
        return 0;
    }
}
