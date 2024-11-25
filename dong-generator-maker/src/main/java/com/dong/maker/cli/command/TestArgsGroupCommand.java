package com.dong.maker.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.dong.maker.generator.file.FileGenerator;
import com.dong.maker.model.DataModel;
import com.dong.maker.model.MainTemplate;
import lombok.Data;
import picocli.CommandLine;

@CommandLine.Command(name = "test", mixinStandardHelpOptions = true)
public class TestArgsGroupCommand implements Runnable {

    @CommandLine.Option(names = {"-l", "--loop"}, description = "是否循环", arity = "0..1", interactive = true, echo = true)
    private boolean loop;

    @CommandLine.ArgGroup(exclusive = false, heading = "核心模板%n")
    MainTemplate mainTemplate;

    @Override
    public void run() {
        System.out.println(loop);
        System.out.println(mainTemplate);
    }

    @Data
    static class MainTemplate {
        @CommandLine.Option(names = {"-mainTemplate.a", "--mainTemplate.author"}, description = "作者", arity = "0..1", interactive = true, echo = true)
        private String author = "dongdongTest";

        @CommandLine.Option(names = {"-mainTemplate.o", "--mainTemplate.outputText"}, description = "输出文本", arity = "0..1", interactive = true, echo = true)
        private String outputText = "test result";
    }

    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(TestArgsGroupCommand.class);
        //commandLine.execute("-l", "-mainTemplate.a", "--mainTemplate.outputText");
        commandLine.execute("--help");
    }
}
